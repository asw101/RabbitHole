package org.alice.ide.instancefactory;

import org.junit.Test;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.ThisExpression;

import static org.junit.Assert.*;

public class ThisMethodInvocationFactoryBehaviorTest {
  @Test
  public void noArgGetterMethodCreatesThisBasedInvocation() {
    JavaMethod method = JavaMethod.getInstance(Object.class, "getClass");
    ThisMethodInvocationFactory factory = ThisMethodInvocationFactory.getInstance(method);

    assertNotNull(factory);
    assertSame(factory, ThisMethodInvocationFactory.getInstance(method));
    MethodInvocation invocation = factory.createExpression();
    assertTrue(invocation.expression.getValue() instanceof ThisExpression);
    assertSame(method, invocation.method.getValue());
    assertEquals("this's Class", factory.getRepr());
    assertSame(method.getReturnType(), factory.getValueType());
  }

  @Test
  public void methodWithParametersReturnsNullFactory() {
    assertNull(ThisMethodInvocationFactory.getInstance(JavaMethod.getInstance(String.class, "substring", int.class)));
  }
}
