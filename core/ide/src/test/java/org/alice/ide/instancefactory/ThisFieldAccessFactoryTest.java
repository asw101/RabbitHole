package org.alice.ide.instancefactory;

import org.lgna.project.ast.*;
import org.junit.Test;

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
    UserField field = createField("f");
    assertNotNull(ThisFieldAccessFactory.getInstance(field));
  }

  @Test
  public void getInstance_sameField_returnsSameInstance() {
    UserField field = createField("f");
    assertSame(ThisFieldAccessFactory.getInstance(field), ThisFieldAccessFactory.getInstance(field));
  }

  @Test
  public void getInstance_differentFields_returnsDifferentInstances() {
    UserField a = createField("a");
    UserField b = createField("b");
    assertNotSame(ThisFieldAccessFactory.getInstance(a), ThisFieldAccessFactory.getInstance(b));
  }

  @Test
  public void getField_returnsSameField() {
    UserField field = createField("myField");
    ThisFieldAccessFactory factory = ThisFieldAccessFactory.getInstance(field);
    assertSame(field, factory.getField());
  }

  @Test
  public void getRepr_containsThis() {
    UserField field = createField("myField");
    ThisFieldAccessFactory factory = ThisFieldAccessFactory.getInstance(field);
    assertTrue(factory.getRepr().startsWith("this."));
  }

  @Test
  public void getRepr_containsFieldName() {
    UserField field = createField("myField");
    ThisFieldAccessFactory factory = ThisFieldAccessFactory.getInstance(field);
    assertTrue(factory.getRepr().contains("myField"));
  }

  @Test
  public void createExpression_returnsFieldAccess() {
    UserField field = createField("f");
    ThisFieldAccessFactory factory = ThisFieldAccessFactory.getInstance(field);
    Expression expr = factory.createExpression();
    assertNotNull(expr);
    assertTrue(expr instanceof FieldAccess);
  }

  @Test
  public void createTransientExpression_returnsFieldAccess() {
    UserField field = createField("f");
    ThisFieldAccessFactory factory = ThisFieldAccessFactory.getInstance(field);
    Expression expr = factory.createTransientExpression();
    assertNotNull(expr);
    assertTrue(expr instanceof FieldAccess);
  }

  @Test
  public void getValueType_matchesFieldValueType() {
    UserField field = createField("f");
    ThisFieldAccessFactory factory = ThisFieldAccessFactory.getInstance(field);
    assertSame(field.getValueType(), factory.getValueType());
  }
}
