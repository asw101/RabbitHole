package org.alice.stageide.sceneeditor;

import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Delegation-wiring contract tests for StorytellingSceneEditor (issue #528 step 2).
 *
 * Verifies that StorytellingSceneEditor correctly wires to its extracted delegates:
 *   - SceneEditorFieldManager (field add/copy/remove)
 *   - SceneEditorCameraHelper (camera/marker utilities)
 *
 * Pure reflection — no GUI, no singleton instantiation.
 */
public class StorytellingSceneEditorDelegationTest {

  private static final String SSE_FQCN = "org.alice.stageide.sceneeditor.StorytellingSceneEditor";
  private static final String FM_FQCN = "org.alice.stageide.sceneeditor.SceneEditorFieldManager";
  private static final String CH_FQCN = "org.alice.stageide.sceneeditor.SceneEditorCameraHelper";

  private static Class<?> sseClass;
  private static Class<?> fmClass;
  private static Class<?> chClass;

  @BeforeClass
  public static void loadClasses() throws Exception {
    sseClass = Class.forName(SSE_FQCN);
    fmClass = Class.forName(FM_FQCN);
    chClass = Class.forName(CH_FQCN);
  }

  // ── 1. FieldManager wiring ────────────────────────────────────────

  @Test
  public void fieldManager_fieldExists() throws Exception {
    Field f = sseClass.getDeclaredField("fieldManager");
    assertNotNull("StorytellingSceneEditor must have a fieldManager field", f);
  }

  @Test
  public void fieldManager_isFinal() throws Exception {
    Field f = sseClass.getDeclaredField("fieldManager");
    assertTrue("fieldManager must be final", Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void fieldManager_isPrivate() throws Exception {
    Field f = sseClass.getDeclaredField("fieldManager");
    assertTrue("fieldManager must be private", Modifier.isPrivate(f.getModifiers()));
  }

  @Test
  public void fieldManager_hasCorrectType() throws Exception {
    Field f = sseClass.getDeclaredField("fieldManager");
    assertEquals("fieldManager must be SceneEditorFieldManager", fmClass, f.getType());
  }

  // ── 2. CameraHelper wiring (static utility — no field needed) ────

  @Test
  public void cameraHelper_noFieldOnSSE() {
    boolean hasCameraHelperField = false;
    for (Field f : sseClass.getDeclaredFields()) {
      if (f.getType() == chClass) {
        hasCameraHelperField = true;
      }
    }
    assertFalse("No SceneEditorCameraHelper field needed (static utility)", hasCameraHelperField);
  }

  // ── 3. SSE still exposes all delegated public methods ─────────────

  @Test
  public void sse_getDoStatementsForCopyField_exists() throws Exception {
    Method m = sseClass.getDeclaredMethod("getDoStatementsForCopyField",
        Class.forName("org.lgna.project.ast.UserField"),
        Class.forName("org.lgna.project.ast.UserField"),
        Class.forName("org.alice.math.immutable.AffineMatrix4x4"));
    assertTrue("must be public", Modifier.isPublic(m.getModifiers()));
    assertEquals("must return Statement[]",
        Class.forName("[Lorg.lgna.project.ast.Statement;"), m.getReturnType());
  }

  @Test
  public void sse_getDoStatementsForAddField_exists() throws Exception {
    Method m = sseClass.getDeclaredMethod("getDoStatementsForAddField",
        Class.forName("org.lgna.project.ast.UserField"),
        Class.forName("org.alice.math.immutable.AffineMatrix4x4"));
    assertTrue("must be public", Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void sse_getUndoStatementsForAddField_exists() throws Exception {
    Method m = sseClass.getDeclaredMethod("getUndoStatementsForAddField",
        Class.forName("org.lgna.project.ast.UserField"));
    assertTrue("must be public", Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void sse_getRiders_exists() throws Exception {
    Method m = sseClass.getDeclaredMethod("getRiders",
        Class.forName("org.lgna.project.ast.UserField"));
    assertTrue("must be public", Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void sse_getDoStatementsForRemoveField_exists() throws Exception {
    Method m = sseClass.getDeclaredMethod("getDoStatementsForRemoveField",
        Class.forName("org.lgna.project.ast.UserField"), Map.class);
    assertTrue("must be public", Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void sse_getUndoStatementsForRemoveField_exists() throws Exception {
    Method m = sseClass.getDeclaredMethod("getUndoStatementsForRemoveField",
        Class.forName("org.lgna.project.ast.UserField"), Map.class);
    assertTrue("must be public", Modifier.isPublic(m.getModifiers()));
  }

  // ── 4. SSE still exposes all camera helper forwarding methods ─────

  @Test
  public void sse_getTransformForNewCameraMarker_exists() throws Exception {
    Method m = sseClass.getDeclaredMethod("getTransformForNewCameraMarker");
    assertTrue("must be public", Modifier.isPublic(m.getModifiers()));
    assertEquals(Class.forName("org.alice.math.immutable.AffineMatrix4x4"), m.getReturnType());
  }

  @Test
  public void sse_getTransformForNewObjectMarker_exists() throws Exception {
    Method m = sseClass.getDeclaredMethod("getTransformForNewObjectMarker");
    assertTrue("must be public", Modifier.isPublic(m.getModifiers()));
    assertEquals(Class.forName("org.alice.math.immutable.AffineMatrix4x4"), m.getReturnType());
  }

  @Test
  public void sse_getColorForNewObjectMarker_exists() throws Exception {
    Method m = sseClass.getDeclaredMethod("getColorForNewObjectMarker");
    assertTrue("must be public", Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void sse_getColorForNewCameraMarker_exists() throws Exception {
    Method m = sseClass.getDeclaredMethod("getColorForNewCameraMarker");
    assertTrue("must be public", Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void sse_getGoodPointOfViewInSceneForObject_exists() throws Exception {
    Method m = sseClass.getDeclaredMethod("getGoodPointOfViewInSceneForObject",
        Class.forName("org.alice.math.immutable.AxisAlignedBox"));
    assertTrue("must be public", Modifier.isPublic(m.getModifiers()));
  }

  @Test
  public void sse_getMarkerForField_exists() throws Exception {
    Method m = sseClass.getDeclaredMethod("getMarkerForField",
        Class.forName("org.lgna.project.ast.UserField"));
    assertTrue("must be public", Modifier.isPublic(m.getModifiers()));
  }

  // ── 5. asSetVehicleCall is accessible from SSE's package ──────────

  @Test
  public void asSetVehicleCall_accessibleFromSSEPackage() throws Exception {
    Method m = fmClass.getDeclaredMethod("asSetVehicleCall",
        Class.forName("org.lgna.project.ast.Statement"));
    assertTrue("must be static", Modifier.isStatic(m.getModifiers()));
    assertFalse("must not be private (called from SSE)", Modifier.isPrivate(m.getModifiers()));
  }

  // ── 6. Delegate classes are in same package as SSE ────────────────

  @Test
  public void fieldManager_samePackage() {
    assertEquals("SceneEditorFieldManager must share SSE's package",
        sseClass.getPackageName(), fmClass.getPackageName());
  }

  @Test
  public void cameraHelper_samePackage() {
    assertEquals("SceneEditorCameraHelper must share SSE's package",
        sseClass.getPackageName(), chClass.getPackageName());
  }

  // ── 7. SSE field management overrides are still @Override ─────────

  @Test
  public void fieldManagementOverrides_declaredOnSSE() {
    Set<String> declaredMethodNames = Arrays.stream(sseClass.getDeclaredMethods())
        .map(Method::getName)
        .collect(Collectors.toSet());

    String[] expectedOverrides = {
        "getDoStatementsForCopyField",
        "getDoStatementsForAddField",
        "getUndoStatementsForAddField",
        "getDoStatementsForRemoveField",
        "getUndoStatementsForRemoveField",
    };

    for (String name : expectedOverrides) {
      assertTrue(name + " must be declared on SSE (for @Override forwarding)",
          declaredMethodNames.contains(name));
    }
  }

  // ── 8. Aggregate: delegate + SSE public method counts stable ──────

  @Test
  public void sse_publicMethodCount_atLeast35() {
    long count = Arrays.stream(sseClass.getDeclaredMethods())
        .filter(m -> Modifier.isPublic(m.getModifiers()))
        .count();
    assertTrue("Expected ≥35 public methods, found " + count, count >= 35);
  }

  @Test
  public void fieldManager_instanceMethodCount_atLeast6() {
    long count = Arrays.stream(fmClass.getDeclaredMethods())
        .filter(m -> !Modifier.isStatic(m.getModifiers()) && !m.isSynthetic())
        .count();
    assertTrue("Expected ≥6 instance methods on FieldManager, found " + count, count >= 6);
  }

  @Test
  public void cameraHelper_staticMethodCount_exactly6() {
    long count = Arrays.stream(chClass.getDeclaredMethods())
        .filter(m -> !m.isSynthetic())
        .count();
    assertEquals("Expected 6 static methods on CameraHelper", 6, count);
  }
}
