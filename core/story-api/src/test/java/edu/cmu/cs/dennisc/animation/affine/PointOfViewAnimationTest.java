package edu.cmu.cs.dennisc.animation.affine;

import edu.cmu.cs.dennisc.scenegraph.Scene;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.OrthogonalMatrix3x3;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import static org.junit.Assert.*;

/** Headless tests for PointOfViewAnimation lifecycle. */
public class PointOfViewAnimationTest {

  private static Transformable createSubject(Scene scene) {
    Transformable t = new Transformable();
    t.setParent(scene);
    return t;
  }

  @Test
  public void constructorWithExplicitBeginAndEndDoesNotThrow() {
    Scene scene = new Scene();
    Transformable subject = createSubject(scene);
    AffineMatrix4x4 begin = AffineMatrix4x4.IDENTITY;
    AffineMatrix4x4 end = new AffineMatrix4x4(OrthogonalMatrix3x3.IDENTITY, new Point3(1, 2, 3));
    PointOfViewAnimation anim = new PointOfViewAnimation(subject, scene, begin, end);
    assertNotNull(anim);
    assertSame(subject, anim.getSubject());
    assertSame(scene, anim.getAsSeenBy());
  }

  @Test
  public void setPointOfViewBeginAcceptsExistingValueSentinel() {
    Scene scene = new Scene();
    Transformable subject = createSubject(scene);
    PointOfViewAnimation anim = new PointOfViewAnimation(subject, scene,
        AffineMatrix4x4.IDENTITY, AffineMatrix4x4.IDENTITY);
    anim.setPointOfViewBegin(PointOfViewAnimation.USE_EXISTING_VALUE_AT_RUN_TIME);
  }

  @Test
  public void setPointOfViewEndAcceptsNullToMeanNaN() {
    Scene scene = new Scene();
    Transformable subject = createSubject(scene);
    PointOfViewAnimation anim = new PointOfViewAnimation(subject, scene,
        AffineMatrix4x4.IDENTITY, AffineMatrix4x4.IDENTITY);
    anim.setPointOfViewEnd(null);
  }

  @Test
  public void completeRunsFullAnimationLifecycle() {
    Scene scene = new Scene();
    Transformable subject = createSubject(scene);
    AffineMatrix4x4 end = new AffineMatrix4x4(OrthogonalMatrix3x3.IDENTITY, new Point3(5, 0, 0));
    PointOfViewAnimation anim = new PointOfViewAnimation(subject, scene,
        AffineMatrix4x4.IDENTITY, end);
    anim.setDuration(0.1);
    anim.update(0.0, null);          // prologue + first update
    anim.update(0.05, null);         // mid-animation
    anim.complete(null);             // forces epilogue
    AffineMatrix4x4 result = subject.getTransformation(scene);
    assertEquals(5.0, result.translation().x(), 1e-6);
  }

  @Test
  public void useExistingValueAtRunTimeProvidesCurrentSubjectTransform() {
    Scene scene = new Scene();
    Transformable subject = createSubject(scene);
    subject.setTransformation(new AffineMatrix4x4(OrthogonalMatrix3x3.IDENTITY, new Point3(7, 8, 9)), scene);
    AffineMatrix4x4 end = new AffineMatrix4x4(OrthogonalMatrix3x3.IDENTITY, new Point3(0, 0, 0));
    PointOfViewAnimation anim = new PointOfViewAnimation(subject, scene,
        PointOfViewAnimation.USE_EXISTING_VALUE_AT_RUN_TIME, end);
    anim.setDuration(0.1);
    anim.update(0.0, null);
    anim.complete(null);
    AffineMatrix4x4 result = subject.getTransformation(scene);
    assertEquals(0.0, result.translation().x(), 1e-6);
    assertEquals(0.0, result.translation().y(), 1e-6);
  }
}
