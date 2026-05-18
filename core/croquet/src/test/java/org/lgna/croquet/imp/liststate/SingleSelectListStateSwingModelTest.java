package org.lgna.croquet.imp.liststate;

import org.junit.Before;
import org.junit.Test;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Tests for {@link SingleSelectListStateSwingModel} — the Swing-level model
 * that pairs a ComboBoxModel with a single-selection ListSelectionModel.
 *
 * <p>No Application instance is required; only raw Swing models are used.</p>
 */
public class SingleSelectListStateSwingModelTest {

  private DefaultComboBoxModel<String> comboBoxModel;
  private SingleSelectListStateSwingModel swingModel;

  @Before
  public void setUp() {
    comboBoxModel = new DefaultComboBoxModel<>(new String[]{"alpha", "bravo", "charlie"});
    swingModel = new SingleSelectListStateSwingModel(comboBoxModel);
  }

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void constructor_createsNonNull() {
    assertNotNull(swingModel);
  }

  @Test
  public void constructor_setsListSelectionToSingleMode() {
    ListSelectionModel lsm = swingModel.getListSelectionModel();
    assertEquals(ListSelectionModel.SINGLE_SELECTION, lsm.getSelectionMode());
  }

  // ── getComboBoxModel ──────────────────────────────────────────────

  @Test
  public void getComboBoxModel_returnsSameInstance() {
    assertSame(comboBoxModel, swingModel.getComboBoxModel());
  }

  @Test
  public void getComboBoxModel_reflectsData() {
    ComboBoxModel model = swingModel.getComboBoxModel();
    assertEquals(3, model.getSize());
    assertEquals("alpha", model.getElementAt(0));
  }

  // ── getListSelectionModel ─────────────────────────────────────────

  @Test
  public void getListSelectionModel_returnsNonNull() {
    assertNotNull(swingModel.getListSelectionModel());
  }

  @Test
  public void getListSelectionModel_isSingleSelection() {
    ListSelectionModel lsm = swingModel.getListSelectionModel();
    assertEquals(ListSelectionModel.SINGLE_SELECTION, lsm.getSelectionMode());
  }

  // ── getSelectionIndex ─────────────────────────────────────────────

  @Test
  public void getSelectionIndex_initiallyNegativeOne() {
    assertEquals(-1, swingModel.getSelectionIndex());
  }

  @Test
  public void getSelectionIndex_afterSetSelectionIndex() {
    swingModel.setSelectionIndex(1);
    assertEquals(1, swingModel.getSelectionIndex());
  }

  @Test
  public void getSelectionIndex_afterClearSelection() {
    swingModel.setSelectionIndex(2);
    swingModel.setSelectionIndex(-1);
    assertEquals(-1, swingModel.getSelectionIndex());
  }

  // ── setSelectionIndex ─────────────────────────────────────────────

  @Test
  public void setSelectionIndex_zero_selectsFirst() {
    swingModel.setSelectionIndex(0);
    assertEquals(0, swingModel.getSelectionIndex());
  }

  @Test
  public void setSelectionIndex_last_selectsLast() {
    swingModel.setSelectionIndex(2);
    assertEquals(2, swingModel.getSelectionIndex());
  }

  @Test
  public void setSelectionIndex_negativeOne_clearsSelection() {
    swingModel.setSelectionIndex(1);
    swingModel.setSelectionIndex(-1);
    assertTrue(swingModel.getListSelectionModel().isSelectionEmpty());
  }

  @Test
  public void setSelectionIndex_updatesLeadIndex() {
    swingModel.setSelectionIndex(2);
    assertEquals(2, swingModel.getListSelectionModel().getLeadSelectionIndex());
  }

  @Test
  public void setSelectionIndex_setsInterval() {
    swingModel.setSelectionIndex(1);
    assertFalse(swingModel.getListSelectionModel().isSelectionEmpty());
    assertTrue(swingModel.getListSelectionModel().isSelectedIndex(1));
  }

  // ── selection cycling ─────────────────────────────────────────────

