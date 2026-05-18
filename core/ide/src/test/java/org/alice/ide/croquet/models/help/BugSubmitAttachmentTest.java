package org.alice.ide.croquet.models.help;

import org.junit.Test;
import static org.junit.Assert.*;

public class BugSubmitAttachmentTest {

  @Test
  public void values_hasTwoConstants() {
    BugSubmitAttachment[] values = BugSubmitAttachment.values();
    assertEquals(2, values.length);
  }

  @Test
  public void valueOf_roundTrips() {
    for (BugSubmitAttachment a : BugSubmitAttachment.values()) {
      assertEquals(a, BugSubmitAttachment.valueOf(a.name()));
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void valueOf_invalid_throws() {
    BugSubmitAttachment.valueOf("MAYBE");
  }
}
