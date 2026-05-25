package edu.cmu.cs.dennisc.render.gl;


import edu.cmu.cs.dennisc.render.gl.imp.adapters.AdapterFactory;
import edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Ray;
import org.alice.math.immutable.Vector4;
import org.junit.Before;
import org.junit.Test;

import java.awt.Dimension;
import java.awt.Point;

import static org.junit.Assert.*;

public class GlrRenderTargetProjectionMathTest {


  @Before public void setUp() { AdapterFactory.forgetAllElements(); }
  @Test public void projectionMatrixRayAndAwtConversion_useCameraAdapter() {
    TestRenderTargetSupport.TestRenderTarget target = new TestRenderTargetSupport.TestRenderTarget(new Dimension(800, 600)); SymmetricPerspectiveCamera camera = TestRenderTargetSupport.perspectiveCamera(); target.addSgCamera(camera);
    assertNotNull(target.getActualProjectionMatrix(camera)); Ray ray = target.getRayAtAwtPoint(new Point(400, 300), camera); assertFalse(ray.isNaN()); Point awt = target.transformFromCameraToAWT(new Vector4(0, 0, -5, 1), camera); assertNotNull(awt); assertNotNull(target.transformFromCameraToAWT(new Point3(0, 0, -5), camera));
  }
}
