/*******************************************************************************
 * Copyright (c) 2006, 2015, Carnegie Mellon University. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Products derived from the software may not be called "Alice", nor may
 *    "Alice" appear in their name, without prior written permission of
 *    Carnegie Mellon University.
 *
 * 4. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgement: "This product includes software
 *    developed by Carnegie Mellon University"
 *
 * 5. The gallery of art assets and animations provided with this software is
 *    contributed by Electronic Arts Inc. and may be used for personal,
 *    non-commercial, and academic use only. Redistributions of any program
 *    source code that utilizes The Sims 2 Assets must also retain the copyright
 *    notice, list of conditions and the disclaimer contained in
 *    The Alice 3.0 Art Gallery License.
 *
 * DISCLAIMER:
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.
 * ANY AND ALL EXPRESS, STATUTORY OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,  FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, AND NON-INFRINGEMENT ARE DISCLAIMED. IN NO EVENT
 * SHALL THE AUTHORS, COPYRIGHT OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, PUNITIVE OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING FROM OR OTHERWISE RELATING TO
 * THE USE OF OR OTHER DEALINGS WITH THE SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/

package org.lgna.project.ast;

import edu.cmu.cs.dennisc.java.util.Lists;
import edu.cmu.cs.dennisc.java.util.logging.Logger;
import edu.cmu.cs.dennisc.property.PropertyUtilities;
import org.alice.serialization.xml.XmlEncoderDecoder;
import org.lgna.project.VersionNotSupportedException;
import org.lgna.project.annotations.AddEventListenerTemplate;
import org.lgna.project.annotations.GetterTemplate;
import org.lgna.project.code.ProcessableNode;
import org.w3c.dom.Document;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Dennis Cosgrove
 */
public class AstUtilities {
  private AstUtilities() {
    throw new AssertionError();
  }

  public static <N extends AbstractNode & ProcessableNode> N createCopy(N original, NamedUserType root) {
    Set<AbstractDeclaration> abstractDeclarations;
    if (root != null) {
      abstractDeclarations = root.createDeclarationSet();
      original.removeDeclarationsThatNeedToBeCopied(abstractDeclarations);
    } else {
      abstractDeclarations = Collections.emptySet();
    }
    XmlEncoderDecoder coder = new XmlEncoderDecoder();
    Document xmlDocument = coder.encode(original, abstractDeclarations);
    try {
      AbstractNode dst = coder.copy(xmlDocument, abstractDeclarations);
      Logger.todo("check copy", dst);
      return (N) dst;
    } catch (VersionNotSupportedException vnse) {
      throw new AssertionError(vnse);
    }
  }

  public static boolean isKeywordExpression(Expression expression) {
    if (expression != null) {
      return expression.getParent() instanceof JavaKeyedArgument;
    } else {
      return false;
    }
  }

  public static Expression getJavaKeyedArgumentSubArgument0Expression(JavaKeyedArgument argument) {
    Expression expresssion = argument.expression.getValue();
    if (expresssion instanceof MethodInvocation methodInvocation) {
      if (methodInvocation.requiredArguments.size() > 0) {
        return methodInvocation.requiredArguments.get(0).expression.getValue();
      } else {
        throw new RuntimeException();
      }
    } else {
      throw new RuntimeException();
    }
  }

  private static List<JavaMethod> updatePersistentPropertyGetters(List<JavaMethod> rv, JavaType javaType) {
    for (JavaMethod method : javaType.getDeclaredMethods()) {
      java.lang.reflect.Method mthd = method.getMethodReflectionProxy().getReification();
      if (mthd != null) {
        if (mthd.isAnnotationPresent(GetterTemplate.class)) {
          GetterTemplate gttrTemplate = mthd.getAnnotation(GetterTemplate.class);
          if (gttrTemplate.isPersistent()) {
            rv.add(method);
          }
        }
      }
    }
    return rv;
  }

