#!/usr/bin/env bash
# File: drinkme/code-atlas/tests/test_source_truth_components.sh
#
# Unit contract tests for source-truth extraction and architecture graph generation.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/lib/atlas_contract_test_lib.sh"

output_dir="$(atlas_make_output_dir)"
trap 'atlas_cleanup_dir "${output_dir}"' EXIT

atlas_note "Generating atlas artifacts into ${output_dir}"
atlas_run_generator "${output_dir}"

source_truth="${output_dir}/alice-source-truth.md"
module_graph_mmd="${output_dir}/alice-module-graph.mmd"
module_graph_dot="${output_dir}/alice-module-graph.dot"

atlas_assert_contains "${source_truth}" 'rysweet/alice3-modernization' \
  "source-truth artifact must identify the repository"
atlas_assert_contains "${source_truth}" 'drinkme/code-atlas' \
  "source-truth artifact must identify the durable artifact root"
atlas_assert_contains "${source_truth}" 'No runtime changes|Do not modify Alice source' \
  "source-truth artifact must preserve the artifact-only lane"
atlas_assert_contains "${source_truth}" 'No source copying|Do not copy Alice source' \
  "source-truth artifact must prohibit copying Alice source into drinkme"

while IFS= read -r module_path; do
  atlas_assert_contains "${source_truth}" "${module_path}" \
    "source-truth artifact must list root reactor module ${module_path}"
  atlas_assert_contains "${module_graph_mmd}" "${module_path}" \
    "Mermaid graph must include root reactor module ${module_path}"
  atlas_assert_contains "${module_graph_dot}" "${module_path}" \
    "DOT graph must include root reactor module ${module_path}"
done < <(atlas_root_reactor_modules)

for profile_or_boundary in includeSims buildInstaller only-eclipse; do
  atlas_assert_contains "${source_truth}" "${profile_or_boundary}" \
    "source-truth artifact must describe ${profile_or_boundary}"
done

for profile_module in \
  'core-nonfree' \
  'core-nonfree/ide-nonfree' \
  'core-nonfree/resources-nonfree' \
  'core-nonfree/story-api-nonfree' \
  'core-nonfree/models-nonfree' \
  'installer'; do
  atlas_assert_contains "${module_graph_mmd}" "${profile_module}" \
    "Mermaid graph must include profile-gated module ${profile_module}"
  atlas_assert_contains "${module_graph_dot}" "${profile_module}" \
    "DOT graph must include profile-gated module ${profile_module}"
done

for seam in \
  'tweedle-lang/Grammar' \
  'ApplicationRoot' \
  'org.alice.ide.rootDirectory' \
  'nbm-maven-plugin' \
  'Pack200'; do
  atlas_assert_contains "${source_truth}" "${seam}" \
    "source-truth artifact must describe build/runtime seam ${seam}"
done

atlas_assert_contains "${module_graph_mmd}" 'subgraph|cluster|includeSims' \
  "Mermaid graph must visually distinguish profile boundaries"
atlas_assert_contains "${module_graph_dot}" 'subgraph|cluster_includeSims|includeSims' \
  "DOT graph must visually distinguish profile boundaries"

atlas_assert_not_contains "${source_truth}" "${ATLAS_REPO_ROOT}" \
  "source-truth artifact must use relative paths, not absolute local paths"
atlas_assert_not_contains "${module_graph_mmd}" "${ATLAS_REPO_ROOT}" \
  "Mermaid graph must use relative paths, not absolute local paths"
atlas_assert_not_contains "${module_graph_dot}" "${ATLAS_REPO_ROOT}" \
  "DOT graph must use relative paths, not absolute local paths"

if command -v dot >/dev/null 2>&1; then
  dot -Tsvg "${module_graph_dot}" >/dev/null
fi
