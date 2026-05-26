package org.alice.ide.ast;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import static org.alice.ide.testing.JacocoReflectionSupport.declaredFields;
import static org.alice.ide.testing.JacocoReflectionSupport.declaredMethods;
import static org.junit.Assert.*;

/**
 * Shared reflection-based assertion helpers for characterization tests.
 * Eliminates duplication of structural checks across test files.
 */
public final class ReflectionTestHelper {

  private ReflectionTestHelper() {
    throw new AssertionError();
  }

  // ── Constructor assertions ──────────────────────────────────────

  public static void assertAllConstructorsPrivate(Class<?> clazz) {
    for (Constructor<?> c : clazz.getDeclaredConstructors()) {
      assertTrue("Constructor of " + clazz.getSimpleName() + " must be private",
          Modifier.isPrivate(c.getModifiers()));
    }
  }

  public static void assertPrivateConstructorThrowsAssertionError(Class<?> clazz) throws Exception {
    Constructor<?> ctor = clazz.getDeclaredConstructor();
    ctor.setAccessible(true);
    try {
      ctor.newInstance();
      fail("Expected AssertionError from " + clazz.getSimpleName() + " private constructor");
    } catch (java.lang.reflect.InvocationTargetException e) {
      assertTrue("Constructor of " + clazz.getSimpleName() + " must throw AssertionError",
          e.getCause() instanceof AssertionError);
    }
  }

  public static void assertSinglePackagePrivateConstructor(Class<?> clazz) {
    Constructor<?>[] ctors = clazz.getDeclaredConstructors();
    assertEquals(clazz.getSimpleName() + " should have exactly one constructor", 1, ctors.length);
    int mods = ctors[0].getModifiers();
    assertFalse("Constructor must not be public", Modifier.isPublic(mods));
    assertFalse("Constructor must not be private", Modifier.isPrivate(mods));
    assertFalse("Constructor must not be protected", Modifier.isProtected(mods));
  }

  // ── Method assertions ───────────────────────────────────────────

  public static void assertAllPublicMethodsStatic(Class<?> clazz) {
    for (Method m : declaredMethods(clazz)) {
      if (Modifier.isPublic(m.getModifiers())) {
        assertTrue("Public method " + m.getName() + " in " + clazz.getSimpleName() + " must be static",
            Modifier.isStatic(m.getModifiers()));
      }
    }
  }

  public static void assertAllMethodsPackagePrivate(Class<?> clazz) {
    for (Method m : declaredMethods(clazz)) {
      int mods = m.getModifiers();
      assertFalse("Method " + m.getName() + " must not be public", Modifier.isPublic(mods));
      assertFalse("Method " + m.getName() + " must not be private", Modifier.isPrivate(mods));
      assertFalse("Method " + m.getName() + " must not be protected", Modifier.isProtected(mods));
    }
  }

  public static Method findMethod(Class<?> clazz, String name) {
    return Arrays.stream(declaredMethods(clazz))
        .filter(m -> m.getName().equals(name))
        .findFirst().orElse(null);
  }

  public static void assertMethodPresent(Class<?> clazz, String name) {
    assertNotNull("Must declare method '" + name + "' in " + clazz.getSimpleName(),
        findMethod(clazz, name));
  }

  public static void assertMethodAbsent(Class<?> clazz, String name, String ownerDescription) {
    assertNull("Should not declare '" + name + "' (belongs to " + ownerDescription + ")",
        findMethod(clazz, name));
  }

  // ── Class-level assertions ──────────────────────────────────────

  public static void assertFinalPackagePrivateClass(Class<?> clazz) {
    int mods = clazz.getModifiers();
    assertTrue(clazz.getSimpleName() + " must be final", Modifier.isFinal(mods));
    assertFalse(clazz.getSimpleName() + " must not be public", Modifier.isPublic(mods));
    assertFalse(clazz.getSimpleName() + " must not be private", Modifier.isPrivate(mods));
    assertFalse(clazz.getSimpleName() + " must not be protected", Modifier.isProtected(mods));
  }

  public static void assertExtendsOnlyObject(Class<?> clazz) {
    assertEquals(clazz.getSimpleName() + " should extend only Object",
        Object.class, clazz.getSuperclass());
  }

  public static void assertImplementsNoInterfaces(Class<?> clazz) {
    assertEquals(clazz.getSimpleName() + " should implement no interfaces",
        0, clazz.getInterfaces().length);
  }

  public static void assertIsTopLevelClass(Class<?> clazz) {
    assertNull(clazz.getSimpleName() + " must be a top-level class",
        clazz.getEnclosingClass());
  }

  public static void assertUtilityClass(Class<?> clazz) throws Exception {
    assertAllConstructorsPrivate(clazz);
    assertPrivateConstructorThrowsAssertionError(clazz);
    assertAllPublicMethodsStatic(clazz);
  }

  // ── Field assertions ────────────────────────────────────────────

  public static Field findField(Class<?> clazz, String name) {
    return Arrays.stream(declaredFields(clazz))
        .filter(f -> f.getName().equals(name))
        .findFirst().orElse(null);
  }

  public static void assertFieldIsPrivateFinal(Class<?> clazz, String fieldName) {
    Field f = findField(clazz, fieldName);
    assertNotNull(fieldName + " field must exist in " + clazz.getSimpleName(), f);
    assertTrue(fieldName + " must be private", Modifier.isPrivate(f.getModifiers()));
    assertTrue(fieldName + " must be final", Modifier.isFinal(f.getModifiers()));
  }

  public static void assertFieldExists(Class<?> clazz, String fieldName) {
    assertNotNull(clazz.getSimpleName() + " must have field: " + fieldName,
        findField(clazz, fieldName));
  }

  public static void assertNoStaticMembers(Class<?> clazz) {
    for (Field f : declaredFields(clazz)) {
      assertFalse("Field " + f.getName() + " should not be static",
          Modifier.isStatic(f.getModifiers()));
    }
    for (Method m : declaredMethods(clazz)) {
      assertFalse("Method " + m.getName() + " should not be static",
          Modifier.isStatic(m.getModifiers()));
    }
  }
}
