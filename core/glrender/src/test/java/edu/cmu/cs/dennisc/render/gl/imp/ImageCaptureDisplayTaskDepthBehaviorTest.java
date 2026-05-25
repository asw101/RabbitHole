package edu.cmu.cs.dennisc.render.gl.imp;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import edu.cmu.cs.dennisc.render.ImageOrientationRequirement;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import org.junit.Test;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class ImageCaptureDisplayTaskDepthBehaviorTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
@Test public void handleDisplay_readsColorAndDepthWhenAlphaRequired() {
    HeadlessRecordingGL2 gl = new HeadlessRecordingGL2(); AtomicInteger observerCalls = new AtomicInteger(); GlrImageBuffer imageBuffer = new GlrImageBuffer(null); ImageCaptureDisplayTask task = new ImageCaptureDisplayTask(null, new Rectangle(1, 2, 4, 3), imageBuffer, ImageOrientationRequirement.RIGHT_SIDE_UP_REQUIRED, GlImpTestSupport.countingObserver(observerCalls)); RenderTargetImp imp = new RenderTargetImp(GlImpTestSupport.renderTargetProxy(new Dimension(4, 3), Collections.emptyMap(), new boolean[]{true}, new AtomicInteger()));
    IsFrameBufferIntact result = task.handleDisplay(imp, GlImpTestSupport.drawableProxy(gl, new AtomicInteger(), new AtomicInteger(), new AtomicInteger(), new AtomicBoolean(true), 4, 3), gl);
    assertEquals(IsFrameBufferIntact.FALSE, result); assertEquals(2, gl.calls("glReadPixels").size()); assertEquals(1, observerCalls.get()); assertNotNull(imageBuffer.getImage());
  }
}
