package org.lgna.croquet;

import edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder;
import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import org.junit.Before;
import org.junit.Test;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Extended tests for {@link StringState} — encode/decode, setSwingValue,
 * getSwingValue, textForBlankCondition, and document synchronization
 * edge cases beyond what StringStateTest covers.
 */
public class StringStateExtendedTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("00000000-0000-0000-ee01-ffffffffffff"), "strExtTest");

  private TestStringState state;

  @Before
  public void setUp() {
    state = new TestStringState(TEST_GROUP, "initial");
    CroquetTestUtils.removeDocumentListeners(state);
  }

  // ── encode/decode round-trip ──────────────────────────────────────

  @Test
  public void encodeValue_decodesBack() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    state.encodeValue(encoder, "hello world");
    BinaryDecoder decoder = encoder.createDecoder();
    String decoded = state.decodeValue(decoder);
    assertEquals("hello world", decoded);
  }

  @Test
  public void encodeValue_emptyString() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    state.encodeValue(encoder, "");
    BinaryDecoder decoder = encoder.createDecoder();
    assertEquals("", state.decodeValue(decoder));
  }

  @Test
  public void encodeValue_specialChars() {
    String special = "hello\nworld\ttab";
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    state.encodeValue(encoder, special);
    BinaryDecoder decoder = encoder.createDecoder();
    assertEquals(special, state.decodeValue(decoder));
  }

  @Test
  public void encodeValue_multipleValues() {
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    state.encodeValue(encoder, "first");
    state.encodeValue(encoder, "second");
    state.encodeValue(encoder, "third");
    BinaryDecoder decoder = encoder.createDecoder();
    assertEquals("first", state.decodeValue(decoder));
    assertEquals("second", state.decodeValue(decoder));
    assertEquals("third", state.decodeValue(decoder));
  }

  // ── setSwingValue via setValueTransactionlessly ───────────────────

  @Test
  public void setValueTransactionlessly_syncsDocumentContent() throws BadLocationException {
    state.setValueTransactionlessly("new value");
    Document doc = state.getSwingModel().getDocument();
    assertEquals("new value", doc.getText(0, doc.getLength()));
  }

  @Test
  public void setValueTransactionlessly_overwrite_updatesDocument() throws BadLocationException {
    state.setValueTransactionlessly("first");
    state.setValueTransactionlessly("second");
    Document doc = state.getSwingModel().getDocument();
    assertEquals("second", doc.getText(0, doc.getLength()));
  }

  @Test
  public void setValueTransactionlessly_toEmpty_clearsDocument() throws BadLocationException {
    state.setValueTransactionlessly("");
    Document doc = state.getSwingModel().getDocument();
    assertEquals("", doc.getText(0, doc.getLength()));
  }

  @Test
  public void setValueTransactionlessly_longString() throws BadLocationException {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 500; i++) {
      sb.append("x");
    }
    String longStr = sb.toString();
    state.setValueTransactionlessly(longStr);
    Document doc = state.getSwingModel().getDocument();
    assertEquals(longStr, doc.getText(0, doc.getLength()));
  }

  // ── getSwingValue ─────────────────────────────────────────────────

  @Test
  public void getValue_matchesConstructorArg() {
    assertEquals("initial", state.getValue());
  }

  @Test
  public void getValue_afterChange_matchesNew() {
    state.setValueTransactionlessly("changed");
    assertEquals("changed", state.getValue());
  }

  // ── appendRepresentation ──────────────────────────────────────────

  @Test
  public void appendRepresentation_appendsExactValue() {
    StringBuilder sb = new StringBuilder();
    state.appendRepresentation(sb, "foobar");
    assertEquals("foobar", sb.toString());
  }

  @Test
  public void appendRepresentation_emptyString() {
    StringBuilder sb = new StringBuilder();
    state.appendRepresentation(sb, "");
    assertEquals("", sb.toString());
  }

  @Test
  public void appendRepresentation_null() {
    StringBuilder sb = new StringBuilder();
    state.appendRepresentation(sb, null);
    assertEquals("null", sb.toString());
  }

  // ── textForBlankCondition ─────────────────────────────────────────

  @Test
  public void textForBlankCondition_setAndGet() {
    state.setTextForBlankCondition("placeholder");
    assertEquals("placeholder", state.getTextForBlankCondition());
  }

  @Test
  public void textForBlankCondition_setNull() {
    state.setTextForBlankCondition("something");
    state.setTextForBlankCondition(null);
    assertNull(state.getTextForBlankCondition());
  }

  @Test
  public void textForBlankCondition_setEmpty() {
    state.setTextForBlankCondition("");
    assertEquals("", state.getTextForBlankCondition());
  }

  // ── isEnabled edge cases ──────────────────────────────────────────

  @Test
  public void setEnabled_sameValue_noException() {
    state.setEnabled(true);
    state.setEnabled(true);
    assertTrue(state.isEnabled());
  }

  @Test
  public void setEnabled_falseToFalse_noException() {
    state.setEnabled(false);
    state.setEnabled(false);
    assertFalse(state.isEnabled());
  }

  // ── changeValueFromEdit ───────────────────────────────────────────

  @Test
  public void changeValueFromEdit_syncsDocument() throws BadLocationException {
    state.changeValueFromEdit("edited");
    Document doc = state.getSwingModel().getDocument();
    assertEquals("edited", doc.getText(0, doc.getLength()));
  }

  // ── getPotentialPrepModelPaths ─────────────────────────────────────

  @Test
  public void getPotentialPrepModelPaths_returnsEmptyForAnyEdit() {
    assertTrue(state.getPotentialPrepModelPaths(null).isEmpty());
  }

  // ── getSwingModel stability ───────────────────────────────────────

  @Test
  public void getSwingModel_sameInstanceAcrossCalls() {
    StringState.SwingModel m1 = state.getSwingModel();
    StringState.SwingModel m2 = state.getSwingModel();
    assertSame(m1, m2);
  }

  @Test
  public void getSwingModel_documentSameInstance() {
    Document d1 = state.getSwingModel().getDocument();
    Document d2 = state.getSwingModel().getDocument();
    assertSame(d1, d2);
  }

  // ── Construct with empty ──────────────────────────────────────────

  @Test
  public void constructor_emptyString_works() throws BadLocationException {
    TestStringState empty = new TestStringState(TEST_GROUP, "");
    CroquetTestUtils.removeDocumentListeners(empty);
    assertEquals("", empty.getValue());
    Document doc = empty.getSwingModel().getDocument();
    assertEquals("", doc.getText(0, doc.getLength()));
  }

  @Test
  public void constructor_whitespace_preserves() {
    TestStringState ws = new TestStringState(TEST_GROUP, "  \t  ");
    CroquetTestUtils.removeDocumentListeners(ws);
    assertEquals("  \t  ", ws.getValue());
  }

  // ── Value listener with encode/decode ─────────────────────────────

  @Test
  public void valueListener_firesWithCorrectPrevAndNext() {
    java.util.concurrent.atomic.AtomicReference<String> prevRef = new java.util.concurrent.atomic.AtomicReference<>();
    java.util.concurrent.atomic.AtomicReference<String> nextRef = new java.util.concurrent.atomic.AtomicReference<>();
    state.addValueListener(new State.ValueListener<String>() {
      @Override
      public void changing(State<String> s, String prev, String next) {}

      @Override
      public void changed(State<String> s, String prev, String next) {
        prevRef.set(prev);
        nextRef.set(next);
      }
    });
    state.setValueTransactionlessly("world");
    assertEquals("initial", prevRef.get());
    assertEquals("world", nextRef.get());
  }

  // ── Test infrastructure ───────────────────────────────────────────

  static class TestStringState extends StringState {
    TestStringState(Group group, String initialValue) {
      super(group, CroquetTestUtils.nextTestUUID(), initialValue);
    }

    @Override
    protected Class<? extends Element> getClassUsedForLocalization() {
      return TestStringState.class;
    }

    @Override
    protected String getSubKeyForLocalization() {
      return "test";
    }
  }
}
