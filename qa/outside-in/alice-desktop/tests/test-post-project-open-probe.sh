#!/usr/bin/env bash
# qa/outside-in/alice-desktop/tests/test-post-project-open-probe.sh
set -u

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
BASE_DIR=$(CDPATH= cd -- "$SCRIPT_DIR/.." && pwd)
PROBE="$BASE_DIR/runners/post-project-open-probe.py"
# shellcheck source=qa/outside-in/alice-desktop/tests/lib/assertions.sh
. "$SCRIPT_DIR/lib/assertions.sh"

tmp_root=$(create_scratch_root "$SCRIPT_DIR") || exit 1
trap 'rm -rf "$tmp_root"' EXIT

# --- Helper: shared valid inventory with Alice 3 main window ---
write_alice_inventory() {
  local path=$1
  cat > "$path" <<'JSON'
{
  "status": "observed",
  "windows": [
    {
      "id": "41943050",
      "title": "Alice 3",
      "class": "sun-awt-X11-XFramePeer",
      "pid": 2468,
      "processName": "java",
      "geometry": {"x": 0, "y": 0, "width": 1280, "height": 900, "screen": 0}
    }
  ]
}
JSON
}

# --- Helper: tab-click observation with projectOpenObserved=true ---
write_tab_click_opened() {
  local path=$1
  cat > "$path" <<'JSON'
{
  "status": "observed",
  "blocker": "none",
  "projectOpenObserved": true,
  "projectOpenDetail": "Select Project frame is no longer present in the AT-SPI tree; project opening is observed.",
  "toggleTabNodeCount": 5
}
JSON
}

# --- Helper: tab-click observation with projectOpenObserved=false ---
write_tab_click_not_opened() {
  local path=$1
  cat > "$path" <<'JSON'
{
  "status": "observed",
  "blocker": "ok-button-not-clicked",
  "projectOpenObserved": false,
  "projectOpenDetail": "OK button click did not succeed."
}
JSON
}

# --- Helper: inventory with a non-Alice Java process/window only ---
write_non_alice_java_inventory() {
  local path=$1
  cat > "$path" <<'JSON'
{
  "status": "observed",
  "windows": [
    {
      "id": "41943051",
      "title": "Maven Test Harness",
      "class": "sun-awt-X11-XFramePeer",
      "pid": 1357,
      "processName": "java",
      "geometry": {"x": 20, "y": 20, "width": 800, "height": 600, "screen": 0}
    }
  ]
}
JSON
}

# --- Helper: inventory with an arbitrary Java process before the Alice 3 window ---
write_mixed_java_inventory() {
  local path=$1
  cat > "$path" <<'JSON'
{
  "status": "observed",
  "windows": [
    {
      "id": "41943051",
      "title": "Maven Test Harness",
      "class": "sun-awt-X11-XFramePeer",
      "pid": 1357,
      "processName": "java",
      "geometry": {"x": 20, "y": 20, "width": 800, "height": 600, "screen": 0}
    },
    {
      "id": "41943052",
      "title": "Alice 3",
      "class": "sun-awt-X11-XFramePeer",
      "pid": 2468,
      "processName": "java",
      "geometry": {"x": 0, "y": 0, "width": 1280, "height": 900, "screen": 0}
    }
  ]
}
JSON
}

# --- Helper: target-starter selection without target-specific open proof ---
write_target_selected_not_opened() {
  local path=$1
  cat > "$path" <<'JSON'
{
  "status": "observed",
  "blocker": "target-starter-open-not-observed",
  "projectOpenObserved": true,
  "projectOpenDetail": "Generic Select Project dismissal was observed, but Africa Full was not proven opened.",
  "targetStarter": {
    "displayName": "Africa Full",
    "repositoryPath": "core/resources/src/application/resources/starter-projects/AfricaFull.a3p"
  },
  "targetStarterSelected": true,
  "targetStarterOpenAttempted": true,
  "openedStarter": null,
  "evidenceStatus": "selected",
  "targetStarterBlocker": {
    "observedAtspiState": "Africa Full selection evidence exists, but openedStarter is not Africa Full.",
    "actionAttempted": "Click OK/Open after selecting Africa Full.",
    "expectedNextAction": "Observe projectOpenObserved=true with openedStarter set to Africa Full.",
    "reasonProgressStopped": "The generic main-window transition is insufficient target-specific proof."
  }
}
JSON
}

