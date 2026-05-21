package edu.cmu.cs.dennisc.property.event;

import edu.cmu.cs.dennisc.property.ListProperty;
import edu.cmu.cs.dennisc.property.InstancePropertyOwner;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class AddListPropertyEventTest {

  @Test
  public void varargsConstructorStoresStartIndexAndElements() {
    ListProperty<String> property = new ListProperty<>(new StubOwner());
    AddListPropertyEvent<String> event = new AddListPropertyEvent<>(property, 2, "a", "b");

    assertSame(property, event.getTypedSource());
    assertEquals(2, event.getStartIndex());
    assertEquals(Arrays.asList("a", "b"), event.getElements());
  }

  @Test
  public void collectionConstructorStoresCollection() {
    ListProperty<String> property = new ListProperty<>(new StubOwner());
    AddListPropertyEvent<String> event = new AddListPropertyEvent<>(property, 1, Arrays.asList("x", "y"));

    assertEquals(1, event.getStartIndex());
    assertEquals(Arrays.asList("x", "y"), event.getElements());
  }

  static final class StubOwner implements InstancePropertyOwner {
    @Override public Iterable<edu.cmu.cs.dennisc.property.InstanceProperty<?>> getProperties() { return java.util.Collections.emptyList(); }
    @Override public edu.cmu.cs.dennisc.property.InstanceProperty<?> getPropertyNamed(String name) { return null; }
    @Override public String lookupNameFor(edu.cmu.cs.dennisc.property.InstanceProperty<?> instanceProperty) { return null; }
    @Override public void firePropertyChanging(PropertyEvent e) { }
    @Override public void firePropertyChanged(PropertyEvent e) { }
    @Override public void fireAdding(AddListPropertyEvent<?> e) { }
    @Override public void fireAdded(AddListPropertyEvent<?> e) { }
    @Override public void fireClearing(ClearListPropertyEvent<?> e) { }
    @Override public void fireCleared(ClearListPropertyEvent<?> e) { }
    @Override public void fireRemoving(RemoveListPropertyEvent<?> e) { }
    @Override public void fireRemoved(RemoveListPropertyEvent<?> e) { }
    @Override public void fireSetting(SetListPropertyEvent<?> e) { }
    @Override public void fireSet(SetListPropertyEvent<?> e) { }
  }
}
