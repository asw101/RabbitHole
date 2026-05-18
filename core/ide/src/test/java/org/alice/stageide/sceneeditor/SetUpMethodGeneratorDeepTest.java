package org.alice.stageide.sceneeditor;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

/**
 * Deep tests for {@link SetUpMethodGenerator} covering the static factory methods
 * that create AST statements for vehicle setup, setter invocations, and field setup.
 */
public class SetUpMethodGeneratorDeepTest {

  private static UserField createModelField(String name, Class<?> type) {
    UserField field = new UserField();
    field.name.setValue(name);
    field.valueType.setValue(JavaType.getInstance(type));
    field.initializer.setValue(new NullLiteral());
    return field;
  }

  // --- createSetVehicleNullStatement ---

  @Test
  public void createSetVehicleNullStatement_returnsExpressionStatement() {
    UserField rider = createModelField("myBiped", org.lgna.story.SBiped.class);
    ExpressionStatement stmt = SetUpMethodGenerator.createSetVehicleNullStatement(rider);
    assertNotNull(stmt);
    assertTrue(stmt.expression.getValue() instanceof MethodInvocation);
  }

  @Test
  public void createSetVehicleNullStatement_methodIsSetVehicle() {
    UserField rider = createModelField("biped", org.lgna.story.SBiped.class);
    ExpressionStatement stmt = SetUpMethodGenerator.createSetVehicleNullStatement(rider);
    MethodInvocation invocation = (MethodInvocation) stmt.expression.getValue();
    assertEquals("setVehicle", invocation.method.getValue().getName());
  }

  @Test
  public void createSetVehicleNullStatement_argumentIsNullLiteral() {
    UserField rider = createModelField("biped", org.lgna.story.SBiped.class);
    ExpressionStatement stmt = SetUpMethodGenerator.createSetVehicleNullStatement(rider);
    MethodInvocation invocation = (MethodInvocation) stmt.expression.getValue();
    assertFalse(invocation.requiredArguments.isEmpty());
    Expression arg = invocation.requiredArguments.get(0).expression.getValue();
    assertTrue(arg instanceof NullLiteral);
  }

  // --- createSetVehicleSceneStatement ---

  @Test
  public void createSetVehicleSceneStatement_returnsStatement() {
    UserField rider = createModelField("model", org.lgna.story.SModel.class);
    ExpressionStatement stmt = SetUpMethodGenerator.createSetVehicleSceneStatement(rider);
    assertNotNull(stmt);
    assertTrue(stmt.expression.getValue() instanceof MethodInvocation);
  }

  @Test
  public void createSetVehicleSceneStatement_argumentIsThisExpression() {
    UserField rider = createModelField("model", org.lgna.story.SModel.class);
    ExpressionStatement stmt = SetUpMethodGenerator.createSetVehicleSceneStatement(rider);
    MethodInvocation invocation = (MethodInvocation) stmt.expression.getValue();
    Expression arg = invocation.requiredArguments.get(0).expression.getValue();
    assertTrue(arg instanceof ThisExpression);
  }

  @Test
  public void createSetVehicleSceneStatement_methodIsSetVehicle() {
    UserField rider = createModelField("quad", org.lgna.story.SQuadruped.class);
    ExpressionStatement stmt = SetUpMethodGenerator.createSetVehicleSceneStatement(rider);
    MethodInvocation invocation = (MethodInvocation) stmt.expression.getValue();
    assertEquals("setVehicle", invocation.method.getValue().getName());
  }

  // --- createSetVehicleFieldStatement ---

  @Test
  public void createSetVehicleFieldStatement_withNonNullVehicle_returnsFieldAccess() {
    UserField rider = createModelField("biped", org.lgna.story.SBiped.class);
    UserField vehicle = createModelField("ground", org.lgna.story.SGround.class);
    ExpressionStatement stmt = SetUpMethodGenerator.createSetVehicleFieldStatement(rider, vehicle);
    assertNotNull(stmt);
    MethodInvocation invocation = (MethodInvocation) stmt.expression.getValue();
    Expression arg = invocation.requiredArguments.get(0).expression.getValue();
    assertTrue(arg instanceof FieldAccess);
  }

  @Test
  public void createSetVehicleFieldStatement_withNullVehicle_returnsThisExpression() {
    UserField rider = createModelField("biped", org.lgna.story.SBiped.class);
    ExpressionStatement stmt = SetUpMethodGenerator.createSetVehicleFieldStatement(rider, null);
    assertNotNull(stmt);
    MethodInvocation invocation = (MethodInvocation) stmt.expression.getValue();
    Expression arg = invocation.requiredArguments.get(0).expression.getValue();
    assertTrue(arg instanceof ThisExpression);
  }

  // --- createSetterInvocation ---

