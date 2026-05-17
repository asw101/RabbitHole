package edu.cmu.cs.dennisc.java.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class StringsTest {

  @Test
  public void equalsIgnoreCase_sameCase() {
    assertTrue(Strings.equalsIgnoreCase("hello", "hello"));
  }

  @Test
  public void equalsIgnoreCase_differentCase() {
    assertTrue(Strings.equalsIgnoreCase("Hello", "hELLO"));
  }

  @Test
  public void equalsIgnoreCase_notEqual() {
    assertFalse(Strings.equalsIgnoreCase("abc", "xyz"));
  }

  @Test
  public void equalsIgnoreCase_bothNull() {
    assertTrue(Strings.equalsIgnoreCase(null, null));
  }

  @Test
  public void equalsIgnoreCase_firstNull() {
    assertFalse(Strings.equalsIgnoreCase(null, "hello"));
  }

  @Test
  public void equalsIgnoreCase_secondNull() {
    assertFalse(Strings.equalsIgnoreCase("hello", null));
  }
}
