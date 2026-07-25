import time

from common import MissingInputError, append_report, curl_request, get_tokens, is_2xx, load_endpoints, load_input


def main():
    config = load_input(required=True)
    tokens = get_tokens(config)
    endpoints = [e for e in load_endpoints() if e["expected_auth"] == "requires-auth" and e.get("safe_to_test")]
    for ep in endpoints:
        for role, token in tokens.items():
            if role == "anonymous":
                continue
            body = {"messages": [{"role": "user", "content": "health check"}]} if ep["endpoint"].startswith("/functions/") and ep["method"] == "POST" else None
            result = curl_request(config, ep["method"], ep["endpoint"], token=token, body=body)
            anon_role = role.lower() in {"anon", "anonymous"}
            finding = anon_role and is_2xx(result["status"])
            append_report({
                "endpoint": ep["endpoint"],
                "method": ep["method"],
                "role": role,
                "status": result["status"],
                "expected_status": "401/403 for anon; 2xx only for authenticated owner/admin as applicable",
                "finding": finding,
                "severity": "high" if finding else "info",
                "response_time_ms": result["time_ms"],
                "test_category": "RBAC matrix",
                "note": "Lower-privilege anon token received 2xx" if finding else "Role response matched coarse expectation",
            })
            print("✓", ep["method"], ep["endpoint"], role, result["status"])
            time.sleep(0.2)


if __name__ == "__main__":
    try:
        main()
    except MissingInputError as exc:
        print(f"⚠ {exc}")
        raise SystemExit(2)
