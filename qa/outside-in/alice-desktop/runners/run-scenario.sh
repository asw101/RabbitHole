#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
BASE_DIR=$(CDPATH= cd -- "$SCRIPT_DIR/.." && pwd)
REPO_ROOT=$(CDPATH= cd -- "$BASE_DIR/../../.." && pwd)
VALIDATOR="$SCRIPT_DIR/validate-scenarios.sh"

usage() {
  cat <<'EOF'
usage:
  run-scenario.sh list
  run-scenario.sh validate
  run-scenario.sh run <scenario-id> [--evidence-dir <dir>] [--timeout-seconds <seconds>]

Environment:
  ALICE_QA_DISPLAY       Reuse a specific X display, for example :99.
  ALICE_QA_SCREEN        Xvfb screen geometry, default 1280x900x24.
  ALICE_QA_READY_WAIT_SECONDS
                         Override GUI readiness wait before screenshot capture.
EOF
}

json_field() {
  local scenario_json=$1
  local field=$2
  SCENARIO_JSON="$scenario_json" python3 - "$field" <<'PY'
import json
import os
import sys

value = json.loads(os.environ["SCENARIO_JSON"])
for part in sys.argv[1].split("."):
    value = value[part]
print(value)
PY
}

write_checklist() {
  local scenario_json=$1
  local run_dir=$2
  SCENARIO_JSON="$scenario_json" RUN_DIR="$run_dir" python3 - <<'PY'
import json
import os
from pathlib import Path

scenario = json.loads(os.environ["SCENARIO_JSON"])
run_dir = Path(os.environ["RUN_DIR"])
path = run_dir / "manual-evidence-checklist.txt"

def section(lines, title, values):
    lines.append("")
    lines.append(title)
    lines.append("-" * len(title))
    for index, value in enumerate(values, 1):
        lines.append(f"{index}. {value}")

lines = [
    f"Scenario: {scenario['id']}",
    f"Title: {scenario['title']}",
    f"Workflow: {scenario['workflow']}",
    f"Automation mode: {scenario['automationMode']}",
]
section(lines, "Preconditions", scenario["preconditions"])
section(lines, "User actions", scenario["userActions"])
section(lines, "Expected outcomes", scenario["expectedOutcomes"])
section(lines, "Required evidence", scenario["evidence"]["required"])
section(lines, "Fallback notes", scenario["fallback"]["notes"])
section(
    lines,
    "Completion status",
    [
        "Checklist generation is not complete until required evidence is attached.",
        "A human performs the workflow, adds artifacts to this run directory, and records review notes.",
        "Human reviewer accepts the scenario only after the required evidence matches the expected outcomes.",
    ],
)

path.write_text("\n".join(lines) + "\n", encoding="utf-8")
print(path)
PY
}

write_manual_status() {
  local scenario_json=$1
  local run_dir=$2
  local checklist_path=$3

  {
    printf 'scenario=%s\n' "$(json_field "$scenario_json" "id")"
    printf 'automationMode=%s\n' "$(json_field "$scenario_json" "automationMode")"
    printf 'outcome=manual-evidence-required\n'
    printf 'checklist=%s\n' "$(basename "$checklist_path")"
  } > "$run_dir/status.txt"
}

validate_positive_integer() {
  local value=$1
  local label=$2
  if [[ ! "$value" =~ ^[1-9][0-9]*$ ]]; then
    printf 'invalid %s: must be a positive integer\n' "$label" >&2
    exit 2
  fi
}

write_environment() {
  local run_dir=$1
  {
    printf 'timestamp_utc=%s\n' "$(date -u +%Y-%m-%dT%H:%M:%SZ)"
    printf 'repo_root=%s\n' "$REPO_ROOT"
    printf 'display=%s\n' "${DISPLAY:-}"
    printf '\n[java]\n'
    java -version 2>&1 || true
    printf '\n[maven]\n'
    mvn -version 2>&1 || true
    printf '\n[uname]\n'
    uname -a 2>&1 || true
  } > "$run_dir/environment.txt"
}

