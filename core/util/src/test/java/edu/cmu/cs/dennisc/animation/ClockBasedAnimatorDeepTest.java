package edu.cmu.cs.dennisc.animation;

import edu.cmu.cs.dennisc.TestWait;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class ClockBasedAnimatorDeepTest {

  @Test
  public void newInstance_speedFactorIsOne() {
    assertEquals(1.0, new ClockBasedAnimator().getSpeedFactor(), 1e-10);
  }

  @Test
  public void firstUpdate_startsSimulationAtZero() {
    ClockBasedAnimator animator = new ClockBasedAnimator();

    animator.update();

    assertEquals(0.0, animator.getCurrentTime(), 1e-10);
  }

  @Test
  public void setSpeedFactor_changesValue() {
    ClockBasedAnimator animator = new ClockBasedAnimator();

    animator.setSpeedFactor(2.0);

    assertEquals(2.0, animator.getSpeedFactor(), 1e-10);
  }

  @Test
  public void setSpeedFactor_zero() {
    ClockBasedAnimator animator = new ClockBasedAnimator();

    animator.setSpeedFactor(0.0);

    assertEquals(0.0, animator.getSpeedFactor(), 1e-10);
  }

  @Test
  public void setSpeedFactor_negative() {
    ClockBasedAnimator animator = new ClockBasedAnimator();

    animator.setSpeedFactor(-1.0);

    assertEquals(-1.0, animator.getSpeedFactor(), 1e-10);
  }

  @Test
  public void setSpeedFactor_multipleAssignments_lastWins() {
    ClockBasedAnimator animator = new ClockBasedAnimator();

    animator.setSpeedFactor(1.5);
    animator.setSpeedFactor(0.25);

    assertEquals(0.25, animator.getSpeedFactor(), 1e-10);
  }

  @Test
  public void update_doesNotThrow() {
    new ClockBasedAnimator().update();
  }

  @Test
  public void update_setsCurrentTime() {
    ClockBasedAnimator animator = new ClockBasedAnimator();

    animator.update();

    assertTrue(animator.getCurrentTime() >= 0.0);
  }

  @Test
  public void multipleUpdates_timeAdvances() throws Exception {
    ClockBasedAnimator animator = new ClockBasedAnimator();

    animator.update();
    double initialTime = animator.getCurrentTime();

    // Intentional real-time wait: ClockBasedAnimator derives simulation time from wall-clock deltas.
    TestWait.sleepForSemanticTime(10, TimeUnit.MILLISECONDS,
        "ClockBasedAnimator time advancement");
    animator.update();

    assertTrue(animator.getCurrentTime() >= initialTime);
  }

  @Test
  public void zeroSpeedFactor_freezesSimulationTimeAfterFirstUpdate() throws Exception {
    ClockBasedAnimator animator = new ClockBasedAnimator();

    animator.setSpeedFactor(0.0);
    animator.update();
    double initialTime = animator.getCurrentTime();

    // Intentional real-time wait: verifies zero speed freezes simulation across elapsed time.
    TestWait.sleepForSemanticTime(10, TimeUnit.MILLISECONDS,
        "ClockBasedAnimator zero-speed elapsed-time assertion");
    animator.update();

    assertEquals(initialTime, animator.getCurrentTime(), 1e-10);
  }

  @Test
  public void negativeSpeedFactor_movesTimeBackwardAfterFirstUpdate() throws Exception {
    ClockBasedAnimator animator = new ClockBasedAnimator();

    animator.setSpeedFactor(-1.0);
    animator.update();

    // Intentional real-time wait: verifies negative speed applies to elapsed wall-clock time.
    TestWait.sleepForSemanticTime(10, TimeUnit.MILLISECONDS,
        "ClockBasedAnimator negative-speed elapsed-time assertion");
    animator.update();

    assertTrue(animator.getCurrentTime() <= 0.0);
  }

  @Test
  public void cancelAnimation_doesNotThrow() {
    new ClockBasedAnimator().cancelAnimation();
  }

  @Test
  public void completeAll_doesNotThrow() {
    new ClockBasedAnimator().completeAll();
  }

  @Test
  public void addRemoveFrameObserver_doesNotThrow() {
    ClockBasedAnimator animator = new ClockBasedAnimator();
    FrameObserver observer = new FrameObserver() {
      @Override
      public void update(double t) {
      }

      @Override
      public void complete() {
      }
    };

    animator.addFrameObserver(observer);
    animator.removeFrameObserver(observer);
  }

  @Test
  public void removedFrameObserver_isNotCalledOnUpdate() {
    ClockBasedAnimator animator = new ClockBasedAnimator();
    final boolean[] called = { false };
    FrameObserver observer = new FrameObserver() {
      @Override
      public void update(double t) {
        called[0] = true;
      }

      @Override
      public void complete() {
      }
    };

    animator.addFrameObserver(observer);
    animator.removeFrameObserver(observer);
    animator.update();
    animator.update();

    assertFalse(called[0]);
  }

  @Test
  public void frameObserver_calledOnUpdate() {
    ClockBasedAnimator animator = new ClockBasedAnimator();
    final boolean[] called = { false };

    animator.addFrameObserver(new FrameObserver() {
      @Override
      public void update(double t) {
        called[0] = true;
      }

      @Override
      public void complete() {
      }
    });

    animator.update();
    animator.update();

    assertTrue(called[0]);
  }

  @Test
  public void speedFactorHalf_timeAdvancesSlower() throws Exception {
    ClockBasedAnimator fastAnimator = new ClockBasedAnimator();
    ClockBasedAnimator slowAnimator = new ClockBasedAnimator();

    slowAnimator.setSpeedFactor(0.5);
    fastAnimator.update();
    slowAnimator.update();

    // Intentional real-time wait: compares simulation deltas produced by different speed factors.
    TestWait.sleepForSemanticTime(50, TimeUnit.MILLISECONDS,
        "ClockBasedAnimator speed-factor comparison");
    fastAnimator.update();
    slowAnimator.update();

    assertTrue(slowAnimator.getCurrentTime() <= fastAnimator.getCurrentTime() + 0.01);
  }
}
