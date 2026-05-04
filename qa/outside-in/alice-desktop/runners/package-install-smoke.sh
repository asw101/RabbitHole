#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
REPO_ROOT=$(CDPATH= cd -- "$SCRIPT_DIR/../../../.." && pwd)

cd "$REPO_ROOT"

echo "== package command =="
echo "mvn -DincludeSims=false -Dinstall4j.skip -pl installer -am package"
mvn -DincludeSims=false -Dinstall4j.skip -pl installer -am package

echo
if [ -d installer/target ]; then
  echo "== installer/target artifacts =="
  find installer/target -maxdepth 3 -type f | sort
else
  echo "installer/target was not produced by the package command" >&2
  exit 1
fi

echo
if [ -d installer/media ]; then
  echo "== installer/media artifacts =="
  find installer/media -maxdepth 2 -type f | sort
else
  echo "installer/media not present; install4j media may be skipped in this environment"
fi

echo
cat <<'NOTE'
Install smoke follow-up: if an installer artifact is present, install it in a disposable profile or VM, launch Alice from the installed entry point once, and attach the install/launch logs to this scenario's evidence directory.
NOTE
