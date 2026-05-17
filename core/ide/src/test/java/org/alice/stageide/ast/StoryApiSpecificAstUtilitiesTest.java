package org.alice.stageide.ast;

import org.junit.Test;
import org.lgna.project.ast.*;

import static org.junit.Assert.*;

/**
 * Tests for {@link StoryApiSpecificAstUtilities} — scene type extraction
 * from program/project types.
 */
public class StoryApiSpecificAstUtilitiesTest {

  @Test
  public void isSceneType_nullType_returnsFalse() {
    assertFalse(StoryApiSpecificAstUtilities.isSceneType(null));
  }

  @Test
  public void isSceneType_objectType_returnsFalse() {
    assertFalse(StoryApiSpecificAstUtilities.isSceneType(JavaType.getInstance(Object.class)));
  }

  @Test
  public void isSceneType_stringType_returnsFalse() {
    assertFalse(StoryApiSpecificAstUtilities.isSceneType(JavaType.getInstance(String.class)));
  }

  @Test
  public void getSceneFieldFromProgramType_noSceneField_returnsNull() {
    NamedUserType programType = new NamedUserType();
    programType.name.setValue("TestProgram");
    programType.superType.setValue(JavaType.getInstance(Object.class));
    assertNull(StoryApiSpecificAstUtilities.getSceneFieldFromProgramType(programType));
  }

  @Test
  public void getSceneTypeFromProgramType_noSceneField_returnsNull() {
    NamedUserType programType = new NamedUserType();
    programType.name.setValue("TestProgram");
    programType.superType.setValue(JavaType.getInstance(Object.class));
    assertNull(StoryApiSpecificAstUtilities.getSceneTypeFromProgramType(programType));
  }

  @Test
  public void getPerformEditorGeneratedSetUpMethod_noMethods_returnsNull() {
    NamedUserType sceneType = new NamedUserType();
    sceneType.name.setValue("TestScene");
    sceneType.superType.setValue(JavaType.getInstance(Object.class));
    assertNull(StoryApiSpecificAstUtilities.getPerformEditorGeneratedSetUpMethod(sceneType));
  }

  @Test
  public void getInitializeEventListenersMethod_noMethods_returnsNull() {
    NamedUserType sceneType = new NamedUserType();
    sceneType.name.setValue("TestScene");
    sceneType.superType.setValue(JavaType.getInstance(Object.class));
    assertNull(StoryApiSpecificAstUtilities.getInitializeEventListenersMethod(sceneType));
  }

  @Test
  public void getUserMethodsInvokedSceneActivationListeners_noListenerMethod_returnsEmptyList() {
    NamedUserType sceneType = new NamedUserType();
    sceneType.name.setValue("TestScene");
    sceneType.superType.setValue(JavaType.getInstance(Object.class));
    assertTrue(StoryApiSpecificAstUtilities.getUserMethodsInvokedSceneActivationListeners(sceneType).isEmpty());
  }
}
