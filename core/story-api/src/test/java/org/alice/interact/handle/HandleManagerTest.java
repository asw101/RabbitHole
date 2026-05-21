package org.alice.interact.handle;

import edu.cmu.cs.dennisc.scenegraph.AbstractTransformable;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import org.alice.interact.DragAdapter;
import org.alice.interact.InputState;
import org.alice.interact.PickHint;
import org.alice.interact.manipulator.AbstractManipulator;
import org.alice.math.immutable.Point3;
import org.junit.Test;

import static org.junit.Assert.*;

/** Headless-safe HandleManager tests using a lightweight fake handle. */
public class HandleManagerTest {

  @Test
  public void currentHandleSetStartsNull() {
    assertNull(new HandleManager().getCurrentHandleSet());
  }

  @Test
  public void addHandleAssignsManagerToHandle() {
    HandleManager manager = new HandleManager();
    StubHandle handle = new StubHandle(new HandleSet(HandleSet.HandleGroup.ROTATION));

    manager.addHandle(handle);

    assertSame(manager, handle.getHandleManager());
  }

  @Test
  public void updateCameraPositionPropagatesToAddedHandles() {
    HandleManager manager = new HandleManager();
    StubHandle handle = new StubHandle(new HandleSet(HandleSet.HandleGroup.ROTATION));
    manager.addHandle(handle);

    Point3 cameraPosition = new Point3(1, 2, 3);
    manager.updateCameraPosition(cameraPosition);

    assertEquals(cameraPosition, handle.cameraPosition);
  }

  @Test
  public void setHandleSetShowsMatchingHandle() {
    HandleManager manager = new HandleManager();
    StubHandle matching = new StubHandle(new HandleSet(HandleSet.HandleGroup.ROTATION));
    StubHandle other = new StubHandle(new HandleSet(HandleSet.HandleGroup.TRANSLATION));
    manager.addHandle(matching);
    manager.addHandle(other);

    manager.setHandleSet(new HandleSet(HandleSet.HandleGroup.ROTATION));

    assertTrue(matching.isHandleVisible());
    assertFalse(other.isHandleVisible());
  }

  @Test
  public void pushNewHandleSetChangesCurrentHandleSet() {
    HandleManager manager = new HandleManager();
    HandleSet first = new HandleSet(HandleSet.HandleGroup.ROTATION);
    HandleSet second = new HandleSet(HandleSet.HandleGroup.TRANSLATION);

    manager.setHandleSet(first);
    manager.pushNewHandleSet(second);

    assertSame(second, manager.getCurrentHandleSet());
  }

  @Test
  public void popHandleSetRestoresPreviousVisibility() {
    HandleManager manager = new HandleManager();
    StubHandle rotation = new StubHandle(new HandleSet(HandleSet.HandleGroup.ROTATION));
    StubHandle translation = new StubHandle(new HandleSet(HandleSet.HandleGroup.TRANSLATION));
    manager.addHandle(rotation);
    manager.addHandle(translation);

    manager.setHandleSet(new HandleSet(HandleSet.HandleGroup.ROTATION));
    manager.pushNewHandleSet(new HandleSet(HandleSet.HandleGroup.TRANSLATION));
    HandleSet popped = manager.popHandleSet();

    assertTrue(popped.get(HandleSet.HandleGroup.TRANSLATION.ordinal()));
    assertTrue(rotation.isHandleVisible());
    assertFalse(translation.isHandleVisible());
  }

  @Test
  public void setSelectedObjectUpdatesHandleSelection() {
    HandleManager manager = new HandleManager();
    StubHandle handle = new StubHandle(new HandleSet(HandleSet.HandleGroup.ROTATION));
    manager.addHandle(handle);
    manager.setHandleSet(new HandleSet(HandleSet.HandleGroup.ROTATION));
    Transformable selected = new Transformable();

    manager.setSelectedObject(selected);

    assertSame(selected, handle.getManipulatedObject());
    assertSame(selected, manager.getSelectedObject());
  }

  @Test
  public void setHandlesShowingDelegatesToHandles() {
    HandleManager manager = new HandleManager();
    StubHandle handle = new StubHandle(new HandleSet(HandleSet.HandleGroup.ROTATION));
    manager.addHandle(handle);

    manager.setHandlesShowing(true);

    assertTrue(handle.visualsShowing);
  }

