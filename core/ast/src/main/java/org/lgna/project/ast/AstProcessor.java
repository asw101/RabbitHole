/*******************************************************************************
 * Copyright (c) 2019 Carnegie Mellon University. All rights reserved.
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

import org.lgna.project.code.CodeOrganizer;
import org.lgna.project.code.InstantiableTweedleNode;

/**
 * AstProcessor implementor work through some portion of the code stored in an AST.
 * Implementors may produce interim products and state as they require.
 * The ordering within an AST is to be defined by the dispatch through the nodes, and should be in the same order
 * as they would executed, meaning statements have a strict ordering,
 * but containing nodes,such as methods and type declarations do not.
 *
 */
public interface AstProcessor {
  CodeOrganizer getNewCodeOrganizerForTypeName(String typeName);

  // TODO move in use by NamedUserType and push down to JavaCodeGenerator
  default boolean isPublicStaticFinalFieldGetterDesired() {
    return true;
  }

  default void processClass(CodeOrganizer codeOrganizer, NamedUserType userType) { }

  default void processResourceType(String jointedModelResource) { }

  default void processDynamicResource(String jointedModelResource, String resourceName, InstantiableTweedleNode[] addedJoints) { }

  default void processConstructor(NamedUserConstructor constructor) { }

  default void processMethod(UserMethod method) { }

  default void processGetter(Getter getter) { }

  default void processIndexedGetter(ArrayItemGetter getter) { }

  default void processSetter(Setter setter) { }

  default void processIndexedSetter(ArrayItemSetter setter) { }

  default void processField(UserField field) { }

  default void processLocalDeclaration(LocalDeclarationStatement stmt) { }

  default void processExpressionStatement(ExpressionStatement stmt) { }

  default void processReturnStatement(ReturnStatement stmt) { }

  default void processBlock(BlockStatement blockStatement) { }

  default void processConstructorBlock(ConstructorBlockStatement constructor) { }

  default void processSuperConstructor(SuperConstructorInvocationStatement supCon) { }

  default void processThisConstructor(ThisConstructorInvocationStatement thisCon) { }

  default void processConditional(ConditionalStatement stmt) { }

  default void processCountLoop(CountLoop loop) { }

  default void processForEach(AbstractForEachLoop loop) { }

  default void processWhileLoop(WhileLoop loop) { }

  default void processDoInOrder(DoInOrder doInOrder) { }

  default void processDoTogether(DoTogether doTogether) { }

  default void processEachInTogether(AbstractEachInTogether eachInTogether) { }

  default void processLambda(UserLambda lambda) { }

  default void processExpression(Expression expression) { }

  default void processMethodCall(MethodInvocation invocation) { }

  default void processKeyedArgument(JavaKeyedArgument arg) { }

  default void processAssignmentExpression(AssignmentExpression assignment) { }

  default void processConcatenation(StringConcatenation concat) { }

  default void processLogicalComplement(LogicalComplement complement) { }

  default void processInfixExpression(InfixExpression infixExpression) { }

  default void processInstantiation(InstanceCreation creation) { }

  default void processArrayInstantiation(ArrayInstanceCreation creation) { }

  default void processArrayAccess(ArrayAccess access) { }

  default void processArrayLength(ArrayLength arrayLength) { }

  default void processFieldAccess(FieldAccess access) { }

  default void processNull() { }

  default void processThisReference() { }

  default void processSuperReference() { }

  default void processBoolean(boolean b) { }

  default void processInt(int n) { }

  default void processFloat(float f) { }

  default void processDouble(double d) { }

  default void processEscapedStringLiteral(StringLiteral literal) { }

  default void processTypeName(AbstractType<?, ?, ?> type) { }

  default void processTypeLiteral(TypeLiteral typeLiteral) { }

  default void processResourceExpression(ResourceExpression resourceExpression) { }

  default void processMultiLineComment(String comment) { }

  default void processVariableIdentifier(AbstractDeclaration variable) { }
}
