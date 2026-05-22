package org.alice.ide.instancefactory;

import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.LocalAccess;

import static org.junit.Assert.*;

public class LocalAccessFactoryBehaviorTest {
  @Test
  public void getInstanceCachesAndCreatesLocalAccessExpressions() {
    var local = InstanceFactoryTestSupport.createLocal("score", JavaType.INTEGER_OBJECT_TYPE);
    LocalAccessFactory factory = LocalAccessFactory.getInstance(local);

    assertSame(factory, LocalAccessFactory.getInstance(local));
    assertTrue(factory.createExpression() instanceof LocalAccess);
    assertTrue(factory.createTransientExpression() instanceof LocalAccess);
    assertSame(local, ((LocalAccess) factory.createExpression()).local.getValue());
    assertEquals("score", factory.getRepr());
    assertSame(JavaType.INTEGER_OBJECT_TYPE, factory.getValueType());
  }
}
