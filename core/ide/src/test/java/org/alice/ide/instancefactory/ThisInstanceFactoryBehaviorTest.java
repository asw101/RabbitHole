package org.alice.ide.instancefactory;

import org.alice.ide.ast.CurrentThisExpression;
import org.junit.Test;
import org.lgna.project.ast.ThisExpression;

import static org.junit.Assert.*;

public class ThisInstanceFactoryBehaviorTest {
  @Test
  public void singletonCreatesThisExpressionsAndReadableRepresentation() {
    ThisInstanceFactory factory = ThisInstanceFactory.getInstance();
    assertSame(factory, ThisInstanceFactory.getInstance());
    assertTrue(factory.createExpression() instanceof ThisExpression);
    assertTrue(factory.createTransientExpression() instanceof CurrentThisExpression);
    assertEquals("this", factory.getRepr());
    assertEquals("this", factory.toString());
  }
}
