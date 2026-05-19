package org.lgna.croquet.codecs;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Reflection-based structure tests for {@link SimpleTabCompositeCodec}.
 * Cannot instantiate without AbstractTabComposite infrastructure.
 */
public class SimpleTabCompositeCodecCoverageTest {

  // ── Class structure ───────────────────────────────────────────────

  @Test
  public void simpleTabCompositeCodec_implementsItemCodec() {
    assertTrue(org.lgna.croquet.ItemCodec.class.isAssignableFrom(SimpleTabCompositeCodec.class));
  }

  @Test
  public void simpleTabCompositeCodec_isPublic() {
    assertTrue(Modifier.isPublic(SimpleTabCompositeCodec.class.getModifiers()));
  }

  @Test
  public void simpleTabCompositeCodec_isNotAbstract() {
    assertFalse(Modifier.isAbstract(SimpleTabCompositeCodec.class.getModifiers()));
  }

  // ── Method presence ───────────────────────────────────────────────

  @Test
  public void hasGetValueClass() {
    assertHasMethod(SimpleTabCompositeCodec.class, "getValueClass");
  }

  @Test
  public void hasDecodeValue() {
    assertHasMethod(SimpleTabCompositeCodec.class, "decodeValue");
  }

  @Test
  public void hasEncodeValue() {
    assertHasMethod(SimpleTabCompositeCodec.class, "encodeValue");
  }

  @Test
  public void hasAppendRepresentation() {
    assertHasMethod(SimpleTabCompositeCodec.class, "appendRepresentation");
  }

  // ── Constructor presence ──────────────────────────────────────────

  @Test
  public void hasConstructor() {
    Constructor<?>[] ctors = SimpleTabCompositeCodec.class.getDeclaredConstructors();
    assertTrue("Should have at least one constructor", ctors.length > 0);
  }

  // ── Package ───────────────────────────────────────────────────────

  @Test
  public void isInCodecsPackage() {
    assertEquals("org.lgna.croquet.codecs", SimpleTabCompositeCodec.class.getPackage().getName());
  }

  private static void assertHasMethod(Class<?> cls, String methodName) {
    for (Method m : cls.getMethods()) {
      if (methodName.equals(m.getName())) {
        return;
      }
    }
    fail(cls.getSimpleName() + " should have method " + methodName);
  }
}
