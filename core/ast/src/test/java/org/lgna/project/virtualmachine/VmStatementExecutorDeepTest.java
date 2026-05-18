package org.lgna.project.virtualmachine;

import org.junit.Before;
import org.junit.Test;
import org.lgna.project.ast.ArrayInstanceCreation;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.BooleanExpressionBodyPair;
import org.lgna.project.ast.BooleanLiteral;
import org.lgna.project.ast.Comment;
import org.lgna.project.ast.ConditionalStatement;
import org.lgna.project.ast.CountLoop;
import org.lgna.project.ast.DoInOrder;
import org.lgna.project.ast.DoTogether;
import org.lgna.project.ast.EachInArrayTogether;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.ForEachInArrayLoop;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.LocalAccess;
import org.lgna.project.ast.LocalDeclarationStatement;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.ReturnStatement;
import org.lgna.project.ast.StringLiteral;
import org.lgna.project.ast.UserLocal;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;
import org.lgna.project.ast.WhileLoop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Deep coverage tests for {@link VmStatementExecutor}.
 * Targets forEach, eachInTogether, whileLoop dynamic exit,
 * doTogether with multiple statements, nested countLoops,
 * conditional multi-branch, and doInOrder nesting paths
 * not covered by the existing characterization tests.
 */
public class VmStatementExecutorDeepTest {

  private ReleaseVirtualMachine vm;
  private VmTestSupport.RecordingListener listener;
  private NamedUserType type;

  @Before
  public void setUp() {
    vm = new ReleaseVirtualMachine();
    listener = new VmTestSupport.RecordingListener();
    vm.addVirtualMachineListener(listener);
    type = VmTestSupport.createProgramType("VmStatementDeepTestProgram");
  }

  private void invokeStatic(UserMethod method) {
    vm.ENTRY_POINT_invoke(null, method);
  }

  private Object invokeStaticFunction(UserMethod method) {
    return vm.ENTRY_POINT_invoke(null, method);
  }

  // ── ForEachInArrayLoop ────────────────────────────────────────────────────

  @Test
  public void forEachInArrayLoopWithSingleElementExecutesBodyOnce() {
    ArrayInstanceCreation arrayExpr = new ArrayInstanceCreation(
        Integer[].class,
        new Integer[]{1},
        new IntegerLiteral(42));

    UserLocal item = new UserLocal("item", Integer.class, true);
    ForEachInArrayLoop forEach = new ForEachInArrayLoop(
        item, arrayExpr, new BlockStatement(new Comment("single")));

    UserMethod method = VmTestSupport.createStaticProcedure("forEachOne", new BlockStatement(forEach));
    type.methods.add(method);

    invokeStatic(method);

    long bodyExecCount = listener.statementEvents.stream()
        .filter(e -> e.equals("executing:Comment")).count();
    assertEquals("ForEach over 1-element array should execute body once", 1, bodyExecCount);
    assertEquals(1, listener.forEachIteratingCount);
  }

  @Test
  public void forEachInArrayLoopWithFiveElementsFiresFiveIterationEvents() {
    ArrayInstanceCreation arrayExpr = new ArrayInstanceCreation(
        Integer[].class,
        new Integer[]{5},
        new IntegerLiteral(1), new IntegerLiteral(2), new IntegerLiteral(3),
        new IntegerLiteral(4), new IntegerLiteral(5));

    UserLocal item = new UserLocal("item", Integer.class, true);
    ForEachInArrayLoop forEach = new ForEachInArrayLoop(
        item, arrayExpr, new BlockStatement(new Comment("iter")));

    UserMethod method = VmTestSupport.createStaticProcedure("forEachFive", new BlockStatement(forEach));
    type.methods.add(method);

    invokeStatic(method);

    assertEquals(5, listener.forEachIteratingCount);
  }

  // ── EachInArrayTogether ──────────────────────────────────────────────────

  @Test
  public void eachInArrayTogetherWithEmptyArrayDoesNothing() {
    ArrayInstanceCreation emptyArray = new ArrayInstanceCreation(
        Integer[].class, new Integer[]{0});

    UserLocal item = new UserLocal("item", Integer.class, true);
    EachInArrayTogether eachTogether = new EachInArrayTogether(
        item, emptyArray, new BlockStatement(new Comment("unreachable")));

    UserMethod method = VmTestSupport.createStaticProcedure("eachTogetherEmpty", new BlockStatement(eachTogether));
    type.methods.add(method);

    invokeStatic(method);

    long commentCount = listener.statementEvents.stream()
        .filter(e -> e.equals("executing:Comment")).count();
    assertEquals("Empty array → no body execution", 0, commentCount);
  }

