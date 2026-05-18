package org.lgna.croquet.imp.cascade;

import org.junit.Test;
import org.lgna.croquet.CroquetTestUtils;

import java.util.Collections;

import static org.junit.Assert.*;

public class RtItemTest {

  @Test
  public void constructorStoresOwner() {
    CascadeTestSupport.TestBlankChild owner = new CascadeTestSupport.TestBlankChild();
    CascadeTestSupport.TestRtItem item = new CascadeTestSupport.TestRtItem(
        new CascadeTestSupport.TestItem("item", "value"), owner, 2);

    assertSame(owner, item.getOwner());
  }

  @Test
  public void constructorStoresIndex() {
    CascadeTestSupport.TestRtItem item = new CascadeTestSupport.TestRtItem(
        new CascadeTestSupport.TestItem("item", "value"), null, 7);

    assertEquals(7, item.getIndex());
  }

  @Test
  public void constructorCreatesBlankChildrenFromModel() {
    CascadeTestSupport.TestBlank blank = new CascadeTestSupport.TestBlank();
    CascadeTestSupport.TestRtItem item = new CascadeTestSupport.TestRtItem(
        new CascadeTestSupport.TestItem("item", "value", blank), null, 0);

    assertEquals(1, item.getBlankChildren().length);
    assertSame(blank, item.getBlankChildren()[0].getElement());
  }

  @Test
  public void getBlankStepAtReturnsNestedBlankNode() {
    CascadeTestSupport.TestBlank blank = new CascadeTestSupport.TestBlank();
    CascadeTestSupport.TestRtItem item = new CascadeTestSupport.TestRtItem(
        new CascadeTestSupport.TestItem("item", "value", blank), null, 0);

    assertSame(item.getBlankChildren()[0].getNode(), item.getBlankStepAt(0));
  }

  @Test
  public void getNearestBlankDelegatesToParentBlank() {
    CascadeTestSupport.TestBlank parentBlankModel = new CascadeTestSupport.TestBlank();
    RtBlank<String> parentBlank = new RtBlank<>(parentBlankModel);
    CascadeTestSupport.TestRtItem item = new CascadeTestSupport.TestRtItem(
        new CascadeTestSupport.TestItem("item", "value"), null, 0);

    item.setParent(parentBlank);

    assertSame(parentBlank, item.getNearestBlank());
  }

  @Test
  public void isAutomaticallyDeterminedIsTrueWhenThereAreNoBlanks() {
    CascadeTestSupport.TestRtItem item = new CascadeTestSupport.TestRtItem(
        new CascadeTestSupport.TestItem("item", "value"), null, 0);

    assertTrue(item.isAutomaticallyDetermined());
  }

  @Test
  public void createValueDelegatesToUnderlyingElement() {
    CascadeTestSupport.TestRtItem item = new CascadeTestSupport.TestRtItem(
        new CascadeTestSupport.TestItem("item", "created"), null, 0);

    assertEquals("created", item.createValue());
  }

  @Test
  public void selectAssignsSelectedFillInOnNearestBlank() {
    CascadeTestSupport.TestBlank hostBlankModel = new CascadeTestSupport.TestBlank();
    RtBlank<String> hostBlank = new RtBlank<>(hostBlankModel);
    CascadeTestSupport.TestRtItem item = new CascadeTestSupport.TestRtItem(
        new CascadeTestSupport.TestItem("item", "value"), null, 0);

    item.setParent(hostBlank);
    item.select();

    assertSame(item.getNode(), hostBlank.getSelectedFillInNode());
  }
}
