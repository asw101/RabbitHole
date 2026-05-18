package org.lgna.croquet.history;

import edu.cmu.cs.dennisc.codec.BinaryEncoder;
import org.junit.After;
import org.junit.Test;
import org.lgna.croquet.*;
import org.lgna.croquet.triggers.DropTrigger;
import org.lgna.croquet.views.DragComponent;
import org.lgna.croquet.DropRejector;
import org.lgna.croquet.views.MenuBar;
import org.lgna.croquet.views.MenuItemContainer;
import org.lgna.croquet.views.SwingComponentView;
import org.lgna.croquet.views.TrackableShape;
import org.lgna.croquet.views.ViewController;
import org.lgna.croquet.views.Menu;
import org.lgna.croquet.views.MenuItem;
import org.lgna.croquet.views.imp.JDragView;

import javax.swing.MenuElement;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class UserActivityDeepTest {

  @After
  public void clearMenuSelection() {
    javax.swing.MenuSelectionManager.defaultManager().clearSelectedPath();
  }

  @Test
  public void addMenuSelectionAddsPrepStep() {
    UserActivity activity = new UserActivity();
    HistoryTestSupport.SelectionFixture fixture = HistoryTestSupport.createSelectionFixture("file", "open");

    activity.addMenuSelection(fixture.selection);

    assertEquals(1, activity.getChildStepCount());
    assertTrue(activity.getChildAt(0) instanceof MenuItemSelectStep);
  }

  @Test
  public void addMenuSelectionFindsFirstMenuSelectStep() {
    UserActivity activity = new UserActivity();
    HistoryTestSupport.SelectionFixture fixture = HistoryTestSupport.createSelectionFixture("file", "open");

    activity.addMenuSelection(fixture.selection);

    assertNotNull(activity.findFirstMenuSelectStep());
    assertSame(fixture.selection, activity.findFirstMenuSelectStep().getMenuSelection());
  }

  @Test
  public void addMenuSelectionReplacesNonPreviousSelection() {
    UserActivity activity = new UserActivity();
    HistoryTestSupport.SelectionFixture first = HistoryTestSupport.createSelectionFixture("file", "open");
    HistoryTestSupport.SelectionFixture second = HistoryTestSupport.createSelectionFixture("edit", "copy");

    activity.addMenuSelection(first.selection);
    activity.addMenuSelection(second.selection);

    assertEquals(1, activity.getChildStepCount());
    assertSame(second.selection.getLastMenuItemPrepModel(), activity.findFirstMenuSelectStep().getModel());
  }

  @Test
  public void getIndexOfPrepStepReturnsPrepStepIndex() {
    UserActivity activity = new UserActivity();
    activity.addMenuSelection(HistoryTestSupport.createSelectionFixture("file", "open").selection);
    MenuItemSelectStep step = activity.findFirstMenuSelectStep();

    assertEquals(0, activity.getIndexOfPrepStep(step));
  }

  @Test
  public void addDragStepAddsPrepStep() {
    UserActivity activity = new UserActivity();
    HistoryTestSupport.TestDragModel model = new HistoryTestSupport.TestDragModel();
    HistoryTestSupport.TestDragComponent component = new HistoryTestSupport.TestDragComponent(model);
    MouseEvent event = new MouseEvent(component.getAwtComponent(), MouseEvent.MOUSE_DRAGGED,
        System.currentTimeMillis(), 0, 4, 5, 1, false);

    DragStep step = activity.addDragStep(model, org.lgna.croquet.triggers.DragTrigger.createUserInstance(component, event));

    assertSame(step, activity.getChildAt(0));
    assertEquals(1, activity.getChildStepCount());
  }

  @Test
  public void findDropSiteUsesDropTriggerOnActivity() {
    UserActivity activity = new UserActivity();
    HistoryTestSupport.TestDropSite site = new HistoryTestSupport.TestDropSite(null);
    MouseEvent event = new MouseEvent(new JButton(), MouseEvent.MOUSE_RELEASED,
        System.currentTimeMillis(), 0, 1, 1, 1, false);

    DropTrigger.setOnUserActivity(activity, null, event, site);

    assertSame(site, activity.findDropSite());
  }

  @Test
  public void getActivityWithoutTriggerCreatesChildWhenTriggerExists() {
    UserActivity activity = new UserActivity();
    activity.setTrigger(new HistoryTestSupport.TestTrigger(activity, null));

    UserActivity withoutTrigger = activity.getActivityWithoutTrigger();

    assertNotSame(activity, withoutTrigger);
    assertSame(activity, withoutTrigger.getOwner());
  }

  @Test
  public void getChildAtReturnsChildActivityAfterPrepSteps() {
    UserActivity activity = new UserActivity();
    activity.addMenuSelection(HistoryTestSupport.createSelectionFixture("file", "open").selection);
    UserActivity child = activity.newChildActivity();

    assertSame(child, activity.getChildAt(1));
  }
}

