package org.alice.stageide.ast;

import org.alice.ide.ast.ExpressionCreator.CannotCreateExpressionException;
import org.junit.Test;
import org.lgna.project.ast.*;
import org.lgna.story.Color;
import org.lgna.story.Orientation;
import org.lgna.story.Position;
import org.lgna.story.Scale;
import org.lgna.story.Size;

import static org.junit.Assert.*;

/**
 * Deep coverage tests for {@link ExpressionCreator} (stageide variant) —
 * exercising position/orientation/scale/size/color expression creation
 * beyond the existing StageExpressionCreatorTest.
 */
public class ExpressionCreatorDeepTest {

  private final ExpressionCreator creator = new ExpressionCreator();

  // ---- Position with zeroes ----

  @Test
  public void createExpression_positionZero_returnsInstanceCreation() throws CannotCreateExpressionException {
    Position pos = new Position(0, 0, 0);
    Expression expr = creator.createExpression(pos);
    assertNotNull(expr);
    assertTrue(expr instanceof InstanceCreation);
  }

  // ---- Position with negative values ----

  @Test
  public void createExpression_positionNegative_returnsInstanceCreation() throws CannotCreateExpressionException {
    Position pos = new Position(-1.5, -2.5, -3.5);
    Expression expr = creator.createExpression(pos);
    assertNotNull(expr);
    assertTrue(expr instanceof InstanceCreation);
  }

  // ---- Orientation identity quaternion ----

  @Test
  public void createExpression_orientationIdentity_returnsInstanceCreation() throws CannotCreateExpressionException {
    Orientation orient = new Orientation(0, 0, 0, 1);
    Expression expr = creator.createExpression(orient);
    assertTrue(expr instanceof InstanceCreation);
  }

  // ---- Orientation non-trivial ----

  @Test
  public void createExpression_orientationRotated_returnsInstanceCreation() throws CannotCreateExpressionException {
    Orientation orient = new Orientation(0.707, 0, 0, 0.707);
    Expression expr = creator.createExpression(orient);
    assertNotNull(expr);
    assertTrue(expr instanceof InstanceCreation);
  }

  // ---- Scale uniform ----

  @Test
  public void createExpression_scaleUniform_returnsInstanceCreation() throws CannotCreateExpressionException {
    Scale scale = new Scale(2.0, 2.0, 2.0);
    Expression expr = creator.createExpression(scale);
    assertTrue(expr instanceof InstanceCreation);
  }

  // ---- Scale non-uniform ----

  @Test
  public void createExpression_scaleNonUniform_returnsInstanceCreation() throws CannotCreateExpressionException {
    Scale scale = new Scale(1.0, 2.0, 0.5);
    Expression expr = creator.createExpression(scale);
    assertNotNull(expr);
    assertTrue(expr instanceof InstanceCreation);
  }

  // ---- Size ----

  @Test
  public void createExpression_size_returnsInstanceCreation() throws CannotCreateExpressionException {
    Size size = new Size(1.0, 2.0, 3.0);
    Expression expr = creator.createExpression(size);
    assertTrue(expr instanceof InstanceCreation);
  }

  // ---- Color named constants ----

  @Test
  public void createExpression_colorGreen_returnsFieldAccess() throws CannotCreateExpressionException {
    Expression expr = creator.createExpression(Color.GREEN);
    assertNotNull(expr);
    // Named colors should be resolved to static field accesses
    assertTrue("Named color should produce FieldAccess", expr instanceof FieldAccess);
  }

  @Test
  public void createExpression_colorYellow_returnsFieldAccess() throws CannotCreateExpressionException {
    Expression expr = creator.createExpression(Color.YELLOW);
    assertNotNull(expr);
    assertTrue(expr instanceof FieldAccess);
  }

  // ---- Color custom (not a named constant) ----

  @Test
  public void createExpression_customColor_returnsInstanceCreation() throws CannotCreateExpressionException {
    Color custom = new Color(0.1, 0.2, 0.3);
    Expression expr = creator.createExpression(custom);
    assertNotNull(expr);
    assertTrue("Custom color should produce InstanceCreation", expr instanceof InstanceCreation);
  }

  // ---- null Position ----

  @Test
  public void createExpression_null_returnsNullLiteral() throws CannotCreateExpressionException {
    Expression expr = creator.createExpression(null);
    assertTrue(expr instanceof NullLiteral);
  }

  // ---- Enum values ----

  @Test
  public void createExpression_enum_returnsFieldAccess() throws CannotCreateExpressionException {
    Expression expr = creator.createExpression(Thread.State.RUNNABLE);
    assertNotNull(expr);
    assertTrue(expr instanceof FieldAccess);
  }

  // ---- Integer ----

  @Test
  public void createExpression_integer_returnsIntegerLiteral() throws CannotCreateExpressionException {
    Expression expr = creator.createExpression(99);
    assertTrue(expr instanceof IntegerLiteral);
  }

  // ---- Double ----

  @Test
  public void createExpression_double_returnsDoubleLiteral() throws CannotCreateExpressionException {
    Expression expr = creator.createExpression(2.718);
    assertTrue(expr instanceof DoubleLiteral);
  }

  // ---- String ----

  @Test
  public void createExpression_string_returnsStringLiteral() throws CannotCreateExpressionException {
    Expression expr = creator.createExpression("test");
    assertTrue(expr instanceof StringLiteral);
  }

  // ---- Unsupported type ----

  @Test(expected = CannotCreateExpressionException.class)
  public void createExpression_unsupported_throws() throws CannotCreateExpressionException {
    creator.createExpression(new Object());
  }
}
