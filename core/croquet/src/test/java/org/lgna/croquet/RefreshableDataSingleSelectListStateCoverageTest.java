package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.data.RefreshableListData;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link RefreshableDataSingleSelectListState} —
 * data refresh, listener forwarding from RefreshableListData, selection tracking.
 */
public class RefreshableDataSingleSelectListStateCoverageTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("c0000000-0000-0000-0013-ffffffffffff"), "refCov");

  private TestRefreshableListData data;
  private TestRefreshableState state;

  @Before
  public void setUp() {
    data = new TestRefreshableListData(Arrays.asList("alpha", "bravo", "charlie"));
    state = new TestRefreshableState(TEST_GROUP, 0, data);
    CroquetTestUtils.removeListSelectionListeners(state);
  }

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void constructor_setsInitialValue() {
    assertEquals("alpha", state.getValue());
  }

  @Test
  public void constructor_itemCountMatches() {
    assertEquals(3, state.getItemCount());
  }

  // ── getData ───────────────────────────────────────────────────────

  @Test
  public void getData_returnsRefreshableListData() {
    assertTrue(state.getData() instanceof RefreshableListData);
  }

  @Test
  public void getData_isSameInstance() {
    assertSame(data, state.getData());
  }

  // ── item access ───────────────────────────────────────────────────

  @Test
  public void getItemAt_returnsCorrectItems() {
    assertEquals("alpha", state.getItemAt(0));
    assertEquals("bravo", state.getItemAt(1));
    assertEquals("charlie", state.getItemAt(2));
  }

  @Test
  public void indexOf_findsItems() {
    assertEquals(1, state.indexOf("bravo"));
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
  public void clearSelection_clearsValue() {
    state.clearSelection();
    assertNull(state.getValue());
  }

  // ── listener dispatch ─────────────────────────────────────────────

  @Test
  public void listener_firesOnSelectionChange() {
    java.util.concurrent.atomic.AtomicReference<String> captured =
        new java.util.concurrent.atomic.AtomicReference<>();
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
  public void toArray_matchesData() {
    assertArrayEquals(new String[]{"alpha", "bravo", "charlie"}, state.toArray());
  }

  // ── appendRepresentation ──────────────────────────────────────────

  @Test
  public void appendRepresentation_delegatesToCodec() {
    StringBuilder sb = new StringBuilder();
    state.appendRepresentation(sb, "bravo");
    assertEquals("bravo", sb.toString());
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

  static class TestRefreshableListData extends RefreshableListData<String> {
    private List<String> items;

    TestRefreshableListData(List<String> items) {
      super(CroquetTestUtils.STRING_CODEC);
      this.items = new java.util.ArrayList<>(items);
    }

    @Override
    protected List<String> createValues() {
      return new java.util.ArrayList<>(items);
    }

    @Override
    public boolean contains(String item) {
      return items.contains(item);
    }

    @Override
    public String[] toArray() {
      return items.toArray(new String[0]);
    }
  }

  static class TestRefreshableState extends RefreshableDataSingleSelectListState<String> {
    TestRefreshableState(Group group, int selectionIndex, TestRefreshableListData data) {
      super(group, CroquetTestUtils.nextTestUUID(), selectionIndex, data);
    }
  }
}
