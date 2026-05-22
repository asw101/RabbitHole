package org.alice.ide.coverage;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MiniIdeSingletonSweep18Test {
  @Test
  public void loadsAndExercisesAssignedSingletonDependentClasses() {
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepNamedClasses(
        "org.alice.ide.instancefactory.croquet.InstanceFactoryState",
        "org.alice.ide.instancefactory.croquet.views.icons.IndirectCurrentAccessibleTypeIcon",
        "org.alice.ide.issue.CurrentProjectAttachment",
        "org.alice.ide.issue.DefaultExceptionHandler",
        "org.alice.ide.issue.IdeUncaughtExceptionHandler",
        "org.alice.ide.issue.croquet.AnomalousSituationComposite",
        "org.alice.ide.javacode.croquet.JavaCodeFrameComposite"
    );

    assertEquals(result.summary(), 7, result.discovered);
    assertEquals(result.summary(), result.discovered, result.loaded);
    assertTrue(result.summary(), result.classLoadFailures.isEmpty());
  }
}
