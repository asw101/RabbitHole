package org.lgna.croquet.data;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.BinaryEncoder;
import org.lgna.croquet.ItemCodec;
import org.junit.Before;
import org.junit.Test;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Tests for {@link MutableListData} — mutable, thread-safe list backed by
 * CopyOnWriteArrayList. Covers construction, element access, mutation
 * operations, listener dispatch, and the {@link ListData} base-class methods.
 */
public class MutableListDataTest {

  private MutableListData<String> data;

  @Before
  public void setUp() {
    data = new MutableListData<>(STRING_CODEC, new String[]{"alpha", "bravo", "charlie"});
  }

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void emptyConstructor_createsEmptyList() {
    MutableListData<String> empty = new MutableListData<>(STRING_CODEC);
    assertEquals(0, empty.getItemCount());
  }

  @Test
  public void arrayConstructor_populatesList() {
    assertEquals(3, data.getItemCount());
  }

  @Test
  public void collectionConstructor_populatesList() {
    MutableListData<String> fromColl =
        new MutableListData<>(STRING_CODEC, Arrays.asList("x", "y"));
    assertEquals(2, fromColl.getItemCount());
    assertEquals("x", fromColl.getItemAt(0));
  }

  // ── getItemAt ─────────────────────────────────────────────────────

  @Test
  public void getItemAt_validIndex_returnsItem() {
    assertEquals("alpha", data.getItemAt(0));
    assertEquals("bravo", data.getItemAt(1));
    assertEquals("charlie", data.getItemAt(2));
  }

  @Test
  public void getItemAt_negativeIndex_returnsNull() {
    assertNull(data.getItemAt(-1));
  }

  @Test
  public void getItemAt_outOfBounds_returnsNull() {
    assertNull(data.getItemAt(99));
  }

  // ── getItemCount ──────────────────────────────────────────────────

  @Test
  public void getItemCount_matchesSize() {
    assertEquals(3, data.getItemCount());
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
    assertEquals(1, data.indexOf("bravo"));
  }

  @Test
  public void indexOf_missingItem_returnsNegativeOne() {
    assertEquals(-1, data.indexOf("missing"));
  }

  // ── iterator ──────────────────────────────────────────────────────

  @Test
  public void iterator_coversAllItems() {
    Iterator<String> it = data.iterator();
    assertTrue(it.hasNext());
    assertEquals("alpha", it.next());
    assertEquals("bravo", it.next());
    assertEquals("charlie", it.next());
    assertFalse(it.hasNext());
  }

  // ── internalAddItem (at end) ──────────────────────────────────────

  @Test
  public void internalAddItem_appends() {
    data.internalAddItem("delta");
    assertEquals(4, data.getItemCount());
    assertEquals("delta", data.getItemAt(3));
  }

  // ── internalAddItem (at index) ────────────────────────────────────

  @Test
  public void internalAddItem_atIndex_inserts() {
    data.internalAddItem(0, "zero");
    assertEquals(4, data.getItemCount());
    assertEquals("zero", data.getItemAt(0));
    assertEquals("alpha", data.getItemAt(1));
  }

  // ── internalRemoveItem ────────────────────────────────────────────

  @Test
  public void internalRemoveItem_removesItem() {
    data.internalRemoveItem("bravo");
    assertEquals(2, data.getItemCount());
    assertFalse(data.contains("bravo"));
  }

  @Test
  public void internalRemoveItem_missingItem_noEffect() {
    data.internalRemoveItem("missing");
    assertEquals(3, data.getItemCount());
  }

  // ── internalSetAllItems ───────────────────────────────────────────

  @Test
  public void internalSetAllItems_replacesContents() {
    data.internalSetAllItems(Arrays.asList("x", "y"));
    assertEquals(2, data.getItemCount());
    assertEquals("x", data.getItemAt(0));
    assertEquals("y", data.getItemAt(1));
  }

