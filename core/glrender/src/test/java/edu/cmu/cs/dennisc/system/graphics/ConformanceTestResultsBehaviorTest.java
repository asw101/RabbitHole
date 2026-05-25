package edu.cmu.cs.dennisc.system.graphics;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;


import com.jogamp.opengl.GL;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ConformanceTestResultsBehaviorTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }


  @Before
  @After
  public void resetSingletonState() throws Exception {
    for (String fieldName : new String[]{"sharedDetails", "synchronousPickDetails", "asynchronousPickDetails"}) {
      Field field = ConformanceTestResults.class.getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(ConformanceTestResults.SINGLETON, null);
    }
  }

  @Test
  public void updateRenderInformationIfNecessary_populatesAndCachesSharedDetails() {
    GL firstGl = createGlProxy("4.6", "Vendor A", "Renderer A", "GL_EXT_alpha GL_EXT_beta");
    ConformanceTestResults.SINGLETON.updateRenderInformationIfNecessary(firstGl);

    ConformanceTestResults.SharedDetails details = ConformanceTestResults.SINGLETON.getSharedDetails();
    assertNotNull(details);
    assertEquals("4.6", details.getVersion());
    assertEquals("Vendor A", details.getVendor());
    assertEquals("Renderer A", details.getRenderer());
    assertArrayEquals(new String[]{"GL_EXT_alpha", "GL_EXT_beta"}, details.getExtensions());

    GL secondGl = createGlProxy("9.9", "Vendor B", "Renderer B", null);
    ConformanceTestResults.SINGLETON.updateRenderInformationIfNecessary(secondGl);

    assertSame(details, ConformanceTestResults.SINGLETON.getSharedDetails());
    assertEquals("4.6", ConformanceTestResults.SINGLETON.getSharedDetails().getVersion());
  }

  @Test
  public void updateRenderInformationIfNecessary_usesEmptyExtensionsWhenDriverReturnsNull() {
    GL gl = createGlProxy("4.5", "Vendor", "Renderer", null);

    ConformanceTestResults.SINGLETON.updateRenderInformationIfNecessary(gl);

    ConformanceTestResults.SharedDetails details = ConformanceTestResults.SINGLETON.getSharedDetails();
    assertNotNull(details);
    assertEquals(0, details.getExtensions().length);
  }

  private static GL createGlProxy(String version, String vendor, String renderer, String extensions) {
    Map<Integer, String> strings = new HashMap<>();
    strings.put(GL.GL_VERSION, version);
    strings.put(GL.GL_VENDOR, vendor);
    strings.put(GL.GL_RENDERER, renderer);
    strings.put(GL.GL_EXTENSIONS, extensions);
    InvocationHandler handler = (proxy, method, args) -> handleGlInvocation(strings, proxy, method, args);
    return (GL) Proxy.newProxyInstance(GL.class.getClassLoader(), new Class<?>[]{GL.class}, handler);
  }

  private static Object handleGlInvocation(
      Map<Integer, String> strings,
      Object proxy,
      Method method,
      Object[] args) {
    String name = method.getName();
    if (name.equals("glGetString")) {
      return strings.get((Integer) args[0]);
    }
    if (name.equals("toString")) {
      return "GLProxy";
    }
    if (name.equals("hashCode")) {
      return System.identityHashCode(proxy);
    }
    if (name.equals("equals")) {
      return proxy == args[0];
    }
    Class<?> returnType = method.getReturnType();
    if (returnType == boolean.class) {
      return false;
    }
    if (returnType == int.class) {
      return 0;
    }
    if (returnType == long.class) {
      return 0L;
    }
    if (returnType == float.class) {
      return 0.0f;
    }
    if (returnType == double.class) {
      return 0.0d;
    }
    return null;
  }

}
