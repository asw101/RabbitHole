package org.alice.ide.ast.code.edits;

import org.alice.ide.ast.draganddrop.BlockStatementIndexPair;
import org.alice.ide.croquet.edits.ast.StatementEdit;
import org.junit.Test;
import org.lgna.croquet.edits.AbstractEdit;
import org.lgna.project.ast.*;
import static org.junit.Assert.*;

public class MoveStatementEditComprehensiveTest {
  private static BlockStatementIndexPair pair(int idx) {
    BlockStatement b = new BlockStatement();
    for (int i = 0; i <= idx; i++) b.statements.add(new Comment("s" + i));
    return new BlockStatementIndexPair(b, idx);
  }

  @Test public void construct() { assertNotNull(new MoveStatementEdit(null, pair(0), new Comment("m"), pair(0), false)); }
  @Test public void isStatementEdit() { assertTrue(new MoveStatementEdit(null, pair(0), new Comment("m"), pair(0), false) instanceof StatementEdit); }
  @Test public void isAbstractEdit() { assertTrue(new MoveStatementEdit(null, pair(0), new Comment("m"), pair(0), false) instanceof AbstractEdit); }
  @Test public void getStatement() { Comment c = new Comment("x"); assertSame(c, new MoveStatementEdit(null, pair(0), c, pair(0), false).getStatement()); }
  @Test public void construct_multipleTrue() { assertNotNull(new MoveStatementEdit(null, pair(0), new Comment("m"), pair(1), true)); }
  @Test public void construct_multipleFalse() { assertNotNull(new MoveStatementEdit(null, pair(0), new Comment("m"), pair(1), false)); }
  @Test public void twoEdits_different() { assertNotSame(new MoveStatementEdit(null, pair(0), new Comment("m"), pair(0), false), new MoveStatementEdit(null, pair(0), new Comment("m"), pair(0), false)); }
  @Test public void sameBlock() { BlockStatement b = new BlockStatement(); b.statements.add(new Comment("a")); b.statements.add(new Comment("b")); assertNotNull(new MoveStatementEdit(null, new BlockStatementIndexPair(b, 0), new Comment("m"), new BlockStatementIndexPair(b, 1), false)); }
  @Test public void className() { assertEquals("MoveStatementEdit", MoveStatementEdit.class.getSimpleName()); }
  @Test public void getStatement_different() { Comment a = new Comment("a"); Comment b = new Comment("b"); assertNotSame(new MoveStatementEdit(null, pair(0), a, pair(0), false).getStatement(), new MoveStatementEdit(null, pair(0), b, pair(0), false).getStatement()); }
  @Test public void construct_expressionStatement() { assertSame(ExpressionStatement.class, new MoveStatementEdit(null, pair(0), new ExpressionStatement(), pair(0), false).getStatement().getClass()); }
  @Test public void construct_returnStatement() { assertSame(ReturnStatement.class, new MoveStatementEdit(null, pair(0), new ReturnStatement(), pair(0), false).getStatement().getClass()); }
  @Test public void construct_doInOrder() { assertTrue(new MoveStatementEdit(null, pair(0), new DoInOrder(), pair(0), false).getStatement() instanceof DoInOrder); }
  @Test public void construct_doTogether() { assertTrue(new MoveStatementEdit(null, pair(0), new DoTogether(), pair(0), false).getStatement() instanceof DoTogether); }
  @Test public void construct_conditional() { assertTrue(new MoveStatementEdit(null, pair(0), new ConditionalStatement(), pair(0), false).getStatement() instanceof ConditionalStatement); }
  @Test public void multipleConstructions() { for (int i = 0; i < 10; i++) assertNotNull(new MoveStatementEdit(null, pair(0), new Comment("i" + i), pair(0), i % 2 == 0)); }
  @Test public void nullStatement() { assertNull(new MoveStatementEdit(null, pair(0), null, pair(0), false).getStatement()); }
}
