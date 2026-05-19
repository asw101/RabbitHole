package org.lgna.common;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class ComponentExecutorDeepTest {

  @Test
  public void run_executesTarget() {
    AtomicBoolean ran = new AtomicBoolean(false);

    new ComponentExecutor(() -> ran.set(true), "test").run();

    assertTrue(ran.get());
  }

  @Test
  public void run_withNullDescriptionStillExecutesTarget() {
    AtomicBoolean ran = new AtomicBoolean(false);

    new ComponentExecutor(() -> ran.set(true), null).run();

    assertTrue(ran.get());
  }

  @Test
  public void run_catchesProgramClosedException() {
    new ComponentExecutor(() -> {
      throw new ProgramClosedException("closed");
    }, "test").run();
  }

  @Test(expected = RuntimeException.class)
  public void run_rethrowsOtherRuntimeException() {
    new ComponentExecutor(() -> {
      throw new IllegalStateException("not ProgramClosedException");
    }, "test").run();
  }

  @Test
  public void start_submitsAndEventuallyRuns() throws Exception {
    CountDownLatch latch = new CountDownLatch(1);

    new ComponentExecutor(latch::countDown, "test").start();

    assertTrue(latch.await(5, TimeUnit.SECONDS));
  }

  @Test
  public void run_catchesNestedProgramClosedException() {
    new ComponentExecutor(() -> {
      throw new RuntimeException(new ProgramClosedException("nested"));
    }, "test").run();
  }

  @Test
  public void run_catchesDeeplyNestedProgramClosedException() {
    new ComponentExecutor(() -> {
      throw new RuntimeException(new RuntimeException(new ProgramClosedException("deep")));
    }, "test").run();
  }

  @Test
  public void start_multipleExecutors() throws Exception {
    CountDownLatch latch = new CountDownLatch(3);

    new ComponentExecutor(latch::countDown, "t1").start();
    new ComponentExecutor(latch::countDown, "t2").start();
    new ComponentExecutor(latch::countDown, "t3").start();

    assertTrue(latch.await(5, TimeUnit.SECONDS));
  }

  @Test
  public void start_sameExecutorTwiceRunsTwice() throws Exception {
    CountDownLatch latch = new CountDownLatch(2);
    ComponentExecutor executor = new ComponentExecutor(latch::countDown, "repeat");

    executor.start();
    executor.start();

    assertTrue(latch.await(5, TimeUnit.SECONDS));
  }

  @Test
  public void run_normalCompletion_noException() {
    new ComponentExecutor(() -> {
      int value = 1 + 1;
      assertEquals(2, value);
    }, "test").run();
  }
}
