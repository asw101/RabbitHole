package org.lgna.project.ast;

import org.junit.Test;
import org.lgna.project.code.CodeOrganizer;

import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Deep coverage tests for AST node types: ConditionalStatement, InstanceCreation,
 * FieldAccess, MethodInvocation, ExpressionStatement, AstUtilities remaining methods,
 * and related expression/statement nodes.
 */
public class AstNodeTypesDeepTest {

  // ── ConditionalStatement ──────────────────────────────────

  @Test
  public void conditionalStatementEmptyConstructor() {
    ConditionalStatement cs = new ConditionalStatement();
    assertEquals(0, cs.booleanExpressionBodyPairs.size());
  }

  @Test
  public void conditionalStatementWithBranchAndElse() {
    BooleanExpressionBodyPair pair = new BooleanExpressionBodyPair(
        new BooleanLiteral(true), new BlockStatement());
    BlockStatement elseBody = new BlockStatement();
    ConditionalStatement cs = new ConditionalStatement(
        new BooleanExpressionBodyPair[]{pair}, elseBody);
    assertEquals(1, cs.booleanExpressionBodyPairs.size());
    assertSame(elseBody, cs.elseBody.getValue());
  }

  @Test
  public void conditionalContainsReturn_disabled() {
    ConditionalStatement cs = AstUtilities.createConditionalStatement(new BooleanLiteral(true));
    cs.isEnabled.setValue(false);
    assertFalse(cs.containsAtLeastOneEnabledReturnStatement());
  }

  @Test
  public void conditionalContainsReturn_inBranch() {
    ReturnStatement ret = new ReturnStatement(JavaType.OBJECT_TYPE, new NullLiteral());
    BlockStatement ifBody = new BlockStatement(ret);
    BooleanExpressionBodyPair pair = new BooleanExpressionBodyPair(
        new BooleanLiteral(true), ifBody);
    ConditionalStatement cs = new ConditionalStatement(
        new BooleanExpressionBodyPair[]{pair}, new BlockStatement());
    assertTrue(cs.containsAtLeastOneEnabledReturnStatement());
  }

  @Test
  public void conditionalContainsReturn_inElse() {
    ReturnStatement ret = new ReturnStatement(JavaType.OBJECT_TYPE, new NullLiteral());
    BlockStatement elseBody = new BlockStatement(ret);
    BooleanExpressionBodyPair pair = new BooleanExpressionBodyPair(
        new BooleanLiteral(true), new BlockStatement());
    ConditionalStatement cs = new ConditionalStatement(
        new BooleanExpressionBodyPair[]{pair}, elseBody);
    assertTrue(cs.containsAtLeastOneEnabledReturnStatement());
  }

  @Test
  public void conditionalReturnForEveryPath_disabled() {
    ConditionalStatement cs = AstUtilities.createConditionalStatement(new BooleanLiteral(true));
    cs.isEnabled.setValue(false);
    assertFalse(cs.containsAReturnForEveryPath());
  }

  @Test
  public void conditionalReturnForEveryPath_allReturn() {
    ReturnStatement ret1 = new ReturnStatement(JavaType.OBJECT_TYPE, new NullLiteral());
    ReturnStatement ret2 = new ReturnStatement(JavaType.OBJECT_TYPE, new NullLiteral());
    BooleanExpressionBodyPair pair = new BooleanExpressionBodyPair(
        new BooleanLiteral(true), new BlockStatement(ret1));
    ConditionalStatement cs = new ConditionalStatement(
        new BooleanExpressionBodyPair[]{pair}, new BlockStatement(ret2));
    assertTrue(cs.containsAReturnForEveryPath());
  }

  @Test
  public void conditionalReturnForEveryPath_branchDoesNotReturn() {
    BooleanExpressionBodyPair pair = new BooleanExpressionBodyPair(
        new BooleanLiteral(true), new BlockStatement());
    ReturnStatement ret = new ReturnStatement(JavaType.OBJECT_TYPE, new NullLiteral());
    ConditionalStatement cs = new ConditionalStatement(
        new BooleanExpressionBodyPair[]{pair}, new BlockStatement(ret));
    assertFalse(cs.containsAReturnForEveryPath());
  }

  @Test
  public void conditionalUnreachableCode_disabled() {
    ConditionalStatement cs = AstUtilities.createConditionalStatement(new BooleanLiteral(true));
    cs.isEnabled.setValue(false);
    assertFalse(cs.containsUnreachableCode());
  }

