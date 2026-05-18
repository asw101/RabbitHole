package org.alice.ide.croquet.codecs;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

/**
 * Tests for {@link NodeCodec} — singleton-cached codec for AST nodes.
 * Covers getInstance factory, getValueClass, and global node map operations.
 */
public class NodeCodecTest {

  // ---- getInstance factory ----

  @Test
  public void getInstance_returnsNonNull() {
    NodeCodec<Node> codec = NodeCodec.getInstance(Node.class);
    assertNotNull(codec);
  }

  @Test
  public void getInstance_differentClasses_returnDifferentCodecs() {
    NodeCodec<Node> c1 = NodeCodec.getInstance(Node.class);
    NodeCodec<BlockStatement> c2 = NodeCodec.getInstance(BlockStatement.class);
    assertNotSame(c1, c2);
  }

  @Test
  public void getInstance_variousTypes_allNonNull() {
    assertNotNull(NodeCodec.getInstance(UserType.class));
    assertNotNull(NodeCodec.getInstance(AbstractType.class));
    assertNotNull(NodeCodec.getInstance(Expression.class));
  }

  // ---- getValueClass ----

  @Test
  public void getValueClass_returnsCorrectClass() {
    assertEquals(Node.class, NodeCodec.getInstance(Node.class).getValueClass());
    assertEquals(BlockStatement.class, NodeCodec.getInstance(BlockStatement.class).getValueClass());
    assertEquals(AbstractDeclaration.class, NodeCodec.getInstance(AbstractDeclaration.class).getValueClass());
    assertEquals(Expression.class, NodeCodec.getInstance(Expression.class).getValueClass());
    assertEquals(NullLiteral.class, NodeCodec.getInstance(NullLiteral.class).getValueClass());
    assertEquals(IntegerLiteral.class, NodeCodec.getInstance(IntegerLiteral.class).getValueClass());
    assertEquals(DoubleLiteral.class, NodeCodec.getInstance(DoubleLiteral.class).getValueClass());
    assertEquals(UserMethod.class, NodeCodec.getInstance(UserMethod.class).getValueClass());
  }

  // ---- global node map ----

  @Test
  public void addNodeToGlobalMap_doesNotThrow() {
    NullLiteral node = new NullLiteral();
    NodeCodec.addNodeToGlobalMap(node);
    NodeCodec.removeNodeFromGlobalMap(node);
  }

  @Test
  public void addAndRemoveMultipleNodes() {
    NullLiteral n1 = new NullLiteral();
    NullLiteral n2 = new NullLiteral();
    IntegerLiteral n3 = new IntegerLiteral(42);

    NodeCodec.addNodeToGlobalMap(n1);
    NodeCodec.addNodeToGlobalMap(n2);
    NodeCodec.addNodeToGlobalMap(n3);

    NodeCodec.removeNodeFromGlobalMap(n1);
    NodeCodec.removeNodeFromGlobalMap(n2);
    NodeCodec.removeNodeFromGlobalMap(n3);
  }

  @Test
  public void removeNodeFromGlobalMap_nonExistent_doesNotThrow() {
    NullLiteral node = new NullLiteral();
    NodeCodec.removeNodeFromGlobalMap(node);
  }

  @Test
  public void addNodeToGlobalMap_variousTypes() {
    BlockStatement block = new BlockStatement();
    UserMethod method = new UserMethod();
    NodeCodec.addNodeToGlobalMap(block);
    NodeCodec.addNodeToGlobalMap(method);
    NodeCodec.removeNodeFromGlobalMap(block);
    NodeCodec.removeNodeFromGlobalMap(method);
  }

  @Test
  public void addSameNodeTwice_doesNotThrow() {
    NullLiteral node = new NullLiteral();
    NodeCodec.addNodeToGlobalMap(node);
    NodeCodec.addNodeToGlobalMap(node);
    NodeCodec.removeNodeFromGlobalMap(node);
  }

  // ---- ItemCodec interface compliance ----

  @Test
  public void implementsItemCodec() {
    NodeCodec<Node> codec = NodeCodec.getInstance(Node.class);
    assertTrue(codec instanceof org.lgna.croquet.ItemCodec);
  }
}
