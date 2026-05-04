#!/usr/bin/env bash
# File: drinkme/code-atlas/tests/lib/atlas_contract_test_lib.sh
#
# Shared helpers for Alice code-atlas contract tests.

set -u

ATLAS_TEST_LIB_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ATLAS_REPO_ROOT="$(cd "${ATLAS_TEST_LIB_DIR}/../../../.." && pwd)"
ATLAS_EXPECTED_ARTIFACTS=(
  "alice-source-truth.md"
  "alice-module-graph.mmd"
  "alice-module-graph.dot"
  "alice-hotspots.md"
  "alice-staleness-map.md"
  "alice-bughunt-findings.md"
)
ATLAS_SOURCE_ROOTS=(core alice-ide netbeans installer external core-nonfree)

: "${ATLAS_COMMAND:=${ATLAS_REPO_ROOT}/drinkme/code-atlas/bin/build-alice-code-atlas}"

atlas_fail() {
  printf 'not ok - %s\n' "$*" >&2
  return 1
}

atlas_note() {
  printf '# %s\n' "$*" >&2
}

atlas_make_output_dir() {
  local attempt
  local candidate

  for attempt in {1..20}; do
    candidate="${ATLAS_REPO_ROOT}/drinkme/code-atlas/.atlas-contract.$$.$RANDOM.${attempt}"
    if [[ ! -e "${candidate}" ]] && mkdir "${candidate}"; then
      printf '%s\n' "${candidate}"
      return
    fi
  done

  atlas_fail "could not create atlas contract output directory under drinkme/code-atlas"
}

atlas_cleanup_dir() {
  local path="$1"
  case "${path}" in
    "${ATLAS_REPO_ROOT}"/drinkme/code-atlas/.atlas-contract.*)
      [[ -d "${path}" ]] && rm -rf "${path}"
      ;;
  esac
}

atlas_require_generator_command() {
  if [[ ! -x "${ATLAS_COMMAND}" ]]; then
    atlas_fail "missing executable atlas generator command: ${ATLAS_COMMAND}"
  fi
}

atlas_run_generator() {
  local output_dir="$1"

  atlas_require_generator_command
  "${ATLAS_COMMAND}" \
    --repo-root "${ATLAS_REPO_ROOT}" \
    --output-dir "${output_dir}" \
    --format both
}

atlas_assert_file_exists() {
  local file_path="$1"
  if [[ ! -f "${file_path}" ]]; then
    atlas_fail "expected file to exist: ${file_path}"
  fi
}

atlas_assert_contains() {
  local file_path="$1"
  local pattern="$2"
  local reason="${3:-expected pattern not found}"

  atlas_assert_file_exists "${file_path}"
  if ! grep -Eq "${pattern}" "${file_path}"; then
    atlas_fail "${reason}: ${pattern} in ${file_path}"
  fi
}

atlas_assert_not_contains() {
  local file_path="$1"
  local pattern="$2"
  local reason="${3:-unexpected pattern found}"

  atlas_assert_file_exists "${file_path}"
  if grep -Eq "${pattern}" "${file_path}"; then
    atlas_fail "${reason}: ${pattern} in ${file_path}"
  fi
}

atlas_assert_tree_not_contains() {
  local root_path="$1"
  local pattern="$2"
  local reason="${3:-unexpected pattern found in tree}"

  if grep -R -E "${pattern}" "${root_path}" >/dev/null 2>&1; then
    atlas_fail "${reason}: ${pattern} under ${root_path}"
  fi
}

atlas_root_reactor_modules() {
  awk '
    /<modules>/ {
      in_modules = 1
      next
    }
    /<\/modules>/ {
      if (in_modules) {
        exit
      }
    }
    in_modules && /<module>/ {
      line = $0
      sub(/^.*<module>/, "", line)
      sub(/<\/module>.*$/, "", line)
      print line
    }
  ' "${ATLAS_REPO_ROOT}/pom.xml"
}

atlas_largest_java_source_path() {
  local roots=()
  local root
  for root in "${ATLAS_SOURCE_ROOTS[@]}"; do
    [[ -d "${ATLAS_REPO_ROOT}/${root}" ]] && roots+=("${ATLAS_REPO_ROOT}/${root}")
  done

  [[ "${#roots[@]}" -gt 0 ]] || return 0

  find "${roots[@]}" \
        -path '*/src/*/java/*' \
        -name '*.java' \
        -type f \
        -not -path '*/target/*' \
        -print0 \
    | xargs -0 -r -n 100000 wc -l \
    | awk -v root="${ATLAS_REPO_ROOT}/" '$2 != "total" && $1 > max {
        max = $1
        path = $2
      }
      END {
        if (path != "") {
          sub(root, "", path)
          print path
        }
      }'
}

atlas_run_expect_failure() {
  local log_file="$1"
  shift

  set +e
  "$@" >"${log_file}" 2>&1
  local status=$?
  set -e

  if [[ "${status}" -eq 0 ]]; then
    atlas_fail "expected command to fail: $*"
  fi
}
