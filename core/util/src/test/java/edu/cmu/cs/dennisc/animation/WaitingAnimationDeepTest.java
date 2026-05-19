package edu.cmu.cs.dennisc.animation;

import org.junit.Test;

import static org.junit.Assert.*;

public class WaitingAnimationDeepTest {

  private static class SimpleAnimation implements Animation {
    double returnValue = 1.0;
    boolean stopCalled = false;
    boolean completeCalled = false;
    AnimationObserver lastObserver = null;

    @Override
    public Animated getAnimated() {
      return null;
    }

    @Override
    public double update(double t, AnimationObserver o) {
      lastObserver = o;
      return returnValue;
    }

    @Override
    public void reset() {
    }

    @Override
    public void stop() {
      stopCalled = true;
    }

    @Override
    public void complete(AnimationObserver o) {
      completeCalled = true;
      lastObserver = o;
    }
  }

  @Test
  public void getException_initiallyNull() {
    assertNull(new WaitingAnimation(new SimpleAnimation(), null, null).getException());
  }

  @Test
  public void setException_getException_roundTrip() {
    WaitingAnimation waitingAnimation = new WaitingAnimation(new SimpleAnimation(), null, null);
    Exception exception = new RuntimeException("test");

    waitingAnimation.setException(exception);

    assertSame(exception, waitingAnimation.getException());
  }

  @Test
  public void setException_overwritesPreviousException() {
    WaitingAnimation waitingAnimation = new WaitingAnimation(new SimpleAnimation(), null, null);
    Exception first = new RuntimeException("first");
    Exception second = new IllegalStateException("second");

    waitingAnimation.setException(first);
    waitingAnimation.setException(second);

    assertSame(second, waitingAnimation.getException());
  }

  @Test
  public void getAnimated_delegatesToAnimation() {
    assertNull(new WaitingAnimation(new SimpleAnimation(), null, null).getAnimated());
  }

  @Test
  public void update_returnsFalse_whenTimeRemaining() {
    SimpleAnimation animation = new SimpleAnimation();
    animation.returnValue = 5.0;

    assertFalse(new WaitingAnimation(animation, null, null).update(0.5));
  }

  @Test
  public void update_returnsTrue_whenNoTimeRemaining() {
    SimpleAnimation animation = new SimpleAnimation();
    animation.returnValue = 0.0;

    assertTrue(new WaitingAnimation(animation, null, null).update(1.0));
  }

  @Test
  public void update_returnsTrue_whenNegativeTimeRemaining() {
    SimpleAnimation animation = new SimpleAnimation();
    animation.returnValue = -1.0;

    assertTrue(new WaitingAnimation(animation, null, null).update(2.0));
  }

  @Test
  public void update_passesObserverToAnimation() {
    SimpleAnimation animation = new SimpleAnimation();
    AnimationObserver observer = new AnimationObserver() {
      @Override
      public void started(Animation an) {
      }

      @Override
      public void finished(Animation an) {
      }
    };

    new WaitingAnimation(animation, observer, null).update(1.0);

    assertSame(observer, animation.lastObserver);
  }

  @Test
  public void stop_callsAnimationStop() {
    SimpleAnimation animation = new SimpleAnimation();

    new WaitingAnimation(animation, null, null).stop();

    assertTrue(animation.stopCalled);
  }

  @Test
  public void complete_callsAnimationComplete() {
    SimpleAnimation animation = new SimpleAnimation();
    AnimationObserver observer = new AnimationObserver() {
      @Override
      public void started(Animation an) {
      }

      @Override
      public void finished(Animation an) {
      }
    };

    new WaitingAnimation(animation, observer, null).complete();

    assertTrue(animation.completeCalled);
    assertSame(observer, animation.lastObserver);
  }

  @Test
  public void complete_withNullObserver_usesNullObserver() {
    SimpleAnimation animation = new SimpleAnimation();

    new WaitingAnimation(animation, null, null).complete();

    assertTrue(animation.completeCalled);
    assertNull(animation.lastObserver);
  }

  @Test
  public void toString_containsClassName() {
    assertTrue(new WaitingAnimation(new SimpleAnimation(), null, null).toString().contains("WaitingAnimation"));
  }

  @Test
  public void toString_containsAnimation() {
    assertTrue(new WaitingAnimation(new SimpleAnimation(), null, null).toString().contains("animation="));
  }

  @Test
  public void toString_containsThreadNameWhenProvided() {
    Thread thread = new Thread("waiting-thread");
    String text = new WaitingAnimation(new SimpleAnimation(), null, thread).toString();

    assertTrue(text.contains("waiting-thread"));
  }

  @Test
  public void update_passesCorrectTime() {
    final double[] received = { -1.0 };
    Animation animation = new Animation() {
      @Override
      public Animated getAnimated() {
        return null;
      }

      @Override
      public double update(double t, AnimationObserver o) {
        received[0] = t;
        return 1.0;
      }

      @Override
      public void reset() {
      }

      @Override
      public void stop() {
      }

      @Override
      public void complete(AnimationObserver o) {
      }
    };

    new WaitingAnimation(animation, null, null).update(42.5);

    assertEquals(42.5, received[0], 1e-10);
  }
}
