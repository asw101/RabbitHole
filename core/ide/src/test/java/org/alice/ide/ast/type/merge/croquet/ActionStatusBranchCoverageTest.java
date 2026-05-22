package org.alice.ide.ast.type.merge.croquet;

import org.junit.Test;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.ManagementLevel;
import org.lgna.project.ast.Member;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class ActionStatusBranchCoverageTest {
  @Test
  public void singularHelperReturnsProcedureForVoidMethods() throws Exception {
    assertEquals("procedure", invokeMemberTextHelper("getSingularMemberClassText", createMethod("wave", JavaType.VOID_TYPE)));
  }

  @Test
  public void singularHelperReturnsFunctionForNonVoidMethods() throws Exception {
    assertEquals("function", invokeMemberTextHelper("getSingularMemberClassText", createMethod("speed", JavaType.INTEGER_OBJECT_TYPE)));
  }

  @Test
  public void singularHelperReturnsPropertyForFields() throws Exception {
    assertEquals("property", invokeMemberTextHelper("getSingularMemberClassText", createField("height")));
  }

  @Test
  public void pluralHelperReturnsProceduresForVoidMethods() throws Exception {
    assertEquals("procedures", invokeMemberTextHelper("getPluralMemberClassText", createMethod("wave", JavaType.VOID_TYPE)));
  }

  @Test
  public void pluralHelperReturnsFunctionsForNonVoidMethods() throws Exception {
    assertEquals("functions", invokeMemberTextHelper("getPluralMemberClassText", createMethod("speed", JavaType.INTEGER_OBJECT_TYPE)));
  }

  @Test
  public void pluralHelperReturnsPropertiesForFields() throws Exception {
    assertEquals("properties", invokeMemberTextHelper("getPluralMemberClassText", createField("height")));
  }

  @Test
  public void renameRequiredUsesProcedurePluralization() {
    assertTrue(ActionStatus.RENAME_REQUIRED.getDescriptionText(createMethod("hop", JavaType.VOID_TYPE)).contains("procedures"));
  }

  @Test
  public void renameRequiredUsesFunctionPluralization() {
    assertTrue(ActionStatus.RENAME_REQUIRED.getDescriptionText(createMethod("score", JavaType.STRING_TYPE)).contains("functions"));
  }

  @Test
  public void renameRequiredUsesPropertyPluralization() {
    assertTrue(ActionStatus.RENAME_REQUIRED.getDescriptionText(createField("color")).contains("properties"));
  }

  @Test
  public void selectionRequiredContainsRenameGuidance() {
    String description = ActionStatus.SELECTION_REQUIRED.getDescriptionText(createField("value"));

    assertTrue(description.contains("rename at least one"));
    assertTrue(description.endsWith("</html>"));
  }

  @Test
  public void omitDescriptionAppendsEmptyNameWhenMemberNameIsEmpty() {
    String description = ActionStatus.OMIT.getDescriptionText(createMethod("", JavaType.VOID_TYPE));

    assertTrue(description.contains("<strong></strong>"));
  }

  @Test
  public void addUniqueDescriptionHandlesEmptyNames() {
    String description = ActionStatus.ADD_UNIQUE.getDescriptionText(createField(""));

    assertTrue(description.contains("<strong></strong>"));
    assertTrue(description.contains("added"));
  }

  @Test
  public void retainedStatusesShareRetainedWording() {
    UserField field = createField("shared");

    assertTrue(ActionStatus.KEEP_OVER_DIFFERENT_SIGNATURE.getDescriptionText(field).contains("retained"));
    assertTrue(ActionStatus.KEEP_OVER_REPLACEMENT.getDescriptionText(field).contains("retained"));
    assertTrue(ActionStatus.KEEP_AND_RENAME.getDescriptionText(field).contains("retained"));
    assertTrue(ActionStatus.KEEP_IDENTICAL.getDescriptionText(field).contains("retained"));
    assertTrue(ActionStatus.KEEP_UNIQUE.getDescriptionText(field).contains("retained"));
  }

  @Test
  public void renameRequiredDescriptionOmittingClosingHtmlIsCharacterized() {
    String description = ActionStatus.RENAME_REQUIRED.getDescriptionText(createField("value"));

    assertFalse(description.endsWith("</html>"));
    assertTrue(description.endsWith("properties."));
  }

  @Test
  public void everyDescriptionContainsStrongMarkup() {
    UserMethod method = createMethod("move", JavaType.VOID_TYPE);
    for (ActionStatus status : ActionStatus.values()) {
      String description = status.getDescriptionText(method);
      assertTrue(status.name(), description.startsWith("<html>"));
      assertTrue(status.name(), description.contains("<strong>"));
      assertTrue(status.name(), description.contains("</strong>"));
    }
  }

  @Test
  public void addAndRenameDescriptionContainsRenamedWording() {
    String description = ActionStatus.ADD_AND_RENAME.getDescriptionText(createField("score"));

    assertTrue(description.contains("added and renamed"));
    assertTrue(description.contains("score"));
  }

  @Test
  public void replaceOverOriginalDescriptionMentionsProjectVersion() {
    String description = ActionStatus.REPLACE_OVER_ORIGINAL.getDescriptionText(createMethod("jump", JavaType.VOID_TYPE));

    assertTrue(description.contains("replace the version already in your project"));
    assertTrue(description.contains("jump"));
  }

  private static String invokeMemberTextHelper(String name, Member member) throws Exception {
    Method method = ActionStatus.class.getDeclaredMethod(name, Member.class);
    method.setAccessible(true);
    return (String) method.invoke(null, member);
  }

  private static UserMethod createMethod(String name, AbstractType<?, ?, ?> returnType) {
    UserMethod method = new UserMethod();
    method.name.setValue(name);
    method.returnType.setValue(returnType);
    method.managementLevel.setValue(ManagementLevel.NONE);
    method.body.setValue(new BlockStatement());
    return method;
  }

  private static UserField createField(String name) {
    UserField field = new UserField();
    field.name.setValue(name);
    field.valueType.setValue(JavaType.STRING_TYPE);
    field.managementLevel.setValue(ManagementLevel.NONE);
    field.initializer.setValue(new NullLiteral());
    return field;
  }
}
