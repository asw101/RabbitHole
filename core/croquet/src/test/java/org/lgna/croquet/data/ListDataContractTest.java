package org.lgna.croquet.data;

import org.lgna.croquet.CroquetTestUtils;
import org.junit.Before;
import org.junit.Test;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Contract tests for the {@link ListData} abstract base via {@link MutableListData} —
 * covers the full ListData/MutableListData API contract including boundary conditions,
 * mutation sequences, and listener ordering.
 */
public class ListDataContractTest {

  private MutableListData<String> data;

  @Before
  public void setUp() {
    data = new MutableListData<>(CroquetTestUtils.STRING_CODEC);
  }

  // ── Empty list contract ───────────────────────────────────────────

  @Test
  public void empty_getItemCount_returnsZero() {
    assertEquals(0, data.getItemCount());
  }

  @Test
  public void empty_contains_returnsFalse() {
    assertFalse(data.contains("anything"));
  }

  @Test
  public void empty_indexOf_returnsNegativeOne() {
    assertEquals(-1, data.indexOf("anything"));
  }

  @Test
  public void empty_getItemAt_zero_returnsNull() {
    assertNull(data.getItemAt(0));
  }

  @Test
  public void empty_iterator_hasNoNext() {
    assertFalse(data.iterator().hasNext());
  }

  @Test
  public void empty_toArray_returnsEmptyArray() {
    String[] arr = data.toArray();
    assertNotNull(arr);
    assertEquals(0, arr.length);
  }

  // ── Single element ────────────────────────────────────────────────

  @Test
  public void singleAdd_incrementsCount() {
    data.internalAddItem("one");
    assertEquals(1, data.getItemCount());
  }

  @Test
  public void singleAdd_containsTrue() {
    data.internalAddItem("one");
    assertTrue(data.contains("one"));
  }

  @Test
  public void singleAdd_indexOf_returnsZero() {
    data.internalAddItem("one");
    assertEquals(0, data.indexOf("one"));
  }

  @Test
  public void singleAdd_getItemAt_returnsItem() {
    data.internalAddItem("one");
    assertEquals("one", data.getItemAt(0));
  }

  @Test
  public void singleAdd_iterator_hasOneElement() {
    data.internalAddItem("one");
    Iterator<String> it = data.iterator();
    assertTrue(it.hasNext());
    assertEquals("one", it.next());
    assertFalse(it.hasNext());
  }

  // ── Add at specific index ─────────────────────────────────────────

  @Test
  public void addAtIndex_zero_insertsAtFront() {
    data.internalAddItem("b");
    data.internalAddItem(0, "a");
    assertEquals("a", data.getItemAt(0));
    assertEquals("b", data.getItemAt(1));
  }

  @Test
  public void addAtIndex_middle_inserts() {
    data.internalAddItem("a");
    data.internalAddItem("c");
    data.internalAddItem(1, "b");
    assertEquals("b", data.getItemAt(1));
    assertEquals(3, data.getItemCount());
  }

  @Test
  public void addAtIndex_end_appends() {
    data.internalAddItem("a");
    data.internalAddItem("b");
    data.internalAddItem(2, "c");
    assertEquals("c", data.getItemAt(2));
  }

  // ── Remove ────────────────────────────────────────────────────────

  @Test
  public void remove_existingItem_decrementsCount() {
    data.internalAddItem("a");
    data.internalAddItem("b");
    data.internalRemoveItem("a");
    assertEquals(1, data.getItemCount());
  }

  @Test
  public void remove_existingItem_notContained() {
    data.internalAddItem("a");
    data.internalRemoveItem("a");
    assertFalse(data.contains("a"));
  }

  @Test
  public void remove_nonExistentItem_noEffect() {
    data.internalAddItem("a");
    data.internalRemoveItem("missing");
    assertEquals(1, data.getItemCount());
  }

  @Test
  public void remove_fromEmpty_noException() {
    data.internalRemoveItem("anything");
    assertEquals(0, data.getItemCount());
  }

  // ── setAllItems ───────────────────────────────────────────────────

  @Test
  public void setAllItems_collection_replacesAll() {
    data.internalAddItem("old");
    data.internalSetAllItems(Arrays.asList("x", "y", "z"));
    assertEquals(3, data.getItemCount());
    assertEquals("x", data.getItemAt(0));
    assertEquals("y", data.getItemAt(1));
    assertEquals("z", data.getItemAt(2));
  }

  @Test
  public void setAllItems_array_replacesAll() {
    data.internalAddItem("old");
    data.internalSetAllItems(new String[]{"p", "q"});
    assertEquals(2, data.getItemCount());
    assertEquals("p", data.getItemAt(0));
  }