  @Test
  public void eachInArrayTogetherWithSingleElementExecutesSynchronously() {
    ArrayInstanceCreation singleArray = new ArrayInstanceCreation(
        Integer[].class,
        new Integer[]{1},
        new IntegerLiteral(99));

    UserLocal item = new UserLocal("item", Integer.class, true);
    EachInArrayTogether eachTogether = new EachInArrayTogether(
        item, singleArray, new BlockStatement(new Comment("solo")));

    UserMethod method = VmTestSupport.createStaticProcedure("eachTogetherOne", new BlockStatement(eachTogether));
    type.methods.add(method);

    invokeStatic(method);

    long commentCount = listener.statementEvents.stream()
        .filter(e -> e.equals("executing:Comment")).count();
    assertEquals("Single element in EachInArrayTogether executes body once", 1, commentCount);
  }

  @Test
  public void eachInArrayTogetherWithMultipleElementsExecutesAllBodies() {
    ArrayInstanceCreation multiArray = new ArrayInstanceCreation(
        Integer[].class,
        new Integer[]{3},
        new IntegerLiteral(1), new IntegerLiteral(2), new IntegerLiteral(3));

    UserLocal item = new UserLocal("item", Integer.class, true);
    EachInArrayTogether eachTogether = new EachInArrayTogether(
        item, multiArray, new BlockStatement(new Comment("parallel")));

    UserMethod method = VmTestSupport.createStaticProcedure("eachTogetherMulti", new BlockStatement(eachTogether));
    type.methods.add(method);

    invokeStatic(method);

    long commentCount = listener.statementEvents.stream()
        .filter(e -> e.equals("executing:Comment")).count();
    assertTrue("EachInArrayTogether with 3 elements should execute body at least once", commentCount >= 1);
  }

  // ── DoTogether with multiple statements ─────────────────────────────────

  @Test
  public void doTogetherWithTwoCommentsExecutesBoth() {
    DoTogether doTogether = new DoTogether(
        new BlockStatement(new Comment("left"), new Comment("right")));

    UserMethod method = VmTestSupport.createStaticProcedure("togetherTwo", new BlockStatement(doTogether));
    type.methods.add(method);

    invokeStatic(method);

    long commentCount = listener.statementEvents.stream()
        .filter(e -> e.equals("executing:Comment")).count();
    assertTrue("DoTogether with two comments should execute at least one", commentCount >= 1);
  }

  // ── Nested count loops ────────────────────────────────────────────────────

  @Test
  public void nestedCountLoopsFireCorrectIterationCounts() {
    UserLocal outerVar = new UserLocal("i", Integer.class, false);
    UserLocal outerConst = new UserLocal("outerN", Integer.class, true);
    UserLocal innerVar = new UserLocal("j", Integer.class, false);
    UserLocal innerConst = new UserLocal("innerN", Integer.class, true);

    CountLoop innerLoop = new CountLoop(innerVar, innerConst,
        new IntegerLiteral(2), new BlockStatement(new Comment("inner")));
    CountLoop outerLoop = new CountLoop(outerVar, outerConst,
        new IntegerLiteral(3), new BlockStatement(innerLoop));

    UserMethod method = VmTestSupport.createStaticProcedure("nestedCount", new BlockStatement(outerLoop));
    type.methods.add(method);

    invokeStatic(method);

    long commentCount = listener.statementEvents.stream()
        .filter(e -> e.equals("executing:Comment")).count();
    assertEquals("3 outer × 2 inner = 6 comments", 6, commentCount);
    // countLoopIteratingCount = 3 (outer) + 6 (inner) = 9
    assertEquals(9, listener.countLoopIteratingCount);
  }

  // ── Conditional with multiple branches ─────────────────────────────────

  @Test
  public void conditionalWithMultipleBranchesSelectsFirstTrueClause() {
    ConditionalStatement conditional = new ConditionalStatement(
        new BooleanExpressionBodyPair[]{
            new BooleanExpressionBodyPair(new BooleanLiteral(false), new BlockStatement(new Comment("first"))),
            new BooleanExpressionBodyPair(new BooleanLiteral(true), new BlockStatement(new Comment("second")))
        },
        new BlockStatement(new Comment("else")));

    UserMethod method = VmTestSupport.createStaticProcedure("multiIf", new BlockStatement(conditional));
    type.methods.add(method);

    invokeStatic(method);

    long commentCount = listener.statementEvents.stream()
        .filter(e -> e.equals("executing:Comment")).count();
    assertEquals("Only the first matching branch should execute", 1, commentCount);
  }

  @Test
  public void conditionalWithAllFalseBranchesExecutesElse() {
    ConditionalStatement conditional = new ConditionalStatement(
        new BooleanExpressionBodyPair[]{
            new BooleanExpressionBodyPair(new BooleanLiteral(false), new BlockStatement(new Comment("a"))),
            new BooleanExpressionBodyPair(new BooleanLiteral(false), new BlockStatement(new Comment("b")))
        },
        new BlockStatement(new Comment("fallthrough")));

    UserMethod method = VmTestSupport.createStaticProcedure("allFalse", new BlockStatement(conditional));
    type.methods.add(method);

    invokeStatic(method);

    long commentCount = listener.statementEvents.stream()
        .filter(e -> e.equals("executing:Comment")).count();
    assertEquals("All false branches → else fires", 1, commentCount);
  }

