package org.alice.ide.member;

import org.junit.Test;
import org.lgna.project.ast.JavaField;
import org.lgna.project.ast.JavaMethod;

import java.util.List;

import static org.junit.Assert.*;

public class MemberTabCompositeDeepBehaviorTest {
  private static final class FieldSamples {
    public int instanceCount;
    public static int staticCount;
  }

  private static class Parent {
    public void wave() {
    }

    public void clap() {
    }
  }

  @Test
  public void isInclusionDesiredKeepsVisibleInstanceFieldsButRejectsStaticFields() throws Exception {
    JavaField instanceField = JavaField.getInstance(FieldSamples.class.getField("instanceCount"));
    JavaField staticField = JavaField.getInstance(FieldSamples.class.getField("staticCount"));

    assertTrue(MemberTabCompositeLogic.isInclusionDesired(instanceField));
    assertFalse(MemberTabCompositeLogic.isInclusionDesired(staticField));
  }

  @Test
  public void withoutOverridesLeavesUnrelatedMethodsInPlace() {
    JavaMethod parentWave = JavaMethod.getInstance(Parent.class, "wave");
    JavaMethod parentClap = JavaMethod.getInstance(Parent.class, "clap");

    assertEquals(List.of(parentWave, parentClap), MemberTabCompositeLogic.withoutOverrides(List.of(parentWave, parentClap)));
  }
}
