package org.alice.ide.instancefactory;

import org.junit.Test;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.LocalAccess;
import org.lgna.project.ast.MethodInvocation;

import static org.junit.Assert.*;

public class LocalAccessMethodInvocationFactoryBehaviorTest {
  @Test
  public void createsMethodInvocationOnLocalAccess() {
    var local = InstanceFactoryTestSupport.createLocal("target", org.lgna.project.ast.JavaType.STRING_TYPE);
    JavaMethod method = JavaMethod.getInstance(Object.class, "getClass");
    LocalAccessMethodInvocationFactory factory = LocalAccessMethodInvocationFactory.getInstance(local, method);

    assertSame(factory, LocalAccessMethodInvocationFactory.getInstance(local, method));
    MethodInvocation invocation = factory.createExpression();
    assertTrue(invocation.expression.getValue() instanceof LocalAccess);
    assertSame(local, ((LocalAccess) invocation.expression.getValue()).local.getValue());
    assertEquals("target's Class", factory.getRepr());
    assertSame(method.getReturnType(), factory.getValueType());
  }
}
