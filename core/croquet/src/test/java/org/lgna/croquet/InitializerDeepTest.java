package org.lgna.croquet;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Deep tests for {@link Initializer} interface — method signature,
 * functional interface conformance, and lambda usage.
 */
public class InitializerDeepTest {

  // ── Interface properties ──────────────────────────────────────────

  @Test
  public void class_isInterface() {
    assertTrue(Initializer.class.isInterface());
  }

  @Test
  public void class_isPublic() {
    assertTrue(Modifier.isPublic(Initializer.class.getModifiers()));
  }

  @Test
  public void class_hasOneMethod() {
    Method[] methods = Initializer.class.getDeclaredMethods();
    assertEquals(1, methods.length);
  }

  @Test
  public void method_initialize_exists() throws Exception {
    Method m = Initializer.class.getMethod("initialize", Object.class);
    assertNotNull(m);
  }

  @Test
  public void method_initialize_returnsVoid() throws Exception {
    Method m = Initializer.class.getMethod("initialize", Object.class);
    assertEquals(void.class, m.getReturnType());
  }

  @Test
  public void method_initialize_takesOneParam() throws Exception {
    Method m = Initializer.class.getMethod("initialize", Object.class);
    assertEquals(1, m.getParameterCount());
  }

  // ── Functional interface usage ────────────────────────────────────

  @Test
  public void lambda_canBeUsed() {
    Initializer<String> init = value -> {};
    assertNotNull(init);
  }

  @Test
  public void lambda_receivesValue() {
    final String[] captured = new String[1];
    Initializer<String> init = value -> captured[0] = value;
    init.initialize("hello");
    assertEquals("hello", captured[0]);
  }

  @Test
  public void lambda_receivesNull() {
    final String[] captured = new String[]{"sentinel"};
    Initializer<String> init = value -> captured[0] = value;
    init.initialize(null);
    assertNull(captured[0]);
  }

  @Test
  public void lambda_integer() {
    final int[] sum = {0};
    Initializer<Integer> init = value -> sum[0] += value;
    init.initialize(5);
    init.initialize(3);
    assertEquals(8, sum[0]);
  }

  @Test
  public void lambda_multipleInvocations() {
    final int[] count = {0};
    Initializer<Object> init = value -> count[0]++;
    init.initialize("a");
    init.initialize("b");
    init.initialize("c");
    assertEquals(3, count[0]);
  }

  // ── Anonymous class usage ─────────────────────────────────────────

  @Test
  public void anonymousClass_canBeUsed() {
    Initializer<String> init = new Initializer<String>() {
      @Override
      public void initialize(String value) {
        // no-op
      }
    };
    assertNotNull(init);
    init.initialize("test");
  }

  // ── Type parameter ────────────────────────────────────────────────

  @Test
  public void typeParameter_exists() {
    assertEquals(1, Initializer.class.getTypeParameters().length);
  }

  @Test
  public void typeParameter_name_isT() {
    assertEquals("T", Initializer.class.getTypeParameters()[0].getName());
  }

  // ── Method reference usage ────────────────────────────────────────

  @Test
  public void methodReference_canBeUsed() {
    StringBuilder sb = new StringBuilder();
    Initializer<String> init = sb::append;
    init.initialize("world");
    assertEquals("world", sb.toString());
  }
}
