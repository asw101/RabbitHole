package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MiniIdeSingletonSweep30Test {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Test
  public void loadsAndExercisesAssignedSingletonDependentClasses() {
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepNamedClasses(
        "org.lgna.ik.poser.animation.composites.AnimatorControlComposite",
        "org.lgna.ik.poser.animation.composites.TimeLineModifierComposite",
        "org.lgna.ik.poser.animation.views.JTimeLineView",
        "org.lgna.ik.poser.animation.views.TimeLinePoseMarker"
    );

    assertEquals(result.summary(), 4, result.discovered);
    assertEquals(result.summary(), result.discovered, result.loaded);
    assertTrue(result.summary(), result.classLoadFailures.isEmpty());
  }
}
