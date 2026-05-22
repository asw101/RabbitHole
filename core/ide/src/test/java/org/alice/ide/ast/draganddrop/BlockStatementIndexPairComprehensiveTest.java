package org.alice.ide.ast.draganddrop;

import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.Comment;
import org.lgna.project.ast.DoInOrder;
import org.lgna.project.ast.ExpressionStatement;

import static org.junit.Assert.*;

public class BlockStatementIndexPairComprehensiveTest {
  @Test public void construct_storesBlockStatement() {
    BlockStatement block = new BlockStatement();
    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 0);
    assertSame(block, pair.getBlockStatement());
  }

  @Test public void construct_storesZeroIndex() {
    BlockStatement block = new BlockStatement();
    block.statements.add(new Comment("first"));
    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 0);
    assertEquals(0, pair.getIndex());
  }

  @Test public void construct_storesHigherIndex() {
    BlockStatement block = new BlockStatement();
    block.statements.add(new Comment("a"));
    block.statements.add(new Comment("b"));
    block.statements.add(new Comment("c"));
    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 2);
    assertEquals(2, pair.getIndex());
    assertEquals(3, pair.getBlockStatement().statements.size());
  }

  @Test public void differentBlocks_areDistinct() {
    BlockStatementIndexPair first = new BlockStatementIndexPair(new BlockStatement(), 0);
    BlockStatementIndexPair second = new BlockStatementIndexPair(new BlockStatement(), 0);
    assertNotSame(first.getBlockStatement(), second.getBlockStatement());
  }

  @Test public void blockCanContainVariousStatementTypes() {
    BlockStatement block = new BlockStatement();
    block.statements.add(new Comment("comment"));
    block.statements.add(new ExpressionStatement());
    block.statements.add(new DoInOrder());

    assertSame(Comment.class, new BlockStatementIndexPair(block, 0).getBlockStatement().statements.get(0).getClass());
    assertSame(ExpressionStatement.class, new BlockStatementIndexPair(block, 1).getBlockStatement().statements.get(1).getClass());
    assertSame(DoInOrder.class, new BlockStatementIndexPair(block, 2).getBlockStatement().statements.get(2).getClass());
  }

  @Test public void equalPairs_sameBlockAndIndex() {
    BlockStatement block = new BlockStatement();
    BlockStatementIndexPair first = new BlockStatementIndexPair(block, 0);
    BlockStatementIndexPair second = new BlockStatementIndexPair(block, 0);
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
  }
}
