package org.alice.ide.instancefactory;

import org.junit.Test;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.UserField;

import static org.junit.Assert.*;

public class ThisFieldAccessFactoryExtendedTest {
  @Test
  public void getInstance_returnsSameInstanceForSameField() {
    UserField field = new UserField();
    field.name.setValue("cat");
    field.valueType.setValue(JavaType.getInstance(Object.class));

    ThisFieldAccessFactory f1 = ThisFieldAccessFactory.getInstance(field);
    ThisFieldAccessFactory f2 = ThisFieldAccessFactory.getInstance(field);
    assertSame(f1, f2);
  }

  @Test
  public void getInstance_returnsDifferentInstancesForDifferentFields() {
    UserField field1 = new UserField();
    field1.name.setValue("dog");
    field1.valueType.setValue(JavaType.getInstance(Object.class));
    UserField field2 = new UserField();
    field2.name.setValue("bird");
    field2.valueType.setValue(JavaType.getInstance(Object.class));

    ThisFieldAccessFactory f1 = ThisFieldAccessFactory.getInstance(field1);
    ThisFieldAccessFactory f2 = ThisFieldAccessFactory.getInstance(field2);
    assertNotSame(f1, f2);
  }

  @Test
  public void getField_returnsConstructorField() {
    UserField field = new UserField();
    field.name.setValue("fish");
    field.valueType.setValue(JavaType.getInstance(Object.class));

    ThisFieldAccessFactory factory = ThisFieldAccessFactory.getInstance(field);
    assertSame(field, factory.getField());
  }

  @Test
  public void getRepr_startsWithThis() {
    UserField field = new UserField();
    field.name.setValue("rabbit");
    field.valueType.setValue(JavaType.getInstance(Object.class));

    ThisFieldAccessFactory factory = ThisFieldAccessFactory.getInstance(field);
    assertTrue(factory.getRepr().startsWith("this."));
  }

  @Test
  public void getRepr_containsFieldName() {
    UserField field = new UserField();
    field.name.setValue("hamster");
    field.valueType.setValue(JavaType.getInstance(Object.class));

    ThisFieldAccessFactory factory = ThisFieldAccessFactory.getInstance(field);
    assertTrue(factory.getRepr().contains("hamster"));
  }

  @Test
  public void getRepr_exactFormat() {
    UserField field = new UserField();
    field.name.setValue("snake");
    field.valueType.setValue(JavaType.getInstance(Object.class));

    ThisFieldAccessFactory factory = ThisFieldAccessFactory.getInstance(field);
    assertEquals("this.snake", factory.getRepr());
  }

  @Test
  public void getValueType_matchesFieldType() {
    UserField field = new UserField();
    field.name.setValue("typed");
    field.valueType.setValue(JavaType.getInstance(String.class));

    ThisFieldAccessFactory factory = ThisFieldAccessFactory.getInstance(field);
    assertEquals(JavaType.getInstance(String.class), factory.getValueType());
  }

  @Test
  public void createExpression_returnsFieldAccess() {
    UserField field = new UserField();
    field.name.setValue("expr");
    field.valueType.setValue(JavaType.getInstance(Object.class));

    ThisFieldAccessFactory factory = ThisFieldAccessFactory.getInstance(field);
    var expr = factory.createExpression();
    assertTrue(expr instanceof FieldAccess);
  }

  @Test
  public void createTransientExpression_returnsFieldAccess() {
    UserField field = new UserField();
    field.name.setValue("transientField");
    field.valueType.setValue(JavaType.getInstance(Object.class));

    ThisFieldAccessFactory factory = ThisFieldAccessFactory.getInstance(field);
    var expr = factory.createTransientExpression();
    assertTrue(expr instanceof FieldAccess);
  }

  @Test
  public void toString_matchesRepr() {
    UserField field = new UserField();
    field.name.setValue("str");
    field.valueType.setValue(JavaType.getInstance(Object.class));

    ThisFieldAccessFactory factory = ThisFieldAccessFactory.getInstance(field);
    assertEquals(factory.getRepr(), factory.toString());
  }
}
