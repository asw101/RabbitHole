package edu.cmu.cs.dennisc.render.joglrenderer;

import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.awt.TextureRenderer;
import com.jogamp.opengl.util.packrect.RectanglePacker;
import com.jogamp.opengl.util.texture.Texture;
import sun.misc.Unsafe;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.HashMap;

final class HeadlessTextRendererFactory {
  private static final Unsafe UNSAFE = lookupUnsafe();

  private HeadlessTextRendererFactory() {
  }

  static NonCachingTextRenderer createRenderer(Font font) {
    return createRenderer(NonCachingTextRenderer.class, font);
  }

  static <T extends NonCachingTextRenderer> T createRenderer(Class<T> type, Font font) {
    try {
      T renderer = type.cast(UNSAFE.allocateInstance(type));
      set(renderer, NonCachingTextRenderer.class, "font", font);
      set(renderer, NonCachingTextRenderer.class, "antialiased", false);
      set(renderer, NonCachingTextRenderer.class, "useFractionalMetrics", false);
      set(renderer, NonCachingTextRenderer.class, "normalizeBoundary", (int) Math.max(1, 0.015 * font.getSize()));
      set(renderer, NonCachingTextRenderer.class, "mipmap", false);
      set(renderer, NonCachingTextRenderer.class, "haveMaxSize", false);
      set(renderer, NonCachingTextRenderer.class, "renderDelegate", new DefaultRenderDelegate());
      set(renderer, NonCachingTextRenderer.class, "cachedBackingStore", null);
      set(renderer, NonCachingTextRenderer.class, "cachedGraphics", null);
      set(renderer, NonCachingTextRenderer.class, "cachedFontRenderContext", null);
      set(renderer, NonCachingTextRenderer.class, "stringLocations", new HashMap<String, com.jogamp.opengl.util.packrect.Rect>());
      set(renderer, NonCachingTextRenderer.class, "numRenderCycles", 0);
      set(renderer, NonCachingTextRenderer.class, "inBeginEndPair", false);
      set(renderer, NonCachingTextRenderer.class, "isOrthoMode", false);
      set(renderer, NonCachingTextRenderer.class, "beginRenderingWidth", 0);
      set(renderer, NonCachingTextRenderer.class, "beginRenderingHeight", 0);
      set(renderer, NonCachingTextRenderer.class, "beginRenderingDepthTestDisabled", false);
      set(renderer, NonCachingTextRenderer.class, "dbgFrame", null);
      set(renderer, NonCachingTextRenderer.class, "debugged", false);
      set(renderer, NonCachingTextRenderer.class, "mPipelinedQuadRenderer", null);
      set(renderer, NonCachingTextRenderer.class, "isExtensionAvailable_GL_VERSION_1_5", false);
      set(renderer, NonCachingTextRenderer.class, "checkFor_isExtensionAvailable_GL_VERSION_1_5", false);
      set(renderer, NonCachingTextRenderer.class, "singleUnicode", new char[1]);

      RectanglePacker packer = new RectanglePacker(new HeadlessManager(renderer), NonCachingTextRenderer.kSize, NonCachingTextRenderer.kSize);
      set(renderer, NonCachingTextRenderer.class, "packer", packer);

      TextRendererGlyphProducer producer = new TextRendererGlyphProducer(font.getNumGlyphs(), renderer);
      set(renderer, NonCachingTextRenderer.class, "mGlyphProducer", producer);

      TextRendererPipeline pipeline = new TextRendererPipeline(renderer);
      set(renderer, NonCachingTextRenderer.class, "pipeline", pipeline);

      TextRendererProperties properties = new TextRendererProperties(renderer);
      set(renderer, NonCachingTextRenderer.class, "properties", properties);
      return renderer;
    } catch (InstantiationException e) {
      throw new AssertionError(e);
    }
  }

