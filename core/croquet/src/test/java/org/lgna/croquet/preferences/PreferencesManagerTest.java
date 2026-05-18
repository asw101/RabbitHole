package org.lgna.croquet.preferences;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.prefs.Preferences;

import static org.junit.Assert.*;

public class PreferencesManagerTest {

  private PreferenceManagerTest.PreferenceTestSupport.PreferenceTestApplication application;
  private PreferencesManager manager;
  private Preferences prefs;

  @Before
  public void setUp() throws Exception {
    application = PreferenceManagerTest.PreferenceTestSupport.activateApplication();
    java.lang.reflect.Method method = org.lgna.croquet.Application.class.getDeclaredMethod("getPreferencesManager");
    method.setAccessible(true);
    manager = (PreferencesManager) method.invoke(application);
    prefs = Preferences.userNodeForPackage(application.getClass());
  }

  @After
  public void tearDown() throws Exception {
    System.clearProperty(PreferencesManager.ORG_ALICE_CLEAR_ALL_PREFERENCES);
    PreferenceManagerTest.PreferenceTestSupport.clearActiveApplication();
    PreferenceManagerTest.PreferenceTestSupport.removePreferenceNode();
  }

  @Test
  public void getValueReturnsDefaultWhenPropertyMissing() {
    assertEquals("fallback", manager.getValue("missing", "fallback"));
  }

  @Test
  public void setValuePersistsProperty() {
    manager.setValue("name", "value");
    assertEquals("value", manager.getValue("name", "fallback"));
  }

  @Test
  public void getUserDirectoryCreatesDefaultLeafDirectory() {
    String userDirectoryKey = "5f80de2f-5119-4131-96d0-c0b80919a589";
    File parent = new File("target/test-preferences/user-root").getAbsoluteFile();
    prefs.put(userDirectoryKey, parent.toURI().toString());

    File dir = manager.getUserDirectory("leaf-key", "leafA");

    assertTrue(dir.exists());
    assertTrue(dir.getAbsolutePath().contains("leafA"));
  }

  @Test
  public void getUserDirectoryUsesStoredAbsolutePath() {
    File expected = new File("target/test-preferences/absolute-leaf").getAbsoluteFile();
    manager.setValue("custom-path", expected.getAbsolutePath());

    File dir = manager.getUserDirectory("custom-path", "ignored");

    assertEquals(expected.getAbsolutePath(), dir.getAbsolutePath());
  }

  @Test
  public void getUserDirectoryUnderstandsUriPath() {
    File expected = new File("target/test-preferences/uri-leaf").getAbsoluteFile();
    manager.setValue("custom-uri", expected.toURI().toString());

    File dir = manager.getUserDirectory("custom-uri", "ignored");

    assertEquals(expected.getAbsolutePath(), dir.getAbsolutePath());
  }

  @Test
  public void getUserDirectorySubstitutesUserDocumentsPlaceholder() {
    String userDirectoryKey = "5f80de2f-5119-4131-96d0-c0b80919a589";
    File parent = new File("target/test-preferences/placeholder-root").getAbsoluteFile();
    prefs.put(userDirectoryKey, parent.toURI().toString());
    manager.setValue("placeholder-path", "${user_application_documents}/child");

    File dir = manager.getUserDirectory("placeholder-path", "ignored");

    assertEquals(new File(parent, "child").getAbsolutePath(), dir.getAbsolutePath());
  }

  @Test
  public void clearAllPropertyClearsExistingPreferences() {
    manager.setValue("clear-me", "value");
    System.setProperty(PreferencesManager.ORG_ALICE_CLEAR_ALL_PREFERENCES, "true");

    assertEquals("fallback", manager.getValue("clear-me", "fallback"));
  }

  @Test
  public void setValueOverwritesExistingValue() {
    manager.setValue("overwrite", "first");
    manager.setValue("overwrite", "second");

    assertEquals("second", manager.getValue("overwrite", "fallback"));
  }
}
