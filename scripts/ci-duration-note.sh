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

printf '::notice title=CI duration::%s %s in %ss\n' "${label}" "${outcome}" "${elapsed_seconds}"

if [[ -n "${GITHUB_STEP_SUMMARY:-}" ]]; then
  {
    printf '### CI duration\n\n'
    printf '| Step | Outcome | Seconds |\n'
    printf '| --- | --- | ---: |\n'
    printf '| %s | %s | %s |\n' "${label}" "${outcome}" "${elapsed_seconds}"
  } >> "${GITHUB_STEP_SUMMARY}"
fi

exit "${status}"
