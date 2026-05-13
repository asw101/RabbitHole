package org.lgna.ik.core.enforcer;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Characterization tests for AngleDeltas — a globally-indexed angle delta
 * vector used in the IK solver's convergence and priority loops.
 *
 * Context-dependent: requires IkEnforcerContext for getForAxis() which maps
 * JacobianAxis → global index via axisToIndex. The self-reference fix
 * (angleDeltas.getByGlobalIndex → this.getByGlobalIndex) is verified here.
 */
public class AngleDeltasTest {

  private static final double DELTA = 1e-12;

  private IkEnforcerContextStub context;
  private List<JacobianAxis> axes;

  @Before
  public void setUp() {
    context = new IkEnforcerContextStub(6); // 2 joints × 3 axes
    axes = context.getIndexToAxis();
  }

  @Test
  public void constructorCreatesZeroedStorage() {
    AngleDeltas ad = new AngleDeltas(6, context);
    assertEquals(6, ad.storage.length);
    for (double v : ad.storage) {
      assertEquals(0.0, v, DELTA);
    }
  }

  @Test
  public void getByGlobalIndexReturnsCorrectValue() {
    AngleDeltas ad = new AngleDeltas(4, context);
    ad.storage[2] = 1.5;
    assertEquals(1.5, ad.getByGlobalIndex(2), DELTA);
  }

  @Test
  public void addAccumulatesElementwise() {
    AngleDeltas a = new AngleDeltas(3, context);
    a.storage[0] = 1.0;
    a.storage[1] = 2.0;
    a.storage[2] = 3.0;

    AngleDeltas b = new AngleDeltas(3, context);
    b.storage[0] = 0.5;
    b.storage[1] = 0.5;
    b.storage[2] = 0.5;

    a.add(b);

    assertArrayEquals(new double[]{1.5, 2.5, 3.5}, a.storage, DELTA);
  }

  @Test
  public void addDoesNotModifyOther() {
    AngleDeltas a = new AngleDeltas(2, context);
    AngleDeltas b = new AngleDeltas(2, context);
    b.storage[0] = 7.0;

    a.add(b);

    assertEquals(7.0, b.storage[0], DELTA);
  }

  @Test
  public void getForAxisUsesContextMapping() {
    AngleDeltas ad = new AngleDeltas(6, context);
    // Set value at global index 3
    ad.storage[3] = 42.0;

    // axes.get(3) maps to global index 3 in the stub
    double result = ad.getForAxis(axes.get(3));
    assertEquals(42.0, result, DELTA);
  }

  @Test
  public void getForAxisSelfReferenceFixReadsFromThis() {
    // Verifies the self-reference fix: after extraction, getForAxis()
    // reads from this.getByGlobalIndex() not from some external field.
    AngleDeltas ad = new AngleDeltas(6, context);
    ad.storage[0] = 10.0;
    ad.storage[1] = 20.0;

    assertEquals(10.0, ad.getForAxis(axes.get(0)), DELTA);
    assertEquals(20.0, ad.getForAxis(axes.get(1)), DELTA);
  }

  @Test
  public void multipleAddsAccumulate() {
    AngleDeltas base = new AngleDeltas(3, context);
    for (int i = 0; i < 5; i++) {
      AngleDeltas inc = new AngleDeltas(3, context);
      inc.storage[0] = 1.0;
      base.add(inc);
    }
    assertEquals(5.0, base.storage[0], DELTA);
  }
}
