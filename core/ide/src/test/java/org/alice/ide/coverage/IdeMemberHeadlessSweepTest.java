package org.alice.ide.coverage;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class IdeMemberHeadlessSweepTest {

  @Test
  public void exerciseIdeMemberClasses() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise(
        "org.alice.ide.member.AddMethodMenuModel",
        "org.alice.ide.member.AddProcedureMenuModel",
        "org.alice.ide.member.AddFunctionMenuModel",
        "org.alice.ide.member.MemberTabCompositeLogic"
    );
    assertTrue("Should load at least 2 classes, loaded=" + stats.getLoadedCount(),
        stats.getLoadedCount() >= 2);
  }
}
