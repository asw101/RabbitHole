package edu.cmu.cs.dennisc.map;

import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class MapToMapToMapDeepTest2 {

  @Test
  public void mapToMap_put_get_roundtrip() {
    MapToMap<String, Integer, Double> mm = MapToMap.newInstance();
    mm.put("pi", 1, 3.14159);
    mm.put("e", 1, 2.71828);
    assertEquals(3.14159, mm.get("pi", 1), 1e-5);
    assertEquals(2.71828, mm.get("e", 1), 1e-5);
  }

  @Test
  public void mapToMap_getInitializingIfAbsent_lazyInit() {
    MapToMap<String, String, Integer> mm = MapToMap.newInstance();
    Integer result = mm.getInitializingIfAbsent("key1", "key2", (a, b) -> 42);
    assertEquals(Integer.valueOf(42), result);
    assertEquals(Integer.valueOf(42), mm.get("key1", "key2"));
  }

  @Test
  public void mapToMap_values_mixedKeys() {
    MapToMap<Integer, Integer, String> mm = MapToMap.newInstance();
    mm.put(1, 1, "a");
    mm.put(1, 2, "b");
    mm.put(2, 1, "c");
    mm.put(2, 2, "d");
    Collection<String> vals = mm.values();
    assertEquals(4, vals.size());
    assertTrue(vals.contains("a"));
    assertTrue(vals.contains("d"));
  }

  @Test
  public void mapToMap_overwrite_preservesOtherKeys() {
    MapToMap<String, String, String> mm = MapToMap.newInstance();
    mm.put("a", "x", "1");
    mm.put("a", "y", "2");
    mm.put("a", "x", "3");
    assertEquals("3", mm.get("a", "x"));
    assertEquals("2", mm.get("a", "y"));
  }

  @Test
  public void mapToMap_nullOuter_returnsNull() {
    MapToMap<String, String, String> mm = MapToMap.newInstance();
    assertNull(mm.get("nonexistent", "key"));
  }
}
