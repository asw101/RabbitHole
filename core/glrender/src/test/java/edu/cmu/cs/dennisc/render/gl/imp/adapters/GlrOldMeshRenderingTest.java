package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;


import com.jogamp.opengl.GL;
import edu.cmu.cs.dennisc.render.gl.imp.PickContext;
import edu.cmu.cs.dennisc.render.gl.imp.RenderContext;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import org.junit.Test;

import static org.junit.Assert.*;

public class GlrOldMeshRenderingTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }

  @Test
  public void renderGeometryDrawsTrianglesAndQuadsInImmediateMode() {
    TestableGlrOldMesh mesh = new TestableGlrOldMesh();
    mesh.assignGeometry();
    HeadlessRecordingGL2 gl = new HeadlessRecordingGL2();
    RenderContext rc = new RenderContext();
    rc.setGL(gl);

    mesh.renderGeometry(rc, GlrVisual.RenderType.OPAQUE);

    assertTrue(gl.wasCalledWith("glBegin", GL.GL_TRIANGLES));
    assertTrue(gl.wasCalledWith("glBegin", com.jogamp.opengl.GL2GL3.GL_QUADS));
    assertEquals(7, gl.calls("glVertex3d").size());
    assertEquals(7, gl.calls("glNormal3f").size());
    assertEquals(7, gl.calls("glTexCoord2f").size());
  }

  @Test
  public void pickGeometryWithoutSubElementsWrapsImmediateRenderingWithNames() {
    TestableGlrOldMesh mesh = new TestableGlrOldMesh();
    mesh.assignGeometry();
    HeadlessRecordingGL2 gl = new HeadlessRecordingGL2();
    PickContext pc = new PickContext(true);
    pc.setGL(gl);

    mesh.pickGeometry(pc, false);

    assertTrue(gl.wasCalledWith("glPushName", -1));
    assertTrue(gl.wasCalledWith("glPopName"));
    assertEquals(7, gl.calls("glVertex3d").size());
  }

  @Test
  public void pickGeometryWithSubElementsStillThrowsTodoException() {
    TestableGlrOldMesh mesh = new TestableGlrOldMesh();
    mesh.assignGeometry();
    PickContext pc = new PickContext(true);
    pc.setGL(new HeadlessRecordingGL2());

    RuntimeException thrown = assertThrows(RuntimeException.class, () -> mesh.pickGeometry(pc, true));
    assertEquals("todo", thrown.getMessage());
  }

  private static final class TestableGlrOldMesh extends GlrOldMesh {
    private void assignGeometry() {
      setField("xyzs", new double[]{0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1});
      setField("ijks", new float[]{0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 0, 0});
      setField("uvs", new float[]{0, 0, 1, 0, 0, 1, 1, 1});
      setField("xyzTriangleIndices", new short[]{0, 1, 2});
      setField("ijkTriangleIndices", new short[]{0, 1, 2});
      setField("uvTriangleIndices", new short[]{0, 1, 2});
      setField("xyzQuadrangleIndices", new short[]{0, 1, 2, 3});
      setField("ijkQuadrangleIndices", new short[]{0, 1, 2, 3});
      setField("uvQuadrangleIndices", new short[]{0, 1, 2, 3});
    }

    private void setField(String name, Object value) {
      try {
        java.lang.reflect.Field field = GlrOldMesh.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(this, value);
      } catch (ReflectiveOperationException e) {
        throw new AssertionError(e);
      }
    }
  }
}
