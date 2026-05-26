package org.alice.stageide.sceneeditor;

import org.junit.Test;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.UserField;
import org.lgna.story.Orientation;
import org.lgna.story.SModel;
import org.lgna.story.Scale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class SetUpMethodGeneratorLogicBehaviorTest {
  @Test
  public void createInstanceExpressionCreatesThisOrFieldAccessForRequestedTarget() {
    UserField field = new UserField("ship", Object.class);

    Expression thisExpression = SetUpMethodGeneratorLogic.createInstanceExpression(true, field);
    Expression fieldExpression = SetUpMethodGeneratorLogic.createInstanceExpression(false, field);

    assertTrue(thisExpression instanceof ThisExpression);
    assertTrue(fieldExpression instanceof FieldAccess);
    assertSame(field, ((FieldAccess) fieldExpression).field.getValue());
  }

  @Test
  public void addDurationIfRequestedUsesNegativeOneAsSkipSentinel() throws Exception {
    UserField field = new UserField("ship", SModel.class);
    ExpressionStatement statement = (ExpressionStatement) SetUpMethodGenerator.createOrientationStatement(false, field, new Orientation());
    MethodInvocation invocation = (MethodInvocation) statement.expression.getValue();

    SetUpMethodGeneratorLogic.addDurationIfRequested(statement, -1);

    assertEquals(0, invocation.keyedArguments.size());
  }

  @Test
  public void shouldCreateSizeStatementDependsOnBoxFlagAndIdentityScale() {
    assertTrue(SetUpMethodGeneratorLogic.shouldCreateSizeStatement(true, Scale.IDENTITY));
    assertFalse(SetUpMethodGeneratorLogic.shouldCreateSizeStatement(false, Scale.IDENTITY));
    assertTrue(SetUpMethodGeneratorLogic.shouldCreateSizeStatement(false, new Scale(2.0, 1.0, 1.0)));
  }

  @Test
  public void shouldLogMissingSetterSkipsThisVehicleGetterOnly() {
    assertFalse(SetUpMethodGeneratorLogic.shouldLogMissingSetter("getVehicle", true));
    assertTrue(SetUpMethodGeneratorLogic.shouldLogMissingSetter("getVehicle", false));
    assertTrue(SetUpMethodGeneratorLogic.shouldLogMissingSetter("getPaint", true));
  }
}
