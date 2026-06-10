package edu.cmu.cs.dennisc;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BooleanSupplier;

public final class TestWait {
  private static final long TIMEOUT_NANOS = TimeUnit.SECONDS.toNanos(5);
  private static final long POLL_MILLIS = 10L;

  private TestWait() {
  }

  public static void until(BooleanSupplier condition, String description) {
    long deadline = System.nanoTime() + TIMEOUT_NANOS;
    while (!condition.getAsBoolean() && System.nanoTime() < deadline) {
      poll(description);
    }
    if (!condition.getAsBoolean()) {
      throw new AssertionError("Timed out waiting for " + description);
    }
  }

  public static <T> T future(Future<T> future, String description) {
    try {
      return future.get(5, TimeUnit.SECONDS);
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      throw new AssertionError("Interrupted while waiting for " + description, ie);
    } catch (TimeoutException te) {
      throw new AssertionError("Timed out waiting for " + description, te);
    } catch (ExecutionException ee) {
      throw new AssertionError("Future failed while waiting for " + description, ee.getCause());
    }
  }

  public static void sleepForSemanticTime(long duration, TimeUnit unit, String description) {
    try {
      Thread.sleep(unit.toMillis(duration));
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      throw new AssertionError("Interrupted during semantic-time wait for " + description, ie);
    }
  }

  private static void poll(String description) {
    try {
      Thread.sleep(POLL_MILLIS);
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      throw new AssertionError("Interrupted while waiting for " + description, ie);
    }
  }
}
