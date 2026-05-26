package org.alice.ide.croquet.models;

import com.jogamp.opengl.GL;
import edu.cmu.cs.dennisc.system.graphics.ConformanceTestResults;
import org.alice.ide.croquet.models.ast.cascade.MethodUtilities;
import org.alice.ide.croquet.models.cascade.ExpressionBlank;
import org.alice.ide.croquet.models.cascade.ParameterBlank;
import org.alice.ide.croquet.models.cascade.TypedExpressionBlank;
import org.alice.ide.croquet.models.cascade.blanks.TypeUnsetBlank;
import org.alice.ide.croquet.models.help.views.GraphicsHelpView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class CroquetModelStaticUtilitiesBehaviorTest {
  @Before
  @After
  public void resetConformanceTestResults() throws Exception {
    Field sharedDetails = ConformanceTestResults.class.getDeclaredField("sharedDetails");
    sharedDetails.setAccessible(true);
    sharedDetails.set(ConformanceTestResults.SINGLETON, null);
  }

  @Test
  public void createParameterBlanks_preservesRequiredParameterOrderAndCaching() {
    UserMethod method = new UserMethod(
        "combine",
        Void.TYPE,
        new UserParameter[] {
            new UserParameter("count", Integer.TYPE),
            new UserParameter("label", String.class)
        },
        new BlockStatement());

    ParameterBlank[] blanks = MethodUtilities.createParameterBlanks(method);

    assertEquals(2, blanks.length);
    assertSame(ParameterBlank.getInstance(method.getRequiredParameters().get(0)), blanks[0]);
    assertSame(ParameterBlank.getInstance(method.getRequiredParameters().get(1)), blanks[1]);
  }

  @Test
  public void expressionBlankFactories_createTypedBlanksAndFallbackToUnsetSingleton() {
    ExpressionBlank stringBlank = ExpressionBlank.getBlankForType(JavaType.getInstance(String.class));
    ExpressionBlank integerBlank = ExpressionBlank.getBlankForType(Integer.TYPE, null);
    ExpressionBlank[] blanks = ExpressionBlank.createBlanks(String.class, Integer.TYPE);

    assertTrue(stringBlank instanceof TypedExpressionBlank);
    assertSame(JavaType.getInstance(String.class), stringBlank.getValueType());
    assertTrue(integerBlank instanceof TypedExpressionBlank);
    assertSame(JavaType.getInstance(Integer.TYPE), integerBlank.getValueType());
    assertArrayEquals(new Object[] { JavaType.getInstance(String.class), JavaType.getInstance(Integer.TYPE) },
        new Object[] { blanks[0].getValueType(), blanks[1].getValueType() });
    assertSame(TypeUnsetBlank.getInstance(), ExpressionBlank.getBlankForType((org.lgna.project.ast.AbstractType<?, ?, ?>)null));
  }

  @Test
  public void rendererSearchUrl_usesBaseSearchAndNormalizesGeForceRendererNames() {
    assertEquals("http://www.google.com/search?q=+graphics+driver", GraphicsHelpView.getRendererSearchUrl());

    ConformanceTestResults.SINGLETON.updateRenderInformationIfNecessary(createGlProxy("4.6", "Vendor", "NVIDIA GeForce RTX 4090", ""));
    assertEquals("http://www.google.com/search?q=+graphics+driver+GeForce", GraphicsHelpView.getRendererSearchUrl());

    resetConformanceStateSilently();
    ConformanceTestResults.SINGLETON.updateRenderInformationIfNecessary(createGlProxy("4.6", "Vendor", "Intel Iris Xe", ""));
    assertEquals("http://www.google.com/search?q=+graphics+driver+Intel+Iris+Xe", GraphicsHelpView.getRendererSearchUrl());
  }

  private void resetConformanceStateSilently() {
    try {
      resetConformanceTestResults();
    } catch (Exception e) {
      throw new AssertionError(e);
    }
  }

  private static GL createGlProxy(String version, String vendor, String renderer, String extensions) {
    Map<Integer, String> strings = new HashMap<>();
    strings.put(GL.GL_VERSION, version);
    strings.put(GL.GL_VENDOR, vendor);
    strings.put(GL.GL_RENDERER, renderer);
    strings.put(GL.GL_EXTENSIONS, extensions);
    InvocationHandler handler = (proxy, method, args) -> handleGlInvocation(strings, proxy, method, args);
    return (GL) Proxy.newProxyInstance(GL.class.getClassLoader(), new Class<?>[] { GL.class }, handler);
  }

  private static Object handleGlInvocation(Map<Integer, String> strings, Object proxy, Method method, Object[] args) {
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
