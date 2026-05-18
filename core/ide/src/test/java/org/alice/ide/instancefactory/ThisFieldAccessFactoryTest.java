package org.alice.ide.instancefactory;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

public class ThisFieldAccessFactoryTest {
  private UserField createField(String name) {
    UserField field = new UserField();
    field.name.setValue(name);
    field.valueType.setValue(JavaType.getInstance(String.class));
    return field;
  }

  @Test
  public void getInstance_returnsNonNull() {
    assertNotNull(ThisFieldAccessFactory.getInstance(createField("f1")));
  }

  @Test
  public void getInstance_sameSingleton() {
    UserField f = createField("f2");
    assertSame(ThisFieldAccessFactory.getInstance(f), ThisFieldAccessFactory.getInstance(f));
  }

  @Test
  public void getField_returnsField() {
    UserField f = createField("f3");
    assertSame(f, ThisFieldAccessFactory.getInstance(f).getField());
  }

  @Test
  public void getValueType_returnsFieldValueType() {
    UserField f = createField("f4");
    assertEquals(JavaType.getInstance(String.class), ThisFieldAccessFactory.getInstance(f).getValueType());
  }

  @Test
  public void createTransientExpression_returnsNonNull() {
    UserField f = createField("f5");
    Expression expr = ThisFieldAccessFactory.getInstance(f).createTransientExpression();
    assertNotNull(expr);
  }

  @Test
  public void createExpression_returnsNonNull() {
    UserField f = createField("f6");
    Expression expr = ThisFieldAccessFactory.getInstance(f).createExpression();
    assertNotNull(expr);
  }
}
