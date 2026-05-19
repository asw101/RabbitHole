package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.codecs.EnumCodec;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link EnumConstantState} — concrete list state
 * backed by an enum's constants. Tests construction, initial selection,
 * value access, selection changes, codec delegation, and iteration.
 */
public class EnumConstantStateCoverageTest {

  private enum TestEnum { ALPHA, BETA, GAMMA }

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("c0000000-0000-0000-0001-000000000001"), "enumCov");

  private EnumConstantState<TestEnum> state;

  @Before
  public void setUp() {
    state = new EnumConstantState<>(TEST_GROUP, CroquetTestUtils.nextTestUUID(), 0, TestEnum.class);
    CroquetTestUtils.removeListSelectionListeners(state);
  }

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void constructor_selectsFirstConstant() {
    assertEquals(TestEnum.ALPHA, state.getValue());
  }

  @Test
  public void constructor_selectsMiddleConstant() {
    EnumConstantState<TestEnum> s = new EnumConstantState<>(TEST_GROUP, CroquetTestUtils.nextTestUUID(), 1, TestEnum.class);
    CroquetTestUtils.removeListSelectionListeners(s);
    assertEquals(TestEnum.BETA, s.getValue());
  }

  @Test
  public void constructor_selectsLastConstant() {
    EnumConstantState<TestEnum> s = new EnumConstantState<>(TEST_GROUP, CroquetTestUtils.nextTestUUID(), 2, TestEnum.class);
    CroquetTestUtils.removeListSelectionListeners(s);
    assertEquals(TestEnum.GAMMA, s.getValue());
  }

  @Test
  public void constructor_negativeIndex_yieldsNull() {
    EnumConstantState<TestEnum> s = new EnumConstantState<>(TEST_GROUP, CroquetTestUtils.nextTestUUID(), -1, TestEnum.class);
    CroquetTestUtils.removeListSelectionListeners(s);
    assertNull(s.getValue());
  }

  // ── Item access ───────────────────────────────────────────────────

  @Test
  public void getItemCount_matchesEnumConstants() {
    assertEquals(TestEnum.values().length, state.getItemCount());
  }

  @Test
  public void getItemAt_returnsCorrectConstants() {
    assertEquals(TestEnum.ALPHA, state.getItemAt(0));
    assertEquals(TestEnum.BETA, state.getItemAt(1));
    assertEquals(TestEnum.GAMMA, state.getItemAt(2));
  }

  @Test
  public void indexOf_findsConstants() {
    assertEquals(0, state.indexOf(TestEnum.ALPHA));
    assertEquals(1, state.indexOf(TestEnum.BETA));
    assertEquals(2, state.indexOf(TestEnum.GAMMA));
  }

  // ── Selection changes ─────────────────────────────────────────────

  @Test
  public void setValueTransactionlessly_changesToBeta() {
    state.setValueTransactionlessly(TestEnum.BETA);
    assertEquals(TestEnum.BETA, state.getValue());
  }

  @Test
  public void setValueTransactionlessly_changesToGamma() {
    state.setValueTransactionlessly(TestEnum.GAMMA);
    assertEquals(TestEnum.GAMMA, state.getValue());
  }

  @Test
  public void setValueTransactionlessly_roundTrip() {
    state.setValueTransactionlessly(TestEnum.GAMMA);
    state.setValueTransactionlessly(TestEnum.ALPHA);
    assertEquals(TestEnum.ALPHA, state.getValue());
  }

  @Test
  public void setValueTransactionlessly_toNull() {
    state.setValueTransactionlessly(null);
    assertNull(state.getValue());
  }

  // ── Codec delegation ──────────────────────────────────────────────

  @Test
  public void getItemCodec_returnsEnumCodec() {
    ItemCodec<TestEnum> codec = state.getItemCodec();
    assertNotNull(codec);
    assertTrue(codec instanceof EnumCodec);
  }

  @Test
  public void getItemCodec_valueClass_matchesEnum() {
    assertEquals(TestEnum.class, state.getItemCodec().getValueClass());
  }

  @Test
  public void appendRepresentation_nonNull() {
    StringBuilder sb = new StringBuilder();
    state.appendRepresentation(sb, TestEnum.BETA);
    assertTrue(sb.length() > 0);
  }

  @Test
  public void appendRepresentation_null() {
    StringBuilder sb = new StringBuilder();
    state.appendRepresentation(sb, null);
    assertTrue(sb.toString().contains("null"));
  }

  // ── Iteration ─────────────────────────────────────────────────────

  @Test
  public void iterator_coversAllConstants() {
    int count = 0;
    for (TestEnum val : state) {
      assertNotNull(val);
      count++;
    }
    assertEquals(TestEnum.values().length, count);
  }

  // ── Hierarchy ─────────────────────────────────────────────────────

  @Test
  public void class_extendsImmutableDataSingleSelectListState() {
    assertTrue(ImmutableDataSingleSelectListState.class.isAssignableFrom(EnumConstantState.class));
  }

  @Test
  public void class_extendsSingleSelectListState() {
    assertTrue(SingleSelectListState.class.isAssignableFrom(EnumConstantState.class));
  }

  @Test
  public void class_isConcrete() {
    assertFalse(java.lang.reflect.Modifier.isAbstract(EnumConstantState.class.getModifiers()));
  }

  // ── Data immutability ─────────────────────────────────────────────

  @Test
  public void getData_returnsNonNull() {
    assertNotNull(state.getData());
  }

  @Test
  public void getData_itemCount_matchesEnum() {
    assertEquals(TestEnum.values().length, state.getData().getItemCount());
  }

  // ── Second enum type ──────────────────────────────────────────────

  private enum SmallEnum { ONLY }

  @Test
  public void singleConstantEnum_works() {
    EnumConstantState<SmallEnum> s =
        new EnumConstantState<>(TEST_GROUP, CroquetTestUtils.nextTestUUID(), 0, SmallEnum.class);
    CroquetTestUtils.removeListSelectionListeners(s);
    assertEquals(SmallEnum.ONLY, s.getValue());
    assertEquals(1, s.getItemCount());
  }
}
