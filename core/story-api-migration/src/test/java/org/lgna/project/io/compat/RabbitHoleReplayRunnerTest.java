package org.lgna.project.io.compat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;

public class RabbitHoleReplayRunnerTest {
  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void roundTripsEveryReplayCaseThroughRabbitHoleIoDeterministically() throws Exception {
    RabbitHoleReplayRunner runner = new RabbitHoleReplayRunner(temporaryFolder.getRoot().toPath());

    for (ReplayCase replayCase : new ReplayCaseFactory().createCases()) {
      ReplaySummary first = runner.summarize(replayCase);
      ReplaySummary second = runner.summarize(replayCase);

      assertEquals("RabbitHole", first.providerName());
      assertEquals(replayCase.id(), first.caseId());
      assertEquals("RabbitHole summaries must be deterministic for " + replayCase.id(), first.text(), second.text());
      assertSummaryShape(replayCase, first.text());
      assertNoMachineLocalLeakage(first.text());
    }
  }

  @Test
  public void summariesContainArchiveManifestAndResourceSectionsWithoutBinaryPayloads() throws Exception {
    RabbitHoleReplayRunner runner = new RabbitHoleReplayRunner(temporaryFolder.getRoot().toPath());
    ReplayCase replayCase = new ReplayCaseFactory().caseNamed("project-with-text-resource");

    String text = runner.summarize(replayCase).text();

    assertTrue(text.contains("[archives:a3p]\n"));
    assertTrue(text.contains("[archives:a3w]\n"));
    assertTrue(text.contains("[manifest:a3p]\n"));
    assertTrue(text.contains("[manifest:a3w]\n"));
    assertTrue(text.contains("[resources]\n"));
    assertTrue(text.matches("(?s).*sha256:[0-9a-f]{64}.*"));
    assertFalse("Summary must not include generated archive bytes.", text.contains("PK\u0003\u0004"));
  }

  private static void assertSummaryShape(ReplayCase replayCase, String text) {
    assertTrue(text.startsWith("case: " + replayCase.id() + "\n"));
    assertTrue(text.contains("summary-schema: rabbithole.dual-baseline-summary/v1\n"));
    assertTrue(text.contains("[source-tree]\n"));
    assertTrue(text.contains("[source-hashes]\n"));
    assertTrue(text.contains("[archives:a3p]\n"));
    assertTrue(text.contains("[archives:a3w]\n"));
    assertTrue(text.contains("[manifest:a3p]\n"));
    assertTrue(text.contains("[manifest:a3w]\n"));
    assertTrue(text.contains("[resources]\n"));
    assertFalse(text.contains("\r"));
  }

  private void assertNoMachineLocalLeakage(String text) {
    assertFalse(text.contains(temporaryFolder.getRoot().getAbsolutePath()));
    assertFalse(text.contains(System.getProperty("user.home")));
    assertFalse(text.contains(System.getProperty("java.io.tmpdir")));
    assertFalse(text.matches("(?s).*\\b[0-9]{4}-[0-9]{2}-[0-9]{2}\\b.*"));
    assertFalse(text.matches("(?s).*@[0-9a-fA-F]{6,}\\b.*"));
  }
}
