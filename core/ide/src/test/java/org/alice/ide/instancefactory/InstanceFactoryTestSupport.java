package org.alice.ide.instancefactory;

import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserLocal;
import org.lgna.project.ast.UserParameter;

final class InstanceFactoryTestSupport {
  private InstanceFactoryTestSupport() {
    throw new AssertionError();
  }

  static UserField createField(String name, AbstractType<?, ?, ?> valueType) {
    UserField field = new UserField();
    field.name.setValue(name);
    field.valueType.setValue(valueType);
    return field;
  }

  static UserParameter createParameter(String name, AbstractType<?, ?, ?> valueType) {
    return new UserParameter(name, valueType);
  }

  static UserLocal createLocal(String name, AbstractType<?, ?, ?> valueType) {
    return new UserLocal(name, valueType, false);
  }

  static UserField createStringField(String name) {
    return createField(name, JavaType.STRING_TYPE);
  }
}
