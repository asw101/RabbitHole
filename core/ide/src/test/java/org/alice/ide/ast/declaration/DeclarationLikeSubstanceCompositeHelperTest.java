package org.alice.ide.ast.declaration;

import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NullLiteral;

import static org.junit.Assert.*;

public class DeclarationLikeSubstanceCompositeHelperTest {
  @Test
  public void resolveValueTypeCreatesArrayTypeWhenRequested() {
    assertEquals(JavaType.getInstance(String.class).getArrayType(),
        DeclarationLikeSubstanceCompositeHelper.resolveValueType(JavaType.getInstance(String.class), true));
    assertEquals(JavaType.getInstance(String.class),
        DeclarationLikeSubstanceCompositeHelper.resolveValueType(JavaType.getInstance(String.class), false));
  }

  @Test
  public void normalizeInitializerReplacesNullWithNullLiteral() {
    NullLiteral existing = new NullLiteral();

    assertSame(existing, DeclarationLikeSubstanceCompositeHelper.normalizeInitializer(existing));
    assertTrue(DeclarationLikeSubstanceCompositeHelper.normalizeInitializer(null) instanceof NullLiteral);
  }

  @Test
  public void isDisplayedMatchesApplicabilityThreshold() {
    assertTrue(DeclarationLikeSubstanceCompositeHelper.isDisplayed(DeclarationLikeSubstanceComposite.ApplicabilityStatus.EDITABLE));
    assertTrue(DeclarationLikeSubstanceCompositeHelper.isDisplayed(DeclarationLikeSubstanceComposite.ApplicabilityStatus.DISPLAYED));
    assertFalse(DeclarationLikeSubstanceCompositeHelper.isDisplayed(DeclarationLikeSubstanceComposite.ApplicabilityStatus.APPLICABLE_BUT_NOT_DISPLAYED));
  }
}
