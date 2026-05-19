package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Deep coverage tests for {@link AbstractModel} — construction, enable/disable,
 * relocalize, safeSetNameAndMnemonic static method, and hierarchy.
 */
public class AbstractModelDeepTest {

  private TestModel model;

  @Before
  public void setUp() {
    model = new TestModel();
  }

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void construction_nonNull() {
    assertNotNull(model);
  }

  @Test
  public void getMigrationId_nonNull() {
    assertNotNull(model.getMigrationId());
  }

  // ── Enable/Disable ────────────────────────────────────────────────

  @Test
  public void isEnabled_defaultTrue() {
    assertTrue(model.isEnabled());
  }

  @Test
  public void setEnabled_false() {
    model.setEnabled(false);
    assertFalse(model.isEnabled());
  }

  @Test
  public void setEnabled_true_afterDisable() {
    model.setEnabled(false);
    model.setEnabled(true);
    assertTrue(model.isEnabled());
  }

  @Test
  public void setEnabled_sameValue_noChange() {
    model.setEnabled(true);
    assertTrue(model.isEnabled());
  }

  @Test
  public void setEnabled_toggleMultiple() {
    model.setEnabled(false);
    model.setEnabled(true);
    model.setEnabled(false);
    assertFalse(model.isEnabled());
  }

  // ── relocalize ────────────────────────────────────────────────────

  @Test
  public void relocalize_callsLocalize() {
    model.localizeCalled = false;
    model.relocalize();
    assertTrue(model.localizeCalled);
  }

  @Test
  public void relocalize_isFinal() throws Exception {
    Method m = AbstractModel.class.getMethod("relocalize");
    assertTrue(Modifier.isFinal(m.getModifiers()));
  }

  // ── Hierarchy ─────────────────────────────────────────────────────

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(AbstractModel.class.getModifiers()));
  }

  @Test
  public void class_extendsAbstractElement() {
    assertTrue(AbstractElement.class.isAssignableFrom(AbstractModel.class));
  }

  @Test
  public void class_implementsModel() {
    assertTrue(Model.class.isAssignableFrom(AbstractModel.class));
  }

  // ── safeSetNameAndMnemonic ────────────────────────────────────────

  @Test
  public void safeSetNameAndMnemonic_method_exists() throws Exception {
    Method m = AbstractModel.class.getDeclaredMethod("safeSetNameAndMnemonic",
        javax.swing.Action.class, String.class, int.class);
    assertNotNull(m);
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertTrue(Modifier.isProtected(m.getModifiers()));
  }

  @Test
  public void safeSetNameAndMnemonic_setsName() throws Exception {
    javax.swing.Action action = new javax.swing.AbstractAction() {
      @Override public void actionPerformed(java.awt.event.ActionEvent e) {}
    };
    Method m = AbstractModel.class.getDeclaredMethod("safeSetNameAndMnemonic",
        javax.swing.Action.class, String.class, int.class);
    m.setAccessible(true);
    m.invoke(null, action, "Test Name", 0);
    assertEquals("Test Name", action.getValue(javax.swing.Action.NAME));
  }

  @Test
  public void safeSetNameAndMnemonic_withMnemonic_setsKey() throws Exception {
    javax.swing.Action action = new javax.swing.AbstractAction() {
      @Override public void actionPerformed(java.awt.event.ActionEvent e) {}
    };
    Method m = AbstractModel.class.getDeclaredMethod("safeSetNameAndMnemonic",
        javax.swing.Action.class, String.class, int.class);
    m.setAccessible(true);
    m.invoke(null, action, "Test Name", java.awt.event.KeyEvent.VK_T);
    assertEquals("Test Name", action.getValue(javax.swing.Action.NAME));
    assertEquals(java.awt.event.KeyEvent.VK_T,
        action.getValue(javax.swing.Action.MNEMONIC_KEY));
  }

  @Test
  public void safeSetNameAndMnemonic_noMnemonic_doesNotSetKey() throws Exception {
    javax.swing.Action action = new javax.swing.AbstractAction() {
      @Override public void actionPerformed(java.awt.event.ActionEvent e) {}
    };
    Method m = AbstractModel.class.getDeclaredMethod("safeSetNameAndMnemonic",
        javax.swing.Action.class, String.class, int.class);
    m.setAccessible(true);
    m.invoke(null, action, "Test", 0);
    // mnemonic should not be set (remains 0 or null)
    Object mnemonicKey = action.getValue(javax.swing.Action.MNEMONIC_KEY);
    assertTrue(mnemonicKey == null || ((Integer) mnemonicKey) == 0);
  }

  @Test
  public void safeSetNameAndMnemonic_mnemonicNotInName_noIndex() throws Exception {
    javax.swing.Action action = new javax.swing.AbstractAction() {
      @Override public void actionPerformed(java.awt.event.ActionEvent e) {}
    };
    Method m = AbstractModel.class.getDeclaredMethod("safeSetNameAndMnemonic",
        javax.swing.Action.class, String.class, int.class);
    m.setAccessible(true);
    // 'Z' is not in "Test Name"
    m.invoke(null, action, "Test Name", java.awt.event.KeyEvent.VK_Z);
    assertEquals("Test Name", action.getValue(javax.swing.Action.NAME));
  }

  // ── toString ──────────────────────────────────────────────────────

  @Test
  public void toString_containsClassName() {
    assertTrue(model.toString().contains("TestModel"));
  }

  @Test
  public void toString_nonEmpty() {
    assertFalse(model.toString().isEmpty());
  }

  // ── initializeIfNecessary ─────────────────────────────────────────

  @Test
  public void initializeIfNecessary_callsLocalize() {
    model.localizeCalled = false;
    model.initializeIfNecessary();
    assertTrue(model.localizeCalled);
  }

  @Test
  public void initializeIfNecessary_idempotent() {
    model.initializeIfNecessary();
    model.localizeCalled = false;
    model.initializeIfNecessary();
    assertFalse(model.localizeCalled);
  }

  // ── Concrete test model ───────────────────────────────────────────

  static class TestModel extends AbstractModel {
    boolean localizeCalled = false;

    TestModel() {
      super(CroquetTestUtils.nextTestUUID());
    }

    @Override
    protected void localize() {
      localizeCalled = true;
    }
  }
}
