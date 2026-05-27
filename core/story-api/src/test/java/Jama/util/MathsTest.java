package Jama.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MathsTest {
  private static final double TOLERANCE = 1.0e-10;

  @Test
  public void hypotMatchesPythagoreanDistanceForRepresentativeInputs() {
    assertEquals(5.0, Maths.hypot(3.0, 4.0), TOLERANCE);
    assertEquals(13.0, Maths.hypot(-5.0, 12.0), TOLERANCE);
  }

  @Test
  public void hypotAvoidsOverflowForLargeInputs() {
    double value = Maths.hypot(3.0e200, 4.0e200);

    assertEquals(5.0e200, value, 1.0e186);
  }

  @Test
  public void hypotReturnsZeroWhenBothInputsAreZero() {
    assertEquals(0.0, Maths.hypot(0.0, 0.0), 0.0);
  }
}
