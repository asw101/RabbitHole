package org.alice.ide.ast.draganddrop.expression;

import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserLocal;
import org.lgna.project.ast.UserParameter;

import static org.junit.Assert.*;

public class ExpressionDragModelComprehensiveTest {
  private static UserField createField(String name, Class<?> valueClass) {
    UserField field = new UserField();
    field.name.setValue(name);
    field.valueType.setValue(JavaType.getInstance(valueClass));
    return field;
  }

  private static UserLocal createLocal(String name) {
    return new UserLocal(name, Object.class, false);
  }

  private static UserParameter createParameter(String name) {
    return new UserParameter(name, Object.class);
  }

  @Test public void fieldAccess_sameFieldSingleton() {
    UserField field = createField("field", Object.class);
    assertSame(FieldAccessDragModel.getInstance(field), FieldAccessDragModel.getInstance(field));
  }

  @Test public void fieldAccess_differentFieldsDifferent() {
    assertNotSame(FieldAccessDragModel.getInstance(createField("a", Object.class)), FieldAccessDragModel.getInstance(createField("b", Object.class)));
  }

  @Test public void fieldAccess_hierarchy() {
    assertTrue(FieldAccessDragModel.getInstance(createField("field", Object.class)) instanceof AbstractExpressionDragModel);
  }

  @Test public void localAccess_sameLocalSingleton() {
    UserLocal local = createLocal("local");
    assertSame(LocalAccessDragModel.getInstance(local), LocalAccessDragModel.getInstance(local));
  }

  @Test public void localAccess_differentLocalsDifferent() {
    assertNotSame(LocalAccessDragModel.getInstance(createLocal("a")), LocalAccessDragModel.getInstance(createLocal("b")));
  }

  @Test public void localAccess_hierarchy() {
    assertTrue(LocalAccessDragModel.getInstance(createLocal("local")) instanceof AbstractExpressionDragModel);
  }

  @Test public void parameterAccess_sameParameterSingleton() {
    UserParameter parameter = createParameter("parameter");
    assertSame(ParameterAccessDragModel.getInstance(parameter), ParameterAccessDragModel.getInstance(parameter));
  }

  @Test public void parameterAccess_differentParametersDifferent() {
    assertNotSame(ParameterAccessDragModel.getInstance(createParameter("a")), ParameterAccessDragModel.getInstance(createParameter("b")));
  }

  @Test public void parameterAccess_hierarchy() {
    assertTrue(ParameterAccessDragModel.getInstance(createParameter("parameter")) instanceof AbstractExpressionDragModel);
  }

  @Test public void fieldArrayAtIndex_sameFieldSingleton() {
    UserField field = createField("arrayField", String[].class);
    assertSame(FieldArrayAtIndexDragModel.getInstance(field), FieldArrayAtIndexDragModel.getInstance(field));
  }

  @Test public void fieldArrayAtIndex_differentFieldsDifferent() {
    assertNotSame(FieldArrayAtIndexDragModel.getInstance(createField("a", String[].class)), FieldArrayAtIndexDragModel.getInstance(createField("b", String[].class)));
  }

  @Test public void fieldArrayAtIndex_hierarchy() {
    assertTrue(FieldArrayAtIndexDragModel.getInstance(createField("arrayField", String[].class)) instanceof AbstractExpressionDragModel);
  }

  @Test public void fieldArrayLength_sameFieldSingleton() {
    UserField field = createField("arrayField", String[].class);
    assertSame(FieldArrayLengthDragModel.getInstance(field), FieldArrayLengthDragModel.getInstance(field));
  }

  @Test public void fieldArrayLength_differentFieldsDifferent() {
    assertNotSame(FieldArrayLengthDragModel.getInstance(createField("a", String[].class)), FieldArrayLengthDragModel.getInstance(createField("b", String[].class)));
  }

  @Test public void fieldArrayLength_hierarchy() {
    assertTrue(FieldArrayLengthDragModel.getInstance(createField("arrayField", String[].class)) instanceof AbstractExpressionDragModel);
  }
}
