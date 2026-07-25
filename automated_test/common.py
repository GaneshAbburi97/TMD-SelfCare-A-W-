import base64
import json
import os
import subprocess
import sys
import time
from datetime import datetime, timezone
from pathlib import Path
from urllib.parse import urljoin, urlparse

ROOT = Path(__file__).resolve().parents[1]
WORK = ROOT / "automated_test"
REPORT = WORK / "report.json"
ENDPOINTS = WORK / "endpoints.json"
SAVEPOINT = WORK / "savepoint.json"
INPUT_CANDIDATES = [ROOT / "input.json", WORK / "input.json"]

try:
    sys.stdout.reconfigure(encoding="utf-8", errors="replace")
    sys.stderr.reconfigure(encoding="utf-8", errors="replace")
except Exception:
    pass


class MissingInputError(RuntimeError):
    pass


def now_iso():
    return datetime.now(timezone.utc).isoformat()


def load_json(path, default):
    if not path.exists():
      return default
    return json.loads(path.read_text(encoding="utf-8"))


def write_json(path, data):
    path.write_text(json.dumps(data, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")


def update_savepoint(**kwargs):
    data = load_json(SAVEPOINT, {})
    data.update(kwargs)
    data["updated_at"] = now_iso()
    write_json(SAVEPOINT, data)


def load_input(required=True):
    for path in INPUT_CANDIDATES:
        if path.exists():
            data = json.loads(path.read_text(encoding="utf-8"))
            base_url = data.get("baseUrl") or data.get("baseURL") or data.get("BASE_URL")
            if not base_url:
                raise MissingInputError("input.json exists but is missing baseUrl")
            data["baseUrl"] = base_url.rstrip("/")
            return data
    if required:
        raise MissingInputError("input.json not found in project root or automated_test/")
    return None


def mask_secret(value):
    if not value:
        return ""
    if len(value) <= 10:
        return "***"
    return f"{value[:4]}...{value[-4:]}"


def get_tokens(config):
    tokens = {"anonymous": None}
    raw_tokens = config.get("tokens") or {}
    for role, token in raw_tokens.items():
        if token:
            tokens[role] = token
    for key, value in config.items():
        if key.lower().endswith("token") and isinstance(value, str) and value:
            tokens[key[:-5] or key] = value
    return tokens


def anon_key(config):
    tokens = config.get("tokens") or {}
    return config.get("anonKey") or tokens.get("anon") or config.get("supabaseAnonKey")


def safe_url(config, path):
    base = config["baseUrl"].rstrip("/") + "/"
    url = urljoin(base, path.lstrip("/"))
    if urlparse(url).netloc != urlparse(base).netloc:
        raise RuntimeError(f"Refusing out-of-scope URL: {url}")
    return url


def curl_request(config, method, path, token=None, body=None, extra_headers=None, max_time=10):
    url = safe_url(config, path)
    headers = {"Content-Type": "application/json"}
    key = anon_key(config)
    if key:
        headers["apikey"] = key
    if token:
        headers["Authorization"] = f"Bearer {token}"
    if extra_headers:
        headers.update(extra_headers)

    if method.upper() == "HEAD":
        cmd = ["curl", "-s", "-I"]
    else:
        cmd = ["curl", "-s", "-X", method.upper()]
    for name, value in headers.items():
        cmd += ["-H", f"{name}: {value}"]
    if body is not None:
        cmd += ["--data", json.dumps(body)]
    cmd += ["-w", "\n%{http_code} %{time_total}\n", "--max-time", str(max_time), url]

    started = time.perf_counter()
    proc = subprocess.run(cmd, capture_output=True, text=True)
    elapsed_ms = int((time.perf_counter() - started) * 1000)
    output = proc.stdout
    lines = output.splitlines()
    status = 0
    seconds = elapsed_ms / 1000
    response_body = output
    if lines:
        trailer = lines[-1].strip().split()
        if len(trailer) == 2 and trailer[0].isdigit():
            status = int(trailer[0])
            try:
                seconds = float(trailer[1])
            except ValueError:
                seconds = elapsed_ms / 1000
            response_body = "\n".join(lines[:-1])
    return {
        "cmd": cmd,
        "returncode": proc.returncode,
        "status": status,
        "time_ms": int(seconds * 1000),
        "body": response_body,
        "stderr": proc.stderr,
    }


def append_report(record):
    records = load_json(REPORT, [])
    clean = {
        "endpoint": record.get("endpoint"),
        "method": record.get("method"),
        "role": record.get("role"),
        "status": record.get("status"),
        "expected_status": record.get("expected_status"),
        "finding": bool(record.get("finding")),
        "severity": record.get("severity", "info"),
        "response_time_ms": record.get("response_time_ms"),
        "test_category": record.get("test_category"),
        "note": record.get("note", ""),
        "timestamp": now_iso(),
    }
    records.append(clean)
    write_json(REPORT, records)


def load_endpoints():
    return load_json(ENDPOINTS, [])


def save_endpoints(endpoints):
    write_json(ENDPOINTS, endpoints)


def is_2xx(status):
    return 200 <= int(status or 0) < 300


def tamper_jwt_unsigned(token, claim="role", value="admin"):
    parts = token.split(".")
    if len(parts) != 3:
        return token + "tampered"
    try:
        payload_raw = parts[1] + "=" * (-len(parts[1]) % 4)
        payload = json.loads(base64.urlsafe_b64decode(payload_raw.encode()))
        payload[claim] = value
        encoded = base64.urlsafe_b64encode(json.dumps(payload, separators=(",", ":")).encode()).decode()
        encoded = encoded.rstrip("=")
        return f"{parts[0]}.{encoded}.{parts[2]}"
    except Exception:
        return token[:-1] + ("x" if token[-1:] != "x" else "y")
