package org.alice.ide.instancefactory;

import org.junit.Test;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.ParameterAccess;

import static org.junit.Assert.*;

public class ParameterAccessMethodInvocationFactoryBehaviorTest {
  @Test
  public void createsMethodInvocationOnParameterAccess() {
    var parameter = InstanceFactoryTestSupport.createParameter("target", org.lgna.project.ast.JavaType.STRING_TYPE);
    JavaMethod method = JavaMethod.getInstance(Object.class, "getClass");
    ParameterAccessMethodInvocationFactory factory = ParameterAccessMethodInvocationFactory.getInstance(parameter, method);

    assertSame(factory, ParameterAccessMethodInvocationFactory.getInstance(parameter, method));
    MethodInvocation invocation = factory.createExpression();
    assertTrue(invocation.expression.getValue() instanceof ParameterAccess);
    assertSame(parameter, ((ParameterAccess) invocation.expression.getValue()).parameter.getValue());
    assertEquals("target's Class", factory.getRepr());
    assertSame(method.getReturnType(), factory.getValueType());
  }
}