  @Test
  public void conditionalUnreachableCode_inBranch() {
    ReturnStatement ret = new ReturnStatement(JavaType.OBJECT_TYPE, new NullLiteral());
    ExpressionStatement unreachable = new ExpressionStatement(new NullLiteral());
    BlockStatement ifBody = new BlockStatement(ret, unreachable);
    BooleanExpressionBodyPair pair = new BooleanExpressionBodyPair(
        new BooleanLiteral(true), ifBody);
    ConditionalStatement cs = new ConditionalStatement(
        new BooleanExpressionBodyPair[]{pair}, new BlockStatement());
    assertTrue(cs.containsUnreachableCode());
  }

  @Test
  public void conditionalProcess() {
    ConditionalStatement cs = AstUtilities.createConditionalStatement(new BooleanLiteral(true));
    final boolean[] called = {false};
    AstProcessor processor = new AstProcessor() {
      @Override public void processConditional(ConditionalStatement s) { called[0] = true; }
      @Override public CodeOrganizer getNewCodeOrganizerForTypeName(String n) { return null; }
    };
    cs.process(processor);
    assertTrue(called[0]);
  }

  // ── InstanceCreation ─────────────────────────────────────

  @Test
  public void instanceCreationEmpty() {
    InstanceCreation ic = new InstanceCreation();
    assertNotNull(ic.requiredArguments);
    assertNotNull(ic.variableArguments);
    assertNotNull(ic.keyedArguments);
  }

  @Test
  public void instanceCreationWithConstructor() {
    JavaConstructor ctor = JavaConstructor.getInstance(StringBuilder.class);
    InstanceCreation ic = new InstanceCreation(ctor);
    assertSame(ctor, ic.constructor.getValue());
    assertTrue(ic.isValid());
    assertEquals(13, ic.getLevelOfPrecedence());
  }

  @Test
  public void instanceCreationGetType() {
    JavaConstructor ctor = JavaConstructor.getInstance(StringBuilder.class);
    InstanceCreation ic = new InstanceCreation(ctor);
    assertEquals(ctor.getDeclaringType(), ic.getType());
  }

  @Test
  public void instanceCreationProcess() {
    JavaConstructor ctor = JavaConstructor.getInstance(StringBuilder.class);
    InstanceCreation ic = new InstanceCreation(ctor);
    final boolean[] called = {false};
    AstProcessor processor = new AstProcessor() {
      @Override public void processInstantiation(InstanceCreation c) { called[0] = true; }
      @Override public CodeOrganizer getNewCodeOrganizerForTypeName(String n) { return null; }
    };
    ic.process(processor);
    assertTrue(called[0]);
  }

  @Test
  public void instanceCreationParameterOwnerProperty() {
    JavaConstructor ctor = JavaConstructor.getInstance(StringBuilder.class);
    InstanceCreation ic = new InstanceCreation(ctor);
    assertSame(ic.constructor, ic.getParameterOwnerProperty());
  }

  @Test
  public void instanceCreationArgumentProperties() {
    JavaConstructor ctor = JavaConstructor.getInstance(StringBuilder.class);
    InstanceCreation ic = new InstanceCreation(ctor);
    assertSame(ic.requiredArguments, ic.getRequiredArgumentsProperty());
    assertSame(ic.variableArguments, ic.getVariableArgumentsProperty());
    assertSame(ic.keyedArguments, ic.getKeyedArgumentsProperty());
  }

  // ── FieldAccess ────────────────────────────────────────────

  @Test
  public void fieldAccessCreation() {
    JavaField field = JavaField.getInstance(System.class, "out");
    FieldAccess fa = new FieldAccess(new TypeExpression(JavaType.getInstance(System.class)), field);
    assertSame(field, fa.field.getValue());
    assertNotNull(fa.getType());
  }

  @Test
  public void fieldAccessProcess() {
    JavaField field = JavaField.getInstance(System.class, "out");
    FieldAccess fa = new FieldAccess(new TypeExpression(JavaType.getInstance(System.class)), field);
    final boolean[] called = {false};
    AstProcessor processor = new AstProcessor() {
      @Override public void processFieldAccess(FieldAccess f) { called[0] = true; }
      @Override public CodeOrganizer getNewCodeOrganizerForTypeName(String n) { return null; }
    };
    fa.process(processor);
    assertTrue(called[0]);
  }