  public static Iterable<JavaMethod> getDeclaredPersistentPropertyGetters(JavaType javaType) {
    List<JavaMethod> rv = Lists.newLinkedList();
    updatePersistentPropertyGetters(rv, javaType);
    return rv;
  }

  public static Iterable<JavaMethod> getPersistentPropertyGetters(AbstractType<?, ?, ?> type) {
    List<JavaMethod> rv = Lists.newLinkedList();
    JavaType javaType = type.getFirstEncounteredJavaType();
    while (true) {
      if (javaType != null) {
        updatePersistentPropertyGetters(rv, javaType);
        if (!javaType.isFollowToSuperClassDesired()) {
          break;
        }
        javaType = javaType.getSuperType();
      } else {
        Logger.severe(type);
        break;
      }
    }
    return rv;
  }

  public static JavaMethod getSetterForGetter(JavaMethod getter, JavaType type) {
    java.lang.reflect.Method gttr = getter.getMethodReflectionProxy().getReification();
    java.lang.reflect.Method sttr = PropertyUtilities.getSetterForGetter(gttr, type.getClassReflectionProxy().getReification());
    if (sttr != null) {
      return JavaMethod.getInstance(sttr);
    } else {
      return null;
    }
  }

  public static JavaMethod getSetterForGetter(JavaMethod getter) {
    return getSetterForGetter(getter, getter.getDeclaringType());
  }

  public static UserMethod createMethod(String name, AbstractType<?, ?, ?> returnType) {
    return new UserMethod(name, returnType, new UserParameter[] {}, new BlockStatement());
  }

  public static UserMethod createFunction(String name, AbstractType<?, ?, ?> returnType) {
    return createMethod(name, returnType);
  }

  public static UserMethod createFunction(String name, Class<?> returnCls) {
    return createMethod(name, JavaType.getInstance(returnCls));
  }

  public static UserMethod createProcedure(String name) {
    return createMethod(name, JavaType.VOID_TYPE);
  }

  public static NamedUserType createType(String name, AbstractType<?, ?, ?> superType) {
    NamedUserConstructor constructor = new NamedUserConstructor();
    constructor.body.setValue(new ConstructorBlockStatement());
    NamedUserType rv = new NamedUserType();
    rv.name.setValue(name);
    rv.superType.setValue(superType);
    rv.constructors.add(constructor);
    return rv;
  }

  public static DoInOrder createDoInOrder() { return new DoInOrder(new BlockStatement()); }
  public static DoTogether createDoTogether() { return new DoTogether(new BlockStatement()); }
  public static Comment createComment() { return new Comment(); }

  public static LocalDeclarationStatement createLocalDeclarationStatement(UserLocal local, Expression initializerExpression) {
    return new LocalDeclarationStatement(local, initializerExpression);
  }

  public static CountLoop createCountLoop(Expression count) {
    return new CountLoop(new UserLocal(null, JavaType.INTEGER_OBJECT_TYPE, false), new UserLocal(null, JavaType.INTEGER_OBJECT_TYPE, true), count, new BlockStatement());
  }

  public static WhileLoop createWhileLoop(Expression conditional) {
    return new WhileLoop(conditional, new BlockStatement());
  }

  public static ConditionalStatement createConditionalStatement(Expression conditional) {
    return new ConditionalStatement(new BooleanExpressionBodyPair[] {new BooleanExpressionBodyPair(conditional, new BlockStatement())}, new BlockStatement());
  }

  public static ForEachInArrayLoop createForEachInArrayLoop(Expression arrayExpression) {
    UserLocal item = new UserLocal(null, arrayExpression.getType().getComponentType(), true);
    return new ForEachInArrayLoop(item, arrayExpression, new BlockStatement());
  }

  public static EachInArrayTogether createEachInArrayTogether(Expression arrayExpression) {
    UserLocal item = new UserLocal(null, arrayExpression.getType().getComponentType(), true);
    return new EachInArrayTogether(item, arrayExpression, new BlockStatement());
  }

