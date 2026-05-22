package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MiniIdeSingletonSweep25Test {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Test
  public void loadsAndExercisesAssignedSingletonDependentClasses() {
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepNamedClasses(
        "org.alice.stageide.gallerybrowser.ImportTab",
        "org.alice.stageide.member.AddListenerProceduresComposite",
        "org.alice.stageide.oneshot.DynamicOneShotMenuModel",
        "org.alice.stageide.oneshot.edits.MethodInvocationEdit",
        "org.alice.stageide.perspectives.PerspectiveSwitchingCardOwnerComposite",
        "org.alice.stageide.perspectives.code.CodeContextSplitComposite",
        "org.alice.stageide.program.ProgramContext"
    );

    assertEquals(result.summary(), 7, result.discovered);
    assertEquals(result.summary(), result.discovered, result.loaded);
    assertTrue(result.summary(), result.classLoadFailures.isEmpty());
  }
}
