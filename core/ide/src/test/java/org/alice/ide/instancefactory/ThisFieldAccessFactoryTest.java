package org.alice.ide.instancefactory;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

public class ThisFieldAccessFactoryTest {
  private static UserField field(String name, Class<?> type) {
    UserField f = new UserField();
    f.name.setValue(name);
    f.valueType.setValue(JavaType.getInstance(type));
    return f;
  }

  @Test
  public void getInstance_cachesSameField() {
    UserField f = field("myField", String.class);
    assertSame(ThisFieldAccessFactory.getInstance(f), ThisFieldAccessFactory.getInstance(f));
  }

  @Test
  public void getField_returnsSame() {
    UserField f = field("testField", Integer.class);
    assertSame(f, ThisFieldAccessFactory.getInstance(f).getField());
  }

  @Test
  public void getValueType_matchesFieldType() {
    assertEquals(JavaType.getInstance(Double.class),
        ThisFieldAccessFactory.getInstance(field("f", Double.class)).getValueType());
  }

  @Test
  public void createExpression_returnsFieldAccess() {
    assertTrue(ThisFieldAccessFactory.getInstance(field("fa", String.class)).createExpression() instanceof FieldAccess);
  }

  @Test
  public void createTransientExpression_returnsFieldAccess() {
    assertTrue(ThisFieldAccessFactory.getInstance(field("fb", String.class)).createTransientExpression() instanceof FieldAccess);
  }

  @Test
  public void getRepr_containsFieldName() {
    assertTrue(ThisFieldAccessFactory.getInstance(field("mySpecialField", String.class)).getRepr().contains("mySpecialField"));
  }
}
