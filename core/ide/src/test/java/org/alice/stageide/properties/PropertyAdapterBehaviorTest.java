package org.alice.stageide.properties;

import org.junit.Test;
import org.lgna.story.Color;
import org.lgna.story.Paint;
import org.lgna.story.STextModel;
import org.lgna.story.implementation.Property;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class PropertyAdapterBehaviorTest {
  private static final class MutableProperty<T> extends Property<T> {
    private T value;

    private MutableProperty(Class<T> valueClass, T initialValue) {
      super(null, valueClass);
      this.value = initialValue;
    }

    @Override
    public T getValue() {
      return this.value;
    }

    @Override
    protected void handleSetValue(T value) {
      this.value = value;
    }

    @Override
    protected T interpolate(T a, T b, double portion) {
      return portion < 1.0 ? a : b;
    }
  }

  @Test
  public void paintPropertyAdapterExposesBackingPropertyTypeAndValue() {
    MutableProperty<Paint> property = new MutableProperty<>(Paint.class, Color.RED);
    PaintPropertyAdapter<Object> adapter = new PaintPropertyAdapter<>(new Object(), property, null);

    assertSame(Paint.class, adapter.getPropertyType());
    assertSame(Color.RED, adapter.getValue());
    assertSame(Color.RED, adapter.getValueCopyIfMutable());
  }

  @Test
  public void paintPropertyAdapterInvokesObserversForInitialAndUpdatedValues() throws Exception {
    MutableProperty<Paint> property = new MutableProperty<>(Paint.class, Color.RED);
    PaintPropertyAdapter<Object> adapter = new PaintPropertyAdapter<>(new Object(), property, null);
    AtomicReference<Paint> observed = new AtomicReference<>();
    CountDownLatch latch = new CountDownLatch(2);

    adapter.addAndInvokeValueChangeObserver(value -> {
      observed.set(value);
      latch.countDown();
    });
    property.setValue(Color.BLUE);

    assertTrue(latch.await(1, TimeUnit.SECONDS));
    assertSame(Color.BLUE, observed.get());
    assertSame(Color.BLUE, adapter.getValue());
  }

  @Test
  public void paintPropertyAdapterSetValueUpdatesBackingPropertyAsynchronously() throws Exception {
    MutableProperty<Paint> property = new MutableProperty<>(Paint.class, Color.RED);
    PaintPropertyAdapter<Object> adapter = new PaintPropertyAdapter<>(new Object(), property, null);
    AtomicReference<Paint> observed = new AtomicReference<>();
    CountDownLatch latch = new CountDownLatch(1);

    adapter.addValueChangeObserver(value -> {
      observed.set(value);
      if (value == Color.GREEN) {
        latch.countDown();
      }
    });
    adapter.setValue(Color.GREEN);

    assertSame(Color.GREEN, adapter.getLastSetValue());
    assertTrue(latch.await(2, TimeUnit.SECONDS));
    assertSame(Color.GREEN, property.getValue());
    assertSame(Color.GREEN, observed.get());
  }

  @Test
  public void textValuePropertyAdapterUpdatesBackingModelAndReturnsDetachedCopy() {
    STextModel model = new STextModel();
    TextValuePropertyAdapter adapter = new TextValuePropertyAdapter(model.getImplementation(), null);

    adapter.setValue("Hello Alice");

    assertEquals("Hello Alice", model.getImplementation().getValue());
    assertEquals("Hello Alice", adapter.getValue());
    assertEquals("Hello Alice", adapter.getValueCopyIfMutable());
    assertNotSame(adapter.getValue(), adapter.getValueCopyIfMutable());
  }
}
