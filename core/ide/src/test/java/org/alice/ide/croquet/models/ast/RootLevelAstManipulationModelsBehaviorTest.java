package org.alice.ide.croquet.models.ast;

import org.junit.Test;
import org.lgna.croquet.Application;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.NodeListProperty;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;
import org.lgna.project.ast.UserType;
import org.lgna.project.ast.UserLocal;
import org.lgna.project.ast.AbstractMember;
import org.lgna.story.SScene;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class RootLevelAstManipulationModelsBehaviorTest {
  private static UserField field(String name) {
    return new UserField(name, String.class, new NullLiteral());
  }

  private static UserMethod method(String name) {
    return new UserMethod(name, JavaType.VOID_TYPE, new UserParameter[0], new BlockStatement());
  }

  private static UserLocal local(String name) {
    return new UserLocal(name, JavaType.getInstance(String.class), false);
  }

  private static UserParameter parameter(String name) {
    return new UserParameter(name, JavaType.getInstance(String.class));
  }

  private static NamedUserType declaringType(String name) {
    return AstUtilities.createType(name, JavaType.getInstance(SScene.class));
  }

  @Test
  public void centerCameraOnOperation_usesProjectGroupAndStableMigrationId() {
    CenterCameraOnOperation operation = new CenterCameraOnOperation();

    assertSame(Application.PROJECT_GROUP, operation.getGroup());
    assertEquals(UUID.fromString("84f45a6e-e17f-4d64-b4a1-8182fdc1a546"), operation.getMigrationId());
  }

  @Test
  public void revertFieldOperation_cachesByFieldAndExposesField() {
    UserField score = field("score");

    RevertFieldOperation first = RevertFieldOperation.getInstance(score);
    RevertFieldOperation second = RevertFieldOperation.getInstance(score);

    assertSame(first, second);
    assertSame(score, first.getField());
    assertSame(Application.PROJECT_GROUP, first.getGroup());
    assertEquals(UUID.fromString("84645a6e-e17f-4d64-b4a1-8182fdc1a546"), first.getMigrationId());
  }

  @Test
  public void localMenuModel_cachesByLocalAndShowsScrollArrows() {
    UserLocal local = local("counter");

    LocalMenuModel first = LocalMenuModel.getInstance(local);
    LocalMenuModel second = LocalMenuModel.getInstance(local);

    assertSame(first, second);
    assertTrue(first.showScrollArrows());
  }

  @Test
  public void parameterAccessMenuModel_cachesByParameterAndShowsScrollArrows() {
    UserParameter parameter = parameter("count");

    ParameterAccessMenuModel first = ParameterAccessMenuModel.getInstance(parameter);
    ParameterAccessMenuModel second = ParameterAccessMenuModel.getInstance(parameter);

    assertSame(first, second);
    assertTrue(first.showScrollArrows());
  }

  @Test
  public void methodHeaderMenuModel_cachesByMethodAndShowsScrollArrows() {
    UserMethod method = method("step");

    MethodHeaderMenuModel first = MethodHeaderMenuModel.getInstance(method);
    MethodHeaderMenuModel second = MethodHeaderMenuModel.getInstance(method);

    assertSame(first, second);
    assertTrue(first.showScrollArrows());
  }

  @Test
  public void deleteMemberOperation_exposesConstructorStateAndDescription() {
    NamedUserType declaringType = declaringType("DeleteMemberScene");
    UserField score = field("score");
    declaringType.fields.add(score);
    TestDeleteMemberOperation operation = new TestDeleteMemberOperation(score, declaringType);

    StringBuilder description = new StringBuilder();
    operation.appendDescription(description, true);

    assertSame(score, operation.getMember());
    assertSame(declaringType, operation.getDeclaringType());
    assertSame(UserField.class, operation.getNodeParameterType());
    assertSame(declaringType.fields, operation.getNodeListProperty(declaringType));
    assertTrue(description.toString().contains("delete:"));
    assertTrue(description.toString().contains("score"));
  }

  @Test
  public void deleteFieldAndDeleteMethodOperations_exposeStableMigrationIds() {
    NamedUserType declaringType = declaringType("DeleteOperationScene");
    UserField score = field("score");
    UserMethod step = method("step");
    declaringType.fields.add(score);
    declaringType.methods.add(step);

    assertEquals(UUID.fromString("29e5416c-c0c4-4b6d-9146-5461d5c73c42"), DeleteFieldOperation.getInstance(score, declaringType).getMigrationId());
    assertEquals(UUID.fromString("ed56c9b9-3eed-48d0-9bbc-f6e251fdd3b5"), DeleteMethodOperation.getInstance(step, declaringType).getMigrationId());
  }

  private static final class TestDeleteMemberOperation extends DeleteMemberOperation<UserField> {
    private TestDeleteMemberOperation(UserField member, UserType<?> declaringType) {
      super(UUID.fromString("00000000-0000-0000-0000-000000000053"), member, declaringType);
    }

    @Override
    public Class<UserField> getNodeParameterType() {
      return UserField.class;
    }

    @Override
    protected NodeListProperty<UserField> getNodeListProperty(UserType<?> declaringType) {
      return declaringType.fields;
    }

    @Override
    protected boolean isClearToDelete(UserField node, org.lgna.croquet.history.UserActivity activity) {
      return true;
    }
  }
}
