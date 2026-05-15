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

import edu.cmu.cs.dennisc.java.lang.ArrayUtilities;
import edu.cmu.cs.dennisc.java.lang.reflect.ReflectionUtilities;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

/**
 * Package-private delegate for expression and invocation creation,
 * extracted from {@link AstUtilities}.
 */
class ExpressionFactory {
  private ExpressionFactory() {
    throw new AssertionError();
  }

  static MethodInvocation createStaticMethodInvocation(AbstractMethod method, Expression... argumentExpressions) {
    return createMethodInvocation(new TypeExpression(method.getDeclaringType()), method, argumentExpressions);
  }

  static FieldAccess createStaticFieldAccess(AbstractField field) {
    assert field.isStatic();
    return new FieldAccess(new TypeExpression(field.getDeclaringType()), field);
  }

  static FieldAccess createStaticFieldAccess(Field fld) {
    return createStaticFieldAccess(JavaField.getInstance(fld));
  }

  static FieldAccess createStaticFieldAccess(Class<?> cls, String fieldName) {
    return createStaticFieldAccess(ReflectionUtilities.getDeclaredField(cls, fieldName));
  }

  static MethodInvocation createNextMethodInvocation(MethodInvocation prevMethodInvocation, Expression expression, AbstractMethod nextMethod) {
    MethodInvocation rv = new MethodInvocation();
    rv.expression.setValue(prevMethodInvocation.expression.getValue());
    rv.method.setValue(nextMethod);
    List<? extends AbstractParameter> parameters = nextMethod.getRequiredParameters();
    final int N = parameters.size();
    for (int i = 0; i < (N - 1); i++) {
      AbstractArgument argument = prevMethodInvocation.requiredArguments.get(i);
      if (argument instanceof SimpleArgument simpleArgument) {
        rv.requiredArguments.add(new SimpleArgument(parameters.get(i), simpleArgument.expression.getValue()));
      } else {
        throw new RuntimeException();
      }
    }
    rv.requiredArguments.add(new SimpleArgument(parameters.get(N - 1), expression));
    return rv;
  }

  static MethodInvocation completeMethodInvocation(MethodInvocation rv, Expression instanceExpression, Expression... argumentExpressions) {
    rv.expression.setValue(instanceExpression);
    int i = 0;
    for (AbstractArgument argument : rv.requiredArguments) {
      if (argument instanceof SimpleArgument simpleArgument) {
        simpleArgument.expression.setValue(argumentExpressions[i]);
      } else {
        throw new RuntimeException();
      }
      i++;
    }
    return rv;
  }

  static MethodInvocation createMethodInvocation(Expression instanceExpression, AbstractMethod method, Expression... argumentExpressions) {
    List<? extends AbstractParameter> requiredParameters = method.getRequiredParameters();
    assert requiredParameters.size() == argumentExpressions.length : method;

    MethodInvocation rv = new MethodInvocation();
    rv.expression.setValue(instanceExpression);
    rv.method.setValue(method);
    int i = 0;
    for (AbstractParameter parameter : requiredParameters) {
      SimpleArgument argument = new SimpleArgument(parameter, argumentExpressions[i]);
      rv.requiredArguments.add(argument);
      i++;
    }
    return rv;
  }

  static ExpressionStatement createMethodInvocationStatement(Expression instanceExpression, AbstractMethod method, Expression... argumentExpressions) {
    return new ExpressionStatement(createMethodInvocation(instanceExpression, method, argumentExpressions));
  }

  static TypeExpression createTypeExpression(AbstractType<?, ?, ?> type) {
    return new TypeExpression(type);
  }

  static TypeExpression createTypeExpression(Class<?> cls) {
    return createTypeExpression(JavaType.getInstance(cls));
  }

  static InstanceCreation createInstanceCreation(AbstractConstructor constructor, Expression... argumentExpressions) {
    InstanceCreation rv = new InstanceCreation(constructor);
    int i = 0;
    for (AbstractParameter parameter : constructor.getRequiredParameters()) {
      SimpleArgument argument = new SimpleArgument(parameter, argumentExpressions[i]);
      rv.requiredArguments.add(argument);
      i++;
    }
    return rv;
  }

