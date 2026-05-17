package edu.cmu.cs.dennisc.scenegraph;

import edu.cmu.cs.dennisc.color.Color4f;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TexturedAppearanceTest {

  @Test
  public void defaultDiffuseColorTextureIsNull() {
    TexturedAppearance ta = new TexturedAppearance();
    assertNull(ta.diffuseColorTexture.getValue());
  }

  @Test
  public void defaultBumpTextureIsNull() {
    TexturedAppearance ta = new TexturedAppearance();
    assertNull(ta.bumpTexture.getValue());
  }

  @Test
  public void defaultTextureIdIsMinusOne() {
    TexturedAppearance ta = new TexturedAppearance();
    assertEquals(-1, ta.textureId.getValue().intValue());
  }

  @Test
  public void defaultIsNotAlphaBlended() {
    TexturedAppearance ta = new TexturedAppearance();
    assertFalse(ta.isDiffuseColorTextureAlphaBlended.getValue());
  }

  @Test
  public void defaultIsNotClamped() {
    TexturedAppearance ta = new TexturedAppearance();
    assertFalse(ta.isDiffuseColorTextureClamped.getValue());
  }

  @Test
  public void setDiffuseColorTextureAlphaBlendedUpdatesValue() {
    TexturedAppearance ta = new TexturedAppearance();
    ta.setDiffuseColorTextureAlphaBlended(true);
    assertTrue(ta.isDiffuseColorTextureAlphaBlended.getValue());
  }

  @Test
  public void setDiffuseColorTextureClampedUpdatesValue() {
    TexturedAppearance ta = new TexturedAppearance();
    ta.setDiffuseColorTextureClamped(true);
    assertTrue(ta.isDiffuseColorTextureClamped.getValue());
  }

  @Test
  public void setDiffuseColorTextureWithNull() {
    TexturedAppearance ta = new TexturedAppearance();
    ta.setDiffuseColorTexture(null);
    assertNull(ta.diffuseColorTexture.getValue());
  }

  @Test
  public void setBumpTextureWithNull() {
    TexturedAppearance ta = new TexturedAppearance();
    ta.setBumpTexture(null);
    assertNull(ta.bumpTexture.getValue());
  }

  @Test
  public void setDiffuseColorTextureAndInferAlphaBlendWithNull() {
    TexturedAppearance ta = new TexturedAppearance();
    ta.setDiffuseColorTextureAndInferAlphaBlend(null);
    assertNull(ta.diffuseColorTexture.getValue());
    assertFalse(ta.isDiffuseColorTextureAlphaBlended.getValue());
  }

  @Test
  public void releaseWithNullTexturesDoesNotThrow() {
    TexturedAppearance ta = new TexturedAppearance();
    ta.release();
    // Should not throw
  }

  @Test
  public void inheritsSimpleAppearanceProperties() {
    TexturedAppearance ta = new TexturedAppearance();
    ta.setDiffuseColor(Color4f.RED);
    // Inherits from SimpleAppearance
    assertNotNull(ta.opacity);
    assertEquals(1.0f, ta.opacity.getValue(), 0.001f);
  }

  @Test
  public void textureIdCanBeSet() {
    TexturedAppearance ta = new TexturedAppearance();
    ta.textureId.setValue(42);
    assertEquals(42, ta.textureId.getValue().intValue());
  }
}
