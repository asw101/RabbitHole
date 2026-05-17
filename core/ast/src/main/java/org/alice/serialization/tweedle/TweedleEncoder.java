package org.alice.serialization.tweedle;

import org.alice.math.immutable.AffineMatrix4x4;
import org.lgna.project.annotations.FieldTemplate;
import org.lgna.project.ast.*;
import org.lgna.project.code.CodeOrganizer;
import org.lgna.project.code.InstantiableTweedleNode;
import org.lgna.project.code.ProcessableNode;

import java.util.*;
import java.util.function.Consumer;

public class TweedleEncoder extends SourceCodeGenerator {
  static final String NODE_DISABLE = "*<";
  static final String NODE_ENABLE = ">*";
  static final String USER_PREFIX = "u_";
  private final Set<AbstractDeclaration> terminalNodes;
  private final StatementEncoder statementEncoder = new StatementEncoder(this);
  private final ResourceEncoder resourceEncoder = new ResourceEncoder(this);
  private final FormattingEncoder formattingEncoder = new FormattingEncoder(this);
  private final ExpressionEncoder expressionEncoder = new ExpressionEncoder(this);
  private final ArgumentEncoder argumentEncoder = new ArgumentEncoder(this);

  TweedleEncoder(Set<AbstractDeclaration> terminals) {
    super(TweedleEncoderData.codeOrganizerDefinitionMap, CodeOrganizer.defaultCodeOrganizer);
    terminalNodes = terminals;
  }
  TweedleEncoder() {
    super(TweedleEncoderData.codeOrganizerDefinitionMap, CodeOrganizer.defaultCodeOrganizer);
    terminalNodes = new HashSet<>();
  }

  public String encode(ProcessableNode node) {
    node.process(this);
    return getCodeStringBuilder().toString();
  }

  /** Class structure **/
  @Override
  public void processResourceType(String jointedModelResource) {
    resourceEncoder.processResourceType(jointedModelResource);
  }
  @Override
  public void processDynamicResource(String dynamicResourceClass, String variant, InstantiableTweedleNode[] addedJoints) {
    resourceEncoder.processDynamicResource(dynamicResourceClass, variant, addedJoints);
  }
  public String getUserJointIdentifier(String jointIdentifier) {
    return resourceEncoder.getUserJointIdentifier(jointIdentifier);
  }
  public void appendNewJointId(String joint, String parentReference) {
    resourceEncoder.appendNewJointId(joint, parentReference);
  }
  public void appendNewJointArrayId(String pattern, String startingJoint) {
    resourceEncoder.appendNewJointArrayId(pattern, startingJoint);
  }
  public String getFieldReference(String type, String field) {
    return resourceEncoder.getFieldReference(type, field);
  }
  public void appendNewPose(InstantiableTweedleNode[] jointTransformations) {
    resourceEncoder.appendNewPose(jointTransformations);
  }
  public void appendNewJointTransformation(String jointId, AffineMatrix4x4 transformation) {
    resourceEncoder.appendNewJointTransformation(jointId, transformation);
  }

  @Override
  protected void appendClassHeader(NamedUserType userType) {
    getCodeStringBuilder().append("class ").append(tweedleTypeName(userType.getName())).append(" extends ").append(userType.getSuperType().getName());
    // TODO Only show for models and replace with resource identifier
    //    if (userType.isModel())
    getCodeStringBuilder().append(" models ").append(tweedleTypeName(userType.getName()));
    openBlock();
  }
  @Override
  protected void appendClassFooter(String userTypeName) {
    appendString(TweedleEncoderData.typesWithAddedCode.getOrDefault(userTypeName, ""));
    super.appendClassFooter(userTypeName);
  }

