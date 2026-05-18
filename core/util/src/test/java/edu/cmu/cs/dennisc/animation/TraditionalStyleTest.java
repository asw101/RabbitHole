package edu.cmu.cs.dennisc.animation;

import org.junit.Test;

import static org.junit.Assert.*;

public class TraditionalStyleTest {
  private static final double EPSILON = 1.0e-6;

  @Test
  public void abruptStyleHasNoSlowFlags() {
    assertFalse(TraditionalStyle.BEGIN_AND_END_ABRUPTLY.isSlowInDesired());
    assertFalse(TraditionalStyle.BEGIN_AND_END_ABRUPTLY.isSlowOutDesired());
  }

  @Test
  public void slowInStyleOnlyRequestsSlowIn() {
    assertTrue(TraditionalStyle.BEGIN_GENTLY_AND_END_ABRUPTLY.isSlowInDesired());
    assertFalse(TraditionalStyle.BEGIN_GENTLY_AND_END_ABRUPTLY.isSlowOutDesired());
  }

  @Test
  public void slowOutStyleOnlyRequestsSlowOut() {
    assertFalse(TraditionalStyle.BEGIN_ABRUPTLY_AND_END_GENTLY.isSlowInDesired());
    assertTrue(TraditionalStyle.BEGIN_ABRUPTLY_AND_END_GENTLY.isSlowOutDesired());
  }

  @Test
  public void slowInAndSlowOutStyleRequestsBoth() {
    assertTrue(TraditionalStyle.BEGIN_AND_END_GENTLY.isSlowInDesired());
    assertTrue(TraditionalStyle.BEGIN_AND_END_GENTLY.isSlowOutDesired());
  }

  @Test
  public void zeroDurationAlwaysReturnsOne() {
    for (TraditionalStyle style : TraditionalStyle.values()) {
      assertEquals(1.0, style.calculatePortion(5.0, 0.0), EPSILON);
    }
  }

  @Test
  public void allStylesStartAtZeroForPositiveDuration() {
    for (TraditionalStyle style : TraditionalStyle.values()) {
      assertEquals(0.0, style.calculatePortion(0.0, 10.0), EPSILON);
    }
  }

  @Test
  public void allStylesEndAtOneForPositiveDuration() {
    for (TraditionalStyle style : TraditionalStyle.values()) {
      assertEquals(1.0, style.calculatePortion(10.0, 10.0), EPSILON);
    }
  }

  @Test
  public void abruptStyleIsLinear() {
    assertEquals(0.5, TraditionalStyle.BEGIN_AND_END_ABRUPTLY.calculatePortion(5.0, 10.0), EPSILON);
  }

  @Test
  public void slowInStyleBeginsMoreSlowlyThanLinear() {
    double portion = TraditionalStyle.BEGIN_GENTLY_AND_END_ABRUPTLY.calculatePortion(2.5, 10.0);

    assertTrue(portion > 0.0);
    assertTrue(portion < 0.25);
  }

  @Test
  public void slowOutStyleBeginsMoreQuicklyThanLinear() {
    double portion = TraditionalStyle.BEGIN_ABRUPTLY_AND_END_GENTLY.calculatePortion(2.5, 10.0);

    assertTrue(portion > 0.25);
    assertTrue(portion < 1.0);
  }

  @Test
  public void gentleAtBothEndsSoftensMidpointComparedToLinear() {
    double portion = TraditionalStyle.BEGIN_AND_END_GENTLY.calculatePortion(5.0, 10.0);

    assertEquals(0.4666666666666667, portion, EPSILON);
    assertTrue(portion < 0.5);
  }
}
