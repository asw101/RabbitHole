package org.alice.ide.ast.declaration;

import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class DeclarationLikeSubstanceCompositeApplicabilityTest {
  @Test
  public void applicabilityStatusReportsProgressiveCapabilities() {
    assertTrue(DeclarationLikeSubstanceComposite.ApplicabilityStatus.EDITABLE.isEditable());
    assertTrue(DeclarationLikeSubstanceComposite.ApplicabilityStatus.DISPLAYED.isDisplayed());
    assertFalse(DeclarationLikeSubstanceComposite.ApplicabilityStatus.DISPLAYED.isEditable());
    assertTrue(DeclarationLikeSubstanceComposite.ApplicabilityStatus.APPLICABLE_BUT_NOT_DISPLAYED.isApplicable());
    assertFalse(DeclarationLikeSubstanceComposite.ApplicabilityStatus.NOT_APPLICABLE.isApplicable());
  }

  @Test
  public void detailsBuilderStoresAssignedValues() throws Exception {
    DeclarationLikeSubstanceComposite.Details details = new DeclarationLikeSubstanceComposite.Details()
        .isFinal(DeclarationLikeSubstanceComposite.ApplicabilityStatus.EDITABLE, true)
        .name(DeclarationLikeSubstanceComposite.ApplicabilityStatus.DISPLAYED, "ship")
        .valueIsArrayType(DeclarationLikeSubstanceComposite.ApplicabilityStatus.APPLICABLE_BUT_NOT_DISPLAYED, false);

    Field isFinalStatus = DeclarationLikeSubstanceComposite.Details.class.getDeclaredField("isFinalStatus");
    isFinalStatus.setAccessible(true);
    Field nameInitialValue = DeclarationLikeSubstanceComposite.Details.class.getDeclaredField("nameInitialValue");
    nameInitialValue.setAccessible(true);
    Field valueIsArrayTypeStatus = DeclarationLikeSubstanceComposite.Details.class.getDeclaredField("valueIsArrayTypeStatus");
    valueIsArrayTypeStatus.setAccessible(true);

    assertSame(DeclarationLikeSubstanceComposite.ApplicabilityStatus.EDITABLE, isFinalStatus.get(details));
    assertEquals("ship", nameInitialValue.get(details));
    assertSame(DeclarationLikeSubstanceComposite.ApplicabilityStatus.APPLICABLE_BUT_NOT_DISPLAYED, valueIsArrayTypeStatus.get(details));
  }
}
