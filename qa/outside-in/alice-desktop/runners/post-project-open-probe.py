#!/usr/bin/env python3
"""Probe the Alice 3 main window AT-SPI state after Select Project is dismissed.

Reads x-window-inventory.json (to recover the Java PID) and
tab-click-observation.json (to confirm projectOpenObserved=true).  If the
project-open step was not observed, records an explicit blocker instead of
connecting to AT-SPI.

When projectOpenObserved=true this probe connects to the AT-SPI registry,
finds the Alice application by PID, and enumerates all top-level frame
children to characterise the IDE window state after project load.  It
records:

  postOpenWindowObserved (bool)
      true  – the alice_app is still accessible in AT-SPI and exposes at
              least one top-level frame whose name is NOT "Select Project".
  mainFrameNames (list[str])
      Names of the top-level AT-SPI frames/windows visible after project open.
  mainFrameChildCounts (list[int])
      childCount for each frame in mainFrameNames (shallow widget-presence
      signal without a deep tree walk).
  mainWindowObservationBlocker (str)
      "none" when postOpenWindowObserved=true, otherwise the machine-readable
      reason this step could not be proved.

Outputs post-project-open-observation.json.

Requires:
  - python3-pyatspi installed (sudo apt-get install -y python3-pyatspi)
  - Alice launched with exec:exec@alice-ide-atk (NO_AT_BRIDGE=1,
    -Xbootclasspath/a:/usr/share/java/java-atk-wrapper.jar)
  - tab-click-probe.py to have produced a tab-click-observation.json with
    projectOpenObserved=true.
"""

from __future__ import annotations

import argparse
import json
import time
from pathlib import Path
from typing import Any

EXPECTED_SELECT_PROJECT_TITLE = "Select Project"
EXPECTED_ALICE_TITLE = "Alice 3"
POST_OPEN_WAIT_SECONDS = 5


def find_java_pid(inventory: dict[str, Any]) -> int | None:
    """Return the PID of any Java window in the inventory, preferring 'Alice 3'."""
    windows = inventory.get("windows", [])
    if not isinstance(windows, list):
        return None
    # Prefer the primary Alice 3 window.
    for window in windows:
        if not isinstance(window, dict):
            continue
        if (
            str(window.get("title", "")) == EXPECTED_ALICE_TITLE
            and str(window.get("processName", "")).lower() == "java"
        ):
            pid = window.get("pid")
            if isinstance(pid, int) and pid > 0:
                return pid
    # Fall back to any Java window (e.g., the Select Project dialog shares PID).
    for window in windows:
        if not isinstance(window, dict):
            continue
        if str(window.get("processName", "")).lower() == "java":
            pid = window.get("pid")
            if isinstance(pid, int) and pid > 0:
                return pid
    return None


def probe_post_open(java_pid: int) -> dict[str, Any]:
    """Connect to AT-SPI and enumerate alice_app frames after project open."""
    try:
        import pyatspi  # noqa: PLC0415
    except ImportError:
        return {
            "status": "blocked",
            "blocker": "pyatspi-not-installed",
            "blockerDetail": "python3-pyatspi is not installed.",
            "javaPid": java_pid,
            "postOpenWindowObserved": False,
            "mainFrameNames": [],
            "mainFrameChildCounts": [],
            "mainWindowObservationBlocker": "pyatspi-not-installed",
        }

    try:
        desktop = pyatspi.Registry.getDesktop(0)
    except Exception as exc:
        return {
            "status": "blocked",
            "blocker": "at-spi-registry-unavailable",
            "blockerDetail": f"Cannot connect to AT-SPI registry: {exc}",
            "javaPid": java_pid,
            "postOpenWindowObserved": False,
            "mainFrameNames": [],
            "mainFrameChildCounts": [],
            "mainWindowObservationBlocker": "at-spi-registry-unavailable",
        }

    # Find Alice by PID.
    alice_app = None
    app_count = 0
    for _attempt in range(5):
        try:
            app_count = desktop.childCount
            for i in range(app_count):
                try:
                    app = desktop.getChildAtIndex(i)
                    if app is None:
                        continue
                    try:
                        app_pid = app.get_process_id()
                    except Exception:
                        app_pid = None
                    if app_pid == java_pid:
                        alice_app = app
                        break
                except Exception:
                    continue
        except Exception:
            pass
        if alice_app is not None and alice_app.childCount > 0:
            break
        time.sleep(2)

    if alice_app is None:
        return {
            "status": "blocked",
            "blocker": "atk-wrapper-not-loaded",
            "blockerDetail": (
                f"Java process PID {java_pid} not found in AT-SPI registry "
                f"({app_count} total AT-SPI apps visible)."
            ),
            "javaPid": java_pid,
            "postOpenWindowObserved": False,
            "mainFrameNames": [],
            "mainFrameChildCounts": [],
            "mainWindowObservationBlocker": "atk-wrapper-not-loaded",
        }

    # Wait briefly to allow Alice to finish loading the project.
    time.sleep(POST_OPEN_WAIT_SECONDS)

    # Enumerate all top-level frames.
    frame_names: list[str] = []
    frame_child_counts: list[int] = []
    for i in range(min(alice_app.childCount, 20)):
        try:
            child = alice_app.getChildAtIndex(i)
            if child is None:
                continue
            name = ""
            child_count = 0
            try:
                name = child.name or ""
            except Exception:
                pass
            try:
                child_count = child.childCount
            except Exception:
                pass
            frame_names.append(name)
            frame_child_counts.append(child_count)
        except Exception:
            continue

    # The proof criterion: at least one frame present that is NOT "Select Project".
    non_select_project_frames = [
        n for n in frame_names if n != EXPECTED_SELECT_PROJECT_TITLE
    ]
    post_open_observed = bool(non_select_project_frames)
    blocker = "none" if post_open_observed else "no-non-select-project-frame-visible"

    return {
        "status": "observed" if post_open_observed else "not-observed",
        "blocker": blocker,
        "blockerDetail": (
            ""
            if post_open_observed
            else (
                "After project-open wait, no top-level AT-SPI frame other than "
                f"'Select Project' is visible. Frames seen: {frame_names}"
            )
        ),
        "javaPid": java_pid,
        "postOpenWindowObserved": post_open_observed,
        "mainFrameNames": frame_names,
        "mainFrameChildCounts": frame_child_counts,
        "mainWindowObservationBlocker": blocker,
    }


