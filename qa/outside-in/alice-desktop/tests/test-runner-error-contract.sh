#!/usr/bin/env bash
# qa/outside-in/alice-desktop/tests/test-runner-error-contract.sh
set -u

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
BASE_DIR=$(CDPATH= cd -- "$SCRIPT_DIR/.." && pwd)
RUNNER="$BASE_DIR/runners/run-scenario.sh"
# shellcheck source=qa/outside-in/alice-desktop/tests/lib/assertions.sh
. "$SCRIPT_DIR/lib/assertions.sh"

tmp_root=$(create_scratch_root "$SCRIPT_DIR") || exit 1
trap 'rm -rf "$tmp_root"' EXIT

"$RUNNER" run alice-desktop-scene-creation --evidence-dir >"$tmp_root/missing-evidence-dir.out" 2>"$tmp_root/missing-evidence-dir.err"
status=$?
assert_exit_code "$status" 2 "missing --evidence-dir value is a command-line usage error"
assert_contains "$tmp_root/missing-evidence-dir.err" '--evidence-dir requires a value' "missing evidence-dir value reports the specific option"

"$RUNNER" run alice-desktop-launch --timeout-seconds >"$tmp_root/missing-timeout.out" 2>"$tmp_root/missing-timeout.err"
status=$?
assert_exit_code "$status" 2 "missing --timeout-seconds value is a command-line usage error"
assert_contains "$tmp_root/missing-timeout.err" '--timeout-seconds requires a value' "missing timeout value reports the specific option"

"$RUNNER" run alice-desktop-scene-creation --not-a-runner-option >"$tmp_root/unknown-option.out" 2>"$tmp_root/unknown-option.err"
status=$?
assert_exit_code "$status" 2 "unknown runner options are command-line usage errors"
assert_contains "$tmp_root/unknown-option.err" 'unknown argument: --not-a-runner-option' "unknown option error names the rejected option"

nested_catalog="$tmp_root/catalog"
mkdir -p "$nested_catalog/nested"
cp "$BASE_DIR"/scenarios/*.yaml "$nested_catalog"/
cp "$BASE_DIR/scenarios/save-load.yaml" "$nested_catalog/nested/save-load.yaml"
ALICE_QA_SCENARIO_DIR="$nested_catalog" "$RUNNER" run "$nested_catalog/nested/save-load.yaml" --evidence-dir "$tmp_root/evidence" >"$tmp_root/nested-path.out" 2>"$tmp_root/nested-path.err"
status=$?
assert_exit_code "$status" 2 "nested scenario YAML paths are rejected as catalog boundary errors"
assert_contains "$tmp_root/nested-path.err" 'directly inside active scenario directory' "nested path error explains the direct catalog file requirement"

unsafe_catalog="$tmp_root/unsafe-catalog"
mkdir -p "$unsafe_catalog"
cp "$BASE_DIR"/scenarios/*.yaml "$unsafe_catalog"/
python3 - "$unsafe_catalog/launch.yaml" <<'PY'
from pathlib import Path
import sys

path = Path(sys.argv[1])
text = path.read_text(encoding="utf-8").replace("    - mvn\n", "    - bash\n", 1)
path.write_text(text, encoding="utf-8")
PY
ALICE_QA_SCENARIO_DIR="$unsafe_catalog" "$RUNNER" run alice-desktop-launch --evidence-dir "$tmp_root/unsafe-evidence" >"$tmp_root/unsafe.out" 2>"$tmp_root/unsafe.err"
status=$?
assert_failure "$status" "runner rejects unapproved automation argv before launch"
assert_contains "$tmp_root/unsafe.err" 'automation\.argv is restricted' "runner surfaces automation allowlist failures"

finish
