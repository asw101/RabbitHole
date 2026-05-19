package org.alice.ide.instancefactory;

import org.alice.ide.ast.CurrentThisExpression;
import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

public class InstanceFactoryCreationTest {

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

  // ===== ThisInstanceFactory =====

  @Test
  public void thisFactory_getInstanceReturnsSingleton() {
    assertSame(ThisInstanceFactory.getInstance(), ThisInstanceFactory.getInstance());
  }

  @Test
  public void thisFactory_getRepr() {
    assertEquals("this", ThisInstanceFactory.getInstance().getRepr());
  }

  @Test
  public void thisFactory_createExpression_returnsThisExpression() {
    Expression expr = ThisInstanceFactory.getInstance().createExpression();
    assertTrue(expr instanceof ThisExpression);
  }

  @Test
  public void thisFactory_createTransientExpression_returnsCurrentThisExpression() {
    Expression expr = ThisInstanceFactory.getInstance().createTransientExpression();
    assertTrue(expr instanceof CurrentThisExpression);
  }

  @Test
  public void thisFactory_toString_equalsRepr() {
    ThisInstanceFactory factory = ThisInstanceFactory.getInstance();
    assertEquals(factory.getRepr(), factory.toString());
  }

  // ===== ThisFieldAccessFactory =====

  @Test
  public void fieldFactory_getInstanceCaches() {
    UserField field = createField("sameField");
    assertSame(ThisFieldAccessFactory.getInstance(field), ThisFieldAccessFactory.getInstance(field));
  }

  @Test
  public void fieldFactory_differentFieldsDifferentInstances() {
    UserField f1 = createField("alpha");
    UserField f2 = createField("beta");
    assertNotSame(ThisFieldAccessFactory.getInstance(f1), ThisFieldAccessFactory.getInstance(f2));
  }

  @Test
  public void fieldFactory_getField() {
    UserField field = createField("target");
    assertSame(field, ThisFieldAccessFactory.getInstance(field).getField());
  }

  @Test
  public void fieldFactory_getRepr() {
    UserField field = createField("myField");
    assertEquals("this.myField", ThisFieldAccessFactory.getInstance(field).getRepr());
  }

  @Test
  public void fieldFactory_createExpression_returnsFieldAccess() {
    UserField field = createField("expr");
    Expression expr = ThisFieldAccessFactory.getInstance(field).createExpression();
    assertTrue(expr instanceof FieldAccess);
  }

  @Test
  public void fieldFactory_getValueType() {
    UserField field = createField("typed");
    AbstractType<?, ?, ?> vt = ThisFieldAccessFactory.getInstance(field).getValueType();
    assertSame(field.getValueType(), vt);
  }

  // ===== LocalAccessFactory =====

  @Test
  public void localFactory_getInstanceCaches() {
    UserLocal local = createLocal("cached");
    assertSame(LocalAccessFactory.getInstance(local), LocalAccessFactory.getInstance(local));
  }

  @Test
  public void localFactory_getLocal() {
    UserLocal local = createLocal("ref");
    assertSame(local, LocalAccessFactory.getInstance(local).getLocal());
  }

  @Test
  public void localFactory_createExpression_returnsLocalAccess() {
    UserLocal local = createLocal("access");
    Expression expr = LocalAccessFactory.getInstance(local).createExpression();
    assertTrue(expr instanceof LocalAccess);
  }

  @Test
  public void localFactory_getRepr() {
    UserLocal local = createLocal("myVar");
    assertEquals("myVar", LocalAccessFactory.getInstance(local).getRepr());
  }

  // ===== ParameterAccessFactory =====

  @Test
  public void paramFactory_getInstanceCaches() {
    UserParameter param = createParam("cached");
    assertSame(ParameterAccessFactory.getInstance(param), ParameterAccessFactory.getInstance(param));
  }

  @Test
  public void paramFactory_getParameter() {
    UserParameter param = createParam("ref");
    assertSame(param, ParameterAccessFactory.getInstance(param).getParameter());
  }

