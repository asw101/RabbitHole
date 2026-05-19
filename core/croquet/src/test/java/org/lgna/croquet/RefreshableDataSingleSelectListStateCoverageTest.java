package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.data.RefreshableListData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link RefreshableDataSingleSelectListState} — abstract state
 * backed by refreshable list data. Tests construction, data refresh, listener forwarding,
 * and selection behavior.
 */
public class RefreshableDataSingleSelectListStateCoverageTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("c0000000-0000-0000-0004-000000000001"), "refCov");

  private List<String> sourceItems;
  private int createValuesCallCount;

  private class TestRefreshableData extends RefreshableListData<String> {
    TestRefreshableData() {
      super(CroquetTestUtils.STRING_CODEC);
    }

    @Override
    protected List<String> createValues() {
      createValuesCallCount++;
      return Collections.unmodifiableList(new ArrayList<>(sourceItems));
    }
  }

  private static class TestRefreshableState extends RefreshableDataSingleSelectListState<String> {
    TestRefreshableState(Group group, int selectionIndex, RefreshableListData<String> data) {
      super(group, CroquetTestUtils.nextTestUUID(), selectionIndex, data);
    }

    @Override
    protected Class<? extends Element> getClassUsedForLocalization() {
      return TestRefreshableState.class;
    }

    @Override
    protected String getSubKeyForLocalization() {
      return "test";
    }
  }

  private TestRefreshableData data;
  private TestRefreshableState state;

  @Before
  public void setUp() {
    sourceItems = new ArrayList<>(List.of("one", "two", "three"));
    createValuesCallCount = 0;
    data = new TestRefreshableData();
    state = new TestRefreshableState(TEST_GROUP, 0, data);
    CroquetTestUtils.removeListSelectionListeners(state);
  }

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void constructor_selectsFirstItem() {
    assertEquals("one", state.getValue());
  }

  @Test
  public void constructor_negativeIndex() {
    TestRefreshableState s = new TestRefreshableState(TEST_GROUP, -1, new TestRefreshableData());
    CroquetTestUtils.removeListSelectionListeners(s);
    assertNull(s.getValue());
  }

  // ── Item access ───────────────────────────────────────────────────

  @Test
  public void getItemCount_matchesSource() {
    assertEquals(3, state.getItemCount());
  }

  @Test
  public void getItemAt_returnsItems() {
    assertEquals("one", state.getItemAt(0));
    assertEquals("two", state.getItemAt(1));
    assertEquals("three", state.getItemAt(2));
  }

  // ── Selection ─────────────────────────────────────────────────────

  @Test
  public void setValueTransactionlessly_changesSelection() {
    state.setValueTransactionlessly("two");
    assertEquals("two", state.getValue());
  }

  @Test
  public void setValueTransactionlessly_toNull() {
    state.setValueTransactionlessly(null);
    assertNull(state.getValue());
  }

  // ── Refresh ───────────────────────────────────────────────────────

  @Test
  public void refresh_updatesData() {
    sourceItems.add("four");
    data.refresh();
    assertEquals(4, state.getItemCount());
    assertEquals("four", state.getItemAt(3));
  }

  @Test
  public void refresh_withRemovedItem() {
    sourceItems.remove("two");
    data.refresh();
    assertEquals(2, state.getItemCount());
    assertEquals(-1, state.indexOf("two"));
  }

  @Test
  public void refresh_callsCreateValues() {
    int before = createValuesCallCount;
    data.refresh();
    assertTrue(createValuesCallCount > before);
  }

  // ── Codec ─────────────────────────────────────────────────────────

  @Test
  public void getItemCodec_valueClass() {
    assertEquals(String.class, state.getItemCodec().getValueClass());
  }

  @Test
  public void appendRepresentation_nonNull() {
    StringBuilder sb = new StringBuilder();
    state.appendRepresentation(sb, "two");
    assertEquals("two", sb.toString());
  }

  // ── Hierarchy ─────────────────────────────────────────────────────

  @Test
  public void class_extendsSingleSelectListState() {
    assertTrue(SingleSelectListState.class.isAssignableFrom(RefreshableDataSingleSelectListState.class));
  }

  @Test
  public void class_isAbstract() {
    assertTrue(java.lang.reflect.Modifier.isAbstract(RefreshableDataSingleSelectListState.class.getModifiers()));
  }

  @Test
  public void getData_returnsRefreshableListData() {
    assertTrue(state.getData() instanceof RefreshableListData);
  }

  // ── Iteration ─────────────────────────────────────────────────────

  @Test
  public void iterator_coversAllItems() {
    int count = 0;
    for (String val : state) {
      count++;
    }
    assertEquals(3, count);
  }
}
