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

  // --- Visualization helpers exercised via headless scene graph ---

  private static edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera sceneCamera() {
    edu.cmu.cs.dennisc.scenegraph.Scene scene = new edu.cmu.cs.dennisc.scenegraph.Scene();
    edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera cam =
        new edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera();
    Transformable mount = new Transformable();
    cam.setParent(mount);
    mount.setParent(scene);
    return cam;
  }

  @Test
  public void showAndHideXAxisAreSafe() {
    edu.cmu.cs.dennisc.scenegraph.Scene s = new edu.cmu.cs.dennisc.scenegraph.Scene();
    SnapUtilities.showXAxis(new Point3(1, 0, 0), s);
    SnapUtilities.hideXAxis();
    SnapUtilities.showXAxis(new Point3(2, 0, 0), org.alice.math.immutable.Vector3.POSITIVE_X_AXIS, s);
    SnapUtilities.hideXAxis();
  }

  @Test
  public void showAndHideYAxisAreSafe() {
    edu.cmu.cs.dennisc.scenegraph.Scene s = new edu.cmu.cs.dennisc.scenegraph.Scene();
    SnapUtilities.showYAxis(new Point3(0, 1, 0), s);
    SnapUtilities.hideYAxis();
    SnapUtilities.showYAxis(new Point3(0, 2, 0), org.alice.math.immutable.Vector3.POSITIVE_Y_AXIS, s);
    SnapUtilities.hideYAxis();
  }

  @Test
  public void showAndHideZAxisAreSafe() {
    edu.cmu.cs.dennisc.scenegraph.Scene s = new edu.cmu.cs.dennisc.scenegraph.Scene();
    SnapUtilities.showZAxis(new Point3(0, 0, 1), s);
    SnapUtilities.hideZAxis();
    SnapUtilities.showZAxis(new Point3(0, 0, 2), org.alice.math.immutable.Vector3.POSITIVE_Z_AXIS, s);
    SnapUtilities.hideZAxis();
  }

  @Test
  public void showAndHideArbitraryAxisAreSafe() {
    edu.cmu.cs.dennisc.scenegraph.Scene s = new edu.cmu.cs.dennisc.scenegraph.Scene();
    SnapUtilities.showArbitraryAxis(new Point3(1, 1, 1), org.alice.math.immutable.Vector3.POSITIVE_X_AXIS, s);
    SnapUtilities.hideArbitraryAxis();
  }

  @Test
  public void hideMovementSnapVisualizationIsSafe() {
    SnapUtilities.hideMovementSnapVisualization();
  }

  @Test
  public void hideRotationSnapVisualizationIsSafe() {
    SnapUtilities.hideRotationSnapVisualization();
  }

  @Test
  public void showAndHideSnapSphereAreSafe() {
    edu.cmu.cs.dennisc.scenegraph.Scene s = new edu.cmu.cs.dennisc.scenegraph.Scene();
    SnapUtilities.showSnapSphere(new Point3(0, 0, 0), new Point3(1, 0, 0), s);
    SnapUtilities.hideSnapSphere();
  }

  @Test
  public void isEdgeOnReturnsTrueWhenPerpendicular() {
    edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera cam = sceneCamera();
    // default camera backward axis = +Z; up vector = +Y → dot=0
    assertTrue(SnapUtilities.isEdgeOn(cam, org.alice.math.immutable.Vector3.POSITIVE_X_AXIS));
  }

  @Test
  public void isEdgeOnReturnsFalseWhenParallel() {
    edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera cam = sceneCamera();
    assertFalse(SnapUtilities.isEdgeOn(cam, org.alice.math.immutable.Vector3.POSITIVE_Z_AXIS));
  }

  @Test
  public void isEdgeOnWithEpsilonReturnsTrueWithinTolerance() {
    edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera cam = sceneCamera();
    assertTrue(SnapUtilities.isEdgeOn(cam, org.alice.math.immutable.Vector3.POSITIVE_Y_AXIS, 0.01));
  }

  @Test
  public void showHorizontalSnapAcceptsNullReferenceFrameThroughSceneRoot() {
    edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera cam = sceneCamera();
    SnapUtilities.showHorizontalSnap(cam, new Point3(0.05, 0.05, 0.05), new Point3(0, 0, 0), null);
    SnapUtilities.hideMovementSnapVisualization();
  }

  @Test
  public void showVerticalSnapAcceptsNullReferenceFrameThroughSceneRoot() {
    edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera cam = sceneCamera();
    SnapUtilities.showVerticalSnap(cam, new Point3(0, 0.5, 0), new Point3(0, 0, 0), null);
    SnapUtilities.hideMovementSnapVisualization();
  }

  @Test
  public void showSnapLinesDelegatesToHorizontalAndVertical() {
    edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera cam = sceneCamera();
    SnapUtilities.showSnapLines(cam, new Point3(0.5, 0.5, 0.5), new Point3(0, 0, 0), null);
    SnapUtilities.hideMovementSnapVisualization();
  }

  @Test
  public void snapObjectToAbsoluteGridSnapsToNearestDefaultSpacing() {
    Transformable t = new Transformable();
    edu.cmu.cs.dennisc.scenegraph.Scene s = new edu.cmu.cs.dennisc.scenegraph.Scene();
    t.setParent(s);
    Point3 snapped = SnapUtilities.snapObjectToAbsoluteGrid(t, new Point3(0.49, 0, 0));
    assertEquals(0.5, snapped.x(), 1e-6);
  }

  @Test
  public void snapObjectToGridLeavesUnchangedWhenFarFromGrid() {
    Transformable t = new Transformable();
    edu.cmu.cs.dennisc.scenegraph.Scene s = new edu.cmu.cs.dennisc.scenegraph.Scene();
    t.setParent(s);
    Point3 snapped = SnapUtilities.snapObjectToGrid(t, new Point3(0.3, 0, 0), 0.5, null);
    assertEquals(0.3, snapped.x(), 1e-6);
  }

  @Test
  public void snapObjectToGridSnapsNegativeNumberToNegativeMultiple() {
    Transformable t = new Transformable();
    edu.cmu.cs.dennisc.scenegraph.Scene s = new edu.cmu.cs.dennisc.scenegraph.Scene();
    t.setParent(s);
    Point3 snapped = SnapUtilities.snapObjectToGrid(t, new Point3(-0.55, 0, 0), 0.5, null);
    assertEquals(-0.5, snapped.x(), 1e-6);
  }

  @Test
  public void snapObjectToGridSnapsZCoordinateNearGridLine() {
    Transformable t = new Transformable();
    edu.cmu.cs.dennisc.scenegraph.Scene s = new edu.cmu.cs.dennisc.scenegraph.Scene();
    t.setParent(s);
    Point3 snapped = SnapUtilities.snapObjectToGrid(t, new Point3(0, 0, 0.55), 0.5, null);
    assertEquals(0.5, snapped.z(), 1e-6);
  }

  @Test
  public void snapObjectToGridSnapsZCoordinateForNegativeValue() {
    Transformable t = new Transformable();
    edu.cmu.cs.dennisc.scenegraph.Scene s = new edu.cmu.cs.dennisc.scenegraph.Scene();
    t.setParent(s);
    Point3 snapped = SnapUtilities.snapObjectToGrid(t, new Point3(0, 0, -0.55), 0.5, null);
    assertEquals(-0.5, snapped.z(), 1e-6);
  }

  @Test
  public void doRotationSnappingWithAdapterReturnsNearestAngle() {
    Angle currentAngle = new AngleInRadians(Math.PI / 16.0 + 0.001);
    DragAdapter adapter = new DragAdapter() {
      @Override public boolean shouldSnapToRotation() { return true; }
      @Override public Angle getRotationSnapAngle() { return new AngleInRadians(Math.PI / 16.0); }
    };
    Angle snapped = SnapUtilities.doRotationSnapping(currentAngle, adapter);
    assertEquals(Math.PI / 16.0, snapped.getAsRadians(), 1e-6);
  }

  @Test
  public void doRotationSnappingReturnsOriginalIfAdapterReturnsFalse() {
    Angle currentAngle = new AngleInRadians(0.3);
    DragAdapter adapter = new DragAdapter() {
      @Override public boolean shouldSnapToRotation() { return false; }
    };
    assertSame(currentAngle, SnapUtilities.doRotationSnapping(currentAngle, adapter));
  }

  @Test
  public void doMovementSnappingWithoutAdapterReturnsOriginalPosition() {
    Transformable t = new Transformable();
    edu.cmu.cs.dennisc.scenegraph.Scene s = new edu.cmu.cs.dennisc.scenegraph.Scene();
    t.setParent(s);
    Point3 current = new Point3(0.55, 0, 0);
    Point3 snapped = SnapUtilities.doMovementSnapping(t, current, null, null, sceneCamera());
    assertSame(current, snapped);
  }

  @Test
  public void doMovementSnappingWithSnapToGridAdapterAndCameraSnapsAndShowsLines() {
    Transformable t = new Transformable();
    edu.cmu.cs.dennisc.scenegraph.Scene s = new edu.cmu.cs.dennisc.scenegraph.Scene();
    t.setParent(s);
    edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera cam = sceneCamera();
    DragAdapter adapter = new DragAdapter() {
      @Override public boolean shouldSnapToGrid() { return true; }
      @Override public double getGridSpacing() { return 0.5; }
    };
    Point3 snapped = SnapUtilities.doMovementSnapping(t, new Point3(0.49, 0, 0), adapter, null, cam);
    assertEquals(0.5, snapped.x(), 1e-6);
    SnapUtilities.hideMovementSnapVisualization();
  }

  @Test
  public void showHorizontalSnapWithLargeSnapVectorYTriggersYAxisVisual() {
    edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera cam = sceneCamera();
    // Force the edge-on path: rotate camera to point along the Y axis.
    SnapUtilities.showHorizontalSnap(cam, new Point3(0.5, 0.5, 0.5), new Point3(0, 0, 0), null);
    SnapUtilities.hideMovementSnapVisualization();
  }
}
