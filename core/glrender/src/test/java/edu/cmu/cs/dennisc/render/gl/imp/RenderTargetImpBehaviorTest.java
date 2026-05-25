package edu.cmu.cs.dennisc.render.gl.imp;


import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import edu.cmu.cs.dennisc.render.RenderTarget;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import edu.cmu.cs.dennisc.scenegraph.AbstractCamera;
import edu.cmu.cs.dennisc.scenegraph.OrthographicCamera;
import org.junit.Test;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.lang.reflect.Proxy;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static org.junit.Assert.*;

public class RenderTargetImpBehaviorTest {


@Test
  public void cameraLifecycleTracksListeningStateAndReturnsTopmostViewportMatch() {
    AtomicInteger addedListeners = new AtomicInteger();
    AtomicInteger removedListeners = new AtomicInteger();
    GLAutoDrawable drawable = createDrawableProxy(addedListeners, removedListeners, new AtomicInteger(), new AtomicBoolean(true));

    OrthographicCamera first = new OrthographicCamera();
    OrthographicCamera second = new OrthographicCamera();
    Map<AbstractCamera, Rectangle> viewports = new HashMap<>();
    viewports.put(first, new Rectangle(0, 0, 50, 50));
    viewports.put(second, new Rectangle(10, 10, 50, 50));

    RenderTargetImp imp = new RenderTargetImp(createRenderTargetProxy(new Dimension(80, 60), viewports, new boolean[]{true}, new AtomicInteger()));

    imp.addSgCamera(first, drawable);
    imp.addSgCamera(second, drawable);

    assertTrue(imp.isListening());
    assertEquals(1, addedListeners.get());
    assertEquals(2, imp.getSgCameraCount());
    assertEquals(second, imp.getCameraAtAwtPoint(new Point(20, 20)));

    imp.removeSgCamera(first, drawable);
    assertTrue(imp.isListening());
    imp.removeSgCamera(second, drawable);
    assertFalse(imp.isListening());
    assertEquals(1, removedListeners.get());
  }

  @Test
  public void clearSgCamerasStopsListeningAndEmptiesCollection() {
    AtomicInteger addedListeners = new AtomicInteger();
    AtomicInteger removedListeners = new AtomicInteger();
    GLAutoDrawable drawable = createDrawableProxy(addedListeners, removedListeners, new AtomicInteger(), new AtomicBoolean(true));
    OrthographicCamera camera = new OrthographicCamera();
    Map<AbstractCamera, Rectangle> viewports = new HashMap<>();
    viewports.put(camera, new Rectangle(0, 0, 10, 10));
    RenderTargetImp imp = new RenderTargetImp(createRenderTargetProxy(new Dimension(10, 10), viewports, new boolean[]{true}, new AtomicInteger()));

    imp.addSgCamera(camera, drawable);
    imp.clearSgCameras(drawable);

    assertEquals(0, imp.getSgCameraCount());
    assertFalse(imp.isListening());
    assertEquals(1, removedListeners.get());
  }

  @Test
  public void bufferFactoryMethodsUseDrawableDimensionsAndNullDrawableReadFallsBackToInput() {
    RenderTargetImp imp = new RenderTargetImp(createRenderTargetProxy(new Dimension(4, 3), new HashMap<>(), new boolean[]{true}, new AtomicInteger()));
    imp.drawableWidth = 4;
    imp.drawableHeight = 3;

    BufferedImage image = imp.createBufferedImageForUseAsColorBuffer();
    FloatBuffer depth = imp.createFloatBufferForUseAsDepthBuffer();
    BufferedImage input = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

    assertEquals(4, image.getWidth());
    assertEquals(3, image.getHeight());
    assertEquals(12, depth.capacity());
    assertSame(input, imp.getColorBufferWithTransparencyBasedOnDepthBuffer(input, null, new boolean[1]));
  }

  @Test
  public void performRenderSkipsWhenDisabledAndClearsWhenEnabledWithoutCameras() {
    boolean[] enabled = {false};
    RenderTargetImp imp = new RenderTargetImp(createRenderTargetProxy(new Dimension(20, 10), new HashMap<>(), enabled, new AtomicInteger()));
    HeadlessRecordingGL2 gl = new HeadlessRecordingGL2();
    imp.renderContext.setGL(gl);

    imp.performRender();
    assertEquals(0, gl.calls("glFlush").size());

    enabled[0] = true;
    imp.drawableWidth = 20;
    imp.drawableHeight = 10;
    imp.performRender();

    assertTrue(gl.wasCalledWith("glClearColor", 0f, 0f, 0f, 1f));
    assertTrue(gl.wasCalledWith("glClear", GL_COLOR_BUFFER_BIT));
    assertEquals(1, gl.calls("glFlush").size());
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

  private static GLAutoDrawable createDrawableProxy(AtomicInteger added, AtomicInteger removed, AtomicInteger displayed, AtomicBoolean autoSwap) {
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
}
