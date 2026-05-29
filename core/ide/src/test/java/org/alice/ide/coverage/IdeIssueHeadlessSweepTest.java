package org.alice.ide.coverage;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class IdeIssueHeadlessSweepTest {

  @Test
  public void exerciseIssueClasses() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise(
        "org.alice.ide.issue.CurrentProjectAttachment",
        "org.alice.ide.issue.DefaultExceptionHandler",
        "org.alice.ide.issue.ExceptionHandler",
        "org.alice.ide.issue.GraphicsPropertiesAttachment",
        "org.alice.ide.issue.IdeUncaughtExceptionHandler",
        "org.alice.ide.issue.ImageAttachment",
        "org.alice.ide.issue.SubmitReportUtilities",
        "org.alice.ide.issue.UserProgramRunningStateUtilities"
    );
    assertTrue("Should load at least 3 classes, loaded=" + stats.getLoadedCount(),
        stats.getLoadedCount() >= 3);
  }
}
