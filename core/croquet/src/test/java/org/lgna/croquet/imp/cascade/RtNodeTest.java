package org.lgna.croquet.imp.cascade;

import org.junit.Test;
import org.lgna.croquet.*;
import org.lgna.croquet.edits.Edit;
import org.lgna.croquet.history.UserActivity;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class RtNodeTest {

  @Test
  public void constructorStoresElement() {
    TestRtNode node = new TestRtNode("alpha");
    assertEquals("alpha", node.getElement());
  }

  @Test
  public void getNodeReturnsNodeInstance() {
    TestRtNode node = new TestRtNode("alpha");
    assertNotNull(node.getNode());
    assertEquals("alpha", node.getNode().getElement());
  }

  @Test
  public void setParentUpdatesParentReference() {
    TestRtNode child = new TestRtNode("child");
    TestRtNode parent = new TestRtNode("parent");

    child.setParent(parent);

    assertSame(parent, child.getParent());
  }

  @Test
  public void getRtRootDelegatesToParent() {
    CascadeTestSupport.TestState state = new CascadeTestSupport.TestState();
    RtRoot<String, CustomItemState<String>> root = new RtRoot<>(state.getCascadeRoot());
    TestRtNode child = new TestRtNode("child");

    child.setParent(root);

    assertSame(root, child.getRtRoot());
  }

  @Test
  public void updateParentsAndNextSiblingsAssignsParents() {
    TestRtNode parent = new TestRtNode("parent");
    TestRtNode childA = new TestRtNode("a");
    TestRtNode childB = new TestRtNode("b");

    parent.updateParentsAndNextSiblings(new RtNode<?, ?>[]{childA, childB});

    assertSame(parent, childA.getParent());
    assertSame(parent, childB.getParent());
  }

  @Test
  public void updateParentsAndNextSiblingsAssignsNextSiblingChain() {
    TestRtNode parent = new TestRtNode("parent");
    TestRtNode childA = new TestRtNode("a");
    TestRtNode childB = new TestRtNode("b");
    TestRtNode childC = new TestRtNode("c");

    parent.updateParentsAndNextSiblings(new RtNode<?, ?>[]{childA, childB, childC});

    assertSame(childB, childA.getNextSibling());
    assertSame(childC, childB.getNextSibling());
    assertNull(childC.getNextSibling());
  }

  @Test
  public void getNextBlankReturnsNearestBlankSibling() {
    TestRtNode parent = new TestRtNode("parent");
    RtBlank<String> blankA = new RtBlank<>(new CascadeTestSupport.TestBlank());
    RtBlank<String> blankB = new RtBlank<>(new CascadeTestSupport.TestBlank());
    TestRtNode child = new TestRtNode("child");
    child.nearestBlank = blankA;

    parent.updateParentsAndNextSiblings(new RtNode<?, ?>[]{blankA, blankB, child});

    assertSame(blankB, child.getNextBlank());
  }

  @Test
  public void toStringIncludesElementText() {
    TestRtNode node = new TestRtNode("alpha");
    assertTrue(node.toString().contains("alpha"));
  }

  private static final class TestCascadeNode extends CascadeNode<String> {
    private TestCascadeNode(String element) {
      super(element);
    }
  }

  private static final class TestRtNode extends RtNode<String, TestCascadeNode> {
    private RtBlank<?> nearestBlank;

    private TestRtNode(String element) {
      super(element, new TestCascadeNode(element));
    }

    @Override
    public RtBlank<?> getNearestBlank() {
      return this.nearestBlank;
    }
  }
}

final class CascadeTestSupport {
  static final Group GROUP = Group.getInstance(
      UUID.fromString("00000000-0000-0000-7750-000000000001"),
      "cascadeTests");

  private CascadeTestSupport() {
  }

  static class TestBlank extends CascadeBlank<String> {
    private final List<CascadeBlankChild> children = new ArrayList<>();

    TestBlank(CascadeBlankChild... children) {
      this.children.addAll(Arrays.asList(children));
    }

    @Override
    protected void updateChildren(List<CascadeBlankChild> children, BlankNode<String> blankNode) {
      children.addAll(this.children);
    }
  }

  static class TestBlankChild implements CascadeBlankChild<String> {
    private final List<CascadeItem<?, ?>> items;

    TestBlankChild(CascadeItem<?, ?>... items) {
      this.items = Arrays.asList(items);
    }

    @Override
    public int getItemCount() {
      return this.items.size();
    }

    @SuppressWarnings("unchecked")
    @Override
    public CascadeItem<String, ?> getItemAt(int index) {
      return (CascadeItem<String, ?>) this.items.get(index);
    }
  }

  static class TestItem extends CascadeItem<String, String> {
    private final String text;
    private final String value;
    private final List<? extends CascadeBlank<String>> blanks;

    @SafeVarargs
    TestItem(String text, String value, CascadeBlank<String>... blanks) {
      super(CroquetTestUtils.nextTestUUID());
      this.text = text;
      this.value = value;
      this.blanks = Arrays.asList(blanks);
    }

    List<? extends CascadeBlank<String>> getBlanks() {
      return this.blanks;
    }

