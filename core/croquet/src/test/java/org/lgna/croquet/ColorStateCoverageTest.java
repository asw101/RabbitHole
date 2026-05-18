package org.lgna.croquet;

import org.junit.Test;
import org.lgna.croquet.color.ColorState;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Reflection-only coverage tests for {@link ColorState} — class structure,
 * hierarchy, and method presence. Cannot instantiate headlessly due to
 * Application dependency in constructor.
 */
public class ColorStateCoverageTest {

  // ── Class hierarchy ───────────────────────────────────────────────

  @Test
  public void colorState_extendsItemState() {
    assertTrue(ItemState.class.isAssignableFrom(ColorState.class));
  }

  @Test
  public void colorState_extendsState() {
    assertTrue(State.class.isAssignableFrom(ColorState.class));
  }

  @Test
  public void colorState_extendsAbstractCompletionModel() {
    assertTrue(AbstractCompletionModel.class.isAssignableFrom(ColorState.class));
  }

  @Test
  public void colorState_isAbstract() {
    assertTrue(Modifier.isAbstract(ColorState.class.getModifiers()));
  }

  @Test
  public void colorState_isPublic() {
    assertTrue(Modifier.isPublic(ColorState.class.getModifiers()));
  }

  // ── Method presence ───────────────────────────────────────────────

  @Test
  public void colorState_hasGetValue() throws NoSuchMethodException {
    assertNotNull(State.class.getMethod("getValue"));
  }

  @Test
  public void colorState_hasSetValueTransactionlessly() throws NoSuchMethodException {
    assertNotNull(State.class.getMethod("setValueTransactionlessly", Object.class));
  }

  @Test
  public void colorState_hasDecodeValue() throws NoSuchMethodException {
    // decodeValue is abstract in State, must be present
    Method[] methods = ColorState.class.getMethods();
    boolean found = false;
    for (Method m : methods) {
      if ("decodeValue".equals(m.getName())) {
        found = true;
        break;
      }
    }
    assertTrue("ColorState should have decodeValue method", found);
  }

  @Test
  public void colorState_hasEncodeValue() {
    Method[] methods = ColorState.class.getMethods();
    boolean found = false;
    for (Method m : methods) {
      if ("encodeValue".equals(m.getName())) {
        found = true;
        break;
      }
    }
    assertTrue("ColorState should have encodeValue method", found);
  }

  @Test
  public void colorState_hasAppendRepresentation() {
    Method[] methods = ColorState.class.getMethods();
    boolean found = false;
    for (Method m : methods) {
      if ("appendRepresentation".equals(m.getName())) {
        found = true;
        break;
      }
    }
    assertTrue("ColorState should have appendRepresentation method", found);
  }

  // ── Package ───────────────────────────────────────────────────────

  @Test
  public void colorState_isInColorPackage() {
    assertEquals("org.lgna.croquet.color", ColorState.class.getPackage().getName());
  }
}
