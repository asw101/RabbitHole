package org.alice.interact.manipulator;

import edu.cmu.cs.dennisc.scenegraph.AsSeenBy;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.alice.interact.DragAdapter;
import org.alice.math.immutable.Angle;
import org.alice.math.immutable.AngleInRadians;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import static org.junit.Assert.*;

/** Headless-safe math and helper tests for SnapUtilities. */
public class SnapUtilitiesTest {

  @Test
  public void getSGVisualForTransformableReturnsFirstVisualChild() {
    Transformable transformable = new Transformable();
    Visual visual = new Visual();
    visual.setParent(transformable);

    assertSame(visual, SnapUtilities.getSGVisualForTransformable(transformable));
  }

  @Test
  public void getSGVisualForTransformableReturnsNullForNullTransformable() {
    assertNull(SnapUtilities.getSGVisualForTransformable(null));
  }

  @Test
  public void getTransformableScaleReturnsIdentityWhenNoVisualExists() {
    assertEquals(org.alice.math.immutable.Matrix3x3.IDENTITY, SnapUtilities.getTransformableScale(new Transformable()));
  }

  @Test
  public void getBoundingBoxFallsBackToDefaultBoxAtTransformLocation() {
    Transformable transformable = new Transformable();
    transformable.setTranslationOnly(new Point3(1, 2, 3), AsSeenBy.SCENE);

    AxisAlignedBox box = SnapUtilities.getBoundingBox(transformable);

    assertEquals(0.0, box.getXMinimum(), 1e-6);
    assertEquals(2.0, box.getYMinimum(), 1e-6);
    assertEquals(2.0, box.getZMinimum(), 1e-6);
    assertEquals(2.0, box.getXMaximum(), 1e-6);
    assertEquals(3.0, box.getYMaximum(), 1e-6);
    assertEquals(4.0, box.getZMaximum(), 1e-6);
  }

  @Test
  public void getBoundingBoxUsesBonusDataWhenPresent() {
    Transformable transformable = new Transformable();
    transformable.setTranslationOnly(new Point3(1, 2, 3), AsSeenBy.SCENE);
    transformable.putBonusDataFor(DragAdapter.BOUNDING_BOX_KEY,
        new AxisAlignedBox(new Point3(-2, -1, -3), new Point3(2, 1, 3)));

    AxisAlignedBox box = SnapUtilities.getBoundingBox(transformable);

    assertEquals(-1.0, box.getXMinimum(), 1e-6);
    assertEquals(1.0, box.getYMinimum(), 1e-6);
    assertEquals(0.0, box.getZMinimum(), 1e-6);
    assertEquals(3.0, box.getXMaximum(), 1e-6);
    assertEquals(3.0, box.getYMaximum(), 1e-6);
    assertEquals(6.0, box.getZMaximum(), 1e-6);
  }

  @Test
  public void getBoundingBoxIgnoresNaNBonusData() {
    Transformable transformable = new Transformable();
    transformable.putBonusDataFor(DragAdapter.BOUNDING_BOX_KEY, AxisAlignedBox.NaN);

    AxisAlignedBox box = SnapUtilities.getBoundingBox(transformable);

    assertEquals(-1.0, box.getXMinimum(), 1e-6);
    assertEquals(0.0, box.getYMinimum(), 1e-6);
    assertEquals(-1.0, box.getZMinimum(), 1e-6);
  }

  @Test
  public void snapObjectToGroundAdjustsYWithinThreshold() {
    Transformable transformable = new Transformable();
    Point3 snapped = SnapUtilities.snapObjectToGround(transformable, new Point3(0, 0.02, 0));
    assertEquals(0.0, snapped.y(), 1e-6);
  }

  @Test
  public void snapObjectToGroundLeavesFarPointUnchanged() {
    Transformable transformable = new Transformable();
    Point3 original = new Point3(0, 0.2, 0);
    assertEquals(original, SnapUtilities.snapObjectToGround(transformable, original));
  }

  @Test
  public void snapObjectToGridSnapsCoordinateNearGridLine() {
    Transformable transformable = new Transformable();
    Point3 snapped = SnapUtilities.snapObjectToGrid(transformable, new Point3(0.55, 0, 0), 0.5, null);
    assertEquals(0.5, snapped.x(), 1e-6);
  }

  @Test
  public void doRotationSnappingWithoutAdapterReturnsOriginalAngle() {
    Angle angle = new AngleInRadians(Math.PI / 7.0);
    assertSame(angle, SnapUtilities.doRotationSnapping(angle, null));
  }
}
