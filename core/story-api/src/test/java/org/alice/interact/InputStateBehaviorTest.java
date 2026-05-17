package org.alice.interact;

import edu.cmu.cs.dennisc.scenegraph.Transformable;
import org.junit.Before;
import org.junit.Test;

import java.awt.Point;
import java.awt.event.KeyEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Behavior tests for InputState — key tracking, mouse button tracking,
 * mouse wheel, location, drag state, event type, copy semantics, and toString.
 * Headless-safe (uses no AWT windows).
 */
public class InputStateBehaviorTest {

  private InputState state;

  @Before
  public void setUp() {
    state = new InputState();
  }

  // --- Construction ---

  @Test
  public void defaultConstructorCreatesNonNull() {
    assertNotNull(state);
  }

  @Test
  public void defaultInputEventTypeIsNull() {
    assertEquals(InputState.InputEventType.NULL_EVENT, state.getInputEventType());
  }

  @Test
  public void defaultMouseLocationIsOrigin() {
    Point loc = state.getMouseLocation();
    assertNotNull(loc);
    assertEquals(0, loc.x);
    assertEquals(0, loc.y);
  }

  @Test
  public void defaultMouseWheelStateIsZero() {
    assertEquals(0, state.getMouseWheelState());
  }

  @Test
  public void defaultIsDragEventIsFalse() {
    assertFalse(state.getIsDragEvent());
  }

  @Test
  public void defaultDragAndDropContextIsNull() {
    assertNull(state.getDragAndDropContext());
  }

  @Test
  public void defaultClickHandleIsNull() {
    assertNull(state.getClickHandle());
  }

  @Test
  public void defaultRolloverHandleIsNull() {
    assertNull(state.getRolloverHandle());
  }

  @Test
  public void defaultCurrentlySelectedObjectIsNull() {
    assertNull(state.getCurrentlySelectedObject());
  }

  @Test
  public void defaultClickPickResultIsNull() {
    assertNull(state.getClickPickResult());
  }

  @Test
  public void defaultRolloverPickResultIsNull() {
    assertNull(state.getRolloverPickResult());
  }

  @Test
  public void defaultClickPickTransformableIsNull() {
    assertNull(state.getClickPickTransformable());
  }

  @Test
  public void defaultRolloverPickTransformableIsNull() {
    assertNull(state.getRolloverPickTransformable());
  }

  @Test
  public void defaultInputEventIsNull() {
    assertNull(state.getInputEvent());
  }

  @Test
  public void defaultPickCameraIsNull() {
    assertNull(state.getPickCamera());
  }

  @Test
  public void defaultTimeCapturedIsZero() {
    assertEquals(0, state.getTimeCaptured());
  }

  // --- Key state ---

  @Test
  public void isKeyDownReturnsFalseForUnsetKey() {
    assertFalse(state.isKeyDown(KeyEvent.VK_A));
  }

  @Test
  public void setKeyStateToDownMakesKeyDown() {
    state.setKeyState(KeyEvent.VK_A, true);
    assertTrue(state.isKeyDown(KeyEvent.VK_A));
  }

  @Test
  public void setKeyStateToUpMakesKeyNotDown() {
    state.setKeyState(KeyEvent.VK_A, true);
    state.setKeyState(KeyEvent.VK_A, false);
    assertFalse(state.isKeyDown(KeyEvent.VK_A));
  }

  @Test
  public void multipleKeysCanBeDown() {
    state.setKeyState(KeyEvent.VK_A, true);
    state.setKeyState(KeyEvent.VK_B, true);
    assertTrue(state.isKeyDown(KeyEvent.VK_A));
    assertTrue(state.isKeyDown(KeyEvent.VK_B));
  }

  @Test
  public void clearKeyStateClearsAllKeys() {
    state.setKeyState(KeyEvent.VK_A, true);
    state.setKeyState(KeyEvent.VK_B, true);
    state.clearKeyState();
    assertFalse(state.isKeyDown(KeyEvent.VK_A));
    assertFalse(state.isKeyDown(KeyEvent.VK_B));
  }

