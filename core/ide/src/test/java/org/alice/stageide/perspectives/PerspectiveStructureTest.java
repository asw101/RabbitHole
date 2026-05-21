package org.alice.stageide.perspectives;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Structural tests for perspective-related classes.
 */
public class PerspectiveStructureTest {

  // ---- PerspectiveState ----

  @Test
  public void perspectiveState_classIsAccessible() {
    assertNotNull(PerspectiveState.class);
  }

  @Test
  public void perspectiveState_isPublic() {
    assertTrue(Modifier.isPublic(PerspectiveState.class.getModifiers()));
  }

  @Test
  public void perspectiveState_isNotAbstract() {
    assertFalse(Modifier.isAbstract(PerspectiveState.class.getModifiers()));
  }

  // ---- ToolBarUtilities ----

  @Test
  public void toolBarUtilities_classIsAccessible() {
    assertNotNull(ToolBarUtilities.class);
  }

  @Test
  public void toolBarUtilities_isPublic() {
    assertTrue(Modifier.isPublic(ToolBarUtilities.class.getModifiers()));
  }

  @Test
  public void toolBarUtilities_hasAppendDocumentSubElements() throws Exception {
    Method m = ToolBarUtilities.class.getMethod("appendDocumentSubElements",
      org.alice.ide.ProjectDocumentFrame.class, java.util.List.class);
    assertNotNull(m);
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }

  @Test
  public void toolBarUtilities_hasAppendUndoRedoSubElements() throws Exception {
    Method m = ToolBarUtilities.class.getMethod("appendUndoRedoSubElements",
      org.alice.ide.ProjectDocumentFrame.class, java.util.List.class);
    assertNotNull(m);
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }

  @Test
  public void toolBarUtilities_hasAppendRunSubElements() throws Exception {
    Method m = ToolBarUtilities.class.getMethod("appendRunSubElements",
      org.alice.ide.ProjectDocumentFrame.class, java.util.List.class);
    assertNotNull(m);
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }

  @Test
  public void toolBarUtilities_privateConstructor_throwsAssertionError() throws Exception {
    java.lang.reflect.Constructor<?> ctor = ToolBarUtilities.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(ctor.getModifiers()));
    ctor.setAccessible(true);
    try {
      ctor.newInstance();
      fail("Expected InvocationTargetException wrapping AssertionError");
    } catch (java.lang.reflect.InvocationTargetException e) {
      assertTrue(e.getCause() instanceof AssertionError);
    }
  }

  // ---- PerspectiveSwitchingCardOwnerComposite ----

  @Test
  public void perspectiveSwitchingCardOwnerComposite_classIsAccessible() {
    assertNotNull(PerspectiveSwitchingCardOwnerComposite.class);
  }

  @Test
  public void perspectiveSwitchingCardOwnerComposite_isPublic() {
    assertTrue(Modifier.isPublic(PerspectiveSwitchingCardOwnerComposite.class.getModifiers()));
  }

  // ---- AbstractCodePerspective ----

  @Test
  public void abstractCodePerspective_classIsAccessible() {
    assertNotNull(AbstractCodePerspective.class);
  }

  @Test
  public void abstractCodePerspective_isAbstract() {
    assertTrue(Modifier.isAbstract(AbstractCodePerspective.class.getModifiers()));
  }

  @Test
  public void abstractCodePerspective_isPublic() {
    assertTrue(Modifier.isPublic(AbstractCodePerspective.class.getModifiers()));
  }

  // ---- CodePerspective ----

  @Test
  public void codePerspective_classIsAccessible() {
    assertNotNull(CodePerspective.class);
  }

  @Test
  public void codePerspective_isPublic() {
    assertTrue(Modifier.isPublic(CodePerspective.class.getModifiers()));
  }

  @Test
  public void codePerspective_extendsAbstractCodePerspective() {
    assertTrue(AbstractCodePerspective.class.isAssignableFrom(CodePerspective.class));
  }

  // ---- SetupScenePerspective ----

  @Test
  public void setupScenePerspective_classIsAccessible() {
    assertNotNull(SetupScenePerspective.class);
  }

  @Test
  public void setupScenePerspective_isPublic() {
    assertTrue(Modifier.isPublic(SetupScenePerspective.class.getModifiers()));
  }

  // ---- All perspective classes in expected package ----

  @Test
  public void allPerspectiveClasses_inExpectedPackage() {
    String pkg = "org.alice.stageide.perspectives";
    assertEquals(pkg, PerspectiveState.class.getPackage().getName());
    assertEquals(pkg, ToolBarUtilities.class.getPackage().getName());
    assertEquals(pkg, PerspectiveSwitchingCardOwnerComposite.class.getPackage().getName());
    assertEquals(pkg, AbstractCodePerspective.class.getPackage().getName());
    assertEquals(pkg, CodePerspective.class.getPackage().getName());
    assertEquals(pkg, SetupScenePerspective.class.getPackage().getName());
  }
}
