package org.alice.stageide.sceneeditor;

import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Gap-fill contract tests for sceneeditor helper classes that lack
 * dedicated test coverage:
 * - {@link ThumbnailGenerator}: constructor, createThumbnail method
 * - {@link ShowJointedModelJointAxesState}: singleton factory, field accessor
 *
 * These are structural / contract tests — they verify API surface
 * and class layout without requiring a running OpenGL context.
 */
public class SceneEditorGapFillTest {

  // Cached to avoid 4 redundant Class.forName lookups
  private static Class<?> storytellingSceneEditorClass;

  @BeforeClass
  public static void cacheClasses() throws Exception {
    storytellingSceneEditorClass = Class.forName(
        "org.alice.stageide.sceneeditor.StorytellingSceneEditor");
  }

  // ======== ThumbnailGenerator ===========================================

  @Test
  public void thumbnailGenerator_classIsLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.sceneeditor.ThumbnailGenerator");
  }

  @Test
  public void thumbnailGenerator_isFinal() {
    assertTrue(Modifier.isFinal(ThumbnailGenerator.class.getModifiers()));
  }

  @Test
  public void thumbnailGenerator_isPublic() {
    assertTrue(Modifier.isPublic(ThumbnailGenerator.class.getModifiers()));
  }

  @Test
  public void thumbnailGenerator_constructor_acceptsWidthHeight() throws NoSuchMethodException {
    Constructor<?> ctor = ThumbnailGenerator.class.getConstructor(int.class, int.class);
    assertTrue(Modifier.isPublic(ctor.getModifiers()));
  }

  @Test
  public void thumbnailGenerator_onlyOneConstructor() {
    assertEquals(1, ThumbnailGenerator.class.getDeclaredConstructors().length);
  }

  @Test
  public void thumbnailGenerator_createThumbnailMethod_exists() throws NoSuchMethodException {
    Method m = ThumbnailGenerator.class.getMethod("createThumbnail");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(java.awt.image.BufferedImage.class, m.getReturnType());
  }

  @Test
  public void thumbnailGenerator_hasOffscreenRenderTargetField() throws NoSuchFieldException {
    Field f = ThumbnailGenerator.class.getDeclaredField("offscreenRenderTarget");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  // ======== ShowJointedModelJointAxesState ================================

  @Test
  public void showJointedModelJointAxesState_classIsLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.sceneeditor.ShowJointedModelJointAxesState");
  }

  @Test
  public void showJointedModelJointAxesState_isPublic() {
    assertTrue(Modifier.isPublic(ShowJointedModelJointAxesState.class.getModifiers()));
  }

  @Test
  public void showJointedModelJointAxesState_extendsBooleanState() {
    assertTrue(org.lgna.croquet.BooleanState.class
        .isAssignableFrom(ShowJointedModelJointAxesState.class));
  }

  @Test
  public void showJointedModelJointAxesState_getInstanceMethod_exists() throws NoSuchMethodException {
    Method m = ShowJointedModelJointAxesState.class.getMethod("getInstance",
        org.lgna.project.ast.AbstractField.class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
    assertEquals(ShowJointedModelJointAxesState.class, m.getReturnType());
  }

  @Test
  public void showJointedModelJointAxesState_getFieldMethod_exists() throws NoSuchMethodException {
    Method m = ShowJointedModelJointAxesState.class.getMethod("getField");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(org.lgna.project.ast.AbstractField.class, m.getReturnType());
  }

  @Test
  public void showJointedModelJointAxesState_constructor_isPrivate() {
    for (Constructor<?> ctor : ShowJointedModelJointAxesState.class.getDeclaredConstructors()) {
      assertTrue("constructor must be private",
          Modifier.isPrivate(ctor.getModifiers()));
    }
  }

  @Test
  public void showJointedModelJointAxesState_hasMapField() throws NoSuchFieldException {
    Field f = ShowJointedModelJointAxesState.class.getDeclaredField("map");
    assertTrue(Modifier.isStatic(f.getModifiers()));
    assertTrue(Modifier.isPrivate(f.getModifiers()));
  }

  @Test
  public void showJointedModelJointAxesState_hasFieldField() throws NoSuchFieldException {
    Field f = ShowJointedModelJointAxesState.class.getDeclaredField("field");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
  }

  // ======== CameraOption =================================================

  @Test
  public void cameraOption_classIsLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.sceneeditor.CameraOption");
  }

  @Test
  public void cameraOption_isEnum() {
    assertTrue(CameraOption.class.isEnum());
  }

  @Test
  public void cameraOption_hasValues() {
    CameraOption[] values = CameraOption.values();
    assertTrue("CameraOption should have at least 1 value", values.length > 0);
  }

  // ======== SceneEditorLifecycleManager cross-check ======================

  @Test
  public void sceneEditorLifecycleManager_classIsLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.sceneeditor.SceneEditorLifecycleManager");
  }

  @Test
  public void sceneEditorLifecycleManager_isNotAbstract() throws ClassNotFoundException {
    Class<?> cls = Class.forName("org.alice.stageide.sceneeditor.SceneEditorLifecycleManager");
    assertFalse(Modifier.isAbstract(cls.getModifiers()));
  }

  // ======== StorytellingSceneEditor singleton pattern ====================

  @Test
  public void storytellingSceneEditor_classIsLoadable() {
    assertNotNull(storytellingSceneEditorClass);
  }

  @Test
  public void storytellingSceneEditor_getInstanceMethod_exists() throws Exception {
    Method m = storytellingSceneEditorClass.getMethod("getInstance");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }

  @Test
  public void storytellingSceneEditor_getSgCameraMethod_exists() throws Exception {
    Method m = storytellingSceneEditorClass.getMethod("getSgCameraForCreatingThumbnails");
    assertTrue(Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void storytellingSceneEditor_preScreenCaptureMethod_exists() throws Exception {
    Method m = storytellingSceneEditorClass.getDeclaredMethod("preScreenCapture");
    assertNotNull(m);
  }

  @Test
  public void storytellingSceneEditor_postScreenCaptureMethod_exists() throws Exception {
    Method m = storytellingSceneEditorClass.getDeclaredMethod("postScreenCapture");
    assertNotNull(m);
  }
}
