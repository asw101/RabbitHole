package org.alice.ide.croquet.models.numberpad;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

/**
 * Tests for NumberPad operation singletons — {@link PlusMinusOperation},
 * {@link BackspaceOperation}, {@link DecimalPointOperation}, and {@link NumeralOperation}.
 * All operations require a NumberModel which requires Swing, so headless-guarded.
 */
public class NumberPadOperationTest {

  private IntegerModel integerModel;

  @Before
  public void setUp() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    integerModel = IntegerModel.getInstance();
    integerModel.setText("");
  }

  // ---- PlusMinusOperation ----

  @Test
  public void plusMinus_getInstance_returnsNonNull() {
    PlusMinusOperation op = PlusMinusOperation.getInstance(integerModel);
    assertNotNull(op);
  }

  @Test
  public void plusMinus_getInstance_returnsSameForSameModel() {
    PlusMinusOperation op1 = PlusMinusOperation.getInstance(integerModel);
    PlusMinusOperation op2 = PlusMinusOperation.getInstance(integerModel);
    assertSame(op1, op2);
  }

  @Test
  public void plusMinus_isInstanceOfNumberPadOperation() {
    PlusMinusOperation op = PlusMinusOperation.getInstance(integerModel);
    assertTrue(op instanceof NumberPadOperation);
  }

  @Test
  public void plusMinus_getInstance_differentModels_returnDifferent() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    DoubleModel doubleModel = DoubleModel.getInstance();
    PlusMinusOperation op1 = PlusMinusOperation.getInstance(integerModel);
    PlusMinusOperation op2 = PlusMinusOperation.getInstance(doubleModel);
    assertNotSame(op1, op2);
  }

  // ---- BackspaceOperation ----

  @Test
  public void backspace_getInstance_returnsNonNull() {
    BackspaceOperation op = BackspaceOperation.getInstance(integerModel);
    assertNotNull(op);
  }

  @Test
  public void backspace_getInstance_returnsSameForSameModel() {
    BackspaceOperation op1 = BackspaceOperation.getInstance(integerModel);
    BackspaceOperation op2 = BackspaceOperation.getInstance(integerModel);
    assertSame(op1, op2);
  }

  @Test
  public void backspace_isInstanceOfNumberPadOperation() {
    BackspaceOperation op = BackspaceOperation.getInstance(integerModel);
    assertTrue(op instanceof NumberPadOperation);
  }

  @Test
  public void backspace_getInstance_differentModels_returnDifferent() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    DoubleModel doubleModel = DoubleModel.getInstance();
    BackspaceOperation op1 = BackspaceOperation.getInstance(integerModel);
    BackspaceOperation op2 = BackspaceOperation.getInstance(doubleModel);
    assertNotSame(op1, op2);
  }

  // ---- DecimalPointOperation ----

  @Test
  public void decimalPoint_getInstance_returnsNonNull() {
    DecimalPointOperation op = DecimalPointOperation.getInstance(integerModel);
    assertNotNull(op);
  }

  @Test
  public void decimalPoint_getInstance_returnsSameForSameModel() {
    DecimalPointOperation op1 = DecimalPointOperation.getInstance(integerModel);
    DecimalPointOperation op2 = DecimalPointOperation.getInstance(integerModel);
    assertSame(op1, op2);
  }

  @Test
  public void decimalPoint_isInstanceOfNumberPadOperation() {
    DecimalPointOperation op = DecimalPointOperation.getInstance(integerModel);
    assertTrue(op instanceof NumberPadOperation);
  }

  @Test
  public void decimalPoint_getInstance_differentModels_returnDifferent() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    DoubleModel doubleModel = DoubleModel.getInstance();
    DecimalPointOperation op1 = DecimalPointOperation.getInstance(integerModel);
    DecimalPointOperation op2 = DecimalPointOperation.getInstance(doubleModel);
    assertNotSame(op1, op2);
  }

  // ---- NumeralOperation ----

  @Test
  public void numeral_getInstance_returnsNonNull() {
    NumeralOperation op = NumeralOperation.getInstance(integerModel, (short) 5);
    assertNotNull(op);
  }

  @Test
  public void numeral_getInstance_returnsSameForSameModelAndDigit() {
    NumeralOperation op1 = NumeralOperation.getInstance(integerModel, (short) 3);
    NumeralOperation op2 = NumeralOperation.getInstance(integerModel, (short) 3);
    assertSame(op1, op2);
  }

  @Test
  public void numeral_getInstance_differentDigits_returnDifferent() {
    NumeralOperation op1 = NumeralOperation.getInstance(integerModel, (short) 1);
    NumeralOperation op2 = NumeralOperation.getInstance(integerModel, (short) 2);
    assertNotSame(op1, op2);
  }

  @Test
  public void numeral_isInstanceOfNumberPadOperation() {
    NumeralOperation op = NumeralOperation.getInstance(integerModel, (short) 0);
    assertTrue(op instanceof NumberPadOperation);
  }

  @Test
  public void numeral_allDigits_0through9() {
    for (short d = 0; d <= 9; d++) {
      NumeralOperation op = NumeralOperation.getInstance(integerModel, d);
      assertNotNull("Digit " + d + " should produce a non-null operation", op);
    }
  }

  @Test
  public void numeral_allDigits_cachedCorrectly() {
    for (short d = 0; d <= 9; d++) {
      NumeralOperation first = NumeralOperation.getInstance(integerModel, d);
      NumeralOperation second = NumeralOperation.getInstance(integerModel, d);
      assertSame("Digit " + d + " should be cached", first, second);
    }
  }

  @Test
  public void numeral_differentModels_differentOperations() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    DoubleModel doubleModel = DoubleModel.getInstance();
    NumeralOperation op1 = NumeralOperation.getInstance(integerModel, (short) 5);
    NumeralOperation op2 = NumeralOperation.getInstance(doubleModel, (short) 5);
    assertNotSame(op1, op2);
  }

  // ---- cross-operation type independence ----

  @Test
  public void differentOperationTypes_areIndependent() {
    PlusMinusOperation pm = PlusMinusOperation.getInstance(integerModel);
    BackspaceOperation bs = BackspaceOperation.getInstance(integerModel);
    DecimalPointOperation dp = DecimalPointOperation.getInstance(integerModel);
    NumeralOperation n5 = NumeralOperation.getInstance(integerModel, (short) 5);

    assertNotSame(pm, bs);
    assertNotSame(pm, dp);
    assertNotSame(pm, n5);
    assertNotSame(bs, dp);
    assertNotSame(bs, n5);
    assertNotSame(dp, n5);
  }

  // ---- FloatModel operations ----

  @Test
  public void floatModel_operations_areNonNull() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    FloatModel floatModel = FloatModel.getInstance();
    assertNotNull(PlusMinusOperation.getInstance(floatModel));
    assertNotNull(BackspaceOperation.getInstance(floatModel));
    assertNotNull(DecimalPointOperation.getInstance(floatModel));
    assertNotNull(NumeralOperation.getInstance(floatModel, (short) 7));
  }

  // ---- DoubleModel operations ----

  @Test
  public void doubleModel_operations_areNonNull() {
    Assume.assumeFalse(GraphicsEnvironment.isHeadless());
    DoubleModel doubleModel = DoubleModel.getInstance();
    assertNotNull(PlusMinusOperation.getInstance(doubleModel));
    assertNotNull(BackspaceOperation.getInstance(doubleModel));
    assertNotNull(DecimalPointOperation.getInstance(doubleModel));
    assertNotNull(NumeralOperation.getInstance(doubleModel, (short) 0));
  }
}
