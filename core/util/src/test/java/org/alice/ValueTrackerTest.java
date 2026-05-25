package org.alice;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ValueTrackerTest {
  @Test
  public void passThroughReturnsLatestValueAndResetDoesNothing() {
    ValueTracker tracker = ValueTracker.PASS_THROUGH;

    assertEquals(3, tracker.check(3));
    tracker.reset();
    assertEquals(-5, tracker.check(-5));
  }

  @Test
  public void maximumTrackerKeepsHighestValueUntilReset() {
    ValueTracker.MaximumTracker tracker = new ValueTracker.MaximumTracker();

    assertEquals(4, tracker.check(4));
    assertEquals(4, tracker.check(2));
    assertEquals(7, tracker.check(7));

    tracker.reset();

    assertEquals(1, tracker.check(1));
  }
}
