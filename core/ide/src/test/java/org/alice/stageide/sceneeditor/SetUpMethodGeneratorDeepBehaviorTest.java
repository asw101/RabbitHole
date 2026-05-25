package org.alice.stageide.sceneeditor;

import org.junit.Test;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.UserField;

import static org.junit.Assert.*;

public class SetUpMethodGeneratorDeepBehaviorTest {
  private static UserField field(String name) {
    UserField field = new UserField();
    field.name.setValue(name);
    field.valueType.setValue(JavaType.getInstance(org.lgna.story.SModel.class));
    return field;
  }

  @Test
  public void createSetVehicleSceneStatementUsesThisExpressionArgument() {
    ExpressionStatement statement = SetUpMethodGenerator.createSetVehicleSceneStatement(field("rider"));

    MethodInvocation invocation = (MethodInvocation) statement.expression.getValue();
    assertTrue(invocation.requiredArguments.get(0).expression.getValue() instanceof ThisExpression);
  }

  @Test
  public void createSetVehicleFieldStatementUsesVehicleFieldArgument() {
    UserField vehicle = field("vehicle");
    ExpressionStatement statement = SetUpMethodGenerator.createSetVehicleFieldStatement(field("rider"), vehicle);

    MethodInvocation invocation = (MethodInvocation) statement.expression.getValue();
    FieldAccess access = (FieldAccess) invocation.requiredArguments.get(0).expression.getValue();
    assertSame(vehicle, access.field.getValue());
  }
}
