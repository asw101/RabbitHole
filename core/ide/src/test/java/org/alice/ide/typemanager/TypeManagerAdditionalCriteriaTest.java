package org.alice.ide.typemanager;

import org.junit.Test;
import org.lgna.project.ast.AbstractField;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.ConstructorBlockStatement;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserConstructor;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.SimpleArgument;
import org.lgna.project.ast.StringLiteral;
import org.lgna.project.ast.SuperConstructorInvocationStatement;
import org.lgna.project.ast.UserField;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TypeManagerAdditionalCriteriaTest {
  private static Object createCriterion(String simpleName, Class<?>[] parameterTypes, Object... args) throws Exception {
    Class<?> cls = Class.forName("org.alice.ide.typemanager.TypeManager$" + simpleName);
    Constructor<?> constructor = cls.getDeclaredConstructor(parameterTypes);
    constructor.setAccessible(true);
    return constructor.newInstance(args);
  }

  private static boolean invokeAccept(Object criterion, NamedUserType type) throws Exception {
    Method accept = criterion.getClass().getDeclaredMethod("accept", NamedUserType.class);
    accept.setAccessible(true);
    return (Boolean) accept.invoke(criterion, type);
  }

  private static NamedUserType typeWithSuperArguments(AbstractType<?, ?, ?> superType, Expression... arguments) {
    NamedUserType type = new NamedUserType();
    type.name.setValue("GeneratedType");
    type.superType.setValue(superType);

    NamedUserConstructor constructor = new NamedUserConstructor();
    ConstructorBlockStatement body = new ConstructorBlockStatement();
    SuperConstructorInvocationStatement superInvocation = new SuperConstructorInvocationStatement();
    for (Expression argument : arguments) {
      superInvocation.requiredArguments.add(new SimpleArgument(null, argument));
    }
    body.constructorInvocationStatement.setValue(superInvocation);
    constructor.body.setValue(body);
    type.constructors.add(constructor);
    return type;
  }

  private static UserField createField(String name) {
    UserField field = new UserField();
    field.name.setValue(name);
    field.valueType.setValue(JavaType.STRING_TYPE);
    return field;
  }

  @Test
  public void superArgumentFieldCriterionMatchesExpectedFieldAccess() throws Exception {
    UserField matchingField = createField("match");
    UserField otherField = createField("other");
    Object criterion = createCriterion(
        "ExtendsTypeWithSuperArgumentFieldCriterion",
        new Class<?>[] {AbstractType.class, AbstractField.class},
        JavaType.getInstance(Object.class),
        matchingField);

    assertTrue(invokeAccept(criterion, typeWithSuperArguments(JavaType.getInstance(Object.class), new FieldAccess(matchingField))));
    assertFalse(invokeAccept(criterion, typeWithSuperArguments(JavaType.getInstance(Object.class), new FieldAccess(otherField))));
  }

  @Test
  public void superArgumentExpressionsCriterionMatchesEquivalentExpressionsOnly() throws Exception {
    Expression[] expressions = {new StringLiteral("alpha"), new IntegerLiteral(7)};
    Object criterion = createCriterion(
        "ExtendsTypeWithSuperArgumentExpressionsCriterion",
        new Class<?>[] {AbstractType.class, Expression[].class},
        JavaType.getInstance(Object.class),
        expressions);

    assertTrue(invokeAccept(criterion, typeWithSuperArguments(JavaType.getInstance(Object.class), new StringLiteral("alpha"), new IntegerLiteral(7))));
    assertFalse(invokeAccept(criterion, typeWithSuperArguments(JavaType.getInstance(Object.class), new StringLiteral("beta"), new IntegerLiteral(7))));
  }
}
