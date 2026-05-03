#!/usr/bin/env bash
# File: drinkme/code-atlas/tests/run_all.sh
#
# Runs all Alice code-atlas TDD contract tests.

set -u

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
tests=(
  "${SCRIPT_DIR}/test_source_truth_components.sh"
  "${SCRIPT_DIR}/test_hotspots_staleness_findings.sh"
  "${SCRIPT_DIR}/test_generation_workflow.sh"
  "${SCRIPT_DIR}/test_error_handling.sh"
)

failures=0
for test_script in "${tests[@]}"; do
  printf '==> %s\n' "${test_script}"
  if bash "${test_script}"; then
    printf 'ok - %s\n' "${test_script}"
  else
    status=$?
    printf 'not ok - %s exited with %s\n' "${test_script}" "${status}" >&2
    failures=$((failures + 1))
  fi
done

if [[ "${failures}" -ne 0 ]]; then
  printf '%s test file(s) failed\n' "${failures}" >&2
  exit 1
fi

printf 'all code-atlas contract tests passed\n'
