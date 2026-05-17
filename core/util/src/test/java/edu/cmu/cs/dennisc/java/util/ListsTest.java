package edu.cmu.cs.dennisc.java.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.Assert.*;

/**
 * Tests for Lists — factory methods for LinkedList, ArrayList,
 * CopyOnWriteArrayList, and primitive array→list conversions.
 */
public class ListsTest {

  // --- newLinkedList ---

  @Test
  public void newLinkedList_empty() {
    LinkedList<String> list = Lists.newLinkedList();
    assertNotNull(list);
    assertTrue(list.isEmpty());
  }

  @Test
  public void newLinkedList_fromCollection() {
    Collection<String> src = Arrays.asList("a", "b", "c");
    LinkedList<String> list = Lists.newLinkedList(src);
    assertEquals(3, list.size());
    assertEquals("a", list.getFirst());
  }

  @Test
  public void newLinkedList_fromVarargs() {
    LinkedList<String> list = Lists.newLinkedList("x", "y", "z");
    assertEquals(3, list.size());
    assertEquals("z", list.getLast());
  }

  // --- newArrayList ---

  @Test
  public void newArrayList_empty() {
    ArrayList<String> list = Lists.newArrayList();
    assertNotNull(list);
    assertTrue(list.isEmpty());
  }

  @Test
  public void newArrayList_fromVarargs() {
    ArrayList<Integer> list = Lists.newArrayList(1, 2, 3);
    assertEquals(3, list.size());
    assertEquals(Integer.valueOf(1), list.get(0));
  }

  @Test
  public void newArrayList_fromCollection() {
    Collection<String> src = Arrays.asList("hello", "world");
    ArrayList<String> list = Lists.newArrayList(src);
    assertEquals(2, list.size());
  }

  @Test
  public void newArrayListWithInitialCapacity() {
    ArrayList<String> list = Lists.newArrayListWithInitialCapacity(100);
    assertNotNull(list);
    assertTrue(list.isEmpty());
  }

  // --- newCopyOnWriteArrayList ---

  @Test
  public void newCopyOnWriteArrayList_empty() {
    CopyOnWriteArrayList<String> list = Lists.newCopyOnWriteArrayList();
    assertNotNull(list);
    assertTrue(list.isEmpty());
  }

  @Test
  public void newCopyOnWriteArrayList_fromVarargs() {
    CopyOnWriteArrayList<String> list = Lists.newCopyOnWriteArrayList("a", "b");
    assertEquals(2, list.size());
  }

  @Test
  public void newCopyOnWriteArrayList_fromCollection() {
    Collection<String> src = Arrays.asList("x", "y");
    CopyOnWriteArrayList<String> list = Lists.newCopyOnWriteArrayList(src);
    assertEquals(2, list.size());
  }

  // --- Primitive array → list ---

  @Test
  public void newList_byteArray() {
    List<Byte> list = Lists.newList(new byte[]{1, 2, 3});
    assertEquals(3, list.size());
    assertEquals(Byte.valueOf((byte) 1), list.get(0));
  }

  @Test
  public void newList_shortArray() {
    List<Short> list = Lists.newList(new short[]{10, 20});
    assertEquals(2, list.size());
    assertEquals(Short.valueOf((short) 10), list.get(0));
  }

  @Test
  public void newList_intArray() {
    List<Integer> list = Lists.newList(new int[]{100, 200, 300});
    assertEquals(3, list.size());
    assertEquals(Integer.valueOf(300), list.get(2));
  }

  @Test
  public void newList_longArray() {
    List<Long> list = Lists.newList(new long[]{1000L, 2000L});
    assertEquals(2, list.size());
    assertEquals(Long.valueOf(1000L), list.get(0));
  }

  @Test
  public void newList_floatArray() {
    List<Float> list = Lists.newList(new float[]{1.5f, 2.5f});
    assertEquals(2, list.size());
    assertEquals(Float.valueOf(1.5f), list.get(0));
  }

  @Test
  public void newList_charArray() {
    List<Character> list = Lists.newList(new char[]{'a', 'b', 'c'});
    assertEquals(3, list.size());
    assertEquals(Character.valueOf('a'), list.get(0));
  }

  @Test
  public void newList_doubleArray() {
    List<Double> list = Lists.newList(new double[]{3.14, 2.71});
    assertEquals(2, list.size());
    assertEquals(Double.valueOf(3.14), list.get(0));
  }

  // --- newArrayListOfSingleArrayList ---

  @Test
  public void newArrayListOfSingleArrayList() {
    List<List<String>> result = Lists.newArrayListOfSingleArrayList("a", "b");
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(2, result.get(0).size());
    assertEquals("a", result.get(0).get(0));
  }

  // --- Empty primitive arrays ---

  @Test
  public void newList_emptyIntArray() {
    List<Integer> list = Lists.newList(new int[0]);
    assertNotNull(list);
    assertTrue(list.isEmpty());
  }

  @Test
  public void newList_emptyByteArray() {
    List<Byte> list = Lists.newList(new byte[0]);
    assertNotNull(list);
    assertTrue(list.isEmpty());
  }
}
