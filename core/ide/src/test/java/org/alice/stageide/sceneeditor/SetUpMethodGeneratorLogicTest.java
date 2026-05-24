package org.alice.stageide.sceneeditor;

import org.junit.Test;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.UserField;
import org.lgna.story.Scale;

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
  public void shouldCreateSizeStatementAlwaysTrueForBoxes() {
    assertTrue(SetUpMethodGeneratorLogic.shouldCreateSizeStatement(true, Scale.IDENTITY));
  }

  @Test
  public void shouldCreateSizeStatementRequiresNonIdentityScaleForNonBoxes() {
    assertFalse(SetUpMethodGeneratorLogic.shouldCreateSizeStatement(false, Scale.IDENTITY));
    assertTrue(SetUpMethodGeneratorLogic.shouldCreateSizeStatement(false, new Scale(2.0, 1.0, 1.0)));
  }
}
