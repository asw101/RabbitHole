import re
import subprocess
import unittest
from pathlib import Path


REPO_ROOT = Path(__file__).resolve().parents[1]
MIGRATION_TEST = (
    REPO_ROOT
    / "core"
    / "story-api-migration"
    / "src"
    / "test"
    / "java"
    / "org"
    / "lgna"
    / "project"
    / "migration"
    / "ProjectMigrationManagerTest.java"
)


def git_output(*args: str) -> str:
    result = subprocess.run(
        ["git", *args],
        cwd=REPO_ROOT,
        check=True,
        capture_output=True,
        text=True,
    )
    return result.stdout.strip()


def origin_develop_ref() -> str:
    try:
        return git_output("rev-parse", "--verify", "origin/develop")
    except subprocess.CalledProcessError as exc:
        raise unittest.SkipTest(
            f"PR #424 recovery contract requires origin/develop: {exc.stderr.strip()}"
        ) from exc


class Pr424MigrationHotspotRecoveryContractTest(unittest.TestCase):
    def test_pr_branch_contains_current_origin_develop_before_review(self) -> None:
        develop = origin_develop_ref()
        result = subprocess.run(
            ["git", "merge-base", "--is-ancestor", develop, "HEAD"],
            cwd=REPO_ROOT,
            capture_output=True,
            text=True,
        )

        self.assertEqual(
            0,
            result.returncode,
            "PR #424 must be reconciled by merging origin/develop into the PR branch, "
            "not by merging the PR into develop.",
        )




    def test_project_migration_manager_characterization_tests_remain_in_scope(self) -> None:
        source = MIGRATION_TEST.read_text(encoding="utf-8")
        # All 18 @Test methods — original 12 characterization + 6 edge-case tests.
        # Update this list whenever a test method is added or renamed.
        expected_methods = [
            "textMigrationResultVersionsAreValidRoundTrippableAndIncreasing",
            "astMigrationResultVersionsAreValidRoundTrippableAndIncreasing",
            "migrationIsApplicableOnlyBeforeItsResultVersion",
            "textMigrationRewritesKnownLegacyStoryAndResourceNames",
            "textMigrationCascadesLegacyDresserThroughIntermediateResourceNames",
            "textMigrationCascadesLegacyDresserFieldThroughIntermediateResourceNames",
            "textMigrationStartingAfterDresserPackageMoveStillAppliesLaterConsolidations",
            "textMigrationDoesNotRewriteWhenVersionIsAlreadyAtThreshold",
            "textMigrationRewritesLegacyJointFieldsAndAccessors",
            "textMigrationRewritesVersion3_2_110ResourceFields",
            "textMigrationCharacterizesVersion3_2_111BonePileBoundary",
            "managerReportsNoPendingMigrationsAtCurrentVersion",
            "textMigrationOfEmptyStringIsNoOp",
            "textMigrationAtCurrentVersionReturnsInputUnchanged",
            "migrationListsAreNonEmpty",
            "textMigrationIsStableWhenReappliedFromResultVersion",
            "textMigrationResultVersionNeverExceedsCurrentVersion",
            "astMigrationResultVersionNeverExceedsCurrentVersion",
        ]

        for method in expected_methods:
            with self.subTest(method=method):
                self.assertIn(f"void {method}(", source)

        # Guard against silent test removal: count must match the inventory
        actual_count = source.count("@Test")
        self.assertEqual(
            len(expected_methods),
            actual_count,
            f"Expected {len(expected_methods)} @Test methods but found {actual_count}. "
            "Update expected_methods when adding or removing tests.",
        )



    def test_java_test_package_matches_production_package(self) -> None:
        """The test must live in org.lgna.project.migration, not org.alice.stageide.migration."""
        source = MIGRATION_TEST.read_text(encoding="utf-8")
        self.assertIn("package org.lgna.project.migration;", source)
        self.assertNotIn("org.alice.stageide.migration", source)



if __name__ == "__main__":
    unittest.main()