  private static void set(Object target, Class<?> type, String name, Object value) {
    try {
      Field field = type.getDeclaredField(name);
      field.setAccessible(true);
      field.set(target, value);
    } catch (ReflectiveOperationException e) {
      throw new AssertionError(e);
    }
  }

  private static Unsafe lookupUnsafe() {
    try {
      Field field = Unsafe.class.getDeclaredField("theUnsafe");
      field.setAccessible(true);
      return (Unsafe) field.get(null);
    } catch (ReflectiveOperationException e) {
      throw new AssertionError(e);
    }
  }

  static final class HeadlessTextureRenderer extends TextureRenderer {
    private BufferedImage image;
    private boolean smoothing = true;
    private Color color = Color.WHITE;
    private float[] rgba = {1.0f, 1.0f, 1.0f, 1.0f};
    private Texture texture;
    private int markDirtyCount;

    private HeadlessTextureRenderer() {
      super(1, 1, true, false);
    }

    static HeadlessTextureRenderer create(int width, int height) {
      try {
        HeadlessTextureRenderer renderer = (HeadlessTextureRenderer) UNSAFE.allocateInstance(HeadlessTextureRenderer.class);
        renderer.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        renderer.smoothing = true;
        renderer.color = Color.WHITE;
        renderer.rgba = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
        renderer.texture = new Texture(1, false, GL.GL_TEXTURE_2D, width, height, width, height, false);
        renderer.markDirtyCount = 0;
        return renderer;
      } catch (InstantiationException e) {
        throw new AssertionError(e);
      }
    }

    int getMarkDirtyCount() {
      return this.markDirtyCount;
    }

    Color getColorValue() {
      return this.color;
    }

    float[] getRgbaValue() {
      return this.rgba;
    }

    @Override
    public int getWidth() {
      return this.image.getWidth();
    }

    @Override
    public int getHeight() {
      return this.image.getHeight();
    }

    @Override
    public void setSmoothing(boolean smoothing) {
      this.smoothing = smoothing;
    }

    @Override
    public boolean getSmoothing() {
      return this.smoothing;
    }

    @Override
    public Graphics2D createGraphics() {
      return this.image.createGraphics();
    }

    @Override
    public Image getImage() {
      return this.image;
    }

    @Override
    public void markDirty(int x, int y, int width, int height) {
      this.markDirtyCount++;
    }

    @Override
    public Texture getTexture() {
      return this.texture;
    }

    @Override
    public void dispose() throws GLException {
    }

    @Override
    public void beginOrthoRendering(int width, int height) throws GLException {
    }

    @Override
    public void beginOrthoRendering(int width, int height, boolean disableDepthTest) throws GLException {
    }

    @Override
    public void begin3DRendering() throws GLException {
    }

    @Override
    public void setColor(float r, float g, float b, float a) throws GLException {
      this.rgba = new float[]{r, g, b, a};
      this.color = null;
    }

    @Override
    public void setColor(Color color) throws GLException {
      this.color = color;
    }

    @Override
    public void drawOrthoRect(int screenx, int screeny) throws GLException {
    }

    @Override
    public void drawOrthoRect(int screenx, int screeny, int texx, int texy, int width, int height) throws GLException {
    }

    @Override
    public void draw3DRect(float x, float y, float z, int texx, int texy, int width, int height, float scaleFactor) throws GLException {
    }

    @Override
    public void endOrthoRendering() throws GLException {
    }

    @Override
    public void end3DRendering() throws GLException {
    }

    @Override
    public boolean isUsingAutoMipmapGeneration() {
      return false;
    }
  }

  private static final class HeadlessManager extends Manager {
    private HeadlessManager(NonCachingTextRenderer textRenderer) {
      super(textRenderer);
    }

    @Override
    public Object allocateBackingStore(int w, int h) {
      return HeadlessTextureRenderer.create(w, h);
    }

    @Override
    public void deleteBackingStore(Object backingStore) {
      ((TextureRenderer) backingStore).dispose();
    }
  }
}
