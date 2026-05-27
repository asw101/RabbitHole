package org.alice.interact;

import org.alice.interact.handle.HandleSet;
import org.alice.math.immutable.Vector3;
import org.junit.Test;

import java.awt.event.KeyEvent;
import java.lang.reflect.Constructor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class ModifierMaskAndMovementDirectionTest {
  @Test
  public void allMustBeValidRequiresEveryConfiguredModifier() {
    InputState state = new InputState();
    state.setKeyState(KeyEvent.VK_SHIFT, true);
    state.setKeyState(KeyEvent.VK_CONTROL, true);

    ModifierMask mask = new ModifierMask(new ModifierMask.ModifierKey[] {
        ModifierMask.ModifierKey.SHIFT,
        ModifierMask.ModifierKey.CONTROL,
    });

    assertTrue(mask.test(state));

    state.setKeyState(KeyEvent.VK_CONTROL, false);
    assertFalse(mask.test(state));
  }

  @Test
  public void anyMayBeValidMatchesWhenEitherModifierIsPressed() throws Exception {
    ModifierMask mask = anyMayBeValid(
        ModifierMask.ModifierKey.SHIFT,
        ModifierMask.ModifierKey.ALT);
    InputState state = new InputState();
    state.setKeyState(KeyEvent.VK_SHIFT, true);

    assertTrue(mask.test(state));

    state.setKeyState(KeyEvent.VK_SHIFT, false);
    state.setKeyState(KeyEvent.VK_ALT, true);
    assertTrue(mask.test(state));
  }

  @Test
  public void predefinedModifierArraysMatchExpectedStates() {
    InputState justShift = new InputState();
    justShift.setKeyState(KeyEvent.VK_SHIFT, true);
    assertTrue(new ModifierMask(ModifierMask.JUST_SHIFT).test(justShift));

    InputState noModifiers = new InputState();
    assertTrue(new ModifierMask(ModifierMask.NO_MODIFIERS_DOWN).test(noModifiers));
    assertFalse(new ModifierMask(ModifierMask.JUST_CONTROL).test(noModifiers));
  }

  @Test
  public void movementDirectionsMapToExpectedHandleGroupsAndVectorsAreNormalized() {
    assertEquals(HandleSet.HandleGroup.Z_AXIS, MovementDirection.FORWARD.getHandleGroup());
    assertEquals(HandleSet.HandleGroup.Y_AND_Z_AXIS, MovementDirection.UP_FORWARD.getHandleGroup());
    assertEquals(HandleSet.HandleGroup.RESIZE_AXIS, MovementDirection.RESIZE.getHandleGroup());

    assertEquals(1.0, MovementDirection.FORWARD.getVector().magnitude(), 1.0e-9);
    assertEquals(1.0, MovementDirection.UP_RIGHT.getVector().magnitude(), 1.0e-9);
    assertEquals(1.0, MovementDirection.LEFT_BACKWARD.getVector().magnitude(), 1.0e-9);
  }

  @Test
  public void movementOppositesOnlyFlipPrimaryAxes() {
    assertEquals(MovementDirection.BACKWARD, MovementDirection.FORWARD.getOpposite());
    assertEquals(MovementDirection.LEFT, MovementDirection.RIGHT.getOpposite());
    assertEquals(MovementDirection.DOWN, MovementDirection.UP.getOpposite());
    assertEquals(MovementDirection.UP_RIGHT, MovementDirection.UP_RIGHT.getOpposite());
    assertNotSame(MovementDirection.FORWARD, MovementDirection.FORWARD.getOpposite());
  }

  @Test
  public void movementDirectionHasDirectionUsesPositiveDotProduct() {
    assertTrue(MovementDirection.FORWARD.hasDirection(new Vector3(0.1, 0.0, -2.0)));
    assertFalse(MovementDirection.FORWARD.hasDirection(new Vector3(0.0, 0.0, 2.0)));
    assertTrue(MovementDirection.UP_RIGHT.hasDirection(new Vector3(2.0, 2.0, 0.0)));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private static ModifierMask anyMayBeValid(ModifierMask.ModifierKey... keys) throws Exception {
    Class testTypeClass = Class.forName("org.alice.interact.ModifierMask$TestType");
    Object testType = Enum.valueOf(testTypeClass, "ANY_MAY_BE_VALID");
    Constructor<ModifierMask> ctor = ModifierMask.class.getConstructor(ModifierMask.ModifierKey[].class, testTypeClass);
    return ctor.newInstance((Object) keys, testType);
  }
}
