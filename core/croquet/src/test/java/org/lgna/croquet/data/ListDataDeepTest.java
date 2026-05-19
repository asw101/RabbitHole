package org.lgna.croquet.data;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.CroquetTestUtils;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import static org.junit.Assert.*;

/**
 * Deep coverage tests for {@link ListData} — abstract base class of all
 * list data implementations. Tests via MutableListData as concrete impl.
 */
public class ListDataDeepTest {

  private MutableListData<String> data;

  @Before
  public void setUp() {
    data = new MutableListData<>(CroquetTestUtils.STRING_CODEC);
  }

  // ── getItemCodec ──────────────────────────────────────────────────

  @Test
  public void getItemCodec_nonNull() {
    assertNotNull(data.getItemCodec());
  }

  @Test
  public void getItemCodec_sameInstance() {
    assertSame(CroquetTestUtils.STRING_CODEC, data.getItemCodec());
  }

  // ── getPreferenceKey ──────────────────────────────────────────────

  @Test
  public void getPreferenceKey_nonNull() {
    assertNotNull(data.getPreferenceKey());
  }

  @Test
  public void getPreferenceKey_containsClassName() {
    assertTrue(data.getPreferenceKey().contains("MutableListData"));
  }

  // ── Empty list operations ─────────────────────────────────────────

  @Test
  public void empty_getItemCount_zero() {
    assertEquals(0, data.getItemCount());
  }

  @Test
  public void empty_contains_false() {
    assertFalse(data.contains("anything"));
  }

  @Test
  public void empty_indexOf_negative() {
    assertTrue(data.indexOf("anything") < 0);
  }

  @Test
  public void empty_iterator_hasNoElements() {
    assertFalse(data.iterator().hasNext());
  }

  @Test
  public void empty_toArray_emptyArray() {
    String[] arr = data.toArray();
    assertNotNull(arr);
    assertEquals(0, arr.length);
  }

  // ── internalAddItem (vararg) ──────────────────────────────────────

  @Test
  public void internalAddItem_appendsItem() {
    data.internalAddItem("first");
    assertEquals(1, data.getItemCount());
    assertEquals("first", data.getItemAt(0));
  }

  @Test
  public void internalAddItem_multipleItems() {
    data.internalAddItem("a");
    data.internalAddItem("b");
    data.internalAddItem("c");
    assertEquals(3, data.getItemCount());
  }

  @Test
  public void internalAddItem_atIndex_zero() {
    data.internalAddItem("existing");
    data.internalAddItem(0, "first");
    assertEquals("first", data.getItemAt(0));
    assertEquals("existing", data.getItemAt(1));
  }

  @Test
  public void internalAddItem_atIndex_middle() {
    data.internalAddItem("a");
    data.internalAddItem("c");
    data.internalAddItem(1, "b");
    assertEquals("b", data.getItemAt(1));
    assertEquals(3, data.getItemCount());
  }

  // ── internalRemoveItem ────────────────────────────────────────────

  @Test
  public void internalRemoveItem_removesItem() {
    data.internalAddItem("remove-me");
    data.internalRemoveItem("remove-me");
    assertEquals(0, data.getItemCount());
  }

  @Test
  public void internalRemoveItem_nonExistent_noException() {
    data.internalRemoveItem("ghost");
  }

  @Test
  public void internalRemoveItem_fromMultiple() {
    data.internalAddItem("a");
    data.internalAddItem("b");
    data.internalAddItem("c");
    data.internalRemoveItem("b");
    assertEquals(2, data.getItemCount());
    assertFalse(data.contains("b"));
  }

  // ── internalSetAllItems ───────────────────────────────────────────

  @Test
  public void internalSetAllItems_collection() {
    data.internalSetAllItems(Arrays.asList("x", "y", "z"));
    assertEquals(3, data.getItemCount());
    assertEquals("x", data.getItemAt(0));
    assertEquals("z", data.getItemAt(2));
  }

  @Test
  public void internalSetAllItems_array() {
    data.internalSetAllItems(new String[]{"1", "2"});
    assertEquals(2, data.getItemCount());
  }

  @Test
  public void internalSetAllItems_emptyCollection() {
    data.internalAddItem("old");
    data.internalSetAllItems(Collections.emptyList());
    assertEquals(0, data.getItemCount());
  }

  @Test
  public void internalSetAllItems_replacesPrevious() {
    data.internalAddItem("old1");
    data.internalAddItem("old2");
    data.internalSetAllItems(Arrays.asList("new1"));
    assertEquals(1, data.getItemCount());
    assertEquals("new1", data.getItemAt(0));
  }

  // ── contains ──────────────────────────────────────────────────────

