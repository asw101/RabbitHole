package org.alice.ide.member;

import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class MemberTabCompositeLogicTest {
  private static class VisibilitySamples {
    private void hidden() {
    }

    public void shown() {
    }

    public static void shownStatically() {
    }
  }

  private static class Parent {
    public void wave() {
    }
  }

  private static class Child extends Parent {
    @Override
    public void wave() {
    }

    public void jump() {
    }
  }

  @Test
  public void getExpandedAccountingForInertForcesExpandedWhenToolPalettesAreInert() {
    assertTrue(MemberTabCompositeLogic.getExpandedAccountingForInert(true, false));
    assertFalse(MemberTabCompositeLogic.getExpandedAccountingForInert(false, false));
    assertTrue(MemberTabCompositeLogic.getExpandedAccountingForInert(false, true));
  }

  @Test
  public void isInclusionDesiredRejectsStaticAndHiddenMembersButKeepsUserAuthoredMembers() throws Exception {
    JavaMethod hiddenMethod = JavaMethod.getInstance(VisibilitySamples.class.getDeclaredMethod("hidden"));
    JavaMethod shownMethod = JavaMethod.getInstance(VisibilitySamples.class.getDeclaredMethod("shown"));
    JavaMethod staticMethod = JavaMethod.getInstance(VisibilitySamples.class.getDeclaredMethod("shownStatically"));
    UserMethod userMethod = new UserMethod("userAuthored", void.class, new UserParameter[0], new BlockStatement());

    assertFalse(MemberTabCompositeLogic.isInclusionDesired(hiddenMethod));
    assertTrue(MemberTabCompositeLogic.isInclusionDesired(shownMethod));
    assertFalse(MemberTabCompositeLogic.isInclusionDesired(staticMethod));
    assertTrue(MemberTabCompositeLogic.isInclusionDesired(userMethod));
  }

  @Test
  public void withoutOverridesRemovesOverridingMethodsWhenSuperMethodIsAlsoPresent() {
    JavaMethod parentWave = JavaMethod.getInstance(Parent.class, "wave");
    JavaMethod childWave = JavaMethod.getInstance(Child.class, "wave");
    JavaMethod childJump = JavaMethod.getInstance(Child.class, "jump");

    List<JavaMethod> filteredMethods = MemberTabCompositeLogic.withoutOverrides(Arrays.asList(parentWave, childWave, childJump));

    assertEquals(Arrays.asList(parentWave, childJump), filteredMethods);
  }
}
