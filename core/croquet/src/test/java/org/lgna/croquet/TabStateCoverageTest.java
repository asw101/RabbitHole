package org.lgna.croquet;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;

import static org.junit.Assert.*;

/**
 * Reflection-only coverage tests for {@link TabState}. Cannot instantiate
 * headlessly because it requires TabComposite infrastructure.
 */
public class TabStateCoverageTest {

  // ── Class hierarchy ───────────────────────────────────────────────

  @Test
  public void class_extendsSingleSelectListState() {
    assertTrue(SingleSelectListState.class.isAssignableFrom(TabState.class));
  }

  @Test
  public void class_extendsItemState() {
    assertTrue(ItemState.class.isAssignableFrom(TabState.class));
  }

  @Test
  public void class_extendsState() {
    assertTrue(State.class.isAssignableFrom(TabState.class));
  }

  @Test
  public void class_isAbstract() {
    assertTrue(Modifier.isAbstract(TabState.class.getModifiers()));
  }

  // ── Type parameters ───────────────────────────────────────────────

  @Test
  public void class_hasTwoTypeParameters() {
    TypeVariable<?>[] typeParams = TabState.class.getTypeParameters();
    assertEquals(2, typeParams.length);
  }

  // ── Constructor ───────────────────────────────────────────────────

  @Test
  public void constructor_exists_groupUuidIntData() throws Exception {
    Constructor<?>[] ctors = TabState.class.getDeclaredConstructors();
    boolean found = false;
    for (Constructor<?> c : ctors) {
      Class<?>[] params = c.getParameterTypes();
      if (params.length == 4
          && params[0] == Group.class
          && params[1] == java.util.UUID.class
          && params[2] == int.class) {
        found = true;
        break;
      }
    }
    assertTrue("Expected 4-arg constructor", found);
  }

  // ── Implements Activatable ────────────────────────────────────────

  @Test
  public void class_implementsActivatable() {
    boolean found = false;
    for (Class<?> iface : TabState.class.getInterfaces()) {
      if (iface.getSimpleName().equals("Activatable")) {
        found = true;
        break;
      }
    }
    assertTrue("Expected Activatable interface", found);
  }
}
