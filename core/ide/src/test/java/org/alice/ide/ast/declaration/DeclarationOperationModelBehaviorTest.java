package org.alice.ide.ast.declaration;

import org.junit.Test;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.DoubleLiteral;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.FieldModifierFinalVolatileOrNeither;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.ManagementLevel;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.StringLiteral;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;
import org.lgna.project.ast.BlockStatement;
import org.lgna.story.SScene;

import static org.junit.Assert.*;

public class DeclarationOperationModelBehaviorTest {
  private NamedUserType createDeclaringType(String name) {
    return AstUtilities.createType(name, JavaType.getInstance(SScene.class));
  }

  @Test
  public void addProcedureCompositeCachesByDeclaringType() {
    NamedUserType declaringType = createDeclaringType("ProcedureScene");

    assertSame(AddProcedureComposite.getInstance(declaringType), AddProcedureComposite.getInstance(declaringType));
  }

  @Test
  public void addProcedurePreviewUsesConfiguredNameAndVoidReturnType() {
    AddProcedureComposite composite = AddProcedureComposite.getInstance(createDeclaringType("ProcedurePreviewScene"));
    composite.getNameState().setValueTransactionlessly("spinAround");

    UserMethod preview = composite.getPreviewValue();

    assertEquals("spinAround", preview.getName());
    assertSame(JavaType.VOID_TYPE, preview.getReturnType());
    assertTrue(preview.isProcedure());
  }

  @Test
  public void addProcedureModifyNameReplacesDeclaringTypePlaceholder() {
    NamedUserType declaringType = createDeclaringType("ProcedureOwner");
    AddProcedureComposite composite = AddProcedureComposite.getInstance(declaringType);

    assertEquals("Add ProcedureOwner member", composite.modifyNameIfNecessary("Add </declaringType/> member"));
  }

  @Test
  public void addFunctionCompositeCachesByDeclaringType() {
    NamedUserType declaringType = createDeclaringType("FunctionScene");

    assertSame(AddFunctionComposite.getInstance(declaringType), AddFunctionComposite.getInstance(declaringType));
  }

  @Test
  public void addFunctionPreviewBuildsArrayReturnTypeFromStates() {
    AddFunctionComposite composite = AddFunctionComposite.getInstance(createDeclaringType("FunctionPreviewScene"));
    composite.getValueComponentTypeState().setValueTransactionlessly(JavaType.STRING_TYPE);
    composite.getValueIsArrayTypeState().setValueTransactionlessly(true);
    composite.getNameState().setValueTransactionlessly("labels");

    UserMethod preview = composite.getPreviewValue();

    assertEquals("labels", preview.getName());
    assertTrue(preview.getReturnType().isArray());
    assertSame(JavaType.STRING_TYPE, preview.getReturnType().getComponentType());
    assertFalse(preview.isProcedure());
  }

  @Test
  public void addUnmanagedFieldPreviewBuildsPrivateFieldFromStates() {
    AddUnmanagedFieldComposite composite = AddUnmanagedFieldComposite.getInstance(createDeclaringType("FieldPreviewScene"));
    Expression initializer = new StringLiteral("hello");
    composite.getIsFinalState().setValueTransactionlessly(true);
    composite.getValueComponentTypeState().setValueTransactionlessly(JavaType.STRING_TYPE);
    composite.getValueIsArrayTypeState().setValueTransactionlessly(false);
    composite.getNameState().setValueTransactionlessly("greeting");
    composite.getInitializerState().setValueTransactionlessly(initializer);

    UserField preview = composite.getPreviewValue();

    assertEquals("greeting", preview.getName());
    assertSame(JavaType.STRING_TYPE, preview.getValueType());
    assertSame(initializer, preview.initializer.getValue());
    assertEquals(ManagementLevel.NONE, preview.managementLevel.getValue());
    assertTrue(preview.isFinal());
    assertEquals(FieldModifierFinalVolatileOrNeither.FINAL, preview.finalVolatileOrNeither.getValue());
  }

  @Test
  public void addUnmanagedFieldModifyNameReplacesDeclaringTypePlaceholder() {
    NamedUserType declaringType = createDeclaringType("FieldOwner");
    AddUnmanagedFieldComposite composite = AddUnmanagedFieldComposite.getInstance(declaringType);

    assertEquals("Create FieldOwner field", composite.modifyNameIfNecessary("Create </declaringType/> field"));
  }

  @Test
  public void managedEditFieldInitialValuesMirrorSourceField() {
    UserField source = new UserField("names", JavaType.STRING_TYPE.getArrayType(), new NullLiteral());
    source.finalVolatileOrNeither.setValue(FieldModifierFinalVolatileOrNeither.FINAL);

    ManagedEditFieldComposite composite = new ManagedEditFieldComposite(source);

    assertTrue(composite.getIsFinalInitialValue());
    assertSame(JavaType.STRING_TYPE, composite.getValueComponentTypeInitialValue());
    assertTrue(composite.getValueIsArrayTypeInitialValue());
    assertEquals("names", composite.getNameInitialValue());
    assertSame(source.initializer.getValue(), composite.getInitializerInitialValue());
    assertTrue(composite.isInitializerDisplayed());
    assertFalse(composite.isInitializerEditable());
  }

  @Test
  public void unmanagedEditFieldLeavesInitializerEditable() {
    UserField source = new UserField("score", JavaType.INTEGER_OBJECT_TYPE, new NullLiteral());

    UnmanagedEditFieldComposite composite = new UnmanagedEditFieldComposite(source);

    assertTrue(composite.isInitializerDisplayed());
    assertTrue(composite.isInitializerEditable());
  }

  @Test
  public void managedEditFieldPreviewUsesEditedStateWithoutMutatingSourceField() {
    UserField source = new UserField("coins", JavaType.INTEGER_OBJECT_TYPE, new NullLiteral());
    source.finalVolatileOrNeither.setValue(FieldModifierFinalVolatileOrNeither.FINAL);
    ManagedEditFieldComposite composite = new ManagedEditFieldComposite(source);
    composite.getIsFinalState().setValueTransactionlessly(false);
    composite.getValueComponentTypeState().setValueTransactionlessly(JavaType.DOUBLE_OBJECT_TYPE);
    composite.getValueIsArrayTypeState().setValueTransactionlessly(false);
    composite.getNameState().setValueTransactionlessly("points");
    composite.getInitializerState().setValueTransactionlessly(new DoubleLiteral(1.5));

    UserField preview = composite.getPreviewValue();

    assertEquals("points", preview.getName());
    assertSame(JavaType.DOUBLE_OBJECT_TYPE, preview.getValueType());
    assertEquals(FieldModifierFinalVolatileOrNeither.NEITHER, preview.finalVolatileOrNeither.getValue());
    assertEquals("coins", source.getName());
    assertSame(JavaType.INTEGER_OBJECT_TYPE, source.getValueType());
    assertEquals(FieldModifierFinalVolatileOrNeither.FINAL, source.finalVolatileOrNeither.getValue());
  }

  @Test
  public void editFieldPreviewReusesSinglePreviewInstanceAcrossUpdates() {
    UserField source = new UserField("value", JavaType.STRING_TYPE, new NullLiteral());
    UnmanagedEditFieldComposite composite = new UnmanagedEditFieldComposite(source);
    composite.getNameState().setValueTransactionlessly("first");
    UserField firstPreview = composite.getPreviewValue();
    composite.getNameState().setValueTransactionlessly("second");
    UserField secondPreview = composite.getPreviewValue();

    assertSame(firstPreview, secondPreview);
    assertEquals("second", secondPreview.getName());
  }
}
