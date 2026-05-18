package org.alice.ide.ast.draganddrop;

import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.NullLiteral;

import static org.junit.Assert.*;

public class BlockStatementIndexPairTest {

  @Test
  public void constructorStoresBlockStatementAndIndex() {
    BlockStatement block = new BlockStatement();

    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 5);

    assertSame(block, pair.getBlockStatement());
    assertEquals(5, pair.getIndex());
  }

  @Test
  public void createInstanceFromChildStatementUsesContainingBlockAndChildIndex() {
    BlockStatement block = new BlockStatement();
    ExpressionStatement first = new ExpressionStatement(new NullLiteral());
    ExpressionStatement second = new ExpressionStatement(new NullLiteral());
    block.statements.add(first);
    block.statements.add(second);

    BlockStatementIndexPair pair = BlockStatementIndexPair.createInstanceFromChildStatement(second);

    assertSame(block, pair.getBlockStatement());
    assertEquals(1, pair.getIndex());
  }

  @Test
  public void getBlockStatementReturnsOriginalBlock() {
    BlockStatement block = new BlockStatement();

    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 0);

    assertSame(block, pair.getBlockStatement());
  }

  @Test
  public void getIndexReturnsOriginalIndex() {
    BlockStatement block = new BlockStatement();

    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 2);

    assertEquals(2, pair.getIndex());
  }

  @Test
  public void equalsAndHashCodeMatchForEquivalentPairs() {
    BlockStatement block = new BlockStatement();
    BlockStatementIndexPair first = new BlockStatementIndexPair(block, 3);
    BlockStatementIndexPair second = new BlockStatementIndexPair(block, 3);

    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
  }

  @Test
  public void equalsReturnsFalseForNullAndDifferentIndex() {
    BlockStatement block = new BlockStatement();
    BlockStatementIndexPair first = new BlockStatementIndexPair(block, 0);
    BlockStatementIndexPair second = new BlockStatementIndexPair(block, 1);

    assertFalse(first.equals(second));
    assertFalse(first.equals(null));
  }

  @Test
  public void toStringContainsClassNameAndIndex() {
    BlockStatement block = new BlockStatement();

    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 4);
    String text = pair.toString();

    assertTrue(text.contains("BlockStatementIndexPair"));
    assertTrue(text.contains("index=4"));
  }
}
