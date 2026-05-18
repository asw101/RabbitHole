package org.alice.interact.condition;

import org.alice.interact.InputState;
import org.alice.interact.ModifierMask;
import org.junit.Test;

import java.awt.event.KeyEvent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class KeyPressConditionTest {
  private static final class ExposedKeyPressCondition extends KeyPressCondition {
    private ExposedKeyPressCondition(int keyValue, ModifierMask modifierMask) {
      super(keyValue, modifierMask);
    }

    public boolean test(InputState state) {
      return super.testState(state);
    }
  }

  private static final class ExposedInvertedKeyPressCondition extends InvertedKeyPressCondition {
    private ExposedInvertedKeyPressCondition(int keyValue) {
      super(keyValue);
    }

    public boolean test(InputState state) {
      return super.testState(state);
    }
  }

  private static InputState state(boolean keyDown, boolean dragEvent, boolean shiftDown) {
    InputState state = new InputState();
    state.setKeyState(KeyEvent.VK_A, keyDown);
    state.setKeyState(KeyEvent.VK_SHIFT, shiftDown);
    state.setIsDragEvent(dragEvent);
    return state;
  }

  @Test
  public void keyPressConditionMatchesPressedKey() {
    assertTrue(new ExposedKeyPressCondition(KeyEvent.VK_A, null).test(state(true, false, false)));
  }

  @Test
  public void keyPressConditionRejectsReleasedKey() {
    assertFalse(new ExposedKeyPressCondition(KeyEvent.VK_A, null).test(state(false, false, false)));
  }

  @Test
  public void justStartedFiresOnKeyDownTransition() {
    KeyPressCondition condition = new KeyPressCondition(KeyEvent.VK_A);
    assertTrue(condition.justStarted(state(true, false, false), state(false, false, false)));
  }

  @Test
  public void justEndedFiresOnKeyUpTransition() {
    KeyPressCondition condition = new KeyPressCondition(KeyEvent.VK_A);
    assertTrue(condition.justEnded(state(false, false, false), state(true, false, false)));
  }

  @Test
  public void stateChangedTracksKeyToggle() {
    KeyPressCondition condition = new KeyPressCondition(KeyEvent.VK_A);
    assertTrue(condition.stateChanged(state(true, false, false), state(false, false, false)));
  }

  @Test
  public void dragEventSuppressesCondition() {
    assertFalse(new ExposedKeyPressCondition(KeyEvent.VK_A, null).test(state(true, true, false)));
  }

  @Test
  public void modifierMaskMustMatchToPass() {
    ExposedKeyPressCondition condition = new ExposedKeyPressCondition(KeyEvent.VK_A, new ModifierMask(ModifierMask.ModifierKey.SHIFT));
    assertTrue(condition.test(state(true, false, true)));
    assertFalse(condition.test(state(true, false, false)));
  }

  @Test
  public void toStringUsesKeyText() {
    assertTrue(new KeyPressCondition(KeyEvent.VK_A).toString().contains("A"));
  }

  @Test
  public void invertedConditionIsFalseWhenKeyIsUp() {
    assertFalse(new ExposedInvertedKeyPressCondition(KeyEvent.VK_A).test(state(false, false, false)));
  }

  @Test
  public void invertedConditionIsAlsoFalseWhenKeyIsDown() {
    assertFalse(new ExposedInvertedKeyPressCondition(KeyEvent.VK_A).test(state(true, false, false)));
  }
}