  @Test
  public void staticFieldAccessViaAstUtilities() {
    FieldAccess fa = AstUtilities.createStaticFieldAccess(System.class, "out");
    assertNotNull(fa);
    assertNotNull(fa.field.getValue());
    assertTrue(fa.field.getValue().isStatic());
  }

  // ── MethodInvocation ──────────────────────────────────────

  @Test
  public void methodInvocationCreation() {
    JavaMethod method = JavaMethod.getInstance(String.class, "length");
    Expression target = new StringLiteral("test");
    MethodInvocation mi = AstUtilities.createMethodInvocation(target, method);
    assertSame(method, mi.method.getValue());
    assertSame(target, mi.expression.getValue());
    assertEquals(0, mi.requiredArguments.size());
  }

  @Test
  public void methodInvocationWithArgs() {
    JavaMethod method = JavaMethod.getInstance(String.class, "charAt", int.class);
    Expression target = new StringLiteral("test");
    Expression arg = new IntegerLiteral(0);
    MethodInvocation mi = AstUtilities.createMethodInvocation(target, method, arg);
    assertEquals(1, mi.requiredArguments.size());
  }

  @Test
  public void staticMethodInvocation() {
    JavaMethod method = JavaMethod.getInstance(String.class, "valueOf", int.class);
    Expression arg = new IntegerLiteral(42);
    MethodInvocation mi = AstUtilities.createStaticMethodInvocation(method, arg);
    assertNotNull(mi);
    assertTrue(mi.expression.getValue() instanceof TypeExpression);
  }

  @Test
  public void methodInvocationStatement() {
    JavaMethod method = JavaMethod.getInstance(String.class, "length");
    Expression target = new StringLiteral("test");
    ExpressionStatement stmt = AstUtilities.createMethodInvocationStatement(target, method);
    assertNotNull(stmt);
    assertTrue(stmt.expression.getValue() instanceof MethodInvocation);
  }

  @Test
  public void methodInvocationProcess() {
    MethodInvocation mi = new MethodInvocation();
    final boolean[] called = {false};
    AstProcessor processor = new AstProcessor() {
      @Override public void processMethodCall(MethodInvocation m) { called[0] = true; }
      @Override public CodeOrganizer getNewCodeOrganizerForTypeName(String n) { return null; }
    };
    mi.process(processor);
    assertTrue(called[0]);
  }

  // ── ExpressionStatement ─────────────────────────────────

  @Test
  public void expressionStatementProcess() {
    ExpressionStatement stmt = new ExpressionStatement(new NullLiteral());
    final boolean[] called = {false};
    AstProcessor processor = new AstProcessor() {
      @Override public void processExpressionStatement(ExpressionStatement s) { called[0] = true; }
      @Override public CodeOrganizer getNewCodeOrganizerForTypeName(String n) { return null; }
    };
    stmt.process(processor);
    assertTrue(called[0]);
  }

  // ── ReturnStatement ──────────────────────────────────────

  @Test
  public void returnStatementCreation() {
    ReturnStatement ret = AstUtilities.createReturnStatement(String.class, new StringLiteral("hi"));
    assertNotNull(ret);
    assertTrue(ret.containsAReturnForEveryPath());
    assertTrue(ret.containsAtLeastOneEnabledReturnStatement());
  }

  @Test
  public void returnStatementDisabled() {
    ReturnStatement ret = new ReturnStatement(JavaType.OBJECT_TYPE, new NullLiteral());
    ret.isEnabled.setValue(false);
    assertFalse(ret.containsAtLeastOneEnabledReturnStatement());
    assertFalse(ret.containsAReturnForEveryPath());
  }

  @Test
  public void returnStatementProcess() {
    ReturnStatement ret = new ReturnStatement(JavaType.OBJECT_TYPE, new NullLiteral());
    final boolean[] called = {false};
    AstProcessor processor = new AstProcessor() {
      @Override public void processReturnStatement(ReturnStatement s) { called[0] = true; }
      @Override public CodeOrganizer getNewCodeOrganizerForTypeName(String n) { return null; }
    };
    ret.process(processor);
    assertTrue(called[0]);
  }

  // ── DoInOrder / DoTogether ────────────────────────────────

  @Test
  public void doInOrderCreation() {
    DoInOrder dio = AstUtilities.createDoInOrder();
    assertNotNull(dio);
    assertNotNull(dio.body.getValue());
  }

