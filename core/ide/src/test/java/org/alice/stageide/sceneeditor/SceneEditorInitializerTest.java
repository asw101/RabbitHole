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
 * TDD contract tests for SceneEditorInitializer — the delegate class
 * extracted from StorytellingSceneEditor.initializeComponents().
 *
 * Owns the ~65-line body of initializeComponents() plus the
 * EXPAND_ICON / CONTRACT_ICON static constants formerly on SSE.
 *
 * Pure reflection — no GUI, no singleton instantiation.
 * These tests FAIL until the extraction is implemented.
 */
public class SceneEditorInitializerTest {

  private static final String FQCN = "org.alice.stageide.sceneeditor.SceneEditorInitializer";
  private static Class<?> clazz;

  @BeforeClass
  public static void loadClass() {
    try {
      clazz = Class.forName(FQCN);
    } catch (ClassNotFoundException e) {
      fail("SceneEditorInitializer must exist as a top-level class: " + e.getMessage());
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

  // ── initialize() method ──────────────────────────────────────────

  @Test
  public void hasInitializeMethod() {
    boolean found = Arrays.stream(clazz.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("initialize") && m.getParameterCount() == 0);
    assertTrue("must have initialize() method", found);
  }

  @Test
  public void initializeMethod_isPackagePrivateOrPublic() {
    Method method = Arrays.stream(clazz.getDeclaredMethods())
        .filter(m -> m.getName().equals("initialize") && m.getParameterCount() == 0)
        .findFirst()
        .orElse(null);
    assertNotNull("initialize() method must exist", method);
    int mods = method.getModifiers();
    // Must not be private — needs to be callable from SSE
    assertFalse("initialize() must not be private", Modifier.isPrivate(mods));
  }

  @Test
  public void initializeMethod_returnsVoid() {
    Method method = Arrays.stream(clazz.getDeclaredMethods())
        .filter(m -> m.getName().equals("initialize") && m.getParameterCount() == 0)
        .findFirst()
        .orElse(null);
    assertNotNull("initialize() method must exist", method);
    assertEquals("initialize() must return void", void.class, method.getReturnType());
  }

  // ── Icon constants moved from SSE ────────────────────────────────

  @Test
  public void hasExpandIconField() {
    assertStaticField("EXPAND_ICON");
  }

  @Test
  public void hasContractIconField() {
    assertStaticField("CONTRACT_ICON");
  }

  @Test
  public void expandIcon_isStatic() {
    try {
      Field f = clazz.getDeclaredField("EXPAND_ICON");
      assertTrue("EXPAND_ICON must be static", Modifier.isStatic(f.getModifiers()));
    } catch (NoSuchFieldException e) {
      fail("Missing EXPAND_ICON field");
    }
  }

  @Test
  public void contractIcon_isStatic() {
    try {
      Field f = clazz.getDeclaredField("CONTRACT_ICON");
      assertTrue("CONTRACT_ICON must be static", Modifier.isStatic(f.getModifiers()));
    } catch (NoSuchFieldException e) {
      fail("Missing CONTRACT_ICON field");
    }
  }

  @Test
  public void iconFields_areIconType() {
    try {
      Field expand = clazz.getDeclaredField("EXPAND_ICON");
      Field contract = clazz.getDeclaredField("CONTRACT_ICON");
      assertEquals("EXPAND_ICON must be javax.swing.Icon",
          "javax.swing.Icon", expand.getType().getName());
      assertEquals("CONTRACT_ICON must be javax.swing.Icon",
          "javax.swing.Icon", contract.getType().getName());
    } catch (NoSuchFieldException e) {
      fail("Missing icon field: " + e.getMessage());
    }
  }

  // ── Helpers ──────────────────────────────────────────────────────

  private static void assertStaticField(String name) {
    try {
      Field f = clazz.getDeclaredField(name);
      assertTrue(name + " must be static", Modifier.isStatic(f.getModifiers()));
    } catch (NoSuchFieldException e) {
      fail("Missing static field: " + name);
    }
  }
}
