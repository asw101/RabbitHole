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

import org.apache.commons.text.StringEscapeUtils;
import org.lgna.project.code.CodeOrganizer;
import org.lgna.project.code.ProcessableNode;

import java.util.*;

public abstract class SourceCodeGenerator implements AstProcessor {

  private final ExpressionCodeEmitter expressionEmitter = new ExpressionCodeEmitter(this);
  private final StatementCodeEmitter statementEmitter = new StatementCodeEmitter(this);
  private final StringBuilder codeStringBuilder = new StringBuilder();
  private final Map<String, CodeOrganizer.CodeOrganizerDefinition> codeOrganizerDefinitions;
  private final CodeOrganizer.CodeOrganizerDefinition defaultCodeOrganizerDefn;
  private int statementDisabledCount = 0;

  public SourceCodeGenerator(Map<String, CodeOrganizer.CodeOrganizerDefinition> codeOrganizerDefinitionMap, CodeOrganizer.CodeOrganizerDefinition defaultCodeDefinitionOrganizer) {
    codeOrganizerDefinitions = Collections.unmodifiableMap(codeOrganizerDefinitionMap);
    defaultCodeOrganizerDefn = defaultCodeDefinitionOrganizer;
  }

  public String getText() { return String.valueOf(getCodeStringBuilder()); }

  protected StringBuilder getCodeStringBuilder() { return codeStringBuilder; }

  // ** Class structure **

  @Override
  public CodeOrganizer getNewCodeOrganizerForTypeName(String typeName) {
    return new CodeOrganizer(codeOrganizerDefinitions.getOrDefault(typeName, defaultCodeOrganizerDefn));
  }

  @Override
  public void processClass(CodeOrganizer codeOrganizer, NamedUserType userType) {
    appendClassHeader(userType);
    for (Map.Entry<String, List<ProcessableNode>> entry : codeOrganizer.getOrderedSections().entrySet()) {
      if (!entry.getValue().isEmpty()) {
        appendSection(codeOrganizer, userType, entry);
      }
    }
    appendClassFooter(userType.getName());
  }

  protected void appendSection(CodeOrganizer codeOrganizer, NamedUserType userType, Map.Entry<String, List<ProcessableNode>> entry) {
    for (ProcessableNode item : entry.getValue()) {
      item.process(this);
    }
  }

  protected abstract void appendClassHeader(NamedUserType userType);

  protected void appendClassFooter(String userTypeName) { closeBlock(); }

  // ** Methods and Fields — delegated to StatementCodeEmitter **

  @Override
  public void processMethod(UserMethod method) { statementEmitter.processMethod(method); }
  public abstract void appendMethodHeader(AbstractMethod method);

  protected void appendParameters(Code code) {
    parenthesize(() -> statementEmitter.appendParameterList(code.getRequiredParameters()));
  }

  protected String getListSeparator() { return ","; }

  @Override
  public void processGetter(Getter getter) { statementEmitter.processGetter(getter); }
  @Override
  public void processIndexedGetter(ArrayItemGetter getter) { statementEmitter.processIndexedGetter(getter); }
  @Override
  public void processSetter(Setter setter) { statementEmitter.processSetter(setter); }
  @Override
  public void processIndexedSetter(ArrayItemSetter setter) { statementEmitter.processIndexedSetter(setter); }
  @Override
  public void processField(UserField field) { statementEmitter.processField(field); }

  // ** Statements — delegated to StatementCodeEmitter **

  protected final void appendStatement(Statement stmt) {
    boolean isDisabled = !stmt.isEnabled.getValue();
    if (isDisabled) {
      pushStatementDisabled();
    }
    try {
      stmt.process(this);
    } finally {
      if (isDisabled) {
        popStatementDisabled();
      }
    }
  }

  @Override
  public void processExpressionStatement(ExpressionStatement stmt) { statementEmitter.processExpressionStatement(stmt); }
  @Override
  public void processReturnStatement(ReturnStatement stmt) { statementEmitter.processReturnStatement(stmt); }
  @Override
  public void processBlock(BlockStatement blockStatement) { statementEmitter.processBlock(blockStatement); }
  @Override
  public void processConstructorBlock(ConstructorBlockStatement constructor) { statementEmitter.processConstructorBlock(constructor); }
  @Override
  public void processThisConstructor(ThisConstructorInvocationStatement thisCon) { statementEmitter.processThisConstructor(thisCon); }

  void appendArguments(ArgumentOwner argumentOwner) {
    parenthesize(() -> appendEachArgument(argumentOwner));
  }

  protected void appendEachArgument(ArgumentOwner argumentOwner) {
    String prefix = "";
    for (SimpleArgument argument : argumentOwner.getRequiredArgumentsProperty()) {
      appendString(prefix);
      statementEmitter.pushAndAppendArgument(argument);
      prefix = getListSeparator();
    }
    for (SimpleArgument argument : argumentOwner.getVariableArgumentsProperty()) {
      appendString(prefix);
      statementEmitter.pushAndAppendArgument(argument);
      prefix = getListSeparator();
    }
    for (JavaKeyedArgument argument : argumentOwner.getKeyedArgumentsProperty()) {
      appendString(prefix);
      appendArgument(argument);
      prefix = getListSeparator();
    }
  }

  protected void appendArgument(JavaKeyedArgument arg) { statementEmitter.pushAndAppendArgument(arg); }

  public abstract void processArgument(AbstractParameter parameter, AbstractArgument argument);

  public void processSingleStatement(Statement stmt, Runnable appender) {
    appender.run();
    appendStatementCompletion(stmt);
  }

  protected void appendSingleCodeLine(Runnable appender) {
    appender.run();
    appendStatementCompletion();
  }

