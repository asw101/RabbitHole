package edu.cmu.cs.dennisc.property;

import edu.cmu.cs.dennisc.property.event.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class InstancePropertyDeepTest {

  private StubPropertyOwner owner;

  @Before
  public void setUp() {
    owner = new StubPropertyOwner();
  }

  @Test
  public void getValue_returnsInitialValue() {
    InstanceProperty<String> prop = new InstanceProperty<>(owner, "hello");
    assertEquals("hello", prop.getValue());
  }

  @Test
  public void getValue_nullInitial() {
    InstanceProperty<Object> prop = new InstanceProperty<>(owner, null);
    assertNull(prop.getValue());
  }

  @Test
  public void setValue_updatesValue() {
    InstanceProperty<Integer> prop = new InstanceProperty<>(owner, 1);
    prop.setValue(42);
    assertEquals(42, (int) prop.getValue());
  }

  @Test
  public void setValue_toNull() {
    InstanceProperty<String> prop = new InstanceProperty<>(owner, "something");
    prop.setValue(null);
    assertNull(prop.getValue());
  }

  @Test
  public void getOwner_returnsOwner() {
    InstanceProperty<String> prop = new InstanceProperty<>(owner, "test");
    assertSame(owner, prop.getOwner());
  }

  @Test
  public void getName_delegatesToOwner() {
    InstanceProperty<String> prop = new InstanceProperty<>(owner, "val");
    assertEquals("stub", prop.getName());
  }

  @Test
  public void toString_containsClassName() {
    InstanceProperty<String> prop = new InstanceProperty<>(owner, "val");
    String str = prop.toString();
    assertTrue(str.contains("InstanceProperty"));
    assertTrue(str.contains("stub"));
  }

  @Test
  public void addPropertyListener_listenerReceivesEvents() {
    InstanceProperty<String> prop = new InstanceProperty<>(owner, "old");
    AtomicInteger callCount = new AtomicInteger(0);
    PropertyListener listener = e -> callCount.incrementAndGet();
    prop.addPropertyListener(listener);
    prop.setValue("new");
    assertEquals(1, callCount.get());
  }

  @Test
  public void removePropertyListener_noMoreEvents() {
    InstanceProperty<String> prop = new InstanceProperty<>(owner, "old");
    AtomicInteger callCount = new AtomicInteger(0);
    PropertyListener listener = e -> callCount.incrementAndGet();
    prop.addPropertyListener(listener);
    prop.setValue("new1");
    assertEquals(1, callCount.get());
    prop.removePropertyListener(listener);
    prop.setValue("new2");
    assertEquals(1, callCount.get());
  }

  @Test
  public void getPropertyListeners_returnsUnmodifiable() {
    InstanceProperty<String> prop = new InstanceProperty<>(owner, "val");
    Collection<PropertyListener> listeners = prop.getPropertyListeners();
    assertTrue(listeners.isEmpty());
    try {
      listeners.add(e -> {});
      fail("Should not be able to add to unmodifiable collection");
    } catch (UnsupportedOperationException expected) {
      // expected
    }
  }

  @Test
  public void multipleListeners_allReceiveEvents() {
    InstanceProperty<Integer> prop = new InstanceProperty<>(owner, 0);
    List<Integer> log1 = new ArrayList<>();
    List<Integer> log2 = new ArrayList<>();
    prop.addPropertyListener(e -> log1.add((Integer) e.getValue()));
    prop.addPropertyListener(e -> log2.add((Integer) e.getValue()));
    prop.setValue(10);
    prop.setValue(20);
    assertEquals(2, log1.size());
    assertEquals(2, log2.size());
    assertEquals(10, (int) log1.get(0));
    assertEquals(20, (int) log1.get(1));
  }

  @Test
  public void setValue_eventContainsNewValue() {
    InstanceProperty<String> prop = new InstanceProperty<>(owner, "before");
    List<Object> captured = new ArrayList<>();
    prop.addPropertyListener(e -> captured.add(e.getValue()));
    prop.setValue("after");
    assertEquals(1, captured.size());
    assertEquals("after", captured.get(0));
  }

  @Test
  public void setValue_eventSourceIsProperty() {
    InstanceProperty<String> prop = new InstanceProperty<>(owner, "x");
    final InstanceProperty<?>[] capturedSource = {null};
    prop.addPropertyListener(e -> capturedSource[0] = e.getTypedSource());
    prop.setValue("y");
    assertSame(prop, capturedSource[0]);
  }

  // Stub owner
  private static class StubPropertyOwner implements InstancePropertyOwner {
    @Override public Iterable<InstanceProperty<?>> getProperties() { return null; }
    @Override public InstanceProperty<?> getPropertyNamed(String name) { return null; }
    @Override public String lookupNameFor(InstanceProperty<?> ip) { return "stub"; }
    @Override public void firePropertyChanging(PropertyEvent e) {}
    @Override public void firePropertyChanged(PropertyEvent e) {}
    @Override public void fireAdding(AddListPropertyEvent<?> e) {}
    @Override public void fireAdded(AddListPropertyEvent<?> e) {}
    @Override public void fireClearing(ClearListPropertyEvent<?> e) {}
    @Override public void fireCleared(ClearListPropertyEvent<?> e) {}
    @Override public void fireRemoving(RemoveListPropertyEvent<?> e) {}
    @Override public void fireRemoved(RemoveListPropertyEvent<?> e) {}
    @Override public void fireSetting(SetListPropertyEvent<?> e) {}
    @Override public void fireSet(SetListPropertyEvent<?> e) {}
  }
}
