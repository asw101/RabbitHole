package org.lgna.project.io.compat;

import java.util.Objects;

final class BaselineMode {
  private final BaselineCheckout checkout;
  private final String reason;

  private BaselineMode(BaselineCheckout checkout, String reason) {
    this.checkout = checkout;
    this.reason = Objects.requireNonNull(reason, "reason");
  }

  static BaselineMode available(BaselineCheckout checkout) {
    return new BaselineMode(Objects.requireNonNull(checkout, "checkout"), "available");
  }

  static BaselineMode unavailable(String reason) {
    String normalizedReason = Objects.requireNonNull(reason, "reason").trim();
    if (normalizedReason.isEmpty()) {
      throw new IllegalArgumentException("unavailable baseline reason must not be blank");
    }
    return new BaselineMode(null, normalizedReason);
  }

  boolean isAvailable() {
    return checkout != null;
  }

  BaselineCheckout checkout() {
    if (checkout == null) {
      throw new IllegalStateException("Baseline checkout is unavailable: " + reason);
    }
    return checkout;
  }

  String reason() {
    return reason;
  }
}