  public static MethodInvocation createStaticMethodInvocation(AbstractMethod method, Expression... argumentExpressions) {
    return ExpressionFactory.createStaticMethodInvocation(method, argumentExpressions);
  }
  public static FieldAccess createStaticFieldAccess(AbstractField field) {
    return ExpressionFactory.createStaticFieldAccess(field);
  }
  public static FieldAccess createStaticFieldAccess(Field fld) {
    return ExpressionFactory.createStaticFieldAccess(fld);
  }
  public static FieldAccess createStaticFieldAccess(Class<?> cls, String fieldName) {
    return ExpressionFactory.createStaticFieldAccess(cls, fieldName);
  }
  public static MethodInvocation createNextMethodInvocation(MethodInvocation prevMethodInvocation, Expression expression, AbstractMethod nextMethod) {
    return ExpressionFactory.createNextMethodInvocation(prevMethodInvocation, expression, nextMethod);
  }
  public static MethodInvocation completeMethodInvocation(MethodInvocation rv, Expression instanceExpression, Expression... argumentExpressions) {
    return ExpressionFactory.completeMethodInvocation(rv, instanceExpression, argumentExpressions);
  }
  public static MethodInvocation createMethodInvocation(Expression instanceExpression, AbstractMethod method, Expression... argumentExpressions) {
    return ExpressionFactory.createMethodInvocation(instanceExpression, method, argumentExpressions);
  }
  public static ExpressionStatement createMethodInvocationStatement(Expression instanceExpression, AbstractMethod method, Expression... argumentExpressions) {
    return ExpressionFactory.createMethodInvocationStatement(instanceExpression, method, argumentExpressions);
  }
  public static TypeExpression createTypeExpression(AbstractType<?, ?, ?> type) {
    return ExpressionFactory.createTypeExpression(type);
  }
  public static TypeExpression createTypeExpression(Class<?> cls) {
    return ExpressionFactory.createTypeExpression(cls);
  }
  public static InstanceCreation createInstanceCreation(AbstractConstructor constructor, Expression... argumentExpressions) {
    return ExpressionFactory.createInstanceCreation(constructor, argumentExpressions);
  }
  public static InstanceCreation createInstanceCreation(AbstractType<?, ?, ?> type) {
    return ExpressionFactory.createInstanceCreation(type);
  }
  public static InstanceCreation createInstanceCreation(Class<?> cls, Class<?>[] parameterClses, Expression... argumentExpressions) {
    return ExpressionFactory.createInstanceCreation(cls, parameterClses, argumentExpressions);
  }
  public static InstanceCreation createInstanceCreation(Class<?> cls) {
    return ExpressionFactory.createInstanceCreation(cls);
  }
  public static ArrayInstanceCreation createArrayInstanceCreation(AbstractType<?, ?, ?> arrayType, Expression... expressions) {
    return ExpressionFactory.createArrayInstanceCreation(arrayType, expressions);
  }
  public static ArrayInstanceCreation createArrayInstanceCreation(Class<?> arrayCls, Expression... expressions) {
    return ExpressionFactory.createArrayInstanceCreation(arrayCls, expressions);
  }
  public static ArrayInstanceCreation createArrayInstanceCreation(AbstractType<?, ?, ?> arrayType, Collection<Expression> expressions) {
    return ExpressionFactory.createArrayInstanceCreation(arrayType, expressions);
  }
  public static ArrayInstanceCreation createArrayInstanceCreation(Class<?> arrayCls, Collection<Expression> expressions) {
    return ExpressionFactory.createArrayInstanceCreation(arrayCls, expressions);
  }
  public static JavaMethod lookupMethod(Class<?> cls, String methodName, Class<?>... parameterTypes) {
    return ExpressionFactory.lookupMethod(cls, methodName, parameterTypes);
  }
  public static ReturnStatement createReturnStatement(AbstractType<?, ?, ?> type, Expression expression) {
    return ExpressionFactory.createReturnStatement(type, expression);
  }
  public static ReturnStatement createReturnStatement(Class<?> cls, Expression expression) {
    return ExpressionFactory.createReturnStatement(cls, expression);
  }
  public static StringConcatenation createStringConcatenation(Expression left, Expression right) {
    return ExpressionFactory.createStringConcatenation(left, right);
  }
  public static UserLambda createUserLambda(AbstractType<?, ?, ?> type) {
    return ExpressionFactory.createUserLambda(type);
  }
  public static UserLambda createUserLambda(Class<?> cls) {
    return ExpressionFactory.createUserLambda(cls);
  }
  public static LambdaExpression createLambdaExpression(AbstractType<?, ?, ?> type) {
    return ExpressionFactory.createLambdaExpression(type);
  }
  public static LambdaExpression createLambdaExpression(Class<?> cls) {
    return ExpressionFactory.createLambdaExpression(cls);
  }

