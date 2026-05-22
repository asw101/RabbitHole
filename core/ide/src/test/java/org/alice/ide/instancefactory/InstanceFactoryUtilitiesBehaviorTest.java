package org.alice.ide.instancefactory;

import org.junit.Test;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.LocalAccess;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.ParameterAccess;
import org.lgna.project.ast.ThisExpression;

import static org.junit.Assert.*;

public class InstanceFactoryUtilitiesBehaviorTest {
  @Test
  public void mapsCommonExpressionTypesToFactories() {
    var field = InstanceFactoryTestSupport.createStringField("field");
    var local = InstanceFactoryTestSupport.createLocal("local", org.lgna.project.ast.JavaType.STRING_TYPE);
    var parameter = InstanceFactoryTestSupport.createParameter("parameter", org.lgna.project.ast.JavaType.STRING_TYPE);
    JavaMethod method = JavaMethod.getInstance(Object.class, "getClass");

    assertTrue(InstanceFactoryUtilities.getInstanceFactoryForExpression(new ThisExpression()) instanceof ThisInstanceFactory);
    assertTrue(InstanceFactoryUtilities.getInstanceFactoryForExpression(new FieldAccess(field)) instanceof ThisFieldAccessFactory);
    assertTrue(InstanceFactoryUtilities.getInstanceFactoryForExpression(new LocalAccess(local)) instanceof LocalAccessFactory);
    assertTrue(InstanceFactoryUtilities.getInstanceFactoryForExpression(new ParameterAccess(parameter)) instanceof ParameterAccessFactory);
    assertTrue(InstanceFactoryUtilities.getInstanceFactoryForExpression(new MethodInvocation(new ThisExpression(), method)) instanceof ThisMethodInvocationFactory);
    assertTrue(InstanceFactoryUtilities.getInstanceFactoryForExpression(new MethodInvocation(new LocalAccess(local), method)) instanceof LocalAccessMethodInvocationFactory);
    assertTrue(InstanceFactoryUtilities.getInstanceFactoryForExpression(new MethodInvocation(new ParameterAccess(parameter), method)) instanceof ParameterAccessMethodInvocationFactory);
  }

  @Test
  public void unsupportedExpressionReturnsNull() {
    assertNull(InstanceFactoryUtilities.getInstanceFactoryForExpression(new NullLiteral()));
  }
}
