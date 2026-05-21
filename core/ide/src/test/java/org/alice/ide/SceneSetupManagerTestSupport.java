package org.alice.ide;

import org.lgna.project.ast.Expression;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.UserField;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

final class SceneSetupManagerTestSupport {
  private SceneSetupManagerTestSupport() {
  }

  static NamedUserType createType(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    type.superType.setValue(JavaType.getInstance(Object.class));
    return type;
  }

  static UserField addField(NamedUserType type, String name) {
    UserField field = new UserField();
    field.name.setValue(name);
    field.valueType.setValue(JavaType.getInstance(Object.class));
    field.initializer.setValue(new NullLiteral());
    type.fields.add(field);
    return field;
  }

  static void setInitializer(UserField field, Expression expression) {
    field.initializer.setValue(expression);
  }

  static FieldAccess access(UserField field) {
    return new FieldAccess(new ThisExpression(), field);
  }

  static MethodInvocation wrappedAccess(UserField field) {
    return new MethodInvocation(access(field), JavaMethod.getInstance(Object.class, "toString"));
  }

  static String reorganize(NamedUserType type) throws Exception {
    return reorganize(type, 0, new HashSet<>());
  }

  static String reorganize(NamedUserType type, int startIndex, Set<UserField> alreadyMoved) throws Exception {
    Constructor<SceneSetupManager> ctor = SceneSetupManager.class.getDeclaredConstructor(IDE.class);
    ctor.setAccessible(true);
    SceneSetupManager manager = ctor.newInstance((IDE) null);
    Method method = SceneSetupManager.class.getDeclaredMethod("reorganizeTypeFieldsIfNecessary", NamedUserType.class, int.class, Set.class);
    method.setAccessible(true);
    return (String) method.invoke(manager, type, startIndex, alreadyMoved);
  }
}
