package org.alice.stageide.sceneeditor.side;

import org.junit.Test;
import static org.junit.Assert.*;

public class PropertyAndMarkerPanelOptionsTest {

  @Test
  public void values_hasTwoConstants() {
    PropertyAndMarkerPanelOptions[] values = PropertyAndMarkerPanelOptions.values();
    assertEquals(2, values.length);
  }

  @Test
  public void valueOf_roundTrips() {
    for (PropertyAndMarkerPanelOptions opt : PropertyAndMarkerPanelOptions.values()) {
      assertEquals(opt, PropertyAndMarkerPanelOptions.valueOf(opt.name()));
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void valueOf_invalid_throws() {
    PropertyAndMarkerPanelOptions.valueOf("INVALID");
  }
}
