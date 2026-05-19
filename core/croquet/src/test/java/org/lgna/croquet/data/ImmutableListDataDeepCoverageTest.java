package org.lgna.croquet.data;

import org.junit.Before;
import org.junit.Test;
import org.lgna.croquet.CroquetTestUtils;

import java.util.Arrays;
import java.util.Iterator;

import static org.junit.Assert.*;

/**
 * Deep coverage tests for {@link ImmutableListData} — immutable list data
 * backed by a fixed array. Tests construction, accessors, iteration,
 * unsupported mutation operations, and edge cases.
 */
public class ImmutableListDataDeepCoverageTest {

  private ImmutableListData<String> data;

  @Before
  public void setUp() {
    data = new ImmutableListData<>(CroquetTestUtils.STRING_CODEC,
        new String[]{"alpha", "beta", "gamma"});
  }

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void construction_nonNull() {
    assertNotNull(data);
  }

  @Test
  public void emptyArray_construction() {
    ImmutableListData<String> empty = new ImmutableListData<>(
        CroquetTestUtils.STRING_CODEC, new String[0]);
    assertEquals(0, empty.getItemCount());
  }

  @Test
  public void singleElement_construction() {
    ImmutableListData<String> single = new ImmutableListData<>(
        CroquetTestUtils.STRING_CODEC, new String[]{"only"});
    assertEquals(1, single.getItemCount());
    assertEquals("only", single.getItemAt(0));
  }

  // ── getItemCount ──────────────────────────────────────────────────

  @Test
  public void getItemCount_three() {
    assertEquals(3, data.getItemCount());
  }

  // ── getItemAt ─────────────────────────────────────────────────────

  @Test
  public void getItemAt_first() {
    assertEquals("alpha", data.getItemAt(0));
  }

  @Test
  public void getItemAt_middle() {
    assertEquals("beta", data.getItemAt(1));
  }

  @Test
  public void getItemAt_last() {
    assertEquals("gamma", data.getItemAt(2));
  }

  // ── contains ──────────────────────────────────────────────────────

  @Test
  public void contains_existing_true() {
    assertTrue(data.contains("alpha"));
  }

  @Test
  public void contains_missing_false() {
    assertFalse(data.contains("delta"));
  }

  @Test
  public void contains_null_false() {
    assertFalse(data.contains(null));
  }

  // ── indexOf ───────────────────────────────────────────────────────

  @Test
  public void indexOf_found() {
    assertEquals(1, data.indexOf("beta"));
  }

  @Test
  public void indexOf_notFound() {
    assertEquals(-1, data.indexOf("missing"));
  }

  @Test
  public void indexOf_first() {
    assertEquals(0, data.indexOf("alpha"));
  }

  @Test
  public void indexOf_last() {
    assertEquals(2, data.indexOf("gamma"));
  }

  // ── iterator ──────────────────────────────────────────────────────

  @Test
  public void iterator_hasThreeElements() {
    int count = 0;
    for (String s : data) {
      assertNotNull(s);
      count++;
    }
    assertEquals(3, count);
  }

  @Test
  public void iterator_order() {
    Iterator<String> it = data.iterator();
    assertEquals("alpha", it.next());
    assertEquals("beta", it.next());
    assertEquals("gamma", it.next());
    assertFalse(it.hasNext());
  }

  @Test
  public void empty_iterator_noElements() {
    ImmutableListData<String> empty = new ImmutableListData<>(
        CroquetTestUtils.STRING_CODEC, new String[0]);
    assertFalse(empty.iterator().hasNext());
  }

  // ── toArray ───────────────────────────────────────────────────────

  @Test
  public void toArray_returnsCorrectArray() {
    String[] arr = data.toArray();
    assertEquals(3, arr.length);
    assertEquals("alpha", arr[0]);
    assertEquals("gamma", arr[2]);
  }

  // ── addListener / removeListener (no-ops) ─────────────────────────

  @Test
  public void addListener_noException() {
    data.addListener(null);
  }

  @Test
  public void removeListener_noException() {
    data.removeListener(null);
  }

  // ── Unsupported mutation operations ───────────────────────────────

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
    data.internalSetAllItems(Arrays.asList("x"));
  }

  // ── getItemCodec ──────────────────────────────────────────────────

  @Test
  public void getItemCodec_nonNull() {
    assertNotNull(data.getItemCodec());
  }

  @Test
  public void getItemCodec_matchesProvided() {
    assertSame(CroquetTestUtils.STRING_CODEC, data.getItemCodec());
  }

  // ── Hierarchy ─────────────────────────────────────────────────────

  @Test
  public void class_isFinal() {
    assertTrue(java.lang.reflect.Modifier.isFinal(
        ImmutableListData.class.getModifiers()));
  }

  @Test
  public void class_extendsListData() {
    assertTrue(ListData.class.isAssignableFrom(ImmutableListData.class));
  }

  // ── getPreferenceKey ──────────────────────────────────────────────

  @Test
  public void getPreferenceKey_nonNull() {
    assertNotNull(data.getPreferenceKey());
  }
}
