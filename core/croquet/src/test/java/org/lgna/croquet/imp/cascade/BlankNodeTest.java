package org.lgna.croquet.imp.cascade;

import org.junit.Test;
import org.lgna.croquet.CustomItemState;

import static org.junit.Assert.*;

public class BlankNodeTest {

  @Test
  public void createInstanceRetainsElement() {
    CascadeTestSupport.TestBlank model = new CascadeTestSupport.TestBlank();
    BlankNode<String> node = BlankNode.createInstance(model);

    assertSame(model, node.getElement());
  }

  @Test
  public void isTopIsFalseWithoutParent() {
    RtBlank<String> blank = new RtBlank<>(new CascadeTestSupport.TestBlank());
    assertFalse(blank.getNode().isTop());
  }

  @Test
  public void isTopIsTrueWhenParentIsRoot() {
    RtBlank<String> blank = new RtBlank<>(new CascadeTestSupport.TestBlank());
    CascadeTestSupport.TestState state = new CascadeTestSupport.TestState();
    RtRoot<String, CustomItemState<String>> root = new RtRoot<>(state.getCascadeRoot());

    blank.setParent(root);

    assertTrue(blank.getNode().isTop());
  }

  @Test
  public void isTopIsFalseWhenParentIsFillIn() {
    RtBlank<String> hostBlank = new RtBlank<>(new CascadeTestSupport.TestBlank());
    RtFillIn<String, String> fillIn = new RtFillIn<>(
        new CascadeTestSupport.TestFillIn("fill", "value", false), null, 0);
    fillIn.setParent(hostBlank);
    RtBlank<String> childBlank = new RtBlank<>(new CascadeTestSupport.TestBlank());
    childBlank.setParent(fillIn);

    assertFalse(childBlank.getNode().isTop());
  }

  @Test
  public void getSelectedFillInContextIsNullInitially() {
    RtBlank<String> blank = new RtBlank<>(new CascadeTestSupport.TestBlank());
    assertNull(blank.getNode().getSelectedFillInContext());
  }

  @Test
  public void getSelectedFillInContextReflectsSelectedItem() {
    RtBlank<String> blank = new RtBlank<>(new CascadeTestSupport.TestBlank());
    CascadeTestSupport.TestRtItem item = new CascadeTestSupport.TestRtItem(
        new CascadeTestSupport.TestItem("item", "value"), null, 0);

    blank.setSelectedFillIn(item);

    assertSame(item.getNode(), blank.getNode().getSelectedFillInContext());
  }

  @Test
  public void rtBlankConstructorCreatesNode() {
    RtBlank<String> blank = new RtBlank<>(new CascadeTestSupport.TestBlank());
    assertNotNull(blank.getNode());
  }

  @Test
  public void getElementReturnsOriginalBlankModel() {
    CascadeTestSupport.TestBlank model = new CascadeTestSupport.TestBlank();
    RtBlank<String> blank = new RtBlank<>(model);

    assertSame(model, blank.getNode().getElement());
  }
}
