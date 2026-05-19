package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link ItemState} via {@link SimpleItemState} —
 * codec delegation, getValue/setValue, appendRepresentation, and listeners.
 */
public class ItemStateCoverageTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("c0000000-0000-0000-0014-ffffffffffff"), "itemCov");

  private TestSimpleItemState state;

  @Before
  public void setUp() {
    state = new TestSimpleItemState(TEST_GROUP, "initial");
  }

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void constructor_setsInitialValue() {
    assertEquals("initial", state.getValue());
  }

  @Test
  public void constructor_nullInitialValue() {
    TestSimpleItemState s = new TestSimpleItemState(TEST_GROUP, null);
    assertNull(s.getValue());
  }

  // ── getItemCodec ──────────────────────────────────────────────────

  @Test
  public void getItemCodec_returnsNonNull() {
    assertNotNull(state.getItemCodec());
  }

  @Test
  public void getItemCodec_valueClass() {
    assertEquals(String.class, state.getItemCodec().getValueClass());
  }

  // ── codec delegation ──────────────────────────────────────────────

  @Test
  public void decodeValue_delegatesToCodec() {
    edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder encoder =
        new edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder();
    state.encodeValue(encoder, "test");
    edu.cmu.cs.dennisc.codec.BinaryDecoder decoder = encoder.createDecoder();
    assertEquals("test", state.decodeValue(decoder));
  }

  @Test
  public void encodeValue_delegatesToCodec() {
    edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder encoder =
        new edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder();
    state.encodeValue(encoder, "hello");
    edu.cmu.cs.dennisc.codec.BinaryDecoder decoder = encoder.createDecoder();
    assertEquals("hello", state.decodeValue(decoder));
  }

  @Test
  public void appendRepresentation_delegatesToCodec() {
    StringBuilder sb = new StringBuilder();
    state.appendRepresentation(sb, "value");
    assertEquals("value", sb.toString());
  }

  @Test
  public void appendRepresentation_nullValue() {
    StringBuilder sb = new StringBuilder();
    state.appendRepresentation(sb, null);
    assertEquals("null", sb.toString());
  }

  // ── appendUserRepr ────────────────────────────────────────────────

  @Test
  public void appendUserRepr_appendsCurrentValue() {
    StringBuilder sb = new StringBuilder();
    state.appendUserRepr(sb);
    assertEquals("initial", sb.toString());
  }

  @Test
  public void appendUserRepr_afterValueChange() {
    state.setValueTransactionlessly("changed");
    StringBuilder sb = new StringBuilder();
    state.appendUserRepr(sb);
    assertEquals("changed", sb.toString());
  }

  // ── setValueTransactionlessly ─────────────────────────────────────

  @Test
  public void setValueTransactionlessly_updatesValue() {
    state.setValueTransactionlessly("new");
    assertEquals("new", state.getValue());
  }

  @Test
  public void setValueTransactionlessly_toNull() {
    state.setValueTransactionlessly(null);
    assertNull(state.getValue());
  }

  // ── changeValueFromEdit ───────────────────────────────────────────

  @Test
  public void changeValueFromEdit_updatesValue() {
    state.changeValueFromEdit("edited");
    assertEquals("edited", state.getValue());
  }

  // ── listener dispatch ─────────────────────────────────────────────

  @Test
  public void valueListener_firesOnChange() {
    AtomicReference<String> captured = new AtomicReference<>();
    state.addValueListener(new State.ValueListener<String>() {
      @Override
      public void changing(State<String> s, String prev, String next) {}

      @Override
      public void changed(State<String> s, String prev, String next) {
        captured.set(next);
      }
    });
    state.setValueTransactionlessly("changed");
    assertEquals("changed", captured.get());
  }

  @Test
  public void newSchoolListener_firesOnChange() {
    AtomicReference<String> captured = new AtomicReference<>();
    state.addNewSchoolValueListener(e -> captured.set(e.getNextValue()));
    state.setValueTransactionlessly("changed");
    assertEquals("changed", captured.get());
  }

  @Test
  public void addAndInvokeValueListener_firesImmediately() {
    AtomicReference<String> captured = new AtomicReference<>();
    state.addAndInvokeValueListener(new State.ValueListener<String>() {
      @Override
      public void changing(State<String> s, String prev, String next) {}

      @Override
      public void changed(State<String> s, String prev, String next) {
        captured.set(next);
      }
    });
    assertEquals("initial", captured.get());
  }

  @Test
  public void addAndInvokeNewSchoolListener_firesImmediately() {
    AtomicReference<String> captured = new AtomicReference<>();
    state.addAndInvokeNewSchoolValueListener(e -> captured.set(e.getNextValue()));
    assertEquals("initial", captured.get());
  }

  // ── encode/decode round trip ──────────────────────────────────────

  @Test
  public void encodeDecodeRoundTrip_multipleValues() {
    edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder encoder =
        new edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder();
    state.encodeValue(encoder, "first");
    state.encodeValue(encoder, "second");
    edu.cmu.cs.dennisc.codec.BinaryDecoder decoder = encoder.createDecoder();
    assertEquals("first", state.decodeValue(decoder));
    assertEquals("second", state.decodeValue(decoder));
  }

  // ── getMigrationId ────────────────────────────────────────────────

  @Test
  public void getMigrationId_returnsNonNull() {
    assertNotNull(state.getMigrationId());
  }

  // ── Test infrastructure ───────────────────────────────────────────

  static class TestSimpleItemState extends SimpleItemState<String> {
    TestSimpleItemState(Group group, String initialValue) {
      super(group, CroquetTestUtils.nextTestUUID(), initialValue, CroquetTestUtils.STRING_CODEC);
    }

    @Override
    protected void localize() {}

    @Override
    public boolean isEnabled() { return true; }

    @Override
    public void setEnabled(boolean isEnabled) {}

    @Override
    public List<List<PrepModel>> getPotentialPrepModelPaths(org.lgna.croquet.edits.Edit edit) {
      return Collections.emptyList();
    }

    @Override
    protected String getSwingValue() {
      return getValue();
    }

    @Override
    protected void setSwingValue(String nextValue) {}
  }
}
