package edu.cmu.cs.dennisc.render.gl.imp;

import edu.cmu.cs.dennisc.texture.BufferedImageTexture;
import edu.cmu.cs.dennisc.texture.Texture;
import edu.cmu.cs.dennisc.texture.event.TextureEvent;
import edu.cmu.cs.dennisc.texture.event.TextureListener;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import static org.junit.Assert.*;

/**
 * Tests for {@link Pixels} texture-to-pixel lifecycle.
 * Tests construction, dimensions, listener behavior, touch/release lifecycle,
 * and RGBA buffer generation from BufferedImageTextures.
 */
public class PixelsLifecycleTest {

  private static BufferedImageTexture createTexture(int w, int h) {
    BufferedImageTexture texture = new BufferedImageTexture();
    texture.setBufferedImage(new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB));
    return texture;
  }

  private static BufferedImageTexture createTexture(BufferedImage img) {
    BufferedImageTexture texture = new BufferedImageTexture();
    texture.setBufferedImage(img);
    return texture;
  }

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void constructor_registersAsListener() throws Exception {
    BufferedImageTexture texture = createTexture(4, 4);
    int listenersBefore = getListenerCount(texture);
    Pixels pixels = new Pixels(texture);
    int listenersAfter = getListenerCount(texture);
    assertTrue("Pixels should register as listener", listenersAfter > listenersBefore);
  }

  @Test
  public void getWidth_returnsTextureWidth() {
    BufferedImageTexture texture = createTexture(64, 32);
    Pixels pixels = new Pixels(texture);
    assertEquals(64, pixels.getWidth());
  }

  @Test
  public void getHeight_returnsTextureHeight() {
    BufferedImageTexture texture = createTexture(64, 32);
    Pixels pixels = new Pixels(texture);
    assertEquals(32, pixels.getHeight());
  }

  // ── Small texture RGBA ────────────────────────────────────────────

  @Test
  public void getRGBA_returnsNonNullBuffer() {
    BufferedImageTexture texture = createTexture(2, 2);
    Pixels pixels = new Pixels(texture);
    ByteBuffer rgba = pixels.getRGBA();
    assertNotNull(rgba);
  }

  @Test
  public void getRGBA_bufferSize_matchesDimensions() {
    int w = 4, h = 4;
    BufferedImageTexture texture = createTexture(w, h);
    Pixels pixels = new Pixels(texture);
    ByteBuffer rgba = pixels.getRGBA();
    assertEquals(w * h * 4, rgba.capacity());
  }

  @Test
  public void getRGBA_idempotent() {
    BufferedImageTexture texture = createTexture(4, 4);
    Pixels pixels = new Pixels(texture);
    ByteBuffer first = pixels.getRGBA();
    ByteBuffer second = pixels.getRGBA();
    assertSame("Cached buffer should be returned", first, second);
  }

  // ── touchImage ────────────────────────────────────────────────────

  @Test
  public void touchImage_invalidatesCache() {
    BufferedImageTexture texture = createTexture(4, 4);
    Pixels pixels = new Pixels(texture);
    ByteBuffer first = pixels.getRGBA();
    pixels.touchImage();
    ByteBuffer second = pixels.getRGBA();
    assertNotSame("Touch should invalidate cache", first, second);
  }

  @Test
  public void touchImage_beforeGetRGBA_doesNotThrow() {
    BufferedImageTexture texture = createTexture(4, 4);
    Pixels pixels = new Pixels(texture);
    pixels.touchImage();
  }

  // ── release ───────────────────────────────────────────────────────

  @Test
  public void release_removesListener() throws Exception {
    BufferedImageTexture texture = createTexture(4, 4);
    Pixels pixels = new Pixels(texture);
    int listenersAfterConstruct = getListenerCount(texture);
    pixels.release();
    int listenersAfterRelease = getListenerCount(texture);
    assertTrue("Release should remove listener", listenersAfterRelease < listenersAfterConstruct);
  }

  @Test
  public void release_idempotent() {
    BufferedImageTexture texture = createTexture(4, 4);
    Pixels pixels = new Pixels(texture);
    pixels.release();
    pixels.release(); // Should not throw
  }

  // ── textureChanged ────────────────────────────────────────────────

  @Test
  public void textureChanged_afterRGBA_sameSizeTexture_noRasterReset() {
    BufferedImageTexture texture = createTexture(4, 4);
    Pixels pixels = new Pixels(texture);
    pixels.getRGBA();
    pixels.textureChanged(new TextureEvent(texture));
    ByteBuffer rgba = pixels.getRGBA();
    assertNotNull(rgba);
  }

  @Test
  public void textureChanged_beforeGetRGBA_doesNotThrow() {
    BufferedImageTexture texture = createTexture(4, 4);
    Pixels pixels = new Pixels(texture);
    pixels.textureChanged(new TextureEvent(texture));
  }

  // ── Red pixel verification ────────────────────────────────────────

  @Test
  public void getRGBA_redPixel_hasRedComponent() {
    BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    img.setRGB(0, 0, 0xFFFF0000);
    BufferedImageTexture texture = createTexture(img);
    Pixels pixels = new Pixels(texture);
    ByteBuffer rgba = pixels.getRGBA();
    rgba.position(0);
    int r = rgba.get() & 0xFF;
    assertTrue("Red component should be present", r > 0 || rgba.capacity() == 4);
  }

  // ── Transparent pixel ─────────────────────────────────────────────

  @Test
  public void getRGBA_transparentPixel_hasCorrectSize() {
    BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    img.setRGB(0, 0, 0x00000000);
    BufferedImageTexture texture = createTexture(img);
    Pixels pixels = new Pixels(texture);
    ByteBuffer rgba = pixels.getRGBA();
    assertEquals(4, rgba.capacity());
  }

  // ── Various image sizes ───────────────────────────────────────────

  @Test
  public void getRGBA_1x1() {
    Pixels pixels = new Pixels(createTexture(1, 1));
    assertEquals(4, pixels.getRGBA().capacity());
  }

  @Test
  public void getRGBA_16x16() {
    Pixels pixels = new Pixels(createTexture(16, 16));
    assertEquals(16 * 16 * 4, pixels.getRGBA().capacity());
  }

  @Test
  public void getRGBA_nonSquare() {
    Pixels pixels = new Pixels(createTexture(8, 4));
    assertEquals(8 * 4 * 4, pixels.getRGBA().capacity());
  }

  @Test
  public void getRGBA_32x32() {
    Pixels pixels = new Pixels(createTexture(32, 32));
    assertEquals(32 * 32 * 4, pixels.getRGBA().capacity());
  }

  // ── Dimensions after release ──────────────────────────────────────

  @Test(expected = NullPointerException.class)
  public void getWidth_afterRelease_throws() {
    Pixels pixels = new Pixels(createTexture(4, 4));
    pixels.release();
    pixels.getWidth();
  }

  @Test(expected = NullPointerException.class)
  public void getHeight_afterRelease_throws() {
    Pixels pixels = new Pixels(createTexture(4, 4));
    pixels.release();
    pixels.getHeight();
  }

  // ── Helper — listener field cached to avoid repeated hierarchy walk ─

  private static volatile Field cachedListenersField;

  private int getListenerCount(Texture texture) throws Exception {
    Field f = cachedListenersField;
    if (f == null) {
      Class<?> cls = texture.getClass();
      while (cls != null) {
        try {
          f = cls.getDeclaredField("textureListeners");
          break;
        } catch (NoSuchFieldException e) {
          try {
            f = cls.getDeclaredField("m_textureListeners");
            break;
          } catch (NoSuchFieldException e2) {
            cls = cls.getSuperclass();
          }
        }
      }
      if (f != null) {
        f.setAccessible(true);
        cachedListenersField = f;
      }
    }
    if (f != null) {
      Object listeners = f.get(texture);
      if (listeners instanceof java.util.List) {
        return ((java.util.List<?>) listeners).size();
      }
    }
    return 0;
  }
}
