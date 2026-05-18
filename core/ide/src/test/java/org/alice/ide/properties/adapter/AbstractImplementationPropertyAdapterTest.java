package org.alice.ide.properties.adapter;

import org.junit.Test;
import org.lgna.story.implementation.Property;

import static org.junit.Assert.*;

public class AbstractImplementationPropertyAdapterTest {

  private static final class TestProperty extends Property<String> {
    private String value;

    private TestProperty(String value) {
      super(null, String.class);
      this.value = value;
    }

    @Override
    public String getValue() {
      return this.value;
    }

    @Override
    protected void handleSetValue(String value) {
      this.value = value;
    }

    @Override
    protected String interpolate(String a, String b, double portion) {
      return portion < 0.5 ? a : b;
    }
  }

  private static final class TestAdapter extends AbstractImplementationPropertyAdapter<String, Object> {
    private TestAdapter(TestProperty property) {
      super("Value", new Object(), property, null);
    }

    @Override
    public String getValueCopyIfMutable() {
      return new String(this.getValue());
    }
  }

  @Test
  public void getValue_readsUnderlyingProperty() {
    TestProperty property = new TestProperty("initial");
    TestAdapter adapter = new TestAdapter(property);

    assertEquals("initial", adapter.getValue());
  }

  @Test
  public void getPropertyType_comesFromProperty() {
    TestAdapter adapter = new TestAdapter(new TestProperty("initial"));

    assertEquals(String.class, adapter.getPropertyType());
  }

  @Test
  public void setValue_updatesPropertyAsynchronously() {
    TestProperty property = new TestProperty("initial");
    TestAdapter adapter = new TestAdapter(property);

    adapter.setValue("updated");

    waitForValue(property, "updated");
    assertEquals("updated", property.getValue());
  }

  @Test
  public void propertyListener_notifiesObserversWhenPropertyChanges() {
    TestProperty property = new TestProperty("initial");
    TestAdapter adapter = new TestAdapter(property);
    String[] observed = new String[1];
    adapter.addValueChangeObserver(value -> observed[0] = value);

    property.setValue("changed");

    assertEquals("changed", observed[0]);
  }

  @Test
  public void getValueCopyIfMutable_returnsEqualDistinctString() {
    TestAdapter adapter = new TestAdapter(new TestProperty("initial"));

    assertEquals(adapter.getValue(), adapter.getValueCopyIfMutable());
    assertNotSame(adapter.getValue(), adapter.getValueCopyIfMutable());
  }

  private static void waitForValue(TestProperty property, String expected) {
    long deadline = System.currentTimeMillis() + 2000;
    while (System.currentTimeMillis() < deadline) {
      if (expected.equals(property.getValue())) {
        return;
      }
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        throw new AssertionError(e);
      }
    }
    fail("Timed out waiting for property value " + expected);
  }
}
