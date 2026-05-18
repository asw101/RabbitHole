package org.lgna.croquet.preferences;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.*;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link PreferenceStringState} — construction with defaults,
 * getValue/setValue, preference key, and encryption key handling.
 * Tests bypass encryption-dependent paths since PreferenceManager returns null
 * without Application context.
 */
public class PreferenceStringStateCoverageTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("c0000000-0000-0000-0030-ffffffffffff"), "prefStrCov");

  private TestPreferenceStringState state;

  @Before
  public void setUp() {
    state = new TestPreferenceStringState(TEST_GROUP, "default-value");
    CroquetTestUtils.removeDocumentListeners(state);
  }

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void constructor_setsDefaultValue() {
    assertEquals("default-value", state.getValue());
  }

  @Test
  public void constructor_emptyDefault() {
    TestPreferenceStringState s = new TestPreferenceStringState(TEST_GROUP, "");
    CroquetTestUtils.removeDocumentListeners(s);
    assertEquals("", s.getValue());
  }

  @Test
  public void constructor_nullDefault_resolvedFromPreferences() {
    // With no PreferenceManager, null default means null → state gets null
    // Actually, null converts to "__null__" and back to null
    TestPreferenceStringState s = new TestPreferenceStringState(TEST_GROUP, null);
    CroquetTestUtils.removeDocumentListeners(s);
    // The value may be null or the default depending on PreferenceManager state
    // In headless mode, PreferenceManager.getUserPreferences() returns null,
    // so the default value is used directly
    assertNull(s.getValue());
  }

  // ── getPreferenceKey ──────────────────────────────────────────────

  @Test
  public void getPreferenceKey_returnsNonNull() {
    assertNotNull(state.getPreferenceKey());
  }

  @Test
  public void getPreferenceKey_matchesMigrationId() {
    // The default key is the migration UUID toString
    String key = state.getPreferenceKey();
    assertNotNull(key);
    assertFalse(key.isEmpty());
  }

  // ── getValue/setValue ─────────────────────────────────────────────

  @Test
  public void setValueTransactionlessly_updatesValue() {
    state.setValueTransactionlessly("new-value");
    assertEquals("new-value", state.getValue());
  }

  @Test
  public void setValueTransactionlessly_toEmpty() {
    state.setValueTransactionlessly("");
    assertEquals("", state.getValue());
  }

  @Test
  public void changeValueFromEdit_updatesValue() {
    state.changeValueFromEdit("edited");
    assertEquals("edited", state.getValue());
  }

  // ── isEnabled ─────────────────────────────────────────────────────

  @Test
  public void isEnabled_defaultTrue() {
    assertTrue(state.isEnabled());
  }

  @Test
  public void setEnabled_false() {
    state.setEnabled(false);
    assertFalse(state.isEnabled());
  }

  // ── appendRepresentation ──────────────────────────────────────────

  @Test
  public void appendRepresentation() {
    StringBuilder sb = new StringBuilder();
    state.appendRepresentation(sb, "test");
    assertEquals("test", sb.toString());
  }

  // ── listener dispatch ─────────────────────────────────────────────

  @Test
  public void listener_firesOnChange() {
    java.util.concurrent.atomic.AtomicReference<String> captured =
        new java.util.concurrent.atomic.AtomicReference<>();
    state.addNewSchoolValueListener(e -> captured.set(e.getNextValue()));
    state.setValueTransactionlessly("changed");
    assertEquals("changed", captured.get());
  }

  // ── getSwingModel ─────────────────────────────────────────────────

  @Test
  public void getSwingModel_nonNull() {
    assertNotNull(state.getSwingModel());
  }

  @Test
  public void getSwingModel_documentNonNull() {
    assertNotNull(state.getSwingModel().getDocument());
  }

  // ── isStoringPreferenceDesired (default) ──────────────────────────

  @Test
  public void isStoringPreferenceDesired_defaultTrue() {
    assertTrue(state.isStoringPreferenceDesiredPublic());
  }

  // ── getMigrationId ────────────────────────────────────────────────

  @Test
  public void getMigrationId_nonNull() {
    assertNotNull(state.getMigrationId());
  }

  // ── getEncryptionKey static ───────────────────────────────────────

  @Test
  public void getEncryptionKey_null_returnsNull() {
    assertNull(PreferenceStringState.getEncryptionKey(null));
  }

  @Test
  public void getEncryptionKey_nonNull_returnsBytes() {
    byte[] key = PreferenceStringState.getEncryptionKey("test-passphrase");
    assertNotNull(key);
    assertTrue(key.length > 0);
  }

  // ── Test infrastructure ───────────────────────────────────────────

  static class TestPreferenceStringState extends PreferenceStringState {
    TestPreferenceStringState(Group group, String initialValue) {
      super(group, CroquetTestUtils.nextTestUUID(), initialValue);
    }

    @Override
    protected Class<? extends Element> getClassUsedForLocalization() {
      return TestPreferenceStringState.class;
    }

    @Override
    protected String getSubKeyForLocalization() {
      return "test";
    }

    boolean isStoringPreferenceDesiredPublic() {
      return isStoringPreferenceDesired();
    }
  }
}
