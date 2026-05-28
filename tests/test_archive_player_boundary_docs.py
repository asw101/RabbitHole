from functools import cache
import re
import unittest
from pathlib import Path

import alice_qa_amplihack


REPO_ROOT = Path(__file__).resolve().parents[1]
ARCHIVE_FIXTURE_SCENARIO = (
    REPO_ROOT
    / "qa"
    / "outside-in"
    / "alice-desktop"
    / "scenarios"
    / "archive-fixture-smoke.yaml"
)
EXPECTED_DIRECT_MAVEN_FLAGS = (
    "-DincludeSims=false",
    "-Dinstall4j.skip",
    "-pl core/story-api-migration -am",
    "-DfailIfNoTests=false",
    "-Dsurefire.failIfNoSpecifiedTests=false",
    "-Dtest=org.lgna.project.io.HistoricalArchiveRoundTripCharacterizationTest",
)


@cache
def scenario_argv() -> tuple[str, ...]:
    scenario = ARCHIVE_FIXTURE_SCENARIO.read_text(encoding="utf-8")
    match = re.search(r"(?m)^  argv:\n(?P<body>(?:    - .+\n)+)", scenario)
    if match is None:
        raise AssertionError("archive fixture smoke scenario must declare automation.argv")
    return tuple(
        line.strip().split("- ", 1)[1]
        for line in match.group("body").splitlines()
        if line.strip()
    )


class ArchivePlayerBoundaryDocsContractTest(unittest.TestCase):
    def test_direct_maven_examples_match_bounded_wrapper_and_scenario_flags(self) -> None:
        wrapper_command = " ".join(alice_qa_amplihack.ARCHIVE_PLAYER_BOUNDARY_COMMAND)
        scenario_command = " ".join(scenario_argv())

        for required_flag in EXPECTED_DIRECT_MAVEN_FLAGS:
            with self.subTest(source="wrapper", flag=required_flag):
                self.assertIn(required_flag, wrapper_command)
            with self.subTest(source="scenario", flag=required_flag):
                self.assertIn(required_flag, scenario_command)


if __name__ == "__main__":
    unittest.main()
