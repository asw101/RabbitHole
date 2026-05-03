package org.alice.crustyproxy;

import org.junit.Test;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class WorkstreamClassifierTest {

  @Test
  public void classifiesActiveCandidateSupersededAndRemoteDeltaBranchesFromEvidence() {
    WorkstreamEvidence evidence = WorkstreamEvidence.builder()
        .activeWorktree("feat/alice-crusty-proxy-review", Path.of("/worktrees/crusty"), "cb8973d")
        .activeWorktree("feat/alice-qa-outside-in", Path.of("/worktrees/qa"), "cb8973d")
        .localBranch("feat/alice-formal-specs", "cb8973d", Instant.parse("2026-05-03T20:00:00Z"))
        .localBranch("loop64-project-io", "1111111", Instant.parse("2026-05-01T20:00:00Z"))
        .remoteBranch("origin/loop64-project-io", "2222222", Instant.parse("2026-05-03T20:00:00Z"))
        .localBranch("loop62-old-parser", "aaaaaaa", Instant.parse("2026-04-01T20:00:00Z"))
        .supersededBy("loop62-old-parser", "loop64-project-io")
        .build();

    List<WorkstreamClassification> classifications = new WorkstreamClassifier().classify(evidence);

    assertEquals(WorkstreamStatus.ACTIVE_LOCAL_WORKTREE,
        find(classifications, "feat/alice-crusty-proxy-review").status());
    assertEquals(WorkstreamStatus.CANDIDATE_BRANCH,
        find(classifications, "feat/alice-formal-specs").status());
    assertEquals(WorkstreamStatus.REMOTE_ONLY_DELTA,
        find(classifications, "loop64-project-io").status());
    assertEquals(WorkstreamStatus.SUPERSEDED_LANE,
        find(classifications, "loop62-old-parser").status());
    assertTrue(find(classifications, "loop64-project-io").evidence().contains("origin/loop64-project-io"));
  }

  @Test
  public void branchNamesDoNotCreateMergeReadyStatusWithoutApprovalAndPassingGates() {
    WorkstreamEvidence evidence = WorkstreamEvidence.builder()
        .localBranch("loop64-source-tests-refactor", "abcdef0", Instant.parse("2026-05-03T21:00:00Z"))
        .branchPurpose("loop64-source-tests-refactor", "source tests refactor")
        .build();

    WorkstreamClassification classification = find(
        new WorkstreamClassifier().classify(evidence),
        "loop64-source-tests-refactor");

    assertNotEquals(WorkstreamStatus.MERGE_READY, classification.status());
    assertFalse(classification.mergeReady());
    assertTrue(classification.blockers().contains("maintainer approval"));
    assertTrue(classification.blockers().contains("gate evidence"));
  }

  @Test
  public void explicitApprovalStillRequiresGateEvidenceBeforeMergeReady() {
    WorkstreamEvidence evidence = WorkstreamEvidence.builder()
        .activeWorktree("feat/alice-source-tests-refactor", Path.of("/worktrees/source-tests"), "cb8973d")
        .maintainerApproval("feat/alice-source-tests-refactor", "approved-by-maintainer")
        .build();

    WorkstreamClassification classification = find(
        new WorkstreamClassifier().classify(evidence),
        "feat/alice-source-tests-refactor");

    assertEquals(WorkstreamStatus.ACTIVE_LOCAL_WORKTREE, classification.status());
    assertFalse(classification.mergeReady());
    assertTrue(classification.blockers().contains("gate evidence"));
  }

  private static WorkstreamClassification find(List<WorkstreamClassification> classifications, String branch) {
    return classifications.stream()
        .filter(classification -> classification.branch().equals(branch))
        .findFirst()
        .orElseThrow(() -> new AssertionError("Missing classification for " + branch));
  }
}
