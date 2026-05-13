package org.alice.stageide.sceneeditor;

import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Point3;
import org.lgna.story.implementation.MarkerImp;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Tests for {@link SceneEditorCameraHelper} — the static camera/marker utility
 * extracted from StorytellingSceneEditor (issue #528 step 2).
 *
 * Structure:
 *   1. Reflection contract tests — final class, private ctor, all-static methods
 *   2. Unit tests for dependency-free methods
 */
public class SceneEditorCameraHelperTest {

  private static final String FQCN = "org.alice.stageide.sceneeditor.SceneEditorCameraHelper";
  private static Class<?> clazz;

  @BeforeClass
  public static void loadClass() throws Exception {
    clazz = Class.forName(FQCN);
  }

  // ── 1. Contract: Class structure ──────────────────────────────────

  @Test
  public void isFinalClass() {
    assertTrue("utility class must be final", Modifier.isFinal(clazz.getModifiers()));
  }

  @Test
  public void isPackagePrivate() {
    int mods = clazz.getModifiers();
    assertFalse("must not be public", Modifier.isPublic(mods));
    assertFalse("must not be private", Modifier.isPrivate(mods));
    assertFalse("must not be protected", Modifier.isProtected(mods));
  }

  @Test
  public void hasExactlyOneConstructor() {
    Constructor<?>[] ctors = clazz.getDeclaredConstructors();
    assertEquals("utility class must have exactly 1 constructor", 1, ctors.length);
  }

  @Test
  public void constructorIsPrivate() {
    Constructor<?>[] ctors = clazz.getDeclaredConstructors();
    assertTrue("constructor must be private", Modifier.isPrivate(ctors[0].getModifiers()));
    assertEquals("constructor must be no-arg", 0, ctors[0].getParameterCount());
  }

  @Test
  public void allMethodsAreStatic() {
    for (Method m : clazz.getDeclaredMethods()) {
      if (m.isSynthetic()) {
        continue;
      }
      assertTrue(m.getName() + " must be static", Modifier.isStatic(m.getModifiers()));
    }
  }

  @Test
  public void allMethodsArePackagePrivateAccess() {
    for (Method m : clazz.getDeclaredMethods()) {
      if (m.isSynthetic()) {
        continue;
      }
      int mods = m.getModifiers();
      assertFalse(m.getName() + " must not be public", Modifier.isPublic(mods));
      assertFalse(m.getName() + " must not be private", Modifier.isPrivate(mods));
      assertFalse(m.getName() + " must not be protected", Modifier.isProtected(mods));
    }
  }

  // ── 2. Contract: Method signatures ────────────────────────────────

  @Test
  public void hasGetTransformForNewCameraMarker() throws Exception {
    Class<?> transformableImp = Class.forName("org.lgna.story.implementation.TransformableImp");
    Method m = clazz.getDeclaredMethod("getTransformForNewCameraMarker", transformableImp);
    assertEquals(AffineMatrix4x4.class, m.getReturnType());
  }

  @Test
  public void hasGetTransformForNewObjectMarker() throws Exception {
    Class<?> entityImp = Class.forName("org.lgna.story.implementation.EntityImp");
    Method m = clazz.getDeclaredMethod("getTransformForNewObjectMarker", entityImp);
    assertEquals(AffineMatrix4x4.class, m.getReturnType());
  }

  @Test
  public void hasGetColorForNewObjectMarker() throws Exception {
    Method m = clazz.getDeclaredMethod("getColorForNewObjectMarker");
    assertEquals(Class.forName("org.lgna.story.Color"), m.getReturnType());
  }

  @Test
  public void hasGetColorForNewCameraMarker() throws Exception {
    Method m = clazz.getDeclaredMethod("getColorForNewCameraMarker");
    assertEquals(Class.forName("org.lgna.story.Color"), m.getReturnType());
  }

  @Test
  public void hasGetGoodPointOfViewInSceneForObject() throws Exception {
    Method m = clazz.getDeclaredMethod("getGoodPointOfViewInSceneForObject", AxisAlignedBox.class);
    assertEquals(AffineMatrix4x4.class, m.getReturnType());
  }

  @Test
  public void hasGetMarkerForField() throws Exception {
    Method m = clazz.getDeclaredMethod("getMarkerForField", Object.class);
    assertEquals(MarkerImp.class, m.getReturnType());
  }

  @Test
  public void methodCount_exactly6() {
    long count = Arrays.stream(clazz.getDeclaredMethods())
        .filter(m -> !m.isSynthetic())
        .count();
    assertEquals("must have exactly 6 methods", 6, count);
  }

  // ── 3. Unit: getTransformForNewObjectMarker ───────────────────────

  @Test
  public void getTransformForNewObjectMarker_null_returnsIdentity() {
    AffineMatrix4x4 result = SceneEditorCameraHelper.getTransformForNewObjectMarker(null);
    assertEquals("null selectedImp → IDENTITY", AffineMatrix4x4.IDENTITY, result);
  }

  // ── 4. Unit: getMarkerForField ────────────────────────────────────

  @Test
  public void getMarkerForField_null_returnsNull() {
    MarkerImp result = SceneEditorCameraHelper.getMarkerForField(null);
    assertNull("null fieldInstance → null", result);
  }

  @Test
  public void getMarkerForField_nonMarker_returnsNull() {
    MarkerImp result = SceneEditorCameraHelper.getMarkerForField("not a marker");
    assertNull("String is not SMarker → null", result);
  }

  @Test
  public void getMarkerForField_plainObject_returnsNull() {
    MarkerImp result = SceneEditorCameraHelper.getMarkerForField(new Object());
    assertNull("Object is not SMarker → null", result);
  }

  // ── 5. Unit: getGoodPointOfViewInSceneForObject ───────────────────

  @Test(expected = RuntimeException.class)
  public void getGoodPointOfViewInSceneForObject_throwsRuntimeException() {
    AxisAlignedBox box = new AxisAlignedBox(Point3.ORIGIN, new Point3(1, 1, 1));
    SceneEditorCameraHelper.getGoodPointOfViewInSceneForObject(box);
  }

  @Test(expected = RuntimeException.class)
  public void getGoodPointOfViewInSceneForObject_throwsEvenWithNull() {
    SceneEditorCameraHelper.getGoodPointOfViewInSceneForObject(null);
  }

  @Test
  public void getGoodPointOfViewInSceneForObject_exceptionMessage() {
    try {
      SceneEditorCameraHelper.getGoodPointOfViewInSceneForObject(null);
      fail("Should have thrown RuntimeException");
    } catch (RuntimeException e) {
      assertEquals("todo", e.getMessage());
    }
  }
}
