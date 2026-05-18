package edu.cmu.cs.dennisc.java.lang;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class ThreadUtilitiesTest {

  private long measureSleepMillis(long millis) {
    long startNanos = System.nanoTime();
    ThreadUtilities.sleep(millis);
    return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
  }

  private RuntimeException interruptSleepingWorker(
      String threadName,
      boolean daemon,
      AtomicBoolean interruptFlagAfterCatch,
      AtomicReference<String> workerNameReference) throws Exception {
    CountDownLatch started = new CountDownLatch(1);
    CountDownLatch finished = new CountDownLatch(1);
    AtomicReference<RuntimeException> failureReference = new AtomicReference<RuntimeException>();
    Thread worker = new Thread(new Runnable() {
      @Override
      public void run() {
        workerNameReference.set(Thread.currentThread().getName());
        started.countDown();
        try {
          ThreadUtilities.sleep(5000L);
        } catch (RuntimeException runtimeException) {
          failureReference.set(runtimeException);
          interruptFlagAfterCatch.set(Thread.currentThread().isInterrupted());
        } finally {
          finished.countDown();
        }
      }
    }, threadName);
    worker.setDaemon(daemon);
    worker.start();
    assertTrue(started.await(2, TimeUnit.SECONDS));
    Thread.sleep(50L);
    worker.interrupt();
    assertTrue(finished.await(2, TimeUnit.SECONDS));
    return failureReference.get();
  }

  private void assertElapsedAtLeastWithTolerance(long requestedMillis, long toleranceMillis) {
    long elapsedMillis = measureSleepMillis(requestedMillis);
    assertTrue("elapsed=" + elapsedMillis + " requested=" + requestedMillis,
        elapsedMillis + toleranceMillis >= requestedMillis);
    assertTrue("elapsed=" + elapsedMillis, elapsedMillis < requestedMillis + 2000L);
  }

  @Test
  public void sleep0MillisCompletesWithoutException() {
    long elapsedMillis = measureSleepMillis(0L);
    assertTrue(elapsedMillis >= 0L);
    assertFalse(Thread.currentThread().isInterrupted());
    ThreadUtilities.sleep(0L);
    assertFalse(Thread.currentThread().isInterrupted());
  }

  @Test
  public void sleep0MillisTimingIsAtLeastRequestedWithinTolerance() {
    assertElapsedAtLeastWithTolerance(0L, 15L);
    assertFalse(Thread.currentThread().isInterrupted());
  }

  @Test
  public void sleep1MillisCompletesWithoutException() {
    long elapsedMillis = measureSleepMillis(1L);
    assertTrue(elapsedMillis >= 0L);
    assertFalse(Thread.currentThread().isInterrupted());
    ThreadUtilities.sleep(1L);
    assertFalse(Thread.currentThread().isInterrupted());
  }

  @Test
  public void sleep1MillisTimingIsAtLeastRequestedWithinTolerance() {
    assertElapsedAtLeastWithTolerance(1L, 15L);
    assertFalse(Thread.currentThread().isInterrupted());
  }

  @Test
  public void sleep2MillisCompletesWithoutException() {
    long elapsedMillis = measureSleepMillis(2L);
    assertTrue(elapsedMillis >= 0L);
    assertFalse(Thread.currentThread().isInterrupted());
    ThreadUtilities.sleep(2L);
    assertFalse(Thread.currentThread().isInterrupted());
  }

  @Test
  public void sleep2MillisTimingIsAtLeastRequestedWithinTolerance() {
    assertElapsedAtLeastWithTolerance(2L, 15L);
    assertFalse(Thread.currentThread().isInterrupted());
  }

  @Test
  public void sleep3MillisCompletesWithoutException() {
    long elapsedMillis = measureSleepMillis(3L);
    assertTrue(elapsedMillis >= 0L);
    assertFalse(Thread.currentThread().isInterrupted());
    ThreadUtilities.sleep(3L);
    assertFalse(Thread.currentThread().isInterrupted());
  }

  @Test
  public void sleep3MillisTimingIsAtLeastRequestedWithinTolerance() {
    assertElapsedAtLeastWithTolerance(3L, 15L);
    assertFalse(Thread.currentThread().isInterrupted());
  }

  @Test
  public void sleep5MillisCompletesWithoutException() {
    long elapsedMillis = measureSleepMillis(5L);
    assertTrue(elapsedMillis >= 0L);
    assertFalse(Thread.currentThread().isInterrupted());
    ThreadUtilities.sleep(5L);
    assertFalse(Thread.currentThread().isInterrupted());
  }

  @Test
  public void sleep5MillisTimingIsAtLeastRequestedWithinTolerance() {
    assertElapsedAtLeastWithTolerance(5L, 15L);
    assertFalse(Thread.currentThread().isInterrupted());
  }

  @Test
  public void sleep10MillisCompletesWithoutException() {
    long elapsedMillis = measureSleepMillis(10L);
    assertTrue(elapsedMillis >= 0L);
    assertFalse(Thread.currentThread().isInterrupted());
    ThreadUtilities.sleep(10L);
    assertFalse(Thread.currentThread().isInterrupted());
  }

  @Test
  public void sleep10MillisTimingIsAtLeastRequestedWithinTolerance() {
    assertElapsedAtLeastWithTolerance(10L, 15L);
    assertFalse(Thread.currentThread().isInterrupted());
  }

  @Test
  public void sleep20MillisCompletesWithoutException() {
    long elapsedMillis = measureSleepMillis(20L);
    assertTrue(elapsedMillis >= 0L);
    assertFalse(Thread.currentThread().isInterrupted());
    ThreadUtilities.sleep(20L);
    assertFalse(Thread.currentThread().isInterrupted());
  }

  @Test
  public void sleep20MillisTimingIsAtLeastRequestedWithinTolerance() {
    assertElapsedAtLeastWithTolerance(20L, 15L);
    assertFalse(Thread.currentThread().isInterrupted());
  }

  @Test
  public void sleep50MillisCompletesWithoutException() {
    long elapsedMillis = measureSleepMillis(50L);
    assertTrue(elapsedMillis >= 0L);
    assertFalse(Thread.currentThread().isInterrupted());
    ThreadUtilities.sleep(50L);
    assertFalse(Thread.currentThread().isInterrupted());
  }

  @Test
  public void sleep50MillisTimingIsAtLeastRequestedWithinTolerance() {
    assertElapsedAtLeastWithTolerance(50L, 15L);
    assertFalse(Thread.currentThread().isInterrupted());
  }

  @Test
  public void sleep100MillisCompletesWithoutException() {
    long elapsedMillis = measureSleepMillis(100L);
    assertTrue(elapsedMillis >= 0L);
    assertFalse(Thread.currentThread().isInterrupted());
    ThreadUtilities.sleep(100L);
    assertFalse(Thread.currentThread().isInterrupted());
  }

  @Test
  public void sleep100MillisTimingIsAtLeastRequestedWithinTolerance() {
    assertElapsedAtLeastWithTolerance(100L, 15L);
    assertFalse(Thread.currentThread().isInterrupted());
  }

  @Test
  public void sleep200MillisCompletesWithoutException() {
    long elapsedMillis = measureSleepMillis(200L);
    assertTrue(elapsedMillis >= 0L);
    assertFalse(Thread.currentThread().isInterrupted());
    ThreadUtilities.sleep(200L);
    assertFalse(Thread.currentThread().isInterrupted());
  }

  @Test
  public void sleep200MillisTimingIsAtLeastRequestedWithinTolerance() {
    assertElapsedAtLeastWithTolerance(200L, 15L);
    assertFalse(Thread.currentThread().isInterrupted());
  }

  @Test
  public void sleepInterruptWrapsInterruptedExceptionInRuntimeException() throws Exception {
    AtomicBoolean interruptFlag = new AtomicBoolean(true);
    AtomicReference<String> workerName = new AtomicReference<String>();
    RuntimeException runtimeException = interruptSleepingWorker(
        "interrupt-wrap-worker",
        false,
        interruptFlag,
        workerName);
    assertNotNull(runtimeException);
    assertTrue(runtimeException.getCause() instanceof InterruptedException);
    assertEquals("interrupt-wrap-worker", workerName.get());
  }

  @Test
  public void sleepInterruptCauseChainIsCorrect() throws Exception {
    AtomicBoolean interruptFlag = new AtomicBoolean(true);
    AtomicReference<String> workerName = new AtomicReference<String>();
    RuntimeException runtimeException = interruptSleepingWorker(
        "interrupt-cause-worker",
        false,
        interruptFlag,
        workerName);
    assertNotNull(runtimeException);
    assertNotNull(runtimeException.getCause());
    assertTrue(runtimeException.getCause() instanceof InterruptedException);
    assertNull(runtimeException.getCause().getCause());
  }

  @Test
  public void sleepInterruptClearsInterruptedFlagAfterThreadSleep() throws Exception {
    AtomicBoolean interruptFlag = new AtomicBoolean(true);
    AtomicReference<String> workerName = new AtomicReference<String>();
    RuntimeException runtimeException = interruptSleepingWorker(
        "interrupt-flag-worker",
        false,
        interruptFlag,
        workerName);
    assertNotNull(runtimeException);
    assertFalse(interruptFlag.get());
    assertEquals("interrupt-flag-worker", workerName.get());
  }

  @Test
  public void sleepFromMultipleConcurrentThreadsCompletesForEveryThread() throws Exception {
    int threadCount = 6;
    CountDownLatch started = new CountDownLatch(threadCount);
    CountDownLatch finished = new CountDownLatch(threadCount);
    AtomicBoolean failed = new AtomicBoolean(false);
    for (int i = 0; i < threadCount; i++) {
      Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
          started.countDown();
          try {
            assertTrue(started.await(2, TimeUnit.SECONDS));
            ThreadUtilities.sleep(20L);
          } catch (Throwable throwable) {
            failed.set(true);
          } finally {
            finished.countDown();
          }
        }
      }, "concurrent-sleeper-" + i);
      thread.start();
    }
    assertTrue(finished.await(5, TimeUnit.SECONDS));
    assertFalse(failed.get());
  }

  @Test
  public void sleepCalledInSequenceAccumulatesElapsedTime() {
    long startNanos = System.nanoTime();
    ThreadUtilities.sleep(10L);
    ThreadUtilities.sleep(20L);
    ThreadUtilities.sleep(30L);
    long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
    assertTrue(elapsedMillis + 15L >= 60L);
    assertFalse(Thread.currentThread().isInterrupted());
  }

  @Test
  public void sleepFromNamedThreadCompletesWithoutChangingThreadName() throws Exception {
    CountDownLatch finished = new CountDownLatch(1);
    AtomicBoolean succeeded = new AtomicBoolean(false);
    AtomicReference<String> threadName = new AtomicReference<String>();
    Thread thread = new Thread(new Runnable() {
      @Override
      public void run() {
        threadName.set(Thread.currentThread().getName());
        ThreadUtilities.sleep(15L);
        succeeded.set(true);
        finished.countDown();
      }
    }, "named-thread-sleeper");
    thread.start();
    assertTrue(finished.await(2, TimeUnit.SECONDS));
    assertTrue(succeeded.get());
    assertEquals("named-thread-sleeper", threadName.get());
  }

  @Test
  public void sleepFromDaemonThreadCompletesWithoutException() throws Exception {
    CountDownLatch finished = new CountDownLatch(1);
    AtomicBoolean succeeded = new AtomicBoolean(false);
    AtomicBoolean daemonFlag = new AtomicBoolean(false);
    Thread thread = new Thread(new Runnable() {
      @Override
      public void run() {
        daemonFlag.set(Thread.currentThread().isDaemon());
        ThreadUtilities.sleep(15L);
        succeeded.set(true);
        finished.countDown();
      }
    }, "daemon-thread-sleeper");
    thread.setDaemon(true);
    thread.start();
    assertTrue(finished.await(2, TimeUnit.SECONDS));
    assertTrue(succeeded.get());
    assertTrue(daemonFlag.get());
  }

  @Test
  public void sleepDoesNotAffectCallingThreadsInterruptStatus() {
    assertFalse(Thread.currentThread().isInterrupted());
    ThreadUtilities.sleep(5L);
    assertFalse(Thread.currentThread().isInterrupted());
    ThreadUtilities.sleep(0L);
    assertFalse(Thread.currentThread().isInterrupted());
  }

  @Test
  public void multipleInterruptedSleepingThreadsEachThrowRuntimeException() throws Exception {
    int threadCount = 3;
    CountDownLatch started = new CountDownLatch(threadCount);
    CountDownLatch finished = new CountDownLatch(threadCount);
    AtomicInteger runtimeExceptionCount = new AtomicInteger(0);
    AtomicBoolean allCausesInterrupted = new AtomicBoolean(true);
    List<Thread> threads = new ArrayList<Thread>();
    for (int i = 0; i < threadCount; i++) {
      Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
          started.countDown();
          try {
            ThreadUtilities.sleep(5000L);
          } catch (RuntimeException runtimeException) {
            runtimeExceptionCount.incrementAndGet();
            allCausesInterrupted.set(allCausesInterrupted.get()
                && (runtimeException.getCause() instanceof InterruptedException));
          } finally {
            finished.countDown();
          }
        }
      }, "interrupt-group-" + i);
      threads.add(thread);
      thread.start();
    }
    assertTrue(started.await(2, TimeUnit.SECONDS));
    Thread.sleep(50L);
    for (Thread thread : threads) {
      thread.interrupt();
    }
    assertTrue(finished.await(5, TimeUnit.SECONDS));
    assertEquals(threadCount, runtimeExceptionCount.get());
    assertTrue(allCausesInterrupted.get());
  }

  @Test
  public void sleepConcurrentThreadsWithDifferentDurationsAllFinish() throws Exception {
    long[] durations = new long[] {0L, 1L, 5L, 10L, 25L, 40L};
    CountDownLatch finished = new CountDownLatch(durations.length);
    AtomicBoolean failed = new AtomicBoolean(false);
    for (int i = 0; i < durations.length; i++) {
      final long duration = durations[i];
      Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            ThreadUtilities.sleep(duration);
          } catch (Throwable throwable) {
            failed.set(true);
          } finally {
            finished.countDown();
          }
        }
      }, "mixed-duration-" + i);
      thread.start();
    }
    assertTrue(finished.await(5, TimeUnit.SECONDS));
    assertFalse(failed.get());
  }

  @Test
  public void sleepRepeatedSmallDurationsRemainStableAcrossSequence() {
    long totalElapsed = 0L;
    for (int i = 0; i < 6; i++) {
      totalElapsed += measureSleepMillis(2L);
    }
    assertTrue(totalElapsed >= 0L);
    assertFalse(Thread.currentThread().isInterrupted());
  }
}
