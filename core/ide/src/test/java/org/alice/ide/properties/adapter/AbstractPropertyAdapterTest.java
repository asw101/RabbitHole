package org.alice.ide.properties.adapter;

import org.junit.Test;

import static org.junit.Assert.*;

public class AbstractPropertyAdapterTest {

  private static final class TestAdapter extends AbstractPropertyAdapter<String, Object> {
    private String value = "initial";
    private boolean started;
    private boolean stopped;

    private TestAdapter(String repr, Object instance) {
      super(repr, instance, null);
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
      return new String(this.value);
    }

    @Override
    protected void startPropertyListening() {
      this.started = true;
    }

    @Override
    protected void stopPropertyListening() {
      this.stopped = true;
    }

    private void setCurrentValue(String value) {
      this.value = value;
    }

    private void resetLifecycleFlags() {
      this.started = false;
      this.stopped = false;
    }

    private void fireValueChanged(String value) {
      this.notifyValueObservers(value);
    }
  }

  @Test
  public void constructor_storesRepresentationAndInstance() {
    Object instance = new Object();
    TestAdapter adapter = new TestAdapter("Name", instance);

    assertEquals("Name", adapter.getRepr());
    assertSame(instance, adapter.getInstance());
  }

  @Test
  public void setValue_tracksLastSetValue() {
    TestAdapter adapter = new TestAdapter("Name", new Object());

    adapter.setValue("next");

    assertEquals("next", adapter.getLastSetValue());
  }

  @Test
  public void setInstance_stopsAndRestartsListening() {
    TestAdapter adapter = new TestAdapter("Name", new Object());
    adapter.resetLifecycleFlags();

    adapter.setInstance(new Object());

    assertTrue(adapter.stopped);
    assertTrue(adapter.started);
  }

  @Test
  public void addAndInvokeValueChangeObserver_usesCurrentValue() {
    TestAdapter adapter = new TestAdapter("Name", new Object());
    String[] observed = new String[1];

    adapter.addAndInvokeValueChangeObserver(value -> observed[0] = value);

    assertEquals("initial", observed[0]);
  }

  @Test
  public void removeValueChangeObserver_stopsNotifications() {
    TestAdapter adapter = new TestAdapter("Name", new Object());
    int[] count = new int[1];
    AbstractPropertyAdapter.ValueChangeObserver<String> observer = value -> count[0]++;
    adapter.addValueChangeObserver(observer);
    adapter.removeValueChangeObserver(observer);
    adapter.setCurrentValue("changed");

    adapter.fireValueChanged("changed");

    assertEquals(0, count[0]);
  }
}
