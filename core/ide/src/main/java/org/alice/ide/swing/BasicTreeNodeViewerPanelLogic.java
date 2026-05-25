package org.alice.ide.swing;

import java.util.LinkedList;
import java.util.List;

final class BasicTreeNodeViewerPanelLogic {
  private BasicTreeNodeViewerPanelLogic() {
    throw new AssertionError();
  }

  static List<BasicTreeNode> getFlattenedTree(BasicTreeNode node) {
    LinkedList<BasicTreeNode> flattenedTree = new LinkedList<BasicTreeNode>();
    addFlattenedTree(node, flattenedTree);
    return flattenedTree;
  }

  static void diffTrees(BasicTreeNode treeA, BasicTreeNode treeB) {
    LinkedList<BasicTreeNode> flatTreeA = new LinkedList<BasicTreeNode>(getFlattenedTree(treeA));
    LinkedList<BasicTreeNode> flatTreeB = new LinkedList<BasicTreeNode>(getFlattenedTree(treeB));
    resetDifferences(flatTreeA);
    resetDifferences(flatTreeB);
    for (BasicTreeNode nodeA : flatTreeA) {
      int matchingIndex = flatTreeB.indexOf(nodeA);
      if (matchingIndex != -1) {
        BasicTreeNode nodeB = flatTreeB.get(matchingIndex);
        if (nodeA.isDifferent(nodeB)) {
          nodeB.markDifferent(BasicTreeNode.Difference.ATTRIBUTES);
        }
        flatTreeB.remove(matchingIndex);
      } else {
        nodeA.markDifferent(BasicTreeNode.Difference.NEW_NODE);
      }
    }
    for (BasicTreeNode nodeB : flatTreeB) {
      nodeB.markDifferent(BasicTreeNode.Difference.NEW_NODE);
    }
  }

  private static void addFlattenedTree(BasicTreeNode node, List<BasicTreeNode> flattenedTree) {
    flattenedTree.add(node);
    for (int i = 0; i < node.getChildCount(); i++) {
      addFlattenedTree((BasicTreeNode) node.getChildAt(i), flattenedTree);
    }
  }

  private static void resetDifferences(List<BasicTreeNode> nodes) {
    for (BasicTreeNode node : nodes) {
      node.markDifferent(BasicTreeNode.Difference.NONE);
    }
  }
}
