package org.lgna.story.implementation.eventhandling;

import org.junit.Test;
import org.lgna.story.MultipleEventPolicy;
import org.lgna.story.event.AbstractEvent;

import java.util.ArrayList;
import java.util.List;
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
    assertTrue(firstStarted.await(5, TimeUnit.SECONDS));

    handler.dispatch(listener, new TestEvent("second"));
    releaseFirst.countDown();

    assertTrue(completed.await(5, TimeUnit.SECONDS));
    assertEquals(List.of("first", "second"), calls);
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
    assertTrue(delivered.await(5, TimeUnit.SECONDS));
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
