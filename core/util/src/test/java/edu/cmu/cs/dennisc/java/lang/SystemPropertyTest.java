package edu.cmu.cs.dennisc.java.lang;

import org.junit.Test;

import static org.junit.Assert.*;

public class SystemPropertyTest {

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
  public void toString_containsKeyAndValue() {
    SystemProperty prop = new SystemProperty("java.version", "17");
    String str = prop.toString();
    assertTrue(str.contains("java.version"));
    assertTrue(str.contains("17"));
    assertTrue(str.contains("SystemProperty"));
  }
}
