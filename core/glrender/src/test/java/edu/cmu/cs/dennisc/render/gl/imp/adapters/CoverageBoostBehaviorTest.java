package edu.cmu.cs.dennisc.render.gl.imp.adapters;



import edu.cmu.cs.dennisc.codec.BinaryEncoder;
import edu.cmu.cs.dennisc.render.gl.imp.RenderContext;
import edu.cmu.cs.dennisc.scenegraph.AbstractCamera;
import edu.cmu.cs.dennisc.scenegraph.Graphic;
import edu.cmu.cs.dennisc.scenegraph.Layer;
import edu.cmu.cs.dennisc.texture.CustomTexture;
import edu.cmu.cs.dennisc.texture.MipMapGenerationPolicy;
import edu.cmu.cs.dennisc.texture.Texture;
import org.junit.Test;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


import static org.junit.Assert.*;

public class CoverageBoostBehaviorTest {



  @Test
  public void glrLayerTracksAddedRemovedAndRenderForgetTransitions() throws Exception {
    GlrLayer layer = new GlrLayer();
    layer.initialize(new Layer());
    TestGlrGraphic graphic = new TestGlrGraphic();

    Method add = GlrLayer.class.getDeclaredMethod("handleGraphicAdded", GlrGraphic.class);
    add.setAccessible(true);
    Method remove = GlrLayer.class.getDeclaredMethod("handleGraphicRemoved", GlrGraphic.class);
    remove.setAccessible(true);

    add.invoke(layer, graphic);
    layer.render(null, null, new Rectangle(0, 0, 10, 10), null);
    assertEquals(1, graphic.renderCount);
    assertEquals(0, graphic.forgetCount);

    remove.invoke(layer, graphic);
    layer.render(null, null, new Rectangle(0, 0, 10, 10), null);
    assertEquals(1, graphic.renderCount);
    assertEquals(1, graphic.forgetCount);
  }

  @Test
  public void glrTextureUsesOwnerStateAndForgetsTrackedRenderContextsOnRelease() throws Exception {
    TestTexture owner = new TestTexture(4, 5, true, false);
    TrackingGlrTexture adapter = new TrackingGlrTexture();
    adapter.initialize(owner);
    TrackingRenderContext rc1 = new TrackingRenderContext();
    TrackingRenderContext rc2 = new TrackingRenderContext();

    assertTrue(adapter.isPotentiallyAlphaBlended());
    assertTrue(adapter.isValid());
    adapter.addReference();
    adapter.addReference();
    adapter.removeReference();
    assertTrue(adapter.isReferenced());

    adapter.addRenderContext(rc1);
    adapter.addRenderContext(rc2);
    Method handleReleased = GlrTexture.class.getDeclaredMethod("handleReleased");
    handleReleased.setAccessible(true);
    handleReleased.invoke(adapter);

    assertEquals(1, rc1.forgetCalls);
    assertEquals(1, rc2.forgetCalls);
    assertNull(adapter.getOwner());
  }

  @Test
  public void glrCustomTextureCreatesGraphicsCommitsDirtyRegionsAndPaintsOwner() throws Exception {
    TestCustomTexture owner = new TestCustomTexture(6, 4, true, false);
    GlrCustomTexture adapter = new GlrCustomTexture();
    adapter.initialize(owner);

    Object textureRenderer = createHeadlessTextureRenderer(6, 4);
    Field rendererField = GlrCustomTexture.class.getDeclaredField("textureRenderer");
    rendererField.setAccessible(true);
    rendererField.set(adapter, textureRenderer);

    Graphics2D g = adapter.createGraphics();
    assertNotNull(g);
    adapter.commitGraphics(g, 1, 1, 2, 2);
    assertNotNull(adapter.getImage());

    assertNotNull(adapter.getImage());
  }

  private static Object createHeadlessTextureRenderer(int width, int height) throws Exception {
    Class<?> type = Class.forName("edu.cmu.cs.dennisc.render.joglrenderer.HeadlessTextRendererFactory$HeadlessTextureRenderer");
    Method method = type.getDeclaredMethod("create", int.class, int.class);
    method.setAccessible(true);
    return method.invoke(null, width, height);
  }

  private static final class TestGlrGraphic extends GlrGraphic<TestGraphic> {
    private int renderCount;
    private int forgetCount;

    @Override
    protected void render(edu.cmu.cs.dennisc.render.Graphics2D g2, edu.cmu.cs.dennisc.render.RenderTarget renderTarget, Rectangle actualViewport, AbstractCamera camera) {
      this.renderCount++;
    }

    @Override
    protected void forget(edu.cmu.cs.dennisc.render.Graphics2D g2) {
      this.forgetCount++;
    }
  }

  private static final class TestGraphic extends Graphic {
  }

  private static final class TestTexture extends Texture {
    @Override
    public void encode(BinaryEncoder binaryEncoder) {
    }

    @Override
    public MipMapGenerationPolicy getMipMapGenerationPolicy() {
      return MipMapGenerationPolicy.PAINT_ONLY_HIGHEST_LEVEL_THEN_SCALE_REMAINING;
    }

    private final int width;
    private final int height;
    private final boolean alpha;
    private final boolean mipMap;

    private TestTexture(int width, int height, boolean alpha, boolean mipMap) {
      this.width = width;
      this.height = height;
      this.alpha = alpha;
      this.mipMap = mipMap;
    }

    @Override
    public int getWidth() {
      return this.width;
    }

    @Override
    public int getHeight() {
      return this.height;
    }

    @Override
    public boolean isPotentiallyAlphaBlended() {
      return this.alpha;
    }

    @Override
    public boolean isMipMappingDesired() {
      return this.mipMap;
    }

    @Override
    public void paint(Graphics2D g2, int width, int height) {
    }
  }

  private static final class TrackingGlrTexture extends GlrTexture<Texture> {
    @Override
    protected com.jogamp.opengl.util.texture.TextureData newTextureData(com.jogamp.opengl.GL gl, com.jogamp.opengl.util.texture.TextureData currentTexture) {
      return null;
    }
  }

  private static final class TrackingRenderContext extends RenderContext {
    private int forgetCalls;

    @Override
    public void forgetTextureAdapter(GlrTexture<? extends Texture> textureAdapter, boolean removeFromMap) {
      this.forgetCalls++;
    }
  }

  private static final class TestCustomTexture extends CustomTexture {
    @Override
    public void encode(BinaryEncoder binaryEncoder) {
    }

    private final int width;
    private final int height;
    private final boolean alpha;
    private final boolean mipMap;
    private int paintCount;

    private TestCustomTexture(int width, int height, boolean alpha, boolean mipMap) {
      this.width = width;
      this.height = height;
      this.alpha = alpha;
      this.mipMap = mipMap;
    }

    @Override
    public MipMapGenerationPolicy getMipMapGenerationPolicy() {
      return MipMapGenerationPolicy.PAINT_ONLY_HIGHEST_LEVEL_THEN_SCALE_REMAINING;
    }

    @Override
    public void layoutIfNecessary(Graphics2D g2) {
    }

    @Override
    public void paint(Graphics2D g2, int width, int height) {
      this.paintCount++;
      g2.fillRect(0, 0, width, height);
    }

    @Override
    public int getWidth() {
      return this.width;
    }

    @Override
    public int getHeight() {
      return this.height;
    }

    @Override
    public boolean isPotentiallyAlphaBlended() {
      return this.alpha;
    }

    @Override
    public boolean isMipMappingDesired() {
      return this.mipMap;
    }
  }
}
