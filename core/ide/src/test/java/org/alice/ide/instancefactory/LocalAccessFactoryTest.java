package org.alice.ide.instancefactory;

import org.lgna.project.ast.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class LocalAccessFactoryTest {

  private UserLocal createLocal(String name) {
    UserLocal local = new UserLocal();
    local.name.setValue(name);
    local.valueType.setValue(JavaType.getInstance(String.class));
    return local;
  }

  @Test
  public void getInstance_returnsNonNull() {
    UserLocal local = createLocal("x");
    assertNotNull(LocalAccessFactory.getInstance(local));
  }

  @Test
  public void getInstance_sameLocal_returnsSameInstance() {
    UserLocal local = createLocal("x");
    assertSame(LocalAccessFactory.getInstance(local), LocalAccessFactory.getInstance(local));
  }

  @Test
  public void getInstance_differentLocals_returnsDifferentInstances() {
    UserLocal a = createLocal("a");
    UserLocal b = createLocal("b");
    assertNotSame(LocalAccessFactory.getInstance(a), LocalAccessFactory.getInstance(b));
  }

  @Test
  public void getLocal_returnsSameLocal() {
    UserLocal local = createLocal("myVar");
    LocalAccessFactory factory = LocalAccessFactory.getInstance(local);
    assertSame(local, factory.getLocal());
  }

  @Test
  public void createExpression_returnsLocalAccess() {
    UserLocal local = createLocal("x");
    LocalAccessFactory factory = LocalAccessFactory.getInstance(local);
    Expression expr = factory.createExpression();
    assertNotNull(expr);
    assertTrue(expr instanceof LocalAccess);
  }

  @Test
  public void createTransientExpression_returnsLocalAccess() {
    UserLocal local = createLocal("x");
    LocalAccessFactory factory = LocalAccessFactory.getInstance(local);
    Expression expr = factory.createTransientExpression();
    assertNotNull(expr);
    assertTrue(expr instanceof LocalAccess);
  }

  @Test
  public void getValueType_matchesLocalValueType() {
    UserLocal local = createLocal("x");
    LocalAccessFactory factory = LocalAccessFactory.getInstance(local);
    assertSame(local.getValueType(), factory.getValueType());
  }
}
