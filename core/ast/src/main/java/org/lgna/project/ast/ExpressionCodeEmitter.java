/*******************************************************************************
 * Copyright (c) 2018 Carnegie Mellon University. All rights reserved.
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

import edu.cmu.cs.dennisc.java.util.logging.Logger;
import org.lgna.project.code.PrecedentedOperation;

import java.util.Stack;

/**
 * Package-private delegate handling expression processing, literal formatting,
 * and operator-precedence parenthesization for {@link SourceCodeGenerator}.
 */
class ExpressionCodeEmitter {

  private final SourceCodeGenerator gen;
  private final Stack<PrecedentedOperation> operatorStack = new Stack<>();

  ExpressionCodeEmitter(SourceCodeGenerator generator) {
    this.gen = generator;
  }

  // -- Expressions --------------------------------------------------------

  void processExpression(Expression expression) {
    if (expression == null) {
      gen.processNull();
    } else {
      expression.process(gen);
    }
  }

  void processMethodCall(MethodInvocation invocation) {
    gen.appendTargetAndMethodName(invocation.expression.getValue(), invocation.method.getValue());
    gen.appendArguments(invocation);
  }

  void processAssignmentExpression(AssignmentExpression assignment) {
    appendPrecedented(assignment, () -> {
      gen.processExpression(assignment.leftHandSide.getValue());
      final AssignmentExpression.Operator assignmentOp = assignment.operator.getValue();
      if (AssignmentExpression.Operator.ASSIGN.equals(assignmentOp)) {
        gen.appendAssignmentOperator();
      } else {
        Logger.errln("Use of unexpected assignment operator " + assignmentOp + " in " + assignment);
        gen.appendString(assignmentOp.toString());
      }
      gen.processExpression(assignment.rightHandSide.getValue());
    });
  }

  void processConcatenation(StringConcatenation concat) {
    appendPrecedented(concat, () -> {
      gen.processExpression(concat.leftOperand.getValue());
      gen.appendConcatenationOperator();
      gen.processExpression(concat.rightOperand.getValue());
    });
  }

  void processLogicalComplement(LogicalComplement complement) {
    appendPrecedented(complement, () -> {
      gen.appendChar('!');
      gen.processExpression(complement.operand.getValue());
    });
  }

  void processInfixExpression(InfixExpression infixExpression) {
    appendPrecedented(infixExpression, () -> {
      gen.processExpression(infixExpression.leftOperand.getValue());
      gen.appendString(infixExpression.getOperatorValue().getSymbol());
      gen.processExpression(infixExpression.rightOperand.getValue());
    });
  }

  void processInstantiation(InstanceCreation creation) {
    appendPrecedented(creation, () -> {
      gen.appendString("new ");
      AbstractType<?, ?, ?> type = creation.getType();
      if (null == type) {
        type = ((UserField) creation.getParent()).valueType.getValue();
      }
      gen.processTypeName(type);
      gen.appendArguments(creation);
    });
  }

  void processArrayInstantiation(ArrayInstanceCreation creation) {
    appendPrecedented(creation, () -> {
      gen.appendString("new ");
      gen.processTypeName(creation.arrayType.getValue().getComponentType());
      gen.appendChar('[');
      gen.appendChar(']');
      gen.appendChar('{');
      String prefix = "";
      for (Expression expression : creation.expressions) {
        gen.appendString(prefix);
        gen.processExpression(expression);
        prefix = ", ";
      }
      gen.appendChar('}');
    });
  }

  void processArrayAccess(ArrayAccess access) {
    pushPrecedented(access, () -> {
      gen.processExpression(access.array.getValue());
      gen.appendChar('[');
      gen.processExpression(access.index.getValue());
      gen.appendChar(']');
    });
  }

  void processArrayLength(ArrayLength arrayLength) {
    gen.processExpression(arrayLength.array.getValue());
    gen.appendString(".length");
  }

  void processFieldAccess(FieldAccess access) {
    pushPrecedented(access, () -> {
      gen.appendTargetAndMember(access.expression.getValue(), gen.identifierName(access.field.getValue()), null);
    });
  }

  // -- Literals -----------------------------------------------------------

  void processInt(int n) {
    if (n == Integer.MAX_VALUE) {
      gen.appendString("Integer.MAX_VALUE");
    } else if (n == Integer.MIN_VALUE) {
      gen.appendString("Integer.MIN_VALUE");
    } else {
      gen.getCodeStringBuilder().append(n);
    }
  }

  void processFloat(float f) {
    if (Float.isNaN(f)) {
      gen.appendString("Float.NaN");
    } else if (f == Float.POSITIVE_INFINITY) {
      gen.appendString("Float.POSITIVE_INFINITY");
    } else if (f == Float.NEGATIVE_INFINITY) {
      gen.appendString("Float.NEGATIVE_INFINITY");
    } else {
      gen.getCodeStringBuilder().append(f);
      gen.appendChar('f');
    }
  }

  void processDouble(double d) {
    if (Double.isNaN(d)) {
      gen.appendString("Double.NaN");
    } else if (d == Double.POSITIVE_INFINITY) {
      gen.appendString("Double.POSITIVE_INFINITY");
    } else if (d == Double.NEGATIVE_INFINITY) {
      gen.appendString("Double.NEGATIVE_INFINITY");
    } else {
      gen.getCodeStringBuilder().append(d);
    }
  }

  void processTypeLiteral(TypeLiteral typeLiteral) {
    gen.processTypeName(typeLiteral.value.getValue());
    gen.appendString(".class");
  }

  // -- Precedence ---------------------------------------------------------

  private void appendPrecedented(PrecedentedOperation expr, Runnable appender) {
    if (areParenthesesNeeded(expr)) {
      gen.parenthesize(() -> pushPrecedented(expr, appender));
    } else {
      pushPrecedented(expr, appender);
    }
  }

  private void pushPrecedented(PrecedentedOperation expr, Runnable appender) {
    operatorStack.push(expr);
    appender.run();
    PrecedentedOperation popped = operatorStack.pop();
    if (popped != expr) {
      Logger.errln("Unexpected expression on stack. These two should have been the same:", expr, popped);
    }
  }

  private boolean areParenthesesNeeded(PrecedentedOperation expr) {
    return !operatorStack.empty() && expr.getLevelOfPrecedence() <= operatorStack.peek().getLevelOfPrecedence();
  }
}
