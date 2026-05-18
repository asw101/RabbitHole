package org.lgna.croquet.preferences;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.BinaryEncoder;
import edu.cmu.cs.dennisc.codec.InputStreamBinaryDecoder;
import edu.cmu.cs.dennisc.codec.OutputStreamBinaryEncoder;
import org.junit.After;
import org.junit.Test;
import org.lgna.croquet.*;
import org.lgna.croquet.data.ListData;
import org.lgna.croquet.data.MutableListData;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.UUID;
import java.util.prefs.Preferences;

import static org.junit.Assert.*;

public class PreferenceManagerTest {

  @After
  public void tearDown() throws Exception {
    PreferenceTestSupport.clearProperty();
    PreferenceTestSupport.clearActiveApplication();
    PreferenceTestSupport.removePreferenceNode();
  }

  @Test
  public void privateConstructorThrowsAssertionError() throws Exception {
    Constructor<PreferenceManager> ctor = PreferenceManager.class.getDeclaredConstructor();
    ctor.setAccessible(true);
    try {
      ctor.newInstance();
      fail("expected assertion error");
    } catch (java.lang.reflect.InvocationTargetException ite) {
      assertTrue(ite.getCause() instanceof AssertionError);
    }
  }

  @Test
  public void getUserPreferencesReturnsNullWithoutApplication() {
    assertNull(PreferenceManager.getUserPreferences());
  }

  @Test
  public void getUserPreferencesReturnsNodeForActiveApplication() throws Exception {
    PreferenceTestSupport.PreferenceTestApplication application = PreferenceTestSupport.activateApplication();
    Preferences prefs = PreferenceManager.getUserPreferences();

    assertNotNull(prefs);
    assertEquals(Preferences.userNodeForPackage(application.getClass()).absolutePath(), prefs.absolutePath());
  }

  @Test
  public void getUserPreferencesClearAllPropertyRemovesStoredValues() throws Exception {
    PreferenceTestSupport.activateApplication();
    Preferences prefs = PreferenceManager.getUserPreferences();
    prefs.put("existing", "value");

    PreferenceTestSupport.enableClearAll();
    Preferences cleared = PreferenceManager.getUserPreferences();

    assertNull(cleared.get("existing", null));
  }

  @Test
  public void decodeListDataReturnsDefaultWithoutApplication() {
    String[] defaults = {"a", "b"};
    assertSame(defaults, PreferenceManager.decodeListData("key", CroquetTestUtils.STRING_CODEC, defaults));
  }

  @Test
  public void decodeListDataReadsStoredValueWithApplication() throws Exception {
    PreferenceTestSupport.activateApplication();
    Preferences prefs = PreferenceManager.getUserPreferences();
    String key = "stored-array";
    String[] stored = {"alpha", "beta"};
    prefs.putByteArray(key, PreferenceTestSupport.encodeArray(stored));

    String[] result = PreferenceManager.decodeListData(key, CroquetTestUtils.STRING_CODEC, new String[]{"default"});

    assertArrayEquals(stored, result);
  }

  @Test
  public void registerAndInitializeSelectionLoadsStoredSelection() throws Exception {
    PreferenceTestSupport.activateApplication();
    PreferenceTestSupport.TestSingleSelectListState state =
        PreferenceTestSupport.createState(-1, new String[]{"red", "green"});
    CroquetTestUtils.removeListSelectionListeners(state);
    Preferences prefs = PreferenceManager.getUserPreferences();
    prefs.putByteArray(state.getMigrationId().toString(), PreferenceTestSupport.encodeItem("green"));

    PreferenceManager.registerAndInitializeSelectionOnlyOfListSelectionState(state);

    assertEquals("green", state.getValue());
  }

  @Test
  public void preservePreferencesWritesRegisteredListData() throws Exception {
    PreferenceTestSupport.activateApplication();
    Preferences prefs = PreferenceManager.getUserPreferences();
    PreferenceTestSupport.TestListData data = new PreferenceTestSupport.TestListData("list-data-key", "one", "two");

    PreferenceManager.registerListData(data);
    PreferenceManager.preservePreferences();

    assertNotNull(prefs.getByteArray("list-data-key", null));
  }

