package org.alice.stageide.perspectives.scenesetup;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Structural tests for scene setup perspective composite classes.
 */
public class SetupSceneCompositeStructureTest {

  // ---- SetupScenePerspectiveComposite ----

  @Test
  public void setupScenePerspectiveComposite_classIsAccessible() {
    assertNotNull(SetupScenePerspectiveComposite.class);
  }

  @Test
  public void setupScenePerspectiveComposite_isPublic() {
    assertTrue(Modifier.isPublic(SetupScenePerspectiveComposite.class.getModifiers()));
  }

  @Test
  public void setupScenePerspectiveComposite_isNotAbstract() {
    assertFalse(Modifier.isAbstract(SetupScenePerspectiveComposite.class.getModifiers()));
  }

  // ---- SetupSceneToolBarComposite ----

  @Test
  public void setupSceneToolBarComposite_classIsAccessible() {
    assertNotNull(SetupSceneToolBarComposite.class);
  }

  @Test
  public void setupSceneToolBarComposite_isPublic() {
    assertTrue(Modifier.isPublic(SetupSceneToolBarComposite.class.getModifiers()));
  }

  @Test
  public void setupSceneToolBarComposite_isNotAbstract() {
    assertFalse(Modifier.isAbstract(SetupSceneToolBarComposite.class.getModifiers()));
  }

  // ---- SceneLayoutComposite ----

  @Test
  public void sceneLayoutComposite_classIsAccessible() {
    assertNotNull(SceneLayoutComposite.class);
  }

  @Test
  public void sceneLayoutComposite_isPublic() {
    assertTrue(Modifier.isPublic(SceneLayoutComposite.class.getModifiers()));
  }

  @Test
  public void sceneLayoutComposite_isNotAbstract() {
    assertFalse(Modifier.isAbstract(SceneLayoutComposite.class.getModifiers()));
  }

  // ---- All scene setup classes are public ----

  @Test
  public void allSceneSetupClasses_arePublic() {
    assertTrue(Modifier.isPublic(SetupScenePerspectiveComposite.class.getModifiers()));
    assertTrue(Modifier.isPublic(SetupSceneToolBarComposite.class.getModifiers()));
    assertTrue(Modifier.isPublic(SceneLayoutComposite.class.getModifiers()));
  }

  // ---- All scene setup classes are concrete ----

  @Test
  public void allSceneSetupClasses_areConcrete() {
    assertFalse(Modifier.isAbstract(SetupScenePerspectiveComposite.class.getModifiers()));
    assertFalse(Modifier.isAbstract(SetupSceneToolBarComposite.class.getModifiers()));
    assertFalse(Modifier.isAbstract(SceneLayoutComposite.class.getModifiers()));
  }

  // ---- All classes in expected package ----

  @Test
  public void allClasses_inExpectedPackage() {
    String pkg = "org.alice.stageide.perspectives.scenesetup";
    assertEquals(pkg, SetupScenePerspectiveComposite.class.getPackage().getName());
    assertEquals(pkg, SetupSceneToolBarComposite.class.getPackage().getName());
    assertEquals(pkg, SceneLayoutComposite.class.getPackage().getName());
  }
}
