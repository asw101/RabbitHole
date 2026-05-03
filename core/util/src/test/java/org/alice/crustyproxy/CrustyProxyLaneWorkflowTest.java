package org.alice.crustyproxy;

import org.junit.Test;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class CrustyProxyLaneWorkflowTest {

  @Test
  public void laneProducesEvidenceBackedArtifactWithoutRemoteOrMergeSideEffects() {
    RecordingCommandRunner commands = new RecordingCommandRunner()
        .returns("git --no-pager branch --show-current", "feat/alice-crusty-proxy-review\n")
        .returns("git --no-pager status --short --", "?? drinkme/\n")
        .returns("git --no-pager worktree list --porcelain",
            "worktree /repo\nHEAD cb8973d\nbranch refs/heads/feat/alice-crusty-proxy-review\n")
        .returns("git submodule status tweedle-lang", " 1b75c0 tweedle-lang\n")
        .returns("test -d tweedle-lang/Grammar", "")
        .returns("git rev-parse --git-common-dir", "/repo/.git/worktrees/feat/alice-crusty-proxy-review\n")
        .returns("find /repo/.git/worktrees/feat/alice-crusty-proxy-review/hooks -maxdepth 1 -type f -perm -111 -printf %f\\n",
            "post-checkout\npost-commit\npost-merge\npre-push\n");
    InMemoryReviewArtifactStore artifacts = new InMemoryReviewArtifactStore();
    CrustyProxyLane lane = new CrustyProxyLane(
        Path.of("/repo"),
        Path.of("/repo/drinkme/2026-05-03-crusty-modernization-review.md"),
        commands,
        artifacts,
        new ReviewGateEvaluator(),
        new WorkstreamClassifier());

    ReviewResult result = lane.review(ReviewRequest.forBranch("feat/alice-crusty-proxy-review")
        .documentationOnly(true)
        .namedSeam("skeptical modernization review lane")
        .build());

    assertFalse(result.mergeApproved());
    assertEquals(Path.of("/repo/drinkme/2026-05-03-crusty-modernization-review.md"), artifacts.lastPath());
    assertTrue(artifacts.lastContent().contains("## Evidence snapshot"));
    assertTrue(artifacts.lastContent().contains("## Workstream classification"));
    assertTrue(artifacts.lastContent().contains("## Gate matrix"));
    assertTrue(artifacts.lastContent().contains("## Coverage policy"));
    assertTrue(artifacts.lastContent().contains("## Class-size policy"));
    assertTrue(artifacts.lastContent().contains("## External service applicability"));
    assertTrue(artifacts.lastContent().contains("## Security and artifact hygiene"));
    assertTrue(artifacts.lastContent().contains("## Next high-value targets"));
    assertTrue(commands.didNotRunForbiddenRemoteOrMergeCommands());
  }

  @Test
  public void missingTweedleGrammarFailsMavenEnvironmentGateBeforeBroadValidation() {
    CrustyProxyEvidenceSnapshot evidence = CrustyProxyEvidenceSnapshot.builder()
        .currentBranch("feat/alice-source-tests-refactor")
        .hasTweedleGrammar(false)
        .mavenVersion("3.9.9")
        .javaVersion("21")
        .build();
    ReviewCandidate candidate = ReviewCandidate.builder("feat/alice-source-tests-refactor")
        .sourceChanging(true)
        .touchedModule("core/tweedle")
        .namedSeam("Tweedle parser compatibility")
        .characterizationTests(List.of("TweedleParseTest"))
        .focusedValidation("mvn -DincludeSims=false -Dinstall4j.skip clean test")
        .build();

    GateReport report = new ReviewGateEvaluator().evaluate(candidate, evidence);

    assertEquals(GateStatus.BLOCKED, report.gate("Maven environment gate").status());
    assertTrue(report.gate("Maven environment gate").evidence().contains("git submodule update --init tweedle-lang"));
    assertTrue(report.gate("Maven environment gate").evidence().contains("tweedle-lang/Grammar"));
  }

  @Test
  public void malformedBranchOrArtifactInputFailsClosed() {
    CrustyProxyLane lane = new CrustyProxyLane(
        Path.of("/repo"),
        Path.of("/repo/drinkme/2026-05-03-crusty-modernization-review.md"),
        new RecordingCommandRunner(),
        new InMemoryReviewArtifactStore(),
        new ReviewGateEvaluator(),
        new WorkstreamClassifier());

    UnsafeReviewInputException thrown = assertThrows(
        UnsafeReviewInputException.class,
        () -> lane.review(ReviewRequest.forBranch("../escape")
            .documentationOnly(true)
            .namedSeam("bad input")
            .build()));

    assertTrue(thrown.getMessage().contains("branch"));
    assertTrue(thrown.getMessage().contains("untrusted"));
  }

  private static final class RecordingCommandRunner implements CrustyProxyCommandRunner {
    private final List<String> commands = new ArrayList<>();
    private final List<CrustyProxyCommandResult> results = new ArrayList<>();

    RecordingCommandRunner returns(String command, String output) {
      return returns(command, output, 0);
    }

    RecordingCommandRunner returns(String command, String output, int exitCode) {
      results.add(new CrustyProxyCommandResult(command, exitCode, output, ""));
      return this;
    }

    @Override
    public CrustyProxyCommandResult run(String command) {
      commands.add(command);
      return results.stream()
          .filter(result -> result.command().equals(command))
          .findFirst()
          .orElseThrow(() -> new AssertionError("Unexpected command: " + command));
    }

    boolean didNotRunForbiddenRemoteOrMergeCommands() {
      return commands.stream().noneMatch(command ->
          command.contains("git push")
              || command.contains("gh pr")
              || command.contains("git merge")
              || command.contains("upstream-source")
              || command.contains("TheAliceProject/alice3"));
    }
  }

  private static final class InMemoryReviewArtifactStore implements ReviewArtifactStore {
    private Path lastPath;
    private String lastContent;

    @Override
    public void write(Path path, String content) {
      this.lastPath = path;
      this.lastContent = content;
    }

    Path lastPath() {
      return lastPath;
    }

    String lastContent() {
      return lastContent;
    }
  }
}