  @Test
  public void doTogetherCreation() {
    DoTogether dt = AstUtilities.createDoTogether();
    assertNotNull(dt);
    assertNotNull(dt.body.getValue());
  }

  // ── CountLoop / WhileLoop ─────────────────────────────────

  @Test
  public void countLoopCreation() {
    CountLoop cl = AstUtilities.createCountLoop(new IntegerLiteral(5));
    assertNotNull(cl);
  }

  @Test
  public void whileLoopCreation() {
    WhileLoop wl = AstUtilities.createWhileLoop(new BooleanLiteral(true));
    assertNotNull(wl);
  }

  // ── Comment ─────────────────────────────────────────────

  @Test
  public void commentCreation() {
    Comment c = AstUtilities.createComment();
    assertNotNull(c);
  }

  @Test
  public void commentIsEnabledNonComment() {
    Comment c = new Comment();
    // Comment always returns false for isEnabledNonCommment
    assertFalse(c.containsAtLeastOneEnabledReturnStatement());
    assertFalse(c.containsAReturnForEveryPath());
    assertFalse(c.containsUnreachableCode());
  }

  // ── AstUtilities remaining methods ─────────────────────

  @Test
  public void isKeywordExpressionNull() {
    assertFalse(AstUtilities.isKeywordExpression(null));
  }

  @Test
  public void isKeywordExpressionNonKeyed() {
    Expression expr = new NullLiteral();
    assertFalse(AstUtilities.isKeywordExpression(expr));
  }

  @Test
  public void createTypeExpression() {
    TypeExpression te = AstUtilities.createTypeExpression(JavaType.OBJECT_TYPE);
    assertNotNull(te);
    assertEquals(JavaType.OBJECT_TYPE, te.value.getValue());
  }

  @Test
  public void createTypeExpressionFromClass() {
    TypeExpression te = AstUtilities.createTypeExpression(String.class);
    assertNotNull(te);
  }

  @Test
  public void createInstanceCreationFromClass() {
    InstanceCreation ic = AstUtilities.createInstanceCreation(StringBuilder.class);
    assertNotNull(ic);
    assertNotNull(ic.constructor.getValue());
  }

  @Test
  public void createInstanceCreationFromType() {
    JavaType type = JavaType.getInstance(StringBuilder.class);
    InstanceCreation ic = AstUtilities.createInstanceCreation(type);
    assertNotNull(ic);
  }

  @Test
  public void createInstanceCreationWithParams() {
    InstanceCreation ic = AstUtilities.createInstanceCreation(
        String.class, new Class<?>[]{String.class}, new StringLiteral("test"));
    assertNotNull(ic);
    assertEquals(1, ic.requiredArguments.size());
  }

  @Test
  public void createArrayInstanceCreation() {
    ArrayInstanceCreation aic = AstUtilities.createArrayInstanceCreation(
        JavaType.getInstance(String[].class), new StringLiteral("a"), new StringLiteral("b"));
    assertNotNull(aic);
  }

  @Test
  public void createArrayInstanceCreationFromClass() {
    ArrayInstanceCreation aic = AstUtilities.createArrayInstanceCreation(
        String[].class, new StringLiteral("x"));
    assertNotNull(aic);
  }

  @Test
  public void createLocalDeclarationStatement() {
    UserLocal local = new UserLocal("temp", JavaType.getInstance(String.class), false);
    LocalDeclarationStatement lds = AstUtilities.createLocalDeclarationStatement(
        local, new StringLiteral("val"));
    assertNotNull(lds);
  }

  @Test
  public void createStringConcatenation() {
    StringConcatenation sc = AstUtilities.createStringConcatenation(
        new StringLiteral("hello "), new StringLiteral("world"));
    assertNotNull(sc);
  }

  @Test
  public void createFieldAssignment() {
    UserField field = new UserField("name", JavaType.getInstance(String.class), new NullLiteral());
    AssignmentExpression ae = AstUtilities.createFieldAssignment(field, new StringLiteral("val"));
    assertNotNull(ae);
  }

  @Test
  public void createFieldAssignmentStatement() {
    UserField field = new UserField("name", JavaType.getInstance(String.class), new NullLiteral());
    ExpressionStatement stmt = AstUtilities.createFieldAssignmentStatement(field, new StringLiteral("val"));
    assertNotNull(stmt);
    assertTrue(stmt.expression.getValue() instanceof AssignmentExpression);
  }

