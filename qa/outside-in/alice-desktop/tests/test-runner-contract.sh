#!/usr/bin/env bash
# qa/outside-in/alice-desktop/tests/test-runner-contract.sh
set -u

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
BASE_DIR=$(CDPATH= cd -- "$SCRIPT_DIR/.." && pwd)
RUNNER="$BASE_DIR/runners/run-scenario.sh"
# shellcheck source=qa/outside-in/alice-desktop/tests/lib/assertions.sh
. "$SCRIPT_DIR/lib/assertions.sh"

tmp_root=$(mktemp -d)
trap 'rm -rf "$tmp_root"' EXIT

evidence_dir="$tmp_root/evidence"
"$RUNNER" run alice-desktop-scene-creation --evidence-dir "$evidence_dir" >"$tmp_root/manual.out" 2>"$tmp_root/manual.err"
status=$?
assert_success "$status" "manual scenario run prepares evidence directory"

run_dir=$(find "$evidence_dir/alice-desktop-scene-creation" -mindepth 1 -maxdepth 1 -type d 2>/dev/null | sort | tail -1)
status_file="$run_dir/status.txt"
assert_file_exists "$status_file" "manual scenario run writes status.txt"
assert_contains "$status_file" '^scenario=alice-desktop-scene-creation$' "manual status records scenario id"
assert_contains "$status_file" '^automationMode=manual-evidence-required$' "manual status records automation mode"
assert_contains "$status_file" '^outcome=manual-evidence-required$' "manual status records that evidence still needs human execution"
assert_contains "$status_file" '^checklist=manual-evidence-checklist.txt$' "manual status points to generated checklist"

checklist="$run_dir/manual-evidence-checklist.txt"
assert_file_exists "$checklist" "manual scenario run writes checklist"
assert_contains "$checklist" '^Completion status$' "checklist includes completion status section"
assert_contains "$checklist" 'not complete until required evidence is attached' "checklist states manual run is not complete"

bad_timeout_evidence="$tmp_root/bad-timeout-evidence"
"$RUNNER" run alice-desktop-scene-creation --evidence-dir "$bad_timeout_evidence" --timeout-seconds not-a-number >"$tmp_root/bad-timeout.out" 2>"$tmp_root/bad-timeout.err"
status=$?
assert_failure "$status" "runner rejects non-integer timeout values for every mode"
assert_contains "$tmp_root/bad-timeout.err" 'timeout.*positive integer|invalid timeout' "invalid timeout error is actionable"

"$RUNNER" run alice-desktop-not-a-scenario --evidence-dir "$tmp_root/unknown-evidence" >"$tmp_root/unknown.out" 2>"$tmp_root/unknown.err"
status=$?
assert_failure "$status" "runner rejects unknown scenario ids"
assert_contains "$tmp_root/unknown.err" 'not found|unknown scenario|alice-desktop-not-a-scenario' "unknown scenario error names requested id"

finish
