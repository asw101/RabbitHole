package org.alice.ide.ast;

import org.junit.Test;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.FauxExpression;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;

import static org.junit.Assert.*;

public class EmptyExpressionComprehensiveTest {
  private enum SampleEnum {
    VALUE
  }

  private void assertTypeFromClass(Class<?> cls) {
    EmptyExpression expression = new EmptyExpression(cls);
    assertSame(JavaType.getInstance(cls), expression.getType());
  }

  @Test
  public void byteWrapperTypeIsSupported() {
    assertTypeFromClass(Byte.class);
  }

  @Test
  public void shortWrapperTypeIsSupported() {
    assertTypeFromClass(Short.class);
  }

  @Test
  public void longWrapperTypeIsSupported() {
    assertTypeFromClass(Long.class);
  }

  @Test
  public void floatWrapperTypeIsSupported() {
    assertTypeFromClass(Float.class);
  }

  @Test
  public void characterWrapperTypeIsSupported() {
    assertTypeFromClass(Character.class);
  }

  @Test
  public void primitiveByteTypeIsSupported() {
    assertTypeFromClass(byte.class);
  }

  @Test
  public void primitiveShortTypeIsSupported() {
    assertTypeFromClass(short.class);
  }

  @Test
  public void primitiveLongTypeIsSupported() {
    assertTypeFromClass(long.class);
  }

  @Test
  public void primitiveFloatTypeIsSupported() {
    assertTypeFromClass(float.class);
  }

  @Test
  public void primitiveCharacterTypeIsSupported() {
    assertTypeFromClass(char.class);
  }

  @Test
  public void enumClassProducesEnumJavaType() {
    assertTypeFromClass(SampleEnum.class);
  }

  @Test
  public void namedUserTypeReferenceIsPreserved() {
    NamedUserType customType = new NamedUserType();
    customType.name.setValue("CustomType");
    customType.superType.setValue(JavaType.OBJECT_TYPE);

    assertSame(customType, new EmptyExpression(customType).getType());
  }

  @Test
  public void primitiveMultiDimensionalArraysAreSupported() {
    assertTypeFromClass(int[][].class);
  }

  @Test
  public void wrapperMultiDimensionalArraysAreSupported() {
    assertTypeFromClass(Double[][].class);
  }

  @Test
  public void nullAbstractTypeIsPreserved() {
    assertNull(new EmptyExpression((AbstractType<?, ?, ?>) null).getType());
  }

  @Test
  public void nullClassProducesNullType() {
    assertNull(new EmptyExpression((Class<?>) null).getType());
  }

  @Test
  public void processUsesFauxExpressionBehavior() {
    try {
      new EmptyExpression(String.class).process(null);
      fail();
    } catch (RuntimeException expected) {
    }
  }

  @Test
  public void emptyExpressionIsValidAndAssignableToFauxExpression() {
    EmptyExpression expression = new EmptyExpression(String.class);
    assertTrue(expression.isValid());
    assertTrue(expression instanceof FauxExpression);
  }

  @Test
  public void primitiveBooleanTypeIsSupported() {
    assertTypeFromClass(boolean.class);
  }

  @Test
  public void primitiveDoubleTypeIsSupported() {
    assertTypeFromClass(double.class);
  }

  @Test
  public void stringArrayTypeIsSupported() {
    assertTypeFromClass(String[].class);
  }

  @Test
  public void objectAbstractTypeReferenceIsPreserved() {
    assertSame(JavaType.OBJECT_TYPE, new EmptyExpression(JavaType.OBJECT_TYPE).getType());
  }

  @Test
  public void repeatedClassConstructionProducesSameJavaType() {
    assertSame(new EmptyExpression(String.class).getType(), new EmptyExpression(String.class).getType());
  }
  @Test
  public void objectArrayTypeIsSupported() {
    assertTypeFromClass(Object[][].class);
  }

  @Test
  public void emptyExpressionIsAlsoIdeExpression() {
    assertTrue(new EmptyExpression(String.class) instanceof IdeExpression);
  }
}
