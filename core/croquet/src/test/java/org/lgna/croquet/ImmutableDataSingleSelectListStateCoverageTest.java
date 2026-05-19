package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.data.ImmutableListData;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link ImmutableDataSingleSelectListState} — abstract state
 * backed by immutable list data. Tests construction, item access, selection,
 * immutability contract, and codec delegation via a concrete inner subclass.
 */
public class ImmutableDataSingleSelectListStateCoverageTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("c0000000-0000-0000-0002-000000000001"), "immCov");

  private static class TestImmutableState extends ImmutableDataSingleSelectListState<String> {
    @SafeVarargs
    TestImmutableState(Group group, int selectionIndex, String... values) {
      super(group, CroquetTestUtils.nextTestUUID(), selectionIndex, CroquetTestUtils.STRING_CODEC, values);
    }

    @Override
    protected Class<? extends Element> getClassUsedForLocalization() {
      return TestImmutableState.class;
    }

    @Override
    protected String getSubKeyForLocalization() {
      return "test";
    }
  }

  private TestImmutableState state;

  @Before
  public void setUp() {
    state = new TestImmutableState(TEST_GROUP, 0, "apple", "banana", "cherry");
    CroquetTestUtils.removeListSelectionListeners(state);
  }

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void constructor_selectsFirstItem() {
    assertEquals("apple", state.getValue());
  }

  @Test
  public void constructor_selectsMiddleItem() {
    TestImmutableState s = new TestImmutableState(TEST_GROUP, 1, "x", "y", "z");
    CroquetTestUtils.removeListSelectionListeners(s);
    assertEquals("y", s.getValue());
  }

  @Test
  public void constructor_negativeIndex_yieldsNull() {
    TestImmutableState s = new TestImmutableState(TEST_GROUP, -1, "x", "y");
    CroquetTestUtils.removeListSelectionListeners(s);
    assertNull(s.getValue());
  }

  // ── Item access ───────────────────────────────────────────────────

  @Test
  public void getItemCount_returnsCorrectSize() {
    assertEquals(3, state.getItemCount());
  }

  @Test
  public void getItemAt_returnsItems() {
    assertEquals("apple", state.getItemAt(0));
    assertEquals("banana", state.getItemAt(1));
    assertEquals("cherry", state.getItemAt(2));
  }

  @Test
  public void indexOf_findsItems() {
    assertEquals(0, state.indexOf("apple"));
    assertEquals(1, state.indexOf("banana"));
    assertEquals(2, state.indexOf("cherry"));
  }

  @Test
  public void indexOf_unknownItem_returnsNegative() {
    assertEquals(-1, state.indexOf("durian"));
  }

  @Test
  public void contains_existingItem() {
    assertTrue(state.getData().contains("apple"));
  }

  @Test
  public void contains_missingItem() {
    assertFalse(state.getData().contains("durian"));
  }

  // ── Selection ─────────────────────────────────────────────────────

  @Test
  public void setValueTransactionlessly_changesSelection() {
    state.setValueTransactionlessly("cherry");
    assertEquals("cherry", state.getValue());
  }

  @Test
  public void setValueTransactionlessly_roundTrip() {
    state.setValueTransactionlessly("banana");
    state.setValueTransactionlessly("apple");
    assertEquals("apple", state.getValue());
  }

  @Test
  public void setValueTransactionlessly_toNull() {
    state.setValueTransactionlessly(null);
    assertNull(state.getValue());
  }

  // ── Immutability contract ─────────────────────────────────────────

  @Test
  public void getData_returnsImmutableListData() {
    assertTrue(state.getData() instanceof ImmutableListData);
  }

  @Test
  public void data_itemCountStable() {
    int before = state.getData().getItemCount();
    state.setValueTransactionlessly("cherry");
    assertEquals(before, state.getData().getItemCount());
  }

  // ── Codec delegation ──────────────────────────────────────────────

  @Test
  public void getItemCodec_returnsProvidedCodec() {
    assertEquals(String.class, state.getItemCodec().getValueClass());
  }

  @Test
  public void appendRepresentation_worksForItems() {
    StringBuilder sb = new StringBuilder();
    state.appendRepresentation(sb, "banana");
    assertEquals("banana", sb.toString());
  }

  // ── Iteration ─────────────────────────────────────────────────────

  @Test
  public void iterator_coversAllItems() {
    int count = 0;
    for (String val : state) {
      assertNotNull(val);
      count++;
    }
    assertEquals(3, count);
  }

  // ── Hierarchy ─────────────────────────────────────────────────────

  @Test
  public void class_extendsSingleSelectListState() {
    assertTrue(SingleSelectListState.class.isAssignableFrom(ImmutableDataSingleSelectListState.class));
  }

  @Test
  public void class_isAbstract() {
    assertTrue(java.lang.reflect.Modifier.isAbstract(ImmutableDataSingleSelectListState.class.getModifiers()));
  }

  // ── Single item ───────────────────────────────────────────────────

  @Test
  public void singleItem_works() {
    TestImmutableState s = new TestImmutableState(TEST_GROUP, 0, "only");
    CroquetTestUtils.removeListSelectionListeners(s);
    assertEquals("only", s.getValue());
    assertEquals(1, s.getItemCount());
  }
}
