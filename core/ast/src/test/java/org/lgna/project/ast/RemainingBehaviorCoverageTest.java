package org.lgna.project.ast;

import org.junit.Test;

import static org.junit.Assert.*;

public class RemainingBehaviorCoverageTest {
  @Test
  public void parameterAccessUsesOwningCodeForValidation() {
    UserParameter parameter = new UserParameter("value", Integer.class);
    ParameterAccess access = new ParameterAccess(parameter);
    UserMethod method = new UserMethod(
        "identity",
        Integer.class,
        new UserParameter[] {parameter},
        new BlockStatement(new ReturnStatement(JavaType.getInstance(Integer.class), access)));

    assertSame(JavaType.getInstance(Integer.class), access.getType());
    assertTrue(access.isValid());
    assertSame(method, parameter.getCode());
  }

  @Test
  public void parameterAccessRejectsDifferentOwningCode() {
    UserParameter parameter = new UserParameter("value", Integer.class);
    UserMethod owner = new UserMethod("owner", Integer.class, new UserParameter[] {parameter}, new BlockStatement());
    ParameterAccess access = new ParameterAccess(parameter);
    UserMethod differentMethod = new UserMethod(
        "different",
        Integer.class,
        new UserParameter[0],
        new BlockStatement(new ReturnStatement(JavaType.getInstance(Integer.class), access)));

    assertFalse(access.isValid());
    assertSame(owner, parameter.getCode());
    assertNotSame(owner, differentMethod);
  }

  @Test
  public void arrayAccessUsesComponentTypeAndChecksArrayExpressions() {
    UserLocal items = new UserLocal("items", JavaType.getInstance(String[].class), true);
    ArrayAccess valid = new ArrayAccess(String[].class, new LocalAccess(items), new IntegerLiteral(0));
    ArrayAccess invalidType = new ArrayAccess(String[].class, new IntegerLiteral(1), new IntegerLiteral(0));
    ArrayAccess missingArray = new ArrayAccess();

    assertSame(JavaType.getInstance(String.class), valid.getType());
    assertTrue(valid.isValid());
    assertFalse(invalidType.isValid());
    assertFalse(missingArray.isValid());
    assertSame(JavaType.getInstance(Integer.class), valid.index.getExpressionType());
  }

  @Test
  public void relationalOperatorsHandleNumericPromotionAndReferenceComparison() {
    assertTrue(RelationalInfixExpression.Operator.LESS.operate((byte) 1, (byte) 2));
    assertTrue(RelationalInfixExpression.Operator.LESS_EQUALS.operate((short) 2, (short) 2));
    assertTrue(RelationalInfixExpression.Operator.GREATER.operate(3, 2));
    assertTrue(RelationalInfixExpression.Operator.GREATER_EQUALS.operate(4L, 4));
    assertTrue(RelationalInfixExpression.Operator.EQUALS.operate(5.0f, 5));
    assertTrue(RelationalInfixExpression.Operator.NOT_EQUALS.operate(6.0d, 7L));

    Object shared = new Object();
    assertTrue(RelationalInfixExpression.Operator.EQUALS.operate(shared, shared));
    assertTrue(RelationalInfixExpression.Operator.NOT_EQUALS.operate(shared, new Object()));

    try {
      RelationalInfixExpression.Operator.LESS.operate("a", "b");
      fail("Expected non-numeric comparison to fail");
    } catch (RuntimeException expected) {
      // expected
    }
  }

  @Test
  public void instanceCreationInstantiatesFromStringLiteralArgumentsOnly() {
    InstanceCreation valid = AstUtilities.createInstanceCreation(
        StringBuilder.class,
        new Class<?>[] {String.class},
        new StringLiteral("hello"));
    InstanceCreation invalid = AstUtilities.createInstanceCreation(
        StringBuilder.class,
        new Class<?>[] {String.class},
        new IntegerLiteral(7));

    Object instance = valid.instantiateDynamicResource();
    assertTrue(instance instanceof StringBuilder);
    assertEquals("hello", instance.toString());
    assertNull(invalid.instantiateDynamicResource());
  }

  @Test
  public void userFieldBuildsManagedDefaultsAndArrayAccessors() {
    UserField managed = new UserField("name", String.class);
    UserField arrayField = new UserField("items", JavaType.getInstance(String[].class), new NullLiteral());

    assertEquals(ManagementLevel.MANAGED, managed.getManagementLevel());
    assertEquals(AccessLevel.PRIVATE, managed.getAccessLevel());
    assertTrue(managed.isFinal());
    assertTrue(managed.initializer.getValue() instanceof InstanceCreation);
    assertNull(managed.getSetter());
    assertTrue(managed.getSetters().isEmpty());

    assertEquals(2, arrayField.getGetters().size());
    assertEquals(2, arrayField.getSetters().size());
    assertNotNull(arrayField.getArrayItemGetter());
    assertNotNull(arrayField.getArrayItemSetter());
  }
}
