package org.lgna.croquet;

import org.junit.Test;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class TreeNodeFillInBehaviorTest {
  @Test
  public void getInstance_cachesByStateAndNode() {
    ExposedTreeState state = new ExposedTreeState();

    assertSame(TreeNodeFillIn.getInstance(state, "left-leaf"), TreeNodeFillIn.getInstance(state, "left-leaf"));
  }

  @Test
  public void fillIn_usesStateTextIconAndNodeValue() {
    ExposedTreeState state = new ExposedTreeState();
    TreeNodeFillIn<String> fillIn = TreeNodeFillIn.getInstance(state, "left-leaf");

    assertEquals("left-leaf", fillIn.getMenuItemText());
    assertSame(state.icon, fillIn.getMenuItemIcon(null));
    assertEquals("left-leaf", fillIn.getTransientValue(null));
    assertEquals("left-leaf", fillIn.createValue(null));
  }

  private static final class ExposedTreeState extends CustomSingleSelectTreeState<String> {
    private final Map<String, List<String>> children = new LinkedHashMap<>();
    private final Map<String, String> parents = new LinkedHashMap<>();
    private final Icon icon = new ImageIcon(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));

    private ExposedTreeState() {
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
    protected Icon getIconForNode(String node) {
      return this.icon;
    }
  }
}
