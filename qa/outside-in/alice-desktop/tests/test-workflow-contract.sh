#!/usr/bin/env bash
# qa/outside-in/alice-desktop/tests/test-workflow-contract.sh
set -u

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
BASE_DIR=$(CDPATH= cd -- "$SCRIPT_DIR/.." && pwd)
VALIDATOR="$BASE_DIR/runners/validate-scenarios.sh"
RUNNER="$BASE_DIR/runners/run-scenario.sh"
# shellcheck source=qa/outside-in/alice-desktop/tests/lib/assertions.sh
. "$SCRIPT_DIR/lib/assertions.sh"

tmp_root=$(mktemp -d)
trap 'rm -rf "$tmp_root"' EXIT

"$RUNNER" list >"$tmp_root/list.out" 2>"$tmp_root/list.err"
status=$?
assert_success "$status" "runner lists scenario catalog"

"$VALIDATOR" --dump-json >"$tmp_root/catalog.json" 2>"$tmp_root/catalog.err"
status=$?
assert_success "$status" "validator dumps scenario catalog for workflow checks"

for workflow in instructor-student-setup launch scene-creation run-debug save-load export; do
  count=$(python3 - "$tmp_root/catalog.json" "$workflow" <<'PY'
import json
import sys

catalog_path, workflow = sys.argv[1:]
catalog = json.load(open(catalog_path, encoding="utf-8"))
print(sum(1 for scenario in catalog if scenario["workflow"] == workflow))
PY
)
  if [ "$count" -eq 1 ]; then
    pass "catalog contains exactly one $workflow workflow scenario"
  else
    fail "catalog contains exactly one $workflow workflow scenario (found $count)"
  fi
done

python3 - "$tmp_root/catalog.json" >"$tmp_root/workflow-contract.out" 2>"$tmp_root/workflow-contract.err" <<'PY'
import json
import sys

catalog = {
    scenario["id"]: scenario
    for scenario in json.load(open(sys.argv[1], encoding="utf-8"))
}
manual_scenarios = [
    "alice-desktop-instructor-student-setup",
    "alice-desktop-scene-creation",
    "alice-desktop-run-debug",
    "alice-desktop-save-load",
    "alice-desktop-export",
]
errors = []

for scenario_id in manual_scenarios:
    scenario = catalog[scenario_id]
    if scenario["automationMode"] != "manual-evidence-required":
        errors.append(f"{scenario_id} must remain manual-evidence-required until GUI automation exists")
    supporting = scenario.get("supportingEvidence", [])
    if "alice-desktop-launch" not in supporting:
        errors.append(f"{scenario_id} must link alice-desktop-launch as supportingEvidence")
    evidence_text = "\n".join(scenario["evidence"]["required"]).lower()
    if "screenshot" not in evidence_text:
        errors.append(f"{scenario_id} must require screenshot evidence")
    if not any(token in evidence_text for token in ("a3p", "artifact", "log", "notes")):
        errors.append(f"{scenario_id} must require a durable artifact, log, or notes")
    if "review-notes.txt" not in evidence_text:
        errors.append(f"{scenario_id} must require review-notes.txt for manual acceptance")

if errors:
    raise AssertionError("\n".join(errors))
PY
status=$?
assert_success "$status" "manual workflow scenarios link launch and require durable evidence"

"$RUNNER" run alice-desktop-instructor-student-setup --evidence-dir "$tmp_root/evidence" >"$tmp_root/manual.out" 2>"$tmp_root/manual.err"
status=$?
assert_success "$status" "workflow runner prepares instructor/student evidence"
run_dir=$(find "$tmp_root/evidence/alice-desktop-instructor-student-setup" -mindepth 1 -maxdepth 1 -type d 2>/dev/null | sort | tail -1)
checklist="$run_dir/manual-evidence-checklist.txt"
assert_file_exists "$checklist" "workflow runner writes manual checklist"
assert_contains "$checklist" '^Required evidence$' "workflow checklist includes required evidence section"
assert_contains "$checklist" '^Completion status$' "workflow checklist includes completion status section"
assert_contains "$checklist" 'Human reviewer|human performs the workflow' "workflow checklist names human review requirement"

finish
