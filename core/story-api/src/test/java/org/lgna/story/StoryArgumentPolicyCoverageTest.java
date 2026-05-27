package org.lgna.story;

import edu.cmu.cs.dennisc.animation.TraditionalStyle;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class StoryArgumentPolicyCoverageTest {
  @Test
  public void animationStyleUsesExplicitDetailAndDefaultFallback() {
    assertSame(TraditionalStyle.BEGIN_AND_END_GENTLY, AnimationStyle.BEGIN_AND_END_GENTLY.getInternal());
    assertSame(AnimationStyle.BEGIN_ABRUPTLY_AND_END_GENTLY, AnimationStyle.getValue(new Object[] {
        AnimationStyle.BEGIN_ABRUPTLY_AND_END_GENTLY,
    }));
    assertSame(AnimationStyle.BEGIN_AND_END_GENTLY, AnimationStyle.getDefaultValue());
    assertSame(AnimationStyle.BEGIN_AND_END_GENTLY, AnimationStyle.getValue(new Object[] { "ignored" }));
  }

  @Test
  public void multipleEventPolicyUsesExplicitDetailAndDefaultFallback() {
    assertSame(MultipleEventPolicy.ENQUEUE, MultipleEventPolicy.getValue(new Object[] {
        MultipleEventPolicy.ENQUEUE,
    }));
    assertSame(MultipleEventPolicy.IGNORE, MultipleEventPolicy.getDefaultValue());
    assertSame(MultipleEventPolicy.COMBINE, MultipleEventPolicy.getValue(new Object[] {
        "ignored",
    }, MultipleEventPolicy.COMBINE));
  }

  @Test
  public void heldKeyPolicyUsesExplicitDetailAndDefaultFallback() {
    assertSame(HeldKeyPolicy.FIRE_ONCE_ON_RELEASE, HeldKeyPolicy.getValue(new Object[] {
        HeldKeyPolicy.FIRE_ONCE_ON_RELEASE,
    }));
    assertSame(HeldKeyPolicy.FIRE_MULTIPLE, HeldKeyPolicy.getDefaultValue());
    assertSame(HeldKeyPolicy.FIRE_ONCE_ON_PRESS, HeldKeyPolicy.getValue(new Object[] {
        42,
    }, HeldKeyPolicy.FIRE_ONCE_ON_PRESS));
  }

  @Test
  public void setOfVisualsReturnsCustomArrayOrDefault() {
    DummyVisual first = new DummyVisual(0.25);
    DummyVisual second = new DummyVisual(0.75);
    Visual[] expected = { first, second };

    assertArrayEquals(expected, SetOfVisuals.getValue(new Object[] {
        new SetOfVisuals(first, second),
    }));

    Visual[] defaultValue = (Visual[]) SetOfVisuals.getDefaultValue();
    assertSame(defaultValue, SetOfVisuals.getValue(new Object[] { "ignored" }));
    assertEquals(0.25, first.getOpacity(), 1.0e-9);
  }

  private static final class DummyVisual implements Visual {
    private double opacity;

    private DummyVisual(double opacity) {
      this.opacity = opacity;
    }

    @Override
    public Double getOpacity() {
      return this.opacity;
    }

    @Override
    public void setOpacity(Number opacity, SetOpacity.Detail... details) {
      this.opacity = opacity.doubleValue();
    }
  }
}
