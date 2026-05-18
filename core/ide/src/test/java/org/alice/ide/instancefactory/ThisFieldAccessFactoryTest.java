package org.alice.ide.instancefactory;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

public class ThisFieldAccessFactoryTest {
  @Test
  public void getInstance_returnsSameForSameField() {
    UserField field = new UserField();
    field.name.setValue("myField");
    field.valueType.setValue(JavaType.getInstance(String.class));
    ThisFieldAccessFactory f1 = ThisFieldAccessFactory.getInstance(field);
    ThisFieldAccessFactory f2 = ThisFieldAccessFactory.getInstance(field);
    assertSame(f1, f2);
  }

  @Test
  public void getField_returnsSameField() {
    UserField field = new UserField();
    field.name.setValue("testField");
    field.valueType.setValue(JavaType.getInstance(Integer.class));
    ThisFieldAccessFactory factory = ThisFieldAccessFactory.getInstance(field);
    assertSame(field, factory.getField());
  }

  @Test
  public void getValueType_matchesFieldType() {
    UserField field = new UserField();
    field.name.setValue("f");
    field.valueType.setValue(JavaType.getInstance(Double.class));
    ThisFieldAccessFactory factory = ThisFieldAccessFactory.getInstance(field);
    assertEquals(JavaType.getInstance(Double.class), factory.getValueType());
  }

  @Test
  public void createExpression_returnsFieldAccess() {
    UserField field = new UserField();
    field.name.setValue("fa");
    field.valueType.setValue(JavaType.getInstance(String.class));
    ThisFieldAccessFactory factory = ThisFieldAccessFactory.getInstance(field);
    Expression expr = factory.createExpression();
    assertNotNull(expr);
    assertTrue(expr instanceof FieldAccess);
  }

  @Test
  public void createTransientExpression_returnsFieldAccess() {
    UserField field = new UserField();
    field.name.setValue("fb");
    field.valueType.setValue(JavaType.getInstance(String.class));
    ThisFieldAccessFactory factory = ThisFieldAccessFactory.getInstance(field);
    Expression expr = factory.createTransientExpression();
    assertNotNull(expr);
    assertTrue(expr instanceof FieldAccess);
  }

  @Test
  public void getRepr_containsFieldName() {
    UserField field = new UserField();
    field.name.setValue("mySpecialField");
    field.valueType.setValue(JavaType.getInstance(String.class));
    ThisFieldAccessFactory factory = ThisFieldAccessFactory.getInstance(field);
    assertTrue(factory.getRepr().contains("mySpecialField"));
  }
}
