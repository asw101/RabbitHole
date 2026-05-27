package edu.cmu.cs.dennisc.scenegraph;

import edu.cmu.cs.dennisc.texture.BufferedImageTexture;
import org.junit.Test;

import java.awt.image.BufferedImage;

import static org.junit.Assert.*;

public class TexturedAppearanceBehaviorTest {
  @Test
  public void inferAlphaBlendTracksTheAssignedTextureFlag() {
    BufferedImageTexture opaque = new BufferedImageTexture();
    opaque.setBufferedImage(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
    opaque.setPotentiallyAlphaBlended(false);

    BufferedImageTexture translucent = new BufferedImageTexture();
    translucent.setBufferedImage(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
    translucent.setPotentiallyAlphaBlended(true);

    TexturedAppearance appearance = new TexturedAppearance();
    appearance.setDiffuseColorTextureAndInferAlphaBlend(opaque);
    assertSame(opaque, appearance.diffuseColorTexture.getValue());
    assertFalse(appearance.isDiffuseColorTextureAlphaBlended.getValue());

    appearance.setDiffuseColorTextureAndInferAlphaBlend(translucent);
    assertSame(translucent, appearance.diffuseColorTexture.getValue());
    assertTrue(appearance.isDiffuseColorTextureAlphaBlended.getValue());
  }

  @Test
  public void settersUpdateTextureReferencesAndFlags() {
    TexturedAppearance appearance = new TexturedAppearance();
    BufferedImageTexture diffuse = new BufferedImageTexture();
    BufferedImageTexture bump = new BufferedImageTexture();

    appearance.setDiffuseColorTexture(diffuse);
    appearance.setBumpTexture(bump);
    appearance.setDiffuseColorTextureAlphaBlended(true);
    appearance.setDiffuseColorTextureClamped(true);

    assertSame(diffuse, appearance.diffuseColorTexture.getValue());
    assertSame(bump, appearance.bumpTexture.getValue());
    assertTrue(appearance.isDiffuseColorTextureAlphaBlended.getValue());
    assertTrue(appearance.isDiffuseColorTextureClamped.getValue());
  }
}
