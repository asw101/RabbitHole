package org.alice.ide.instancefactory;

import org.junit.Test;
import org.lgna.project.ast.*;

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
    LocalAccessFactory factory = LocalAccessFactory.getInstance(createLocal("x"));
    assertNotNull(factory);
  }

  @Test
  public void getInstance_sameSingleton() {
    UserLocal local = createLocal("y");
    LocalAccessFactory f1 = LocalAccessFactory.getInstance(local);
    LocalAccessFactory f2 = LocalAccessFactory.getInstance(local);
    assertSame(f1, f2);
  }

  @Test
  public void getLocal_returnsLocal() {
    UserLocal local = createLocal("z");
    LocalAccessFactory factory = LocalAccessFactory.getInstance(local);
    assertSame(local, factory.getLocal());
  }

  @Test
  public void getValueType_returnsLocalValueType() {
    UserLocal local = createLocal("w");
    LocalAccessFactory factory = LocalAccessFactory.getInstance(local);
    assertEquals(JavaType.getInstance(String.class), factory.getValueType());
  }

  @Test
  public void createTransientExpression_returnsLocalAccess() {
    UserLocal local = createLocal("v");
    LocalAccessFactory factory = LocalAccessFactory.getInstance(local);
    Expression expr = factory.createTransientExpression();
    assertNotNull(expr);
    assertTrue(expr instanceof LocalAccess);
  }

  @Test
  public void createExpression_returnsLocalAccess() {
    UserLocal local = createLocal("u");
    LocalAccessFactory factory = LocalAccessFactory.getInstance(local);
    Expression expr = factory.createExpression();
    assertNotNull(expr);
    assertTrue(expr instanceof LocalAccess);
  }
}
