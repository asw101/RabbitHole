package edu.cmu.cs.dennisc.property;

import edu.cmu.cs.dennisc.pattern.AbstractInstancePropertyOwner;
import edu.cmu.cs.dennisc.property.event.PropertyEvent;
import edu.cmu.cs.dennisc.property.event.PropertyListener;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

public class InstancePropertyListenerTest {

  @Test
  public void listenersReceiveChangeEventsAndOwnerCachesName() {
    TestOwner owner = new TestOwner();
    List<PropertyEvent> events = new ArrayList<>();
    PropertyListener listener = events::add;
    owner.value.addPropertyListener(listener);

    owner.value.setValue("next");

    assertEquals(1, events.size());
    assertEquals("next", events.get(0).getValue());
    assertEquals("value", owner.value.getName());
    Collection<PropertyListener> listeners = owner.value.getPropertyListeners();
    assertTrue(listeners.contains(listener));
  }

  @Test
  public void removePropertyListenerStopsNotifications() {
    TestOwner owner = new TestOwner();
    List<PropertyEvent> events = new ArrayList<>();
    PropertyListener listener = events::add;
    owner.value.addPropertyListener(listener);
    owner.value.removePropertyListener(listener);

    owner.value.setValue("next");

    assertTrue(events.isEmpty());
  }

  public static final class TestOwner extends AbstractInstancePropertyOwner {
    public final StringProperty value = new StringProperty(this, "start");
  }
}
