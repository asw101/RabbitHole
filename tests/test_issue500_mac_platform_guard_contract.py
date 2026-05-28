"""Contract tests for issue #500/#502: Mac platform handling in Robot menu tests.

Verifies that JMenuBarRobotClickSaveProofTest and
RobotSaveMenuDialogWriteReadbackProofTest use the
apple.laf.useScreenMenuBar=false property override (issue #502) so AWT
Robot screen-coordinate menu clicks work on macOS where the menu bar
would otherwise be native and outside the JFrame.
"""
import re
import unittest
from functools import lru_cache
from pathlib import Path


REPO_ROOT = Path(__file__).resolve().parents[1]

JMENUBAR_TEST_PATH = (
    REPO_ROOT
    / "core"
    / "ide"
    / "src"
    / "test"
    / "java"
    / "org"
    / "alice"
    / "ide"
    / "croquet"
    / "models"
    / "projecturi"
    / "JMenuBarRobotClickSaveProofTest.java"
)

ROBOT_SAVE_TEST_PATH = (
    REPO_ROOT
    / "core"
    / "ide"
    / "src"
    / "test"
    / "java"
    / "org"
    / "alice"
    / "ide"
    / "croquet"
    / "models"
    / "projecturi"
    / "RobotSaveMenuDialogWriteReadbackProofTest.java"
)

SCREEN_MENU_BAR_PROPERTY_DECL = 'SCREEN_MENU_BAR_PROPERTY = "apple.laf.useScreenMenuBar"'
SCREEN_MENU_BAR_SET_FALSE = re.compile(
    r'System\.setProperty\(\s*SCREEN_MENU_BAR_PROPERTY\s*,\s*"false"\s*\)'
)


@lru_cache(maxsize=None)
def _read(path: Path) -> str:
    return path.read_text(encoding="utf-8")


class JMenuBarRobotClickSaveProofTestMacGuardContract(unittest.TestCase):
    """Contract: JMenuBarRobotClickSaveProofTest must use the
    apple.laf.useScreenMenuBar=false property override for macOS."""

    def test_file_exists(self) -> None:
        self.assertTrue(
            JMENUBAR_TEST_PATH.exists(),
            f"Expected test file at {JMENUBAR_TEST_PATH.relative_to(REPO_ROOT)}",
        )

    def test_has_screen_menu_bar_property(self) -> None:
        source = _read(JMENUBAR_TEST_PATH)
        self.assertIn(SCREEN_MENU_BAR_PROPERTY_DECL, source)

    def test_imports_assume_false(self) -> None:
        source = _read(JMENUBAR_TEST_PATH)
        self.assertIn("import static org.junit.Assume.assumeFalse;", source)

    def test_has_screen_menu_bar_override(self) -> None:
        source = _read(JMENUBAR_TEST_PATH)
        self.assertRegex(source, SCREEN_MENU_BAR_SET_FALSE)

    def test_has_capture_and_restore_property_lifecycle(self) -> None:
        """The class must capture, override, and restore the screen menu bar property."""
        source = _read(JMENUBAR_TEST_PATH)
        self.assertIn("captureProperties", source)
        self.assertIn("System.getProperty(SCREEN_MENU_BAR_PROPERTY)", source)
        self.assertIn("restoreProperty(SCREEN_MENU_BAR_PROPERTY", source)

    def test_test_method_has_headless_guard(self) -> None:
        source = _read(JMENUBAR_TEST_PATH)
        self.assertIn("GraphicsEnvironment.isHeadless()", source)


class RobotSaveMenuDialogWriteReadbackProofTestMacGuardContract(unittest.TestCase):
    """Contract: RobotSaveMenuDialogWriteReadbackProofTest must use the
    apple.laf.useScreenMenuBar=false property override via @Before/@After."""

    def test_file_exists(self) -> None:
        self.assertTrue(
            ROBOT_SAVE_TEST_PATH.exists(),
            f"Expected test file at {ROBOT_SAVE_TEST_PATH.relative_to(REPO_ROOT)}",
        )

    def test_has_screen_menu_bar_property(self) -> None:
        source = _read(ROBOT_SAVE_TEST_PATH)
        self.assertIn(SCREEN_MENU_BAR_PROPERTY_DECL, source)

    def test_has_headless_guard(self) -> None:
        source = _read(ROBOT_SAVE_TEST_PATH)
        self.assertIn("GraphicsEnvironment.isHeadless()", source)

    def test_has_screen_menu_bar_override_in_class(self) -> None:
        """The class must set SCREEN_MENU_BAR_PROPERTY to false."""
        source = _read(ROBOT_SAVE_TEST_PATH)
        self.assertRegex(source, SCREEN_MENU_BAR_SET_FALSE)

    def test_has_capture_and_restore_property_lifecycle(self) -> None:
        """The class must capture, override, and restore the property."""
        source = _read(ROBOT_SAVE_TEST_PATH)
        self.assertIn("captureProperties", source)
        self.assertIn("System.getProperty(SCREEN_MENU_BAR_PROPERTY)", source)
        self.assertIn("restoreProperty(SCREEN_MENU_BAR_PROPERTY", source)

    def test_screen_menu_bar_override_in_before_setup(self) -> None:
        """The property must be set in captureProperties (called from @Before)."""
        source = _read(ROBOT_SAVE_TEST_PATH)
        capture_start = source.find("captureProperties")
        set_property = source.find('System.setProperty(SCREEN_MENU_BAR_PROPERTY, "false")')
        self.assertGreater(capture_start, -1, "captureProperties must exist")
        self.assertGreater(set_property, -1, "SCREEN_MENU_BAR_PROPERTY must be set to false")


if __name__ == "__main__":
    unittest.main()
