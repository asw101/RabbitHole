package org.alice.stageide.sceneeditor;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Tests for {@link ThumbnailGenerator} — class structure and API surface.
 * The actual rendering requires OpenGL context, so these tests verify
 * the structural contract without invoking the renderer.
 */
public class ThumbnailGeneratorTest {

  // ---- class structure --------------------------------------------------

  @Test
  public void classIsLoadable() throws ClassNotFoundException {
    Class.forName("org.alice.stageide.sceneeditor.ThumbnailGenerator");
  }

  @Test
  public void classIsFinal() {
    assertTrue(Modifier.isFinal(ThumbnailGenerator.class.getModifiers()));
  }

  @Test
  public void classIsNotAbstract() {
    assertFalse(Modifier.isAbstract(ThumbnailGenerator.class.getModifiers()));
  }

  // ---- constructor ------------------------------------------------------

  @Test
  public void constructor_acceptsWidthAndHeight() throws NoSuchMethodException {
    Constructor<?> ctor = ThumbnailGenerator.class.getConstructor(int.class, int.class);
    assertTrue(Modifier.isPublic(ctor.getModifiers()));
  }

  @Test
  public void noDefaultConstructor() {
    Constructor<?>[] ctors = ThumbnailGenerator.class.getConstructors();
    for (var ctor : ctors) {
      assertTrue("should require width/height", ctor.getParameterCount() > 0);
    }
  }

  // ---- createThumbnail method -------------------------------------------

  @Test
  public void createThumbnailMethod_exists() throws NoSuchMethodException {
    Method m = ThumbnailGenerator.class.getMethod("createThumbnail");
    assertTrue(Modifier.isPublic(m.getModifiers()));
    assertEquals(java.awt.image.BufferedImage.class, m.getReturnType());
  }

  @Test
  public void createThumbnailMethod_takesNoArgs() throws NoSuchMethodException {
    Method m = ThumbnailGenerator.class.getMethod("createThumbnail");
    assertEquals(0, m.getParameterCount());
  }

  // ---- no static state --------------------------------------------------

  @Test
  public void noStaticMutableFields() {
    for (var field : ThumbnailGenerator.class.getDeclaredFields()) {
      if (Modifier.isStatic(field.getModifiers())) {
        assertTrue("static field " + field.getName() + " should be final",
            Modifier.isFinal(field.getModifiers()));
      }
    }
  }
}
