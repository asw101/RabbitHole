package edu.cmu.cs.dennisc.render.joglrenderer;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import org.junit.Test;

import java.awt.Font;

import static org.junit.Assert.*;

public class TextRendererQuadRendererBehaviorTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }

  private static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 16);

  @Test
  public void immediateModeDrawConsumesQueuedVertices() {
    HeadlessRecordingGL2 gl = new HeadlessRecordingGL2();
    try (TestGLContext ignored = TestGLContext.makeCurrent(gl)) {
      NonCachingTextRenderer renderer = HeadlessTextRendererFactory.createRenderer(FONT);
      renderer.setUseVertexArrays(false);
      TextRendererQuadRenderer quadRenderer = new TextRendererQuadRenderer(renderer);

      queueSingleQuad(quadRenderer);
      quadRenderer.draw();

      assertEquals(0, quadRenderer.mOutstandingGlyphsVerticesPipeline);
      assertTrue(gl.calls("glBegin").size() >= 1);
      assertTrue(gl.calls("glEnd").size() >= 1);
      assertTrue(gl.calls("glVertex3f").size() >= 4);
      assertTrue(gl.calls("glTexCoord2f").size() >= 4);
    }
  }

  @Test
  public void vertexArrayDrawUsesArrayPointersWhenVbosAreUnavailable() {
    HeadlessRecordingGL2 gl = new HeadlessRecordingGL2();
    try (TestGLContext ignored = TestGLContext.makeCurrent(gl)) {
      NonCachingTextRenderer renderer = HeadlessTextRendererFactory.createRenderer(FONT);
      renderer.setUseVertexArrays(true);
      TextRendererQuadRenderer quadRenderer = new TextRendererQuadRenderer(renderer);

      queueSingleQuad(quadRenderer);
      quadRenderer.draw();

      assertFalse(quadRenderer.usingVBOs);
      assertEquals(0, quadRenderer.mOutstandingGlyphsVerticesPipeline);
      assertTrue(gl.calls("glEnableClientState").size() >= 2);
      assertTrue(gl.calls("glVertexPointer").size() >= 1);
      assertTrue(gl.calls("glTexCoordPointer").size() >= 1);
      assertTrue(gl.calls("glDrawArrays").size() >= 1);
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
}
