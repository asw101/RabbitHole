import unittest
import subprocess
from functools import lru_cache
from pathlib import Path


REPO_ROOT = Path(__file__).resolve().parents[1]
FORMAL_SPEC_LANE = Path("docs/concepts/formal-spec-lane.md")
REPO_SURFACE_README = Path("docs/atlas/repo-surface/README.md")
REPO_SURFACE_DOT = Path("docs/atlas/repo-surface/repo-surface.dot")
REPO_SURFACE_MMD = Path("docs/atlas/repo-surface/repo-surface.mmd")
IO_UTILITIES_TEST = Path(
    "core/story-api-migration/src/test/java/org/lgna/project/io/IoUtilitiesTest.java"
)
BACKUP_SELECTOR_TEST = Path(
    "core/ide/src/test/java/org/alice/ide/ProjectBackupSelectorTest.java"
)
BACKUP_RECOVERY_IO_TEST = Path(
    "core/ide/src/test/java/org/alice/ide/ProjectBackupRecoveryIoTest.java"
)
PROJECT_FILE_UTILITIES_TEST = Path(
    "core/ide/src/test/java/org/alice/ide/ProjectFileUtilitiesTest.java"
)


@lru_cache(maxsize=None)
def read_repo_text(relative_path: Path) -> str:
    return (REPO_ROOT / relative_path).read_text(encoding="utf-8")


def git_ls_files(*pathspecs: str) -> list[str]:
    result = subprocess.run(
        ["git", "ls-files", *pathspecs],
        cwd=REPO_ROOT,
        check=True,
        capture_output=True,
        text=True,
    )
    return [line for line in result.stdout.splitlines() if line]


class PR426FormalContractWiringTest(unittest.TestCase):
    def test_top_level_investigation_directories_are_not_tracked_surfaces(self) -> None:
        tracked_paths = git_ls_files("drinkme/**", "eatme/**")
        self.assertEqual(
            [],
            tracked_paths,
            "Top-level drinkme/ and eatme/ files must not be tracked repository surfaces",
        )

    def test_formal_contract_lives_in_durable_repository_surfaces(self) -> None:
        formal_text = read_repo_text(FORMAL_SPEC_LANE)
        for marker in (
            "## Contract layers",
            "## Recovery invariants",
            "Acceptance contract | This document",
            "Recovery policy | This document and `core/ide` characterization tests",
            "Executable characterization | `core/ide` and `core/story-api-migration` JUnit tests",
            "Top-level `drinkme/` and `eatme/` directories are not tracked repository",
        ):
            self.assertIn(marker, formal_text)
        for obsolete_artifact_path in (
            "eatme/specs/save-load-export/project-archive.feature",
            "eatme/formal/backup-load-recovery/BackupLoadRecovery.tla",
            "eatme/formal/backup-load-recovery/BackupLoadRecovery.cfg",
            "drinkme/formal-spec-save-load-export-evaluation.md",
        ):
            self.assertNotIn(obsolete_artifact_path, formal_text)

    def test_repo_surface_atlas_omits_deleted_top_level_directories(self) -> None:
        readme_text = read_repo_text(REPO_SURFACE_README)
        self.assertIn(
            "Top-level `drinkme/` and `eatme/` directories are intentionally absent",
            readme_text,
        )
        for diagram_path in (REPO_SURFACE_DOT, REPO_SURFACE_MMD):
            diagram_text = read_repo_text(diagram_path)
            self.assertNotIn("drinkme", diagram_text)
            self.assertNotIn("eatme", diagram_text)

    def test_eatme_evidence_workflow_references_remain_valid(self) -> None:
        formal_text = read_repo_text(FORMAL_SPEC_LANE)
        for marker in (
            "tools/eatme-save-project",
            "tools/eatme-reopen-project",
            "org.alice.tools.Eatme",
            "JSON schema",
            "They do not imply a top-level `eatme/` directory.",
        ):
            self.assertIn(marker, formal_text)

    def test_pr426_project_archive_feature_maps_to_executable_junit_anchors(self) -> None:
        formal_text = read_repo_text(FORMAL_SPEC_LANE)
        io_test_text = read_repo_text(IO_UTILITIES_TEST)
        for marker in (
            "Saving an editable project writes a readable `.a3p` archive",
            "Saving includes `thumbnail.png` and a matching manifest icon",
            "Exporting a project writes a `.a3w` player archive",
            "Archive readers report missing, future, malformed, or unsafe archive metadata",
            "Resource entries use safe relative paths",
        ):
            self.assertIn(marker, formal_text)
        for anchor in (
            "pr426ProjectArchiveContractRejectsMalformedPlayerArchiveMetadataBeforeXmlFallback",
            "pr426ProjectArchiveContractRejectsUnsafeSupplementalEntryNames",
            "jsonPlayerExportUsesSafeDistinctResourceEntries",
            "jsonPlayerExportDoesNotLeakAbsoluteResourcePaths",
            "jsonPlayerReaderRejectsTraversalResourceReference",
            "jsonPlayerReaderReportsFutureVersion",
            "jsonPlayerReaderReportsMissingVersion",
            "corruptManifestDoesNotFallBackToXmlReader",
        ):
            self.assertIn(anchor, io_test_text)

    def test_pr426_backup_recovery_model_maps_to_executable_junit_anchors(self) -> None:
        formal_text = read_repo_text(FORMAL_SPEC_LANE)
        selector_text = read_repo_text(BACKUP_SELECTOR_TEST)
        recovery_text = read_repo_text(BACKUP_RECOVERY_IO_TEST)
        file_utilities_text = read_repo_text(PROJECT_FILE_UTILITIES_TEST)
        for marker in (
            "Prompted backups are safe",
            "Unloadable backups are skipped",
            "Stale async completion cannot replace state",
            "Recovery eventually reaches a terminal result",
            "Backup recovery considers candidates in newest-first order",
        ):
            self.assertIn(marker, formal_text)
        for anchor in (
            "pr426BackupContractSelectsNewestSafeCandidateAndNeverReselectsFailedOrUnsafe",
            "corruptedMainProjectSkipsBackupSymlinkEscapingBackupDirectory",
            "corruptedMainProjectSkipsCandidatesFromSymlinkedBackupDirectory",
        ):
            self.assertIn(anchor, selector_text)
        for anchor in (
            "pr426BackupContractStopsAfterSuccessfulRecovery",
            "corruptMainProjectSkipsUnloadableBackupAndLoadsNextBackupWithResources",
            "corruptMainProjectAndAllBackupsPlanUserVisibleFailure",
        ):
            self.assertIn(anchor, recovery_text)
        self.assertIn(
            "pr426BackupPathContractKeepsNamedBackupDirectoryBesideProjectFile",
            file_utilities_text,
        )


if __name__ == "__main__":
    unittest.main()
