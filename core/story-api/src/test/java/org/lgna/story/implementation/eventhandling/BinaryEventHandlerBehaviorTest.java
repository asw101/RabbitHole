package org.lgna.story.implementation.eventhandling;

import org.alice.math.immutable.Point2;
import org.alice.math.immutable.Point3;
import org.junit.Test;
import org.lgna.story.MultipleEventPolicy;
import org.lgna.story.SModel;
import org.lgna.story.SThing;
import org.lgna.story.event.CollisionEndListener;
import org.lgna.story.event.CollisionEvent;
import org.lgna.story.event.CollisionStartListener;
import org.lgna.story.event.OcclusionEndListener;
import org.lgna.story.event.OcclusionEvent;
import org.lgna.story.event.OcclusionStartListener;
import org.lgna.story.event.ProximityEnterListener;
import org.lgna.story.event.ProximityEvent;
import org.lgna.story.event.ProximityExitListener;
import org.lgna.story.implementation.EntityImp;
import org.lgna.story.implementation.ModelImp;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;

public class BinaryEventHandlerBehaviorTest {
  @Test
  public void collisionHandlerFiresStartAndDelayedEndForSweptMovement() {
    Map<SThing, VerticalPrismCollisionHull> hulls = new HashMap<>();
    TestThing left = new TestThing("left", new StubHull(new Point3(0.0, 0.0, 0.0), 2.0, 1.0));
    TestThing right = new TestThing("right", new StubHull(new Point3(4.0, 0.0, 0.0), 2.0, 1.0));
    SyncCollisionHandler handler = new SyncCollisionHandler(hulls);
    List<CollisionEvent> starts = new ArrayList<>();
    List<CollisionEvent> ends = new ArrayList<>();

    handler.addCollisionListener((CollisionStartListener) starts::add, List.of(left), List.of(right), MultipleEventPolicy.IGNORE);
    handler.addCollisionListener((CollisionEndListener) ends::add, List.of(left), List.of(right), MultipleEventPolicy.IGNORE);

    handler.trigger(left);
    assertTrue("initial collision check only seeds prior state", starts.isEmpty());

    right.setCollisionHull(new StubHull(new Point3(0.5, 0.0, 0.0), 2.0, 1.0));
    handler.trigger(right);

    assertEquals(1, starts.size());
    assertSame(left, starts.get(0).getSThingFromSetA());
    assertSame(right, starts.get(0).getSThingFromSetB());

    right.setCollisionHull(new StubHull(new Point3(6.0, 0.0, 0.0), 2.0, 1.0));
    handler.trigger(right);
    assertTrue("combination hull should keep the swept movement colliding for one check", ends.isEmpty());

    handler.trigger(right);
    assertEquals(1, ends.size());
    assertSame(left, ends.get(0).getSThingFromSetA());
    assertSame(right, ends.get(0).getSThingFromSetB());
  }

  @Test
  public void collisionHandlerDoTheseCollideUsesCurrentCollisionHulls() {
    Map<SThing, VerticalPrismCollisionHull> hulls = new HashMap<>();
    TestThing left = new TestThing("left", new StubHull(new Point3(0.0, 0.0, 0.0), 2.0, 1.0));
    TestThing right = new TestThing("right", new StubHull(new Point3(3.0, 0.0, 0.0), 2.0, 1.0));
    SyncCollisionHandler handler = new SyncCollisionHandler(hulls);

    assertFalse(handler.doTheseCollide(left, right));

    right.setCollisionHull(new StubHull(new Point3(0.25, 0.0, 0.0), 2.0, 1.0));
    hulls.clear();

    assertTrue(handler.doTheseCollide(left, right));
  }

