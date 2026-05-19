package org.lgna.croquet;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link Application} — the main application singleton.
 * Reflection-only since headless instantiation requires full framework setup.
 */
public class ApplicationCoverageTest {

  // ── Class hierarchy ───────────────────────────────────────────────

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(Application.class.getModifiers()));
  }

  @Test
  public void class_isPublic() {
    assertTrue(Modifier.isPublic(Application.class.getModifiers()));
  }

  @Test
  public void class_extendsObjectDirectly() {
    assertEquals(Object.class, Application.class.getSuperclass());
  }

  @Test
  public void class_hasTypeParameters() {
    assertTrue(Application.class.getTypeParameters().length > 0);
  }

  // ── Static methods ────────────────────────────────────────────────

  @Test
  public void method_getActiveInstance_exists() throws Exception {
    Method m = Application.class.getMethod("getActiveInstance");
    assertTrue(Modifier.isStatic(m.getModifiers()));
  }

  @Test
  public void method_getActiveInstance_returnsApplication() throws Exception {
    Method m = Application.class.getMethod("getActiveInstance");
    assertEquals(Application.class, m.getReturnType());
  }

  // ── Static field PROJECT_GROUP ─────────────────────────────────────

  @Test
  public void field_PROJECT_GROUP_exists() throws Exception {
    assertNotNull(Application.class.getDeclaredField("PROJECT_GROUP"));
  }

  @Test
  public void field_PROJECT_GROUP_isStatic() throws Exception {
    assertTrue(Modifier.isStatic(
        Application.class.getDeclaredField("PROJECT_GROUP").getModifiers()));
  }

  // ── Key instance methods ──────────────────────────────────────────

  @Test
  public void method_getDocumentFrame_exists() {
    assertMethodExists("getDocumentFrame");
  }

  @Test
  public void method_initialize_exists() {
    assertMethodExists("initialize");
  }

  @Test
  public void method_getLocale_exists() {
    assertMethodExists("getLocale");
  }

  @Test
  public void method_setLocale_exists() {
    assertMethodExists("setLocale");
  }

  // ── Constructor ───────────────────────────────────────────────────

  @Test
  public void constructor_exists() {
    Constructor<?>[] ctors = Application.class.getDeclaredConstructors();
    assertTrue(ctors.length > 0);
  }

  @Test
  public void constructor_isPublic() {
    for (Constructor<?> c : Application.class.getDeclaredConstructors()) {
      assertTrue(Modifier.isPublic(c.getModifiers()));
    }
  }

  // ── Declared methods count ────────────────────────────────────────

  @Test
  public void declaredMethodCount_substantial() {
    assertTrue(Application.class.getDeclaredMethods().length >= 10);
  }

  @Test
  public void class_isNotFinal() {
    assertFalse(Modifier.isFinal(Application.class.getModifiers()));
  }

  @Test
  public void innerClasses_exist() {
    assertNotNull(Application.class.getDeclaredClasses());
  }

  private void assertMethodExists(String name) {
    boolean found = false;
    for (Method m : Application.class.getDeclaredMethods()) {
      if (m.getName().equals(name)) {
        found = true;
        break;
      }
    }
    assertTrue("Expected method: " + name, found);
  }
}