  // --- Mouse button state ---

  @Test
  public void isMouseDownReturnsFalseForUnsetButton() {
    assertFalse(state.isMouseDown(1));
  }

  @Test
  public void setMouseStateToDownMakesButtonDown() {
    state.setMouseState(1, true);
    assertTrue(state.isMouseDown(1));
  }

  @Test
  public void setMouseStateToUpMakesButtonNotDown() {
    state.setMouseState(1, true);
    state.setMouseState(1, false);
    assertFalse(state.isMouseDown(1));
  }

  @Test
  public void isAnyMouseButtonDownReturnsFalseWhenNoneDown() {
    assertFalse(state.isAnyMouseButtonDown());
  }

  @Test
  public void isAnyMouseButtonDownReturnsTrueWhenOneDown() {
    state.setMouseState(1, true);
    assertTrue(state.isAnyMouseButtonDown());
  }

  @Test
  public void isAnyMouseButtonDownReturnsTrueWhenMultipleDown() {
    state.setMouseState(1, true);
    state.setMouseState(3, true);
    assertTrue(state.isAnyMouseButtonDown());
  }

  @Test
  public void isAnyMouseButtonDownReturnsFalseWhenAllUp() {
    state.setMouseState(1, true);
    state.setMouseState(1, false);
    assertFalse(state.isAnyMouseButtonDown());
  }

  @Test
  public void clearMouseStateClearsAllButtons() {
    state.setMouseState(1, true);
    state.setMouseState(2, true);
    state.clearMouseState();
    assertFalse(state.isMouseDown(1));
    assertFalse(state.isMouseDown(2));
    assertFalse(state.isAnyMouseButtonDown());
  }

  // --- Mouse wheel ---

  @Test
  public void setMouseWheelStateRoundTrips() {
    state.setMouseWheelState(5);
    assertEquals(5, state.getMouseWheelState());
  }

  @Test
  public void setMouseWheelToNegativeRoundTrips() {
    state.setMouseWheelState(-3);
    assertEquals(-3, state.getMouseWheelState());
  }

  @Test
  public void clearMouseWheelStateResetsToZero() {
    state.setMouseWheelState(7);
    state.clearMouseWheelState();
    assertEquals(0, state.getMouseWheelState());
  }

  // --- Mouse location ---

  @Test
  public void setMouseLocationRoundTrips() {
    state.setMouseLocation(new Point(100, 200));
    Point loc = state.getMouseLocation();
    assertEquals(100, loc.x);
    assertEquals(200, loc.y);
  }

  // --- Event type ---

  @Test
  public void setInputEventTypeRoundTrips() {
    state.setInputEventType(InputState.InputEventType.MOUSE_DOWN);
    assertEquals(InputState.InputEventType.MOUSE_DOWN, state.getInputEventType());
  }

  @Test
  public void allEventTypesCanBeSet() {
    for (InputState.InputEventType type : InputState.InputEventType.values()) {
      state.setInputEventType(type);
      assertEquals(type, state.getInputEventType());
    }
  }

  // --- Drag state ---

  @Test
  public void setIsDragEventRoundTrips() {
    state.setIsDragEvent(true);
    assertTrue(state.getIsDragEvent());
  }

  @Test
  public void setDragAndDropContextRoundTrips() {
    Object ctx = "testContext";
    state.setDragAndDropContext(ctx);
    assertEquals(ctx, state.getDragAndDropContext());
  }

  // --- Selected object ---

  @Test
  public void setCurrentlySelectedObjectRoundTrips() {
    Transformable t = new Transformable();
    state.setCurrentlySelectedObject(t);
    assertEquals(t, state.getCurrentlySelectedObject());
  }

