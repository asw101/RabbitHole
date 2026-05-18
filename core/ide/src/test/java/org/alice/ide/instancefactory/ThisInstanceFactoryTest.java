package org.alice.ide.instancefactory;

import org.junit.Test;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.ThisExpression;

import static org.junit.Assert.*;

public class ThisInstanceFactoryTest {
  @Test
  public void getInstance_returnsSingleton() {
    ThisInstanceFactory f1 = ThisInstanceFactory.getInstance();
    ThisInstanceFactory f2 = ThisInstanceFactory.getInstance();
    assertSame(f1, f2);
  }

  @Test
  public void reprAndToString_returnThis() {
    ThisInstanceFactory instance = ThisInstanceFactory.getInstance();
    assertEquals("this", instance.getRepr());
    assertEquals("this", instance.toString());
  }

  @Test
  public void createExpression_returnsThisExpression() {
    Expression expr = ThisInstanceFactory.getInstance().createExpression();
    assertNotNull(expr);
    assertTrue(expr instanceof ThisExpression);
  }

  @Test
  public void createTransientExpression_returnsNonNull() {
    assertNotNull(ThisInstanceFactory.getInstance().createTransientExpression());
  }

  @Test
  public void accessors_returnExpectedDefaults() {
    ThisInstanceFactory instance = ThisInstanceFactory.getInstance();
    assertNotNull(instance.getIconFactory());
    assertEquals(0, instance.getMutablePropertiesOfInterest().length);
  }
}
