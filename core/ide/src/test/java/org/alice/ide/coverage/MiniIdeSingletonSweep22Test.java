package org.alice.ide.coverage;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MiniIdeSingletonSweep22Test {
  @Test
  public void loadsAndExercisesAssignedSingletonDependentClasses() {
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepNamedClasses(
        "org.alice.ide.stencil.PotentialDropReceptorsFeedbackView",
        "org.alice.ide.testing.framesize.croquet.CycleFrameSizeOperation",
        "org.alice.ide.typehierarchy.components.TypeHierarchyView",
        "org.alice.ide.typemanager.TypeManager",
        "org.alice.ide.uricontent.AbstractFileProjectLoader",
        "org.alice.ide.x.AstI18nFactory",
        "org.alice.ide.x.TemplateAstI18nFactory"
    );

    assertEquals(result.summary(), 7, result.discovered);
    assertEquals(result.summary(), result.discovered, result.loaded);
    assertTrue(result.summary(), result.classLoadFailures.isEmpty());
  }
}
