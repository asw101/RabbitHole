package org.alice.ide.ast.declaration;

import org.junit.Test;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NullLiteral;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class AddFieldCompositeFieldDetailsBuilderTest {
  @Test
  public void buildCarriesConfiguredFieldMetadataIntoDetails() throws Exception {
    Expression initializer = new NullLiteral();
    DeclarationLikeSubstanceComposite.Details details = new AddFieldComposite.FieldDetailsBuilder()
        .isFinal(DeclarationLikeSubstanceComposite.ApplicabilityStatus.EDITABLE, true)
        .valueComponentType(DeclarationLikeSubstanceComposite.ApplicabilityStatus.DISPLAYED, JavaType.getInstance(String.class))
        .valueIsArrayType(DeclarationLikeSubstanceComposite.ApplicabilityStatus.APPLICABLE_BUT_NOT_DISPLAYED, true)
        .initializer(DeclarationLikeSubstanceComposite.ApplicabilityStatus.EDITABLE, initializer)
        .build();

    assertSame(DeclarationLikeSubstanceComposite.ApplicabilityStatus.EDITABLE, get(details, "isFinalStatus"));
    assertEquals(true, get(details, "inFinalInitialValue"));
    assertSame(DeclarationLikeSubstanceComposite.ApplicabilityStatus.DISPLAYED, get(details, "valueComponentTypeStatus"));
    assertSame(JavaType.getInstance(String.class), get(details, "valueComponentTypeInitialValue"));
    assertSame(DeclarationLikeSubstanceComposite.ApplicabilityStatus.APPLICABLE_BUT_NOT_DISPLAYED, get(details, "valueIsArrayTypeStatus"));
    assertEquals(true, get(details, "valueIsArrayTypeInitialValue"));
    assertSame(DeclarationLikeSubstanceComposite.ApplicabilityStatus.EDITABLE, get(details, "initializerStatus"));
    assertSame(initializer, get(details, "initializerInitialValue"));
    assertSame(DeclarationLikeSubstanceComposite.ApplicabilityStatus.EDITABLE, get(details, "nameStatus"));
    assertEquals("", get(details, "nameInitialValue"));
  }

  @Test
  public void buildDefaultsFinalApplicabilityToNotApplicableWhenUnset() throws Exception {
    DeclarationLikeSubstanceComposite.Details details = new AddFieldComposite.FieldDetailsBuilder()
        .valueComponentType(DeclarationLikeSubstanceComposite.ApplicabilityStatus.EDITABLE, JavaType.getInstance(Integer.class))
        .valueIsArrayType(DeclarationLikeSubstanceComposite.ApplicabilityStatus.EDITABLE, false)
        .initializer(DeclarationLikeSubstanceComposite.ApplicabilityStatus.DISPLAYED, null)
        .build();

    assertSame(DeclarationLikeSubstanceComposite.ApplicabilityStatus.NOT_APPLICABLE, get(details, "isFinalStatus"));
    assertEquals(false, get(details, "inFinalInitialValue"));
  }

  @Test
  public void builderMethodsAreChainable() {
    AddFieldComposite.FieldDetailsBuilder builder = new AddFieldComposite.FieldDetailsBuilder();

    assertSame(builder, builder.isFinal(DeclarationLikeSubstanceComposite.ApplicabilityStatus.EDITABLE, true));
    assertSame(builder, builder.valueComponentType(DeclarationLikeSubstanceComposite.ApplicabilityStatus.EDITABLE, JavaType.getInstance(Double.class)));
    assertSame(builder, builder.valueIsArrayType(DeclarationLikeSubstanceComposite.ApplicabilityStatus.EDITABLE, false));
    assertSame(builder, builder.initializer(DeclarationLikeSubstanceComposite.ApplicabilityStatus.DISPLAYED, new NullLiteral()));
  }

  private static Object get(Object target, String fieldName) throws Exception {
    Field field = DeclarationLikeSubstanceComposite.Details.class.getDeclaredField(fieldName);
    field.setAccessible(true);
    return field.get(target);
  }
}
