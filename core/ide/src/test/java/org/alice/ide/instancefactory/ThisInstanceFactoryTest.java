package org.alice.ide.instancefactory;

import org.junit.Test;

import static org.junit.Assert.*;

public class ThisInstanceFactoryTest {

  @Test
  public void getInstance_returnsNonNull() {
    assertNotNull(ThisInstanceFactory.getInstance());
  }

  @Test
  public void getInstance_returnsSameInstance() {
    assertSame(ThisInstanceFactory.getInstance(), ThisInstanceFactory.getInstance());
  }

  @Test
  public void getRepr_returnsThis() {
    assertEquals("this", ThisInstanceFactory.getInstance().getRepr());
  }

  @Test
  public void toString_containsThis() {
    assertTrue(ThisInstanceFactory.getInstance().toString().contains("this"));
  }

  @Test
  public void createTransientExpression_notNull() {
    assertNotNull(ThisInstanceFactory.getInstance().createTransientExpression());
  }

  @Test
  public void createExpression_notNull() {
    assertNotNull(ThisInstanceFactory.getInstance().createExpression());
  }
}
