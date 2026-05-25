package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import edu.cmu.cs.dennisc.render.gl.imp.RenderContext;
import edu.cmu.cs.dennisc.scenegraph.Layer;
import edu.cmu.cs.dennisc.scenegraph.TexturedAppearance;
import edu.cmu.cs.dennisc.scenegraph.graphics.Bubble;
import edu.cmu.cs.dennisc.scenegraph.graphics.SpeechBubble;
import edu.cmu.cs.dennisc.scenegraph.graphics.ThoughtBubble;
import edu.cmu.cs.dennisc.texture.Texture;
import org.junit.Test;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class MoreCoverageBehaviorTest {
  @Test
  public void texturedAppearancePipelineAndReleaseUseTrackedTextureAdapters() throws Exception {
    TexturedAppearance sg = new TexturedAppearance();
    GlrTexturedAppearance adapter = new GlrTexturedAppearance();
    adapter.initialize(sg);

    TrackingGlrTexture diffuse = new TrackingGlrTexture();
    diffuse.initialize(new SimpleTexture(4, 4, true, false));
    diffuse.addReference();
    TrackingGlrTexture bump = new TrackingGlrTexture();
    bump.initialize(new SimpleTexture(2, 2, true, false));
    bump.addReference();

    setField(adapter, "diffuseColorTextureAdapter", diffuse);
    setField(adapter, "bumpTextureAdapter", bump);
    setField(adapter, "isDiffuseColorTextureClamped", true);

    TrackingPipelineRenderContext rc = new TrackingPipelineRenderContext();
    adapter.setPipelineState(rc, 0);
    assertSame(diffuse, rc.diffuse);
    assertTrue(rc.clamped);
    assertSame(bump, rc.bump);

    Constructor<?> ctor = GlrTexturedAppearance.class.getDeclaredConstructor();
    assertNotNull(ctor);
    java.lang.reflect.Method release = GlrTexturedAppearance.class.getDeclaredMethod("handleReleased");
    release.setAccessible(true);
    release.invoke(adapter);
    assertEquals(1, diffuse.releaseCount);
    assertEquals(2, bump.releaseCount);
  }

  @Test
  public void propertyChangedCreatesTextureAdaptersForDiffuseAndBumpTextures() throws Exception {
    TexturedAppearance sg = new TexturedAppearance();
    GlrTexturedAppearance adapter = new GlrTexturedAppearance();
    adapter.initialize(sg);

    SimpleCustomTexture diffuse = new SimpleCustomTexture();
    SimpleCustomTexture bump = new SimpleCustomTexture();
    sg.diffuseColorTexture.setValue(diffuse);
    adapter.propertyChanged(sg.diffuseColorTexture);
    assertTrue(getField(adapter, "diffuseColorTextureAdapter") instanceof GlrCustomTexture);
    sg.bumpTexture.setValue(bump);
    adapter.propertyChanged(sg.bumpTexture);
    assertTrue(getField(adapter, "bumpTextureAdapter") instanceof GlrCustomTexture);
    sg.diffuseColorTexture.setValue(null);
    adapter.propertyChanged(sg.diffuseColorTexture);
    assertNull(getField(adapter, "diffuseColorTextureAdapter"));
    sg.bumpTexture.setValue(null);
    adapter.propertyChanged(sg.bumpTexture);
    assertNull(getField(adapter, "bumpTextureAdapter"));
  }

  @Test
  public void adapterFactoryCreatesAdaptersForCommonScenegraphTypes() throws Exception {
    Layer layer = new Layer();
    SpeechBubble speechBubble = new SpeechBubble(simpleOriginator());
    ThoughtBubble thoughtBubble = new ThoughtBubble(simpleOriginator());

    assertTrue(AdapterFactory.getAdapterFor(layer) instanceof GlrLayer);
    assertTrue(AdapterFactory.getAdapterFor(speechBubble) instanceof GlrGraphic);
    assertTrue(AdapterFactory.getAdapterFor(thoughtBubble) instanceof GlrGraphic);
    assertEquals(3, AdapterFactory.getAdaptersFor(new edu.cmu.cs.dennisc.pattern.Releasable[]{layer, speechBubble, thoughtBubble}, GlrObject.class).length);
    assertNull(AdapterFactory.getAdaptersFor(null, GlrObject.class));

    java.lang.reflect.Method create = AdapterFactory.class.getDeclaredMethod("createAdapterFor", edu.cmu.cs.dennisc.pattern.Releasable.class);
    create.setAccessible(true);
    assertNotNull(create.invoke(null, layer));
  }

  private static Bubble.Originator simpleOriginator() {
    return (origin, body, textBoundsOffset, bubble, renderTarget, actualViewport, camera, textSize) -> {
      origin.setLocation(20, 30);
      body.setLocation(40, 50);
      textBoundsOffset.setLocation(5, 5);
    };
  }

  private static void setField(Object target, String name, Object value) throws Exception {
    Field field = target.getClass().getDeclaredField(name);
    field.setAccessible(true);
    field.set(target, value);
  }

  private static Object getField(Object target, String name) throws Exception {
    Field field = target.getClass().getDeclaredField(name);
    field.setAccessible(true);
    return field.get(target);
  }

  private static final class SimpleCustomTexture extends edu.cmu.cs.dennisc.texture.CustomTexture {
    @Override public void encode(edu.cmu.cs.dennisc.codec.BinaryEncoder binaryEncoder) { }
    @Override public edu.cmu.cs.dennisc.texture.MipMapGenerationPolicy getMipMapGenerationPolicy() { return edu.cmu.cs.dennisc.texture.MipMapGenerationPolicy.PAINT_ONLY_HIGHEST_LEVEL_THEN_SCALE_REMAINING; }
    @Override public void layoutIfNecessary(java.awt.Graphics2D g2) { }
    @Override public void paint(java.awt.Graphics2D g2, int width, int height) { }
    @Override public int getWidth() { return 4; }
    @Override public int getHeight() { return 4; }
    @Override public boolean isPotentiallyAlphaBlended() { return true; }
    @Override public boolean isMipMappingDesired() { return false; }
  }

  private static final class SimpleTexture extends Texture {
    private final int width;
    private final int height;
    private final boolean alpha;
    private final boolean mipMap;

    private SimpleTexture(int width, int height, boolean alpha, boolean mipMap) {
      this.width = width;
      this.height = height;
      this.alpha = alpha;
      this.mipMap = mipMap;
    }

    @Override public void encode(edu.cmu.cs.dennisc.codec.BinaryEncoder binaryEncoder) { }
    @Override public edu.cmu.cs.dennisc.texture.MipMapGenerationPolicy getMipMapGenerationPolicy() { return edu.cmu.cs.dennisc.texture.MipMapGenerationPolicy.PAINT_ONLY_HIGHEST_LEVEL_THEN_SCALE_REMAINING; }
    @Override public int getWidth() { return this.width; }
    @Override public int getHeight() { return this.height; }
    @Override public boolean isPotentiallyAlphaBlended() { return this.alpha; }
    @Override public boolean isMipMappingDesired() { return this.mipMap; }
    @Override public void paint(java.awt.Graphics2D g2, int width, int height) { }
  }

  private static final class TrackingPipelineRenderContext extends RenderContext {
    private GlrTexture<?> diffuse;
    private boolean clamped;
    private GlrTexture<?> bump;

    private TrackingPipelineRenderContext() {
      this.setGL(new edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2());
    }

    @Override
    public void setDiffuseColorTextureAdapter(GlrTexture<? extends Texture> diffuseColorTextureAdapter, boolean isDiffuseColorTextureClamped) {
      this.diffuse = diffuseColorTextureAdapter;
      this.clamped = isDiffuseColorTextureClamped;
    }

    @Override
    public void setBumpTextureAdapter(GlrTexture<? extends Texture> bumpTextureAdapter) {
      this.bump = bumpTextureAdapter;
    }
  }

  private static final class TrackingGlrTexture extends GlrTexture<Texture> {
    private int releaseCount;

    @Override
    protected void handleReleased() {
      this.releaseCount++;
    }

    @Override
    protected com.jogamp.opengl.util.texture.TextureData newTextureData(com.jogamp.opengl.GL gl, com.jogamp.opengl.util.texture.TextureData currentTexture) {
      return null;
    }
  }
}
