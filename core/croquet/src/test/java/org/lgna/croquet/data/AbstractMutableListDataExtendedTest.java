package org.lgna.croquet.data;

import org.lgna.croquet.CroquetTestUtils;
import org.junit.Before;
import org.junit.Test;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Extended tests for {@link AbstractMutableListData} listener lifecycle
 * and {@link ListData} contract via {@link MutableListData}.
 */
public class AbstractMutableListDataExtendedTest {

  private MutableListData<String> data;

  @Before
  public void setUp() {
    data = new MutableListData<>(CroquetTestUtils.STRING_CODEC, new String[]{"a", "b", "c"});
  }

  // ── Listener add/remove ───────────────────────────────────────────

  @Test
  public void addListener_multipleListeners_allFire() {
    AtomicInteger count1 = new AtomicInteger();
    AtomicInteger count2 = new AtomicInteger();

    data.addListener(new CountingListener(count1));
    data.addListener(new CountingListener(count2));

    data.internalAddItem("d");
    assertEquals(1, count1.get());
    assertEquals(1, count2.get());
  }

  @Test
  public void removeListener_onlyRemovesSpecified() {
    AtomicInteger count1 = new AtomicInteger();
    AtomicInteger count2 = new AtomicInteger();

    ListDataListener l1 = new CountingListener(count1);
    ListDataListener l2 = new CountingListener(count2);

    data.addListener(l1);
    data.addListener(l2);
    data.removeListener(l1);

    data.internalAddItem("d");
    assertEquals(0, count1.get());
    assertEquals(1, count2.get());
  }

  @Test
  public void removeListener_nonExistent_noException() {
    data.removeListener(new CountingListener(new AtomicInteger()));
  }

  @Test
  public void addListener_sameListenerTwice_firesTwice() {
    AtomicInteger count = new AtomicInteger();
    ListDataListener l = new CountingListener(count);

    data.addListener(l);
    data.addListener(l);

    data.internalAddItem("d");
    assertEquals(2, count.get());
  }

  // ── fireContentsChanged event details ─────────────────────────────

  @Test
  public void contentsChanged_eventSource_isData() {
    AtomicReference<Object> sourceRef = new AtomicReference<>();
    data.addListener(new ListDataListener() {
      @Override public void intervalAdded(ListDataEvent e) {}
      @Override public void intervalRemoved(ListDataEvent e) {}
      @Override public void contentsChanged(ListDataEvent e) {
        sourceRef.set(e.getSource());
      }
    });
    data.internalAddItem("d");
    assertSame(data, sourceRef.get());
  }

  @Test
  public void contentsChanged_eventType() {
    AtomicInteger typeRef = new AtomicInteger(-1);
    data.addListener(new ListDataListener() {
      @Override public void intervalAdded(ListDataEvent e) {}
      @Override public void intervalRemoved(ListDataEvent e) {}
      @Override public void contentsChanged(ListDataEvent e) {
        typeRef.set(e.getType());
      }
    });
    data.internalAddItem("d");
    assertEquals(ListDataEvent.CONTENTS_CHANGED, typeRef.get());
  }

  @Test
  public void contentsChanged_firesOnRemove() {
    AtomicInteger count = new AtomicInteger();
    data.addListener(new CountingListener(count));
    data.internalRemoveItem("b");
    assertEquals(1, count.get());
  }

  @Test
  public void contentsChanged_firesOnSetAll() {
    AtomicInteger count = new AtomicInteger();
    data.addListener(new CountingListener(count));
    data.internalSetAllItems(Arrays.asList("x", "y"));
    assertEquals(1, count.get());
  }

  // ── ListData.internalAddItem(item) convenience ────────────────────

  @Test
  public void internalAddItem_noIndex_appendsToEnd() {
    data.internalAddItem("z");
    assertEquals(4, data.getItemCount());
    assertEquals("z", data.getItemAt(3));
  }

  // ── ListData.internalSetAllItems(T[]) overload ────────────────────

  @Test
  public void internalSetAllItems_array_replacesAll() {
    data.internalSetAllItems(new String[]{"x", "y"});
    assertEquals(2, data.getItemCount());
    assertEquals("x", data.getItemAt(0));
  }

  // ── ListData.toArray ──────────────────────────────────────────────

  @Test
  public void toArray_returnsCorrectItems() {
    String[] arr = data.toArray();
    assertArrayEquals(new String[]{"a", "b", "c"}, arr);
  }

  @Test
  public void toArray_afterAdd_includesNewItem() {
    data.internalAddItem("d");
    String[] arr = data.toArray();
    assertArrayEquals(new String[]{"a", "b", "c", "d"}, arr);
  }

  // ── ListData.getPreferenceKey ─────────────────────────────────────

  @Test
  public void getPreferenceKey_containsClassName() {
    assertTrue(data.getPreferenceKey().contains("MutableListData"));
  }

  // ── ListData.getItemCodec ─────────────────────────────────────────

  @Test
  public void getItemCodec_returnsSameCodec() {
    assertSame(CroquetTestUtils.STRING_CODEC, data.getItemCodec());
  }

  // ── Iterator after mutations ──────────────────────────────────────

  @Test
  public void iterator_afterAdd_includesNew() {
    data.internalAddItem("d");
    List<String> items = new ArrayList<>();
    data.iterator().forEachRemaining(items::add);
    assertEquals(Arrays.asList("a", "b", "c", "d"), items);
  }

  @Test
  public void iterator_afterRemove_excludesRemoved() {
    data.internalRemoveItem("b");
    List<String> items = new ArrayList<>();
    data.iterator().forEachRemaining(items::add);
    assertEquals(Arrays.asList("a", "c"), items);
  }

  // ── indexOf after mutations ───────────────────────────────────────

  @Test
  public void indexOf_afterAdd_findsNewItem() {
    data.internalAddItem("d");
    assertEquals(3, data.indexOf("d"));
  }

  @Test
  public void indexOf_afterRemove_returnsNegativeOne() {
    data.internalRemoveItem("b");
    assertEquals(-1, data.indexOf("b"));
  }

  // ── contains after mutations ──────────────────────────────────────

  @Test
  public void contains_afterSetAll_reflectsNewContent() {
    data.internalSetAllItems(Arrays.asList("x", "y"));
    assertTrue(data.contains("x"));
    assertFalse(data.contains("a"));
  }

  // ── Helper ────────────────────────────────────────────────────────

  private static class CountingListener implements ListDataListener {
    private final AtomicInteger count;

    CountingListener(AtomicInteger count) {
      this.count = count;
    }

    @Override public void intervalAdded(ListDataEvent e) {}
    @Override public void intervalRemoved(ListDataEvent e) {}
    @Override public void contentsChanged(ListDataEvent e) {
      count.incrementAndGet();
    }
  }
}
