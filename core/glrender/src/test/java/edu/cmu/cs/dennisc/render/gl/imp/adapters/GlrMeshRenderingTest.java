package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import com.jogamp.opengl.GL;
import edu.cmu.cs.dennisc.render.gl.imp.PickContext;
import edu.cmu.cs.dennisc.render.gl.imp.RenderContext;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import org.junit.Test;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static com.jogamp.opengl.GL2ES3.GL_MAX_ELEMENTS_INDICES;
import static com.jogamp.opengl.GL2ES3.GL_MAX_ELEMENTS_VERTICES;
import static org.junit.Assert.*;

public class GlrMeshRenderingTest {
  @Test
  public void renderMeshFallsBackToImmediateArraysWhenLimitsAreSmall() {
    HeadlessRecordingGL2 gl = new HeadlessRecordingGL2().withInteger(GL_MAX_ELEMENTS_INDICES, 0).withInteger(GL_MAX_ELEMENTS_VERTICES, 0);
    RenderContext rc = new RenderContext();
    rc.setGL(gl);

    GlrMesh.renderMesh(rc, vertices(), normals(), texcoords(), indices());

    assertTrue(gl.wasCalledWith("glBegin", GL.GL_TRIANGLES));
    assertEquals(3, gl.calls("glTexCoord2f").size());
    assertEquals(3, gl.calls("glNormal3f").size());
    assertEquals(3, gl.calls("glVertex3d").size());
  }

  @Test
  public void renderMeshUsesClientBuffersWhenLimitsAreLarge() {
    HeadlessRecordingGL2 gl = new HeadlessRecordingGL2().withInteger(GL_MAX_ELEMENTS_INDICES, 999).withInteger(GL_MAX_ELEMENTS_VERTICES, 999);
    RenderContext rc = new RenderContext();
    rc.setGL(gl);

    GlrMesh.renderMesh(rc, vertices(), normals(), texcoords(), indices());

    assertTrue(gl.wasCalledWith("glEnableClientState", com.jogamp.opengl.fixedfunc.GLPointerFunc.GL_VERTEX_ARRAY));
    assertTrue(gl.wasCalledWith("glEnableClientState", com.jogamp.opengl.fixedfunc.GLPointerFunc.GL_NORMAL_ARRAY));
    assertTrue(gl.wasCalledWith("glEnableClientState", com.jogamp.opengl.fixedfunc.GLPointerFunc.GL_TEXTURE_COORD_ARRAY));
    assertEquals(1, gl.calls("glDrawElements").size());
    assertTrue(gl.wasCalledWith("glDisableClientState", com.jogamp.opengl.fixedfunc.GLPointerFunc.GL_TEXTURE_COORD_ARRAY));
  }

  @Test
  public void pickMeshFallsBackToImmediateArraysWhenLimitsAreSmall() {
    HeadlessRecordingGL2 gl = new HeadlessRecordingGL2().withInteger(GL_MAX_ELEMENTS_INDICES, 0).withInteger(GL_MAX_ELEMENTS_VERTICES, 0);
    PickContext pc = new PickContext(true);
    pc.setGL(gl);

    GlrMesh.pickMesh(pc, vertices(), indices());

    assertTrue(gl.wasCalledWith("glPushName", -1));
    assertTrue(gl.wasCalledWith("glBegin", GL.GL_TRIANGLES));
    assertEquals(3, gl.calls("glVertex3d").size());
    assertTrue(gl.wasCalledWith("glPopName"));
  }

  @Test
  public void pickMeshUsesClientBuffersWhenLimitsAreLarge() {
    HeadlessRecordingGL2 gl = new HeadlessRecordingGL2().withInteger(GL_MAX_ELEMENTS_INDICES, 999).withInteger(GL_MAX_ELEMENTS_VERTICES, 999);
    PickContext pc = new PickContext(true);
    pc.setGL(gl);

    GlrMesh.pickMesh(pc, vertices(), indices());

    assertTrue(gl.wasCalledWith("glEnableClientState", com.jogamp.opengl.fixedfunc.GLPointerFunc.GL_VERTEX_ARRAY));
    assertEquals(1, gl.calls("glDrawElements").size());
    assertTrue(gl.wasCalledWith("glDisableClientState", com.jogamp.opengl.fixedfunc.GLPointerFunc.GL_VERTEX_ARRAY));
  }

  private static DoubleBuffer vertices() {
    return DoubleBuffer.wrap(new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0});
  }

  private static FloatBuffer normals() {
    return FloatBuffer.wrap(new float[]{0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f});
  }

  private static FloatBuffer texcoords() {
    return FloatBuffer.wrap(new float[]{0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f});
  }

  private static IntBuffer indices() {
    return IntBuffer.wrap(new int[]{0, 1, 2});
  }
}
