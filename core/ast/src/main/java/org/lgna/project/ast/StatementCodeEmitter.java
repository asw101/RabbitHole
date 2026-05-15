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

import java.util.Stack;

/**
 * Package-private delegate handling statement processing, code-flow constructs,
 * member emission, and lambda support for {@link SourceCodeGenerator}.
 */
class StatementCodeEmitter {

  private final SourceCodeGenerator gen;
  private final Stack<AbstractType<?, ?, ?>> typeForLambdaStack = new Stack<>();

  StatementCodeEmitter(SourceCodeGenerator generator) {
    this.gen = generator;
  }

  // -- Statements ---------------------------------------------------------

  void processExpressionStatement(ExpressionStatement stmt) {
    gen.processSingleStatement(stmt, () -> gen.processExpression(stmt.expression.getValue()));
  }

  void processReturnStatement(ReturnStatement stmt) {
    gen.processSingleStatement(stmt, () -> {
      gen.appendString("return ");
      gen.processExpression(stmt.expression.getValue());
    });
  }

  void processBlock(BlockStatement blockStatement) {
    gen.openBlock();
    appendBody(blockStatement);
    gen.closeBlockInline();
  }

  void processConstructorBlock(ConstructorBlockStatement constructor) {
    gen.openBlock();
    gen.appendStatement(constructor.constructorInvocationStatement.getValue());
    appendBody(constructor);
    gen.closeBlock();
  }

  private void appendBody(BlockStatement block) {
    for (Statement statement : block.statements) {
      gen.appendStatement(statement);
    }
  }

  void processThisConstructor(ThisConstructorInvocationStatement thisCon) {
    gen.processSingleStatement(thisCon, () -> {
      gen.processThisReference();
      gen.appendArguments(thisCon);
    });
  }

  // -- Code flow ----------------------------------------------------------

  void processConditional(ConditionalStatement stmt) {
    gen.appendCodeFlowStatement(stmt, () -> {
      String text = "if";
      for (BooleanExpressionBodyPair booleanExpressionBodyPair : stmt.booleanExpressionBodyPairs) {
        gen.appendString(text);
        gen.parenthesize(() -> gen.processExpression(booleanExpressionBodyPair.expression.getValue()));
        gen.appendStatement(booleanExpressionBodyPair.body.getValue());
        text = " else if";
      }
      gen.appendString(" else");
      gen.appendStatement(stmt.elseBody.getValue());
    });
  }

  void processForEach(AbstractForEachLoop loop) {
    gen.appendCodeFlowStatement(loop, () -> {
      UserLocal itemValue = loop.item.getValue();
      loop.repairStaleGeneratedItemName();
      final Expression items = loop.getArrayOrIterableProperty().getValue();
      gen.appendForEachToken();
      gen.appendEachItemsClause(itemValue, items);
      gen.appendStatement(loop.body.getValue());
    });
  }

  void processWhileLoop(WhileLoop loop) {
    gen.appendCodeFlowStatement(loop, () -> {
      gen.appendString("while ");
      gen.parenthesize(() -> gen.processExpression(loop.conditional.getValue()));
      gen.appendStatement(loop.body.getValue());
    });
  }

  // -- Lambda support -----------------------------------------------------

  void pushTypeForLambda(AbstractType<?, ?, ?> type) {
    typeForLambdaStack.push(type);
  }

  AbstractType<?, ?, ?> popTypeForLambda() {
    return typeForLambdaStack.pop();
  }

  void processLambda(UserLambda lambda) {
    AbstractType<?, ?, ?> type = typeForLambdaStack.peek();
    AbstractMethod singleAbstractMethod = AstMethodLookupHelpers.getSingleAbstractMethod(type);
    if (gen.isLambdaSupported()) {
      gen.appendParameters(lambda);
      gen.appendString(" ->");
    } else {
      gen.appendString("new ");
      gen.processTypeName(type);
      gen.appendString("()");
      gen.openBlock();
      gen.appendMethodHeader(singleAbstractMethod);
    }
    gen.appendStatement(lambda.body.getValue());
    if (!gen.isLambdaSupported()) {
      gen.closeBlock();
    }
  }

  // -- Arguments ----------------------------------------------------------

  void pushAndAppendArgument(AbstractArgument argument) {
    AbstractParameter parameter = argument.parameter.getValue();
    AbstractType<?, ?, ?> type = argument.getExpressionTypeForParameterType(parameter.getValueType());
    pushTypeForLambda(type);
    try {
      gen.processArgument(parameter, argument);
    } finally {
      assert popTypeForLambda() == type;
    }
  }

  // -- Members ------------------------------------------------------------

  void processMethod(UserMethod method) {
    gen.appendMethodHeader(method);
    gen.appendStatement(method.body.getValue());
  }

  void processGetter(Getter getter) {
    UserField field = getter.getField();
    gen.appendMethodHeader(getter);
    gen.openBlock();
    gen.appendSingleCodeLine(() -> {
      gen.appendString("return this.");
      gen.processVariableIdentifier(field);
    });
    gen.closeBlock();
  }

  void processIndexedGetter(ArrayItemGetter getter) {
    UserField field = getter.getField();
    gen.appendMethodHeader(getter);
    gen.openBlock();
    gen.appendSingleCodeLine(() -> {
      gen.appendString("return this.");
      gen.processVariableIdentifier(field);
      gen.appendString("[index]");
    });
    gen.closeBlock();
  }

  void processSetter(Setter setter) {
    UserField field = setter.getField();
    gen.appendMethodHeader(setter);
    gen.openBlock();
    gen.appendSingleCodeLine(() -> {
      gen.appendString("this.");
      gen.processVariableIdentifier(field);
      gen.appendAssignmentOperator();
      gen.processVariableIdentifier(field);
    });
    gen.closeBlock();
  }

  void processIndexedSetter(ArrayItemSetter setter) {
    UserField field = setter.getField();
    gen.appendMethodHeader(setter);
    gen.openBlock();
    gen.appendSingleCodeLine(() -> {
      gen.appendString("this.");
      gen.processVariableIdentifier(field);
      gen.appendString("[index]");
      gen.appendAssignmentOperator();
      gen.appendString("value");
    });
    gen.closeBlock();
  }

  void processField(UserField field) {
    gen.appendSingleCodeLine(() -> {
      gen.processTypeName(field.valueType.getValue());
      gen.appendSpace();
      gen.processVariableIdentifier(field);
      gen.appendAssignmentOperator();
      gen.processExpression(field.initializer.getValue());
    });
  }

  // -- Parameters ---------------------------------------------------------

  void appendParameterList(java.util.List<? extends AbstractParameter> requiredParameters) {
    String prefix = "";
    int i = 0;
    for (AbstractParameter parameter : requiredParameters) {
      gen.appendString(prefix);
      gen.processTypeName(parameter.getValueType());
      gen.appendSpace();
      String parameterName = gen.identifierName(parameter);
      gen.appendString(parameterName != null ? parameterName : "p" + i);
      prefix = gen.getListSeparator();
      i += 1;
    }
  }

  // -- Comments -----------------------------------------------------------

  void processMultiLineComment(String comment) {
    for (String line : SourceCodeGenerator.splitIntoLines(comment)) {
      gen.appendSingleLineComment(line);
    }
  }
}