# ---- 1. Missing inventory → blocked ----
opened_tab1="$tmp_root/opened-tab1.json"
write_tab_click_opened "$opened_tab1"
missing_out="$tmp_root/missing-inventory-out.json"
python3 "$PROBE" "$tmp_root/no-inventory.json" "$opened_tab1" "$missing_out"
status=$?
assert_success "$status" "probe exits 0 when inventory file is missing"
assert_contains "$missing_out" '"status": "blocked"' "missing-inventory records blocked status"
assert_contains "$missing_out" '"blocker": "input-unreadable"' "missing-inventory names input-unreadable blocker"
assert_contains "$missing_out" '"postOpenWindowObserved": false' "missing-inventory does not claim post-open observed"
assert_contains "$missing_out" '"mainWindowObservationBlocker": "input-unreadable"' "missing-inventory names exact mainWindowObservationBlocker"

# ---- 2. Missing tab-click observation → blocked ----
inventory2="$tmp_root/inventory2.json"
write_alice_inventory "$inventory2"
missing_tab_out="$tmp_root/missing-tab-click-out.json"
python3 "$PROBE" "$inventory2" "$tmp_root/no-tab-click.json" "$missing_tab_out"
status=$?
assert_success "$status" "probe exits 0 when tab-click observation is missing"
assert_contains "$missing_tab_out" '"status": "blocked"' "missing-tab-click records blocked status"
assert_contains "$missing_tab_out" '"blocker": "input-unreadable"' "missing-tab-click names input-unreadable blocker"
assert_contains "$missing_tab_out" '"postOpenWindowObserved": false' "missing-tab-click does not claim post-open observed"

# ---- 3. Malformed inventory JSON → blocked ----
malformed_inv="$tmp_root/malformed-inv.json"
printf 'not-json\n' > "$malformed_inv"
opened_tab3="$tmp_root/opened-tab3.json"
write_tab_click_opened "$opened_tab3"
malformed_inv_out="$tmp_root/malformed-inv-out.json"
python3 "$PROBE" "$malformed_inv" "$opened_tab3" "$malformed_inv_out"
status=$?
assert_success "$status" "probe exits 0 for malformed inventory JSON"
assert_contains "$malformed_inv_out" '"status": "blocked"' "malformed-inventory records blocked status"
assert_contains "$malformed_inv_out" '"blocker": "input-unreadable"' "malformed-inventory names input-unreadable blocker"

# ---- 4. Malformed tab-click JSON → blocked ----
inventory4="$tmp_root/inventory4.json"
write_alice_inventory "$inventory4"
malformed_tab="$tmp_root/malformed-tab.json"
printf 'not-json\n' > "$malformed_tab"
malformed_tab_out="$tmp_root/malformed-tab-out.json"
python3 "$PROBE" "$inventory4" "$malformed_tab" "$malformed_tab_out"
status=$?
assert_success "$status" "probe exits 0 for malformed tab-click JSON"
assert_contains "$malformed_tab_out" '"status": "blocked"' "malformed-tab-click records blocked status"
assert_contains "$malformed_tab_out" '"blocker": "input-unreadable"' "malformed-tab-click names input-unreadable blocker"

# ---- 5. projectOpenObserved=false → blocked with project-not-opened ----
inventory5="$tmp_root/inventory5.json"
write_alice_inventory "$inventory5"
not_opened_tab="$tmp_root/not-opened-tab.json"
write_tab_click_not_opened "$not_opened_tab"
not_opened_out="$tmp_root/not-opened-out.json"
python3 "$PROBE" "$inventory5" "$not_opened_tab" "$not_opened_out"
status=$?
assert_success "$status" "probe exits 0 when project was not opened"
assert_contains "$not_opened_out" '"status": "blocked"' "project-not-opened records blocked status"
assert_contains "$not_opened_out" '"blocker": "project-not-opened"' "project-not-opened names exact blocker"
assert_contains "$not_opened_out" '"postOpenWindowObserved": false' "project-not-opened does not claim observation"
assert_contains "$not_opened_out" '"mainWindowObservationBlocker": "project-not-opened"' "project-not-opened names exact mainWindowObservationBlocker"
assert_contains "$not_opened_out" '"mainFrameNames": \[\]' "project-not-opened records empty mainFrameNames"
assert_contains "$not_opened_out" '"mainFrameChildCounts": \[\]' "project-not-opened records empty mainFrameChildCounts"

