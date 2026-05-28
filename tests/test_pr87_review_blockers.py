import re
import subprocess
import unittest
from pathlib import Path


REPO_ROOT = Path(__file__).resolve().parents[1]
CANONICAL_METHOD = "savedProjectCanBeReopenedEditedSavedAgainReopenedAndExported"

IO_UTILITIES_TEST = (
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
    / "IoUtilitiesTest.java"
)



def git_output(*args: str) -> str:
    result = subprocess.run(
        ["git", *args],
        cwd=REPO_ROOT,
        check=True,
        capture_output=True,
        text=True,
    )
    return result.stdout


def pr_branch_output(*args: str) -> str:
    result = subprocess.run(
        ["git", *args],
        cwd=REPO_ROOT,
        capture_output=True,
        text=True,
    )
    if result.returncode != 0:
        raise unittest.SkipTest(
            f"PR branch contract requires origin/develop: {result.stderr.strip()}"
        )
    return result.stdout


def pr_branch_commits() -> list[str]:
    commits = pr_branch_output("log", "--format=%s", "origin/develop..HEAD").splitlines()
    if not commits:
        raise unittest.SkipTest("PR branch contract is only enforced when HEAD is ahead of origin/develop.")
    explicit_pr_numbers = set(re.findall(r"\bPR\s*#(\d+)\b", "\n".join(commits), flags=re.IGNORECASE))
    if explicit_pr_numbers and explicit_pr_numbers != {"87"}:
        raise unittest.SkipTest(
            "PR #87 branch-history contract is not applicable to PR(s): "
            + ", ".join(sorted(explicit_pr_numbers))
        )
    return commits


class Pr87ReviewBlockerContractTest(unittest.TestCase):
    def test_io_utilities_test_declares_the_canonical_regression_method(self) -> None:
        source = IO_UTILITIES_TEST.read_text(encoding="utf-8")

        declarations = re.findall(rf"\bvoid\s+{re.escape(CANONICAL_METHOD)}\s*\(", source)

        self.assertEqual(
            [f"void {CANONICAL_METHOD}("],
            declarations,
            "IoUtilitiesTest should own exactly one canonical save/reopen/edit/export regression method.",
        )


    def test_pr_branch_does_not_introduce_lfs_configuration_or_tracked_files(self) -> None:
        pr_branch_commits()
        changed_files = set(pr_branch_output("diff", "--name-only", "origin/develop...HEAD").splitlines())

        self.assertFalse(
            {".gitattributes", ".lfsconfig"} & changed_files,
            "PR #87 must not introduce Git LFS configuration.",
        )
        for file_name in changed_files:
            self.assertNotIn(
                ".git/lfs",
                file_name,
                "PR #87 must not introduce LFS-managed artifacts.",
            )

    def test_pr_branch_history_has_no_wip_or_checkpoint_commit(self) -> None:
        commits = pr_branch_commits()
        disallowed_subject = re.compile(r"\b(wip|checkpoint)\b", flags=re.IGNORECASE)

        self.assertEqual(
            [],
            [subject for subject in commits if disallowed_subject.search(subject)],
            "PR branch history must not contain WIP or checkpoint commits.",
        )


if __name__ == "__main__":
    unittest.main()
