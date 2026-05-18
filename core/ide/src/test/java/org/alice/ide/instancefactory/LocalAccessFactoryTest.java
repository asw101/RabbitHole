package org.alice.ide.instancefactory;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

public class LocalAccessFactoryTest {
  @Test
  public void getInstance_returnsSameForSameLocal() {
    UserLocal local = new UserLocal("x", JavaType.getInstance(String.class), false);
    LocalAccessFactory f1 = LocalAccessFactory.getInstance(local);
    LocalAccessFactory f2 = LocalAccessFactory.getInstance(local);
    assertSame(f1, f2);
  }

  @Test
  public void getLocal_returnsSameLocal() {
    UserLocal local = new UserLocal("myVar", JavaType.getInstance(Integer.class), false);
    LocalAccessFactory factory = LocalAccessFactory.getInstance(local);
    assertSame(local, factory.getLocal());
  }

  @Test
  public void getValueType_matchesLocalType() {
    UserLocal local = new UserLocal("v", JavaType.getInstance(Double.class), false);
    LocalAccessFactory factory = LocalAccessFactory.getInstance(local);
    assertEquals(JavaType.getInstance(Double.class), factory.getValueType());
  }

  @Test
  public void createExpression_returnsLocalAccess() {
    UserLocal local = new UserLocal("a", JavaType.getInstance(String.class), false);
    LocalAccessFactory factory = LocalAccessFactory.getInstance(local);
    Expression expr = factory.createExpression();
    assertNotNull(expr);
    assertTrue(expr instanceof LocalAccess);
  }

  @Test
  public void createTransientExpression_returnsLocalAccess() {
    UserLocal local = new UserLocal("b", JavaType.getInstance(String.class), false);
    LocalAccessFactory factory = LocalAccessFactory.getInstance(local);
    Expression expr = factory.createTransientExpression();
    assertNotNull(expr);
    assertTrue(expr instanceof LocalAccess);
  }

  @Test
  public void getRepr_containsLocalName() {
    UserLocal local = new UserLocal("myLocal", JavaType.getInstance(String.class), false);
    LocalAccessFactory factory = LocalAccessFactory.getInstance(local);
    assertTrue(factory.getRepr().contains("myLocal"));
  }
}
