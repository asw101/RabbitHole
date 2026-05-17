package org.alice.interact;

import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.UnitQuaternion;
import org.junit.Test;

import java.awt.event.KeyEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for QuaternionAndTranslation, ModifierMask, MovementKey, InteractionGroup.
 * All headless-safe value/helper classes.
 */
public class InteractHelpersTest {

  // ══════════════════════════════════════════════════════════════════════════
  //  QuaternionAndTranslation
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void defaultConstructorCreatesIdentity() {
    QuaternionAndTranslation qt = new QuaternionAndTranslation();
    assertEquals(UnitQuaternion.IDENTITY, qt.getQuaternion());
    assertEquals(Point3.ORIGIN, qt.getTranslation());
  }

  @Test
  public void parameterConstructor() {
    Point3 t = new Point3(1, 2, 3);
    QuaternionAndTranslation qt = new QuaternionAndTranslation(UnitQuaternion.IDENTITY, t);
    assertEquals(UnitQuaternion.IDENTITY, qt.getQuaternion());
    assertEquals(t, qt.getTranslation());
  }

  @Test
  public void copyConstructor() {
    Point3 t = new Point3(4, 5, 6);
    QuaternionAndTranslation original = new QuaternionAndTranslation(UnitQuaternion.IDENTITY, t);
    QuaternionAndTranslation copy = new QuaternionAndTranslation(original);
    assertEquals(original.getQuaternion(), copy.getQuaternion());
    assertEquals(original.getTranslation(), copy.getTranslation());
  }

  @Test
  public void matrixConstructor() {
    AffineMatrix4x4 m = AffineMatrix4x4.createTranslation(7, 8, 9);
    QuaternionAndTranslation qt = new QuaternionAndTranslation(m);
    assertEquals(7.0, qt.getTranslation().x(), 1e-6);
    assertEquals(8.0, qt.getTranslation().y(), 1e-6);
    assertEquals(9.0, qt.getTranslation().z(), 1e-6);
  }

  @Test
  public void getAffineMatrixRoundTrips() {
    AffineMatrix4x4 m = AffineMatrix4x4.createTranslation(1, 2, 3);
    QuaternionAndTranslation qt = new QuaternionAndTranslation(m);
    AffineMatrix4x4 result = qt.getAffineMatrix();
    assertNotNull(result);
    assertEquals(1.0, result.translation().x(), 1e-6);
    assertEquals(2.0, result.translation().y(), 1e-6);
    assertEquals(3.0, result.translation().z(), 1e-6);
  }

  @Test
  public void interpolationAtZeroReturnsSelf() {
    QuaternionAndTranslation a = new QuaternionAndTranslation(UnitQuaternion.IDENTITY, new Point3(0, 0, 0));
    QuaternionAndTranslation b = new QuaternionAndTranslation(UnitQuaternion.IDENTITY, new Point3(10, 10, 10));
    QuaternionAndTranslation result = new QuaternionAndTranslation();
    result.setToInterpolation(a, b, 0.0);
    assertEquals(0.0, result.getTranslation().x(), 1e-6);
  }

  @Test
  public void interpolationAtOneReturnsTarget() {
    QuaternionAndTranslation a = new QuaternionAndTranslation(UnitQuaternion.IDENTITY, new Point3(0, 0, 0));
    QuaternionAndTranslation b = new QuaternionAndTranslation(UnitQuaternion.IDENTITY, new Point3(10, 10, 10));
    QuaternionAndTranslation result = new QuaternionAndTranslation();
    result.setToInterpolation(a, b, 1.0);
    assertEquals(10.0, result.getTranslation().x(), 1e-6);
  }

