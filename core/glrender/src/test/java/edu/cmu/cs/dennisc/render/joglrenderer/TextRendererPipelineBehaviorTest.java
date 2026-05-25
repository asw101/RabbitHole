package edu.cmu.cs.dennisc.render.joglrenderer;



import com.jogamp.opengl.util.packrect.Rect;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import org.junit.Test;
import sun.misc.Unsafe;

import java.awt.Color;
import java.awt.Font;
import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class TextRendererPipelineBehaviorTest {



  private static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 16);
  private static final Unsafe UNSAFE = lookupUnsafe();

  @Test
  public void draw3DRobustCachesStringsAndReusesExistingRectangles() {
    NonCachingTextRenderer renderer = HeadlessTextRendererFactory.createRenderer(FONT);
    HeadlessTextRendererFactory.HeadlessTextureRenderer backingStore = (HeadlessTextRendererFactory.HeadlessTextureRenderer) renderer.getBackingStore();

    renderer.pipeline.draw3D_ROBUST("hello", 2.0f, 3.0f, 4.0f, 1.5f);

    Rect rect = renderer.stringLocations.get("hello");
    assertNotNull(rect);
    assertEquals(1, renderer.stringLocations.size());
    assertEquals(1, backingStore.getMarkDirtyCount());
    assertTrue(((TextData) rect.getUserData()).used());

    renderer.pipeline.draw3D_ROBUST("hello", 5.0f, 6.0f, 7.0f, 1.0f);

    assertEquals(1, renderer.stringLocations.size());
    assertEquals(1, backingStore.getMarkDirtyCount());
  }

  @Test
  public void internalDrawAndFlushUseGlyphProducerAndQuadRenderer() throws Exception {
    NonCachingTextRenderer renderer = HeadlessTextRendererFactory.createRenderer(FONT);
    RecordingQuadRenderer quadRenderer = allocate(RecordingQuadRenderer.class);
    renderer.mPipelinedQuadRenderer = quadRenderer;

    renderer.pipeline.internal_draw3D(new StringBuilder("world"), 1.0f, 2.0f, 3.0f, 1.0f);
    renderer.pipeline.flushGlyphPipeline();

    assertTrue(renderer.stringLocations.containsKey("world"));
    assertEquals(1, quadRenderer.drawCalls);
  }

  @Test
  public void beginAndEndRenderingTrackStateAndApplyCachedColor() {
    HeadlessRecordingGL2 gl = new HeadlessRecordingGL2().withInteger(com.jogamp.opengl.GL.GL_MAX_TEXTURE_SIZE, 256);
    try (TestGLContext ignored = TestGLContext.makeCurrent(gl)) {
      NonCachingTextRenderer renderer = HeadlessTextRendererFactory.createRenderer(FONT);
      renderer.properties.haveCachedColor = true;
      renderer.properties.cachedColor = Color.MAGENTA;
      renderer.properties.needToResetColor = true;
      renderer.mipmap = true;

      renderer.pipeline.beginRendering(true, 20, 10, true);

      assertTrue(renderer.inBeginEndPair);
      assertTrue(renderer.isOrthoMode);
      assertEquals(20, renderer.beginRenderingWidth);
      assertEquals(10, renderer.beginRenderingHeight);
      assertTrue(renderer.haveMaxSize);
      assertFalse(renderer.properties.needToResetColor);
      assertEquals(Color.MAGENTA, ((HeadlessTextRendererFactory.HeadlessTextureRenderer) renderer.getBackingStore()).getColorValue());

      renderer.pipeline.endRendering(true);

      assertFalse(renderer.inBeginEndPair);
      assertEquals(1, renderer.numRenderCycles);
      assertTrue(gl.calls("glPushClientAttrib").size() >= 1);
      assertTrue(gl.calls("glPopClientAttrib").size() >= 1);
    }
  }

  private static <T> T allocate(Class<T> type) {
    try {
      return type.cast(UNSAFE.allocateInstance(type));
    } catch (InstantiationException e) {
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

  private static final class RecordingQuadRenderer extends TextRendererQuadRenderer {
    private int drawCalls;

    private RecordingQuadRenderer() {
      super(null);
    }

    @Override
    void draw() {
      this.drawCalls++;
    }
  }
}
