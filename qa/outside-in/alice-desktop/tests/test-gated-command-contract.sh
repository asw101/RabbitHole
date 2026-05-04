#!/usr/bin/env bash
# qa/outside-in/alice-desktop/tests/test-gated-command-contract.sh
set -u

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
BASE_DIR=$(CDPATH= cd -- "$SCRIPT_DIR/.." && pwd)
RUNNER="$BASE_DIR/runners/run-scenario.sh"
# shellcheck source=qa/outside-in/alice-desktop/tests/lib/assertions.sh
. "$SCRIPT_DIR/lib/assertions.sh"

tmp_root=$(create_scratch_root "$SCRIPT_DIR") || exit 1
trap 'rm -rf "$tmp_root"' EXIT

gated_evidence="$tmp_root/gated-evidence"
"$RUNNER" run alice-desktop-netbeans-package-smoke --evidence-dir "$gated_evidence" >"$tmp_root/gated.out" 2>"$tmp_root/gated.err"
status=$?
assert_success "$status" "gated command scenario does not execute heavy command by default"
run_dir=$(single_child_dir "$gated_evidence/alice-desktop-netbeans-package-smoke")
status=$?
assert_success "$status" "gated command scenario creates one evidence directory"
assert_file_exists "$run_dir/status.txt" "gated command scenario writes status.txt"
assert_file_exists "$run_dir/manual-evidence-checklist.txt" "gated command scenario writes fallback checklist"
assert_contains "$run_dir/status.txt" '^automationMode=gated-command-smoke$' "gated status records automation mode"
assert_contains "$run_dir/status.txt" '^outcome=gated-not-run$' "gated status records skipped command outcome"
assert_contains "$run_dir/status.txt" '^gate=ALICE_QA_RUN_GATED_SMOKES$' "gated status names enabling variable"

fake_bin="$tmp_root/bin"
mkdir -p "$fake_bin"
cat > "$fake_bin/mvn" <<'SH'
#!/usr/bin/env bash
printf 'gated-command-ran\n'
printf 'argv=%s\n' "$*"
SH
chmod +x "$fake_bin/mvn"

enabled_evidence="$tmp_root/enabled-evidence"
PATH="$fake_bin:$PATH" ALICE_QA_RUN_GATED_SMOKES=1 \
  "$RUNNER" run alice-desktop-project-io-smoke --evidence-dir "$enabled_evidence" >"$tmp_root/enabled.out" 2>"$tmp_root/enabled.err"
status=$?
assert_success "$status" "enabled gated command scenario executes argv directly"
enabled_run_dir=$(single_child_dir "$enabled_evidence/alice-desktop-project-io-smoke")
status=$?
assert_success "$status" "enabled gated command scenario creates one evidence directory"
assert_file_exists "$enabled_run_dir/command.log" "enabled gated command scenario writes command.log"
assert_contains "$enabled_run_dir/command.log" 'gated-command-ran' "enabled gated command captures command output"
assert_contains "$enabled_run_dir/status.txt" '^outcome=passed$' "enabled gated command records pass outcome"
assert_contains "$enabled_run_dir/status.txt" '^exitCode=0$' "enabled gated command records exit code"

finish
