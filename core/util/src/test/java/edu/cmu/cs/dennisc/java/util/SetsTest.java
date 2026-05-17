package edu.cmu.cs.dennisc.java.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.CopyOnWriteArraySet;

import static org.junit.Assert.*;

public class SetsTest {

  @Test
  public void newHashSet_empty() {
    HashSet<String> set = Sets.newHashSet();
    assertNotNull(set);
    assertTrue(set.isEmpty());
  }

  @Test
  public void newHashSet_withValues() {
    HashSet<String> set = Sets.newHashSet("a", "b", "c");
    assertEquals(3, set.size());
    assertTrue(set.contains("a"));
    assertTrue(set.contains("b"));
    assertTrue(set.contains("c"));
  }

  @Test
  public void newHashSet_withDuplicates() {
    HashSet<String> set = Sets.newHashSet("a", "a", "b");
    assertEquals(2, set.size());
  }

  @Test
  public void newHashSet_withNullVarargs() {
    HashSet<String> set = Sets.newHashSet((String[]) null);
    assertNotNull(set);
    assertTrue(set.isEmpty());
  }

  @Test
  public void newHashSet_fromCollection() {
    Collection<Integer> source = Arrays.asList(1, 2, 3, 2);
    HashSet<Integer> set = Sets.newHashSet(source);
    assertEquals(3, set.size());
    assertTrue(set.contains(1));
    assertTrue(set.contains(2));
    assertTrue(set.contains(3));
  }

  @Test
  public void newHashSet_fromEmptyCollection() {
    HashSet<String> set = Sets.newHashSet(Arrays.asList());
    assertNotNull(set);
    assertTrue(set.isEmpty());
  }

  @Test
  public void newCopyOnWriteArraySet_empty() {
    CopyOnWriteArraySet<String> set = Sets.newCopyOnWriteArraySet();
    assertNotNull(set);
    assertTrue(set.isEmpty());
  }

  @Test
  public void newCopyOnWriteArraySet_withValues() {
    CopyOnWriteArraySet<String> set = Sets.newCopyOnWriteArraySet("x", "y", "z");
    assertEquals(3, set.size());
    assertTrue(set.contains("x"));
    assertTrue(set.contains("y"));
    assertTrue(set.contains("z"));
  }

  @Test
  public void newCopyOnWriteArraySet_withDuplicates() {
    CopyOnWriteArraySet<Integer> set = Sets.newCopyOnWriteArraySet(1, 1, 2);
    assertEquals(2, set.size());
  }

  @Test
  public void newCopyOnWriteArraySet_fromCollection() {
    Collection<String> source = Arrays.asList("alpha", "beta", "alpha");
    CopyOnWriteArraySet<String> set = Sets.newCopyOnWriteArraySet(source);
    assertEquals(2, set.size());
    assertTrue(set.contains("alpha"));
    assertTrue(set.contains("beta"));
  }

  @Test
  public void newCopyOnWriteArraySet_fromEmptyCollection() {
    CopyOnWriteArraySet<String> set = Sets.newCopyOnWriteArraySet(Arrays.asList());
    assertNotNull(set);
    assertTrue(set.isEmpty());
  }

  @Test
  public void newHashSet_subtypeValues() {
    HashSet<Number> set = Sets.<Number, Integer>newHashSet(1, 2, 3);
    assertEquals(3, set.size());
    assertTrue(set.contains(1));
  }

  @Test
  public void newCopyOnWriteArraySet_subtypeValues() {
    CopyOnWriteArraySet<Number> set = Sets.<Number, Integer>newCopyOnWriteArraySet(10, 20);
    assertEquals(2, set.size());
    assertTrue(set.contains(10));
  }
}
