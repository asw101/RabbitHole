package org.lgna.croquet.data;

import org.lgna.croquet.CroquetTestUtils;
import org.lgna.croquet.ItemCodec;
import org.junit.Test;

import edu.cmu.cs.dennisc.codec.BinaryDecoder;
import edu.cmu.cs.dennisc.codec.BinaryEncoder;

import javax.swing.event.ListDataListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import static org.junit.Assert.*;

/**
 * Tests for {@link ImmutableListData} — construction, element access,
 * and immutable list behavior.
 */
public class ImmutableListDataExtendedTest {

  // ── Construction ──────────────────────────────────────────────────

  @Test
  public void constructor_emptyArray() {
    ImmutableListData<String> data = new ImmutableListData<>(CroquetTestUtils.STRING_CODEC, new String[]{});
    assertEquals(0, data.getItemCount());
  }

  @Test
  public void constructor_withArray() {
    ImmutableListData<String> data = new ImmutableListData<>(CroquetTestUtils.STRING_CODEC, new String[]{"a", "b", "c"});
    assertEquals(3, data.getItemCount());
  }

  // ── getItemAt ─────────────────────────────────────────────────────

  @Test
  public void getItemAt_validIndex() {
    ImmutableListData<String> data = new ImmutableListData<>(CroquetTestUtils.STRING_CODEC, new String[]{"x", "y", "z"});
    assertEquals("x", data.getItemAt(0));
    assertEquals("y", data.getItemAt(1));
    assertEquals("z", data.getItemAt(2));
  }

  // ── contains ──────────────────────────────────────────────────────

  @Test
  public void contains_existingItem() {
    ImmutableListData<String> data = new ImmutableListData<>(CroquetTestUtils.STRING_CODEC, new String[]{"a", "b"});
    assertTrue(data.contains("a"));
    assertTrue(data.contains("b"));
  }

  @Test
  public void contains_missingItem() {
    ImmutableListData<String> data = new ImmutableListData<>(CroquetTestUtils.STRING_CODEC, new String[]{"a"});
    assertFalse(data.contains("z"));
  }

  // ── indexOf ───────────────────────────────────────────────────────

  @Test
  public void indexOf_existingItem() {
    ImmutableListData<String> data = new ImmutableListData<>(CroquetTestUtils.STRING_CODEC, new String[]{"p", "q", "r"});
    assertEquals(0, data.indexOf("p"));
    assertEquals(1, data.indexOf("q"));
    assertEquals(2, data.indexOf("r"));
  }

  @Test
  public void indexOf_missingItem() {
    ImmutableListData<String> data = new ImmutableListData<>(CroquetTestUtils.STRING_CODEC, new String[]{"a"});
    assertEquals(-1, data.indexOf("z"));
  }

  // ── iterator ──────────────────────────────────────────────────────

  @Test
  public void iterator_coversAllItems() {
    ImmutableListData<String> data = new ImmutableListData<>(CroquetTestUtils.STRING_CODEC, new String[]{"a", "b"});
    Iterator<String> it = data.iterator();
    assertTrue(it.hasNext());
    assertEquals("a", it.next());
    assertEquals("b", it.next());
    assertFalse(it.hasNext());
  }

  // ── toArray ───────────────────────────────────────────────────────

  @Test
  public void toArray_returnsAllItems() {
    ImmutableListData<String> data = new ImmutableListData<>(CroquetTestUtils.STRING_CODEC, new String[]{"a", "b", "c"});
    String[] arr = data.toArray();
    assertArrayEquals(new String[]{"a", "b", "c"}, arr);
  }

  // ── getItemCodec ──────────────────────────────────────────────────

  @Test
  public void getItemCodec_matchesConstructor() {
    ImmutableListData<String> data = new ImmutableListData<>(CroquetTestUtils.STRING_CODEC, new String[]{"a"});
    assertSame(CroquetTestUtils.STRING_CODEC, data.getItemCodec());
  }

  // ── getPreferenceKey ──────────────────────────────────────────────

  @Test
  public void getPreferenceKey_containsClassName() {
    ImmutableListData<String> data = new ImmutableListData<>(CroquetTestUtils.STRING_CODEC, new String[]{"a"});
    assertTrue(data.getPreferenceKey().contains("ImmutableListData"));
  }

  // ── internalAddItem on immutable data ───────────────────────────────

  @Test(expected = UnsupportedOperationException.class)
  public void internalAddItem_throws() {
    ImmutableListData<String> data = new ImmutableListData<>(CroquetTestUtils.STRING_CODEC, new String[]{"a"});
    data.internalAddItem("b");
  }

  @Test(expected = UnsupportedOperationException.class)
  public void internalAddItem_atIndex_throws() {
    ImmutableListData<String> data = new ImmutableListData<>(CroquetTestUtils.STRING_CODEC, new String[]{"a"});
    data.internalAddItem(0, "b");
  }

  @Test(expected = UnsupportedOperationException.class)
  public void internalRemoveItem_throws() {
    ImmutableListData<String> data = new ImmutableListData<>(CroquetTestUtils.STRING_CODEC, new String[]{"a"});
    data.internalRemoveItem("a");
  }

  @Test(expected = UnsupportedOperationException.class)
  public void internalSetAllItems_throws() {
    ImmutableListData<String> data = new ImmutableListData<>(CroquetTestUtils.STRING_CODEC, new String[]{"a"});
    data.internalSetAllItems(Arrays.asList("b"));
  }

  // ── RefreshableListData ───────────────────────────────────────────

  @Test
  public void refreshableListData_emptyInitially() {
    RefreshableListData<String> data = new TestRefreshableListData();
    assertEquals(0, data.getItemCount());
  }

  @Test
  public void refreshableListData_afterRefresh() {
    TestRefreshableListData data = new TestRefreshableListData();
    data.setBackingData("a", "b", "c");
    data.refresh();
    assertEquals(3, data.getItemCount());
    assertEquals("a", data.getItemAt(0));
  }

  @Test
  public void refreshableListData_toArray() {
    TestRefreshableListData data = new TestRefreshableListData();
    data.setBackingData("x", "y");
    data.refresh();
    String[] arr = data.toArray();
    assertArrayEquals(new String[]{"x", "y"}, arr);
  }

  @Test
  public void refreshableListData_contains() {
    TestRefreshableListData data = new TestRefreshableListData();
    data.setBackingData("a", "b");
    data.refresh();
    assertTrue(data.contains("a"));
    assertFalse(data.contains("z"));
  }

  @Test
  public void refreshableListData_indexOf() {
    TestRefreshableListData data = new TestRefreshableListData();
    data.setBackingData("p", "q");
    data.refresh();
    assertEquals(0, data.indexOf("p"));
    assertEquals(1, data.indexOf("q"));
    assertEquals(-1, data.indexOf("z"));
  }

  // ── Test infrastructure ───────────────────────────────────────────

  private static class TestRefreshableListData extends RefreshableListData<String> {
    private String[] backing = new String[0];

    TestRefreshableListData() {
      super(CroquetTestUtils.STRING_CODEC);
    }

    void setBackingData(String... items) {
      this.backing = items;
    }

    @Override
    protected java.util.List<String> createValues() {
      return Arrays.asList(backing);
    }
  }
}
