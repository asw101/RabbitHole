package org.lgna.story;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class SMarkerFacadeColorTest {
  @Test
  public void colorIdRoundTripsThroughMarkerImplementation() {
    SThingMarker marker = new SThingMarker();

    marker.setColorId(Color.MAGENTA);

    assertEquals(Color.MAGENTA, marker.getColorId());
    assertEquals(Color.MAGENTA, marker.getImplementation().paint.getValue());
  }

  @Test
  public void setVehicleDelegatesToMarkerImplementation() {
    SThingMarker marker = new SThingMarker();
    SBox vehicle = new SBox();

    marker.setVehicle(vehicle);

    assertSame(vehicle, marker.getVehicle());
    assertSame(vehicle.getImplementation(), marker.getImplementation().getVehicle());
  }

  @Test
  public void setVehicleNullClearsImplementationVehicle() {
    SThingMarker marker = new SThingMarker();
    marker.setVehicle(new SBox());

    marker.setVehicle(null);

    assertNull(marker.getVehicle());
    assertNull(marker.getImplementation().getVehicle());
  }
}
