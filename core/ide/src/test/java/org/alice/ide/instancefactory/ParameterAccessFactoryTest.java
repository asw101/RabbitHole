package org.alice.ide.instancefactory;

import org.lgna.project.ast.*;
import org.junit.Test;

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
    UserParameter param = createParam("p");
    assertNotNull(ParameterAccessFactory.getInstance(param));
  }

  @Test
  public void getInstance_sameParam_returnsSameInstance() {
    UserParameter param = createParam("p");
    assertSame(ParameterAccessFactory.getInstance(param), ParameterAccessFactory.getInstance(param));
  }

  @Test
  public void getInstance_differentParams_returnsDifferentInstances() {
    UserParameter a = createParam("a");
    UserParameter b = createParam("b");
    assertNotSame(ParameterAccessFactory.getInstance(a), ParameterAccessFactory.getInstance(b));
  }

  @Test
  public void getParameter_returnsSameParameter() {
    UserParameter param = createParam("myParam");
    ParameterAccessFactory factory = ParameterAccessFactory.getInstance(param);
    assertSame(param, factory.getParameter());
  }

  @Test
  public void createExpression_returnsParameterAccess() {
    UserParameter param = createParam("p");
    ParameterAccessFactory factory = ParameterAccessFactory.getInstance(param);
    Expression expr = factory.createExpression();
    assertNotNull(expr);
    assertTrue(expr instanceof ParameterAccess);
  }

  @Test
  public void createTransientExpression_returnsParameterAccess() {
    UserParameter param = createParam("p");
    ParameterAccessFactory factory = ParameterAccessFactory.getInstance(param);
    Expression expr = factory.createTransientExpression();
    assertNotNull(expr);
    assertTrue(expr instanceof ParameterAccess);
  }

  @Test
  public void getValueType_matchesParameterValueType() {
    UserParameter param = createParam("p");
    ParameterAccessFactory factory = ParameterAccessFactory.getInstance(param);
    assertSame(param.getValueType(), factory.getValueType());
  }
}
