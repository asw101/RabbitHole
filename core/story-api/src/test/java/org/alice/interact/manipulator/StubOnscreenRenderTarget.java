package org.alice.interact.manipulator;

import edu.cmu.cs.dennisc.render.OnscreenRenderTarget;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import edu.cmu.cs.dennisc.scenegraph.AbstractCamera;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Ray;
import org.alice.math.immutable.Vector3;

import javax.swing.JPanel;
import java.awt.Point;

/**
 * Headless stub for OnscreenRenderTarget. Provides a JPanel and predictable
 * rays at arbitrary points (vertical downward by default) so that
 * manipulators can be driven through doStartManipulator / doDataUpdateManipulator
 * without requiring real OpenGL.
 */
public final class StubOnscreenRenderTarget {

  /** Returns a proxy that produces strictly horizontal rays at any mouse point. */
  public static OnscreenRenderTarget horizontalRays() {
    return withRayProducer(point -> new Ray(
        new Point3(0, 0, -5),
        new Vector3(0, 0, 1).normalized()));
  }

  /** Returns a proxy that produces forward+down rays from a point offset by mouse position. */
  public static OnscreenRenderTarget downwardRays() {
    return withRayProducer(point -> {
      // Origin slightly behind/above scene, direction generally forward and down so
      // intersections with both Y-up and Z-backward planes succeed.
      double nx = (point.x - 400) * 0.01;
      double ny = (point.y - 300) * 0.01;
      Vector3 dir = new Vector3(nx, -0.5, 1.0).normalized();
      return new Ray(new Point3(0, 5, -10), dir);
    });
  }

  /** Returns a proxy that produces rays based on a custom Point→Ray function. */
  public static OnscreenRenderTarget withRayProducer(Function<Point, Ray> rayProducer) {
    final JPanel panel = new JPanel();
    panel.setSize(800, 600);

    final InvocationHandler handler = new InvocationHandler() {
      private final Map<String, Object> defaults = defaultReturns();

      @Override
      public Object invoke(Object proxy, Method method, Object[] args) {
        String name = method.getName();
        switch (name) {
          case "getRayAtAwtPoint":
            return rayProducer.apply((Point) args[0]);
          case "transformFromCameraToAWT":
            // Return the origin point so tests can validate behavior, never throw.
            return new Point(0, 0);
          case "getAwtComponent":
            return panel;
          case "getSurfaceWidth":
            return panel.getWidth();
          case "getSurfaceHeight":
            return panel.getHeight();
          case "getSurfaceSize":
            return panel.getSize();
          case "isRenderingEnabled":
            return Boolean.TRUE;
          case "isLetterboxed":
            return Boolean.FALSE;
          case "getSgCameras":
            return java.util.Collections.<AbstractCamera>emptyList();
          case "getSgCameraCount":
            return Integer.valueOf(0);
          case "equals":
            return proxy == args[0];
          case "hashCode":
            return System.identityHashCode(proxy);
          case "toString":
            return "StubOnscreenRenderTarget";
          default:
            return defaults.get(method.getReturnType().getName());
        }
      }
    };

    return (OnscreenRenderTarget) Proxy.newProxyInstance(
        StubOnscreenRenderTarget.class.getClassLoader(),
        new Class<?>[]{OnscreenRenderTarget.class},
        handler);
  }

  private static Map<String, Object> defaultReturns() {
    Map<String, Object> map = new HashMap<>();
    map.put("boolean", Boolean.FALSE);
    map.put("byte", Byte.valueOf((byte) 0));
    map.put("short", Short.valueOf((short) 0));
    map.put("int", Integer.valueOf(0));
    map.put("long", Long.valueOf(0L));
    map.put("float", Float.valueOf(0.0f));
    map.put("double", Double.valueOf(0.0));
    map.put("char", Character.valueOf('\0'));
    return map;
  }

  private StubOnscreenRenderTarget() {}
}
