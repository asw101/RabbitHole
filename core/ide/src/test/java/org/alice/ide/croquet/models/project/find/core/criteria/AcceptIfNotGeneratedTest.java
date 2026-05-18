package org.alice.ide.croquet.models.project.find.core.criteria;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

/**
 * Tests for {@link AcceptIfNotGenerated} — singleton criterion that rejects
 * expressions inside generated methods.
 */
public class AcceptIfNotGeneratedTest {

  private Expression createExpressionInMethod(ManagementLevel level) {
    UserMethod method = new UserMethod();
    method.name.setValue("testMethod");
    method.returnType.setValue(JavaType.VOID_TYPE);
    method.managementLevel.setValue(level);

    BlockStatement body = new BlockStatement();
    method.body.setValue(body);

    NullLiteral expr = new NullLiteral();
    ExpressionStatement stmt = new ExpressionStatement(expr);
    body.statements.add(stmt);

    return expr;
  }

  // ---- singleton ----

  @Test
  public void getInstance_returnsNonNull() {
    assertNotNull(AcceptIfNotGenerated.getInstance());
  }

  @Test
  public void getInstance_returnsSameInstance() {
    assertSame(AcceptIfNotGenerated.getInstance(), AcceptIfNotGenerated.getInstance());
  }

  // ---- accept behavior ----

  @Test
  public void accept_nonGenerated_returnsTrue() {
    Expression expr = createExpressionInMethod(ManagementLevel.NONE);
    assertTrue(AcceptIfNotGenerated.getInstance().accept(expr));
  }

  @Test
  public void accept_generated_returnsFalse() {
    Expression expr = createExpressionInMethod(ManagementLevel.GENERATED);
    assertFalse(AcceptIfNotGenerated.getInstance().accept(expr));
  }

  @Test
  public void accept_managed_returnsFalse() {
    Expression expr = createExpressionInMethod(ManagementLevel.MANAGED);
    assertFalse(AcceptIfNotGenerated.getInstance().accept(expr));
  }

  // ---- Criterion interface ----

  @Test
  public void implementsCriterion() {
    assertTrue(AcceptIfNotGenerated.getInstance() instanceof edu.cmu.cs.dennisc.pattern.Criterion);
  }

  // ---- management level contract ----

  @Test
  public void managementLevel_NONE_isNotGenerated() {
    assertFalse(ManagementLevel.NONE.isGenerated());
  }

  @Test
  public void managementLevel_GENERATED_isGenerated() {
    assertTrue(ManagementLevel.GENERATED.isGenerated());
  }

  @Test
  public void managementLevel_MANAGED_isGenerated() {
    assertTrue(ManagementLevel.MANAGED.isGenerated());
  }

  @Test
  public void managementLevel_MANAGED_isManaged() {
    assertTrue(ManagementLevel.MANAGED.isManaged());
  }

  @Test
  public void managementLevel_GENERATED_isNotManaged() {
    assertFalse(ManagementLevel.GENERATED.isManaged());
  }

  @Test
  public void managementLevel_NONE_isNotManaged() {
    assertFalse(ManagementLevel.NONE.isManaged());
  }

  // ---- multiple expressions in same method ----

  @Test
  public void accept_multipleExpressionsInNonGeneratedMethod_allAccepted() {
    UserMethod method = new UserMethod();
    method.name.setValue("userMethod");
    method.returnType.setValue(JavaType.VOID_TYPE);
    method.managementLevel.setValue(ManagementLevel.NONE);

    BlockStatement body = new BlockStatement();
    method.body.setValue(body);

    NullLiteral expr1 = new NullLiteral();
    IntegerLiteral expr2 = new IntegerLiteral(42);
    body.statements.add(new ExpressionStatement(expr1));
    body.statements.add(new ExpressionStatement(expr2));

    AcceptIfNotGenerated criterion = AcceptIfNotGenerated.getInstance();
    assertTrue(criterion.accept(expr1));
    assertTrue(criterion.accept(expr2));
  }

  @Test
  public void accept_multipleExpressionsInGeneratedMethod_allRejected() {
    UserMethod method = new UserMethod();
    method.name.setValue("generatedMethod");
    method.returnType.setValue(JavaType.VOID_TYPE);
    method.managementLevel.setValue(ManagementLevel.GENERATED);

    BlockStatement body = new BlockStatement();
    method.body.setValue(body);

    NullLiteral expr1 = new NullLiteral();
    IntegerLiteral expr2 = new IntegerLiteral(42);
    body.statements.add(new ExpressionStatement(expr1));
    body.statements.add(new ExpressionStatement(expr2));

    AcceptIfNotGenerated criterion = AcceptIfNotGenerated.getInstance();
    assertFalse(criterion.accept(expr1));
    assertFalse(criterion.accept(expr2));
  }
}
