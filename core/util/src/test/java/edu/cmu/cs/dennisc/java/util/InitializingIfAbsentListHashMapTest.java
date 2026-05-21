package edu.cmu.cs.dennisc.java.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class InitializingIfAbsentListHashMapTest {
  @Test
  public void getInitializingIfAbsentToLinkedListCreatesAndCachesList() {
    InitializingIfAbsentListHashMap<String, Integer> map = new InitializingIfAbsentListHashMap<>();

    List<Integer> first = map.getInitializingIfAbsentToLinkedList("numbers");
    first.add(7);
    List<Integer> second = map.getInitializingIfAbsentToLinkedList("numbers");

    Assert.assertSame(first, second);
    Assert.assertEquals(java.util.Collections.singletonList(7), second);
  }
}