  @Test
  public void proximityHandlerFiresEnterAndExitAtConfiguredDistance() {
    TestThing left = new TestThing("left", new StubHull(new Point3(0.0, 0.0, 0.0), 2.0, 1.0));
    TestThing right = new TestThing("right", new StubHull(new Point3(5.0, 0.0, 0.0), 2.0, 1.0));
    SyncProximityHandler handler = new SyncProximityHandler(new HashMap<>());
    List<ProximityEvent> enters = new ArrayList<>();
    List<ProximityEvent> exits = new ArrayList<>();

    handler.addProximityEventListener((ProximityEnterListener) enters::add, List.of(left), List.of(right), 2.0, MultipleEventPolicy.IGNORE);
    handler.addProximityEventListener((ProximityExitListener) exits::add, List.of(left), List.of(right), 2.0, MultipleEventPolicy.IGNORE);

    handler.trigger(left);
    assertTrue("initial proximity check only seeds prior state", enters.isEmpty());

    right.setCollisionHull(new StubHull(new Point3(2.5, 0.0, 0.0), 2.0, 1.0));
    handler.trigger(right);

    assertEquals(1, enters.size());
    assertSame(left, enters.get(0).getSThingFromSetA());
    assertSame(right, enters.get(0).getSThingFromSetB());

    right.setCollisionHull(new StubHull(new Point3(8.0, 0.0, 0.0), 2.0, 1.0));
    handler.trigger(right);

    assertEquals(1, exits.size());
    assertSame(left, exits.get(0).getSThingFromSetA());
    assertSame(right, exits.get(0).getSThingFromSetB());
  }

  @Test
  public void proximityHandlerReportsGroupAThingFirst() {
    TestThing groupA = new TestThing("groupA", new StubHull(new Point3(0.0, 0.0, 0.0), 2.0, 1.0));
    TestThing groupB = new TestThing("groupB", new StubHull(new Point3(5.0, 0.0, 0.0), 2.0, 1.0));
    SyncProximityHandler handler = new SyncProximityHandler(new HashMap<>());
    List<ProximityEvent> enters = new ArrayList<>();

    handler.addProximityEventListener((ProximityEnterListener) enters::add, List.of(groupA), List.of(groupB), 2.0, MultipleEventPolicy.IGNORE);
    handler.trigger(groupB);
    assertTrue(enters.isEmpty());

    groupB.setCollisionHull(new StubHull(new Point3(2.5, 0.0, 0.0), 2.0, 1.0));
    handler.trigger(groupB);

    assertEquals(1, enters.size());
    assertSame(groupA, enters.get(0).getSThingFromSetA());
    assertSame(groupB, enters.get(0).getSThingFromSetB());
  }

  @Test
  public void occlusionHandlerFiresStartAndEndWithForegroundOrdering() {
    TestModel background = new TestModel("background", 8.0);
    TestModel foreground = new TestModel("foreground", 4.0);
    SyncOcclusionHandler handler = new SyncOcclusionHandler();
    List<OcclusionEvent> starts = new ArrayList<>();
    List<OcclusionEvent> ends = new ArrayList<>();

    handler.addOcclusionEventListener((OcclusionStartListener) starts::add, List.of(background), List.of(foreground), MultipleEventPolicy.IGNORE);
    handler.addOcclusionEventListener((OcclusionEndListener) ends::add, List.of(background), List.of(foreground), MultipleEventPolicy.IGNORE);

    handler.setOccluded(background, foreground, false);
    handler.trigger(background);
    assertTrue(starts.isEmpty());

    handler.setOccluded(background, foreground, true);
    handler.trigger(background);

    assertEquals(1, starts.size());
    assertSame(foreground, starts.get(0).getForegroundModel());
    assertSame(background, starts.get(0).getBackgroundModel());

    handler.setOccluded(background, foreground, false);
    handler.trigger(background);

    assertEquals(1, ends.size());
    assertSame(foreground, ends.get(0).getForegroundModel());
    assertSame(background, ends.get(0).getBackgroundModel());
  }

  private static final class SyncCollisionHandler extends CollisionHandler {
    private SyncCollisionHandler(Map<SThing, VerticalPrismCollisionHull> hulls) {
      super(hulls);
    }

