package org.lgna.croquet;

import org.junit.Test;

import javax.swing.AbstractAction;
import javax.swing.Action;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Extended tests for {@link AbstractModel} — deeper coverage of
 * safeSetNameAndMnemonic edge cases, getMigrationId, isEnabled/setEnabled,
 * and relocalize.
 */
public class AbstractModelExtendedTest {

  // ── safeSetNameAndMnemonic: all mnemonic positions ────────────────

  @Test
  public void safeSetNameAndMnemonic_mnemonicAtStart() {
    Action action = createAction();
    AbstractModel.safeSetNameAndMnemonic(action, "Save", KeyEvent.VK_S);
    assertEquals(KeyEvent.VK_S, action.getValue(Action.MNEMONIC_KEY));
  }

  @Test
  public void safeSetNameAndMnemonic_mnemonicAtEnd() {
    Action action = createAction();
    AbstractModel.safeSetNameAndMnemonic(action, "Close", KeyEvent.VK_E);
    assertEquals(KeyEvent.VK_E, action.getValue(Action.MNEMONIC_KEY));
  }

  @Test
  public void safeSetNameAndMnemonic_mnemonicInMiddle() {
    Action action = createAction();
    AbstractModel.safeSetNameAndMnemonic(action, "Edit", KeyEvent.VK_D);
    assertEquals(KeyEvent.VK_D, action.getValue(Action.MNEMONIC_KEY));
  }

  @Test
  public void safeSetNameAndMnemonic_noMnemonic_setsNameOnly() {
    Action action = createAction();
    AbstractModel.safeSetNameAndMnemonic(action, "Help", 0);
    assertEquals("Help", action.getValue(Action.NAME));
  }

  @Test
  public void safeSetNameAndMnemonic_charNotInName_noMnemonicSet() {
    Action action = createAction();
    AbstractModel.safeSetNameAndMnemonic(action, "File", KeyEvent.VK_Z);
    Object mnemonic = action.getValue(Action.MNEMONIC_KEY);
    assertTrue("Mnemonic should be 0 or not VK_Z",
        mnemonic == null || ((int) mnemonic) != KeyEvent.VK_Z);
  }

  @Test
  public void safeSetNameAndMnemonic_bothCases_prefersLowerIndex() {
    Action action = createAction();
    // "aAbout" has 'a' at 0, 'A' at 1
    AbstractModel.safeSetNameAndMnemonic(action, "aAbout", KeyEvent.VK_A);
    assertEquals(KeyEvent.VK_A, action.getValue(Action.MNEMONIC_KEY));
  }

  @Test
  public void safeSetNameAndMnemonic_onlyUpperCase_usesIt() {
    Action action = createAction();
    AbstractModel.safeSetNameAndMnemonic(action, "QUIT", KeyEvent.VK_Q);
    assertEquals(KeyEvent.VK_Q, action.getValue(Action.MNEMONIC_KEY));
  }

  @Test
  public void safeSetNameAndMnemonic_onlyLowerCase_usesIt() {
    Action action = createAction();
    AbstractModel.safeSetNameAndMnemonic(action, "open", KeyEvent.VK_O);
    assertEquals(KeyEvent.VK_O, action.getValue(Action.MNEMONIC_KEY));
  }

