package org.alice.ide.ast.code;

import org.alice.ide.ast.draganddrop.BlockStatementIndexPair;
import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

/**
 * Tests for {@link EnvelopStatementsOperation} covering getInstance caching
 * and location accessors.
 */
public class EnvelopStatementsOperationTest {

  @Test
  public void getInstance_returnsSameInstanceForSameArgs() {
    BlockStatement block1 = new BlockStatement();
    BlockStatement block2 = new BlockStatement();
    BlockStatementIndexPair from = new BlockStatementIndexPair(block1, 0);
    BlockStatementIndexPair to = new BlockStatementIndexPair(block2, 1);

    EnvelopStatementsOperation op1 = EnvelopStatementsOperation.getInstance(from, to);
    EnvelopStatementsOperation op2 = EnvelopStatementsOperation.getInstance(from, to);
    assertSame(op1, op2);
  }

  @Test
  public void getInstance_differentArgs_returnsDifferentInstances() {
    BlockStatement block = new BlockStatement();
    BlockStatementIndexPair from1 = new BlockStatementIndexPair(block, 0);
    BlockStatementIndexPair to1 = new BlockStatementIndexPair(block, 1);
    BlockStatementIndexPair from2 = new BlockStatementIndexPair(block, 2);
    BlockStatementIndexPair to2 = new BlockStatementIndexPair(block, 3);

    EnvelopStatementsOperation op1 = EnvelopStatementsOperation.getInstance(from1, to1);
    EnvelopStatementsOperation op2 = EnvelopStatementsOperation.getInstance(from2, to2);
    assertNotSame(op1, op2);
  }

  @Test
  public void getFromLocation_returnsStoredValue() {
    BlockStatement block = new BlockStatement();
    BlockStatementIndexPair from = new BlockStatementIndexPair(block, 5);
    BlockStatementIndexPair to = new BlockStatementIndexPair(block, 10);

    EnvelopStatementsOperation op = EnvelopStatementsOperation.getInstance(from, to);
    assertSame(from, op.getFromLocation());
  }

  @Test
  public void getToLocation_returnsStoredValue() {
    BlockStatement block = new BlockStatement();
    BlockStatementIndexPair from = new BlockStatementIndexPair(block, 0);
    BlockStatementIndexPair to = new BlockStatementIndexPair(block, 3);

    EnvelopStatementsOperation op = EnvelopStatementsOperation.getInstance(from, to);
    assertSame(to, op.getToLocation());
  }

  @Test
  public void getInstance_withDifferentBlocks_worksCorrectly() {
    BlockStatement fromBlock = new BlockStatement();
    BlockStatement toBlock = new BlockStatement();
    BlockStatementIndexPair from = new BlockStatementIndexPair(fromBlock, 0);
    BlockStatementIndexPair to = new BlockStatementIndexPair(toBlock, 0);

    EnvelopStatementsOperation op = EnvelopStatementsOperation.getInstance(from, to);
    assertNotNull(op);
    assertSame(from, op.getFromLocation());
    assertSame(to, op.getToLocation());
  }
}
