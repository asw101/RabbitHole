package org.alice.stageide.sceneeditor;

import org.junit.Test;
import org.lgna.project.ast.*;
import org.lgna.story.MutableRider;
import org.lgna.story.SModel;
import org.lgna.story.SThing;

import static org.junit.Assert.*;

public class SetUpMethodGeneratorTest {

  private static UserField createModelField(String name) {
    UserField field = new UserField();
    field.name.setValue(name);
    field.valueType.setValue(JavaType.getInstance(SModel.class));
    return field;
  }

  @Test
  public void createSetVehicleNullStatementReturnsNonNull() {
    UserField rider = createModelField("myRider");
    ExpressionStatement stmt = SetUpMethodGenerator.createSetVehicleNullStatement(rider);
    assertNotNull("setVehicle(null) statement should not be null", stmt);
    assertTrue("Statement expression should be a MethodInvocation",
        stmt.expression.getValue() instanceof MethodInvocation);
  }

  @Test
  public void createSetVehicleSceneStatementReturnsNonNull() {
    UserField rider = createModelField("myRider");
    ExpressionStatement stmt = SetUpMethodGenerator.createSetVehicleSceneStatement(rider);
    assertNotNull("setVehicle(this) statement should not be null", stmt);
    MethodInvocation invocation = (MethodInvocation) stmt.expression.getValue();
    assertNotNull(invocation);
  }

  @Test
  public void createSetVehicleFieldStatementReturnsNonNull() {
    UserField rider = createModelField("rider");
    UserField vehicle = createModelField("vehicle");
    ExpressionStatement stmt = SetUpMethodGenerator.createSetVehicleFieldStatement(rider, vehicle);
    assertNotNull("setVehicle(field) statement should not be null", stmt);
  }

  @Test
  public void createSetterInvocationReturnsMethodInvocation() {
    UserField field = createModelField("testModel");
    AbstractMethod setter = AstMethodLookupHelpers.lookupMethod(MutableRider.class, "setVehicle", (Class<?>) SThing.class);
    assertNotNull("Should find setVehicle method", setter);

    MethodInvocation invocation = SetUpMethodGenerator.createSetterInvocation(false, field, setter, new NullLiteral());
    assertNotNull(invocation);
    assertTrue("Instance expression should be a FieldAccess",
        invocation.expression.getValue() instanceof FieldAccess);
  }

  @Test
  public void createSetterInvocationWithThisUsesThisExpression() {
    UserField field = createModelField("testModel");
    AbstractMethod setter = AstMethodLookupHelpers.lookupMethod(MutableRider.class, "setVehicle", (Class<?>) SThing.class);
    assertNotNull(setter);

    MethodInvocation invocation = SetUpMethodGenerator.createSetterInvocation(true, field, setter, new NullLiteral());
    assertNotNull(invocation);
    assertTrue("Instance expression should be ThisExpression when isThis=true",
        invocation.expression.getValue() instanceof ThisExpression);
  }

  @Test
  public void createSetterStatementReturnsExpressionStatement() {
    UserField field = createModelField("testModel");
    AbstractMethod setter = AstMethodLookupHelpers.lookupMethod(MutableRider.class, "setVehicle", (Class<?>) SThing.class);
    assertNotNull(setter);

    ExpressionStatement stmt = SetUpMethodGenerator.createSetterStatement(false, field, setter, new NullLiteral());
    assertNotNull("createSetterStatement should return a non-null ExpressionStatement", stmt);
    assertTrue(stmt.expression.getValue() instanceof MethodInvocation);
  }

  @Test
  public void createSetVehicleNullStatementContainsNullArgument() {
    UserField rider = createModelField("riderField");
    ExpressionStatement stmt = SetUpMethodGenerator.createSetVehicleNullStatement(rider);
    MethodInvocation invocation = (MethodInvocation) stmt.expression.getValue();
    // The first required argument should be a NullLiteral
    SimpleArgument arg = invocation.requiredArguments.get(0);
    assertTrue("Argument should be NullLiteral",
        arg.expression.getValue() instanceof NullLiteral);
  }
}
