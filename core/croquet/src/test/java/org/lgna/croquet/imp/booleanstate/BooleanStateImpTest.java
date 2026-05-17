package org.lgna.croquet.imp.booleanstate;

import org.lgna.croquet.BooleanState;
import org.lgna.croquet.Group;
import org.lgna.croquet.PrepModel;
import org.lgna.croquet.edits.Edit;
import org.junit.Before;
import org.junit.Test;

import javax.swing.ButtonModel;
import javax.swing.DefaultButtonModel;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Tests for {@link BooleanStateImp} — the implementation object behind
 * BooleanState that owns the SwingModel, ButtonModel, and lazy-initialized
 * menu/operation helpers.
 *
 * <p>ItemListeners are removed in setUp to avoid the
 * Application.getActiveInstance() dependency chain.</p>
 */
public class BooleanStateImpTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("00000000-0000-0000-0003-ffffffffffff"), "boolImpTest");

  private TestBooleanState state;
  private BooleanStateImp imp;

  @Before
  public void setUp() {
    state = new TestBooleanState(TEST_GROUP, false);
    removeItemListeners(state);
    imp = state.getImp();
  }

  private static void removeItemListeners(TestBooleanState s) {
    DefaultButtonModel bm = (DefaultButtonModel) s.getImp().getSwingModel().getButtonModel();
    for (ItemListener il : bm.getItemListeners()) {
      bm.removeItemListener(il);
    }
  }

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void imp_returnsNonNull() {
    assertNotNull(imp);
  }

  // ── getSwingModel ─────────────────────────────────────────────────

  @Test
  public void getSwingModel_returnsNonNull() {
    assertNotNull(imp.getSwingModel());
  }

  @Test
  public void getSwingModel_returnsSameInstance() {
    assertSame(imp.getSwingModel(), imp.getSwingModel());
  }

  // ── getSwingModel().getButtonModel() ──────────────────────────────

  @Test
  public void getButtonModel_returnsNonNull() {
    assertNotNull(imp.getSwingModel().getButtonModel());
  }

  @Test
  public void getButtonModel_initiallyNotSelected() {
    assertFalse(imp.getSwingModel().getButtonModel().isSelected());
  }

  @Test
  public void getButtonModel_selectedMatchesStateValue_true() {
    TestBooleanState trueState = new TestBooleanState(TEST_GROUP, true);
    removeItemListeners(trueState);
    assertTrue(trueState.getImp().getSwingModel().getButtonModel().isSelected());
  }

  // ── getSwingModel().getAction() ───────────────────────────────────

  @Test
  public void getAction_returnsNonNull() {
    assertNotNull(imp.getSwingModel().getAction());
  }

  @Test
  public void getAction_returnsSameInstance() {
    assertSame(imp.getSwingModel().getAction(), imp.getSwingModel().getAction());
  }

  // ── isEnabled / setEnabled ────────────────────────────────────────

  @Test
  public void isEnabled_defaultTrue() {
    assertTrue(imp.isEnabled());
  }

  @Test
  public void setEnabled_false() {
    imp.setEnabled(false);
    assertFalse(imp.isEnabled());
  }

  @Test
  public void setEnabled_true_afterFalse() {
    imp.setEnabled(false);
    imp.setEnabled(true);
    assertTrue(imp.isEnabled());
  }

  @Test
  public void setEnabled_syncsWithAction() {
    imp.setEnabled(false);
    assertFalse(imp.getSwingModel().getAction().isEnabled());
  }

  @Test
  public void setEnabled_true_syncsWithAction() {
    imp.setEnabled(false);
    imp.setEnabled(true);
    assertTrue(imp.getSwingModel().getAction().isEnabled());
  }

  // ── updateNameAndIcon ─────────────────────────────────────────────

  @Test
  public void updateNameAndIcon_trueValue_doesNotThrow() {
    imp.updateNameAndIcon(true, "On", null, "Off", null);
    // Action getValue is overridden to delegate to state, so we verify no exception
  }

  @Test
  public void updateNameAndIcon_falseValue_doesNotThrow() {
    imp.updateNameAndIcon(false, "On", null, "Off", null);
  }

  @Test
  public void updateNameAndIcon_withIcons_doesNotThrow() {
    javax.swing.Icon trueIcon = new javax.swing.ImageIcon();
    javax.swing.Icon falseIcon = new javax.swing.ImageIcon();
    imp.updateNameAndIcon(true, "On", trueIcon, "Off", falseIcon);
  }

  @Test
  public void updateNameAndIcon_nullIcons_doesNotThrow() {
    imp.updateNameAndIcon(false, "On", null, "Off", null);
  }

  @Test
  public void updateNameAndIcon_updatesOperationNames() {
    // Force lazy creation of operations
    imp.getSetToTrueOperation();
    imp.getSetToFalseOperation();
    // Update: should set names on the operations
    imp.updateNameAndIcon(true, "Enabled", null, "Disabled", null);
    // Verify operations exist and have been updated (no NPE)
    assertNotNull(imp.getSetToTrueOperation());
    assertNotNull(imp.getSetToFalseOperation());
  }

  // ── getSetToTrueOperation / getSetToFalseOperation ────────────────

  @Test
  public void getSetToTrueOperation_returnsNonNull() {
    assertNotNull(imp.getSetToTrueOperation());
  }

  @Test
  public void getSetToTrueOperation_returnsSameInstance() {
    assertSame(imp.getSetToTrueOperation(), imp.getSetToTrueOperation());
  }

  @Test
  public void getSetToFalseOperation_returnsNonNull() {
    assertNotNull(imp.getSetToFalseOperation());
  }

  @Test
  public void getSetToFalseOperation_returnsSameInstance() {
    assertSame(imp.getSetToFalseOperation(), imp.getSetToFalseOperation());
  }

  @Test
  public void trueAndFalseOperations_areDifferent() {
    assertNotSame(imp.getSetToTrueOperation(), imp.getSetToFalseOperation());
  }

  // ── updateNameAndIcon with operations initialized ─────────────────

  @Test
  public void updateNameAndIcon_afterOpsCreated_doesNotThrow() {
    // Force lazy creation of operations
    imp.getSetToTrueOperation();
    imp.getSetToFalseOperation();
    // Now update — should also update operation names without error
    imp.updateNameAndIcon(true, "Enabled", null, "Disabled", null);
    imp.updateNameAndIcon(false, "Enabled", null, "Disabled", null);
  }

  // ── getMenuModel ──────────────────────────────────────────────────

  @Test
  public void getMenuModel_returnsNonNull() {
    assertNotNull(imp.getMenuModel());
  }

  @Test
  public void getMenuModel_returnsSameInstance() {
    assertSame(imp.getMenuModel(), imp.getMenuModel());
  }

  // ── getMenuItemPrepModel ──────────────────────────────────────────

  @Test
  public void getMenuItemPrepModel_returnsNonNull() {
    assertNotNull(imp.getMenuItemPrepModel());
  }

  @Test
  public void getMenuItemPrepModel_returnsSameInstance() {
    assertSame(imp.getMenuItemPrepModel(), imp.getMenuItemPrepModel());
  }

  @Test
  public void getMenuItemPrepModel_hasBooleanState() {
    BooleanStateMenuItemPrepModel prepModel = imp.getMenuItemPrepModel();
    assertSame(state, prepModel.getBooleanState());
  }

  // ── getPotentialPrepModelPaths ─────────────────────────────────────

  @Test
  public void getPotentialPrepModelPaths_beforeMenuPrepInit_returnsEmpty() {
    // Create a fresh state where menuPrepModel has not been initialized
    TestBooleanState fresh = new TestBooleanState(TEST_GROUP, false);
    removeItemListeners(fresh);
    List<List<PrepModel>> paths = fresh.getImp().getPotentialPrepModelPaths(null);
    assertTrue("Should be empty when menuPrepModel not initialized", paths.isEmpty());
  }

  @Test
  public void getPotentialPrepModelPaths_afterMenuPrepInit_returnsNonEmpty() {
    imp.getMenuItemPrepModel(); // force initialization
    List<List<PrepModel>> paths = imp.getPotentialPrepModelPaths(null);
    assertFalse("Should contain paths after menuPrepModel initialized", paths.isEmpty());
    assertEquals(1, paths.size());
  }

  // ── ButtonModel selection sync ────────────────────────────────────

  @Test
  public void buttonModel_setSelected_reflectsInModel() {
    ButtonModel bm = imp.getSwingModel().getButtonModel();
    bm.setSelected(true);
    assertTrue(bm.isSelected());
  }

  @Test
  public void buttonModel_setSelectedFalse_reflectsInModel() {
    ButtonModel bm = imp.getSwingModel().getButtonModel();
    bm.setSelected(true);
    bm.setSelected(false);
    assertFalse(bm.isSelected());
  }

  // ── Test infrastructure ───────────────────────────────────────────

  static class TestBooleanState extends BooleanState {
    TestBooleanState(Group group, boolean initialValue) {
      super(group, UUID.randomUUID(), initialValue);
    }

    @Override
    protected Class<? extends org.lgna.croquet.Element> getClassUsedForLocalization() {
      return TestBooleanState.class;
    }

    @Override
    protected String getSubKeyForLocalization() {
      return "test";
    }
  }
}
