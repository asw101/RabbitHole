package org.alice.ide;

import javax.swing.SwingUtilities;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

public final class IdeTestWait {
  private static final long TIMEOUT_NANOS = TimeUnit.SECONDS.toNanos(20);
  private static final long POLL_MILLIS = 10L;

  private IdeTestWait() {
  }

  public static void until(BooleanSupplier condition, String description) {
    until(condition, description, TIMEOUT_NANOS, TimeUnit.NANOSECONDS);
  }

  public static void until(BooleanSupplier condition, String description, long timeout, TimeUnit unit) {
    if (!isTrueWithin(condition, timeout, unit, description)) {
      throw new AssertionError("Timed out waiting for " + description);
    }
  }

  public static boolean isTrueWithin(BooleanSupplier condition, long timeout, TimeUnit unit) {
    return isTrueWithin(condition, timeout, unit, "condition");
  }

  private static boolean isTrueWithin(
      BooleanSupplier condition, long timeout, TimeUnit unit, String description) {
    long deadline = System.nanoTime() + unit.toNanos(timeout);
    while (!condition.getAsBoolean() && System.nanoTime() < deadline) {
      poll(description);
    }
    return condition.getAsBoolean();
  }

  public static void untilOnEdt(BooleanSupplier condition, String description) {
    until(() -> onEdt(condition), description);
  }

  public static void drainEdt() {
    if (SwingUtilities.isEventDispatchThread()) {
      return;
    }
    try {
      SwingUtilities.invokeAndWait(() -> {
      });
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      throw new AssertionError("Interrupted while draining Swing event queue", ie);
    } catch (InvocationTargetException ite) {
      throw new AssertionError("Swing event queue drain failed", ite.getCause());
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

  private static boolean onEdt(BooleanSupplier condition) {
    if (SwingUtilities.isEventDispatchThread()) {
      return condition.getAsBoolean();
    }
    AtomicBoolean result = new AtomicBoolean();
    try {
      SwingUtilities.invokeAndWait(() -> result.set(condition.getAsBoolean()));
      return result.get();
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      throw new AssertionError("Interrupted while evaluating Swing condition", ie);
    } catch (InvocationTargetException ite) {
      throw new AssertionError("Swing condition failed", ite.getCause());
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