  protected void appendStatementCompletion(Statement stmt) { appendChar(';'); }
  protected void appendStatementCompletion() { appendChar(';'); }

  protected void pushStatementDisabled() { ++statementDisabledCount; }
  protected void popStatementDisabled() { --statementDisabledCount; }
  boolean isCodeNowDisabled() { return statementDisabledCount == 1; }
  boolean isCodeNowEnabled() { return statementDisabledCount == 0; }

  // ** Code Flow — delegated to StatementCodeEmitter **

  protected void appendCodeFlowStatement(Statement stmt, Runnable appender) { appender.run(); }

  @Override
  public void processConditional(ConditionalStatement stmt) { statementEmitter.processConditional(stmt); }
  @Override
  public void processForEach(AbstractForEachLoop loop) { statementEmitter.processForEach(loop); }

  protected abstract void appendForEachToken();

  protected void appendEachItemsClause(UserLocal itemValue, Expression items) {
    parenthesize(() -> {
      processTypeName(itemValue.getValueType());
      appendSpace();
      appendString(identifierName(itemValue));
      appendInEachToken();
      processExpression(items);
    });
  }

  protected abstract void appendInEachToken();

  @Override
  public void processWhileLoop(WhileLoop loop) { statementEmitter.processWhileLoop(loop); }
  @Override
  public void processLambda(UserLambda lambda) { statementEmitter.processLambda(lambda); }

  boolean isLambdaSupported() { return true; }

  // ** Expressions — delegated to ExpressionCodeEmitter **

  @Override
  public void processExpression(Expression expression) { expressionEmitter.processExpression(expression); }
  @Override
  public void processMethodCall(MethodInvocation invocation) { expressionEmitter.processMethodCall(invocation); }

  protected void appendTargetAndMethodName(Expression target, AbstractMethod method) {
    appendTargetAndMember(target, method.getName(), method.getReturnType());
  }

  protected void appendTargetAndMember(Expression target, String member, AbstractType<?, ?, ?> returnType) {
    processExpression(target);
    appendAccessSeparator();
    appendString(member);
  }

  @Override
  public void processAssignmentExpression(AssignmentExpression assignment) { expressionEmitter.processAssignmentExpression(assignment); }
  @Override
  public void processConcatenation(StringConcatenation concat) { expressionEmitter.processConcatenation(concat); }

  protected abstract void appendConcatenationOperator();

  @Override
  public void processLogicalComplement(LogicalComplement complement) { expressionEmitter.processLogicalComplement(complement); }
  @Override
  public void processInfixExpression(InfixExpression infixExpression) { expressionEmitter.processInfixExpression(infixExpression); }
  @Override
  public void processInstantiation(InstanceCreation creation) { expressionEmitter.processInstantiation(creation); }
  @Override
  public void processArrayInstantiation(ArrayInstanceCreation creation) { expressionEmitter.processArrayInstantiation(creation); }
  @Override
  public void processArrayAccess(ArrayAccess access) { expressionEmitter.processArrayAccess(access); }
  @Override
  public void processArrayLength(ArrayLength arrayLength) { expressionEmitter.processArrayLength(arrayLength); }
  @Override
  public void processFieldAccess(FieldAccess access) { expressionEmitter.processFieldAccess(access); }

  // ** Comments **

  @Override
  public void processMultiLineComment(String comment) { statementEmitter.processMultiLineComment(comment); }

  protected void appendSingleLineComment(String line) {
    appendString("// ");
    appendString(line);
    appendNewLine();
  }

  static String[] splitIntoLines(String src) { return src.split("\n"); }

  public abstract String getLocalizedComment(AbstractType<?, ?, ?> type, String itemName, Locale locale);

  // ** Primitives and syntax **

  @Override
  public void processNull() { appendString("null"); }
  @Override
  public void processThisReference() { appendString("this"); }
  @Override
  public void processSuperReference() { appendString("super"); }
  @Override
  public void processBoolean(boolean b) { codeStringBuilder.append(b); }
  @Override
  public void processInt(int n) { expressionEmitter.processInt(n); }
  @Override
  public void processFloat(float f) { expressionEmitter.processFloat(f); }
  @Override
  public void processDouble(double d) { expressionEmitter.processDouble(d); }

  @Override
  public void processEscapedStringLiteral(StringLiteral literal) { appendEscapedString(literal.value.getValue()); }

  protected void appendEscapedString(String value) {
    appendChar('"');
    appendString(StringEscapeUtils.escapeJava(value));
    appendChar('"');
  }

  protected void appendChar(char c) { codeStringBuilder.append(c); }
  protected void appendSpace() { appendChar(' '); }
  protected void appendAccessSeparator() { appendChar('.'); }
  protected void appendString(String s) { codeStringBuilder.append(s); }
  protected void appendNewLine() { appendChar('\n'); }

  protected void parenthesize(Runnable appender) {
    appendChar('(');
    appender.run();
    appendChar(')');
  }

  protected void openBlock() { appendChar('{'); }
  protected void closeBlock() { closeBlockInline(); }
  protected void closeBlockInline() { appendChar('}'); }

  protected void bracketize(Runnable appender) {
    openBlock();
    appender.run();
    closeBlock();
  }

  protected abstract void appendAssignmentOperator();

  @Override
  public void processVariableIdentifier(AbstractDeclaration variable) { appendString(identifierName(variable)); }

  protected String identifierName(AbstractDeclaration variable) { return variable.getValidName(); }

  @Override
  public void processTypeLiteral(TypeLiteral typeLiteral) { expressionEmitter.processTypeLiteral(typeLiteral); }

}
