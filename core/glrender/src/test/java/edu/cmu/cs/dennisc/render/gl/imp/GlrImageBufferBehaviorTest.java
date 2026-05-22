package edu.cmu.cs.dennisc.render.gl.imp;

import edu.cmu.cs.dennisc.color.Color4f;
import org.junit.Assume;
import java.awt.GraphicsEnvironment;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class GlrImageBufferBehaviorTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Test
  public void acquireImageReusesImageForMatchingDimensions() {
    GlrImageBuffer buffer = new GlrImageBuffer(Color4f.BLACK);

    BufferedImage first = buffer.acquireImage(4, 3);
    BufferedImage second = buffer.acquireImage(4, 3);

    assertSame(first, second);
    assertEquals(BufferedImage.TYPE_4BYTE_ABGR, first.getType());
    assertSame(Color4f.BLACK, buffer.getBackgroundColor());
  }

  @Test
  public void acquireImageRecreatesImageWhenDimensionsChange() {
    GlrImageBuffer buffer = new GlrImageBuffer(Color4f.BLACK);

    BufferedImage first = buffer.acquireImage(4, 3);
    BufferedImage resized = buffer.acquireImage(6, 2);

    assertNotSame(first, resized);
    assertEquals(6, resized.getWidth());
    assertEquals(2, resized.getHeight());
  }

  @Test
  public void acquireFloatBufferReturnsNullWhenAlphaIsNotRequired() {
    GlrImageBuffer buffer = new GlrImageBuffer(Color4f.BLACK);

    assertNull(buffer.acquireFloatBuffer(3, 2));
  }

  @Test
  public void acquireFloatBufferAllocatesAndResizesForTransparentBackground() {
    GlrImageBuffer buffer = new GlrImageBuffer(null);

    FloatBuffer first = buffer.acquireFloatBuffer(3, 2);
    FloatBuffer second = buffer.acquireFloatBuffer(3, 2);
    FloatBuffer resized = buffer.acquireFloatBuffer(2, 5);

    assertEquals(6, first.capacity());
    assertSame(first, second);
    assertNotSame(first, resized);
    assertEquals(10, resized.capacity());
  }
}
