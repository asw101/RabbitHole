package org.alice.ide.instancefactory;

import org.junit.Test;

import static org.junit.Assert.*;

public class InstanceFactoryInterfaceTest {
  @Test
  public void thisInstanceFactory_implementsInterface() {
    InstanceFactory factory = ThisInstanceFactory.getInstance();
    assertNotNull(factory);
    assertTrue(factory instanceof InstanceFactory);
  }

  @Test
  public void thisInstanceFactory_getRepr_returnsNonNull() {
    InstanceFactory factory = ThisInstanceFactory.getInstance();
    assertNotNull(factory.getRepr());
  }

  @Test
  public void thisInstanceFactory_createExpression_returnsNonNull() {
    InstanceFactory factory = ThisInstanceFactory.getInstance();
    assertNotNull(factory.createExpression());
  }

  @Test
  public void thisInstanceFactory_createTransientExpression_returnsNonNull() {
    InstanceFactory factory = ThisInstanceFactory.getInstance();
    assertNotNull(factory.createTransientExpression());
  }

  @Test
  public void thisInstanceFactory_getIconFactory_returnsNonNull() {
    InstanceFactory factory = ThisInstanceFactory.getInstance();
    assertNotNull(factory.getIconFactory());
  }

  @Test
  public void thisInstanceFactory_getMutablePropertiesOfInterest_returnsNonNull() {
    InstanceFactory factory = ThisInstanceFactory.getInstance();
    assertNotNull(factory.getMutablePropertiesOfInterest());
  }

  @Test
  public void thisInstanceFactory_isAbstractInstanceFactory() {
    ThisInstanceFactory factory = ThisInstanceFactory.getInstance();
    assertTrue(factory instanceof AbstractInstanceFactory);
  }

  @Test
  public void abstractInstanceFactory_toString_matchesRepr() {
    ThisInstanceFactory factory = ThisInstanceFactory.getInstance();
    assertEquals(factory.getRepr(), factory.toString());
  }
}