select_display() {
  if [ -n "${ALICE_QA_DISPLAY:-}" ]; then
    printf '%s\n' "$ALICE_QA_DISPLAY"
    return 0
  fi

  local number
  for number in $(seq 90 120); do
    if [ ! -e "/tmp/.X${number}-lock" ]; then
      printf ':%s\n' "$number"
      return 0
    fi
  done

  return 1
}

capture_screenshot() {
  local output=$1
  if command -v import >/dev/null 2>&1; then
    import -window root "$output"
  elif command -v gnome-screenshot >/dev/null 2>&1; then
    gnome-screenshot --file "$output"
  elif command -v xwd >/dev/null 2>&1; then
    xwd -root -silent -out "${output%.png}.xwd"
  else
    return 3
  fi
}

run_xvfb_real_alice() {
  local scenario_json=$1
  local run_dir=$2
  local timeout_override=$3

  local command cwd configured_timeout ready_wait run_timeout display
  command=$(json_field "$scenario_json" "automation.command")
  cwd=$(json_field "$scenario_json" "automation.cwd")
  configured_timeout=$(json_field "$scenario_json" "automation.timeoutSeconds")
  ready_wait="${ALICE_QA_READY_WAIT_SECONDS:-$(json_field "$scenario_json" "automation.readyWaitSeconds")}"
  run_timeout="${timeout_override:-$configured_timeout}"
  xvfb_pid=
  alice_pid=

  if ! command -v Xvfb >/dev/null 2>&1; then
    write_checklist "$scenario_json" "$run_dir" >/dev/null
    printf 'Xvfb is not available; wrote manual fallback checklist to %s\n' "$run_dir" >&2
    return 2
  fi

  if ! display=$(select_display); then
    write_checklist "$scenario_json" "$run_dir" >/dev/null
    printf 'No free X display found; wrote manual fallback checklist to %s\n' "$run_dir" >&2
    return 2
  fi

  Xvfb "$display" -screen 0 "${ALICE_QA_SCREEN:-1280x900x24}" > "$run_dir/xvfb.log" 2>&1 &
  xvfb_pid=$!
  alice_pid=

  cleanup() {
    if [ -n "${alice_pid:-}" ] && kill -0 "$alice_pid" >/dev/null 2>&1; then
      kill "$alice_pid" >/dev/null 2>&1 || true
      wait "$alice_pid" >/dev/null 2>&1 || true
    fi
    if kill -0 "$xvfb_pid" >/dev/null 2>&1; then
      kill "$xvfb_pid" >/dev/null 2>&1 || true
      wait "$xvfb_pid" >/dev/null 2>&1 || true
    fi
  }
  trap cleanup EXIT

  sleep 2
  if ! kill -0 "$xvfb_pid" >/dev/null 2>&1; then
    write_checklist "$scenario_json" "$run_dir" >/dev/null
    printf 'Xvfb exited before Alice launch; see %s/xvfb.log\n' "$run_dir" >&2
    return 2
  fi

  export DISPLAY=$display
  write_environment "$run_dir"

  (
    cd "$REPO_ROOT/$cwd"
    timeout -k 10s "${run_timeout}s" bash -lc "$command"
  ) > "$run_dir/launch.log" 2>&1 &
  alice_pid=$!

  local ready_status=not-checked
  local waited=0
  if command -v xdotool >/dev/null 2>&1; then
    ready_status=not-found
    while [ "$waited" -lt "$ready_wait" ]; do
      if ! kill -0 "$alice_pid" >/dev/null 2>&1; then
        ready_status=process-exited
        break
      fi
      if xdotool search --onlyvisible --name Alice >/dev/null 2>&1; then
        ready_status=alice-window-found
        break
      elif xdotool search --onlyvisible --class ".*" >/dev/null 2>&1; then
        ready_status=visible-window-found
        break
      fi
      sleep 1
      waited=$((waited + 1))
    done
  else
    sleep "$ready_wait"
    ready_status=waited-without-window-detector
  fi

  local screenshot_status=screenshot-captured
  if ! capture_screenshot "$run_dir/screenshot.png" > "$run_dir/screenshot.log" 2>&1; then
    screenshot_status=screenshot-failed
    write_checklist "$scenario_json" "$run_dir" >/dev/null
  fi

  local process_status=running
  if ! kill -0 "$alice_pid" >/dev/null 2>&1; then
    process_status=exited-before-capture
  fi

  {
    printf 'scenario=%s\n' "$(json_field "$scenario_json" "id")"
    printf 'automationMode=%s\n' "$(json_field "$scenario_json" "automationMode")"
    printf 'display=%s\n' "$display"
    printf 'readyStatus=%s\n' "$ready_status"
    printf 'processStatus=%s\n' "$process_status"
    printf 'screenshotStatus=%s\n' "$screenshot_status"
    printf 'timeoutSeconds=%s\n' "$run_timeout"
  } > "$run_dir/status.txt"

  if [ "$screenshot_status" != screenshot-captured ]; then
    printf 'Screenshot capture failed; see %s/screenshot.log\n' "$run_dir" >&2
    return 2
  fi
  if [ "$process_status" != running ]; then
    printf 'Alice launch process exited before evidence capture; see %s/launch.log\n' "$run_dir" >&2
    return 1
  fi
  if [ "$ready_status" = process-exited ]; then
    printf 'Alice launch process exited before window readiness; see %s/launch.log\n' "$run_dir" >&2
    return 1
  fi
  if [ "$ready_status" = not-found ]; then
    printf 'No visible Alice desktop window was detected; see %s/status.txt\n' "$run_dir" >&2
    return 1
  fi

  printf 'Evidence written to %s\n' "$run_dir"
}

