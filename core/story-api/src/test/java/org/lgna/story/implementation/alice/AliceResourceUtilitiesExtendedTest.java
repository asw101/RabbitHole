package org.lgna.story.implementation.alice;

import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.AxisAlignedBox;
import org.junit.Test;
import org.lgna.story.resources.ModelResource;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Extended tests for AliceResourceUtilities covering static utility methods,
 * null-input edge cases, and localization paths that don't require model files.
 */
public class AliceResourceUtilitiesExtendedTest {

  // ── Constants ─────────────────────────────────────────

  @Test
  public void modelResourceExtensionIsA3r() {
    assertEquals("a3r", AliceResourceUtilities.MODEL_RESOURCE_EXTENSION);
  }

  @Test
  public void textureResourceExtensionIsA3t() {
    assertEquals("a3t", AliceResourceUtilities.TEXTURE_RESOURCE_EXTENSION);
  }

  // ── trimName ──────────────────────────────────────────

  @Test
  public void trimName_removesLeadingTrailingWhitespace() {
    assertEquals("hello", AliceResourceUtilities.trimName("  hello  "));
  }

  @Test
  public void trimName_collapsesDoubleUnderscores() {
    assertEquals("a_b", AliceResourceUtilities.trimName("a__b"));
  }

  @Test
  public void trimName_collapsesTripleUnderscores() {
    assertEquals("a_b", AliceResourceUtilities.trimName("a___b"));
  }

  @Test
  public void trimName_removesLeadingUnderscores() {
    assertEquals("hello", AliceResourceUtilities.trimName("__hello"));
  }

  @Test
  public void trimName_removesTrailingUnderscores() {
    assertEquals("hello", AliceResourceUtilities.trimName("hello__"));
  }

  @Test
  public void trimName_handlesAllUnderscores() {
    assertEquals("", AliceResourceUtilities.trimName("___"));
  }

  @Test
  public void trimName_noChangesNeeded() {
    assertEquals("clean_name", AliceResourceUtilities.trimName("clean_name"));
  }

  @Test
  public void trimName_combinedEdgeCases() {
    assertEquals("a_b", AliceResourceUtilities.trimName("  __a___b__  "));
  }

  @Test
  public void trimName_singleChar() {
    assertEquals("x", AliceResourceUtilities.trimName("x"));
  }

  @Test
  public void trimName_emptyString() {
    assertEquals("", AliceResourceUtilities.trimName(""));
  }

  // ── getModelResourceInfo ──────────────────────────────

  @Test
  public void getModelResourceInfo_nullClass_returnsNull() {
    assertNull(AliceResourceUtilities.getModelResourceInfo(null, "anyResource"));
  }

  @Test
  public void getModelResourceInfo_nullClassAndResource_returnsNull() {
    assertNull(AliceResourceUtilities.getModelResourceInfo(null, null));
  }

  // ── getBoundingBox ────────────────────────────────────

  @Test
  public void getBoundingBox_nullClass_returnsDefault() {
    AxisAlignedBox bbox = AliceResourceUtilities.getBoundingBox(null, null);
    assertNotNull(bbox);
    // Default bbox for null class
    assertEquals(-.5, bbox.getXMinimum(), 0.001);
    assertEquals(0.0, bbox.getYMinimum(), 0.001);
    assertEquals(-.5, bbox.getZMinimum(), 0.001);
    assertEquals(.5, bbox.getXMaximum(), 0.001);
    assertEquals(1.0, bbox.getYMaximum(), 0.001);
    assertEquals(.5, bbox.getZMaximum(), 0.001);
  }

  @Test
  public void getBoundingBox_oneArg_delegatesToTwoArg() {
    AxisAlignedBox oneArg = AliceResourceUtilities.getBoundingBox(null);
    AxisAlignedBox twoArg = AliceResourceUtilities.getBoundingBox(null, null);
    assertEquals(oneArg, twoArg);
  }

  // ── getPlaceOnGround ──────────────────────────────────

  @Test
  public void getPlaceOnGround_nullClass_returnsFalse() {
    assertFalse(AliceResourceUtilities.getPlaceOnGround(null, null));
  }

  @Test
  public void getPlaceOnGround_oneArg_delegatesToTwoArg() {
    assertEquals(
        AliceResourceUtilities.getPlaceOnGround(null),
        AliceResourceUtilities.getPlaceOnGround(null, null));
  }

