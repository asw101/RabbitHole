from functools import lru_cache
import re
import unittest
from pathlib import Path


REPO_ROOT = Path(__file__).resolve().parents[1]

ARCHIVE_FIXTURE_SCENARIO = (
    REPO_ROOT / "qa" / "outside-in" / "alice-desktop" / "scenarios" / "archive-fixture-smoke.yaml"
)
TWEEDLE_BOUNDARY_SCENARIO = (
    REPO_ROOT / "qa" / "outside-in" / "alice-desktop" / "scenarios" / "tweedle-decoder-boundary-smoke.yaml"
)
HISTORICAL_ARCHIVE_TEST = (
    REPO_ROOT
    / "core"
    / "story-api-migration"
    / "src"
    / "test"
    / "java"
    / "org"
    / "lgna"
    / "project"
    / "io"
    / "HistoricalArchiveRoundTripCharacterizationTest.java"
)
DECODER_TEST = (
    REPO_ROOT
    / "core"
    / "ast"
    / "src"
    / "test"
    / "java"
    / "org"
    / "alice"
    / "serialization"
    / "tweedle"
    / "TweedleEncoderDecoderTest.java"
)


REFERENCE_DECODER_SELECTORS = [
    "zeroArgumentThisMethodCallDecodeCreatesMethodInvocation",
    "zeroArgumentThisMethodCallDecodeRejectsArgumentBearingCall",
    "zeroArgumentThisMethodCallDecodeRejectsOptionalParameterTargetMethod",
    "zeroArgumentThisMethodCallDecodeRejectsUnknownMethod",
    "zeroArgumentThisMethodCallDecodeRejectsDuplicateTargetMethodName",
    "zeroArgumentThisMethodCallDecodeRejectsNonThisTarget",
    "zeroArgumentThisMethodCallDecodeRejectsStaticTargetMethod",
    "zeroArgumentThisMethodCallDecodeRejectsChainedCall",
]
UNSUPPORTED_DECODER_SELECTORS = REFERENCE_DECODER_SELECTORS[1:]


@lru_cache(maxsize=None)
def read_text(path: Path) -> str:
    return path.read_text(encoding="utf-8")




class Pr463ArchivePlayerBoundaryContractTest(unittest.TestCase):

    def test_archive_fixture_smoke_preserves_archive_io_wording_and_missing_entry_evidence(self) -> None:
        scenario = read_text(ARCHIVE_FIXTURE_SCENARIO)
        lower = scenario.lower()

        self.assertIn("workflow: archive-fixture-smoke", scenario)
        self.assertIn("automationMode: gated-command-smoke", scenario)
        self.assertIn("archive-io", lower)
        self.assertIn("archive i/o", lower)
        self.assertIn("missing-entry", lower)
        self.assertIn("unsupported tweedle diagnostics", lower)
        self.assertNotIn("project io", lower)
        self.assertNotIn("project-io", lower)
        self.assertNotIn("alice-desktop-project-io-smoke", lower)

    def test_tweedle_decoder_boundary_smoke_stays_gated_core_ast_decoder_evidence_only(self) -> None:
        scenario = read_text(TWEEDLE_BOUNDARY_SCENARIO)

        self.assertIn("workflow: tweedle-decoder-boundary-smoke", scenario)
        self.assertIn("automationMode: gated-command-smoke", scenario)
        self.assertIn("- core/ast", scenario)
        for selector in UNSUPPORTED_DECODER_SELECTORS:
            with self.subTest(selector=selector):
                self.assertIn(selector, scenario)
        self.assertIn("gated decoder-boundary evidence only", scenario)
        self.assertNotIn("IoUtilities.readProject", scenario)

    def test_java_characterization_suite_declares_unit_edge_and_error_contracts(self) -> None:
        source = read_text(HISTORICAL_ARCHIVE_TEST)

        expected_tests = [
            "generatedJsonPlayerArchiveDecodesProgramLiteralArithmeticFieldInitializer",
            "generatedJsonPlayerArchiveWithArgumentBearingExplicitThisMethodCallReportsUnsupportedDecodeBoundary",
            "generatedJsonPlayerArchiveMissingManifestDeclaredProgramEntryFailsClearly",
            "generatedJsonPlayerArchiveMissingManifestDeclaredSiblingEntryFailsClearly",
            "generatedJsonPlayerArchiveWithMixedIdentifierProgramInitializerIsRejectedWithoutPartialProgramDecode",
            "generatedJsonPlayerArchiveWithResourceFieldInitializerProgramTypeIsRejectedWithoutPartialProgramDecode",
        ]
        for test_name in expected_tests:
            with self.subTest(test_name=test_name):
                self.assertRegex(source, rf"\bvoid\s+{re.escape(test_name)}\s*\(")



    def test_documented_decoder_selectors_exist_in_java_characterization_suite(self) -> None:
        source = read_text(DECODER_TEST)

        for selector in REFERENCE_DECODER_SELECTORS:
            with self.subTest(selector=selector):
                self.assertRegex(source, rf"\bvoid\s+{re.escape(selector)}\s*\(")



if __name__ == "__main__":
    unittest.main()
