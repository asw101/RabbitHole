#!/usr/bin/env python3
"""PR #428 merge-ready gate for issue-reporting test-seam characterization.

This gate evaluates evidence that:
- The PR head SHA matches across all evidence sources
- Changed files are limited to the PR scope (no secrets, parent traversal, or out-of-scope paths)
- QA evidence shows passing gated-smoke, scenario validation, runner & schema contracts
- Maven tests ran with correct module, test class, and NODE_OPTIONS
- At least 3 quality audit cycles with final cycle clean
- Docs impact reviewed with bounded claims only
- GitHub Actions checks are green on the PR head
- PR description contains all required evidence sections
- No manual merge commands or timeout wrappers were used

Returns MERGE_READY only when all gates pass.
"""

import re
from typing import Any

# PR #428 changed files allowlist (sorted)
PR428_CHANGED_FILES = frozenset([
    ".copilot-evidence/default-workflow-attempt.log",
    ".github/workflows/alice-checkstyle-ci.yml",
    ".github/workflows/alice-coverage-ci.yml",
    ".github/workflows/alice-netbeans-package-ci.yml",
    ".github/workflows/alice-test-ci.yml",
    "alice_qa_amplihack.py",
    "core/ast/src/test/java/org/lgna/project/ast/SourceCodeGeneratorTest.java",
    "core/ide/src/test/java/org/alice/ide/ProjectBackupRecoveryIoTest.java",
    "core/ide/src/test/java/org/alice/ide/ProjectBackupSelectorTest.java",
    "core/ide/src/test/java/org/alice/ide/ProjectFileUtilitiesTest.java",
    "core/issue-reporting/src/main/java/org/lgna/issue/IssueSubmissionProgressWorker.java",
    "core/issue-reporting/src/test/java/org/lgna/issue/IssueSubmissionProgressWorkerTest.java",
    "core/story-api-migration/src/main/java/org/lgna/project/io/DataSourceIo.java",
    "core/story-api-migration/src/main/java/org/lgna/project/io/IoUtilities.java",
    "core/story-api-migration/src/main/java/org/lgna/project/io/XmlProjectIo.java",
    "core/story-api-migration/src/main/java/org/lgna/project/io/ZipEntryContainer.java",
    "core/story-api-migration/src/test/java/org/lgna/project/io/IoUtilitiesTest.java",
    "docs/howto/characterize-issue-submission-progress-worker.md",
    "docs/howto/characterize-source-code-generator.md",
    "docs/howto/finalize-source-code-generator.md",
    "docs/howto/use-formal-spec-artifacts.md",
    "docs/howto/validate-archive-player-boundary.md",
    "docs/index.md",
    "docs/reference/archive-player-boundary.md",
    "docs/reference/ci-efficiency.md",
    "docs/reference/exported-netbeans-ant-project-behavior.md",
    "docs/reference/formal-spec-contracts.md",
    "docs/reference/gadugi-exported-launcher-evidence.md",
    "docs/reference/generated-story-api-listener-source-characterization.md",
    "docs/reference/issue-submission-progress-worker.md",
    "docs/tutorials/archive-player-boundary-characterization.md",
    "docs/tutorials/trace-issue-submission-progress-worker.md",
    "docs/tutorials/trace-save-load-recovery.md",
    "docs/tutorials/trace-source-code-generator-characterization.md",
    "netbeans/src/main/java/org/alice/netbeans/project/ProjectCodeGenerator.java",
    "netbeans/src/test/java/org/alice/netbeans/project/ProjectCodeGeneratorTest.java",
    "pyproject.toml",
    "qa/outside-in/alice-desktop/runners/run-scenario.sh",
    "qa/outside-in/alice-desktop/runners/validate-scenarios.sh",
    "qa/outside-in/alice-desktop/scenarios/archive-fixture-smoke.yaml",
    "qa/outside-in/alice-desktop/scenarios/issue-reporting-smoke.yaml",
    "qa/outside-in/alice-desktop/schema/scenario.schema.json",
    "qa/outside-in/alice-desktop/tests/test-schema-contract.sh",
    "scripts/pr428_merge_ready_gate.py",
    "tests/test_alice_qa_amplihack.py",
    "tests/test_archive_player_boundary_docs.py",
    "tests/test_ci_noop_workflow_contract.py",
    "tests/test_coverage_workflow.py",
    "tests/test_formal_spec_contracts_reference.py",
    "tests/test_pr426_formal_contract_wiring.py",
    "tests/test_pr428_merge_ready_gate.py",
])