  @Test
  public void setAllItems_emptyCollection_clearsAll() {
    data.internalAddItem("a");
    data.internalAddItem("b");
    data.internalSetAllItems(Arrays.asList());
    assertEquals(0, data.getItemCount());
  }

  // ── toArray consistency ───────────────────────────────────────────

  @Test
  public void toArray_matchesIteration() {
    data.internalSetAllItems(Arrays.asList("a", "b", "c"));
    String[] arr = data.toArray();
    List<String> fromIterator = new ArrayList<>();
    data.iterator().forEachRemaining(fromIterator::add);
    assertArrayEquals(arr, fromIterator.toArray(new String[0]));
  }

  @Test
  public void toArray_matchesGetItemAt() {
    data.internalSetAllItems(Arrays.asList("a", "b", "c"));
    String[] arr = data.toArray();
    for (int i = 0; i < arr.length; i++) {
      assertEquals(arr[i], data.getItemAt(i));
    }
  }

  // ── Boundary conditions ───────────────────────────────────────────

  @Test
  public void getItemAt_negativeIndex_returnsNull() {
    data.internalAddItem("a");
    assertNull(data.getItemAt(-1));
  }

  @Test
  public void getItemAt_largeIndex_returnsNull() {
    data.internalAddItem("a");
    assertNull(data.getItemAt(100));
  }

  // ── Listener ordering: multiple mutations ─────────────────────────

  @Test
  public void listener_firesForEachMutation() {
    AtomicInteger count = new AtomicInteger();
    data.addListener(new ListDataListener() {
      @Override public void intervalAdded(ListDataEvent e) {}
      @Override public void intervalRemoved(ListDataEvent e) {}
      @Override public void contentsChanged(ListDataEvent e) { count.incrementAndGet(); }
    });
    data.internalAddItem("a");
    data.internalAddItem("b");
    data.internalRemoveItem("a");
    data.internalSetAllItems(Arrays.asList("x"));
    assertEquals(4, count.get());
  }

  @Test
  public void listener_removed_stopsReceiving() {
    AtomicInteger count = new AtomicInteger();
    ListDataListener l = new ListDataListener() {
      @Override public void intervalAdded(ListDataEvent e) {}
      @Override public void intervalRemoved(ListDataEvent e) {}
      @Override public void contentsChanged(ListDataEvent e) { count.incrementAndGet(); }
    };
    data.addListener(l);
    data.internalAddItem("a");
    assertEquals(1, count.get());
    data.removeListener(l);
    data.internalAddItem("b");
    assertEquals(1, count.get());
  }

  // ── getPreferenceKey ──────────────────────────────────────────────

  @Test
  public void getPreferenceKey_returnsClassName() {
    String key = data.getPreferenceKey();
    assertNotNull(key);
    assertTrue(key.contains("MutableListData"));
  }

  // ── getItemCodec ──────────────────────────────────────────────────

  @Test
  public void getItemCodec_matchesConstructorArg() {
    assertSame(CroquetTestUtils.STRING_CODEC, data.getItemCodec());
  }

  // ── Mutation sequences ────────────────────────────────────────────

  @Test
  public void addRemoveAdd_correctState() {
    data.internalAddItem("a");
    data.internalRemoveItem("a");
    data.internalAddItem("b");
    assertEquals(1, data.getItemCount());
    assertEquals("b", data.getItemAt(0));
  }

  @Test
  public void setAll_thenAdd_works() {
    data.internalSetAllItems(Arrays.asList("x", "y"));
    data.internalAddItem("z");
    assertEquals(3, data.getItemCount());
    assertEquals("z", data.getItemAt(2));
  }

  @Test
  public void setAll_thenRemove_works() {
    data.internalSetAllItems(Arrays.asList("x", "y", "z"));
    data.internalRemoveItem("y");
    assertEquals(2, data.getItemCount());
    assertEquals("x", data.getItemAt(0));
    assertEquals("z", data.getItemAt(1));
  }

  // ── Large dataset ─────────────────────────────────────────────────

  @Test
  public void largeDataset_addAndQuery() {
    for (int i = 0; i < 1000; i++) {
      data.internalAddItem("item_" + i);
    }
    assertEquals(1000, data.getItemCount());
    assertEquals("item_0", data.getItemAt(0));
    assertEquals("item_999", data.getItemAt(999));
    assertTrue(data.contains("item_500"));
    assertEquals(500, data.indexOf("item_500"));
  }
}
