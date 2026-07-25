import re
from pathlib import Path

from common import ROOT, append_report

SECRET_RE = re.compile(
    r"(gsk_[A-Za-z0-9_-]{20,}|eyJ[A-Za-z0-9_-]{20,}\.[A-Za-z0-9_-]{20,}\.[A-Za-z0-9_-]{20,}|AIza[0-9A-Za-z_-]{20,})"
)
SKIP_PARTS = {"node_modules", "build", "dist", ".git", "automated_test"}
SKIP_NAMES = {"input.json", ".env.local"}


def main():
    findings = []
    for path in ROOT.rglob("*"):
        if not path.is_file() or any(part in SKIP_PARTS for part in path.parts) or path.name in SKIP_NAMES:
            continue
        if path.suffix.lower() not in {".js", ".jsx", ".ts", ".tsx", ".kt", ".java", ".json", ".env", ".local", ".xml"}:
            continue
        try:
            text = path.read_text(encoding="utf-8", errors="ignore")
        except OSError:
            continue
        for match in SECRET_RE.finditer(text):
            findings.append((path, match.group(0)[:4] + "..." + match.group(0)[-4:]))

    for path, masked in findings:
        append_report({
            "endpoint": "codebase",
            "method": "SCAN",
            "role": "local",
            "status": 0,
            "expected_status": "no committed secrets",
            "finding": True,
            "severity": "high",
            "response_time_ms": 0,
            "test_category": "Hardcoded creds",
            "note": f"Potential committed secret in {path.relative_to(ROOT)} value={masked}",
        })
        print("✗", path.relative_to(ROOT), masked)
    if not findings:
        print("✓ No hardcoded credential patterns found")


if __name__ == "__main__":
    main()
