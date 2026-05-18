package org.lgna.croquet;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Extended tests for {@link CancelException} covering all constructor variants,
 * message propagation, cause chaining, and interaction with UserActivity cancel logic.
 */
public class CancelExceptionExtendedTest {

  // ── Default constructor ───────────────────────────────────────────

  @Test
  public void defaultConstructor_noMessage() {
    CancelException ce = new CancelException();
    assertNull(ce.getMessage());
  }

  @Test
  public void defaultConstructor_noCause() {
    CancelException ce = new CancelException();
    assertNull(ce.getCause());
  }

  @Test
  public void defaultConstructor_isRuntimeException() {
    CancelException ce = new CancelException();
    assertTrue(ce instanceof RuntimeException);
  }

  // ── Message constructor ───────────────────────────────────────────

  @Test
  public void messageConstructor_preservesMessage() {
    CancelException ce = new CancelException("user canceled");
    assertEquals("user canceled", ce.getMessage());
  }

  @Test
  public void messageConstructor_noCause() {
    CancelException ce = new CancelException("msg");
    assertNull(ce.getCause());
  }

  @Test
  public void messageConstructor_emptyMessage() {
    CancelException ce = new CancelException("");
    assertEquals("", ce.getMessage());
  }

  @Test
  public void messageConstructor_nullMessage() {
    CancelException ce = new CancelException((String) null);
    assertNull(ce.getMessage());
  }

  // ── Throwable constructor ─────────────────────────────────────────

  @Test
  public void throwableConstructor_preservesCause() {
    RuntimeException cause = new RuntimeException("root");
    CancelException ce = new CancelException(cause);
    assertSame(cause, ce.getCause());
  }

  @Test
  public void throwableConstructor_messageFromCause() {
    RuntimeException cause = new RuntimeException("root");
    CancelException ce = new CancelException(cause);
    assertTrue(ce.getMessage().contains("root"));
  }

  @Test
  public void throwableConstructor_nullCause() {
    CancelException ce = new CancelException((Throwable) null);
    assertNull(ce.getCause());
  }

  // ── Message+Throwable constructor ─────────────────────────────────

  @Test
  public void messageAndCauseConstructor_preservesBoth() {
    RuntimeException cause = new RuntimeException("inner");
    CancelException ce = new CancelException("outer", cause);
    assertEquals("outer", ce.getMessage());
    assertSame(cause, ce.getCause());
  }

  @Test
  public void messageAndCauseConstructor_nullMessage() {
    RuntimeException cause = new RuntimeException("inner");
    CancelException ce = new CancelException(null, cause);
    assertNull(ce.getMessage());
    assertSame(cause, ce.getCause());
  }

  @Test
  public void messageAndCauseConstructor_nullCause() {
    CancelException ce = new CancelException("msg", null);
    assertEquals("msg", ce.getMessage());
    assertNull(ce.getCause());
  }

  @Test
  public void messageAndCauseConstructor_bothNull() {
    CancelException ce = new CancelException(null, null);
    assertNull(ce.getMessage());
    assertNull(ce.getCause());
  }

  // ── Throwability ──────────────────────────────────────────────────

  @Test
  public void canBeCaught_asRuntimeException() {
    try {
      throw new CancelException("test");
    } catch (RuntimeException re) {
      assertTrue(re instanceof CancelException);
    }
  }

  @Test
  public void canBeCaught_asException() {
    try {
      throw new CancelException("test");
    } catch (Exception e) {
      assertTrue(e instanceof CancelException);
    }
  }

  @Test
  public void stackTrace_isPopulated() {
    CancelException ce = new CancelException();
    assertNotNull(ce.getStackTrace());
    assertTrue(ce.getStackTrace().length > 0);
  }

  // ── getCause vs self ──────────────────────────────────────────────
  // This pattern is used in UserActivity.cancel(CancelException)

  @Test
  public void defaultCancelException_causeNotSelf() {
    CancelException ce = new CancelException();
    // getCause() returns null, which != ce → UserActivity treats as ERROR
    assertNotEquals(ce, ce.getCause());
  }

  @Test
  public void withInitCause_causeNotSelf() {
    CancelException ce = new CancelException();
    RuntimeException cause = new RuntimeException();
    ce.initCause(cause);
    assertNotEquals(ce, ce.getCause());
  }

  @Test
  public void selfCause_isDetectable() {
    // This pattern cannot happen normally (initCause(this) throws IllegalArgumentException)
    // but the comparison in UserActivity.cancel checks ce.getCause() != ce
    CancelException ce = new CancelException();
    // Verify the comparison logic: null != ce → true → means "error" path
    assertTrue(ce.getCause() != ce);
  }

  // ── toString ──────────────────────────────────────────────────────

  @Test
  public void toString_containsClassName() {
    CancelException ce = new CancelException();
    assertTrue(ce.toString().contains("CancelException"));
  }

  @Test
  public void toString_withMessage_containsMessage() {
    CancelException ce = new CancelException("dialog closed");
    assertTrue(ce.toString().contains("dialog closed"));
  }

  // ── Chained causes ────────────────────────────────────────────────

  @Test
  public void chainedCauses_preserveOrder() {
    Exception root = new Exception("root");
    RuntimeException middle = new RuntimeException("middle", root);
    CancelException top = new CancelException(middle);
    assertSame(middle, top.getCause());
    assertSame(root, top.getCause().getCause());
  }

  @Test
  public void suppressed_initiallyEmpty() {
    CancelException ce = new CancelException();
    assertEquals(0, ce.getSuppressed().length);
  }

  @Test
  public void addSuppressed_preserved() {
    CancelException ce = new CancelException();
    ce.addSuppressed(new RuntimeException("suppressed"));
    assertEquals(1, ce.getSuppressed().length);
  }
}
