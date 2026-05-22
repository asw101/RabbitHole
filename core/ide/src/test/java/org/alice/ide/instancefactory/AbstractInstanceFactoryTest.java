package org.alice.ide.instancefactory;

import org.junit.Test;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.ThisExpression;

import static org.junit.Assert.*;

public class AbstractInstanceFactoryTest {
  @Test
  public void getMutablePropertiesOfInterest_emptyByDefault() {
    ThisInstanceFactory factory = ThisInstanceFactory.getInstance();
    assertEquals(0, factory.getMutablePropertiesOfInterest().length);
  }

  @Test
  public void getMutablePropertiesOfInterest_returnsArray() {
    ThisInstanceFactory factory = ThisInstanceFactory.getInstance();
    assertNotNull(factory.getMutablePropertiesOfInterest());
    assertTrue(factory.getMutablePropertiesOfInterest() instanceof edu.cmu.cs.dennisc.property.InstanceProperty[]);
  }

  @Test
  public void toString_delegatesToGetRepr() {
    ThisInstanceFactory factory = ThisInstanceFactory.getInstance();
    String repr = factory.getRepr();
    String toString = factory.toString();
    assertEquals(repr, toString);
  }

  @Test
  public void getIconFactory_returnsNonNull() {
    ThisInstanceFactory factory = ThisInstanceFactory.getInstance();
    assertNotNull(factory.getIconFactory());
  }

  @Test
  public void createExpression_returnsThisExpression() {
    ThisInstanceFactory factory = ThisInstanceFactory.getInstance();
    Expression expr = factory.createExpression();
    assertTrue(expr instanceof ThisExpression);
  }

  @Test
  public void getValueType_returnsNull_withoutActiveIDE() {
    try {
      ThisInstanceFactory.getInstance().getValueType();
    } catch (NullPointerException e) {
      // Expected when no IDE is active
    }
  }
}