def blocked_payload(path: Path, exc: Exception) -> dict[str, Any]:
    return {
        "status": "blocked",
        "blocker": "input-unreadable",
        "blockerDetail": f"Could not read {path}: {exc}",
        "javaPid": None,
        "postOpenWindowObserved": False,
        "mainFrameNames": [],
        "mainFrameChildCounts": [],
        "mainWindowObservationBlocker": "input-unreadable",
    }


def project_not_opened_payload(tab_click_path: Path) -> dict[str, Any]:
    return {
        "status": "blocked",
        "blocker": "project-not-opened",
        "blockerDetail": (
            f"{tab_click_path.name} does not record projectOpenObserved=true; "
            "post-project-open window state cannot be proved without a prior "
            "confirmed project open."
        ),
        "javaPid": None,
        "postOpenWindowObserved": False,
        "mainFrameNames": [],
        "mainFrameChildCounts": [],
        "mainWindowObservationBlocker": "project-not-opened",
    }


def no_java_pid_payload(inventory_path: Path) -> dict[str, Any]:
    return {
        "status": "blocked",
        "blocker": "java-pid-not-in-inventory",
        "blockerDetail": (
            f"No Java window found in {inventory_path.name}; "
            "cannot identify the Alice process for AT-SPI introspection."
        ),
        "javaPid": None,
        "postOpenWindowObserved": False,
        "mainFrameNames": [],
        "mainFrameChildCounts": [],
        "mainWindowObservationBlocker": "java-pid-not-in-inventory",
    }


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("inventory", help="Path to x-window-inventory.json")
    parser.add_argument("tab_click", help="Path to tab-click-observation.json")
    parser.add_argument("output", help="Path to write post-project-open-observation.json")
    args = parser.parse_args()

    inventory_path = Path(args.inventory)
    tab_click_path = Path(args.tab_click)
    output_path = Path(args.output)

    # Load inventory.
    try:
        inventory = json.loads(inventory_path.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as exc:
        payload = blocked_payload(inventory_path, exc)
        output_path.write_text(
            json.dumps(payload, indent=2, sort_keys=True) + "\n", encoding="utf-8"
        )
        return 0

    # Load tab-click observation.
    try:
        tab_click = json.loads(tab_click_path.read_text(encoding="utf-8"))
    except (OSError, json.JSONDecodeError) as exc:
        payload = blocked_payload(tab_click_path, exc)
        output_path.write_text(
            json.dumps(payload, indent=2, sort_keys=True) + "\n", encoding="utf-8"
        )
        return 0

    # Require projectOpenObserved=true before connecting to AT-SPI.
    if not tab_click.get("projectOpenObserved", False):
        payload = project_not_opened_payload(tab_click_path)
        output_path.write_text(
            json.dumps(payload, indent=2, sort_keys=True) + "\n", encoding="utf-8"
        )
        return 0

    java_pid = find_java_pid(inventory)
    if java_pid is None:
        payload = no_java_pid_payload(inventory_path)
        output_path.write_text(
            json.dumps(payload, indent=2, sort_keys=True) + "\n", encoding="utf-8"
        )
        return 0

    payload = probe_post_open(java_pid)
    output_path.write_text(
        json.dumps(payload, indent=2, sort_keys=True) + "\n", encoding="utf-8"
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
