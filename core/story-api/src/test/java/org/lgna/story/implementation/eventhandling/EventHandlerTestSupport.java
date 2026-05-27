package org.lgna.story.implementation.eventhandling;

import org.alice.math.immutable.Point2;
import org.alice.math.immutable.Point3;
import org.lgna.story.MultipleEventPolicy;
import org.lgna.story.SModel;
import org.lgna.story.SThing;
import org.lgna.story.event.CollisionEvent;
import org.lgna.story.event.OcclusionEndListener;
import org.lgna.story.event.OcclusionEvent;
import org.lgna.story.event.OcclusionStartListener;
import org.lgna.story.event.ProximityEvent;
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

final class EventHandlerTestSupport {
  private EventHandlerTestSupport() {
  }

  @SuppressWarnings("unchecked")
  static <T> T fieldValue(Class<?> owner, Object instance, String name) {
    try {
      Field field = owner.getDeclaredField(name);
      field.setAccessible(true);
      return (T) field.get(instance);
    } catch (ReflectiveOperationException e) {
      throw new AssertionError(e);
    }
  }

  static final class TestThing extends SThing {
    private final String name;
    private VerticalPrismCollisionHull collisionHull;

    TestThing(String name, VerticalPrismCollisionHull collisionHull) {
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

    void setCollisionHull(VerticalPrismCollisionHull collisionHull) {
      this.collisionHull = collisionHull;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  static final class TestModel extends SModel {
    private final String name;
    final double distanceFromCamera;

    TestModel(String name, double distanceFromCamera) {
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

  static final class StubHull extends VerticalPrismCollisionHull {
    private final double radius;

    StubHull(Point3 centerBase, double height, double radius) {
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

  static final class SyncCollisionHandler extends CollisionHandler {
    SyncCollisionHandler(Map<SThing, VerticalPrismCollisionHull> hulls) {
      super(hulls);
    }

    @Override
    protected void startTrackingThing(SThing thing) {
      if (!getModelList().contains(thing)) {
        getModelList().add(thing);
      }
      interactionListeners.computeIfAbsent(thing, ignored -> new ConcurrentHashMap<>());
      wereTouching().computeIfAbsent(thing, ignored -> new ConcurrentHashMap<>());
    }

    void trigger(SThing changedThing) {
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

    Map<SThing, Map<SThing, Boolean>> wereTouching() {
      return fieldValue(CollisionHandler.class, this, "wereTouching");
    }
  }

  static final class SyncProximityHandler extends ProximityEventHandler {
    SyncProximityHandler(Map<SThing, VerticalPrismCollisionHull> hulls) {
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
      wereClose().computeIfAbsent(listener, ignored -> new LinkedHashMap<>());
      wereClose().get(listener).computeIfAbsent(a, ignored -> new LinkedHashMap<>());
      wereClose().get(listener).computeIfAbsent(b, ignored -> new LinkedHashMap<>());
    }

    void trigger(SThing changedThing) {
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

    Map<Object, Map<SThing, Map<SThing, Boolean>>> wereClose() {
      return fieldValue(ProximityEventHandler.class, this, "wereClose");
    }
  }

  static final class SyncOcclusionHandler extends OcclusionHandler {
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
      wereOccluded().computeIfAbsent(thing, ignored -> new ConcurrentHashMap<>());
    }

    void setOccluded(SModel a, SModel b, boolean value) {
      occluded.put(pairKey(a, b), value);
    }

    void trigger(SModel changedThing) {
      checkForEvents(changedThing);
    }

    @Override
    protected void checkForEvents(SThing changedThing) {
      SModel changedModel = (SModel) changedThing;
      Map<SModel, Set<Object>> thingsToOcclude = interactionListeners.get(changedModel);
      for (SModel model : thingsToOcclude.keySet()) {
        Set<Object> listeners = thingsToOcclude.get(model);
        boolean doTheseOcclude = occluded.getOrDefault(pairKey(changedModel, model), false);
        boolean isOcclusionStart = wasFalse(wereOccluded(), changedModel, model) && doTheseOcclude;
        boolean isOcclusionEnd = wasTrue(wereOccluded(), changedModel, model) && !doTheseOcclude;
        wereOccluded().get(changedModel).put(model, doTheseOcclude);
        wereOccluded().get(model).put(changedModel, doTheseOcclude);
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

    Map<SModel, Map<SModel, Boolean>> wereOccluded() {
      return fieldValue(OcclusionHandler.class, this, "wereOccluded");
    }

    private String pairKey(SModel a, SModel b) {
      return a + "|" + b;
    }
  }
}
