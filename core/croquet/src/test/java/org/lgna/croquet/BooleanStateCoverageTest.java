package org.lgna.croquet;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Extended coverage tests for {@link BooleanState} — edge cases, encode/decode
 * round-trip, toggle sequences, listener interaction, and ButtonModel sync.
 */
public class BooleanStateCoverageTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("c0000000-0000-0000-0001-ffffffffffff"), "boolCov");

  private TestBooleanState state;

  @Before
  public void setUp() {
    state = new TestBooleanState(TEST_GROUP, false);
    CroquetTestUtils.removeItemListeners(state);
  }

  // ── encode/decode round-trip ──────────────────────────────────────

  @Test
  public void encodeAndDecode_false_roundTrips() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    state.encodeValue(encoder, false);
    BinaryDecoder decoder = encoder.createDecoder();
    assertEquals(Boolean.FALSE, state.decodeValue(decoder));
  }

  @Test
  public void encodeAndDecode_true_roundTrips() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    state.encodeValue(encoder, true);
    BinaryDecoder decoder = encoder.createDecoder();
    assertEquals(Boolean.TRUE, state.decodeValue(decoder));
  }

  @Test
  public void encodeAndDecode_multipleValues_sequential() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    state.encodeValue(encoder, true);
    state.encodeValue(encoder, false);
    state.encodeValue(encoder, true);
    BinaryDecoder decoder = encoder.createDecoder();
    assertTrue(state.decodeValue(decoder));
    assertFalse(state.decodeValue(decoder));
    assertTrue(state.decodeValue(decoder));
  }

  // ── toggle edge cases ─────────────────────────────────────────────

  @Test
  public void rapidToggle_tenTimes_endsCorrectly() {
    for (int i = 0; i < 10; i++) {
      state.setValueTransactionlessly(i % 2 == 0);
    }
    // 10 iterations, last is i=9 → 9%2==1 → false
    assertFalse(state.getValue());
  }

  @Test
  public void setValueTransactionlessly_sameValue_doesNotFireListener() {
    AtomicInteger count = new AtomicInteger();
    state.addValueListener(new State.ValueListener<Boolean>() {
      @Override
      public void changing(State<Boolean> s, Boolean prev, Boolean next) {}

      @Override
      public void changed(State<Boolean> s, Boolean prev, Boolean next) {
        count.incrementAndGet();
      }
    });
    state.setValueTransactionlessly(false); // same as initial
    assertEquals(0, count.get());
  }

  @Test
  public void setValueTransactionlessly_differentValue_firesListenerOnce() {
    AtomicInteger count = new AtomicInteger();
    state.addValueListener(new State.ValueListener<Boolean>() {
      @Override
      public void changing(State<Boolean> s, Boolean prev, Boolean next) {}

      @Override
      public void changed(State<Boolean> s, Boolean prev, Boolean next) {
        count.incrementAndGet();
      }
    });
    state.setValueTransactionlessly(true);
    assertEquals(1, count.get());
  }

  // ── ButtonModel sync edge cases ───────────────────────────────────

  @Test
  public void buttonModel_initiallyNotSelected() {
    assertFalse(state.getImp().getSwingModel().getButtonModel().isSelected());
  }

  @Test
  public void buttonModel_selectedAfterSetTrue() {
    state.setValueTransactionlessly(true);
    assertTrue(state.getImp().getSwingModel().getButtonModel().isSelected());
  }

  @Test
  public void buttonModel_deselectedAfterToggleBack() {
    state.setValueTransactionlessly(true);
    state.setValueTransactionlessly(false);
    assertFalse(state.getImp().getSwingModel().getButtonModel().isSelected());
  }

  // ── changeValueFromEdit sequences ─────────────────────────────────

  @Test
  public void changeValueFromEdit_multipleSequential() {
    state.changeValueFromEdit(true);
    assertTrue(state.getValue());
    state.changeValueFromEdit(false);
    assertFalse(state.getValue());
    state.changeValueFromEdit(true);
    assertTrue(state.getValue());
  }

  // ── Multiple old-school listeners ─────────────────────────────────

  @Test
  public void multipleListeners_allFire() {
    AtomicBoolean listener1Fired = new AtomicBoolean();
    AtomicBoolean listener2Fired = new AtomicBoolean();

    state.addValueListener(new State.ValueListener<Boolean>() {
      @Override
      public void changing(State<Boolean> s, Boolean prev, Boolean next) {}

      @Override
      public void changed(State<Boolean> s, Boolean prev, Boolean next) {
        listener1Fired.set(true);
      }
    });
    state.addValueListener(new State.ValueListener<Boolean>() {
      @Override
      public void changing(State<Boolean> s, Boolean prev, Boolean next) {}

      @Override
      public void changed(State<Boolean> s, Boolean prev, Boolean next) {
        listener2Fired.set(true);
      }
    });

    state.setValueTransactionlessly(true);
    assertTrue(listener1Fired.get());
    assertTrue(listener2Fired.get());
  }

  // ── Multiple new-school listeners ─────────────────────────────────

  @Test
  public void multipleNewSchoolListeners_allFire() {
    AtomicBoolean l1 = new AtomicBoolean();
    AtomicBoolean l2 = new AtomicBoolean();
    state.addNewSchoolValueListener(e -> l1.set(true));
    state.addNewSchoolValueListener(e -> l2.set(true));
    state.setValueTransactionlessly(true);
    assertTrue(l1.get());
    assertTrue(l2.get());
  }

  // ── Text alternation with value changes ───────────────────────────

  @Test
  public void textForTrue_afterValueChange_staysConsistent() {
    state.setTextForTrueAndTextForFalse("ON", "OFF");
    state.setValueTransactionlessly(true);
    assertEquals("ON", state.getTrueText());
    assertEquals("OFF", state.getFalseText());
  }

  @Test
  public void getTextFor_togglesWithBoolArg() {
    state.setTextForTrueAndTextForFalse("Yes", "No");
    assertEquals("Yes", state.getTextFor(true));
    assertEquals("No", state.getTextFor(false));
  }

  // ── Icon for both true and false ──────────────────────────────────

  @Test
  public void setIconForBothTrueAndFalse_setsSameIcon() {
    javax.swing.Icon icon = new javax.swing.ImageIcon();
    state.setIconForBothTrueAndFalse(icon);
    assertSame(icon, state.getTrueIcon());
    assertSame(icon, state.getFalseIcon());
  }

  // ── appendRepresentation edge cases ───────────────────────────────

  @Test
  public void appendRepresentation_toNonEmptyStringBuilder() {
    StringBuilder sb = new StringBuilder("prefix:");
    state.appendRepresentation(sb, true);
    assertEquals("prefix:true", sb.toString());
  }

  // ── enabled with value change ─────────────────────────────────────

  @Test
  public void setValueWhileDisabled_stillUpdatesValue() {
    state.setEnabled(false);
    state.setValueTransactionlessly(true);
    assertTrue(state.getValue());
  }

  // ── changing callback receives correct prev/next ──────────────────

  @Test
  public void changingCallback_receivesCorrectPrevAndNext() {
    AtomicReference<Boolean> prevInChanging = new AtomicReference<>();
    AtomicReference<Boolean> nextInChanging = new AtomicReference<>();

    state.addValueListener(new State.ValueListener<Boolean>() {
      @Override
      public void changing(State<Boolean> s, Boolean prev, Boolean next) {
        prevInChanging.set(prev);
        nextInChanging.set(next);
      }

      @Override
      public void changed(State<Boolean> s, Boolean prev, Boolean next) {}
    });

    state.setValueTransactionlessly(true);
    assertEquals(Boolean.FALSE, prevInChanging.get());
    assertEquals(Boolean.TRUE, nextInChanging.get());
  }

  // ── initialValue true ─────────────────────────────────────────────

  @Test
  public void initialTrue_encodeDecode_roundTrips() {
    TestBooleanState trueState = new TestBooleanState(TEST_GROUP, true);
    CroquetTestUtils.removeItemListeners(trueState);

    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    trueState.encodeValue(encoder, trueState.getValue());
    BinaryDecoder decoder = encoder.createDecoder();
    assertTrue(trueState.decodeValue(decoder));
  }

  // ── listener ordering across both old and new school ──────────────

  @Test
  public void mixedListeners_oldSchoolFiresBeforeNewSchool() {
    List<String> order = new ArrayList<>();

    state.addValueListener(new State.ValueListener<Boolean>() {
      @Override
      public void changing(State<Boolean> s, Boolean prev, Boolean next) {}

      @Override
      public void changed(State<Boolean> s, Boolean prev, Boolean next) {
        order.add("old");
      }
    });
    state.addNewSchoolValueListener(e -> order.add("new"));

    state.setValueTransactionlessly(true);
    assertEquals(2, order.size());
    // Old school fires in changeValue before new school
    assertEquals("old", order.get(0));
    assertEquals("new", order.get(1));
  }
}
