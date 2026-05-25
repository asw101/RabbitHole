package edu.cmu.cs.dennisc.render.gl.imp;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;



import com.jogamp.opengl.GL;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import org.junit.Test;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.junit.Assert.*;

public class MoreCoverageBehaviorTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }



  @Test
  public void renderContextCaptureBuffersHandlesDepthAndErrorPaths() throws Exception {
    BufferFillingGL gl = new BufferFillingGL();
    RenderContext renderContext = new RenderContext();
    renderContext.setGL(gl);

    BufferedImage withDepth = new BufferedImage(2, 2, BufferedImage.TYPE_4BYTE_ABGR);
    FloatBuffer depth = FloatBuffer.allocate(4);
    boolean[] upsideDown = new boolean[1];
    gl.setDepthMode();
    renderContext.captureBuffers(withDepth, depth, upsideDown);

    byte[] data = ((java.awt.image.DataBufferByte) withDepth.getRaster().getDataBuffer()).getData();
    assertEquals(0, data[0]);
    assertEquals((byte) 255, data[4]);
    assertTrue(upsideDown[0]);
    assertEquals(0, depth.position());

    BufferedImage withoutDepth = new BufferedImage(2, 2, BufferedImage.TYPE_4BYTE_ABGR);
    gl.setColorMode();
    renderContext.captureBuffers(withoutDepth, null, null);
    assertTrue(gl.calls("glReadPixels").size() >= 3);
  }

  @Test
  public void renderContextLetterboxingUsesScissorOnAllBorders() throws Exception {
    HeadlessRecordingGL2 gl = new HeadlessRecordingGL2();
    RenderContext renderContext = new RenderContext();
    renderContext.setGL(gl);
    Field clearRect = RenderContext.class.getDeclaredField("clearRect");
    clearRect.setAccessible(true);
    ((Rectangle) clearRect.get(renderContext)).setBounds(10, 20, 30, 40);

    renderContext.renderLetterboxingIfNecessary(100, 80);

    assertTrue(gl.wasCalledWith("glEnable", GL.GL_SCISSOR_TEST));
    assertEquals(4, gl.calls("glScissor").size());
    assertEquals(4, gl.calls("glClear").size());
    assertTrue(gl.wasCalledWith("glDisable", GL.GL_SCISSOR_TEST));
  }

  @Test
  public void renderContextWrapperMethodsDelegateToResourceCacheAndListeners() {
    HeadlessRecordingGL2 gl = new HeadlessRecordingGL2();
    RenderContext renderContext = new RenderContext();
    renderContext.setGL(gl);
    NoOpTextureAdapter texture = new NoOpTextureAdapter();
    java.util.concurrent.atomic.AtomicInteger cleared = new java.util.concurrent.atomic.AtomicInteger();
    RenderContext.UnusedTexturesListener listener = unused -> cleared.incrementAndGet();
    RenderContext.addUnusedTexturesListener(listener);
    renderContext.clearUnusedTextures();
    RenderContext.removeUnusedTexturesListener(listener);
    renderContext.forgetTextureAdapter(texture, true);
    renderContext.forgetTextureAdapter(texture);
    renderContext.setViewportAndAddToClearRect(new Rectangle(1, 2, 3, 4));
    assertEquals(1, cleared.get());
    assertTrue(gl.wasCalledWith("glViewport", 1, 2, 3, 4));
  }

  @Test
  public void primitiveAndTessellationRenderersHandleAdditionalShapes() {
    HeadlessRecordingGL2 gl = new HeadlessRecordingGL2();
    Graphics2D graphics = createGraphics(gl);
    graphics.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, new float[]{2.0f, 2.0f}, 0.0f));

    graphics.drawRoundRect(1, 2, 20, 10, 4, 3);
    graphics.fillRoundRect(1, 2, 20, 10, 4, 3);
    graphics.drawOval(2, 3, 8, 6);
    graphics.fillOval(2, 3, 8, 6);
    graphics.drawPolygon(new int[]{0, 5, 10}, new int[]{0, 8, 0}, 3);
    graphics.fillPolygon(new int[]{0, 5, 10}, new int[]{0, 8, 0}, 3);
    graphics.draw(new Rectangle2D.Double(0, 0, 8, 6));
    Path2D.Double shape = new Path2D.Double();
    shape.moveTo(0, 0);
    shape.lineTo(6, 0);
    shape.lineTo(3, 5);
    shape.closePath();
    graphics.fill(shape);

    assertTrue(gl.calls("glVertex2d").size() > 0);
    assertTrue(gl.calls("glVertex2i").size() > 0);
    assertTrue(gl.calls("glVertex2f").size() > 0);
    assertTrue(gl.wasCalledWith("glEnable", com.jogamp.opengl.GL2.GL_LINE_STIPPLE));
    assertTrue(gl.calls("glLineWidth").size() >= 2);
  }

  private static Graphics2D createGraphics(HeadlessRecordingGL2 gl) {
    RenderContext renderContext = new RenderContext();
    renderContext.setGL(gl);
    Graphics2D graphics = new Graphics2D(renderContext);
    graphics.initialize(new Dimension(64, 48));
    return graphics;
  }

  private static final class NoOpTextureAdapter extends edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrTexture<edu.cmu.cs.dennisc.texture.Texture> {
    @Override
    protected com.jogamp.opengl.util.texture.TextureData newTextureData(com.jogamp.opengl.GL gl, com.jogamp.opengl.util.texture.TextureData currentTexture) {
      return null;
    }
  }

  private static final class BufferFillingGL extends HeadlessRecordingGL2 {
    private boolean depthMode;
    private int errorIndex;

    private void setDepthMode() {
      this.depthMode = true;
      this.errorIndex = 0;
    }

    private void setColorMode() {
      this.depthMode = false;
      this.errorIndex = 0;
    }

    @Override
    public void glReadPixels(int x, int y, int width, int height, int format, int type, Buffer buffer) {
      super.glReadPixels(x, y, width, height, format, type, buffer);
      if (buffer instanceof ByteBuffer byteBuffer) {
        for (int i = 0; i < byteBuffer.capacity(); i++) {
          byteBuffer.put(i, (byte) 7);
        }
      } else if (buffer instanceof FloatBuffer floatBuffer) {
        float[] values = {1.0f, 0.5f, 1.0f, 0.0f};
        for (int i = 0; i < floatBuffer.capacity(); i++) {
          floatBuffer.put(i, values[i]);
        }
      }
    }

    @Override
    public int glGetError() {
      if (!this.depthMode && this.errorIndex++ == 0) {
        return 1;
      }
      return 0;
    }
  }
}
