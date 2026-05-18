package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.data.MutableListData;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link MutableDataSingleSelectListState} — add/remove items,
 * selection tracking, listeners, codec delegation, and constructor variants.
 */
public class MutableDataSingleSelectListStateCoverageTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("c0000000-0000-0000-0012-ffffffffffff"), "mutCov");

  private TestMutableState state;

  @Before
  public void setUp() {
    state = new TestMutableState(TEST_GROUP, 0, "alpha", "bravo", "charlie");
    CroquetTestUtils.removeListSelectionListeners(state);
  }

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void constructor_withItems_setsInitialValue() {
    assertEquals("alpha", state.getValue());
    assertEquals(3, state.getItemCount());
  }

  @Test
  public void constructor_emptyCodecOnly() {
    TestMutableStateEmpty s = new TestMutableStateEmpty(TEST_GROUP);
    CroquetTestUtils.removeListSelectionListeners(s);
    assertEquals(0, s.getItemCount());
    assertNull(s.getValue());
  }

  // ── add/remove items ──────────────────────────────────────────────

  @Test
  public void addItem_appends() {
    state.addItem("delta");
    assertEquals(4, state.getItemCount());
    assertEquals("delta", state.getItemAt(3));
  }

  @Test
  public void addItem_atIndex() {
    state.addItem(0, "zero");
    assertEquals(4, state.getItemCount());
    assertEquals("zero", state.getItemAt(0));
  }

  @Test
  public void removeItem_decreasesCount() {
    state.removeItem("bravo");
    assertEquals(2, state.getItemCount());
    assertFalse(state.containsItem("bravo"));
  }

  @Test
  public void removeItem_preservesSelection() {
    state.removeItem("charlie");
    assertEquals("alpha", state.getValue());
  }

  // ── selection tracking after mutations ────────────────────────────

  @Test
  public void setItems_preservesSelectionWhenPresent() {
    state.setItems(Arrays.asList("alpha", "bravo", "delta"));
    assertEquals("alpha", state.getValue());
  }

  @Test
  public void setItems_clearsSelectionWhenAbsent() {
    state.setItems(Arrays.asList("x", "y"));
    assertNull(state.getValue());
    assertEquals(-1, state.getSelectedIndex());
  }

  @Test
  public void clear_removesAll() {
    state.clear();
    assertEquals(0, state.getItemCount());
    assertEquals(-1, state.getSelectedIndex());
  }

  @Test
  public void removeItemAndSelectAppropriateReplacement_selectedItem() {
    state.removeItemAndSelectAppropriateReplacement("alpha");
    // After removing selected item, first remaining is selected
    assertNotNull(state.getValue());
  }

  // ── listener dispatch ─────────────────────────────────────────────

  @Test
  public void listener_firesOnValueChange() {
    AtomicReference<String> captured = new AtomicReference<>();
    state.addNewSchoolValueListener(e -> captured.set(e.getNextValue()));
    state.setValueTransactionlessly("charlie");
    assertEquals("charlie", captured.get());
  }

  @Test
  public void listener_firesAfterAddAndSelect() {
    AtomicReference<String> captured = new AtomicReference<>();
    state.addNewSchoolValueListener(e -> captured.set(e.getNextValue()));
    state.addItem("delta");
    state.setValueTransactionlessly("delta");
    assertEquals("delta", captured.get());
  }

  @Test
  public void listener_sameValue_doesNotFire() {
    AtomicInteger count = new AtomicInteger();
    state.addValueListener(new State.ValueListener<String>() {
      @Override
      public void changing(State<String> s, String prev, String next) {}

      @Override
      public void changed(State<String> s, String prev, String next) {
        count.incrementAndGet();
      }
    });
    state.setValueTransactionlessly("alpha"); // same as current
    assertEquals(0, count.get());
  }

  // ── codec delegation ──────────────────────────────────────────────

  @Test
  public void appendRepresentation_delegatesToCodec() {
    StringBuilder sb = new StringBuilder();
    state.appendRepresentation(sb, "bravo");
    assertEquals("bravo", sb.toString());
  }

  // ── iteration ─────────────────────────────────────────────────────

  @Test
  public void iterator_afterMutations() {
    state.addItem("delta");
    state.removeItem("bravo");
    int count = 0;
    for (String s : state) {
      count++;
    }
    assertEquals(3, count);
  }

  // ── setListData ───────────────────────────────────────────────────

  @Test
  public void setListData_replacesAndSetsIndex() {
    state.setListData(1, Arrays.asList("x", "y", "z"));
    assertEquals("y", state.getValue());
    assertEquals(3, state.getItemCount());
  }

  // ── setRandomSelectedValue ────────────────────────────────────────

  @Test
  public void setRandomSelectedValue_selectsValid() {
    state.setRandomSelectedValue();
    assertTrue(state.getSelectedIndex() >= 0);
    assertTrue(state.getSelectedIndex() < state.getItemCount());
  }

  @Test
  public void setRandomSelectedValue_emptyList() {
    state.clear();
    state.setRandomSelectedValue();
    assertEquals(-1, state.getSelectedIndex());
  }

  // ── getData ───────────────────────────────────────────────────────

  @Test
  public void getData_returnsMutableListData() {
    assertTrue(state.getData() instanceof MutableListData);
  }

  // ── getSwingModel ─────────────────────────────────────────────────

  @Test
  public void getSwingModel_nonNull() {
    assertNotNull(state.getSwingModel());
  }

  @Test
  public void comboBoxModel_sizeMatchesAfterAdd() {
    state.addItem("delta");
    assertEquals(4, state.getSwingModel().getComboBoxModel().getSize());
  }

  // ── Test infrastructure ───────────────────────────────────────────

  static class TestMutableState extends MutableDataSingleSelectListState<String> {
    @SafeVarargs
    TestMutableState(Group group, int selectionIndex, String... values) {
      super(group, CroquetTestUtils.nextTestUUID(), selectionIndex,
          CroquetTestUtils.STRING_CODEC, values);
    }
  }

  static class TestMutableStateEmpty extends MutableDataSingleSelectListState<String> {
    TestMutableStateEmpty(Group group) {
      super(group, CroquetTestUtils.nextTestUUID(), CroquetTestUtils.STRING_CODEC);
    }
  }
}
