package org.alice.ide.instancefactory;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

public class ParameterAccessFactoryTest {
  private UserParameter createParam(String name) {
    UserParameter param = new UserParameter();
    param.name.setValue(name);
    param.valueType.setValue(JavaType.getInstance(Double.class));
    return param;
  }

  @Test
  public void getInstance_returnsNonNull() {
    assertNotNull(ParameterAccessFactory.getInstance(createParam("p1")));
  }

  @Test
  public void getInstance_sameSingleton() {
    UserParameter p = createParam("p2");
    assertSame(ParameterAccessFactory.getInstance(p), ParameterAccessFactory.getInstance(p));
  }

  @Test
  public void getParameter_returnsParameter() {
    UserParameter p = createParam("p3");
    assertSame(p, ParameterAccessFactory.getInstance(p).getParameter());
  }

  @Test
  public void getValueType_returnsParameterType() {
    UserParameter p = createParam("p4");
    assertEquals(JavaType.getInstance(Double.class), ParameterAccessFactory.getInstance(p).getValueType());
  }

  @Test
  public void createTransientExpression_returnsParameterAccess() {
    UserParameter p = createParam("p5");
    Expression expr = ParameterAccessFactory.getInstance(p).createTransientExpression();
    assertNotNull(expr);
    assertTrue(expr instanceof ParameterAccess);
  }

  @Test
  public void createExpression_returnsParameterAccess() {
    UserParameter p = createParam("p6");
    Expression expr = ParameterAccessFactory.getInstance(p).createExpression();
    assertNotNull(expr);
    assertTrue(expr instanceof ParameterAccess);
  }
}
