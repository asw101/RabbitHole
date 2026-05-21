package edu.cmu.cs.dennisc.color.animation;

import edu.cmu.cs.dennisc.animation.TraditionalStyle;
import edu.cmu.cs.dennisc.color.Color4f;
import org.junit.Assert;
import org.junit.Test;

public class Color4fAnimationTest {
  private static class TestAnimation extends Color4fAnimation {
    private Color4f value;

    private TestAnimation(Color4f start, Color4f end) {
      super(1.0, TraditionalStyle.BEGIN_AND_END_ABRUPTLY, start, end);
    }

    @Override
    protected void updateValue(Color4f value) {
      this.value = value;
    }

    private Color4f copy(Color4f other) {
      return newE(other);
    }

    private void applyPortion(double portion) {
      setPortion(portion);
    }
  }

  @Test
  public void newEReturnsCopyOrNull() {
    TestAnimation animation = new TestAnimation(Color4f.RED, Color4f.BLUE);

    Color4f copy = animation.copy(Color4f.GREEN);

    Assert.assertEquals(Color4f.GREEN, copy);
    Assert.assertNotSame(Color4f.GREEN, copy);
    Assert.assertNull(animation.copy(null));
  }

  @Test
  public void setPortionInterpolatesColor() {
    TestAnimation animation = new TestAnimation(Color4f.BLACK, Color4f.WHITE);

    animation.applyPortion(0.5);

    Assert.assertEquals(0.5f, animation.value.red, 0.0f);
    Assert.assertEquals(0.5f, animation.value.green, 0.0f);
    Assert.assertEquals(0.5f, animation.value.blue, 0.0f);
  }
}
