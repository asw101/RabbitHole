package org.alice.math.immutable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AngleTest {

  private static final double EPSILON = 1e-10;

  @Test
  void conversions_are_consistent_across_units() {
    Angle angle = new AngleInDegrees(180.0);

    assertEquals(Math.PI, angle.getAsRadians(), EPSILON);
    assertEquals(180.0, angle.getAsDegrees(), EPSILON);
    assertEquals(0.5, angle.getAsRevolutions(), EPSILON);
    assertTrue(angle.isCloseTo(new AngleInRadians(Math.PI)));
    assertTrue(angle.isCloseTo(new AngleInRevolutions(0.5)));
  }

  @Test
  void zero_and_nan_flags_follow_underlying_values() {
    assertTrue(Angle.ZERO.isZero());
    assertFalse(new AngleInRadians(0.25).isZero());
    assertTrue(Angle.NaN.isNaN());
    assertFalse(new AngleInDegrees(90.0).isNaN());
  }

  @Test
  void minus_zero_returns_same_instance() {
    Angle angle = new AngleInDegrees(45.0);

    assertSame(angle, angle.minus(Angle.ZERO));
  }

  @Test
  void negated_times_and_interpolate_preserve_unit_type() {
    AngleInRadians radians = new AngleInRadians(Math.PI / 4.0);
    AngleInRevolutions revolutions = new AngleInRevolutions(0.25);

    assertEquals(-Math.PI / 4.0, radians.negated().getAsRadians(), EPSILON);
    assertEquals(0.5, revolutions.times(2.0).getAsRevolutions(), EPSILON);
    assertEquals(90.0, new AngleInDegrees(0.0).interpolateToward(new AngleInDegrees(180.0), 0.5).getAsDegrees(), EPSILON);
  }

  @Test
  void toNearestPi_and_toNearest_use_closest_multiple() {
    assertEquals(Math.PI, new AngleInRadians(0.75 * Math.PI).toNearestPi().getAsRadians(), EPSILON);
    assertEquals(-Math.PI, new AngleInRadians(-0.6 * Math.PI).toNearestPi().getAsRadians(), EPSILON);
    assertEquals(90.0, new AngleInDegrees(135.0).toNearest(new AngleInDegrees(90.0)).getAsDegrees(), EPSILON);
  }
}
