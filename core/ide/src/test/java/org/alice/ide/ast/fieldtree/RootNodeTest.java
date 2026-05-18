package org.alice.ide.ast.fieldtree;

import org.junit.Test;

import static org.junit.Assert.*;

public class RootNodeTest {
  @Test
  public void constructor_parentIsNull() {
    RootNode root = new RootNode();
    assertNull(root.getParent());
  }

  @Test
  public void constructor_declarationIsNull() {
    RootNode root = new RootNode();
    assertNull(root.getDeclaration());
  }

  @Test
  public void collapseThreshold_isMaxValue() {
    RootNode root = new RootNode();
    assertEquals(Integer.MAX_VALUE, root.getCollapseThreshold());
  }

  @Test
  public void collapseThresholdForDescendants_isMaxValue() {
    RootNode root = new RootNode();
    assertEquals(Integer.MAX_VALUE, root.getCollapseThresholdForDescendants());
  }

  @Test
  public void typeNodes_initiallyEmpty() {
    RootNode root = new RootNode();
    assertTrue(root.getTypeNodes().isEmpty());
  }

  @Test
  public void fieldNodes_initiallyEmpty() {
    RootNode root = new RootNode();
    assertTrue(root.getFieldNodes().isEmpty());
  }

  @Test
  public void toString_containsClassName() {
    RootNode root = new RootNode();
    String s = root.toString();
    assertTrue(s.contains("TypeNode") || s.contains("RootNode"));
  }
}