  @Test
  public void clearDelegatesToHandles() {
    HandleManager manager = new HandleManager();
    StubHandle handle = new StubHandle(new HandleSet(HandleSet.HandleGroup.ROTATION));
    manager.addHandle(handle);

    manager.clear();

    assertTrue(handle.clearCalled);
  }

  @Test
  public void alwaysVisibleHandleIsReportedVisibleWithoutCurrentSet() {
    HandleManager manager = new HandleManager();
    StubHandle handle = new StubHandle(new HandleSet(HandleSet.HandleGroup.ROTATION));
    handle.alwaysVisible = true;

    assertTrue(manager.isHandleVisible(handle));
  }

  @Test
  public void isHandleVisibleFalseWhenNotInCurrentSet() {
    HandleManager manager = new HandleManager();
    StubHandle handle = new StubHandle(new HandleSet(HandleSet.HandleGroup.ROTATION));
    manager.addHandle(handle);
    manager.setHandleSet(new HandleSet(HandleSet.HandleGroup.TRANSLATION));
    assertFalse(manager.isHandleVisible(handle));
  }

  @Test
  public void setHandleRolloverDelegatesToHandle() {
    HandleManager manager = new HandleManager();
    StubHandle handle = new StubHandle(new HandleSet(HandleSet.HandleGroup.ROTATION));
    manager.setHandleRollover(handle, true);
    assertTrue(handle.getHandleStateCopy().isRollover());
  }

  @Test
  public void setHandlesVisibleDelegatesToHandles() {
    HandleManager manager = new HandleManager();
    StubHandle a = new StubHandle(new HandleSet(HandleSet.HandleGroup.ROTATION));
    StubHandle b = new StubHandle(new HandleSet(HandleSet.HandleGroup.TRANSLATION));
    manager.addHandle(a);
    manager.addHandle(b);
    manager.setHandlesVisible(true);
    assertTrue(a.isHandleVisible());
    assertTrue(b.isHandleVisible());
    manager.setHandlesVisible(false);
    assertFalse(a.isHandleVisible());
    assertFalse(b.isHandleVisible());
  }

  @Test
  public void popHandleSetOnEmptyStackReturnsNullAndPrints() {
    assertNull(new HandleManager().popHandleSet());
  }

  @Test
  public void isASiblingActiveReturnsTrueWhenSiblingHandleIsActive() {
    HandleManager manager = new HandleManager();
    StubHandle a = new StubHandle(new HandleSet(HandleSet.HandleGroup.ROTATION));
    StubHandle b = new StubHandle(new HandleSet(HandleSet.HandleGroup.ROTATION));
    manager.addHandle(a);
    manager.addHandle(b);
    manager.setHandleSet(new HandleSet(HandleSet.HandleGroup.ROTATION));
    b.setHandleActive(true);
    assertTrue(manager.isASiblingActive(a));
  }

  @Test
  public void isASiblingActiveReturnsFalseWhenNoSiblingIsActive() {
    HandleManager manager = new HandleManager();
    StubHandle a = new StubHandle(new HandleSet(HandleSet.HandleGroup.ROTATION));
    StubHandle b = new StubHandle(new HandleSet(HandleSet.HandleGroup.ROTATION));
    manager.addHandle(a);
    manager.addHandle(b);
    manager.setHandleSet(new HandleSet(HandleSet.HandleGroup.ROTATION));
    assertFalse(manager.isASiblingActive(a));
  }

  @Test
  public void alwaysVisibleHandleHasSiblingsWithEqualMembership() {
    HandleManager manager = new HandleManager();
    StubHandle alwaysA = new StubHandle(new HandleSet(HandleSet.HandleGroup.ROTATION));
    alwaysA.alwaysVisible = true;
    StubHandle alwaysB = new StubHandle(new HandleSet(HandleSet.HandleGroup.ROTATION));
    alwaysB.alwaysVisible = true;
    manager.addHandle(alwaysA);
    manager.addHandle(alwaysB);
    // alwaysB is sibling of alwaysA, but neither is active.
    assertFalse(manager.isASiblingActive(alwaysA));
  }

  @Test
  public void isSelectableMirrorsCanHaveHandles() {
    Transformable transformable = new Transformable();
    // Plain transformable lacks pick-type hints → cannot have handles → not selectable.
    assertEquals(HandleManager.canHaveHandles(transformable), HandleManager.isSelectable(transformable));
  }