# ---- 6. No Alice 3 Java window in inventory → blocked with alice-window-java-pid-not-identified ----
no_java_inv="$tmp_root/no-java-inv.json"
cat > "$no_java_inv" <<'JSON'
{
  "status": "observed",
  "windows": [
    {
      "title": "Some Browser",
      "pid": 9999,
      "processName": "chromium",
      "geometry": {"width": 1280, "height": 900}
    }
  ]
}
JSON
opened_tab6="$tmp_root/opened-tab6.json"
write_tab_click_opened "$opened_tab6"
no_java_out="$tmp_root/no-java-out.json"
python3 "$PROBE" "$no_java_inv" "$opened_tab6" "$no_java_out"
status=$?
assert_success "$status" "probe exits 0 when no Java window is in inventory"
assert_contains "$no_java_out" '"status": "blocked"' "no-java-pid records blocked status"
assert_contains "$no_java_out" '"blocker": "alice-window-java-pid-not-identified"' "no-java-pid names exact blocker"
assert_contains "$no_java_out" 'Unable to identify the Java process for the Alice 3 main window' "no-java-pid explains missing Alice 3 window Java process"
assert_contains "$no_java_out" 'Refusing to introspect an arbitrary Java process' "no-java-pid refuses arbitrary Java introspection"
assert_contains "$no_java_out" '"postOpenWindowObserved": false' "no-java-pid does not claim post-open observed"
assert_contains "$no_java_out" '"mainWindowObservationBlocker": "alice-window-java-pid-not-identified"' "no-java-pid names exact mainWindowObservationBlocker"

# ---- 7. Non-Alice Java process is rejected instead of introspected ----
non_alice_java_inv="$tmp_root/non-alice-java-inv.json"
write_non_alice_java_inventory "$non_alice_java_inv"
opened_tab7="$tmp_root/opened-tab7.json"
write_tab_click_opened "$opened_tab7"
non_alice_java_out="$tmp_root/non-alice-java-out.json"
python3 "$PROBE" "$non_alice_java_inv" "$opened_tab7" "$non_alice_java_out"
status=$?
assert_success "$status" "probe exits 0 when only a non-Alice Java process is in inventory"
assert_contains "$non_alice_java_out" '"status": "blocked"' "non-Alice Java process records blocked status"
assert_contains "$non_alice_java_out" '"blocker": "alice-window-java-pid-not-identified"' "non-Alice Java process names exact blocker"
assert_contains "$non_alice_java_out" '"javaPid": null' "non-Alice Java process is not selected for introspection"
assert_contains "$non_alice_java_out" 'Unable to identify the Java process for the Alice 3 main window' "non-Alice Java blocker explains missing Alice 3 window Java process"
assert_contains "$non_alice_java_out" 'Refusing to introspect an arbitrary Java process' "non-Alice Java blocker refuses arbitrary Java introspection"
assert_contains "$non_alice_java_out" '"postOpenWindowObserved": false' "non-Alice Java process does not claim post-open observed"
assert_contains "$non_alice_java_out" '"mainWindowObservationBlocker": "alice-window-java-pid-not-identified"' "non-Alice Java process names exact mainWindowObservationBlocker"
assert_not_contains "$non_alice_java_out" '"javaPid": 1357' "non-Alice Java PID is not recorded as selected"

# ---- 8. Arbitrary Java process before Alice 3 window is not selected ----
mixed_java_inv="$tmp_root/mixed-java-inv.json"
write_mixed_java_inventory "$mixed_java_inv"
opened_tab8="$tmp_root/opened-tab8.json"
write_tab_click_opened "$opened_tab8"
mixed_java_out="$tmp_root/mixed-java-out.json"
python3 "$PROBE" "$mixed_java_inv" "$opened_tab8" "$mixed_java_out"
status=$?
assert_success "$status" "probe exits 0 when Alice window appears after a non-Alice Java process"
assert_contains "$mixed_java_out" '"javaPid": 2468' "mixed Java inventory selects the Alice 3 window PID"
assert_not_contains "$mixed_java_out" '"javaPid": 1357' "mixed Java inventory does not select the first arbitrary Java PID"

