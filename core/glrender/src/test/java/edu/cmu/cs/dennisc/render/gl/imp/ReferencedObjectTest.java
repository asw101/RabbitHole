package edu.cmu.cs.dennisc.render.gl.imp;

import org.junit.Test;

import java.lang.reflect.Constructor;

import static org.junit.Assert.*;

/**
 * Tests for {@link ReferencedObject} — pure reference-counting logic.
 * Uses reflection since the class is package-private.
 */
public class ReferencedObjectTest {

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void constructor_setsObject() throws Exception {
    Object ref = newReferencedObject("hello", 1);
    assertEquals("hello", getObject(ref));
  }

  @Test
  public void constructor_nullObject() throws Exception {
    Object ref = newReferencedObject(null, 0);
    assertNull(getObject(ref));
  }

  @Test
  public void constructor_positiveCount_isReferenced() throws Exception {
    Object ref = newReferencedObject("x", 1);
    assertTrue(isReferenced(ref));
  }

  @Test
  public void constructor_zeroCount_notReferenced() throws Exception {
    Object ref = newReferencedObject("x", 0);
    assertFalse(isReferenced(ref));
  }

  @Test
  public void constructor_negativeCount_notReferenced() throws Exception {
    Object ref = newReferencedObject("x", -1);
    assertFalse(isReferenced(ref));
  }

  // ── addReference ─────────────────────────────────────────────────

  @Test
  public void addReference_zeroToOne() throws Exception {
    Object ref = newReferencedObject("x", 0);
    addReference(ref);
    assertTrue(isReferenced(ref));
  }

  @Test
  public void addReference_incrementsCount() throws Exception {
    Object ref = newReferencedObject("x", 2);
    addReference(ref);
    assertTrue(isReferenced(ref));
  }

  // ── removeReference ──────────────────────────────────────────────

  @Test
  public void removeReference_oneToZero() throws Exception {
    Object ref = newReferencedObject("x", 1);
    removeReference(ref);
    assertFalse(isReferenced(ref));
  }

  @Test
  public void removeReference_twoToOne() throws Exception {
    Object ref = newReferencedObject("x", 2);
    removeReference(ref);
    assertTrue(isReferenced(ref));
  }

  // ── add then remove cycle ────────────────────────────────────────

  @Test
  public void addRemoveCycle_returnsToOriginal() throws Exception {
    Object ref = newReferencedObject("x", 0);
    assertFalse(isReferenced(ref));
    addReference(ref);
    assertTrue(isReferenced(ref));
    removeReference(ref);
    assertFalse(isReferenced(ref));
  }

  @Test
  public void multipleAddRemove_tracksCorrectly() throws Exception {
    Object ref = newReferencedObject("x", 0);
    addReference(ref);
    addReference(ref);
    addReference(ref);
    assertTrue(isReferenced(ref));
    removeReference(ref);
    removeReference(ref);
    assertTrue(isReferenced(ref));
    removeReference(ref);
    assertFalse(isReferenced(ref));
  }

  // ── getObject is stable ──────────────────────────────────────────

  @Test
  public void getObject_unchangedByRefCounting() throws Exception {
    String obj = "stable";
    Object ref = newReferencedObject(obj, 0);
    addReference(ref);
    removeReference(ref);
    assertSame(obj, getObject(ref));
  }

  // ── Helpers — all reflection cached in static init ─────────────────

  private static final Class<?> CLAZZ;
  private static final Constructor<?> CTOR;
  private static final java.lang.reflect.Method GET_OBJECT;
  private static final java.lang.reflect.Method IS_REFERENCED;
  private static final java.lang.reflect.Method ADD_REFERENCE;
  private static final java.lang.reflect.Method REMOVE_REFERENCE;

  static {
    try {
      CLAZZ = Class.forName("edu.cmu.cs.dennisc.render.gl.imp.ReferencedObject");
      CTOR = CLAZZ.getDeclaredConstructor(Object.class, int.class);
      CTOR.setAccessible(true);
      GET_OBJECT = CLAZZ.getDeclaredMethod("getObject");
      GET_OBJECT.setAccessible(true);
      IS_REFERENCED = CLAZZ.getDeclaredMethod("isReferenced");
      IS_REFERENCED.setAccessible(true);
      ADD_REFERENCE = CLAZZ.getDeclaredMethod("addReference");
      ADD_REFERENCE.setAccessible(true);
      REMOVE_REFERENCE = CLAZZ.getDeclaredMethod("removeReference");
      REMOVE_REFERENCE.setAccessible(true);
    } catch (Exception e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  private static Object newReferencedObject(Object obj, int count) throws Exception {
    return CTOR.newInstance(obj, count);
  }

  private static Object getObject(Object ref) throws Exception {
    return GET_OBJECT.invoke(ref);
  }

  private static boolean isReferenced(Object ref) throws Exception {
    return (boolean) IS_REFERENCED.invoke(ref);
  }

  private static void addReference(Object ref) throws Exception {
    ADD_REFERENCE.invoke(ref);
  }

  private static void removeReference(Object ref) throws Exception {
    REMOVE_REFERENCE.invoke(ref);
  }
}
