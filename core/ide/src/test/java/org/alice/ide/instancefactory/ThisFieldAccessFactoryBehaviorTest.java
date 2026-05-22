package org.alice.ide.instancefactory;

import org.junit.Test;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.ThisExpression;

import static org.junit.Assert.*;

public class ThisFieldAccessFactoryBehaviorTest {
  @Test
  public void getInstanceCachesAndCreatesThisFieldAccessExpressions() {
    var field = InstanceFactoryTestSupport.createStringField("name");
    ThisFieldAccessFactory factory = ThisFieldAccessFactory.getInstance(field);

    assertSame(factory, ThisFieldAccessFactory.getInstance(field));
    FieldAccess expression = factory.createExpression();
    assertTrue(expression.expression.getValue() instanceof ThisExpression);
    assertSame(field, expression.field.getValue());
    assertEquals("this.name", factory.getRepr());
    assertSame(field.getValueType(), factory.getValueType());
  }
}
