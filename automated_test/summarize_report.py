from collections import Counter

from common import REPORT, load_json


def main():
    records = load_json(REPORT, [])
    findings = [r for r in records if r.get("finding")]
    by_sev = Counter(r.get("severity", "info") for r in findings)
    print("✓ DAST report summary")
    print(f"Tests run: {len(records)}")
    print(f"Findings: {len(findings)}")
    for sev in ["critical", "high", "medium", "low", "info"]:
        print(f"{sev}: {by_sev.get(sev, 0)}")
    print("")
    for item in findings[:10]:
        print(f"✗ {item['severity']} {item['test_category']} {item['method']} {item['endpoint']} :: {item['note']}")


if __name__ == "__main__":
    main()
