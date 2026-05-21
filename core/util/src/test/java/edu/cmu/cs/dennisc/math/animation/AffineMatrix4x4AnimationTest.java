package edu.cmu.cs.dennisc.math.animation;

import edu.cmu.cs.dennisc.animation.TraditionalStyle;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.OrthogonalMatrix3x3;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Vector3;
import org.junit.Assert;
import org.junit.Test;

public class AffineMatrix4x4AnimationTest {
  private static class TestAnimation extends AffineMatrix4x4Animation {
    private AffineMatrix4x4 value;

    private TestAnimation(AffineMatrix4x4 start, AffineMatrix4x4 end) {
      super(1.0, TraditionalStyle.BEGIN_AND_END_ABRUPTLY, start, end);
    }

    @Override
    protected void updateValue(AffineMatrix4x4 value) {
      this.value = value;
    }

    private void runPrologue() {
      prologue();
    }

    private void applyPortion(double portion) {
      setPortion(portion);
    }

    private void runEpilogue() {
      epilogue();
    }
  }

  @Test
  public void lifecycleUpdatesStartInterpolatedAndEndValues() {
    OrthogonalMatrix3x3 identity = new OrthogonalMatrix3x3(Vector3.POSITIVE_X_AXIS, Vector3.POSITIVE_Y_AXIS, Vector3.POSITIVE_Z_AXIS);
    AffineMatrix4x4 start = new AffineMatrix4x4(identity, new Point3(0.0, 0.0, 0.0));
    AffineMatrix4x4 end = new AffineMatrix4x4(identity, new Point3(10.0, 20.0, 30.0));
    TestAnimation animation = new TestAnimation(start, end);

    animation.runPrologue();
    Assert.assertEquals(start, animation.value);

    animation.applyPortion(0.5);
    Assert.assertEquals(5.0, animation.value.translation().x(), 0.0);
    Assert.assertEquals(10.0, animation.value.translation().y(), 0.0);
    Assert.assertEquals(15.0, animation.value.translation().z(), 0.0);

    animation.runEpilogue();
    Assert.assertEquals(end, animation.value);
  }
}
