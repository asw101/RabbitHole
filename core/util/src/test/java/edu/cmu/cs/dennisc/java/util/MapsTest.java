package edu.cmu.cs.dennisc.java.util;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;

/**
 * Tests for Maps — HashMap, WeakHashMap, ConcurrentHashMap,
 * and InitializingIfAbsent map factories.
 */
public class MapsTest {

  // --- newHashMap ---

  @Test
  public void newHashMap_returnsEmptyMap() {
    HashMap<String, Integer> map = Maps.newHashMap();
    assertNotNull(map);
    assertTrue(map.isEmpty());
  }

  @Test
  public void newHashMap_isModifiable() {
    HashMap<String, String> map = Maps.newHashMap();
    map.put("key", "value");
    assertEquals("value", map.get("key"));
  }

  // --- newWeakHashMap ---

  @Test
  public void newWeakHashMap_returnsEmptyMap() {
    WeakHashMap<String, Integer> map = Maps.newWeakHashMap();
    assertNotNull(map);
    assertTrue(map.isEmpty());
  }

  @Test
  public void newWeakHashMap_isModifiable() {
    WeakHashMap<String, String> map = Maps.newWeakHashMap();
    map.put("key", "value");
    assertEquals("value", map.get("key"));
  }

  // --- newConcurrentHashMap ---

  @Test
  public void newConcurrentHashMap_returnsEmptyMap() {
    ConcurrentHashMap<String, Integer> map = Maps.newConcurrentHashMap();
    assertNotNull(map);
    assertTrue(map.isEmpty());
  }

  @Test
  public void newConcurrentHashMap_isModifiable() {
    ConcurrentHashMap<String, String> map = Maps.newConcurrentHashMap();
    map.put("key", "value");
    assertEquals("value", map.get("key"));
  }

  // --- newInitializingIfAbsentHashMap ---

  @Test
  public void newInitializingIfAbsentHashMap_returnsEmptyMap() {
    InitializingIfAbsentMap<String, Integer> map = Maps.newInitializingIfAbsentHashMap();
    assertNotNull(map);
    assertTrue(map.isEmpty());
  }

  @Test
  public void initializingIfAbsentHashMap_getWithInitializer() {
    InitializingIfAbsentMap<String, Integer> map = Maps.newInitializingIfAbsentHashMap();
    Integer result = map.get("key", k -> k.length());
    assertEquals(Integer.valueOf(3), result);
  }

  @Test
  public void initializingIfAbsentHashMap_memoizes() {
    InitializingIfAbsentMap<String, Integer> map = Maps.newInitializingIfAbsentHashMap();
    final int[] callCount = {0};
    map.get("test", k -> {
      callCount[0]++;
      return 42;
    });
    map.get("test", k -> {
      callCount[0]++;
      return 99;
    });
    assertEquals(1, callCount[0]);
    assertEquals(Integer.valueOf(42), map.get("test", k -> 0));
  }

  @Test
  public void initializingIfAbsentHashMap_multipleKeys() {
    InitializingIfAbsentMap<String, String> map = Maps.newInitializingIfAbsentHashMap();
    map.get("a", k -> "value-a");
    map.get("b", k -> "value-b");
    assertEquals("value-a", map.get("a", k -> "ignored"));
    assertEquals("value-b", map.get("b", k -> "ignored"));
    assertEquals(2, map.size());
  }

  @Test
  public void initializingIfAbsentHashMap_standardMapOps() {
    InitializingIfAbsentMap<String, Integer> map = Maps.newInitializingIfAbsentHashMap();
    map.put("direct", 100);
    assertEquals(Integer.valueOf(100), map.get("direct"));
    assertTrue(map.containsKey("direct"));
    assertEquals(1, map.size());
  }

  // --- newInitializingIfAbsentListHashMap ---

  @Test
  public void newInitializingIfAbsentListHashMap_returnsEmptyMap() {
    InitializingIfAbsentListHashMap<String, Integer> map = Maps.newInitializingIfAbsentListHashMap();
    assertNotNull(map);
    assertTrue(map.isEmpty());
  }

  @Test
  public void initializingIfAbsentListHashMap_putAndGet() {
    InitializingIfAbsentListHashMap<String, Integer> map = Maps.newInitializingIfAbsentListHashMap();
    map.put("key", java.util.Arrays.asList(1, 2, 3));
    List<Integer> result = map.get("key");
    assertEquals(3, result.size());
  }

  // --- Type safety ---

  @Test
  public void allMaps_returnCorrectTypes() {
    assertTrue(Maps.newHashMap() instanceof HashMap);
    assertTrue(Maps.newWeakHashMap() instanceof WeakHashMap);
    assertTrue(Maps.newConcurrentHashMap() instanceof ConcurrentHashMap);
    assertTrue(Maps.newInitializingIfAbsentHashMap() instanceof Map);
  }
}
