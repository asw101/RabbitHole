package org.alice.ide.instancefactory;

import org.junit.Test;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.ThisExpression;

import static org.junit.Assert.*;

public class ThisFieldAccessMethodInvocationFactoryBehaviorTest {
  @Test
  public void createsMethodInvocationOnThisFieldAccess() {
    var field = InstanceFactoryTestSupport.createStringField("target");
    JavaMethod method = JavaMethod.getInstance(Object.class, "getClass");
    ThisFieldAccessMethodInvocationFactory factory = ThisFieldAccessMethodInvocationFactory.getInstance(field, method);

    assertSame(factory, ThisFieldAccessMethodInvocationFactory.getInstance(field, method));
    MethodInvocation invocation = factory.createExpression();
    assertTrue(invocation.expression.getValue() instanceof FieldAccess);
    FieldAccess access = (FieldAccess) invocation.expression.getValue();
    assertTrue(access.expression.getValue() instanceof ThisExpression);
    assertSame(field, access.field.getValue());
    assertEquals("this.target's Class", factory.getRepr());
    assertSame(method.getReturnType(), factory.getValueType());
  }
}
