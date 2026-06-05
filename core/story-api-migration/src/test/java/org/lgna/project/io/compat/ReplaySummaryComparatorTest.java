package org.lgna.project.io.compat;

import org.junit.Test;
import org.lgna.project.Project;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.story.SProgram;

import java.util.List;

import static org.junit.Assert.*;

public class ReplaySummaryComparatorTest {
  @Test
  public void matchingSummariesPassExactly() {
    ReplayCase replayCase = new ReplayCase("match-case", project("MatchCase"), List.of());
    ReplaySummary baseline = summary("match-case", "Baseline", "version.txt");
    ReplaySummary rabbitHole = summary("match-case", "RabbitHole", "version.txt");

    new ReplaySummaryComparator().assertMatches(replayCase, baseline, rabbitHole);
  }

  @Test
  public void mismatchFailureNamesCaseProvidersSectionAndCompactDiff() {
    ReplayCase replayCase = new ReplayCase("mismatch-case", project("MismatchCase"), List.of());
    ReplaySummary baseline = summary("mismatch-case", "Baseline", "version.txt");
    ReplaySummary rabbitHole = summary("mismatch-case", "RabbitHole", "changed-version.txt");

    AssertionError thrown = assertThrows(
        AssertionError.class,
        () -> new ReplaySummaryComparator().assertMatches(replayCase, baseline, rabbitHole));
    String message = thrown.getMessage();

    assertTrue(message.contains("mismatch-case"));
    assertTrue(message.contains("Baseline"));
    assertTrue(message.contains("RabbitHole"));
    assertTrue(message.contains("[archives:a3p]"));
    assertTrue(message.contains("-version.txt"));
    assertTrue(message.contains("+changed-version.txt"));
  }

  @Test
  public void mismatchedCaseIdsFailBeforeTextComparison() {
    ReplayCase replayCase = new ReplayCase("expected-case", project("ExpectedCase"), List.of());
    ReplaySummary baseline = summary("different-case", "Baseline", "version.txt");
    ReplaySummary rabbitHole = summary("expected-case", "RabbitHole", "version.txt");

    AssertionError thrown = assertThrows(
        AssertionError.class,
        () -> new ReplaySummaryComparator().assertMatches(replayCase, baseline, rabbitHole));

    assertTrue(thrown.getMessage().contains("expected-case"));
    assertTrue(thrown.getMessage().contains("different-case"));
  }

  private static ReplaySummary summary(String caseId, String providerName, String archiveLine) {
    return new ReplaySummary(
        caseId,
        providerName,
        "case: " + caseId + "\n"
            + "summary-schema: rabbithole.dual-baseline-summary/v1\n"
            + "\n"
            + "[archives:a3p]\n"
            + archiveLine + "\n");
  }

  private static Project project(String name) {
    NamedUserType programType = new NamedUserType();
    programType.name.setValue(name);
    programType.superType.setValue(JavaType.getInstance(SProgram.class));
    return new Project(programType, Project.SceneCameraType.WindowCamera);
  }
}
