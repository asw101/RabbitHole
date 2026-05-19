package edu.cmu.cs.dennisc.animation;

import org.junit.Test;

import static org.junit.Assert.*;

public class DurationBasedAnimationDeepTest {

  private static class ConcreteDurationAnimation extends DurationBasedAnimation {
    double lastPortion = -1.0;

    ConcreteDurationAnimation() {
      super();
    }

    ConcreteDurationAnimation(Number duration) {
      super(duration);
    }

    ConcreteDurationAnimation(Number duration, Style style) {
      super(duration, style);
    }

    @Override
    protected void prologue() {
    }

    @Override
    protected void setPortion(double portion) {
      lastPortion = portion;
    }

    @Override
    protected void epilogue() {
    }

    double invokeUpdate(double deltaSincePrologue, double deltaSinceLastUpdate, AnimationObserver observer) {
      return update(deltaSincePrologue, deltaSinceLastUpdate, observer);
    }

    void invokePreEpilogue() {
      preEpilogue();
    }
  }

  private static class FixedStyle implements Style {
    private final double portion;

    FixedStyle(double portion) {
      this.portion = portion;
    }

    @Override
    public double calculatePortion(double timeElapsed, double timeTotal) {
      return portion;
    }
  }

  @Test
  public void defaultConstructor_duration1_styleGently() {
    ConcreteDurationAnimation animation = new ConcreteDurationAnimation();

    assertEquals(1.0, animation.getDuration(), 1e-10);
    assertSame(TraditionalStyle.BEGIN_AND_END_GENTLY, animation.getStyle());
  }

  @Test
  public void defaultDuration_isOne() {
    assertEquals(1.0, DurationBasedAnimation.DEFAULT_DURATION, 1e-10);
  }

  @Test
  public void defaultStyle_isBeginAndEndGently() {
    assertSame(TraditionalStyle.BEGIN_AND_END_GENTLY, DurationBasedAnimation.DEFAULT_STYLE);
  }

  @Test
  public void constructorWithDuration_setsValue() {
    assertEquals(2.5, new ConcreteDurationAnimation(2.5).getDuration(), 1e-10);
  }

  @Test
  public void constructorWithDuration_usesDefaultStyle() {
    assertSame(TraditionalStyle.BEGIN_AND_END_GENTLY, new ConcreteDurationAnimation(2.5).getStyle());
  }

  @Test
  public void constructorWithDurationAndStyle() {
    ConcreteDurationAnimation animation = new ConcreteDurationAnimation(3.0, TraditionalStyle.BEGIN_AND_END_ABRUPTLY);

    assertEquals(3.0, animation.getDuration(), 1e-10);
    assertSame(TraditionalStyle.BEGIN_AND_END_ABRUPTLY, animation.getStyle());
  }

  @Test
  public void setDuration_updatesDuration() {
    ConcreteDurationAnimation animation = new ConcreteDurationAnimation();

    animation.setDuration(5.0);

    assertEquals(5.0, animation.getDuration(), 1e-10);
  }

  @Test
  public void setDuration_acceptsInteger() {
    ConcreteDurationAnimation animation = new ConcreteDurationAnimation();

    animation.setDuration(3);

    assertEquals(3.0, animation.getDuration(), 1e-10);
  }

  @Test
  public void setStyle_updatesStyle() {
    ConcreteDurationAnimation animation = new ConcreteDurationAnimation();

    animation.setStyle(TraditionalStyle.BEGIN_GENTLY_AND_END_ABRUPTLY);

    assertSame(TraditionalStyle.BEGIN_GENTLY_AND_END_ABRUPTLY, animation.getStyle());
  }

  @Test
  public void setDuration_zeroDuration() {
    assertEquals(0.0, new ConcreteDurationAnimation(0.0).getDuration(), 1e-10);
  }

  @Test
  public void constructorWithIntegerDuration() {
    assertEquals(2.0, new ConcreteDurationAnimation(Integer.valueOf(2)).getDuration(), 1e-10);
  }

  @Test
  public void constructorWithFloatDuration() {
    assertEquals(1.5, new ConcreteDurationAnimation(Float.valueOf(1.5f)).getDuration(), 0.01);
  }

  @Test
  public void setDuration_negativeValue_isStored() {
    ConcreteDurationAnimation animation = new ConcreteDurationAnimation();

    animation.setDuration(-2.0);

    assertEquals(-2.0, animation.getDuration(), 1e-10);
  }

  @Test
  public void setStyle_canSwitchBackToDefaultStyle() {
    ConcreteDurationAnimation animation = new ConcreteDurationAnimation();

    animation.setStyle(TraditionalStyle.BEGIN_AND_END_ABRUPTLY);
    animation.setStyle(DurationBasedAnimation.DEFAULT_STYLE);

    assertSame(DurationBasedAnimation.DEFAULT_STYLE, animation.getStyle());
  }

  @Test
  public void update_positiveDuration_usesStyleCalculation() {
    ConcreteDurationAnimation animation = new ConcreteDurationAnimation(4.0, new FixedStyle(0.25));

    double remaining = animation.invokeUpdate(1.0, 0.5, null);

    assertEquals(0.25, animation.lastPortion, 1e-10);
    assertEquals(3.0, remaining, 1e-10);
  }

  @Test
  public void update_zeroDuration_setsPortionToOne() {
    ConcreteDurationAnimation animation = new ConcreteDurationAnimation(0.0, new FixedStyle(0.25));

    double remaining = animation.invokeUpdate(0.5, 0.1, null);

    assertEquals(1.0, animation.lastPortion, 1e-10);
    assertEquals(-0.5, remaining, 1e-10);
  }

  @Test
  public void preEpilogue_setsPortionToOne() {
    ConcreteDurationAnimation animation = new ConcreteDurationAnimation(2.0, new FixedStyle(0.1));

    animation.lastPortion = 0.1;
    animation.invokePreEpilogue();

    assertEquals(1.0, animation.lastPortion, 1e-10);
  }
}
