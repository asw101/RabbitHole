package org.alice.ide.properties.adapter;

import org.alice.ide.IdeTestWait;

/**
 * Shared helper for property-adapter tests that need to poll for async value changes.
 */
final class PropertyAdapterTestHelper {
  private PropertyAdapterTestHelper() {
  }

  static <T> void waitForValue(java.util.function.Supplier<T> currentValue, T expected) {
    IdeTestWait.until(() -> expected.equals(currentValue.get()),
        "property value " + expected);
  }
}