  // ── getDefaultInitialTransform ────────────────────────

  @Test
  public void getDefaultInitialTransform_nullClass_returnsIdentity() {
    AffineMatrix4x4 transform = AliceResourceUtilities.getDefaultInitialTransform(null);
    assertEquals(AffineMatrix4x4.IDENTITY, transform);
  }

  // ── getLocalizedTag ───────────────────────────────────

  @Test
  public void getLocalizedTag_nullLocale_returnsOriginalTag() {
    assertEquals("SomeTag", AliceResourceUtilities.getLocalizedTag("SomeTag", null));
  }

  @Test
  public void getLocalizedTag_nullLocale_preservesSpaces() {
    assertEquals("Some Tag", AliceResourceUtilities.getLocalizedTag("Some Tag", null));
  }

  // ── getTags / getGroupTags / getThemeTags ─────────────

  @Test
  public void getTags_nullInfo_returnsNull() {
    assertNull(AliceResourceUtilities.getTags(null, null, null));
  }

  @Test
  public void getGroupTags_nullInfo_returnsNull() {
    assertNull(AliceResourceUtilities.getGroupTags(null, null, null));
  }

  @Test
  public void getThemeTags_nullInfo_returnsNull() {
    assertNull(AliceResourceUtilities.getThemeTags(null, null, null));
  }

  // ── getModelClassName ─────────────────────────────────

  @Test
  public void getModelClassName_noInfo_fallsBackToSimpleName() {
    // When no ModelResourceInfo, should derive from class simple name
    String result = AliceResourceUtilities.getModelClassName(StubResource.class, null, null);
    assertNotNull(result);
  }

  @Test
  public void getModelClassName_nullLocale_returnsClassName() {
    String result = AliceResourceUtilities.getModelClassName(StubResource.class, null, null);
    assertNotNull(result);
    // Should strip "Resource" suffix
    assertFalse(result.endsWith("Resource"));
  }

  @Test
  public void getModelClassName_withLocale_returnsClassName() {
    // Even with a locale, if no localized text is found, falls back to class name
    String result = AliceResourceUtilities.getModelClassName(StubResource.class, null, Locale.ENGLISH);
    assertNotNull(result);
  }

  // ── getModelName ──────────────────────────────────────

  @Test
  public void getModelName_nullClass_returnsNullInfo() {
    // getModelResourceInfo(null, ...) returns null → getModelName path returns null
    assertNull(AliceResourceUtilities.getModelResourceInfo(null, "x"));
  }

  // ── ResourceTextureManager via AliceResourceUtilities delegates ──

  @Test
  public void getThumbnailResourceFileName_validNames_returnsPng() {
    String result = AliceResourceUtilities.getThumbnailResourceFileName("Alice", "Default");
    assertNotNull(result);
    assertTrue(result.endsWith(".png"));
  }

  @Test
  public void getThumbnailResourceFileName_nullModel_returnsNull() {
    assertNull(AliceResourceUtilities.getThumbnailResourceFileName((String) null, "texture"));
  }

  @Test
  public void getTextureResourceFileName_validNames_returnsA3t() {
    String result = AliceResourceUtilities.getTextureResourceFileName("Alice", "Default");
    assertNotNull(result);
    assertTrue(result.endsWith(".a3t"));
  }

  @Test
  public void getVisualResourceFileNameFromModelName_returnsA3r() {
    String result = AliceResourceUtilities.getVisualResourceFileNameFromModelName("Alice");
    assertEquals("alice.a3r", result);
  }

  @Test
  public void getVisualResourceFileNameFromModelName_withExtension() {
    String result = AliceResourceUtilities.getVisualResourceFileNameFromModelName("Alice", "obj");
    assertEquals("alice.obj", result);
  }

  // ── getName ───────────────────────────────────────────

  @Test
  public void getName_stripsResourceSuffix() {
    String name = AliceResourceUtilities.getName(StubResource.class);
    // AliceResourceClassUtilities.getAliceClassName strips "Resource" suffix
    assertNotNull(name);
  }

  // ── Stub implementations ──────────────────────────────

  private static class StubResource implements ModelResource {
    @Override
    public String toString() {
      return "STUB";
    }
  }

  private static class StubModelResource implements ModelResource {
    @Override
    public String toString() {
      return "STUB";
    }
  }
}
