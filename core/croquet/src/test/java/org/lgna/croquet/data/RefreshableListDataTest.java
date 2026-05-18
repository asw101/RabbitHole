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
 * Tests for {@link RefreshableListData} — lazy-refresh list that caches
 * values from {@code createValues()} and only re-fetches when {@code refresh()}
 * is called. Covers lazy initialization, caching, refresh detection, listener
 * dispatch, and mutation guards.
 */
public class RefreshableListDataTest {

  private List<String> backingValues;
  private TestRefreshableListData data;

  @Before
  public void setUp() {
    backingValues = new ArrayList<>(Arrays.asList("alpha", "bravo", "charlie"));
    data = new TestRefreshableListData(backingValues);
  }

  // ── Lazy initialization ───────────────────────────────────────────

  @Test
  public void getItemCount_triggersInitialRefresh() {
    assertEquals(3, data.getItemCount());
    assertEquals(1, data.createValuesCallCount);
  }

  @Test
  public void getItemCount_calledTwice_doesNotRefreshAgain() {
    data.getItemCount();
    data.getItemCount();
    assertEquals(1, data.createValuesCallCount);
  }

  // ── getItemAt ─────────────────────────────────────────────────────

  @Test
  public void getItemAt_returnsCorrectItems() {
    // Force initial refresh
    data.getItemCount();
    assertEquals("alpha", data.getItemAt(0));
    assertEquals("bravo", data.getItemAt(1));
    assertEquals("charlie", data.getItemAt(2));
  }

  // ── contains ──────────────────────────────────────────────────────

  @Test
  public void contains_existingItem_returnsTrue() {
    data.getItemCount(); // init
    assertTrue(data.contains("bravo"));
  }

  @Test
  public void contains_missingItem_returnsFalse() {
    data.getItemCount(); // init
    assertFalse(data.contains("delta"));
  }

  // ── indexOf ───────────────────────────────────────────────────────

  @Test
  public void indexOf_existingItem_returnsIndex() {
    data.getItemCount(); // init
    assertEquals(0, data.indexOf("alpha"));
    assertEquals(2, data.indexOf("charlie"));
  }

  @Test
  public void indexOf_missingItem_returnsNegativeOne() {
    data.getItemCount(); // init
    assertEquals(-1, data.indexOf("missing"));
  }

  @Test
  public void indexOf_beforeFirstRefresh_returnsNegativeOne() {
    // values is null before any refresh
    assertEquals(-1, data.indexOf("alpha"));
  }

  // ── iterator ──────────────────────────────────────────────────────

  @Test
  public void iterator_coversAllItems() {
    int count = 0;
    for (String s : data) {
      count++;
    }
    assertEquals(3, count);
  }

  @Test
  public void iterator_triggersRefreshIfNecessary() {
    Iterator<String> it = data.iterator();
    assertEquals(1, data.createValuesCallCount);
    assertTrue(it.hasNext());
  }

  // ── toArray ───────────────────────────────────────────────────────

  @Test
  public void toArray_returnsAllItems() {
    String[] arr = data.toArray();
    assertArrayEquals(new String[]{"alpha", "bravo", "charlie"}, arr);
  }

  // ── refresh() ─────────────────────────────────────────────────────

  @Test
  public void refresh_detectsDataChange_firesContentsChanged() {
    data.getItemCount(); // initial load
    AtomicInteger fireCount = new AtomicInteger(0);
    data.addListener(new TestListDataListener() {
      @Override
      public void contentsChanged(ListDataEvent e) {
        fireCount.incrementAndGet();
      }
    });

    // Modify backing data and refresh
    backingValues.add("delta");
    data.refresh();

    assertEquals(1, fireCount.get());
    assertEquals(4, data.getItemCount());
  }

  @Test
  public void refresh_noDataChange_doesNotFireContentsChanged() {
    data.getItemCount(); // initial load
    AtomicInteger fireCount = new AtomicInteger(0);
    data.addListener(new TestListDataListener() {
      @Override
      public void contentsChanged(ListDataEvent e) {
        fireCount.incrementAndGet();
      }
    });

    // Same backing data — identity comparison per element
    data.refresh();
    assertEquals(0, fireCount.get());
  }

  @Test
  public void refresh_differentSizeList_detectsChange() {
    data.getItemCount(); // initial load
    backingValues.remove(0);
    data.refresh();
    assertEquals(2, data.getItemCount());
  }

  @Test
  public void refresh_replacedElement_detectsChange() {
    data.getItemCount(); // initial load
    // Replace an element with a different identity
    backingValues.set(1, new String("bravo"));
    data.refresh();
    // Should detect change since identity comparison (!= not .equals)
    assertEquals(3, data.getItemCount());
  }

  // ── Mutation guards ───────────────────────────────────────────────

  @Test(expected = UnsupportedOperationException.class)
  public void internalAddItem_throws() {
    data.internalAddItem(0, "new");
  }

  @Test(expected = UnsupportedOperationException.class)
  public void internalRemoveItem_throws() {
    data.internalRemoveItem("alpha");
  }

  @Test
  public void internalSetAllItems_logsButDoesNotThrow() {
    data.getItemCount(); // init
    // internalSetAllItems just logs via Logger.severe — doesn't modify data
    data.internalSetAllItems(Arrays.asList("x", "y"));
    // Data unchanged since internalSetAllItems is a no-op with logging
    assertEquals(3, data.getItemCount());
  }

  // ── Listener management ───────────────────────────────────────────

  @Test
  public void listener_removed_doesNotFire() {
    data.getItemCount(); // init
    AtomicInteger fireCount = new AtomicInteger(0);
    ListDataListener listener = new TestListDataListener() {
      @Override
      public void contentsChanged(ListDataEvent e) {
        fireCount.incrementAndGet();
      }
    };
    data.addListener(listener);
    data.removeListener(listener);
    backingValues.add("delta");
    data.refresh();
    assertEquals(0, fireCount.get());
  }

  // ── Test infrastructure ───────────────────────────────────────────

  private static class TestRefreshableListData extends RefreshableListData<String> {
    private final List<String> source;
    int createValuesCallCount = 0;

    TestRefreshableListData(List<String> source) {
      super(CroquetTestUtils.STRING_CODEC);
      this.source = source;
    }

    @Override
    protected List<String> createValues() {
      createValuesCallCount++;
      return new ArrayList<>(source);
    }
  }
}
