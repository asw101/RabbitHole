#!/usr/bin/env bash
# qa/outside-in/alice-desktop/tests/test-swing-widget-probe.sh
set -u

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
BASE_DIR=$(CDPATH= cd -- "$SCRIPT_DIR/.." && pwd)
PROBE="$BASE_DIR/runners/swing-widget-probe.py"
# shellcheck source=qa/outside-in/alice-desktop/tests/lib/assertions.sh
. "$SCRIPT_DIR/lib/assertions.sh"

tmp_root=$(create_scratch_root "$SCRIPT_DIR") || exit 1
trap 'rm -rf "$tmp_root"' EXIT

assert_json_field() {
  local path=$1
  local field=$2
  local expected=$3
  local label=$4
  local actual
  actual=$(python3 - "$path" "$field" <<'PY'
import json, sys
data = json.loads(open(sys.argv[1], encoding="utf-8").read())
for part in sys.argv[2].split("."):
    data = data.get(part, "") if isinstance(data, dict) else ""
print(data)
PY
  )
  if [ "$actual" = "$expected" ]; then
    pass "$label"
  else
    fail "$label (expected '$expected', got '$actual')"
  fi
}

# --- missing inventory file → blocked ---
missing_out="$tmp_root/missing-out.json"
python3 "$PROBE" "$tmp_root/does-not-exist.json" "$missing_out" >/dev/null 2>&1
status=$?
assert_success "$status" "probe exits 0 even when inventory file is missing"
assert_file_exists "$missing_out" "probe writes output when inventory is missing"
assert_json_field "$missing_out" status "blocked" "missing-inventory probe status is blocked"
assert_json_field "$missing_out" blocker "inventory-unreadable" "missing-inventory probe blocker names inventory-unreadable"

# --- malformed JSON inventory → blocked ---
malformed_inventory="$tmp_root/malformed.json"
printf 'not valid json\n' > "$malformed_inventory"
malformed_out="$tmp_root/malformed-out.json"
python3 "$PROBE" "$malformed_inventory" "$malformed_out" >/dev/null 2>&1
status=$?
assert_success "$status" "probe exits 0 for malformed inventory JSON"
assert_file_exists "$malformed_out" "probe writes output for malformed inventory"
assert_json_field "$malformed_out" status "blocked" "malformed-inventory probe status is blocked"
assert_json_field "$malformed_out" blocker "inventory-unreadable" "malformed-inventory probe blocker is inventory-unreadable"

# --- inventory with no windows → not-observed ---
empty_windows_inventory="$tmp_root/empty-windows.json"
printf '{"status":"observed","windows":[]}\n' > "$empty_windows_inventory"
empty_windows_out="$tmp_root/empty-windows-out.json"
python3 "$PROBE" "$empty_windows_inventory" "$empty_windows_out" >/dev/null 2>&1
status=$?
assert_success "$status" "probe exits 0 for empty windows inventory"
assert_json_field "$empty_windows_out" status "not-observed" "empty-windows probe status is not-observed"
assert_json_field "$empty_windows_out" blocker "select-project-window-not-in-inventory" "empty-windows probe blocker names missing window"

# --- inventory with non-Java Select Project window → not-observed ---
non_java_inventory="$tmp_root/non-java.json"
cat > "$non_java_inventory" <<'JSON'
{
  "status": "observed",
  "windows": [
    {
      "id": "1234",
      "title": "Select Project",
      "class": "SomeApp",
      "pid": 9999,
      "processName": "notjava",
      "geometry": {"x": 0, "y": 0, "width": 800, "height": 600}
    }
  ]
}
JSON
non_java_out="$tmp_root/non-java-out.json"
python3 "$PROBE" "$non_java_inventory" "$non_java_out" >/dev/null 2>&1
status=$?
assert_success "$status" "probe exits 0 for non-Java Select Project window"
assert_json_field "$non_java_out" status "not-observed" "non-Java Select Project probe status is not-observed"
assert_json_field "$non_java_out" blocker "select-project-window-not-in-inventory" "non-Java Select Project probe names missing java window"

