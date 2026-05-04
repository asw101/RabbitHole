#!/usr/bin/env bash
# qa/outside-in/alice-desktop/tests/run-tests.sh
set -u

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
status=0

for test_file in "$SCRIPT_DIR"/test-*.sh; do
  printf '\n== %s ==\n' "${test_file#$SCRIPT_DIR/}"
  if bash "$test_file"; then
    printf 'PASS %s\n' "${test_file#$SCRIPT_DIR/}"
  else
    printf 'FAIL %s\n' "${test_file#$SCRIPT_DIR/}" >&2
    status=1
  fi
done

exit "$status"
