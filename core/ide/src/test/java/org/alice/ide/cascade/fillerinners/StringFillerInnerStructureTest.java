package org.alice.ide.cascade.fillerinners;

import org.junit.Test;
import org.lgna.project.ast.JavaType;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class StringFillerInnerStructureTest {
  @Test
  public void classExtendsExpressionFillerInner() {
    assertEquals(ExpressionFillerInner.class, StringFillerInner.class.getSuperclass());
    assertTrue(Modifier.isPublic(StringFillerInner.class.getModifiers()));
  }

  @Test
  public void getLiteralsReturnsExpectedDefaultValue() {
    assertArrayEquals(new String[] {"hello"}, StringFillerInner.getLiterals());
  }

  @Test
  public void constructorConfiguresStringType() {
    StringFillerInner fillerInner = new StringFillerInner();

    assertTrue(fillerInner.isAssignableTo(JavaType.getInstance(Object.class)));
    assertFalse(fillerInner.isAssignableTo(JavaType.getInstance(Number.class)));
  }
}
