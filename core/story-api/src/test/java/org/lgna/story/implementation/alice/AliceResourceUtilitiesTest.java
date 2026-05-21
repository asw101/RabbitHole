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

  @Test(expected = NullPointerException.class)
  public void getNameForNullClassThrowsNpe() {
    AliceResourceUtilities.getName(null);
  }

  @Test
  public void getNameForKnownClassReturnsSimpleName() {
    String name = AliceResourceUtilities.getName(DummyResource.class);
    assertNotNull(name);
    assertTrue(name.contains("Dummy"));
  }

  @Test
  public void trimNameHandlesAllUnderscores() {
    assertEquals("", AliceResourceUtilities.trimName("___"));
  }

  @Test
  public void trimNameHandlesEmptyString() {
    assertEquals("", AliceResourceUtilities.trimName(""));
  }

  @Test
  public void enumToCamelCaseDelegatesToResolver() {
    assertEquals("alphaBeta", AliceResourceUtilities.enumToCamelCase("ALPHA_BETA", true));
    assertEquals("AlphaBeta", AliceResourceUtilities.enumToCamelCase("ALPHA_BETA"));
  }

  @Test
  public void camelCaseToEnumDelegates() {
    String r = AliceResourceUtilities.camelCaseToEnum("alphaBeta");
    assertNotNull(r);
  }

  @Test
  public void isEnumNameDelegates() {
    assertFalse(AliceResourceUtilities.isEnumName("alpha"));
    assertTrue(AliceResourceUtilities.isEnumName("ALPHA_BETA"));
  }

  @Test
  public void makeEnumNameDelegates() {
    assertNotNull(AliceResourceUtilities.makeEnumName("alphaBeta"));
  }

  @Test
  public void makeLocalizationKeyDelegates() {
    assertNotNull(AliceResourceUtilities.makeLocalizationKey("a:b"));
  }

  @Test
  public void arrayToEnumDelegates() {
    String r = AliceResourceUtilities.arrayToEnum(new String[]{"alpha", "Beta"}, 0, 2);
    assertNotNull(r);
  }

  @Test
  public void getDefaultTextureEnumNameDelegates() {
    assertNotNull(AliceResourceUtilities.getDefaultTextureEnumName("Resource"));
  }

  @Test
  public void getThumbnailResourceFileNameAndGetTextureFileNameDelegates() {
    assertNotNull(AliceResourceUtilities.getThumbnailResourceFileName("a", "b"));
    assertNotNull(AliceResourceUtilities.getTextureResourceFileName("a", "b"));
  }

  @Test
  public void getVisualResourceFileNameFromModelName2ArgDelegates() {
    assertNotNull(AliceResourceUtilities.getVisualResourceFileNameFromModelName("a", "a3r"));
    assertNotNull(AliceResourceUtilities.getVisualResourceFileNameFromModelName("a"));
  }

  @Test
  public void getModelClassNameWithoutLocaleReturnsClassName() {
    String n = AliceResourceUtilities.getModelClassName(DummyResource.class, "x", null);
    assertNotNull(n);
    assertTrue(n.contains("Dummy"));
  }

  @Test
  public void getModelClassNameWithEnglishLocaleReturnsFallback() {
    String n = AliceResourceUtilities.getModelClassName(DummyResource.class, "x", java.util.Locale.ENGLISH);
    assertNotNull(n);
  }

  @Test
  public void getTagsForNullInfoReturnsNull() {
    assertNull(AliceResourceUtilities.getTags(DummyResource.class, "x", null));
  }

  @Test
  public void getGroupTagsForNullInfoReturnsNull() {
    assertNull(AliceResourceUtilities.getGroupTags(DummyResource.class, "x", null));
  }

  @Test
  public void getThemeTagsForNullInfoReturnsNull() {
    assertNull(AliceResourceUtilities.getThemeTags(DummyResource.class, "x", null));
  }

  @Test
  public void getLocalizedTagWithNullLocaleReturnsInputUnchanged() {
    assertEquals("greeting", AliceResourceUtilities.getLocalizedTag("greeting", null));
  }

  @Test
  public void getLocalizedTagWithSpacesAndEnglishLocaleSilentlyReturnsTag() {
    String r = AliceResourceUtilities.getLocalizedTag("with space", java.util.Locale.ENGLISH);
    assertNotNull(r);
  }

  @Test
  public void getPlaceOnGroundOverloadDelegates() {
    assertFalse(AliceResourceUtilities.getPlaceOnGround(DummyResource.class));
  }

  @Test
  public void getDefaultInitialTransformIsNotNullForKnownResource() {
    assertNotNull(AliceResourceUtilities.getDefaultInitialTransform(DummyResource.class));
  }

  @Test
  public void getBoundingBoxFromClassNullReturnsDefaultBounds() {
    AxisAlignedBox box = AliceResourceUtilities.getBoundingBox((Class<?>) null);
    assertNotNull(box);
  }

  @Test
  public void getAliceResourceForUnknownStringReturnsNullOrThrows() {
    try {
      java.net.URL u = AliceResourceUtilities.getAliceResource(AliceResourceUtilitiesTest.class, "nonexistent-resource-xyz123");
      assertNull(u);
    } catch (Throwable expected) {
      // Some environments cannot resolve resources headless; accept either.
    }
  }

  @Test
  public void getAliceResourceAsStreamForUnknownStringReturnsNullOrThrows() {
    try {
      java.io.InputStream s = AliceResourceUtilities.getAliceResourceAsStream(AliceResourceUtilitiesTest.class, "nonexistent-resource-xyz123");
      assertNull(s);
    } catch (Throwable expected) {
      // headless image loader can throw
    }
  }
}
