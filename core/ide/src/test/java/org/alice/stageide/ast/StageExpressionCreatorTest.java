package org.alice.stageide.ast;

import org.junit.Test;
import org.lgna.project.ast.*;
import org.lgna.story.Color;
import org.lgna.story.Position;
import org.lgna.story.Orientation;
import org.lgna.story.Scale;
import org.lgna.story.Size;

import static org.junit.Assert.*;

/**
 * Tests for {@link ExpressionCreator} (stageide version) —
 * createCustomExpression dispatch, color/position/orientation expression creation.
 */
public class StageExpressionCreatorTest {

  private final ExpressionCreator creator = new ExpressionCreator();

  // ---- createExpression with null ----

  @Test
  public void createExpression_null_returnsNullLiteral() throws org.alice.ide.ast.ExpressionCreator.CannotCreateExpressionException {
    Expression expr = creator.createExpression(null);
    assertTrue(expr instanceof NullLiteral);
  }

  // ---- createExpression with Double ----

  @Test
  public void createExpression_double_returnsExpression() throws org.alice.ide.ast.ExpressionCreator.CannotCreateExpressionException {
    Expression expr = creator.createExpression(3.14);
    assertNotNull(expr);
  }

  // ---- createExpression with Integer ----

  @Test
  public void createExpression_integer_returnsExpression() throws org.alice.ide.ast.ExpressionCreator.CannotCreateExpressionException {
    Expression expr = creator.createExpression(42);
    assertNotNull(expr);
  }

  // ---- createExpression with String ----

  @Test
  public void createExpression_string_returnsExpression() throws org.alice.ide.ast.ExpressionCreator.CannotCreateExpressionException {
    Expression expr = creator.createExpression("hello");
    assertNotNull(expr);
  }

  // ---- createExpression with Enum ----

  @Test
  public void createExpression_enum_returnsExpression() throws org.alice.ide.ast.ExpressionCreator.CannotCreateExpressionException {
    Expression expr = creator.createExpression(Thread.State.NEW);
    assertNotNull(expr);
  }

  // ---- createExpression with Color ----

  @Test
  public void createExpression_color_returnsExpression() throws org.alice.ide.ast.ExpressionCreator.CannotCreateExpressionException {
    Expression expr = creator.createExpression(Color.RED);
    assertNotNull(expr);
  }

  @Test
  public void createExpression_colorBlue_returnsExpression() throws org.alice.ide.ast.ExpressionCreator.CannotCreateExpressionException {
    Expression expr = creator.createExpression(Color.BLUE);
    assertNotNull(expr);
  }

  @Test
  public void createExpression_colorWhite_returnsExpression() throws org.alice.ide.ast.ExpressionCreator.CannotCreateExpressionException {
    Expression expr = creator.createExpression(Color.WHITE);
    assertNotNull(expr);
  }

  @Test
  public void createExpression_colorBlack_returnsExpression() throws org.alice.ide.ast.ExpressionCreator.CannotCreateExpressionException {
    Expression expr = creator.createExpression(Color.BLACK);
    assertNotNull(expr);
  }

  // ---- createExpression with Position ----

  @Test
  public void createExpression_position_returnsExpression() throws org.alice.ide.ast.ExpressionCreator.CannotCreateExpressionException {
    Position pos = new Position(1.0, 2.0, 3.0);
    Expression expr = creator.createExpression(pos);
    assertNotNull(expr);
    assertTrue(expr instanceof InstanceCreation);
  }

  // ---- createExpression with Orientation ----

  @Test
  public void createExpression_orientation_returnsExpression() throws org.alice.ide.ast.ExpressionCreator.CannotCreateExpressionException {
    Orientation orient = new Orientation(0, 0, 0, 1);
    Expression expr = creator.createExpression(orient);
    assertNotNull(expr);
    assertTrue(expr instanceof InstanceCreation);
  }

  // ---- createExpression with Scale ----

  @Test
  public void createExpression_scale_returnsExpression() throws org.alice.ide.ast.ExpressionCreator.CannotCreateExpressionException {
    Scale scale = new Scale(1.0, 1.0, 1.0);
    Expression expr = creator.createExpression(scale);
    assertNotNull(expr);
    assertTrue(expr instanceof InstanceCreation);
  }

  // ---- createExpression with Size ----

  @Test
  public void createExpression_size_returnsExpression() throws org.alice.ide.ast.ExpressionCreator.CannotCreateExpressionException {
    Size size = new Size(2.0, 3.0, 4.0);
    Expression expr = creator.createExpression(size);
    assertNotNull(expr);
    assertTrue(expr instanceof InstanceCreation);
  }

  // ---- createExpression with unsupported type ----

  @Test(expected = org.alice.ide.ast.ExpressionCreator.CannotCreateExpressionException.class)
  public void createExpression_unsupported_throws() throws org.alice.ide.ast.ExpressionCreator.CannotCreateExpressionException {
    creator.createExpression(new Object());
  }
}