  @Test
  public void interpolationAtHalf() {
    QuaternionAndTranslation a = new QuaternionAndTranslation(UnitQuaternion.IDENTITY, new Point3(0, 0, 0));
    QuaternionAndTranslation b = new QuaternionAndTranslation(UnitQuaternion.IDENTITY, new Point3(10, 10, 10));
    QuaternionAndTranslation result = new QuaternionAndTranslation();
    result.setToInterpolation(a, b, 0.5);
    assertEquals(5.0, result.getTranslation().x(), 1e-6);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  ModifierMask
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void modifierKeyEnumValues() {
    ModifierMask.ModifierKey[] keys = ModifierMask.ModifierKey.values();
    assertEquals(6, keys.length);
  }

  @Test
  public void modifierKeyHasKeyValue() {
    assertTrue(ModifierMask.ModifierKey.CONTROL.getKeyValue() > 0);
    assertTrue(ModifierMask.ModifierKey.SHIFT.getKeyValue() > 0);
    assertTrue(ModifierMask.ModifierKey.ALT.getKeyValue() > 0);
  }

  @Test
  public void modifierKeyTestKeyControlDown() {
    InputState state = new InputState();
    int controlKey = ModifierMask.ModifierKey.CONTROL.getKeyValue();
    state.setKeyState(controlKey, true);
    assertTrue(ModifierMask.ModifierKey.CONTROL.testKey(state));
    assertFalse(ModifierMask.ModifierKey.NOT_CONTROL.testKey(state));
  }

  @Test
  public void modifierKeyTestKeyControlUp() {
    InputState state = new InputState();
    assertFalse(ModifierMask.ModifierKey.CONTROL.testKey(state));
    assertTrue(ModifierMask.ModifierKey.NOT_CONTROL.testKey(state));
  }

  @Test
  public void modifierKeyTestKeyShiftDown() {
    InputState state = new InputState();
    state.setKeyState(KeyEvent.VK_SHIFT, true);
    assertTrue(ModifierMask.ModifierKey.SHIFT.testKey(state));
    assertFalse(ModifierMask.ModifierKey.NOT_SHIFT.testKey(state));
  }

  @Test
  public void modifierMaskDefaultConstructor() {
    ModifierMask mask = new ModifierMask();
    InputState state = new InputState();
    assertTrue(mask.test(state));
  }

  @Test
  public void modifierMaskNoModifiersDown() {
    ModifierMask mask = new ModifierMask(ModifierMask.NO_MODIFIERS_DOWN);
    InputState state = new InputState();
    assertTrue("No keys down should pass NO_MODIFIERS_DOWN", mask.test(state));
  }

  @Test
  public void modifierMaskNoModifiersFailsWhenShiftDown() {
    ModifierMask mask = new ModifierMask(ModifierMask.NO_MODIFIERS_DOWN);
    InputState state = new InputState();
    state.setKeyState(KeyEvent.VK_SHIFT, true);
    assertFalse("Shift down should fail NO_MODIFIERS_DOWN", mask.test(state));
  }

  @Test
  public void modifierMaskJustShift() {
    ModifierMask mask = new ModifierMask(ModifierMask.JUST_SHIFT);
    InputState state = new InputState();
    state.setKeyState(KeyEvent.VK_SHIFT, true);
    assertTrue("Shift-only should pass JUST_SHIFT", mask.test(state));
  }

  @Test
  public void modifierMaskJustControl() {
    ModifierMask mask = new ModifierMask(ModifierMask.JUST_CONTROL);
    InputState state = new InputState();
    int controlKey = ModifierMask.ModifierKey.CONTROL.getKeyValue();
    state.setKeyState(controlKey, true);
    assertTrue("Control-only should pass JUST_CONTROL", mask.test(state));
  }

  @Test
  public void modifierMaskJustAlt() {
    ModifierMask mask = new ModifierMask(ModifierMask.JUST_ALT);
    InputState state = new InputState();
    int altKey = ModifierMask.ModifierKey.ALT.getKeyValue();
    state.setKeyState(altKey, true);
    assertTrue("Alt-only should pass JUST_ALT", mask.test(state));
  }

  @Test
  public void modifierMaskAnyValidWithSingleKey() {
    ModifierMask mask = new ModifierMask(new ModifierMask.ModifierKey[] {
        ModifierMask.ModifierKey.SHIFT
    });
    InputState state = new InputState();
    assertFalse(mask.anyValid(state));
    state.setKeyState(KeyEvent.VK_SHIFT, true);
    assertTrue(mask.anyValid(state));
  }

  @Test
  public void modifierMaskSingleKeyConstructor() {
    ModifierMask mask = new ModifierMask(ModifierMask.ModifierKey.SHIFT);
    InputState state = new InputState();
    state.setKeyState(KeyEvent.VK_SHIFT, true);
    assertTrue(mask.test(state));
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  MovementKey
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void movementKeyConstructorTwoArgs() {
    MovementKey mk = new MovementKey(KeyEvent.VK_UP, new org.alice.interact.condition.MovementDescription(MovementDirection.FORWARD));
    assertNotNull(mk);
  }

  @Test
  public void movementKeyConstructorThreeArgs() {
    MovementKey mk = new MovementKey(KeyEvent.VK_PAGE_UP, new org.alice.interact.condition.MovementDescription(MovementDirection.UP, MovementType.LOCAL), 0.5);
    assertNotNull(mk);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  MovementDirection
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void movementDirectionValues() {
    MovementDirection[] dirs = MovementDirection.values();
    assertTrue(dirs.length >= 6);
  }

  @Test
  public void movementDirectionForward() {
    assertNotNull(MovementDirection.FORWARD);
    assertNotNull(MovementDirection.BACKWARD);
    assertNotNull(MovementDirection.LEFT);
    assertNotNull(MovementDirection.RIGHT);
    assertNotNull(MovementDirection.UP);
    assertNotNull(MovementDirection.DOWN);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  MovementType
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void movementTypeValues() {
    MovementType[] types = MovementType.values();
    assertTrue(types.length >= 2);
    assertNotNull(MovementType.LOCAL);
    assertNotNull(MovementType.STOOD_UP);
  }

  // ══════════════════════════════════════════════════════════════════════════
  //  Static arrays in DragAdapter
  // ══════════════════════════════════════════════════════════════════════════

  @Test
  public void boundingBoxKeyIsNotNull() {
    assertNotNull(DragAdapter.BOUNDING_BOX_KEY);
  }
}
