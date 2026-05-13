package org.lgna.story.implementation.eventhandling;

import edu.cmu.cs.dennisc.animation.Animation;
import edu.cmu.cs.dennisc.animation.AnimationObserver;
import edu.cmu.cs.dennisc.animation.Animator;
import edu.cmu.cs.dennisc.animation.FrameObserver;
import org.junit.Test;
import org.lgna.story.MultipleEventPolicy;
import org.lgna.story.SScene;
import org.lgna.story.event.SceneActivationEvent;
import org.lgna.story.event.TimeEvent;
import org.lgna.story.event.TimeListener;
import org.lgna.story.implementation.ProgramImp;
import org.lgna.story.implementation.SceneImp;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class TimerEventHandlerTest {

  @Test
  public void automaticDisplayCompletionFiresActivatedTimeListener() throws Exception {
    TimerEventHandler handler = new TimerEventHandler();
    SceneImp scene = new TestScene().getImplementation();
    FakeAnimator animator = new FakeAnimator();
    animator.currentTime = 1.5;
    handler.setScene(scene);
    CountDownLatch fired = new CountDownLatch(1);
    handler.addListener(new TimeListener() {
      @Override
      public void timeElapsed(TimeEvent event) {
        fired.countDown();
      }
    }, 1.0, MultipleEventPolicy.IGNORE);
    setProgram(scene, new FakeProgramImp(animator));

    try {
      handler.sceneActivated(new SceneActivationEvent());
      handler.handleAutomaticDisplayCompleted();

      assertTrue("time listener should fire from automatic display completion",
          fired.await(5, TimeUnit.SECONDS));
    } finally {
      handler.disable();
    }
  }

  private static void setProgram(SceneImp scene, ProgramImp program) throws Exception {
    Field programField = SceneImp.class.getDeclaredField("program");
    programField.setAccessible(true);
    programField.set(scene, program);
  }

  private static class FakeProgramImp extends ProgramImp {
    private final Animator animator;

    FakeProgramImp(Animator animator) {
      super(null, null);
      this.animator = animator;
    }

    @Override
    public Animator getAnimator() {
      return this.animator;
    }
  }

  private static class FakeAnimator implements Animator {
    private double currentTime;

    @Override
    public double getCurrentTime() {
      return this.currentTime;
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
    public void invokeAndWait(Animation animation, AnimationObserver animationObserver)
        throws InterruptedException, InvocationTargetException {
    }

    @Override
    public void invokeAndWait_ThrowRuntimeExceptionsIfNecessary(
        Animation animation,
        AnimationObserver animationObserver) {
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
  }

  private static class TestScene extends SScene {
    @Override
    public void handleActiveChanged(Boolean isActive, Integer activationCount) {
    }
  }
}