  public static <M extends AbstractMethod> M getSingleAbstractMethod(AbstractType<?, M, ?> type) {
    return ExpressionFactory.getSingleAbstractMethod(type);
  }

  public static AssignmentExpression createFieldAssignment(Expression expression, UserField field, Expression valueExpression) {
    return AssignmentFactory.createFieldAssignment(expression, field, valueExpression);
  }
  public static ExpressionStatement createFieldAssignmentStatement(Expression expression, UserField field, Expression valueExpression) {
    return AssignmentFactory.createFieldAssignmentStatement(expression, field, valueExpression);
  }
  public static AssignmentExpression createFieldAssignment(UserField field, Expression valueExpression) {
    return AssignmentFactory.createFieldAssignment(field, valueExpression);
  }
  public static ExpressionStatement createFieldAssignmentStatement(UserField field, Expression valueExpression) {
    return AssignmentFactory.createFieldAssignmentStatement(field, valueExpression);
  }
  public static AssignmentExpression createFieldArrayAssignment(Expression expression, UserField field, Expression indexExpression, Expression valueExpression) {
    return AssignmentFactory.createFieldArrayAssignment(expression, field, indexExpression, valueExpression);
  }
  public static ExpressionStatement createFieldArrayAssignmentStatement(Expression expression, UserField field, Expression indexExpression, Expression valueExpression) {
    return AssignmentFactory.createFieldArrayAssignmentStatement(expression, field, indexExpression, valueExpression);
  }
  public static AssignmentExpression createFieldArrayAssignment(UserField field, Expression indexExpression, Expression valueExpression) {
    return AssignmentFactory.createFieldArrayAssignment(field, indexExpression, valueExpression);
  }
  public static ExpressionStatement createFieldArrayAssignmentStatement(UserField field, Expression indexExpression, Expression valueExpression) {
    return AssignmentFactory.createFieldArrayAssignmentStatement(field, indexExpression, valueExpression);
  }
  public static AssignmentExpression createLocalAssignment(UserLocal local, Expression valueExpression) {
    return AssignmentFactory.createLocalAssignment(local, valueExpression);
  }
  public static ExpressionStatement createLocalAssignmentStatement(UserLocal local, Expression valueExpression) {
    return AssignmentFactory.createLocalAssignmentStatement(local, valueExpression);
  }
  public static AssignmentExpression createLocalArrayAssignment(UserLocal local, Expression indexExpression, Expression valueExpression) {
    return AssignmentFactory.createLocalArrayAssignment(local, indexExpression, valueExpression);
  }
  public static ExpressionStatement createLocalArrayAssignmentStatement(UserLocal local, Expression indexExpression, Expression valueExpression) {
    return AssignmentFactory.createLocalArrayAssignmentStatement(local, indexExpression, valueExpression);
  }
  public static AssignmentExpression createParameterArrayAssignment(UserParameter parameter, Expression indexExpression, Expression valueExpression) {
    return AssignmentFactory.createParameterArrayAssignment(parameter, indexExpression, valueExpression);
  }
  public static ExpressionStatement createParameterArrayAssignmentStatement(UserParameter parameter, Expression indexExpression, Expression valueExpression) {
    return AssignmentFactory.createParameterArrayAssignmentStatement(parameter, indexExpression, valueExpression);
  }

