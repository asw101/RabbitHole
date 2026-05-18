package edu.cmu.cs.dennisc.animation;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class AbstractAnimationTest {
  private static final double EPSILON = 1.0e-6;

  private static final class SpyObserver implements AnimationObserver {
    private int startedCount;
    private int finishedCount;
    private final List<Animation> startedAnimations = new ArrayList<>();
    private final List<Animation> finishedAnimations = new ArrayList<>();

    @Override
    public void started(Animation animation) {
      this.startedCount++;
      this.startedAnimations.add(animation);
    }

    @Override
    public void finished(Animation animation) {
      this.finishedCount++;
      this.finishedAnimations.add(animation);
    }
  }

  private static final class SpyAnimation extends AbstractAnimation {
    private final double[] remainingValues;
    private int remainingIndex;
    private int prologueCount;
    private int updateCount;
    private int preEpilogueCount;
    private int epilogueCount;
    private int stopCount;
    private final List<Double> deltaSincePrologueValues = new ArrayList<>();
    private final List<Double> deltaSinceLastUpdateValues = new ArrayList<>();

    private SpyAnimation(double... remainingValues) {
      this.remainingValues = remainingValues.length == 0 ? new double[]{1.0} : remainingValues;
    }

    @Override
    protected void prologue() {
      this.prologueCount++;
    }

    @Override
    protected double update(double tDeltaSincePrologue, double tDeltaSinceLastUpdate, AnimationObserver animationObserver) {
      this.updateCount++;
      this.deltaSincePrologueValues.add(tDeltaSincePrologue);
      this.deltaSinceLastUpdateValues.add(tDeltaSinceLastUpdate);
      int index = Math.min(this.remainingIndex, this.remainingValues.length - 1);
      this.remainingIndex++;
      return this.remainingValues[index];
    }

    @Override
    protected void preEpilogue() {
      this.preEpilogueCount++;
    }

    @Override
    protected void epilogue() {
      this.epilogueCount++;
    }

    @Override
    public void stop() {
      this.stopCount++;
      super.stop();
    }
  }

  @Test
  public void updateStartsLifecycleAndNotifiesObserver() {
    SpyAnimation animation = new SpyAnimation(2.0);
    SpyObserver observer = new SpyObserver();

    double remaining = animation.update(5.0, observer);

    assertEquals(2.0, remaining, EPSILON);
    assertEquals(1, animation.prologueCount);
    assertEquals(1, animation.updateCount);
    assertEquals(0.0, animation.deltaSincePrologueValues.get(0), EPSILON);
    assertTrue(Double.isNaN(animation.deltaSinceLastUpdateValues.get(0)));
    assertEquals(1, observer.startedCount);
    assertEquals(0, observer.finishedCount);
    assertSame(animation, observer.startedAnimations.get(0));
  }

  @Test
  public void secondUpdateUsesElapsedTimeAndPreviousTime() {
    SpyAnimation animation = new SpyAnimation(5.0, 3.0);

    animation.update(2.0, null);
    double remaining = animation.update(2.75, null);

    assertEquals(3.0, remaining, EPSILON);
    assertEquals(2, animation.updateCount);
    assertEquals(0.75, animation.deltaSincePrologueValues.get(1), EPSILON);
    assertEquals(0.75, animation.deltaSinceLastUpdateValues.get(1), EPSILON);
  }

  @Test
  public void zeroRemainingFinishesWithinSameUpdate() {
    SpyAnimation animation = new SpyAnimation(0.0);
    SpyObserver observer = new SpyObserver();

    double remaining = animation.update(3.0, observer);

    assertEquals(0.0, remaining, EPSILON);
    assertEquals(1, animation.preEpilogueCount);
    assertEquals(1, animation.epilogueCount);
    assertEquals(1, observer.startedCount);
    assertEquals(1, observer.finishedCount);
    assertSame(animation, observer.finishedAnimations.get(0));
  }

  @Test
  public void updateAfterCompletionReturnsZeroWithoutExtraLifecycle() {
    SpyAnimation animation = new SpyAnimation(0.0);

    animation.update(1.0, null);
    double remaining = animation.update(2.0, null);

    assertEquals(0.0, remaining, EPSILON);
    assertEquals(1, animation.prologueCount);
    assertEquals(1, animation.updateCount);
    assertEquals(1, animation.preEpilogueCount);
    assertEquals(1, animation.epilogueCount);
  }

  @Test
  public void resetRestartsLifecycleAndClearsPreviousTime() {
    SpyAnimation animation = new SpyAnimation(5.0, 4.0);

    animation.update(1.0, null);
    animation.reset();
    animation.update(10.0, null);

    assertEquals(2, animation.prologueCount);
    assertEquals(2, animation.updateCount);
    assertEquals(0.0, animation.deltaSincePrologueValues.get(1), EPSILON);
    assertTrue(Double.isNaN(animation.deltaSinceLastUpdateValues.get(1)));
  }

  @Test
  public void completeRunsFullLifecycleAndAllowsReuse() {
    SpyAnimation animation = new SpyAnimation(9.0, 4.0);
    SpyObserver observer = new SpyObserver();

    animation.complete(observer);
    double remaining = animation.update(7.0, observer);

    assertEquals(2, animation.prologueCount);
    assertEquals(2, animation.updateCount);
    assertEquals(1, animation.preEpilogueCount);
    assertEquals(1, animation.epilogueCount);
    assertEquals(0.0, animation.deltaSincePrologueValues.get(0), EPSILON);
    assertEquals(0.0, animation.deltaSinceLastUpdateValues.get(0), EPSILON);
    assertEquals(2, observer.startedCount);
    assertEquals(1, observer.finishedCount);
    assertEquals(4.0, remaining, EPSILON);
  }

  @Test
  public void completeWithNullObserverStillRunsLifecycle() {
    SpyAnimation animation = new SpyAnimation(1.0);

    animation.complete(null);

    assertEquals(1, animation.prologueCount);
    assertEquals(1, animation.updateCount);
    assertEquals(1, animation.preEpilogueCount);
    assertEquals(1, animation.epilogueCount);
  }

  @Test
  public void completeImmediatelyAfterResetDoesNothing() {
    SpyAnimation animation = new SpyAnimation(1.0);

    animation.reset();
    animation.complete(null);

    assertEquals(0, animation.prologueCount);
    assertEquals(0, animation.updateCount);
    assertEquals(0, animation.preEpilogueCount);
    assertEquals(0, animation.epilogueCount);
  }

  @Test
  public void stopCanBeCalledWithoutChangingFutureUpdates() {
    SpyAnimation animation = new SpyAnimation(6.0);

    animation.stop();
    double remaining = animation.update(4.0, null);

    assertEquals(1, animation.stopCount);
    assertEquals(1, animation.prologueCount);
    assertEquals(1, animation.updateCount);
    assertEquals(6.0, remaining, EPSILON);
  }
}
