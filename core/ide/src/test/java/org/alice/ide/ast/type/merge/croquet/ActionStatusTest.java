package org.alice.ide.ast.type.merge.croquet;

import org.junit.Test;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.JavaType;
import static org.junit.Assert.*;

public class ActionStatusTest {
  @Test
  public void hasAllExpectedValues() {
    ActionStatus[] values = ActionStatus.values();
    assertTrue(values.length >= 10);
  }
  @Test
  public void omitIsPresent() {
    assertNotNull(ActionStatus.valueOf("OMIT"));
  }
  @Test
  public void addUniqueIsPresent() {
    assertNotNull(ActionStatus.valueOf("ADD_UNIQUE"));
  }
  @Test
  public void addAndRenameIsPresent() {
    assertNotNull(ActionStatus.valueOf("ADD_AND_RENAME"));
  }
  @Test
  public void replaceOverOriginalIsPresent() {
    assertNotNull(ActionStatus.valueOf("REPLACE_OVER_ORIGINAL"));
  }
  @Test
  public void keepIdenticalIsPresent() {
    assertNotNull(ActionStatus.valueOf("KEEP_IDENTICAL"));
  }
  @Test
  public void selectionRequiredIsPresent() {
    assertNotNull(ActionStatus.valueOf("SELECTION_REQUIRED"));
  }
  @Test
  public void renameRequiredIsPresent() {
    assertNotNull(ActionStatus.valueOf("RENAME_REQUIRED"));
  }
  @Test
  public void getDescriptionText_omit_containsMemberName() {
    UserField field = createField("testField");
    String desc = ActionStatus.OMIT.getDescriptionText(field);
    assertTrue(desc.contains("testField"));
    assertTrue(desc.contains("omit"));
  }
  @Test
  public void getDescriptionText_addUnique_containsMemberName() {
    UserField field = createField("myProp");
    String desc = ActionStatus.ADD_UNIQUE.getDescriptionText(field);
    assertTrue(desc.contains("myProp"));
    assertTrue(desc.contains("added"));
  }
  @Test
  public void getDescriptionText_keepIdentical_containsMemberName() {
    UserField field = createField("keepMe");
    String desc = ActionStatus.KEEP_IDENTICAL.getDescriptionText(field);
    assertTrue(desc.contains("keepMe"));
  }
  @Test
  public void getDescriptionText_returnsHtml() {
    UserField field = createField("x");
    for (ActionStatus status : ActionStatus.values()) {
      String desc = status.getDescriptionText(field);
      assertTrue(status.name() + " description should be HTML", desc.startsWith("<html>"));
    }
  }
  private static UserField createField(String name) {
    UserField field = new UserField();
    field.name.setValue(name);
    field.valueType.setValue(JavaType.getInstance(String.class));
    return field;
  }
}
