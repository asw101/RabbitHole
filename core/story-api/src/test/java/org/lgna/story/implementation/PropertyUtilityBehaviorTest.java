package org.lgna.story.implementation;

import edu.cmu.cs.dennisc.scenegraph.TexturedVisual;
import org.junit.Test;
import org.lgna.common.resources.ImageResource;
import org.lgna.story.Color;
import org.lgna.story.ImageSource;
import org.lgna.story.Paint;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class PropertyUtilityBehaviorTest {
  private static class StubOwner extends PropertyOwnerImp {
    @Override
    public ProgramImp getProgram() {
      return null;
    }
  }

  private static class StubFloatProperty extends FloatProperty {
    private float value;

    StubFloatProperty(float initialValue) {
      super(new StubOwner());
      this.value = initialValue;
    }

    @Override
    public Float getValue() {
      return this.value;
    }

    @Override
    protected void handleSetValue(Float value) {
      this.value = value;
    }

    float interpolateValue(float a, float b, double portion) {
      return super.interpolate(a, b, portion);
    }
  }

  private static class StubPaintProperty extends PaintProperty {
    private Paint appliedValue = Color.WHITE;

    StubPaintProperty() {
      super(new StubOwner());
    }

    @Override
    protected void internalSetValue(Paint value) {
      this.appliedValue = value;
    }

    Paint getAppliedValue() {
      return this.appliedValue;
    }

    Paint interpolateValue(Paint a, Paint b, double portion) {
      return super.interpolate(a, b, portion);
    }
  }

  private static BufferedImage createImage(int width, int height, int imageType, int rgb) {
    BufferedImage image = new BufferedImage(width, height, imageType);
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        image.setRGB(x, y, rgb);
      }
    }
    return image;
  }

  private static ImageResource createImageResource(BufferedImage image) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(image, "png", baos);
    ImageResource resource = new ImageResource(UUID.randomUUID());
    resource.setName("sample.png");
    resource.setOriginalFileName("sample.png");
    resource.setContent("image/png", baos.toByteArray());
    return resource;
  }

  @Test
  public void floatPropertyInterpolateBlendsBetweenEndpoints() {
    StubFloatProperty property = new StubFloatProperty(0.0f);

    assertEquals(2.5f, property.interpolateValue(1.0f, 4.0f, 0.5), 1e-6f);
    assertEquals(-1.0f, property.interpolateValue(-1.0f, 3.0f, 0.0), 1e-6f);
  }

  @Test
  public void paintPropertyDefaultsToWhiteAndTracksAppliedValue() {
    StubPaintProperty property = new StubPaintProperty();

    assertEquals(Color.WHITE, property.getValue());

    property.setValue(Color.BLUE);

    assertEquals(Color.BLUE, property.getValue());
    assertSame(Color.BLUE, property.getAppliedValue());
  }

  @Test
  public void paintPropertyInterpolatesColorsUsingColorInterpolation() {
    StubPaintProperty property = new StubPaintProperty();

    Color halfway = (Color) property.interpolateValue(Color.BLACK, Color.WHITE, 0.5);

    assertEquals(0.5, halfway.getRed(), 1e-6);
    assertEquals(0.5, halfway.getGreen(), 1e-6);
    assertEquals(0.5, halfway.getBlue(), 1e-6);
  }

  @Test
  public void paintPropertyUsesTargetPaintForNonColorInterpolation() throws IOException {
    StubPaintProperty property = new StubPaintProperty();
    ImageSource source = new ImageSource(createImageResource(createImage(2, 2, BufferedImage.TYPE_INT_RGB, 0xFFAA3300)));
    ImageSource target = new ImageSource(createImageResource(createImage(2, 2, BufferedImage.TYPE_INT_ARGB, 0x880033AA)));

    Paint interpolated = property.interpolateValue(source, target, 0.25);

    assertSame(target, interpolated);
  }

  @Test
  public void texturedPaintUtilitiesAppliesColorPaintWithoutTexture() {
    TexturedVisual visual = new TexturedVisual() {
    };

    TexturedPaintUtilities.setPaint(visual, Color.RED);

    assertEquals(Color.RED.toColor4f(), visual.getAppearance().diffuseColor.getValue());
    assertNull(visual.getTexture());
  }

  @Test
  public void texturedPaintUtilitiesAppliesImageSourceTextureAndWhiteDiffuseColor() throws IOException {
    TexturedVisual visual = new TexturedVisual() {
    };
    ImageSource source = new ImageSource(createImageResource(createImage(2, 2, BufferedImage.TYPE_INT_ARGB, 0x88ABCDEF)));

    TexturedPaintUtilities.setPaint(visual, source);

    assertNotNull(visual.getTexture());
    assertSame(source.getTextureIfPresent(), visual.getTexture());
    assertEquals(Color.WHITE.toColor4f(), visual.getAppearance().diffuseColor.getValue());
    assertTrue(visual.getAppearance().isDiffuseColorTextureAlphaBlended.getValue());
  }
}
