package org.lgna.croquet;

import edu.cmu.cs.dennisc.javax.swing.models.TreeModel;
import org.junit.Test;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class CustomSingleSelectTreeStateBehaviorTest {
  @Test
  public void treeModel_buildsPathsUsingParentRelationships() {
    CroquetTestUtils.ensureTestApplication();
    TestTreeState state = new TestTreeState();
    TreeModel<String> treeModel = state.getTreeModel();

    TreePath path = treeModel.getTreePath("left-leaf");

    assertArrayEquals(new Object[]{"root", "left", "left-leaf"}, path.getPath());
    assertEquals(2, treeModel.getChildCount("root"));
    assertEquals("left", treeModel.getChild("root", 0));
  }

  @Test
  public void refresh_reloadsNodeOnUnderlyingMutableTreeModel() {
    CroquetTestUtils.ensureTestApplication();
    TestTreeState state = new TestTreeState();
    List<TreeModelEvent> events = new ArrayList<>();
    state.getTreeModel().addTreeModelListener(new TreeModelListener() {
      @Override
      public void treeNodesChanged(TreeModelEvent e) {
      }

      @Override
      public void treeNodesInserted(TreeModelEvent e) {
      }

      @Override
      public void treeNodesRemoved(TreeModelEvent e) {
      }

      @Override
      public void treeStructureChanged(TreeModelEvent e) {
        events.add(e);
      }
    });

    state.refresh("left");

    assertFalse(events.isEmpty());
  }

  private static final class TestTreeState extends CustomSingleSelectTreeState<String> {
    private final Map<String, List<String>> children = new LinkedHashMap<>();
    private final Map<String, String> parents = new LinkedHashMap<>();

    private TestTreeState() {
      super(Application.DOCUMENT_UI_GROUP, CroquetTestUtils.nextTestUUID(), "left", CroquetTestUtils.STRING_CODEC);
      this.children.put("root", Arrays.asList("left", "right"));
      this.children.put("left", Arrays.asList("left-leaf"));
      this.children.put("right", new ArrayList<>());
      this.children.put("left-leaf", new ArrayList<>());
      this.parents.put("left", "root");
      this.parents.put("right", "root");
      this.parents.put("left-leaf", "left");
    }

    @Override
    protected int getChildCount(String parent) {
      return this.children.get(parent).size();
    }

    @Override
    protected String getChild(String parent, int index) {
      return this.children.get(parent).get(index);
    }

    @Override
    protected int getIndexOfChild(String parent, String child) {
      return this.children.get(parent).indexOf(child);
    }

    @Override
    protected String getRoot() {
      return "root";
    }

    @Override
    public String getParent(String node) {
      return this.parents.get(node);
    }

    @Override
    public boolean isLeaf(String node) {
      return this.children.get(node).isEmpty();
    }

    @Override
    protected String getTextForNode(String node) {
      return node;
    }

    @Override
    protected javax.swing.Icon getIconForNode(String node) {
      return null;
    }
  }
}