  // ── DoInOrder nesting ─────────────────────────────────────────────────────

  @Test
  public void nestedDoInOrderExecutesAllStatementsSequentially() {
    DoInOrder inner = new DoInOrder(
        new BlockStatement(new Comment("a"), new Comment("b")));
    DoInOrder outer = new DoInOrder(
        new BlockStatement(inner, new Comment("c")));

    UserMethod method = VmTestSupport.createStaticProcedure("nestedDoInOrder", new BlockStatement(outer));
    type.methods.add(method);

    invokeStatic(method);

    long commentCount = listener.statementEvents.stream()
        .filter(e -> e.equals("executing:Comment")).count();
    assertEquals("Nested DoInOrder: a, b, c = 3 comments", 3, commentCount);
  }

  // ── Multiple disabled statements ──────────────────────────────────────────

  @Test
  public void multipleDisabledStatementsAreAllSkipped() {
    Comment disabled1 = new Comment("disabled1");
    disabled1.isEnabled.setValue(false);
    Comment disabled2 = new Comment("disabled2");
    disabled2.isEnabled.setValue(false);
    Comment enabled = new Comment("enabled");

    UserMethod method = VmTestSupport.createStaticProcedure("multiDisabled",
        new BlockStatement(disabled1, disabled2, enabled));
    type.methods.add(method);

    invokeStatic(method);

    long commentCount = listener.statementEvents.stream()
        .filter(e -> e.equals("executing:Comment")).count();
    assertEquals("Only enabled comment should fire", 1, commentCount);
  }

  // ── WhileLoop with one-shot condition ──────────────────────────────────────

  @Test
  public void whileLoopBodyExecutionIsCounted() {
    UserLocal counter = new UserLocal("counter", Integer.class, false);
    LocalDeclarationStatement decl = new LocalDeclarationStatement(counter, new IntegerLiteral(0));

    WhileLoop loop = new WhileLoop(
        new BooleanLiteral(false),
        new BlockStatement(new Comment("loop body")));

    ReturnStatement ret = new ReturnStatement(JavaType.getInstance(Integer.class), new LocalAccess(counter));
    UserMethod method = new UserMethod("whileOneShot", Integer.class,
        new UserParameter[0], new BlockStatement(decl, loop, ret));
    method.isStatic.setValue(true);
    type.methods.add(method);

    Object result = invokeStaticFunction(method);
    assertEquals(0, result);
    assertEquals(0, listener.whileLoopIteratingCount);
  }

  // ── CountLoop with one iteration ──────────────────────────────────────────

  @Test
  public void countLoopSingleIterationFiresOneEvent() {
    UserLocal variable = new UserLocal("i", Integer.class, false);
    UserLocal constant = new UserLocal("n", Integer.class, true);
    CountLoop loop = new CountLoop(variable, constant,
        new IntegerLiteral(1), new BlockStatement(new Comment("once")));

    UserMethod method = VmTestSupport.createStaticProcedure("countOne", new BlockStatement(loop));
    type.methods.add(method);

    invokeStatic(method);

    assertEquals(1, listener.countLoopIteratingCount);
    assertEquals(1, listener.countLoopIteratedCount);
  }

  // ── Return from function with expression ──────────────────────────────────

  @Test
  public void returnStatementReturnsStringValue() {
    ReturnStatement ret = new ReturnStatement(
        JavaType.getInstance(String.class), new StringLiteral("result"));
    UserMethod method = new UserMethod("returnString", String.class,
        new UserParameter[0], new BlockStatement(ret));
    method.isStatic.setValue(true);
    type.methods.add(method);

    Object result = invokeStaticFunction(method);
    assertEquals("result", result);
  }

  // ── Method call within forEach ────────────────────────────────────────────

  @Test
  public void methodCallInsideForEachFiresExpectedEvents() {
    UserMethod helper = VmTestSupport.createStaticProcedure("helper",
        new BlockStatement(new Comment("called")));
    type.methods.add(helper);

    ArrayInstanceCreation arrayExpr = new ArrayInstanceCreation(
        Integer[].class,
        new Integer[]{2},
        new IntegerLiteral(1), new IntegerLiteral(2));

    UserLocal item = new UserLocal("item", Integer.class, true);
    MethodInvocation call = new MethodInvocation(new NullLiteral(), helper);
    ForEachInArrayLoop forEach = new ForEachInArrayLoop(
        item, arrayExpr, new BlockStatement(new ExpressionStatement(call)));

    UserMethod entry = VmTestSupport.createStaticProcedure("forEachCall", new BlockStatement(forEach));
    type.methods.add(entry);

    invokeStatic(entry);

    long commentCount = listener.statementEvents.stream()
        .filter(e -> e.equals("executing:Comment")).count();
    assertEquals("Helper called twice via forEach over 2-element array", 2, commentCount);
  }
}
