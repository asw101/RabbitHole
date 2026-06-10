package edu.cmu.cs.dennisc.render.gl;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

public final class GlRenderTestWait {
  private static final long TIMEOUT_NANOS = TimeUnit.SECONDS.toNanos(5);
  private static final long POLL_MILLIS = 10L;

  private GlRenderTestWait() {
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

  private static void poll(String description) {
    try {
      Thread.sleep(POLL_MILLIS);
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      throw new AssertionError("Interrupted while waiting for " + description, ie);
    }
  }
}
