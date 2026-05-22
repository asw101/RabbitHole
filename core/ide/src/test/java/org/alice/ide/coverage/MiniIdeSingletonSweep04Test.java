package org.alice.ide.coverage;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MiniIdeSingletonSweep04Test {
  @Test
  public void loadsAndExercisesAssignedSingletonDependentClasses() {
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepNamedClasses(
        "org.alice.ide.ast.type.croquet.ImportTypeIteratingOperation",
        "org.alice.ide.ast.type.merge.croquet.edits.ImportTypeEdit",
        "org.alice.ide.capture.ImageCaptureComposite",
        "org.alice.ide.cascade.ExpressionCascadeManager",
        "org.alice.ide.cascade.fillerinners.PoseFillerInner",
        "org.alice.ide.cascade.fillerinners.ResourceFillerInner",
        "org.alice.ide.clipboard.CopyFromClipboardOperation"
    );

    assertEquals(result.summary(), 7, result.discovered);
    assertEquals(result.summary(), result.discovered, result.loaded);
    assertTrue(result.summary(), result.classLoadFailures.isEmpty());
  }
}
