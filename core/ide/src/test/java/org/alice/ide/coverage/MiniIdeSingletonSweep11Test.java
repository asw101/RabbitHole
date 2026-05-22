package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MiniIdeSingletonSweep11Test {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Test
  public void loadsAndExercisesAssignedSingletonDependentClasses() {
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepNamedClasses(
        "org.alice.ide.croquet.models.cascade.PreviousExpressionBasedFillInWithBlanks",
        "org.alice.ide.croquet.models.cascade.PreviousExpressionBasedFillInWithoutBlanks",
        "org.alice.ide.croquet.models.cascade.TypeExpressionCascadeMenu",
        "org.alice.ide.croquet.models.cascade.integer.MathCascadeMenu",
        "org.alice.ide.croquet.models.cascade.number.MathCascadeMenu",
        "org.alice.ide.croquet.models.declaration.GalleryResourceUtilities",
        "org.alice.ide.croquet.models.gallerybrowser.GalleryDragModel"
    );

    assertEquals(result.summary(), 7, result.discovered);
    assertEquals(result.summary(), result.discovered, result.loaded);
    assertTrue(result.summary(), result.classLoadFailures.isEmpty());
  }
}