    @Override
    protected void startTrackingThing(SThing thing) {
      if (!getModelList().contains(thing)) {
        getModelList().add(thing);
      }
      interactionListeners.computeIfAbsent(thing, ignored -> new ConcurrentHashMap<>());
      reflectedWereTouching().computeIfAbsent(thing, ignored -> new ConcurrentHashMap<>());
    }

    private void trigger(SThing changedThing) {
      checkForEvents(changedThing);
    }

    @Override
    protected void fireEvent(Object listener, CollisionEvent event) {
      fire(listener, event);
    }

    @Override
    protected void fireEvent(Object listener, CollisionEvent event, Object multiEventLock) {
      fire(listener, event);
    }

    @SuppressWarnings("unchecked")
    private Map<SThing, Map<SThing, Boolean>> reflectedWereTouching() {
      return (Map<SThing, Map<SThing, Boolean>>) reflectedField(CollisionHandler.class, this, "wereTouching");
    }
  }

  private static final class SyncProximityHandler extends ProximityEventHandler {
    private SyncProximityHandler(Map<SThing, VerticalPrismCollisionHull> hulls) {
      super(hulls);
    }

    @Override
    protected void startTrackingThing(SThing thing) {
      if (!getModelList().contains(thing)) {
        getModelList().add(thing);
      }
      interactionListeners.computeIfAbsent(thing, ignored -> new ConcurrentHashMap<>());
    }

    @Override
    protected void startTrackingInteraction(SThing a, SThing b, Object listener) {
      super.startTrackingInteraction(a, b, listener);
      reflectedWereClose().computeIfAbsent(listener, ignored -> new LinkedHashMap<>());
      reflectedWereClose().get(listener).computeIfAbsent(a, ignored -> new LinkedHashMap<>());
      reflectedWereClose().get(listener).computeIfAbsent(b, ignored -> new LinkedHashMap<>());
    }

    private void trigger(SThing changedThing) {
      checkForEvents(changedThing);
    }

    @Override
    protected void fireEvent(Object listener, ProximityEvent event) {
      fire(listener, event);
    }

    @Override
    protected void fireEvent(Object listener, ProximityEvent event, Object multiEventLock) {
      fire(listener, event);
    }

    @SuppressWarnings("unchecked")
    private Map<Object, Map<SThing, Map<SThing, Boolean>>> reflectedWereClose() {
      return (Map<Object, Map<SThing, Map<SThing, Boolean>>>) reflectedField(ProximityEventHandler.class, this, "wereClose");
    }
  }

  private static final class SyncOcclusionHandler extends OcclusionHandler {
    private final Map<String, Boolean> occluded = new HashMap<>();

    @Override
    public void addOcclusionEventListener(Object occlusionEventListener, List<SModel> groupA, List<SModel> groupB, MultipleEventPolicy policy) {
      startTrackingListener(occlusionEventListener, groupA, groupB, policy);
    }

    @Override
    protected void startTrackingThing(SModel thing) {
      if (!getModelList().contains(thing)) {
        getModelList().add(thing);
      }
      interactionListeners.computeIfAbsent(thing, ignored -> new ConcurrentHashMap<>());
      reflectedWereOccluded().computeIfAbsent(thing, ignored -> new ConcurrentHashMap<>());
    }

    private void setOccluded(SModel a, SModel b, boolean value) {
      occluded.put(pairKey(a, b), value);
    }

    private void trigger(SModel changedThing) {
      checkForEvents(changedThing);
    }

