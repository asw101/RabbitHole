package edu.cmu.cs.dennisc.animation;

import edu.cmu.cs.dennisc.TestWait;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class WaitingAnimationTest {
  private static final class SpyAnimated implements Animated {
    private int applyCount;

    @Override
    public void applyAnimation() {
      this.applyCount++;
    }
  }

  private static final class SpyObserver implements AnimationObserver {
    @Override
    public void started(Animation animation) {
    }

    @Override
    public void finished(Animation animation) {
    }

    @Override
    public String toString() {
      return "observer";
    }
  }

  private static final class SpyAnimation implements Animation {
    private final double[] remainingValues;
    private final Animated animated;
    private int updateIndex;
    private int updateCount;
    private int stopCount;
    private int completeCount;
    private double lastCurrentTime = Double.NaN;
    private AnimationObserver lastObserver;

    private SpyAnimation(Animated animated, double... remainingValues) {
      this.animated = animated;
      this.remainingValues = remainingValues.length == 0 ? new double[]{1.0} : remainingValues;
    }

    @Override
    public void reset() {
    }

    @Override
    public double update(double tCurrent, AnimationObserver animationObserver) {
      this.updateCount++;
      this.lastCurrentTime = tCurrent;
      this.lastObserver = animationObserver;
      int index = Math.min(this.updateIndex, this.remainingValues.length - 1);
      this.updateIndex++;
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

    @Override
    public String toString() {
      return "animation";
    }
  }

  @Test
  public void getExceptionDefaultsToNull() {
    WaitingAnimation waitingAnimation = new WaitingAnimation(new SpyAnimation(null, 1.0), null, null);

    assertNull(waitingAnimation.getException());
  }

  @Test
  public void setExceptionStoresException() {
    WaitingAnimation waitingAnimation = new WaitingAnimation(new SpyAnimation(null, 1.0), null, null);
    Exception exception = new IllegalStateException("boom");

    waitingAnimation.setException(exception);

    assertSame(exception, waitingAnimation.getException());
  }

  @Test
  public void getAnimatedDelegatesToAnimation() {
    SpyAnimated animated = new SpyAnimated();
    WaitingAnimation waitingAnimation = new WaitingAnimation(new SpyAnimation(animated, 1.0), null, null);

    assertSame(animated, waitingAnimation.getAnimated());
  }

  @Test
  public void updateReturnsFalseWhenAnimationNeedsMoreTime() {
    SpyObserver observer = new SpyObserver();
    SpyAnimation animation = new SpyAnimation(null, 2.5);
    WaitingAnimation waitingAnimation = new WaitingAnimation(animation, observer, null);

    boolean finished = waitingAnimation.update(4.0);

    assertFalse(finished);
    assertEquals(1, animation.updateCount);
    assertEquals(4.0, animation.lastCurrentTime, 0.0);
    assertSame(observer, animation.lastObserver);
  }

  @Test
  public void updateReturnsTrueWhenAnimationReturnsZero() {
    SpyAnimation animation = new SpyAnimation(null, 0.0);
    WaitingAnimation waitingAnimation = new WaitingAnimation(animation, null, null);

    assertTrue(waitingAnimation.update(1.0));
  }

  @Test
  public void updateReturnsTrueWhenAnimationReturnsNegativeValue() {
    SpyAnimation animation = new SpyAnimation(null, -0.5);
    WaitingAnimation waitingAnimation = new WaitingAnimation(animation, null, null);

    assertTrue(waitingAnimation.update(1.0));
  }

  @Test
  public void stopDelegatesToAnimation() {
    SpyAnimation animation = new SpyAnimation(null, 1.0);
    WaitingAnimation waitingAnimation = new WaitingAnimation(animation, null, null);

    waitingAnimation.stop();

    assertEquals(1, animation.stopCount);
  }

  @Test
  public void completeDelegatesToAnimation() {
    SpyObserver observer = new SpyObserver();
    SpyAnimation animation = new SpyAnimation(null, 1.0);
    WaitingAnimation waitingAnimation = new WaitingAnimation(animation, observer, null);

    waitingAnimation.complete();

    assertEquals(1, animation.completeCount);
    assertSame(observer, animation.lastObserver);
  }

  @Test
  public void toStringIncludesAnimationObserverAndThread() {
    Thread thread = new Thread("waiter");
    WaitingAnimation waitingAnimation = new WaitingAnimation(new SpyAnimation(null, 1.0), new SpyObserver(), thread);

    String text = waitingAnimation.toString();

    assertTrue(text.contains("WaitingAnimation"));
    assertTrue(text.contains("animation=animation"));
    assertTrue(text.contains("observer=observer"));
    assertTrue(text.contains("thread="));
    assertTrue(text.contains("waiter"));
  }

  @Test
  public void finishingUpdateNotifiesWaitingThread() throws Exception {
    CountDownLatch ready = new CountDownLatch(1);
    CountDownLatch awakened = new CountDownLatch(1);
    Thread waiter = new Thread("waiter") {
      @Override
      public void run() {
        synchronized (this) {
          ready.countDown();
          try {
            this.wait();
            awakened.countDown();
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
        }
      }
    };
    waiter.start();
    assertTrue(ready.await(1, TimeUnit.SECONDS));
    TestWait.until(
        () -> waiter.getState() == Thread.State.WAITING,
        "waiting animation worker to enter WAITING");
    assertEquals(Thread.State.WAITING, waiter.getState());

    WaitingAnimation waitingAnimation = new WaitingAnimation(new SpyAnimation(null, 0.0), null, waiter);
    waitingAnimation.update(0.0);

    assertTrue(awakened.await(1, TimeUnit.SECONDS));
    waiter.join(500L);
    assertFalse(waiter.isAlive());
  }

  @Test
  public void animatedObjectIsNotAppliedByWaitingAnimationItself() {
    SpyAnimated animated = new SpyAnimated();
    WaitingAnimation waitingAnimation = new WaitingAnimation(new SpyAnimation(animated, 0.0), null, null);

    waitingAnimation.update(0.0);

    assertEquals(0, animated.applyCount);
  }
}
