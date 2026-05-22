package edu.cmu.cs.dennisc.render.gl.imp;

import com.jogamp.opengl.GL;
import org.junit.Assume;
import java.awt.GraphicsEnvironment;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.TextureData;
import edu.cmu.cs.dennisc.render.gl.ForgettableBinding;
import edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrTexture;
import edu.cmu.cs.dennisc.render.gl.imp.testing.RecordingGL2;
import edu.cmu.cs.dennisc.scenegraph.Vertex;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Vector3f;
import org.junit.Assert;
import org.junit.Test;

import java.awt.Rectangle;
import java.util.List;

public class RenderContextBehaviorTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  private static final double EPSILON = 0.000001;

  @Test
  public void scalingAndOpacityStateAffectRecordedOpenGlValues() {
    RecordingGL2 gl = new RecordingGL2();
    RenderContext context = new RenderContext();
    context.setGL(gl);

    context.initialize();
    Assert.assertFalse(context.isScaled());

    context.incrementScaledCount();
    Assert.assertTrue(context.isScaled());
    context.pushScaledCountAndSetToZero();
    Assert.assertFalse(context.isScaled());
    context.popAndRestoreScaledCount();
    Assert.assertTrue(context.isScaled());
    context.decrementScaledCount();
    Assert.assertFalse(context.isScaled());

    context.pushGlobalOpacity();
    context.multiplyGlobalOpacity(0.25f);
    context.setGlobalBrightness(0.5f);
    context.setColor(new float[] {0.6f, 0.2f, 1.0f, 0.8f}, 0.5f);
    context.setMaterial(GL.GL_FRONT, GL2.GL_SPECULAR, new float[] {0.4f, 0.8f, 0.2f, 0.6f}, 0.5f);
    context.popGlobalOpacity();
    context.setClearColor(new float[] {0.2f, 0.4f, 0.6f, 0.8f});

    Assert.assertArrayEquals(new float[] {0.3f, 0.1f, 0.5f, 0.1f}, gl.singleFloatArrayCall("glColor4fv"), 0.00001f);
    Assert.assertArrayEquals(new float[] {0.2f, 0.4f, 0.1f, 0.075f}, gl.singleFloatArrayCall("glMaterialfv"), 0.00001f);
    Assert.assertArrayEquals(new float[] {0.1f, 0.2f, 0.3f, 0.4f}, gl.singleFloatArgs("glClearColor"), 0.00001f);
    Assert.assertTrue(gl.wasCalledWith("glEnable", GL2.GL_NORMALIZE));
    Assert.assertTrue(gl.wasCalledWith("glDisable", GL2.GL_NORMALIZE));
  }

  @Test
  public void affectorSetupTextureSelectionAndVertexRenderingUsePureMathOnly() {
    RecordingGL2 gl = new RecordingGL2();
    RenderContext context = new RenderContext();
    context.setGL(gl);
    context.initialize();
    context.setGlobalBrightness(0.5f);

    context.beginAffectorSetup();
    context.addAmbient(new float[] {0.2f, 0.4f, 0.6f, 1.0f}, 2.0f);
    context.setIsFogEnabled(true);
    int firstLightId = context.getNextLightID();
    Assert.assertEquals(GL2.GL_LIGHT0, firstLightId);
    context.endAffectorSetup();

    Assert.assertArrayEquals(new float[] {0.2f, 0.4f, 0.6f, 0.5f}, gl.singleFloatArrayCall("glLightModelfv"), 0.00001f);
    Assert.assertTrue(gl.wasCalledWith("glEnable", GL2.GL_FOG));
    Assert.assertTrue(gl.wasCalledWith("glEnable", GL.GL_DEPTH_TEST));
    Assert.assertTrue(gl.wasCalledWith("glEnable", GL2.GL_COLOR_MATERIAL));
    Assert.assertTrue(gl.wasCalledWith("glEnable", GL.GL_CULL_FACE));

    context.setLightColor(GL2.GL_LIGHT0, new float[] {0.5f, 0.25f, 1.0f, 1.0f}, 2.0f);
    List<float[]> lightCalls = gl.floatArrayCalls("glLightfv");
    Assert.assertArrayEquals(new float[] {0.5f, 0.25f, 1.0f, 1.0f}, lightCalls.get(lightCalls.size() - 2), 0.00001f);
    Assert.assertArrayEquals(new float[] {0.5f, 0.25f, 1.0f, 1.0f}, lightCalls.get(lightCalls.size() - 1), 0.00001f);

    context.setFogColor(new float[] {0.6f, 0.4f, 0.2f, 1.0f});
    Assert.assertArrayEquals(new float[] {0.3f, 0.2f, 0.1f, 0.5f}, gl.lastFloatArrayCall("glFogfv"), 0.00001f);

    StubTexture texture = new StubTexture(2.0f, 1.0f);
    context.setDiffuseColorTextureAdapter(texture, true);
    Assert.assertEquals(1, texture.bindCount);
    Assert.assertTrue(context.isTextureEnabled());
    Assert.assertEquals(2.0f, context.getURatio(), EPSILON);
    Assert.assertEquals(1.0f, context.getVRatio(), EPSILON);
    Assert.assertTrue(gl.wasCalledWith("glEnable", GL.GL_TEXTURE_2D));
    Assert.assertTrue(gl.wasCalledWith("glTexParameteri", GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP));
    Assert.assertTrue(gl.wasCalledWith("glTexParameteri", GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP));

    context.setIsShadingEnabled(true);
    Vertex vertex = new Vertex(
        new Point3(1.0, 2.0, 3.0),
        new Vector3f(0.0f, 0.0f, 1.0f),
        new edu.cmu.cs.dennisc.color.Color4f(0.2f, 0.4f, 0.6f, 0.8f),
        null,
        new edu.cmu.cs.dennisc.texture.TextureCoordinate2f(0.25f, 0.75f));
    context.renderVertex(vertex);

    Assert.assertArrayEquals(new float[] {0.5f, 0.75f}, gl.lastFloatArgs("glTexCoord2f"), 0.00001f);
    Assert.assertArrayEquals(new float[] {0.2f, 0.4f, 0.6f, 0.8f}, gl.lastFloatArgs("glColor4f"), 0.00001f);
    Assert.assertArrayEquals(new float[] {0.0f, 0.0f, 1.0f}, gl.lastFloatArgs("glNormal3f"), 0.00001f);
    Assert.assertArrayEquals(new double[] {1.0, 2.0, 3.0}, gl.lastDoubleArgs("glVertex3d"), EPSILON);
    Assert.assertTrue(gl.wasCalledWith("glEnable", GL2.GL_LIGHTING));

    context.clearDiffuseColorTextureAdapter();
    Assert.assertTrue(Float.isNaN(context.getURatio()));
    Assert.assertTrue(Float.isNaN(context.getVRatio()));

    context.setDiffuseColorTextureAdapter(null, false);
    Assert.assertFalse(context.isTextureEnabled());
    Assert.assertTrue(gl.wasCalledWith("glDisable", GL.GL_TEXTURE_2D));
    context.setIsShadingEnabled(false);
    Assert.assertTrue(gl.wasCalledWith("glDisable", GL2.GL_LIGHTING));
  }

  @Test
  public void viewportAndLetterboxingUseScissorRegionsOutsideClearRect() {
    RecordingGL2 gl = new RecordingGL2();
    RenderContext context = new RenderContext();
    context.setGL(gl);
    context.initialize();

    context.setViewportAndAddToClearRect(new Rectangle(10, 5, 20, 15));
    context.renderLetterboxingIfNecessary(40, 30);

    Assert.assertTrue(gl.wasCalledWith("glViewport", 10, 5, 20, 15));
    Assert.assertTrue(gl.wasCalledWith("glEnable", GL.GL_SCISSOR_TEST));
    Assert.assertTrue(gl.wasCalledWith("glDisable", GL.GL_SCISSOR_TEST));
    Assert.assertEquals(4, gl.calls("glScissor").size());
    Assert.assertEquals(4, gl.calls("glClear").size());
  }

  private static final class StubTexture extends GlrTexture<edu.cmu.cs.dennisc.texture.BufferedImageTexture> {
    private final float uRatio;
    private final float vRatio;
    private int bindCount;

    private StubTexture(float uRatio, float vRatio) {
      this.uRatio = uRatio;
      this.vRatio = vRatio;
    }

    @Override
    public boolean isValid() {
      return true;
    }

    @Override
    public float mapU(float u) {
      return u * this.uRatio;
    }

    @Override
    public float mapV(float v) {
      return v * this.vRatio;
    }

    @Override
    public ForgettableBinding bindTexture(RenderContext rc) {
      this.bindCount++;
      return null;
    }

    @Override
    protected TextureData newTextureData(GL gl, TextureData currentTexture) {
      return currentTexture;
    }
  }

}
