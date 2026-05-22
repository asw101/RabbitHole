package org.alice.ide.instancefactory;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

public class LocalAccessFactoryTest {
  private static UserLocal local(String name, Class<?> type) {
    return new UserLocal(name, JavaType.getInstance(type), false);
  }

  @Test
  public void getInstance_cachesSameLocal() {
    UserLocal local = local("x", String.class);
    assertSame(LocalAccessFactory.getInstance(local), LocalAccessFactory.getInstance(local));
  }

  @Test
  public void getLocal_returnsSameLocal() {
    UserLocal local = local("myVar", Integer.class);
    assertSame(local, LocalAccessFactory.getInstance(local).getLocal());
  }

  @Test
  public void getValueType_matchesLocalType() {
    LocalAccessFactory factory = LocalAccessFactory.getInstance(local("v", Double.class));
    assertEquals(JavaType.getInstance(Double.class), factory.getValueType());
  }

  @Test
  public void createExpression_returnsLocalAccess() {
    Expression expr = LocalAccessFactory.getInstance(local("a", String.class)).createExpression();
    assertTrue(expr instanceof LocalAccess);
  }

  @Test
  public void createTransientExpression_returnsLocalAccess() {
    Expression expr = LocalAccessFactory.getInstance(local("b", String.class)).createTransientExpression();
    assertTrue(expr instanceof LocalAccess);
  }

  @Test
  public void getRepr_containsLocalName() {
    assertTrue(LocalAccessFactory.getInstance(local("myLocal", String.class)).getRepr().contains("myLocal"));
  }
}
