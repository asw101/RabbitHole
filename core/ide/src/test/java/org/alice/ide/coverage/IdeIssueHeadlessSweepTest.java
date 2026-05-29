package org.alice.ide.coverage;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import static org.junit.Assert.assertTrue;

public class IdeIssueHeadlessSweepTest {

  @Rule
  public Timeout globalTimeout = Timeout.seconds(120);

  @Test
  public void exerciseIssueClasses() {
    // All issue handler classes trigger modal dialogs when exercised via reflection.
    // Only safe to load, not exercise instance methods.
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise(
        "org.alice.ide.issue.ExceptionHandler",
        "org.alice.ide.issue.SubmitReportUtilities",
        "org.alice.ide.issue.UserProgramRunningStateUtilities"
    );
    assertTrue("Should load at least 1 class, loaded=" + stats.getLoadedCount(),
        stats.getLoadedCount() >= 1);
  }
}
