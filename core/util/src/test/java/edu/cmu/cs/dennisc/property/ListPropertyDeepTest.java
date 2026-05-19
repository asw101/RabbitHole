package edu.cmu.cs.dennisc.property;

import edu.cmu.cs.dennisc.property.event.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class ListPropertyDeepTest {

  private StubPropertyOwner owner;

  @Before
  public void setUp() {
    owner = new StubPropertyOwner();
  }

  // --- Basic operations ---

  @Test
  public void newListProperty_isEmpty() {
    ListProperty<String> lp = new ListProperty<>(owner);
    assertTrue(lp.isEmpty());
    assertEquals(0, lp.size());
  }

  @Test
  public void add_singleElement() {
    ListProperty<String> lp = new ListProperty<>(owner);
    lp.add("hello");
    assertEquals(1, lp.size());
    assertEquals("hello", lp.get(0));
  }

  @Test
  public void add_multipleElements() {
    ListProperty<String> lp = new ListProperty<>(owner);
    lp.add("a", "b", "c");
    assertEquals(3, lp.size());
    assertEquals("a", lp.get(0));
    assertEquals("b", lp.get(1));
    assertEquals("c", lp.get(2));
  }

  @Test
  public void add_atIndex() {
    ListProperty<String> lp = new ListProperty<>(owner);
    lp.add("a", "c");
    lp.add(1, "b");
    assertEquals(3, lp.size());
    assertEquals("a", lp.get(0));
    assertEquals("b", lp.get(1));
    assertEquals("c", lp.get(2));
  }

  @Test
  public void addAll_collection() {
    ListProperty<String> lp = new ListProperty<>(owner);
    lp.addAll(Arrays.asList("x", "y", "z"));
    assertEquals(3, lp.size());
    assertEquals("x", lp.get(0));
  }

  @Test
  public void addAll_atIndex() {
    ListProperty<String> lp = new ListProperty<>(owner);
    lp.add("first", "last");
    lp.addAll(1, Arrays.asList("middle1", "middle2"));
    assertEquals(4, lp.size());
    assertEquals("middle1", lp.get(1));
    assertEquals("middle2", lp.get(2));
    assertEquals("last", lp.get(3));
  }

  @Test
  public void remove_byIndex() {
    ListProperty<String> lp = new ListProperty<>(owner);
    lp.add("a", "b", "c");
    String removed = (String) lp.remove(1);
    assertEquals("b", removed);
    assertEquals(2, lp.size());
    assertEquals("a", lp.get(0));
    assertEquals("c", lp.get(1));
  }

  @Test
  public void clear_emptiesList() {
    ListProperty<String> lp = new ListProperty<>(owner);
    lp.add("a", "b");
    lp.clear();
    assertTrue(lp.isEmpty());
    assertEquals(0, lp.size());
  }

  @Test
  public void set_replacesElement() {
    ListProperty<String> lp = new ListProperty<>(owner);
    lp.add("a", "b", "c");
    lp.set(1, "B");
    assertEquals("B", lp.get(1));
    assertEquals(3, lp.size());
  }

  @Test
  public void set_multipleElements() {
    ListProperty<String> lp = new ListProperty<>(owner);
    lp.add("a", "b", "c", "d");
    lp.set(1, "B", "C");
    assertEquals("a", lp.get(0));
    assertEquals("B", lp.get(1));
    assertEquals("C", lp.get(2));
    assertEquals("d", lp.get(3));
  }

  @Test
  public void set_withList() {
    ListProperty<String> lp = new ListProperty<>(owner);
    lp.add("a", "b", "c");
    lp.set(0, Arrays.asList("X", "Y", "Z"));
    assertEquals("X", lp.get(0));
    assertEquals("Y", lp.get(1));
    assertEquals("Z", lp.get(2));
  }

  @Test
  public void contains_andContainsAll() {
    ListProperty<String> lp = new ListProperty<>(owner);
    lp.add("a", "b", "c");
    assertTrue(lp.contains("b"));
    assertFalse(lp.contains("z"));
    assertTrue(lp.containsAll(Arrays.asList("a", "c")));
    assertFalse(lp.containsAll(Arrays.asList("a", "z")));
  }

  @Test
  public void indexOf_andLastIndexOf() {
    ListProperty<String> lp = new ListProperty<>(owner);
    lp.add("a", "b", "a");
    assertEquals(0, lp.indexOf("a"));
    assertEquals(2, lp.lastIndexOf("a"));
    assertEquals(-1, lp.indexOf("z"));
  }

  @Test
  public void subList_returnsView() {
    ListProperty<String> lp = new ListProperty<>(owner);
    lp.add("a", "b", "c", "d");
    List<String> sub = lp.subList(1, 3);
    assertEquals(2, sub.size());
    assertEquals("b", sub.get(0));
    assertEquals("c", sub.get(1));
  }

  @Test
  public void subListCopy_returnsCopy() {
    ListProperty<String> lp = new ListProperty<>(owner);
    lp.add("a", "b", "c", "d");
    List<String> copy = lp.subListCopy(1, 3);
    assertEquals(2, copy.size());
    assertEquals("b", copy.get(0));
    // Modifying copy doesn't affect original
    copy.set(0, "Z");
    assertEquals("b", lp.get(1));
  }

  @Test
  public void toArray_objectArray() {
    ListProperty<String> lp = new ListProperty<>(owner);
    lp.add("a", "b");
    Object[] arr = lp.toArray();
    assertEquals(2, arr.length);
    assertEquals("a", arr[0]);
  }

  @Test
  public void toArray_typedArray() {
    ListProperty<String> lp = new ListProperty<>(owner);
    lp.add("a", "b");
    String[] arr = lp.toArray(new String[0]);
    assertEquals(2, arr.length);
    assertEquals("a", arr[0]);
  }

  @Test
  public void toArray_withComponentType() {
    ListProperty<String> lp = new ListProperty<>(owner);
    lp.add("a", "b");
    String[] arr = lp.toArray(String.class);
    assertEquals(2, arr.length);
  }

  @Test
  public void iterator_traversesAll() {
    ListProperty<String> lp = new ListProperty<>(owner);
    lp.add("a", "b", "c");
    List<String> collected = new ArrayList<>();
    for (String s : lp) {
      collected.add(s);
    }
    assertEquals(Arrays.asList("a", "b", "c"), collected);
  }

  // --- Swap and Slide ---

  @Test
  public void swap_twoElements() {
    ListProperty<String> lp = new ListProperty<>(owner);
    lp.add("a", "b", "c");
    lp.swap(0, 2);
    assertEquals("c", lp.get(0));
    assertEquals("b", lp.get(1));
    assertEquals("a", lp.get(2));
  }

  @Test
  public void swap_sameIndex_noChange() {
    ListProperty<String> lp = new ListProperty<>(owner);
    lp.add("a", "b");
    lp.swap(0, 0);
    assertEquals("a", lp.get(0));
    assertEquals("b", lp.get(1));
  }

  @Test
  public void slide_forwardOnePosition() {
    ListProperty<String> lp = new ListProperty<>(owner);
    lp.add("a", "b", "c", "d");
    lp.slide(0, 2);
    assertEquals("b", lp.get(0));
    assertEquals("c", lp.get(1));
    assertEquals("a", lp.get(2));
    assertEquals("d", lp.get(3));
  }

  @Test
  public void slide_backwardOnePosition() {
    ListProperty<String> lp = new ListProperty<>(owner);
    lp.add("a", "b", "c", "d");
    lp.slide(2, 0);
    assertEquals("c", lp.get(0));
    assertEquals("a", lp.get(1));
    assertEquals("b", lp.get(2));
    assertEquals("d", lp.get(3));
  }

  @Test
  public void slide_sameIndex_noChange() {
    ListProperty<String> lp = new ListProperty<>(owner);
    lp.add("a", "b", "c");
    lp.slide(1, 1);
    assertEquals("a", lp.get(0));
    assertEquals("b", lp.get(1));
    assertEquals("c", lp.get(2));
  }

  // --- Listener notifications ---

  @Test
  public void addListener_receivesAddEvents() {
    ListProperty<String> lp = new ListProperty<>(owner);
    AtomicInteger addCount = new AtomicInteger(0);
    lp.addListPropertyListener(new NoOpListPropertyListener<String>() {
      @Override
      public void added(AddListPropertyEvent<String> e) {
        addCount.incrementAndGet();
      }
    });
    lp.add("a");
    lp.add("b");
    assertEquals(2, addCount.get());
  }

  @Test
  public void clearListener_receivesClearEvent() {
    ListProperty<String> lp = new ListProperty<>(owner);
    lp.add("a", "b");
    AtomicBoolean cleared = new AtomicBoolean(false);
    lp.addListPropertyListener(new NoOpListPropertyListener<String>() {
      @Override
      public void cleared(ClearListPropertyEvent<String> e) {
        cleared.set(true);
      }
    });
    lp.clear();
    assertTrue(cleared.get());
  }

  @Test
  public void removeListener_receivesRemoveEvent() {
    ListProperty<String> lp = new ListProperty<>(owner);
    lp.add("a", "b", "c");
    AtomicBoolean removed = new AtomicBoolean(false);
    lp.addListPropertyListener(new NoOpListPropertyListener<String>() {
      @Override
      public void removed(RemoveListPropertyEvent<String> e) {
        removed.set(true);
      }
    });
    lp.remove(1);
    assertTrue(removed.get());
  }

  @Test
  public void setListener_receivesSetEvent() {
    ListProperty<String> lp = new ListProperty<>(owner);
    lp.add("a", "b");
    AtomicBoolean wasSet = new AtomicBoolean(false);
    lp.addListPropertyListener(new NoOpListPropertyListener<String>() {
      @Override
      public void set(SetListPropertyEvent<String> e) {
        wasSet.set(true);
      }
    });
    lp.set(0, "A");
    assertTrue(wasSet.get());
  }

  @Test
  public void removeListPropertyListener_stopsNotifications() {
    ListProperty<String> lp = new ListProperty<>(owner);
    AtomicInteger count = new AtomicInteger(0);
    ListPropertyListener<String> listener = new NoOpListPropertyListener<String>() {
      @Override
      public void added(AddListPropertyEvent<String> e) {
        count.incrementAndGet();
      }
    };
    lp.addListPropertyListener(listener);
    lp.add("a");
    assertEquals(1, count.get());
    lp.removeListPropertyListener(listener);
    lp.add("b");
    assertEquals(1, count.get());
  }

  @Test
  public void removeExclusive_removesRange() {
    ListProperty<String> lp = new ListProperty<>(owner);
    lp.add("a", "b", "c", "d", "e");
    lp.removeExclusive(1, 4);
    assertEquals(2, lp.size());
    assertEquals("a", lp.get(0));
    assertEquals("e", lp.get(1));
  }

  @Test
  public void removeInclusive_removesRange() {
    ListProperty<String> lp = new ListProperty<>(owner);
    lp.add("a", "b", "c", "d", "e");
    lp.removeInclusive(1, 3);
    assertEquals(2, lp.size());
    assertEquals("a", lp.get(0));
    assertEquals("e", lp.get(1));
  }

  @Test
  public void setValue_replacesEntireList() {
    ListProperty<String> lp = new ListProperty<>(owner);
    lp.add("old1", "old2");
    ArrayList<String> newList = new ArrayList<>(Arrays.asList("new1", "new2", "new3"));
    lp.setValue(newList);
    assertEquals(3, lp.size());
    assertEquals("new1", lp.get(0));
  }

  // No-op listener base for partial overrides
  private static abstract class NoOpListPropertyListener<E> implements ListPropertyListener<E> {
    @Override public void added(AddListPropertyEvent<E> e) {}
    @Override public void cleared(ClearListPropertyEvent<E> e) {}
    @Override public void removed(RemoveListPropertyEvent<E> e) {}
    @Override public void set(SetListPropertyEvent<E> e) {}
  }

  private static class StubPropertyOwner implements InstancePropertyOwner {
    @Override public Iterable<InstanceProperty<?>> getProperties() { return null; }
    @Override public InstanceProperty<?> getPropertyNamed(String name) { return null; }
    @Override public String lookupNameFor(InstanceProperty<?> ip) { return "stub"; }
    @Override public void firePropertyChanging(PropertyEvent e) {}
    @Override public void firePropertyChanged(PropertyEvent e) {}
    @Override public void fireAdding(AddListPropertyEvent<?> e) {}
    @Override public void fireAdded(AddListPropertyEvent<?> e) {}
    @Override public void fireClearing(ClearListPropertyEvent<?> e) {}
    @Override public void fireCleared(ClearListPropertyEvent<?> e) {}
    @Override public void fireRemoving(RemoveListPropertyEvent<?> e) {}
    @Override public void fireRemoved(RemoveListPropertyEvent<?> e) {}
    @Override public void fireSetting(SetListPropertyEvent<?> e) {}
    @Override public void fireSet(SetListPropertyEvent<?> e) {}
  }
}