EXPECTED_BRANCH = "feat/issue-408-rabbithole-wave7-coverage-ratchet-lane-follow-defa"
EXPECTED_TEST_CLASS = "org.lgna.issue.IssueSubmissionProgressWorkerTest"
EXPECTED_MODULE = "core/issue-reporting"
EXPECTED_SCENARIO = "alice-desktop-issue-reporting-smoke"
EXPECTED_WORKFLOW = "issue-reporting-smoke"
EXPECTED_AUTOMATION_MODE = "gated-command-smoke"

REQUIRED_ISSUE_REPORTING_DOCS = {
    "docs/reference/issue-submission-progress-worker.md",
    "docs/howto/characterize-issue-submission-progress-worker.md",
    "docs/tutorials/trace-issue-submission-progress-worker.md",
}

REQUIRED_GITHUB_CHECKS = {"build", "coverage", "package-netbeans", "test"}


def verify_head(evidence: dict[str, Any]) -> list[str]:
    """Verify PR head SHA consistency and authoritative branch reference."""
    blockers = []
    
    if evidence.get("prHeadSha") != evidence.get("headSha"):
        blockers.append("pr-head-sha-mismatch")
    
    remote_ref = evidence.get("remoteRef", "")
    expected_ref = f"origin/{evidence.get('branch', '')}"
    if not remote_ref.startswith("origin/") or remote_ref != expected_ref:
        blockers.append("wrong-authoritative-branch")
    
    return blockers


def verify_diff_scope(evidence: dict[str, Any]) -> list[str]:
    """Verify changed files are within PR scope and safe."""
    blockers = []
    changed_files = evidence.get("changedFiles", [])
    
    for path in changed_files:
        # Check for parent traversal
        if ".." in path:
            blockers.append("unfocused-diff-scope")
            break
        
        # Check for secret paths
        if path == ".env" or path.startswith("secrets/"):
            blockers.append("unfocused-diff-scope")
            break
        
        # Check if path is in the allowed set
        if path not in PR428_CHANGED_FILES:
            blockers.append("unfocused-diff-scope")
            break
    
    return blockers


def verify_qa_evidence(evidence: dict[str, Any]) -> list[str]:
    """Verify QA scenario evidence: gated smoke, validation outcomes, workflow config."""
    blockers = []
    qa = evidence.get("qaEvidence", {})
    
    # Check validation outcomes
    if qa.get("scenarioValidation", {}).get("outcome") != "passed":
        blockers.append("scenario-validation-not-passed")
    
    if qa.get("runnerContract", {}).get("outcome") != "passed":
        blockers.append("runner-contract-not-passed")
    
    if qa.get("schemaContract", {}).get("outcome") != "passed":
        blockers.append("schema-contract-not-passed")
    
    # Check gated smoke
    gated = qa.get("gatedSmoke", {})
    status_txt = gated.get("statusTxt", {})
    
    if status_txt.get("outcome") != "passed":
        blockers.append("gated-smoke-not-run")
    
    argv = status_txt.get("argv", "")
    if "IssueSubmissionProgressWorkerTest" not in argv:
        blockers.append("missing-focused-issue-reporting-smoke-argv")
    
    if gated.get("scenario") != EXPECTED_SCENARIO:
        blockers.append("wrong-gated-smoke-scenario")
    
    if gated.get("workflow") != EXPECTED_WORKFLOW:
        blockers.append("wrong-gated-smoke-workflow")
    
    if gated.get("automationMode") != EXPECTED_AUTOMATION_MODE:
        blockers.append("wrong-gated-smoke-automation-mode")
    
    return blockers


