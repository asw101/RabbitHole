package org.alice.ide;

import org.junit.Test;
import static org.junit.Assert.*;

public class ReasonToDisableSomeAmountOfRenderingTest {

  @Test
  public void values_hasThreeConstants() {
    ReasonToDisableSomeAmountOfRendering[] values = ReasonToDisableSomeAmountOfRendering.values();
    assertEquals(3, values.length);
    assertEquals(ReasonToDisableSomeAmountOfRendering.MODAL_DIALOG_WITH_RENDER_WINDOW_OF_ITS_OWN, values[0]);
    assertEquals(ReasonToDisableSomeAmountOfRendering.DRAG_AND_DROP, values[1]);
    assertEquals(ReasonToDisableSomeAmountOfRendering.CLICK_AND_CLACK, values[2]);
  }

  @Test
  public void valueOf_roundTrips() {
    for (ReasonToDisableSomeAmountOfRendering r : ReasonToDisableSomeAmountOfRendering.values()) {
      assertEquals(r, ReasonToDisableSomeAmountOfRendering.valueOf(r.name()));
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void valueOf_invalidName_throws() {
    ReasonToDisableSomeAmountOfRendering.valueOf("INVALID");
  }
}
