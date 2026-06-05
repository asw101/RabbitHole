package org.lgna.project.io.compat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.*;

public class BaselineCheckoutTest {
  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void missingConfigurationResolvesToUnavailableFallbackMode() {
    BaselineMode mode = BaselineCheckout.resolve(new Properties(), Map.of());

    assertFalse(mode.isAvailable());
    assertTrue(mode.reason().contains("not configured"));
    assertThrows(IllegalStateException.class, mode::checkout);
  }

  @Test
  public void systemPropertyTakesPrecedenceOverEnvironmentVariable() throws Exception {
    File envCheckout = temporaryFolder.newFolder("env-baseline");
    File propertyCheckout = temporaryFolder.newFolder("property-baseline");
    Properties properties = new Properties();
    properties.setProperty(BaselineCheckout.PROPERTY_NAME, propertyCheckout.getAbsolutePath());

    BaselineMode mode = BaselineCheckout.resolve(
        properties,
        Map.of(BaselineCheckout.ENVIRONMENT_VARIABLE, envCheckout.getAbsolutePath()));

    assertTrue(mode.isAvailable());
    assertEquals(propertyCheckout.toPath().toRealPath(), mode.checkout().root());
  }

  @Test
  public void environmentVariableIsUsedWhenSystemPropertyIsUnset() throws Exception {
    File envCheckout = temporaryFolder.newFolder("env-only-baseline");

    BaselineMode mode = BaselineCheckout.resolve(
        new Properties(),
        Map.of(BaselineCheckout.ENVIRONMENT_VARIABLE, envCheckout.getAbsolutePath()));

    assertTrue(mode.isAvailable());
    assertEquals(envCheckout.toPath().toRealPath(), mode.checkout().root());
  }

  @Test
  public void configuredMissingPathIsHardFailure() {
    File missing = new File(temporaryFolder.getRoot(), "missing-baseline");
    Properties properties = new Properties();
    properties.setProperty(BaselineCheckout.PROPERTY_NAME, missing.getAbsolutePath());

    IllegalArgumentException thrown = assertThrows(
        IllegalArgumentException.class,
        () -> BaselineCheckout.resolve(properties, Map.of()));

    assertTrue(thrown.getMessage().contains(BaselineCheckout.PROPERTY_NAME));
    assertTrue(thrown.getMessage().contains(missing.getAbsolutePath()));
  }

  @Test
  public void configuredFilePathIsHardFailure() throws Exception {
    File file = temporaryFolder.newFile("not-a-checkout");
    Properties properties = new Properties();
    properties.setProperty(BaselineCheckout.PROPERTY_NAME, file.getAbsolutePath());

    IllegalArgumentException thrown = assertThrows(
        IllegalArgumentException.class,
        () -> BaselineCheckout.resolve(properties, Map.of()));

    assertTrue(thrown.getMessage().contains("directory"));
    assertTrue(thrown.getMessage().contains(file.getAbsolutePath()));
  }
}
