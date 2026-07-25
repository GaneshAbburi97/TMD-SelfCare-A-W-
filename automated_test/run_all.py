import subprocess
import sys
from pathlib import Path

from common import load_input, update_savepoint, MissingInputError

SCRIPTS = [
    "discover_endpoints.py",
    "test_authn_bypass.py",
    "test_authz_rbac.py",
    "test_idor.py",
    "test_token_tampering.py",
    "test_injection_probe.py",
    "test_rate_limiting.py",
    "test_hardcoded_creds.py",
]


def main():
    try:
        load_input(required=True)
    except MissingInputError as exc:
        print(f"⚠ {exc}")
        print("Create input.json from automated_test/input.example.json before running tests.")
        return 2

    here = Path(__file__).resolve().parent
    for script in SCRIPTS:
        print(f"\n=== {script} ===")
        result = subprocess.run([sys.executable, str(here / script)])
        if result.returncode not in (0,):
            print(f"⚠ {script} exited with {result.returncode}; continuing to next category")
    update_savepoint(phase="tests", status="completed", last_completed_step="step3_tests", next_step="step4_report")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
