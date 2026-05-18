package org.alice.ide.instancefactory;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

public class InstanceFactoryUtilitiesTest {
  @Test
  public void getInstanceFactoryForExpression_null_returnsNull() {
    InstanceFactory result = InstanceFactoryUtilities.getInstanceFactoryForExpression(null);
    assertNull(result);
  }

  @Test
  public void getInstanceFactoryForExpression_localAccess_returnsLocalAccessFactory() {
    UserLocal local = new UserLocal();
    local.name.setValue("loc");
    local.valueType.setValue(JavaType.getInstance(String.class));
    LocalAccess access = new LocalAccess(local);
    InstanceFactory result = InstanceFactoryUtilities.getInstanceFactoryForExpression(access);
    assertNotNull(result);
    assertTrue(result instanceof LocalAccessFactory);
  }

  @Test
  public void getInstanceFactoryForExpression_parameterAccess_returnsParameterAccessFactory() {
    UserParameter param = new UserParameter();
    param.name.setValue("par");
    param.valueType.setValue(JavaType.getInstance(Integer.class));
    ParameterAccess access = new ParameterAccess(param);
    InstanceFactory result = InstanceFactoryUtilities.getInstanceFactoryForExpression(access);
    assertNotNull(result);
    assertTrue(result instanceof ParameterAccessFactory);
  }

  @Test
  public void getInstanceFactoryForExpression_thisExpression_returnsThisInstanceFactory() {
    ThisExpression thisExpr = new ThisExpression();
    InstanceFactory result = InstanceFactoryUtilities.getInstanceFactoryForExpression(thisExpr);
    assertNotNull(result);
    assertTrue(result instanceof ThisInstanceFactory);
  }
}
