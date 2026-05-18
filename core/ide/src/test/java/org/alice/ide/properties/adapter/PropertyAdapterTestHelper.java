package org.alice.ide.properties.adapter;

import static org.junit.Assert.fail;

/**
 * Shared helper for property-adapter tests that need to poll for async value changes.
 */
final class PropertyAdapterTestHelper {
  private PropertyAdapterTestHelper() {
  }

  static <T> void waitForValue(java.util.function.Supplier<T> currentValue, T expected) {
    long deadline = System.currentTimeMillis() + 2000;
    while (System.currentTimeMillis() < deadline) {
      if (expected.equals(currentValue.get())) {
        return;
      }
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        throw new AssertionError(e);
      }
    }
    fail("Timed out waiting for property value " + expected);
  }
}
