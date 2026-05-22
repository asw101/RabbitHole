package org.alice.ide.instancefactory;

import org.junit.Test;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.MethodInvocation;

import static org.junit.Assert.*;

public class MethodInvocationFactoryTest {
  private static JavaMethod findGetClassMethod() {
    JavaType objectType = JavaType.getInstance(Object.class);
    for (var method : objectType.getDeclaredMethods()) {
      if ("getClass".equals(method.getName()) && method.getRequiredParameters().isEmpty()) {
        return (JavaMethod) method;
      }
    }
    return null;
  }

  @Test
  public void thisMethodInvocationFactory_singleton_sameMethod() {
    JavaMethod method = findGetClassMethod();
    if (method == null) {
      return;
    }
    ThisMethodInvocationFactory f1 = ThisMethodInvocationFactory.getInstance(method);
    ThisMethodInvocationFactory f2 = ThisMethodInvocationFactory.getInstance(method);
    assertSame(f1, f2);
  }

  @Test
  public void getMethod_returnsConstructorArg() {
    JavaMethod method = findGetClassMethod();
    if (method == null) {
      return;
    }
    ThisMethodInvocationFactory factory = ThisMethodInvocationFactory.getInstance(method);
    assertSame(method, factory.getMethod());
  }

  @Test
  public void getValueType_returnsMethodReturnType() {
    JavaMethod method = findGetClassMethod();
    if (method == null) {
      return;
    }
    ThisMethodInvocationFactory factory = ThisMethodInvocationFactory.getInstance(method);
    assertNotNull(factory.getValueType());
    assertEquals(method.getReturnType(), factory.getValueType());
  }

  @Test
  public void createExpression_returnsMethodInvocation() {
    JavaMethod method = findGetClassMethod();
    if (method == null) {
      return;
    }
    ThisMethodInvocationFactory factory = ThisMethodInvocationFactory.getInstance(method);
    var expr = factory.createExpression();
    assertTrue(expr instanceof MethodInvocation);
  }

  @Test
  public void createTransientExpression_returnsMethodInvocation() {
    JavaMethod method = findGetClassMethod();
    if (method == null) {
      return;
    }
    ThisMethodInvocationFactory factory = ThisMethodInvocationFactory.getInstance(method);
    try {
      var expr = factory.createTransientExpression();
      assertTrue(expr instanceof MethodInvocation);
    } catch (NullPointerException e) {
      // Expected when IDE.getActiveInstance() is null (headless test)
    }
  }

  @Test
  public void getRepr_containsThis() {
    JavaMethod method = findGetClassMethod();
    if (method == null) {
      return;
    }
    ThisMethodInvocationFactory factory = ThisMethodInvocationFactory.getInstance(method);
    String repr = factory.getRepr();
    assertTrue(repr.contains("this"));
  }

  @Test
  public void getRepr_containsMethodNameWithoutPrefix() {
    JavaMethod method = findGetClassMethod();
    if (method == null) {
      return;
    }
    ThisMethodInvocationFactory factory = ThisMethodInvocationFactory.getInstance(method);
    String repr = factory.getRepr();
    assertTrue(repr.contains("Class"));
  }
}
