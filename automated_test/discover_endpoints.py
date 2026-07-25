import json
import re
import sys
from pathlib import Path

from common import (
    ROOT,
    curl_request,
    load_input,
    save_endpoints,
    update_savepoint,
    MissingInputError,
)

SKIP_RE = re.compile(r"/(health|actuator|metrics)(/|$)", re.I)


def add_endpoint(endpoints, path, method, expected_auth, source, safe_to_test=True, notes=""):
    if SKIP_RE.search(path):
        return
    item = {
        "endpoint": path,
        "method": method.upper(),
        "expected_auth": expected_auth,
        "role": expected_auth,
        "source": source,
        "safe_to_test": safe_to_test,
        "notes": notes,
    }
    key = (item["endpoint"], item["method"])
    if key not in {(e["endpoint"], e["method"]) for e in endpoints}:
        endpoints.append(item)


def discover_from_schema(endpoints):
    for schema_path in [ROOT / "TMDApp2" / "supabase" / "schema.sql"]:
        if not schema_path.exists():
            continue
        text = schema_path.read_text(encoding="utf-8", errors="ignore")
        for table in sorted(set(re.findall(r"create\s+table\s+public\.([a-zA-Z0-9_]+)", text, re.I))):
            path = f"/rest/v1/{table}"
            add_endpoint(endpoints, path, "GET", "requires-auth", str(schema_path), True, "PostgREST table with RLS enabled")
            add_endpoint(endpoints, path, "HEAD", "requires-auth", str(schema_path), True, "PostgREST table with RLS enabled")
            add_endpoint(endpoints, path, "POST", "requires-auth", str(schema_path), False, "Write endpoint; do not run without explicit confirmation")
            add_endpoint(endpoints, path, "PATCH", "requires-auth", str(schema_path), False, "Write endpoint; do not run without explicit confirmation")
            add_endpoint(endpoints, path, "DELETE", "requires-auth", str(schema_path), False, "Destructive endpoint; skipped by default")


def discover_from_functions(endpoints):
    for fn_dir in [
        ROOT / "tmd-web" / "supabase" / "functions",
        ROOT / "TMDApp2" / "supabase" / "functions",
    ]:
        if not fn_dir.exists():
            continue
        for index_file in fn_dir.glob("*/index.ts"):
            fn_name = index_file.parent.name
            path = f"/functions/v1/{fn_name}"
            add_endpoint(endpoints, path, "OPTIONS", "public", str(index_file), True, "CORS preflight")
            add_endpoint(endpoints, path, "POST", "requires-auth", str(index_file), True, "Safe probe uses minimal messages body")


def discover_from_auth_usage(endpoints):
    auth_endpoints = [
        ("/auth/v1/signup", "POST", "public", False, "May create a user; skipped by default"),
        ("/auth/v1/token?grant_type=password", "POST", "public", False, "Requires supplied credentials; skipped by default"),
        ("/auth/v1/token?grant_type=id_token", "POST", "public", False, "Requires Google ID token; skipped by default"),
        ("/auth/v1/recover", "POST", "public", False, "May send email; skipped by default"),
        ("/auth/v1/logout", "POST", "requires-auth", True, "Session invalidation endpoint"),
        ("/auth/v1/user", "GET", "requires-auth", True, "Current authenticated user"),
        ("/auth/v1/authorize", "GET", "public", False, "Provider redirect endpoint; skipped by default"),
    ]
    for path, method, expected, safe, note in auth_endpoints:
        add_endpoint(endpoints, path, method, expected, "Supabase auth client usage", safe, note)


def discover_openapi(endpoints, config):
    for spec_path in ["/v3/api-docs", "/swagger.json", "/openapi.json"]:
        result = curl_request(config, "GET", spec_path, max_time=10)
        if result["status"] != 200:
            continue
        try:
            spec = json.loads(result["body"])
        except json.JSONDecodeError:
            continue
        for path, methods in (spec.get("paths") or {}).items():
            for method in methods.keys():
                if method.upper() in {"GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS"}:
                    safe = method.upper() in {"GET", "HEAD", "OPTIONS"}
                    add_endpoint(endpoints, path, method, "unknown", spec_path, safe, "Discovered from OpenAPI")


def main():
    endpoints = []
    discover_from_schema(endpoints)
    discover_from_functions(endpoints)
    discover_from_auth_usage(endpoints)

    remote_note = "⚠ remote OpenAPI discovery skipped: input.json missing"
    try:
        config = load_input(required=True)
        discover_openapi(endpoints, config)
        remote_note = "✓ remote OpenAPI discovery attempted within baseUrl scope"
    except MissingInputError:
        pass

    endpoints.sort(key=lambda e: (e["endpoint"], e["method"]))
    save_endpoints(endpoints)
    update_savepoint(
        phase="step1_discovery",
        status="completed_offline" if "skipped" in remote_note else "completed",
        last_completed_step="step1_discover_endpoints",
        next_step="await_user_confirmation_for_testing",
        discovered_endpoint_count=len(endpoints),
    )

    print("✓ Step 1 endpoint discovery complete")
    print(remote_note)
    print(f"✓ Discovered endpoints: {len(endpoints)}")
    print("")
    for idx, ep in enumerate(endpoints, 1):
        flag = "✓" if ep["safe_to_test"] else "⚠"
        print(f"{idx:02d}. {flag} {ep['method']:7} {ep['endpoint']} [{ep['expected_auth']}] - {ep['notes']}")
    print("")
    print("⚠ Pausing here as requested. Confirm before running DAST tests.")


if __name__ == "__main__":
    try:
        main()
    except Exception as exc:
        print(f"✗ Discovery failed: {exc}", file=sys.stderr)
        sys.exit(1)
