#!/usr/bin/env bash
# File: drinkme/code-atlas/tests/test_generation_workflow.sh
#
# Integration contract tests for the end-to-end Alice code-atlas generation workflow.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/lib/atlas_contract_test_lib.sh"

output_dir="$(atlas_make_output_dir)"
trap 'atlas_cleanup_dir "${output_dir}"' EXIT

atlas_note "Generating atlas artifacts into ${output_dir}"
atlas_run_generator "${output_dir}"

for artifact in "${ATLAS_EXPECTED_ARTIFACTS[@]}"; do
  atlas_assert_file_exists "${output_dir}/${artifact}"
done

unexpected_files="$(
  find "${output_dir}" -type f \
    ! -name '*.md' \
    ! -name '*.mmd' \
    ! -name '*.dot' \
    -print
)"
if [[ -n "${unexpected_files}" ]]; then
  atlas_fail "atlas output must contain only Markdown, Mermaid, and DOT source files; found: ${unexpected_files}"
fi

copied_source_files="$(
  find "${output_dir}" -type f \
    \( -name '*.java' -o -name '*.class' -o -name '*.jar' -o -name '*.zip' -o -name '*.a3p' \) \
    -print
)"
if [[ -n "${copied_source_files}" ]]; then
  atlas_fail "atlas output must not copy Alice source or runtime artifacts into drinkme: ${copied_source_files}"
fi

atlas_assert_tree_not_contains "${output_dir}" "${ATLAS_REPO_ROOT}" \
  "atlas output must use relative evidence paths"
atlas_assert_tree_not_contains "${output_dir}" 'TheAliceProject/alice3' \
  "atlas output must not use upstream Alice tracking"

atlas_assert_contains "${output_dir}/alice-module-graph.mmd" 'includeSims' \
  "workflow must generate an includeSims boundary in Mermaid"
atlas_assert_contains "${output_dir}/alice-module-graph.dot" 'includeSims' \
  "workflow must generate an includeSims boundary in DOT"
atlas_assert_contains "${output_dir}/alice-module-graph.mmd" 'buildInstaller' \
  "workflow must generate a buildInstaller boundary in Mermaid"
atlas_assert_contains "${output_dir}/alice-module-graph.dot" 'buildInstaller' \
  "workflow must generate a buildInstaller boundary in DOT"

for artifact in "${ATLAS_EXPECTED_ARTIFACTS[@]}"; do
  if [[ ! -s "${output_dir}/${artifact}" ]]; then
    atlas_fail "artifact must not be empty: ${artifact}"
  fi
done

if command -v dot >/dev/null 2>&1; then
  dot -Tsvg "${output_dir}/alice-module-graph.dot" >/dev/null
fi
