package org.alice.ide.instancefactory;

import org.junit.Test;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.ThisExpression;

import static org.junit.Assert.*;

public class ThisInstanceFactoryTest {
  @Test
  public void getInstance_returnsSameInstance() {
    ThisInstanceFactory f1 = ThisInstanceFactory.getInstance();
    ThisInstanceFactory f2 = ThisInstanceFactory.getInstance();
    assertSame(f1, f2);
  }

  @Test
  public void getRepr_returnsThis() {
    assertEquals("this", ThisInstanceFactory.getInstance().getRepr());
  }

  @Test
  public void toString_returnsThis() {
    assertEquals("this", ThisInstanceFactory.getInstance().toString());
  }

  @Test
  public void createExpression_returnsThisExpression() {
    Expression expr = ThisInstanceFactory.getInstance().createExpression();
    assertNotNull(expr);
    assertTrue(expr instanceof ThisExpression);
  }

  @Test
  public void createTransientExpression_returnsNonNull() {
    Expression expr = ThisInstanceFactory.getInstance().createTransientExpression();
    assertNotNull(expr);
  }

  @Test
  public void getIconFactory_returnsNonNull() {
    assertNotNull(ThisInstanceFactory.getInstance().getIconFactory());
  }

  @Test
  public void getMutablePropertiesOfInterest_returnsEmpty() {
    assertEquals(0, ThisInstanceFactory.getInstance().getMutablePropertiesOfInterest().length);
  }
}