  @Test
  public void internalSetAllItems_arrayOverload_replacesContents() {
    data.internalSetAllItems(new String[]{"p", "q", "r", "s"});
    assertEquals(4, data.getItemCount());
    assertEquals("p", data.getItemAt(0));
  }

  // ── toArray ───────────────────────────────────────────────────────

  @Test
  public void toArray_returnsAllItems() {
    String[] arr = data.toArray();
    assertArrayEquals(new String[]{"alpha", "bravo", "charlie"}, arr);
  }

  // ── getItemCodec ──────────────────────────────────────────────────

  @Test
  public void getItemCodec_returnsSameCodec() {
    assertSame(STRING_CODEC, data.getItemCodec());
  }

  // ── getPreferenceKey ──────────────────────────────────────────────

  @Test
  public void getPreferenceKey_returnsClassName() {
    assertNotNull(data.getPreferenceKey());
    assertTrue(data.getPreferenceKey().contains("MutableListData"));
  }

  // ── Listener add/remove/fire ──────────────────────────────────────

  @Test
  public void listener_firesContentsChangedOnAdd() {
    AtomicInteger fireCount = new AtomicInteger(0);
    data.addListener(new TestListDataListener() {
      @Override
      public void contentsChanged(ListDataEvent e) {
        fireCount.incrementAndGet();
      }
    });
    data.internalAddItem(0, "zero");
    assertEquals(1, fireCount.get());
  }

  @Test
  public void listener_firesContentsChangedOnRemove() {
    AtomicInteger fireCount = new AtomicInteger(0);
    data.addListener(new TestListDataListener() {
      @Override
      public void contentsChanged(ListDataEvent e) {
        fireCount.incrementAndGet();
      }
    });
    data.internalRemoveItem("alpha");
    assertEquals(1, fireCount.get());
  }

  @Test
  public void listener_firesContentsChangedOnSetAll() {
    AtomicInteger fireCount = new AtomicInteger(0);
    data.addListener(new TestListDataListener() {
      @Override
      public void contentsChanged(ListDataEvent e) {
        fireCount.incrementAndGet();
      }
    });
    data.internalSetAllItems(Arrays.asList("x"));
    assertEquals(1, fireCount.get());
  }

  @Test
  public void listener_removed_doesNotFire() {
    AtomicInteger fireCount = new AtomicInteger(0);
    ListDataListener listener = new TestListDataListener() {
      @Override
      public void contentsChanged(ListDataEvent e) {
        fireCount.incrementAndGet();
      }
    };
    data.addListener(listener);
    data.removeListener(listener);
    data.internalAddItem("delta");
    assertEquals(0, fireCount.get());
  }

  // ── Empty list edge cases ─────────────────────────────────────────

  @Test
  public void emptyList_getItemAt_outOfBounds_returnsNull() {
    MutableListData<String> empty = new MutableListData<>(STRING_CODEC);
    assertNull(empty.getItemAt(0));
  }

  @Test
  public void emptyList_iterator_hasNoElements() {
    MutableListData<String> empty = new MutableListData<>(STRING_CODEC);
    assertFalse(empty.iterator().hasNext());
  }

  // ── Test infrastructure ───────────────────────────────────────────

  static final ItemCodec<String> STRING_CODEC = new ItemCodec<String>() {
    @Override
    public Class<String> getValueClass() {
      return String.class;
    }

    @Override
    public String decodeValue(BinaryDecoder binaryDecoder) {
      return binaryDecoder.decodeString();
    }

    @Override
    public void encodeValue(BinaryEncoder binaryEncoder, String value) {
      binaryEncoder.encode(value);
    }

    @Override
    public void appendRepresentation(StringBuilder sb, String value) {
      sb.append(value);
    }
  };

  private static class TestListDataListener implements ListDataListener {
    @Override
    public void intervalAdded(ListDataEvent e) {
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
    }
  }
}
