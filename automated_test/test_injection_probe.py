import time
from urllib.parse import quote

from common import MissingInputError, append_report, curl_request, get_tokens, load_endpoints, load_input


PAYLOADS = ["' or '1'='1", "\" || true || \"", "')) OR 1=1 --"]
ERROR_LEAK_PATTERNS = [
    "syntax error at or near",
    "unterminated quoted string",
    "sqlstate",
    "stack trace",
    "database error",
]


def main():
    config = load_input(required=True)
    tokens = get_tokens(config)
    token = tokens.get("user") or tokens.get("anon")
    for ep in [e for e in load_endpoints() if e["endpoint"].startswith("/rest/v1/") and e["method"] == "GET"]:
        for payload in PAYLOADS:
            path = ep["endpoint"] + f"?select=id&id=eq.{quote(payload)}&limit=1"
            result = curl_request(config, "GET", path, token=token)
            body_l = result["body"].lower()
            finding = result["status"] >= 500 or any(s in body_l for s in ERROR_LEAK_PATTERNS)
            append_report({
                "endpoint": ep["endpoint"],
                "method": "GET",
                "role": "user-or-anon",
                "status": result["status"],
                "expected_status": "400/401/403/200 without backend error leakage",
                "finding": finding,
                "severity": "medium" if finding else "info",
                "response_time_ms": result["time_ms"],
                "test_category": "Injection probe",
                "note": "Detection-only payload; response body not stored",
            })
            print(("✗" if finding else "✓"), "GET", ep["endpoint"], result["status"])
            time.sleep(0.2)


if __name__ == "__main__":
    try:
        main()
    except MissingInputError as exc:
        print(f"⚠ {exc}")
        raise SystemExit(2)
