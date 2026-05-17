package edu.cmu.cs.dennisc.java.lang;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for ThrowableUtilities — stack trace→String and →byte[] conversions.
 */
public class ThrowableUtilitiesTest {

  @Test
  public void getStackTraceAsString_notNull() {
    Throwable t = new RuntimeException("test exception");
    String result = ThrowableUtilities.getStackTraceAsString(t);
    assertNotNull(result);
    assertTrue(result.length() > 0);
  }

  @Test
  public void getStackTraceAsString_containsMessage() {
    Throwable t = new RuntimeException("unique-error-message");
    String result = ThrowableUtilities.getStackTraceAsString(t);
    assertTrue(result.contains("unique-error-message"));
  }

  @Test
  public void getStackTraceAsString_containsClassName() {
    Throwable t = new IllegalArgumentException("arg error");
    String result = ThrowableUtilities.getStackTraceAsString(t);
    assertTrue(result.contains("IllegalArgumentException"));
  }

  @Test
  public void getStackTraceAsString_containsStackFrames() {
    Throwable t = new RuntimeException("with stack");
    String result = ThrowableUtilities.getStackTraceAsString(t);
    assertTrue(result.contains("at "));
  }

  @Test
  public void getStackTraceAsString_nestedCause() {
    Throwable cause = new java.io.IOException("io error");
    Throwable t = new RuntimeException("wrapper", cause);
    String result = ThrowableUtilities.getStackTraceAsString(t);
    assertTrue(result.contains("io error"));
    assertTrue(result.contains("Caused by"));
  }

  @Test
  public void getStackTraceAsByteArray_notNull() {
    Throwable t = new RuntimeException("byte test");
    byte[] result = ThrowableUtilities.getStackTraceAsByteArray(t);
    assertNotNull(result);
    assertTrue(result.length > 0);
  }

  @Test
  public void getStackTraceAsByteArray_containsMessage() {
    Throwable t = new RuntimeException("byte-message");
    byte[] result = ThrowableUtilities.getStackTraceAsByteArray(t);
    String asString = new String(result);
    assertTrue(asString.contains("byte-message"));
  }

  @Test
  public void getStackTraceAsByteArray_matchesString() {
    Throwable t = new RuntimeException("consistency check");
    String asString = ThrowableUtilities.getStackTraceAsString(t);
    byte[] asBytes = ThrowableUtilities.getStackTraceAsByteArray(t);
    // Both should contain the same exception info
    assertTrue(new String(asBytes).contains("consistency check"));
    assertTrue(asString.contains("consistency check"));
  }

}