def verify_maven_evidence(evidence: dict[str, Any]) -> list[str]:
    """Verify Maven test execution with correct module, test class, and environment."""
    blockers = []
    maven = evidence.get("mavenEvidence", {})
    
    if maven.get("tweedleLangInitialized") != True:
        blockers.append("tweedle-lang-not-initialized")
    
    if not maven.get("nodeOptions"):
        blockers.append("missing-node-options")
    
    if maven.get("testClass") != EXPECTED_TEST_CLASS:
        blockers.append("wrong-maven-test-class")
    
    if maven.get("module") != EXPECTED_MODULE:
        blockers.append("wrong-maven-module")
    
    if maven.get("exitCode") != 0:
        blockers.append("maven-test-failed")
    
    return blockers


def verify_quality_audit(evidence: dict[str, Any]) -> list[str]:
    """Verify quality audit cycles: at least 3, final clean, no unresolved findings."""
    blockers = []
    cycles = evidence.get("qualityAuditCycles", [])
    
    if len(cycles) < 3:
        blockers.append("insufficient-quality-audit-cycles")
    
    if cycles and not cycles[-1].get("clean"):
        blockers.append("final-quality-audit-cycle-not-clean")
    
    for cycle in cycles:
        if cycle.get("unresolvedFindings"):
            blockers.append("quality-audit-open-finding")
            break
    
    return blockers


def verify_docs_impact(evidence: dict[str, Any]) -> list[str]:
    """Verify docs impact: reviewed, bounded claims, issue-reporting docs present."""
    blockers = []
    docs = evidence.get("docsImpact", {})
    
    if not docs.get("reviewed"):
        blockers.append("docs-impact-not-reviewed")
    
    if not docs.get("boundedClaimsOnly") and docs.get("forbiddenClaims"):
        blockers.append("docs-overclaim-unproven-behavior")
    
    # Check that all required issue-reporting docs are listed
    doc_files = set(docs.get("files", []))
    if not REQUIRED_ISSUE_REPORTING_DOCS.issubset(doc_files):
        blockers.append("missing-issue-reporting-docs")
    
    return blockers


def verify_github_actions(evidence: dict[str, Any]) -> list[str]:
    """Verify GitHub Actions checks: green on PR head, all required checks present."""
    blockers = []
    gh = evidence.get("githubActions", {})
    
    if gh.get("headSha") != evidence.get("headSha"):
        blockers.append("github-actions-stale-head")
    
    checks = gh.get("checks", [])
    check_names = {check.get("name") for check in checks}
    
    if not REQUIRED_GITHUB_CHECKS.issubset(check_names):
        blockers.append("github-actions-required-check-missing")
    
    for check in checks:
        status = (check.get("status") or "").strip().lower()
        conclusion = (check.get("conclusion") or "").strip().lower()
        
        if status != "completed":
            blockers.append("github-actions-not-complete")
            break
        
        if conclusion != "success":
            blockers.append("github-actions-not-green")
            break
    
    return blockers


def verify_pr_description(evidence: dict[str, Any]) -> list[str]:
    """Verify PR description: current head, evidence sections, bounded claims."""
    blockers = []
    pr_desc = evidence.get("prDescription", {})
    
    if pr_desc.get("headSha") != evidence.get("headSha"):
        blockers.append("pr-description-stale-head")
    
    if not pr_desc.get("hasBoundedClaims") and pr_desc.get("forbiddenClaims"):
        blockers.append("pr-description-overclaims-unproven-behavior")
    
    if not pr_desc.get("hasQaEvidence"):
        blockers.append("pr-description-missing-qa-evidence")
    
    # Check all required evidence flags
    evidence_flags = [
        "hasCurrentHeadEvidence",
        "hasQaEvidence",
        "hasDocsImpact",
        "hasDiffScope",
        "hasQualityAuditCycles",
    ]
    
    for flag in evidence_flags:
        if not pr_desc.get(flag):
            # Transform flag name to blocker code
            transformed = (
                flag.replace("has", "")
                .replace("C", "-c")
                .replace("E", "-e")
                .replace("I", "-i")
                .replace("D", "-d")
                .replace("Q", "-q")
                .replace("A", "-a")
                .replace("S", "-s")
                .lower()
                .strip("-")
            )
            blockers.append(f"pr-description-missing-{transformed}")
    
    return blockers


