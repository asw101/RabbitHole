package org.lgna.project.io.compat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

record BaselineCheckout(Path root) {
  static final String PROPERTY_NAME = "rabbithole.baseline.checkout";
  static final String ENVIRONMENT_VARIABLE = "RABBITHOLE_BASELINE_CHECKOUT";

  BaselineCheckout {
    root = Objects.requireNonNull(root, "root").toAbsolutePath().normalize();
    if (!Files.exists(root)) {
      throw new IllegalArgumentException("Baseline checkout path does not exist: " + root);
    }
    if (!Files.isDirectory(root)) {
      throw new IllegalArgumentException("Baseline checkout path must be a directory: " + root);
    }
  }

  static BaselineMode resolve(Properties properties, Map<String, String> environment) {
    Objects.requireNonNull(properties, "properties");
    Objects.requireNonNull(environment, "environment");
    String propertyValue = blankToNull(properties.getProperty(PROPERTY_NAME));
    if (propertyValue != null) {
      return BaselineMode.available(resolveConfiguredPath(PROPERTY_NAME, propertyValue));
    }
    String environmentValue = blankToNull(environment.get(ENVIRONMENT_VARIABLE));
    if (environmentValue != null) {
      return BaselineMode.available(resolveConfiguredPath(ENVIRONMENT_VARIABLE, environmentValue));
    }
    return BaselineMode.unavailable(
        "Baseline checkout not configured; set " + PROPERTY_NAME + " or " + ENVIRONMENT_VARIABLE);
  }

  static BaselineMode resolveFromCurrentProcess() {
    return resolve(System.getProperties(), System.getenv());
  }

  private static BaselineCheckout resolveConfiguredPath(String source, String value) {
    Path configuredPath = Path.of(value).toAbsolutePath().normalize();
    if (!Files.exists(configuredPath)) {
      throw new IllegalArgumentException(source + " points to a missing baseline checkout: " + value);
    }
    if (!Files.isDirectory(configuredPath)) {
      throw new IllegalArgumentException(source + " must point to a baseline checkout directory: " + value);
    }
    try {
      return new BaselineCheckout(configuredPath.toRealPath());
    } catch (java.io.IOException e) {
      throw new IllegalArgumentException(source + " could not be resolved as a real path: " + value, e);
    }
  }

  private static String blankToNull(String value) {
    if (value == null) {
      return null;
    }
    String trimmed = value.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }
}
