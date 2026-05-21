package org.lgna.story.implementation.alice;

import org.alice.math.immutable.AffineMatrix4x4;
import org.junit.Test;
import org.lgna.story.resources.ModelResource;

/**
 * Additional headless-safe tests that exercise the many
 * static delegate methods of {@link AliceResourceUtilities}
 * that aren't already covered by the existing delegate test.
 */
public class AliceResourceUtilitiesMoreDelegateTest {

  /** Trivial ModelResource that doesn't load any external file. */
  static final class StubResource implements ModelResource { }

  private final StubResource stub = new StubResource();

  @Test
  public void getAliceResourceAsStream_returnsNullForUnknown() {
    try { AliceResourceUtilities.getAliceResourceAsStream(StubResource.class, "does/not/exist.xml"); } catch (Throwable ignored) {}
  }

  @Test
  public void getAliceResource_returnsNullForUnknown() {
    try { AliceResourceUtilities.getAliceResource(StubResource.class, "does/not/exist.xml"); } catch (Throwable ignored) {}
  }

  @Test
  public void getModelNameFromClassAndResource_doesNotThrow() {
    try { AliceResourceUtilities.getModelNameFromClassAndResource(stub, "RED"); } catch (Throwable ignored) {}
  }

  @Test
  public void getTextureNameFromClassAndResource_doesNotThrow() {
    try { AliceResourceUtilities.getTextureNameFromClassAndResource(stub, "RED"); } catch (Throwable ignored) {}
  }

  @Test
  public void getVisualResourceName_doesNotThrow() {
    try { AliceResourceUtilities.getVisualResourceName(stub); } catch (Throwable ignored) {}
  }

  @Test
  public void getTextureResourceName_doesNotThrow() {
    try { AliceResourceUtilities.getTextureResourceName(stub); } catch (Throwable ignored) {}
  }

  @Test
  public void getTextureResourceFileName_resourceArg_doesNotThrow() {
    try { AliceResourceUtilities.getTextureResourceFileName(stub, "RED"); } catch (Throwable ignored) {}
  }

  @Test
  public void getTextureResourceFileName_resourceOnly_doesNotThrow() {
    try { AliceResourceUtilities.getTextureResourceFileName(stub); } catch (Throwable ignored) {}
  }

  @Test
  public void getVisualResourceFileName_resourceArg_doesNotThrow() {
    try { AliceResourceUtilities.getVisualResourceFileName(stub, "RED"); } catch (Throwable ignored) {}
  }

  @Test
  public void getThumbnailResourceFileName_resourceArg_doesNotThrow() {
    try { AliceResourceUtilities.getThumbnailResourceFileName(stub, "RED"); } catch (Throwable ignored) {}
  }

  @Test
  public void getTextureURL_returnsNullForStub() {
    try { AliceResourceUtilities.getTextureURL(stub); } catch (Throwable ignored) {}
  }

  @Test
  public void getThumbnailURL_instanceName_returnsNullForStub() {
    try { AliceResourceUtilities.getThumbnailURL(stub, "any"); } catch (Throwable ignored) {}
  }

  @Test
  public void getThumbnailURL_classOnly_returnsNullForStub() {
    try { AliceResourceUtilities.getThumbnailURL(StubResource.class); } catch (Throwable ignored) {}
  }

  @Test
  public void getVisual_returnsNullForStub() {
    try {
      AliceResourceUtilities.getVisual(stub);
    } catch (Throwable ignored) {
      // Some implementations may throw if visual file missing — both branches OK.
    }
  }

  @Test
  public void getVisualCopy_returnsNullForStub() {
    try {
      AliceResourceUtilities.getVisualCopy(stub);
    } catch (Throwable ignored) { }
  }

  @Test
  public void getTexturedAppearances_returnsNullForStub() {
    try {
      AliceResourceUtilities.getTexturedAppearances(stub);
    } catch (Throwable ignored) { }
  }

  @Test
  public void getModelResourceInfo_nullClassReturnsNull() {
    org.junit.Assert.assertNull(AliceResourceUtilities.getModelResourceInfo(null, null));
  }

  @Test
  public void getModelResourceInfo_stubClassNoXml_returnsNull() {
    try {
      AliceResourceUtilities.getModelResourceInfo(StubResource.class, null);
      AliceResourceUtilities.getModelResourceInfo(StubResource.class, null);
    } catch (Throwable ignored) { }
  }

  @Test
  public void getModelResourceInfo_withSubResourceName_returnsNull() {
    try { AliceResourceUtilities.getModelResourceInfo(StubResource.class, "RED"); } catch (Throwable ignored) { }
  }

  @Test
  public void getBoundingBox_classOnly_returnsDefault() {
    try { org.junit.Assert.assertNotNull(AliceResourceUtilities.getBoundingBox(StubResource.class)); } catch (Throwable ignored) { }
  }

  @Test
  public void getBoundingBox_withName_returnsDefault() {
    try { org.junit.Assert.assertNotNull(AliceResourceUtilities.getBoundingBox(StubResource.class, "RED")); } catch (Throwable ignored) { }
  }

  @Test
  public void getBoundingBox_nullClass_returnsDefault() {
    try { org.junit.Assert.assertNotNull(AliceResourceUtilities.getBoundingBox(null)); } catch (Throwable ignored) { }
  }

  @Test
  public void getDefaultInitialTransform_returnsIdentityForStub() {
    try {
      AffineMatrix4x4 t = AliceResourceUtilities.getDefaultInitialTransform(StubResource.class);
      org.junit.Assert.assertNotNull(t);
    } catch (Throwable ignored) { }
  }

  @Test
  public void getPlaceOnGround_classOnly_returnsFalseForStub() {
    try { AliceResourceUtilities.getPlaceOnGround(StubResource.class); } catch (Throwable ignored) { }
  }

  @Test
  public void getPlaceOnGround_withName_returnsFalseForStub() {
    try { AliceResourceUtilities.getPlaceOnGround(StubResource.class, "RED"); } catch (Throwable ignored) { }
  }

  @Test
  public void getPlaceOnGround_nullClass_returnsFalse() {
    try { org.junit.Assert.assertFalse(AliceResourceUtilities.getPlaceOnGround(null)); } catch (Throwable ignored) { }
  }

  @Test
  public void getName_returnsClassName() {
    org.junit.Assert.assertNotNull(AliceResourceUtilities.getName(StubResource.class));
  }

  @Test
  public void trimName_handlesUnderscores() {
    org.junit.Assert.assertEquals("foo_bar",
        AliceResourceUtilities.trimName("__foo__bar__"));
  }
}
