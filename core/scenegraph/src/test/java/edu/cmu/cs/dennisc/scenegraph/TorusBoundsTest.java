package edu.cmu.cs.dennisc.scenegraph;

import edu.cmu.cs.dennisc.scenegraph.event.BoundEvent;
import edu.cmu.cs.dennisc.scenegraph.event.BoundListener;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static edu.cmu.cs.dennisc.scenegraph.ScenegraphTestAssertions.assertPointEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TorusBoundsTest {

  @Test
  public void defaultTorusBoundsUseXZPlane() {
    Torus torus = new Torus();
    // defaults: majorRadius=0.9, minorRadius=0.1, plane=XZ
    double yesRadius = 0.9 + 0.1;  // 1.0
    double noRadius = 0.1;

    AxisAlignedBox bbox = torus.getAxisAlignedMinimumBoundingBox();
    assertNotNull(bbox);
    // XZ plane: thin on Y
    assertPointEquals("min", new Point3(-yesRadius, -noRadius, -yesRadius), bbox.minimum());
    assertPointEquals("max", new Point3(yesRadius, noRadius, yesRadius), bbox.maximum());
  }

  @Test
  public void xyPlaneBoundsHaveThinZ() {
    Torus torus = new Torus();
    torus.majorRadius.setValue(2.0);
    torus.minorRadius.setValue(0.5);
    torus.coordinatePlane.setValue(Torus.CoordinatePlane.XY);

    double yesRadius = 2.5;
    double noRadius = 0.5;

    AxisAlignedBox bbox = torus.getAxisAlignedMinimumBoundingBox();
    assertNotNull(bbox);
    assertPointEquals("min", new Point3(-yesRadius, -yesRadius, -noRadius), bbox.minimum());
    assertPointEquals("max", new Point3(yesRadius, yesRadius, noRadius), bbox.maximum());
  }

  @Test
  public void yzPlaneBoundsHaveThinX() {
    Torus torus = new Torus();
    torus.majorRadius.setValue(2.0);
    torus.minorRadius.setValue(0.5);
    torus.coordinatePlane.setValue(Torus.CoordinatePlane.YZ);

    double yesRadius = 2.5;
    double noRadius = 0.5;

    AxisAlignedBox bbox = torus.getAxisAlignedMinimumBoundingBox();
    assertNotNull(bbox);
    assertPointEquals("min", new Point3(-noRadius, -yesRadius, -yesRadius), bbox.minimum());
    assertPointEquals("max", new Point3(noRadius, yesRadius, yesRadius), bbox.maximum());
  }

  @Test
  public void xzPlaneBoundsHaveThinY() {
    Torus torus = new Torus();
    torus.majorRadius.setValue(3.0);
    torus.minorRadius.setValue(1.0);
    torus.coordinatePlane.setValue(Torus.CoordinatePlane.XZ);

    double yesRadius = 4.0;
    double noRadius = 1.0;

    AxisAlignedBox bbox = torus.getAxisAlignedMinimumBoundingBox();
    assertNotNull(bbox);
    assertPointEquals("min", new Point3(-yesRadius, -noRadius, -yesRadius), bbox.minimum());
    assertPointEquals("max", new Point3(yesRadius, noRadius, yesRadius), bbox.maximum());
  }

  @Test
  public void changingCoordinatePlaneDirtiesBoundsAndFiresEvent() {
    Torus torus = new Torus();
    AxisAlignedBox oldBounds = torus.getAxisAlignedMinimumBoundingBox();

    AtomicInteger events = new AtomicInteger();
    torus.addBoundListener((BoundEvent e) -> events.incrementAndGet());

    torus.coordinatePlane.setValue(Torus.CoordinatePlane.YZ);

    assertTrue("BoundListener should have fired", events.get() >= 1);
    AxisAlignedBox newBounds = torus.getAxisAlignedMinimumBoundingBox();
    assertNotNull(newBounds);
  }

  @Test
  public void changingMajorRadiusUpdatesBounds() {
    Torus torus = new Torus();
    AxisAlignedBox oldBounds = torus.getAxisAlignedMinimumBoundingBox();

    torus.majorRadius.setValue(5.0);
    AxisAlignedBox newBounds = torus.getAxisAlignedMinimumBoundingBox();

    double expectedYes = 5.0 + 0.1;
    assertPointEquals("min", new Point3(-expectedYes, -0.1, -expectedYes), newBounds.minimum());
    assertPointEquals("max", new Point3(expectedYes, 0.1, expectedYes), newBounds.maximum());
  }

  @Test
  public void changingMinorRadiusUpdatesBounds() {
    Torus torus = new Torus();
    torus.minorRadius.setValue(0.5);

    AxisAlignedBox bbox = torus.getAxisAlignedMinimumBoundingBox();
    double expectedYes = 0.9 + 0.5;
    double expectedNo = 0.5;
    assertPointEquals("min", new Point3(-expectedYes, -expectedNo, -expectedYes), bbox.minimum());
    assertPointEquals("max", new Point3(expectedYes, expectedNo, expectedYes), bbox.maximum());
  }

}
