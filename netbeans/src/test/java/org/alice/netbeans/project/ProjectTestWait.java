package org.alice.netbeans.project;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class ProjectTestWait {
  private static final long TIMEOUT_NANOS = TimeUnit.SECONDS.toNanos(5);
  private static final long POLL_MILLIS = 10L;

  private ProjectTestWait() {
  }

  @FunctionalInterface
  public interface CheckedBooleanSupplier {
    boolean getAsBoolean() throws Exception;
  }

  @FunctionalInterface
  public interface CheckedSupplier<T> {
    T get() throws Exception;
  }

  @FunctionalInterface
  public interface ThrowingRunnable {
    void run() throws Exception;
  }

  public static void until(CheckedBooleanSupplier condition, String description) {
    long deadline = System.nanoTime() + TIMEOUT_NANOS;
    while (!evaluate(condition, description) && System.nanoTime() < deadline) {
      poll(description);
    }
    if (!evaluate(condition, description)) {
      throw new AssertionError("Timed out waiting for " + description);
    }
  }

  public static void await(CountDownLatch latch, String description) {
    try {
      if (!latch.await(5, TimeUnit.SECONDS)) {
        throw new AssertionError("Timed out waiting for " + description);
      }
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      throw new AssertionError("Interrupted while waiting for " + description, ie);
    }
  }

  public static <T> T untilNotNull(CheckedSupplier<T> supplier, String description) {
    long deadline = System.nanoTime() + TIMEOUT_NANOS;
    T value = getValue(supplier, description);
    while (value == null && System.nanoTime() < deadline) {
      poll(description);
      value = getValue(supplier, description);
    }
    if (value == null) {
      throw new AssertionError("Timed out waiting for " + description);
    }
    return value;
  }

  public static <T> T get(Future<T> future, String description) {
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

  public static String captureSystemOutUntil(
      ThrowingRunnable action,
      CheckedBooleanSupplier completion,
      String description) throws Exception {
    PrintStream previousOut = System.out;
    ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
    try (PrintStream capture = new PrintStream(capturedOutput, true, StandardCharsets.UTF_8)) {
      System.setOut(capture);
      action.run();
      until(completion, description);
      capture.flush();
    } finally {
      System.setOut(previousOut);
    }
    return capturedOutput.toString(StandardCharsets.UTF_8);
  }

  private static boolean evaluate(CheckedBooleanSupplier condition, String description) {
    try {
      return condition.getAsBoolean();
    } catch (Exception ex) {
      throw new AssertionError("Condition failed while waiting for " + description, ex);
    }
  }

  private static <T> T getValue(CheckedSupplier<T> supplier, String description) {
    try {
      return supplier.get();
    } catch (Exception ex) {
      throw new AssertionError("Supplier failed while waiting for " + description, ex);
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
