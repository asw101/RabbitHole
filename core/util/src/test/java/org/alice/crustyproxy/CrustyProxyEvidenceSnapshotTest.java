package org.alice.crustyproxy;

import org.junit.Test;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class CrustyProxyEvidenceSnapshotTest {

  @Test
  public void snapshotCapturesRequiredLocalEvidenceAndSavedNodePreference() {
    RecordingCommandRunner runner = new RecordingCommandRunner()
        .returns("git --no-pager branch --show-current", "feat/alice-crusty-proxy-review\n")
        .returns("git --no-pager status --short --", "?? drinkme/\n?? .claude/\n")
        .returns("git --no-pager worktree list --porcelain",
            "worktree /repo\nHEAD cb8973df0f17f5a02a0700a58b0f951693083ab3\nbranch refs/heads/feat/alice-crusty-proxy-review\n")
        .returns("git submodule status tweedle-lang", "-1b75c0 tweedle-lang\n")
        .returns("test -d tweedle-lang/Grammar", "", 1)
        .returns("git rev-parse --git-common-dir", "/repo/.git/worktrees/feat/alice-crusty-proxy-review\n")
        .returns("find /repo/.git/worktrees/feat/alice-crusty-proxy-review/hooks -maxdepth 1 -type f -perm -111 -printf %f\\n",
            "post-checkout\npost-commit\npost-merge\npre-push\n");

    CrustyProxyEvidenceCollector collector = new CrustyProxyEvidenceCollector(
        Path.of("/repo"),
        Path.of("/repo/drinkme/2026-05-03-crusty-modernization-review.md"),
        runner,
        new EnvironmentSettings(Map.of("NODE_OPTIONS", "--max-old-space-size=32768")));

    CrustyProxyEvidenceSnapshot snapshot = collector.collect();

    assertEquals("feat/alice-crusty-proxy-review", snapshot.currentBranch());
    assertTrue(snapshot.dirtyState().contains("?? drinkme/"));
    assertTrue(snapshot.worktrees().containsBranch("feat/alice-crusty-proxy-review"));
    assertFalse(snapshot.hasTweedleGrammar());
    assertFalse(snapshot.isBroadMavenValidationAllowed());
    assertEquals(Set.of("post-checkout", "post-commit", "post-merge", "pre-push"), snapshot.activeHooks());
    assertEquals(32768, snapshot.nodeMaxOldSpaceSizeMegabytes());
    assertTrue(snapshot.citations().containsCommand("git --no-pager worktree list --porcelain"));
    assertTrue(snapshot.citations().containsCommand("git submodule status tweedle-lang"));
  }

  @Test
  public void refusesArtifactPathsOutsideDrinkme() {
    CrustyProxyEvidenceCollector collector = new CrustyProxyEvidenceCollector(
        Path.of("/repo"),
        Path.of("/repo/core/util/src/main/java/NotAReviewArtifact.md"),
        new RecordingCommandRunner(),
        EnvironmentSettings.empty());

    InvalidReviewArtifactPathException thrown = assertThrows(
        InvalidReviewArtifactPathException.class,
        collector::collect);

    assertTrue(thrown.getMessage().contains("drinkme"));
    assertTrue(thrown.getMessage().contains("/repo/core/util/src/main/java/NotAReviewArtifact.md"));
  }

  @Test
  public void refusesPathTraversalInArtifactDestination() {
    CrustyProxyEvidenceCollector collector = new CrustyProxyEvidenceCollector(
        Path.of("/repo"),
        Path.of("/repo/drinkme/../core/escaped.md"),
        new RecordingCommandRunner(),
        EnvironmentSettings.empty());

    InvalidReviewArtifactPathException thrown = assertThrows(
        InvalidReviewArtifactPathException.class,
        collector::collect);

    assertTrue(thrown.getMessage().contains("repository root"));
    assertTrue(thrown.getMessage().contains("drinkme"));
  }

  private static final class RecordingCommandRunner implements CrustyProxyCommandRunner {
    private final Map<String, CrustyProxyCommandResult> results = new LinkedHashMap<>();

    RecordingCommandRunner returns(String command, String output) {
      return returns(command, output, 0);
    }

    RecordingCommandRunner returns(String command, String output, int exitCode) {
      results.put(command, new CrustyProxyCommandResult(command, exitCode, output, ""));
      return this;
    }

    @Override
    public CrustyProxyCommandResult run(String command) {
      if (!results.containsKey(command)) {
        throw new AssertionError("Unexpected command: " + command);
      }
      return results.get(command);
    }
  }
}
