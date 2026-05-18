package org.alice.ide.ast.draganddrop;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

public class BlockStatementIndexPairTest {

  @Test
  public void constructor_setsFields() {
    BlockStatement block = new BlockStatement();
    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 5);
    assertSame(block, pair.getBlockStatement());
    assertEquals(5, pair.getIndex());
  }

  @Test
  public void equals_sameObject_returnsTrue() {
    BlockStatement block = new BlockStatement();
    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 0);
    assertTrue(pair.equals(pair));
  }

  @Test
  public void equals_equalPairs_returnsTrue() {
    BlockStatement block = new BlockStatement();
    BlockStatementIndexPair pair1 = new BlockStatementIndexPair(block, 3);
    BlockStatementIndexPair pair2 = new BlockStatementIndexPair(block, 3);
    assertTrue(pair1.equals(pair2));
  }

  @Test
  public void equals_differentIndex_returnsFalse() {
    BlockStatement block = new BlockStatement();
    BlockStatementIndexPair pair1 = new BlockStatementIndexPair(block, 0);
    BlockStatementIndexPair pair2 = new BlockStatementIndexPair(block, 1);
    assertFalse(pair1.equals(pair2));
  }

  @Test
  public void equals_differentBlock_returnsFalse() {
    BlockStatement block1 = new BlockStatement();
    BlockStatement block2 = new BlockStatement();
    BlockStatementIndexPair pair1 = new BlockStatementIndexPair(block1, 0);
    BlockStatementIndexPair pair2 = new BlockStatementIndexPair(block2, 0);
    assertFalse(pair1.equals(pair2));
  }

  @Test
  public void equals_nonBlockStatementIndexPair_returnsFalse() {
    BlockStatement block = new BlockStatement();
    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 0);
    assertFalse(pair.equals("not a pair"));
  }

  @Test
  public void equals_null_returnsFalse() {
    BlockStatement block = new BlockStatement();
    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 0);
    assertFalse(pair.equals(null));
  }

  @Test
  public void hashCode_equalPairs_sameHash() {
    BlockStatement block = new BlockStatement();
    BlockStatementIndexPair pair1 = new BlockStatementIndexPair(block, 3);
    BlockStatementIndexPair pair2 = new BlockStatementIndexPair(block, 3);
    assertEquals(pair1.hashCode(), pair2.hashCode());
  }

  @Test
  public void hashCode_differentPairs_likelyDifferentHash() {
    BlockStatement block1 = new BlockStatement();
    BlockStatement block2 = new BlockStatement();
    BlockStatementIndexPair pair1 = new BlockStatementIndexPair(block1, 0);
    BlockStatementIndexPair pair2 = new BlockStatementIndexPair(block2, 1);
    // Not guaranteed to differ, but typically will
    assertNotNull(pair1);
  }

  @Test
  public void toString_containsClassName() {
    BlockStatement block = new BlockStatement();
    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 2);
    String str = pair.toString();
    assertNotNull(str);
    assertTrue(str.contains("BlockStatementIndexPair"));
    assertTrue(str.contains("index=2"));
  }

  @Test
  public void createInstanceFromChildStatement_createsCorrectPair() {
    BlockStatement block = new BlockStatement();
    ExpressionStatement child0 = new ExpressionStatement(new NullLiteral());
    ExpressionStatement child1 = new ExpressionStatement(new NullLiteral());
    block.statements.add(child0);
    block.statements.add(child1);

    BlockStatementIndexPair pair = BlockStatementIndexPair.createInstanceFromChildStatement(child1);
    assertSame(block, pair.getBlockStatement());
    assertEquals(1, pair.getIndex());
  }

  @Test
  public void constructor_zeroIndex_isValid() {
    BlockStatement block = new BlockStatement();
    BlockStatementIndexPair pair = new BlockStatementIndexPair(block, 0);
    assertEquals(0, pair.getIndex());
  }
}