# ---- 9. pyatspi not installed → blocked with pyatspi-not-installed ----
# The probe falls through to probe_post_open when project is open and PID is found.
# Without a live AT-SPI session, pyatspi import fails on most test machines.
# We assert the probe exits 0 and records either pyatspi-not-installed or
# at-spi-registry-unavailable (both are legitimate blocked outcomes in CI).
inventory9="$tmp_root/inventory9.json"
write_alice_inventory "$inventory9"
opened_tab9="$tmp_root/opened-tab9.json"
write_tab_click_opened "$opened_tab9"
atk_out="$tmp_root/atk-out.json"
python3 "$PROBE" "$inventory9" "$opened_tab9" "$atk_out"
status=$?
assert_success "$status" "probe exits 0 when AT-SPI is not available in test environment"
assert_contains "$atk_out" '"status": "blocked"' "no-AT-SPI records blocked status"
assert_contains "$atk_out" '"postOpenWindowObserved": false' "no-AT-SPI does not claim post-open observed"
# The blocker is either pyatspi-not-installed or at-spi-registry-unavailable or atk-wrapper-not-loaded.
# Use a broad regex to capture all three legitimate blockers.
assert_contains "$atk_out" '"blocker": "(pyatspi-not-installed|at-spi-registry-unavailable|atk-wrapper-not-loaded)"' \
  "no-AT-SPI names a precise AT-SPI-related blocker"

# ---- 10. Generic main-window observation does not imply Africa Full proof ----
inventory10="$tmp_root/inventory10.json"
write_alice_inventory "$inventory10"
target_selected_tab="$tmp_root/target-selected-tab.json"
write_target_selected_not_opened "$target_selected_tab"
target_selected_out="$tmp_root/target-selected-out.json"
python3 "$PROBE" "$inventory10" "$target_selected_tab" "$target_selected_out"
status=$?
assert_success "$status" "probe exits 0 when Africa Full target evidence is selected but not opened"
assert_contains "$target_selected_out" '"status": "blocked"' "target-selected-not-opened records blocked status"
assert_contains "$target_selected_out" '"blocker": "target-starter-open-not-proven"' "target-selected-not-opened refuses generic main-window proof"
assert_contains "$target_selected_out" '"postOpenWindowObserved": false' "target-selected-not-opened does not claim post-open window observation"
assert_contains "$target_selected_out" '"mainWindowObservationBlocker": "target-starter-open-not-proven"' "target-selected-not-opened names exact mainWindowObservationBlocker"

# ---- 11. Ready post-open frame returns without fixed wait ----
python3 - "$PROBE" >"$tmp_root/post-open-polling.out" 2>"$tmp_root/post-open-polling.err" <<'PY'
import importlib.util
import sys
import types
from pathlib import Path


class FakeNode:
    def __init__(self, name, children=None, process_id=None):
        self.name = name
        self.children = list(children or [])
        self.process_id = process_id

    @property
    def childCount(self):
        return len(self.children)

    def getChildAtIndex(self, index):
        return self.children[index]

    def get_process_id(self):
        return self.process_id


class FakeRegistry:
    desktop = None

    @staticmethod
    def getDesktop(index):
        if index != 0:
            raise RuntimeError("only desktop 0 exists in this test")
        return FakeRegistry.desktop


def load_probe(path):
    spec = importlib.util.spec_from_file_location("post_project_open_probe_under_test", path)
    module = importlib.util.module_from_spec(spec)
    sys.modules["pyatspi"] = types.SimpleNamespace(Registry=FakeRegistry)
    spec.loader.exec_module(module)
    return module


probe = load_probe(Path(sys.argv[1]))
alice_app = FakeNode("", [FakeNode("Alice 3")], process_id=2468)
FakeRegistry.desktop = FakeNode("desktop", [alice_app])
sleep_calls = []
probe.time.sleep = lambda seconds: sleep_calls.append(seconds)

payload = probe.probe_post_open(2468)
if payload.get("status") != "observed":
    raise AssertionError(f"expected observed payload, got {payload!r}")
if payload.get("mainFrameNames") != ["Alice 3"]:
    raise AssertionError(f"unexpected frame names: {payload.get('mainFrameNames')!r}")
if sleep_calls:
    raise AssertionError(f"ready frame should not incur fixed sleeps, saw {sleep_calls!r}")
PY
assert_success "$?" "ready post-open frame is observed without fixed sleep"

# ---- 12. Probe output is valid JSON ----
for out_file in "$missing_out" "$missing_tab_out" "$malformed_inv_out" "$malformed_tab_out" \
                "$not_opened_out" "$no_java_out" "$non_alice_java_out" "$mixed_java_out" \
                "$atk_out" "$target_selected_out"; do
  python3 - "$out_file" <<'PY'
import json, sys
try:
    json.load(open(sys.argv[1]))
except Exception as e:
    print(f"invalid JSON in {sys.argv[1]}: {e}", file=sys.stderr)
    sys.exit(1)
PY
  assert_success "$?" "probe output is valid JSON: $(basename "$out_file")"
done

finish