  @Test
  public void selectionCycle_throughAllIndices() {
    for (int i = 0; i < 3; i++) {
      swingModel.setSelectionIndex(i);
      assertEquals(i, swingModel.getSelectionIndex());
    }
  }

  @Test
  public void selectionCycle_clearAndReselect() {
    swingModel.setSelectionIndex(2);
    assertEquals(2, swingModel.getSelectionIndex());

    swingModel.setSelectionIndex(-1);
    assertEquals(-1, swingModel.getSelectionIndex());

    swingModel.setSelectionIndex(0);
    assertEquals(0, swingModel.getSelectionIndex());
  }

  // ── fireListSelectionChanged ──────────────────────────────────────

  @Test
  public void fireListSelectionChanged_notifiesListeners() {
    AtomicBoolean fired = new AtomicBoolean(false);
    swingModel.getListSelectionModel().addListSelectionListener(e -> fired.set(true));

    swingModel.fireListSelectionChanged(0, 2, false);
    assertTrue("Listener should have been notified", fired.get());
  }

  @Test
  public void fireListSelectionChanged_passesCorrectEventParams() {
    AtomicReference<ListSelectionEvent> captured = new AtomicReference<>();
    swingModel.getListSelectionModel().addListSelectionListener(captured::set);

    swingModel.fireListSelectionChanged(1, 3, true);
    ListSelectionEvent event = captured.get();
    assertNotNull(event);
    assertEquals(1, event.getFirstIndex());
    assertEquals(3, event.getLastIndex());
    assertTrue(event.getValueIsAdjusting());
  }

  @Test
  public void fireListSelectionChanged_notAdjusting() {
    AtomicReference<ListSelectionEvent> captured = new AtomicReference<>();
    swingModel.getListSelectionModel().addListSelectionListener(captured::set);

    swingModel.fireListSelectionChanged(0, 0, false);
    assertFalse(captured.get().getValueIsAdjusting());
  }

  @Test
  public void fireListSelectionChanged_sourceIsSwingModel() {
    AtomicReference<ListSelectionEvent> captured = new AtomicReference<>();
    swingModel.getListSelectionModel().addListSelectionListener(captured::set);

    swingModel.fireListSelectionChanged(0, 1, false);
    assertSame(swingModel, captured.get().getSource());
  }

  @Test
  public void fireListSelectionChanged_multipleListeners() {
    AtomicBoolean fired1 = new AtomicBoolean(false);
    AtomicBoolean fired2 = new AtomicBoolean(false);
    swingModel.getListSelectionModel().addListSelectionListener(e -> fired1.set(true));
    swingModel.getListSelectionModel().addListSelectionListener(e -> fired2.set(true));

    swingModel.fireListSelectionChanged(0, 0, false);
    assertTrue(fired1.get());
    assertTrue(fired2.get());
  }

  @Test
  public void fireListSelectionChanged_noListeners_doesNotThrow() {
    // Remove any default listeners first
    DefaultListSelectionModel lsm = (DefaultListSelectionModel) swingModel.getListSelectionModel();
    for (ListSelectionListener l : lsm.getListSelectionListeners()) {
      lsm.removeListSelectionListener(l);
    }
    swingModel.fireListSelectionChanged(0, 0, false);
    // No exception = pass
  }

  // ── empty ComboBoxModel ───────────────────────────────────────────

  @Test
  public void emptyComboBoxModel_selectionNegativeOne() {
    DefaultComboBoxModel<String> empty = new DefaultComboBoxModel<>();
    SingleSelectListStateSwingModel emptyModel = new SingleSelectListStateSwingModel(empty);
    assertEquals(-1, emptyModel.getSelectionIndex());
  }

  // ── ComboBoxModel with selection ──────────────────────────────────

  @Test
  public void comboBoxModel_selectedItem_independent() {
    comboBoxModel.setSelectedItem("bravo");
    // ComboBoxModel selected item is independent of ListSelectionModel
    assertEquals("bravo", comboBoxModel.getSelectedItem());
    // But swingModel selection is based on ListSelectionModel, not ComboBoxModel
    assertEquals(-1, swingModel.getSelectionIndex());
  }
}
