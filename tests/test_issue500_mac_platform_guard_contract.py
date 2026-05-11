"""Contract tests for issue #500: Mac platform detection in Robot menu tests.

Originally verified that JMenuBarRobotClickSaveProofTest and
RobotSaveMenuDialogWriteReadbackProofTest had assumeFalse(SystemUtilities.isMac())
guards so AWT Robot screen-coordinate menu clicks skip on macOS.

Issue #502 superseded the isMac() skip approach with a property-override pattern
that sets apple.laf.useScreenMenuBar=false in @Before/@After, keeping the menu
in the JFrame where Robot coordinates work on every platform. These tests now
verify that the old isMac guards are REMOVED and the reference doc reflects
both approaches (the original #500 guard and the #502 override).
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

REFERENCE_DOC_PATH = REPO_ROOT / "docs" / "reference" / "mac-compatible-test-guards.md"

SYSTEM_UTILITIES_IMPORT = "import edu.cmu.cs.dennisc.java.lang.SystemUtilities;"
ASSUME_FALSE_STATIC_IMPORT = "import static org.junit.Assume.assumeFalse;"
IS_MAC_GUARD_PATTERN = re.compile(
    r"assumeFalse\(\s*\".*macOS.*native.*menu.*bar.*\"\s*,\s*SystemUtilities\.isMac\(\)\s*\)"
)

# The Robot-driven test method in RobotSaveMenuDialogWriteReadbackProofTest
ROBOT_METHOD = "robotFileSaveApprovesChooserWritesReadableMarkedProjectOrWritesBlocker"

# Evidence-contract methods that must NOT have the isMac guard
EVIDENCE_METHODS = [
    "incompleteArtifactIsBlockedAndDoesNotClaimChooserWriteOrReadback",
    "artifactRequiresRobotSaveClickBeforeReportingProven",
    "completeArtifactReportsNarrowProvenClaim",
    "targetOutsideProofRootIsBlockedAndRedacted",
    "wrongSelectedFileDoesNotRewriteExpectedTargetBoundaryEvidence",
]


@lru_cache(maxsize=None)
def _read(path: Path) -> str:
    return path.read_text(encoding="utf-8")


def _extract_method_body(source: str, method_name: str) -> str:
    """Extract the body of a Java method from source by name.

    Returns the text from the method signature through the next
    unindented closing brace or next @Test annotation, whichever
    comes first.
    """
    pattern = re.compile(
        rf"public\s+void\s+{re.escape(method_name)}\s*\(", re.DOTALL
    )
    match = pattern.search(source)
    if not match:
        raise AssertionError(f"Method {method_name} not found in source")
    start = match.start()
    # Find the end: next @Test annotation or end of class
    rest = source[start:]
    # Look for the next @Test to delimit
    next_test = re.search(r"\n\s*@Test\b", rest[1:])
    if next_test:
        return rest[: next_test.start() + 1]
    return rest


class JMenuBarRobotClickSaveProofTestMacGuardContract(unittest.TestCase):
    """Contract: JMenuBarRobotClickSaveProofTest must NOT have the isMac guard
    (superseded by property override in issue #502)."""

    def test_file_exists(self) -> None:
        self.assertTrue(
            JMENUBAR_TEST_PATH.exists(),
            f"Expected test file at {JMENUBAR_TEST_PATH.relative_to(REPO_ROOT)}",
        )

    def test_no_system_utilities_import(self) -> None:
        """Issue #502 removed SystemUtilities — only used for isMac()."""
        source = _read(JMENUBAR_TEST_PATH)
        self.assertNotIn(
            SYSTEM_UTILITIES_IMPORT,
            source,
            "JMenuBarRobotClickSaveProofTest must NOT import "
            "SystemUtilities after issue #502 property-override",
        )

    def test_no_is_mac_guard(self) -> None:
        """Issue #502 replaced isMac() skip with property override."""
        source = _read(JMENUBAR_TEST_PATH)
        self.assertNotRegex(
            source,
            IS_MAC_GUARD_PATTERN,
            "JMenuBarRobotClickSaveProofTest must NOT call "
            "assumeFalse(isMac()) after issue #502",
        )

    def test_headless_guard_still_present(self) -> None:
        """The headless guard is independent of isMac and must survive."""
        source = _read(JMENUBAR_TEST_PATH)
        self.assertIn(
            "GraphicsEnvironment.isHeadless()",
            source,
            "JMenuBarRobotClickSaveProofTest must keep the headless guard",
        )


class RobotSaveMenuDialogWriteReadbackProofTestMacGuardContract(unittest.TestCase):
    """Contract: RobotSaveMenuDialogWriteReadbackProofTest must NOT have
    the isMac guard (superseded by property override in issue #502)."""

    def test_file_exists(self) -> None:
        self.assertTrue(
            ROBOT_SAVE_TEST_PATH.exists(),
            f"Expected test file at {ROBOT_SAVE_TEST_PATH.relative_to(REPO_ROOT)}",
        )

    def test_no_system_utilities_import(self) -> None:
        """Issue #502 removed SystemUtilities — only used for isMac()."""
        source = _read(ROBOT_SAVE_TEST_PATH)
        self.assertNotIn(
            SYSTEM_UTILITIES_IMPORT,
            source,
            "RobotSaveMenuDialogWriteReadbackProofTest must NOT import "
            "SystemUtilities after issue #502 property-override",
        )

    def test_robot_method_no_is_mac_guard(self) -> None:
        """Issue #502 replaced isMac() skip with property override."""
        source = _read(ROBOT_SAVE_TEST_PATH)
        method_body = _extract_method_body(source, ROBOT_METHOD)
        self.assertNotRegex(
            method_body,
            IS_MAC_GUARD_PATTERN,
            f"Robot method {ROBOT_METHOD} must NOT have isMac guard after issue #502",
        )

    def test_evidence_methods_do_not_have_is_mac_guard(self) -> None:
        """The evidence-contract methods must NOT have the isMac guard.
        They validate artifact schemas, not Robot UI interactions."""
        source = _read(ROBOT_SAVE_TEST_PATH)
        for method_name in EVIDENCE_METHODS:
            method_body = _extract_method_body(source, method_name)
            self.assertNotRegex(
                method_body,
                IS_MAC_GUARD_PATTERN,
                f"Evidence method {method_name} must NOT have isMac guard",
            )


class MacGuardDocumentationContract(unittest.TestCase):
    """Contract: reference doc must describe the evolution from isMac guard (#500)
    to property-override (#502)."""

    def test_reference_doc_exists(self) -> None:
        self.assertTrue(
            REFERENCE_DOC_PATH.exists(),
            f"Expected reference doc at {REFERENCE_DOC_PATH.relative_to(REPO_ROOT)}",
        )

    def test_reference_doc_mentions_issue_500(self) -> None:
        text = _read(REFERENCE_DOC_PATH)
        self.assertIn("#500", text, "Reference doc must mention issue #500")

    def test_reference_doc_mentions_both_test_classes(self) -> None:
        text = _read(REFERENCE_DOC_PATH)
        self.assertIn(
            "JMenuBarRobotClickSaveProofTest",
            text,
            "Reference doc must mention JMenuBarRobotClickSaveProofTest",
        )
        self.assertIn(
            "RobotSaveMenuDialogWriteReadbackProofTest",
            text,
            "Reference doc must mention RobotSaveMenuDialogWriteReadbackProofTest",
        )

    def test_reference_doc_describes_system_utilities_api(self) -> None:
        """Reference doc should still describe the original SystemUtilities
        API for historical context."""
        text = _read(REFERENCE_DOC_PATH)
        self.assertIn(
            "SystemUtilities.isMac()",
            text,
            "Reference doc must describe SystemUtilities.isMac() API",
        )

    def test_reference_doc_mentions_robot_screen_coordinate(self) -> None:
        text = _read(REFERENCE_DOC_PATH)
        self.assertIn(
            "Robot screen-coordinate",
            text,
            "Doc must mention Robot screen-coordinate clicks",
        )


if __name__ == "__main__":
    unittest.main()
