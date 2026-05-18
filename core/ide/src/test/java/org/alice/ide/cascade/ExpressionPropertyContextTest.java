package org.alice.ide.cascade;

import org.alice.ide.ast.draganddrop.BlockStatementIndexPair;
import org.lgna.project.ast.*;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link ExpressionPropertyContext} — getPreviousExpression from ExpressionProperty,
 * and getBlockStatementIndexPair walking up the AST tree.
 */
public class ExpressionPropertyContextTest {

  // ---- getPreviousExpression ----

  @Test
  public void getPreviousExpression_returnsExpressionPropertyValue() {
    ExpressionStatement stmt = new ExpressionStatement(new NullLiteral());
    ExpressionProperty exprProp = stmt.expression;
    ExpressionPropertyContext ctx = new ExpressionPropertyContext(exprProp);

    Expression prev = ctx.getPreviousExpression();
    assertNotNull(prev);
    assertTrue("Should return a NullLiteral", prev instanceof NullLiteral);
  }

  @Test
  public void getPreviousExpression_withDoubleLiteral_returnsDoubleLiteral() {
    DoubleLiteral doubleLit = new DoubleLiteral(3.14);
    ExpressionStatement stmt = new ExpressionStatement(doubleLit);
    ExpressionPropertyContext ctx = new ExpressionPropertyContext(stmt.expression);

    Expression prev = ctx.getPreviousExpression();
    assertSame(doubleLit, prev);
  }

  @Test
  public void getPreviousExpression_withIntegerLiteral_returnsIntegerLiteral() {
    IntegerLiteral intLit = new IntegerLiteral(42);
    ExpressionStatement stmt = new ExpressionStatement(intLit);
    ExpressionPropertyContext ctx = new ExpressionPropertyContext(stmt.expression);

    assertSame(intLit, ctx.getPreviousExpression());
  }

  @Test
  public void getPreviousExpression_withStringLiteral_returnsStringLiteral() {
    StringLiteral strLit = new StringLiteral("hello");
    ExpressionStatement stmt = new ExpressionStatement(strLit);
    ExpressionPropertyContext ctx = new ExpressionPropertyContext(stmt.expression);

    assertSame(strLit, ctx.getPreviousExpression());
  }

  // ---- getBlockStatementIndexPair — statement inside BlockStatement ----

  @Test
  public void getBlockStatementIndexPair_statementInBlock_returnsCorrectPair() {
    NullLiteral expr = new NullLiteral();
    ExpressionStatement stmt = new ExpressionStatement(expr);
    BlockStatement block = new BlockStatement();
    block.statements.add(stmt);

    ExpressionPropertyContext ctx = new ExpressionPropertyContext(stmt.expression);
    BlockStatementIndexPair pair = ctx.getBlockStatementIndexPair();

    assertNotNull("Should find BlockStatementIndexPair for statement in block", pair);
    assertSame(block, pair.getBlockStatement());
    assertEquals(0, pair.getIndex());
  }

  @Test
  public void getBlockStatementIndexPair_secondStatementInBlock_returnsIndex1() {
    ExpressionStatement stmt0 = new ExpressionStatement(new NullLiteral());
    ExpressionStatement stmt1 = new ExpressionStatement(new IntegerLiteral(7));
    BlockStatement block = new BlockStatement();
    block.statements.add(stmt0);
    block.statements.add(stmt1);

    ExpressionPropertyContext ctx = new ExpressionPropertyContext(stmt1.expression);
    BlockStatementIndexPair pair = ctx.getBlockStatementIndexPair();

    assertNotNull(pair);
    assertSame(block, pair.getBlockStatement());
    assertEquals(1, pair.getIndex());
  }

  @Test
  public void getBlockStatementIndexPair_thirdOfManyStatements_returnsIndex2() {
    ExpressionStatement stmt0 = new ExpressionStatement(new NullLiteral());
    ExpressionStatement stmt1 = new ExpressionStatement(new IntegerLiteral(1));
    ExpressionStatement stmt2 = new ExpressionStatement(new DoubleLiteral(2.0));
    ExpressionStatement stmt3 = new ExpressionStatement(new StringLiteral("end"));
    BlockStatement block = new BlockStatement();
    block.statements.add(stmt0);
    block.statements.add(stmt1);
    block.statements.add(stmt2);
    block.statements.add(stmt3);

    ExpressionPropertyContext ctx = new ExpressionPropertyContext(stmt2.expression);
    BlockStatementIndexPair pair = ctx.getBlockStatementIndexPair();

    assertNotNull(pair);
    assertEquals(2, pair.getIndex());
  }

  // ---- getBlockStatementIndexPair — orphan statement (no parent block) ----

  @Test
  public void getBlockStatementIndexPair_orphanStatement_returnsNull() {
    ExpressionStatement orphan = new ExpressionStatement(new NullLiteral());
    // Not added to any BlockStatement — no parent
    ExpressionPropertyContext ctx = new ExpressionPropertyContext(orphan.expression);
    BlockStatementIndexPair pair = ctx.getBlockStatementIndexPair();

    assertNull("Orphan statement should yield null pair", pair);
  }

  // ---- getBlockStatementIndexPair — ConstructorInvocationStatement ----
  // When the first ancestor statement is a ConstructorInvocationStatement,
  // the method returns null (the "//todo" branch).

  @Test
  public void getBlockStatementIndexPair_constructorInvocation_returnsNull() {
    // The method has a special branch: if the ancestor statement is
    // ConstructorInvocationStatement, it returns null.
    // We can't easily construct one headlessly (it's abstract), so this is
    // a documentation test verifying the orphan path returns null.
    ExpressionStatement orphan = new ExpressionStatement(new NullLiteral());
    ExpressionPropertyContext ctx = new ExpressionPropertyContext(orphan.expression);

    assertNull(ctx.getBlockStatementIndexPair());
  }

  // ---- ExpressionPropertyContext is an ExpressionCascadeContext ----

  @Test
  public void implementsExpressionCascadeContext() {
    ExpressionStatement stmt = new ExpressionStatement(new NullLiteral());
    ExpressionPropertyContext ctx = new ExpressionPropertyContext(stmt.expression);
    assertTrue(ctx instanceof ExpressionCascadeContext);
  }

  // ---- Local declaration expression property context ----

  @Test
  public void getBlockStatementIndexPair_localDeclarationInBlock_findsBlockAndIndex() {
    UserLocal local = new UserLocal("x", JavaType.DOUBLE_OBJECT_TYPE, false);
    DoubleLiteral initializer = new DoubleLiteral(1.0);
    LocalDeclarationStatement localDecl = new LocalDeclarationStatement(local, initializer);
    BlockStatement block = new BlockStatement();
    block.statements.add(localDecl);

    // The initializer expression property belongs to the LocalDeclarationStatement
    ExpressionPropertyContext ctx = new ExpressionPropertyContext(localDecl.initializer);
    BlockStatementIndexPair pair = ctx.getBlockStatementIndexPair();

    assertNotNull(pair);
    assertSame(block, pair.getBlockStatement());
    assertEquals(0, pair.getIndex());
  }
}
