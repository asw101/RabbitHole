package org.lgna.croquet.preferences;

import org.lgna.croquet.BooleanState;
import org.lgna.croquet.CroquetTestUtils;
import org.lgna.croquet.Element;
import org.lgna.croquet.Group;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;
import java.util.prefs.Preferences;

import static org.junit.Assert.*;

/**
 * Tests for {@link PreferenceBooleanState} — a BooleanState that persists
 * its value via java.util.prefs.Preferences.
 *
 * <p>ItemListeners are removed in setUp to avoid the
 * Application.getActiveInstance() dependency chain.</p>
 */
public class PreferenceBooleanStateTest {

  private static final Group TEST_GROUP =
      Group.getInstance(UUID.fromString("00000000-0000-0000-000b-ffffffffffff"), "prefBoolTest");

  private TestPreferenceBooleanState stateTrue;
  private TestPreferenceBooleanState stateFalse;

  @Before
  public void setUp() {
    stateTrue = new TestPreferenceBooleanState(TEST_GROUP, true, "testPrefTrue");
    CroquetTestUtils.removeItemListeners(stateTrue);
    stateFalse = new TestPreferenceBooleanState(TEST_GROUP, false, "testPrefFalse");
    CroquetTestUtils.removeItemListeners(stateFalse);
  }

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void constructor_withTrue_setsInitialTrue() {
    assertTrue(stateTrue.getValue());
  }

  @Test
  public void constructor_withFalse_setsInitialFalse() {
    assertFalse(stateFalse.getValue());
  }

  @Test
  public void constructor_returnsNonNull() {
    assertNotNull(stateTrue);
  }

  // ── BooleanState behavior inheritance ─────────────────────────────

  @Test
  public void getImp_returnsNonNull() {
    assertNotNull(stateTrue.getImp());
  }

  @Test
  public void getSwingModel_returnsNonNull() {
    assertNotNull(stateTrue.getImp().getSwingModel());
  }

  @Test
  public void getButtonModel_returnsNonNull() {
    assertNotNull(stateTrue.getImp().getSwingModel().getButtonModel());
  }

  @Test
  public void buttonModel_reflectsInitialTrue() {
    assertTrue(stateTrue.getImp().getSwingModel().getButtonModel().isSelected());
  }

  @Test
  public void buttonModel_reflectsInitialFalse() {
    assertFalse(stateFalse.getImp().getSwingModel().getButtonModel().isSelected());
  }

  // ── Value mutation ────────────────────────────────────────────────

  @Test
  public void setValueTransactionlessly_toFalse() {
    stateTrue.setValueTransactionlessly(false);
    assertFalse(stateTrue.getValue());
  }

  @Test
  public void setValueTransactionlessly_toTrue() {
    stateFalse.setValueTransactionlessly(true);
    assertTrue(stateFalse.getValue());
  }

  @Test
  public void setValueTransactionlessly_syncsButtonModel() {
    stateTrue.setValueTransactionlessly(false);
    assertFalse(stateTrue.getImp().getSwingModel().getButtonModel().isSelected());
  }

  // ── isEnabled ─────────────────────────────────────────────────────

  @Test
  public void isEnabled_defaultTrue() {
    assertTrue(stateTrue.isEnabled());
  }

  @Test
  public void setEnabled_false() {
    stateTrue.setEnabled(false);
    assertFalse(stateTrue.isEnabled());
  }

  // ── getMigrationId ────────────────────────────────────────────────

  @Test
  public void getMigrationId_returnsNonNull() {
    assertNotNull(stateTrue.getMigrationId());
  }

  // ── preserveAll ───────────────────────────────────────────────────

  @Test
  public void preserveAll_doesNotThrow() {
    Preferences prefs = Preferences.userNodeForPackage(PreferenceBooleanStateTest.class);
    PreferenceBooleanState.preserveAll(prefs);
    // Clean up test preferences
    try {
      prefs.removeNode();
    } catch (Exception e) {
      // Ignore cleanup failures
    }
  }

  // ── Constructor with default preferenceKey ────────────────────────

  @Test
  public void constructor_withoutPreferenceKey_usesUUID() {
    TestPreferenceBooleanStateDefaultKey s =
        new TestPreferenceBooleanStateDefaultKey(TEST_GROUP, true);
    CroquetTestUtils.removeItemListeners(s);
    assertTrue(s.getValue());
  }

  // ── initializeIfNecessary ─────────────────────────────────────────

  @Test
  public void initializeIfNecessary_doesNotThrow() {
    stateTrue.initializeIfNecessary();
  }

  // ── appendRepresentation ──────────────────────────────────────────

  @Test
  public void appendRepresentation_appendsBooleanValue() {
    StringBuilder sb = new StringBuilder();
    stateTrue.appendRepresentation(sb, true);
    assertEquals("true", sb.toString());
  }

  @Test
  public void appendRepresentation_appendsFalse() {
    StringBuilder sb = new StringBuilder();
    stateFalse.appendRepresentation(sb, false);
    assertEquals("false", sb.toString());
  }

  // ── Test infrastructure ───────────────────────────────────────────

  static class TestPreferenceBooleanState extends PreferenceBooleanState {
    TestPreferenceBooleanState(Group group, boolean initialValue, String prefKey) {
      super(group, UUID.randomUUID(), initialValue, prefKey);
    }

    @Override
    protected Class<? extends Element> getClassUsedForLocalization() {
      return TestPreferenceBooleanState.class;
    }

    @Override
    protected String getSubKeyForLocalization() {
      return "test";
    }
  }

  static class TestPreferenceBooleanStateDefaultKey extends PreferenceBooleanState {
    TestPreferenceBooleanStateDefaultKey(Group group, boolean initialValue) {
      super(group, UUID.randomUUID(), initialValue);
    }

    @Override
    protected Class<? extends Element> getClassUsedForLocalization() {
      return TestPreferenceBooleanStateDefaultKey.class;
    }

    @Override
    protected String getSubKeyForLocalization() {
      return "testDefault";
    }
  }
}
