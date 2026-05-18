package org.lgna.croquet.imp.cascade;

import org.junit.Test;
import org.lgna.croquet.CascadeBlankChild;
import org.lgna.croquet.CustomItemState;
import org.lgna.croquet.views.ViewController;

import static org.junit.Assert.*;

public class RtSeparatorTest {

  @Test
  public void constructorStoresOwner() {
    CascadeBlankChild<Void> owner = new CascadeBlankChild<Void>() {
      @Override public int getItemCount() { return 0; }
      @Override public org.lgna.croquet.CascadeItem<Void, ?> getItemAt(int index) { return null; }
    };
    RtSeparator separator = new RtSeparator(new CascadeTestSupport.TestSeparator("sep", null), owner, 1);

    assertSame(owner, separator.getOwner());
  }

  @Test
  public void constructorStoresIndex() {
    RtSeparator separator = new RtSeparator(new CascadeTestSupport.TestSeparator("sep", null), null, 4);
    assertEquals(4, separator.getIndex());
  }

  @Test
  public void isLastIsAlwaysTrue() {
    RtSeparator separator = new RtSeparator(new CascadeTestSupport.TestSeparator("sep", null), null, 0);
    assertTrue(separator.isLast());
  }

  @Test
  public void blankChildrenAreEmpty() {
    RtSeparator separator = new RtSeparator(new CascadeTestSupport.TestSeparator("sep", null), null, 0);
    assertEquals(0, separator.getBlankChildren().length);
  }

  @Test
  public void isAutomaticallyDeterminedIsTrue() {
    RtSeparator separator = new RtSeparator(new CascadeTestSupport.TestSeparator("sep", null), null, 0);
    assertTrue(separator.isAutomaticallyDetermined());
  }

  @Test
  public void createMenuItemReturnsNullWhenTextAndIconAreMissing() {
    RtSeparator separator = new RtSeparator(new CascadeTestSupport.TestSeparator(null, null), null, 0);
    assertNull(separator.createMenuItem());
  }

  @Test
  public void createMenuItemReturnsDisabledMenuItemWhenTextExists() {
    CascadeTestSupport.TestState state = new CascadeTestSupport.TestState();
    RtRoot<String, CustomItemState<String>> root = new RtRoot<>(state.getCascadeRoot());
    RtSeparator separator = new RtSeparator(new CascadeTestSupport.TestSeparator("sep", null), null, 0);
    separator.setParent(root);

    ViewController<?, ?> view = separator.createMenuItem();

    assertNotNull(view);
    assertFalse(view.getAwtComponent().isEnabled());
  }

  @Test
  public void getNearestBlankDelegatesToParentBlank() {
    RtBlank<String> blank = new RtBlank<>(new CascadeTestSupport.TestBlank());
    RtSeparator separator = new RtSeparator(new CascadeTestSupport.TestSeparator("sep", null), null, 0);
    separator.setParent(blank);

    assertSame(blank, separator.getNearestBlank());
  }
}