  /** Methods and Fields **/
  @Override
  public void processConstructor(NamedUserConstructor constructor) {
    appendIndent();
    processTypeName(constructor.getDeclaringType());
    appendParameters(constructor);
    appendStatement(constructor.body.getValue());
  }
  @Override
  public void processSuperConstructor(SuperConstructorInvocationStatement supCon) {
    processSingleStatement(supCon, () -> {
      processSuperReference();
      parenthesize(() -> appendEachArgument(supCon));
    });
  }
  @Override
  public void processMethod(UserMethod method) {
    super.processMethod(method);
    appendNewLine();
  }
  @Override
  public void appendMethodHeader(AbstractMethod method) {
    appendNewLine();
    appendIndent();
    if (method.isStatic()) {
      appendString("static ");
    }
    processTypeName(method.getReturnType());
    appendSpace();
    appendString(method.getName());
    appendParameters(method);
  }
  @Override
  public void processInstantiation(InstanceCreation creation) {
    expressionEncoder.processInstantiation(creation);
  }

  /** Statements **/
  @Override
  public void processLocalDeclaration(LocalDeclarationStatement stmt) {
    processSingleStatement(stmt, () -> {
      UserLocal localVar = stmt.local.getValue();
      processTypeName(localVar.getValueType());
      appendSpace();
      processVariableIdentifier(localVar);
      appendAssignmentOperator();
      processExpression(stmt.initializer.getValue());
    });
  }
  @Override
  protected void appendStatementCompletion(Statement stmt) {
    statementEncoder.appendStatementCompletion(stmt);
  }
  @Override
  protected void appendStatementCompletion() {
    statementEncoder.appendStatementCompletion();
  }
  @Override
  protected void pushStatementDisabled() {
    statementEncoder.pushStatementDisabled();
  }

  // Bridge methods for StatementEncoder
  void superAppendStatementCompletion(Statement stmt) {
    super.appendStatementCompletion(stmt);
  }
  void superAppendStatementCompletion() {
    super.appendStatementCompletion();
  }
  void superPushStatementDisabled() {
    super.pushStatementDisabled();
  }
  void forwardAppendString(String s) {
    appendString(s);
  }
  void forwardAppendSpace() {
    appendSpace();
  }
  void forwardAppendNewLine() {
    appendNewLine();
  }

  // Bridge methods for ResourceEncoder
  StringBuilder forwardGetCodeStringBuilder() {
    return getCodeStringBuilder();
  }
  void forwardBracketize(Runnable appender) {
    bracketize(appender);
  }
  void forwardAppendEscapedString(String value) {
    appendEscapedString(value);
  }

  // Bridge methods for FormattingEncoder
  void forwardAppendChar(char c) {
    appendChar(c);
  }
  void forwardParenthesize(Runnable appender) {
    parenthesize(appender);
  }
  String forwardGetListSeparator() {
    return getListSeparator();
  }

  // Bridge methods for ExpressionEncoder
  void superProcessInstantiation(InstanceCreation creation) {
    super.processInstantiation(creation);
  }
  void forwardAppendAccessSeparator() {
    appendAccessSeparator();
  }

  // Bridge methods for ArgumentEncoder
  String forwardIdentifierName(AbstractDeclaration decl) {
    return identifierName(decl);
  }

  @Override
  protected void appendArgument(JavaKeyedArgument arg) {
    argumentEncoder.processKeyedArgument(arg);
  }
  @Override
  public void processKeyedArgument(JavaKeyedArgument arg) {
    argumentEncoder.processKeyedArgument(arg);
  }
  @Override
  public void processArgument(AbstractParameter parameter, AbstractArgument argument) {
    argumentEncoder.processArgument(parameter, argument);
  }
  @Override
  public void processSingleStatement(Statement stmt, Runnable appender) {
    formattingEncoder.appendIndent(stmt);
    super.processSingleStatement(stmt, appender);
  }
  @Override
  protected void appendSingleCodeLine(Runnable appender) {
    appendIndent();
    super.appendSingleCodeLine(appender);
  }

