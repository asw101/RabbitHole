#!/usr/bin/env bash
# qa/outside-in/alice-desktop/tests/test-validator-contract.sh
set -u

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
BASE_DIR=$(CDPATH= cd -- "$SCRIPT_DIR/.." && pwd)
VALIDATOR="$BASE_DIR/runners/validate-scenarios.sh"
# shellcheck source=qa/outside-in/alice-desktop/tests/lib/assertions.sh
. "$SCRIPT_DIR/lib/assertions.sh"

tmp_root=$(mktemp -d)
trap 'rm -rf "$tmp_root"' EXIT

"$VALIDATOR" >"$tmp_root/valid.out" 2>"$tmp_root/valid.err"
status=$?
assert_success "$status" "current scenario catalog validates"

missing_dir="$tmp_root/missing-workflow"
mkdir -p "$missing_dir"
cp "$BASE_DIR"/scenarios/*.yaml "$missing_dir"/
rm "$missing_dir/export.yaml"
ALICE_QA_SCENARIO_DIR="$missing_dir" "$VALIDATOR" >"$tmp_root/missing.out" 2>"$tmp_root/missing.err"
status=$?
assert_failure "$status" "validator rejects catalogs missing required workflow coverage"
assert_contains "$tmp_root/missing.err" 'missing workflow.*export|export.*workflow' "missing workflow error names export"

duplicate_dir="$tmp_root/duplicate-workflow"
mkdir -p "$duplicate_dir"
cp "$BASE_DIR"/scenarios/*.yaml "$duplicate_dir"/
perl -0pi -e 's/workflow: export/workflow: save-load/' "$duplicate_dir/export.yaml"
ALICE_QA_SCENARIO_DIR="$duplicate_dir" "$VALIDATOR" >"$tmp_root/duplicate.out" 2>"$tmp_root/duplicate.err"
status=$?
assert_failure "$status" "validator rejects duplicate workflow coverage"
assert_contains "$tmp_root/duplicate.err" 'duplicate workflow|workflow.*save-load' "duplicate workflow error names workflow"

missing_ref_dir="$tmp_root/missing-supporting-evidence"
mkdir -p "$missing_ref_dir"
cp "$BASE_DIR"/scenarios/*.yaml "$missing_ref_dir"/
perl -0pi -e 's/alice-desktop-launch/alice-desktop-does-not-exist/g' "$missing_ref_dir/scene-creation.yaml"
ALICE_QA_SCENARIO_DIR="$missing_ref_dir" "$VALIDATOR" >"$tmp_root/ref.out" 2>"$tmp_root/ref.err"
status=$?
assert_failure "$status" "validator rejects missing supportingEvidence references"
assert_contains "$tmp_root/ref.err" 'supportingEvidence|does-not-exist' "missing supportingEvidence error names reference"

finish
