package org.lgna.story.implementation;

import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Vector3;
import org.junit.Test;
import org.lgna.story.SThingMarker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ObjectMarkerImpSpatialTest {

  @Test
  public void translationUpdatesTheMarkerLocalPosition() {
    ObjectMarkerImp marker = new SThingMarker().getImplementation();

    marker.setLocalTransformation(AffineMatrix4x4.createTranslation(1, 2, 3));

    Point3 position = marker.getLocalPosition();
    assertEquals(1.0, position.x(), 1e-6);
    assertEquals(2.0, position.y(), 1e-6);
    assertEquals(3.0, position.z(), 1e-6);
  }

  @Test
  public void rotationChangesOrientationWithoutChangingPosition() {
    ObjectMarkerImp marker = new SThingMarker().getImplementation();
    marker.setLocalTransformation(AffineMatrix4x4.createTranslation(1, 2, 3));

    marker.applyRotationInRadians(new Vector3(0, 1, 0), Math.PI / 2.0, marker);

    assertFalse(marker.getLocalOrientation().isIdentity());
    Point3 position = marker.getLocalPosition();
    assertEquals(1.0, position.x(), 1e-6);
    assertEquals(2.0, position.y(), 1e-6);
    assertEquals(3.0, position.z(), 1e-6);
  }

  @Test
  public void fourQuarterTurnsReturnTheMarkerToIdentityOrientation() {
    ObjectMarkerImp marker = new SThingMarker().getImplementation();

    for (int i = 0; i < 4; i++) {
      marker.applyRotationInRadians(new Vector3(0, 1, 0), Math.PI / 2.0, marker);
    }

    assertTrue(marker.getLocalOrientation().isIdentity());
  }
}
