package edu.cmu.cs.dennisc.math;

import org.junit.Test;

import static org.junit.Assert.*;

public class EpsilonUtilitiesExtendedTest {

  @Test
  public void isWithinEpsilon_exactlyEqual() {
    assertTrue(EpsilonUtilities.isWithinReasonableEpsilon(1.0, 1.0));
  }

  @Test
  public void isWithinEpsilon_veryClose() {
    assertTrue(EpsilonUtilities.isWithinReasonableEpsilon(1.0, 1.0 + 1e-15));
  }

  @Test
  public void isWithinEpsilon_notClose() {
    assertFalse(EpsilonUtilities.isWithinReasonableEpsilon(1.0, 2.0));
  }

  @Test
  public void isWithinEpsilon_zero() {
    assertTrue(EpsilonUtilities.isWithinReasonableEpsilon(0.0, 0.0));
  }

  @Test
  public void isWithinEpsilon_negatives() {
    assertTrue(EpsilonUtilities.isWithinReasonableEpsilon(-1.0, -1.0));
    assertFalse(EpsilonUtilities.isWithinReasonableEpsilon(-1.0, 1.0));
  }

  @Test
  public void isWithinReasonableEpsilon_customValues() {
    assertTrue(EpsilonUtilities.isWithinReasonableEpsilon(Math.PI, Math.PI));
    assertTrue(EpsilonUtilities.isWithinReasonableEpsilon(Math.E, Math.E));
  }

  @Test
  public void isWithinReasonableEpsilon_symmetry() {
    double a = 1.0;
    double b = 1.0 + 1e-12;
    assertEquals(
        EpsilonUtilities.isWithinReasonableEpsilon(a, b),
        EpsilonUtilities.isWithinReasonableEpsilon(b, a));
  }

  @Test
  public void isWithinReasonableEpsilon_largeNumbers() {
    double a = 1e10;
    double b = 1e10 + 0.001;
    assertTrue(EpsilonUtilities.isWithinReasonableEpsilon(a, b));
  }
}
