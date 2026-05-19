package org.lgna.croquet;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Coverage tests for {@link SingleSelectTableRowState} — table row selection state.
 * Reflection-only since headless instantiation requires table model infrastructure.
 */
public class SingleSelectTableRowStateCoverageTest {

  // ── Class hierarchy ───────────────────────────────────────────────

  @Test
  public void class_extendsState() {
    assertTrue(State.class.isAssignableFrom(SingleSelectTableRowState.class));
  }

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(SingleSelectTableRowState.class.getModifiers()));
  }

  @Test
  public void class_isPublic() {
    assertTrue(Modifier.isPublic(SingleSelectTableRowState.class.getModifiers()));
  }

  @Test
  public void class_hasTypeParameters() {
    assertTrue(SingleSelectTableRowState.class.getTypeParameters().length > 0);
  }

  // ── Constructor ───────────────────────────────────────────────────

  @Test
  public void constructors_exist() {
    Constructor<?>[] ctors = SingleSelectTableRowState.class.getDeclaredConstructors();
    assertTrue(ctors.length > 0);
  }

  // ── Key methods ───────────────────────────────────────────────────

  @Test
  public void method_getValue_inherited() throws Exception {
    assertNotNull(SingleSelectTableRowState.class.getMethod("getValue"));
  }

  @Test
  public void method_addValueListener_inherited() {
    boolean found = false;
    for (Method m : SingleSelectTableRowState.class.getMethods()) {
      if ("addValueListener".equals(m.getName())) {
        found = true;
        break;
      }
    }
    assertTrue("Expected addValueListener", found);
  }

  @Test
  public void method_getSwingModel_declared() {
    assertMethodExists("getSwingModel");
  }

  @Test
  public void method_getItemCount_declared() {
    assertMethodExists("getItemCount");
  }

  @Test
  public void method_getItemAt_declared() {
    assertMethodExists("getItemAt");
  }

  // ── Declared method count ─────────────────────────────────────────

  @Test
  public void declaredMethodCount_reasonable() {
    assertTrue(SingleSelectTableRowState.class.getDeclaredMethods().length >= 5);
  }

  @Test
  public void class_isNotFinal() {
    assertFalse(Modifier.isFinal(SingleSelectTableRowState.class.getModifiers()));
  }

  private void assertMethodExists(String name) {
    boolean found = false;
    for (Method m : SingleSelectTableRowState.class.getDeclaredMethods()) {
      if (m.getName().equals(name)) {
        found = true;
        break;
      }
    }
    assertTrue("Expected method: " + name, found);
  }
}
