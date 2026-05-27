package org.lgna.story.implementation;

import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.UnitQuaternion;
import org.junit.Test;
import org.lgna.story.Orientation;
import org.lgna.story.Position;
import org.lgna.story.resources.BipedResource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class JointIdTransformationPairTest {
  @Test
  public void affineMatrixConstructorPreservesTranslationAndFlag() {
    AffineMatrix4x4 transformation = new AffineMatrix4x4(
        UnitQuaternion.IDENTITY.asMatrix3x3(),
        new Point3(1.0, 2.0, 3.0)
    );
    JointIdTransformationPair pair = new JointIdTransformationPair(
        BipedResource.HEAD,
        transformation,
        false
    );

    assertSame(BipedResource.HEAD, pair.getJointId());
    assertSame(transformation, pair.getTransformation());
    assertFalse(pair.affectsTranslation());
    assertTrue(pair.orientationOnly());
  }

  @Test
  public void orientationAndPositionConstructorBuildsTranslatedTransformation() {
    JointIdTransformationPair pair = new JointIdTransformationPair(
        BipedResource.LEFT_HIP,
        new Orientation(0, 0, 0, 1),
        new Position(4, 5, 6)
    );

    assertTrue(pair.affectsTranslation());
    assertFalse(pair.orientationOnly());
    assertEquals(4.0, pair.getTransformation().translation().x(), 1e-9);
    assertEquals(5.0, pair.getTransformation().translation().y(), 1e-9);
    assertEquals(6.0, pair.getTransformation().translation().z(), 1e-9);
  }

  @Test
  public void quaternionOnlyConstructorMarksPairAsOrientationOnly() {
    JointIdTransformationPair pair = new JointIdTransformationPair(
        BipedResource.RIGHT_HIP,
        UnitQuaternion.IDENTITY
    );

    assertFalse(pair.affectsTranslation());
    assertTrue(pair.orientationOnly());
    assertEquals(0.0, pair.getTransformation().translation().x(), 1e-9);
    assertEquals(0.0, pair.getTransformation().translation().y(), 1e-9);
    assertEquals(0.0, pair.getTransformation().translation().z(), 1e-9);
  }
}
