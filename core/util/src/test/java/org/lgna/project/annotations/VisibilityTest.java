package org.lgna.project.annotations;

import org.junit.Test;

import static org.junit.Assert.*;

public class VisibilityTest {

  @Test
  public void values_containsThreeEntries() {
    Visibility[] values = Visibility.values();
    assertEquals(3, values.length);
  }

  @Test
  public void primeTime_exists() {
    Visibility v = Visibility.PRIME_TIME;
    assertEquals("PRIME_TIME", v.name());
    assertEquals(0, v.ordinal());
  }

  @Test
  public void tuckedAway_exists() {
    Visibility v = Visibility.TUCKED_AWAY;
    assertEquals("TUCKED_AWAY", v.name());
    assertEquals(1, v.ordinal());
  }

  @Test
  public void completelyHidden_exists() {
    Visibility v = Visibility.COMPLETELY_HIDDEN;
    assertEquals("COMPLETELY_HIDDEN", v.name());
    assertEquals(2, v.ordinal());
  }

  @Test
  public void valueOf_roundTrips() {
    for (Visibility v : Visibility.values()) {
      assertSame(v, Visibility.valueOf(v.name()));
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void valueOf_invalid_throws() {
    Visibility.valueOf("NONEXISTENT");
  }

  @Test
  public void ordinalOrdering() {
    assertTrue(Visibility.PRIME_TIME.ordinal() < Visibility.TUCKED_AWAY.ordinal());
    assertTrue(Visibility.TUCKED_AWAY.ordinal() < Visibility.COMPLETELY_HIDDEN.ordinal());
  }
}
