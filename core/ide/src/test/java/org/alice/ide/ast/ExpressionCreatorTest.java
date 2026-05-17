package org.alice.ide.ast;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

/**
 * Tests for {@link ExpressionCreator} — expression creation from Java values.
 * Tests the concrete helper methods in the abstract class via a minimal subclass.
 */
public class ExpressionCreatorTest {

  private static class TestableExpressionCreator extends ExpressionCreator {
    @Override
    protected Expression createCustomExpression(Object value) throws CannotCreateExpressionException {
      throw new CannotCreateExpressionException(value);
    }
  }

  private final TestableExpressionCreator creator = new TestableExpressionCreator();

  // ---- createExpression with null ----

  @Test
  public void createExpression_null_returnsNullLiteral() throws ExpressionCreator.CannotCreateExpressionException {
    Expression expr = creator.createExpression(null);
    assertTrue(expr instanceof NullLiteral);
  }

  // ---- createExpression with Double ----

  @Test
  public void createExpression_double_returnsExpression() throws ExpressionCreator.CannotCreateExpressionException {
    Expression expr = creator.createExpression(3.14);
    assertNotNull(expr);
  }

  // ---- createExpression with Integer ----

  @Test
  public void createExpression_integer_returnsExpression() throws ExpressionCreator.CannotCreateExpressionException {
    Expression expr = creator.createExpression(42);
    assertNotNull(expr);
  }

  // ---- createExpression with String ----

  @Test
  public void createExpression_string_returnsExpression() throws ExpressionCreator.CannotCreateExpressionException {
    Expression expr = creator.createExpression("hello");
    assertNotNull(expr);
  }

  @Test
  public void createExpression_nullString_returnsNullLiteral() throws ExpressionCreator.CannotCreateExpressionException {
    Expression expr = creator.createExpression(null);
    assertTrue(expr instanceof NullLiteral);
  }

  // ---- createExpression with Boolean falls through to custom ----

  @Test(expected = ExpressionCreator.CannotCreateExpressionException.class)
  public void createExpression_boolean_delegatesToCustom() throws ExpressionCreator.CannotCreateExpressionException {
    // Boolean is not handled by the base switch; falls through to createCustomExpression
    creator.createExpression(Boolean.TRUE);
  }

  // ---- createExpression with unsupported type falls through to custom ----

  @Test(expected = ExpressionCreator.CannotCreateExpressionException.class)
  public void createExpression_unsupportedType_throwsCannotCreate() throws ExpressionCreator.CannotCreateExpressionException {
    creator.createExpression(new Object());
  }

  @Test(expected = ExpressionCreator.CannotCreateExpressionException.class)
  public void createExpression_list_throwsCannotCreate() throws ExpressionCreator.CannotCreateExpressionException {
    creator.createExpression(java.util.List.of(1, 2, 3));
  }

  // ---- CannotCreateExpressionException ----

  @Test
  public void cannotCreateException_storesValue() {
    Object val = "test";
    ExpressionCreator.CannotCreateExpressionException ex =
        new ExpressionCreator.CannotCreateExpressionException(val);
    assertSame(val, ex.getValue());
  }

  // ---- constants ----

  @Test
  public void decimalPlaceConstants() {
    assertEquals(3, ExpressionCreator.MILLI_DECIMAL_PLACES);
    assertEquals(6, ExpressionCreator.MICRO_DECIMAL_PLACES);
    assertEquals(ExpressionCreator.MICRO_DECIMAL_PLACES, ExpressionCreator.DEFAULT_DECIMAL_PLACES);
  }
}
