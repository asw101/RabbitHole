package edu.cmu.cs.dennisc.java.util;

import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class InitializingIfAbsentHashMapDeepTest {

  @Test
  public void get_initializesOnFirstAccess() {
    InitializingIfAbsentHashMap<String, Integer> map = new InitializingIfAbsentHashMap<>();
    Integer result = map.get("key", k -> 42);
    assertEquals(Integer.valueOf(42), result);
  }

  @Test
  public void get_returnsExistingOnSecondAccess() {
    InitializingIfAbsentHashMap<String, String> map = new InitializingIfAbsentHashMap<>();
    map.get("key", k -> "first");
    String result = map.get("key", k -> "second");
    assertEquals("first", result);
  }

  @Test
  public void get_initializerCalledOnlyOnce() {
    InitializingIfAbsentHashMap<String, Integer> map = new InitializingIfAbsentHashMap<>();
    AtomicInteger counter = new AtomicInteger(0);
    map.get("k", k -> counter.incrementAndGet());
    map.get("k", k -> counter.incrementAndGet());
    map.get("k", k -> counter.incrementAndGet());
    assertEquals(1, counter.get());
    assertEquals(Integer.valueOf(1), map.get("k"));
  }

  @Test
  public void get_multipleKeys() {
    InitializingIfAbsentHashMap<String, String> map = new InitializingIfAbsentHashMap<>();
    map.get("a", k -> "alpha");
    map.get("b", k -> "beta");
    assertEquals("alpha", map.get("a"));
    assertEquals("beta", map.get("b"));
    assertEquals(2, map.size());
  }

  @Test
  public void get_nullValue() {
    InitializingIfAbsentHashMap<String, String> map = new InitializingIfAbsentHashMap<>();
    String result = map.get("key", k -> null);
    assertNull(result);
    assertTrue(map.containsKey("key"));
  }

  @Test
  public void get_initializerReceivesKey() {
    InitializingIfAbsentHashMap<String, String> map = new InitializingIfAbsentHashMap<>();
    String result = map.get("hello", k -> k.toUpperCase());
    assertEquals("HELLO", result);
  }

  @Test
  public void listHashMap_getInitializingIfAbsentToLinkedList() {
    InitializingIfAbsentListHashMap<String, Integer> map = new InitializingIfAbsentListHashMap<>();
    List<Integer> list = map.getInitializingIfAbsentToLinkedList("nums");
    assertNotNull(list);
    assertTrue(list.isEmpty());
    list.add(1);
    list.add(2);
    List<Integer> same = map.getInitializingIfAbsentToLinkedList("nums");
    assertSame(list, same);
    assertEquals(2, same.size());
  }

  @Test
  public void listHashMap_separateKeys() {
    InitializingIfAbsentListHashMap<String, String> map = new InitializingIfAbsentListHashMap<>();
    List<String> a = map.getInitializingIfAbsentToLinkedList("a");
    List<String> b = map.getInitializingIfAbsentToLinkedList("b");
    a.add("x");
    b.add("y");
    b.add("z");
    assertEquals(1, a.size());
    assertEquals(2, b.size());
  }
}