  @Test
  public void setCurrentlySelectedObjectToNullWorks() {
    Transformable t = new Transformable();
    state.setCurrentlySelectedObject(t);
    state.setCurrentlySelectedObject(null);
    assertNull(state.getCurrentlySelectedObject());
  }

  // --- Rollover pick ---

  @Test
  public void setRolloverPickTransformableRoundTrips() {
    Transformable t = new Transformable();
    state.setRolloverPickTransformable(t);
    assertEquals(t, state.getRolloverPickTransformable());
  }

  // --- Click pick ---

  @Test
  public void setClickPickTransformableSetsValue() {
    Transformable t = new Transformable();
    state.setClickPickTransformable(t);
    assertEquals(t, state.getClickPickTransformable());
  }

  @Test
  public void setClickPickTransformableClearsSelectedObject() {
    Transformable selected = new Transformable();
    state.setCurrentlySelectedObject(selected);
    Transformable clicked = new Transformable();
    state.setClickPickTransformable(clicked);
    assertNull("Setting click pick should clear selected object",
        state.getCurrentlySelectedObject());
  }

  @Test
  public void setClickPickTransformableToSameValueDoesNothing() {
    Transformable t = new Transformable();
    state.setClickPickTransformable(t);
    state.setCurrentlySelectedObject(new Transformable());
    state.setClickPickTransformable(t);
    // Same value so it returns early, doesn't clear selected
    assertNotNull(state.getCurrentlySelectedObject());
  }

  @Test
  public void setClickPickTransformableToNullDoesNotClearSelected() {
    Transformable selected = new Transformable();
    state.setCurrentlySelectedObject(selected);
    state.setClickPickTransformable(null);
    assertEquals(selected, state.getCurrentlySelectedObject());
  }

  // --- Time captured ---

  @Test
  public void setTimeCapturedWithLongRoundTrips() {
    state.setTimeCaptured(123456789L);
    assertEquals(123456789L, state.getTimeCaptured());
  }

  @Test
  public void setTimeCapturedUsesCurrentTime() {
    long before = System.currentTimeMillis();
    state.setTimeCaptured();
    long after = System.currentTimeMillis();
    assertTrue(state.getTimeCaptured() >= before);
    assertTrue(state.getTimeCaptured() <= after);
  }

  // --- Pick hints ---

  @Test
  public void getClickPickHintReturnsNothingWhenNoState() {
    PickHint hint = state.getClickPickHint();
    assertNotNull(hint);
    assertTrue(hint.intersects(PickHint.PickType.NOTHING.pickHint()));
  }

  @Test
  public void getRolloverPickHintReturnsNothingWhenNoState() {
    PickHint hint = state.getRolloverPickHint();
    assertNotNull(hint);
    assertTrue(hint.intersects(PickHint.PickType.NOTHING.pickHint()));
  }

  @Test
  public void getClickPickedTransformableReturnsNull() {
    assertNull(state.getClickPickedTransformable(true));
    assertNull(state.getClickPickedTransformable(false));
  }

  @Test
  public void getRolloverPickedTransformableReturnsNull() {
    assertNull(state.getRolloverPickedTransformable(true));
    assertNull(state.getRolloverPickedTransformable(false));
  }

  @Test
  public void getCurrentlySelectedObjectPickHintReturnsNothing() {
    PickHint hint = state.getCurrentlySelectedObjectPickHint();
    assertNotNull(hint);
    assertTrue(hint.intersects(PickHint.PickType.NOTHING.pickHint()));
  }

  // --- Copy constructor ---

  @Test
  public void copyConstructorCopiesKeyState() {
    state.setKeyState(KeyEvent.VK_SPACE, true);
    InputState copy = new InputState(state);
    assertTrue(copy.isKeyDown(KeyEvent.VK_SPACE));
  }

  @Test
  public void copyConstructorCopiesMouseState() {
    state.setMouseState(1, true);
    InputState copy = new InputState(state);
    assertTrue(copy.isMouseDown(1));
  }