final class HistoryTestSupport {
  static final Group GROUP = Group.getInstance(
      UUID.fromString("00000000-0000-0000-7750-000000000100"),
      "historyTests");

  private HistoryTestSupport() {
  }

  static MenuSelection createSelection(String menuText, String itemText) {
    return createSelectionFixture(menuText, itemText).selection;
  }

  static SelectionFixture createSelectionFixture(String menuText, String itemText) {
    MenuBarComposite composite = new MenuBarComposite(CroquetTestUtils.nextTestUUID());
    TestMenuModel menuModel = new TestMenuModel(menuText);
    composite.addItem(menuModel);
    MenuBar menuBar = composite.getView();
    Menu menu = (Menu) menuBar.getMenuComponent(0);
    TestOperation operation = itemText != null ? new TestOperation(itemText) : null;
    MenuItem itemView = operation != null
        ? (MenuItem) operation.getMenuItemPrepModel().createMenuItemAndAddTo(menu)
        : null;

    MenuElement[] path = itemView != null
        ? new MenuElement[]{menuBar.getAwtComponent(), menu.getAwtComponent(), itemView.getAwtComponent()}
        : new MenuElement[]{menuBar.getAwtComponent(), menu.getAwtComponent()};
    javax.swing.MenuSelectionManager.defaultManager().setSelectedPath(path);
    return new SelectionFixture(new MenuSelection(), menuModel, operation, menuBar, menu, itemView);
  }

  static final class SelectionFixture {
    final MenuSelection selection;
    final TestMenuModel menuModel;
    final TestOperation operation;
    final MenuBar menuBar;
    final Menu menu;
    final MenuItem itemView;

    SelectionFixture(MenuSelection selection, TestMenuModel menuModel, TestOperation operation,
        MenuBar menuBar, Menu menu, MenuItem itemView) {
      this.selection = selection;
      this.menuModel = menuModel;
      this.operation = operation;
      this.menuBar = menuBar;
      this.menu = menu;
      this.itemView = itemView;
    }
  }

  static final class TestOperation extends Operation {
    private final String text;

    TestOperation(String text) {
      super(GROUP, CroquetTestUtils.nextTestUUID());
      this.text = text;
    }

    @Override
    protected void localize() {
      this.setName(this.text);
    }

    @Override
    protected void performInActivity(UserActivity userActivity) {
    }
  }

  static final class TestMenuModel extends AbstractMenuModel {
    private final String text;

    TestMenuModel(String text) {
      super(CroquetTestUtils.nextTestUUID(), null);
      this.text = text;
    }

    @Override
    protected void localize() {
      super.localize();
      this.setName(this.text);
    }
  }

  static final class TestPrepModel extends StandardMenuItemPrepModel {
    TestPrepModel() {
      super(CroquetTestUtils.nextTestUUID());
    }

    TestPrepModel(String ignored) {
      this();
    }

    @Override
    protected void localize() {
    }

    @Override
    public ViewController<?, ?> createMenuItemAndAddTo(MenuItemContainer menuItemContainer) {
      return null;
    }
  }

  static class TestView extends ViewController<JPanel, Model> {
    TestView() {
      super(null);
    }

    @Override
    protected JPanel createAwtComponent() {
      return new JPanel();
    }
  }

