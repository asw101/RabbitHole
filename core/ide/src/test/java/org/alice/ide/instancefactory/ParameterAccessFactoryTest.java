package org.alice.ide.instancefactory;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

public class ParameterAccessFactoryTest {
  private static UserParameter param(String name, Class<?> type) {
    UserParameter p = new UserParameter();
    p.name.setValue(name);
    p.valueType.setValue(JavaType.getInstance(type));
    return p;
  }

  @Test
  public void getInstance_cachesSameParameter() {
    UserParameter p = param("p1", String.class);
    assertSame(ParameterAccessFactory.getInstance(p), ParameterAccessFactory.getInstance(p));
  }

  @Test
  public void getParameter_returnsSame() {
    UserParameter p = param("param", Integer.class);
    assertSame(p, ParameterAccessFactory.getInstance(p).getParameter());
  }

  @Test
  public void getValueType_matchesParameterType() {
    assertEquals(JavaType.getInstance(Double.class),
        ParameterAccessFactory.getInstance(param("val", Double.class)).getValueType());
  }

  @Test
  public void createExpression_returnsParameterAccess() {
    assertTrue(ParameterAccessFactory.getInstance(param("x", String.class)).createExpression() instanceof ParameterAccess);
  }

  @Test
  public void createTransientExpression_returnsParameterAccess() {
    assertTrue(ParameterAccessFactory.getInstance(param("y", String.class)).createTransientExpression() instanceof ParameterAccess);
  }

  @Test
  public void getRepr_containsParameterName() {
    assertTrue(ParameterAccessFactory.getInstance(param("myParam", String.class)).getRepr().contains("myParam"));
  }
}
