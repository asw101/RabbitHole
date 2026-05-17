package edu.cmu.cs.dennisc.scenegraph;

import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.AngleInRadians;
import org.alice.math.immutable.OrthogonalMatrix3x3;
import org.alice.math.immutable.Point3;
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
