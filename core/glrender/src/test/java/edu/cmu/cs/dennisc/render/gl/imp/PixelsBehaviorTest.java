package edu.cmu.cs.dennisc.render.gl.imp;

import edu.cmu.cs.dennisc.texture.BufferedImageTexture;
import edu.cmu.cs.dennisc.texture.event.TextureListener;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

public class PixelsBehaviorTest {
  @Test
  public void getRgbaCachesBufferForCurrentTextureSize() {
    BufferedImageTexture texture = new BufferedImageTexture();
    texture.setBufferedImage(new BufferedImage(2, 3, BufferedImage.TYPE_INT_ARGB));
    Pixels pixels = new Pixels(texture);

    ByteBuffer first = pixels.getRGBA();
    ByteBuffer second = pixels.getRGBA();

    assertEquals(2 * 3 * 4, first.capacity());
    assertSame(first, second);
  }

  @Test
  public void textureResizeInvalidatesCachedBuffer() {
    BufferedImageTexture texture = new BufferedImageTexture();
    texture.setBufferedImage(new BufferedImage(2, 3, BufferedImage.TYPE_INT_ARGB));
    Pixels pixels = new Pixels(texture);

    ByteBuffer first = pixels.getRGBA();
    texture.setBufferedImage(new BufferedImage(4, 1, BufferedImage.TYPE_INT_ARGB));
    ByteBuffer resized = pixels.getRGBA();

    assertNotSame(first, resized);
    assertEquals(4 * 1 * 4, resized.capacity());
  }

  @Test
  public void releaseRemovesTextureListener() {
    BufferedImageTexture texture = new BufferedImageTexture();
    texture.setBufferedImage(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
    Pixels pixels = new Pixels(texture);

    assertEquals(1, listenerCount(texture));
    pixels.release();

    assertEquals(0, listenerCount(texture));
  }

  private static int listenerCount(BufferedImageTexture texture) {
    int count = 0;
    for (TextureListener ignored : texture.accessTextureListeners()) {
      count++;
    }
    return count;
  }
}
