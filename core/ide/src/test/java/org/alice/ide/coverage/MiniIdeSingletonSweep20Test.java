package org.alice.ide.coverage;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MiniIdeSingletonSweep20Test {
  @Test
  public void loadsAndExercisesAssignedSingletonDependentClasses() {
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepNamedClasses(
        "org.alice.ide.members.components.MembersView",
        "org.alice.ide.members.components.templates.MethodPopupMenuModel",
        "org.alice.ide.meta.DeclarationMeta",
        "org.alice.ide.name.validators.ResourceNameValidator",
        "org.alice.ide.operations.ast.DeleteParameterOperation",
        "org.alice.ide.projecturi.FileSystemTab",
        "org.alice.ide.projecturi.MyProjectsTab"
    );

    assertEquals(result.summary(), 7, result.discovered);
    assertEquals(result.summary(), result.discovered, result.loaded);
    assertTrue(result.summary(), result.classLoadFailures.isEmpty());
  }
}
