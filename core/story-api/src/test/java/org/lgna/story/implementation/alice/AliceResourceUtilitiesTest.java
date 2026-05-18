package org.lgna.story.implementation.alice;

import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.AxisAlignedBox;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

/** Headless-safe value and null-path tests for AliceResourceUtilities. */
public class AliceResourceUtilitiesTest {

  @Test
  public void modelResourceExtensionConstantMatchesExpectedValue() {
    assertEquals("a3r", AliceResourceUtilities.MODEL_RESOURCE_EXTENSION);
  }

  @Test
  public void textureResourceExtensionConstantMatchesExpectedValue() {
    assertEquals("a3t", AliceResourceUtilities.TEXTURE_RESOURCE_EXTENSION);
  }

  @Test
  public void trimNameCollapsesWhitespaceAndUnderscores() {
    assertEquals("alpha_beta", AliceResourceUtilities.trimName("  __alpha___beta__  "));
  }

  @Test
  public void getKeyIncludesResourceNameWhenProvided() {
    String key = AliceResourceUtilities.getKey(DummyResource.class, "Blue");
    assertTrue(key.endsWith("DummyResourceBlue"));
  }

  @Test
  public void getKeyUsesOnlyClassNameWhenResourceIsNull() {
    String key = AliceResourceUtilities.getKey(DummyResource.class, null);
    assertTrue(key.endsWith("DummyResource"));
  }

  @Test
  public void getModelResourceInfoReturnsNullForNullClass() {
    assertNull(AliceResourceUtilities.getModelResourceInfo(null, "anything"));
  }

  @Test
  public void getBoundingBoxForNullClassReturnsDefaultBounds() {
    AxisAlignedBox box = AliceResourceUtilities.getBoundingBox(null, null);
    assertEquals(-0.5, box.getXMinimum(), 1e-6);
    assertEquals(0.0, box.getYMinimum(), 1e-6);
    assertEquals(0.5, box.getXMaximum(), 1e-6);
    assertEquals(1.0, box.getYMaximum(), 1e-6);
  }

  @Test
  public void getDefaultInitialTransformForNullClassIsIdentity() {
    assertEquals(AffineMatrix4x4.IDENTITY, AliceResourceUtilities.getDefaultInitialTransform(null));
  }

  @Test
  public void getPlaceOnGroundForNullClassReturnsFalse() {
    assertFalse(AliceResourceUtilities.getPlaceOnGround(null, null));
  }

  @Test
  public void protectedConstructorThrowsAssertionErrorWhenInvokedReflectively() throws Exception {
    Constructor<AliceResourceUtilities> constructor = AliceResourceUtilities.class.getDeclaredConstructor();
    constructor.setAccessible(true);
    try {
      constructor.newInstance();
      fail("Expected AssertionError");
    } catch (InvocationTargetException ite) {
      assertTrue(ite.getCause() instanceof AssertionError);
    }
  }

  private static final class DummyResource implements org.lgna.story.resources.ModelResource {
  }
}
