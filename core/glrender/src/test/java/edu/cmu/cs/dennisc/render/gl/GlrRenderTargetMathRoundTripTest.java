package edu.cmu.cs.dennisc.render.gl;



import edu.cmu.cs.dennisc.render.gl.imp.adapters.AdapterFactory;
import edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera;
import org.alice.math.immutable.Vector4;
import org.junit.Before;
import org.junit.Test;

import java.awt.Dimension;

import static org.junit.Assert.*;

public class GlrRenderTargetMathRoundTripTest {



  @Before public void setUp() { AdapterFactory.forgetAllElements(); }
  @Test public void viewportToCameraAndBack_roundTripsCoordinates() {
    TestRenderTargetSupport.TestRenderTarget target = new TestRenderTargetSupport.TestRenderTarget(new Dimension(800, 600)); SymmetricPerspectiveCamera camera = TestRenderTargetSupport.perspectiveCamera(); target.addSgCamera(camera); Vector4 viewport = new Vector4(320, 240, 10, 1);
    Vector4 cameraSpace = target.transformFromViewportToCamera(viewport, camera); Vector4 roundTrip = target.transformFromCameraToViewport(cameraSpace, camera);
    assertEquals(viewport.x(), roundTrip.x(), 0.01); assertEquals(viewport.y(), roundTrip.y(), 0.01); assertEquals(viewport.z(), roundTrip.z(), 0.01);
  }
}