  @Test
  public void createSetterInvocation_isThis_usesThisExpression() {
    UserField field = createModelField("cam", org.lgna.story.SCamera.class);
    AbstractMethod setter = AstMethodLookupHelpers.lookupMethod(
        org.lgna.story.MutableRider.class, "setVehicle", org.lgna.story.SThing.class);
    MethodInvocation inv = SetUpMethodGenerator.createSetterInvocation(true, field, setter, new NullLiteral());
    assertNotNull(inv);
    assertTrue(inv.expression.getValue() instanceof ThisExpression);
  }

  @Test
  public void createSetterInvocation_notThis_usesFieldAccess() {
    UserField field = createModelField("cam", org.lgna.story.SCamera.class);
    AbstractMethod setter = AstMethodLookupHelpers.lookupMethod(
        org.lgna.story.MutableRider.class, "setVehicle", org.lgna.story.SThing.class);
    MethodInvocation inv = SetUpMethodGenerator.createSetterInvocation(false, field, setter, new NullLiteral());
    assertNotNull(inv);
    assertTrue(inv.expression.getValue() instanceof FieldAccess);
  }

  // --- createSetterStatement ---

  @Test
  public void createSetterStatement_returnsExpressionStatement() {
    UserField field = createModelField("biped", org.lgna.story.SBiped.class);
    AbstractMethod setter = AstMethodLookupHelpers.lookupMethod(
        org.lgna.story.MutableRider.class, "setVehicle", org.lgna.story.SThing.class);
    ExpressionStatement stmt = SetUpMethodGenerator.createSetterStatement(true, field, setter, new NullLiteral());
    assertNotNull(stmt);
    assertTrue(stmt.expression.getValue() instanceof MethodInvocation);
  }

  @Test
  public void createSetterStatement_notThis_wrapsFieldAccess() {
    UserField field = createModelField("flyer", org.lgna.story.SFlyer.class);
    AbstractMethod setter = AstMethodLookupHelpers.lookupMethod(
        org.lgna.story.MutableRider.class, "setVehicle", org.lgna.story.SThing.class);
    ExpressionStatement stmt = SetUpMethodGenerator.createSetterStatement(false, field, setter, new ThisExpression());
    assertNotNull(stmt);
    MethodInvocation inv = (MethodInvocation) stmt.expression.getValue();
    assertTrue(inv.expression.getValue() instanceof FieldAccess);
  }

  // --- createSetVehicleNullStatement with different types ---

  @Test
  public void createSetVehicleNull_withFlyer_works() {
    UserField rider = createModelField("eagle", org.lgna.story.SFlyer.class);
    ExpressionStatement stmt = SetUpMethodGenerator.createSetVehicleNullStatement(rider);
    assertNotNull(stmt);
    MethodInvocation inv = (MethodInvocation) stmt.expression.getValue();
    assertEquals("setVehicle", inv.method.getValue().getName());
  }

  @Test
  public void createSetVehicleNull_withQuadruped_works() {
    UserField rider = createModelField("dog", org.lgna.story.SQuadruped.class);
    ExpressionStatement stmt = SetUpMethodGenerator.createSetVehicleNullStatement(rider);
    assertNotNull(stmt);
  }

  // --- createSetVehicleScene with different types ---

  @Test
  public void createSetVehicleScene_withCamera_works() {
    UserField rider = createModelField("camera", org.lgna.story.SCamera.class);
    ExpressionStatement stmt = SetUpMethodGenerator.createSetVehicleSceneStatement(rider);
    assertNotNull(stmt);
    MethodInvocation invocation = (MethodInvocation) stmt.expression.getValue();
    assertEquals("setVehicle", invocation.method.getValue().getName());
  }

  // --- createSetVehicleFieldStatement with same field as vehicle ---

  @Test
  public void createSetVehicleField_selfReference_returnsFieldAccess() {
    UserField field = createModelField("biped", org.lgna.story.SBiped.class);
    ExpressionStatement stmt = SetUpMethodGenerator.createSetVehicleFieldStatement(field, field);
    assertNotNull(stmt);
    MethodInvocation inv = (MethodInvocation) stmt.expression.getValue();
    assertTrue(inv.requiredArguments.get(0).expression.getValue() instanceof FieldAccess);
  }

  // --- instance expression is FieldAccess for rider ---

  @Test
  public void createSetVehicleNullStatement_instanceIsFieldAccess() {
    UserField rider = createModelField("biped", org.lgna.story.SBiped.class);
    ExpressionStatement stmt = SetUpMethodGenerator.createSetVehicleNullStatement(rider);
    MethodInvocation inv = (MethodInvocation) stmt.expression.getValue();
    assertTrue(inv.expression.getValue() instanceof FieldAccess);
  }
}
