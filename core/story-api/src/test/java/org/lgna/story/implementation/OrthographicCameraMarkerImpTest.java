package org.lgna.story.implementation;

import org.alice.math.immutable.ClippedZPlane;
import org.junit.Test;
import org.lgna.story.OrthographicCameraMarker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class OrthographicCameraMarkerImpTest {
  @Test
  public void orthographicCameraMarkerTracksPicturePlaneWithoutCreatingVisuals() {
    OrthographicCameraMarker marker = new OrthographicCameraMarker();
    OrthographicCameraMarkerImp implementation = marker.getImplementation();
    ClippedZPlane picturePlane = new ClippedZPlane(1.0, 2.0);

    assertSame(marker, implementation.getAbstraction());
    assertSame(ClippedZPlane.DEFAULT, implementation.getPicturePlane());

    implementation.setPicturePlane(picturePlane);

    assertSame(picturePlane, implementation.getPicturePlane());
    assertEquals(0, implementation.getSgVisuals().length);
    assertEquals(0, implementation.getSgPaintAppearances().length);
    assertEquals(0, implementation.getSgOpacityAppearances().length);
  }
}
