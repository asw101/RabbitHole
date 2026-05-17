package org.lgna.croquet.data;

import org.lgna.croquet.CroquetTestUtils;
import org.junit.Before;
import org.junit.Test;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.Arrays;
import java.util.Iterator;

import static org.junit.Assert.*;

/**
 * Tests for {@link ImmutableListData} — fixed-size list that throws
 * {@link UnsupportedOperationException} on all mutation attempts.
 * Covers element access, contains, indexOf, iterator, and mutation guards.
 */
public class ImmutableListDataTest {

  private ImmutableListData<String> data;

  @Before
  public void setUp() {
    data = new ImmutableListData<>(CroquetTestUtils.STRING_CODEC,
        new String[]{"alpha", "bravo", "charlie"});
  }

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void constructor_setsItemCount() {
    assertEquals(3, data.getItemCount());
  }

  // ── getItemAt ─────────────────────────────────────────────────────

  @Test
  public void getItemAt_returnsCorrectItems() {
    assertEquals("alpha", data.getItemAt(0));
    assertEquals("bravo", data.getItemAt(1));
    assertEquals("charlie", data.getItemAt(2));
  }

  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void getItemAt_outOfBounds_throws() {
    data.getItemAt(99);
  }

  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void getItemAt_negative_throws() {
    data.getItemAt(-1);
  }

  // ── contains ──────────────────────────────────────────────────────

  @Test
  public void contains_existingItem_returnsTrue() {
    assertTrue(data.contains("bravo"));
  }

  @Test
  public void contains_missingItem_returnsFalse() {
    assertFalse(data.contains("delta"));
  }

  // ── indexOf ───────────────────────────────────────────────────────

  @Test
  public void indexOf_existingItem_returnsIndex() {
    assertEquals(0, data.indexOf("alpha"));
    assertEquals(2, data.indexOf("charlie"));
  }

  @Test
  public void indexOf_missingItem_returnsNegativeOne() {
    assertEquals(-1, data.indexOf("missing"));
  }

  // ── iterator ──────────────────────────────────────────────────────

  @Test
  public void iterator_coversAllItems() {
    Iterator<String> it = data.iterator();
    int count = 0;
    while (it.hasNext()) {
      it.next();
      count++;
    }
    assertEquals(3, count);
  }

  // ── toArray ───────────────────────────────────────────────────────

  @Test
  public void toArray_returnsAllItems() {
    String[] arr = data.toArray();
    assertArrayEquals(new String[]{"alpha", "bravo", "charlie"}, arr);
  }

  // ── Mutation throws UnsupportedOperationException ─────────────────

  @Test(expected = UnsupportedOperationException.class)
  public void internalAddItem_throws() {
    data.internalAddItem(0, "new");
  }

  @Test(expected = UnsupportedOperationException.class)
  public void internalAddItem_append_throws() {
    data.internalAddItem("new");
  }

  @Test(expected = UnsupportedOperationException.class)
  public void internalRemoveItem_throws() {
    data.internalRemoveItem("alpha");
  }

  @Test(expected = UnsupportedOperationException.class)
  public void internalSetAllItems_throws() {
    data.internalSetAllItems(Arrays.asList("x", "y"));
  }

  // ── Listener no-ops ───────────────────────────────────────────────

  @Test
  public void addListener_doesNotThrow() {
    data.addListener(new ListDataListener() {
      @Override public void intervalAdded(ListDataEvent e) {}
      @Override public void intervalRemoved(ListDataEvent e) {}
      @Override public void contentsChanged(ListDataEvent e) {}
    });
    // No exception — addListener is a no-op for immutable data
  }

  @Test
  public void removeListener_doesNotThrow() {
    data.removeListener(new ListDataListener() {
      @Override public void intervalAdded(ListDataEvent e) {}
      @Override public void intervalRemoved(ListDataEvent e) {}
      @Override public void contentsChanged(ListDataEvent e) {}
    });
    // No exception — removeListener is a no-op for immutable data
  }

  // ── getItemCodec ──────────────────────────────────────────────────

  @Test
  public void getItemCodec_returnsSameCodec() {
    assertSame(CroquetTestUtils.STRING_CODEC, data.getItemCodec());
  }

  // ── Empty immutable list ──────────────────────────────────────────

  @Test
  public void emptyList_hasZeroCount() {
    ImmutableListData<String> empty =
        new ImmutableListData<>(CroquetTestUtils.STRING_CODEC, new String[]{});
    assertEquals(0, empty.getItemCount());
  }

  @Test
  public void emptyList_iterator_hasNoElements() {
    ImmutableListData<String> empty =
        new ImmutableListData<>(CroquetTestUtils.STRING_CODEC, new String[]{});
    assertFalse(empty.iterator().hasNext());
  }
}
