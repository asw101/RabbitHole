package org.alice.ide.instancefactory;

import org.lgna.project.ast.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class InstanceFactoryUtilitiesTest {

  @Test
  public void thisExpression_returnsThisInstanceFactory() {
    ThisExpression thisExpr = new ThisExpression();
    InstanceFactory result = InstanceFactoryUtilities.getInstanceFactoryForExpression(thisExpr);
    assertNotNull(result);
    assertTrue(result instanceof ThisInstanceFactory);
  }

  @Test
  public void nullExpression_returnsNull() {
    InstanceFactory result = InstanceFactoryUtilities.getInstanceFactoryForExpression(null);
    assertNull(result);
  }

  @Test
  public void fieldAccess_withThisAndUserField_returnsThisFieldAccessFactory() {
    UserField field = new UserField();
    field.name.setValue("myField");
    field.valueType.setValue(JavaType.getInstance(String.class));
    FieldAccess fieldAccess = new FieldAccess(new ThisExpression(), field);
    InstanceFactory result = InstanceFactoryUtilities.getInstanceFactoryForExpression(fieldAccess);
    assertNotNull(result);
    assertTrue(result instanceof ThisFieldAccessFactory);
  }

  @Test
  public void localAccess_returnsLocalAccessFactory() {
    UserLocal local = new UserLocal();
    local.name.setValue("myLocal");
    local.valueType.setValue(JavaType.getInstance(Integer.class));
    LocalAccess localAccess = new LocalAccess(local);
    InstanceFactory result = InstanceFactoryUtilities.getInstanceFactoryForExpression(localAccess);
    assertNotNull(result);
    assertTrue(result instanceof LocalAccessFactory);
  }

  @Test
  public void parameterAccess_withUserParameter_returnsParameterAccessFactory() {
    UserParameter param = new UserParameter();
    param.name.setValue("myParam");
    param.valueType.setValue(JavaType.getInstance(Double.class));
    ParameterAccess paramAccess = new ParameterAccess(param);
    InstanceFactory result = InstanceFactoryUtilities.getInstanceFactoryForExpression(paramAccess);
    assertNotNull(result);
    assertTrue(result instanceof ParameterAccessFactory);
  }

  @Test
  public void thisExpression_returnsSameSingleton() {
    ThisExpression e1 = new ThisExpression();
    ThisExpression e2 = new ThisExpression();
    InstanceFactory r1 = InstanceFactoryUtilities.getInstanceFactoryForExpression(e1);
    InstanceFactory r2 = InstanceFactoryUtilities.getInstanceFactoryForExpression(e2);
    assertSame(r1, r2);
  }

  @Test
  public void fieldAccess_sameField_returnsSameFactory() {
    UserField field = new UserField();
    field.name.setValue("shared");
    field.valueType.setValue(JavaType.getInstance(String.class));
    FieldAccess fa1 = new FieldAccess(new ThisExpression(), field);
    FieldAccess fa2 = new FieldAccess(new ThisExpression(), field);
    InstanceFactory r1 = InstanceFactoryUtilities.getInstanceFactoryForExpression(fa1);
    InstanceFactory r2 = InstanceFactoryUtilities.getInstanceFactoryForExpression(fa2);
    assertSame(r1, r2);
  }
}
