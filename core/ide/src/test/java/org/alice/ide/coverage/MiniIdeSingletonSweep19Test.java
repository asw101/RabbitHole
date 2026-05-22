package org.alice.ide.coverage;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MiniIdeSingletonSweep19Test {
  @Test
  public void loadsAndExercisesAssignedSingletonDependentClasses() {
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepNamedClasses(
        "org.alice.ide.javacode.croquet.views.JavaCodeView",
        "org.alice.ide.member.AddMethodMenuModel",
        "org.alice.ide.member.FunctionTabComposite",
        "org.alice.ide.member.FunctionsOfReturnTypeSubComposite",
        "org.alice.ide.member.MemberTabComposite",
        "org.alice.ide.member.ProcedureTabComposite",
        "org.alice.ide.member.views.MethodsSubView"
    );

    assertEquals(result.summary(), 7, result.discovered);
    assertEquals(result.summary(), result.discovered, result.loaded);
    assertTrue(result.summary(), result.classLoadFailures.isEmpty());
  }
}
