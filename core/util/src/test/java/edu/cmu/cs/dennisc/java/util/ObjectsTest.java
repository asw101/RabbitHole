package edu.cmu.cs.dennisc.java.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class ObjectsTest {

  @Test
  public void equals_sameValue() {
    assertTrue(Objects.equals("hello", "hello"));
  }

  @Test
  public void equals_differentValue() {
    assertFalse(Objects.equals("hello", "world"));
  }

  @Test
  public void equals_bothNull() {
    assertTrue(Objects.equals(null, null));
  }

  @Test
  public void equals_firstNull() {
    assertFalse(Objects.equals(null, "hello"));
  }

  @Test
  public void equals_secondNull() {
    assertFalse(Objects.equals("hello", null));
  }

  @Test
  public void notEquals_sameValue() {
    assertFalse(Objects.notEquals("hello", "hello"));
  }

  @Test
  public void notEquals_differentValue() {
    assertTrue(Objects.notEquals("hello", "world"));
  }

  @Test
  public void notEquals_bothNull() {
    assertFalse(Objects.notEquals(null, null));
  }
}
