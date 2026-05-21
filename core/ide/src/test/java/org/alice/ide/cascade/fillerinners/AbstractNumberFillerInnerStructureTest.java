package org.alice.ide.cascade.fillerinners;

import org.junit.Test;
import org.lgna.project.annotations.ValueDetails;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.Expression;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import static org.junit.Assert.*;

public class AbstractNumberFillerInnerStructureTest {
  @Test
  public void classIsPublicAbstractAndExtendsExpressionFillerInner() {
    assertTrue(Modifier.isPublic(AbstractNumberFillerInner.class.getModifiers()));
    assertTrue(Modifier.isAbstract(AbstractNumberFillerInner.class.getModifiers()));
    assertEquals(ExpressionFillerInner.class, AbstractNumberFillerInner.class.getSuperclass());
  }

  @Test
  public void constructorsMatchExpectedSignatures() throws Exception {
    Constructor<AbstractNumberFillerInner> abstractTypeCtor = AbstractNumberFillerInner.class.getDeclaredConstructor(AbstractType.class);
    Constructor<AbstractNumberFillerInner> classCtor = AbstractNumberFillerInner.class.getDeclaredConstructor(Class.class);

    assertTrue(Modifier.isPublic(abstractTypeCtor.getModifiers()));
    assertTrue(Modifier.isPublic(classCtor.getModifiers()));
  }

  @Test
  public void appendItemsOverrideRetainsExpressionContract() throws Exception {
    Method method = AbstractNumberFillerInner.class.getMethod(
        "appendItems", List.class, ValueDetails.class, boolean.class, Expression.class);

    assertEquals(void.class, method.getReturnType());
    assertFalse(Modifier.isAbstract(method.getModifiers()));
  }
}
