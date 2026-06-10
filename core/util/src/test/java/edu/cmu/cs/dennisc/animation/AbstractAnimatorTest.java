package edu.cmu.cs.dennisc.animation;

import edu.cmu.cs.dennisc.TestWait;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class AbstractAnimatorTest {
  private static class TestAnimator extends AbstractAnimator {
    private double nextTime;
    private int updateCurrentTimeCount;
    private boolean acceptableThread = true;

    public void setNextTime(double nextTime) {
      this.nextTime = nextTime;
    }

    @Override
    protected void updateCurrentTime() {
      this.updateCurrentTimeCount++;
      this.setCurrentTime(this.nextTime);
    }

    @Override
    protected boolean isAcceptableThread() {
      return this.acceptableThread;
    }
  }

  private static final class QueueAnimation implements Animation {
    private final double[] remainingValues;
    private final Animated animated;
    private int updateIndex;
    private int updateCount;
    private int stopCount;
    private int completeCount;
    private AnimationObserver lastObserver;

    private QueueAnimation(Animated animated, double... remainingValues) {
      this.animated = animated;
      this.remainingValues = remainingValues.length == 0 ? new double[]{1.0} : remainingValues;
    }

    @Override
    public void reset() {
    }

    @Override
    public double update(double tCurrent, AnimationObserver animationObserver) {
      this.lastObserver = animationObserver;
      int index = Math.min(this.updateIndex, this.remainingValues.length - 1);
      this.updateIndex++;
      this.updateCount++;
      return this.remainingValues[index];
    }

    @Override
    public void stop() {
      this.stopCount++;
    }

    @Override
    public void complete(AnimationObserver animationObserver) {
      this.completeCount++;
      this.lastObserver = animationObserver;
    }

    @Override
    public Animated getAnimated() {
      return this.animated;
    }
  }

  private static final class LifecycleAnimation extends AbstractAnimation {
    private final double[] remainingValues;
    private final Animated animated;
    private int updateIndex;
    private int prologueCount;
    private int updateCount;
    private int preEpilogueCount;
    private int epilogueCount;

    private LifecycleAnimation(Animated animated, double... remainingValues) {
      this.animated = animated;
      this.remainingValues = remainingValues.length == 0 ? new double[]{1.0} : remainingValues;
    }

    @Override
    public Animated getAnimated() {
      return this.animated;
    }

    @Override
    protected void prologue() {
      this.prologueCount++;
    }

    @Override
    protected double update(double tDeltaSincePrologue, double tDeltaSinceLastUpdate, AnimationObserver animationObserver) {
      int index = Math.min(this.updateIndex, this.remainingValues.length - 1);
      this.updateIndex++;
      this.updateCount++;
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
  }

  private static final class SpyAnimated implements Animated {
    private int applyCount;

    @Override
    public void applyAnimation() {
      this.applyCount++;
    }
  }

  private static final class SpyFrameObserver implements FrameObserver {
    private final List<Double> updates = new ArrayList<>();
    private int completeCount;

    @Override
    public void update(double tCurrent) {
      this.updates.add(tCurrent);
    }

    @Override
    public void complete() {
      this.completeCount++;
    }
  }

  private static final class SpyObserver implements AnimationObserver {
    private int startedCount;
    private int finishedCount;

    @Override
    public void started(Animation animation) {
      this.startedCount++;
    }

    @Override
    public void finished(Animation animation) {
      this.finishedCount++;
    }
  }

  private static final class InterruptedAnimator extends TestAnimator {
    @Override
    public void invokeAndWait(Animation animation, AnimationObserver animationObserver) throws InterruptedException {
      throw new InterruptedException("interrupted");
    }
  }

  private static final class InvocationTargetThrowingAnimator extends TestAnimator {
    @Override
    public void invokeAndWait(Animation animation, AnimationObserver animationObserver) throws InvocationTargetException {
      throw new InvocationTargetException(new IllegalStateException("boom"));
    }
  }

  @Test
  public void speedFactorDefaultsToOneAndCanBeChanged() {
    TestAnimator animator = new TestAnimator();

    assertEquals(1.0, animator.getSpeedFactor(), 0.0);
    animator.setSpeedFactor(2.5);
    assertEquals(2.5, animator.getSpeedFactor(), 0.0);
  }

  @Test
  public void updateUsesSubclassProvidedCurrentTime() {
    TestAnimator animator = new TestAnimator();
    animator.setNextTime(4.5);

    animator.update();

    assertEquals(4.5, animator.getCurrentTime(), 0.0);
    assertEquals(1, animator.updateCurrentTimeCount);
  }

  @Test
  public void invokeLaterQueuesAnimationUntilFinished() {
    TestAnimator animator = new TestAnimator();
    QueueAnimation animation = new QueueAnimation(null, 3.0, 0.0);
    SpyObserver observer = new SpyObserver();

    animator.invokeLater(animation, observer);
    animator.setNextTime(1.0);
    animator.update();
    animator.setNextTime(2.0);
    animator.update();

    assertEquals(2, animation.updateCount);
    assertSame(observer, animation.lastObserver);
  }

  @Test
  public void updateAppliesEachAnimatedObjectOnlyOncePerFrame() {
    TestAnimator animator = new TestAnimator();
    SpyAnimated animated = new SpyAnimated();

    animator.invokeLater(new QueueAnimation(animated, 0.0), null);
    animator.invokeLater(new QueueAnimation(animated, 0.0), null);
    animator.setNextTime(1.0);
    animator.update();

    assertEquals(1, animated.applyCount);
  }

  @Test
  public void transitionToPausedRunsOneLastUpdateThenStops() {
    TestAnimator animator = new TestAnimator();
    QueueAnimation animation = new QueueAnimation(null, 4.0, 3.0, 2.0);

    animator.invokeLater(animation, null);
    animator.setNextTime(1.0);
    animator.update();
    animator.setSpeedFactor(0.0);
    animator.setNextTime(2.0);
    animator.update();
    animator.setNextTime(3.0);
    animator.update();

    assertEquals(2, animation.updateCount);
  }

  @Test
  public void frameObserversCanBeAddedAndRemoved() {
    TestAnimator animator = new TestAnimator();
    SpyFrameObserver observer = new SpyFrameObserver();

    animator.addFrameObserver(observer);
    animator.setNextTime(1.25);
    animator.update();
    animator.removeFrameObserver(observer);
    animator.setNextTime(2.5);
    animator.update();

    assertEquals(1, observer.updates.size());
    assertEquals(1.25, observer.updates.get(0), 0.0);
  }

  @Test
  public void completeAllCompletesAnimationsAndFrameObservers() {
    TestAnimator animator = new TestAnimator();
    QueueAnimation animation = new QueueAnimation(null, 10.0);
    SpyObserver observer = new SpyObserver();
    SpyFrameObserver frameObserver = new SpyFrameObserver();

    animator.invokeLater(animation, observer);
    animator.addFrameObserver(frameObserver);
    animator.completeAll();

    assertEquals(1, animation.completeCount);
    assertSame(observer, animation.lastObserver);
    assertEquals(1, frameObserver.completeCount);
  }

  @Test
  public void cancelAnimationStopsQueuedAnimationsAndClearsQueue() {
    TestAnimator animator = new TestAnimator();
    QueueAnimation animation = new QueueAnimation(null, 1.0);

    animator.invokeLater(animation, null);
    animator.cancelAnimation();
    animator.setNextTime(1.0);
    animator.update();

    assertEquals(1, animation.stopCount);
    assertEquals(0, animation.updateCount);
  }

  @Test
  public void invokeAndWaitBlocksUntilAnimationCompletes() throws Exception {
    TestAnimator animator = new TestAnimator();
    LifecycleAnimation animation = new LifecycleAnimation(null, 1.0, 0.0);
    SpyObserver observer = new SpyObserver();
    AtomicReference<Throwable> failure = new AtomicReference<>();

    Thread worker = new Thread(() -> {
      try {
        animator.invokeAndWait(animation, observer);
      } catch (Throwable t) {
        failure.set(t);
      }
    }, "animation-worker");
    worker.start();
    TestWait.until(
        () -> worker.getState() == Thread.State.WAITING,
        "animation worker to enter WAITING");
    assertEquals(Thread.State.WAITING, worker.getState());

    animator.setNextTime(0.0);
    animator.update();
    assertTrue(worker.isAlive());

    animator.setNextTime(1.0);
    animator.update();
    worker.join(1000L);

    assertNull(failure.get());
    assertFalse(worker.isAlive());
    assertEquals(1, animation.prologueCount);
    assertEquals(2, animation.updateCount);
    assertEquals(1, animation.preEpilogueCount);
    assertEquals(1, animation.epilogueCount);
    assertEquals(1, observer.startedCount);
    assertEquals(1, observer.finishedCount);
  }

  @Test
  public void invokeAndWaitThrowRuntimeExceptionsWrapsInterruptedException() {
    InterruptedAnimator animator = new InterruptedAnimator();

    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> animator.invokeAndWait_ThrowRuntimeExceptionsIfNecessary(new QueueAnimation(null, 0.0), null));

    assertTrue(exception.getCause() instanceof InterruptedException);
  }

  @Test
  public void invokeAndWaitThrowRuntimeExceptionsWrapsInvocationTargetException() {
    InvocationTargetThrowingAnimator animator = new InvocationTargetThrowingAnimator();

    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> animator.invokeAndWait_ThrowRuntimeExceptionsIfNecessary(new QueueAnimation(null, 0.0), null));

    assertTrue(exception.getCause() instanceof InvocationTargetException);
  }
}
