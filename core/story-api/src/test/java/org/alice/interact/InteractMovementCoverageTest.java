package org.alice.interact;

import org.alice.interact.handle.HandleSet;
import org.alice.math.immutable.Vector3;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InteractMovementCoverageTest {
  private static final double TOLERANCE = 1.0e-10;

  @Test
  public void movementDirectionExposesNormalizedVectorsAndOpposites() {
    Vector3 forward = MovementDirection.FORWARD.getVector();
    Vector3 upRight = MovementDirection.UP_RIGHT.getVector();

    assertEquals(0.0, forward.x(), TOLERANCE);
    assertEquals(0.0, forward.y(), TOLERANCE);
    assertEquals(-1.0, forward.z(), TOLERANCE);
    assertEquals(1.0, upRight.magnitude(), TOLERANCE);
    assertEquals(MovementDirection.BACKWARD, MovementDirection.FORWARD.getOpposite());
    assertEquals(MovementDirection.UP_RIGHT, MovementDirection.UP_RIGHT.getOpposite());
  }

  @Test
  public void movementDirectionMapsToExpectedHandleGroupsAndDirectionTests() {
    assertEquals(HandleSet.HandleGroup.X_AND_Y_AXIS, MovementDirection.UP_RIGHT.getHandleGroup());
    assertEquals(HandleSet.HandleGroup.Y_AND_Z_AXIS, MovementDirection.UP_FORWARD.getHandleGroup());
    assertEquals(HandleSet.HandleGroup.RESIZE_AXIS, MovementDirection.RESIZE.getHandleGroup());
    assertTrue(MovementDirection.LEFT.hasDirection(new Vector3(-1.0, 0.0, 0.0)));
    assertFalse(MovementDirection.LEFT.hasDirection(new Vector3(1.0, 0.0, 0.0)));
  }

  @Test
  public void modifierMaskEvaluatesRequiredAndForbiddenKeys() {
    InputState state = new InputState();
    state.setKeyState(ModifierMask.ModifierKey.SHIFT.getKeyValue(), true);
    state.setKeyState(ModifierMask.ModifierKey.CONTROL.getKeyValue(), false);
    state.setKeyState(ModifierMask.ModifierKey.ALT.getKeyValue(), false);

    ModifierMask shiftOnly = new ModifierMask(ModifierMask.JUST_SHIFT);

    assertTrue(shiftOnly.allValid(state));
    assertTrue(shiftOnly.test(state));
    assertTrue(new ModifierMask(ModifierMask.ModifierKey.SHIFT).anyValid(state));
    assertFalse(new ModifierMask(new ModifierMask.ModifierKey[] {
        ModifierMask.ModifierKey.CONTROL,
        ModifierMask.ModifierKey.ALT
    }).anyValid(state));
  }
}