    @Override
    public String getTransientValue(ItemNode<? super String, String> node) {
      return "transient:" + this.value;
    }

    @Override
    public String createValue(ItemNode<? super String, String> node) {
      return this.value;
    }

    @Override
    protected JComponent createMenuItemIconProxy(ItemNode<? super String, String> node) {
      return this.text != null ? new JLabel(this.text) : null;
    }

    @Override
    public String getMenuItemText() {
      return this.text;
    }
  }

  static final class TestItemNode extends AbstractItemNode<String, String, TestItem> {
    TestItemNode(TestItem model) {
      super(model);
    }

    @Override
    public BlankNode<String> getBlankStepAt(int index) {
      throw new AssertionError("not used in tests");
    }
  }

  static class TestRtItem extends RtItem<String, String, TestItem, TestItemNode> {
    TestRtItem(TestItem element, CascadeBlankChild<String> owner, int index) {
      super(element, new TestItemNode(element), owner, index);
    }

    @Override
    protected List<? extends CascadeBlank<String>> getModelBlanks() {
      return this.getElement().getBlanks();
    }
  }

  static class TestFillIn extends CascadeFillIn<String, String> {
    private final String text;
    private final String value;
    private final boolean automatic;
    private final List<? extends CascadeBlank<String>> blanks;

    @SafeVarargs
    TestFillIn(String text, String value, boolean automatic, CascadeBlank<String>... blanks) {
      super(CroquetTestUtils.nextTestUUID());
      this.text = text;
      this.value = value;
      this.automatic = automatic;
      this.blanks = Arrays.asList(blanks);
    }

    @Override
    public boolean isAutomaticallySelectedWhenSoleOption() {
      return this.automatic;
    }

    @Override
    public List<? extends CascadeBlank<String>> getBlanks() {
      return this.blanks;
    }

    @Override
    public String getTransientValue(ItemNode<? super String, String> node) {
      return this.createValue(node);
    }

    @Override
    public String createValue(ItemNode<? super String, String> node) {
      if (this.blanks.isEmpty()) {
        return this.value;
      }
      String[] values = this.createFromBlanks(node, String.class);
      return this.value + ":" + String.join(",", values);
    }

    @Override
    protected JComponent createMenuItemIconProxy(ItemNode<? super String, String> node) {
      return this.text != null ? new JLabel(this.text) : null;
    }

    @Override
    public String getMenuItemText() {
      return this.text;
    }
  }

  static class TestSeparator extends CascadeSeparator {
    private final String text;
    private final Icon icon;

    TestSeparator(String text, Icon icon) {
      super(CroquetTestUtils.nextTestUUID());
      this.text = text;
      this.icon = icon;
    }

    @Override
    protected JComponent createMenuItemIconProxy(ItemNode<? super Void, Void> node) {
      if (this.text == null && this.icon == null) {
        return null;
      }
      return new JLabel(this.text, this.icon, SwingConstants.LEADING);
    }

    @Override
    public String getMenuItemText() {
      return this.text;
    }
  }

  static class TestState extends CustomItemState<String> {
    private final List<? extends CascadeBlank<String>> blanks;
    private String swingValue;

    TestState(CascadeBlank<String>... blanks) {
      super(GROUP, CroquetTestUtils.nextTestUUID(), null, CroquetTestUtils.STRING_CODEC);
      this.blanks = Arrays.asList(blanks);
    }

    @Override
    protected List<? extends CascadeBlank<String>> getBlanks() {
      return this.blanks;
    }

    @Override
    protected String getSwingValue() {
      return this.swingValue;
    }

    @Override
    protected void setSwingValue(String nextValue) {
      this.swingValue = nextValue;
    }
  }

  static class TestCascade extends Cascade<String> {
    private final List<? extends CascadeBlank<String>> blanks;
    private final boolean returnNullEdit;

    TestCascade(boolean returnNullEdit, CascadeBlank<String>... blanks) {
      super(GROUP, CroquetTestUtils.nextTestUUID(), String.class);
      this.returnNullEdit = returnNullEdit;
      this.blanks = Arrays.asList(blanks);
    }

    @Override
    protected List<? extends CascadeBlank<String>> getBlanks() {
      return this.blanks;
    }

    @Override
    protected Edit createEdit(UserActivity userActivity, String[] values) {
      if (this.returnNullEdit) {
        return null;
      }
      return new TestEdit(values.length > 0 ? values[0] : null);
    }
  }

  static class TestEdit implements Edit {
    private final String value;

    TestEdit(String value) {
      this.value = value;
    }

    @Override
    public Group getGroup() {
      return GROUP;
    }

    @Override
    public boolean canUndo() {
      return true;
    }

    @Override
    public boolean canRedo() {
      return true;
    }

    @Override
    public void doOrRedo(boolean isDo) {
    }

    @Override
    public void undo() {
    }

    @Override
    public String getRedoPresentation() {
      return this.value;
    }

    @Override
    public String getUndoPresentation() {
      return this.value;
    }

    @Override
    public String getTerseDescription() {
      return this.value;
    }

    @Override
    public String getDetailedDescription() {
      return this.value;
    }

    @Override
    public String getLogDescription() {
      return this.value;
    }
  }
}
