import time

from common import MissingInputError, append_report, curl_request, get_tokens, is_2xx, load_endpoints, load_input, tamper_jwt_unsigned


def main():
    config = load_input(required=True)
    tokens = get_tokens(config)
    source_token = tokens.get("user") or next((v for k, v in tokens.items() if k != "anonymous" and v), None)
    if not source_token:
        print("⚠ Token tampering skipped: no user token in input.json")
        return
    tampered = tamper_jwt_unsigned(source_token)
    for ep in [e for e in load_endpoints() if e["expected_auth"] == "requires-auth" and e.get("safe_to_test")]:
        body = {"messages": [{"role": "user", "content": "health check"}]} if ep["endpoint"].startswith("/functions/") and ep["method"] == "POST" else None
        result = curl_request(config, ep["method"], ep["endpoint"], token=tampered, body=body)
        finding = is_2xx(result["status"])
        append_report({
            "endpoint": ep["endpoint"],
            "method": ep["method"],
            "role": "tampered-token",
            "status": result["status"],
            "expected_status": "401/403",
            "finding": finding,
            "severity": "critical" if finding else "info",
            "response_time_ms": result["time_ms"],
            "test_category": "Token tampering",
            "note": "JWT payload modified without re-signing",
        })
        print(("✗" if finding else "✓"), ep["method"], ep["endpoint"], result["status"])
        time.sleep(0.2)


if __name__ == "__main__":
    try:
        main()
    except MissingInputError as exc:
        print(f"⚠ {exc}")
        raise SystemExit(2)
