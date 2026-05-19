package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.data.MutableListData;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link MutableDataSingleSelectListState} — abstract state
 * backed by mutable list data. Tests construction, mutation (add/remove/set),
 * selection tracking after mutations, and all three constructor variants.
 */
public class MutableDataSingleSelectListStateCoverageTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("c0000000-0000-0000-0003-000000000001"), "mutCov");

  private static class TestMutableState extends MutableDataSingleSelectListState<String> {
    @SafeVarargs
    TestMutableState(Group group, int selectionIndex, String... values) {
      super(group, CroquetTestUtils.nextTestUUID(), selectionIndex, CroquetTestUtils.STRING_CODEC, values);
    }

    TestMutableState(Group group, ItemCodec<String> codec) {
      super(group, CroquetTestUtils.nextTestUUID(), codec);
    }

    @Override
    protected Class<? extends Element> getClassUsedForLocalization() {
      return TestMutableState.class;
    }

    @Override
    protected String getSubKeyForLocalization() {
      return "test";
    }
  }

  private TestMutableState state;

  @Before
  public void setUp() {
    state = new TestMutableState(TEST_GROUP, 0, "red", "green", "blue");
    CroquetTestUtils.removeListSelectionListeners(state);
  }

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void constructor_withValues_selectsFirst() {
    assertEquals("red", state.getValue());
  }

  @Test
  public void constructor_emptyCodecOnly() {
    TestMutableState s = new TestMutableState(TEST_GROUP, CroquetTestUtils.STRING_CODEC);
    CroquetTestUtils.removeListSelectionListeners(s);
    assertNull(s.getValue());
    assertEquals(0, s.getItemCount());
  }

  @Test
  public void constructor_negativeIndex() {
    TestMutableState s = new TestMutableState(TEST_GROUP, -1, "x", "y");
    CroquetTestUtils.removeListSelectionListeners(s);
    assertNull(s.getValue());
    assertEquals(2, s.getItemCount());
  }

  // ── Item access ───────────────────────────────────────────────────

  @Test
  public void getItemCount_matchesInitialValues() {
    assertEquals(3, state.getItemCount());
  }

  @Test
  public void getItemAt_returnsCorrectItems() {
    assertEquals("red", state.getItemAt(0));
    assertEquals("green", state.getItemAt(1));
    assertEquals("blue", state.getItemAt(2));
  }

  // ── Mutation ──────────────────────────────────────────────────────

  @Test
  public void addItem_increasesCount() {
    state.getData().internalAddItem("yellow");
    assertEquals(4, state.getItemCount());
  }

  @Test
  public void addItem_isAccessible() {
    state.getData().internalAddItem("yellow");
    assertEquals("yellow", state.getItemAt(3));
  }

  @Test
  public void removeItem_decreasesCount() {
    state.getData().internalRemoveItem("green");
    assertEquals(2, state.getItemCount());
  }

  @Test
  public void removeItem_removedNotAccessible() {
    state.getData().internalRemoveItem("green");
    assertEquals(-1, state.indexOf("green"));
  }

  @Test
  public void clear_removesAllItems() {
    state.getData().internalRemoveItem("red");
    state.getData().internalRemoveItem("green");
    state.getData().internalRemoveItem("blue");
    assertEquals(0, state.getItemCount());
  }

  // ── Selection tracking ────────────────────────────────────────────

  @Test
  public void setValueTransactionlessly_changesSelection() {
    state.setValueTransactionlessly("blue");
    assertEquals("blue", state.getValue());
  }

  @Test
  public void setValueTransactionlessly_toNull() {
    state.setValueTransactionlessly(null);
    assertNull(state.getValue());
  }

  @Test
  public void setValueTransactionlessly_roundTrip() {
    state.setValueTransactionlessly("green");
    state.setValueTransactionlessly("red");
    assertEquals("red", state.getValue());
  }

  // ── Codec delegation ──────────────────────────────────────────────

  @Test
  public void getItemCodec_valueClass() {
    assertEquals(String.class, state.getItemCodec().getValueClass());
  }

  @Test
  public void appendRepresentation_nonNull() {
    StringBuilder sb = new StringBuilder();
    state.appendRepresentation(sb, "blue");
    assertEquals("blue", sb.toString());
  }

  // ── Hierarchy ─────────────────────────────────────────────────────

  @Test
  public void class_extendsSingleSelectListState() {
    assertTrue(SingleSelectListState.class.isAssignableFrom(MutableDataSingleSelectListState.class));
  }

  @Test
  public void class_isAbstract() {
    assertTrue(java.lang.reflect.Modifier.isAbstract(MutableDataSingleSelectListState.class.getModifiers()));
  }

  @Test
  public void getData_returnsMutableListData() {
    assertTrue(state.getData() instanceof MutableListData);
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

  @Test
  public void iterator_afterAdd() {
    state.getData().internalAddItem("yellow");
    int count = 0;
    for (String val : state) {
      count++;
    }
    assertEquals(4, count);
  }
}
