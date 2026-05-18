package org.alice.ide.croquet.models.numberpad;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

/**
 * Tests for {@link NumeralOperation}, {@link BackspaceOperation}, and
 * {@link DecimalPointOperation} — NumberPadOperation subclasses ("triggers").
 * Headless-guarded since operations depend on NumberModel which creates JTextField.
 */
public class NumberPadOperationTest {

  @Before
  public void setUp() {
    Assume.assumeFalse("Skipping in headless environment", GraphicsEnvironment.isHeadless());
  }

  // ---- NumeralOperation ----

  @Test
  public void numeralOperation_getInstance_returnsNonNull() {
    IntegerModel model = IntegerModel.getInstance();
    NumeralOperation op = NumeralOperation.getInstance(model, (short) 5);
    assertNotNull(op);
  }

  @Test
  public void numeralOperation_cachedPerModelAndDigit() {
    IntegerModel model = IntegerModel.getInstance();
    NumeralOperation a = NumeralOperation.getInstance(model, (short) 7);
    NumeralOperation b = NumeralOperation.getInstance(model, (short) 7);
    assertSame(a, b);
  }

  @Test
  public void numeralOperation_differentDigits_differentInstances() {
    IntegerModel model = IntegerModel.getInstance();
    NumeralOperation a = NumeralOperation.getInstance(model, (short) 3);
    NumeralOperation b = NumeralOperation.getInstance(model, (short) 5);
    assertNotSame(a, b);
  }

  @Test
  public void numeralOperation_allDigits() {
    IntegerModel model = IntegerModel.getInstance();
    for (short i = 0; i <= 9; i++) {
      NumeralOperation op = NumeralOperation.getInstance(model, i);
      assertNotNull("Digit " + i, op);
    }
  }

  @Test
  public void numeralOperation_differentModels_differentInstances() {
    IntegerModel intModel = IntegerModel.getInstance();
    DoubleModel dblModel = DoubleModel.getInstance();
    NumeralOperation a = NumeralOperation.getInstance(intModel, (short) 1);
    NumeralOperation b = NumeralOperation.getInstance(dblModel, (short) 1);
    assertNotSame(a, b);
  }

  @Test
  public void numeralOperation_sameDigitSameModel_cached() {
    DoubleModel model = DoubleModel.getInstance();
    NumeralOperation first = NumeralOperation.getInstance(model, (short) 0);
    NumeralOperation second = NumeralOperation.getInstance(model, (short) 0);
    assertSame(first, second);
  }

  // ---- BackspaceOperation ----

  @Test
  public void backspaceOperation_getInstance_returnsNonNull() {
    IntegerModel model = IntegerModel.getInstance();
    BackspaceOperation op = BackspaceOperation.getInstance(model);
    assertNotNull(op);
  }

  @Test
  public void backspaceOperation_cachedPerModel() {
    IntegerModel model = IntegerModel.getInstance();
    BackspaceOperation a = BackspaceOperation.getInstance(model);
    BackspaceOperation b = BackspaceOperation.getInstance(model);
    assertSame(a, b);
  }

  @Test
  public void backspaceOperation_differentModels_differentInstances() {
    IntegerModel intModel = IntegerModel.getInstance();
    DoubleModel dblModel = DoubleModel.getInstance();
    BackspaceOperation a = BackspaceOperation.getInstance(intModel);
    BackspaceOperation b = BackspaceOperation.getInstance(dblModel);
    assertNotSame(a, b);
  }

  // ---- DecimalPointOperation ----

  @Test
  public void decimalPointOperation_getInstance_returnsNonNull() {
    DoubleModel model = DoubleModel.getInstance();
    DecimalPointOperation op = DecimalPointOperation.getInstance(model);
    assertNotNull(op);
  }

  @Test
  public void decimalPointOperation_cachedPerModel() {
    DoubleModel model = DoubleModel.getInstance();
    DecimalPointOperation a = DecimalPointOperation.getInstance(model);
    DecimalPointOperation b = DecimalPointOperation.getInstance(model);
    assertSame(a, b);
  }

  @Test
  public void decimalPointOperation_differentModels_differentInstances() {
    DoubleModel dblModel = DoubleModel.getInstance();
    FloatModel fltModel = FloatModel.getInstance();
    DecimalPointOperation a = DecimalPointOperation.getInstance(dblModel);
    DecimalPointOperation b = DecimalPointOperation.getInstance(fltModel);
    assertNotSame(a, b);
  }

  // ---- operation type hierarchy ----

  @Test
  public void numeralOperation_extendsNumberPadOperation() {
    IntegerModel model = IntegerModel.getInstance();
    NumeralOperation op = NumeralOperation.getInstance(model, (short) 0);
    assertTrue(op instanceof NumberPadOperation);
  }

  @Test
  public void backspaceOperation_extendsNumberPadOperation() {
    IntegerModel model = IntegerModel.getInstance();
    BackspaceOperation op = BackspaceOperation.getInstance(model);
    assertTrue(op instanceof NumberPadOperation);
  }

  @Test
  public void decimalPointOperation_extendsNumberPadOperation() {
    DoubleModel model = DoubleModel.getInstance();
    DecimalPointOperation op = DecimalPointOperation.getInstance(model);
    assertTrue(op instanceof NumberPadOperation);
  }

  // ---- FloatModel operations ----

  @Test
  public void floatModel_numeralOperation() {
    FloatModel model = FloatModel.getInstance();
    NumeralOperation op = NumeralOperation.getInstance(model, (short) 8);
    assertNotNull(op);
  }

  @Test
  public void floatModel_backspaceOperation() {
    FloatModel model = FloatModel.getInstance();
    BackspaceOperation op = BackspaceOperation.getInstance(model);
    assertNotNull(op);
  }

  @Test
  public void floatModel_decimalPointOperation() {
    FloatModel model = FloatModel.getInstance();
    DecimalPointOperation op = DecimalPointOperation.getInstance(model);
    assertNotNull(op);
  }
}