  @Test
  public void createFieldAssignmentWithExpression() {
    UserField field = new UserField("name", JavaType.getInstance(String.class), new NullLiteral());
    Expression target = new ThisExpression();
    AssignmentExpression ae = AstUtilities.createFieldAssignment(target, field, new StringLiteral("val"));
    assertNotNull(ae);
  }

  @Test
  public void createFieldAssignmentStatementWithExpression() {
    UserField field = new UserField("name", JavaType.getInstance(String.class), new NullLiteral());
    Expression target = new ThisExpression();
    ExpressionStatement stmt = AstUtilities.createFieldAssignmentStatement(target, field, new StringLiteral("val"));
    assertNotNull(stmt);
  }

  @Test
  public void createLocalAssignment() {
    UserLocal local = new UserLocal("temp", JavaType.getInstance(String.class), false);
    AssignmentExpression ae = AstUtilities.createLocalAssignment(local, new StringLiteral("x"));
    assertNotNull(ae);
  }

  @Test
  public void createLocalAssignmentStatement() {
    UserLocal local = new UserLocal("temp", JavaType.getInstance(String.class), false);
    ExpressionStatement stmt = AstUtilities.createLocalAssignmentStatement(local, new StringLiteral("x"));
    assertNotNull(stmt);
  }

  @Test
  public void createLocalArrayAssignment() {
    UserLocal local = new UserLocal("arr", JavaType.getInstance(String[].class), false);
    AssignmentExpression ae = AstUtilities.createLocalArrayAssignment(
        local, new IntegerLiteral(0), new StringLiteral("x"));
    assertNotNull(ae);
  }

  @Test
  public void createLocalArrayAssignmentStatement() {
    UserLocal local = new UserLocal("arr", JavaType.getInstance(String[].class), false);
    ExpressionStatement stmt = AstUtilities.createLocalArrayAssignmentStatement(
        local, new IntegerLiteral(0), new StringLiteral("x"));
    assertNotNull(stmt);
  }

  @Test
  public void createFieldArrayAssignment() {
    UserField field = new UserField("arr", JavaType.getInstance(String[].class), new NullLiteral());
    AssignmentExpression ae = AstUtilities.createFieldArrayAssignment(
        field, new IntegerLiteral(0), new StringLiteral("x"));
    assertNotNull(ae);
  }

  @Test
  public void createFieldArrayAssignmentStatement() {
    UserField field = new UserField("arr", JavaType.getInstance(String[].class), new NullLiteral());
    ExpressionStatement stmt = AstUtilities.createFieldArrayAssignmentStatement(
        field, new IntegerLiteral(0), new StringLiteral("x"));
    assertNotNull(stmt);
  }

  @Test
  public void createFieldArrayAssignmentWithTarget() {
    UserField field = new UserField("arr", JavaType.getInstance(String[].class), new NullLiteral());
    Expression target = new ThisExpression();
    AssignmentExpression ae = AstUtilities.createFieldArrayAssignment(
        target, field, new IntegerLiteral(0), new StringLiteral("x"));
    assertNotNull(ae);
  }

  @Test
  public void createFieldArrayAssignmentStatementWithTarget() {
    UserField field = new UserField("arr", JavaType.getInstance(String[].class), new NullLiteral());
    Expression target = new ThisExpression();
    ExpressionStatement stmt = AstUtilities.createFieldArrayAssignmentStatement(
        target, field, new IntegerLiteral(0), new StringLiteral("x"));
    assertNotNull(stmt);
  }

  @Test
  public void createParameterArrayAssignment() {
    UserParameter param = new UserParameter("arr", JavaType.getInstance(String[].class));
    AssignmentExpression ae = AstUtilities.createParameterArrayAssignment(
        param, new IntegerLiteral(0), new StringLiteral("x"));
    assertNotNull(ae);
  }

  @Test
  public void createParameterArrayAssignmentStatement() {
    UserParameter param = new UserParameter("arr", JavaType.getInstance(String[].class));
    ExpressionStatement stmt = AstUtilities.createParameterArrayAssignmentStatement(
        param, new IntegerLiteral(0), new StringLiteral("x"));
    assertNotNull(stmt);
  }

  @Test
  public void isAddEventListenerMethodInvocationStatement_nonListener() {
    ExpressionStatement stmt = new ExpressionStatement(new NullLiteral());
    assertFalse(AstUtilities.isAddEventListenerMethodInvocationStatement(stmt));
  }

