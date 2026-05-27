package edu.cmu.cs.dennisc.javax.swing.models;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AbstractListModelTest {
  private static final class SampleListModel extends AbstractListModel<String> {
    private final List<String> values;

    private SampleListModel(List<String> values) {
      this.values = values;
    }

    @Override
    public int getSize() {
      return values.size();
    }

    @Override
    public Object getElementAt(int index) {
      return values.get(index);
    }
  }

  @Test
  void findsItemsWithDuplicatesAndNulls() {
    SampleListModel model = new SampleListModel(Arrays.asList("alpha", null, "beta", null, "alpha"));

    assertEquals("alpha", model.get(0));
    assertEquals(0, model.indexOf("alpha"));
    assertEquals(4, model.lastIndexOf("alpha"));
    assertEquals(1, model.indexOf(null));
    assertEquals(3, model.lastIndexOf(null));
    assertTrue(model.contains("beta"));
    assertFalse(model.contains("gamma"));
    assertFalse(model.isEmpty());
  }

  @Test
  void reportsMissingItemsAndEmptyState() {
    SampleListModel model = new SampleListModel(List.of());

    assertTrue(model.isEmpty());
    assertEquals(-1, model.indexOf("missing"));
    assertEquals(-1, model.lastIndexOf("missing"));
    assertFalse(model.contains("missing"));
  }
}
