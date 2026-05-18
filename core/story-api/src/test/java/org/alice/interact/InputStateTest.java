package org.alice.interact;

import edu.cmu.cs.dennisc.render.PickResult;
import edu.cmu.cs.dennisc.scenegraph.AbstractCamera;
import edu.cmu.cs.dennisc.scenegraph.AbstractTransformable;
import edu.cmu.cs.dennisc.scenegraph.Component;
import edu.cmu.cs.dennisc.scenegraph.Geometry;
import edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.alice.interact.DragAdapter;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Matrix4x4;
import org.alice.interact.handle.HandleManager;
import org.alice.interact.handle.HandleSet;
import org.alice.interact.handle.HandleState;
import org.alice.interact.handle.ManipulationHandle;
import org.alice.interact.manipulator.AbstractManipulator;
import org.alice.math.immutable.Point3;
import org.junit.Before;
import org.junit.Test;

import java.awt.Canvas;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class InputStateTest {
  private static final class FakeHandle implements ManipulationHandle {
    private final PickHint pickHint;
    private final boolean pickable;

    private FakeHandle(PickHint pickHint, boolean pickable) {
      this.pickHint = pickHint;
      this.pickable = pickable;
    }

    @Override public void setHandleManager(HandleManager handleManager) {}
    @Override public HandleManager getHandleManager() { return null; }
    @Override public HandleSet getHandleSet() { return null; }
    @Override public boolean isAlwaysVisible() { return false; }
    @Override public void addToSet(HandleSet handleSet) {}
    @Override public void addToGroup(HandleSet.HandleGroup group) {}
    @Override public void addToGroups(HandleSet.HandleGroup... groups) {}
    @Override public boolean isMemberOf(HandleSet set) { return false; }
    @Override public boolean isMemberOf(HandleSet.HandleGroup group) { return false; }
    @Override public AbstractTransformable getManipulatedObject() { return null; }
    @Override public void setSelectedObject(AbstractTransformable manipulatedObject) {}
    @Override public void setHandleRollover(boolean rollover) {}
    @Override public void setHandleVisible(boolean visible) {}
    @Override public boolean isHandleVisible() { return false; }
    @Override public void setHandleActive(boolean active) {}
    @Override public void setVisualsShowing(boolean showing) {}
    @Override public boolean isRenderable() { return true; }
    @Override public boolean isPickable() { return pickable; }
    @Override public HandleState getHandleStateCopy() { return null; }
    @Override public AbstractManipulator getManipulation(InputState input) { return null; }
    @Override public void setManipulation(AbstractManipulator manipulation) {}
    @Override public PickHint getPickHint() { return pickHint; }
    @Override public void setDragAdapterAndAddHandle(DragAdapter dragAdapter) {}
    @Override public void setDragAdapter(DragAdapter dragAdapter) {}
    @Override public void setCameraPosition(Point3 cameraPosition) {}
    @Override public void clear() {}
    @Override public void setName(String name) {}
    @Override public String getName() { return "fake"; }
  }

  private InputState state;

  @Before
  public void setUp() {
    state = new InputState();
  }

  private static PickResult createPickResult(Component source, AbstractTransformable parent) {
    Visual visual = new Visual();
    visual.setParent(parent);
    Geometry geometry = new Geometry() {
      @Override public AxisAlignedBox updateBoundingBox() { return AxisAlignedBox.NaN; }
      @Override public AffineMatrix4x4 getPlane() { return AffineMatrix4x4.IDENTITY; }
      @Override public void transform(Matrix4x4 trans) {}
    };
    return new PickResult(source, visual, true, geometry, 0, Point3.ORIGIN);
  }

  private static MouseWheelEvent wheelEvent(int rotation) {
    return new MouseWheelEvent(new Canvas(), MouseWheelEvent.MOUSE_WHEEL, System.currentTimeMillis(), 0,
        1, 1, 0, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, 1, rotation);
  }

  @Test
  public void defaultInputEventTypeIsNullEvent() {
    assertEquals(InputState.InputEventType.NULL_EVENT, state.getInputEventType());
  }

  @Test
  public void defaultPickCameraIsNull() {
    assertNull(state.getPickCamera());
  }

  @Test
  public void pickCameraUsesRolloverSourceCameraFirst() {
    AbstractCamera rolloverCamera = new SymmetricPerspectiveCamera();
    state.setRolloverPickResult(new PickResult(rolloverCamera));
    state.setClickPickResult(new PickResult(new SymmetricPerspectiveCamera()));

    assertSame(rolloverCamera, state.getPickCamera());
  }

  @Test
  public void pickCameraFallsBackToClickSourceCamera() {
    AbstractCamera clickCamera = new SymmetricPerspectiveCamera();
    state.setClickPickResult(new PickResult(clickCamera));

    assertSame(clickCamera, state.getPickCamera());
  }

  @Test
  public void setInputEventStoresReference() {
    KeyEvent event = new KeyEvent(new Canvas(), KeyEvent.KEY_PRESSED, 1L, 0, KeyEvent.VK_A, 'a');
    state.setInputEvent(event);
    assertSame(event, state.getInputEvent());
  }

  @Test
  public void keyStateRoundTrips() {
    state.setKeyState(KeyEvent.VK_SHIFT, true);
    assertTrue(state.isKeyDown(KeyEvent.VK_SHIFT));
  }

  @Test
  public void clearKeyStateDoesNotAffectMouseButtons() {
    state.setKeyState(KeyEvent.VK_A, true);
    state.setMouseState(1, true);
    state.clearKeyState();

    assertFalse(state.isKeyDown(KeyEvent.VK_A));
    assertTrue(state.isMouseDown(1));
  }

  @Test
  public void mouseStateRoundTrips() {
    state.setMouseState(1, true);
    assertTrue(state.isMouseDown(1));
  }

  @Test
  public void clearMouseStateDoesNotAffectKeys() {
    state.setKeyState(KeyEvent.VK_B, true);
    state.setMouseState(1, true);
    state.clearMouseState();

    assertTrue(state.isKeyDown(KeyEvent.VK_B));
    assertFalse(state.isMouseDown(1));
  }

  @Test
  public void mouseWheelRoundTrips() {
    state.setMouseWheelState(-2);
    assertEquals(-2, state.getMouseWheelState());
  }

  @Test
  public void clearMouseWheelDoesNotAffectKeyState() {
    state.setKeyState(KeyEvent.VK_C, true);
    state.setMouseWheelState(5);
    state.clearMouseWheelState();

    assertEquals(0, state.getMouseWheelState());
    assertTrue(state.isKeyDown(KeyEvent.VK_C));
  }

  @Test
  public void mouseLocationRoundTrips() {
    state.setMouseLocation(new Point(9, 11));
    assertEquals(new Point(9, 11), state.getMouseLocation());
  }

  @Test
  public void clickHandleRoundTrips() {
    FakeHandle handle = new FakeHandle(new PickHint(PickHint.PickType.THREE_D_HANDLE), true);
    state.setClickHandle(handle);
    assertSame(handle, state.getClickHandle());
  }

  @Test
  public void rolloverHandleRoundTrips() {
    FakeHandle handle = new FakeHandle(new PickHint(PickHint.PickType.TWO_D_HANDLE), true);
    state.setRolloverHandle(handle);
    assertSame(handle, state.getRolloverHandle());
  }

  @Test
  public void dragAndDropContextRoundTrips() {
    Object context = "drag-context";
    state.setDragAndDropContext(context);
    assertSame(context, state.getDragAndDropContext());
  }

  @Test
  public void clickPickHintUsesHandleHintWhenPresent() {
    FakeHandle handle = new FakeHandle(new PickHint(PickHint.PickType.THREE_D_HANDLE), true);
    state.setClickHandle(handle);

    assertTrue(state.getClickPickHint().get(PickHint.PickType.THREE_D_HANDLE));
  }

  @Test
  public void rolloverPickHintUsesHandleHintWhenNoPickResultExists() {
    FakeHandle handle = new FakeHandle(new PickHint(PickHint.PickType.TWO_D_HANDLE), true);
    state.setRolloverHandle(handle);

    assertTrue(state.getRolloverPickHint().get(PickHint.PickType.TWO_D_HANDLE));
  }

  @Test
  public void getClickPickedTransformableReturnsRawParentWhenNotFirstClass() {
    Transformable parent = new Transformable();
    state.setClickPickResult(createPickResult(parent, parent));

    assertSame(parent, state.getClickPickedTransformable(false));
  }

  @Test
  public void getClickPickedTransformableReturnsNullForFirstClassWithoutEntity() {
    Transformable parent = new Transformable();
    state.setClickPickResult(createPickResult(parent, parent));

    assertNull(state.getClickPickedTransformable(true));
  }

  @Test
  public void setClickPickTransformableClearsDifferentSelection() {
    Transformable selected = new Transformable();
    Transformable clicked = new Transformable();
    state.setCurrentlySelectedObject(selected);

    state.setClickPickTransformable(clicked);

    assertNull(state.getCurrentlySelectedObject());
    assertSame(clicked, state.getClickPickTransformable());
  }

  @Test
  public void setClickPickTransformableSameValueKeepsSelection() {
    Transformable clicked = new Transformable();
    state.setClickPickTransformable(clicked);
    state.setCurrentlySelectedObject(new Transformable());

    state.setClickPickTransformable(clicked);

    assertNotNull(state.getCurrentlySelectedObject());
  }

  @Test
  public void timeCapturedRoundTripsLongValue() {
    state.setTimeCaptured(12345L);
    assertEquals(12345L, state.getTimeCaptured());
  }

  @Test
  public void copyStateClonesKeyMapIndependently() {
    InputState source = new InputState();
    source.setKeyState(KeyEvent.VK_X, true);
    InputState copy = new InputState();
    copy.copyState(source);
    source.setKeyState(KeyEvent.VK_X, false);

    assertTrue(copy.isKeyDown(KeyEvent.VK_X));
  }

  @Test
  public void copyStateClonesMouseMapIndependently() {
    InputState source = new InputState();
    source.setMouseState(1, true);
    InputState copy = new InputState(source);
    source.setMouseState(1, false);

    assertTrue(copy.isMouseDown(1));
  }

  @Test
  public void copyStateDoesNotCopyInputEventReference() {
    InputState source = new InputState();
    source.setInputEvent(wheelEvent(1));
    InputState copy = new InputState();

    copy.copyState(source);

    assertNull(copy.getInputEvent());
  }

  @Test
  public void toStringIncludesActiveKeyAndMouseButton() {
    state.setKeyState(KeyEvent.VK_A, true);
    state.setMouseState(1, true);
    String text = state.toString();

    assertTrue(text.contains("A"));
    assertTrue(text.contains("button 1"));
  }
}
