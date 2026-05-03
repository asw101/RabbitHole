#!/usr/bin/env bash
# File: drinkme/code-atlas/tests/test_hotspots_staleness_findings.sh
#
# Unit contract tests for hotspot analysis, staleness inventory, and finding classification.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/lib/atlas_contract_test_lib.sh"

output_dir="$(atlas_make_output_dir)"
trap 'atlas_cleanup_dir "${output_dir}"' EXIT

atlas_note "Generating atlas artifacts into ${output_dir}"
atlas_run_generator "${output_dir}"

hotspots="${output_dir}/alice-hotspots.md"
staleness="${output_dir}/alice-staleness-map.md"
findings="${output_dir}/alice-bughunt-findings.md"

largest_java_source="$(atlas_largest_java_source_path)"
if [[ -z "${largest_java_source}" ]]; then
  atlas_fail "could not compute largest Java source file"
fi

atlas_assert_contains "${hotspots}" "${largest_java_source}" \
  "hotspot artifact must include the current largest non-target Java source file"
atlas_assert_contains "${hotspots}" 'Characterization tests before refactor|characterization tests before refactor' \
  "hotspot artifact must require characterization tests before hotspot refactors"
atlas_assert_contains "${hotspots}" 'module.*LOC|Approx.*module.*LOC|Module.*Java LOC' \
  "hotspot artifact must include module-level LOC metrics"
atlas_assert_not_contains "${hotspots}" '/target/|target/classes|target/generated' \
  "hotspot metrics must exclude generated target output"

for marker in TODO FIXME HACK Deprecated deprecated; do
  atlas_assert_contains "${staleness}" "${marker}" \
    "staleness artifact must classify ${marker} markers"
done

for stale_surface in README 'Pack200' 'Generated.*target|target/' 'docs'; do
  atlas_assert_contains "${staleness}" "${stale_surface}" \
    "staleness artifact must include ${stale_surface} drift checks"
done

for required_field in Finding Severity Status Evidence Impact 'Suggested next action'; do
  atlas_assert_contains "${findings}" "${required_field}" \
    "bug-hunt findings must include ${required_field} field"
done

atlas_assert_contains "${findings}" 'Status: *(candidate|needs-attention|confirmed|speculative)' \
  "bug-hunt findings must use an explicit evidence status"
atlas_assert_contains "${findings}" 'NetBeans|optional nonfree|models-nonfree|story-api-nonfree' \
  "bug-hunt findings must track the NetBeans optional nonfree classpath candidate"

if grep -A12 -Ei 'NetBeans|optional nonfree|models-nonfree|story-api-nonfree' "${findings}" \
  | grep -Eq 'Status: *confirmed'; then
  atlas_fail "NetBeans optional nonfree mismatch must remain candidate/needs-attention until reproduced"
fi

atlas_assert_not_contains "${findings}" 'TheAliceProject/alice3' \
  "bug-hunt findings must not target the upstream Alice repository"
atlas_assert_not_contains "${findings}" "${ATLAS_REPO_ROOT}" \
  "bug-hunt findings must use relative paths, not absolute local paths"