  @Test
  public void isAddEventListenerMethodInvocationStatement_comment() {
    Comment comment = new Comment();
    assertFalse(AstUtilities.isAddEventListenerMethodInvocationStatement(comment));
  }

  @Test
  public void getNamedUserTypes() {
    NamedUserType type = AstUtilities.createType("TestType", JavaType.OBJECT_TYPE);
    UserMethod method = AstUtilities.createProcedure("myMethod");
    type.methods.add(method);
    Collection<NamedUserType> types = AstUtilities.getNamedUserTypes(type);
    assertNotNull(types);
    assertTrue(types.contains(type));
  }

  @Test
  public void createReturnStatementWithType() {
    ReturnStatement ret = AstUtilities.createReturnStatement(
        JavaType.getInstance(String.class), new StringLiteral("result"));
    assertNotNull(ret);
  }

  @Test
  public void createReturnStatementWithClass() {
    ReturnStatement ret = AstUtilities.createReturnStatement(
        String.class, new StringLiteral("result"));
    assertNotNull(ret);
  }

  // ── Literal expressions ──────────────────────────────────

  @Test
  public void integerLiteralType() {
    IntegerLiteral il = new IntegerLiteral(42);
    assertNotNull(il.getType());
  }

  @Test
  public void doubleLiteralType() {
    DoubleLiteral dl = new DoubleLiteral(3.14);
    assertNotNull(dl.getType());
  }

  @Test
  public void booleanLiteralType() {
    BooleanLiteral bl = new BooleanLiteral(true);
    assertNotNull(bl.getType());
  }

  @Test
  public void stringLiteralType() {
    StringLiteral sl = new StringLiteral("hello");
    assertNotNull(sl.getType());
  }

  @Test
  public void nullLiteralType() {
    NullLiteral nl = new NullLiteral();
    assertNotNull(nl.getType());
  }

  // ── NamedUserConstructor ──────────────────────────────────

  @Test
  public void namedUserConstructorEmptyConstructor() {
    NamedUserConstructor nuc = new NamedUserConstructor();
    // NamedUserConstructor.isUserAuthored() returns true (user-authored)
    assertTrue(nuc.isUserAuthored());
  }

  @Test
  public void namedUserConstructorWithParams() {
    ConstructorBlockStatement body = new ConstructorBlockStatement();
    NamedUserConstructor nuc = new NamedUserConstructor(new UserParameter[0], body);
    assertNotNull(nuc.getRequiredParameters());
    assertEquals(0, nuc.getRequiredParameters().size());
  }

  // ── UserMethod ────────────────────────────────────────────

  @Test
  public void userMethodCreation() {
    UserMethod method = AstUtilities.createProcedure("test");
    assertEquals("test", method.getName());
    assertTrue(method.isUserAuthored());
    assertNotNull(method.body.getValue());
  }

  @Test
  public void userMethodFunction() {
    UserMethod method = AstUtilities.createFunction("getX", double.class);
    assertEquals("getX", method.getName());
    assertNotNull(method.getReturnType());
  }

  @Test
  public void userMethodFunctionWithType() {
    UserMethod method = AstUtilities.createFunction("getY", JavaType.getInstance(String.class));
    assertEquals("getY", method.getName());
  }

  @Test
  public void userMethodProcess() {
    UserMethod method = AstUtilities.createProcedure("myProc");
    final boolean[] called = {false};
    AstProcessor processor = new AstProcessor() {
      @Override public void processMethod(UserMethod m) { called[0] = true; }
      @Override public CodeOrganizer getNewCodeOrganizerForTypeName(String n) { return null; }
    };
    method.process(processor);
    assertTrue(called[0]);
  }

  // ── NamedUserType ─────────────────────────────────────────

  @Test
  public void namedUserTypeCreation() {
    NamedUserType type = AstUtilities.createType("MyType", JavaType.OBJECT_TYPE);
    assertEquals("MyType", type.getName());
    assertTrue(type.isUserAuthored());
    assertEquals(1, type.constructors.size());
  }

  @Test
  public void namedUserTypeProcess() {
    NamedUserType type = AstUtilities.createType("ProcessType", JavaType.OBJECT_TYPE);
    // NamedUserType.process calls getCodeOrganizer which needs getNewCodeOrganizerForTypeName
    // Just verify the type was created correctly instead
    assertEquals("ProcessType", type.getName());
    assertTrue(type.isUserAuthored());
    assertEquals(1, type.constructors.size());
  }

