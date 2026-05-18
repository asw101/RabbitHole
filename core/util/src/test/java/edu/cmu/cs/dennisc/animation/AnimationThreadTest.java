package edu.cmu.cs.dennisc.animation;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

public class AnimationThreadTest {
  private static final class NamedAnimator implements Animator {
    private int invokeCount;
    private Animation lastAnimation;
    private AnimationObserver lastObserver;
    private RuntimeException runtimeException;

    @Override
    public double getCurrentTime() {
      return 0.0;
    }

    @Override
    public double getSpeedFactor() {
      return 1.0;
    }

    @Override
    public void setSpeedFactor(double speedFactor) {
    }

    @Override
    public void update() {
    }

    @Override
    public void invokeLater(Animation animation, AnimationObserver animationObserver) {
    }

    @Override
    public void invokeAndWait(Animation animation, AnimationObserver animationObserver) throws InterruptedException, InvocationTargetException {
    }

    @Override
    public void invokeAndWait_ThrowRuntimeExceptionsIfNecessary(Animation animation, AnimationObserver animationObserver) {
      this.invokeCount++;
      this.lastAnimation = animation;
      this.lastObserver = animationObserver;
      if (this.runtimeException != null) {
        throw this.runtimeException;
      }
    }

    @Override
    public void addFrameObserver(FrameObserver runnable) {
    }

    @Override
    public void removeFrameObserver(FrameObserver runnable) {
    }

    @Override
    public void completeAll() {
    }

    @Override
    public void cancelAnimation() {
    }

    @Override
    public String toString() {
      return "animator";
    }
  }

  private static final class NamedAnimation implements Animation {
    private final String name;

    private NamedAnimation(String name) {
      this.name = name;
    }

    @Override
    public void reset() {
    }

    @Override
    public double update(double tCurrent, AnimationObserver animationObserver) {
      return 0.0;
    }

    @Override
    public void stop() {
    }

    @Override
    public void complete(AnimationObserver animationObserver) {
    }

    @Override
    public String toString() {
      return this.name;
    }
  }

  private static final class NamedObserver implements AnimationObserver {
    private final String name;

    private NamedObserver(String name) {
      this.name = name;
    }

    @Override
    public void started(Animation animation) {
    }

    @Override
    public void finished(Animation animation) {
    }

    @Override
    public String toString() {
      return this.name;
    }
  }

  @Test
  public void runDelegatesToAnimator() {
    NamedAnimator animator = new NamedAnimator();
    NamedAnimation animation = new NamedAnimation("animation");
    NamedObserver observer = new NamedObserver("observer");
    AnimationThread thread = new AnimationThread(animator, animation, observer);

    thread.run();

    assertEquals(1, animator.invokeCount);
    assertSame(animation, animator.lastAnimation);
    assertSame(observer, animator.lastObserver);
  }

  @Test
  public void startRunsAnimatorAsynchronously() throws Exception {
    NamedAnimator animator = new NamedAnimator();
    AnimationThread thread = new AnimationThread(animator, new NamedAnimation("animation"), new NamedObserver("observer"));

    thread.start();
    thread.join(1000L);

    assertFalse(thread.isAlive());
    assertEquals(1, animator.invokeCount);
  }

  @Test
  public void runCanBeCalledMultipleTimesDirectly() {
    NamedAnimator animator = new NamedAnimator();
    AnimationThread thread = new AnimationThread(animator, new NamedAnimation("animation"), new NamedObserver("observer"));

    thread.run();
    thread.run();

    assertEquals(2, animator.invokeCount);
  }

  @Test
  public void runPropagatesRuntimeException() {
    NamedAnimator animator = new NamedAnimator();
    animator.runtimeException = new IllegalStateException("boom");
    AnimationThread thread = new AnimationThread(animator, new NamedAnimation("animation"), new NamedObserver("observer"));

    IllegalStateException exception = assertThrows(IllegalStateException.class, thread::run);

    assertEquals("boom", exception.getMessage());
  }

  @Test
  public void toStringContainsClassNameAndId() {
    AnimationThread thread = new AnimationThread(new NamedAnimator(), new NamedAnimation("animation"), new NamedObserver("observer"));

    String text = thread.toString();

    assertTrue(text.contains("AnimationThread"));
    assertTrue(text.contains("id="));
  }

  @Test
  public void toStringContainsAnimatorAnimationAndObserver() {
    AnimationThread thread = new AnimationThread(new NamedAnimator(), new NamedAnimation("animation"), new NamedObserver("observer"));

    String text = thread.toString();

    assertTrue(text.contains("animator=animator"));
    assertTrue(text.contains("animation=animation"));
    assertTrue(text.contains("observer=observer"));
  }

  @Test
  public void nullObserverIsPassedThrough() {
    NamedAnimator animator = new NamedAnimator();
    NamedAnimation animation = new NamedAnimation("animation");
    AnimationThread thread = new AnimationThread(animator, animation, null);

    thread.run();

    assertSame(animation, animator.lastAnimation);
    assertNull(animator.lastObserver);
  }

  @Test
  public void nullAnimationIsPassedThrough() {
    NamedAnimator animator = new NamedAnimator();
    NamedObserver observer = new NamedObserver("observer");
    AnimationThread thread = new AnimationThread(animator, null, observer);

    thread.run();

    assertNull(animator.lastAnimation);
    assertSame(observer, animator.lastObserver);
  }

  @Test
  public void toStringShowsNullFieldsWhenPresent() {
    AnimationThread thread = new AnimationThread(new NamedAnimator(), null, null);

    String text = thread.toString();

    assertTrue(text.contains("animation=null"));
    assertTrue(text.contains("observer=null"));
  }
}
