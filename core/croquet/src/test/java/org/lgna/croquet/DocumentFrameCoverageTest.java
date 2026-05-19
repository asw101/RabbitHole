package org.lgna.croquet;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link DocumentFrame} — manages application document frame.
 * Reflection-only since headless instantiation is not possible.
 */
public class DocumentFrameCoverageTest {

  // ── Class hierarchy ───────────────────────────────────────────────

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(DocumentFrame.class.getModifiers()));
  }

  @Test
  public void class_isPublic() {
    assertTrue(Modifier.isPublic(DocumentFrame.class.getModifiers()));
  }

  @Test
  public void class_hasNoTypeParameters() {
    assertEquals(0, DocumentFrame.class.getTypeParameters().length);
  }

  // ── Constructor ───────────────────────────────────────────────────

  @Test
  public void constructors_exist() {
    Constructor<?>[] ctors = DocumentFrame.class.getDeclaredConstructors();
    assertTrue(ctors.length > 0);
  }

  // ── Key methods ───────────────────────────────────────────────────

  @Test
  public void method_getFrame_exists() {
    assertMethodExists("getFrame");
  }

  @Test
  public void method_getDocument_exists() {
    assertMethodExists("getDocument");
  }

  @Test
  public void method_peekWindow_exists() {
    assertMethodExists("peekWindow");
  }

  @Test
  public void method_pushWindow_exists() {
    assertMethodExists("pushWindow");
  }

  // ── Declared method count ─────────────────────────────────────────

  @Test
  public void declaredMethodCount_reasonable() {
    assertTrue(DocumentFrame.class.getDeclaredMethods().length >= 5);
  }

  @Test
  public void class_isNotFinal() {
    assertFalse(Modifier.isFinal(DocumentFrame.class.getModifiers()));
  }

  private void assertMethodExists(String name) {
    boolean found = false;
    for (Method m : DocumentFrame.class.getDeclaredMethods()) {
      if (m.getName().equals(name)) {
        found = true;
        break;
      }
    }
    assertTrue("Expected method: " + name, found);
  }
}
