package org.lgna.project.io.compat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;

public class DualBaselineReplayHarnessTest {
  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void fallbackModeValidatesDeterministicRabbitHoleSummariesWithoutBaselineCheckout() throws Exception {
    BaselineMode mode = BaselineMode.unavailable("not configured");
    RabbitHoleReplayRunner runner = new RabbitHoleReplayRunner(temporaryFolder.getRoot().toPath());
    ReplaySummaryComparator comparator = new ReplaySummaryComparator();

    assertFalse(mode.isAvailable());
    for (ReplayCase replayCase : new ReplayCaseFactory().createCases()) {
      ReplaySummary first = runner.summarize(replayCase);
      ReplaySummary second = runner.summarize(replayCase);
      comparator.assertMatches(replayCase, first, second);
      assertEquals(first.text(), second.text());
    }
  }

  @Test
  public void strictModePassesWhenFakeBaselineMatchesRabbitHoleSummaryExactly() throws Exception {
    ReplaySummaryComparator comparator = new ReplaySummaryComparator();
    ReplaySummaryProvider rabbitHole = replayCase -> summary(replayCase.id(), "RabbitHole", "version.txt");
    ReplaySummaryProvider baseline = replayCase -> summary(replayCase.id(), "Baseline", "version.txt");

    for (ReplayCase replayCase : new ReplayCaseFactory().createCases()) {
      comparator.assertMatches(replayCase, baseline.summarize(replayCase), rabbitHole.summarize(replayCase));
    }
  }

  @Test
  public void strictModeFailsWhenFakeBaselineDiffersFromRabbitHoleSummary() throws Exception {
    ReplayCase replayCase = new ReplayCaseFactory().caseNamed("project-with-multiple-resources");
    ReplaySummaryProvider rabbitHole = input -> summary(input.id(), "RabbitHole", "resources/generated-b.txt");
    ReplaySummaryProvider baseline = input -> summary(input.id(), "Baseline", "resources/generated-a.txt");

    AssertionError thrown = assertThrows(
        AssertionError.class,
        () -> new ReplaySummaryComparator().assertMatches(
            replayCase,
            baseline.summarize(replayCase),
            rabbitHole.summarize(replayCase)));

    assertTrue(thrown.getMessage().contains("project-with-multiple-resources"));
    assertTrue(thrown.getMessage().contains("resources/generated-a.txt"));
    assertTrue(thrown.getMessage().contains("resources/generated-b.txt"));
  }

  private static ReplaySummary summary(String caseId, String providerName, String archiveEntry) {
    return new ReplaySummary(
        caseId,
        providerName,
        "case: " + caseId + "\n"
            + "summary-schema: rabbithole.dual-baseline-summary/v1\n"
            + "\n"
            + "[archives:a3p]\n"
            + archiveEntry + "\n");
  }
}
