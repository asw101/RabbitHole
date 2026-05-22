package org.alice.stageide.sceneeditor;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

/**
 * Comprehensive tests for {@link SetUpMethodGenerator} static factory methods.
 */
public class SetUpMethodGeneratorBranchCoverageTest {

  private static UserField createField(String name) {
    UserField field = new UserField();
    field.name.setValue(name);
    field.valueType.setValue(JavaType.getInstance(org.lgna.story.SModel.class));
    return field;
  }

  @Test
  public void createSetVehicleNullStatement_notNull() {
    UserField rider = createField("rider");
    ExpressionStatement stmt = SetUpMethodGenerator.createSetVehicleNullStatement(rider);
    assertNotNull(stmt);
  }

  @Test
  public void createSetVehicleNullStatement_isExpressionStatement() {
    UserField rider = createField("rider");
    ExpressionStatement stmt = SetUpMethodGenerator.createSetVehicleNullStatement(rider);
    assertNotNull(stmt.expression.getValue());
    assertTrue(stmt.expression.getValue() instanceof MethodInvocation);
  }

  @Test
  public void createSetVehicleNullStatement_hasNullArg() {
    UserField rider = createField("riderField");
    ExpressionStatement stmt = SetUpMethodGenerator.createSetVehicleNullStatement(rider);
    MethodInvocation inv = (MethodInvocation) stmt.expression.getValue();
    SimpleArgument arg = inv.requiredArguments.get(0);
    assertTrue(arg.expression.getValue() instanceof NullLiteral);
  }

  @Test
  public void createSetVehicleSceneStatement_notNull() {
    UserField rider = createField("rider");
    ExpressionStatement stmt = SetUpMethodGenerator.createSetVehicleSceneStatement(rider);
    assertNotNull(stmt);
  }

  @Test
  public void createSetVehicleSceneStatement_isMethodInvocation() {
    UserField rider = createField("rider");
    ExpressionStatement stmt = SetUpMethodGenerator.createSetVehicleSceneStatement(rider);
    assertTrue(stmt.expression.getValue() instanceof MethodInvocation);
  }

  @Test
  public void createSetVehicleFieldStatement_notNull() {
    UserField rider = createField("rider");
    UserField vehicle = createField("car");
    ExpressionStatement stmt = SetUpMethodGenerator.createSetVehicleFieldStatement(rider, vehicle);
    assertNotNull(stmt);
  }

  @Test
  public void createSetVehicleFieldStatement_isMethodInvocation() {
    UserField rider = createField("rider");
    UserField vehicle = createField("vehicle");
    ExpressionStatement stmt = SetUpMethodGenerator.createSetVehicleFieldStatement(rider, vehicle);
    assertTrue(stmt.expression.getValue() instanceof MethodInvocation);
  }

  @Test
  public void createSetterInvocation_notNull() {
    UserField field = createField("model");
    AbstractMethod setter = AstMethodLookupHelpers.lookupMethod(
        org.lgna.story.MutableRider.class, "setVehicle", (Class<?>) org.lgna.story.SThing.class);
    assertNotNull("Should find setVehicle method", setter);
    MethodInvocation inv = SetUpMethodGenerator.createSetterInvocation(false, field, setter, new NullLiteral());
    assertNotNull(inv);
  }

  @Test
  public void createSetterInvocation_fieldAccess_notThis() {
    UserField field = createField("model");
    AbstractMethod setter = AstMethodLookupHelpers.lookupMethod(
        org.lgna.story.MutableRider.class, "setVehicle", (Class<?>) org.lgna.story.SThing.class);
    assertNotNull(setter);
    MethodInvocation inv = SetUpMethodGenerator.createSetterInvocation(false, field, setter, new NullLiteral());
    assertTrue(inv.expression.getValue() instanceof FieldAccess);
  }

  @Test
  public void createSetterInvocation_thisExpression() {
    UserField field = createField("model");
    AbstractMethod setter = AstMethodLookupHelpers.lookupMethod(
        org.lgna.story.MutableRider.class, "setVehicle", (Class<?>) org.lgna.story.SThing.class);
    assertNotNull(setter);
    MethodInvocation inv = SetUpMethodGenerator.createSetterInvocation(true, field, setter, new NullLiteral());
    assertTrue(inv.expression.getValue() instanceof ThisExpression);
  }

  @Test
  public void createSetterStatement_notNull() {
    UserField field = createField("model");
    AbstractMethod setter = AstMethodLookupHelpers.lookupMethod(
        org.lgna.story.MutableRider.class, "setVehicle", (Class<?>) org.lgna.story.SThing.class);
    assertNotNull(setter);
    ExpressionStatement stmt = SetUpMethodGenerator.createSetterStatement(false, field, setter, new NullLiteral());
    assertNotNull(stmt);
    assertTrue(stmt.expression.getValue() instanceof MethodInvocation);
  }

  @Test
  public void createSetVehicleNullStatement_differentFields() {
    for (int i = 0; i < 5; i++) {
      UserField field = createField("rider" + i);
      ExpressionStatement stmt = SetUpMethodGenerator.createSetVehicleNullStatement(field);
      assertNotNull("Statement for rider" + i, stmt);
    }
  }

  @Test
  public void className_isSetUpMethodGenerator() {
    assertEquals("SetUpMethodGenerator", SetUpMethodGenerator.class.getSimpleName());
  }
}
