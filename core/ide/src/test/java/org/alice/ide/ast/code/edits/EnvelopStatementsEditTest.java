package org.alice.ide.ast.code.edits;

import org.alice.ide.ast.draganddrop.BlockStatementIndexPair;
import org.junit.Test;
import org.lgna.croquet.edits.AbstractEdit;
import org.lgna.project.ast.*;
import static org.junit.Assert.*;

public class EnvelopStatementsEditTest {
  private static BlockStatementIndexPair createPair(int index) {
    BlockStatement block = new BlockStatement();
    for (int i = 0; i <= index; i++) block.statements.add(new Comment("s" + i));
    return new BlockStatementIndexPair(block, index);
  }

  @Test public void construct_succeeds() { assertNotNull(new EnvelopStatementsEdit(null, createPair(0), createPair(0))); }
  @Test public void isAbstractEdit() { assertTrue(new EnvelopStatementsEdit(null, createPair(0), createPair(0)) instanceof AbstractEdit); }
  @Test public void differentPairs() { assertNotNull(new EnvelopStatementsEdit(null, createPair(0), createPair(1))); }
  @Test public void twoEdits_different() { assertNotSame(new EnvelopStatementsEdit(null, createPair(0), createPair(0)), new EnvelopStatementsEdit(null, createPair(0), createPair(0))); }
  @Test public void className() { assertEquals("EnvelopStatementsEdit", EnvelopStatementsEdit.class.getSimpleName()); }
  @Test public void samePair() { BlockStatementIndexPair p = createPair(0); assertNotNull(new EnvelopStatementsEdit(null, p, p)); }
  @Test public void higherIndices() { assertNotNull(new EnvelopStatementsEdit(null, createPair(2), createPair(3))); }
  @Test public void differentBlocks() {
    BlockStatement b1 = new BlockStatement(); b1.statements.add(new Comment("s1"));
    BlockStatement b2 = new BlockStatement(); b2.statements.add(new Comment("s2"));
    assertNotNull(new EnvelopStatementsEdit(null, new BlockStatementIndexPair(b1, 0), new BlockStatementIndexPair(b2, 0)));
  }
  @Test public void helperCreatesCorrectPair() { BlockStatementIndexPair p = createPair(2); assertEquals(2, p.getIndex()); assertEquals(3, p.getBlockStatement().statements.size()); }
  @Test public void multipleConstructions() { for (int i = 0; i < 5; i++) assertNotNull(new EnvelopStatementsEdit(null, createPair(0), createPair(1))); }
}
