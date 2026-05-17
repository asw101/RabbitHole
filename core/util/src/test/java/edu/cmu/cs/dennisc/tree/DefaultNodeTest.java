package edu.cmu.cs.dennisc.tree;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class DefaultNodeTest {

  @Test
  public void createUnsafeInstance_storesValue() {
    DefaultNode<String> node = DefaultNode.createUnsafeInstance("root", String.class);
    assertEquals("root", node.getValue());
    assertTrue(node.getChildren().isEmpty());
  }

  @Test
  public void createSafeInstance_storesValue() {
    DefaultNode<String> node = DefaultNode.createSafeInstance("root", String.class);
    assertEquals("root", node.getValue());
    assertTrue(node.getChildren().isEmpty());
  }

  @Test
  public void addChild_value_addsToChildren() {
    DefaultNode<String> root = DefaultNode.createUnsafeInstance("root", String.class);
    DefaultNode<String> child = root.addChild("child1");
    assertEquals(1, root.getChildren().size());
    assertEquals("child1", child.getValue());
  }

  @Test
  public void addChild_node_addsToChildren() {
    DefaultNode<String> root = DefaultNode.createUnsafeInstance("root", String.class);
    DefaultNode<String> child = DefaultNode.createUnsafeInstance("child1", String.class);
    root.addChild(child);
    assertEquals(1, root.getChildren().size());
    assertSame(child, root.getChildren().get(0));
  }

  @Test
  public void removeChild_byNode() {
    DefaultNode<String> root = DefaultNode.createSafeInstance("root", String.class);
    DefaultNode<String> child = root.addChild("child1");
    root.addChild("child2");
    assertEquals(2, root.getChildren().size());
    root.removeChild(child);
    assertEquals(1, root.getChildren().size());
  }

  @Test
  public void removeChild_byValue() {
    DefaultNode<String> root = DefaultNode.createUnsafeInstance("root", String.class);
    root.addChild("A");
    root.addChild("B");
    root.addChild("C");
    DefaultNode<String> removed = root.removeChild("B");
    assertNotNull(removed);
    assertEquals("B", removed.getValue());
    assertEquals(2, root.getChildren().size());
  }

  @Test
  public void removeChild_byValue_notFound() {
    DefaultNode<String> root = DefaultNode.createUnsafeInstance("root", String.class);
    root.addChild("A");
    DefaultNode<String> removed = root.removeChild("X");
    assertNull(removed);
    assertEquals(1, root.getChildren().size());
  }

  @Test
  public void contains_directChild() {
    DefaultNode<String> root = DefaultNode.createUnsafeInstance("root", String.class);
    root.addChild("child");
    assertTrue(root.contains("child"));
  }

  @Test
  public void contains_self() {
    DefaultNode<String> root = DefaultNode.createUnsafeInstance("root", String.class);
    assertTrue(root.contains("root"));
  }

  @Test
  public void contains_deepChild() {
    DefaultNode<String> root = DefaultNode.createUnsafeInstance("root", String.class);
    DefaultNode<String> child = root.addChild("child");
    child.addChild("grandchild");
    assertTrue(root.contains("grandchild"));
  }

  @Test
  public void contains_notFound() {
    DefaultNode<String> root = DefaultNode.createUnsafeInstance("root", String.class);
    root.addChild("child");
    assertFalse(root.contains("missing"));
  }

  @Test
  public void get_self() {
    DefaultNode<String> root = DefaultNode.createUnsafeInstance("root", String.class);
    assertSame(root, root.get("root"));
  }

  @Test
  public void get_child() {
    DefaultNode<String> root = DefaultNode.createUnsafeInstance("root", String.class);
    DefaultNode<String> child = root.addChild("child");
    assertSame(child, root.get("child"));
  }

  @Test
  public void get_deepChild() {
    DefaultNode<String> root = DefaultNode.createUnsafeInstance("root", String.class);
    DefaultNode<String> child = root.addChild("child");
    DefaultNode<String> gc = child.addChild("gc");
    assertSame(gc, root.get("gc"));
  }

  @Test
  public void get_notFound() {
    DefaultNode<String> root = DefaultNode.createUnsafeInstance("root", String.class);
    assertNull(root.get("missing"));
  }

  @Test
  public void toString_containsValue() {
    DefaultNode<String> root = DefaultNode.createUnsafeInstance("hello", String.class);
    assertTrue(root.toString().contains("hello"));
  }

  @Test
  public void toString_nullValue() {
    DefaultNode<String> root = DefaultNode.createUnsafeInstance(null, String.class);
    assertTrue(root.toString().contains("null"));
  }

  @Test
  public void safeInstance_supportsConcurrentAccess() {
    DefaultNode<Integer> root = DefaultNode.createSafeInstance(0, Integer.class);
    root.addChild(1);
    root.addChild(2);
    root.addChild(3);
    List<DefaultNode<Integer>> children = root.getChildren();
    assertEquals(3, children.size());
  }
}
