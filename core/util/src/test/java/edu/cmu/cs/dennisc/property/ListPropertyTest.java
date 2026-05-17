package edu.cmu.cs.dennisc.property;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class ListPropertyTest {

  private StubOwner owner;
  private ListProperty<String> list;

  @Before
  public void setUp() {
    owner = new StubOwner();
    list = new ListProperty<>(owner);
  }

  @Test
  public void newList_isEmpty() {
    assertTrue(list.isEmpty());
    assertEquals(0, list.size());
  }

  @Test
  public void add_singleElement() {
    list.add("alpha");
    assertEquals(1, list.size());
    assertEquals("alpha", list.get(0));
    assertFalse(list.isEmpty());
  }

  @Test
  public void add_multipleElements() {
    list.add("alpha", "beta", "gamma");
    assertEquals(3, list.size());
    assertEquals("alpha", list.get(0));
    assertEquals("beta", list.get(1));
    assertEquals("gamma", list.get(2));
  }

  @Test
  public void add_atIndex() {
    list.add("alpha");
    list.add("gamma");
    list.add(1, "beta");
    assertEquals(3, list.size());
    assertEquals("beta", list.get(1));
  }

  @Test
  public void addAll_collection() {
    boolean changed = list.addAll(Arrays.asList("a", "b", "c"));
    assertTrue(changed);
    assertEquals(3, list.size());
  }

  @Test
  public void addAll_atIndex() {
    list.add("alpha");
    list.add("delta");
    boolean changed = list.addAll(1, Arrays.asList("beta", "gamma"));
    assertTrue(changed);
    assertEquals(4, list.size());
    assertEquals("beta", list.get(1));
    assertEquals("gamma", list.get(2));
    assertEquals("delta", list.get(3));
  }

  @Test
  public void remove_byIndex() {
    list.add("alpha", "beta", "gamma");
    String removed = list.remove(1);
    assertEquals("beta", removed);
    assertEquals(2, list.size());
  }

  @Test
  public void removeExclusive() {
    list.add("a", "b", "c", "d", "e");
    list.removeExclusive(1, 4);
    assertEquals(2, list.size());
    assertEquals("a", list.get(0));
    assertEquals("e", list.get(1));
  }

  @Test
  public void removeInclusive() {
    list.add("a", "b", "c", "d", "e");
    list.removeInclusive(1, 3);
    assertEquals(2, list.size());
    assertEquals("a", list.get(0));
    assertEquals("e", list.get(1));
  }

  @Test
  public void clear_emptiesList() {
    list.add("alpha", "beta");
    list.clear();
    assertTrue(list.isEmpty());
    assertEquals(0, list.size());
  }

  @Test
  public void contains_existingElement() {
    list.add("alpha", "beta");
    assertTrue(list.contains("alpha"));
    assertTrue(list.contains("beta"));
    assertFalse(list.contains("gamma"));
  }

  @Test
  public void containsAll() {
    list.add("alpha", "beta", "gamma");
    assertTrue(list.containsAll(Arrays.asList("alpha", "beta")));
    assertFalse(list.containsAll(Arrays.asList("alpha", "delta")));
  }

  @Test
  public void indexOf_found() {
    list.add("alpha", "beta", "gamma");
    assertEquals(1, list.indexOf("beta"));
  }

  @Test
  public void indexOf_notFound() {
    list.add("alpha");
    assertEquals(-1, list.indexOf("missing"));
  }

  @Test
  public void lastIndexOf() {
    list.add("alpha", "beta", "alpha");
    assertEquals(2, list.lastIndexOf("alpha"));
  }

  @Test
  public void set_atIndex() {
    list.add("alpha", "beta", "gamma");
    list.set(1, "BETA");
    assertEquals("BETA", list.get(1));
    assertEquals(3, list.size());
  }

  @Test
  public void set_multipleAtIndex() {
    list.add("alpha", "beta", "gamma", "delta");
    list.set(1, "B", "C");
    assertEquals("B", list.get(1));
    assertEquals("C", list.get(2));
  }

  @Test
  public void set_listAtIndex() {
    list.add("alpha", "beta", "gamma");
    list.set(1, Arrays.asList("B", "C"));
    assertEquals("B", list.get(1));
    assertEquals("C", list.get(2));
  }

  @Test
  public void swap() {
    list.add("alpha", "beta", "gamma");
    list.swap(0, 2);
    assertEquals("gamma", list.get(0));
    assertEquals("alpha", list.get(2));
    assertEquals("beta", list.get(1));
  }

  @Test
  public void slide() {
    list.add("a", "b", "c", "d");
    list.slide(0, 2);
    assertEquals("b", list.get(0));
    assertEquals("c", list.get(1));
    assertEquals("a", list.get(2));
  }

  @Test
  public void subList_returnsView() {
    list.add("a", "b", "c", "d");
    List<String> sub = list.subList(1, 3);
    assertEquals(2, sub.size());
    assertEquals("b", sub.get(0));
    assertEquals("c", sub.get(1));
  }

  @Test
  public void subListCopy_returnsCopy() {
    list.add("a", "b", "c", "d");
    List<String> copy = list.subListCopy(1, 3);
    assertEquals(2, copy.size());
    assertEquals("b", copy.get(0));
  }

  @Test
  public void toArray_object() {
    list.add("alpha", "beta");
    Object[] arr = list.toArray();
    assertEquals(2, arr.length);
    assertEquals("alpha", arr[0]);
  }

  @Test
  public void toArray_typed() {
    list.add("alpha", "beta");
    String[] arr = list.toArray(new String[0]);
    assertEquals(2, arr.length);
    assertEquals("alpha", arr[0]);
  }

  @Test
  public void toArray_classTyped() {
    list.add("alpha", "beta");
    String[] arr = list.toArray(String.class);
    assertEquals(2, arr.length);
    assertEquals("alpha", arr[0]);
  }

  @Test
  public void iterator_iteratesAll() {
    list.add("alpha", "beta", "gamma");
    Iterator<String> it = list.iterator();
    assertTrue(it.hasNext());
    assertEquals("alpha", it.next());
    assertEquals("beta", it.next());
    assertEquals("gamma", it.next());
    assertFalse(it.hasNext());
  }

  @Test
  public void setValue_replacesAll() {
    list.add("old1", "old2");
    ArrayList<String> newValue = new ArrayList<>(Arrays.asList("new1", "new2", "new3"));
    list.setValue(newValue);
    assertEquals(3, list.size());
    assertEquals("new1", list.get(0));
  }

  // Concrete subclass for testing
  static class StubOwner extends edu.cmu.cs.dennisc.pattern.AbstractInstancePropertyOwner {
    @Override
    public int hashCode() {
      return System.identityHashCode(this);
    }
  }
}
