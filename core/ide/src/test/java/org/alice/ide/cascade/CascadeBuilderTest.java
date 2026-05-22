package org.alice.ide.cascade;

import org.junit.Test;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.StringLiteral;

import static org.junit.Assert.*;

public class CascadeBuilderTest {
  private final CascadeBuilder builder = new CascadeBuilder();

  @Test
  public void normalizeType_numberUsesDouble() {
    assertSame(JavaType.DOUBLE_OBJECT_TYPE, builder.normalizeType(JavaType.getInstance(Number.class)));
  }

  @Test
  public void getTypeResolution_objectAtRootWithPreviousExpressionUsesPreviousType() {
    CascadeBuilder.TypeResolution resolution = builder.getTypeResolution(
        JavaType.OBJECT_TYPE, true, new StringLiteral("hello"));

    assertSame(JavaType.STRING_TYPE, resolution.getResolvedType());
    assertTrue(resolution.isOtherTypeMenuDesired());
  }

  @Test
  public void getTypeResolution_objectWithoutPreviousExpressionFallsBackToString() {
    CascadeBuilder.TypeResolution resolution = builder.getTypeResolution(
        JavaType.OBJECT_TYPE, true, null);

    assertSame(JavaType.STRING_TYPE, resolution.getResolvedType());
    assertTrue(resolution.isOtherTypeMenuDesired());
  }

  @Test
  public void isPreviousExpressionApplicableRequiresRootAndCompatibleType() {
    Expression previousExpression = new StringLiteral("value");

    assertTrue(builder.isPreviousExpressionApplicable(true, JavaType.OBJECT_TYPE, previousExpression));
    assertFalse(builder.isPreviousExpressionApplicable(false, JavaType.OBJECT_TYPE, previousExpression));
    assertFalse(builder.isPreviousExpressionApplicable(true, JavaType.INTEGER_OBJECT_TYPE, previousExpression));
  }

  @Test
  public void shouldCreateArrayLengthFillInsMatchesCurrentIntegerRules() {
    assertTrue(builder.shouldCreateArrayLengthFillIns(true, JavaType.INTEGER_OBJECT_TYPE, null));
    assertTrue(builder.shouldCreateArrayLengthFillIns(true, JavaType.OBJECT_TYPE, new IntegerLiteral(1)));
    assertFalse(builder.shouldCreateArrayLengthFillIns(true, JavaType.OBJECT_TYPE, null));
    assertFalse(builder.shouldCreateArrayLengthFillIns(false, JavaType.INTEGER_OBJECT_TYPE, new IntegerLiteral(1)));
  }

  @Test
  public void stringConcatenationFlagsFollowPreviousExpressionShape() {
    assertTrue(builder.shouldShowStringConcatenationOptions(true, new StringLiteral("alpha")));
    assertTrue(builder.shouldShowStringConcatenationRightOperandOnly(true, new StringLiteral("alpha")));
    assertFalse(builder.shouldShowStringConcatenationOptions(false, new StringLiteral("alpha")));
    assertFalse(builder.shouldShowStringConcatenationRightOperandOnly(true, new NullLiteral()));
    assertFalse(builder.shouldShowStringConcatenationRightOperandOnly(true, new IntegerLiteral(7)));
  }
}
