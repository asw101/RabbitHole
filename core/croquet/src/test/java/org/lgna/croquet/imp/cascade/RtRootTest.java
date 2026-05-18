package org.lgna.croquet.imp.cascade;

import org.junit.Test;
import org.lgna.croquet.Cascade;
import org.lgna.croquet.CustomItemState;
import org.lgna.croquet.history.UserActivity;
import org.lgna.croquet.views.*;

import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class RtRootTest {

  @Test
  public void getRtRootReturnsSelf() {
    CascadeTestSupport.TestState state = new CascadeTestSupport.TestState();
    RtRoot<String, CustomItemState<String>> root = new RtRoot<>(state.getCascadeRoot());
    assertSame(root, root.getRtRoot());
  }

  @Test
  public void getNearestBlankReturnsNull() {
    CascadeTestSupport.TestState state = new CascadeTestSupport.TestState();
    RtRoot<String, CustomItemState<String>> root = new RtRoot<>(state.getCascadeRoot());
    assertNull(root.getNearestBlank());
  }

  @Test
  public void createValuesCollectsValuesFromSelectedBlanks() {
    CascadeTestSupport.TestBlank blankA = new CascadeTestSupport.TestBlank();
    CascadeTestSupport.TestBlank blankB = new CascadeTestSupport.TestBlank();
    CascadeTestSupport.TestState state = new CascadeTestSupport.TestState(blankA, blankB);
    RtRoot<String, CustomItemState<String>> root = new RtRoot<>(state.getCascadeRoot());

    root.getBlankChildren()[0].setSelectedFillIn(new RtFillIn<>(
        new CascadeTestSupport.TestFillIn("a", "alpha", false), null, 0));
    root.getBlankChildren()[1].setSelectedFillIn(new RtFillIn<>(
        new CascadeTestSupport.TestFillIn("b", "beta", false), null, 0));

    assertArrayEquals(new String[]{"alpha", "beta"}, root.createValues(String.class));
  }

  @Test
  public void cancelMarksActivityCanceledAndSetsCompletionModel() {
    CascadeTestSupport.TestState state = new CascadeTestSupport.TestState();
    RtRoot<String, CustomItemState<String>> root = new RtRoot<>(state.getCascadeRoot());
    UserActivity activity = new UserActivity();

    root.cancel(activity);

    assertTrue(activity.isCanceled());
    assertSame(state, activity.getCompletionModel());
  }

  @Test
  public void completeUpdatesBackingStateValue() {
    CascadeTestSupport.TestBlank blank = new CascadeTestSupport.TestBlank();
    CascadeTestSupport.TestState state = new CascadeTestSupport.TestState(blank);
    RtRoot<String, CustomItemState<String>> root = new RtRoot<>(state.getCascadeRoot());
    UserActivity activity = new UserActivity();
    root.getBlankChildren()[0].setSelectedFillIn(new RtFillIn<>(
        new CascadeTestSupport.TestFillIn("a", "alpha", false), null, 0));

    root.complete(activity);

    assertEquals("alpha", state.getValue());
    assertSame(state, activity.getCompletionModel());
  }

  @Test
  public void completeCancelsActivityWhenCascadeProducesNoEdit() {
    CascadeTestSupport.TestBlank blank = new CascadeTestSupport.TestBlank();
    CascadeTestSupport.TestCascade cascade = new CascadeTestSupport.TestCascade(true, blank);
    RtRoot<String, Cascade<String>> root = new RtRoot<>(cascade.getRoot());
    UserActivity activity = new UserActivity();
    root.getBlankChildren()[0].setSelectedFillIn(new RtFillIn<>(
        new CascadeTestSupport.TestFillIn("a", "alpha", false), null, 0));

    root.complete(activity);

    assertTrue(activity.isCanceled());
  }

  @Test
  public void popupMenuCanceledCancelsContainerActivity() {
    CascadeTestSupport.TestState state = new CascadeTestSupport.TestState();
    RtRoot<String, CustomItemState<String>> root = new RtRoot<>(state.getCascadeRoot());
    UserActivity activity = new UserActivity();
    TestMenuItemContainer container = new TestMenuItemContainer(activity);
    PopupMenuListener listener = root.createPopupMenuListener(container);

    listener.popupMenuCanceled(new PopupMenuEvent(new javax.swing.JPopupMenu()));

    assertTrue(activity.isCanceled());
  }

  @Test
  public void popupMenuWillBecomeInvisibleRemovesAllMenuItems() {
    CascadeTestSupport.TestState state = new CascadeTestSupport.TestState();
    RtRoot<String, CustomItemState<String>> root = new RtRoot<>(state.getCascadeRoot());
    UserActivity activity = new UserActivity();
    TestMenuItemContainer container = new TestMenuItemContainer(activity);
    PopupMenuListener listener = root.createPopupMenuListener(container);

    listener.popupMenuWillBecomeInvisible(new PopupMenuEvent(new javax.swing.JPopupMenu()));

    assertEquals(1, container.removeAllCount);
  }

  private static final class TestMenuItemContainer implements MenuItemContainer {
    private final UserActivity activity;
    private final List<AwtComponentView<?>> components = new ArrayList<>();
    int removeAllCount;

    private TestMenuItemContainer(UserActivity activity) {
      this.activity = activity;
    }

    @Override public ViewController<?, ?> getViewController() { return null; }
    @Override public void addPopupMenuListener(PopupMenuListener listener) { }
    @Override public void removePopupMenuListener(PopupMenuListener listener) { }
    @Override public UserActivity getActivity() { return this.activity; }
    @Override public AwtContainerView<?> getParent() { return null; }
    @Override public AwtComponentView<?>[] getMenuComponents() { return this.components.toArray(new AwtComponentView<?>[0]); }
    @Override public AwtComponentView<?> getMenuComponent(int i) { return this.components.get(i); }
    @Override public int getMenuComponentCount() { return this.components.size(); }
    @Override public void addMenu(Menu menu) { this.components.add(menu); }
    @Override public void addMenuItem(MenuItem menuItem) { this.components.add(menuItem); }
    @Override public void addCascadeMenu(CascadeMenu cascadeMenu) { this.components.add(cascadeMenu); }
    @Override public void addCascadeMenuItem(CascadeMenuItem cascadeMenuItem) { this.components.add(cascadeMenuItem); }
    @Override public void addCheckBoxMenuItem(CheckBoxMenuItem checkBoxMenuItem) { this.components.add(checkBoxMenuItem); }
    @Override public void addCascadeCombo(CascadeMenuItem cascadeMenuItem, CascadeMenu cascadeMenu) { this.components.add(cascadeMenuItem); this.components.add(cascadeMenu); }
    @Override public void addSeparator() { }
    @Override public void addSeparator(MenuTextSeparator menuTextSeparator) { }
    @Override public void forgetAndRemoveAllMenuItems() { this.components.clear(); }
    @Override public void removeAllMenuItems() { this.removeAllCount++; this.components.clear(); }
  }
}
