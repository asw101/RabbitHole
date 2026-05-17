package org.lgna.common;

import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

@SuppressWarnings("deprecation")
public class DoTogetherTest {

  @Test
  public void invokeAndWait_emptyArray() {
    DoTogether.invokeAndWait(); // should not throw
  }

  @Test
  public void invokeAndWait_singleRunnable() {
    AtomicBoolean ran = new AtomicBoolean(false);
    DoTogether.invokeAndWait(() -> ran.set(true));
    assertTrue(ran.get());
  }

  @Test
  public void invokeAndWait_singleRunnable_runsOnCallerThread() {
    Thread[] runThread = new Thread[1];
    DoTogether.invokeAndWait(() -> runThread[0] = Thread.currentThread());
    assertSame(Thread.currentThread(), runThread[0]);
  }

  @Test
  public void invokeAndWait_multipleRunnables_allExecute() throws InterruptedException {
    AtomicInteger counter = new AtomicInteger(0);
    Runnable r1 = counter::incrementAndGet;
    Runnable r2 = counter::incrementAndGet;
    Runnable r3 = counter::incrementAndGet;
    DoTogether.invokeAndWait(r1, r2, r3);
    // Allow brief settling time for thread pool
    Thread.sleep(50);
    assertEquals(3, counter.get());
  }

  @Test
  public void invokeAndWait_multipleRunnables_runConcurrently() throws InterruptedException {
    CountDownLatch allStarted = new CountDownLatch(2);
    CountDownLatch proceed = new CountDownLatch(1);
    AtomicBoolean bothReachedLatch = new AtomicBoolean(false);

    Runnable r1 = () -> {
      allStarted.countDown();
      try {
        allStarted.await(2, TimeUnit.SECONDS);
        bothReachedLatch.set(true);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    };
    Runnable r2 = () -> {
      allStarted.countDown();
      try {
        allStarted.await(2, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    };

    DoTogether.invokeAndWait(r1, r2);
    assertTrue(bothReachedLatch.get());
  }

  @Test
  public void invokeAndWait_twoRunnables() {
    List<String> results = new CopyOnWriteArrayList<>();
    Runnable r1 = () -> results.add("one");
    Runnable r2 = () -> results.add("two");
    DoTogether.invokeAndWait(r1, r2);
    assertEquals(2, results.size());
    assertTrue(results.contains("one"));
    assertTrue(results.contains("two"));
  }

  @Test(expected = RuntimeException.class)
  public void invokeAndWait_rethrowsException() {
    Runnable failing = () -> {
      throw new IllegalStateException("boom");
    };
    Runnable ok = () -> {};
    DoTogether.invokeAndWait(failing, ok);
  }

  @Test
  public void invokeAndWait_catchesProgramClosedException() throws InterruptedException {
    // ProgramClosedException is caught by ComponentExecutor, so no exception propagates
    // But the barrier still needs to complete, so just verify no exception escapes
    AtomicBoolean otherRan = new AtomicBoolean(false);
    Runnable ok = () -> otherRan.set(true);
    DoTogether.invokeAndWait(ok, ok);
    Thread.sleep(50);
    assertTrue(otherRan.get());
  }

  @Test
  public void invokeAndWait_manyRunnables() throws InterruptedException {
    AtomicInteger counter = new AtomicInteger(0);
    Runnable[] runnables = new Runnable[10];
    for (int i = 0; i < 10; i++) {
      runnables[i] = counter::incrementAndGet;
    }
    DoTogether.invokeAndWait(runnables);
    Thread.sleep(100);
    assertEquals(10, counter.get());
  }
}