  public static Map<SimpleArgumentListProperty, SimpleArgument> removeParameter(Map<SimpleArgumentListProperty, SimpleArgument> rv, NodeListProperty<UserParameter> parametersProperty, UserParameter userParameter, int index, List<SimpleArgumentListProperty> argumentListProperties) {
    assert rv != null;
    assert parametersProperty.get(index) == userParameter;
    rv.clear();
    parametersProperty.remove(index);
    for (SimpleArgumentListProperty argumentListProperty : argumentListProperties) {
      SimpleArgument argument = argumentListProperty.remove(index);
      if (argument != null) {
        rv.put(argumentListProperty, argument);
      }
    }
    return rv;
  }

  public static void addParameter(Map<SimpleArgumentListProperty, SimpleArgument> map, NodeListProperty<UserParameter> parametersProperty, UserParameter userParameter, int index, List<SimpleArgumentListProperty> argumentListProperties) {
    parametersProperty.add(index, userParameter);
    for (SimpleArgumentListProperty argumentListProperty : argumentListProperties) {
      SimpleArgument argument = map.get(argumentListProperty);
      if (argument == null) {
        Logger.todo("argument == null");
        argument = new SimpleArgument(userParameter, new NullLiteral());
      }
      argumentListProperty.add(index, argument);
    }
  }

  public static AbstractType<?, ?, ?>[] getParameterValueTypes(AbstractMethod method) {
    return TypeAnalysisHelper.getParameterValueTypes(method);
  }

  public static boolean isAddEventListenerMethodInvocationStatement(Statement statement) {
    if (statement instanceof ExpressionStatement expressionStatement) {
      Expression expression = expressionStatement.expression.getValue();
      if (expression instanceof MethodInvocation methodInvocation) {
        AbstractMethod method = methodInvocation.method.getValue();
        if (method instanceof JavaMethod javaMethod) {
          return javaMethod.isAnnotationPresent(AddEventListenerTemplate.class);
        }
      }
    }
    return false;
  }

  public static AbstractType<?, ?, ?> getKeywordFactoryType(JavaKeyedArgument argument) {
    AbstractParameter parameter = argument.parameter.getValue();
    if (parameter.isKeyworded()) {
      AbstractType<?, ?, ?> parameterType = parameter.getValueType();
      if ((parameterType != null) && parameterType.isArray()) {
        AbstractType<?, ?, ?> componentType = parameterType.getComponentType();
        if (componentType != null) {
          return componentType.getKeywordFactoryType();
        }
      }
    }
    return null;
  }

  public static AbstractMethod getOverridenMethod(AbstractMethod method) {
    return TypeAnalysisHelper.getOverridenMethod(method);
  }
  public static Set<UserMethod> getAllInvokedMethods(UserMethod seed) {
    return TypeAnalysisHelper.getAllInvokedMethods(seed);
  }

  public static void fixRequiredArgumentsIfNecessary(MethodInvocation methodInvocation) {
    TypeAnalysisHelper.fixRequiredArgumentsIfNecessary(methodInvocation);
  }

  public static Collection<NamedUserType> getNamedUserTypes(Node node) {
    return TypeAnalysisHelper.getNamedUserTypes(node);
  }

  public static AbstractType<?, ?, ?> getDeclaringTypeIfMemberOrTypeItselfIfType(AbstractDeclaration declaration) {
    return TypeAnalysisHelper.getDeclaringTypeIfMemberOrTypeItselfIfType(declaration);
  }

  public static List<AbstractMethod> getAllMethods(AbstractType<?, ?, ?> type) {
    return TypeAnalysisHelper.getAllMethods(type);
  }
}
