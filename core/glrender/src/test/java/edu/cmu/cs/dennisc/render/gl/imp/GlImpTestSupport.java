package edu.cmu.cs.dennisc.render.gl.imp;

import com.jogamp.opengl.GLAutoDrawable;
import edu.cmu.cs.dennisc.render.ImageCaptureObserver;
import edu.cmu.cs.dennisc.render.RenderTarget;
import edu.cmu.cs.dennisc.render.gl.imp.testing.HeadlessRecordingGL2;
import edu.cmu.cs.dennisc.scenegraph.AbstractCamera;
import edu.cmu.cs.dennisc.system.graphics.ConformanceTestResults;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

final class GlImpTestSupport {
  private GlImpTestSupport() {
  }

  static RenderTarget renderTargetProxy(Dimension surfaceSize, Map<AbstractCamera, Rectangle> viewports, boolean[] renderingEnabled, AtomicInteger repaints) {
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

  static GLAutoDrawable drawableProxy(HeadlessRecordingGL2 gl, AtomicInteger addedListeners, AtomicInteger removedListeners, AtomicInteger displayed, AtomicBoolean autoSwap, int width, int height) {
    return (GLAutoDrawable) Proxy.newProxyInstance(
        GLAutoDrawable.class.getClassLoader(),
        new Class[]{GLAutoDrawable.class},
        (proxy, method, args) -> {
          switch (method.getName()) {
            case "addGLEventListener":
              addedListeners.incrementAndGet();
              return null;
            case "removeGLEventListener":
              removedListeners.incrementAndGet();
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
            case "getGL":
              return gl;
            default:
              return defaultValue(method.getReturnType());
          }
        });
  }

  static ImageCaptureObserver countingObserver(AtomicInteger calls) {
    return imageBuffer -> calls.incrementAndGet();
  }

  static void resetConformanceTestResults() {
    try {
      for (String fieldName : new String[]{"sharedDetails", "synchronousPickDetails", "asynchronousPickDetails"}) {
        Field field = ConformanceTestResults.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(ConformanceTestResults.SINGLETON, null);
      }
    } catch (ReflectiveOperationException e) {
      throw new AssertionError(e);
    }
  }

  static Object defaultValue(Class<?> type) {
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
