package org.alice.ide.cascade;

import org.junit.Test;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.StringLiteral;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class CascadeBuilderBehaviorTest {
  private final CascadeBuilder builder = new CascadeBuilder();

  @Test
  public void normalizeTypeAndTypeResolutionFollowTheCurrentObjectFallbackRules() {
    CascadeBuilder.TypeResolution rootResolution = builder.getTypeResolution(JavaType.OBJECT_TYPE, true, new StringLiteral("hello"));
    CascadeBuilder.TypeResolution nonRootResolution = builder.getTypeResolution(JavaType.OBJECT_TYPE, false, null);

    assertSame(JavaType.DOUBLE_OBJECT_TYPE, builder.normalizeType(JavaType.getInstance(Number.class)));
    assertSame(JavaType.STRING_TYPE, rootResolution.getResolvedType());
    assertTrue(rootResolution.isOtherTypeMenuDesired());
    assertSame(JavaType.STRING_TYPE, nonRootResolution.getResolvedType());
    assertTrue(nonRootResolution.isOtherTypeMenuDesired());
  }

  @Test
  public void isPreviousExpressionApplicableRequiresRootAndCompatibleTypes() {
    StringLiteral previousExpression = new StringLiteral("value");

    assertTrue(builder.isPreviousExpressionApplicable(true, JavaType.OBJECT_TYPE, previousExpression));
    assertFalse(builder.isPreviousExpressionApplicable(false, JavaType.OBJECT_TYPE, previousExpression));
    assertFalse(builder.isPreviousExpressionApplicable(true, JavaType.INTEGER_OBJECT_TYPE, previousExpression));
  }

  @Test
  public void integerArrayLengthAndStringConcatenationOptionsDependOnCurrentContext() {
    assertTrue(builder.shouldCreateArrayLengthFillIns(true, JavaType.INTEGER_OBJECT_TYPE, null));
    assertTrue(builder.shouldCreateArrayLengthFillIns(true, JavaType.OBJECT_TYPE, new IntegerLiteral(1)));
    assertFalse(builder.shouldCreateArrayLengthFillIns(true, JavaType.OBJECT_TYPE, null));

    assertTrue(builder.shouldShowStringConcatenationOptions(true, new StringLiteral("alpha")));
    assertTrue(builder.shouldShowStringConcatenationRightOperandOnly(true, new StringLiteral("alpha")));
    assertFalse(builder.shouldShowStringConcatenationOptions(false, new StringLiteral("alpha")));
    assertFalse(builder.shouldShowStringConcatenationRightOperandOnly(true, new NullLiteral()));
    assertFalse(builder.shouldShowStringConcatenationRightOperandOnly(true, new IntegerLiteral(7)));
  }
}
