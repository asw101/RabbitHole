package edu.cmu.cs.dennisc.property.event;

import edu.cmu.cs.dennisc.property.InstanceProperty;
import edu.cmu.cs.dennisc.property.InstancePropertyOwner;
import edu.cmu.cs.dennisc.property.ListProperty;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

public class PropertyEventDeepTest {

  private InstancePropertyOwner stubOwner = new StubPropertyOwner();

  // --- PropertyEvent ---

  @Test
  public void propertyEvent_sourceIsProperty() {
    InstanceProperty<String> prop = new InstanceProperty<>(stubOwner, "hello");
    PropertyEvent event = new PropertyEvent(prop, stubOwner, "world");
    assertSame(prop, event.getSource());
    assertSame(prop, event.getTypedSource());
  }

  @Test
  public void propertyEvent_ownerAndValue() {
    InstanceProperty<Integer> prop = new InstanceProperty<>(stubOwner, 42);
    PropertyEvent event = new PropertyEvent(prop, stubOwner, 99);
    assertSame(stubOwner, event.getOwner());
    assertEquals(99, event.getValue());
  }

  @Test
  public void propertyEvent_nullValue() {
    InstanceProperty<String> prop = new InstanceProperty<>(stubOwner, null);
    PropertyEvent event = new PropertyEvent(prop, stubOwner, null);
    assertNull(event.getValue());
  }

  // --- AddListPropertyEvent ---

  @Test
  public void addListPropertyEvent_fromVarargs() {
    ListProperty<String> lp = new ListProperty<>(stubOwner);
    AddListPropertyEvent<String> event = new AddListPropertyEvent<>(lp, 0, "a", "b", "c");
    assertEquals(0, event.getStartIndex());
    Collection<? extends String> elements = event.getElements();
    assertEquals(3, elements.size());
    assertTrue(elements.contains("a"));
    assertTrue(elements.contains("b"));
    assertTrue(elements.contains("c"));
    assertSame(lp, event.getSource());
  }

  @Test
  public void addListPropertyEvent_fromCollection() {
    ListProperty<Integer> lp = new ListProperty<>(stubOwner);
    List<Integer> items = Arrays.asList(10, 20, 30);
    AddListPropertyEvent<Integer> event = new AddListPropertyEvent<>(lp, 5, items);
    assertEquals(5, event.getStartIndex());
    assertEquals(3, event.getElements().size());
  }

  @Test
  public void addListPropertyEvent_emptyElements() {
    ListProperty<String> lp = new ListProperty<>(stubOwner);
    AddListPropertyEvent<String> event = new AddListPropertyEvent<>(lp, 0);
    assertEquals(0, event.getElements().size());
  }

  // --- RemoveListPropertyEvent ---

  @Test
  public void removeListPropertyEvent_fromVarargs() {
    ListProperty<String> lp = new ListProperty<>(stubOwner);
    RemoveListPropertyEvent<String> event = new RemoveListPropertyEvent<>(lp, 2, "x", "y");
    assertEquals(2, event.getStartIndex());
    assertEquals(2, event.getElements().size());
  }

  @Test
  public void removeListPropertyEvent_fromCollection() {
    ListProperty<String> lp = new ListProperty<>(stubOwner);
    List<String> removed = Arrays.asList("a", "b");
    RemoveListPropertyEvent<String> event = new RemoveListPropertyEvent<>(lp, 0, removed);
    assertEquals(0, event.getStartIndex());
    assertSame(removed, event.getElements());
  }

  // --- SetListPropertyEvent ---

  @Test
  public void setListPropertyEvent_fromVarargs() {
    ListProperty<String> lp = new ListProperty<>(stubOwner);
    SetListPropertyEvent<String> event = new SetListPropertyEvent<>(lp, 1, "replaced");
    assertEquals(1, event.getStartIndex());
    assertEquals(1, event.getElements().size());
    assertTrue(event.getElements().contains("replaced"));
  }

  @Test
  public void setListPropertyEvent_fromCollection() {
    ListProperty<Integer> lp = new ListProperty<>(stubOwner);
    List<Integer> vals = Arrays.asList(100, 200);
    SetListPropertyEvent<Integer> event = new SetListPropertyEvent<>(lp, 3, vals);
    assertEquals(3, event.getStartIndex());
    assertEquals(2, event.getElements().size());
  }

  // --- ClearListPropertyEvent ---

  @Test
  public void clearListPropertyEvent_sourceIsListProperty() {
    ListProperty<String> lp = new ListProperty<>(stubOwner);
    ClearListPropertyEvent<String> event = new ClearListPropertyEvent<>(lp);
    assertSame(lp, event.getSource());
    assertSame(lp, event.getTypedSource());
  }

  // --- AddAllListPropertyEvent ---

  @Test
  public void addAllListPropertyEvent_indexAndCollection() {
    ListProperty<String> lp = new ListProperty<>(stubOwner);
    List<String> items = Arrays.asList("p", "q", "r");
    AddAllListPropertyEvent<String> event = new AddAllListPropertyEvent<>(lp, 7, items);
    assertEquals(7, event.getIndex());
    assertEquals(3, event.getCollection().size());
    assertSame(items, event.getCollection());
  }

  @Test
  public void addAllListPropertyEvent_emptyCollection() {
    ListProperty<Double> lp = new ListProperty<>(stubOwner);
    List<Double> empty = new ArrayList<>();
    AddAllListPropertyEvent<Double> event = new AddAllListPropertyEvent<>(lp, 0, empty);
    assertEquals(0, event.getIndex());
    assertTrue(event.getCollection().isEmpty());
  }

  // --- SimplifiedListPropertyAdapter ---

  @Test
  public void simplifiedAdapter_delegatesAllToChanged() {
    ListProperty<String> lp = new ListProperty<>(stubOwner);
    final List<String> log = new ArrayList<>();

    SimplifiedListPropertyAdapter<String> adapter = new SimplifiedListPropertyAdapter<String>() {
      @Override
      protected void changed(ListPropertyEvent<String> e) {
        log.add(e.getClass().getSimpleName());
      }
    };

    adapter.added(new AddListPropertyEvent<>(lp, 0, "a"));
    adapter.cleared(new ClearListPropertyEvent<>(lp));
    adapter.removed(new RemoveListPropertyEvent<>(lp, 0, "a"));
    adapter.set(new SetListPropertyEvent<>(lp, 0, "b"));

    assertEquals(4, log.size());
    assertEquals("AddListPropertyEvent", log.get(0));
    assertEquals("ClearListPropertyEvent", log.get(1));
    assertEquals("RemoveListPropertyEvent", log.get(2));
    assertEquals("SetListPropertyEvent", log.get(3));
  }

  // --- PropertyListener ---

  @Test
  public void propertyListener_isInterface() {
    assertTrue(PropertyListener.class.isInterface());
  }

  @Test
  public void listPropertyListener_isInterface() {
    assertTrue(ListPropertyListener.class.isInterface());
  }

  // Minimal stub to satisfy InstancePropertyOwner contract
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