  static final class PreferenceTestSupport {
    static final Class<?> APP_CLASS = PreferenceTestApplication.class;
    static final Group GROUP = Group.getInstance(
        UUID.fromString("00000000-0000-0000-7750-000000000200"),
        "preferenceTests");

    private PreferenceTestSupport() {
    }

    static PreferenceTestApplication activateApplication() throws Exception {
      clearActiveApplication();
      return new PreferenceTestApplication();
    }

    static void clearActiveApplication() throws Exception {
      Field field = Application.class.getDeclaredField("singleton");
      field.setAccessible(true);
      field.set(null, null);
    }

    static void removePreferenceNode() throws Exception {
      try {
        Preferences.userNodeForPackage(PreferenceTestApplication.class).removeNode();
      } catch (IllegalStateException ignored) {
      }
    }

    static void enableClearAll() {
      System.setProperty(PreferencesManager.ORG_ALICE_CLEAR_ALL_PREFERENCES, "true");
    }

    static void clearProperty() {
      System.clearProperty(PreferencesManager.ORG_ALICE_CLEAR_ALL_PREFERENCES);
    }

    static byte[] encodeItem(String value) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      BinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
      CroquetTestUtils.STRING_CODEC.encodeValue(encoder, value);
      encoder.flush();
      return baos.toByteArray();
    }

    static byte[] encodeArray(String[] values) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      BinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
      encoder.encode(values != null);
      if (values != null) {
        encoder.encode(values.length);
        for (String value : values) {
          CroquetTestUtils.STRING_CODEC.encodeValue(encoder, value);
        }
      }
      encoder.flush();
      return baos.toByteArray();
    }

    static TestSingleSelectListState createState(int selectionIndex, String[] values) {
      MutableListData<String> data = new MutableListData<>(CroquetTestUtils.STRING_CODEC, values);
      return new TestSingleSelectListState(selectionIndex, data);
    }

    static final class PreferenceTestApplication extends Application<org.lgna.croquet.DocumentFrame> {
      @Override
      public org.lgna.croquet.DocumentFrame getDocumentFrame() {
        return null;
      }

      @Override
      protected Operation getAboutOperation() {
        return null;
      }

      @Override
      protected Operation getPreferencesOperation() {
        return null;
      }

      @Override
      protected void handleOpenFiles(java.util.List<File> files) {
      }

      @Override
      protected void handleWindowOpened(java.awt.event.WindowEvent e) {
      }

      @Override
      public void handleQuit(org.lgna.croquet.history.UserActivity activity) {
      }

      @Override
      public String getApplicationSubPath() {
        return "target/test-preferences/app";
      }
    }

    static final class TestSingleSelectListState extends MutableDataSingleSelectListState<String> {
      TestSingleSelectListState(int selectionIndex, MutableListData<String> data) {
        super(GROUP, CroquetTestUtils.nextTestUUID(), selectionIndex, data);
      }
    }

    static final class TestListData extends ListData<String> {
      private final java.util.List<String> values = new java.util.ArrayList<>();
      private final String key;

      TestListData(String key, String... values) {
        super(CroquetTestUtils.STRING_CODEC);
        this.key = key;
        this.values.addAll(java.util.Arrays.asList(values));
      }

      @Override
      public String getPreferenceKey() {
        return this.key;
      }

      @Override
      public void addListener(javax.swing.event.ListDataListener listener) {
      }

      @Override
      public void removeListener(javax.swing.event.ListDataListener listener) {
      }

      @Override
      public boolean contains(String item) {
        return this.values.contains(item);
      }

      @Override
      public String getItemAt(int index) {
        return this.values.get(index);
      }

      @Override
      public int getItemCount() {
        return this.values.size();
      }

      @Override
      public int indexOf(String item) {
        return this.values.indexOf(item);
      }

      @Override
      public void internalAddItem(int index, String item) {
        this.values.add(index, item);
      }

      @Override
      public void internalRemoveItem(String item) {
        this.values.remove(item);
      }

      @Override
      public void internalSetAllItems(java.util.Collection<String> items) {
        this.values.clear();
        this.values.addAll(items);
      }

      @Override
      public java.util.Iterator<String> iterator() {
        return this.values.iterator();
      }

      @Override
      protected String[] toArray(Class<String> componentType) {
        return this.values.toArray(new String[0]);
      }
    }
  }
}