  /** Code Flow **/
  @Override
  protected void appendCodeFlowStatement(Statement stmt, Runnable appender) {
    formattingEncoder.appendIndent(stmt);
    statementEncoder.appendCodeFlowStatement(stmt, appender);
  }
  @Override
  public void processCountLoop(CountLoop loop) {
    appendCodeFlowStatement(loop, () -> {
      appendString("countUpTo( ");
      appendString(loop.getVariableName());
      appendString(" < ");
      processExpression(loop.count.getValue());
      appendString(" )");
      appendStatement(loop.body.getValue());
    });
  }
  @Override
  protected void appendForEachToken() {
    appendString("forEach");
  }
  @Override
  protected void appendInEachToken() {
    appendString(" in ");
  }
  @Override
  public void processDoInOrder(DoInOrder doInOrder) {
    appendLabeledCodeFlow(doInOrder, "doInOrder");
  }
  @Override
  public void processDoTogether(DoTogether doTogether) {
    appendLabeledCodeFlow(doTogether, "doTogether");
  }
  private void appendLabeledCodeFlow(AbstractStatementWithBody stmt, String label) {
    appendCodeFlowStatement(stmt, () -> {
      appendString(label);
      appendStatement(stmt.body.getValue());
    });
  }
  @Override
  public void processEachInTogether(AbstractEachInTogether eachInTogether) {
    appendCodeFlowStatement(eachInTogether, () -> {
      appendString("eachTogether");
      UserLocal itemValue = eachInTogether.item.getValue();
      Expression items = eachInTogether.getArrayOrIterableProperty().getValue();
      appendEachItemsClause(itemValue, items);
      appendStatement(eachInTogether.body.getValue());
    });
  }

  /** Expressions **/
  @Override
  protected void appendTargetAndMember(Expression target, String member, AbstractType<?, ?, ?> returnType) {
    expressionEncoder.appendTargetAndMember(target, member, returnType);
  }
  @Override
  public void processResourceExpression(ResourceExpression resourceExpression) {
    expressionEncoder.processResourceExpression(resourceExpression);
  }

  /** Comments **/
  @Override
  protected void appendSingleLineComment(String line) {
    appendIndent();
    super.appendSingleLineComment(line);
  }
  @Override
  public String getLocalizedComment(AbstractType<?, ?, ?> type, String itemName, Locale locale) {
    return "//";
  }

  /** Primitives and syntax **/
  @Override
  protected void openBlock() {
    appendString(" {\n");
    formattingEncoder.pushIndent();
  }
  @Override
  protected void closeBlockInline() {
    formattingEncoder.popIndent();
    appendIndent();
    super.closeBlockInline();
  }
  @Override
  protected void closeBlock() {
    super.closeBlock();
    appendNewLine();
  }
  @Override
  protected void appendAssignmentOperator() {
    appendString(" <- ");
  }
  @Override
  protected String identifierName(AbstractDeclaration variable) {
    final String varName = super.identifierName(variable);
    if (variable.isUserAuthored() && !TweedleEncoderData.systemIdentifiers.contains(varName)) {
      return USER_PREFIX + varName;
    } else {
      return varName;
    }
  }
  @Override
  protected void appendConcatenationOperator() {
    appendString(" .. ");
  }
  @Override
  public void processTypeName(AbstractType<?, ?, ?> type) {
    appendString(type == null ? "MISSING_TYPE" : tweedleTypeName(type.getName()));
  }
  String tweedleTypeName(String typeName) {
    return TweedleEncoderData.typesToRename.getOrDefault(typeName, typeName);
  }
  @Override
  protected String getListSeparator() {
    return ", ";
  }

  /** Formatting and helper bridges (delegated to FormattingEncoder) **/
  void appendVisibilityTag(FieldTemplate fieldAnnotation) {
    formattingEncoder.appendVisibilityTag(fieldAnnotation);
  }
  void appendInstantiation(String className, Runnable args) {
    formattingEncoder.appendInstantiation(className, args);
  }
  void appendArg(String label, String value) {
    formattingEncoder.appendArg(label, value);
  }
  void appendArg(String label, Runnable value) {
    formattingEncoder.appendArg(label, value);
  }
  void appendAnotherArg(String label, String value) {
    formattingEncoder.appendAnotherArg(label, value);
  }
  void appendAnotherArg(String label, Runnable value) {
    formattingEncoder.appendAnotherArg(label, value);
  }
  <T> void appendList(T[] values, Consumer<T> appendValue, String separator) {
    formattingEncoder.appendList(values, appendValue, separator);
  }
  void quoteString(String aString) {
    formattingEncoder.quoteString(aString);
  }
  void appendIndent() {
    formattingEncoder.appendIndent();
  }
}
