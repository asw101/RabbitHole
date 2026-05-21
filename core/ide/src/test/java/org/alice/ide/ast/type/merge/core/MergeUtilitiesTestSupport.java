package org.alice.ide.ast.type.merge.core;

import org.lgna.project.ast.AbstractField;
import org.lgna.project.ast.AbstractMethod;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.ManagementLevel;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

final class MergeUtilitiesTestSupport {
  private MergeUtilitiesTestSupport() {
  }

  static NamedUserType namedType(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    type.superType.setValue(JavaType.getInstance(Object.class));
    return type;
  }

  static UserMethod addMethod(NamedUserType type, String name) {
    UserMethod method = new UserMethod();
    method.name.setValue(name);
    method.returnType.setValue(JavaType.VOID_TYPE);
    method.managementLevel.setValue(ManagementLevel.NONE);
    method.body.setValue(new BlockStatement());
    type.methods.add(method);
    return method;
  }

  static UserField addField(NamedUserType type, String name, AbstractType<?, ?, ?> valueType) {
    UserField field = new UserField();
    field.name.setValue(name);
    field.valueType.setValue(valueType);
    field.initializer.setValue(new NullLiteral());
    type.fields.add(field);
    return field;
  }

  static AbstractMethod invokeFindMethodMatch(AbstractType<?, ?, ?> type, AbstractMethod original) throws Exception {
    Method method = MergeUtilities.class.getDeclaredMethod("findMatch", AbstractType.class, AbstractMethod.class);
    method.setAccessible(true);
    return (AbstractMethod) method.invoke(null, type, original);
  }

  static AbstractField invokeFindFieldMatch(AbstractType<?, ?, ?> type, AbstractField original) throws Exception {
    Method method = MergeUtilities.class.getDeclaredMethod("findMatch", AbstractType.class, AbstractField.class);
    method.setAccessible(true);
    return (AbstractField) method.invoke(null, type, original);
  }

  static boolean invokeIsAcceptableType(AbstractType<?, ?, ?> declaringType, List<NamedUserType> types) throws Exception {
    Method method = MergeUtilities.class.getDeclaredMethod("isAcceptableType", AbstractType.class, List.class);
    method.setAccessible(true);
    return (Boolean) method.invoke(null, declaringType, types);
  }

  static MethodInvocation invocation(AbstractMethod method) {
    return new MethodInvocation(new ThisExpression(), method);
  }

  static FieldAccess access(AbstractField field) {
    return new FieldAccess(new ThisExpression(), field);
  }

  static List<NamedUserType> list(NamedUserType... types) {
    List<NamedUserType> list = new ArrayList<>();
    java.util.Collections.addAll(list, types);
    return list;
  }

  static JavaMethod objectToString() {
    return JavaMethod.getInstance(Object.class, "toString");
  }
}
