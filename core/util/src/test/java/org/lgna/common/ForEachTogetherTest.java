package org.lgna.common;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

@SuppressWarnings("deprecation")
public class ForEachTogetherTest {

  @Test
  public void invokeAndWait_emptyArray() {
    AtomicInteger count = new AtomicInteger(0);
    ForEachTogether.invokeAndWait(new String[]{}, value -> count.incrementAndGet());
    assertEquals(0, count.get());
  }

  @Test
  public void invokeAndWait_singleElementArray() {
    List<String> results = new CopyOnWriteArrayList<>();
    ForEachTogether.invokeAndWait(new String[]{"only"}, results::add);
    assertEquals(1, results.size());
    assertEquals("only", results.get(0));
  }

  @Test
  public void invokeAndWait_multipleElementArray() throws InterruptedException {
    List<String> results = new CopyOnWriteArrayList<>();
    ForEachTogether.invokeAndWait(new String[]{"a", "b", "c"}, results::add);
    Thread.sleep(100);
    assertEquals(3, results.size());
    assertTrue(results.contains("a"));
    assertTrue(results.contains("b"));
    assertTrue(results.contains("c"));
  }

  @Test
  public void invokeAndWait_twoElementArray() throws InterruptedException {
    AtomicInteger sum = new AtomicInteger(0);
    ForEachTogether.invokeAndWait(new Integer[]{10, 20}, sum::addAndGet);
    Thread.sleep(100);
    assertEquals(30, sum.get());
  }

  @Test
  public void invokeAndWait_iterableCollection() throws InterruptedException {
    List<String> source = Arrays.asList("x", "y", "z");
    List<String> results = new CopyOnWriteArrayList<>();
    ForEachTogether.invokeAndWait(source, results::add);
    Thread.sleep(100);
    assertEquals(3, results.size());
    assertTrue(results.contains("x"));
    assertTrue(results.contains("y"));
    assertTrue(results.contains("z"));
  }

  @Test
  public void invokeAndWait_iterableEmptyCollection() {
    List<String> source = Collections.emptyList();
    AtomicInteger count = new AtomicInteger(0);
    ForEachTogether.invokeAndWait(source, value -> count.incrementAndGet());
    assertEquals(0, count.get());
  }

  @Test
  public void invokeAndWait_iterableSingleElement() {
    List<String> source = Collections.singletonList("solo");
    List<String> results = new CopyOnWriteArrayList<>();
    ForEachTogether.invokeAndWait(source, results::add);
    assertEquals(1, results.size());
    assertEquals("solo", results.get(0));
  }

  @Test
  public void invokeAndWait_nonCollectionIterable() throws InterruptedException {
    // Custom iterable that is NOT a Collection to test the non-Collection branch
    Iterable<Integer> iterable = () -> Arrays.asList(1, 2, 3).iterator();
    List<Integer> results = new CopyOnWriteArrayList<>();
    ForEachTogether.invokeAndWait(iterable, results::add);
    Thread.sleep(100);
    assertEquals(3, results.size());
    assertTrue(results.contains(1));
    assertTrue(results.contains(2));
    assertTrue(results.contains(3));
  }

  @Test
  public void invokeAndWait_collectionIterable() throws InterruptedException {
    // Pass an actual Collection to test the Collection branch
    ArrayList<String> collection = new ArrayList<>(Arrays.asList("alpha", "beta"));
    List<String> results = new CopyOnWriteArrayList<>();
    ForEachTogether.invokeAndWait(collection, results::add);
    Thread.sleep(100);
    assertEquals(2, results.size());
  }

  @Test
  public void invokeAndWait_largeArray() throws InterruptedException {
    Integer[] nums = new Integer[20];
    for (int i = 0; i < 20; i++) {
      nums[i] = i;
    }
    AtomicInteger sum = new AtomicInteger(0);
    ForEachTogether.invokeAndWait(nums, sum::addAndGet);
    Thread.sleep(200);
    assertEquals(190, sum.get()); // 0+1+...+19 = 190
  }

  @Test
  public void invokeAndWait_singleElementNonCollectionIterable() throws InterruptedException {
    Iterable<String> iterable = () -> Collections.singletonList("one").iterator();
    List<String> results = new CopyOnWriteArrayList<>();
    ForEachTogether.invokeAndWait(iterable, results::add);
    Thread.sleep(100);
    assertEquals(1, results.size());
  }
}
