package edu.cmu.cs.dennisc.clock;

import org.junit.Test;

import static org.junit.Assert.*;

public class ClockTest {

  @Test
  public void getCurrentTime_returnsNonNegative() {
    assertTrue(Clock.getCurrentTime() >= 0.0);
  }

  @Test
  public void getCurrentTime_monotonicallyIncreases() throws Exception {
    double first = Clock.getCurrentTime();

    Thread.sleep(10);

    assertTrue(Clock.getCurrentTime() >= first);
  }

  @Test
  public void getCurrentTime_returnsReasonableValue() {
    assertTrue(Clock.getCurrentTime() < 3600.0);
  }

  @Test
  public void getCurrentTime_inSeconds() throws Exception {
    double first = Clock.getCurrentTime();

    Thread.sleep(100);

    double delta = Clock.getCurrentTime() - first;
    assertTrue(delta >= 0.05 && delta < 1.0);
  }

  @Test
  public void getCurrentTime_calledTwice_doesNotThrow() {
    Clock.getCurrentTime();
    Clock.getCurrentTime();
  }

  @Test
  public void getCurrentTime_isFinite() {
    assertTrue(Double.isFinite(Clock.getCurrentTime()));
  }

  @Test
  public void clockClass_isAbstract() {
    assertTrue(java.lang.reflect.Modifier.isAbstract(Clock.class.getModifiers()));
  }

  @Test
  public void repeatedCalls_neverMoveBackward() {
    double previous = Clock.getCurrentTime();

    for (int i = 0; i < 10; i++) {
      double current = Clock.getCurrentTime();
      assertTrue(current >= previous);
      previous = current;
    }
  }
}
