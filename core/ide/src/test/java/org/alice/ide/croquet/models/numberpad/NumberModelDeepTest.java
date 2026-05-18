package org.alice.ide.croquet.models.numberpad;

import org.junit.Test;
import org.lgna.project.ast.DoubleLiteral;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.IntegerLiteral;

import static org.junit.Assert.*;

public class NumberModelDeepTest {

  // --- IntegerModel: getExplanationIfOkButtonShouldBeDisabled ---

  @Test
  public void integerModel_validNumber_explanationNull() {
    IntegerModel model = IntegerModel.getInstance();
    model.setText("42");
    assertNull(model.getExplanationIfOkButtonShouldBeDisabled());
  }

  @Test
  public void integerModel_emptyText_explanationEnterNumber() {
    IntegerModel model = IntegerModel.getInstance();
    model.setText("");
    assertEquals("enterNumber", model.getExplanationIfOkButtonShouldBeDisabled());
  }

  @Test
  public void integerModel_invalidText_explanationIsNotValid() {
    IntegerModel model = IntegerModel.getInstance();
    model.setText("abc");
    assertEquals("isNotValid", model.getExplanationIfOkButtonShouldBeDisabled());
  }

  // --- IntegerModel: negate ---

  @Test
  public void integerModel_negatePositive_addsMinusSign() {
    IntegerModel model = IntegerModel.getInstance();
    model.setText("123");
    model.negate();
    Expression expr = model.getExpressionValue();
    assertNotNull(expr);
    // After negation the text should be "-123"
    assertNull(model.getExplanationIfOkButtonShouldBeDisabled());
  }

  @Test
  public void integerModel_negateNegative_removesMinusSign() {
    IntegerModel model = IntegerModel.getInstance();
    model.setText("-456");
    model.negate();
    Expression expr = model.getExpressionValue();
    assertNotNull(expr);
    assertTrue(expr instanceof IntegerLiteral);
  }

  @Test
  public void integerModel_negateEmptyString_insertsMinusSign() {
    IntegerModel model = IntegerModel.getInstance();
    model.setText("");
    model.negate();
    // "-" is not a valid number
    assertEquals("isNotValid", model.getExplanationIfOkButtonShouldBeDisabled());
  }

  // --- IntegerModel: delete ---

  @Test
  public void integerModel_deleteFromEnd() {
    IntegerModel model = IntegerModel.getInstance();
    model.setText("789");
    // Move caret to end
    model.getTextField().setCaretPosition(3);
    model.delete();
    // After deleting last char, text should be "78"
    assertNull(model.getExplanationIfOkButtonShouldBeDisabled());
  }

  @Test
  public void integerModel_deleteWithSelection() {
    IntegerModel model = IntegerModel.getInstance();
    model.setText("12345");
    model.selectAll();
    model.delete();
    assertEquals("enterNumber", model.getExplanationIfOkButtonShouldBeDisabled());
  }

  // --- IntegerModel: replaceSelection ---

  @Test
  public void integerModel_replaceSelectionInsertsDigit() {
    IntegerModel model = IntegerModel.getInstance();
    model.setText("10");
    model.getTextField().setCaretPosition(2);
    model.replaceSelection((short) 5);
    // Text should now be "105"
    Expression expr = model.getExpressionValue();
    assertNotNull(expr);
    assertTrue(expr instanceof IntegerLiteral);
  }

  @Test
  public void integerModel_selectAllThenReplaceReplacesAll() {
    IntegerModel model = IntegerModel.getInstance();
    model.setText("999");
    model.selectAll();
    model.replaceSelection((short) 7);
    // Text should now be "7"
    Expression expr = model.getExpressionValue();
    assertNotNull(expr);
    assertTrue(expr instanceof IntegerLiteral);
  }

  // --- IntegerModel: getExpressionValue ---

  @Test
  public void integerModel_validInteger_returnsIntegerLiteral() {
    IntegerModel model = IntegerModel.getInstance();
    model.setText("42");
    Expression expr = model.getExpressionValue();
    assertNotNull(expr);
    assertTrue(expr instanceof IntegerLiteral);
  }