# --- inventory with Java Select Project window → triggers AT-SPI probe ---
# Expected results depend on environment:
# - If pyatspi is not installed: blocker=pyatspi-not-installed
# - If pyatspi is installed but Alice is not running: blocker=atk-wrapper-not-loaded or at-spi-registry-unavailable
# - If Alice is running with ATK wrapper: status=observed with tabLabels
java_sp_inventory="$tmp_root/java-sp.json"
cat > "$java_sp_inventory" <<'JSON'
{
  "status": "observed",
  "windows": [
    {
      "id": "5678",
      "title": "Select Project",
      "class": "SunAwtFrame",
      "pid": 12345,
      "processName": "java",
      "geometry": {"x": 100, "y": 100, "width": 900, "height": 600}
    }
  ]
}
JSON
java_sp_out="$tmp_root/java-sp-out.json"
python3 "$PROBE" "$java_sp_inventory" "$java_sp_out" >/dev/null 2>&1
status=$?
assert_success "$status" "probe exits 0 for Java Select Project window inventory"
assert_file_exists "$java_sp_out" "probe writes swing-widget-observation for Java Select Project window"

# Validate the output has the required structural fields regardless of AT-SPI outcome
python3 - "$java_sp_out" <<'PY'
import json, sys
data = json.loads(open(sys.argv[1], encoding="utf-8").read())
required = ["status", "blocker", "blockerDetail", "tabLabels", "widgetLabels", "atkWrapperJar"]
for field in required:
    assert field in data, f"missing required field: {field}"
assert data["status"] in ("observed", "blocked", "not-observed"), f"unexpected status: {data['status']!r}"
assert isinstance(data["tabLabels"], list), "tabLabels must be a list"
assert isinstance(data["widgetLabels"], list), "widgetLabels must be a list"
assert data["atkWrapperJar"] == "/usr/share/java/java-atk-wrapper.jar", \
    f"atkWrapperJar must be the standard path, got {data['atkWrapperJar']!r}"
PY
assert_success "$?" "probe output for Java Select Project window has all required structural fields"

known_blockers="pyatspi-not-installed at-spi-registry-unavailable at-spi-desktop-enumeration-failed atk-wrapper-not-loaded select-project-not-accessible none"
actual_blocker=$(python3 - "$java_sp_out" <<'PY'
import json, sys
print(json.loads(open(sys.argv[1], encoding="utf-8").read()).get("blocker", ""))
PY
)
blocker_known=0
for b in $known_blockers; do
  if [ "$actual_blocker" = "$b" ]; then
    blocker_known=1
    break
  fi
done
if [ "$blocker_known" -eq 1 ]; then
  pass "probe blocker value '$actual_blocker' is a known machine-readable blocker token"
else
  fail "probe blocker '$actual_blocker' is not a recognized blocker token"
fi

# If atk-wrapper-not-loaded, verify the blockerDetail names the exact JAVA_TOOL_OPTIONS needed
actual_status=$(python3 - "$java_sp_out" <<'PY'
import json, sys
print(json.loads(open(sys.argv[1], encoding="utf-8").read()).get("status", ""))
PY
)
if [ "$actual_blocker" = "atk-wrapper-not-loaded" ]; then
  assert_contains "$java_sp_out" 'JAVA_TOOL_OPTIONS' "atk-wrapper-not-loaded blocker detail names JAVA_TOOL_OPTIONS"
  assert_contains "$java_sp_out" 'java-atk-wrapper.jar' "atk-wrapper-not-loaded blocker detail names the ATK wrapper jar path"
  assert_contains "$java_sp_out" 'org.GNOME.Accessibility.AtkWrapper' "atk-wrapper-not-loaded blocker detail names the ATK wrapper class"
fi

# If observed, verify tab labels are present
if [ "$actual_status" = "observed" ]; then
  python3 - "$java_sp_out" <<'PY'
import json, sys
data = json.loads(open(sys.argv[1], encoding="utf-8").read())
assert "tabLabels" in data, "observed output must have tabLabels"
assert "tabLabelMatch" in data, "observed output must have tabLabelMatch"
assert "selectProjectFrameName" in data, "observed output must name the frame"
PY
  assert_success "$?" "observed output has tabLabels, tabLabelMatch, and selectProjectFrameName"
fi

# --- output file is valid JSON with no extra trailing content ---
python3 - "$java_sp_out" <<'PY'
import json, sys
text = open(sys.argv[1], encoding="utf-8").read()
json.loads(text)  # raises if invalid
assert text.endswith("\n"), "output must end with newline"
PY
assert_success "$?" "probe output is valid JSON ending with newline"

[ "$failures" -eq 0 ] || exit 1
