package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.preferences.PreferenceStringState;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link PreferenceStringState} — construction
 * with default values, preference key behavior, and getValue/setValue
 * using the simplest constructor that avoids encryption dependencies.
 */
public class PreferenceStringStateCoverageTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("c0000000-0000-0000-0009-000000000001"), "prefStrCov");

  private static class TestPreferenceStringState extends PreferenceStringState {
    TestPreferenceStringState(Group group, UUID id, String initialValue) {
      super(group, id, initialValue);
    }

    @Override
    protected Class<? extends Element> getClassUsedForLocalization() {
      return TestPreferenceStringState.class;
    }

    @Override
    protected String getSubKeyForLocalization() {
      return "test";
    }
  }

  private UUID testId;
  private TestPreferenceStringState state;

  @Before
  public void setUp() {
    testId = CroquetTestUtils.nextTestUUID();
    state = new TestPreferenceStringState(TEST_GROUP, testId, "default");
    CroquetTestUtils.removeDocumentListeners(state);
  }

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void constructor_setsDefaultValue() {
    assertEquals("default", state.getValue());
  }

  @Test
  public void constructor_nullDefault() {
    TestPreferenceStringState s = new TestPreferenceStringState(TEST_GROUP, CroquetTestUtils.nextTestUUID(), null);
    CroquetTestUtils.removeDocumentListeners(s);
    assertNull(s.getValue());
  }

  @Test
  public void constructor_emptyDefault() {
    TestPreferenceStringState s = new TestPreferenceStringState(TEST_GROUP, CroquetTestUtils.nextTestUUID(), "");
    CroquetTestUtils.removeDocumentListeners(s);
    assertEquals("", s.getValue());
  }

  // ── Preference key ────────────────────────────────────────────────

  @Test
  public void getPreferenceKey_matchesUUID() {
    assertEquals(testId.toString(), state.getPreferenceKey());
  }

  // ── Value manipulation ────────────────────────────────────────────

  @Test
  public void setValueTransactionlessly_changes() {
    state.setValueTransactionlessly("updated");
    assertEquals("updated", state.getValue());
  }

  @Test
  public void setValueTransactionlessly_toNull() {
    state.setValueTransactionlessly(null);
    assertNull(state.getValue());
  }

  @Test
  public void setValueTransactionlessly_roundTrip() {
    state.setValueTransactionlessly("temp");
    state.setValueTransactionlessly("default");
    assertEquals("default", state.getValue());
  }

  // ── Hierarchy ─────────────────────────────────────────────────────

  @Test
  public void class_extendsStringState() {
    assertTrue(StringState.class.isAssignableFrom(PreferenceStringState.class));
  }

  @Test
  public void class_extendsState() {
    assertTrue(State.class.isAssignableFrom(PreferenceStringState.class));
  }

  @Test
  public void class_isAbstract() {
    assertTrue(java.lang.reflect.Modifier.isAbstract(PreferenceStringState.class.getModifiers()));
  }

  // ── Encryption key helper ─────────────────────────────────────────

  @Test
  public void getEncryptionKey_null_returnsNull() throws Exception {
    java.lang.reflect.Method m = PreferenceStringState.class.getDeclaredMethod("getEncryptionKey", String.class);
    m.setAccessible(true);
    assertNull(m.invoke(null, (String) null));
  }

  @Test
  public void getEncryptionKey_nonNull_returnsBytes() throws Exception {
    java.lang.reflect.Method m = PreferenceStringState.class.getDeclaredMethod("getEncryptionKey", String.class);
    m.setAccessible(true);
    byte[] result = (byte[]) m.invoke(null, "test");
    assertNotNull(result);
    assertTrue(result.length > 0);
  }
}