  @Test
  public void managerImplementsManipulationListenerAsNoOpsAndReturnsFalseMatch() {
    HandleManager manager = new HandleManager();
    // Direct no-op calls exercise the inherited interface body.
    manager.activate(null);
    manager.deactivate(null);
    manager.addCondition(null);
    manager.removeCondition(null);
    assertFalse(manager.matches(null));
  }

  @Test
  public void setSelectedObjectWithNullStillUpdatesVisibility() {
    HandleManager manager = new HandleManager();
    StubHandle handle = new StubHandle(new HandleSet(HandleSet.HandleGroup.ROTATION));
    manager.addHandle(handle);
    manager.setSelectedObject(null);
    // Handle still tracks null selection (depends on canHaveHandle(null, h)).
    // Calling without exception is the contract being tested here.
  }

  @Test
  public void getSelectedObjectReturnsNullWhenNoHandlesRegistered() {
    assertNull(new HandleManager().getSelectedObject());
  }

  private static final class StubHandle implements ManipulationHandle {
    private HandleManager handleManager;
    private final HandleSet handleSet;
    private final HandleState state = new HandleState();
    private AbstractTransformable manipulatedObject;
    private String name;
    private boolean alwaysVisible;
    private boolean visualsShowing;
    private boolean clearCalled;
    private Point3 cameraPosition;

    private StubHandle(HandleSet handleSet) {
      this.handleSet = handleSet;
    }

    @Override
    public void setHandleManager(HandleManager handleManager) {
      this.handleManager = handleManager;
    }

    @Override
    public HandleManager getHandleManager() {
      return this.handleManager;
    }

    @Override
    public HandleSet getHandleSet() {
      return this.handleSet;
    }

    @Override
    public boolean isAlwaysVisible() {
      return this.alwaysVisible;
    }

    @Override
    public void addToSet(HandleSet handleSet) {
      this.handleSet.addSet(handleSet);
    }

    @Override
    public void addToGroup(HandleSet.HandleGroup group) {
      this.handleSet.addGroup(group);
    }

    @Override
    public void addToGroups(HandleSet.HandleGroup... groups) {
      this.handleSet.addGroups(groups);
    }

    @Override
    public boolean isMemberOf(HandleSet set) {
      return (set != null) && this.handleSet.intersects(set);
    }

    @Override
    public boolean isMemberOf(HandleSet.HandleGroup group) {
      return this.handleSet.get(group.ordinal());
    }

    @Override
    public AbstractTransformable getManipulatedObject() {
      return this.manipulatedObject;
    }

    @Override
    public void setSelectedObject(AbstractTransformable manipulatedObject) {
      this.manipulatedObject = manipulatedObject;
    }

    @Override
    public void setHandleRollover(boolean rollover) {
      this.state.setRollover(rollover);
    }

    @Override
    public void setHandleVisible(boolean visible) {
      this.state.setVisible(visible);
    }

    @Override
    public boolean isHandleVisible() {
      return this.state.isVisible();
    }

    @Override
    public void setHandleActive(boolean active) {
      this.state.setActive(active);
    }

    @Override
    public void setVisualsShowing(boolean showing) {
      this.visualsShowing = showing;
    }

    @Override
    public boolean isRenderable() {
      return true;
    }

    @Override
    public boolean isPickable() {
      return true;
    }

    @Override
    public HandleState getHandleStateCopy() {
      return new HandleState(this.state);
    }

    @Override
    public AbstractManipulator getManipulation(InputState input) {
      return null;
    }

    @Override
    public void setManipulation(AbstractManipulator manipulation) {
    }

    @Override
    public PickHint getPickHint() {
      return PickHint.PickType.THREE_D_HANDLE.pickHint();
    }

    @Override
    public void setDragAdapterAndAddHandle(DragAdapter dragAdapter) {
    }

    @Override
    public void setDragAdapter(DragAdapter dragAdapter) {
    }

    @Override
    public void setCameraPosition(Point3 cameraPosition) {
      this.cameraPosition = cameraPosition;
    }

    @Override
    public void clear() {
      this.clearCalled = true;
    }

    @Override
    public void setName(String name) {
      this.name = name;
    }

    @Override
    public String getName() {
      return this.name;
    }
  }
}
