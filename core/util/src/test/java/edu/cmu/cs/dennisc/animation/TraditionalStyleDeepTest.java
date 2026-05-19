package edu.cmu.cs.dennisc.animation;

import org.junit.Test;

import static org.junit.Assert.*;

public class TraditionalStyleDeepTest {

  @Test
  public void enumHasFourValues() {
    assertEquals(4, TraditionalStyle.values().length);
  }

  @Test
  public void beginAndEndAbruptly_slowInFalse_slowOutFalse() {
    assertFalse(TraditionalStyle.BEGIN_AND_END_ABRUPTLY.isSlowInDesired());
    assertFalse(TraditionalStyle.BEGIN_AND_END_ABRUPTLY.isSlowOutDesired());
  }

  @Test
  public void beginGentlyAndEndAbruptly_slowInTrue_slowOutFalse() {
    assertTrue(TraditionalStyle.BEGIN_GENTLY_AND_END_ABRUPTLY.isSlowInDesired());
    assertFalse(TraditionalStyle.BEGIN_GENTLY_AND_END_ABRUPTLY.isSlowOutDesired());
  }

  @Test
  public void beginAbruptlyAndEndGently_slowInFalse_slowOutTrue() {
    assertFalse(TraditionalStyle.BEGIN_ABRUPTLY_AND_END_GENTLY.isSlowInDesired());
    assertTrue(TraditionalStyle.BEGIN_ABRUPTLY_AND_END_GENTLY.isSlowOutDesired());
  }

  @Test
  public void beginAndEndGently_slowInTrue_slowOutTrue() {
    assertTrue(TraditionalStyle.BEGIN_AND_END_GENTLY.isSlowInDesired());
    assertTrue(TraditionalStyle.BEGIN_AND_END_GENTLY.isSlowOutDesired());
  }

  @Test
  public void abruptly_calculatePortion_isLinear() {
    assertEquals(0.5, TraditionalStyle.BEGIN_AND_END_ABRUPTLY.calculatePortion(0.5, 1.0), 1e-10);
  }

  @Test
  public void abruptly_calculatePortion_atZero() {
    assertEquals(0.0, TraditionalStyle.BEGIN_AND_END_ABRUPTLY.calculatePortion(0.0, 1.0), 1e-10);
  }

  @Test
  public void abruptly_calculatePortion_atOne() {
    assertEquals(1.0, TraditionalStyle.BEGIN_AND_END_ABRUPTLY.calculatePortion(1.0, 1.0), 1e-10);
  }

  @Test
  public void abruptly_calculatePortion_quarter() {
    assertEquals(0.25, TraditionalStyle.BEGIN_AND_END_ABRUPTLY.calculatePortion(0.25, 1.0), 1e-10);
  }

  @Test
  public void abruptly_calculatePortion_negativeTimeStaysLinear() {
    assertEquals(-0.5, TraditionalStyle.BEGIN_AND_END_ABRUPTLY.calculatePortion(-0.5, 1.0), 1e-10);
  }

  @Test
  public void abruptly_calculatePortion_afterEndStaysLinear() {
    assertEquals(1.5, TraditionalStyle.BEGIN_AND_END_ABRUPTLY.calculatePortion(1.5, 1.0), 1e-10);
  }

  @Test
  public void gently_calculatePortion_atZero() {
    assertEquals(0.0, TraditionalStyle.BEGIN_AND_END_GENTLY.calculatePortion(0.0, 1.0), 0.01);
  }

  @Test
  public void gently_calculatePortion_atEnd() {
    assertEquals(1.0, TraditionalStyle.BEGIN_AND_END_GENTLY.calculatePortion(1.0, 1.0), 0.01);
  }

  @Test
  public void gently_calculatePortion_midRange() {
    double portion = TraditionalStyle.BEGIN_AND_END_GENTLY.calculatePortion(0.5, 1.0);

    assertTrue(portion > 0.0 && portion < 1.0);
  }

  @Test
  public void beginGently_calculatePortion_atZero() {
    assertEquals(0.0, TraditionalStyle.BEGIN_GENTLY_AND_END_ABRUPTLY.calculatePortion(0.0, 1.0), 0.01);
  }

  @Test
  public void beginGently_calculatePortion_atEnd() {
    assertEquals(1.0, TraditionalStyle.BEGIN_GENTLY_AND_END_ABRUPTLY.calculatePortion(1.0, 1.0), 0.01);
  }

  @Test
  public void endGently_calculatePortion_atZero() {
    assertEquals(0.0, TraditionalStyle.BEGIN_ABRUPTLY_AND_END_GENTLY.calculatePortion(0.0, 1.0), 0.01);
  }

  @Test
  public void endGently_calculatePortion_atEnd() {
    assertEquals(1.0, TraditionalStyle.BEGIN_ABRUPTLY_AND_END_GENTLY.calculatePortion(1.0, 1.0), 0.01);
  }

  @Test
  public void allStyles_portionIncreasesMonotonically() {
    for (TraditionalStyle style : TraditionalStyle.values()) {
      double previous = -1.0;
      for (int i = 0; i <= 10; i++) {
        double t = i / 10.0;
        double portion = style.calculatePortion(t, 1.0);
        assertTrue(style.name() + " at t=" + t + ": " + portion + " not >= " + previous, portion >= previous - 0.001);
        previous = portion;
      }
    }
  }

  @Test
  public void calculatePortion_zeroTotal_returnsOne() {
    for (TraditionalStyle style : TraditionalStyle.values()) {
      assertEquals(1.0, style.calculatePortion(0.5, 0.0), 1e-10);
    }
  }

  @Test
  public void calculatePortion_longDuration() {
    double portion = TraditionalStyle.BEGIN_AND_END_GENTLY.calculatePortion(5.0, 10.0);

    assertTrue(portion > 0.0 && portion < 1.0);
  }

  @Test
  public void values_areInDeclarationOrder() {
    TraditionalStyle[] values = TraditionalStyle.values();

    assertSame(TraditionalStyle.BEGIN_AND_END_ABRUPTLY, values[0]);
    assertSame(TraditionalStyle.BEGIN_GENTLY_AND_END_ABRUPTLY, values[1]);
    assertSame(TraditionalStyle.BEGIN_ABRUPTLY_AND_END_GENTLY, values[2]);
    assertSame(TraditionalStyle.BEGIN_AND_END_GENTLY, values[3]);
  }

  @Test
  public void valueOf_returnsCorrectInstance() {
    assertSame(TraditionalStyle.BEGIN_AND_END_GENTLY, TraditionalStyle.valueOf("BEGIN_AND_END_GENTLY"));
  }

  @Test
  public void implementsStyle() {
    for (TraditionalStyle style : TraditionalStyle.values()) {
      assertTrue(style instanceof Style);
    }
  }
}
