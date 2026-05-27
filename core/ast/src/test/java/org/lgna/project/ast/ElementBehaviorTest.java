package org.lgna.project.ast;

import edu.cmu.cs.dennisc.property.InstanceProperty;
import edu.cmu.cs.dennisc.property.event.AddListPropertyEvent;
import edu.cmu.cs.dennisc.property.event.ClearListPropertyEvent;
import edu.cmu.cs.dennisc.property.event.ListPropertyListener;
import edu.cmu.cs.dennisc.property.event.PropertyEvent;
import edu.cmu.cs.dennisc.property.event.PropertyListener;
import edu.cmu.cs.dennisc.property.event.RemoveListPropertyEvent;
import edu.cmu.cs.dennisc.property.event.SetListPropertyEvent;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ElementBehaviorTest {
  public static class TestElement extends Element {
    public final InstanceProperty<String> title = new InstanceProperty<>(this, "untitled");
    public final InstanceProperty<Integer> count = new InstanceProperty<>(this, 0);
    public final InstanceProperty<TestElement> child = new InstanceProperty<>(this, null);
  }

  @Test
  public void propertiesAreDiscoverableByNameAndOwner() {
    TestElement element = new TestElement();

    assertTrue(element.getProperties().contains(element.title));
    assertTrue(element.getProperties().contains(element.count));
    assertTrue(element.getProperties().contains(element.child));
    assertSame(element.title, element.getPropertyNamed("Title"));
    assertSame(element.count, element.getPropertyNamed("Count"));
    assertEquals("child", element.lookupNameFor(element.child));
    assertNull(element.getPropertyNamed("Missing"));
  }

  @Test
  public void propertyAndListListenersFireUntilRemoved() {
    TestElement element = new TestElement();
    AtomicInteger propertyChanges = new AtomicInteger();
    AtomicInteger listEvents = new AtomicInteger();

    PropertyListener propertyListener = new PropertyListener() {
      @Override
      public void propertyChanged(PropertyEvent e) {
        propertyChanges.incrementAndGet();
      }
    };
    ListPropertyListener<Object> listListener = new ListPropertyListener<Object>() {
      @Override
      public void added(AddListPropertyEvent<Object> e) {
        listEvents.incrementAndGet();
      }

      @Override
      public void cleared(ClearListPropertyEvent<Object> e) {
        listEvents.incrementAndGet();
      }

      @Override
      public void removed(RemoveListPropertyEvent<Object> e) {
        listEvents.incrementAndGet();
      }

      @Override
      public void set(SetListPropertyEvent<Object> e) {
        listEvents.incrementAndGet();
      }
    };

    element.addPropertyListener(propertyListener);
    element.addListPropertyListener(listListener);
    element.firePropertyChanged(null);
    element.fireAdded(null);
    element.fireCleared(null);
    element.fireRemoved(null);
    element.fireSet(null);

    assertEquals(1, propertyChanges.get());
    assertEquals(4, listEvents.get());

    element.removePropertyListener(propertyListener);
    element.removeListPropertyListener(listListener);
    element.firePropertyChanged(null);
    element.fireAdded(null);

    assertEquals(1, propertyChanges.get());
    assertEquals(4, listEvents.get());
  }

  @Test
  public void equivalenceComparesNestedElementPropertiesRecursively() {
    TestElement left = new TestElement();
    TestElement right = new TestElement();
    TestElement child = new TestElement();
    TestElement different = new TestElement();

    left.title.setValue("hero");
    right.title.setValue("hero");
    child.title.setValue("child");
    different.title.setValue("different");
    left.child.setValue(child);
    right.child.setValue(new TestElement());
    right.child.getValue().title.setValue("child");

    assertTrue(left.isEquivalentTo(right));

    right.child.setValue(different);
    assertFalse(left.isEquivalentTo(right));
  }
}
