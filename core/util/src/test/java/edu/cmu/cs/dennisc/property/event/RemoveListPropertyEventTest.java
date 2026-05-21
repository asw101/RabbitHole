package edu.cmu.cs.dennisc.property.event;

import edu.cmu.cs.dennisc.property.InstancePropertyOwner;
import edu.cmu.cs.dennisc.property.ListProperty;
import edu.cmu.cs.dennisc.property.InstanceProperty;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class RemoveListPropertyEventTest {

  @Test
  public void varargsConstructorStoresRemovedElements() {
    ListProperty<String> property = new ListProperty<>(new StubOwner());
    RemoveListPropertyEvent<String> event = new RemoveListPropertyEvent<>(property, 3, "gone", "away");

    assertEquals(3, event.getStartIndex());
    assertEquals(Arrays.asList("gone", "away"), event.getElements());
  }

  @Test
  public void collectionConstructorStoresRemovedCollection() {
    ListProperty<String> property = new ListProperty<>(new StubOwner());
    RemoveListPropertyEvent<String> event = new RemoveListPropertyEvent<>(property, 0, Arrays.asList("left"));

    assertSame(property, event.getTypedSource());
    assertEquals(Arrays.asList("left"), event.getElements());
  }

  private static final class StubOwner implements InstancePropertyOwner {
    @Override public Iterable<InstanceProperty<?>> getProperties() { return java.util.Collections.emptyList(); }
    @Override public InstanceProperty<?> getPropertyNamed(String name) { return null; }
    @Override public String lookupNameFor(InstanceProperty<?> instanceProperty) { return null; }
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
