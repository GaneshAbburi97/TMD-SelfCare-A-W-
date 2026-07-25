import time

from common import MissingInputError, append_report, curl_request, is_2xx, load_endpoints, load_input


def main():
    config = load_input(required=True)
    endpoints = [e for e in load_endpoints() if e["expected_auth"] == "requires-auth" and e.get("safe_to_test")]
    malformed = "Bearer not.a.valid.jwt"
    for ep in endpoints:
        for role, token, note in [
            ("anonymous", None, "No bearer token"),
            ("malformed", malformed.replace("Bearer ", ""), "Malformed bearer token"),
        ]:
            body = {"messages": [{"role": "user", "content": "health check"}]} if ep["endpoint"].startswith("/functions/") and ep["method"] == "POST" else None
            result = curl_request(config, ep["method"], ep["endpoint"], token=token, body=body)
            finding = is_2xx(result["status"])
            append_report({
                "endpoint": ep["endpoint"],
                "method": ep["method"],
                "role": role,
                "status": result["status"],
                "expected_status": "401/403",
                "finding": finding,
                "severity": "high" if finding else "info",
                "response_time_ms": result["time_ms"],
                "test_category": "AuthN bypass",
                "note": note if not finding else f"{note}; protected endpoint returned 2xx",
            })
            print(("✗" if finding else "✓"), ep["method"], ep["endpoint"], role, result["status"])
            time.sleep(0.2)


if __name__ == "__main__":
    try:
        main()
    except MissingInputError as exc:
        print(f"⚠ {exc}")
        raise SystemExit(2)
