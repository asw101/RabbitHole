package edu.cmu.cs.dennisc.java.lang;

import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class IterableUtilitiesTest {

  @Test
  public void toArray_fromList() {
    List<String> list = Arrays.asList("a", "b", "c");
    String[] result = IterableUtilities.toArray(list, String.class);
    assertArrayEquals(new String[]{"a", "b", "c"}, result);
  }

  @Test
  public void toArray_fromCustomIterable() {
    Iterable<Integer> iterable = () -> new Iterator<Integer>() {
      private int current = 1;

      @Override
      public boolean hasNext() {
        return current <= 3;
      }

      @Override
      public Integer next() {
        return current++;
      }
    };
    Integer[] result = IterableUtilities.toArray(iterable, Integer.class);
    assertArrayEquals(new Integer[]{1, 2, 3}, result);
  }

  @Test
  public void toArray_emptyIterable() {
    List<String> empty = Arrays.asList();
    String[] result = IterableUtilities.toArray(empty, String.class);
    assertNotNull(result);
    assertEquals(0, result.length);
  }

  @Test
  public void toArray_untypedFromList() {
    List<String> list = Arrays.asList("x", "y");
    Object[] result = IterableUtilities.toArray(list);
    assertEquals(2, result.length);
    assertEquals("x", result[0]);
    assertEquals("y", result[1]);
  }
}
