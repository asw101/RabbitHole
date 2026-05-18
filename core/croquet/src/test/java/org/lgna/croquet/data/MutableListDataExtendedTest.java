package org.lgna.croquet.data;

import org.lgna.croquet.CroquetTestUtils;
import org.lgna.croquet.ItemCodec;
import org.junit.Test;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Extended tests for {@link MutableListData} — covers add/remove operations,
 * listener notifications, and boundary conditions.
 */
public class MutableListDataExtendedTest {

  private static final ItemCodec<String> CODEC = CroquetTestUtils.STRING_CODEC;

  @Test
  public void newMutableListData_isEmpty() {
    MutableListData<String> data = new MutableListData<>(CODEC);
    assertEquals(0, data.getItemCount());
  }

  @Test
  public void add_singleItem() {
    MutableListData<String> data = new MutableListData<>(CODEC);
    data.internalAddItem(data.getItemCount(), "first");
    assertEquals(1, data.getItemCount());
    assertEquals("first", data.getItemAt(0));
  }

  @Test
  public void add_multipleItems() {
    MutableListData<String> data = new MutableListData<>(CODEC);
    data.internalAddItem(data.getItemCount(), "a");
    data.internalAddItem(data.getItemCount(), "b");
    data.internalAddItem(data.getItemCount(), "c");
    assertEquals(3, data.getItemCount());
  }

  @Test
  public void add_preservesOrder() {
    MutableListData<String> data = new MutableListData<>(CODEC);
    data.internalAddItem(data.getItemCount(), "first");
    data.internalAddItem(data.getItemCount(), "second");
    data.internalAddItem(data.getItemCount(), "third");
    assertEquals("first", data.getItemAt(0));
    assertEquals("second", data.getItemAt(1));
    assertEquals("third", data.getItemAt(2));
  }

  @Test
  public void remove_singleItem() {
    MutableListData<String> data = new MutableListData<>(CODEC);
    data.internalAddItem(data.getItemCount(), "a");
    data.internalAddItem(data.getItemCount(), "b");
    data.internalRemoveItem("a");
    assertEquals(1, data.getItemCount());
    assertEquals("b", data.getItemAt(0));
  }

  @Test
  public void remove_lastItem() {
    MutableListData<String> data = new MutableListData<>(CODEC);
    data.internalAddItem(data.getItemCount(), "only");
    data.internalRemoveItem("only");
    assertEquals(0, data.getItemCount());
  }

  @Test
  public void clear_removesAll() {
    MutableListData<String> data = new MutableListData<>(CODEC);
    data.internalAddItem(data.getItemCount(), "a");
    data.internalAddItem(data.getItemCount(), "b");
    data.internalAddItem(data.getItemCount(), "c");
    data.internalSetAllItems(java.util.Collections.emptyList());
    assertEquals(0, data.getItemCount());
  }

  @Test
  public void indexOf_existingItem() {
    MutableListData<String> data = new MutableListData<>(CODEC);
    data.internalAddItem(data.getItemCount(), "x");
    data.internalAddItem(data.getItemCount(), "y");
    data.internalAddItem(data.getItemCount(), "z");
    assertEquals(1, data.indexOf("y"));
  }

  @Test
  public void indexOf_missingItem() {
    MutableListData<String> data = new MutableListData<>(CODEC);
    data.internalAddItem(data.getItemCount(), "x");
    assertEquals(-1, data.indexOf("missing"));
  }

  @Test
  public void contains_true() {
    MutableListData<String> data = new MutableListData<>(CODEC);
    data.internalAddItem(data.getItemCount(), "present");
    assertTrue(data.contains("present"));
  }

  @Test
  public void contains_false() {
    MutableListData<String> data = new MutableListData<>(CODEC);
    data.internalAddItem(data.getItemCount(), "present");
    assertFalse(data.contains("absent"));
  }

  @Test
  public void getItemCodec_returns_codec() {
    MutableListData<String> data = new MutableListData<>(CODEC);
    assertSame(CODEC, data.getItemCodec());
  }

  @Test
  public void listener_notifiedOnAdd() {
    MutableListData<String> data = new MutableListData<>(CODEC);
    AtomicInteger addCount = new AtomicInteger(0);
    data.addListener(new ListDataListener() {
      @Override public void intervalAdded(ListDataEvent e) { addCount.incrementAndGet(); }
      @Override public void intervalRemoved(ListDataEvent e) {}
      @Override public void contentsChanged(ListDataEvent e) {}
    });
    data.internalAddItem(data.getItemCount(), "item");
    assertEquals(1, addCount.get());
  }

  @Test
  public void listener_notifiedOnRemove() {
    MutableListData<String> data = new MutableListData<>(CODEC);
    data.internalAddItem(data.getItemCount(), "item");
    AtomicInteger removeCount = new AtomicInteger(0);
    data.addListener(new ListDataListener() {
      @Override public void intervalAdded(ListDataEvent e) {}
      @Override public void intervalRemoved(ListDataEvent e) { removeCount.incrementAndGet(); }
      @Override public void contentsChanged(ListDataEvent e) {}
    });
    data.internalRemoveItem("item");
    assertEquals(1, removeCount.get());
  }

  @Test
  public void toArray_correctContent() {
    MutableListData<String> data = new MutableListData<>(CODEC);
    data.internalAddItem(data.getItemCount(), "a");
    data.internalAddItem(data.getItemCount(), "b");
    String[] arr = data.toArray(String.class);
    assertArrayEquals(new String[]{"a", "b"}, arr);
  }

  @Test
  public void toArray_empty() {
    MutableListData<String> data = new MutableListData<>(CODEC);
    String[] arr = data.toArray(String.class);
    assertEquals(0, arr.length);
  }
}
