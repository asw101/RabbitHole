package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;

import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Tests for {@link StringState} — state wrapper around a PlainDocument
 * that synchronizes a String value with a Swing Document model.
 *
 * <p>The DocumentListener is removed in setUp to avoid the
 * Application.getActiveInstance() dependency chain that fires
 * when the document is modified from Swing.</p>
 */
public class StringStateTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("00000000-0000-0000-0001-ffffffffffff"), "strTest");

  private TestStringState state;

  @Before
  public void setUp() {
    state = new TestStringState(TEST_GROUP, "hello");
    removeDocumentListeners(state);
  }

  private static void removeDocumentListeners(TestStringState s) {
    Document doc = s.getSwingModel().getDocument();
    for (DocumentListener dl : ((javax.swing.text.AbstractDocument) doc).getDocumentListeners()) {
      doc.removeDocumentListener(dl);
    }
  }

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void constructor_setsInitialValue() {
    assertEquals("hello", state.getValue());
  }

  @Test
  public void constructor_syncsDocumentContent() throws BadLocationException {
    Document doc = state.getSwingModel().getDocument();
    String text = doc.getText(0, doc.getLength());
    assertEquals("hello", text);
  }

  @Test
  public void constructor_emptyString() {
    TestStringState empty = new TestStringState(TEST_GROUP, "");
    removeDocumentListeners(empty);
    assertEquals("", empty.getValue());
  }

  // ── setValueTransactionlessly ─────────────────────────────────────

  @Test
  public void setValueTransactionlessly_updatesValue() {
    state.setValueTransactionlessly("world");
    assertEquals("world", state.getValue());
  }

  @Test
  public void setValueTransactionlessly_syncsDocument() throws BadLocationException {
    state.setValueTransactionlessly("world");
    Document doc = state.getSwingModel().getDocument();
    assertEquals("world", doc.getText(0, doc.getLength()));
  }

  @Test
  public void setValueTransactionlessly_toEmpty() {
    state.setValueTransactionlessly("");
    assertEquals("", state.getValue());
  }

  // ── getSwingModel ─────────────────────────────────────────────────

  @Test
  public void getSwingModel_returnsNonNull() {
    assertNotNull(state.getSwingModel());
  }

  @Test
  public void getSwingModel_documentIsNonNull() {
    assertNotNull(state.getSwingModel().getDocument());
  }

  // ── isEnabled / setEnabled ────────────────────────────────────────

  @Test
  public void isEnabled_defaultTrue() {
    assertTrue(state.isEnabled());
  }

  @Test
  public void setEnabled_false_updatesState() {
    state.setEnabled(false);
    assertFalse(state.isEnabled());
  }

  @Test
  public void setEnabled_true_afterFalse_restores() {
    state.setEnabled(false);
    state.setEnabled(true);
    assertTrue(state.isEnabled());
  }

  @Test
  public void setEnabled_sameValue_noEffect() {
    state.setEnabled(true);
    assertTrue(state.isEnabled());
  }

  // ── appendRepresentation ──────────────────────────────────────────

  @Test
  public void appendRepresentation_appendsValue() {
    StringBuilder sb = new StringBuilder();
    state.appendRepresentation(sb, "test");
    assertEquals("test", sb.toString());
  }

  // ── getPotentialPrepModelPaths ─────────────────────────────────────

  @Test
  public void getPotentialPrepModelPaths_returnsEmptyList() {
    assertTrue(state.getPotentialPrepModelPaths(null).isEmpty());
  }

  // ── Old-school value listener ─────────────────────────────────────

  @Test
  public void valueListener_firesOnChange() {
    AtomicReference<String> prevRef = new AtomicReference<>();
    AtomicReference<String> nextRef = new AtomicReference<>();
    State.ValueListener<String> listener = new State.ValueListener<String>() {
      @Override
      public void changing(State<String> s, String prev, String next) {}

      @Override
      public void changed(State<String> s, String prev, String next) {
        prevRef.set(prev);
        nextRef.set(next);
      }
    };
    state.addValueListener(listener);
    state.setValueTransactionlessly("world");
    assertEquals("hello", prevRef.get());
    assertEquals("world", nextRef.get());
  }

  @Test
  public void valueListener_removedDoesNotFire() {
    AtomicReference<String> captured = new AtomicReference<>();
    State.ValueListener<String> listener = new State.ValueListener<String>() {
      @Override
      public void changing(State<String> s, String prev, String next) {}

      @Override
      public void changed(State<String> s, String prev, String next) {
        captured.set(next);
      }
    };
    state.addValueListener(listener);
    state.removeValueListener(listener);
    state.setValueTransactionlessly("world");
    assertNull(captured.get());
  }

  // ── New-school value listener ─────────────────────────────────────

  @Test
  public void newSchoolListener_firesOnChange() {
    AtomicReference<String> captured = new AtomicReference<>();
    org.lgna.croquet.event.ValueListener<String> listener = e -> captured.set(e.getNextValue());
    state.addNewSchoolValueListener(listener);
    state.setValueTransactionlessly("world");
    assertEquals("world", captured.get());
  }

  @Test
  public void newSchoolListener_removedDoesNotFire() {
    AtomicReference<String> captured = new AtomicReference<>();
    org.lgna.croquet.event.ValueListener<String> listener = e -> captured.set(e.getNextValue());
    state.addNewSchoolValueListener(listener);
    state.removeNewSchoolValueListener(listener);
    state.setValueTransactionlessly("world");
    assertNull(captured.get());
  }

  // ── changeValueFromEdit ───────────────────────────────────────────

  @Test
  public void changeValueFromEdit_updatesValue() {
    state.changeValueFromEdit("edited");
    assertEquals("edited", state.getValue());
  }

  // ── textForBlankCondition ─────────────────────────────────────────

  @Test
  public void textForBlankCondition_initiallyNull() {
    assertNull(state.getTextForBlankCondition());
  }

  @Test
  public void setTextForBlankCondition_updatesValue() {
    state.setTextForBlankCondition("Enter text...");
    assertEquals("Enter text...", state.getTextForBlankCondition());
  }

  // ── Multiple value changes ────────────────────────────────────────

  @Test
  public void multipleChanges_lastValueSticks() {
    state.setValueTransactionlessly("a");
    state.setValueTransactionlessly("b");
    state.setValueTransactionlessly("c");
    assertEquals("c", state.getValue());
  }

  @Test
  public void sameValueChange_doesNotFireListener() {
    AtomicReference<String> captured = new AtomicReference<>();
    state.addValueListener(new State.ValueListener<String>() {
      @Override
      public void changing(State<String> s, String prev, String next) {}

      @Override
      public void changed(State<String> s, String prev, String next) {
        captured.set(next);
      }
    });
    // Set to same value — should not fire since previousValue == nextValue
    state.setValueTransactionlessly("hello");
    assertNull(captured.get());
  }

  // ── Test infrastructure ───────────────────────────────────────────

  static class TestStringState extends StringState {
    TestStringState(Group group, String initialValue) {
      super(group, UUID.randomUUID(), initialValue);
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
