package org.lgna.story.implementation.eventhandling;

import org.junit.Test;
import org.lgna.story.MultipleEventPolicy;
import org.lgna.story.event.AbstractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AbstractEventHandlerAsyncTest {

  @Test
  public void enqueuePolicyDeliversQueuedEventsAfterActiveListenerCompletes() throws Exception {
    TestEventHandler handler = new TestEventHandler();
    List<String> calls = new ArrayList<>();
    CountDownLatch firstStarted = new CountDownLatch(1);
    CountDownLatch releaseFirst = new CountDownLatch(1);
    CountDownLatch completed = new CountDownLatch(2);
    AtomicInteger invocationCount = new AtomicInteger();

    TestListener listener = event -> {
      calls.add(event.name);
      if (invocationCount.incrementAndGet() == 1) {
        firstStarted.countDown();
        assertTrue(releaseFirst.await(5, TimeUnit.SECONDS));
      }
      completed.countDown();
    };
    handler.addListener(listener, MultipleEventPolicy.ENQUEUE);

    handler.dispatch(listener, new TestEvent("first"));
    EventTestSupport.await(firstStarted, "first queued listener invocation to start");

    handler.dispatch(listener, new TestEvent("second"));
    releaseFirst.countDown();

    EventTestSupport.await(completed, "queued listener invocations to complete");
    assertEquals(List.of("first", "second"), calls);
    EventTestSupport.until(
        () -> !handler.isFiringMap.get(listener).get(listener),
        "isFiringMap flag to clear after queued event delivery");
    assertFalse(handler.isFiringMap.get(listener).get(listener));
  }

  @Test
  public void silenceAndRestoreToggleEventDelivery() throws Exception {
    TestEventHandler handler = new TestEventHandler();
    CountDownLatch delivered = new CountDownLatch(1);
    TestListener listener = event -> delivered.countDown();
    handler.addListener(listener, MultipleEventPolicy.IGNORE);

    handler.silenceListeners();
    handler.dispatch(listener, new TestEvent("muted"));
    assertFalse(delivered.await(250, TimeUnit.MILLISECONDS));

    handler.restoreListeners();
    handler.dispatch(listener, new TestEvent("restored"));
    EventTestSupport.await(delivered, "restored listener delivery");
  }

  @Test
  public void isFiringMapClearedEvenWhenFireThrows() throws Exception {
    TestEventHandler handler = new TestEventHandler();
    CountDownLatch fireStarted = new CountDownLatch(1);

    TestListener listener = event -> {
      fireStarted.countDown();
      throw new RuntimeException("deliberate test explosion");
    };
    handler.addListener(listener, MultipleEventPolicy.IGNORE);

    handler.dispatch(listener, new TestEvent("boom"));

    // Wait for fire() to have been invoked (the listener signals via latch)
    EventTestSupport.await(fireStarted, "listener fire invocation");

    Map<Object, Boolean> activeThings = handler.isFiringMap.get(listener);
    EventTestSupport.until(
        () -> !activeThings.get(listener),
        "isFiringMap flag to clear after listener failure");
    assertFalse(
        "isFiringMap flag stuck true — fire() exception prevented cleanup",
        activeThings.get(listener));
  }

  private interface TestListener {
    void handle(TestEvent event) throws Exception;
  }

  private static final class TestEvent extends AbstractEvent {
    private final String name;

    private TestEvent(String name) {
      this.name = name;
    }
  }

  private static final class TestEventHandler extends AbstractEventHandler<TestListener, TestEvent> {
    void addListener(TestListener listener, MultipleEventPolicy policy) {
      registerIsFiringMap(listener);
      registerPolicyMap(listener, policy);
    }

    void dispatch(TestListener listener, TestEvent event) {
      fireEvent(listener, event);
    }

    @Override
    protected void fire(TestListener listener, TestEvent event) {
      try {
        listener.handle(event);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
}
