package org.alice.ide.typemanager;

import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.NamedUserType;

import java.lang.reflect.Method;
import java.util.List;

final class TypeManagerTestSupport {
  private TypeManagerTestSupport() {
  }

  static NamedUserType invokeCreateTypeFor(AbstractType<?, ?, ?> superType, String typeName, AbstractType<?, ?, ?>[] parameterTypes, Expression[] argumentExpressions) throws Exception {
    Method method = TypeManager.class.getDeclaredMethod("createTypeFor", AbstractType.class, String.class, AbstractType[].class, Expression[].class);
    method.setAccessible(true);
    return (NamedUserType) method.invoke(null, superType, typeName, parameterTypes, argumentExpressions);
  }

  @SuppressWarnings("unchecked")
  static List<AbstractType<?, ?, ?>> invokeUpdateArgumentTypes(List<AbstractType<?, ?, ?>> list, AbstractType<?, ?, ?> rootArgumentType, AbstractType<?, ?, ?> argumentType) throws Exception {
    Method method = TypeManager.class.getDeclaredMethod("updateArgumentTypes", List.class, AbstractType.class, AbstractType.class);
    method.setAccessible(true);
    return (List<AbstractType<?, ?, ?>>) method.invoke(null, list, rootArgumentType, argumentType);
  }

  static AbstractType<?, ?, ?>[] invokeGetArgumentTypes(AbstractType<?, ?, ?> ancestorType, AbstractType<?, ?, ?> resourceType) throws Exception {
    Method method = TypeManager.class.getDeclaredMethod("getArgumentTypes", AbstractType.class, AbstractType.class);
    method.setAccessible(true);
    return (AbstractType<?, ?, ?>[]) method.invoke(null, ancestorType, resourceType);
  }
}