  @Test
  public void safeSetNameAndMnemonic_prevMnemonicCleared() {
    Action action = createAction();
    action.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_X);
    AbstractModel.safeSetNameAndMnemonic(action, "Save", KeyEvent.VK_S);
    assertEquals(KeyEvent.VK_S, action.getValue(Action.MNEMONIC_KEY));
  }

  @Test
  public void safeSetNameAndMnemonic_prevMnemonicToZero() {
    Action action = createAction();
    action.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
    AbstractModel.safeSetNameAndMnemonic(action, "File", 0);
    Object mnemonic = action.getValue(Action.MNEMONIC_KEY);
    assertTrue("Should be cleared", mnemonic == null || (int) mnemonic == 0);
  }

  @Test
  public void safeSetNameAndMnemonic_emptyName_noMnemonicSet() {
    Action action = createAction();
    AbstractModel.safeSetNameAndMnemonic(action, "", KeyEvent.VK_A);
    assertEquals("", action.getValue(Action.NAME));
  }

  @Test
  public void safeSetNameAndMnemonic_singleCharName_matches() {
    Action action = createAction();
    AbstractModel.safeSetNameAndMnemonic(action, "X", KeyEvent.VK_X);
    assertEquals(KeyEvent.VK_X, action.getValue(Action.MNEMONIC_KEY));
  }

  @Test
  public void safeSetNameAndMnemonic_singleCharName_noMatch() {
    Action action = createAction();
    AbstractModel.safeSetNameAndMnemonic(action, "X", KeyEvent.VK_A);
    Object mnemonic = action.getValue(Action.MNEMONIC_KEY);
    assertTrue("Should not be VK_A", mnemonic == null || (int) mnemonic != KeyEvent.VK_A);
  }

  // ── safeSetNameAndMnemonic: sequential calls ──────────────────────

  @Test
  public void safeSetNameAndMnemonic_calledTwice_secondWins() {
    Action action = createAction();
    AbstractModel.safeSetNameAndMnemonic(action, "First", KeyEvent.VK_F);
    AbstractModel.safeSetNameAndMnemonic(action, "Second", KeyEvent.VK_S);
    assertEquals("Second", action.getValue(Action.NAME));
    assertEquals(KeyEvent.VK_S, action.getValue(Action.MNEMONIC_KEY));
  }

  @Test
  public void safeSetNameAndMnemonic_calledThreeTimes() {
    Action action = createAction();
    AbstractModel.safeSetNameAndMnemonic(action, "A", KeyEvent.VK_A);
    AbstractModel.safeSetNameAndMnemonic(action, "Bravo", KeyEvent.VK_B);
    AbstractModel.safeSetNameAndMnemonic(action, "Charlie", KeyEvent.VK_C);
    assertEquals("Charlie", action.getValue(Action.NAME));
    assertEquals(KeyEvent.VK_C, action.getValue(Action.MNEMONIC_KEY));
  }

  // ── isEnabled / setEnabled via concrete subclass ──────────────────

  @Test
  public void concreteModel_isEnabled_defaultTrue() {
    TestModel model = new TestModel();
    assertTrue(model.isEnabled());
  }

  @Test
  public void concreteModel_setEnabled_false() {
    TestModel model = new TestModel();
    model.setEnabled(false);
    assertFalse(model.isEnabled());
  }

  @Test
  public void concreteModel_setEnabled_toggle() {
    TestModel model = new TestModel();
    model.setEnabled(false);
    model.setEnabled(true);
    assertTrue(model.isEnabled());
  }

  @Test
  public void concreteModel_setEnabled_sameValueNoException() {
    TestModel model = new TestModel();
    model.setEnabled(true);
    assertTrue(model.isEnabled());
  }

  // ── getMigrationId ────────────────────────────────────────────────

  @Test
  public void concreteModel_getMigrationId_matchesConstructorId() {
    UUID id = UUID.fromString("00000000-1111-2222-3333-444444444444");
    TestModel model = new TestModel(id);
    assertEquals(id, model.getMigrationId());
  }

  // ── relocalize ────────────────────────────────────────────────────

  @Test
  public void concreteModel_relocalize_callsLocalize() {
    TestModel model = new TestModel();
    model.initializeIfNecessary();
    model.localizeCalled = false;
    model.relocalize();
    assertTrue(model.localizeCalled);
  }

  // ── Test infrastructure ───────────────────────────────────────────

  private static Action createAction() {
    return new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {}
    };
  }

  static class TestModel extends AbstractModel {
    boolean localizeCalled = false;

    TestModel() {
      super(CroquetTestUtils.nextTestUUID());
    }

    TestModel(UUID id) {
      super(id);
    }

    @Override
    protected void localize() {
      localizeCalled = true;
    }

    @Override
    protected Class<? extends Element> getClassUsedForLocalization() {
      return TestModel.class;
    }

    @Override
    protected String getSubKeyForLocalization() {
      return "test";
    }
  }
}
