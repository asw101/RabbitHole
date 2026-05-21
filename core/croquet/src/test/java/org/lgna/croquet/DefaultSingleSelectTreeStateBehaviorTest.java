package org.lgna.croquet;

import edu.cmu.cs.dennisc.javax.swing.models.TreeModel;
import org.junit.Test;

import javax.swing.tree.TreePath;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class DefaultSingleSelectTreeStateBehaviorTest {
  @Test
  public void getTreeModel_textAndIconBehaveAsDefaultImplementation() {
    SimpleStringTreeModel treeModel = new SimpleStringTreeModel();
    ExposedDefaultState state = new ExposedDefaultState(treeModel);

    assertSame(treeModel, state.getTreeModel());
    assertEquals("leaf", state.textFor("leaf"));
    assertNull(state.iconFor("leaf"));
  }

  @Test
  public void createTree_usesStateSelectionModel() {
    CroquetTestUtils.ensureTestApplication();
    SimpleStringTreeModel treeModel = new SimpleStringTreeModel();
    DefaultSingleSelectTreeState<String> state = new DefaultSingleSelectTreeState<>(Application.DOCUMENT_UI_GROUP, CroquetTestUtils.nextTestUUID(), CroquetTestUtils.STRING_CODEC, "left", treeModel);

    org.lgna.croquet.views.Tree<String> tree = state.createTree();

    assertSame(treeModel, tree.getAwtComponent().getModel());
    assertSame(state.getSwingModel().getTreeSelectionModel(), tree.getAwtComponent().getSelectionModel());
  }

  private static final class ExposedDefaultState extends DefaultSingleSelectTreeState<String> {
    private ExposedDefaultState(TreeModel<String> treeModel) {
      super(Application.DOCUMENT_UI_GROUP, CroquetTestUtils.nextTestUUID(), CroquetTestUtils.STRING_CODEC, "left", treeModel);
    }

    private String textFor(String node) {
      return this.getTextForNode(node);
    }

    private javax.swing.Icon iconFor(String node) {
      return this.getIconForNode(node);
    }
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
      if (e == null) {
        return null;
      }
      if ("root".equals(e)) {
        return new TreePath(new Object[]{"root"});
      }
      return new TreePath(new Object[]{"root", e});
    }
  }
}
