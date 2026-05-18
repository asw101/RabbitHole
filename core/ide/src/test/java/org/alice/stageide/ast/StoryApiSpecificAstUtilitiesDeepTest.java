package org.alice.stageide.ast;

import org.junit.Test;
import org.lgna.project.Project;
import org.lgna.project.Project.SceneCameraType;
import org.lgna.project.ast.*;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Deep coverage tests for {@link StoryApiSpecificAstUtilities} —
 * scene type extraction, null handling, and program type traversal.
 */
public class StoryApiSpecificAstUtilitiesDeepTest {

  // ---- getSceneFieldFromProgramType null input ----

  @Test
  public void getSceneFieldFromProgramType_null_returnsNull() {
    assertNull(StoryApiSpecificAstUtilities.getSceneFieldFromProgramType(null));
  }

  // ---- getSceneTypeFromProgramType null input ----

  @Test
  public void getSceneTypeFromProgramType_null_returnsNull() {
    assertNull(StoryApiSpecificAstUtilities.getSceneTypeFromProgramType(null));
  }

  // ---- getSceneTypeFromProject null input ----

  @Test
  public void getSceneTypeFromProject_null_returnsNull() {
    assertNull(StoryApiSpecificAstUtilities.getSceneTypeFromProject(null));
  }

  // ---- getSceneTypeFromDocument null input ----

  @Test
  public void getSceneTypeFromDocument_null_returnsNull() {
    assertNull(StoryApiSpecificAstUtilities.getSceneTypeFromDocument(null));
  }

  // ---- getSceneFieldFromProgramType with a field ----

  @Test
  public void getSceneFieldFromProgramType_withField_returnsFirstField() {
    NamedUserType sceneType = createNamedType("MyScene");
    NamedUserType programType = createNamedType("MyProgram");
    UserField sceneField = createField("myScene", sceneType);
    programType.fields.add(sceneField);

    UserField result = StoryApiSpecificAstUtilities.getSceneFieldFromProgramType(programType);
    assertNotNull(result);
    assertEquals("myScene", result.getName());
  }

  // ---- getSceneTypeFromProgramType with a field that has a NamedUserType value type ----

  @Test
  public void getSceneTypeFromProgramType_withField_returnsFieldValueType() {
    NamedUserType sceneType = createNamedType("MyScene");
    NamedUserType programType = createNamedType("MyProgram");
    UserField sceneField = createField("myScene", sceneType);
    programType.fields.add(sceneField);

    NamedUserType result = StoryApiSpecificAstUtilities.getSceneTypeFromProgramType(programType);
    assertSame(sceneType, result);
  }

  // ---- getSceneTypeFromProject ----

  @Test
  public void getSceneTypeFromProject_withValidProject_returnsSceneType() {
    NamedUserType sceneType = createNamedType("TestScene");
    NamedUserType programType = createNamedType("TestProgram");
    UserField sceneField = createField("myScene", sceneType);
    programType.fields.add(sceneField);

    Project project = new Project(programType, SceneCameraType.WindowCamera);
    NamedUserType result = StoryApiSpecificAstUtilities.getSceneTypeFromProject(project);
    assertSame(sceneType, result);
  }

  // ---- isSceneType with various types ----

  @Test
  public void isSceneType_null_returnsFalse() {
    assertFalse(StoryApiSpecificAstUtilities.isSceneType(null));
  }

  @Test
  public void isSceneType_integerType_returnsFalse() {
    assertFalse(StoryApiSpecificAstUtilities.isSceneType(JavaType.getInstance(Integer.class)));
  }

  @Test
  public void isSceneType_voidType_returnsFalse() {
    assertFalse(StoryApiSpecificAstUtilities.isSceneType(JavaType.VOID_TYPE));
  }

  // ---- getUserMethodsInvokedSceneActivationListeners ----

  @Test
  public void getUserMethodsInvokedSceneActivationListeners_null_returnsEmptyList() {
    List<UserMethod> result = StoryApiSpecificAstUtilities.getUserMethodsInvokedSceneActivationListeners(null);
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  public void getUserMethodsInvokedSceneActivationListeners_typeWithNoMethods_returnsEmpty() {
    NamedUserType type = createNamedType("EmptyScene");
    List<UserMethod> result = StoryApiSpecificAstUtilities.getUserMethodsInvokedSceneActivationListeners(type);
    assertTrue(result.isEmpty());
  }

  // ---- getPerformEditorGeneratedSetUpMethod ----

  @Test
  public void getPerformEditorGeneratedSetUpMethod_null_returnsNull() {
    assertNull(StoryApiSpecificAstUtilities.getPerformEditorGeneratedSetUpMethod(null));
  }

  // ---- getInitializeEventListenersMethod ----

  @Test
  public void getInitializeEventListenersMethod_null_returnsNull() {
    assertNull(StoryApiSpecificAstUtilities.getInitializeEventListenersMethod(null));
  }

  // ---- helpers ----

  private static NamedUserType createNamedType(String name) {
    NamedUserType type = new NamedUserType();
    type.name.setValue(name);
    type.superType.setValue(JavaType.getInstance(Object.class));
    return type;
  }

  private static UserField createField(String name, AbstractType<?, ?, ?> valueType) {
    UserField field = new UserField();
    field.name.setValue(name);
    field.valueType.setValue(valueType);
    field.initializer.setValue(new NullLiteral());
    return field;
  }
}
