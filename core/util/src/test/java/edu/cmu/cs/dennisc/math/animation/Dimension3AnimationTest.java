package edu.cmu.cs.dennisc.math.animation;

import edu.cmu.cs.dennisc.animation.TraditionalStyle;
import org.alice.math.immutable.Dimension3;
import org.junit.Assert;
import org.junit.Test;

public class Dimension3AnimationTest {
  private static class TestAnimation extends Dimension3Animation {
    private Dimension3 value;

    private TestAnimation(Dimension3 start, Dimension3 end) {
      super(1.0, TraditionalStyle.BEGIN_AND_END_ABRUPTLY, start, end);
    }

    @Override
    protected void updateValue(Dimension3 value) {
      this.value = value;
    }

    private Dimension3 copyOrNaN(Dimension3 other) {
      return newE(other);
    }

    private void applyPortion(double portion) {
      setPortion(portion);
    }
  }

  @Test
  public void newEReturnsNaNWhenGivenNull() {
    Assert.assertTrue(new TestAnimation(new Dimension3(0.0, 0.0, 0.0), Dimension3.UNIT_SIZE).copyOrNaN(null).isNaN());
  }

  @Test
  public void setPortionInterpolatesDimension3() {
    TestAnimation animation = new TestAnimation(new Dimension3(0.0, 0.0, 0.0), Dimension3.UNIT_SIZE);

    animation.applyPortion(0.25);

    Assert.assertEquals(0.25, animation.value.x(), 0.0);
    Assert.assertEquals(0.25, animation.value.y(), 0.0);
    Assert.assertEquals(0.25, animation.value.z(), 0.0);
  }
}
