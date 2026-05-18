package org.alice.ide.properties.adapter;

import org.junit.Test;
import org.lgna.story.implementation.Property;

import static org.junit.Assert.*;

public class DoublePropertyAdapterTest {

  private static final class TestProperty extends Property<Double> {
    private Double value;

    private TestProperty(Double value) {
      super(null, Double.class);
      this.value = value;
    }

    @Override
    public Double getValue() {
      return this.value;
    }

    @Override
    protected void handleSetValue(Double value) {
      this.value = value;
    }

    @Override
    protected Double interpolate(Double a, Double b, double portion) {
      return portion < 0.5 ? a : b;
    }
  }

  @Test
  public void constructor_preservesRepresentation() {
    DoublePropertyAdapter<Object> adapter = new DoublePropertyAdapter<>("Double", new Object(), new TestProperty(1.5), null);

    assertEquals("Double", adapter.getRepr());
  }

  @Test
  public void getValue_readsUnderlyingProperty() {
    TestProperty property = new TestProperty(1.5);
    DoublePropertyAdapter<Object> adapter = new DoublePropertyAdapter<>("Double", new Object(), property, null);

    assertEquals(Double.valueOf(1.5), adapter.getValue());
  }

  @Test
  public void getPropertyType_returnsDoubleClass() {
    DoublePropertyAdapter<Object> adapter = new DoublePropertyAdapter<>("Double", new Object(), new TestProperty(1.5), null);

    assertEquals(Double.class, adapter.getPropertyType());
  }

  @Test
  public void getValueCopyIfMutable_returnsCurrentValue() {
    TestProperty property = new TestProperty(2.25);
    DoublePropertyAdapter<Object> adapter = new DoublePropertyAdapter<>("Double", new Object(), property, null);

    assertEquals(property.getValue(), adapter.getValueCopyIfMutable());
    assertSame(property.getValue(), adapter.getValueCopyIfMutable());
  }

  @Test
  public void setValue_updatesUnderlyingProperty() {
    TestProperty property = new TestProperty(1.5);
    DoublePropertyAdapter<Object> adapter = new DoublePropertyAdapter<>("Double", new Object(), property, null);

    adapter.setValue(3.75);

    waitForValue(property, 3.75);
    assertEquals(Double.valueOf(3.75), property.getValue());
  }

  @Test
  public void propertyListener_notifiesObserversWhenPropertyChanges() {
    TestProperty property = new TestProperty(1.5);
    DoublePropertyAdapter<Object> adapter = new DoublePropertyAdapter<>("Double", new Object(), property, null);
    Double[] observed = new Double[1];
    adapter.addValueChangeObserver(value -> observed[0] = value);

    property.setValue(4.5);

    assertEquals(Double.valueOf(4.5), observed[0]);
  }

  private static void waitForValue(TestProperty property, double expected) {
    long deadline = System.currentTimeMillis() + 2000;
    while (System.currentTimeMillis() < deadline) {
      if (Double.valueOf(expected).equals(property.getValue())) {
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
