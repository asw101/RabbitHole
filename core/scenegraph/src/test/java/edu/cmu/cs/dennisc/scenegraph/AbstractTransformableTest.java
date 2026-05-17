package edu.cmu.cs.dennisc.scenegraph;

import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.AngleInRadians;
import org.alice.math.immutable.AxisRotation;
import org.alice.math.immutable.OrthogonalMatrix3x3;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Vector3;
import org.alice.math.immutable.Vector4;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AbstractTransformableTest {
  private static final double EPSILON = 0.000001;

  @Test(expected = NullPointerException.class)
  public void setLocalTransformationRejectsNull() {
    Transformable t = new Transformable();
    t.setLocalTransformation(null);
  }

  @Test(expected = RuntimeException.class)
  public void setLocalTransformationRejectsNaN() {
    Transformable t = new Transformable();
    AffineMatrix4x4 nanMatrix = AffineMatrix4x4.createTranslation(Double.NaN, 0, 0);
    t.setLocalTransformation(nanMatrix);
  }

  @Test
  public void setLocalTransformationWithAffectOnlyChangesSelectedComponents() {
    Transformable t = new Transformable();
    AffineMatrix4x4 initial = AffineMatrix4x4.createTranslation(1, 2, 3);
    t.setLocalTransformation(initial);

    AffineMatrix4x4 change = AffineMatrix4x4.createTranslation(99, 88, 77);
    t.setLocalTransformation(change, TransformationAffect.AFFECT_TRANSLATION_X_ONLY);

    AffineMatrix4x4 result = t.getLocalTransformation();
    assertEquals(99, result.translation().x(), EPSILON);
    assertEquals(2, result.translation().y(), EPSILON);
    assertEquals(3, result.translation().z(), EPSILON);
  }

  @Test
  public void getAbsoluteTransformationComposesVehicleChain() {
    Scene scene = new Scene();
    Transformable parent = new Transformable();
    Transformable child = new Transformable();

    parent.setLocalTransformation(AffineMatrix4x4.createTranslation(10, 0, 0));
    child.setLocalTransformation(AffineMatrix4x4.createTranslation(0, 5, 0));

    scene.addComponent(parent);
    parent.addComponent(child);

    AffineMatrix4x4 absolute = child.getAbsoluteTransformation();
    assertPointEquals(new Point3(10, 5, 0), absolute.translation());
  }

  @Test
  public void getAbsoluteTransformationReturnsLocalWhenParentIsScene() {
    Scene scene = new Scene();
    Transformable t = new Transformable();
    AffineMatrix4x4 local = AffineMatrix4x4.createTranslation(7, 8, 9);
    t.setLocalTransformation(local);
    scene.addComponent(t);

    AffineMatrix4x4 absolute = t.getAbsoluteTransformation();
    assertPointEquals(local.translation(), absolute.translation());
  }

  @Test
  public void getInverseAbsoluteTransformationInvertsCorrectly() {
    Scene scene = new Scene();
    Transformable t = new Transformable();
    t.setLocalTransformation(AffineMatrix4x4.createTranslation(3, 4, 5));
    scene.addComponent(t);

    AffineMatrix4x4 absolute = t.getAbsoluteTransformation();
    AffineMatrix4x4 inverse = t.getInverseAbsoluteTransformation();
    AffineMatrix4x4 product = absolute.times(inverse);

    // Should be approximately identity
    assertPointEquals(Point3.ORIGIN, product.translation());
    assertOrientationNearIdentity(product.orientation());
  }

  @Test
  public void getTransformationAsSeenBySceneReturnsAbsolute() {
    Scene scene = new Scene();
    Transformable parent = new Transformable();
    Transformable target = new Transformable();

    parent.setLocalTransformation(AffineMatrix4x4.createTranslation(10, 0, 0));
    target.setLocalTransformation(AffineMatrix4x4.createTranslation(0, 5, 0));

    scene.addComponent(parent);
    parent.addComponent(target);

    AffineMatrix4x4 asSeenByScene = target.getTransformation(AsSeenBy.SCENE);
    AffineMatrix4x4 absolute = target.getAbsoluteTransformation();

    assertPointEquals(absolute.translation(), asSeenByScene.translation());
  }

  @Test
  public void getTransformationAsSeenByParentReturnsLocal() {
    Scene scene = new Scene();
    Transformable parent = new Transformable();
    Transformable child = new Transformable();

    AffineMatrix4x4 local = AffineMatrix4x4.createTranslation(1, 2, 3);
    parent.setLocalTransformation(AffineMatrix4x4.createTranslation(10, 20, 30));
    child.setLocalTransformation(local);

    scene.addComponent(parent);
    parent.addComponent(child);

    AffineMatrix4x4 asSeenByParent = child.getTransformation(AsSeenBy.PARENT);
    assertPointEquals(local.translation(), asSeenByParent.translation());
  }

  @Test
  public void getTransformationAsSeenBySelfReturnsIdentity() {
    Scene scene = new Scene();
    Transformable t = new Transformable();
    t.setLocalTransformation(AffineMatrix4x4.createTranslation(5, 6, 7));
    scene.addComponent(t);

    AffineMatrix4x4 asSeenBySelf = t.getTransformation(AsSeenBy.SELF);
    assertPointEquals(Point3.ORIGIN, asSeenBySelf.translation());
    assertOrientationNearIdentity(asSeenBySelf.orientation());
  }

  @Test
  public void getTransformationAsSeenByArbitraryFrameComposesCorrectly() {
    Scene scene = new Scene();
    Transformable a = new Transformable();
    Transformable target = new Transformable();
    Transformable observer = new Transformable();

    a.setLocalTransformation(AffineMatrix4x4.createTranslation(10, 0, 0));
    target.setLocalTransformation(AffineMatrix4x4.createTranslation(0, 5, 0));
    observer.setLocalTransformation(AffineMatrix4x4.createTranslation(3, 0, 0));

    scene.addComponent(a);
    a.addComponent(target);
    scene.addComponent(observer);

    // target absolute = (10, 5, 0), observer absolute = (3, 0, 0)
    // relative should be approximately (7, 5, 0)
    AffineMatrix4x4 relative = target.getTransformation(observer);
    assertPointEquals(new Point3(7, 5, 0), relative.translation());
  }

  @Test
  public void setTranslationOnlyWithNaNReplacesWithZero() {
    Scene scene = new Scene();
    Transformable t = new Transformable();
    scene.addComponent(t);

    t.setTranslationOnly(Double.NaN, 5.0, Double.NaN, AsSeenBy.PARENT);

    AffineMatrix4x4 result = t.getLocalTransformation();
    assertEquals(0, result.translation().x(), EPSILON);
    assertEquals(5, result.translation().y(), EPSILON);
    assertEquals(0, result.translation().z(), EPSILON);
  }

  @Test
  public void applyTranslationInLocalFrameMovesAlongLocalAxes() {
    Scene scene = new Scene();
    Transformable t = new Transformable();
    scene.addComponent(t);

    // Rotate 90° about Y axis
    t.applyRotationAboutYAxis(new AngleInRadians(Math.PI / 2));
    Point3 beforeTranslation = t.getAbsoluteTransformation().translation();

    // Apply translation (1,0,0) in SELF frame — should move along rotated axis
    t.applyTranslation(1, 0, 0, AsSeenBy.SELF);
    Point3 afterTranslation = t.getAbsoluteTransformation().translation();

    // After 90° Y rotation, local X maps to world -Z
    double deltaX = afterTranslation.x() - beforeTranslation.x();
    double deltaZ = afterTranslation.z() - beforeTranslation.z();
    // The movement should primarily be along Z, not X
    assertTrue("Expected movement along Z axis, not X", Math.abs(deltaZ) > Math.abs(deltaX) - EPSILON);
  }

  @Test
  public void applyTranslationInVehicleFrameMovesAlongParentAxes() {
    Scene scene = new Scene();
    Transformable t = new Transformable();
    scene.addComponent(t);

    t.setLocalTransformation(AffineMatrix4x4.createTranslation(0, 0, 0));
    t.applyTranslation(5, 3, 1, AsSeenBy.PARENT);

    AffineMatrix4x4 result = t.getLocalTransformation();
    assertPointEquals(new Point3(5, 3, 1), result.translation());
  }

  @Test
  public void applyRotationAboutYAxisChangesOrientation() {
    Scene scene = new Scene();
    Transformable t = new Transformable();
    scene.addComponent(t);

    t.setLocalTransformation(AffineMatrix4x4.IDENTITY);
    t.applyRotationAboutYAxis(new AngleInRadians(Math.PI / 2));

    AffineMatrix4x4 result = t.getLocalTransformation();
    // Translation should remain at origin
    assertPointEquals(Point3.ORIGIN, result.translation());
    // Orientation should have changed from identity
    OrthogonalMatrix3x3 orientation = result.orientation();
    assertNotNull(orientation);
    // After 90° Y rotation, the right vector (1,0,0) should map to approximately (0,0,-1)
    double rightX = orientation.right().x();
    double rightZ = orientation.right().z();
    assertTrue("Orientation should have changed after Y rotation",
        Math.abs(rightX) < 0.1 || Math.abs(rightZ) > 0.1);
  }

  @Test
  public void notifyTransformationListenersFiresAbsoluteChangeEvents() {
    Scene scene = new Scene();
    Transformable t = new Transformable();
    scene.addComponent(t);

    AtomicInteger eventCount = new AtomicInteger();
    t.addAbsoluteTransformationListener(e -> eventCount.incrementAndGet());

    t.notifyTransformationListeners();

    assertEquals(1, eventCount.get());
  }

  private static void assertPointEquals(Point3 expected, Point3 actual) {
    assertEquals("x", expected.x(), actual.x(), EPSILON);
    assertEquals("y", expected.y(), actual.y(), EPSILON);
    assertEquals("z", expected.z(), actual.z(), EPSILON);
  }

  @Test
  public void applyRotationAboutXAxisChangesOrientation() {
    Scene scene = new Scene();
    Transformable t = new Transformable();
    scene.addComponent(t);

    t.setLocalTransformation(AffineMatrix4x4.IDENTITY);
    t.applyRotationAboutXAxis(new AngleInRadians(Math.PI / 2));

    AffineMatrix4x4 result = t.getLocalTransformation();
    assertPointEquals(Point3.ORIGIN, result.translation());
    OrthogonalMatrix3x3 o = result.orientation();
    // After 90° X rotation, up(0,1,0) maps to ~(0,0,-1)
    assertTrue("Up vector should have changed after X rotation",
        Math.abs(o.up().y()) < 0.1);
  }

  @Test
  public void applyRotationAboutZAxisChangesOrientation() {
    Scene scene = new Scene();
    Transformable t = new Transformable();
    scene.addComponent(t);

    t.setLocalTransformation(AffineMatrix4x4.IDENTITY);
    t.applyRotationAboutZAxis(new AngleInRadians(Math.PI / 2));

    AffineMatrix4x4 result = t.getLocalTransformation();
    assertPointEquals(Point3.ORIGIN, result.translation());
    OrthogonalMatrix3x3 o = result.orientation();
    // After 90° Z rotation, right(1,0,0) maps to ~(0,1,0)
    assertTrue("Right vector should have changed after Z rotation",
        Math.abs(o.right().x()) < 0.1);
  }

  @Test
  public void applyRotationAboutArbitraryAxis() {
    Scene scene = new Scene();
    Transformable t = new Transformable();
    scene.addComponent(t);

    t.setLocalTransformation(AffineMatrix4x4.IDENTITY);
    // Rotate 180° about the Y axis via arbitrary-axis API
    t.applyRotationAboutArbitraryAxis(new Vector3(0, 1, 0), new AngleInRadians(Math.PI));

    AffineMatrix4x4 result = t.getLocalTransformation();
    assertPointEquals(Point3.ORIGIN, result.translation());
    // After 180° Y rotation, right(1,0,0) maps to ~(-1,0,0)
    assertEquals(-1.0, result.orientation().right().x(), 0.001);
  }

  @Test
  public void setTransformationRelativeToScene() {
    Scene scene = new Scene();
    Transformable parent = new Transformable();
    Transformable child = new Transformable();

    parent.setLocalTransformation(AffineMatrix4x4.createTranslation(10, 0, 0));
    scene.addComponent(parent);
    parent.addComponent(child);

    // Set child absolute position to (20, 0, 0) relative to scene
    child.setTransformation(AffineMatrix4x4.createTranslation(20, 0, 0), AsSeenBy.SCENE);

    AffineMatrix4x4 abs = child.getAbsoluteTransformation();
    assertPointEquals(new Point3(20, 0, 0), abs.translation());
  }

  @Test
  public void setTransformationRelativeToSelfAppendsLocally() {
    Scene scene = new Scene();
    Transformable t = new Transformable();
    scene.addComponent(t);

    t.setLocalTransformation(AffineMatrix4x4.createTranslation(5, 0, 0));
    t.setTransformation(AffineMatrix4x4.createTranslation(1, 0, 0), AsSeenBy.SELF);

    AffineMatrix4x4 result = t.getLocalTransformation();
    // Self-relative set appends to current transform
    assertEquals(6.0, result.translation().x(), EPSILON);
  }

  @Test
  public void applyTranslationWithTuple3() {
    Scene scene = new Scene();
    Transformable t = new Transformable();
    scene.addComponent(t);

    t.setLocalTransformation(AffineMatrix4x4.IDENTITY);
    t.applyTranslation(new Point3(3, 4, 5), AsSeenBy.PARENT);

    assertPointEquals(new Point3(3, 4, 5), t.getLocalTransformation().translation());
  }

  @Test
  public void applyTranslationDefaultSelf() {
    Scene scene = new Scene();
    Transformable t = new Transformable();
    scene.addComponent(t);

    t.setLocalTransformation(AffineMatrix4x4.IDENTITY);
    t.applyTranslation(1, 2, 3);

    AffineMatrix4x4 result = t.getLocalTransformation();
    assertPointEquals(new Point3(1, 2, 3), result.translation());
  }

  @Test
  public void applyTranslationWithTuple3DefaultSelf() {
    Scene scene = new Scene();
    Transformable t = new Transformable();
    scene.addComponent(t);

    t.setLocalTransformation(AffineMatrix4x4.IDENTITY);
    t.applyTranslation(new Point3(7, 8, 9));

    assertPointEquals(new Point3(7, 8, 9), t.getLocalTransformation().translation());
  }

  @Test
  public void setAxesOnlyChangesOrientationKeepsTranslation() {
    Scene scene = new Scene();
    Transformable t = new Transformable();
    scene.addComponent(t);

    t.setLocalTransformation(AffineMatrix4x4.createTranslation(5, 10, 15));
    // Set orientation to 90° Y rotation, should keep translation
    OrthogonalMatrix3x3 rotY90 = AffineMatrix4x4.createOrientation(
        AxisRotation.createYAxisRotation(new AngleInRadians(Math.PI / 2))).orientation();
    t.setAxesOnly(rotY90, AsSeenBy.PARENT);

    AffineMatrix4x4 result = t.getLocalTransformation();
    // Translation should be preserved
    assertPointEquals(new Point3(5, 10, 15), result.translation());
    // Orientation should have changed
    assertTrue("Orientation should not be identity after setAxesOnly",
        Math.abs(result.orientation().right().x()) < 0.1);
  }

  @Test
  public void setAxesOnlyToStandUpWithSceneFrame() {
    Scene scene = new Scene();
    Transformable t = new Transformable();
    scene.addComponent(t);

    t.setLocalTransformation(AffineMatrix4x4.IDENTITY);
    // Apply a tilt rotation about X
    t.applyRotationAboutXAxis(new AngleInRadians(Math.PI / 6));

    // Stand up should try to restore upright orientation
    t.setAxesOnlyToStandUp();

    AffineMatrix4x4 result = t.getLocalTransformation();
    assertNotNull(result);
    // Up vector should be closer to (0,1,0) after standing up
    double upY = result.orientation().up().y();
    assertTrue("Up.y should be positive after stand-up", upY > 0);
  }

  @Test
  public void getTranslationReturnsTransformTranslation() {
    Scene scene = new Scene();
    Transformable parent = new Transformable();
    Transformable child = new Transformable();

    parent.setLocalTransformation(AffineMatrix4x4.createTranslation(10, 0, 0));
    child.setLocalTransformation(AffineMatrix4x4.createTranslation(0, 5, 0));
    scene.addComponent(parent);
    parent.addComponent(child);

    Point3 translation = child.getTranslation(AsSeenBy.SCENE);
    assertPointEquals(new Point3(10, 5, 0), translation);
  }

  @Test
  public void getAxesReturnsOrientationPart() {
    Scene scene = new Scene();
    Transformable t = new Transformable();
    scene.addComponent(t);

    t.setLocalTransformation(AffineMatrix4x4.createTranslation(5, 0, 0));
    OrthogonalMatrix3x3 axes = t.getAxes(AsSeenBy.PARENT);
    assertOrientationNearIdentity(axes);
  }

  @Test
  public void setParentWithoutMovingKeepsLocalTransform() {
    Scene scene = new Scene();
    Transformable parent1 = new Transformable();
    Transformable parent2 = new Transformable();
    Transformable child = new Transformable();

    parent1.setLocalTransformation(AffineMatrix4x4.createTranslation(10, 0, 0));
    parent2.setLocalTransformation(AffineMatrix4x4.createTranslation(20, 0, 0));
    child.setLocalTransformation(AffineMatrix4x4.createTranslation(5, 5, 5));

    scene.addComponent(parent1);
    scene.addComponent(parent2);
    parent1.addComponent(child);

    // Move child to parent2 without adjusting transform
    child.setParentWithoutMoving(parent2);

    // Local transform should remain the same
    assertPointEquals(new Point3(5, 5, 5), child.getLocalTransformation().translation());
    // But absolute transform changes because of different parent
    assertPointEquals(new Point3(25, 5, 5), child.getAbsoluteTransformation().translation());
  }

  @Test
  public void transformToAbsolutePoint3() {
    Scene scene = new Scene();
    Transformable t = new Transformable();
    t.setLocalTransformation(AffineMatrix4x4.createTranslation(10, 20, 30));
    scene.addComponent(t);

    Point3 local = new Point3(1, 0, 0);
    Point3 absolute = t.transformToAbsolute(local);
    assertPointEquals(new Point3(11, 20, 30), absolute);
  }

  @Test
  public void transformFromAbsolutePoint3() {
    Scene scene = new Scene();
    Transformable t = new Transformable();
    t.setLocalTransformation(AffineMatrix4x4.createTranslation(10, 20, 30));
    scene.addComponent(t);

    Point3 absolute = new Point3(11, 20, 30);
    Point3 local = t.transformFromAbsolute(absolute);
    assertPointEquals(new Point3(1, 0, 0), local);
  }

  @Test
  public void transformToAbsoluteVector3() {
    Scene scene = new Scene();
    Transformable t = new Transformable();
    t.setLocalTransformation(AffineMatrix4x4.createTranslation(10, 20, 30));
    scene.addComponent(t);

    Vector3 localDir = new Vector3(1, 0, 0);
    Vector3 absDir = t.transformToAbsolute(localDir);
    // Vectors are direction-only, so translation shouldn't apply in direction transform
    assertNotNull(absDir);
  }

  @Test
  public void transformFromAbsoluteVector3() {
    Scene scene = new Scene();
    Transformable t = new Transformable();
    t.setLocalTransformation(AffineMatrix4x4.createTranslation(10, 20, 30));
    scene.addComponent(t);

    Vector3 absDir = new Vector3(1, 0, 0);
    Vector3 localDir = t.transformFromAbsolute(absDir);
    assertNotNull(localDir);
  }

  @Test
  public void transformToPoint3BetweenComponents() {
    Scene scene = new Scene();
    Transformable a = new Transformable();
    Transformable b = new Transformable();

    a.setLocalTransformation(AffineMatrix4x4.createTranslation(10, 0, 0));
    b.setLocalTransformation(AffineMatrix4x4.createTranslation(5, 0, 0));
    scene.addComponent(a);
    scene.addComponent(b);

    // Transform origin of a to b's frame
    Point3 result = a.transformTo(Point3.ORIGIN, b);
    // In b's frame, a's origin (10,0,0) should be at (5,0,0)
    assertPointEquals(new Point3(5, 0, 0), result);
  }

  @Test
  public void transformFromPoint3BetweenComponents() {
    Scene scene = new Scene();
    Transformable a = new Transformable();
    Transformable b = new Transformable();

    a.setLocalTransformation(AffineMatrix4x4.createTranslation(10, 0, 0));
    b.setLocalTransformation(AffineMatrix4x4.createTranslation(5, 0, 0));
    scene.addComponent(a);
    scene.addComponent(b);

    Point3 result = a.transformFrom(Point3.ORIGIN, b);
    // b's origin in world = (5,0,0), in a's frame = (-5,0,0)
    assertPointEquals(new Point3(-5, 0, 0), result);
  }

  @Test
  public void transformToAbsoluteVector4() {
    Scene scene = new Scene();
    Transformable t = new Transformable();
    t.setLocalTransformation(AffineMatrix4x4.createTranslation(10, 0, 0));
    scene.addComponent(t);

    Vector4 local = new Vector4(1, 0, 0, 1);
    Vector4 absolute = t.transformToAbsolute(local);
    assertNotNull(absolute);
    assertEquals(11.0, absolute.x(), EPSILON);
  }

  @Test
  public void transformFromAbsoluteVector4() {
    Scene scene = new Scene();
    Transformable t = new Transformable();
    t.setLocalTransformation(AffineMatrix4x4.createTranslation(10, 0, 0));
    scene.addComponent(t);

    Vector4 absolute = new Vector4(11, 0, 0, 1);
    Vector4 local = t.transformFromAbsolute(absolute);
    assertNotNull(local);
    assertEquals(1.0, local.x(), EPSILON);
  }

  @Test
  public void transformToVector4BetweenComponents() {
    Scene scene = new Scene();
    Transformable a = new Transformable();
    Transformable b = new Transformable();

    a.setLocalTransformation(AffineMatrix4x4.createTranslation(10, 0, 0));
    b.setLocalTransformation(AffineMatrix4x4.createTranslation(5, 0, 0));
    scene.addComponent(a);
    scene.addComponent(b);

    Vector4 result = a.transformTo(new Vector4(0, 0, 0, 1), b);
    assertNotNull(result);
    assertEquals(5.0, result.x(), EPSILON);
  }

  @Test
  public void transformFromVector4BetweenComponents() {
    Scene scene = new Scene();
    Transformable a = new Transformable();
    Transformable b = new Transformable();

    a.setLocalTransformation(AffineMatrix4x4.createTranslation(10, 0, 0));
    b.setLocalTransformation(AffineMatrix4x4.createTranslation(5, 0, 0));
    scene.addComponent(a);
    scene.addComponent(b);

    Vector4 result = a.transformFrom(new Vector4(0, 0, 0, 1), b);
    assertNotNull(result);
    assertEquals(-5.0, result.x(), EPSILON);
  }

  @Test
  public void transformToVector3BetweenComponents() {
    Scene scene = new Scene();
    Transformable a = new Transformable();
    Transformable b = new Transformable();

    a.setLocalTransformation(AffineMatrix4x4.createTranslation(10, 0, 0));
    b.setLocalTransformation(AffineMatrix4x4.createTranslation(5, 0, 0));
    scene.addComponent(a);
    scene.addComponent(b);

    // Directions transform differently than points
    Vector3 result = a.transformTo(new Vector3(1, 0, 0), b);
    assertNotNull(result);
  }

  @Test
  public void transformFromVector3BetweenComponents() {
    Scene scene = new Scene();
    Transformable a = new Transformable();
    Transformable b = new Transformable();

    a.setLocalTransformation(AffineMatrix4x4.createTranslation(10, 0, 0));
    b.setLocalTransformation(AffineMatrix4x4.createTranslation(5, 0, 0));
    scene.addComponent(a);
    scene.addComponent(b);

    Vector3 result = a.transformFrom(new Vector3(1, 0, 0), b);
    assertNotNull(result);
  }

  @Test
  public void sceneTransformToAbsoluteIsIdentity() {
    Scene scene = new Scene();
    // Scene.transformToAbsolute should return input (isAbsolute() == true)
    Point3 p = new Point3(1, 2, 3);
    Point3 result = scene.transformToAbsolute(p);
    assertPointEquals(p, result);
  }

  @Test
  public void sceneTransformFromAbsoluteIsIdentity() {
    Scene scene = new Scene();
    Point3 p = new Point3(1, 2, 3);
    Point3 result = scene.transformFromAbsolute(p);
    assertPointEquals(p, result);
  }

  @Test
  public void removeAbsoluteTransformationListenerStopsEvents() {
    Scene scene = new Scene();
    Transformable t = new Transformable();
    scene.addComponent(t);

    AtomicInteger events = new AtomicInteger();
    var listener = new edu.cmu.cs.dennisc.scenegraph.event.AbsoluteTransformationListener() {
      @Override
      public void absoluteTransformationChanged(edu.cmu.cs.dennisc.scenegraph.event.AbsoluteTransformationEvent e) {
        events.incrementAndGet();
      }
    };

    t.addAbsoluteTransformationListener(listener);
    t.notifyTransformationListeners();
    assertEquals(1, events.get());

    t.removeAbsoluteTransformationListener(listener);
    t.notifyTransformationListeners();
    assertEquals(1, events.get());
  }

  @Test
  public void removeHierarchyListenerStopsEvents() {
    Scene scene = new Scene();
    Transformable t = new Transformable();

    AtomicInteger events = new AtomicInteger();
    var listener = new edu.cmu.cs.dennisc.scenegraph.event.HierarchyListener() {
      @Override
      public void hierarchyChanged(edu.cmu.cs.dennisc.scenegraph.event.HierarchyEvent e) {
        events.incrementAndGet();
      }
    };

    t.addHierarchyListener(listener);
    scene.addComponent(t);
    assertTrue(events.get() >= 1);

    int before = events.get();
    t.removeHierarchyListener(listener);
    scene.removeComponent(t);
    assertEquals(before, events.get());
  }

  @Test
  public void setTranslationOnlyWithTuple3OverloadWorks() {
    Scene scene = new Scene();
    Transformable t = new Transformable();
    scene.addComponent(t);

    t.setTranslationOnly(new Point3(7, 8, 9), AsSeenBy.PARENT);

    AffineMatrix4x4 result = t.getLocalTransformation();
    assertPointEquals(new Point3(7, 8, 9), result.translation());
  }

  @Test
  public void applyRotationAboutXAxisWithReferenceFrame() {
    Scene scene = new Scene();
    Transformable t = new Transformable();
    scene.addComponent(t);

    t.setLocalTransformation(AffineMatrix4x4.IDENTITY);
    t.applyRotationAboutXAxis(new AngleInRadians(Math.PI / 4), AsSeenBy.PARENT);

    AffineMatrix4x4 result = t.getLocalTransformation();
    assertNotNull(result);
    assertPointEquals(Point3.ORIGIN, result.translation());
  }

  @Test
  public void applyRotationAboutZAxisWithReferenceFrame() {
    Scene scene = new Scene();
    Transformable t = new Transformable();
    scene.addComponent(t);

    t.setLocalTransformation(AffineMatrix4x4.IDENTITY);
    t.applyRotationAboutZAxis(new AngleInRadians(Math.PI / 4), AsSeenBy.PARENT);

    AffineMatrix4x4 result = t.getLocalTransformation();
    assertNotNull(result);
  }

  @Test
  public void applyRotationAboutYAxisWithReferenceFrame() {
    Scene scene = new Scene();
    Transformable t = new Transformable();
    scene.addComponent(t);

    t.setLocalTransformation(AffineMatrix4x4.IDENTITY);
    t.applyRotationAboutYAxis(new AngleInRadians(Math.PI / 3), AsSeenBy.PARENT);

    AffineMatrix4x4 result = t.getLocalTransformation();
    assertNotNull(result);
  }

  @Test
  public void applyRotationAboutArbitraryAxisWithReferenceFrame() {
    Scene scene = new Scene();
    Transformable t = new Transformable();
    scene.addComponent(t);

    t.setLocalTransformation(AffineMatrix4x4.IDENTITY);
    t.applyRotationAboutArbitraryAxis(new Vector3(0, 0, 1), new AngleInRadians(Math.PI / 6), AsSeenBy.PARENT);

    AffineMatrix4x4 result = t.getLocalTransformation();
    assertNotNull(result);
  }

  @Test
  public void getAbsoluteTransformationWithNoVehicleReturnsLocal() {
    Transformable t = new Transformable();
    t.setLocalTransformation(AffineMatrix4x4.createTranslation(3, 4, 5));

    // No parent set, getVehicle() returns null → returns local
    AffineMatrix4x4 abs = t.getAbsoluteTransformation();
    assertPointEquals(new Point3(3, 4, 5), abs.translation());
  }

  private static void assertOrientationNearIdentity(OrthogonalMatrix3x3 o) {
    assertEquals("right.x", 1.0, o.right().x(), EPSILON);
    assertEquals("right.y", 0.0, o.right().y(), EPSILON);
    assertEquals("right.z", 0.0, o.right().z(), EPSILON);
    assertEquals("up.x", 0.0, o.up().x(), EPSILON);
    assertEquals("up.y", 1.0, o.up().y(), EPSILON);
    assertEquals("up.z", 0.0, o.up().z(), EPSILON);
    assertEquals("backward.x", 0.0, o.backward().x(), EPSILON);
    assertEquals("backward.y", 0.0, o.backward().y(), EPSILON);
    assertEquals("backward.z", 1.0, o.backward().z(), EPSILON);
  }
}
