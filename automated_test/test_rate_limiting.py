import time

from common import MissingInputError, append_report, curl_request, get_tokens, load_endpoints, load_input


def main():
    config = load_input(required=True)
    tokens = get_tokens(config)
    candidates = [
        e for e in load_endpoints()
        if e.get("safe_to_test") and e["method"] in {"GET", "HEAD", "OPTIONS"} and e["expected_auth"] == "public"
    ]
    if not candidates:
        candidates = [e for e in load_endpoints() if e.get("safe_to_test") and e["method"] in {"GET", "HEAD", "OPTIONS"}]
    if not candidates:
        print("⚠ Rate limiting skipped: no safe GET/HEAD/OPTIONS endpoint")
        return
    ep = candidates[0]
    token = tokens.get("user") or tokens.get("anon")
    statuses = []
    start = time.perf_counter()
    for _ in range(30):
        result = curl_request(config, ep["method"], ep["endpoint"], token=token, max_time=10)
        statuses.append(result["status"])
        time.sleep(0.05)
    elapsed_ms = int((time.perf_counter() - start) * 1000)
    limited = any(s == 429 for s in statuses)
    append_report({
        "endpoint": ep["endpoint"],
        "method": ep["method"],
        "role": "user-or-anon",
        "status": statuses[-1] if statuses else 0,
        "expected_status": "429/403 during bounded burst or documented upstream limit",
        "finding": not limited,
        "severity": "low" if not limited else "info",
        "response_time_ms": elapsed_ms,
        "test_category": "Rate limiting",
        "note": f"30 request bounded burst; statuses={sorted(set(statuses))}",
    })
    print(("⚠" if not limited else "✓"), ep["method"], ep["endpoint"], f"statuses={sorted(set(statuses))}")


if __name__ == "__main__":
    try:
        main()
    except MissingInputError as exc:
        print(f"⚠ {exc}")
        raise SystemExit(2)
