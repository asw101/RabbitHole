package edu.cmu.cs.dennisc.map;

import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class MapToMapDeepTest {

  @Test
  public void put_and_get() {
    MapToMap<String, String, Integer> mm = new MapToMap<>();
    mm.put("a", "x", 1);
    assertEquals(Integer.valueOf(1), mm.get("a", "x"));
  }

  @Test
  public void get_missing_outerKey_returnsNull() {
    MapToMap<String, String, Integer> mm = new MapToMap<>();
    assertNull(mm.get("missing", "key"));
  }

  @Test
  public void get_missing_innerKey_returnsNull() {
    MapToMap<String, String, Integer> mm = new MapToMap<>();
    mm.put("a", "x", 1);
    assertNull(mm.get("a", "missing"));
  }

  @Test
  public void put_overwrite() {
    MapToMap<String, String, String> mm = new MapToMap<>();
    mm.put("a", "b", "first");
    mm.put("a", "b", "second");
    assertEquals("second", mm.get("a", "b"));
  }

  @Test
  public void multipleOuterKeys() {
    MapToMap<String, String, Integer> mm = new MapToMap<>();
    mm.put("a", "x", 1);
    mm.put("b", "x", 2);
    assertEquals(Integer.valueOf(1), mm.get("a", "x"));
    assertEquals(Integer.valueOf(2), mm.get("b", "x"));
  }

  @Test
  public void multipleInnerKeys() {
    MapToMap<String, String, Integer> mm = new MapToMap<>();
    mm.put("a", "x", 1);
    mm.put("a", "y", 2);
    mm.put("a", "z", 3);
    assertEquals(Integer.valueOf(1), mm.get("a", "x"));
    assertEquals(Integer.valueOf(2), mm.get("a", "y"));
    assertEquals(Integer.valueOf(3), mm.get("a", "z"));
  }

  @Test
  public void values_returnsAllValues() {
    MapToMap<String, String, Integer> mm = new MapToMap<>();
    mm.put("a", "x", 1);
    mm.put("a", "y", 2);
    mm.put("b", "z", 3);
    Collection<Integer> vals = mm.values();
    assertEquals(3, vals.size());
    assertTrue(vals.contains(1));
    assertTrue(vals.contains(2));
    assertTrue(vals.contains(3));
  }

  @Test
  public void values_empty() {
    MapToMap<String, String, Integer> mm = new MapToMap<>();
    assertTrue(mm.values().isEmpty());
  }

  @Test
  public void getInitializingIfAbsent() {
    MapToMap<String, String, String> mm = new MapToMap<>();
    String val = mm.getInitializingIfAbsent("a", "b", (a, b) -> a + "-" + b);
    assertEquals("a-b", val);
    assertEquals("a-b", mm.get("a", "b"));
  }

  @Test
  public void getInitializingIfAbsent_doesNotOverwrite() {
    MapToMap<String, String, Integer> mm = new MapToMap<>();
    mm.put("a", "b", 42);
    Integer val = mm.getInitializingIfAbsent("a", "b", (a, b) -> 99);
    assertEquals(Integer.valueOf(42), val);
  }

  @Test
  public void newInstance_factory() {
    MapToMap<String, String, Integer> mm = MapToMap.newInstance();
    assertNotNull(mm);
    mm.put("x", "y", 100);
    assertEquals(Integer.valueOf(100), mm.get("x", "y"));
  }
}