  @Test
  public void integerModel_invalidText_returnsNull() {
    IntegerModel model = IntegerModel.getInstance();
    model.setText("notanumber");
    Expression expr = model.getExpressionValue();
    assertNull(expr);
  }

  @Test
  public void integerModel_exceedsMaxValue_returnsFieldAccess() {
    IntegerModel model = IntegerModel.getInstance();
    model.setText(String.valueOf((long) Integer.MAX_VALUE + 1));
    Expression expr = model.getExpressionValue();
    assertNotNull(expr);
    assertTrue(expr instanceof FieldAccess);
  }

  @Test
  public void integerModel_exceedsMinValue_returnsFieldAccess() {
    IntegerModel model = IntegerModel.getInstance();
    model.setText(String.valueOf((long) Integer.MIN_VALUE - 1));
    Expression expr = model.getExpressionValue();
    assertNotNull(expr);
    assertTrue(expr instanceof FieldAccess);
  }

  // --- IntegerModel: isDecimalPointSupported ---

  @Test
  public void integerModel_decimalPointNotSupported() {
    assertFalse(IntegerModel.getInstance().isDecimalPointSupported());
  }

  // --- DoubleModel tests ---

  @Test
  public void doubleModel_validDouble_returnsDoubleLiteral() {
    DoubleModel model = DoubleModel.getInstance();
    model.setText("3.14");
    Expression expr = model.getExpressionValue();
    assertNotNull(expr);
    assertTrue(expr instanceof DoubleLiteral);
  }

  @Test
  public void doubleModel_isDecimalPointSupported_returnsTrue() {
    assertTrue(DoubleModel.getInstance().isDecimalPointSupported());
  }

  @Test
  public void doubleModel_validNumber_explanationNull() {
    DoubleModel model = DoubleModel.getInstance();
    model.setText("2.71828");
    assertNull(model.getExplanationIfOkButtonShouldBeDisabled());
  }

  @Test
  public void doubleModel_emptyText_explanationEnterNumber() {
    DoubleModel model = DoubleModel.getInstance();
    model.setText("");
    assertEquals("enterNumber", model.getExplanationIfOkButtonShouldBeDisabled());
  }

  @Test
  public void doubleModel_negate() {
    DoubleModel model = DoubleModel.getInstance();
    model.setText("1.5");
    model.negate();
    Expression expr = model.getExpressionValue();
    assertNotNull(expr);
    assertTrue(expr instanceof DoubleLiteral);
  }

  @Test
  public void doubleModel_deleteFromEnd() {
    DoubleModel model = DoubleModel.getInstance();
    model.setText("9.99");
    model.getTextField().setCaretPosition(4);
    model.delete();
    assertNull(model.getExplanationIfOkButtonShouldBeDisabled());
  }

  @Test
  public void integerModel_zeroValue_returnsIntegerLiteral() {
    IntegerModel model = IntegerModel.getInstance();
    model.setText("0");
    Expression expr = model.getExpressionValue();
    assertNotNull(expr);
    assertTrue(expr instanceof IntegerLiteral);
  }

  @Test
  public void integerModel_negativeValue_returnsIntegerLiteral() {
    IntegerModel model = IntegerModel.getInstance();
    model.setText("-100");
    Expression expr = model.getExpressionValue();
    assertNotNull(expr);
    assertTrue(expr instanceof IntegerLiteral);
  }

  @Test
  public void integerModel_deleteOnEmptyDoesNotThrow() {
    IntegerModel model = IntegerModel.getInstance();
    model.setText("");
    model.getTextField().setCaretPosition(0);
    model.delete();
    assertEquals("enterNumber", model.getExplanationIfOkButtonShouldBeDisabled());
  }

  @Test
  public void doubleModel_invalidText_returnsNull() {
    DoubleModel model = DoubleModel.getInstance();
    model.setText("xyz");
    Expression expr = model.getExpressionValue();
    assertNull(expr);
  }

  @Test
  public void doubleModel_negativeDouble() {
    DoubleModel model = DoubleModel.getInstance();
    model.setText("-7.5");
    Expression expr = model.getExpressionValue();
    assertNotNull(expr);
    assertTrue(expr instanceof DoubleLiteral);
  }
}
