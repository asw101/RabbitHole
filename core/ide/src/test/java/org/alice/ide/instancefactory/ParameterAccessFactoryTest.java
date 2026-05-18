package org.alice.ide.instancefactory;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

public class ParameterAccessFactoryTest {
  @Test
  public void getInstance_returnsSameForSameParameter() {
    UserParameter param = new UserParameter();
    param.name.setValue("p1");
    param.valueType.setValue(JavaType.getInstance(String.class));
    ParameterAccessFactory f1 = ParameterAccessFactory.getInstance(param);
    ParameterAccessFactory f2 = ParameterAccessFactory.getInstance(param);
    assertSame(f1, f2);
  }

  @Test
  public void getParameter_returnsSameParameter() {
    UserParameter param = new UserParameter();
    param.name.setValue("param");
    param.valueType.setValue(JavaType.getInstance(Integer.class));
    ParameterAccessFactory factory = ParameterAccessFactory.getInstance(param);
    assertSame(param, factory.getParameter());
  }

  @Test
  public void getValueType_matchesParameterType() {
    UserParameter param = new UserParameter();
    param.name.setValue("val");
    param.valueType.setValue(JavaType.getInstance(Double.class));
    ParameterAccessFactory factory = ParameterAccessFactory.getInstance(param);
    assertEquals(JavaType.getInstance(Double.class), factory.getValueType());
  }

  @Test
  public void createExpression_returnsParameterAccess() {
    UserParameter param = new UserParameter();
    param.name.setValue("x");
    param.valueType.setValue(JavaType.getInstance(String.class));
    ParameterAccessFactory factory = ParameterAccessFactory.getInstance(param);
    Expression expr = factory.createExpression();
    assertNotNull(expr);
    assertTrue(expr instanceof ParameterAccess);
  }

  @Test
  public void createTransientExpression_returnsParameterAccess() {
    UserParameter param = new UserParameter();
    param.name.setValue("y");
    param.valueType.setValue(JavaType.getInstance(String.class));
    ParameterAccessFactory factory = ParameterAccessFactory.getInstance(param);
    Expression expr = factory.createTransientExpression();
    assertNotNull(expr);
    assertTrue(expr instanceof ParameterAccess);
  }

  @Test
  public void getRepr_containsParameterName() {
    UserParameter param = new UserParameter();
    param.name.setValue("myParam");
    param.valueType.setValue(JavaType.getInstance(String.class));
    ParameterAccessFactory factory = ParameterAccessFactory.getInstance(param);
    assertTrue(factory.getRepr().contains("myParam"));
  }
}
