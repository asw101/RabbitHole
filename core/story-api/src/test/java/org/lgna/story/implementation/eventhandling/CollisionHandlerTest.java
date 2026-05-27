package org.lgna.story.implementation.eventhandling;

import org.alice.math.immutable.Point3;
import org.junit.Test;
import org.lgna.story.MultipleEventPolicy;
import org.lgna.story.SThing;
import org.lgna.story.event.CollisionEndListener;
import org.lgna.story.event.CollisionEvent;
import org.lgna.story.event.CollisionStartListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class CollisionHandlerTest {
  @Test
  public void addCollisionListenerRegistersTrackedPairsAndGroupOrdering() {
    Map<SThing, VerticalPrismCollisionHull> hulls = new HashMap<>();
    EventHandlerTestSupport.TestThing left = new EventHandlerTestSupport.TestThing("left", new EventHandlerTestSupport.StubHull(new Point3(0.0, 0.0, 0.0), 2.0, 1.0));
    EventHandlerTestSupport.TestThing right = new EventHandlerTestSupport.TestThing("right", new EventHandlerTestSupport.StubHull(new Point3(4.0, 0.0, 0.0), 2.0, 1.0));
    EventHandlerTestSupport.SyncCollisionHandler handler = new EventHandlerTestSupport.SyncCollisionHandler(hulls);
    CollisionStartListener listener = event -> {
    };

    handler.addCollisionListener(listener, List.of(left), List.of(right), MultipleEventPolicy.COMBINE);

    Map<Object, List<SThing>> groupAByListener = EventHandlerTestSupport.fieldValue(CollisionHandler.class, handler, "listenerToGroupA");
    assertEquals(List.of(left), groupAByListener.get(listener));
    assertTrue(handler.interactionListeners.get(left).get(right).contains(listener));
    assertTrue(handler.interactionListeners.get(right).get(left).contains(listener));
    assertEquals(List.of(left, right), handler.getModelList());
  }

  @Test
  public void collisionLifecycleFiresStartThenDelayedEndForSweptMovement() {
    EventHandlerTestSupport.TestThing left = new EventHandlerTestSupport.TestThing("left", new EventHandlerTestSupport.StubHull(new Point3(0.0, 0.0, 0.0), 2.0, 1.0));
    EventHandlerTestSupport.TestThing right = new EventHandlerTestSupport.TestThing("right", new EventHandlerTestSupport.StubHull(new Point3(4.0, 0.0, 0.0), 2.0, 1.0));
    EventHandlerTestSupport.SyncCollisionHandler handler = new EventHandlerTestSupport.SyncCollisionHandler(new HashMap<>());
    List<CollisionEvent> starts = new ArrayList<>();
    List<CollisionEvent> ends = new ArrayList<>();

    handler.addCollisionListener((CollisionStartListener) starts::add, List.of(left), List.of(right), MultipleEventPolicy.IGNORE);
    handler.addCollisionListener((CollisionEndListener) ends::add, List.of(left), List.of(right), MultipleEventPolicy.IGNORE);

    handler.trigger(left);
    assertTrue(starts.isEmpty());

    right.setCollisionHull(new EventHandlerTestSupport.StubHull(new Point3(0.5, 0.0, 0.0), 2.0, 1.0));
    handler.trigger(right);
    assertEquals(1, starts.size());
    assertSame(left, starts.get(0).getSThingFromSetA());
    assertSame(right, starts.get(0).getSThingFromSetB());

    right.setCollisionHull(new EventHandlerTestSupport.StubHull(new Point3(6.0, 0.0, 0.0), 2.0, 1.0));
    handler.trigger(right);
    assertTrue(ends.isEmpty());

    handler.trigger(right);
    assertEquals(1, ends.size());
    assertSame(left, ends.get(0).getSThingFromSetA());
    assertSame(right, ends.get(0).getSThingFromSetB());
  }

  @Test
  public void doTheseCollideUsesFreshHullValuesAfterChanges() {
    Map<SThing, VerticalPrismCollisionHull> hulls = new HashMap<>();
    EventHandlerTestSupport.TestThing left = new EventHandlerTestSupport.TestThing("left", new EventHandlerTestSupport.StubHull(new Point3(0.0, 0.0, 0.0), 2.0, 1.0));
    EventHandlerTestSupport.TestThing right = new EventHandlerTestSupport.TestThing("right", new EventHandlerTestSupport.StubHull(new Point3(3.0, 0.0, 0.0), 2.0, 1.0));
    EventHandlerTestSupport.SyncCollisionHandler handler = new EventHandlerTestSupport.SyncCollisionHandler(hulls);

    org.junit.Assert.assertFalse(handler.doTheseCollide(left, right));

    right.setCollisionHull(new EventHandlerTestSupport.StubHull(new Point3(0.25, 0.0, 0.0), 2.0, 1.0));
    hulls.clear();

    assertTrue(handler.doTheseCollide(left, right));
  }
}
