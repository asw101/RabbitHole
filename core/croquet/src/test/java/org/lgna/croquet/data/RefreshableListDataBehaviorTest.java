package org.lgna.croquet.data;

import org.junit.Test;
import org.lgna.croquet.CroquetTestUtils;

import javax.swing.event.ListDataEvent;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class RefreshableListDataBehaviorTest {
  @Test
  public void getItemCount_cachesValuesUntilRefresh() {
    TestRefreshableListData data = new TestRefreshableListData(
        Arrays.asList(Arrays.asList("a", "b"), Arrays.asList("c")));

    assertEquals(2, data.getItemCount());
    assertEquals(2, data.getItemCount());
    assertEquals(1, data.createValuesCount);
  }

  @Test
  public void refresh_withChangedValues_notifiesListenersAndUpdatesContents() {
    TestRefreshableListData data = new TestRefreshableListData(
        Arrays.asList(Arrays.asList("a", "b"), Arrays.asList("c")));
    data.getItemCount();
    AtomicInteger fireCount = new AtomicInteger();
    data.addListener(new TestListDataListener() {
      @Override
      public void contentsChanged(ListDataEvent e) {
        fireCount.incrementAndGet();
      }
    });

    data.refresh();

    assertEquals(1, fireCount.get());
    assertArrayEquals(new String[]{"c"}, data.toArray());
  }

  @Test
  public void refresh_withSameValues_doesNotNotify_andInternalSetAllItemsDoesNothing() {
    java.util.List<String> shared = Arrays.asList("same", "values");
    TestRefreshableListData data = new TestRefreshableListData(Arrays.asList(shared, shared));
    data.getItemCount();
    AtomicInteger fireCount = new AtomicInteger();
    data.addListener(new TestListDataListener() {
      @Override
      public void contentsChanged(ListDataEvent e) {
        fireCount.incrementAndGet();
      }
    });

    data.refresh();
    data.internalSetAllItems(Arrays.asList("ignored"));

    assertEquals(0, fireCount.get());
    assertArrayEquals(new String[]{"same", "values"}, data.toArray());
  }

  @Test
  public void internalAddAndRemoveItem_throwUnsupportedOperationException() {
    TestRefreshableListData data = new TestRefreshableListData(Arrays.asList(Arrays.asList("a")));

    assertThrows(UnsupportedOperationException.class, () -> data.internalAddItem(0, "b"));
    assertThrows(UnsupportedOperationException.class, () -> data.internalRemoveItem("a"));
  }

  private static final class TestRefreshableListData extends RefreshableListData<String> {
    private final java.util.List<java.util.List<String>> snapshots;
    private int index;
    private int createValuesCount;

    private TestRefreshableListData(java.util.List<java.util.List<String>> snapshots) {
      super(CroquetTestUtils.STRING_CODEC);
      this.snapshots = snapshots;
    }

    @Override
    protected List<String> createValues() {
      this.createValuesCount++;
      java.util.List<String> result = this.snapshots.get(Math.min(this.index, this.snapshots.size() - 1));
      this.index++;
      return result;
    }
  }
}
