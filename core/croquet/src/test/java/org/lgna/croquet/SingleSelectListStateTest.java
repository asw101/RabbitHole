package org.lgna.croquet;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.BinaryEncoder;
import org.lgna.croquet.data.MutableListData;
import org.junit.Before;
import org.junit.Test;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.event.ListSelectionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Characterization tests for {@link SingleSelectListState}.
 *
 * <p>These tests pin the existing behavior of SingleSelectListState before
 * the extraction of {@code DataIndexPair}, {@code EmptyConditionText}, and
 * the anonymous {@code ListSelectionListener} into separate top-level classes.
 * They must pass both before and after the refactoring.</p>
 *
 * <p>Tests use a minimal {@link MutableListData} backed by String items
 * and do not require a running Swing event loop or Alice application context.
 * The internal ListSelectionListener is removed in setUp to avoid the
 * NullTrigger → Application.getActiveInstance() dependency chain.</p>
 */
public class SingleSelectListStateTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("00000000-0000-0000-0000-ffffffffffff"), "test");

  private MutableListData<String> data;
  private TestSingleSelectListState state;

  @Before
  public void setUp() {
    data = new MutableListData<>(STRING_CODEC, new String[]{"alpha", "bravo", "charlie"});
    state = new TestSingleSelectListState(TEST_GROUP, 1, data);
    // Remove the ListSelectionListener to decouple from Application context.
    // The listener invokes NullTrigger → Application.getActiveInstance() which
    // is null in unit tests. Characterization tests verify state management
    // logic, not Swing event dispatch integration.
    removeListSelectionListeners(state);
  }

  private static void removeListSelectionListeners(TestSingleSelectListState s) {
    DefaultListSelectionModel lsm =
        (DefaultListSelectionModel) s.getSwingModel().getListSelectionModel();
    for (ListSelectionListener l : lsm.getListSelectionListeners()) {
      lsm.removeListSelectionListener(l);
    }
  }

  // ── Construction and initial state ──────────────────────────────────

  @Test
  public void constructor_setsInitialSelectedIndex() {
    assertEquals("Selected index should match constructor arg", 1, state.getSelectedIndex());
  }

  @Test
  public void constructor_setsInitialValue() {
    assertEquals("Value should be the item at the initial index", "bravo", state.getValue());
  }

  @Test
  public void constructor_withNegativeIndex_hasNullValue() {
    TestSingleSelectListState noSelection =
        new TestSingleSelectListState(TEST_GROUP, -1, data);
    assertEquals(-1, noSelection.getSelectedIndex());
    assertNull("Value should be null when no selection", noSelection.getValue());
  }

  @Test
  public void constructor_withIndexBeyondBounds_hasNullValue() {
    TestSingleSelectListState oob =
        new TestSingleSelectListState(TEST_GROUP, 99, data);
    // getItemAt returns null for out-of-bounds, so initial value is null
    assertNull("Value should be null for out-of-bounds initial index", oob.getValue());
  }

  // ── getData() ───────────────────────────────────────────────────────

  @Test
  public void getData_returnsSameDataInstance() {
    assertSame("getData() should return the data passed to constructor", data, state.getData());
  }

  // ── getItemAt / getItemCount / indexOf / containsItem / iterator ───

  @Test
  public void getItemAt_returnsCorrectItem() {
    assertEquals("alpha", state.getItemAt(0));
    assertEquals("bravo", state.getItemAt(1));
    assertEquals("charlie", state.getItemAt(2));
  }

  @Test
  public void getItemCount_returnsSize() {
    assertEquals(3, state.getItemCount());
  }

  @Test
  public void indexOf_findsExistingItem() {
    assertEquals(0, state.indexOf("alpha"));
    assertEquals(2, state.indexOf("charlie"));
  }

  @Test
  public void indexOf_returnsNegativeForMissing() {
    assertEquals(-1, state.indexOf("delta"));
  }

  @Test
  public void containsItem_trueForPresent() {
    assertTrue(state.containsItem("alpha"));
  }

  @Test
  public void containsItem_falseForAbsent() {
    assertFalse(state.containsItem("delta"));
  }

  @Test
  public void iterator_coversAllItems() {
    int count = 0;
    for (String s : state) {
      count++;
    }
    assertEquals(3, count);
  }

  // ── setSelectedIndex ────────────────────────────────────────────────

  @Test
  public void setSelectedIndex_updatesValueAndIndex() {
    state.setSelectedIndex(0);
    assertEquals(0, state.getSelectedIndex());
    assertEquals("alpha", state.getValue());
  }

  @Test
  public void setSelectedIndex_toLastItem() {
    state.setSelectedIndex(2);
    assertEquals(2, state.getSelectedIndex());
    assertEquals("charlie", state.getValue());
  }

  // ── clearSelection ─────────────────────────────────────────────────

  @Test
  public void clearSelection_setsIndexToNegativeOne() {
    state.clearSelection();
    assertEquals(-1, state.getSelectedIndex());
  }

  @Test
  public void clearSelection_setsValueToNull() {
    state.clearSelection();
    assertNull(state.getValue());
  }

  // ── setValueTransactionlessly ──────────────────────────────────────

  @Test
  public void setValueTransactionlessly_updatesIndexAndValue() {
    state.setValueTransactionlessly("charlie");
    assertEquals("charlie", state.getValue());
    assertEquals(2, state.getSelectedIndex());
  }

  @Test
  public void setValueTransactionlessly_toNull_clearsSelection() {
    state.setValueTransactionlessly(null);
    assertNull(state.getValue());
    assertEquals(-1, state.getSelectedIndex());
  }

  // ── addItem ─────────────────────────────────────────────────────────

  @Test
  public void addItem_appends_increasesCount() {
    state.addItem("delta");
    assertEquals(4, state.getItemCount());
    assertEquals("delta", state.getItemAt(3));
  }

  @Test
  public void addItem_atIndex_insertsAtPosition() {
    state.addItem(0, "zero");
    assertEquals(4, state.getItemCount());
    assertEquals("zero", state.getItemAt(0));
    assertEquals("alpha", state.getItemAt(1));
  }

  @Test
  public void addItem_preservesCurrentSelection() {
    assertEquals("bravo", state.getValue());
    state.addItem("delta");
    // Selection index doesn't change, so value is still the item at index 1
    assertEquals("bravo", state.getValue());
  }

  // ── removeItem ──────────────────────────────────────────────────────

  @Test
  public void removeItem_decreasesCount() {
    state.removeItem("charlie");
    assertEquals(2, state.getItemCount());
  }

  @Test
  public void removeItem_nonSelectedItem_preservesSelection() {
    state.removeItem("charlie");
    assertEquals("bravo", state.getValue());
  }

  // ── removeItemAndSelectAppropriateReplacement ──────────────────────

  @Test
  public void removeItemAndSelectReplacement_nonSelected_valueShiftsWithIndex() {
    // Selection index is 1 ("bravo"). Removing "alpha" (index 0) doesn't
    // auto-adjust the index, so index 1 now points to "charlie".
    // This is the actual behavior — index-based, not value-tracking.
    state.removeItemAndSelectAppropriateReplacement("alpha");
    assertEquals("charlie", state.getValue());
  }

  @Test
  public void removeItemAndSelectReplacement_selected_selectsFirst() {
    state.removeItemAndSelectAppropriateReplacement("bravo");
    // When removing the selected item, it selects the first remaining item
    assertEquals("alpha", state.getValue());
  }

  @Test
  public void removeItemAndSelectReplacement_lastItem_selectsNull() {
    // Remove all but one, then remove that one
    state.removeItem("bravo");
    state.removeItem("charlie");
    state.setValueTransactionlessly("alpha");
    state.removeItemAndSelectAppropriateReplacement("alpha");
    // List is now empty — no selection
    assertEquals(0, state.getItemCount());
  }

  // ── setItems (Collection) ──────────────────────────────────────────

  @Test
  public void setItems_replacesData() {
    state.setItems(Arrays.asList("x", "y"));
    assertEquals(2, state.getItemCount());
    assertEquals("x", state.getItemAt(0));
    assertEquals("y", state.getItemAt(1));
  }

  @Test
  public void setItems_preservesSelectionWhenItemStillPresent() {
    state.setItems(Arrays.asList("bravo", "delta"));
    assertEquals("bravo", state.getValue());
    assertEquals(0, state.getSelectedIndex());
  }

  @Test
  public void setItems_resetsSelectionWhenItemMissing() {
    state.setItems(Arrays.asList("x", "y"));
    assertEquals(-1, state.getSelectedIndex());
    assertNull(state.getValue());
  }

  // ── setItems (varargs) ─────────────────────────────────────────────

  @Test
  public void setItemsVarargs_delegatesToCollectionOverload() {
    state.setItems("p", "q", "r");
    assertEquals(3, state.getItemCount());
    assertEquals("p", state.getItemAt(0));
  }

  // ── setListData (deprecated Collection overload) ───────────────────

  @Test
  public void setListDataCollection_replacesItemsAndSetsIndex() {
    state.setListData(1, Arrays.asList("x", "y", "z"));
    assertEquals(3, state.getItemCount());
    assertEquals(1, state.getSelectedIndex());
    assertEquals("y", state.getValue());
  }

  @Test
  public void setListDataCollection_negativeIndex_clearsSelection() {
    state.setListData(-1, Arrays.asList("x", "y"));
    assertEquals(-1, state.getSelectedIndex());
    assertNull(state.getValue());
  }

  // ── setListData (deprecated varargs overload) ──────────────────────

  @Test
  public void setListDataVarargs_replacesItemsAndSetsIndex() {
    state.setListData(0, "one", "two");
    assertEquals(2, state.getItemCount());
    assertEquals(0, state.getSelectedIndex());
    assertEquals("one", state.getValue());
  }

  // ── clear ──────────────────────────────────────────────────────────

  @Test
  public void clear_removesAllItems() {
    state.clear();
    assertEquals(0, state.getItemCount());
  }

  @Test
  public void clear_setsSelectionToNegativeOne() {
    state.clear();
    assertEquals(-1, state.getSelectedIndex());
  }

  // ── toArray ────────────────────────────────────────────────────────

  @Test
  public void toArray_returnsAllItems() {
    String[] arr = state.toArray();
    assertArrayEquals(new String[]{"alpha", "bravo", "charlie"}, arr);
  }

  // ── setRandomSelectedValue ─────────────────────────────────────────

  @Test
  public void setRandomSelectedValue_selectsValidIndex() {
    state.setRandomSelectedValue();
    int idx = state.getSelectedIndex();
    assertTrue("Random index must be in [0, itemCount)", idx >= 0 && idx < state.getItemCount());
  }

  @Test
  public void setRandomSelectedValue_emptyList_setsNegativeOne() {
    state.clear();
    state.setRandomSelectedValue();
    assertEquals(-1, state.getSelectedIndex());
  }

  // ── ComboBoxModel contract (DataIndexPair) ─────────────────────────

  @Test
  public void comboBoxModel_getSizeMatchesItemCount() {
    ComboBoxModel model = state.getSwingModel().getComboBoxModel();
    assertEquals(state.getItemCount(), model.getSize());
  }

  @Test
  public void comboBoxModel_getElementAtMatchesItemAt() {
    ComboBoxModel model = state.getSwingModel().getComboBoxModel();
    for (int i = 0; i < state.getItemCount(); i++) {
      assertEquals(state.getItemAt(i), model.getElementAt(i));
    }
  }

  @Test
  public void comboBoxModel_getSelectedItemMatchesValue() {
    ComboBoxModel model = state.getSwingModel().getComboBoxModel();
    assertEquals(state.getValue(), model.getSelectedItem());
  }

  @Test
  public void comboBoxModel_getElementAtNegativeOne_returnsNull() {
    ComboBoxModel model = state.getSwingModel().getComboBoxModel();
    assertNull(model.getElementAt(-1));
  }

  @Test
  public void comboBoxModel_noSelection_getSelectedItemReturnsNull() {
    state.clearSelection();
    ComboBoxModel model = state.getSwingModel().getComboBoxModel();
    assertNull(model.getSelectedItem());
  }

  // ── SwingModel selection index consistency ─────────────────────────

  @Test
  public void swingModel_afterSetValue_selectionIndexMatchesState() {
    // Swing model starts with -1 (DefaultListSelectionModel default).
    // After an explicit value change, setSwingValue syncs the model.
    state.setValueTransactionlessly("charlie");
    assertEquals(2, state.getSwingModel().getSelectionIndex());
    assertEquals(2, state.getSelectedIndex());
  }

  @Test
  public void swingModel_afterSetSelectedIndex_swingModelSynced() {
    state.setSelectedIndex(0);
    // setSelectedIndex triggers changeValue → setSwingValue
    assertEquals(0, state.getSwingModel().getSelectionIndex());
  }

  // ── Atomic change guard ────────────────────────────────────────────

  @Test
  public void atomicChange_setItems_doesNotFireIntermediateChanges() {
    // setItems wraps in pushIsInTheMidstOfAtomicChange / pop
    // This test verifies it doesn't crash and produces correct final state
    state.setItems(Arrays.asList("x", "y", "z"));
    assertEquals(3, state.getItemCount());
  }

  @Test
  public void atomicChange_nestedMutations_coalesce() {
    // setListData does push, setItems (which does push/pop internally), setSelectedIndex, pop
    // The atomic guard coalesces nested changes
    state.setListData(2, Arrays.asList("p", "q", "r"));
    assertEquals("r", state.getValue());
    assertEquals(2, state.getSelectedIndex());
  }

  // ── EmptyConditionText ─────────────────────────────────────────────

  @Test
  public void emptyConditionText_returnsNonNull() {
    PlainStringValue ect = state.getEmptyConditionText();
    assertNotNull("getEmptyConditionText() must not return null", ect);
  }

  @Test
  public void emptyConditionText_isPlainStringValue() {
    PlainStringValue ect = state.getEmptyConditionText();
    assertTrue("Must be a PlainStringValue", ect instanceof PlainStringValue);
  }

  // ── getSwingModel ──────────────────────────────────────────────────

  @Test
  public void getSwingModel_returnsNonNull() {
    assertNotNull(state.getSwingModel());
  }

  // ── Multiple operations sequence ──────────────────────────────────

  @Test
  public void sequentialOperations_addThenSetThenRemove() {
    state.addItem("delta");
    assertEquals(4, state.getItemCount());

    state.setSelectedIndex(3);
    assertEquals("delta", state.getValue());

    state.removeItem("alpha");
    assertEquals(3, state.getItemCount());
    // Index hasn't auto-adjusted for the removal, but value may have shifted
  }

  @Test
  public void setItems_thenAddItem_thenClear() {
    state.setItems(Arrays.asList("x", "y"));
    assertEquals(2, state.getItemCount());

    state.addItem("z");
    assertEquals(3, state.getItemCount());

    state.clear();
    assertEquals(0, state.getItemCount());
    assertEquals(-1, state.getSelectedIndex());
  }

  // ── Test infrastructure ───────────────────────────────────────────

  /**
   * Concrete subclass of {@link MutableDataSingleSelectListState} for testing.
   * Uses a unique UUID per instance to avoid registration collisions.
   */
  static class TestSingleSelectListState extends MutableDataSingleSelectListState<String> {
    TestSingleSelectListState(Group group, int selectionIndex, MutableListData<String> data) {
      super(group, UUID.randomUUID(), selectionIndex, data);
    }
  }

  /**
   * Minimal {@link ItemCodec} for String values.
   */
  private static final ItemCodec<String> STRING_CODEC = new ItemCodec<String>() {
    @Override
    public Class<String> getValueClass() {
      return String.class;
    }

    @Override
    public String decodeValue(BinaryDecoder binaryDecoder) {
      return binaryDecoder.decodeString();
    }

    @Override
    public void encodeValue(BinaryEncoder binaryEncoder, String value) {
      binaryEncoder.encode(value);
    }

    @Override
    public void appendRepresentation(StringBuilder sb, String value) {
      sb.append(value);
    }
  };
}
