package org.alice.interact.animation;

import org.junit.Test;

import static org.junit.Assert.*;

/** Headless-safe tests for TargetBasedFrameObserver timing and state. */
public class TargetBasedFrameObserverTest {

  @Test
  public void defaultConstructorUsesDefaultSpeed() {
    assertEquals(12.0, new DoubleObserver().getSpeed(), 1e-6);
  }

  @Test
  public void constructorCopiesCurrentValue() {
    assertEquals(Double.valueOf(2.5), new DoubleObserver(2.5).getCurrentValue());
  }

  @Test
  public void observerStartsInternallyDoneUntilTargetIsReset() {
    DoubleObserver observer = new DoubleObserver(0.0, 1.0, 1.0);
    observer.update(0.0);
    observer.update(1.0);
    assertEquals(0.0, observer.getCurrentValue(), 1e-6);
  }

  @Test
  public void setSpeedUpdatesSpeed() {
    DoubleObserver observer = new DoubleObserver();
    observer.setSpeed(3.5);
    assertEquals(3.5, observer.getSpeed(), 1e-6);
  }

  @Test
  public void forceValueUpdateInvokesUpdateValue() {
    DoubleObserver observer = new DoubleObserver(1.0);
    observer.forceValueUpdate();
    assertEquals(Double.valueOf(1.0), observer.lastUpdatedValue);
    assertEquals(1, observer.updateCount);
  }

  @Test
  public void completeMovesCurrentValueToTarget() {
    DoubleObserver observer = new DoubleObserver(0.0, 5.0, 1.0);
    observer.complete();
    assertEquals(5.0, observer.getCurrentValue(), 1e-6);
    assertEquals(Double.valueOf(5.0), observer.lastUpdatedValue);
  }

  @Test
  public void setTargetActivatesObserverAndUpdateMovesTowardTarget() {
    DoubleObserver observer = new DoubleObserver(0.0, 0.0, 2.0);
    observer.setTarget(1.0);
    observer.update(0.0);
    observer.update(0.5);
    assertTrue(observer.getCurrentValue() > 0.0);
  }

  @Test
  public void largeFrameDeltaIsClampedToPointOneSeconds() {
    DoubleObserver observer = new DoubleObserver(0.0, 0.0, 1.0);
    observer.setTarget(5.0);
    observer.update(0.0);
    observer.update(5.0);
    assertEquals(0.1, observer.getCurrentValue(), 1e-6);
  }

  @Test
  public void nearTargetSetViaSetTargetLeavesObserverAtCurrentValue() {
    DoubleObserver observer = new DoubleObserver(0.0, 0.0, 10.0);
    observer.setTarget(0.0005);
    observer.update(0.0);
    observer.update(0.1);
    assertEquals(0.0, observer.getCurrentValue(), 1e-9);
  }

  @Test
  public void minDistanceConstantIsPositive() {
    assertTrue(DoubleObserver.minDistance() > 0.0);
  }

  private static final class DoubleObserver extends TargetBasedFrameObserver<Double> {
    private Double lastUpdatedValue;
    private int updateCount;

    private DoubleObserver() {
      super();
    }

    private DoubleObserver(Double currentValue) {
      super(currentValue);
    }

    private DoubleObserver(Double currentValue, Double targetValue, double speed) {
      super(currentValue, targetValue, speed);
    }

    private static double minDistance() {
      return MIN_DISTANCE_TO_DONE;
    }

    @Override
    protected void updateValue(Double value) {
      lastUpdatedValue = value;
      updateCount++;
    }

    @Override
    protected boolean isCloseEnoughToBeDone() {
      return Math.abs(targetValue - currentValue) <= MIN_DISTANCE_TO_DONE;
    }

    @Override
    public boolean isDone() {
      return Math.abs(targetValue - currentValue) <= MIN_DISTANCE_TO_DONE;
    }

    @Override
    protected Double interpolate(Double v0, Double v1, double deltaSinceLastUpdate) {
      double diff = v1 - v0;
      double maxStep = getSpeed() * deltaSinceLastUpdate;
      if (Math.abs(diff) <= maxStep) {
        return v1;
      }
      return v0 + (Math.signum(diff) * maxStep);
    }

    @Override
    protected Double newE(Double other) {
      return other == null ? null : Double.valueOf(other);
    }
  }
}
