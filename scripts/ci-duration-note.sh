#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<'USAGE'
Usage: scripts/ci-duration-note.sh LABEL -- COMMAND [ARG...]

Runs COMMAND, preserves its exit status, and emits a GitHub Actions notice plus
an optional step-summary row with the elapsed wall-clock duration.
USAGE
}

error() {
  printf '[ci-duration] ERROR: %s\n' "$*" >&2
}

github_escape() {
  local value="$1"
  value="${value//'%'/'%25'}"
  value="${value//$'\r'/'%0D'}"
  value="${value//$'\n'/'%0A'}"
  printf '%s' "${value}"
}

markdown_cell_escape() {
  local value="$1"
  value="${value//$'\r'/' '}"
  value="${value//$'\n'/' '}"
  value="${value//'|'/'\|'}"
  printf '%s' "${value}"
}

if (($# < 3)); then
  usage >&2
  exit 2
fi

label="$1"
shift

if [[ -z "${label// }" ]]; then
  error "LABEL must not be blank."
  exit 2
fi

if [[ "$1" != "--" ]]; then
  error "Command must be separated from LABEL with --."
  usage >&2
  exit 2
fi
shift

if (($# == 0)); then
  error "Missing command after --."
  exit 2
fi

start_epoch=$(date +%s)
status=0
"$@" || status=$?
end_epoch=$(date +%s)
elapsed_seconds=$((end_epoch - start_epoch))

if ((status == 0)); then
  outcome="completed"
else
  outcome="failed"
fi

notice_label="$(github_escape "${label}")"
summary_label="$(markdown_cell_escape "${label}")"

printf '::notice title=CI duration::%s %s in %ss\n' "${notice_label}" "${outcome}" "${elapsed_seconds}"

if [[ -n "${GITHUB_STEP_SUMMARY:-}" ]]; then
  summary_dir="$(dirname -- "${GITHUB_STEP_SUMMARY}")"
  if [[ (-e "${GITHUB_STEP_SUMMARY}" && ! -w "${GITHUB_STEP_SUMMARY}") || ! -d "${summary_dir}" || ! -w "${summary_dir}" ]]; then
    printf '[ci-duration] WARNING: could not write GitHub step summary: %s\n' "${GITHUB_STEP_SUMMARY}" >&2
  else
    if ! {
      if ! grep -Fq '| Step | Outcome | Seconds |' "${GITHUB_STEP_SUMMARY}" 2>/dev/null; then
        printf '### CI duration\n\n'
        printf '| Step | Outcome | Seconds |\n'
        printf '| --- | --- | ---: |\n'
      fi
      printf '| %s | %s | %s |\n' "${summary_label}" "${outcome}" "${elapsed_seconds}"
    } >> "${GITHUB_STEP_SUMMARY}"; then
      printf '[ci-duration] WARNING: could not write GitHub step summary: %s\n' "${GITHUB_STEP_SUMMARY}" >&2
    fi
  fi
fi

exit "${status}"
