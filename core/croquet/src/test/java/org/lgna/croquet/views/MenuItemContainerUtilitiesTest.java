package org.lgna.croquet.views;

import org.junit.Test;
import org.lgna.croquet.StandardMenuItemPrepModel;
import org.lgna.croquet.history.UserActivity;

import javax.swing.event.PopupMenuListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MenuItemContainerUtilitiesTest {
  @Test
  public void constructor_isUtilityStyleAndThrowsAssertionError() throws Exception {
    Constructor<MenuItemContainerUtilities> constructor = MenuItemContainerUtilities.class.getDeclaredConstructor();
    constructor.setAccessible(true);

    try {
      constructor.newInstance();
      fail("Expected AssertionError");
    } catch (InvocationTargetException ite) {
      assertTrue(ite.getCause() instanceof AssertionError);
    }
  }

  @Test
  public void setMenuElements_withEmptyList_clearsExistingItems() {
    RecordingContainer container = new RecordingContainer();

    MenuItemContainerUtilities.setMenuElements(container, Collections.emptyList());

    assertEquals(1, container.forgetAndRemoveAllMenuItemsCount);
    assertEquals(0, container.separatorCount);
  }

  @Test
  public void setMenuElements_withNullEntry_addsSeparator_and_notifiesObserver() {
    RecordingContainer container = new RecordingContainer();
    AtomicInteger observerCount = new AtomicInteger();
    AtomicReference<ViewController<?, ?>> observedMenuElement = new AtomicReference<>();

    MenuItemContainerUtilities.setMenuElements(
        container,
        Collections.singletonList((StandardMenuItemPrepModel) null),
        (menuItemContainer, model, menuElement) -> {
          observerCount.incrementAndGet();
          assertEquals(container, menuItemContainer);
          assertNull(model);
          observedMenuElement.set(menuElement);
        });

    assertEquals(1, container.forgetAndRemoveAllMenuItemsCount);
    assertEquals(1, container.separatorCount);
    assertEquals(1, observerCount.get());
    assertNull(observedMenuElement.get());
  }

  @Test
  public void setMenuElements_arrayOverload_clearsExistingItems() {
    RecordingContainer container = new RecordingContainer();

    MenuItemContainerUtilities.setMenuElements(container, new StandardMenuItemPrepModel[0]);

    assertEquals(1, container.forgetAndRemoveAllMenuItemsCount);
    assertEquals(0, container.separatorCount);
  }

  private static final class RecordingContainer implements MenuItemContainer {
    private int forgetAndRemoveAllMenuItemsCount;
    private int separatorCount;

    @Override
    public ViewController<?, ?> getViewController() {
      return null;
    }

    @Override
    public void addPopupMenuListener(PopupMenuListener listener) {
    }

    @Override
    public void removePopupMenuListener(PopupMenuListener listener) {
    }

    @Override
    public UserActivity getActivity() {
      return null;
    }

    @Override
    public AwtContainerView<?> getParent() {
      return null;
    }

    @Override
    public AwtComponentView<?>[] getMenuComponents() {
      return new AwtComponentView<?>[0];
    }

    @Override
    public AwtComponentView<?> getMenuComponent(int i) {
      return null;
    }

    @Override
    public int getMenuComponentCount() {
      return 0;
    }

    @Override
    public void addMenu(Menu menu) {
    }

    @Override
    public void addMenuItem(MenuItem menuItem) {
    }

    @Override
    public void addCascadeMenu(CascadeMenu cascadeMenu) {
    }

    @Override
    public void addCascadeMenuItem(CascadeMenuItem cascadeMenuItem) {
    }

    @Override
    public void addCheckBoxMenuItem(CheckBoxMenuItem checkBoxMenuItem) {
    }

    @Override
    public void addCascadeCombo(CascadeMenuItem cascadeMenuItem, CascadeMenu cascadeMenu) {
    }

    @Override
    public void addSeparator() {
      this.separatorCount += 1;
    }

    @Override
    public void addSeparator(MenuTextSeparator menuTextSeparator) {
      this.separatorCount += 1;
    }

    @Override
    public void forgetAndRemoveAllMenuItems() {
      this.forgetAndRemoveAllMenuItemsCount += 1;
    }

    @Override
    public void removeAllMenuItems() {
    }
  }
}
