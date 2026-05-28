import os
import subprocess
import tempfile
import unittest
from contextlib import contextmanager
from pathlib import Path
from typing import Iterator, Optional


REPO_ROOT = Path(__file__).resolve().parents[1]
GUARD_SCRIPT = REPO_ROOT / "scripts" / "project-archive-reopen-edit-noop-guard.sh"
PR_BRANCH = "wave6-project-reopen-edit-chain-1778302300"
BASE_BRANCH = "develop"
VALIDATION_COMMAND = (
    "NODE_OPTIONS=--max-old-space-size=32768 mvn -DincludeSims=false "
    "-Dinstall4j.skip -DfailIfNoTests=false "
    "-Dsurefire.failIfNoSpecifiedTests=false -pl core/story-api-migration -am "
    "-Dtest=org.lgna.project.io.IoUtilitiesTest test"
)
SCOPE_EXCLUSIONS = (
    "Scope exclusions: no full desktop lesson automation, full UI automation, "
    "desktop Save-menu completion, visible rendering correctness, grading, "
    "grading correctness, full Save completion, full first-lesson completion, "
    "creative assessment, full Tweedle/player decode, player runtime behavior, "
    "or broad migration correctness claims"
)
REQUIRED_PR_CHECKS = (
    "GitGuardian Security Checks",
    "Alice Checkstyle CI/build (pull_request)",
    "Alice Coverage Reports/coverage (pull_request)",
    "Alice NetBeans Package CI/package-netbeans (pull_request)",
    "Alice Test CI/test (pull_request)",
)
CURRENT_PR_CHECK_EVIDENCE = "Checks: " + "; ".join(
    f"{check_name} successful at current PR head" for check_name in REQUIRED_PR_CHECKS
)


