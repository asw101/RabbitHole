package org.lgna.croquet;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder;
import org.junit.Before;
import org.junit.Test;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Extended coverage tests for {@link StringState} — empty/null/unicode,
 * encode/decode round-trip, document sync edge cases, and listener fidelity.
 */
public class StringStateCoverageTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("c0000000-0000-0000-0002-ffffffffffff"), "strCov");

  private StringStateTest.TestStringState state;

  @Before
  public void setUp() {
    state = new StringStateTest.TestStringState(TEST_GROUP, "initial");
    CroquetTestUtils.removeDocumentListeners(state);
  }

  // ── encode/decode round-trip ──────────────────────────────────────

  @Test
  public void encodeAndDecode_normalString_roundTrips() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    state.encodeValue(encoder, "hello world");
    BinaryDecoder decoder = encoder.createDecoder();
    assertEquals("hello world", state.decodeValue(decoder));
  }

  @Test
  public void encodeAndDecode_emptyString_roundTrips() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    state.encodeValue(encoder, "");
    BinaryDecoder decoder = encoder.createDecoder();
    assertEquals("", state.decodeValue(decoder));
  }

  @Test
  public void encodeAndDecode_unicode_roundTrips() {
    String unicode = "日本語テスト 🎉";
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    state.encodeValue(encoder, unicode);
    BinaryDecoder decoder = encoder.createDecoder();
    assertEquals(unicode, state.decodeValue(decoder));
  }

  @Test
  public void encodeAndDecode_multipleStrings() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    state.encodeValue(encoder, "alpha");
    state.encodeValue(encoder, "bravo");
    BinaryDecoder decoder = encoder.createDecoder();
    assertEquals("alpha", state.decodeValue(decoder));
    assertEquals("bravo", state.decodeValue(decoder));
  }

  // ── unicode values ────────────────────────────────────────────────

  @Test
  public void setValue_unicode_updatesValue() {
    state.setValueTransactionlessly("こんにちは");
    assertEquals("こんにちは", state.getValue());
  }

  @Test
  public void setValue_unicodeEmoji_syncsDocument() throws BadLocationException {
    state.setValueTransactionlessly("🔥🚀");
    Document doc = state.getSwingModel().getDocument();
    assertEquals("🔥🚀", doc.getText(0, doc.getLength()));
  }

  // ── whitespace edge cases ─────────────────────────────────────────

  @Test
  public void setValue_whitespaceOnly() {
    state.setValueTransactionlessly("   ");
    assertEquals("   ", state.getValue());
  }

  @Test
  public void setValue_newlines() {
    state.setValueTransactionlessly("line1\nline2");
    assertEquals("line1\nline2", state.getValue());
  }

  @Test
  public void setValue_tabs() {
    state.setValueTransactionlessly("col1\tcol2");
    assertEquals("col1\tcol2", state.getValue());
  }

  // ── very long string ──────────────────────────────────────────────

  @Test
  public void setValue_longString_1000chars() {
    String longStr = "x".repeat(1000);
    state.setValueTransactionlessly(longStr);
    assertEquals(longStr, state.getValue());
    assertEquals(1000, state.getValue().length());
  }

  // ── appendRepresentation edge cases ───────────────────────────────

  @Test
  public void appendRepresentation_emptyString() {
    StringBuilder sb = new StringBuilder();
    state.appendRepresentation(sb, "");
    assertEquals("", sb.toString());
  }

  @Test
  public void appendRepresentation_nullValue() {
    StringBuilder sb = new StringBuilder();
    state.appendRepresentation(sb, null);
    assertEquals("null", sb.toString());
  }

  // ── listener firing count ─────────────────────────────────────────

  @Test
  public void multipleDistinctValues_eachFiresListener() {
    AtomicInteger count = new AtomicInteger();
    state.addValueListener(new State.ValueListener<String>() {
      @Override
      public void changing(State<String> s, String prev, String next) {}

      @Override
      public void changed(State<String> s, String prev, String next) {
        count.incrementAndGet();
      }
    });
    state.setValueTransactionlessly("a");
    state.setValueTransactionlessly("b");
    state.setValueTransactionlessly("c");
    assertEquals(3, count.get());
  }

  @Test
  public void sameValue_doesNotFireListener() {
    AtomicInteger count = new AtomicInteger();
    state.addValueListener(new State.ValueListener<String>() {
      @Override
      public void changing(State<String> s, String prev, String next) {}

      @Override
      public void changed(State<String> s, String prev, String next) {
        count.incrementAndGet();
      }
    });
    state.setValueTransactionlessly("initial");
    assertEquals(0, count.get());
  }

  // ── document sync after multiple changes ──────────────────────────

  @Test
  public void documentSync_afterMultipleChanges() throws BadLocationException {
    state.setValueTransactionlessly("first");
    state.setValueTransactionlessly("second");
    state.setValueTransactionlessly("third");
    Document doc = state.getSwingModel().getDocument();
    assertEquals("third", doc.getText(0, doc.getLength()));
  }

  // ── changeValueFromEdit with special chars ────────────────────────

  @Test
  public void changeValueFromEdit_specialChars() {
    state.changeValueFromEdit("<html>&amp;</html>");
    assertEquals("<html>&amp;</html>", state.getValue());
  }

  // ── textForBlankCondition ─────────────────────────────────────────

  @Test
  public void textForBlankCondition_setAndReset() {
    state.setTextForBlankCondition("hint1");
    assertEquals("hint1", state.getTextForBlankCondition());
    state.setTextForBlankCondition("hint2");
    assertEquals("hint2", state.getTextForBlankCondition());
  }

  @Test
  public void textForBlankCondition_setToNull() {
    state.setTextForBlankCondition("hint");
    state.setTextForBlankCondition(null);
    assertNull(state.getTextForBlankCondition());
  }

  // ── enabled state interaction ─────────────────────────────────────

  @Test
  public void setValueWhileDisabled_stillWorks() {
    state.setEnabled(false);
    state.setValueTransactionlessly("disabled-change");
    assertEquals("disabled-change", state.getValue());
  }

  @Test
  public void enabledToggle_doesNotAffectValue() {
    state.setEnabled(false);
    state.setEnabled(true);
    assertEquals("initial", state.getValue());
  }
}
