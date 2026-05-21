package org.lgna.croquet.views;

import edu.cmu.cs.dennisc.javax.swing.models.TreeModel;
import org.junit.Test;
import org.lgna.croquet.Application;
import org.lgna.croquet.CroquetTestUtils;
import org.lgna.croquet.DefaultSingleSelectTreeState;

import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TreeBehaviorTest {
  @Test
  public void constructor_bindsModelAndSelectionPath() {
    CroquetTestUtils.ensureTestApplication();
    SimpleStringTreeModel treeModel = new SimpleStringTreeModel();
    DefaultSingleSelectTreeState<String> state = new DefaultSingleSelectTreeState<>(Application.DOCUMENT_UI_GROUP, CroquetTestUtils.nextTestUUID(), CroquetTestUtils.STRING_CODEC, "left", treeModel);

    Tree<String> tree = new Tree<>(state);

    assertSame(treeModel, tree.getAwtComponent().getModel());
    assertEquals(new TreePath(new Object[]{"root", "left"}), tree.getSelectionPath());
  }

  @Test
  public void expandAndCollapseOperations_updateJTreeState() {
    CroquetTestUtils.ensureTestApplication();
    SimpleStringTreeModel treeModel = new SimpleStringTreeModel();
    DefaultSingleSelectTreeState<String> state = new DefaultSingleSelectTreeState<>(Application.DOCUMENT_UI_GROUP, CroquetTestUtils.nextTestUUID(), CroquetTestUtils.STRING_CODEC, "left", treeModel);
    Tree<String> tree = new Tree<>(state);

    tree.expandNode("root");
    assertTrue(tree.getAwtComponent().isExpanded(0));
    tree.collapseNode("root");

    assertFalse(tree.getAwtComponent().isExpanded(0));
  }

  @Test
  public void rendererAndRootVisibility_delegateToSwingTree() {
    CroquetTestUtils.ensureTestApplication();
    SimpleStringTreeModel treeModel = new SimpleStringTreeModel();
    DefaultSingleSelectTreeState<String> state = new DefaultSingleSelectTreeState<>(Application.DOCUMENT_UI_GROUP, CroquetTestUtils.nextTestUUID(), CroquetTestUtils.STRING_CODEC, "left", treeModel);
    Tree<String> tree = new Tree<>(state);
    DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();

    tree.setCellRenderer(renderer);
    tree.setRootVisible(false);

    assertSame(renderer, tree.getCellRenderer());
    assertFalse(tree.getAwtComponent().isRootVisible());
  }

  private static final class SimpleStringTreeModel implements TreeModel<String> {
    private final List<String> children = Arrays.asList("left", "right");

    @Override
    public String getRoot() {
      return "root";
    }

    @Override
    public String getChild(Object parent, int index) {
      return this.children.get(index);
    }

    @Override
    public int getChildCount(Object parent) {
      return "root".equals(parent) ? this.children.size() : 0;
    }

    @Override
    public boolean isLeaf(Object node) {
      return !"root".equals(node);
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
      return this.children.indexOf(child);
    }

    @Override
    public void addTreeModelListener(javax.swing.event.TreeModelListener l) {
    }

    @Override
    public void removeTreeModelListener(javax.swing.event.TreeModelListener l) {
    }

    @Override
    public TreePath getTreePath(String e) {
      return e == null ? null : ("root".equals(e) ? new TreePath(new Object[]{"root"}) : new TreePath(new Object[]{"root", e}));
    }
  }
}