class ProjectArchiveReopenEditNoopGuardTest(unittest.TestCase):
    _worktree_tempdir = None
    _linked_worktree = None
    _linked_worktree_head = None

    @classmethod
    def setUpClass(cls) -> None:
        cls._worktree_tempdir = tempfile.TemporaryDirectory(prefix="alice-archive-guard-")
        cls._linked_worktree = Path(cls._worktree_tempdir.name) / "linked-worktree"
        try:
            subprocess.run(
                ["git", "worktree", "add", "--detach", str(cls._linked_worktree), "HEAD"],
                cwd=REPO_ROOT,
                check=True,
                stdout=subprocess.PIPE,
                stderr=subprocess.PIPE,
                text=True,
            )
        except (OSError, subprocess.CalledProcessError):
            cls._worktree_tempdir.cleanup()
            cls._worktree_tempdir = None
            cls._linked_worktree = None
            raise
        cls._linked_worktree_head = cls.git_output(cls._linked_worktree, "rev-parse", "HEAD")

    @classmethod
    def tearDownClass(cls) -> None:
        if cls._linked_worktree is not None:
            subprocess.run(
                ["git", "worktree", "remove", "--force", str(cls._linked_worktree)],
                cwd=REPO_ROOT,
                check=False,
                stdout=subprocess.PIPE,
                stderr=subprocess.PIPE,
                text=True,
            )
            cls._linked_worktree = None
        if cls._worktree_tempdir is not None:
            cls._worktree_tempdir.cleanup()
            cls._worktree_tempdir = None
        cls._linked_worktree_head = None

    def test_guard_script_exists_as_repo_owned_entrypoint(self) -> None:
        self.assertTrue(
            GUARD_SCRIPT.is_file(),
            f"missing repo-owned guard script: {GUARD_SCRIPT}",
        )
        self.assertTrue(
            os.access(GUARD_SCRIPT, os.X_OK),
            f"guard script must be executable: {GUARD_SCRIPT}",
        )

    def test_guard_rejects_non_git_candidate_path_clearly(self) -> None:
        with tempfile.TemporaryDirectory() as temporary_directory:
            result = self.run_guard(Path(temporary_directory), "--print-root")

        self.assertNotEqual(0, result.returncode)
        combined_output = (result.stdout + result.stderr).lower()
        self.assertIn("git", combined_output)
        self.assertIn("worktree", combined_output)

    def test_guard_resolves_linked_worktree_root_from_nested_candidate_path(self) -> None:
        with self.linked_worktree() as linked_worktree:
            nested_candidate = linked_worktree / "core" / "ide"
            result = self.run_guard(nested_candidate, "--print-root")

        self.assertEqual("", result.stderr)
        self.assertEqual(0, result.returncode)
        self.assertEqual(str(linked_worktree.resolve()), result.stdout.strip())




    def test_guard_rejects_committed_diff_with_unrelated_paths(self) -> None:
        with self.linked_worktree() as linked_worktree:
            unrelated_file = linked_worktree / "unrelated-committed-review-note.py"
            unrelated_file.write_text("not part of the project archive reopen/edit seam\n", encoding="utf-8")
            subprocess.run(
                ["git", "-C", str(linked_worktree), "add", "unrelated-committed-review-note.py"],
                check=True,
                stdout=subprocess.PIPE,
                stderr=subprocess.PIPE,
                text=True,
            )
            subprocess.run(
                [
                    "git",
                    "-C",
                    str(linked_worktree),
                    "-c",
                    "user.name=Project Archive Guard Test",
                    "-c",
                    "user.email=project-archive-guard@example.invalid",
                    "commit",
                    "--no-verify",
                    "-m",
                    "Add unrelated committed guard test file",
                ],
                check=True,
                stdout=subprocess.PIPE,
                stderr=subprocess.PIPE,
                text=True,
            )

            result = self.run_guard(linked_worktree)

        self.assertNotEqual(0, result.returncode)
        combined_output = (result.stdout + result.stderr).lower()
        self.assertIn("committed diff", combined_output)
        self.assertIn("outside project archive reopen/edit recovery scope", combined_output)
        self.assertIn("unrelated-committed-review-note.py", combined_output)


    def test_guard_rejects_clean_worktree_noop_evidence_for_stale_base_head(self) -> None:
        with self.linked_worktree() as linked_worktree:
            head = self.linked_worktree_head()
            base_head = self.base_head()
            stale_base_head = "0" * 40 if base_head != "0" * 40 else "1" * 40
            evidence_file = self.write_evidence(
                linked_worktree,
                self.exact_head_noop_evidence(head).replace(
                    f"origin/{BASE_BRANCH} HEAD: {base_head}",
                    f"origin/{BASE_BRANCH} HEAD: {stale_base_head}",
                ),
            )

            result = self.run_guard(
                linked_worktree,
                "--allow-noop-evidence",
                str(evidence_file),
                "--expected-head",
                head,
            )

        self.assertNotEqual(0, result.returncode)
        combined_output = (result.stdout + result.stderr).lower()
        self.assertIn("stale", combined_output)
        self.assertIn(f"origin/{BASE_BRANCH}", combined_output)

    def test_guard_rejects_clean_worktree_noop_evidence_without_base_head(self) -> None:
        with self.linked_worktree() as linked_worktree:
            head = self.linked_worktree_head()
            evidence_text = "\n".join(
                line for line in self.exact_head_noop_evidence(head).splitlines()
                if not line.startswith(f"origin/{BASE_BRANCH} HEAD:")
            )
            evidence_file = self.write_evidence(linked_worktree, evidence_text)

            result = self.run_guard(
                linked_worktree,
                "--allow-noop-evidence",
                str(evidence_file),
                "--expected-head",
                head,
            )

        self.assertNotEqual(0, result.returncode)
        combined_output = (result.stdout + result.stderr).lower()
        self.assertIn(f"origin/{BASE_BRANCH}", combined_output)



    def test_guard_rejects_expected_head_that_is_not_worktree_head(self) -> None:
        with self.linked_worktree() as linked_worktree:
            head = self.linked_worktree_head()
            stale_head = "0" * 40 if head != "0" * 40 else "1" * 40
            evidence_file = self.write_evidence(linked_worktree, self.exact_head_noop_evidence(head))

            result = self.run_guard(
                linked_worktree,
                "--allow-noop-evidence",
                str(evidence_file),
                "--expected-head",
                stale_head,
            )

        self.assertNotEqual(0, result.returncode)
        combined_output = (result.stdout + result.stderr).lower()
        self.assertIn("worktree head", combined_output)




























    def test_guard_reports_missing_noop_evidence_file(self) -> None:
        with self.linked_worktree() as linked_worktree:
            head = self.linked_worktree_head()
            missing_evidence_file = linked_worktree.parent / "missing-readiness-evidence.md"

            result = self.run_guard(
                linked_worktree,
                "--allow-noop-evidence",
                str(missing_evidence_file),
                "--expected-head",
                head,
            )

        self.assertNotEqual(0, result.returncode)
        combined_output = (result.stdout + result.stderr).lower()
        self.assertIn("evidence", combined_output)
        self.assertIn("missing", combined_output)

    @contextmanager
    def linked_worktree(self) -> Iterator[Path]:
        if self._linked_worktree is None:
            self.fail("linked guard worktree was not initialized")
        self.clean_linked_worktree()
        yield self._linked_worktree

    def linked_worktree_head(self) -> str:
        if self._linked_worktree_head is None:
            self.fail("linked guard worktree HEAD was not initialized")
        return self._linked_worktree_head

    @staticmethod
    def git_output(worktree: Path, *args: str) -> str:
        return subprocess.run(
            ["git", "-C", str(worktree), *args],
            check=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True,
        ).stdout.strip()

    def clean_linked_worktree(self) -> None:
        if self._linked_worktree is None:
            self.fail("linked guard worktree was not initialized")
        if self._linked_worktree_head is None:
            self.fail("linked guard worktree HEAD was not initialized")
        subprocess.run(
            ["git", "-C", str(self._linked_worktree), "reset", "--hard", self._linked_worktree_head],
            check=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True,
        )
        subprocess.run(
            ["git", "-C", str(self._linked_worktree), "clean", "-fd"],
            check=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True,
        )

    def write_evidence(self, linked_worktree: Path, evidence_text: str) -> Path:
        evidence_file = linked_worktree.parent / "readiness-evidence.md"
        evidence_file.write_text(evidence_text, encoding="utf-8")
        return evidence_file

    def base_head(self) -> str:
        return self.git_output(REPO_ROOT, "rev-parse", f"origin/{BASE_BRANCH}^{{commit}}")

    def merge_base(self, head: str) -> str:
        return self.git_output(REPO_ROOT, "merge-base", f"origin/{BASE_BRANCH}", head)

    def exact_head_noop_evidence(
        self,
        head: str,
        *,
        include_noop_justification: bool = True,
        include_scope_exclusions: bool = True,
        include_noop_head: bool = True,
        include_current_pr_checks: bool = True,
        include_runnable_qa_scenario_evidence: bool = True,
        include_docs_impact: bool = True,
        include_quality_audit_cycles: bool = True,
        include_pr_description_evidence: bool = True,
        scope_exclusions: str = SCOPE_EXCLUSIONS,
        pr_check_evidence: str = CURRENT_PR_CHECK_EVIDENCE,
        runnable_qa_scenario_evidence: Optional[str] = None,
        quality_audit_evidence: Optional[str] = None,
        pr_description_evidence: Optional[str] = None,
    ) -> str:
        base_head = self.base_head()
        merge_base = self.merge_base(head)
        lines = [
            "PR: 402",
            f"Branch: {PR_BRANCH}",
            f"Base: {BASE_BRANCH}",
            f"PR head: {head}",
            f"Local HEAD: {head}",
            f"Remote branch HEAD: {head}",
            f"origin/{BASE_BRANCH} HEAD: {base_head}",
            f"Merge-base: {merge_base}",
            f"Merge-base status: merge-base computed from origin/{BASE_BRANCH} and PR head",
            f"Committed diff scope: all origin/{BASE_BRANCH}...{head} paths are project archive reopen/edit recovery scoped",
            "Worktree status: clean",
            "Diff summary: limited to project archive reopen/edit characterization/readiness surfaces",
            f"Validation command: {VALIDATION_COMMAND}",
            f"Validation result: exit 0 PASS at {head}",
            "Positive claim scope: repository-owned archive reopen/edit behavior only",
        ]
        if include_runnable_qa_scenario_evidence:
            lines.append(runnable_qa_scenario_evidence or self.runnable_qa_scenario_evidence(head))
        if include_docs_impact:
            lines.append(
                "Docs impact: reviewed docs/reference/pr-402-reopen-edit-recovery-output-contract.md, "
                "docs/reference/project-archive-reopen-edit-seam.md, "
                "docs/howto/validate-project-archive-reopen-edit-seam.md, and docs/index.md "
                f"at {head}"
            )
        if include_quality_audit_cycles:
            lines.extend((quality_audit_evidence or self.clean_quality_audit_evidence(head)).splitlines())
        if include_pr_description_evidence:
            lines.append(
                pr_description_evidence
                or (
                    f"PR description evidence: PR 402 body cites current head {head}, "
                    "bounded validation, scope exclusions, and no overclaim wording"
                )
            )
        if include_current_pr_checks:
            lines.append(pr_check_evidence)
        if include_scope_exclusions:
            lines.append(scope_exclusions)
        lines.append("Stale evidence note: older evidence must not be reused for a different HEAD")
        if include_noop_justification:
            validated_head = head if include_noop_head else "the validated PR head"
            base_state = base_head if include_noop_head else "the recorded base"
            merge_base_state = merge_base if include_noop_head else "the recorded merge-base"
            validation_result = f"passed at {head}," if include_noop_head else "passed,"
            lines.extend(
                [
                    "No-op justification:",
                    f"  PR 402 branch {PR_BRANCH} already points at",
                    f"  {validated_head}, local HEAD matches both the PR head and remote",
                    f"  branch head, origin/{BASE_BRANCH} is {base_state}, merge-base is {merge_base_state},",
                    f"  and the origin/{BASE_BRANCH}...{validated_head} diff is limited to",
                    "  project archive reopen/edit characterization/readiness surfaces,",
                    "  focused archive reopen/edit validation",
                    f"  {validation_result} and no scoped PR check blocker requires a code or docs",
                    "  change.",
                ]
            )
        return "\n".join(lines)

    @staticmethod
    def runnable_qa_scenario_evidence(head: str) -> str:
        return (
            "Runnable QA/scenario evidence: NODE_OPTIONS=--max-old-space-size=32768 "
            "qa/outside-in/alice-desktop/runners/validate-scenarios.sh "
            f"exit 0 PASS at {head}; no diff-scoped scenario smoke required"
        )

    @staticmethod
    def clean_quality_audit_evidence(head: str) -> str:
        return "\n".join(
            [
                "Quality-audit cycle 1:",
                "  SEEK: exact-head and branch mismatch risk",
                f"  VALIDATE: PR head, local HEAD, and remote branch head equal {head}",
                "  FIX: no-op; exact-head evidence is current",
                "  Result: clean",
                "Quality-audit cycle 2:",
                "  SEEK: validation, QA/scenario, and docs evidence gaps",
                "  VALIDATE: focused Maven command, scenario catalog validator, and docs impact review",
                "  FIX: no-op; required evidence is present",
                "  Result: clean",
                "Quality-audit cycle 3:",
                "  SEEK: final merge-ready gate scan for stale head evidence, missing QA/docs, and overclaims",
                "  VALIDATE: current-head checks, PR body review, diff scope review, and guard evidence",
                "  FIX: no-op; all gates have current-head evidence",
                "  Result: clean",
            ]
        )

    @staticmethod
    def blocked_final_quality_audit_evidence(head: str) -> str:
        return "\n".join(
            [
                "Quality-audit cycle 1:",
                "  SEEK: exact-head and branch mismatch risk",
                f"  VALIDATE: PR head, local HEAD, and remote branch head equal {head}",
                "  FIX: no-op; exact-head evidence is current",
                "  Result: clean",
                "Quality-audit cycle 2:",
                "  SEEK: validation, QA/scenario, and docs evidence gaps",
                "  VALIDATE: focused Maven command, scenario catalog validator, and docs impact review",
                "  FIX: no-op; required evidence is present",
                "  Result: clean",
                "Quality-audit cycle 3:",
                "  SEEK: final merge-ready gate scan for stale head evidence, missing QA/docs, and overclaims",
                "  VALIDATE: PR body review found stale-head evidence",
                "  FIX: no-op unavailable until PR description is refreshed",
                "  Result: blocker: stale PR description evidence",
            ]
        )

    def run_guard(self, candidate_path: Path, *extra_args: str) -> subprocess.CompletedProcess[str]:
        if not GUARD_SCRIPT.is_file():
            self.fail(f"missing repo-owned guard script: {GUARD_SCRIPT}")
        return subprocess.run(
            [str(GUARD_SCRIPT), str(candidate_path), *extra_args],
            cwd=REPO_ROOT,
            check=False,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True,
        )


if __name__ == "__main__":
    unittest.main()
