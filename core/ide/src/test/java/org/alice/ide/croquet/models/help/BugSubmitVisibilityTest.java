package org.alice.ide.croquet.models.help;

import org.junit.Test;
import static org.junit.Assert.*;

public class BugSubmitVisibilityTest {

  @Test
  public void values_hasTwoConstants() {
    BugSubmitVisibility[] values = BugSubmitVisibility.values();
    assertEquals(2, values.length);
    assertSame(BugSubmitVisibility.PUBLIC, values[0]);
    assertSame(BugSubmitVisibility.PRIVATE, values[1]);
  }

  @Test
  public void valueOf_roundTrips() {
    for (BugSubmitVisibility v : BugSubmitVisibility.values()) {
      assertEquals(v, BugSubmitVisibility.valueOf(v.name()));
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void valueOf_invalid_throws() {
    BugSubmitVisibility.valueOf("UNKNOWN");
  }
}
