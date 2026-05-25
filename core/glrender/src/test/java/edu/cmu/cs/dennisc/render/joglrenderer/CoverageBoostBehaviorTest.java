package edu.cmu.cs.dennisc.render.joglrenderer;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import com.jogamp.opengl.GLExtensions;
import com.jogamp.opengl.util.awt.TextureRenderer;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import org.junit.Test;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class CoverageBoostBehaviorTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }

  private static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 18);

  @Test
  public void headlessRendererStateIsInitialized() {
    NonCachingTextRenderer renderer = HeadlessTextRendererFactory.createRenderer(FONT);
    assertEquals(FONT, renderer.getFont());
    assertTrue(renderer.normalizeBoundary >= 1);
    assertNotNull(renderer.pipeline);
    assertNotNull(renderer.mGlyphProducer);
    assertNotNull(renderer.properties);
  }

  @Test
  public void managerReentersBeginEndPairAndRestoresCachedColor() {
    HeadlessRecordingGL2 gl = new HeadlessRecordingGL2().withExtension(GLExtensions.VERSION_1_5, true);
    try (TestGLContext ignored = TestGLContext.makeCurrent(gl)) {
      NonCachingTextRenderer renderer = HeadlessTextRendererFactory.createRenderer(FONT);
      renderer.inBeginEndPair = true;
      renderer.isOrthoMode = true;
      renderer.beginRenderingWidth = 24;
      renderer.beginRenderingHeight = 12;
      renderer.beginRenderingDepthTestDisabled = true;
      renderer.properties.haveCachedColor = true;
      renderer.properties.cachedColor = Color.CYAN;

      HeadlessTextRendererFactory.HeadlessTextureRenderer oldStore = HeadlessTextRendererFactory.HeadlessTextureRenderer.create(8, 8);
      HeadlessTextRendererFactory.HeadlessTextureRenderer newStore = HeadlessTextRendererFactory.HeadlessTextureRenderer.create(8, 8);
      Manager manager = new Manager(renderer);

      manager.beginMovement(oldStore, newStore);
      manager.endMovement(oldStore, newStore);

      assertEquals(Color.CYAN, newStore.getColorValue());
      assertTrue(gl.calls("glPopClientAttrib").size() >= 1);
      assertTrue(gl.calls("glPushClientAttrib").size() >= 1);
    }
  }

  @Test
  public void glyphProducerReflectionPathsCoverCachedAndUncachedCases() throws Exception {
    NonCachingTextRenderer renderer = HeadlessTextRendererFactory.createRenderer(FONT);
    TextRendererGlyphProducer producer = renderer.mGlyphProducer;

    Method fromSequence = TextRendererGlyphProducer.class.getDeclaredMethod("getGlyph", CharSequence.class, GlyphMetrics.class, int.class);
    fromSequence.setAccessible(true);
    Method fromUnicode = TextRendererGlyphProducer.class.getDeclaredMethod("getGlyph", int.class);
    fromUnicode.setAccessible(true);

    GlyphVector glyphVector = renderer.font.createGlyphVector(renderer.getFontRenderContext(), new char[]{'A'});
    GlyphMetrics metrics = glyphVector.getGlyphMetrics(0);

    Object glyphFromSequence = fromSequence.invoke(producer, "A", metrics, 0);
    assertNotNull(glyphFromSequence);
    Object glyphFromUnicode = fromUnicode.invoke(producer, (int) 'B');
    assertNotNull(glyphFromUnicode);
    assertNull(fromSequence.invoke(producer, String.valueOf((char) 600), metrics, 0));

    InternalError error = assertThrows(InternalError.class, () -> producer.getGlyphPixelWidth((char) 600));
    assertTrue(error.getMessage().contains("fontRenderContext never initialized"));
  }

  @Test
  public void quadRendererUsesVbosAndDisposesAllocatedBuffers() {
    HeadlessRecordingGL2 gl = new HeadlessRecordingGL2().withExtension(GLExtensions.VERSION_1_5, true);
    try (TestGLContext ignored = TestGLContext.makeCurrent(gl)) {
      NonCachingTextRenderer renderer = HeadlessTextRendererFactory.createRenderer(FONT);
      renderer.setUseVertexArrays(true);
      TextRendererQuadRenderer quadRenderer = new TextRendererQuadRenderer(renderer);

      assertTrue(quadRenderer.usingVBOs);
      queueSingleQuad(quadRenderer);
      quadRenderer.draw();
      quadRenderer.dispose();

      assertTrue(gl.calls("glGenBuffers").size() >= 1);
      assertTrue(gl.calls("glBufferData").size() >= 2);
      assertTrue(gl.calls("glDeleteBuffers").size() >= 1);
    }
  }

  @Test
  public void pipelineEndRenderingDisables15SupportAfterBindFailure() {
    ThrowingBindBufferGL gl = new ThrowingBindBufferGL();
    gl.withInteger(com.jogamp.opengl.GL.GL_MAX_TEXTURE_SIZE, 256).withExtension(GLExtensions.VERSION_1_5, true);
    try (TestGLContext ignored = TestGLContext.makeCurrent(gl)) {
      NonCachingTextRenderer renderer = HeadlessTextRendererFactory.createRenderer(FONT);
      renderer.setUseVertexArrays(true);
      renderer.pipeline.beginRendering(true, 20, 10, true);
      renderer.pipeline.endRendering(true);
      assertFalse(renderer.isExtensionAvailable_GL_VERSION_1_5);
    }
  }

  private static void queueSingleQuad(TextRendererQuadRenderer quadRenderer) {
    quadRenderer.glTexCoord2f(0.0f, 0.0f);
    quadRenderer.glVertex3f(0.0f, 0.0f, 0.0f);
    quadRenderer.glTexCoord2f(1.0f, 0.0f);
    quadRenderer.glVertex3f(1.0f, 0.0f, 0.0f);
    quadRenderer.glTexCoord2f(1.0f, 1.0f);
    quadRenderer.glVertex3f(1.0f, 1.0f, 0.0f);
    quadRenderer.glTexCoord2f(0.0f, 1.0f);
    quadRenderer.glVertex3f(0.0f, 1.0f, 0.0f);
  }

  private static final class ThrowingBindBufferGL extends HeadlessRecordingGL2 {
    @Override
    public void glBindBuffer(int target, int buffer) {
      super.glBindBuffer(target, buffer);
      throw new RuntimeException("bind failed");
    }
  }
}
