package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link StringState} — empty/null/unicode handling,
 * listener dispatch, and Swing document sync edge cases.
 */
public class StringStateCoverageTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("c0000000-0000-0000-000f-000000000001"), "strCov");

  private static class TestStringState extends StringState {
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

  private TestStringState state;

  @Before
  public void setUp() {
    state = new TestStringState(TEST_GROUP, "initial");
    CroquetTestUtils.removeDocumentListeners(state);
  }

  // ── Empty / null ──────────────────────────────────────────────────

  @Test
  public void emptyString_initial() {
    TestStringState s = new TestStringState(TEST_GROUP, "");
    CroquetTestUtils.removeDocumentListeners(s);
    assertEquals("", s.getValue());
  }

  @Test
  public void nullString_initial() {
    TestStringState s = new TestStringState(TEST_GROUP, null);
    CroquetTestUtils.removeDocumentListeners(s);
    assertNull(s.getValue());
  }

  @Test
  public void setToEmpty() {
    state.setValueTransactionlessly("");
    assertEquals("", state.getValue());
  }

  @Test
  public void setToNull() {
    state.setValueTransactionlessly(null);
    assertNull(state.getValue());
  }

  @Test
  public void setFromNullToValue() {
    TestStringState s = new TestStringState(TEST_GROUP, null);
    CroquetTestUtils.removeDocumentListeners(s);
    s.setValueTransactionlessly("hello");
    assertEquals("hello", s.getValue());
  }

  // ── Unicode ───────────────────────────────────────────────────────

  @Test
  public void unicode_cjk() {
    state.setValueTransactionlessly("日本語テスト");
    assertEquals("日本語テスト", state.getValue());
  }

  @Test
  public void unicode_emoji() {
    state.setValueTransactionlessly("🎉🚀");
    assertEquals("🎉🚀", state.getValue());
  }

  @Test
  public void unicode_arabic() {
    state.setValueTransactionlessly("مرحبا");
    assertEquals("مرحبا", state.getValue());
  }

  // ── Long strings ──────────────────────────────────────────────────

  @Test
  public void longString_1000chars() {
    String longStr = "x".repeat(1000);
    state.setValueTransactionlessly(longStr);
    assertEquals(longStr, state.getValue());
  }

  // ── Whitespace ────────────────────────────────────────────────────

  @Test
  public void whitespaceOnly() {
    state.setValueTransactionlessly("   ");
    assertEquals("   ", state.getValue());
  }

  @Test
  public void newlines() {
    state.setValueTransactionlessly("line1\nline2");
    assertEquals("line1\nline2", state.getValue());
  }

  @Test
  public void tabs() {
    state.setValueTransactionlessly("col1\tcol2");
    assertEquals("col1\tcol2", state.getValue());
  }

  // ── Listener ──────────────────────────────────────────────────────

  @Test
  public void newSchoolListener_firesOnChange() {
    AtomicReference<String> captured = new AtomicReference<>();
    state.addNewSchoolValueListener(e -> captured.set(e.getNextValue()));

    state.setValueTransactionlessly("changed");
    assertEquals("changed", captured.get());
  }

  @Test
  public void newSchoolListener_prevValue() {
    AtomicReference<String> prevCapture = new AtomicReference<>();
    state.addNewSchoolValueListener(e -> prevCapture.set(e.getPreviousValue()));

    state.setValueTransactionlessly("changed");
    assertEquals("initial", prevCapture.get());
  }

  // ── Round-trip ────────────────────────────────────────────────────

  @Test
  public void roundTrip_multipleChanges() {
    state.setValueTransactionlessly("a");
    state.setValueTransactionlessly("b");
    state.setValueTransactionlessly("c");
    assertEquals("c", state.getValue());
  }

  @Test
  public void roundTrip_backToOriginal() {
    state.setValueTransactionlessly("changed");
    state.setValueTransactionlessly("initial");
    assertEquals("initial", state.getValue());
  }

  // ── Hierarchy ─────────────────────────────────────────────────────

  @Test
  public void class_extendsState() {
    assertTrue(State.class.isAssignableFrom(StringState.class));
  }

  @Test
  public void class_isAbstract() {
    assertTrue(java.lang.reflect.Modifier.isAbstract(StringState.class.getModifiers()));
  }
}
