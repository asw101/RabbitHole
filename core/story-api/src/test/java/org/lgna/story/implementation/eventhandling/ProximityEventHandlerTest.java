package org.lgna.story.implementation.eventhandling;

import org.alice.math.immutable.Point3;
import org.junit.Test;
import org.lgna.story.MultipleEventPolicy;
import org.lgna.story.SThing;
import org.lgna.story.event.ProximityEnterListener;
import org.lgna.story.event.ProximityEvent;
import org.lgna.story.event.ProximityExitListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ProximityEventHandlerTest {
  @Test
  public void addProximityListenerStoresDistanceListenerStateAndGroupOrdering() {
    EventHandlerTestSupport.TestThing left = new EventHandlerTestSupport.TestThing("left", new EventHandlerTestSupport.StubHull(new Point3(0.0, 0.0, 0.0), 2.0, 1.0));
    EventHandlerTestSupport.TestThing right = new EventHandlerTestSupport.TestThing("right", new EventHandlerTestSupport.StubHull(new Point3(5.0, 0.0, 0.0), 2.0, 1.0));
    EventHandlerTestSupport.SyncProximityHandler handler = new EventHandlerTestSupport.SyncProximityHandler(new HashMap<>());
    ProximityEnterListener listener = event -> {
    };

    handler.addProximityEventListener(listener, List.of(left), List.of(right), 2.5, MultipleEventPolicy.ENQUEUE);

    Map<Object, Double> distances = EventHandlerTestSupport.fieldValue(ProximityEventHandler.class, handler, "listenerDistances");
    Map<Object, List<SThing>> groupAByListener = EventHandlerTestSupport.fieldValue(ProximityEventHandler.class, handler, "listenerToGroupA");
    assertEquals(Double.valueOf(2.5), distances.get(listener));
    assertEquals(List.of(left), groupAByListener.get(listener));
    assertTrue(handler.wereClose().containsKey(listener));
    assertTrue(handler.interactionListeners.get(left).get(right).contains(listener));
  }

  @Test
  public void proximityLifecycleFiresEnterAndExitAtConfiguredDistance() {
    EventHandlerTestSupport.TestThing left = new EventHandlerTestSupport.TestThing("left", new EventHandlerTestSupport.StubHull(new Point3(0.0, 0.0, 0.0), 2.0, 1.0));
    EventHandlerTestSupport.TestThing right = new EventHandlerTestSupport.TestThing("right", new EventHandlerTestSupport.StubHull(new Point3(5.0, 0.0, 0.0), 2.0, 1.0));
    EventHandlerTestSupport.SyncProximityHandler handler = new EventHandlerTestSupport.SyncProximityHandler(new HashMap<>());
    List<ProximityEvent> enters = new ArrayList<>();
    List<ProximityEvent> exits = new ArrayList<>();

    handler.addProximityEventListener((ProximityEnterListener) enters::add, List.of(left), List.of(right), 2.0, MultipleEventPolicy.IGNORE);
    handler.addProximityEventListener((ProximityExitListener) exits::add, List.of(left), List.of(right), 2.0, MultipleEventPolicy.IGNORE);

    handler.trigger(left);
    assertTrue(enters.isEmpty());

    right.setCollisionHull(new EventHandlerTestSupport.StubHull(new Point3(2.5, 0.0, 0.0), 2.0, 1.0));
    handler.trigger(right);
    assertEquals(1, enters.size());
    assertSame(left, enters.get(0).getSThingFromSetA());
    assertSame(right, enters.get(0).getSThingFromSetB());

    right.setCollisionHull(new EventHandlerTestSupport.StubHull(new Point3(8.0, 0.0, 0.0), 2.0, 1.0));
    handler.trigger(right);
    assertEquals(1, exits.size());
    assertSame(left, exits.get(0).getSThingFromSetA());
    assertSame(right, exits.get(0).getSThingFromSetB());
  }

  @Test
  public void proximityEventsAlwaysReportGroupAThingFirst() {
    EventHandlerTestSupport.TestThing groupA = new EventHandlerTestSupport.TestThing("groupA", new EventHandlerTestSupport.StubHull(new Point3(0.0, 0.0, 0.0), 2.0, 1.0));
    EventHandlerTestSupport.TestThing groupB = new EventHandlerTestSupport.TestThing("groupB", new EventHandlerTestSupport.StubHull(new Point3(5.0, 0.0, 0.0), 2.0, 1.0));
    EventHandlerTestSupport.SyncProximityHandler handler = new EventHandlerTestSupport.SyncProximityHandler(new HashMap<>());
    List<ProximityEvent> enters = new ArrayList<>();

    handler.addProximityEventListener((ProximityEnterListener) enters::add, List.of(groupA), List.of(groupB), 2.0, MultipleEventPolicy.IGNORE);
    handler.trigger(groupB);
    assertTrue(enters.isEmpty());

    groupB.setCollisionHull(new EventHandlerTestSupport.StubHull(new Point3(2.5, 0.0, 0.0), 2.0, 1.0));
    handler.trigger(groupB);

    assertEquals(1, enters.size());
    assertSame(groupA, enters.get(0).getSThingFromSetA());
    assertSame(groupB, enters.get(0).getSThingFromSetB());
  }
}
