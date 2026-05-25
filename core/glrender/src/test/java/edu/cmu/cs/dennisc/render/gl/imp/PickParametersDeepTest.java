package edu.cmu.cs.dennisc.render.gl.imp;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.junit.Test;

import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class PickParametersDeepTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }

  @Test
  public void constructor_exists() throws Exception {
    Constructor<?> c = PickParameters.class.getConstructor(
        edu.cmu.cs.dennisc.render.RenderTarget.class,
        edu.cmu.cs.dennisc.scenegraph.AbstractCamera.class,
        Point.class,
        boolean.class,
        edu.cmu.cs.dennisc.render.PickObserver.class
    );
    assertNotNull(c);
  }

  @Test
  public void getX_method_exists() throws Exception {
    Method m = PickParameters.class.getMethod("getX");
    assertEquals(int.class, m.getReturnType());
  }

  @Test
  public void getFlippedY_method_exists() throws Exception {
    Method m = PickParameters.class.getMethod("getFlippedY", Rectangle.class);
    assertEquals(int.class, m.getReturnType());
  }

  @Test
  public void isSubElementRequired_method_exists() throws Exception {
    Method m = PickParameters.class.getMethod("isSubElementRequired");
    assertEquals(boolean.class, m.getReturnType());
  }

  @Test
  public void accessAllPickResults_returnsListType() throws Exception {
    Method m = PickParameters.class.getMethod("accessAllPickResults");
    assertTrue(java.util.List.class.isAssignableFrom(m.getReturnType()));
  }

  @Test
  public void accessFrontMostPickResult_method_exists() throws Exception {
    Method m = PickParameters.class.getMethod("accessFrontMostPickResult");
    assertEquals(edu.cmu.cs.dennisc.render.PickResult.class, m.getReturnType());
  }

  @Test
  public void getRenderTarget_method_exists() throws Exception {
    Method m = PickParameters.class.getMethod("getRenderTarget");
    assertEquals(edu.cmu.cs.dennisc.render.RenderTarget.class, m.getReturnType());
  }

  @Test
  public void getSGCamera_method_exists() throws Exception {
    Method m = PickParameters.class.getMethod("getSGCamera");
    assertEquals(edu.cmu.cs.dennisc.scenegraph.AbstractCamera.class, m.getReturnType());
  }

  @Test
  public void getPickObserver_method_exists() throws Exception {
    Method m = PickParameters.class.getMethod("getPickObserver");
    assertEquals(edu.cmu.cs.dennisc.render.PickObserver.class, m.getReturnType());
  }

  @Test
  public void addPickResult_method_exists() throws Exception {
    Method m = PickParameters.class.getMethod("addPickResult",
        edu.cmu.cs.dennisc.scenegraph.Component.class,
        edu.cmu.cs.dennisc.scenegraph.Visual.class,
        boolean.class,
        edu.cmu.cs.dennisc.scenegraph.Geometry.class,
        int.class,
        org.alice.math.immutable.Point3.class
    );
    assertNotNull(m);
  }
}
