package org.alice.stageide.ast;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

/**
 * Deep coverage tests for {@link BootstrapUtilities} —
 * testing static helpers that don't need StageIDE.getActiveInstance().
 */
public class BootstrapUtilitiesDeepTest {

  // ---- MY_FIRST_PROCEDURE_NAME ----

  @Test
  public void myFirstProcedureName_equalsMyFirstMethod() {
    assertEquals("myFirstMethod", BootstrapUtilities.MY_FIRST_PROCEDURE_NAME);
  }

  @Test
  public void myFirstProcedureName_isNonNull() {
    assertNotNull(BootstrapUtilities.MY_FIRST_PROCEDURE_NAME);
  }

  @Test
  public void myFirstProcedureName_startsWithLowerCase() {
    assertTrue(Character.isLowerCase(BootstrapUtilities.MY_FIRST_PROCEDURE_NAME.charAt(0)));
  }

  // ---- createFieldAccess(Enum) ----

  @Test
  public void createFieldAccess_threadStateNew_returnsFieldAccess() {
    FieldAccess fa = BootstrapUtilities.createFieldAccess(Thread.State.NEW);
    assertNotNull(fa);
    assertNotNull(fa.field.getValue());
    // The expression should be a TypeExpression for the enum class
    assertTrue(fa.expression.getValue() instanceof TypeExpression);
  }

  @Test
  public void createFieldAccess_threadStateRunnable_fieldNameMatches() {
    FieldAccess fa = BootstrapUtilities.createFieldAccess(Thread.State.RUNNABLE);
    assertNotNull(fa);
    assertEquals("RUNNABLE", fa.field.getValue().getName());
  }

  @Test
  public void createFieldAccess_differentEnumValues_produceDifferentFieldNames() {
    FieldAccess fa1 = BootstrapUtilities.createFieldAccess(Thread.State.NEW);
    FieldAccess fa2 = BootstrapUtilities.createFieldAccess(Thread.State.TERMINATED);
    assertNotEquals(fa1.field.getValue().getName(), fa2.field.getValue().getName());
  }

  // ---- createMethodInvocationStatement ----

  @Test
  public void createMethodInvocationStatement_returnsExpressionStatement() {
    JavaMethod method = JavaMethod.getInstance(String.class, "length");
    Expression expr = new StringLiteral("hello");
    ExpressionStatement stmt = BootstrapUtilities.createMethodInvocationStatement(expr, method);
    assertNotNull(stmt);
    assertTrue(stmt.expression.getValue() instanceof MethodInvocation);
  }

  @Test
  public void createMethodInvocationStatement_methodIsCorrect() {
    JavaMethod method = JavaMethod.getInstance(String.class, "length");
    Expression expr = new StringLiteral("test");
    ExpressionStatement stmt = BootstrapUtilities.createMethodInvocationStatement(expr, method);
    MethodInvocation mi = (MethodInvocation) stmt.expression.getValue();
    assertSame(method, mi.method.getValue());
  }

  @Test
  public void createMethodInvocationStatement_withArgs_includesArguments() {
    JavaMethod method = JavaMethod.getInstance(String.class, "substring", Integer.TYPE);
    Expression expr = new StringLiteral("hello");
    Expression arg = new IntegerLiteral(2);
    ExpressionStatement stmt = BootstrapUtilities.createMethodInvocationStatement(expr, method, arg);
    assertNotNull(stmt);
    MethodInvocation mi = (MethodInvocation) stmt.expression.getValue();
    assertFalse("Should have arguments", mi.requiredArguments.getValue().isEmpty());
  }

  // ---- createFieldAccess with various enum types ----

  @Test
  public void createFieldAccess_roundingMode_returnsFieldAccess() {
    FieldAccess fa = BootstrapUtilities.createFieldAccess(java.math.RoundingMode.HALF_UP);
    assertNotNull(fa);
    assertEquals("HALF_UP", fa.field.getValue().getName());
  }
}
