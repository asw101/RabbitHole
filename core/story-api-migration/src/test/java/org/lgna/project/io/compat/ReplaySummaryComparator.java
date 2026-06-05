package org.lgna.project.io.compat;

import java.util.Objects;

final class ReplaySummaryComparator {
  void assertMatches(ReplayCase replayCase, ReplaySummary baseline, ReplaySummary rabbitHole) {
    Objects.requireNonNull(replayCase, "replayCase");
    Objects.requireNonNull(baseline, "baseline");
    Objects.requireNonNull(rabbitHole, "rabbitHole");
    if (!replayCase.id().equals(baseline.caseId()) || !replayCase.id().equals(rabbitHole.caseId())) {
      throw new AssertionError(
          "Replay summary case id mismatch for expected case "
              + replayCase.id()
              + ": "
              + baseline.providerName()
              + "="
              + baseline.caseId()
              + ", "
              + rabbitHole.providerName()
              + "="
              + rabbitHole.caseId());
    }
    if (baseline.text().equals(rabbitHole.text())) {
      return;
    }
    throw new AssertionError(
        "Replay summary mismatch for case "
            + replayCase.id()
            + " between "
            + baseline.providerName()
            + " and "
            + rabbitHole.providerName()
            + " in "
            + firstDifferingSection(baseline.text(), rabbitHole.text())
            + "\n"
            + compactDiff(baseline.text(), rabbitHole.text()));
  }

  private static String firstDifferingSection(String expected, String actual) {
    String[] expectedLines = expected.split("\n", -1);
    String[] actualLines = actual.split("\n", -1);
    int max = Math.max(expectedLines.length, actualLines.length);
    String section = "<header>";
    for (int i = 0; i < max; i++) {
      if ((i < expectedLines.length) && isSectionHeader(expectedLines[i])) {
        section = expectedLines[i];
      }
      if ((i < actualLines.length) && isSectionHeader(actualLines[i])) {
        section = actualLines[i];
      }
      String expectedLine = (i < expectedLines.length) ? expectedLines[i] : "<missing>";
      String actualLine = (i < actualLines.length) ? actualLines[i] : "<missing>";
      if (!expectedLine.equals(actualLine)) {
        return section;
      }
    }
    return section;
  }

  private static boolean isSectionHeader(String line) {
    return line.startsWith("[") && line.endsWith("]");
  }

  private static String compactDiff(String expected, String actual) {
    String[] expectedLines = expected.split("\n", -1);
    String[] actualLines = actual.split("\n", -1);
    int firstDifference = firstDifference(expectedLines, actualLines);
    int from = Math.max(0, firstDifference - 2);
    int to = Math.min(Math.max(expectedLines.length, actualLines.length), firstDifference + 3);
    StringBuilder diff = new StringBuilder("--- baseline\n+++ rabbithole\n");
    for (int i = from; i < to; i++) {
      String expectedLine = (i < expectedLines.length) ? expectedLines[i] : null;
      String actualLine = (i < actualLines.length) ? actualLines[i] : null;
      if (Objects.equals(expectedLine, actualLine)) {
        diff.append(' ').append(expectedLine).append('\n');
      } else {
        if (expectedLine != null) {
          diff.append('-').append(expectedLine).append('\n');
        }
        if (actualLine != null) {
          diff.append('+').append(actualLine).append('\n');
        }
      }
    }
    return diff.toString();
  }

  private static int firstDifference(String[] expectedLines, String[] actualLines) {
    int max = Math.max(expectedLines.length, actualLines.length);
    for (int i = 0; i < max; i++) {
      String expected = (i < expectedLines.length) ? expectedLines[i] : null;
      String actual = (i < actualLines.length) ? actualLines[i] : null;
      if (!Objects.equals(expected, actual)) {
        return i;
      }
    }
    return 0;
  }
}
