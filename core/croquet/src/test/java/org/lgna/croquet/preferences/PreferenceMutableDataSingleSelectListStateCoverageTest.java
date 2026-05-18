package org.lgna.croquet.preferences;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.*;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link PreferenceMutableDataSingleSelectListState} —
 * construction, data management, selection, and preference registration.
 */
public class PreferenceMutableDataSingleSelectListStateCoverageTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("c0000000-0000-0000-0031-ffffffffffff"), "prefListCov");

  private TestPreferenceMutableState state;

  @Before
  public void setUp() {
    state = new TestPreferenceMutableState(TEST_GROUP, 0, "alpha", "bravo", "charlie");
    CroquetTestUtils.removeListSelectionListeners(state);
  }

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void constructor_setsInitialValue() {
    assertEquals("alpha", state.getValue());
  }

  @Test
  public void constructor_itemCount() {
    assertEquals(3, state.getItemCount());
  }

  @Test
  public void constructor_selectedIndex() {
    assertEquals(0, state.getSelectedIndex());
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

  // ── add/remove items ──────────────────────────────────────────────

  @Test
  public void addItem_increases_count() {
    state.addItem("delta");
    assertEquals(4, state.getItemCount());
  }

  @Test
  public void removeItem_decreases_count() {
    state.removeItem("charlie");
    assertEquals(2, state.getItemCount());
  }

  // ── getData ───────────────────────────────────────────────────────

  @Test
  public void getData_nonNull() {
    assertNotNull(state.getData());
  }

  // ── iteration ─────────────────────────────────────────────────────

  @Test
  public void iterator_coversAll() {
    int count = 0;
    for (String s : state) {
      count++;
    }
    assertEquals(3, count);
  }

  // ── extends MutableDataSingleSelectListState ──────────────────────

  @Test
  public void hierarchy_extendsMutableDataSingleSelectListState() {
    assertTrue(MutableDataSingleSelectListState.class.isAssignableFrom(
        PreferenceMutableDataSingleSelectListState.class));
  }

  @Test
  public void hierarchy_extendsSingleSelectListState() {
    assertTrue(SingleSelectListState.class.isAssignableFrom(
        PreferenceMutableDataSingleSelectListState.class));
  }

  // ── Test infrastructure ───────────────────────────────────────────

  static class TestPreferenceMutableState extends PreferenceMutableDataSingleSelectListState<String> {
    @SafeVarargs
    TestPreferenceMutableState(Group group, int selectionIndex, String... values) {
      super(group, CroquetTestUtils.nextTestUUID(), selectionIndex,
          CroquetTestUtils.STRING_CODEC, values);
    }
  }
}
