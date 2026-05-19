package org.lgna.croquet.data;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.CroquetTestUtils;

import javax.swing.event.ListDataEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Extended tests for {@link MutableListData} — mutation, listeners, thread safety.
 */
public class MutableListDataExtendedTest {

  private MutableListData<String> data;

  @Before
  public void setUp() {
    data = new MutableListData<>(CroquetTestUtils.STRING_CODEC);
  }

  // ── Initial state ─────────────────────────────────────────────────

  @Test
  public void initialState_isEmpty() {
    assertEquals(0, data.getItemCount());
  }

  @Test
  public void initialState_toArrayEmpty() {
    assertEquals(0, data.toArray().length);
  }

  @Test
  public void initialState_iteratorEmpty() {
    assertFalse(data.iterator().hasNext());
  }

  // ── add / remove ──────────────────────────────────────────────────

  @Test
  public void add_singleItem() {
    data.internalAddItem(0, "alpha");
    assertEquals(1, data.getItemCount());
    assertEquals("alpha", data.getItemAt(0));
  }

  @Test
  public void add_multipleItems() {
    data.internalAddItem(0, "a");
    data.internalAddItem(1, "b");
    data.internalAddItem(2, "c");
    assertEquals(3, data.getItemCount());
  }

  @Test
  public void remove_singleItem() {
    data.internalAddItem(0, "x");
    data.internalRemoveItem("x");
    assertEquals(0, data.getItemCount());
  }

  @Test
  public void remove_preservesOtherItems() {
    data.internalAddItem(0, "a");
    data.internalAddItem(1, "b");
    data.internalAddItem(2, "c");
    data.internalRemoveItem("b");
    assertEquals(2, data.getItemCount());
    assertEquals("a", data.getItemAt(0));
    assertEquals("c", data.getItemAt(1));
  }

  // ── setAllItems ───────────────────────────────────────────────────

  @Test
  public void setAllItems_replacesAll() {
    data.internalAddItem(0, "old");
    data.internalSetAllItems(Arrays.asList("new1", "new2"));
    assertEquals(2, data.getItemCount());
    assertEquals("new1", data.getItemAt(0));
    assertEquals("new2", data.getItemAt(1));
  }

  @Test
  public void setAllItems_emptyList_clears() {
    data.internalAddItem(data.getItemCount(), "x");
    data.internalSetAllItems(Collections.emptyList());
    assertEquals(0, data.getItemCount());
  }

  // ── Listeners ─────────────────────────────────────────────────────

  @Test
  public void listener_firedOnAdd() {
    AtomicInteger count = new AtomicInteger();
    data.addListener(new TestListDataListener() {
      @Override
      public void contentsChanged(ListDataEvent e) {
        count.incrementAndGet();
      }
      @Override
      public void intervalAdded(ListDataEvent e) {
        count.incrementAndGet();
      }
    });
    data.internalAddItem(data.getItemCount(), "a");
    assertTrue(count.get() > 0);
  }

  @Test
  public void listener_firedOnRemove() {
    data.internalAddItem(data.getItemCount(), "a");
    AtomicInteger count = new AtomicInteger();
    data.addListener(new TestListDataListener() {
      @Override
      public void contentsChanged(ListDataEvent e) {
        count.incrementAndGet();
      }
      @Override
      public void intervalRemoved(ListDataEvent e) {
        count.incrementAndGet();
      }
    });
    data.internalRemoveItem("a");
    assertTrue(count.get() > 0);
  }

  @Test
  public void listener_firedOnSetAll() {
    AtomicInteger count = new AtomicInteger();
    data.addListener(new TestListDataListener() {
      @Override
      public void contentsChanged(ListDataEvent e) {
        count.incrementAndGet();
      }
    });
    data.internalSetAllItems(Arrays.asList("a", "b"));
    assertTrue(count.get() > 0);
  }

  @Test
  public void removeListener_stopsNotifications() {
    AtomicInteger count = new AtomicInteger();
    TestListDataListener listener = new TestListDataListener() {
      @Override
      public void contentsChanged(ListDataEvent e) {
        count.incrementAndGet();
      }
      @Override
      public void intervalAdded(ListDataEvent e) {
        count.incrementAndGet();
      }
    };
    data.addListener(listener);
    data.internalAddItem(data.getItemCount(), "a");
    int after1 = count.get();
    data.removeListener(listener);
    data.internalAddItem(data.getItemCount(), "b");
    assertEquals(after1, count.get());
  }

  // ── Iterator ──────────────────────────────────────────────────────

  @Test
  public void iterator_returnsAllItems() {
    data.internalAddItem(data.getItemCount(), "a");
    data.internalAddItem(data.getItemCount(), "b");
    int count = 0;
    for (String s : data) {
      count++;
      assertNotNull(s);
    }
    assertEquals(2, count);
  }

  // ── toArray ───────────────────────────────────────────────────────

  @Test
  public void toArray_returnsCorrectSize() {
    data.internalSetAllItems(Arrays.asList("x", "y", "z"));
    assertEquals(3, data.toArray().length);
  }

  @Test
  public void toArray_returnsCorrectValues() {
    data.internalSetAllItems(Arrays.asList("x", "y"));
    Object[] arr = data.toArray();
    assertEquals("x", arr[0]);
    assertEquals("y", arr[1]);
  }

  // ── getItemCodec ──────────────────────────────────────────────────

  @Test
  public void getItemCodec_returnsNonNull() {
    assertNotNull(data.getItemCodec());
  }

  @Test
  public void getItemCodec_returnsStringCodec() {
    assertSame(CroquetTestUtils.STRING_CODEC, data.getItemCodec());
  }

  // ── Edge cases ────────────────────────────────────────────────────

  @Test
  public void add_duplicateItems() {
    data.internalAddItem(data.getItemCount(), "dup");
    data.internalAddItem(data.getItemCount(), "dup");
    assertEquals(2, data.getItemCount());
  }

  @Test
  public void remove_nonExistent_noException() {
    data.internalRemoveItem("nonexistent");
    assertEquals(0, data.getItemCount());
  }

  @Test
  public void indexOf_returnsCorrectPosition() {
    data.internalSetAllItems(Arrays.asList("a", "b", "c"));
    assertEquals(1, data.indexOf("b"));
  }

  @Test
  public void indexOf_notFound_returnsNegative() {
    data.internalSetAllItems(Arrays.asList("a", "b"));
    assertTrue(data.indexOf("z") < 0);
  }

  @Test
  public void contains_true() {
    data.internalAddItem(data.getItemCount(), "hello");
    assertTrue(data.contains("hello"));
  }

  @Test
  public void contains_false() {
    assertFalse(data.contains("nope"));
  }
}
