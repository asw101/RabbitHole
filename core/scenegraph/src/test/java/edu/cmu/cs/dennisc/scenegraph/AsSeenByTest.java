package edu.cmu.cs.dennisc.scenegraph;

import org.alice.math.immutable.AffineMatrix4x4;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class AsSeenByTest {
  @Test
  public void sceneReferenceFrameReportsSceneVehicleAndLocalRelationships() {
    Scene scene = new Scene();
    Transformable child = new Transformable();
    Transformable grandchild = new Transformable();
    child.setParent(scene);
    grandchild.setParent(child);

    assertTrue(AsSeenBy.SCENE.isSceneOf(child));
    assertTrue(AsSeenBy.SCENE.isSceneOf(grandchild));
    assertTrue(AsSeenBy.SCENE.isVehicleOf(child));
    assertFalse(AsSeenBy.SCENE.isVehicleOf(grandchild));
    assertTrue(AsSeenBy.SCENE.isLocalOf(scene));
    assertFalse(AsSeenBy.SCENE.isLocalOf(child));
    assertSame(AffineMatrix4x4.IDENTITY, AsSeenBy.SCENE.getAbsoluteTransformation());
    assertSame(AffineMatrix4x4.IDENTITY, AsSeenBy.SCENE.getInverseAbsoluteTransformation());
  }

  @Test
  public void sceneTransformationDelegatesToOtherInverseAbsoluteTransformation() {
    AffineMatrix4x4 inverse = AffineMatrix4x4.createTranslation(1.0, 2.0, 3.0);
    ReferenceFrame frame = new ReferenceFrame() {
      @Override
      public boolean isSceneOf(Component other) {
        return false;
      }

      @Override
      public boolean isVehicleOf(Component other) {
        return false;
      }

      @Override
      public boolean isLocalOf(Component other) {
        return false;
      }

      @Override
      public AffineMatrix4x4 getInverseAbsoluteTransformation() {
        return inverse;
      }

      @Override
      public AffineMatrix4x4 getAbsoluteTransformation() {
        return AffineMatrix4x4.IDENTITY;
      }

      @Override
      public AffineMatrix4x4 getTransformation(ReferenceFrame other) {
        return AffineMatrix4x4.IDENTITY;
      }
    };

    assertSame(inverse, AsSeenBy.SCENE.getTransformation(frame));
  }

  @Test
  public void parentAndSelfTransformationsInvertTheOtherFrameTransformation() {
    ReferenceFrame frame = new ReferenceFrame() {
      @Override
      public boolean isSceneOf(Component other) {
        return false;
      }

      @Override
      public boolean isVehicleOf(Component other) {
        return false;
      }

      @Override
      public boolean isLocalOf(Component other) {
        return false;
      }

      @Override
      public AffineMatrix4x4 getInverseAbsoluteTransformation() {
        return AffineMatrix4x4.IDENTITY;
      }

      @Override
      public AffineMatrix4x4 getAbsoluteTransformation() {
        return AffineMatrix4x4.IDENTITY;
      }

      @Override
      public AffineMatrix4x4 getTransformation(ReferenceFrame other) {
        if (other == AsSeenBy.PARENT) {
          return AffineMatrix4x4.createTranslation(2.0, 0.0, 0.0);
        }
        if (other == AsSeenBy.SELF) {
          return AffineMatrix4x4.createTranslation(0.0, 3.0, 0.0);
        }
        return AffineMatrix4x4.IDENTITY;
      }
    };

    assertTrue(AsSeenBy.PARENT.getTransformation(frame).isWithinReasonableEpsilonOf(AffineMatrix4x4.createTranslation(-2.0, 0.0, 0.0)));
    assertTrue(AsSeenBy.SELF.getTransformation(frame).isWithinReasonableEpsilonOf(AffineMatrix4x4.createTranslation(0.0, -3.0, 0.0)));
  }

  @Test
  public void parentAndSelfAbsoluteTransformationMethodsCurrentlyThrow() {
    expectRuntimeException(() -> AsSeenBy.PARENT.getAbsoluteTransformation());
    expectRuntimeException(() -> AsSeenBy.PARENT.getInverseAbsoluteTransformation());
    expectRuntimeException(() -> AsSeenBy.SELF.getAbsoluteTransformation());
    expectRuntimeException(() -> AsSeenBy.SELF.getInverseAbsoluteTransformation());
  }

  private static void expectRuntimeException(Runnable action) {
    try {
      action.run();
    } catch (RuntimeException expected) {
      return;
    }
    throw new AssertionError("Expected RuntimeException");
  }
}
