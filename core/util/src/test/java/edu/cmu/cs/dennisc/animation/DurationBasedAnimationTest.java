package edu.cmu.cs.dennisc.animation;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DurationBasedAnimationTest {
  private static final double EPSILON = 1.0e-6;

  private static final class TrackingStyle implements Style {
    private final double portionToReturn;
    private double lastElapsed = Double.NaN;
    private double lastTotal = Double.NaN;
    private int callCount;

    private TrackingStyle(double portionToReturn) {
      this.portionToReturn = portionToReturn;
    }

    @Override
    public double calculatePortion(double timeElapsed, double timeTotal) {
      this.callCount++;
      this.lastElapsed = timeElapsed;
      this.lastTotal = timeTotal;
      return this.portionToReturn;
    }
  }

  private static final class TrackingObserver implements DurationBasedAnimationObserver {
    private int startedCount;
    private int finishedCount;
    private int updatedCount;
    private double lastPortion = Double.NaN;
    private boolean breakOnUpdate;

    @Override
    public void started(Animation animation) {
      this.startedCount++;
    }

    @Override
    public void finished(Animation animation) {
      this.finishedCount++;
    }

    @Override
    public void updated(DurationBasedAnimation durationBasedAnimation, double portion) throws BreakException {
      this.updatedCount++;
      this.lastPortion = portion;
      if (this.breakOnUpdate) {
        throw new BreakException();
      }
    }
  }

  private static final class TrackingAnimation extends DurationBasedAnimation {
    private int prologueCount;
    private int epilogueCount;
    private double portionSeenByEpilogue = Double.NaN;
    private final List<Double> portions = new ArrayList<>();

    private TrackingAnimation() {
      super();
    }

    private TrackingAnimation(Number duration) {
      super(duration);
    }

    private TrackingAnimation(Number duration, Style style) {
      super(duration, style);
    }

    @Override
    protected void prologue() {
      this.prologueCount++;
    }

    @Override
    protected void setPortion(double portion) {
      this.portions.add(portion);
    }

    @Override
    protected void epilogue() {
      this.epilogueCount++;
      this.portionSeenByEpilogue = this.portions.get(this.portions.size() - 1);
    }
  }

  @Test
  public void defaultConstructorUsesDefaultDurationAndStyle() {
    TrackingAnimation animation = new TrackingAnimation();

    assertEquals(DurationBasedAnimation.DEFAULT_DURATION, animation.getDuration(), 0.0);
    assertSame(DurationBasedAnimation.DEFAULT_STYLE, animation.getStyle());
  }

  @Test
  public void durationConstructorUsesDefaultStyle() {
    TrackingAnimation animation = new TrackingAnimation(2.5);

    assertEquals(2.5, animation.getDuration(), 0.0);
    assertSame(DurationBasedAnimation.DEFAULT_STYLE, animation.getStyle());
  }

  @Test
  public void fullConstructorUsesProvidedValues() {
    TrackingStyle style = new TrackingStyle(0.5);
    TrackingAnimation animation = new TrackingAnimation(3.0, style);

    assertEquals(3.0, animation.getDuration(), 0.0);
    assertSame(style, animation.getStyle());
  }

  @Test
  public void setDurationUpdatesValue() {
    TrackingAnimation animation = new TrackingAnimation();

    animation.setDuration(4);

    assertEquals(4.0, animation.getDuration(), 0.0);
  }

  @Test
  public void setStyleUpdatesValue() {
    TrackingAnimation animation = new TrackingAnimation();
    TrackingStyle style = new TrackingStyle(0.75);

    animation.setStyle(style);

    assertSame(style, animation.getStyle());
  }

  @Test
  public void updateUsesStyleAndNotifiesDurationObserver() {
    TrackingStyle style = new TrackingStyle(0.25);
    TrackingAnimation animation = new TrackingAnimation(4.0, style);
    TrackingObserver observer = new TrackingObserver();

    double remaining = animation.update(3.0, observer);

    assertEquals(4.0, remaining, EPSILON);
    assertEquals(1, style.callCount);
    assertEquals(0.0, style.lastElapsed, EPSILON);
    assertEquals(4.0, style.lastTotal, EPSILON);
    assertEquals(1, observer.startedCount);
    assertEquals(1, observer.updatedCount);
    assertEquals(0.25, observer.lastPortion, EPSILON);
    assertEquals(0.25, animation.portions.get(0), EPSILON);
  }

  @Test
  public void secondUpdateReturnsRemainingDuration() {
    TrackingAnimation animation = new TrackingAnimation(2.0, TraditionalStyle.BEGIN_AND_END_ABRUPTLY);

    animation.update(5.0, null);
    double remaining = animation.update(6.25, null);

    assertEquals(0.75, remaining, EPSILON);
  }

  @Test
  public void zeroDurationFinishesImmediatelyAtFullPortion() {
    TrackingAnimation animation = new TrackingAnimation(0.0, TraditionalStyle.BEGIN_AND_END_ABRUPTLY);
    TrackingObserver observer = new TrackingObserver();

    double remaining = animation.update(1.0, observer);

    assertEquals(0.0, remaining, EPSILON);
    assertEquals(1.0, animation.portions.get(0), EPSILON);
    assertEquals(1.0, animation.portions.get(animation.portions.size() - 1), EPSILON);
    assertEquals(1.0, animation.portionSeenByEpilogue, EPSILON);
    assertEquals(1, observer.finishedCount);
  }

  @Test
  public void negativeDurationAlsoFinishesImmediatelyAtFullPortion() {
    TrackingAnimation animation = new TrackingAnimation(-1.0, TraditionalStyle.BEGIN_AND_END_ABRUPTLY);

    double remaining = animation.update(1.0, null);

    assertEquals(0.0, remaining, EPSILON);
    assertEquals(1.0, animation.portions.get(0), EPSILON);
    assertEquals(1.0, animation.portionSeenByEpilogue, EPSILON);
  }

  @Test
  public void breakExceptionStopsAnimationAndStillFinishesLifecycle() {
    TrackingAnimation animation = new TrackingAnimation(4.0, TraditionalStyle.BEGIN_AND_END_ABRUPTLY);
    TrackingObserver observer = new TrackingObserver();
    observer.breakOnUpdate = true;

    double remaining = animation.update(2.0, observer);

    assertEquals(0.0, remaining, EPSILON);
    assertEquals(1, observer.startedCount);
    assertEquals(1, observer.updatedCount);
    assertEquals(1, observer.finishedCount);
    assertEquals(1, animation.epilogueCount);
    assertEquals(1.0, animation.portionSeenByEpilogue, EPSILON);
  }

  @Test
  public void completeForcesFinalPortionToOne() {
    TrackingStyle style = new TrackingStyle(0.3);
    TrackingAnimation animation = new TrackingAnimation(5.0, style);

    animation.complete(null);

    assertEquals(1, animation.prologueCount);
    assertEquals(1, style.callCount);
    assertEquals(0.3, animation.portions.get(0), EPSILON);
    assertEquals(1.0, animation.portions.get(animation.portions.size() - 1), EPSILON);
    assertEquals(1.0, animation.portionSeenByEpilogue, EPSILON);
    assertEquals(1, animation.epilogueCount);
  }
}