  static class TestTrigger extends org.lgna.croquet.triggers.Trigger {
    private final ViewController<?, ?> viewController;

    TestTrigger(UserActivity activity, ViewController<?, ?> viewController) {
      super(activity);
      this.viewController = viewController;
    }

    @Override
    public ViewController<?, ?> getViewController() {
      return this.viewController;
    }

    @Override
    public void showPopupMenu(org.lgna.croquet.views.PopupMenu popupMenu) {
    }
  }

  static final class TestDragModel extends AbstractModel implements DragModel {
    final List<DropReceptor> receptors = new ArrayList<>();
    int dragStartedCount;
    int enteredCount;
    int exitedCount;
    int stoppedCount;

    TestDragModel() {
      super(CroquetTestUtils.nextTestUUID());
    }

    @Override protected void localize() { }
    @Override public List<? extends DropReceptor> createListOfPotentialDropReceptors() { return this.receptors; }
    @Override public void handleDragStarted(DragStep step) { this.dragStartedCount++; }
    @Override public void handleDragEnteredDropReceptor(DragStep step) { this.enteredCount++; }
    @Override public void handleDragExitedDropReceptor(DragStep step) { this.exitedCount++; }
    @Override public void handleDragStopped(DragStep step) { this.stoppedCount++; }
    @Override public Triggerable getDropOperation(DragStep step, DropSite dropSite) { return null; }
  }

  static final class TestDragComponent extends DragComponent<TestDragModel> {
    TestDragComponent(TestDragModel model) {
      super(model, false);
    }

    @Override
    protected JDragView createAwtComponent() {
      return new JDragView();
    }

    @Override
    protected void fillBounds(Graphics2D g2, int x, int y, int width, int height) {
      g2.fillRect(x, y, width, height);
    }

    @Override
    protected void paintPrologue(Graphics2D g2, int x, int y, int width, int height) {
    }

    @Override
    protected void paintEpilogue(Graphics2D g2, int x, int y, int width, int height) {
    }
  }

  static final class TestSwingView extends SwingComponentView<JPanel> {
    @Override
    protected JPanel createAwtComponent() {
      return new JPanel();
    }
  }

  static final class TestDropSite implements DropSite {
    private final DropReceptor owner;

    TestDropSite(DropReceptor owner) {
      this.owner = owner;
    }

    @Override
    public DropReceptor getOwningDropReceptor() {
      return this.owner;
    }

    @Override
    public void encode(BinaryEncoder binaryEncoder) {
    }
  }

  static final class TestDropReceptor implements DropReceptor {
    final TestSwingView view = new TestSwingView();
    final List<DropRejector> rejectors = new ArrayList<>();
    int dragStartedCount;
    int dragEnteredCount;
    int dragExitedCount;
    int dragStoppedCount;
    Triggerable dropOperation;
    DropSite dropSite = new TestDropSite(this);

    @Override public TrackableShape getTrackableShape(DropSite potentialDropSite) { return null; }
    @Override public boolean isPotentiallyAcceptingOf(DragModel dragModel) { return true; }
    @Override public SwingComponentView<?> getViewController() { return this.view; }
    @Override public void dragStarted(DragStep step) { this.dragStartedCount++; }
    @Override public void dragEntered(DragStep step) { this.dragEnteredCount++; }
    @Override public DropSite dragUpdated(DragStep step) { return this.dropSite; }
    @Override public Triggerable dragDropped(DragStep step) { return this.dropOperation; }
    @Override public void dragExited(DragStep step, boolean isDropRecipient) { this.dragExitedCount++; }
    @Override public void dragStopped(DragStep step) { this.dragStoppedCount++; }
    @Override public void addDropRejector(DropRejector dropRejector) { this.rejectors.add(dropRejector); }
    @Override public void removeDropRejector(DropRejector dropRejector) { this.rejectors.remove(dropRejector); }
    @Override public void clearDropRejectors() { this.rejectors.clear(); }
    @Override public List<DropRejector> getDropRejectors() { return this.rejectors; }
  }
}
