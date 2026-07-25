import time

from common import MissingInputError, append_report, curl_request, get_tokens, is_2xx, load_endpoints, load_input


def main():
    config = load_input(required=True)
    other_user_id = (config.get("ids") or {}).get("otherUserId") or config.get("otherUserId")
    if not other_user_id:
        print("⚠ IDOR skipped: provide ids.otherUserId in input.json")
        return
    tokens = get_tokens(config)
    user_token = tokens.get("user") or next((v for k, v in tokens.items() if k != "anonymous" and v), None)
    if not user_token:
        print("⚠ IDOR skipped: no user token in input.json")
        return
    for ep in [e for e in load_endpoints() if e["endpoint"].startswith("/rest/v1/") and e["method"] == "GET"]:
        path = ep["endpoint"] + f"?select=id&user_id=eq.{other_user_id}&limit=1"
        result = curl_request(config, "GET", path, token=user_token)
        body = result["body"].strip()
        finding = is_2xx(result["status"]) and body not in ("[]", "")
        append_report({
            "endpoint": ep["endpoint"],
            "method": "GET",
            "role": "user",
            "status": result["status"],
            "expected_status": "200 with empty result or 401/403",
            "finding": finding,
            "severity": "critical" if finding else "info",
            "response_time_ms": result["time_ms"],
            "test_category": "IDOR",
            "note": "Queried another principal by user_id without dumping returned data",
        })
        print(("✗" if finding else "✓"), "GET", ep["endpoint"], result["status"])
        time.sleep(0.2)


if __name__ == "__main__":
    try:
        main()
    except MissingInputError as exc:
        print(f"⚠ {exc}")
        raise SystemExit(2)
