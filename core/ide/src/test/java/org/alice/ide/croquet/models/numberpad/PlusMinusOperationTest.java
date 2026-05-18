package org.alice.ide.croquet.models.numberpad;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

/**
 * Tests for {@link PlusMinusOperation} — NumberPadOperation that negates values.
 * Headless-guarded since it depends on NumberModel which creates JTextField.
 */
public class PlusMinusOperationTest {

  @Before
  public void setUp() {
    Assume.assumeFalse("Skipping in headless environment", GraphicsEnvironment.isHeadless());
  }

  // ---- getInstance ----

  @Test
  public void getInstance_returnsNonNull() {
    IntegerModel model = IntegerModel.getInstance();
    PlusMinusOperation op = PlusMinusOperation.getInstance(model);
    assertNotNull(op);
  }

  @Test
  public void getInstance_cachedPerModel() {
    IntegerModel model = IntegerModel.getInstance();
    PlusMinusOperation a = PlusMinusOperation.getInstance(model);
    PlusMinusOperation b = PlusMinusOperation.getInstance(model);
    assertSame(a, b);
  }

  @Test
  public void getInstance_differentModels_differentInstances() {
    IntegerModel intModel = IntegerModel.getInstance();
    DoubleModel dblModel = DoubleModel.getInstance();
    PlusMinusOperation a = PlusMinusOperation.getInstance(intModel);
    PlusMinusOperation b = PlusMinusOperation.getInstance(dblModel);
    assertNotSame(a, b);
  }

  // ---- type hierarchy ----

  @Test
  public void extendsNumberPadOperation() {
    IntegerModel model = IntegerModel.getInstance();
    PlusMinusOperation op = PlusMinusOperation.getInstance(model);
    assertTrue(op instanceof NumberPadOperation);
  }

  // ---- caching with all models ----

  @Test
  public void cachedForIntegerModel() {
    IntegerModel model = IntegerModel.getInstance();
    PlusMinusOperation first = PlusMinusOperation.getInstance(model);
    PlusMinusOperation second = PlusMinusOperation.getInstance(model);
    assertSame(first, second);
  }

  @Test
  public void cachedForDoubleModel() {
    DoubleModel model = DoubleModel.getInstance();
    PlusMinusOperation first = PlusMinusOperation.getInstance(model);
    PlusMinusOperation second = PlusMinusOperation.getInstance(model);
    assertSame(first, second);
  }

  @Test
  public void cachedForFloatModel() {
    FloatModel model = FloatModel.getInstance();
    PlusMinusOperation first = PlusMinusOperation.getInstance(model);
    PlusMinusOperation second = PlusMinusOperation.getInstance(model);
    assertSame(first, second);
  }

  // ---- all three models produce distinct instances ----

  @Test
  public void allThreeModels_distinctInstances() {
    PlusMinusOperation intOp = PlusMinusOperation.getInstance(IntegerModel.getInstance());
    PlusMinusOperation dblOp = PlusMinusOperation.getInstance(DoubleModel.getInstance());
    PlusMinusOperation fltOp = PlusMinusOperation.getInstance(FloatModel.getInstance());

    assertNotSame(intOp, dblOp);
    assertNotSame(dblOp, fltOp);
    assertNotSame(intOp, fltOp);
  }
}
