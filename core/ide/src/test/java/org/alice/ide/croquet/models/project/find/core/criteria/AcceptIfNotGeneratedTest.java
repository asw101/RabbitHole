package org.alice.ide.croquet.models.project.find.core.criteria;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

/**
 * Tests for {@link AcceptIfNotGenerated} — Criterion<Expression> singleton
 * that filters out expressions inside generated UserMethods.
 */
public class AcceptIfNotGeneratedTest {

  // ---- singleton ----

  @Test
  public void getInstance_returnsNonNull() {
    AcceptIfNotGenerated instance = AcceptIfNotGenerated.getInstance();
    assertNotNull(instance);
  }

  @Test
  public void getInstance_returnsSameInstance() {
    AcceptIfNotGenerated a = AcceptIfNotGenerated.getInstance();
    AcceptIfNotGenerated b = AcceptIfNotGenerated.getInstance();
    assertSame(a, b);
  }

  @Test
  public void getInstance_multipleCallsAllSame() {
    AcceptIfNotGenerated first = AcceptIfNotGenerated.getInstance();
    for (int i = 0; i < 10; i++) {
      assertSame("Call " + i, first, AcceptIfNotGenerated.getInstance());
    }
  }

  // ---- Criterion interface ----

  @Test
  public void implementsCriterion() {
    AcceptIfNotGenerated instance = AcceptIfNotGenerated.getInstance();
    assertTrue(instance instanceof edu.cmu.cs.dennisc.pattern.Criterion);
  }

  // ---- accept behavior with real AST tree ----

  private Expression createExpressionInMethod(ManagementLevel level) {
    NamedUserType type = new NamedUserType();
    type.name.setValue("TestType");
    UserMethod method = new UserMethod();
    method.name.setValue("testMethod");
    method.managementLevel.setValue(level);
    NullLiteral expr = new NullLiteral();
    ExpressionStatement stmt = new ExpressionStatement(expr);
    BlockStatement body = new BlockStatement(stmt);
    method.body.setValue(body);
    type.methods.add(method);
    return expr;
  }

  @Test
  public void accept_nonGeneratedMethod_returnsTrue() {
    Expression expr = createExpressionInMethod(ManagementLevel.NONE);
    assertTrue(AcceptIfNotGenerated.getInstance().accept(expr));
  }

  @Test
  public void accept_generatedMethod_returnsFalse() {
    Expression expr = createExpressionInMethod(ManagementLevel.GENERATED);
    assertFalse(AcceptIfNotGenerated.getInstance().accept(expr));
  }

  @Test
  public void accept_managedMethod_returnsFalse() {
    Expression expr = createExpressionInMethod(ManagementLevel.MANAGED);
    // MANAGED is also generated (isGenerated=true)
    assertFalse(AcceptIfNotGenerated.getInstance().accept(expr));
  }

  // ---- multiple expressions ----

  @Test
  public void accept_multipleNonGenerated_allTrue() {
    for (int i = 0; i < 5; i++) {
      Expression expr = createExpressionInMethod(ManagementLevel.NONE);
      assertTrue("Expr " + i, AcceptIfNotGenerated.getInstance().accept(expr));
    }
  }

  @Test
  public void accept_multipleGenerated_allFalse() {
    for (int i = 0; i < 5; i++) {
      Expression expr = createExpressionInMethod(ManagementLevel.GENERATED);
      assertFalse("Expr " + i, AcceptIfNotGenerated.getInstance().accept(expr));
    }
  }

  // ---- singleton stability ----

  @Test
  public void getInstance_afterAccept_stillSame() {
    AcceptIfNotGenerated before = AcceptIfNotGenerated.getInstance();
    Expression expr = createExpressionInMethod(ManagementLevel.NONE);
    before.accept(expr);
    AcceptIfNotGenerated after = AcceptIfNotGenerated.getInstance();
    assertSame(before, after);
  }
}
