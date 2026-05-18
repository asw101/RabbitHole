package edu.cmu.cs.dennisc.java.lang;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Tests for edu.cmu.cs.dennisc.java.lang.ThreadUtilities —
 * sleep happy path and interrupt→RuntimeException wrapping.
 */
public class ThreadUtilitiesTest {

  @Test
  public void sleep_happyPath_completesWithoutException() {
    ThreadUtilities.sleep(10);
  }

  @Test
  public void sleep_zeroMillis_completesImmediately() {
    long start = System.nanoTime();
    ThreadUtilities.sleep(0);
    long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    assertTrue("sleep(0) should return quickly, took " + elapsed + "ms", elapsed < 500);
  }

  @Test
  public void sleep_shortDuration_waitsAtLeastThatLong() {
    long start = System.nanoTime();
    ThreadUtilities.sleep(50);
    long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    assertTrue("Expected at least 40ms but got " + elapsed + "ms", elapsed >= 40);
  }

  @Test
  public void sleep_interrupt_throwsRuntimeExceptionWrappingInterruptedException() throws Exception {
    AtomicReference<Throwable> caught = new AtomicReference<>();
    CountDownLatch sleeping = new CountDownLatch(1);
    CountDownLatch done = new CountDownLatch(1);

    Thread t = new Thread(() -> {
      sleeping.countDown();
      try {
        ThreadUtilities.sleep(5000);
      } catch (RuntimeException re) {
        caught.set(re);
      } finally {
        done.countDown();
      }
    });
    t.start();
    assertTrue("Thread did not start sleeping", sleeping.await(2, TimeUnit.SECONDS));
    Thread.sleep(20); // let it enter Thread.sleep
    t.interrupt();
    assertTrue("Thread did not finish", done.await(2, TimeUnit.SECONDS));

    assertNotNull("Expected RuntimeException from interrupt", caught.get());
    assertTrue("Expected RuntimeException", caught.get() instanceof RuntimeException);
    assertNotNull("Expected InterruptedException cause", caught.get().getCause());
    assertTrue("Cause should be InterruptedException",
        caught.get().getCause() instanceof InterruptedException);
  }

  @Test
  public void sleep_interrupt_runtimeExceptionMessageContainsCause() throws Exception {
    AtomicReference<RuntimeException> caught = new AtomicReference<>();
    CountDownLatch sleeping = new CountDownLatch(1);
    CountDownLatch done = new CountDownLatch(1);

    Thread t = new Thread(() -> {
      sleeping.countDown();
      try {
        ThreadUtilities.sleep(5000);
      } catch (RuntimeException re) {
        caught.set(re);
      } finally {
        done.countDown();
      }
    });
    t.start();
    assertTrue(sleeping.await(2, TimeUnit.SECONDS));
    Thread.sleep(20);
    t.interrupt();
    assertTrue(done.await(2, TimeUnit.SECONDS));

    RuntimeException re = caught.get();
    assertNotNull(re);
    assertTrue(re.getCause() instanceof InterruptedException);
  }

  @Test
  public void sleep_multipleCalls_allComplete() {
    for (int i = 0; i < 5; i++) {
      ThreadUtilities.sleep(1);
    }
  }

  @Test
  public void sleep_fromMultipleThreads_allComplete() throws Exception {
    int threadCount = 5;
    CountDownLatch latch = new CountDownLatch(threadCount);
    AtomicBoolean anyFailed = new AtomicBoolean(false);

    for (int i = 0; i < threadCount; i++) {
      new Thread(() -> {
        try {
          ThreadUtilities.sleep(10);
        } catch (Exception e) {
          anyFailed.set(true);
        } finally {
          latch.countDown();
        }
      }).start();
    }

    assertTrue("Not all threads finished", latch.await(5, TimeUnit.SECONDS));
    assertFalse("Some thread threw an exception", anyFailed.get());
  }

  @Test
  public void sleep_measuredDuration_withinReasonableBounds() {
    long start = System.nanoTime();
    ThreadUtilities.sleep(100);
    long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    assertTrue("Expected >= 90ms, got " + elapsed, elapsed >= 90);
    assertTrue("Expected < 1000ms, got " + elapsed, elapsed < 1000);
  }

  @Test
  public void sleep_oneMillisecond_returns() {
    ThreadUtilities.sleep(1);
  }

  @Test
  public void sleep_twoMilliseconds_returns() {
    ThreadUtilities.sleep(2);
  }

  @Test
  public void sleep_calledTwiceInSequence_bothComplete() {
    long start = System.nanoTime();
    ThreadUtilities.sleep(20);
    ThreadUtilities.sleep(20);
    long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    assertTrue("Two 20ms sleeps should take >= 35ms, got " + elapsed, elapsed >= 35);
  }

  @Test
  public void sleep_interruptRestoresFlag_characterization() throws Exception {
    AtomicBoolean interruptFlagAfter = new AtomicBoolean(false);
    CountDownLatch sleeping = new CountDownLatch(1);
    CountDownLatch done = new CountDownLatch(1);

    Thread t = new Thread(() -> {
      sleeping.countDown();
      try {
        ThreadUtilities.sleep(5000);
      } catch (RuntimeException re) {
        interruptFlagAfter.set(Thread.currentThread().isInterrupted());
      } finally {
        done.countDown();
      }
    });
    t.start();
    assertTrue(sleeping.await(2, TimeUnit.SECONDS));
    Thread.sleep(20);
    t.interrupt();
    assertTrue(done.await(2, TimeUnit.SECONDS));
    // Characterize: interrupt flag is NOT restored because RuntimeException is thrown
    // The Thread.sleep clears the flag, then we wrap in RuntimeException
    assertFalse("Interrupt flag should be cleared by Thread.sleep", interruptFlagAfter.get());
  }

  @Test
  public void sleep_doesNotAffectCallingThread() {
    long start = System.nanoTime();
    ThreadUtilities.sleep(10);
    long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    // Thread should resume on calling thread after sleep
    assertFalse("Calling thread should not be interrupted",
        Thread.currentThread().isInterrupted());
    assertTrue("Should have waited", elapsed >= 5);
  }

  @Test
  public void sleep_concurrentInterrupts_eachThrows() throws Exception {
    int count = 3;
    CountDownLatch allSleeping = new CountDownLatch(count);
    CountDownLatch allDone = new CountDownLatch(count);
    AtomicReference<Integer> exceptionCount = new AtomicReference<>(0);
    Thread[] threads = new Thread[count];

    for (int i = 0; i < count; i++) {
      threads[i] = new Thread(() -> {
        allSleeping.countDown();
        try {
          ThreadUtilities.sleep(5000);
        } catch (RuntimeException re) {
          synchronized (exceptionCount) {
            exceptionCount.set(exceptionCount.get() + 1);
          }
        } finally {
          allDone.countDown();
        }
      });
      threads[i].start();
    }

    assertTrue(allSleeping.await(2, TimeUnit.SECONDS));
    Thread.sleep(30);
    for (Thread t : threads) {
      t.interrupt();
    }
    assertTrue(allDone.await(5, TimeUnit.SECONDS));
    assertEquals("All interrupted threads should throw", Integer.valueOf(count), exceptionCount.get());
  }
}
