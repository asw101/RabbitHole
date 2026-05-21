package edu.cmu.cs.dennisc.math.animation;

import edu.cmu.cs.dennisc.animation.TraditionalStyle;
import org.alice.math.immutable.Point3;
import org.junit.Assert;
import org.junit.Test;

public class Point3AnimationTest {
  private static class TestAnimation extends Point3Animation {
    private Point3 value;

    private TestAnimation(Point3 start, Point3 end) {
      super(1.0, TraditionalStyle.BEGIN_AND_END_ABRUPTLY, start, end);
    }

    @Override
    protected void updateValue(Point3 value) {
      this.value = value;
    }

    private Point3 copyOrNaN(Point3 other) {
      return newE(other);
    }

    private void applyPortion(double portion) {
      setPortion(portion);
    }
  }

  @Test
  public void newEReturnsNaNWhenGivenNull() {
    Assert.assertTrue(new TestAnimation(Point3.ORIGIN, new Point3(1.0, 1.0, 1.0)).copyOrNaN(null).isNaN());
  }

  @Test
  public void setPortionInterpolatesPoint3() {
    TestAnimation animation = new TestAnimation(Point3.ORIGIN, new Point3(1.0, 1.0, 1.0));

    animation.applyPortion(0.5);

    Assert.assertEquals(0.5, animation.value.x(), 0.0);
    Assert.assertEquals(0.5, animation.value.y(), 0.0);
    Assert.assertEquals(0.5, animation.value.z(), 0.0);
  }
}
