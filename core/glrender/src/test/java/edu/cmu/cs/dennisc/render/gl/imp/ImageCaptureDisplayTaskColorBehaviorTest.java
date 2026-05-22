package edu.cmu.cs.dennisc.render.gl.imp;

import edu.cmu.cs.dennisc.color.Color4f;
import org.junit.Assume;
import java.awt.GraphicsEnvironment;
import edu.cmu.cs.dennisc.render.ImageOrientationRequirement;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import org.junit.Test;

import java.awt.Dimension;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class ImageCaptureDisplayTaskColorBehaviorTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Test public void handleDisplay_usesFullSurfaceWhenViewportIsNull() {
    HeadlessRecordingGL2 gl = new HeadlessRecordingGL2(); AtomicInteger observerCalls = new AtomicInteger(); GlrImageBuffer imageBuffer = new GlrImageBuffer(Color4f.BLACK); ImageCaptureDisplayTask task = new ImageCaptureDisplayTask(null, null, imageBuffer, ImageOrientationRequirement.UPSIDE_DOWN_ACCEPTABLE, GlImpTestSupport.countingObserver(observerCalls)); RenderTargetImp imp = new RenderTargetImp(GlImpTestSupport.renderTargetProxy(new Dimension(6, 5), Collections.emptyMap(), new boolean[]{true}, new AtomicInteger()));
    task.handleDisplay(imp, GlImpTestSupport.drawableProxy(gl, new AtomicInteger(), new AtomicInteger(), new AtomicInteger(), new AtomicBoolean(true), 6, 5), gl);
    assertEquals(1, gl.calls("glReadPixels").size()); assertEquals(1, observerCalls.get()); assertEquals(6, imageBuffer.getImage().getWidth()); assertEquals(5, imageBuffer.getImage().getHeight());
  }
}
