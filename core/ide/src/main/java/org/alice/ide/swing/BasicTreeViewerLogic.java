package org.alice.ide.swing;

import edu.cmu.cs.dennisc.color.Color4f;
import org.alice.math.immutable.AffineMatrix4x4;

import javax.swing.tree.TreePath;
import java.awt.Color;

final class BasicTreeViewerLogic {
  private BasicTreeViewerLogic() {
    throw new AssertionError();
  }

  static String getNodeNameText(BasicTreeNode node) {
    return node.name == null ? "<NO NAME>" : "'" + node.name + "'";
  }

  static String getColorText(Color4f color) {
    if (color == null) {
      return "NO COLOR";
    }
    return "%.2f, %.2f, %.2f, %.2f".formatted(color.red, color.green, color.blue, color.alpha);
  }

  static Color getBackgroundColor(Color4f color) {
    if (color == null) {
      return null;
    }
    return new Color((int) (color.red * 255), (int) (color.green * 255), (int) (color.blue * 255));
  }

  static String getPositionText(AffineMatrix4x4 absoluteTransform) {
    if (absoluteTransform == null) {
      return "NO POSITION";
    }
    return "[%.3f, %.3f, %.3f]".formatted(absoluteTransform.translation().x(), absoluteTransform.translation().y(), absoluteTransform.translation().z());
  }

  static String getShowingText(boolean isShowing) {
    return isShowing ? "SHOWING" : "NOT SHOWING";
  }

  static String getOpacityText(double opacity) {
    return "%.2f".formatted(opacity);
  }

  static String buildStackTraceText(StackTraceElement[] stackTrace) {
    if (stackTrace == null) {
      return "";
    }
    StringBuilder sb = new StringBuilder();
    for (StackTraceElement element : stackTrace) {
      sb.append(element).append('\n');
    }
    return sb.toString();
  }

  static TreePath createSelectionPath(BasicTreeNode rootNode, int hashCode) {
    BasicTreeNode foundNode = rootNode.getMatchingNode(hashCode);
    return foundNode != null ? new TreePath(foundNode.getPath()) : null;
  }

  static TreePath createSelectionPath(BasicTreeNode rootNode, BasicTreeNode node) {
    BasicTreeNode foundNode = rootNode.getMatchingNode(node);
    return foundNode != null ? new TreePath(foundNode.getPath()) : null;
  }
}