  @Test
  public void paramFactory_createExpression_returnsParameterAccess() {
    UserParameter param = createParam("access");
    Expression expr = ParameterAccessFactory.getInstance(param).createExpression();
    assertTrue(expr instanceof ParameterAccess);
  }

  @Test
  public void paramFactory_getRepr() {
    UserParameter param = createParam("myParam");
    assertEquals("myParam", ParameterAccessFactory.getInstance(param).getRepr());
  }

  // ===== ThisMethodInvocationFactory =====

  @Test
  public void thisMethodFactory_noParams_returnsCachedInstance() {
    UserMethod method = createNoArgMethod("getColor");
    assertSame(
        ThisMethodInvocationFactory.getInstance(method),
        ThisMethodInvocationFactory.getInstance(method)
    );
  }

  @Test
  public void thisMethodFactory_withParams_returnsNull() {
    UserParameter p = createParam("arg");
    UserMethod method = new UserMethod("setColor", JavaType.getInstance(Object.class),
        new UserParameter[]{p}, new BlockStatement());
    assertNull(ThisMethodInvocationFactory.getInstance(method));
  }

  @Test
  public void thisMethodFactory_getMethod() {
    UserMethod method = createNoArgMethod("getShape");
    assertSame(method, ThisMethodInvocationFactory.getInstance(method).getMethod());
  }

  @Test
  public void thisMethodFactory_createExpression_returnsMethodInvocation() {
    UserMethod method = createNoArgMethod("getSize");
    Expression expr = ThisMethodInvocationFactory.getInstance(method).createExpression();
    assertTrue(expr instanceof MethodInvocation);
  }

  @Test
  public void thisMethodFactory_getRepr() {
    UserMethod method = createNoArgMethod("getFoo");
    assertEquals("this's Foo", ThisMethodInvocationFactory.getInstance(method).getRepr());
  }

  // ===== ThisFieldAccessMethodInvocationFactory =====

  @Test
  public void fieldMethodFactory_getInstanceCaches() {
    UserField field = createField("fmCached");
    UserMethod method = createNoArgMethod("getWeight");
    assertSame(
        ThisFieldAccessMethodInvocationFactory.getInstance(field, method),
        ThisFieldAccessMethodInvocationFactory.getInstance(field, method)
    );
  }

  @Test
  public void fieldMethodFactory_getField() {
    UserField field = createField("fmField");
    UserMethod method = createNoArgMethod("getHeight");
    assertSame(field, ThisFieldAccessMethodInvocationFactory.getInstance(field, method).getField());
  }

  @Test
  public void fieldMethodFactory_getRepr() {
    UserField field = createField("myField");
    UserMethod method = createNoArgMethod("getFoo");
    String repr = ThisFieldAccessMethodInvocationFactory.getInstance(field, method).getRepr();
    assertEquals("this.myField's Foo", repr);
  }

  @Test
  public void fieldMethodFactory_createExpression_returnsMethodInvocation() {
    UserField field = createField("fmExpr");
    UserMethod method = createNoArgMethod("getWidth");
    Expression expr = ThisFieldAccessMethodInvocationFactory.getInstance(field, method).createExpression();
    assertTrue(expr instanceof MethodInvocation);
  }

  // ===== LocalAccessMethodInvocationFactory =====

  @Test
  public void localMethodFactory_getInstanceCaches() {
    UserLocal local = createLocal("lmCached");
    UserMethod method = createNoArgMethod("getAge");
    assertSame(
        LocalAccessMethodInvocationFactory.getInstance(local, method),
        LocalAccessMethodInvocationFactory.getInstance(local, method)
    );
  }

  @Test
  public void localMethodFactory_getLocal() {
    UserLocal local = createLocal("lmLocal");
    UserMethod method = createNoArgMethod("getDepth");
    assertSame(local, LocalAccessMethodInvocationFactory.getInstance(local, method).getLocal());
  }

  @Test
  public void localMethodFactory_getRepr() {
    UserLocal local = createLocal("myVar");
    UserMethod method = createNoArgMethod("getFoo");
    assertEquals("myVar's Foo", LocalAccessMethodInvocationFactory.getInstance(local, method).getRepr());
  }