  @Test
  public void contains_true_afterAdd() {
    data.internalAddItem("item");
    assertTrue(data.contains("item"));
  }

  @Test
  public void contains_false_afterRemove() {
    data.internalAddItem("item");
    data.internalRemoveItem("item");
    assertFalse(data.contains("item"));
  }

  // ── indexOf ───────────────────────────────────────────────────────

  @Test
  public void indexOf_found() {
    data.internalAddItem("a");
    data.internalAddItem("b");
    assertEquals(1, data.indexOf("b"));
  }

  @Test
  public void indexOf_notFound() {
    data.internalAddItem("a");
    assertTrue(data.indexOf("z") < 0);
  }

  // ── iterator ──────────────────────────────────────────────────────

  @Test
  public void iterator_iteratesAll() {
    data.internalSetAllItems(Arrays.asList("a", "b", "c"));
    Iterator<String> it = data.iterator();
    assertTrue(it.hasNext());
    assertEquals("a", it.next());
    assertEquals("b", it.next());
    assertEquals("c", it.next());
    assertFalse(it.hasNext());
  }

  @Test
  public void iterator_forEachLoop() {
    data.internalSetAllItems(Arrays.asList("x", "y"));
    int count = 0;
    for (String s : data) {
      assertNotNull(s);
      count++;
    }
    assertEquals(2, count);
  }

  // ── toArray ───────────────────────────────────────────────────────

  @Test
  public void toArray_matchesItems() {
    data.internalSetAllItems(Arrays.asList("a", "b"));
    String[] arr = data.toArray();
    assertEquals(2, arr.length);
    assertEquals("a", arr[0]);
    assertEquals("b", arr[1]);
  }

  // ── Listener management ───────────────────────────────────────────

  @Test
  public void addListener_removeListener_noException() {
    CountingListener listener = new CountingListener();
    data.addListener(listener);
    data.removeListener(listener);
  }

  @Test
  public void listener_firesOnAdd() {
    CountingListener listener = new CountingListener();
    data.addListener(listener);
    data.internalAddItem("trigger");
    assertTrue(listener.contentsChangedCount > 0);
  }

  @Test
  public void listener_firesOnRemove() {
    data.internalAddItem("item");
    CountingListener listener = new CountingListener();
    data.addListener(listener);
    data.internalRemoveItem("item");
    assertTrue(listener.contentsChangedCount > 0);
  }

  @Test
  public void listener_firesOnSetAll() {
    CountingListener listener = new CountingListener();
    data.addListener(listener);
    data.internalSetAllItems(Arrays.asList("a", "b"));
    assertTrue(listener.contentsChangedCount > 0);
  }

  @Test
  public void listener_removedDoesNotFire() {
    CountingListener listener = new CountingListener();
    data.addListener(listener);
    data.removeListener(listener);
    data.internalAddItem("quiet");
    assertEquals(0, listener.contentsChangedCount);
  }

  @Test
  public void multipleListeners_allNotified() {
    CountingListener l1 = new CountingListener();
    CountingListener l2 = new CountingListener();
    data.addListener(l1);
    data.addListener(l2);
    data.internalAddItem("x");
    assertTrue(l1.contentsChangedCount > 0);
    assertTrue(l2.contentsChangedCount > 0);
  }

  // ── getItemAt edge cases ──────────────────────────────────────────

  @Test
  public void getItemAt_outOfBounds_returnsNull() {
    assertNull(data.getItemAt(0));
  }

  @Test
  public void getItemAt_negative_returnsNull() {
    assertNull(data.getItemAt(-1));
  }

  // ── Hierarchy ─────────────────────────────────────────────────────

  @Test
  public void listData_isAbstract() {
    assertTrue(java.lang.reflect.Modifier.isAbstract(ListData.class.getModifiers()));
  }

  @Test
  public void listData_implementsIterable() {
    assertTrue(Iterable.class.isAssignableFrom(ListData.class));
  }

  @Test
  public void mutableListData_extendsAbstractMutableListData() {
    assertTrue(AbstractMutableListData.class.isAssignableFrom(MutableListData.class));
  }

  @Test
  public void abstractMutableListData_extendsListData() {
    assertTrue(ListData.class.isAssignableFrom(AbstractMutableListData.class));
  }

  // ── CountingListener ──────────────────────────────────────────────

  private static class CountingListener implements ListDataListener {
    int contentsChangedCount = 0;
    int intervalAddedCount = 0;
    int intervalRemovedCount = 0;

    @Override
    public void contentsChanged(ListDataEvent e) {
      contentsChangedCount++;
    }

    @Override
    public void intervalAdded(ListDataEvent e) {
      intervalAddedCount++;
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
      intervalRemovedCount++;
    }
  }
}
