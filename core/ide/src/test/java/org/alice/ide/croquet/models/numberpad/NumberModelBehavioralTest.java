package org.alice.ide.croquet.models.numberpad;

import org.junit.Before;
import org.junit.Test;
import org.lgna.project.ast.DoubleLiteral;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.IntegerLiteral;

import java.text.DecimalFormatSymbols;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class NumberModelBehavioralTest {

  private IntegerModel integerModel;
  private DoubleModel doubleModel;

  @Before
  public void resetModels() {
    integerModel = IntegerModel.getInstance();
    doubleModel = DoubleModel.getInstance();
    integerModel.setText("");
    doubleModel.setText("");
  }

  @Test
  public void replacingSelectionAndBackspacingUpdatesIntegerTextPredictably() {
    integerModel.setText("1234");
    integerModel.getTextField().select(1, 3);

    integerModel.replaceSelection((short) 9);
    assertEquals("194", integerModel.getTextField().getText());

    integerModel.getTextField().setCaretPosition(2);
    integerModel.delete();

    assertEquals("14", integerModel.getTextField().getText());
    assertTrue(integerModel.getExpressionValue() instanceof IntegerLiteral);
    assertNull(integerModel.getExplanationIfOkButtonShouldBeDisabled());
  }

  @Test
  public void negatingLocalizedDoubleInputKeepsTheExpressionValid() {
    String separator = String.valueOf(new DecimalFormatSymbols().getDecimalSeparator());

    doubleModel.setText("12" + separator + "5");
    doubleModel.negate();

    assertEquals("-12" + separator + "5", doubleModel.getTextField().getText());
    assertTrue(doubleModel.getExpressionValue() instanceof DoubleLiteral);
    assertNull(doubleModel.getExplanationIfOkButtonShouldBeDisabled());
  }

  @Test
  public void integerOverflowMapsToBoundaryFieldAccesses() {
    integerModel.setText(String.valueOf((long) Integer.MAX_VALUE + 1L));
    Expression maxExpression = integerModel.getExpressionValue();

    assertTrue(maxExpression instanceof FieldAccess);
    assertEquals("MAX_VALUE", ((FieldAccess) maxExpression).field.getValue().getName());

    integerModel.setText(String.valueOf((long) Integer.MIN_VALUE - 1L));
    Expression minExpression = integerModel.getExpressionValue();

    assertTrue(minExpression instanceof FieldAccess);
    assertEquals("MIN_VALUE", ((FieldAccess) minExpression).field.getValue().getName());
  }
}
