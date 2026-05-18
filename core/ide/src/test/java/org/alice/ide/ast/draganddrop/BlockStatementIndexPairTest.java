package org.alice.ide.ast.draganddrop;

import org.junit.Test;
import org.lgna.project.ast.BlockStatement;

import static org.junit.Assert.*;

public class BlockStatementIndexPairTest {
  @Test
  public void constructor_setsFields() {
    BlockStatement bs = new BlockStatement();
    BlockStatementIndexPair pair = new BlockStatementIndexPair(bs, 3);
    assertSame(bs, pair.getBlockStatement());
    assertEquals(3, pair.getIndex());
  }

  @Test
  public void equals_sameBlockAndIndex() {
    BlockStatement bs = new BlockStatement();
    BlockStatementIndexPair pair1 = new BlockStatementIndexPair(bs, 2);
    BlockStatementIndexPair pair2 = new BlockStatementIndexPair(bs, 2);
    assertEquals(pair1, pair2);
  }

  @Test
  public void equals_differentIndex() {
    BlockStatement bs = new BlockStatement();
    BlockStatementIndexPair pair1 = new BlockStatementIndexPair(bs, 1);
    BlockStatementIndexPair pair2 = new BlockStatementIndexPair(bs, 2);
    assertNotEquals(pair1, pair2);
  }

  @Test
  public void equals_differentBlock() {
    BlockStatement bs1 = new BlockStatement();
    BlockStatement bs2 = new BlockStatement();
    BlockStatementIndexPair pair1 = new BlockStatementIndexPair(bs1, 0);
    BlockStatementIndexPair pair2 = new BlockStatementIndexPair(bs2, 0);
    assertNotEquals(pair1, pair2);
  }

  @Test
  public void equals_self() {
    BlockStatement bs = new BlockStatement();
    BlockStatementIndexPair pair = new BlockStatementIndexPair(bs, 0);
    assertEquals(pair, pair);
  }

  @Test
  public void equals_null() {
    BlockStatement bs = new BlockStatement();
    BlockStatementIndexPair pair = new BlockStatementIndexPair(bs, 0);
    assertNotEquals(pair, null);
  }

  @Test
  public void equals_differentClass() {
    BlockStatement bs = new BlockStatement();
    BlockStatementIndexPair pair = new BlockStatementIndexPair(bs, 0);
    assertNotEquals(pair, "not a pair");
  }

  @Test
  public void hashCode_consistent() {
    BlockStatement bs = new BlockStatement();
    BlockStatementIndexPair pair = new BlockStatementIndexPair(bs, 5);
    int h1 = pair.hashCode();
    int h2 = pair.hashCode();
    assertEquals(h1, h2);
  }

  @Test
  public void hashCode_equalObjects() {
    BlockStatement bs = new BlockStatement();
    BlockStatementIndexPair pair1 = new BlockStatementIndexPair(bs, 3);
    BlockStatementIndexPair pair2 = new BlockStatementIndexPair(bs, 3);
    assertEquals(pair1.hashCode(), pair2.hashCode());
  }

  @Test
  public void toString_containsIndex() {
    BlockStatement bs = new BlockStatement();
    BlockStatementIndexPair pair = new BlockStatementIndexPair(bs, 7);
    String s = pair.toString();
    assertTrue(s.contains("7"));
    assertTrue(s.contains("BlockStatementIndexPair"));
  }
}
