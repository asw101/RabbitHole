package org.alice.ide.swing;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.junit.Assert.*;

public class BasicTreeNodeViewerPanelLogicTest {
  private static final class AlwaysDifferentTreeNode extends BasicTreeNode {
    public AlwaysDifferentTreeNode(Object object) {
      super(object);
    }

    @Override
    public boolean isDifferent(BasicTreeNode other) {
      return true;
    }
  }

  private static void assertConstructorThrowsAssertionError(Class<?> type) throws Exception {
    Constructor<?> constructor = type.getDeclaredConstructor();
    constructor.setAccessible(true);
    try {
      constructor.newInstance();
      fail("Expected constructor to reject instantiation");
    } catch (InvocationTargetException ite) {
      assertTrue(ite.getCause() instanceof AssertionError);
    }
  }

  @Test
  public void utilityConstructorRejectsInstantiation() throws Exception {
    assertConstructorThrowsAssertionError(BasicTreeNodeViewerPanelLogic.class);
  }

  @Test
  public void getFlattenedTreeReturnsPreorderTraversal() {
    BasicTreeNode root = new BasicTreeNode("root");
    BasicTreeNode left = new BasicTreeNode("left");
    BasicTreeNode right = new BasicTreeNode("right");
    BasicTreeNode leaf = new BasicTreeNode("leaf");
    root.add(left);
    root.add(right);
    left.add(leaf);

    List<BasicTreeNode> flattened = BasicTreeNodeViewerPanelLogic.getFlattenedTree(root);

    assertArrayEquals(new Object[] {root, left, leaf, right}, flattened.toArray());
  }

  @Test
  public void diffTreesMarksNodesMissingFromNewTree() {
    BasicTreeNode originalRoot = new BasicTreeNode("root");
    BasicTreeNode removedChild = new BasicTreeNode("removed");
    originalRoot.add(removedChild);

    BasicTreeNode updatedRoot = new BasicTreeNode("root");

    BasicTreeNodeViewerPanelLogic.diffTrees(originalRoot, updatedRoot);

    assertEquals(BasicTreeNode.Difference.NEW_NODE, removedChild.difference);
    assertEquals(BasicTreeNode.Difference.NONE, updatedRoot.difference);
  }

  @Test
  public void diffTreesMarksNodesAddedToNewTree() {
    BasicTreeNode originalRoot = new BasicTreeNode("root");
    BasicTreeNode updatedRoot = new BasicTreeNode("root");
    BasicTreeNode addedChild = new BasicTreeNode("added");
    updatedRoot.add(addedChild);

    BasicTreeNodeViewerPanelLogic.diffTrees(originalRoot, updatedRoot);

    assertEquals(BasicTreeNode.Difference.NEW_NODE, addedChild.difference);
    assertEquals(BasicTreeNode.Difference.NONE, originalRoot.difference);
  }

  @Test
  public void diffTreesMarksAttributeDifferencesOnMatchingNodes() {
    AlwaysDifferentTreeNode originalRoot = new AlwaysDifferentTreeNode(Integer.valueOf(42));
    BasicTreeNode updatedRoot = new BasicTreeNode(Integer.valueOf(42));

    BasicTreeNodeViewerPanelLogic.diffTrees(originalRoot, updatedRoot);

    assertEquals(BasicTreeNode.Difference.ATTRIBUTES, updatedRoot.difference);
    assertEquals(BasicTreeNode.Difference.NONE, originalRoot.difference);
  }
}
