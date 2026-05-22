package org.alice.ide.instancefactory;

import org.junit.Test;
import org.lgna.project.ast.ArrayAccess;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.JavaType;

import static org.junit.Assert.*;

public class ThisFieldAccessArrayElementMethodInvocationFactoryBehaviorTest {
  @Test
  public void createsArrayElementAccessFromMethodInvocation() {
    var field = InstanceFactoryTestSupport.createField("type", JavaType.getInstance(Class.class));
    JavaMethod method = JavaMethod.getInstance(Class.class, "getMethods");
    ThisFieldAccessArrayElementMethodInvocationFactory factory = ThisFieldAccessArrayElementMethodInvocationFactory.getInstance(field, method);

    assertSame(factory, ThisFieldAccessArrayElementMethodInvocationFactory.getInstance(field, method));
    assertTrue(factory.createExpression() instanceof ArrayAccess);
    assertEquals("this.type's Methods[ 0 ]", factory.getRepr());
    assertSame(method.getReturnType().getComponentType(), factory.getValueType());
  }
}
