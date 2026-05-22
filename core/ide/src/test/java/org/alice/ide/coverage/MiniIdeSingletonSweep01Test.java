package org.alice.ide.coverage;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MiniIdeSingletonSweep01Test {
  @Test
  public void loadsAndExercisesAssignedSingletonDependentClasses() {
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepNamedClasses(
        "org.alice.ide.ApiConfigurationManager",
        "org.alice.ide.IDE",
        "org.alice.ide.MetaDeclarationFauxState",
        "org.alice.ide.ProjectApplication",
        "org.alice.ide.ProjectFileUtilities",
        "org.alice.ide.ProjectHistoryManager",
        "org.alice.ide.ProjectStack"
    );

    assertEquals(result.summary(), 7, result.discovered);
    assertEquals(result.summary(), result.discovered, result.loaded);
    assertTrue(result.summary(), result.classLoadFailures.isEmpty());
  }
}
