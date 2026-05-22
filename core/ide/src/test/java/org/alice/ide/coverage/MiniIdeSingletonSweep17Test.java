package org.alice.ide.coverage;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MiniIdeSingletonSweep17Test {
  @Test
  public void loadsAndExercisesAssignedSingletonDependentClasses() {
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepNamedClasses(
        "org.alice.ide.highlight.IdeHighlightStencil",
        "org.alice.ide.icons.FieldIcon",
        "org.alice.ide.icons.IconFactoryManager",
        "org.alice.ide.instancefactory.AbstractInstanceFactory",
        "org.alice.ide.instancefactory.ThisFieldAccessFactory",
        "org.alice.ide.instancefactory.ThisInstanceFactory",
        "org.alice.ide.instancefactory.croquet.InstanceFactoryFillIn"
    );

    assertEquals(result.summary(), 7, result.discovered);
    assertEquals(result.summary(), result.discovered, result.loaded);
    assertTrue(result.summary(), result.classLoadFailures.isEmpty());
  }
}
