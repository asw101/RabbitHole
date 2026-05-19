package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.preferences.PreferenceMutableDataSingleSelectListState;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link PreferenceMutableDataSingleSelectListState} — concrete
 * preference-backed mutable list state. Tests construction, selection, data management.
 */
public class PreferenceMutableDataSingleSelectListStateCoverageTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("c0000000-0000-0000-000a-000000000001"), "prefMutCov");

  private PreferenceMutableDataSingleSelectListState<String> state;

  @Before
  public void setUp() {
    state = new PreferenceMutableDataSingleSelectListState<>(
        TEST_GROUP, CroquetTestUtils.nextTestUUID(), 0, CroquetTestUtils.STRING_CODEC, "alpha", "beta", "gamma");
    CroquetTestUtils.removeListSelectionListeners(state);
  }

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void constructor_selectsFirstItem() {
    assertEquals("alpha", state.getValue());
  }

  @Test
  public void constructor_itemCount() {
    assertEquals(3, state.getItemCount());
  }

  // ── Selection ─────────────────────────────────────────────────────

  @Test
  public void setValueTransactionlessly_changesToBeta() {
    state.setValueTransactionlessly("beta");
    assertEquals("beta", state.getValue());
  }

  @Test
  public void setValueTransactionlessly_roundTrip() {
    state.setValueTransactionlessly("gamma");
    state.setValueTransactionlessly("alpha");
    assertEquals("alpha", state.getValue());
  }

  // ── Data management ───────────────────────────────────────────────

  @Test
  public void addItem_increasesCount() {
    state.getData().internalAddItem("delta");
    assertEquals(4, state.getItemCount());
  }

  @Test
  public void removeItem_decreasesCount() {
    state.getData().internalRemoveItem("beta");
    assertEquals(2, state.getItemCount());
  }

  @Test
  public void getItemAt_returnsCorrectItems() {
    assertEquals("alpha", state.getItemAt(0));
    assertEquals("beta", state.getItemAt(1));
    assertEquals("gamma", state.getItemAt(2));
  }

  // ── Hierarchy ─────────────────────────────────────────────────────

  @Test
  public void class_extendsMutableDataSingleSelectListState() {
    assertTrue(MutableDataSingleSelectListState.class.isAssignableFrom(PreferenceMutableDataSingleSelectListState.class));
  }

  @Test
  public void class_isConcrete() {
    assertFalse(java.lang.reflect.Modifier.isAbstract(PreferenceMutableDataSingleSelectListState.class.getModifiers()));
  }

  // ── Codec ─────────────────────────────────────────────────────────

  @Test
  public void getItemCodec_valueClass() {
    assertEquals(String.class, state.getItemCodec().getValueClass());
  }
}
