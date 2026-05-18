package edu.cmu.cs.dennisc.java.lang;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class SystemPropertyTest {

  // --- Basic getters ---

  @Test
  public void getKey_returnsKey() {
    SystemProperty prop = new SystemProperty("os.name", "Linux");
    assertEquals("os.name", prop.getKey());
  }

  @Test
  public void getValue_returnsValue() {
    SystemProperty prop = new SystemProperty("os.name", "Linux");
    assertEquals("Linux", prop.getValue());
  }

  @Test
  public void getKey_emptyKey() {
    SystemProperty prop = new SystemProperty("", "val");
    assertEquals("", prop.getKey());
  }

  @Test
  public void getValue_emptyValue() {
    SystemProperty prop = new SystemProperty("key", "");
    assertEquals("", prop.getValue());
  }

  @Test
  public void getKey_nullValue_keyStillWorks() {
    SystemProperty prop = new SystemProperty("key", null);
    assertEquals("key", prop.getKey());
  }

  @Test
  public void getValue_nullValue_returnsNull() {
    SystemProperty prop = new SystemProperty("key", null);
    assertNull(prop.getValue());
  }

  @Test
  public void getKey_withDots() {
    SystemProperty prop = new SystemProperty("java.home.dir", "/usr/lib");
    assertEquals("java.home.dir", prop.getKey());
  }

  @Test
  public void getValue_withSpaces() {
    SystemProperty prop = new SystemProperty("key", "value with spaces");
    assertEquals("value with spaces", prop.getValue());
  }

  @Test
  public void getValue_withSpecialChars() {
    SystemProperty prop = new SystemProperty("key", "path=/usr/bin:$HOME");
    assertEquals("path=/usr/bin:$HOME", prop.getValue());
  }

  // --- compareTo ---

  @Test
  public void compareTo_alphabeticalByKey() {
    SystemProperty a = new SystemProperty("alpha", "1");
    SystemProperty b = new SystemProperty("beta", "2");
    assertTrue(a.compareTo(b) < 0);
    assertTrue(b.compareTo(a) > 0);
  }

  @Test
  public void compareTo_sameKey() {
    SystemProperty a = new SystemProperty("key", "val1");
    SystemProperty b = new SystemProperty("key", "val2");
    assertEquals(0, a.compareTo(b));
  }

  @Test
  public void compareTo_reflexive() {
    SystemProperty prop = new SystemProperty("test", "value");
    assertEquals(0, prop.compareTo(prop));
  }

  @Test
  public void compareTo_symmetric() {
    SystemProperty a = new SystemProperty("alpha", "1");
    SystemProperty b = new SystemProperty("beta", "2");
    assertTrue("a < b", a.compareTo(b) < 0);
    assertTrue("b > a", b.compareTo(a) > 0);
  }

  @Test
  public void compareTo_transitive() {
    SystemProperty a = new SystemProperty("alpha", "1");
    SystemProperty b = new SystemProperty("beta", "2");
    SystemProperty c = new SystemProperty("gamma", "3");
    assertTrue(a.compareTo(b) < 0);
    assertTrue(b.compareTo(c) < 0);
    assertTrue("Transitivity: a < c", a.compareTo(c) < 0);
  }

  @Test
  public void compareTo_sameKey_differentValues_returnsZero() {
    SystemProperty a = new SystemProperty("same", "different1");
    SystemProperty b = new SystemProperty("same", "different2");
    assertEquals(0, a.compareTo(b));
  }

  @Test
  public void compareTo_emptyKeys_areEqual() {
    SystemProperty a = new SystemProperty("", "v1");
    SystemProperty b = new SystemProperty("", "v2");
    assertEquals(0, a.compareTo(b));
  }

  @Test
  public void compareTo_emptyKey_lessThanNonEmpty() {
    SystemProperty empty = new SystemProperty("", "v");
    SystemProperty nonEmpty = new SystemProperty("a", "v");
    assertTrue(empty.compareTo(nonEmpty) < 0);
  }

  @Test
  public void compareTo_caseMatters() {
    SystemProperty upper = new SystemProperty("Alpha", "1");
    SystemProperty lower = new SystemProperty("alpha", "2");
    // 'A' < 'a' in Java String comparison
    assertTrue(upper.compareTo(lower) < 0);
  }

  @Test
  public void compareTo_numericKeys() {
    SystemProperty a = new SystemProperty("1", "v");
    SystemProperty b = new SystemProperty("2", "v");
    SystemProperty c = new SystemProperty("10", "v");
    // String comparison: "1" < "10" < "2"
    assertTrue(a.compareTo(c) < 0);
    assertTrue(c.compareTo(b) < 0);
  }

  @Test
  public void compareTo_specialCharKeys() {
    SystemProperty dot = new SystemProperty(".", "v");
    SystemProperty slash = new SystemProperty("/", "v");
    int result = dot.compareTo(slash);
    // '.' (46) < '/' (47)
    assertTrue(result < 0);
  }

  // --- toString ---

  @Test
  public void toString_containsKeyAndValue() {
    SystemProperty prop = new SystemProperty("java.version", "17");
    String str = prop.toString();
    assertTrue(str.contains("java.version"));
    assertTrue(str.contains("17"));
    assertTrue(str.contains("SystemProperty"));
  }

  @Test
  public void toString_exactFormat() {
    SystemProperty prop = new SystemProperty("mykey", "myval");
    assertEquals("SystemProperty[mykey:myval]", prop.toString());
  }

  @Test
  public void toString_emptyKeyAndValue() {
    SystemProperty prop = new SystemProperty("", "");
    assertEquals("SystemProperty[:]", prop.toString());
  }

  @Test
  public void toString_nullValue_showsNull() {
    SystemProperty prop = new SystemProperty("key", null);
    String str = prop.toString();
    assertEquals("SystemProperty[key:null]", str);
  }

  @Test
  public void toString_startsWithClassName() {
    SystemProperty prop = new SystemProperty("k", "v");
    assertTrue(prop.toString().startsWith("SystemProperty["));
  }

  @Test
  public void toString_endsWithBracket() {
    SystemProperty prop = new SystemProperty("k", "v");
    assertTrue(prop.toString().endsWith("]"));
  }

  @Test
  public void toString_containsColon() {
    SystemProperty prop = new SystemProperty("k", "v");
    assertTrue(prop.toString().contains(":"));
  }

  @Test
  public void toString_longKeyAndValue() {
    String longKey = "a.very.long.key.name.with.many.segments";
    String longValue = "a very long value that contains lots of text";
    SystemProperty prop = new SystemProperty(longKey, longValue);
    String str = prop.toString();
    assertTrue(str.contains(longKey));
    assertTrue(str.contains(longValue));
  }

  @Test
  public void toString_specialCharsInValue() {
    SystemProperty prop = new SystemProperty("path", "/usr/local/bin:/opt/bin");
    String str = prop.toString();
    assertTrue(str.contains("/usr/local/bin:/opt/bin"));
  }

  // --- Sorting ---

  @Test
  public void sorting_listOfProperties() {
    List<SystemProperty> list = new ArrayList<>();
    list.add(new SystemProperty("zoo", "z"));
    list.add(new SystemProperty("alpha", "a"));
    list.add(new SystemProperty("mid", "m"));
    Collections.sort(list);
    assertEquals("alpha", list.get(0).getKey());
    assertEquals("mid", list.get(1).getKey());
    assertEquals("zoo", list.get(2).getKey());
  }

  @Test
  public void sorting_withDuplicateKeys() {
    List<SystemProperty> list = new ArrayList<>();
    list.add(new SystemProperty("b", "2"));
    list.add(new SystemProperty("a", "1"));
    list.add(new SystemProperty("a", "3"));
    list.add(new SystemProperty("c", "4"));
    Collections.sort(list);
    assertEquals("a", list.get(0).getKey());
    assertEquals("a", list.get(1).getKey());
    assertEquals("b", list.get(2).getKey());
    assertEquals("c", list.get(3).getKey());
  }

  @Test
  public void sorting_singleElement() {
    List<SystemProperty> list = new ArrayList<>();
    list.add(new SystemProperty("only", "v"));
    Collections.sort(list);
    assertEquals("only", list.get(0).getKey());
  }

  @Test
  public void sorting_emptyList() {
    List<SystemProperty> list = new ArrayList<>();
    Collections.sort(list);
    assertTrue(list.isEmpty());
  }

  // --- Constructor edge cases ---

  @Test
  public void constructor_preservesExactKey() {
    SystemProperty prop = new SystemProperty("  spaces  ", "v");
    assertEquals("  spaces  ", prop.getKey());
  }

  @Test
  public void constructor_preservesExactValue() {
    SystemProperty prop = new SystemProperty("k", "\ttab\nnewline");
    assertEquals("\ttab\nnewline", prop.getValue());
  }

  @Test
  public void constructor_unicodeKey() {
    SystemProperty prop = new SystemProperty("caf\u00e9", "latte");
    assertEquals("caf\u00e9", prop.getKey());
  }

  @Test
  public void constructor_unicodeValue() {
    SystemProperty prop = new SystemProperty("symbol", "\u2603");
    assertEquals("\u2603", prop.getValue());
  }

  // --- Multiple instances ---

  @Test
  public void multipleInstances_independent() {
    SystemProperty a = new SystemProperty("k1", "v1");
    SystemProperty b = new SystemProperty("k2", "v2");
    assertEquals("k1", a.getKey());
    assertEquals("v1", a.getValue());
    assertEquals("k2", b.getKey());
    assertEquals("v2", b.getValue());
  }

  @Test
  public void sameKeyDifferentValue_compareToZero() {
    SystemProperty a = new SystemProperty("key", "alpha");
    SystemProperty b = new SystemProperty("key", "beta");
    assertEquals(0, a.compareTo(b));
    assertEquals(0, b.compareTo(a));
  }

  @Test
  public void compareTo_longKeys() {
    SystemProperty a = new SystemProperty("aaaa.bbbb.cccc.dddd", "v");
    SystemProperty b = new SystemProperty("aaaa.bbbb.cccc.eeee", "v");
    assertTrue(a.compareTo(b) < 0);
  }
}
