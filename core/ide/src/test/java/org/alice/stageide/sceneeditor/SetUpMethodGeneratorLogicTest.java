package org.alice.stageide.sceneeditor;

import org.junit.Test;
import org.lgna.project.ast.AstMethodLookupHelpers;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.DoubleLiteral;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.UserField;
import org.lgna.story.Scale;
import org.lgna.story.STurnable;
import org.lgna.story.SetOrientationRelativeToVehicle;

import static org.junit.Assert.*;

public class SetUpMethodGeneratorLogicTest {
  @Test
  public void createInstanceExpressionUsesThisWhenRequested() {
    assertTrue(SetUpMethodGeneratorLogic.createInstanceExpression(true, null) instanceof ThisExpression);
  }

  @Test
  public void createInstanceExpressionUsesFieldAccessOtherwise() {
    UserField field = new UserField("ship", Object.class);

    assertTrue(SetUpMethodGeneratorLogic.createInstanceExpression(false, field) instanceof FieldAccess);
  }

  @Test
  public void addDurationIfRequestedAddsKeyedArgument() {
    UserField field = new UserField("ship", Object.class);
    ExpressionStatement statement = AstUtilities.createMethodInvocationStatement(
        new FieldAccess(field),
        AstMethodLookupHelpers.lookupMethod(STurnable.class, "setOrientationRelativeToVehicle", org.lgna.story.Orientation.class, SetOrientationRelativeToVehicle.Detail[].class),
        new NullLiteral());

    SetUpMethodGeneratorLogic.addDurationIfRequested(statement, 0.5);

    MethodInvocation invocation = (MethodInvocation) statement.expression.getValue();
    assertEquals(1, invocation.keyedArguments.size());
    MethodInvocation durationInvocation = (MethodInvocation) invocation.keyedArguments.get(0).expression.getValue();
    assertEquals("duration", durationInvocation.method.getValue().getName());
    assertEquals(0.5, ((DoubleLiteral) durationInvocation.requiredArguments.get(0).expression.getValue()).value.getValue(), 0.0);
  }

  @Test
  public void addDurationIfRequestedSkipsSentinelDuration() {
    ExpressionStatement statement = new ExpressionStatement(new MethodInvocation());

    SetUpMethodGeneratorLogic.addDurationIfRequested(statement, -1);

    MethodInvocation invocation = (MethodInvocation) statement.expression.getValue();
    assertEquals(0, invocation.keyedArguments.size());
  }

  @Test
  public void shouldCreateSizeStatementAlwaysTrueForBoxes() {
    assertTrue(SetUpMethodGeneratorLogic.shouldCreateSizeStatement(true, Scale.IDENTITY));
  }

  @Test
  public void shouldCreateSizeStatementRequiresNonIdentityScaleForNonBoxes() {
    assertFalse(SetUpMethodGeneratorLogic.shouldCreateSizeStatement(false, Scale.IDENTITY));
    assertTrue(SetUpMethodGeneratorLogic.shouldCreateSizeStatement(false, new Scale(2.0, 1.0, 1.0)));
  }

  @Test
  public void shouldLogMissingSetterSuppressesThisVehicleGetterNoise() {
    assertFalse(SetUpMethodGeneratorLogic.shouldLogMissingSetter("getVehicle", true));
    assertTrue(SetUpMethodGeneratorLogic.shouldLogMissingSetter("getVehicle", false));
    assertTrue(SetUpMethodGeneratorLogic.shouldLogMissingSetter("getPaint", true));
  }
}
