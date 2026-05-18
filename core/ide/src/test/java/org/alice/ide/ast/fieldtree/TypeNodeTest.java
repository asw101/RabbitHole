package org.alice.ide.ast.fieldtree;

import org.junit.Test;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.JavaType;

import static org.junit.Assert.*;

public class TypeNodeTest {
  @Test
  public void createAndAddToParent_addsToParentTypeNodes() {
    RootNode root = new RootNode();
    AbstractType<?, ?, ?> type = JavaType.getInstance(String.class);
    TypeNode child = TypeNode.createAndAddToParent(root, type, 5, 3);
    assertEquals(1, root.getTypeNodes().size());
    assertSame(child, root.getTypeNodes().get(0));
  }

  @Test
  public void createAndAddToParent_setsParent() {
    RootNode root = new RootNode();
    TypeNode child = TypeNode.createAndAddToParent(root, JavaType.getInstance(String.class), 5, 3);
    assertSame(root, child.getParent());
  }

  @Test
  public void getDeclaration_returnsType() {
    RootNode root = new RootNode();
    AbstractType<?, ?, ?> type = JavaType.getInstance(Integer.class);
    TypeNode child = TypeNode.createAndAddToParent(root, type, 5, 3);
    assertSame(type, child.getDeclaration());
  }

  @Test
  public void collapseThresholds_matchConstructorArgs() {
    RootNode root = new RootNode();
    TypeNode child = TypeNode.createAndAddToParent(root, JavaType.getInstance(Double.class), 7, 4);
    assertEquals(7, child.getCollapseThreshold());
    assertEquals(4, child.getCollapseThresholdForDescendants());
  }

  @Test
  public void fieldNodes_initiallyEmpty() {
    RootNode root = new RootNode();
    TypeNode child = TypeNode.createAndAddToParent(root, JavaType.getInstance(String.class), 5, 3);
    assertTrue(child.getFieldNodes().isEmpty());
  }

  @Test
  public void typeNodes_initiallyEmpty() {
    RootNode root = new RootNode();
    TypeNode child = TypeNode.createAndAddToParent(root, JavaType.getInstance(String.class), 5, 3);
    assertTrue(child.getTypeNodes().isEmpty());
  }

  @Test
  public void removeEmptyTypeNodes_removesEmptyChild() {
    RootNode root = new RootNode();
    TypeNode child = TypeNode.createAndAddToParent(root, JavaType.getInstance(String.class), 5, 3);
    assertEquals(1, root.getTypeNodes().size());
    root.removeEmptyTypeNodes();
    assertTrue(root.getTypeNodes().isEmpty());
    assertNull(child.getParent().getParent());
  }

  @Test
  public void sort_doesNotThrowOnEmpty() {
    RootNode root = new RootNode();
    root.sort();
  }

  @Test
  public void collapseIfAppropriate_movesFieldsToParentWhenBelowThreshold() {
    RootNode root = new RootNode();
    TypeNode child = TypeNode.createAndAddToParent(root, JavaType.getInstance(String.class), 5, 3);
    child.collapseIfAppropriate();
    assertTrue(child.getFieldNodes().isEmpty());
  }

  @Test
  public void createAndAddToParent_nullParent_noException() {
    AbstractType<?, ?, ?> type = JavaType.getInstance(String.class);
    TypeNode node = TypeNode.createAndAddToParent(null, type, 5, 3);
    assertNull(node.getParent());
    assertSame(type, node.getDeclaration());
  }
}
