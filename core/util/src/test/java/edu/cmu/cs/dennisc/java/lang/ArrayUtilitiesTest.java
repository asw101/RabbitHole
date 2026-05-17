package edu.cmu.cs.dennisc.java.lang;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for ArrayUtilities — reverse, concat, createArray from collections,
 * set, typed array creation, and toString variants.
 */
public class ArrayUtilitiesTest {

  // --- reverseInPlace ---

  @Test
  public void reverseInPlace_evenLength() {
    String[] arr = {"a", "b", "c", "d"};
    ArrayUtilities.reverseInPlace(arr);
    assertArrayEquals(new String[]{"d", "c", "b", "a"}, arr);
  }

  @Test
  public void reverseInPlace_oddLength() {
    String[] arr = {"x", "y", "z"};
    ArrayUtilities.reverseInPlace(arr);
    assertArrayEquals(new String[]{"z", "y", "x"}, arr);
  }

  @Test
  public void reverseInPlace_singleElement() {
    String[] arr = {"only"};
    ArrayUtilities.reverseInPlace(arr);
    assertArrayEquals(new String[]{"only"}, arr);
  }

  @Test
  public void reverseInPlace_empty() {
    String[] arr = {};
    ArrayUtilities.reverseInPlace(arr);
    assertEquals(0, arr.length);
  }

  @Test
  public void reverseInPlace_twoElements() {
    Integer[] arr = {1, 2};
    ArrayUtilities.reverseInPlace(arr);
    assertArrayEquals(new Integer[]{2, 1}, arr);
  }

  // --- concat ---

  @Test
  public void concat_singleElementWithArray() {
    String[] result = ArrayUtilities.concat(String.class, "first", "second", "third");
    assertEquals(3, result.length);
    assertEquals("first", result[0]);
    assertEquals("second", result[1]);
    assertEquals("third", result[2]);
  }

  @Test
  public void concat_singleElementOnly() {
    String[] result = ArrayUtilities.concat(String.class, "alone");
    assertEquals(1, result.length);
    assertEquals("alone", result[0]);
  }

  // --- concatArrays ---

  @Test
  public void concatArrays_twoArrays() {
    String[] a = {"a", "b"};
    String[] b = {"c", "d"};
    @SuppressWarnings("unchecked")
    String[] result = ArrayUtilities.concatArrays(String.class, a, b);
    assertEquals(4, result.length);
    assertArrayEquals(new String[]{"a", "b", "c", "d"}, result);
  }

  @Test
  public void concatArrays_emptyArrays() {
    String[] a = {};
    String[] b = {};
    @SuppressWarnings("unchecked")
    String[] result = ArrayUtilities.concatArrays(String.class, a, b);
    // concatArrays returns null when total length is zero
    assertNull(result);
  }

  @Test
  public void concatArrays_oneEmpty() {
    String[] a = {"x"};
    String[] b = {};
    @SuppressWarnings("unchecked")
    String[] result = ArrayUtilities.concatArrays(String.class, a, b);
    assertEquals(1, result.length);
    assertEquals("x", result[0]);
  }

  // --- createArray from Collection ---

  @Test
  public void createArray_fromCollection() {
    List<String> list = Arrays.asList("one", "two", "three");
    String[] result = ArrayUtilities.createArray(list, String.class);
    assertArrayEquals(new String[]{"one", "two", "three"}, result);
  }

  @Test
  public void createArray_fromEmptyCollection() {
    List<String> list = Collections.emptyList();
    String[] result = ArrayUtilities.createArray(list, String.class);
    assertNotNull(result);
    assertEquals(0, result.length);
  }

  @Test
  public void createArray_nullCollection_withFlag() {
    String[] result = ArrayUtilities.createArray(null, String.class, true);
    assertNotNull(result);
    assertEquals(0, result.length);
  }

  @Test
  public void createArray_nullCollection_withoutFlag() {
    String[] result = ArrayUtilities.createArray(null, String.class, false);
    assertNull(result);
  }

  // --- set (collection from array) ---

  @Test
  public void set_populatesCollection() {
    List<String> list = new ArrayList<>();
    list.add("old");
    ArrayUtilities.set(list, "new1", "new2");
    assertEquals(2, list.size());
    assertTrue(list.contains("new1"));
    assertTrue(list.contains("new2"));
  }

  // --- Primitive array creation ---

  @Test
  public void createShortArray() {
    List<Short> list = Arrays.asList((short) 1, (short) 2, (short) 3);
    short[] result = ArrayUtilities.createShortArray(list);
    assertArrayEquals(new short[]{1, 2, 3}, result);
  }

  @Test
  public void createIntArray() {
    List<Integer> list = Arrays.asList(10, 20, 30);
    int[] result = ArrayUtilities.createIntArray(list);
    assertArrayEquals(new int[]{10, 20, 30}, result);
  }

  @Test
  public void createFloatArray() {
    List<Float> list = Arrays.asList(1.5f, 2.5f);
    float[] result = ArrayUtilities.createFloatArray(list);
    assertArrayEquals(new float[]{1.5f, 2.5f}, result, 0.0f);
  }

  @Test
  public void createDoubleArray() {
    List<Double> list = Arrays.asList(1.1, 2.2, 3.3);
    double[] result = ArrayUtilities.createDoubleArray(list);
    assertArrayEquals(new double[]{1.1, 2.2, 3.3}, result, 0.0);
  }

  // --- toString ---

  @Test
  public void toString_array() {
    Object arr = new String[]{"a", "b"};
    String result = ArrayUtilities.toString(arr);
    assertEquals("[a, b]", result);
  }

  @Test
  public void toString_nonArray() {
    String result = ArrayUtilities.toString("simple");
    assertEquals("simple", result);
  }

  @Test
  public void toString_null() {
    String result = ArrayUtilities.toString(null);
    assertNull(result);
  }

  @Test
  public void toString_primitiveArray() {
    Object arr = new int[]{1, 2, 3};
    String result = ArrayUtilities.toString(arr);
    assertEquals("[1, 2, 3]", result);
  }
}
