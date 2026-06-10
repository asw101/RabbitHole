package org.lgna.story.implementation.eventhandling;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

public final class EventTestSupport {
  private static final long TIMEOUT_NANOS = TimeUnit.SECONDS.toNanos(5);
  private static final long POLL_MILLIS = 10L;

  private EventTestSupport() {
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

  public static void until(BooleanSupplier condition, String description) {
    long deadline = System.nanoTime() + TIMEOUT_NANOS;
    while (!condition.getAsBoolean() && System.nanoTime() < deadline) {
      poll(description);
    }
    if (!condition.getAsBoolean()) {
      throw new AssertionError("Timed out waiting for " + description);
    }
  }

  public static void waitForEventDispatchIdle(AbstractEventHandler<?, ?> handler) {
    until(() -> isIdle(handler), "event dispatch to become idle");
  }

  public static void waitForSceneActivationDispatchIdle(EventManager eventManager) {
    waitForEventDispatchIdle(sceneActivationHandler(eventManager));
  }

  private static boolean isIdle(AbstractEventHandler<?, ?> handler) {
    for (Map<Object, Boolean> activeThings : handler.isFiringMap.values()) {
      for (Boolean active : activeThings.values()) {
        if (Boolean.TRUE.equals(active)) {
          return false;
        }
      }
    }
    return true;
  }

  private static SceneActivationHandler sceneActivationHandler(EventManager eventManager) {
    try {
      Field field = EventManager.class.getDeclaredField("sceneActivationHandler");
      field.setAccessible(true);
      return (SceneActivationHandler) field.get(eventManager);
    } catch (ReflectiveOperationException roe) {
      throw new AssertionError("Unable to inspect EventManager scene activation handler", roe);
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
