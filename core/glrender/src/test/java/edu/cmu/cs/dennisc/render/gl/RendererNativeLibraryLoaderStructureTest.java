package edu.cmu.cs.dennisc.render.gl;


import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Structural tests for {@link RendererNativeLibraryLoader}.
 * Verifies utility-class pattern, idempotent initialization, and
 * the isInitializationAttempted guard flag.
 */
public class RendererNativeLibraryLoaderStructureTest {


  @Test
  public void class_isPublic() {
    assertTrue(Modifier.isPublic(RendererNativeLibraryLoader.class.getModifiers()));
  }

  @Test
  public void constructor_isPrivate() throws Exception {
    Constructor<?> ctor = RendererNativeLibraryLoader.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(ctor.getModifiers()));
  }

  @Test(expected = java.lang.reflect.InvocationTargetException.class)
  public void constructor_throwsAssertionError() throws Exception {
    Constructor<?> ctor = RendererNativeLibraryLoader.class.getDeclaredConstructor();
    ctor.setAccessible(true);
    ctor.newInstance();
  }

  @Test
  public void isInitializationAttempted_fieldExists() throws Exception {
    Field f = RendererNativeLibraryLoader.class.getDeclaredField("isInitializationAttempted");
    f.setAccessible(true);
    assertTrue(Modifier.isStatic(f.getModifiers()));
    assertEquals(boolean.class, f.getType());
  }

  @Test
  public void initializeIfNecessary_isSynchronized() throws Exception {
    Method m = RendererNativeLibraryLoader.class.getDeclaredMethod("initializeIfNecessary");
    assertTrue(Modifier.isSynchronized(m.getModifiers()));
  }

  @Test
  public void initializeIfNecessary_isPublicStatic() throws Exception {
    Method m = RendererNativeLibraryLoader.class.getDeclaredMethod("initializeIfNecessary");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }

  @Test
  public void main_methodExists() throws Exception {
    Method m = RendererNativeLibraryLoader.class.getDeclaredMethod("main", String[].class);
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }
}