command_name=${1:-}
case "$command_name" in
  list)
    "$VALIDATOR" --list
    ;;
  validate)
    "$VALIDATOR"
    ;;
  run)
    shift
    scenario_id=${1:-}
    if [ -z "$scenario_id" ]; then
      usage >&2
      exit 2
    fi
    shift

    evidence_base="$BASE_DIR/evidence"
    timeout_override=
    while [ "$#" -gt 0 ]; do
      case "$1" in
        --evidence-dir)
          evidence_base=${2:?--evidence-dir requires a value}
          shift 2
          ;;
        --timeout-seconds)
          timeout_override=${2:?--timeout-seconds requires a value}
          validate_positive_integer "$timeout_override" "timeout"
          shift 2
          ;;
        *)
          printf 'unknown argument: %s\n' "$1" >&2
          usage >&2
          exit 2
          ;;
      esac
    done

    scenario_json=$("$VALIDATOR" --dump-json "$scenario_id")
    timestamp=$(date -u +%Y%m%dT%H%M%SZ)
    run_dir="$evidence_base/$scenario_id/$timestamp"
    mkdir -p "$run_dir"
    write_environment "$run_dir"

    automation_mode=$(json_field "$scenario_json" "automationMode")
    case "$automation_mode" in
      xvfb-real-alice)
        run_xvfb_real_alice "$scenario_json" "$run_dir" "$timeout_override"
        ;;
      command-wrapper|manual-evidence-required|unit-evidence-linked)
        checklist=$(write_checklist "$scenario_json" "$run_dir")
        write_manual_status "$scenario_json" "$run_dir" "$checklist"
        printf 'Manual or supporting-evidence scenario prepared: %s\n' "$checklist"
        ;;
      *)
        printf 'unsupported automationMode: %s\n' "$automation_mode" >&2
        exit 1
        ;;
    esac
    ;;
  -h|--help|help|"")
    usage
    ;;
  *)
    printf 'unknown command: %s\n' "$command_name" >&2
    usage >&2
    exit 2
    ;;
esac