  static InstanceCreation createInstanceCreation(AbstractType<?, ?, ?> type) {
    return createInstanceCreation(type.getDeclaredConstructor());
  }

  static InstanceCreation createInstanceCreation(Class<?> cls, Class<?>[] parameterClses, Expression... argumentExpressions) {
    return createInstanceCreation(JavaConstructor.getInstance(cls, parameterClses), argumentExpressions);
  }

  static InstanceCreation createInstanceCreation(Class<?> cls) {
    return createInstanceCreation(JavaType.getInstance(cls));
  }

  static ArrayInstanceCreation createArrayInstanceCreation(AbstractType<?, ?, ?> arrayType, Expression... expressions) {
    Integer[] lengths = {expressions.length};
    return new ArrayInstanceCreation(arrayType, lengths, expressions);
  }

  static ArrayInstanceCreation createArrayInstanceCreation(Class<?> arrayCls, Expression... expressions) {
    return createArrayInstanceCreation(JavaType.getInstance(arrayCls), expressions);
  }

  static ArrayInstanceCreation createArrayInstanceCreation(AbstractType<?, ?, ?> arrayType, Collection<Expression> expressions) {
    return createArrayInstanceCreation(arrayType, ArrayUtilities.createArray(expressions, Expression.class));
  }

  static ArrayInstanceCreation createArrayInstanceCreation(Class<?> arrayCls, Collection<Expression> expressions) {
    return createArrayInstanceCreation(JavaType.getInstance(arrayCls), ArrayUtilities.createArray(expressions, Expression.class));
  }

  static JavaMethod lookupMethod(Class<?> cls, String methodName, Class<?>... parameterTypes) {
    return JavaMethod.getInstance(cls, methodName, parameterTypes);
  }

  static ReturnStatement createReturnStatement(AbstractType<?, ?, ?> type, Expression expression) {
    return new ReturnStatement(type, expression);
  }

  static ReturnStatement createReturnStatement(Class<?> cls, Expression expression) {
    return createReturnStatement(JavaType.getInstance(cls), expression);
  }

  static StringConcatenation createStringConcatenation(Expression left, Expression right) {
    return new StringConcatenation(left, right);
  }

  static <M extends AbstractMethod> M getSingleAbstractMethod(AbstractType<?, M, ?> type) {
    List<M> methods = type.getDeclaredMethods();
    assert methods.size() == 1 : type;
    M singleAbstractMethod = methods.getFirst();
    assert singleAbstractMethod.isAbstract() : singleAbstractMethod;
    return singleAbstractMethod;
  }

  static UserLambda createUserLambda(AbstractType<?, ?, ?> type) {
    AbstractMethod singleAbstractMethod = getSingleAbstractMethod(type);
    List<? extends AbstractParameter> srcRequiredParameters = singleAbstractMethod.getRequiredParameters();
    UserParameter[] dstRequiredParameters = new UserParameter[srcRequiredParameters.size()];
    for (int i = 0; i < dstRequiredParameters.length; i++) {
      AbstractParameter srcRequiredParameter = srcRequiredParameters.get(i);
      String name = srcRequiredParameter.getName();
      if (name == null || name.isEmpty()) {
        name = "p" + i;
      }
      dstRequiredParameters[i] = new UserParameter(name, srcRequiredParameter.getValueType());
    }
    UserLambda rv = new UserLambda(singleAbstractMethod.getReturnType(), dstRequiredParameters, new BlockStatement());
    rv.isSignatureLocked.setValue(true);
    return rv;
  }

  static UserLambda createUserLambda(Class<?> cls) {
    return createUserLambda(JavaType.getInstance(cls));
  }

  static LambdaExpression createLambdaExpression(AbstractType<?, ?, ?> type) {
    return new LambdaExpression(createUserLambda(type));
  }

  static LambdaExpression createLambdaExpression(Class<?> cls) {
    return createLambdaExpression(JavaType.getInstance(cls));
  }
}