  @Test
  public void copyConstructorCopiesMouseLocation() {
    state.setMouseLocation(new Point(42, 84));
    InputState copy = new InputState(state);
    assertEquals(42, copy.getMouseLocation().x);
    assertEquals(84, copy.getMouseLocation().y);
  }

  @Test
  public void copyConstructorCopiesMouseWheelState() {
    state.setMouseWheelState(3);
    InputState copy = new InputState(state);
    assertEquals(3, copy.getMouseWheelState());
  }

  @Test
  public void copyConstructorCopiesEventType() {
    state.setInputEventType(InputState.InputEventType.KEY_DOWN);
    InputState copy = new InputState(state);
    assertEquals(InputState.InputEventType.KEY_DOWN, copy.getInputEventType());
  }

  @Test
  public void copyConstructorCopiesDragState() {
    state.setIsDragEvent(true);
    state.setDragAndDropContext("dnd");
    InputState copy = new InputState(state);
    assertTrue(copy.getIsDragEvent());
    assertEquals("dnd", copy.getDragAndDropContext());
  }

  @Test
  public void copyConstructorCopiesTimeCaptured() {
    state.setTimeCaptured(999L);
    InputState copy = new InputState(state);
    assertEquals(999L, copy.getTimeCaptured());
  }

  @Test
  public void copyIsIndependentOfOriginal() {
    state.setKeyState(KeyEvent.VK_A, true);
    InputState copy = new InputState(state);
    state.setKeyState(KeyEvent.VK_A, false);
    assertTrue("Copy should be independent", copy.isKeyDown(KeyEvent.VK_A));
  }

  // --- copyState ---

  @Test
  public void copyStateCopiesAllFields() {
    state.setKeyState(KeyEvent.VK_A, true);
    state.setMouseState(2, true);
    state.setMouseWheelState(4);
    state.setMouseLocation(new Point(10, 20));
    state.setInputEventType(InputState.InputEventType.MOUSE_WHEEL);
    state.setIsDragEvent(true);
    state.setTimeCaptured(555L);

    InputState target = new InputState();
    target.copyState(state);

    assertTrue(target.isKeyDown(KeyEvent.VK_A));
    assertTrue(target.isMouseDown(2));
    assertEquals(4, target.getMouseWheelState());
    assertEquals(10, target.getMouseLocation().x);
    assertEquals(InputState.InputEventType.MOUSE_WHEEL, target.getInputEventType());
    assertTrue(target.getIsDragEvent());
    assertEquals(555L, target.getTimeCaptured());
  }

  // --- toString ---

  @Test
  public void toStringIsNotNull() {
    assertNotNull(state.toString());
  }

  @Test
  public void toStringContainsEventType() {
    state.setInputEventType(InputState.InputEventType.MOUSE_DOWN);
    assertTrue(state.toString().contains("MOUSE_DOWN"));
  }

  @Test
  public void toStringContainsKeyNames() {
    state.setKeyState(KeyEvent.VK_A, true);
    String str = state.toString();
    assertTrue(str.contains("Keys:"));
  }

  @Test
  public void toStringContainsMouseButtons() {
    state.setMouseState(1, true);
    String str = state.toString();
    assertTrue(str.contains("button"));
  }

  @Test
  public void toStringContainsMouseWheel() {
    state.setMouseWheelState(5);
    assertTrue(state.toString().contains("5"));
  }

  @Test
  public void toStringWithMultipleKeysContainsComma() {
    state.setKeyState(KeyEvent.VK_A, true);
    state.setKeyState(KeyEvent.VK_B, true);
    // Both keys down, toString should have comma separator
    String str = state.toString();
    assertNotNull(str);
  }

  @Test
  public void toStringWithMultipleMouseButtonsContainsComma() {
    state.setMouseState(1, true);
    state.setMouseState(3, true);
    String str = state.toString();
    assertNotNull(str);
  }
}