    @Override
    protected void checkForEvents(SThing changedThing) {
      SModel changedModel = (SModel) changedThing;
      Map<SModel, Set<Object>> thingsToOcclude = interactionListeners.get(changedModel);
      for (SModel model : thingsToOcclude.keySet()) {
        Set<Object> listeners = thingsToOcclude.get(model);
        boolean doTheseOcclude = occluded.getOrDefault(pairKey(changedModel, model), false);
        boolean isOcclusionStart = wasFalse(reflectedWereOccluded(), changedModel, model) && doTheseOcclude;
        boolean isOcclusionEnd = wasTrue(reflectedWereOccluded(), changedModel, model) && !doTheseOcclude;
        reflectedWereOccluded().get(changedModel).put(model, doTheseOcclude);
        reflectedWereOccluded().get(model).put(changedModel, doTheseOcclude);
        if (!isOcclusionStart && !isOcclusionEnd) {
          continue;
        }
        TestModel changed = (TestModel) changedModel;
        TestModel other = (TestModel) model;
        boolean isChangedModelInBackground = other.distanceFromCamera < changed.distanceFromCamera;
        SModel foreground = isChangedModelInBackground ? model : changedModel;
        SModel background = isChangedModelInBackground ? changedModel : model;
        for (Object listener : listeners) {
          if (isOcclusionStart && listener instanceof OcclusionStartListener) {
            fireEvent(listener, new org.lgna.story.event.StartOcclusionEvent(foreground, background));
          } else if (isOcclusionEnd && listener instanceof OcclusionEndListener) {
            fireEvent(listener, new org.lgna.story.event.EndOcclusionEvent(foreground, background));
          }
        }
      }
    }

    @Override
    protected void fireEvent(Object listener, OcclusionEvent event) {
      fire(listener, event);
    }

    @Override
    protected void fireEvent(Object listener, OcclusionEvent event, Object multiEventLock) {
      fire(listener, event);
    }

    @SuppressWarnings("unchecked")
    private Map<SModel, Map<SModel, Boolean>> reflectedWereOccluded() {
      return (Map<SModel, Map<SModel, Boolean>>) reflectedField(OcclusionHandler.class, this, "wereOccluded");
    }

    private String pairKey(SModel a, SModel b) {
      return a + "|" + b;
    }
  }

  private static Object reflectedField(Class<?> owner, Object instance, String name) {
    try {
      Field field = owner.getDeclaredField(name);
      field.setAccessible(true);
      return field.get(instance);
    } catch (ReflectiveOperationException e) {
      throw new AssertionError(e);
    }
  }

  private static class TestThing extends SThing {
    private final String name;
    private VerticalPrismCollisionHull collisionHull;

    private TestThing(String name, VerticalPrismCollisionHull collisionHull) {
      this.name = name;
      this.collisionHull = collisionHull;
    }

    @Override
    public <T extends EntityImp> T getImplementation() {
      return null;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public void setName(String name) {
    }

    @Override
    public VerticalPrismCollisionHull getCollisionHull() {
      return collisionHull;
    }

    private void setCollisionHull(VerticalPrismCollisionHull collisionHull) {
      this.collisionHull = collisionHull;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  private static final class TestModel extends SModel {
    private final String name;
    private final double distanceFromCamera;

    private TestModel(String name, double distanceFromCamera) {
      this.name = name;
      this.distanceFromCamera = distanceFromCamera;
    }

    @Override
    public ModelImp getImplementation() {
      return null;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public void setName(String name) {
    }

    @Override
    public String toString() {
      return name;
    }
  }

  private static final class StubHull extends VerticalPrismCollisionHull {
    private final double radius;

    private StubHull(Point3 centerBase, double height, double radius) {
      super(centerBase, height);
      this.radius = radius;
    }

    @Override
    public double distanceAlong(double xDistance, double zDistance) {
      return radius;
    }

    @Override
    protected List<Point2> getCrossSectionVertices(Point3 newCenter) {
      Point3 base = newCenter == null ? centerBase : newCenter;
      List<Point2> vertices = new ArrayList<>();
      vertices.add(new Point2(base.x() - radius, base.z() - radius));
      vertices.add(new Point2(base.x() + radius, base.z() + radius));
      return vertices;
    }
  }
}
