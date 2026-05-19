package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.edits.Edit;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link ItemState} via {@link SimpleItemState} — tests
 * codec delegation, getValue/setValue, appendRepresentation, and
 * the inner selection state/operation caching.
 */
public class ItemStateCoverageTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("c0000000-0000-0000-0005-000000000001"), "itemCov");

  private static class TestSimpleItemState extends SimpleItemState<String> {
    private String swingValue;

    TestSimpleItemState(Group group, String initialValue) {
      super(group, CroquetTestUtils.nextTestUUID(), initialValue, CroquetTestUtils.STRING_CODEC);
      this.swingValue = initialValue;
    }

    @Override
    public List<List<PrepModel>> getPotentialPrepModelPaths(Edit edit) {
      return Collections.emptyList();
    }

    @Override
    protected String getSwingValue() {
      return this.swingValue;
    }

    @Override
    protected void setSwingValue(String nextValue) {
      this.swingValue = nextValue;
    }

    @Override
    protected void localize() {
    }

    @Override
    protected Class<? extends Element> getClassUsedForLocalization() {
      return TestSimpleItemState.class;
    }

    @Override
    protected String getSubKeyForLocalization() {
      return "test";
    }
  }

  private TestSimpleItemState state;

  @Before
  public void setUp() {
    state = new TestSimpleItemState(TEST_GROUP, "hello");
  }

  // ── Codec delegation ──────────────────────────────────────────────

  @Test
  public void getItemCodec_returnsNonNull() {
    assertNotNull(state.getItemCodec());
  }

  @Test
  public void getItemCodec_valueClass() {
    assertEquals(String.class, state.getItemCodec().getValueClass());
  }

  @Test
  public void appendRepresentation_delegatesToCodec() {
    StringBuilder sb = new StringBuilder();
    state.appendRepresentation(sb, "world");
    assertEquals("world", sb.toString());
  }

  @Test
  public void appendRepresentation_null() {
    StringBuilder sb = new StringBuilder();
    state.appendRepresentation(sb, null);
    assertEquals("null", sb.toString());
  }

  @Test
  public void appendUserRepr_usesCurrentValue() {
    StringBuilder sb = new StringBuilder();
    state.appendUserRepr(sb);
    assertTrue(sb.length() > 0);
  }

  // ── Selection operations ──────────────────────────────────────────

  @Test
  public void getItemSelectedState_returnsBooleanState() {
    BooleanState selected = state.getItemSelectedState("hello");
    assertNotNull(selected);
  }

  @Test
  public void getItemSelectedState_cachedForSameItem() {
    BooleanState s1 = state.getItemSelectedState("hello");
    BooleanState s2 = state.getItemSelectedState("hello");
    assertSame(s1, s2);
  }

  @Test
  public void getItemSelectionOperation_returnsOperation() {
    Operation op = state.getItemSelectionOperation("hello");
    assertNotNull(op);
  }

  @Test
  public void getItemSelectionOperation_cachedForSameItem() {
    Operation o1 = state.getItemSelectionOperation("hello");
    Operation o2 = state.getItemSelectionOperation("hello");
    assertSame(o1, o2);
  }

  @Test
  public void getAlternateLocalizationItemSelectionOperation_returnsOperation() {
    Operation op = state.getAlternateLocalizationItemSelectionOperation("hello");
    assertNotNull(op);
  }

  // ── Hierarchy ─────────────────────────────────────────────────────

  @Test
  public void class_extendsState() {
    assertTrue(State.class.isAssignableFrom(ItemState.class));
  }

  @Test
  public void class_isAbstract() {
    assertTrue(java.lang.reflect.Modifier.isAbstract(ItemState.class.getModifiers()));
  }

  @Test
  public void simpleItemState_extendsItemState() {
    assertTrue(ItemState.class.isAssignableFrom(SimpleItemState.class));
  }

  @Test
  public void simpleItemState_isAbstract() {
    assertTrue(java.lang.reflect.Modifier.isAbstract(SimpleItemState.class.getModifiers()));
  }

  // ── getValue ──────────────────────────────────────────────────────

  @Test
  public void getValue_returnsInitialValue() {
    assertEquals("hello", state.getValue());
  }

  @Test
  public void setValueTransactionlessly_updates() {
    state.setValueTransactionlessly("world");
    assertEquals("world", state.getValue());
  }

  @Test
  public void setValueTransactionlessly_toNull() {
    state.setValueTransactionlessly(null);
    assertNull(state.getValue());
  }
}
