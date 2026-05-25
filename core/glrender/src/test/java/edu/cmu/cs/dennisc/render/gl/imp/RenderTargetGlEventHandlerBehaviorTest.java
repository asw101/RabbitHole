package edu.cmu.cs.dennisc.render.gl.imp;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;


import edu.cmu.cs.dennisc.render.event.RenderTargetInitializeEvent;
import edu.cmu.cs.dennisc.render.event.RenderTargetListener;
import edu.cmu.cs.dennisc.render.event.RenderTargetRenderEvent;
import edu.cmu.cs.dennisc.render.event.RenderTargetResizeEvent;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import org.junit.Test;

import java.awt.Dimension;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class RenderTargetGlEventHandlerBehaviorTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }


@Test public void initDisplayAndReshape_updateImpDimensionsAndFireEvents() {
    GlImpTestSupport.resetConformanceTestResults();
    HeadlessRecordingGL2 gl = new HeadlessRecordingGL2(); RenderTargetImp imp = new RenderTargetImp(GlImpTestSupport.renderTargetProxy(new Dimension(7, 6), Collections.emptyMap(), new boolean[]{true}, new AtomicInteger())); AtomicInteger initialized = new AtomicInteger(); AtomicInteger resized = new AtomicInteger(); imp.addRenderTargetListener(new RenderTargetListener() { @Override public void initialized(RenderTargetInitializeEvent e) { initialized.incrementAndGet(); } @Override public void cleared(RenderTargetRenderEvent e) { } @Override public void rendered(RenderTargetRenderEvent e) { } @Override public void resized(RenderTargetResizeEvent e) { resized.incrementAndGet(); } @Override public void displayChanged(edu.cmu.cs.dennisc.render.event.RenderTargetDisplayChangeEvent e) { } }); var drawable = GlImpTestSupport.drawableProxy(gl, new AtomicInteger(), new AtomicInteger(), new AtomicInteger(), new AtomicBoolean(true), 7, 6); imp.drawable = drawable; RenderTargetGlEventHandler handler = new RenderTargetGlEventHandler(imp);
    handler.init(drawable); handler.reshape(drawable, 0, 0, 9, 8); handler.display(drawable);
    GlImpTestSupport.resetConformanceTestResults();
    assertEquals(1, initialized.get()); assertEquals(1, resized.get()); assertEquals(9, imp.drawableWidth); assertEquals(8, imp.drawableHeight); assertTrue(gl.calls("glFlush").size() >= 1 || gl.calls("glClear").size() >= 1);
  }
}
