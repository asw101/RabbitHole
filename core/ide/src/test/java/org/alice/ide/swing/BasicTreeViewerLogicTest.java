package org.alice.ide.swing;

import edu.cmu.cs.dennisc.color.Color4f;
import org.alice.math.immutable.AffineMatrix4x4;
import org.junit.Test;

import javax.swing.tree.TreePath;
import java.awt.Color;

import static org.junit.Assert.*;

public class BasicTreeViewerLogicTest {
  @Test
  public void createSelectionPathFindsNestedNodeByHashCode() {
    BasicTreeNode root = new BasicTreeNode("root");
    BasicTreeNode child = new BasicTreeNode("child");
    BasicTreeNode grandchild = new BasicTreeNode("grandchild");
    root.add(child);
    child.add(grandchild);

    TreePath path = BasicTreeViewerLogic.createSelectionPath(root, grandchild.hashCode);

    assertNotNull(path);
    assertArrayEquals(new Object[] {root, child, grandchild}, path.getPath());
  }

  @Test
  public void createSelectionPathReturnsNullWhenNodeIsMissing() {
    BasicTreeNode root = new BasicTreeNode("root");

    assertNull(BasicTreeViewerLogic.createSelectionPath(root, Integer.MIN_VALUE));
  }

  @Test
  public void formattingHelpersProduceViewerText() {
    BasicTreeNode named = new BasicTreeNode("named");
    named.name = "Dragon";
    BasicTreeNode unnamed = new BasicTreeNode("unnamed");
    unnamed.name = null;

    assertEquals("'Dragon'", BasicTreeViewerLogic.getNodeNameText(named));
    assertEquals("<NO NAME>", BasicTreeViewerLogic.getNodeNameText(unnamed));
    assertEquals("NO COLOR", BasicTreeViewerLogic.getColorText(null));
    assertEquals(new Color(127, 63, 255), BasicTreeViewerLogic.getBackgroundColor(new Color4f(0.5f, 0.25f, 1.0f, 0.75f)));
    assertEquals("[1.000, 2.500, -3.000]", BasicTreeViewerLogic.getPositionText(AffineMatrix4x4.createTranslation(1.0, 2.5, -3.0)));
    assertEquals("pkg.Type.wave(Type.java:42)\n", BasicTreeViewerLogic.buildStackTraceText(new StackTraceElement[] {new StackTraceElement("pkg.Type", "wave", "Type.java", 42)}));
  }
}
