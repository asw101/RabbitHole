package org.alice.stageide.modelresource;

import org.alice.ide.icons.GroupIconFactory;
import org.alice.ide.icons.ThemeIconFactory;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.*;

public class TagKeyStructureTest {

  private static final class TestTagKey extends TagKey {
    private TestTagKey(String tag) {
      super(tag);
    }

    @Override
    public org.lgna.croquet.icon.IconFactory getIconFactory() {
      return null;
    }
  }

  @Test
  public void tagKey_classIsAbstract() {
    assertTrue(Modifier.isAbstract(TagKey.class.getModifiers()));
  }

  @Test
  public void tagKey_extendsResourceKey() {
    assertEquals(ResourceKey.class, TagKey.class.getSuperclass());
  }

  @Test
  public void separator_constant_isColon() {
    assertEquals(':', TagKey.SEPARATOR);
  }

  @Test
  public void getTag_plainTag_returnsOriginalValue() {
    TestTagKey key = new TestTagKey("animals");
    assertEquals("animals", key.getTag());
  }

  @Test
  public void getInternalName_nestedTag_returnsSuffixAfterSeparator() {
    TestTagKey key = new TestTagKey("theme:animals");
    assertEquals("animals", key.getInternalName());
  }

  @Test
  public void getInternalName_plainTag_returnsOriginalValue() {
    TestTagKey key = new TestTagKey("animals");
    assertEquals("animals", key.getInternalName());
  }

  @Test
  public void getLocalizedCreationText_anyTag_matchesLocalizedName() {
    TestTagKey key = new TestTagKey("theme:animals");
    assertEquals(key.getLocalizedName(), key.getLocalizedCreationText());
  }

  @Test
  public void getSearchText_anyTag_containsInternalName() {
    TestTagKey key = new TestTagKey("theme:animals");
    String searchText = key.getSearchText();
    assertNotNull(searchText);
    assertTrue(searchText.contains("animals"));
  }

  @Test
  public void createInstanceCreation_anyCache_returnsNull() {
    TestTagKey key = new TestTagKey("theme:animals");
    assertNull(key.createInstanceCreation((Set<org.lgna.project.ast.NamedUserType>) null));
  }

  @Test
  public void getTags_anyTag_returnsNull() {
    assertNull(new TestTagKey("theme:animals").getTags());
  }

  @Test
  public void getGroupTags_anyTag_returnsNull() {
    assertNull(new TestTagKey("theme:animals").getGroupTags());
  }

  @Test
  public void getThemeTags_anyTag_returnsNull() {
    assertNull(new TestTagKey("theme:animals").getThemeTags());
  }

  @Test
  public void isLeaf_anyTag_returnsFalse() {
    assertFalse(new TestTagKey("theme:animals").isLeaf());
  }

  @Test
  public void isInstanceCreator_anyTag_returnsFalse() {
    assertFalse(new TestTagKey("theme:animals").isInstanceCreator());
  }

  @Test
  public void groupTagKey_extendsTagKey() {
    assertEquals(TagKey.class, GroupTagKey.class.getSuperclass());
  }

  @Test
  public void themeTagKey_extendsTagKey() {
    assertEquals(TagKey.class, ThemeTagKey.class.getSuperclass());
  }

  @Test
  public void groupTagKey_constructor_publicSignature_matchesExpectedTypes() throws Exception {
    Constructor<GroupTagKey> constructor = GroupTagKey.class.getConstructor(String.class, java.util.List.class);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
  }

  @Test
  public void themeTagKey_constructor_publicSignature_matchesExpectedTypes() throws Exception {
    Constructor<ThemeTagKey> constructor = ThemeTagKey.class.getConstructor(String.class);
    assertTrue(Modifier.isPublic(constructor.getModifiers()));
  }

  @Test
  public void groupTagKey_getIconFactory_emptyList_returnsGroupIconFactory() {
    GroupTagKey key = new GroupTagKey("group:animals", Collections.emptyList());
    assertNotNull(key.getIconFactory());
    assertTrue(key.getIconFactory() instanceof GroupIconFactory);
  }

  @Test
  public void themeTagKey_getIconFactory_simpleTheme_returnsThemeIconFactory() {
    ThemeTagKey key = new ThemeTagKey("household");
    assertNotNull(key.getIconFactory());
    assertTrue(key.getIconFactory() instanceof ThemeIconFactory);
  }
}
