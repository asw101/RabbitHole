package org.lgna.project.virtualmachine;

import org.junit.Before;
import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.Comment;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.RelationalInfixExpression;
import org.lgna.project.ast.SimpleArgument;
import org.lgna.project.ast.StringConcatenation;
import org.lgna.project.ast.StringLiteral;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;
import org.lgna.project.ast.WhileLoop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class VmMethodInvocationErrorTest {
  private ReleaseVirtualMachine vm;
  private NamedUserType type;

  @Before
  public void setUp() {
    vm = new ReleaseVirtualMachine();
    type = VmTestSupport.createProgramType("VmMethodInvocationErrorProgram");
  }

  @Test
  public void invalidMethodInvocationThrowsContextualException() {
    VmTestSupport.RecordingListener listener = new VmTestSupport.RecordingListener();
    vm.addVirtualMachineListener(listener);
    UserMethod orphan = new UserMethod("orphan", Void.TYPE,
        new UserParameter[0], new BlockStatement(new Comment("unreachable")));
    orphan.isStatic.setValue(true);

    MethodInvocation invalidCall = new MethodInvocation(new NullLiteral(), orphan);
    UserMethod entry = new UserMethod("entry", Void.TYPE,
        new UserParameter[0], new BlockStatement(new ExpressionStatement(invalidCall)));
    entry.isStatic.setValue(true);
    type.methods.add(entry);

    try {
      vm.ENTRY_POINT_invoke(null, entry);
      fail("Invalid method invocation should throw a contextual VM exception");
    } catch (LgnaVmMethodInvocationException e) {
      assertEquals("Invalid method invocation", e.getMessage());
      assertSame(invalidCall, e.getMethodInvocation());
      assertSame(orphan, e.getMethod());
      assertNull(e.getTarget());
      assertEquals(0, e.getArguments().length);
    }

    long commentEvents = listener.statementEvents.stream()
        .filter(e -> e.equals("executing:Comment")).count();
    assertEquals("Invalid method body should not execute", 0, commentEvents);
  }

  @Test
  public void userMethodInvocationFailureCarriesTargetArgumentsMethodAndCause() {
    WhileLoop loop = new WhileLoop(
        new NullLiteral(),
        new BlockStatement(new Comment("unreachable")));
    UserParameter amount = new UserParameter("amount", Integer.class);
    UserMethod helper = new UserMethod("failsInBody", Void.TYPE,
        new UserParameter[] {amount}, new BlockStatement(loop));
    helper.isStatic.setValue(true);
    type.methods.add(helper);

    MethodInvocation call = new MethodInvocation(new NullLiteral(), helper,
        new SimpleArgument(amount, new IntegerLiteral(7)));
    UserMethod entry = new UserMethod("entry", Void.TYPE,
        new UserParameter[0], new BlockStatement(new ExpressionStatement(call)));
    entry.isStatic.setValue(true);
    type.methods.add(entry);

    try {
      vm.ENTRY_POINT_invoke(null, entry);
      fail("Failing method invocation should throw a contextual VM exception");
    } catch (LgnaVmMethodInvocationException e) {
      assertEquals("Method invocation failed", e.getMessage());
      assertSame(call, e.getMethodInvocation());
      assertSame(helper, e.getMethod());
      assertNull(e.getTarget());
      assertEquals(1, e.getArguments().length);
      assertEquals(7, e.getArguments()[0]);
      assertTrue(e.getCause() instanceof LgnaVmNullPointerException);
      assertEquals("while condition is null", e.getCause().getMessage());
    }
  }

  @Test
  public void javaMethodInvocationFailureCarriesEvaluatedArgumentsAndCause() {
    JavaMethod javaMethod = JavaMethod.getInstance(ThrowingJavaMethods.class, "failWith", Integer.class);
    MethodInvocation call = new MethodInvocation(new NullLiteral(), javaMethod,
        new SimpleArgument(javaMethod.getRequiredParameters().get(0), new IntegerLiteral(9)));
    UserMethod entry = new UserMethod("entry", Void.TYPE,
        new UserParameter[0], new BlockStatement(new ExpressionStatement(call)));
    entry.isStatic.setValue(true);
    type.methods.add(entry);

    try {
      vm.ENTRY_POINT_invoke(null, entry);
      fail("Failing Java method invocation should throw a contextual VM exception");
    } catch (LgnaVmMethodInvocationException e) {
      assertEquals("Method invocation failed", e.getMessage());
      assertSame(call, e.getMethodInvocation());
      assertSame(javaMethod, e.getMethod());
      assertNull(e.getTarget());
      assertEquals(1, e.getArguments().length);
      assertEquals(9, e.getArguments()[0]);
      assertTrue(e.getCause() instanceof IllegalStateException);
      assertEquals("java failure <9 &>", e.getCause().getMessage());
      assertTrue(e.getFormattedString().contains("java failure &lt;9 &amp;&gt;"));
      assertTrue(!e.getFormattedString().contains("java failure <9 &>"));
    }
  }

  @Test
  public void instanceMethodInvocationFailureCarriesEvaluatedTarget() {
    JavaMethod javaMethod = JavaMethod.getInstance(String.class, "charAt", int.class);
    MethodInvocation call = new MethodInvocation(new StringLiteral("abc"), javaMethod,
        new SimpleArgument(javaMethod.getRequiredParameters().get(0), new IntegerLiteral(9)));
    UserMethod entry = new UserMethod("entry", Void.TYPE,
        new UserParameter[0], new BlockStatement(new ExpressionStatement(call)));
    entry.isStatic.setValue(true);
    type.methods.add(entry);

    try {
      vm.ENTRY_POINT_invoke(null, entry);
      fail("Failing instance method invocation should throw a contextual VM exception");
    } catch (LgnaVmMethodInvocationException e) {
      assertSame(call, e.getMethodInvocation());
      assertSame(javaMethod, e.getMethod());
      assertEquals("abc", e.getTarget());
      assertEquals(1, e.getArguments().length);
      assertEquals(9, e.getArguments()[0]);
      assertTrue(e.getCause() instanceof StringIndexOutOfBoundsException);
    }
  }

  @Test
  public void methodInvocationArgumentEvaluationFailureKeepsOriginalVmException() {
    JavaMethod javaMethod = JavaMethod.getInstance(String.class, "valueOf", Object.class);
    Expression failingArgument = new RelationalInfixExpression(
        new IntegerLiteral(1),
        RelationalInfixExpression.Operator.LESS,
        new NullLiteral(),
        Integer.class, Integer.class);
    MethodInvocation call = new MethodInvocation(new NullLiteral(), javaMethod,
        new SimpleArgument(javaMethod.getRequiredParameters().get(0), failingArgument));
    UserMethod entry = new UserMethod("entry", Void.TYPE,
        new UserParameter[0], new BlockStatement(new ExpressionStatement(call)));
    entry.isStatic.setValue(true);
    type.methods.add(entry);

    try {
      vm.ENTRY_POINT_invoke(null, entry);
      fail("Argument evaluation should keep its original VM exception");
    } catch (LgnaVmNullPointerException e) {
      assertEquals("right operand is null.", e.getMessage());
    } catch (LgnaVmMethodInvocationException e) {
      fail("Argument evaluation failure should not be wrapped as an invocation failure");
    }
  }

  @Test
  public void methodInvocationTargetEvaluationFailureKeepsOriginalVmException() {
    JavaMethod javaMethod = JavaMethod.getInstance(String.class, "charAt", int.class);
    Expression failingTarget = new StringConcatenation(
        new RelationalInfixExpression(
            new IntegerLiteral(1),
            RelationalInfixExpression.Operator.LESS,
            new NullLiteral(),
            Integer.class, Integer.class),
        new StringLiteral(""));
    MethodInvocation call = new MethodInvocation(failingTarget, javaMethod,
        new SimpleArgument(javaMethod.getRequiredParameters().get(0), new IntegerLiteral(0)));
    UserMethod entry = new UserMethod("entry", Void.TYPE,
        new UserParameter[0], new BlockStatement(new ExpressionStatement(call)));
    entry.isStatic.setValue(true);
    type.methods.add(entry);

    try {
      vm.ENTRY_POINT_invoke(null, entry);
      fail("Target evaluation should keep its original VM exception");
    } catch (LgnaVmNullPointerException e) {
      assertEquals("right operand is null.", e.getMessage());
    } catch (LgnaVmMethodInvocationException e) {
      fail("Target evaluation failure should not be wrapped as an invocation failure");
    }
  }

  @Test
  public void methodInvocationExceptionRejectsNullArgumentDiagnostics() {
    JavaMethod javaMethod = JavaMethod.getInstance(String.class, "valueOf", Object.class);
    MethodInvocation call = new MethodInvocation(new NullLiteral(), javaMethod);

    try {
      new LgnaVmMethodInvocationException(vm, call, null, javaMethod, null, new IllegalStateException("boom"));
      fail("Null argument diagnostics should not be silently converted to an empty array");
    } catch (NullPointerException e) {
      assertEquals("arguments", e.getMessage());
    }
  }

  public static class ThrowingJavaMethods {
    public static void failWith(Integer amount) {
      throw new IllegalStateException("java failure <" + amount + " &>");
    }
  }
}
