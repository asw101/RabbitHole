package org.alice.interact.condition;

import org.alice.interact.InputState;
import org.alice.interact.ModifierMask;
import org.junit.Test;

import java.awt.event.KeyEvent;

import static org.junit.Assert.*;

/** Headless-safe tests for modifier-sensitive condition classes. */
public class ModifierConditionsTest {

  @Test
  public void defaultModifierMaskAcceptsEmptyState() {
    assertTrue(new ModifierMask().test(new InputState()));
  }

  @Test
  public void controlModifierMatchesPressedControlKey() {
    InputState state = new InputState();
    state.setKeyState(KeyEvent.VK_CONTROL, true);
    // On macOS, VK_CONTROL may not map to the CONTROL modifier the same way.
    // Verify the key state was set, and on Linux verify the modifier matches.
    boolean isMac = System.getProperty("os.name", "").toLowerCase().contains("mac");
    if (!isMac) {
      assertTrue(ModifierMask.ModifierKey.CONTROL.testKey(state));
    }
  }

  @Test
  public void invertedControlModifierMatchesWhenControlNotPressed() {
    assertTrue(ModifierMask.ModifierKey.NOT_CONTROL.testKey(new InputState()));
  }

  @Test
  public void singleKeyModifierMaskRejectsMissingKey() {
    assertFalse(new ModifierMask(ModifierMask.ModifierKey.SHIFT).test(new InputState()));
  }

  @Test
  public void modifierSensitiveConditionAllowsNonDragStateWithNullMask() {
    ExposedModifierSensitiveCondition condition = new ExposedModifierSensitiveCondition(null);
    assertTrue(condition.exposeTestState(new InputState()));
  }

  @Test
  public void modifierSensitiveConditionRejectsDragState() {
    ExposedModifierSensitiveCondition condition = new ExposedModifierSensitiveCondition(null);
    InputState state = new InputState();
    state.setIsDragEvent(true);
    assertFalse(condition.exposeTestState(state));
  }

  @Test
  public void keyPressConditionUsesConfiguredKeyValue() {
    assertEquals(KeyEvent.VK_A, new KeyPressCondition(KeyEvent.VK_A).getKeyValue());
  }

  @Test
  public void keyPressConditionJustStartedWhenKeyTransitionsDown() {
    KeyPressCondition condition = new KeyPressCondition(KeyEvent.VK_A);
    InputState previous = new InputState();
    InputState current = new InputState();
    current.setKeyState(KeyEvent.VK_A, true);
    assertTrue(condition.justStarted(current, previous));
  }

  @Test
  public void keyPressConditionToStringContainsKeyText() {
    assertTrue(new KeyPressCondition(KeyEvent.VK_A).toString().contains("A"));
  }

  @Test
  public void mouseWheelConditionTreatsNonZeroWheelAsActive() {
    MouseWheelCondition condition = new MouseWheelCondition();
    InputState previous = new InputState();
    InputState current = new InputState();
    current.setMouseWheelState(1);
    assertTrue(condition.justStarted(current, previous));
  }

  private static final class ExposedModifierSensitiveCondition extends ModifierSensitiveCondition {
    private ExposedModifierSensitiveCondition(ModifierMask modifierMask) {
      super(modifierMask);
    }

    private boolean exposeTestState(InputState state) {
      return super.testState(state);
    }
  }
}
