package org.alice.ide.member;

import org.junit.Test;
import org.lgna.project.annotations.Visibility;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.JavaField;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MemberTabCompositeLogicBehaviorTest {
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

  private static final class FieldSamples {
    public static String STATIC_LABEL = "static";
  }

  private static void assertConstructorThrowsAssertionError(Class<?> type) throws Exception {
    Constructor<?> constructor = type.getDeclaredConstructor();
    constructor.setAccessible(true);
    try {
      constructor.newInstance();
      fail("Expected constructor to reject instantiation");
    } catch (InvocationTargetException ite) {
      assertTrue(ite.getCause() instanceof AssertionError);
    }
  }

  @Test
  public void utilityConstructorRejectsInstantiation() throws Exception {
    assertConstructorThrowsAssertionError(MemberTabCompositeLogic.class);
  }

  @Test
  public void isInclusionDesiredRejectsStaticAndHiddenFields() throws Exception {
    JavaField staticField = JavaField.getInstance(FieldSamples.class.getField("STATIC_LABEL"));
    UserField hiddenField = new UserField("hidden", String.class);
    hiddenField.setVisibility(Visibility.COMPLETELY_HIDDEN);
    UserMethod visibleMethod = new UserMethod("visible", void.class, new UserParameter[0], new BlockStatement());

    assertFalse(MemberTabCompositeLogic.isInclusionDesired(staticField));
    assertFalse(MemberTabCompositeLogic.isInclusionDesired(hiddenField));
    assertTrue(MemberTabCompositeLogic.isInclusionDesired(visibleMethod));
  }

  @Test
  public void withoutOverridesKeepsOverridingMethodsWhenTheSuperclassMethodIsAbsent() {
    JavaMethod childWave = JavaMethod.getInstance(Child.class, "wave");
    JavaMethod childJump = JavaMethod.getInstance(Child.class, "jump");

    assertEquals(List.of(childWave, childJump), MemberTabCompositeLogic.withoutOverrides(List.of(childWave, childJump)));
  }
}
