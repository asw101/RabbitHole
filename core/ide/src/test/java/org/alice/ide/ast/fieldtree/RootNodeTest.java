package org.alice.ide.ast.fieldtree;

import org.junit.Test;

import static org.junit.Assert.*;

public class RootNodeTest {

  @Test
  public void constructorCreatesValidInstance() {
    assertNotNull(new RootNode());
  }

  @Test
  public void getParentReturnsNull() {
    RootNode root = new RootNode();

    assertNull(root.getParent());
  }

  @Test
  public void getDeclarationReturnsNull() {
    RootNode root = new RootNode();

    assertNull(root.getDeclaration());
  }

  @Test
  public void getCollapseThresholdReturnsIntegerMaxValue() {
    RootNode root = new RootNode();

    assertEquals(Integer.MAX_VALUE, root.getCollapseThreshold());
  }

  @Test
  public void getCollapseThresholdForDescendantsReturnsIntegerMaxValue() {
    RootNode root = new RootNode();

    assertEquals(Integer.MAX_VALUE, root.getCollapseThresholdForDescendants());
  }

  @Test
  public void getTypeNodesIsInitiallyEmpty() {
    RootNode root = new RootNode();

    assertTrue(root.getTypeNodes().isEmpty());
  }

  @Test
  public void getFieldNodesIsInitiallyEmpty() {
    RootNode root = new RootNode();

    assertTrue(root.getFieldNodes().isEmpty());
  }
}
