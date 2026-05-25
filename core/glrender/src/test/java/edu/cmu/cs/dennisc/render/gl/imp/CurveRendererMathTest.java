package edu.cmu.cs.dennisc.render.gl.imp;


import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class CurveRendererMathTest {

  @Test
  public void drawSphereWithoutTextureUsesFansAndNoTextureCoordinates() {
    CurveRenderer renderer = new CurveRenderer();
    FakeContext context = new FakeContext(false);

    renderer.drawSphere(context, 2.0);

    assertEquals(51, context.recordingGl.calls("glBegin").size());
    assertEquals(0, context.recordingGl.calls("glTexCoord2d").size());
    assertTrue(context.recordingGl.wasCalledWith("glVertex3d", 0.0, 0.0, 2.0));
    assertTrue(context.recordingGl.calls("glNormal3d").size() > 0);
  }

  @Test
  public void drawSphereWithTextureUsesOnlyQuadStripsAndTextureCoordinates() {
    CurveRenderer renderer = new CurveRenderer();
    FakeContext context = new FakeContext(true);

    renderer.drawSphere(context, 1.5);

    assertEquals(51, context.recordingGl.calls("glBegin").size());
    assertTrue(context.recordingGl.calls("glTexCoord2d").size() > 0);
    assertFalse(context.recordingGl.wasCalledWith("glBegin", GL.GL_TRIANGLE_FAN));
  }

  @Test
  public void drawDiskAndCylinderSideEmitExpectedVertexPatterns() {
    CurveRenderer renderer = new CurveRenderer();
    FakeContext context = new FakeContext(true);

    renderer.drawDisk(context, 1.0, 2.0, 0.5f, 0.75f, 4.0f);
    renderer.drawCylinderSide(context, 1.0, 0.5, 5.0, 0.2f, 0.7f);

    assertTrue(context.recordingGl.calls("glVertex2f").size() > 0);
    assertTrue(context.recordingGl.calls("glVertex3f").size() > 0);
    assertTrue(context.recordingGl.calls("glTexCoord2f").size() > 0);
    assertTrue(context.recordingGl.calls("glNormal3d").size() > 0);
  }

  @Test
  public void drawTorusHonorsTextureToggle() {
    CurveRenderer renderer = new CurveRenderer();
    FakeContext textured = new FakeContext(true);
    FakeContext untextured = new FakeContext(false);

    renderer.drawTorus(textured, 3.0, 1.0);
    renderer.drawTorus(untextured, 3.0, 1.0);

    assertTrue(textured.recordingGl.calls("glTexCoord2d").size() > 0);
    assertEquals(0, untextured.recordingGl.calls("glTexCoord2d").size());
    assertTrue(textured.recordingGl.calls("glVertex3d").size() > 0);
    assertTrue(untextured.recordingGl.calls("glVertex3d").size() > 0);
  }

  @Test
  public void normal3dNormalizesNonZeroVectorsAndLeavesZeroVectorAlone() throws Exception {
    CurveRenderer renderer = new CurveRenderer();
    HeadlessRecordingGL2 gl = new HeadlessRecordingGL2();
    Method normal3d = CurveRenderer.class.getDeclaredMethod("normal3d", GL.class, double.class, double.class, double.class);
    normal3d.setAccessible(true);

    normal3d.invoke(renderer, gl, 3.0, 4.0, 0.0);
    assertArrayEquals(new double[]{0.6, 0.8, 0.0}, gl.lastDoubleArgs("glNormal3d"), 0.00001);

    normal3d.invoke(renderer, gl, 0.0, 0.0, 0.0);
    assertArrayEquals(new double[]{0.0, 0.0, 0.0}, gl.lastDoubleArgs("glNormal3d"), 0.00001);
  }

  private static final class FakeContext extends Context {
    private final boolean textureEnabled;
    private final HeadlessRecordingGL2 recordingGl;

    private FakeContext(boolean textureEnabled) {
      this.textureEnabled = textureEnabled;
      this.recordingGl = new HeadlessRecordingGL2();
      this.gl = this.recordingGl;
    }

    @Override
    public boolean isTextureEnabled() {
      return this.textureEnabled;
    }

    @Override
    public boolean isLightingEnabled() {
      return true;
    }

    @Override
    protected void enableNormalize() {
    }

    @Override
    protected void disableNormalize() {
    }
  }
}
