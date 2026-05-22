package edu.cmu.cs.dennisc.render.gl;

import edu.cmu.cs.dennisc.render.gl.imp.adapters.AdapterFactory;
import edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera;
import org.junit.Before;
import org.junit.Test;

import java.awt.Dimension;

import static org.junit.Assert.*;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;
public class GlrRenderTargetDelegationBehaviorTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Before public void setUp() { AdapterFactory.forgetAllElements(); }
  @Test public void cameraCollectionAndRenderingFlag_delegateThroughImp() {
    TestRenderTargetSupport.TestRenderTarget target = new TestRenderTargetSupport.TestRenderTarget(new Dimension(640, 480)); SymmetricPerspectiveCamera camera = TestRenderTargetSupport.perspectiveCamera();
    assertEquals(640, target.getSurfaceWidth()); assertEquals(480, target.getSurfaceHeight());
    target.addSgCamera(camera); assertEquals(1, target.getSgCameraCount()); assertSame(camera, target.getSgCameraAt(0));
    target.setRenderingEnabled(false); target.setRenderingEnabled(false); assertFalse(target.isRenderingEnabled()); assertEquals(1, target.getRepaintCount());
    target.removeSgCamera(camera); assertEquals(0, target.getSgCameraCount());
  }
}