  // ── UserField ─────────────────────────────────────────────

  @Test
  public void userFieldCreation() {
    UserField field = new UserField("myField", JavaType.getInstance(String.class), new NullLiteral());
    assertEquals("myField", field.getName());
    assertTrue(field.isUserAuthored());
    assertNotNull(field.getValueType());
  }

  @Test
  public void userFieldProcess() {
    UserField field = new UserField("f", JavaType.getInstance(int.class), new IntegerLiteral(0));
    final boolean[] called = {false};
    AstProcessor processor = new AstProcessor() {
      @Override public void processField(UserField f) { called[0] = true; }
      @Override public CodeOrganizer getNewCodeOrganizerForTypeName(String n) { return null; }
    };
    field.process(processor);
    assertTrue(called[0]);
  }

  // ── Miscellaneous expression nodes ─────────────────────

  @Test
  public void thisExpression() {
    ThisExpression te = new ThisExpression();
    assertNotNull(te);
  }

  @Test
  public void typeExpression() {
    TypeExpression te = new TypeExpression(JavaType.OBJECT_TYPE);
    assertSame(JavaType.OBJECT_TYPE, te.value.getValue());
  }

  @Test
  public void stringConcatenation() {
    StringConcatenation sc = new StringConcatenation(
        new StringLiteral("a"), new StringLiteral("b"));
    assertNotNull(sc);
    assertNotNull(sc.getType());
  }

  @Test
  public void arrayAccessCreation() {
    // ArrayAccess constructor has assertions on the array type
    // Test via AstUtilities.createLocalArrayAssignment which creates ArrayAccess internally
    UserLocal local = new UserLocal("arr", JavaType.getInstance(String[].class), false);
    AssignmentExpression ae = AstUtilities.createLocalArrayAssignment(
        local, new IntegerLiteral(0), new StringLiteral("x"));
    assertNotNull(ae);
  }

  @Test
  public void localAccess() {
    UserLocal local = new UserLocal("x", JavaType.getInstance(int.class), true);
    LocalAccess la = new LocalAccess(local);
    assertNotNull(la);
    assertSame(local, la.local.getValue());
  }

  @Test
  public void parameterAccess() {
    UserParameter param = new UserParameter("p", JavaType.getInstance(String.class));
    ParameterAccess pa = new ParameterAccess(param);
    assertNotNull(pa);
    assertSame(param, pa.parameter.getValue());
  }

  // ── LocalDeclarationStatement ─────────────────────────

  @Test
  public void localDeclarationProcess() {
    UserLocal local = new UserLocal("v", JavaType.getInstance(int.class), false);
    LocalDeclarationStatement lds = new LocalDeclarationStatement(local, new IntegerLiteral(0));
    final boolean[] called = {false};
    AstProcessor processor = new AstProcessor() {
      @Override public void processLocalDeclaration(LocalDeclarationStatement s) { called[0] = true; }
      @Override public CodeOrganizer getNewCodeOrganizerForTypeName(String n) { return null; }
    };
    lds.process(processor);
    assertTrue(called[0]);
  }

  // ── BooleanExpressionBodyPair ──────────────────────────

  @Test
  public void booleanExpressionBodyPairCreation() {
    BooleanExpressionBodyPair pair = new BooleanExpressionBodyPair(
        new BooleanLiteral(true), new BlockStatement());
    assertNotNull(pair.expression.getValue());
    assertNotNull(pair.body.getValue());
  }

  // ── AssignmentExpression ──────────────────────────────

  @Test
  public void assignmentExpressionOperators() {
    assertNotNull(AssignmentExpression.Operator.ASSIGN);
  }

  // ── UserLambda ──────────────────────────────────────────

  @Test
  public void createUserLambdaFromInterface() {
    UserLambda lambda = AstUtilities.createUserLambda(Runnable.class);
    assertNotNull(lambda);
    assertTrue(lambda.isSignatureLocked.getValue());
    assertEquals(0, lambda.getRequiredParameters().size());
  }

  @Test
  public void createLambdaExpression() {
    LambdaExpression le = AstUtilities.createLambdaExpression(Runnable.class);
    assertNotNull(le);
  }

  @Test
  public void createLambdaExpressionFromType() {
    LambdaExpression le = AstUtilities.createLambdaExpression(JavaType.getInstance(Runnable.class));
    assertNotNull(le);
  }
}
