package org.lgna.croquet.data;

import org.lgna.croquet.CroquetTestUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Expanded tests for {@link MutableListData} — deeper coverage of
 * add, remove, setAll, listener dispatch, and edge cases.
 */
public class MutableListDataExpandedTest {

  @Test
  public void emptyConstructor_isEmpty() {
    MutableListData<String> data = new MutableListData<>(CroquetTestUtils.STRING_CODEC);
    assertEquals(0, data.getItemCount());
  }

  @Test
  public void arrayConstructor_populatesItems() {
    MutableListData<String> data = new MutableListData<>(
        CroquetTestUtils.STRING_CODEC, new String[]{"a", "b", "c"});
    assertEquals(3, data.getItemCount());
  }

  @Test
  public void collectionConstructor_populatesItems() {
    MutableListData<String> data = new MutableListData<>(
        CroquetTestUtils.STRING_CODEC, Arrays.asList("x", "y"));
    assertEquals(2, data.getItemCount());
  }

  @Test
  public void getItemAt_returnsCorrectItem() {
    MutableListData<String> data = new MutableListData<>(
        CroquetTestUtils.STRING_CODEC, new String[]{"a", "b", "c"});
    assertEquals("a", data.getItemAt(0));
    assertEquals("b", data.getItemAt(1));
    assertEquals("c", data.getItemAt(2));
  }

  @Test
  public void getItemAt_negativeIndex_returnsNull() {
    MutableListData<String> data = new MutableListData<>(
        CroquetTestUtils.STRING_CODEC, new String[]{"a"});
    assertNull(data.getItemAt(-1));
  }

  @Test
  public void getItemAt_outOfBounds_returnsNull() {
    MutableListData<String> data = new MutableListData<>(
        CroquetTestUtils.STRING_CODEC, new String[]{"a"});
    assertNull(data.getItemAt(5));
  }

  @Test
  public void contains_true() {
    MutableListData<String> data = new MutableListData<>(
        CroquetTestUtils.STRING_CODEC, new String[]{"a", "b"});
    assertTrue(data.contains("a"));
  }

  @Test
  public void contains_false() {
    MutableListData<String> data = new MutableListData<>(
        CroquetTestUtils.STRING_CODEC, new String[]{"a", "b"});
    assertFalse(data.contains("z"));
  }

  @Test
  public void indexOf_found() {
    MutableListData<String> data = new MutableListData<>(
        CroquetTestUtils.STRING_CODEC, new String[]{"a", "b", "c"});
    assertEquals(1, data.indexOf("b"));
  }

  @Test
  public void indexOf_notFound() {
    MutableListData<String> data = new MutableListData<>(
        CroquetTestUtils.STRING_CODEC, new String[]{"a"});
    assertEquals(-1, data.indexOf("z"));
  }

  @Test
  public void internalAddItem_atIndex() {
    MutableListData<String> data = new MutableListData<>(
        CroquetTestUtils.STRING_CODEC, new String[]{"a", "c"});
    data.internalAddItem(1, "b");
    assertEquals(3, data.getItemCount());
    assertEquals("b", data.getItemAt(1));
  }

  @Test
  public void internalRemoveItem() {
    MutableListData<String> data = new MutableListData<>(
        CroquetTestUtils.STRING_CODEC, new String[]{"a", "b", "c"});
    data.internalRemoveItem("b");
    assertEquals(2, data.getItemCount());
    assertFalse(data.contains("b"));
  }

  @Test
  public void internalSetAllItems_replacesAll() {
    MutableListData<String> data = new MutableListData<>(
        CroquetTestUtils.STRING_CODEC, new String[]{"a", "b"});
    data.internalSetAllItems(Arrays.asList("x", "y", "z"));
    assertEquals(3, data.getItemCount());
    assertEquals("x", data.getItemAt(0));
  }

  @Test
  public void iterator_iteratesAllItems() {
    MutableListData<String> data = new MutableListData<>(
        CroquetTestUtils.STRING_CODEC, new String[]{"a", "b", "c"});
    Iterator<String> it = data.iterator();
    assertTrue(it.hasNext());
    assertEquals("a", it.next());
    assertEquals("b", it.next());
    assertEquals("c", it.next());
    assertFalse(it.hasNext());
  }

  @Test
  public void getItemCodec_returnsCodec() {
    MutableListData<String> data = new MutableListData<>(CroquetTestUtils.STRING_CODEC);
    assertSame(CroquetTestUtils.STRING_CODEC, data.getItemCodec());
  }

  @Test
  public void internalAddItem_tail() {
    MutableListData<String> data = new MutableListData<>(CroquetTestUtils.STRING_CODEC);
    data.internalAddItem("first");
    data.internalAddItem("second");
    assertEquals(2, data.getItemCount());
    assertEquals("first", data.getItemAt(0));
    assertEquals("second", data.getItemAt(1));
  }

  @Test
  public void listener_firesOnAdd() {
    MutableListData<String> data = new MutableListData<>(CroquetTestUtils.STRING_CODEC);
    AtomicInteger count = new AtomicInteger(0);
    data.addListener(new TestListDataListener() {
      @Override
      public void contentsChanged(javax.swing.event.ListDataEvent e) {
        count.incrementAndGet();
      }
    });
    data.internalAddItem(0, "item");
    assertTrue(count.get() > 0);
  }

  @Test
  public void listener_firesOnRemove() {
    MutableListData<String> data = new MutableListData<>(
        CroquetTestUtils.STRING_CODEC, new String[]{"a"});
    AtomicInteger count = new AtomicInteger(0);
    data.addListener(new TestListDataListener() {
      @Override
      public void contentsChanged(javax.swing.event.ListDataEvent e) {
        count.incrementAndGet();
      }
    });
    data.internalRemoveItem("a");
    assertTrue(count.get() > 0);
  }

  @Test
  public void listener_firesOnSetAll() {
    MutableListData<String> data = new MutableListData<>(CroquetTestUtils.STRING_CODEC);
    AtomicInteger count = new AtomicInteger(0);
    data.addListener(new TestListDataListener() {
      @Override
      public void contentsChanged(javax.swing.event.ListDataEvent e) {
        count.incrementAndGet();
      }
    });
    data.internalSetAllItems(Arrays.asList("x", "y"));
    assertTrue(count.get() > 0);
  }
}
