package edu.cmu.cs.dennisc.animation;

import edu.cmu.cs.dennisc.animation.interpolation.DoubleAnimation;
import edu.cmu.cs.dennisc.animation.interpolation.FloatAnimation;
import edu.cmu.cs.dennisc.animation.interpolation.InterpolationAnimation;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class InterpolationAnimationTest {
  private static final double EPSILON = 1.0e-6;

  private static final class MutableValue {
    private double value;

    private MutableValue(double value) {
      this.value = value;
    }
  }

  private static final class MutableInterpolationAnimation extends InterpolationAnimation<MutableValue> {
    private List<MutableValue> newEInputs = new ArrayList<>();
    private final List<MutableValue> updatedValues = new ArrayList<>();
    private MutableValue lastUpdatedReference;

    private MutableInterpolationAnimation(Number duration, Style style, MutableValue v0, MutableValue v1) {
      super(duration, style, v0, v1);
    }

    @Override
    protected MutableValue newE(MutableValue other) {
      if (this.newEInputs == null) {
        this.newEInputs = new ArrayList<>();
      }
      this.newEInputs.add(other == null ? null : new MutableValue(other.value));
      return other == null ? new MutableValue(-1.0) : new MutableValue(other.value);
    }

    @Override
    protected MutableValue interpolate(MutableValue v0, MutableValue v1, double portion) {
      return new MutableValue(v0.value + ((v1.value - v0.value) * portion));
    }

    @Override
    protected void updateValue(MutableValue v) {
      this.lastUpdatedReference = v;
      this.updatedValues.add(new MutableValue(v.value));
    }

    private void applyPortion(double portion) {
      this.setPortion(portion);
    }

    private void runPrologue() {
      this.prologue();
    }

    private void runEpilogue() {
      this.epilogue();
    }
  }

  private static final class TestDoubleAnimation extends DoubleAnimation {
    private Double lastValue;

    private TestDoubleAnimation(Number duration, Style style, Double d0, Double d1) {
      super(duration, style, d0, d1);
    }

    @Override
    protected void updateValue(Double v) {
      this.lastValue = v;
    }

    private void applyPortion(double portion) {
      this.setPortion(portion);
    }
  }

  private static final class TestFloatAnimation extends FloatAnimation {
    private Float lastValue;

    private TestFloatAnimation(Number duration, Style style, Float f0, Float f1) {
      super(duration, style, f0, f1);
    }

    @Override
    protected void updateValue(Float v) {
      this.lastValue = v;
    }

    private void applyPortion(double portion) {
      this.setPortion(portion);
    }
  }

  @Test
  public void constructorCopiesEndpointsInsteadOfKeepingAliases() {
    MutableValue start = new MutableValue(1.0);
    MutableValue end = new MutableValue(5.0);
    MutableInterpolationAnimation animation = new MutableInterpolationAnimation(2.0,
        TraditionalStyle.BEGIN_AND_END_ABRUPTLY, start, end);

    start.value = 100.0;
    end.value = 200.0;
    animation.applyPortion(0.5);

    assertEquals(3.0, animation.updatedValues.get(0).value, EPSILON);
  }

  @Test
  public void zeroPortionUsesCopiedStartValue() {
    MutableValue start = new MutableValue(1.0);
    MutableInterpolationAnimation animation = new MutableInterpolationAnimation(2.0,
        TraditionalStyle.BEGIN_AND_END_ABRUPTLY, start, new MutableValue(5.0));

    start.value = 100.0;
    animation.applyPortion(0.0);

    assertEquals(1.0, animation.updatedValues.get(0).value, EPSILON);
  }

  @Test
  public void prologueDoesNotUpdateValue() {
    MutableInterpolationAnimation animation = new MutableInterpolationAnimation(2.0,
        TraditionalStyle.BEGIN_AND_END_ABRUPTLY, new MutableValue(1.0), new MutableValue(5.0));

    animation.runPrologue();

    assertTrue(animation.updatedValues.isEmpty());
  }

  @Test
  public void setPortionInterpolatesAndUpdatesValue() {
    MutableInterpolationAnimation animation = new MutableInterpolationAnimation(2.0,
        TraditionalStyle.BEGIN_AND_END_ABRUPTLY, new MutableValue(2.0), new MutableValue(6.0));

    animation.applyPortion(0.25);

    assertEquals(3.0, animation.updatedValues.get(0).value, EPSILON);
  }

  @Test
  public void epilogueUsesCopiedTargetValue() {
    MutableValue end = new MutableValue(5.0);
    MutableInterpolationAnimation animation = new MutableInterpolationAnimation(2.0,
        TraditionalStyle.BEGIN_AND_END_ABRUPTLY, new MutableValue(1.0), end);

    end.value = 20.0;
    animation.runEpilogue();

    assertNotSame(end, animation.lastUpdatedReference);
    assertEquals(5.0, animation.updatedValues.get(0).value, EPSILON);
  }

  @Test
  public void updateLifecycleUsesDurationBasedPortions() {
    MutableInterpolationAnimation animation = new MutableInterpolationAnimation(2.0,
        TraditionalStyle.BEGIN_AND_END_ABRUPTLY, new MutableValue(0.0), new MutableValue(10.0));

    animation.update(0.0, null);
    animation.update(1.0, null);
    double remaining = animation.update(2.0, null);

    assertEquals(0.0, animation.updatedValues.get(0).value, EPSILON);
    assertEquals(5.0, animation.updatedValues.get(1).value, EPSILON);
    assertEquals(10.0, animation.updatedValues.get(animation.updatedValues.size() - 1).value, EPSILON);
    assertEquals(0.0, remaining, EPSILON);
  }

  @Test
  public void completeEndsAtTargetValue() {
    MutableInterpolationAnimation animation = new MutableInterpolationAnimation(2.0,
        TraditionalStyle.BEGIN_AND_END_ABRUPTLY, new MutableValue(0.0), new MutableValue(10.0));

    animation.complete(null);

    assertEquals(10.0, animation.updatedValues.get(animation.updatedValues.size() - 1).value, EPSILON);
  }

  @Test
  public void doubleAnimationInterpolatesMidpoint() {
    TestDoubleAnimation animation = new TestDoubleAnimation(1.0, TraditionalStyle.BEGIN_AND_END_ABRUPTLY, 2.0, 6.0);

    animation.applyPortion(0.5);

    assertEquals(Double.valueOf(4.0), animation.lastValue);
  }

  @Test
  public void floatAnimationInterpolatesMidpoint() {
    TestFloatAnimation animation = new TestFloatAnimation(1.0, TraditionalStyle.BEGIN_AND_END_ABRUPTLY, 2.0f, 6.0f);

    animation.applyPortion(0.5);

    assertEquals(Float.valueOf(4.0f), animation.lastValue);
  }
}
