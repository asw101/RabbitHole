package org.alice.stageide.perspectives.code;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Structural tests for code perspective composite classes.
 */
public class CodePerspectiveCompositeStructureTest {

  // ---- CodeContextSplitComposite ----

  @Test
  public void codeContextSplitComposite_classIsAccessible() {
    assertNotNull(CodeContextSplitComposite.class);
  }

  @Test
  public void codeContextSplitComposite_isPublic() {
    assertTrue(Modifier.isPublic(CodeContextSplitComposite.class.getModifiers()));
  }

  @Test
  public void codeContextSplitComposite_isNotAbstract() {
    assertFalse(Modifier.isAbstract(CodeContextSplitComposite.class.getModifiers()));
  }

  // ---- CodePerspectiveComposite ----

  @Test
  public void codePerspectiveComposite_classIsAccessible() {
    assertNotNull(CodePerspectiveComposite.class);
  }

  @Test
  public void codePerspectiveComposite_isPublic() {
    assertTrue(Modifier.isPublic(CodePerspectiveComposite.class.getModifiers()));
  }

  @Test
  public void codePerspectiveComposite_isNotAbstract() {
    assertFalse(Modifier.isAbstract(CodePerspectiveComposite.class.getModifiers()));
  }

  // ---- CodeToolBarComposite ----

  @Test
  public void codeToolBarComposite_classIsAccessible() {
    assertNotNull(CodeToolBarComposite.class);
  }

  @Test
  public void codeToolBarComposite_isPublic() {
    assertTrue(Modifier.isPublic(CodeToolBarComposite.class.getModifiers()));
  }

  @Test
  public void codeToolBarComposite_isNotAbstract() {
    assertFalse(Modifier.isAbstract(CodeToolBarComposite.class.getModifiers()));
  }

  // ---- All code perspective classes are public ----

  @Test
  public void allCodePerspectiveClasses_arePublic() {
    assertTrue(Modifier.isPublic(CodeContextSplitComposite.class.getModifiers()));
    assertTrue(Modifier.isPublic(CodePerspectiveComposite.class.getModifiers()));
    assertTrue(Modifier.isPublic(CodeToolBarComposite.class.getModifiers()));
  }

  // ---- All code perspective classes are concrete ----

  @Test
  public void allCodePerspectiveClasses_areConcrete() {
    assertFalse(Modifier.isAbstract(CodeContextSplitComposite.class.getModifiers()));
    assertFalse(Modifier.isAbstract(CodePerspectiveComposite.class.getModifiers()));
    assertFalse(Modifier.isAbstract(CodeToolBarComposite.class.getModifiers()));
  }

  // ---- All classes in expected package ----

  @Test
  public void allClasses_inExpectedPackage() {
    String pkg = "org.alice.stageide.perspectives.code";
    assertEquals(pkg, CodeContextSplitComposite.class.getPackage().getName());
    assertEquals(pkg, CodePerspectiveComposite.class.getPackage().getName());
    assertEquals(pkg, CodeToolBarComposite.class.getPackage().getName());
  }
}
