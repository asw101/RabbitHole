package org.alice.ide.coverage;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MiniIdeSingletonSweep05Test {
  @Test
  public void loadsAndExercisesAssignedSingletonDependentClasses() {
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepNamedClasses(
        "org.alice.ide.clipboard.CopyToClipboardOperation",
        "org.alice.ide.clipboard.edits.ClipboardEdit",
        "org.alice.ide.codedrop.CodePanelWithDropReceptor",
        "org.alice.ide.codeeditor.CodeEditor",
        "org.alice.ide.codeeditor.CommentPane",
        "org.alice.ide.codeeditor.ParametersPane",
        "org.alice.ide.common.AssignmentExpressionPane"
    );

    assertEquals(result.summary(), 7, result.discovered);
    assertEquals(result.summary(), result.discovered, result.loaded);
    assertTrue(result.summary(), result.classLoadFailures.isEmpty());
  }
}
