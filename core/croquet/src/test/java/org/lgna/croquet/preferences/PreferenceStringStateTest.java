package org.lgna.croquet.preferences;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.CroquetTestUtils;
import org.lgna.croquet.Element;
import org.lgna.croquet.Group;

import java.util.UUID;
import java.util.prefs.Preferences;

import static org.junit.Assert.*;

public class PreferenceStringStateTest {

  private static final Group GROUP = Group.getInstance(
      UUID.fromString("00000000-0000-0000-7750-000000000201"),
      "preferenceStringTests");

  @Before
  public void setUp() throws Exception {
    PreferenceManagerTest.PreferenceTestSupport.clearActiveApplication();
    PreferenceManagerTest.PreferenceTestSupport.removePreferenceNode();
  }

  @After
  public void tearDown() throws Exception {
    PreferenceManagerTest.PreferenceTestSupport.clearActiveApplication();
    PreferenceManagerTest.PreferenceTestSupport.removePreferenceNode();
  }

  @Test
  public void explicitPreferenceKeyIsReturned() {
    TestPreferenceStringState state = new TestPreferenceStringState("initial", "explicit-key", null);
    CroquetTestUtils.removeDocumentListeners(state);

    assertEquals("explicit-key", state.getPreferenceKey());
  }

  @Test
  public void defaultPreferenceKeyUsesMigrationId() {
    TestPreferenceStringStateDefaultKey state = new TestPreferenceStringStateDefaultKey("initial");
    CroquetTestUtils.removeDocumentListeners(state);

    assertEquals(state.getMigrationId().toString(), state.getPreferenceKey());
  }

  @Test
  public void getEncryptionKeyReturnsNullForNullString() {
    assertNull(PreferenceStringState.getEncryptionKey(null));
  }

  @Test
  public void getEncryptionKeyReturnsUtf8Bytes() {
    assertArrayEquals("secret".getBytes(java.nio.charset.StandardCharsets.UTF_8),
        PreferenceStringState.getEncryptionKey("secret"));
  }

  @Test
  public void constructorUsesInitialValueWithoutApplication() {
    TestPreferenceStringState state = new TestPreferenceStringState("initial", "key", null);
    CroquetTestUtils.removeDocumentListeners(state);

    assertEquals("initial", state.getValue());
  }

  @Test
  public void preserveAllStoresPlainTextValue() throws Exception {
    PreferenceManagerTest.PreferenceTestSupport.activateApplication();
    TestPreferenceStringState state = new TestPreferenceStringState("initial", "plain-key", null);
    CroquetTestUtils.removeDocumentListeners(state);
    state.setValueTransactionlessly("updated");
    Preferences prefs = PreferenceManager.getUserPreferences();

    PreferenceStringState.preserveAll(prefs);

    assertEquals("updated", prefs.get("plain-key", null));
  }

  @Test
  public void preserveAllRemovesValueWhenStorageIsNotDesired() throws Exception {
    PreferenceManagerTest.PreferenceTestSupport.activateApplication();
    Preferences prefs = PreferenceManager.getUserPreferences();
    prefs.put("remove-key", "present");
    NoStorePreferenceStringState state = new NoStorePreferenceStringState("initial", "remove-key");
    CroquetTestUtils.removeDocumentListeners(state);

    PreferenceStringState.preserveAll(prefs);

    assertNull(prefs.get("remove-key", null));
  }

  @Test
  public void nullValueRoundTripsThroughPreferences() throws Exception {
    PreferenceManagerTest.PreferenceTestSupport.activateApplication();
    Preferences prefs = PreferenceManager.getUserPreferences();
    TestPreferenceStringState state = new TestPreferenceStringState(null, "null-key", null);
    CroquetTestUtils.removeDocumentListeners(state);

    PreferenceStringState.preserveAll(prefs);
    TestPreferenceStringState reloaded = new TestPreferenceStringState("fallback", "null-key", null);
    CroquetTestUtils.removeDocumentListeners(reloaded);

    assertNull(reloaded.getValue());
  }

  @Test
  public void invalidEncryptedPreferenceFallsBackToDefault() throws Exception {
    PreferenceManagerTest.PreferenceTestSupport.activateApplication();
    Preferences prefs = PreferenceManager.getUserPreferences();
    prefs.put("encrypted-key", "not-valid-base64");
    TestPreferenceStringState state = new TestPreferenceStringState(
        "fallback", "encrypted-key", PreferenceStringState.getEncryptionKey("12345678"));
    CroquetTestUtils.removeDocumentListeners(state);

    assertEquals("fallback", state.getValue());
  }

  static class TestPreferenceStringState extends PreferenceStringState {
    TestPreferenceStringState(String initialValue, String preferenceKey, byte[] encryptionKey) {
      super(GROUP, CroquetTestUtils.nextTestUUID(), initialValue, preferenceKey, encryptionKey);
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

  static class TestPreferenceStringStateDefaultKey extends PreferenceStringState {
    TestPreferenceStringStateDefaultKey(String initialValue) {
      super(GROUP, CroquetTestUtils.nextTestUUID(), initialValue);
    }

    @Override
    protected Class<? extends Element> getClassUsedForLocalization() {
      return TestPreferenceStringStateDefaultKey.class;
    }

    @Override
    protected String getSubKeyForLocalization() {
      return "defaultKey";
    }
  }

  static class NoStorePreferenceStringState extends TestPreferenceStringState {
    NoStorePreferenceStringState(String initialValue, String preferenceKey) {
      super(initialValue, preferenceKey, null);
    }

    @Override
    protected boolean isStoringPreferenceDesired() {
      return false;
    }
  }
}
