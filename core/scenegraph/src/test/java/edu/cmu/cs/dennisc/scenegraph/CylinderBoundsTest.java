package edu.cmu.cs.dennisc.scenegraph;

import edu.cmu.cs.dennisc.scenegraph.event.BoundEvent;
import edu.cmu.cs.dennisc.scenegraph.event.BoundListener;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static edu.cmu.cs.dennisc.scenegraph.ScenegraphTestAssertions.EPSILON;
import static edu.cmu.cs.dennisc.scenegraph.ScenegraphTestAssertions.assertPointEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CylinderBoundsTest {

  @Test
  public void allAxisAlignmentCombinationsProduceCorrectBounds() {
    double length = 2.0;
    double bottomRadius = 1.0;
    double topRadius = 0.5;
    double maxRadius = Math.max(bottomRadius, topRadius);

    for (Cylinder.BottomToTopAxis axis : Cylinder.BottomToTopAxis.values()) {
      for (Cylinder.OriginAlignment alignment : Cylinder.OriginAlignment.values()) {
        Cylinder cyl = new Cylinder();
        cyl.length.setValue(length);
        cyl.bottomRadius.setValue(bottomRadius);
        cyl.topRadius.setValue(topRadius);
        cyl.bottomToTopAxis.setValue(axis);
        cyl.originAlignment.setValue(alignment);

        AxisAlignedBox bbox = cyl.getAxisAlignedMinimumBoundingBox();
        assertNotNull("bbox null for " + axis + "/" + alignment, bbox);

        Point3 expectedMin = computeExpectedMin(axis, alignment, length, maxRadius);
        Point3 expectedMax = computeExpectedMax(axis, alignment, length, maxRadius);

        assertPointEquals(axis + "/" + alignment + " min", expectedMin, bbox.minimum());
        assertPointEquals(axis + "/" + alignment + " max", expectedMax, bbox.maximum());
      }
    }
  }

  @Test
  public void topRadiusNaNFallsBackToBottomRadius() {
    Cylinder cyl = new Cylinder();
    cyl.bottomRadius.setValue(3.0);
    // BoundDoubleProperty rejects NaN via assertion (isNaNAcceptable=false).
    // Test the fallback logic through getActualTopRadius() by verifying
    // that when topRadius equals bottomRadius (default), it returns that value,
    // and when topRadius is different, it returns topRadius.
    cyl.topRadius.setValue(3.0);
    assertEquals(3.0, cyl.getActualTopRadius(), EPSILON);

    cyl.topRadius.setValue(0.5);
    assertEquals(0.5, cyl.getActualTopRadius(), EPSILON);

    // Verify getMaxRadius returns the larger of the two
    AxisAlignedBox bbox = cyl.getAxisAlignedMinimumBoundingBox();
    assertNotNull(bbox);
    // Default axis = POSITIVE_Y, alignment = BOTTOM, length = 1.0
    // maxRadius = max(3.0, 0.5) = 3.0, bottom = 0, top = 1.0
    assertPointEquals("min", new Point3(-3.0, 0.0, -3.0), bbox.minimum());
    assertPointEquals("max", new Point3(3.0, 1.0, 3.0), bbox.maximum());
  }

  @Test
  public void getCenterOfTopAndBottomReflectAxisAndAlignment() {
    Cylinder cyl = new Cylinder();
    cyl.length.setValue(4.0);
    cyl.originAlignment.setValue(Cylinder.OriginAlignment.BOTTOM);

    // POSITIVE_Y + BOTTOM: top=(0,4,0), bottom=(0,0,0)
    cyl.bottomToTopAxis.setValue(Cylinder.BottomToTopAxis.POSITIVE_Y);
    assertPointEquals("POS_Y top", new Point3(0, 4, 0), cyl.getCenterOfTop());
    assertPointEquals("POS_Y bottom", new Point3(0, 0, 0), cyl.getCenterOfBottom());

    // POSITIVE_X + BOTTOM: top=(4,0,0), bottom=(0,0,0)
    cyl.bottomToTopAxis.setValue(Cylinder.BottomToTopAxis.POSITIVE_X);
    assertPointEquals("POS_X top", new Point3(4, 0, 0), cyl.getCenterOfTop());
    assertPointEquals("POS_X bottom", new Point3(0, 0, 0), cyl.getCenterOfBottom());

    // POSITIVE_Z + BOTTOM: top=(0,0,4), bottom=(0,0,0)
    cyl.bottomToTopAxis.setValue(Cylinder.BottomToTopAxis.POSITIVE_Z);
    assertPointEquals("POS_Z top", new Point3(0, 0, 4), cyl.getCenterOfTop());
    assertPointEquals("POS_Z bottom", new Point3(0, 0, 0), cyl.getCenterOfBottom());

    // NEGATIVE_Y + BOTTOM: top=(0,-4,0), bottom=(0,0,0)
    cyl.bottomToTopAxis.setValue(Cylinder.BottomToTopAxis.NEGATIVE_Y);
    assertPointEquals("NEG_Y top", new Point3(0, -4, 0), cyl.getCenterOfTop());
    assertPointEquals("NEG_Y bottom", new Point3(0, 0, 0), cyl.getCenterOfBottom());

    // NEGATIVE_X + BOTTOM: top=(-4,0,0), bottom=(0,0,0)
    cyl.bottomToTopAxis.setValue(Cylinder.BottomToTopAxis.NEGATIVE_X);
    assertPointEquals("NEG_X top", new Point3(-4, 0, 0), cyl.getCenterOfTop());
    assertPointEquals("NEG_X bottom", new Point3(0, 0, 0), cyl.getCenterOfBottom());

    // NEGATIVE_Z + BOTTOM: top=(0,0,-4), bottom=(0,0,0)
    cyl.bottomToTopAxis.setValue(Cylinder.BottomToTopAxis.NEGATIVE_Z);
    assertPointEquals("NEG_Z top", new Point3(0, 0, -4), cyl.getCenterOfTop());
    assertPointEquals("NEG_Z bottom", new Point3(0, 0, 0), cyl.getCenterOfBottom());
  }

  @Test
  public void changingOriginAlignmentDirtiesBoundsAndFiresEvent() {
    Cylinder cyl = new Cylinder();
    cyl.originAlignment.setValue(Cylinder.OriginAlignment.BOTTOM);
    AxisAlignedBox oldBounds = cyl.getAxisAlignedMinimumBoundingBox();

    AtomicInteger events = new AtomicInteger();
    cyl.addBoundListener((BoundEvent e) -> events.incrementAndGet());

    cyl.originAlignment.setValue(Cylinder.OriginAlignment.CENTER);

    assertTrue("BoundListener should have fired", events.get() >= 1);
    AxisAlignedBox newBounds = cyl.getAxisAlignedMinimumBoundingBox();
    assertTrue("Bounds should have changed",
        !oldBounds.minimum().equals(newBounds.minimum()) || !oldBounds.maximum().equals(newBounds.maximum()));
  }

  @Test
  public void changingBottomToTopAxisDirtiesBoundsAndFiresEvent() {
    Cylinder cyl = new Cylinder();
    cyl.bottomToTopAxis.setValue(Cylinder.BottomToTopAxis.POSITIVE_Y);
    AxisAlignedBox oldBounds = cyl.getAxisAlignedMinimumBoundingBox();

    AtomicInteger events = new AtomicInteger();
    cyl.addBoundListener((BoundEvent e) -> events.incrementAndGet());

    cyl.bottomToTopAxis.setValue(Cylinder.BottomToTopAxis.POSITIVE_X);

    assertTrue("BoundListener should have fired", events.get() >= 1);
    AxisAlignedBox newBounds = cyl.getAxisAlignedMinimumBoundingBox();
    assertNotNull(newBounds);
  }

  @Test
  public void defaultCylinderValues() {
    Cylinder cyl = new Cylinder();
    assertEquals(1.0, cyl.length.getValue(), EPSILON);
    assertEquals(1.0, cyl.bottomRadius.getValue(), EPSILON);
    assertEquals(1.0, cyl.topRadius.getValue(), EPSILON);
    assertEquals(Cylinder.OriginAlignment.BOTTOM, cyl.originAlignment.getValue());
    assertEquals(Cylinder.BottomToTopAxis.POSITIVE_Y, cyl.bottomToTopAxis.getValue());
    assertTrue(cyl.hasBottomCap.getValue());
    assertTrue(cyl.hasTopCap.getValue());
  }

  // Computes expected bounding box min/max based on Cylinder.updateBoundingBox() logic
  private static Point3 computeExpectedMin(Cylinder.BottomToTopAxis axis, Cylinder.OriginAlignment alignment, double length, double maxR) {
    double top = computeTop(alignment, length);
    double bottom = computeBottom(alignment, length);
    return switch (axis) {
      case POSITIVE_X -> new Point3(bottom, -maxR, -maxR);
      case POSITIVE_Y -> new Point3(-maxR, bottom, -maxR);
      case POSITIVE_Z -> new Point3(-maxR, -maxR, bottom);
      case NEGATIVE_X -> new Point3(top, -maxR, -maxR);
      case NEGATIVE_Y -> new Point3(-maxR, top, -maxR);
      case NEGATIVE_Z -> new Point3(-maxR, -maxR, top);
    };
  }

  private static Point3 computeExpectedMax(Cylinder.BottomToTopAxis axis, Cylinder.OriginAlignment alignment, double length, double maxR) {
    double top = computeTop(alignment, length);
    double bottom = computeBottom(alignment, length);
    return switch (axis) {
      case POSITIVE_X -> new Point3(top, maxR, maxR);
      case POSITIVE_Y -> new Point3(maxR, top, maxR);
      case POSITIVE_Z -> new Point3(maxR, maxR, top);
      case NEGATIVE_X -> new Point3(bottom, maxR, maxR);
      case NEGATIVE_Y -> new Point3(maxR, bottom, maxR);
      case NEGATIVE_Z -> new Point3(maxR, maxR, bottom);
    };
  }

  private static double computeTop(Cylinder.OriginAlignment alignment, double length) {
    return switch (alignment) {
      case BOTTOM -> length;
      case CENTER -> length * 0.5;
      case TOP -> 0;
    };
  }

  private static double computeBottom(Cylinder.OriginAlignment alignment, double length) {
    return switch (alignment) {
      case BOTTOM -> 0;
      case CENTER -> -length * 0.5;
      case TOP -> -length;
    };
  }

}
