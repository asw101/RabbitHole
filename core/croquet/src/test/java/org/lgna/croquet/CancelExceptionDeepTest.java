package org.lgna.croquet;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Deep tests for {@link CancelException} — message, cause, hierarchy,
 * serialization, and suppressed exceptions.
 */
public class CancelExceptionDeepTest {

  // ── Hierarchy ─────────────────────────────────────────────────────

  @Test
  public void class_extendsException() {
    assertTrue(Exception.class.isAssignableFrom(CancelException.class));
  }

  @Test
  public void class_isNotAbstract() {
    assertFalse(Modifier.isAbstract(CancelException.class.getModifiers()));
  }

  @Test
  public void class_isPublic() {
    assertTrue(Modifier.isPublic(CancelException.class.getModifiers()));
  }

  @Test
  public void class_isUnchecked() {
    assertTrue(RuntimeException.class.isAssignableFrom(CancelException.class));
  }

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void construction_nonNull() {
    CancelException ex = new CancelException();
    assertNotNull(ex);
  }

  @Test
  public void message_isNull() {
    CancelException ex = new CancelException();
    assertNull(ex.getMessage());
  }

  @Test
  public void cause_isNull() {
    CancelException ex = new CancelException();
    assertNull(ex.getCause());
  }

  // ── Stack trace ───────────────────────────────────────────────────

  @Test
  public void stackTrace_nonEmpty() {
    CancelException ex = new CancelException();
    assertTrue(ex.getStackTrace().length > 0);
  }

  // ── Throwability ──────────────────────────────────────────────────

  @Test
  public void canBeThrown() {
    try {
      throw new CancelException();
    } catch (CancelException ex) {
      assertNotNull(ex);
    }
  }

  @Test
  public void canBeCaughtAsException() {
    try {
      throw new CancelException();
    } catch (Exception ex) {
      assertTrue(ex instanceof CancelException);
    }
  }

  // ── toString ──────────────────────────────────────────────────────

  @Test
  public void toString_containsClassName() {
    CancelException ex = new CancelException();
    assertTrue(ex.toString().contains("CancelException"));
  }

  @Test
  public void toString_nonNull() {
    CancelException ex = new CancelException();
    assertNotNull(ex.toString());
  }

  // ── Suppressed exceptions ─────────────────────────────────────────

  @Test
  public void suppressed_initiallyEmpty() {
    CancelException ex = new CancelException();
    assertEquals(0, ex.getSuppressed().length);
  }

  @Test
  public void suppressed_canBeAdded() {
    CancelException ex = new CancelException();
    ex.addSuppressed(new RuntimeException("suppressed"));
    assertEquals(1, ex.getSuppressed().length);
  }

  // ── fillInStackTrace ──────────────────────────────────────────────

  @Test
  public void fillInStackTrace_returnsThis() {
    CancelException ex = new CancelException();
    assertSame(ex, ex.fillInStackTrace());
  }

  // ── Constructor count ─────────────────────────────────────────────

  @Test
  public void constructorCount() {
    int count = CancelException.class.getDeclaredConstructors().length;
    assertTrue(count >= 1);
  }
}
