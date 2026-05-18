package org.lgna.croquet.imp.cascade;

import org.junit.Test;
import org.lgna.croquet.CascadeUnfilledInCancel;

import static org.junit.Assert.*;

public class RtBlankTest {

  @Test
  public void getNearestBlankReturnsSelf() {
    RtBlank<String> blank = new RtBlank<>(new CascadeTestSupport.TestBlank());
    assertSame(blank, blank.getNearestBlank());
  }

  @Test
  public void selectedFillInNodeIsNullInitially() {
    RtBlank<String> blank = new RtBlank<>(new CascadeTestSupport.TestBlank());
    assertNull(blank.getSelectedFillInNode());
  }

  @Test
  public void setSelectedFillInUpdatesSelectedFillInNode() {
    RtBlank<String> blank = new RtBlank<>(new CascadeTestSupport.TestBlank());
    CascadeTestSupport.TestRtItem item = new CascadeTestSupport.TestRtItem(
        new CascadeTestSupport.TestItem("item", "value"), null, 0);

    blank.setSelectedFillIn(item);

    assertSame(item.getNode(), blank.getSelectedFillInNode());
  }

  @Test
  public void createValueDelegatesToSelectedFillIn() {
    RtBlank<String> blank = new RtBlank<>(new CascadeTestSupport.TestBlank());
    CascadeTestSupport.TestRtItem item = new CascadeTestSupport.TestRtItem(
        new CascadeTestSupport.TestItem("item", "created"), null, 0);
    blank.setSelectedFillIn(item);

    assertEquals("created", blank.createValue());
  }

  @Test(expected = RuntimeException.class)
  public void createValueWithoutSelectionThrowsRuntimeException() {
    RtBlank<String> blank = new RtBlank<>(new CascadeTestSupport.TestBlank());
    blank.createValue();
  }

  @Test
  public void isAutomaticallyDeterminedIsTrueForSingleAutomaticFillIn() {
    CascadeTestSupport.TestFillIn fillIn = new CascadeTestSupport.TestFillIn("auto", "value", true);
    RtBlank<String> blank = new RtBlank<>(new CascadeTestSupport.TestBlank(
        new CascadeTestSupport.TestBlankChild(fillIn)));

    assertTrue(blank.isAutomaticallyDetermined());
    assertNotNull(blank.getSelectedFillInNode());
  }

  @Test
  public void isAutomaticallyDeterminedIsFalseForMultipleFillIns() {
    CascadeTestSupport.TestFillIn fillInA = new CascadeTestSupport.TestFillIn("a", "a", true);
    CascadeTestSupport.TestFillIn fillInB = new CascadeTestSupport.TestFillIn("b", "b", true);
    RtBlank<String> blank = new RtBlank<>(new CascadeTestSupport.TestBlank(
        new CascadeTestSupport.TestBlankChild(fillInA, fillInB)));

    assertFalse(blank.isAutomaticallyDetermined());
  }

  @Test
  public void comboOffsetsAreRecordedForTwoItemChild() {
    CascadeTestSupport.TestFillIn fillInA = new CascadeTestSupport.TestFillIn("a", "a", false);
    CascadeTestSupport.TestFillIn fillInB = new CascadeTestSupport.TestFillIn("b", "b", false);
    RtBlank<String> blank = new RtBlank<>(new CascadeTestSupport.TestBlank(
        new CascadeTestSupport.TestBlankChild(fillInA, fillInB)));

    RtBlank.ItemChildrenAndComboOffsetsPair pair = blank.getItemChildrenAndComboOffsets();

    assertEquals(2, pair.getItemChildren().length);
    assertTrue(pair.isComboOffset(0));
    assertFalse(pair.isComboOffset(1));
  }

  @Test
  public void separatorOnlyChildrenCauseCancelToBeAdded() {
    CascadeTestSupport.TestSeparator separator = new CascadeTestSupport.TestSeparator(null, null);
    RtBlank<String> blank = new RtBlank<>(new CascadeTestSupport.TestBlank(
        new CascadeTestSupport.TestBlankChild(separator)));

    RtItem[] children = blank.getItemChildrenAndComboOffsets().getItemChildren();

    assertEquals(2, children.length);
    assertTrue(children[0] instanceof RtSeparator);
    assertTrue(children[1] instanceof RtCancel);
    assertSame(CascadeUnfilledInCancel.getInstance(), children[1].getElement());
  }
}
