package edu.cmu.cs.dennisc.java.util;

import org.junit.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class IteratorsDeepTest {

  @Test
  public void emptyIterator_hasNext_returnsFalse() {
    Iterator<String> iter = Iterators.emptyIterator();
    assertFalse(iter.hasNext());
  }

  @Test(expected = NoSuchElementException.class)
  public void emptyIterator_next_throws() {
    Iterator<String> iter = Iterators.emptyIterator();
    iter.next();
  }

  @Test(expected = IllegalStateException.class)
  public void emptyIterator_remove_throws() {
    Iterator<String> iter = Iterators.emptyIterator();
    iter.remove();
  }

  @Test
  public void emptyIterator_sameInstance() {
    Iterator<String> a = Iterators.emptyIterator();
    Iterator<Integer> b = Iterators.emptyIterator();
    assertSame(a, b);
  }

  @Test
  public void emptyIterator_multipleHasNext_alwaysFalse() {
    Iterator<Object> iter = Iterators.emptyIterator();
    for (int i = 0; i < 10; i++) {
      assertFalse(iter.hasNext());
    }
  }

  @Test
  public void emptyIterator_notNull() {
    assertNotNull(Iterators.emptyIterator());
  }
}
