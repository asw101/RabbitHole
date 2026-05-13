package org.alice.stageide.sceneeditor;

import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * TDD contract tests for SceneEditorLifecycleManager — the delegate class
 * extracted from StorytellingSceneEditor's lifecycle methods:
 *   - setActiveScene() SSE-specific body (~95 lines)
 *   - addField() SSE-specific logic (~29 lines)
 *   - handleProjectOpened() pre-super logic (~12 lines)
 *   - useSceneAsVehicleForDisconnectedModels() private helper (~12 lines)
 *
 * Pure reflection — no GUI, no singleton instantiation.
 * These tests FAIL until the extraction is implemented.
 */
public class SceneEditorLifecycleManagerTest {

  private static final String FQCN = "org.alice.stageide.sceneeditor.SceneEditorLifecycleManager";
  private static Class<?> clazz;

  @BeforeClass
  public static void loadClass() {
    try {
      clazz = Class.forName(FQCN);
    } catch (ClassNotFoundException e) {
      fail("SceneEditorLifecycleManager must exist as a top-level class: " + e.getMessage());
    }
  }

  // ── Class structure ──────────────────────────────────────────────

  @Test
  public void isPackagePrivate() {
    int mods = clazz.getModifiers();
    assertFalse("must not be public", Modifier.isPublic(mods));
    assertFalse("must not be private", Modifier.isPrivate(mods));
    assertFalse("must not be protected", Modifier.isProtected(mods));
  }

  @Test
  public void isTopLevelClass() {
    assertNull("must not be an inner/nested class", clazz.getEnclosingClass());
  }

  @Test
  public void isNotAbstract() {
    assertFalse("must not be abstract", Modifier.isAbstract(clazz.getModifiers()));
  }

  // ── Constructor ──────────────────────────────────────────────────

  @Test
  public void constructorTakesStorytellingSceneEditor() {
    Constructor<?>[] ctors = clazz.getDeclaredConstructors();
    boolean found = false;
    for (Constructor<?> c : ctors) {
      Class<?>[] params = c.getParameterTypes();
      if (params.length == 1
          && params[0].getName().equals("org.alice.stageide.sceneeditor.StorytellingSceneEditor")) {
        found = true;
      }
    }
    assertTrue("must have constructor(StorytellingSceneEditor)", found);
  }

  // ── activateScene() — extracted from SSE.setActiveScene() ────────

  @Test
  public void hasActivateSceneMethod() {
    boolean found = Arrays.stream(clazz.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("activateScene"));
    assertTrue("must have activateScene() method", found);
  }

  @Test
  public void activateScene_takesUserField() {
    Method method = findMethod("activateScene");
    assertNotNull("activateScene method must exist", method);
    Class<?>[] params = method.getParameterTypes();
    assertEquals("activateScene must take 1 parameter", 1, params.length);
    assertEquals("activateScene parameter must be UserField",
        "org.lgna.project.ast.UserField", params[0].getName());
  }

  @Test
  public void activateScene_returnsVoid() {
    Method method = findMethod("activateScene");
    assertNotNull("activateScene method must exist", method);
    assertEquals("activateScene must return void", void.class, method.getReturnType());
  }

  @Test
  public void activateScene_isNotPrivate() {
    Method method = findMethod("activateScene");
    assertNotNull("activateScene method must exist", method);
    assertFalse("activateScene must not be private",
        Modifier.isPrivate(method.getModifiers()));
  }

  // ── handleAddField() — extracted from SSE.addField() ─────────────

  @Test
  public void hasHandleAddFieldMethod() {
    boolean found = Arrays.stream(clazz.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("handleAddField"));
    assertTrue("must have handleAddField() method", found);
  }

  @Test
  public void handleAddField_takesUserField() {
    Method method = findMethod("handleAddField");
    assertNotNull("handleAddField method must exist", method);
    Class<?>[] params = method.getParameterTypes();
    assertEquals("handleAddField must take 1 parameter", 1, params.length);
    assertEquals("handleAddField parameter must be UserField",
        "org.lgna.project.ast.UserField", params[0].getName());
  }

  @Test
  public void handleAddField_isNotPrivate() {
    Method method = findMethod("handleAddField");
    assertNotNull("handleAddField method must exist", method);
    assertFalse("handleAddField must not be private",
        Modifier.isPrivate(method.getModifiers()));
  }

  // ── prepareForProject() — extracted from SSE.handleProjectOpened() ──

  @Test
  public void hasPrepareForProjectMethod() {
    boolean found = Arrays.stream(clazz.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("prepareForProject"));
    assertTrue("must have prepareForProject() method", found);
  }

  @Test
  public void prepareForProject_takesProject() {
    Method method = findMethod("prepareForProject");
    assertNotNull("prepareForProject method must exist", method);
    Class<?>[] params = method.getParameterTypes();
    assertEquals("prepareForProject must take 1 parameter", 1, params.length);
    assertEquals("prepareForProject parameter must be Project",
        "org.lgna.project.Project", params[0].getName());
  }

  @Test
  public void prepareForProject_isNotPrivate() {
    Method method = findMethod("prepareForProject");
    assertNotNull("prepareForProject method must exist", method);
    assertFalse("prepareForProject must not be private",
        Modifier.isPrivate(method.getModifiers()));
  }

  // ── useSceneAsVehicleForDisconnectedModels — moved here from SSE ──

  @Test
  public void hasUseSceneAsVehicleMethod() {
    boolean found = Arrays.stream(clazz.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("useSceneAsVehicleForDisconnectedModels"));
    assertTrue("useSceneAsVehicleForDisconnectedModels must exist on lifecycle manager", found);
  }

  @Test
  public void useSceneAsVehicle_isPrivate() {
    Method method = findMethod("useSceneAsVehicleForDisconnectedModels");
    assertNotNull("useSceneAsVehicleForDisconnectedModels must exist", method);
    assertTrue("useSceneAsVehicleForDisconnectedModels must be private",
        Modifier.isPrivate(method.getModifiers()));
  }

  // ── SHOW_JOINTED_MODEL_VISUALIZATIONS_KEY — moved here from SSE ──

  @Test
  public void hasShowJointedModelVisualizationsKey() {
    try {
      clazz.getDeclaredField("SHOW_JOINTED_MODEL_VISUALIZATIONS_KEY");
    } catch (NoSuchFieldException e) {
      fail("SHOW_JOINTED_MODEL_VISUALIZATIONS_KEY must exist on lifecycle manager");
    }
  }

  @Test
  public void showJointedModelVisualizationsKey_isStaticFinal() {
    try {
      Field f = clazz.getDeclaredField("SHOW_JOINTED_MODEL_VISUALIZATIONS_KEY");
      int mods = f.getModifiers();
      assertTrue("must be static", Modifier.isStatic(mods));
      assertTrue("must be final", Modifier.isFinal(mods));
    } catch (NoSuchFieldException e) {
      fail("Missing SHOW_JOINTED_MODEL_VISUALIZATIONS_KEY field");
    }
  }

  // ── Helpers ──────────────────────────────────────────────────────

  private static Method findMethod(String name) {
    return Arrays.stream(clazz.getDeclaredMethods())
        .filter(m -> m.getName().equals(name))
        .findFirst()
        .orElse(null);
  }
}
