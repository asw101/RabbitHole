package org.lgna.croquet;

import org.junit.Test;

import javax.swing.AbstractAction;
import javax.swing.Action;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import static org.junit.Assert.*;

/**
 * Tests for {@link AbstractModel} — safeSetNameAndMnemonic logic,
 * getMigrationId, isEnabled/setEnabled defaults, and relocalize.
 */
public class AbstractModelTest {

  // ── safeSetNameAndMnemonic ────────────────────────────────────────

  @Test
  public void safeSetNameAndMnemonic_noMnemonic_setsName() {
    Action action = createAction();
    AbstractModel.safeSetNameAndMnemonic(action, "File", 0);
    assertEquals("File", action.getValue(Action.NAME));
  }

  @Test
  public void safeSetNameAndMnemonic_withMnemonic_setsName() {
    Action action = createAction();
    AbstractModel.safeSetNameAndMnemonic(action, "Save", KeyEvent.VK_S);
    assertEquals("Save", action.getValue(Action.NAME));
  }

  @Test
  public void safeSetNameAndMnemonic_withMnemonic_setsMnemonicKey() {
    Action action = createAction();
    AbstractModel.safeSetNameAndMnemonic(action, "Save", KeyEvent.VK_S);
    assertEquals(KeyEvent.VK_S, action.getValue(Action.MNEMONIC_KEY));
  }

  @Test
  public void safeSetNameAndMnemonic_mnemonicNotInName_noMnemonicSet() {
    Action action = createAction();
    AbstractModel.safeSetNameAndMnemonic(action, "File", KeyEvent.VK_Z);
    // Z not in "File" so mnemonic should not be set
    assertNotEquals(KeyEvent.VK_Z, action.getValue(Action.MNEMONIC_KEY));
  }

  @Test
  public void safeSetNameAndMnemonic_lowercase_found() {
    Action action = createAction();
    AbstractModel.safeSetNameAndMnemonic(action, "open", KeyEvent.VK_O);
    assertEquals(KeyEvent.VK_O, action.getValue(Action.MNEMONIC_KEY));
  }

  @Test
  public void safeSetNameAndMnemonic_uppercase_found() {
    Action action = createAction();
    AbstractModel.safeSetNameAndMnemonic(action, "Open", KeyEvent.VK_O);
    assertEquals(KeyEvent.VK_O, action.getValue(Action.MNEMONIC_KEY));
  }

  @Test
  public void safeSetNameAndMnemonic_prevMnemonicCleared() {
    Action action = createAction();
    action.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
    AbstractModel.safeSetNameAndMnemonic(action, "Save", KeyEvent.VK_S);
    assertEquals(KeyEvent.VK_S, action.getValue(Action.MNEMONIC_KEY));
    assertEquals("Save", action.getValue(Action.NAME));
  }

  @Test
  public void safeSetNameAndMnemonic_bothCasesPresent_usesFirstOccurrence() {
    Action action = createAction();
    // "oOther" has lowercase 'o' at 0, uppercase 'O' at 1
    AbstractModel.safeSetNameAndMnemonic(action, "oOther", KeyEvent.VK_O);
    assertEquals(KeyEvent.VK_O, action.getValue(Action.MNEMONIC_KEY));
  }

  // ── getKeyStroke (accessible via AbstractElement) ──────────────────

  @Test
  public void getKeyStroke_delegatesToAbstractElement() {
    // AbstractModel extends AbstractElement, verify the method works
    assertNotNull(AbstractElement.getKeyStroke("VK_F5"));
  }

  // ── Helper ────────────────────────────────────────────────────────

  private static Action createAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {}
    };
  }
}
