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

/**
 * Package-private delegate for assignment expression creation,
 * extracted from {@link AstUtilities}.
 */
class AssignmentFactory {
  private AssignmentFactory() {
    throw new AssertionError();
  }

  static AssignmentExpression createFieldAssignment(Expression expression, UserField field, Expression valueExpression) {
    assert field.isFinal() == false : field;
    Expression fieldAccess = new FieldAccess(expression, field);
    return new AssignmentExpression(field.valueType.getValue(), fieldAccess, AssignmentExpression.Operator.ASSIGN, valueExpression);
  }

  static ExpressionStatement createFieldAssignmentStatement(Expression expression, UserField field, Expression valueExpression) {
    return new ExpressionStatement(createFieldAssignment(expression, field, valueExpression));
  }

  static AssignmentExpression createFieldAssignment(UserField field, Expression valueExpression) {
    return createFieldAssignment(new ThisExpression(), field, valueExpression);
  }

  static ExpressionStatement createFieldAssignmentStatement(UserField field, Expression valueExpression) {
    return new ExpressionStatement(createFieldAssignment(field, valueExpression));
  }

  static AssignmentExpression createFieldArrayAssignment(Expression expression, UserField field, Expression indexExpression, Expression valueExpression) {
    Expression fieldAccess = new FieldAccess(expression, field);
    ArrayAccess arrayAccess = new ArrayAccess(field.valueType.getValue(), fieldAccess, indexExpression);
    return new AssignmentExpression(field.valueType.getValue().getComponentType(), arrayAccess, AssignmentExpression.Operator.ASSIGN, valueExpression);
  }

  static ExpressionStatement createFieldArrayAssignmentStatement(Expression expression, UserField field, Expression indexExpression, Expression valueExpression) {
    return new ExpressionStatement(createFieldArrayAssignment(expression, field, indexExpression, valueExpression));
  }

  static AssignmentExpression createFieldArrayAssignment(UserField field, Expression indexExpression, Expression valueExpression) {
    return createFieldArrayAssignment(new ThisExpression(), field, indexExpression, valueExpression);
  }

  static ExpressionStatement createFieldArrayAssignmentStatement(UserField field, Expression indexExpression, Expression valueExpression) {
    return new ExpressionStatement(createFieldArrayAssignment(field, indexExpression, valueExpression));
  }

  static AssignmentExpression createLocalAssignment(UserLocal local, Expression valueExpression) {
    assert local.isFinal.getValue() == false : local;
    Expression localAccess = new LocalAccess(local);
    return new AssignmentExpression(local.valueType.getValue(), localAccess, AssignmentExpression.Operator.ASSIGN, valueExpression);
  }

  static ExpressionStatement createLocalAssignmentStatement(UserLocal local, Expression valueExpression) {
    return new ExpressionStatement(createLocalAssignment(local, valueExpression));
  }

  static AssignmentExpression createLocalArrayAssignment(UserLocal local, Expression indexExpression, Expression valueExpression) {
    Expression localAccess = new LocalAccess(local);
    ArrayAccess arrayAccess = new ArrayAccess(local.valueType.getValue(), localAccess, indexExpression);
    return new AssignmentExpression(local.valueType.getValue().getComponentType(), arrayAccess, AssignmentExpression.Operator.ASSIGN, valueExpression);
  }

  static ExpressionStatement createLocalArrayAssignmentStatement(UserLocal local, Expression indexExpression, Expression valueExpression) {
    return new ExpressionStatement(createLocalArrayAssignment(local, indexExpression, valueExpression));
  }

  static AssignmentExpression createParameterArrayAssignment(UserParameter parameter, Expression indexExpression, Expression valueExpression) {
    Expression parameterAccess = new ParameterAccess(parameter);
    ArrayAccess arrayAccess = new ArrayAccess(parameter.valueType.getValue(), parameterAccess, indexExpression);
    return new AssignmentExpression(parameter.valueType.getValue().getComponentType(), arrayAccess, AssignmentExpression.Operator.ASSIGN, valueExpression);
  }

  static ExpressionStatement createParameterArrayAssignmentStatement(UserParameter parameter, Expression indexExpression, Expression valueExpression) {
    return new ExpressionStatement(createParameterArrayAssignment(parameter, indexExpression, valueExpression));
  }
}