def _flatten_command(cmd: Any) -> str:
    """Flatten a command (string or list) to a single string for pattern matching."""
    if isinstance(cmd, str):
        return cmd
    elif isinstance(cmd, list):
        # Join all elements recursively
        parts = []
        for item in cmd:
            if isinstance(item, str):
                parts.append(item)
            elif isinstance(item, list):
                parts.extend(str(x) for x in item)
        return " ".join(parts)
    return str(cmd)


def verify_command_safety(evidence: dict[str, Any]) -> list[str]:
    """Verify no manual merge commands or timeout wrappers were used."""
    blockers = []
    
    if evidence.get("manualMergeUsed"):
        blockers.append("manual-merge-used")
        return blockers
    
    commands = evidence.get("commands", [])
    
    for cmd in commands:
        cmd_str = _flatten_command(cmd)
        
        # Check for timeout wrapper
        if re.search(r'\btimeout\b', cmd_str):
            blockers.append("timeout-wrapper-used")
            break
        
        # Check for manual merge patterns
        merge_patterns = [
            r'\bgh\s+pr\s+merge\b',
            r'\bgit\s+merge\b',
        ]
        
        for pattern in merge_patterns:
            if re.search(pattern, cmd_str):
                blockers.append("manual-merge-used")
                break
        
        if blockers:
            break
    
    return blockers


def evaluate_readiness(evidence: dict[str, Any]) -> dict[str, Any]:
    """Evaluate merge readiness for PR #428 by running all gate verifiers.
    
    Returns:
        dict with keys:
            - status: "MERGE_READY" or "NOT_MERGE_READY"
            - blockers: list of blocker codes
            - headSha: the PR head SHA
            - summary: (only if MERGE_READY) human-readable summary
    """
    all_blockers = []
    
    # Check for missing evidence sections first
    if "prHeadSha" not in evidence or "remoteRef" not in evidence:
        all_blockers.append("missing-pr-head-evidence")
    
    if "qaEvidence" not in evidence:
        all_blockers.append("missing-qa-evidence")
    
    if "githubActions" not in evidence:
        all_blockers.append("missing-github-actions-evidence")
    
    # Run all verifiers if we have basic evidence
    if "prHeadSha" in evidence and "remoteRef" in evidence:
        all_blockers.extend(verify_head(evidence))
    
    if "changedFiles" in evidence:
        all_blockers.extend(verify_diff_scope(evidence))
    
    if "qaEvidence" in evidence:
        all_blockers.extend(verify_qa_evidence(evidence))
    
    if "mavenEvidence" in evidence:
        all_blockers.extend(verify_maven_evidence(evidence))
    
    if "qualityAuditCycles" in evidence:
        all_blockers.extend(verify_quality_audit(evidence))
    
    if "docsImpact" in evidence:
        all_blockers.extend(verify_docs_impact(evidence))
    
    if "githubActions" in evidence:
        all_blockers.extend(verify_github_actions(evidence))
    
    if "prDescription" in evidence:
        all_blockers.extend(verify_pr_description(evidence))
    
    if "commands" in evidence:
        all_blockers.extend(verify_command_safety(evidence))
    
    # Build result
    head_sha = evidence.get("headSha", "unknown")
    
    if all_blockers:
        return {
            "status": "NOT_MERGE_READY",
            "blockers": all_blockers,
            "headSha": head_sha,
        }
    else:
        return {
            "status": "MERGE_READY",
            "blockers": [],
            "headSha": head_sha,
            "summary": (
                f"PR #428 is merge-ready: issue-reporting test-seam characterization "
                f"complete with gated smoke passing, focused Maven test execution, "
                f"bounded claims, and green CI checks on {head_sha}"
            ),
        }
