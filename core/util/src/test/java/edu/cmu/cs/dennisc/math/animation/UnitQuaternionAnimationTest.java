package edu.cmu.cs.dennisc.math.animation;

import edu.cmu.cs.dennisc.animation.TraditionalStyle;
import org.alice.math.immutable.UnitQuaternion;
import org.junit.Assert;
import org.junit.Test;

public class UnitQuaternionAnimationTest {
  private static class TestAnimation extends UnitQuaternionAnimation {
    private UnitQuaternion value;

    private TestAnimation(UnitQuaternion start, UnitQuaternion end) {
      super(1.0, TraditionalStyle.BEGIN_AND_END_ABRUPTLY, start, end);
    }

    @Override
    protected void updateValue(UnitQuaternion value) {
      this.value = value;
    }

    private UnitQuaternion copyOrNaN(UnitQuaternion other) {
      return newE(other);
    }

    private void applyPortion(double portion) {
      setPortion(portion);
    }
  }

  @Test
  public void newEReturnsNaNWhenGivenNull() {
    Assert.assertTrue(new TestAnimation(UnitQuaternion.IDENTITY, UnitQuaternion.NaN).copyOrNaN(null).isNaN());
  }

  @Test
  public void setPortionInterpolatesQuaternion() {
    TestAnimation animation = new TestAnimation(UnitQuaternion.IDENTITY, new UnitQuaternion(0.0, 1.0, 0.0, 0.0));

    animation.applyPortion(0.5);

    Assert.assertFalse(animation.value.isNaN());
    Assert.assertEquals(Math.sqrt(0.5), animation.value.y(), 1.0e-6);
    Assert.assertEquals(Math.sqrt(0.5), animation.value.w(), 1.0e-6);
  }
}
