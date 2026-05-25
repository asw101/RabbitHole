package org.alice.ide.croquet.models.ast;

import org.junit.Test;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;
import org.lgna.story.SScene;

import static org.junit.Assert.*;

public class DeleteOperationBehaviorTest {
  private NamedUserType createDeclaringType(String name) {
    return AstUtilities.createType(name, JavaType.getInstance(SScene.class));
  }

  @Test
  public void deleteFieldOperationCachesByFieldAcrossLookupOverloads() {
    NamedUserType declaringType = createDeclaringType("DeleteFieldScene");
    UserField field = new UserField("score", JavaType.INTEGER_OBJECT_TYPE, new NullLiteral());
    declaringType.fields.add(field);

    DeleteFieldOperation direct = DeleteFieldOperation.getInstance(field);
    DeleteFieldOperation explicit = DeleteFieldOperation.getInstance(field, declaringType);

    assertSame(direct, explicit);
  }

  @Test
  public void deleteFieldOperationUsesFieldMetadataAndDeletionFlag() {
    NamedUserType declaringType = createDeclaringType("DeleteFieldMetadataScene");
    UserField field = new UserField("score", JavaType.INTEGER_OBJECT_TYPE, new NullLiteral());
    field.isDeletionAllowed.setValue(false);
    declaringType.fields.add(field);

    DeleteFieldOperation operation = DeleteFieldOperation.getInstance(field, declaringType);

    assertSame(UserField.class, operation.getNodeParameterType());
    assertSame(declaringType.fields, operation.getNodeListProperty(declaringType));
    assertFalse(operation.isEnabled());
  }

  @Test
  public void deleteMethodOperationCachesByMethodAcrossLookupOverloads() {
    NamedUserType declaringType = createDeclaringType("DeleteMethodScene");
    UserMethod method = new UserMethod("storyAction", JavaType.VOID_TYPE, new UserParameter[0], new BlockStatement());
    declaringType.methods.add(method);

    DeleteMethodOperation direct = DeleteMethodOperation.getInstance(method);
    DeleteMethodOperation explicit = DeleteMethodOperation.getInstance(method, declaringType);

    assertSame(direct, explicit);
  }

  @Test
  public void deleteMethodOperationUsesMethodMetadata() {
    NamedUserType declaringType = createDeclaringType("DeleteMethodMetadataScene");
    UserMethod method = new UserMethod("storyAction", JavaType.VOID_TYPE, new UserParameter[0], new BlockStatement());
    declaringType.methods.add(method);

    DeleteMethodOperation operation = DeleteMethodOperation.getInstance(method, declaringType);

    assertSame(UserMethod.class, operation.getNodeParameterType());
    assertSame(declaringType.methods, operation.getNodeListProperty(declaringType));
    assertTrue(operation.isEnabled());
  }
}
