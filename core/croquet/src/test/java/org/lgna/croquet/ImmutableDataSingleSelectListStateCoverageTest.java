package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.data.ImmutableListData;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link ImmutableDataSingleSelectListState} —
 * immutability contract, item access, selection, and codec delegation.
 */
public class ImmutableDataSingleSelectListStateCoverageTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("c0000000-0000-0000-0011-ffffffffffff"), "immCov");

  private TestImmutableState state;

  @Before
  public void setUp() {
    state = new TestImmutableState(TEST_GROUP, 0, "alpha", "bravo", "charlie");
    CroquetTestUtils.removeListSelectionListeners(state);
  }

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void constructor_setsInitialValue() {
    assertEquals("alpha", state.getValue());
  }

  @Test
  public void constructor_middleIndex() {
    TestImmutableState s = new TestImmutableState(TEST_GROUP, 1, "a", "b", "c");
    CroquetTestUtils.removeListSelectionListeners(s);
    assertEquals("b", s.getValue());
  }

  @Test
  public void constructor_lastIndex() {
    TestImmutableState s = new TestImmutableState(TEST_GROUP, 2, "x", "y", "z");
    CroquetTestUtils.removeListSelectionListeners(s);
    assertEquals("z", s.getValue());
  }

  @Test
  public void constructor_noSelection() {
    TestImmutableState s = new TestImmutableState(TEST_GROUP, -1, "a", "b");
    CroquetTestUtils.removeListSelectionListeners(s);
    assertNull(s.getValue());
  }

  // ── getData ───────────────────────────────────────────────────────

  @Test
  public void getData_returnsImmutableListData() {
    assertTrue(state.getData() instanceof ImmutableListData);
  }

  @Test
  public void getData_itemCountMatches() {
    assertEquals(3, state.getData().getItemCount());
  }

  // ── item access ───────────────────────────────────────────────────

  @Test
  public void getItemAt_allPositions() {
    assertEquals("alpha", state.getItemAt(0));
    assertEquals("bravo", state.getItemAt(1));
    assertEquals("charlie", state.getItemAt(2));
  }

  @Test
  public void getItemCount() {
    assertEquals(3, state.getItemCount());
  }

  @Test
  public void indexOf() {
    assertEquals(1, state.indexOf("bravo"));
  }

  @Test
  public void indexOf_missing() {
    assertEquals(-1, state.indexOf("delta"));
  }

  @Test
  public void containsItem_present() {
    assertTrue(state.containsItem("charlie"));
  }

  @Test
  public void containsItem_absent() {
    assertFalse(state.containsItem("delta"));
  }

  // ── selection ─────────────────────────────────────────────────────

  @Test
  public void setSelectedIndex_updatesValue() {
    state.setSelectedIndex(2);
    assertEquals("charlie", state.getValue());
  }

  @Test
  public void setValueTransactionlessly_updatesIndex() {
    state.setValueTransactionlessly("bravo");
    assertEquals(1, state.getSelectedIndex());
  }

  @Test
  public void clearSelection() {
    state.clearSelection();
    assertNull(state.getValue());
    assertEquals(-1, state.getSelectedIndex());
  }

  // ── listener ──────────────────────────────────────────────────────

  @Test
  public void listener_firesOnSelectionChange() {
    AtomicReference<String> captured = new AtomicReference<>();
    state.addNewSchoolValueListener(e -> captured.set(e.getNextValue()));
    state.setValueTransactionlessly("charlie");
    assertEquals("charlie", captured.get());
  }

  // ── iteration ─────────────────────────────────────────────────────

  @Test
  public void iterator_coversAllItems() {
    int count = 0;
    for (String s : state) {
      count++;
    }
    assertEquals(3, count);
  }

  // ── toArray ───────────────────────────────────────────────────────

  @Test
  public void toArray() {
    assertArrayEquals(new String[]{"alpha", "bravo", "charlie"}, state.toArray());
  }

  // ── appendRepresentation ──────────────────────────────────────────

  @Test
  public void appendRepresentation_delegatesToCodec() {
    StringBuilder sb = new StringBuilder();
    state.appendRepresentation(sb, "bravo");
    assertEquals("bravo", sb.toString());
  }

  // ── single-element list ───────────────────────────────────────────

  @Test
  public void singleElement_getValue() {
    TestImmutableState s = new TestImmutableState(TEST_GROUP, 0, "only");
    CroquetTestUtils.removeListSelectionListeners(s);
    assertEquals("only", s.getValue());
    assertEquals(1, s.getItemCount());
  }

  // ── getSwingModel ─────────────────────────────────────────────────

  @Test
  public void getSwingModel_nonNull() {
    assertNotNull(state.getSwingModel());
  }

  @Test
  public void comboBoxModel_sizeMatches() {
    assertEquals(3, state.getSwingModel().getComboBoxModel().getSize());
  }

  // ── Test infrastructure ───────────────────────────────────────────

  static class TestImmutableState extends ImmutableDataSingleSelectListState<String> {
    @SafeVarargs
    TestImmutableState(Group group, int selectionIndex, String... values) {
      super(group, CroquetTestUtils.nextTestUUID(), selectionIndex, CroquetTestUtils.STRING_CODEC, values);
    }
  }
}
