package edu.cmu.cs.dennisc.render.gl.imp;

import edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrGeometry;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import edu.cmu.cs.dennisc.scenegraph.Geometry;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Matrix4x4;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Ray;
import org.junit.Test;

import static org.junit.Assert.*;

public class GlResourceCacheBehaviorTest {

@Test
  public void displayListLifecycle_isTrackedAndForgotten() {
    HeadlessRecordingGL2 gl = new HeadlessRecordingGL2();
    RenderContext rc = new RenderContext();
    rc.setGL(gl);
    TestGeometryAdapter geometry = new TestGeometryAdapter();
    geometry.initialize(new TestGeometry());

    Integer generated = rc.generateDisplayListID(geometry);
    assertNotNull(generated);
    assertEquals(generated, rc.getDisplayListID(geometry));

    rc.forgetGeometryAdapter(geometry);
    rc.actuallyForgetDisplayListsIfNecessary();
    rc.forgetAllCachedItems();

    assertTrue(gl.calls("glGenLists").size() > 0);
    assertTrue(gl.calls("glDeleteLists").size() > 0);
  }

  private static final class TestGeometry extends Geometry {
    @Override protected AxisAlignedBox updateBoundingBox() { return AxisAlignedBox.NaN; }
    @Override public AffineMatrix4x4 getPlane() { return AffineMatrix4x4.IDENTITY; }
    @Override public void transform(Matrix4x4 trans) { }
  }

  private static final class TestGeometryAdapter extends GlrGeometry<TestGeometry> {
    @Override public boolean isAlphaBlended() { return false; }
    @Override protected void renderGeometry(RenderContext rc, edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrVisual.RenderType renderType) { }
    @Override protected void pickGeometry(PickContext pc, boolean isSubElementRequired) { }
    @Override public Point3 getIntersectionInSource(Ray ray, Matrix4x4 m, int subElement) { return Point3.ORIGIN; }
  }
}
