package edu.cmu.cs.dennisc.map;

import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class MapToMapTest {

  @Test
  public void put_and_get() {
    MapToMap<String, String, Integer> map = MapToMap.newInstance();
    map.put("row1", "col1", 42);
    assertEquals(Integer.valueOf(42), map.get("row1", "col1"));
  }

  @Test
  public void get_emptyMap_returnsNull() {
    MapToMap<String, String, String> map = MapToMap.newInstance();
    assertNull(map.get("missing", "key"));
  }

  @Test
  public void get_missingInnerKey_returnsNull() {
    MapToMap<String, String, Integer> map = MapToMap.newInstance();
    map.put("row", "col1", 1);
    assertNull(map.get("row", "col2"));
  }

  @Test
  public void values_returnsAllValues() {
    MapToMap<String, String, Integer> map = MapToMap.newInstance();
    map.put("r1", "c1", 10);
    map.put("r1", "c2", 20);
    map.put("r2", "c1", 30);
    Collection<Integer> values = map.values();
    assertEquals(3, values.size());
    assertTrue(values.contains(10));
    assertTrue(values.contains(20));
    assertTrue(values.contains(30));
  }

  @Test
  public void getInitializingIfAbsent_createsOnFirst() {
    MapToMap<String, String, String> map = MapToMap.newInstance();
    String result = map.getInitializingIfAbsent("a", "b",
        (a, b) -> a + "-" + b);
    assertEquals("a-b", result);
  }

  @Test
  public void getInitializingIfAbsent_returnsCachedOnSecond() {
    MapToMap<String, String, Integer> map = MapToMap.newInstance();
    final int[] callCount = {0};
    AbstractMapToMap.Initializer<String, String, Integer> init = (a, b) -> {
      callCount[0]++;
      return 99;
    };
    map.getInitializingIfAbsent("x", "y", init);
    map.getInitializingIfAbsent("x", "y", init);
    assertEquals(1, callCount[0]);
  }
}
