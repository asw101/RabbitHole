package edu.cmu.cs.dennisc.render.gl.imp;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;


import com.jogamp.opengl.GLAutoDrawable;
import edu.cmu.cs.dennisc.render.RenderTarget;
import edu.cmu.cs.dennisc.render.event.RenderTargetDisplayChangeEvent;
import edu.cmu.cs.dennisc.render.event.RenderTargetInitializeEvent;
import edu.cmu.cs.dennisc.render.event.RenderTargetListener;
import edu.cmu.cs.dennisc.render.event.RenderTargetRenderEvent;
import edu.cmu.cs.dennisc.render.event.RenderTargetResizeEvent;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import edu.cmu.cs.dennisc.scenegraph.AbstractCamera;
import org.junit.Test;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.lang.reflect.Proxy;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class RenderTargetImpAdditionalBehaviorTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }


@Test
  public void gettersAndListenerNotificationsWork() {
    AtomicInteger repaints = new AtomicInteger();
    RenderTarget renderTarget = createRenderTargetProxy(new Dimension(12, 9), new HashMap<>(), new boolean[]{true}, repaints);
    RenderTargetImp imp = new RenderTargetImp(renderTarget);
    CountingListener listener = new CountingListener();

    assertNotNull(imp.getSynchronousPicker());
    assertNotNull(imp.getSynchronousImageCapturer());
    assertNotNull(imp.getAsynchronousPicker());
    assertNotNull(imp.getAsynchronousImageCapturer());
    assertSame(renderTarget, imp.getRenderTarget());

    imp.addRenderTargetListener(listener);
    imp.fireInitialized(new RenderTargetInitializeEvent(renderTarget, 12, 9));
    imp.fireResized(new RenderTargetResizeEvent(renderTarget, 20, 10));
    imp.removeRenderTargetListener(listener);
    imp.fireInitialized(new RenderTargetInitializeEvent(renderTarget, 1, 1));

    assertEquals(1, listener.initialized);
    assertEquals(1, listener.resized);
  }

  @Test
  public void performRenderWithListenersFiresClearAndRenderedEvents() {
    RenderTargetImp imp = new RenderTargetImp(createRenderTargetProxy(new Dimension(20, 10), new HashMap<>(), new boolean[]{true}, new AtomicInteger()));
    HeadlessRecordingGL2 gl = new HeadlessRecordingGL2();
    CountingListener listener = new CountingListener();
    imp.addRenderTargetListener(listener);
    imp.renderContext.setGL(gl);
    imp.drawableWidth = 20;
    imp.drawableHeight = 10;

    imp.performRender();

    assertEquals(1, listener.cleared);
    assertEquals(1, listener.rendered);
    assertTrue(gl.wasCalledWith("glClearColor", 0f, 0f, 0f, 1f));
  }

  @Test
  public void createBufferedImageAndDeferredBufferCaptureUseDrawableProxyState() {
    AtomicInteger displayed = new AtomicInteger();
    AtomicBoolean autoSwap = new AtomicBoolean(true);
    GLAutoDrawable drawable = createDrawableProxy(new AtomicInteger(), new AtomicInteger(), displayed, autoSwap, 6, 5);
    RenderTargetImp imp = new RenderTargetImp(createRenderTargetProxy(new Dimension(6, 5), new HashMap<>(), new boolean[]{true}, new AtomicInteger()));
    imp.drawable = drawable;
    imp.drawableWidth = 3;
    imp.drawableHeight = 2;

    BufferedImage created = imp.createBufferedImageForUseAsColorBuffer();
    BufferedImage input = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
    FloatBuffer depth = FloatBuffer.allocate(4);
    boolean[] upsideDown = new boolean[1];
    BufferedImage returned = imp.getColorBufferWithTransparencyBasedOnDepthBuffer(input, depth, upsideDown);

    assertEquals(6, created.getWidth());
    assertEquals(5, created.getHeight());
    assertSame(input, returned);
    assertEquals(1, displayed.get());
    assertTrue(autoSwap.get());
    assertEquals(6, imp.screenWidth);
    assertEquals(5, imp.screenHeight);
  }

  @Test
  public void getCameraAtPointReturnsNullWhenNothingContainsPoint() {
    RenderTargetImp imp = new RenderTargetImp(createRenderTargetProxy(new Dimension(5, 5), new HashMap<>(), new boolean[]{true}, new AtomicInteger()));
    imp.drawableWidth = 2;
    imp.drawableHeight = 1;
    assertNull(imp.getCameraAtAwtPoint(new java.awt.Point(100, 100)));
    assertEquals(BufferedImage.TYPE_4BYTE_ABGR, imp.createBufferedImageForUseAsColorBufferWithTransparencyBasedOnDepthBuffer().getType());
  }

  private static RenderTarget createRenderTargetProxy(Dimension surfaceSize, Map<AbstractCamera, Rectangle> viewports, boolean[] renderingEnabled, AtomicInteger repaints) {
    return (RenderTarget) Proxy.newProxyInstance(
        RenderTarget.class.getClassLoader(),
        new Class[]{RenderTarget.class},
        (proxy, method, args) -> {
          switch (method.getName()) {
            case "getSurfaceSize":
              return surfaceSize;
            case "getActualViewportAsAwtRectangle":
              return viewports.get(args[0]);
            case "isRenderingEnabled":
              return renderingEnabled[0];
            case "repaint":
              repaints.incrementAndGet();
              return null;
            default:
              return defaultValue(method.getReturnType());
          }
        });
  }

  private static GLAutoDrawable createDrawableProxy(AtomicInteger added, AtomicInteger removed, AtomicInteger displayed, AtomicBoolean autoSwap, int width, int height) {
    return (GLAutoDrawable) Proxy.newProxyInstance(
        GLAutoDrawable.class.getClassLoader(),
        new Class[]{GLAutoDrawable.class},
        (proxy, method, args) -> {
          switch (method.getName()) {
            case "addGLEventListener":
              added.incrementAndGet();
              return null;
            case "removeGLEventListener":
              removed.incrementAndGet();
              return null;
            case "display":
              displayed.incrementAndGet();
              return null;
            case "setAutoSwapBufferMode":
              autoSwap.set((Boolean) args[0]);
              return null;
            case "getContext":
              return null;
            case "getSurfaceWidth":
              return width;
            case "getSurfaceHeight":
              return height;
            default:
              return defaultValue(method.getReturnType());
          }
        });
  }

  private static Object defaultValue(Class<?> type) {
    if (type == Boolean.TYPE) {
      return false;
    } else if (type == Integer.TYPE) {
      return 0;
    } else if (type == Long.TYPE) {
      return 0L;
    } else if (type == Float.TYPE) {
      return 0f;
    } else if (type == Double.TYPE) {
      return 0d;
    }
    return null;
  }

  private static final class CountingListener implements RenderTargetListener {
    private int initialized;
    private int cleared;
    private int rendered;
    private int resized;

    @Override
    public void initialized(RenderTargetInitializeEvent e) {
      this.initialized++;
    }

    @Override
    public void cleared(RenderTargetRenderEvent e) {
      this.cleared++;
    }

    @Override
    public void rendered(RenderTargetRenderEvent e) {
      this.rendered++;
    }

    @Override
    public void resized(RenderTargetResizeEvent e) {
      this.resized++;
    }

    @Override
    public void displayChanged(RenderTargetDisplayChangeEvent e) {
    }
  }
}
