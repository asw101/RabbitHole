package org.alice.ide.instancefactory;

import org.junit.Test;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.ParameterAccess;

import static org.junit.Assert.*;

public class ParameterAccessMethodInvocationMethodInvocationFactoryBehaviorTest {
  @Test
  public void createsNestedMethodInvocationAndReadableRepresentation() {
    var parameter = InstanceFactoryTestSupport.createParameter("value", org.lgna.project.ast.JavaType.STRING_TYPE);
    JavaMethod innerMethod = JavaMethod.getInstance(Object.class, "getClass");
    JavaMethod outerMethod = JavaMethod.getInstance(Class.class, "getMethods");
    ParameterAccessMethodInvocationMethodInvocationFactory factory = ParameterAccessMethodInvocationMethodInvocationFactory.getInstance(parameter, innerMethod, outerMethod);

    assertSame(factory, ParameterAccessMethodInvocationMethodInvocationFactory.getInstance(parameter, innerMethod, outerMethod));
    MethodInvocation invocation = factory.createExpression();
    assertTrue(invocation.expression.getValue() instanceof MethodInvocation);
    MethodInvocation innerInvocation = (MethodInvocation) invocation.expression.getValue();
    assertTrue(innerInvocation.expression.getValue() instanceof ParameterAccess);
    assertEquals("value.getClass()'s Methods", factory.getRepr());
    assertSame(outerMethod.getReturnType(), factory.getValueType());
  }
}
