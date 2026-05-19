package org.lgna.croquet;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link UnsupportedGenerationException} — all four constructors,
 * message propagation, cause chaining, and hierarchy verification.
 */
public class UnsupportedGenerationExceptionTest {

  // ── Hierarchy ──────────────────────────────────────────────────────

  @Test
  public void class_extendsException() {
    assertTrue(Exception.class.isAssignableFrom(UnsupportedGenerationException.class));
  }

  @Test
  public void class_extendsThrowable() {
    assertTrue(Throwable.class.isAssignableFrom(UnsupportedGenerationException.class));
  }

  @Test
  public void class_isNotAbstract() {
    assertFalse(java.lang.reflect.Modifier.isAbstract(
        UnsupportedGenerationException.class.getModifiers()));
  }

  @Test
  public void class_isChecked() {
    assertFalse(RuntimeException.class.isAssignableFrom(
        UnsupportedGenerationException.class));
  }

  // ── No-arg constructor ────────────────────────────────────────────

  @Test
  public void noArgConstructor_createsInstance() {
    UnsupportedGenerationException ex = new UnsupportedGenerationException();
    assertNotNull(ex);
  }

  @Test
  public void noArgConstructor_messageIsNull() {
    UnsupportedGenerationException ex = new UnsupportedGenerationException();
    assertNull(ex.getMessage());
  }

  @Test
  public void noArgConstructor_causeIsNull() {
    UnsupportedGenerationException ex = new UnsupportedGenerationException();
    assertNull(ex.getCause());
  }

  // ── String constructor ────────────────────────────────────────────

  @Test
  public void stringConstructor_setsMessage() {
    UnsupportedGenerationException ex =
        new UnsupportedGenerationException("test msg");
    assertEquals("test msg", ex.getMessage());
  }

  @Test
  public void stringConstructor_nullMessage() {
    UnsupportedGenerationException ex =
        new UnsupportedGenerationException((String) null);
    assertNull(ex.getMessage());
  }

  @Test
  public void stringConstructor_emptyMessage() {
    UnsupportedGenerationException ex =
        new UnsupportedGenerationException("");
    assertEquals("", ex.getMessage());
  }

  @Test
  public void stringConstructor_causeIsNull() {
    UnsupportedGenerationException ex =
        new UnsupportedGenerationException("msg");
    assertNull(ex.getCause());
  }

  // ── String + Throwable constructor ────────────────────────────────

  @Test
  public void stringThrowableConstructor_setsMessage() {
    Throwable cause = new RuntimeException("root");
    UnsupportedGenerationException ex =
        new UnsupportedGenerationException("wrapper", cause);
    assertEquals("wrapper", ex.getMessage());
  }

  @Test
  public void stringThrowableConstructor_setsCause() {
    Throwable cause = new RuntimeException("root");
    UnsupportedGenerationException ex =
        new UnsupportedGenerationException("wrapper", cause);
    assertSame(cause, ex.getCause());
  }

  @Test
  public void stringThrowableConstructor_nullMessage() {
    Throwable cause = new RuntimeException("root");
    UnsupportedGenerationException ex =
        new UnsupportedGenerationException(null, cause);
    assertNull(ex.getMessage());
  }

  @Test
  public void stringThrowableConstructor_nullCause() {
    UnsupportedGenerationException ex =
        new UnsupportedGenerationException("msg", null);
    assertNull(ex.getCause());
  }

  @Test
  public void stringThrowableConstructor_bothNull() {
    UnsupportedGenerationException ex =
        new UnsupportedGenerationException(null, null);
    assertNull(ex.getMessage());
    assertNull(ex.getCause());
  }

  // ── Throwable constructor ─────────────────────────────────────────

  @Test
  public void throwableConstructor_setsCause() {
    Throwable cause = new IllegalStateException("bad");
    UnsupportedGenerationException ex =
        new UnsupportedGenerationException(cause);
    assertSame(cause, ex.getCause());
  }

  @Test
  public void throwableConstructor_messageContainsCauseInfo() {
    Throwable cause = new IllegalStateException("bad");
    UnsupportedGenerationException ex =
        new UnsupportedGenerationException(cause);
    assertNotNull(ex.getMessage());
    assertTrue(ex.getMessage().contains("bad"));
  }

  @Test
  public void throwableConstructor_nullCause() {
    UnsupportedGenerationException ex =
        new UnsupportedGenerationException((Throwable) null);
    assertNull(ex.getCause());
  }

  // ── Throwability ──────────────────────────────────────────────────

  @Test
  public void canBeThrown() {
    try {
      throw new UnsupportedGenerationException("thrown");
    } catch (UnsupportedGenerationException ex) {
      assertEquals("thrown", ex.getMessage());
    }
  }

  @Test
  public void canBeCaughtAsException() {
    try {
      throw new UnsupportedGenerationException("test");
    } catch (Exception ex) {
      assertTrue(ex instanceof UnsupportedGenerationException);
    }
  }

  // ── Stack trace ───────────────────────────────────────────────────

  @Test
  public void stackTrace_nonEmpty() {
    UnsupportedGenerationException ex =
        new UnsupportedGenerationException("trace");
    assertTrue(ex.getStackTrace().length > 0);
  }

  @Test
  public void toString_containsClassName() {
    UnsupportedGenerationException ex =
        new UnsupportedGenerationException("detail");
    assertTrue(ex.toString().contains("UnsupportedGenerationException"));
  }

  @Test
  public void toString_containsMessage() {
    UnsupportedGenerationException ex =
        new UnsupportedGenerationException("detail");
    assertTrue(ex.toString().contains("detail"));
  }

  // ── Constructor count ─────────────────────────────────────────────

  @Test
  public void hasFourConstructors() {
    assertEquals(4,
        UnsupportedGenerationException.class.getDeclaredConstructors().length);
  }
}
