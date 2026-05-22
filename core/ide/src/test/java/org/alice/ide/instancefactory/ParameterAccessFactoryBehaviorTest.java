package org.alice.ide.instancefactory;

import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.ParameterAccess;

import static org.junit.Assert.*;

public class ParameterAccessFactoryBehaviorTest {
  @Test
  public void getInstanceCachesAndCreatesParameterAccessExpressions() {
    var parameter = InstanceFactoryTestSupport.createParameter("value", JavaType.STRING_TYPE);
    ParameterAccessFactory factory = ParameterAccessFactory.getInstance(parameter);

    assertSame(factory, ParameterAccessFactory.getInstance(parameter));
    assertTrue(factory.createExpression() instanceof ParameterAccess);
    assertTrue(factory.createTransientExpression() instanceof ParameterAccess);
    assertSame(parameter, ((ParameterAccess) factory.createExpression()).parameter.getValue());
    assertEquals("value", factory.getRepr());
    assertSame(JavaType.STRING_TYPE, factory.getValueType());
  }
}
