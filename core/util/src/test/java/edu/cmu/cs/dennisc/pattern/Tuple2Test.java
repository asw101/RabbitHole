package edu.cmu.cs.dennisc.pattern;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Tuple2Test {

  // --- Factory ---

  @Test
  void createInstance_returnsNonNull() {
    Tuple2<String, Integer> t = Tuple2.createInstance("hello", 42);
    assertNotNull(t);
  }

  // --- getA / getB ---

  @Test
  void getA_returnsFirstValue() {
    Tuple2<String, Integer> t = Tuple2.createInstance("hello", 42);
    assertEquals("hello", t.getA());
  }

  @Test
  void getB_returnsSecondValue() {
    Tuple2<String, Integer> t = Tuple2.createInstance("hello", 42);
    assertEquals(42, t.getB());
  }

  @Test
  void getA_withNullValue() {
    Tuple2<String, Integer> t = Tuple2.createInstance(null, 1);
    assertNull(t.getA());
  }

  @Test
  void getB_withNullValue() {
    Tuple2<String, Integer> t = Tuple2.createInstance("x", null);
    assertNull(t.getB());
  }

  // --- setA / setB ---

  @SuppressWarnings("deprecation")
  @Test
  void setA_updatesValue() {
    Tuple2<String, Integer> t = Tuple2.createInstance("old", 1);
    t.setA("new");
    assertEquals("new", t.getA());
  }

  @SuppressWarnings("deprecation")
  @Test
  void setB_updatesValue() {
    Tuple2<String, Integer> t = Tuple2.createInstance("x", 1);
    t.setB(99);
    assertEquals(99, t.getB());
  }

  @SuppressWarnings("deprecation")
  @Test
  void setA_toNull() {
    Tuple2<String, Integer> t = Tuple2.createInstance("x", 1);
    t.setA(null);
    assertNull(t.getA());
  }

  // --- toString ---

  @Test
  void toString_containsClassName() {
    Tuple2<String, Integer> t = Tuple2.createInstance("hello", 42);
    String s = t.toString();
    assertTrue(s.contains("Tuple2"));
  }

  @Test
  void toString_containsAValue() {
    Tuple2<String, Integer> t = Tuple2.createInstance("hello", 42);
    assertTrue(t.toString().contains("hello"));
  }

  @Test
  void toString_containsBValue() {
    Tuple2<String, Integer> t = Tuple2.createInstance("hello", 42);
    assertTrue(t.toString().contains("42"));
  }

  @Test
  void toString_withNullValues() {
    Tuple2<String, String> t = Tuple2.createInstance(null, null);
    String s = t.toString();
    assertNotNull(s);
    assertTrue(s.contains("null"));
  }

  // --- equals ---

  @Test
  void equals_sameValues() {
    Tuple2<String, Integer> t1 = Tuple2.createInstance("a", 1);
    Tuple2<String, Integer> t2 = Tuple2.createInstance("a", 1);
    assertEquals(t1, t2);
  }

  @Test
  void equals_differentA() {
    Tuple2<String, Integer> t1 = Tuple2.createInstance("a", 1);
    Tuple2<String, Integer> t2 = Tuple2.createInstance("b", 1);
    assertNotEquals(t1, t2);
  }

  @Test
  void equals_differentB() {
    Tuple2<String, Integer> t1 = Tuple2.createInstance("a", 1);
    Tuple2<String, Integer> t2 = Tuple2.createInstance("a", 2);
    assertNotEquals(t1, t2);
  }

  @Test
  void equals_sameTupleIsEqual() {
    Tuple2<String, Integer> t = Tuple2.createInstance("a", 1);
    assertEquals(t, t);
  }

  @Test
  void equals_notEqualToNull() {
    Tuple2<String, Integer> t = Tuple2.createInstance("a", 1);
    assertNotEquals(null, t);
  }

  @Test
  void equals_notEqualToOtherType() {
    Tuple2<String, Integer> t = Tuple2.createInstance("a", 1);
    assertNotEquals("not a tuple", t);
  }

  @Test
  void equals_bothNullValues() {
    Tuple2<String, String> t1 = Tuple2.createInstance(null, null);
    Tuple2<String, String> t2 = Tuple2.createInstance(null, null);
    assertEquals(t1, t2);
  }

  // --- Different types ---

  @Test
  void createInstance_withDifferentTypes() {
    Tuple2<Double, Boolean> t = Tuple2.createInstance(3.14, true);
    assertEquals(3.14, t.getA());
    assertTrue(t.getB());
  }
}
