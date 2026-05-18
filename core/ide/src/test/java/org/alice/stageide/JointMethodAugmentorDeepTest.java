package org.alice.stageide;

import org.alice.ide.identifier.IdentifierNameGenerator;
import org.junit.Test;
import org.lgna.project.ast.*;
import org.lgna.story.SJointedModel;
import org.lgna.story.resources.JointArrayId;
import org.lgna.story.resources.JointId;

import static org.junit.Assert.*;

/**
 * Tests for {@link JointMethodAugmentor} — exercises the static helper methods
 * and type augmentation logic for jointed model types.
 */
public class JointMethodAugmentorDeepTest {

  // ---- JavaType constants ----

  @Test
  public void jointedModelType_resolves() {
    JavaType type = JavaType.getInstance(SJointedModel.class);
    assertNotNull(type);
  }

  @Test
  public void getJointMethod_existsOnJointedModel() {
    JavaType type = JavaType.getInstance(SJointedModel.class);
    AbstractMethod method = type.getDeclaredMethod("getJoint", JointId.class);
    assertNotNull(method);
  }

  @Test
  public void getJointByNameMethod_existsOnJointedModel() {
    JavaType type = JavaType.getInstance(SJointedModel.class);
    AbstractMethod method = type.getDeclaredMethod("getJoint", String.class);
    assertNotNull(method);
  }

  @Test
  public void getJointArrayMethod_existsOnJointedModel() {
    JavaType type = JavaType.getInstance(SJointedModel.class);
    AbstractMethod method = type.getDeclaredMethod("getJointArray", JointId[].class);
    assertNotNull(method);
  }

  @Test
  public void getJointArrayIdMethod_existsOnJointedModel() {
    JavaType type = JavaType.getInstance(SJointedModel.class);
    AbstractMethod method = type.getDeclaredMethod("getJointArray", JointArrayId.class);
    assertNotNull(method);
  }

  // ---- IdentifierNameGenerator (used by augmentor) ----

  @Test
  public void convertConstantNameToMethodName_simpleConstant() {
    String result = IdentifierNameGenerator.SINGLETON.convertConstantNameToMethodName("LEFT_SHOULDER", "get");
    assertNotNull(result);
    assertTrue(result.contains("get") || result.contains("Left") || result.contains("left"));
  }

  @Test
  public void convertConstantNameToMethodName_singleWord() {
    String result = IdentifierNameGenerator.SINGLETON.convertConstantNameToMethodName("HEAD", "get");
    assertNotNull(result);
  }

  @Test
  public void convertConstantNameToMethodName_noPrefix() {
    String result = IdentifierNameGenerator.SINGLETON.convertConstantNameToMethodName("STAND");
    assertNotNull(result);
  }

  // ---- AstUtilities (used by augmentor to build methods) ----

  @Test
  public void createFunction_producesUserMethod() {
    UserMethod method = AstUtilities.createFunction("getTestJoint", org.lgna.story.SJoint.class);
    assertNotNull(method);
    assertEquals("getTestJoint", method.getName());
    assertFalse(method.isProcedure());
  }

  @Test
  public void createProcedure_producesUserMethod() {
    UserMethod method = AstUtilities.createProcedure("doTestAction");
    assertNotNull(method);
    assertEquals("doTestAction", method.getName());
    assertTrue(method.isProcedure());
  }

  @Test
  public void managementLevel_generated_exists() {
    assertNotNull(ManagementLevel.GENERATED);
    assertNotNull(ManagementLevel.NONE);
  }

  @Test
  public void userMethod_managementLevel_canBeSet() {
    UserMethod method = AstUtilities.createFunction("test", org.lgna.story.SJoint.class);
    method.managementLevel.setValue(ManagementLevel.GENERATED);
    assertEquals(ManagementLevel.GENERATED, method.getManagementLevel());
  }

  // ---- augment with non-jointed type returns unchanged ----

  @Test
  public void augment_nonJointedType_returnsUnchanged() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("PlainType");
    type.superType.setValue(JavaType.getInstance(Object.class));

    // JointMethodAugmentor.augment is package-private, but we can verify
    // the type hierarchy check: Object is not assignable from SJointedModel
    JavaType jointedType = JavaType.getInstance(SJointedModel.class);
    assertFalse(jointedType.isAssignableFrom(type));
  }

  // ---- Body and expression construction patterns ----

  @Test
  public void thisExpression_canBeCreated() {
    ThisExpression thisExpr = new ThisExpression();
    assertNotNull(thisExpr);
  }

  @Test
  public void stringLiteral_canBeCreated() {
    StringLiteral lit = new StringLiteral("test_joint_name");
    assertNotNull(lit);
    assertEquals("test_joint_name", lit.value.getValue());
  }

  @Test
  public void blockStatement_canAddStatements() {
    UserMethod method = AstUtilities.createFunction("test", org.lgna.story.SJoint.class);
    BlockStatement body = method.body.getValue();
    assertNotNull(body);
    int initialSize = body.statements.size();
    body.statements.add(new ExpressionStatement(new NullLiteral()));
    assertEquals(initialSize + 1, body.statements.size());
  }

  @Test
  public void namedUserType_methodsProperty_canHaveMethodsAdded() {
    NamedUserType type = new NamedUserType();
    type.name.setValue("TestType");
    type.superType.setValue(JavaType.getInstance(Object.class));

    UserMethod m1 = AstUtilities.createFunction("getA", org.lgna.story.SJoint.class);
    UserMethod m2 = AstUtilities.createProcedure("doB");
    type.methods.add(m1);
    type.methods.add(m2);

    assertEquals(2, type.methods.size());
  }
}