  // ===== ParameterAccessMethodInvocationFactory =====

  @Test
  public void paramMethodFactory_getInstanceCaches() {
    UserParameter param = createParam("pmCached");
    UserMethod method = createNoArgMethod("getLabel");
    assertSame(
        ParameterAccessMethodInvocationFactory.getInstance(param, method),
        ParameterAccessMethodInvocationFactory.getInstance(param, method)
    );
  }

  @Test
  public void paramMethodFactory_getParameter() {
    UserParameter param = createParam("pmParam");
    UserMethod method = createNoArgMethod("getTag");
    assertSame(param, ParameterAccessMethodInvocationFactory.getInstance(param, method).getParameter());
  }

  @Test
  public void paramMethodFactory_getRepr() {
    UserParameter param = createParam("myParam");
    UserMethod method = createNoArgMethod("getFoo");
    assertEquals("myParam's Foo", ParameterAccessMethodInvocationFactory.getInstance(param, method).getRepr());
  }

  // ===== ParameterAccessMethodInvocationMethodInvocationFactory =====

  @Test
  public void paramMethodMethodFactory_getInstanceCaches() {
    UserParameter param = createParam("pmmCached");
    UserMethod inner = createNoArgMethod("getInner");
    UserMethod outer = createNoArgMethod("getOuter");
    assertSame(
        ParameterAccessMethodInvocationMethodInvocationFactory.getInstance(param, inner, outer),
        ParameterAccessMethodInvocationMethodInvocationFactory.getInstance(param, inner, outer)
    );
  }

  @Test
  public void paramMethodMethodFactory_getParameter() {
    UserParameter param = createParam("pmmParam");
    UserMethod inner = createNoArgMethod("getAlpha");
    UserMethod outer = createNoArgMethod("getBeta");
    assertSame(param, ParameterAccessMethodInvocationMethodInvocationFactory.getInstance(param, inner, outer).getParameter());
  }

  @Test
  public void paramMethodMethodFactory_getInnerMethod() {
    UserParameter param = createParam("pmmInner");
    UserMethod inner = createNoArgMethod("getFirst");
    UserMethod outer = createNoArgMethod("getSecond");
    assertSame(inner, ParameterAccessMethodInvocationMethodInvocationFactory.getInstance(param, inner, outer).getInnerMethod());
  }

  @Test
  public void paramMethodMethodFactory_getRepr() {
    UserParameter param = createParam("myParam");
    UserMethod inner = createNoArgMethod("getFoo");
    UserMethod outer = createNoArgMethod("getBar");
    String repr = ParameterAccessMethodInvocationMethodInvocationFactory.getInstance(param, inner, outer).getRepr();
    assertEquals("myParam.getFoo()'s Bar", repr);
  }

  // ===== ThisFieldAccessArrayElementMethodInvocationFactory =====

  @Test
  public void arrayFactory_getInstanceCaches() {
    UserField field = createField("arrCached");
    UserMethod method = createNoArgMethod("getItems");
    assertSame(
        ThisFieldAccessArrayElementMethodInvocationFactory.getInstance(field, method),
        ThisFieldAccessArrayElementMethodInvocationFactory.getInstance(field, method)
    );
  }

  @Test
  public void arrayFactory_getField() {
    UserField field = createField("arrField");
    UserMethod method = createNoArgMethod("getElems");
    assertSame(field, ThisFieldAccessArrayElementMethodInvocationFactory.getInstance(field, method).getField());
  }

  @Test
  public void arrayFactory_getMethod() {
    UserField field = createField("arrMethod");
    UserMethod method = createNoArgMethod("getList");
    assertSame(method, ThisFieldAccessArrayElementMethodInvocationFactory.getInstance(field, method).getMethod());
  }

  @Test
  public void arrayFactory_getRepr() {
    UserField field = createField("myField");
    UserMethod method = createNoArgMethod("getFoo");
    String repr = ThisFieldAccessArrayElementMethodInvocationFactory.getInstance(field, method).getRepr();
    assertEquals("this.myField's Foo[ 0 ]", repr);
  }
}
