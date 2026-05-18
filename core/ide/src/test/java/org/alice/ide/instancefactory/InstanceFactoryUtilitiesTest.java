package org.alice.ide.instancefactory;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

public class InstanceFactoryUtilitiesTest {
  @Test
  public void getInstanceFactoryForExpression_thisExpression() {
    ThisExpression expr = new ThisExpression();
    InstanceFactory factory = InstanceFactoryUtilities.getInstanceFactoryForExpression(expr);
    assertNotNull(factory);
    assertTrue(factory instanceof ThisInstanceFactory);
  }

  @Test
  public void getInstanceFactoryForExpression_localAccess() {
    UserLocal local = new UserLocal("x", JavaType.getInstance(String.class), false);
    LocalAccess access = new LocalAccess(local);
    InstanceFactory factory = InstanceFactoryUtilities.getInstanceFactoryForExpression(access);
    assertNotNull(factory);
    assertTrue(factory instanceof LocalAccessFactory);
  }

  @Test
  public void getInstanceFactoryForExpression_parameterAccess() {
    UserParameter param = new UserParameter();
    param.name.setValue("p");
    param.valueType.setValue(JavaType.getInstance(Integer.class));
    ParameterAccess access = new ParameterAccess(param);
    InstanceFactory factory = InstanceFactoryUtilities.getInstanceFactoryForExpression(access);
    assertNotNull(factory);
    assertTrue(factory instanceof ParameterAccessFactory);
  }

  @Test
  public void getInstanceFactoryForExpression_nullExpression() {
    InstanceFactory factory = InstanceFactoryUtilities.getInstanceFactoryForExpression(null);
    assertNull(factory);
  }
}
