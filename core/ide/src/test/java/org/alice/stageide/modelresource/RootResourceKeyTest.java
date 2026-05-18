package org.alice.stageide.modelresource;

import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.*;

public class RootResourceKeyTest {
  private RootResourceKey createKey(String keyText, String defaultDisplayText) {
    return new RootResourceKey(keyText, defaultDisplayText);
  }

  @Test
  public void getSearchText_returnsNull() {
    assertNull(createKey("AllClasses", "all classes").getSearchText());
  }

  @Test
  public void getInternalName_returnsDefaultDisplayText() {
    assertEquals("all classes", createKey("AllClasses", "all classes").getInternalName());
  }

  @Test
  public void getLocalizedName_existingKey_notNull() {
    String localizedName = createKey("AllClasses", "all classes").getLocalizedName();
    assertNotNull(localizedName);
    assertFalse(localizedName.isEmpty());
  }

  @Test
  public void getLocalizedCreationText_matchesLocalizedName() {
    RootResourceKey key = createKey("AllClasses", "all classes");
    assertEquals(key.getLocalizedName(), key.getLocalizedCreationText());
  }

  @Test
  public void getLocalizedName_missingKey_fallsBackToDefault() {
    assertEquals("Default Value", createKey("doesNotExist", "Default Value").getLocalizedName());
  }

  @Test
  public void getIconFactory_returnsNull() {
    assertNull(createKey("AllClasses", "all classes").getIconFactory());
  }

  @Test(expected = Error.class)
  public void createInstanceCreation_throwsError() {
    createKey("AllClasses", "all classes").createInstanceCreation(new HashSet<>());
  }

  @Test
  public void isNotLeafOrInstanceCreator() {
    RootResourceKey key = createKey("AllClasses", "all classes");
    assertFalse(key.isLeaf());
    assertFalse(key.isInstanceCreator());
  }

  @Test
  public void tagsAndOperations_returnNull() {
    RootResourceKey key = createKey("AllClasses", "all classes");
    assertNull(key.getTags());
    assertNull(key.getGroupTags());
    assertNull(key.getThemeTags());
    assertNull(key.getLeftClickOperation(null, null));
    assertNull(key.getDropOperation(null, null, null));
  }

  @Test
  public void toString_containsClassName() {
    assertTrue(createKey("AllClasses", "all classes").toString().contains("RootResourceKey"));
  }
}
