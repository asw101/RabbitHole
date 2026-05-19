package edu.cmu.cs.dennisc.tree;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class DefaultNodeDeepTest {

  @Test
  public void createUnsafe_getValue() {
    DefaultNode<String> node = DefaultNode.createUnsafeInstance("root", String.class);
    assertEquals("root", node.getValue());
  }

  @Test
  public void createSafe_getValue() {
    DefaultNode<String> node = DefaultNode.createSafeInstance("root", String.class);
    assertEquals("root", node.getValue());
  }

  @Test
  public void addChild_byNode() {
    DefaultNode<String> root = DefaultNode.createUnsafeInstance("root", String.class);
    DefaultNode<String> child = DefaultNode.createUnsafeInstance("child", String.class);
    root.addChild(child);
    assertEquals(1, root.getChildren().size());
  }

  @Test
  public void addChild_byValue() {
    DefaultNode<String> root = DefaultNode.createUnsafeInstance("root", String.class);
    DefaultNode<String> child = root.addChild("child");
    assertEquals("child", child.getValue());
    assertEquals(1, root.getChildren().size());
  }

  @Test
  public void addMultipleChildren() {
    DefaultNode<String> root = DefaultNode.createUnsafeInstance("root", String.class);
    root.addChild("a");
    root.addChild("b");
    root.addChild("c");
    assertEquals(3, root.getChildren().size());
  }

  @Test
  public void removeChild_byNode() {
    DefaultNode<String> root = DefaultNode.createUnsafeInstance("root", String.class);
    DefaultNode<String> child = DefaultNode.createUnsafeInstance("child", String.class);
    root.addChild(child);
    root.removeChild(child);
    assertEquals(0, root.getChildren().size());
  }

  @Test
  public void removeChild_byValue() {
    DefaultNode<String> root = DefaultNode.createUnsafeInstance("root", String.class);
    root.addChild("child");
    DefaultNode<String> removed = root.removeChild("child");
    assertNotNull(removed);
    assertEquals("child", removed.getValue());
    assertEquals(0, root.getChildren().size());
  }

  @Test
  public void removeChild_byValue_notFound() {
    DefaultNode<String> root = DefaultNode.createUnsafeInstance("root", String.class);
    root.addChild("a");
    DefaultNode<String> removed = root.removeChild("b");
    assertNull(removed);
    assertEquals(1, root.getChildren().size());
  }

  @Test
  public void contains_self() {
    DefaultNode<String> root = DefaultNode.createUnsafeInstance("root", String.class);
    assertTrue(root.contains("root"));
  }

  @Test
  public void contains_child() {
    DefaultNode<String> root = DefaultNode.createUnsafeInstance("root", String.class);
    root.addChild("child");
    assertTrue(root.contains("child"));
  }

  @Test
  public void contains_grandchild() {
    DefaultNode<String> root = DefaultNode.createUnsafeInstance("root", String.class);
    DefaultNode<String> child = root.addChild("child");
    child.addChild("grandchild");
    assertTrue(root.contains("grandchild"));
  }

  @Test
  public void contains_missing() {
    DefaultNode<String> root = DefaultNode.createUnsafeInstance("root", String.class);
    assertFalse(root.contains("missing"));
  }

  @Test
  public void get_self() {
    DefaultNode<String> root = DefaultNode.createUnsafeInstance("root", String.class);
    assertSame(root, root.get("root"));
  }

  @Test
  public void get_missing_returnsNull() {
    DefaultNode<String> root = DefaultNode.createUnsafeInstance("root", String.class);
    assertNull(root.get("missing"));
  }

  @Test
  public void toString_withValue() {
    DefaultNode<String> node = DefaultNode.createUnsafeInstance("test", String.class);
    String s = node.toString();
    assertTrue(s.contains("test"));
    assertTrue(s.contains("DefaultNode"));
  }

  @Test
  public void children_empty() {
    DefaultNode<String> node = DefaultNode.createSafeInstance("empty", String.class);
    assertEquals(0, node.getChildren().size());
  }
}
