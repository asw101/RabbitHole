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
  public void colorState_hasGetValue() {
    assertHasMethod(State.class, "getValue");
  }

  @Test
  public void colorState_hasSetValueTransactionlessly() {
    assertHasMethod(State.class, "setValueTransactionlessly");
  }

  @Test
  public void colorState_hasDecodeValue() {
    assertHasMethod(ColorState.class, "decodeValue");
  }

  @Test
  public void colorState_hasEncodeValue() {
    assertHasMethod(ColorState.class, "encodeValue");
  }

  @Test
  public void colorState_hasAppendRepresentation() {
    assertHasMethod(ColorState.class, "appendRepresentation");
  }

  private static void assertHasMethod(Class<?> cls, String methodName) {
    for (Method m : cls.getMethods()) {
      if (methodName.equals(m.getName())) {
        return;
      }
    }
    fail(cls.getSimpleName() + " should have method " + methodName);
  }

  // ── Package ───────────────────────────────────────────────────────

  @Test
  public void colorState_isInColorPackage() {
    assertEquals("org.lgna.croquet.color", ColorState.class.getPackage().getName());
  }
}
