package org.lgna.story.implementation.eventhandling;

import org.junit.Test;
import org.lgna.story.MultipleEventPolicy;
import org.lgna.story.SThing;
import org.lgna.story.event.AbstractEvent;
import org.lgna.story.implementation.EntityImp;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;

public class AbstractBinaryEventHandlerTest {
  @Test
  public void startTrackingListenerAddsDistinctPairsAndRegistersPolicyState() {
    TestThing alpha = new TestThing("alpha");
    TestThing beta = new TestThing("beta");
    TestThing gamma = new TestThing("gamma");
    Object listener = new Object();
    TestBinaryHandler handler = new TestBinaryHandler();

    handler.begin(listener, List.of(alpha, beta), List.of(beta, gamma), MultipleEventPolicy.COMBINE);

    assertEquals(Set.of(alpha, beta, gamma), Set.copyOf(handler.getModelList()));
    assertTrue(handler.interactionListeners.get(alpha).get(gamma).contains(listener));
    assertTrue(handler.interactionListeners.get(gamma).get(alpha).contains(listener));
    assertFalse(handler.interactionListeners.get(beta).containsKey(beta));
    assertEquals(MultipleEventPolicy.COMBINE, handler.policyMap.get(listener));
    assertTrue(handler.isFiringMap.containsKey(listener));
  }

  @Test
  public void checkMarksRapidUpdatesUntilEnoughTimePasses() throws Exception {
    TestThing thing = new TestThing("subject");
    TestBinaryHandler handler = new TestBinaryHandler();

    handler.invokeCheck(thing);
    handler.invokeCheck(thing);

    assertEquals(List.of(thing), handler.checked);
    assertEquals(List.of(thing), handler.changed);

    forceLastCheckTime(handler, thing, System.currentTimeMillis() - 200L);
    handler.invokeCheck(thing);

    assertEquals(List.of(thing, thing), handler.checked);
  }

  @Test
  public void wasTrueAndWasFalseOnlyReturnTrueForRecordedState() {
    TestThing left = new TestThing("left");
    TestThing right = new TestThing("right");
    TestBinaryHandler handler = new TestBinaryHandler();
    Map<TestThing, Map<TestThing, Boolean>> previousState = new LinkedHashMap<>();
    previousState.put(left, new LinkedHashMap<>());
    previousState.put(right, new LinkedHashMap<>());
    previousState.get(left).put(right, true);
    previousState.get(right).put(left, false);

    assertTrue(handler.lookupWasTrue(previousState, left, right));
    assertFalse(handler.lookupWasFalse(previousState, left, right));
    assertTrue(handler.lookupWasFalse(previousState, right, left));
    assertFalse(handler.lookupWasTrue(previousState, right, right));
    assertFalse(handler.lookupWasFalse(previousState, left, left));
  }

  @SuppressWarnings("unchecked")
  private static void forceLastCheckTime(TestBinaryHandler handler, TestThing thing, long value) throws Exception {
    Field field = AbstractBinaryEventHandler.class.getDeclaredField("lastCheckTimes");
    field.setAccessible(true);
    Map<SThing, Long> lastCheckTimes = (Map<SThing, Long>) field.get(handler);
    lastCheckTimes.put(thing, value);
  }

  private static final class TestBinaryHandler extends AbstractBinaryEventHandler<Object, TestEvent, TestThing> {
    private final List<SThing> checked = new ArrayList<>();
    private final List<SThing> changed = new ArrayList<>();

    private void begin(Object listener, List<TestThing> groupA, List<TestThing> groupB, MultipleEventPolicy policy) {
      startTrackingListener(listener, groupA, groupB, policy);
    }

    private void invokeCheck(TestThing thing) {
      check(thing);
    }

    private boolean lookupWasTrue(Map<TestThing, Map<TestThing, Boolean>> previousState, TestThing a, TestThing b) {
      return wasTrue(previousState, a, b);
    }

    private boolean lookupWasFalse(Map<TestThing, Map<TestThing, Boolean>> previousState, TestThing a, TestThing b) {
      return wasFalse(previousState, a, b);
    }

    @Override
    protected void startTrackingThing(TestThing thing) {
      if (!getModelList().contains(thing)) {
        getModelList().add(thing);
      }
      interactionListeners.computeIfAbsent(thing, ignored -> new ConcurrentHashMap<>());
    }

    @Override
    protected void markAsChanged(SThing changedThing) {
      changed.add(changedThing);
    }

    @Override
    protected void checkForEvents(SThing changedThing) {
      checked.add(changedThing);
    }

    @Override
    protected void fire(Object listener, TestEvent event) {
    }
  }

  private static final class TestEvent extends AbstractEvent {
  }

  private static final class TestThing extends SThing {
    private final String name;

    private TestThing(String name) {
      this.name = name;
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
  }
}
