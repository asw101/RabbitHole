package org.alice.ide.instancefactory;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

public class InstanceFactoryUtilitiesTest {

  private static UserField createField(String name) {
    return new UserField(name, JavaType.getInstance(Object.class), new NullLiteral());
  }

  private static UserLocal createLocal(String name) {
    return new UserLocal(name, JavaType.getInstance(Object.class), false);
  }

  private static UserParameter createParam(String name) {
    return new UserParameter(name, JavaType.getInstance(Object.class));
  }

  private static UserMethod createNoArgMethod(String name) {
    return new UserMethod(name, JavaType.getInstance(Object.class), new UserParameter[0], new BlockStatement());
  }

  // --- ThisExpression ---

  @Test
  public void thisExpression_returnsThisInstanceFactory() {
    InstanceFactory result = InstanceFactoryUtilities.getInstanceFactoryForExpression(new ThisExpression());
    assertNotNull(result);
    assertTrue(result instanceof ThisInstanceFactory);
  }

  // --- FieldAccess ---

  @Test
  public void fieldAccess_thisAndUserField_returnsThisFieldAccessFactory() {
    UserField field = createField("scene");
    FieldAccess fa = new FieldAccess(new ThisExpression(), field);

    InstanceFactory result = InstanceFactoryUtilities.getInstanceFactoryForExpression(fa);
    assertNotNull(result);
    assertTrue(result instanceof ThisFieldAccessFactory);
    assertSame(field, ((ThisFieldAccessFactory) result).getField());
  }

  @Test
  public void fieldAccess_nonThisExpression_returnsNull() {
    UserField field = createField("other");
    FieldAccess fa = new FieldAccess(new NullLiteral(), field);

    assertNull(InstanceFactoryUtilities.getInstanceFactoryForExpression(fa));
  }

  // --- MethodInvocation on this ---

  @Test
  public void methodInvocation_this_noParams_returnsThisMethodInvocationFactory() {
    UserMethod method = createNoArgMethod("getPaint");
    MethodInvocation mi = new MethodInvocation(new ThisExpression(), method);

    InstanceFactory result = InstanceFactoryUtilities.getInstanceFactoryForExpression(mi);
    assertNotNull(result);
    assertTrue(result instanceof ThisMethodInvocationFactory);
  }

  @Test
  public void methodInvocation_this_withParams_returnsNull() {
    UserParameter param = createParam("color");
    UserMethod method = new UserMethod("setColor", JavaType.getInstance(Object.class),
        new UserParameter[]{param}, new BlockStatement());
    MethodInvocation mi = new MethodInvocation(new ThisExpression(), method);

    assertNull(InstanceFactoryUtilities.getInstanceFactoryForExpression(mi));
  }

  // --- MethodInvocation on field access ---

  @Test
  public void methodInvocation_fieldAccess_returnsThisFieldAccessMethodInvocationFactory() {
    UserField field = createField("vehicle");
    UserMethod method = createNoArgMethod("getSpeed");
    FieldAccess fa = new FieldAccess(new ThisExpression(), field);
    MethodInvocation mi = new MethodInvocation(fa, method);

    InstanceFactory result = InstanceFactoryUtilities.getInstanceFactoryForExpression(mi);
    assertNotNull(result);
    assertTrue(result instanceof ThisFieldAccessMethodInvocationFactory);
    assertSame(field, ((ThisFieldAccessMethodInvocationFactory) result).getField());
  }

  // --- MethodInvocation on local access ---

  @Test
  public void methodInvocation_localAccess_returnsLocalAccessMethodInvocationFactory() {
    UserLocal local = createLocal("tempObj");
    UserMethod method = createNoArgMethod("getData");
    MethodInvocation mi = new MethodInvocation(new LocalAccess(local), method);

    InstanceFactory result = InstanceFactoryUtilities.getInstanceFactoryForExpression(mi);
    assertNotNull(result);
    assertTrue(result instanceof LocalAccessMethodInvocationFactory);
  }

  // --- MethodInvocation on parameter access ---

  @Test
  public void methodInvocation_paramAccess_returnsParameterAccessMethodInvocationFactory() {
    UserParameter param = createParam("entity");
    UserMethod method = createNoArgMethod("getName");
    MethodInvocation mi = new MethodInvocation(new ParameterAccess(param), method);

    InstanceFactory result = InstanceFactoryUtilities.getInstanceFactoryForExpression(mi);
    assertNotNull(result);
    assertTrue(result instanceof ParameterAccessMethodInvocationFactory);
  }

  // --- Direct local/parameter access ---

  @Test
  public void localAccess_returnsLocalAccessFactory() {
    UserLocal local = createLocal("item");
    LocalAccess la = new LocalAccess(local);

    InstanceFactory result = InstanceFactoryUtilities.getInstanceFactoryForExpression(la);
    assertNotNull(result);
    assertTrue(result instanceof LocalAccessFactory);
    assertSame(local, ((LocalAccessFactory) result).getLocal());
  }

  @Test
  public void parameterAccess_userParam_returnsParameterAccessFactory() {
    UserParameter param = createParam("target");
    ParameterAccess pa = new ParameterAccess(param);

    InstanceFactory result = InstanceFactoryUtilities.getInstanceFactoryForExpression(pa);
    assertNotNull(result);
    assertTrue(result instanceof ParameterAccessFactory);
    assertSame(param, ((ParameterAccessFactory) result).getParameter());
  }

  // --- Unrecognized expressions ---

  @Test
  public void nullLiteral_returnsNull() {
    assertNull(InstanceFactoryUtilities.getInstanceFactoryForExpression(new NullLiteral()));
  }

  @Test
  public void integerLiteral_returnsNull() {
    assertNull(InstanceFactoryUtilities.getInstanceFactoryForExpression(new IntegerLiteral(42)));
  }
}
