package org.alice.ide.properties.adapter;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class AbstractPropertyAdapterTest {
  private static final class TestAdapter extends AbstractPropertyAdapter<String, Object> {
    private String value;

    private TestAdapter(String repr, String initialValue) {
      super(repr, new Object(), null);
      this.value = initialValue;
    }

    @Override
    public String getValue() {
      return this.value;
    }

    @Override
    public Class<String> getPropertyType() {
      return String.class;
    }

    @Override
    public String getValueCopyIfMutable() {
      return this.value == null ? null : new String(this.value);
    }

    void updateValueAndNotify(String nextValue) {
      this.value = nextValue;
      this.notifyValueObservers(nextValue);
    }
  }

  @Test
  public void reprAndUndoRedoDescriptionShareConfiguredText() {
    TestAdapter adapter = new TestAdapter("Display Name", "alpha");

    assertEquals("Display Name", adapter.getRepr());
    assertEquals("Display Name", adapter.getUndoRedoDescription());
  }

  @Test
  public void setValueTracksLastSetValue() {
    TestAdapter adapter = new TestAdapter("Display Name", "alpha");
    adapter.setValue("beta");

    assertEquals("beta", adapter.getLastSetValue());
  }

  @Test
  public void addAndInvokeObserverImmediatelyReceivesCurrentValue() {
    TestAdapter adapter = new TestAdapter("Display Name", "alpha");
    AtomicReference<String> observed = new AtomicReference<>();

    adapter.addAndInvokeValueChangeObserver(observed::set);

    assertEquals("alpha", observed.get());
  }

  @Test
  public void removeObserverStopsFutureNotifications() {
    TestAdapter adapter = new TestAdapter("Display Name", "alpha");
    AtomicReference<String> observed = new AtomicReference<>();
    AbstractPropertyAdapter.ValueChangeObserver<String> observer = observed::set;

    adapter.addValueChangeObserver(observer);
    adapter.removeValueChangeObserver(observer);
    adapter.updateValueAndNotify("beta");

    assertNull(observed.get());
  }
}
