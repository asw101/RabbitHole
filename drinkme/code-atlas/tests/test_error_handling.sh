#!/usr/bin/env bash
# File: drinkme/code-atlas/tests/test_error_handling.sh
#
# Edge-case and error-handling contract tests for the Alice code-atlas generator CLI.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/lib/atlas_contract_test_lib.sh"

atlas_require_generator_command

output_dir="$(atlas_make_output_dir)"
log_file="${output_dir}/generator-error.log"
trap 'atlas_cleanup_dir "${output_dir}"' EXIT

atlas_run_expect_failure "${log_file}" \
  "${ATLAS_COMMAND}" \
  --repo-root "${output_dir}/missing-repo" \
  --output-dir "${output_dir}/missing-repo-output" \
  --format both
atlas_assert_contains "${log_file}" 'repo root|repository|pom.xml|not found|missing' \
  "missing repository roots must fail with an actionable diagnostic"

atlas_run_expect_failure "${log_file}" \
  "${ATLAS_COMMAND}" \
  --repo-root "${ATLAS_REPO_ROOT}" \
  --output-dir "${output_dir}/invalid-format-output" \
  --format png
atlas_assert_contains "${log_file}" 'format|Mermaid|DOT|both|unsupported' \
  "unsupported formats must fail with an actionable diagnostic"

not_a_directory="${output_dir}/not-a-directory"
printf 'not a directory\n' >"${not_a_directory}"
atlas_run_expect_failure "${log_file}" \
  "${ATLAS_COMMAND}" \
  --repo-root "${ATLAS_REPO_ROOT}" \
  --output-dir "${not_a_directory}" \
  --format both
atlas_assert_contains "${log_file}" 'output|directory|not a directory|cannot write' \
  "invalid output destinations must fail with an actionable diagnostic"

if [[ -e "${output_dir}/invalid-format-output/alice-source-truth.md" ]]; then
  atlas_fail "generator must not write success-shaped artifacts after invalid format input"
fi
