package org.lgna.croquet;

import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class UnsupportedGenerationExceptionTest {

  private String message;
  private Throwable cause;

  @Before
  public void setUp() {
    message = "not supported";
    cause = new IllegalStateException("root cause");
  }

  // ── Hierarchy ──────────────────────────────────────────────────────

  @Test
  public void extendsException() {
    assertTrue(Exception.class.isAssignableFrom(UnsupportedGenerationException.class));
  }

  @Test
  public void isCheckedException() {
    assertFalse(RuntimeException.class.isAssignableFrom(UnsupportedGenerationException.class));
  }

  @Test
  public void serializable() {
    assertTrue(Serializable.class.isAssignableFrom(UnsupportedGenerationException.class));
  }

  // ── Constructors ───────────────────────────────────────────────────

  @Test
  public void noArg_constructor() {
    UnsupportedGenerationException exception = new UnsupportedGenerationException();

    assertNotNull(exception);
    assertNull(exception.getMessage());
    assertNull(exception.getCause());
  }

  @Test
  public void message_constructor() {
    UnsupportedGenerationException exception = new UnsupportedGenerationException(message);

    assertEquals(message, exception.getMessage());
    assertNull(exception.getCause());
  }

  @Test
  public void messageAndCause_constructor() {
    UnsupportedGenerationException exception = new UnsupportedGenerationException(message, cause);

    assertEquals(message, exception.getMessage());
    assertSame(cause, exception.getCause());
  }

  @Test
  public void cause_constructor() {
    UnsupportedGenerationException exception = new UnsupportedGenerationException(cause);

    assertEquals(cause.toString(), exception.getMessage());
    assertSame(cause, exception.getCause());
  }

  // ── Accessors ──────────────────────────────────────────────────────

  @Test
  public void getMessage_returnsCorrectMessage() {
    assertEquals(message, new UnsupportedGenerationException(message).getMessage());
  }

  @Test
  public void getCause_returnsCorrectCause() {
    assertSame(cause, new UnsupportedGenerationException(message, cause).getCause());
  }

  // ── Reflection ─────────────────────────────────────────────────────

  @Test
  public void constructorSignatures_viaReflection() {
    Constructor<?>[] constructors = UnsupportedGenerationException.class.getDeclaredConstructors();
    Set<String> signatures = new HashSet<String>();
    for (Constructor<?> constructor : constructors) {
      signatures.add(Arrays.toString(constructor.getParameterTypes()));
    }

    assertEquals(4, constructors.length);
    assertTrue(signatures.contains("[]"));
    assertTrue(signatures.contains("[class java.lang.String]"));
    assertTrue(signatures.contains("[class java.lang.String, class java.lang.Throwable]"));
    assertTrue(signatures.contains("[class java.lang.Throwable]"));
  }
}
