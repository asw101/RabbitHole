package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.data.ImmutableListData;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link EnumConstantState} — constructor, getValue, setValue,
 * listener dispatch, codec delegation, and iteration.
 */
public class EnumConstantStateCoverageTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("c0000000-0000-0000-0010-ffffffffffff"), "enumCov");

  private enum TestDirection {
    NORTH, SOUTH, EAST, WEST
  }

  private enum SingleValue {
    ONLY
  }

  private TestEnumState state;

  @Before
  public void setUp() {
    state = new TestEnumState(TEST_GROUP, 0, TestDirection.class);
    CroquetTestUtils.removeListSelectionListeners(state);
  }

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void constructor_setsInitialValueByIndex() {
    assertEquals(TestDirection.NORTH, state.getValue());
  }

  @Test
  public void constructor_index1_selectsSecondEnum() {
    TestEnumState s = new TestEnumState(TEST_GROUP, 1, TestDirection.class);
    CroquetTestUtils.removeListSelectionListeners(s);
    assertEquals(TestDirection.SOUTH, s.getValue());
  }

  @Test
  public void constructor_lastIndex() {
    TestEnumState s = new TestEnumState(TEST_GROUP, 3, TestDirection.class);
    CroquetTestUtils.removeListSelectionListeners(s);
    assertEquals(TestDirection.WEST, s.getValue());
  }

  // ── getItemCount ──────────────────────────────────────────────────

  @Test
  public void getItemCount_matchesEnumConstants() {
    assertEquals(4, state.getItemCount());
  }

  @Test
  public void getItemCount_singleValueEnum() {
    TestSingleEnumState s = new TestSingleEnumState(TEST_GROUP, 0);
    CroquetTestUtils.removeListSelectionListeners(s);
    assertEquals(1, s.getItemCount());
  }

  // ── getItemAt ─────────────────────────────────────────────────────

  @Test
  public void getItemAt_returnsCorrectEnums() {
    assertEquals(TestDirection.NORTH, state.getItemAt(0));
    assertEquals(TestDirection.SOUTH, state.getItemAt(1));
    assertEquals(TestDirection.EAST, state.getItemAt(2));
    assertEquals(TestDirection.WEST, state.getItemAt(3));
  }

  // ── indexOf ───────────────────────────────────────────────────────

  @Test
  public void indexOf_existingEnum() {
    assertEquals(2, state.indexOf(TestDirection.EAST));
  }

  @Test
  public void indexOf_firstEnum() {
    assertEquals(0, state.indexOf(TestDirection.NORTH));
  }

  // ── containsItem ──────────────────────────────────────────────────

  @Test
  public void containsItem_allEnumsPresent() {
    for (TestDirection d : TestDirection.values()) {
      assertTrue(state.containsItem(d));
    }
  }

  // ── setValueTransactionlessly ─────────────────────────────────────

  @Test
  public void setValueTransactionlessly_updatesSelection() {
    state.setValueTransactionlessly(TestDirection.WEST);
    assertEquals(TestDirection.WEST, state.getValue());
  }

  @Test
  public void setValueTransactionlessly_updatesIndex() {
    state.setValueTransactionlessly(TestDirection.EAST);
    assertEquals(2, state.getSelectedIndex());
  }

  // ── clearSelection ────────────────────────────────────────────────

  @Test
  public void clearSelection_setsNull() {
    state.clearSelection();
    assertNull(state.getValue());
    assertEquals(-1, state.getSelectedIndex());
  }

  // ── listener dispatch ─────────────────────────────────────────────

  @Test
  public void oldSchoolListener_firesOnChange() {
    AtomicReference<TestDirection> captured = new AtomicReference<>();
    state.addValueListener(new State.ValueListener<TestDirection>() {
      @Override
      public void changing(State<TestDirection> s, TestDirection prev, TestDirection next) {}

      @Override
      public void changed(State<TestDirection> s, TestDirection prev, TestDirection next) {
        captured.set(next);
      }
    });
    state.setValueTransactionlessly(TestDirection.SOUTH);
    assertEquals(TestDirection.SOUTH, captured.get());
  }

  @Test
  public void newSchoolListener_firesOnChange() {
    AtomicReference<TestDirection> captured = new AtomicReference<>();
    state.addNewSchoolValueListener(e -> captured.set(e.getNextValue()));
    state.setValueTransactionlessly(TestDirection.EAST);
    assertEquals(TestDirection.EAST, captured.get());
  }

  @Test
  public void listener_doesNotFire_whenSameValue() {
    AtomicInteger count = new AtomicInteger();
    state.addValueListener(new State.ValueListener<TestDirection>() {
      @Override
      public void changing(State<TestDirection> s, TestDirection prev, TestDirection next) {}

      @Override
      public void changed(State<TestDirection> s, TestDirection prev, TestDirection next) {
        count.incrementAndGet();
      }
    });
    state.setValueTransactionlessly(TestDirection.NORTH); // same as initial
    assertEquals(0, count.get());
  }

  // ── codec delegation ──────────────────────────────────────────────

  @Test
  public void appendRepresentation_delegatesToCodec() {
    StringBuilder sb = new StringBuilder();
    state.appendRepresentation(sb, TestDirection.SOUTH);
    assertEquals("SOUTH", sb.toString());
  }

  @Test
  public void appendRepresentation_null() {
    StringBuilder sb = new StringBuilder();
    state.appendRepresentation(sb, null);
    assertEquals("null", sb.toString());
  }

  // ── iteration ─────────────────────────────────────────────────────

  @Test
  public void iterator_coversAllEnums() {
    int count = 0;
    for (TestDirection d : state) {
      count++;
    }
    assertEquals(4, count);
  }

  // ── toArray ───────────────────────────────────────────────────────

  @Test
  public void toArray_matchesEnumValues() {
    TestDirection[] arr = state.toArray();
    assertArrayEquals(TestDirection.values(), arr);
  }

  // ── getData ───────────────────────────────────────────────────────

  @Test
  public void getData_returnsImmutableListData() {
    assertTrue(state.getData() instanceof ImmutableListData);
  }

  // ── getSwingModel ─────────────────────────────────────────────────

  @Test
  public void getSwingModel_nonNull() {
    assertNotNull(state.getSwingModel());
  }

  @Test
  public void comboBoxModel_sizeMatchesEnumCount() {
    assertEquals(4, state.getSwingModel().getComboBoxModel().getSize());
  }

  // ── Test infrastructure ───────────────────────────────────────────

  static class TestEnumState extends EnumConstantState<TestDirection> {
    TestEnumState(Group group, int selectionIndex, Class<TestDirection> cls) {
      super(group, CroquetTestUtils.nextTestUUID(), selectionIndex, cls);
    }
  }

  static class TestSingleEnumState extends EnumConstantState<SingleValue> {
    TestSingleEnumState(Group group, int selectionIndex) {
      super(group, CroquetTestUtils.nextTestUUID(), selectionIndex, SingleValue.class);
    }
  }
}
