package org.alice.ide.ast;

import org.junit.After;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * Characterization tests for {@link AstEventManager}.
 * Covers concurrency safety, re-entrancy, listener ordering,
 * stress tests for CopyOnWriteArrayList-backed event management,
 * and structural verification.
 *
 * Complements the basic behavioral tests in {@link AstEventManagerTest}.
 */
public class AstEventManagerCharacterizationTest {

  private final List<AstEventManager.TypeHierarchyListener> registeredListeners = new ArrayList<>();

  @After
  public void tearDown() {
    for (AstEventManager.TypeHierarchyListener listener : registeredListeners) {
      AstEventManager.removeTypeHierarchyListener(listener);
    }
    registeredListeners.clear();
  }

  private AstEventManager.TypeHierarchyListener trackListener(AstEventManager.TypeHierarchyListener l) {
    registeredListeners.add(l);
    return l;
  }

  // ── Structural characterization ────────────────────────────────

  @Test
  public void isUtilityClass() throws Exception {
    ReflectionTestHelper.assertUtilityClass(AstEventManager.class);
  }

  @Test
  public void typeHierarchyListenersFieldIsCopyOnWriteArrayList() throws Exception {
    Field field = AstEventManager.class.getDeclaredField("typeHierarchyListeners");
    field.setAccessible(true);
    Object value = field.get(null);
    assertTrue("Backing list must be CopyOnWriteArrayList for thread safety",
        value instanceof CopyOnWriteArrayList);
  }

  @Test
  public void typeHierarchyListenersFieldIsStaticFinal() throws Exception {
    Field field = AstEventManager.class.getDeclaredField("typeHierarchyListeners");
    int mods = field.getModifiers();
    assertTrue("typeHierarchyListeners must be static", Modifier.isStatic(mods));
    assertTrue("typeHierarchyListeners must be final", Modifier.isFinal(mods));
  }

  @Test
  public void typeHierarchyListenerInterfaceIsPublicStatic() {
    Class<?>[] innerClasses = AstEventManager.class.getDeclaredClasses();
    boolean found = false;
    for (Class<?> inner : innerClasses) {
      if (inner.getSimpleName().equals("TypeHierarchyListener")) {
        found = true;
        assertTrue("TypeHierarchyListener must be an interface", inner.isInterface());
        assertTrue("TypeHierarchyListener must be public",
            Modifier.isPublic(inner.getModifiers()));
        long abstractCount = java.util.Arrays.stream(inner.getDeclaredMethods())
            .filter(m -> Modifier.isAbstract(m.getModifiers()))
            .count();
        assertEquals("TypeHierarchyListener should have exactly 1 abstract method", 1, abstractCount);
      }
    }
    assertTrue("TypeHierarchyListener interface must exist", found);
  }

  // ── Ordering characterization ──────────────────────────────────

  @Test
  public void listenersAreFiredInRegistrationOrder() {
    List<String> order = Collections.synchronizedList(new ArrayList<>());
    AstEventManager.TypeHierarchyListener first = trackListener(() -> order.add("first"));
    AstEventManager.TypeHierarchyListener second = trackListener(() -> order.add("second"));
    AstEventManager.TypeHierarchyListener third = trackListener(() -> order.add("third"));

    AstEventManager.addTypeHierarchyListener(first);
    AstEventManager.addTypeHierarchyListener(second);
    AstEventManager.addTypeHierarchyListener(third);
    AstEventManager.fireTypeHierarchyListeners();

    assertEquals(3, order.size());
    assertEquals("first", order.get(0));
    assertEquals("second", order.get(1));
    assertEquals("third", order.get(2));
  }

  @Test
  public void addAndInvoke_addsToEndOfList_thenInvokes() {
    AtomicInteger existingCount = new AtomicInteger(0);
    AstEventManager.TypeHierarchyListener existing = trackListener(existingCount::incrementAndGet);
    AstEventManager.addTypeHierarchyListener(existing);

    AtomicInteger newCount = new AtomicInteger(0);
    AstEventManager.TypeHierarchyListener newListener = trackListener(newCount::incrementAndGet);
    AstEventManager.addAndInvokeTypeHierarchyListener(newListener);

    // addAndInvoke should have called newListener once immediately
    assertEquals("new listener called once by addAndInvoke", 1, newCount.get());
    // existing should NOT have been called by addAndInvoke
    assertEquals("existing listener not called by addAndInvoke", 0, existingCount.get());
  }

  // ── Re-entrancy ────────────────────────────────────────────────

  @Test
  public void firingDuringFire_doesNotDeadlock() {
    AtomicInteger innerCount = new AtomicInteger(0);
    AtomicInteger outerFireCount = new AtomicInteger(0);
    AstEventManager.TypeHierarchyListener inner = trackListener(innerCount::incrementAndGet);
    AstEventManager.TypeHierarchyListener outer = trackListener(() -> {
      // Re-entrant fire during iteration — CopyOnWriteArrayList should handle this
      // Limit to single re-entrant call to avoid infinite recursion
      if (outerFireCount.getAndIncrement() == 0) {
        AstEventManager.fireTypeHierarchyListeners();
      }
    });

    AstEventManager.addTypeHierarchyListener(inner);
    AstEventManager.addTypeHierarchyListener(outer);

    // This should not deadlock or throw ConcurrentModificationException
    AstEventManager.fireTypeHierarchyListeners();

    // inner was called at least twice: once by outer fire, once by outer's re-entrant fire
    assertTrue("Inner listener should be called at least twice due to re-entrancy",
        innerCount.get() >= 2);
  }

  @Test
  public void removeDuringFire_doesNotThrowConcurrentModification() {
    AtomicInteger callCount = new AtomicInteger(0);
    AstEventManager.TypeHierarchyListener selfRemovingListener = new AstEventManager.TypeHierarchyListener() {
      @Override
      public void typeHierarchyHasPotentiallyChanged() {
        callCount.incrementAndGet();
        AstEventManager.removeTypeHierarchyListener(this);
      }
    };
    registeredListeners.add(selfRemovingListener);

    AstEventManager.addTypeHierarchyListener(selfRemovingListener);

    // Should not throw ConcurrentModificationException
    AstEventManager.fireTypeHierarchyListeners();
    assertEquals("Self-removing listener called exactly once", 1, callCount.get());

    // Verify it was removed
    callCount.set(0);
    AstEventManager.fireTypeHierarchyListeners();
    assertEquals("Removed listener should not be called again", 0, callCount.get());
  }

  @Test
  public void addDuringFire_doesNotAffectCurrentIteration() {
    AtomicInteger lateCount = new AtomicInteger(0);
    AstEventManager.TypeHierarchyListener lateListener = trackListener(lateCount::incrementAndGet);

    AstEventManager.TypeHierarchyListener adder = trackListener(() -> {
      AstEventManager.addTypeHierarchyListener(lateListener);
    });

    AstEventManager.addTypeHierarchyListener(adder);
    AstEventManager.fireTypeHierarchyListeners();

    // CopyOnWriteArrayList snapshot: lateListener won't be in current iteration
    assertEquals("Late-added listener should NOT be called during current fire", 0, lateCount.get());

    // But next fire should include it
    AstEventManager.fireTypeHierarchyListeners();
    assertTrue("Late-added listener should be called on subsequent fire",
        lateCount.get() >= 1);
  }

  // ── Concurrency stress ─────────────────────────────────────────

  @Test
  public void concurrentAddRemoveAndFire_doesNotCorrupt() throws Exception {
    final int THREAD_COUNT = 4;
    final int OPS_PER_THREAD = 25;
    ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
    CountDownLatch startLatch = new CountDownLatch(1);
    CountDownLatch doneLatch = new CountDownLatch(THREAD_COUNT);
    AtomicReference<Throwable> error = new AtomicReference<>(null);

    for (int t = 0; t < THREAD_COUNT; t++) {
      executor.submit(() -> {
        try {
          startLatch.await();
          for (int i = 0; i < OPS_PER_THREAD; i++) {
            AstEventManager.TypeHierarchyListener listener = () -> {};
            AstEventManager.addTypeHierarchyListener(listener);
            AstEventManager.fireTypeHierarchyListeners();
            AstEventManager.removeTypeHierarchyListener(listener);
          }
        } catch (Throwable e) {
          error.compareAndSet(null, e);
        } finally {
          doneLatch.countDown();
        }
      });
    }

    startLatch.countDown();
    try {
      assertTrue("All threads should finish within 10 seconds",
          doneLatch.await(10, TimeUnit.SECONDS));
    } finally {
      executor.shutdownNow();
    }

    assertNull("No exceptions during concurrent operations: " +
        (error.get() != null ? error.get().toString() : ""), error.get());
  }

  @Test
  public void concurrentAddAndInvoke_doesNotCorrupt() throws Exception {
    final int THREAD_COUNT = 4;
    final int OPS_PER_THREAD = 25;
    ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
    CountDownLatch startLatch = new CountDownLatch(1);
    CountDownLatch doneLatch = new CountDownLatch(THREAD_COUNT);
    AtomicReference<Throwable> error = new AtomicReference<>(null);

    for (int t = 0; t < THREAD_COUNT; t++) {
      executor.submit(() -> {
        try {
          startLatch.await();
          for (int i = 0; i < OPS_PER_THREAD; i++) {
            AtomicInteger count = new AtomicInteger(0);
            AstEventManager.TypeHierarchyListener listener = count::incrementAndGet;
            AstEventManager.addAndInvokeTypeHierarchyListener(listener);
            assertTrue("addAndInvoke must call listener at least once", count.get() >= 1);
            AstEventManager.removeTypeHierarchyListener(listener);
          }
        } catch (Throwable e) {
          error.compareAndSet(null, e);
        } finally {
          doneLatch.countDown();
        }
      });
    }

    startLatch.countDown();
    try {
      assertTrue("All threads should finish within 10 seconds",
          doneLatch.await(10, TimeUnit.SECONDS));
    } finally {
      executor.shutdownNow();
    }

    assertNull("No exceptions during concurrent addAndInvoke: " +
        (error.get() != null ? error.get().toString() : ""), error.get());
  }

  // ── Duplicate listener ─────────────────────────────────────────

  @Test
  public void duplicateListenerRegistration_bothReceiveCalls() {
    AtomicInteger callCount = new AtomicInteger(0);
    AstEventManager.TypeHierarchyListener listener = trackListener(callCount::incrementAndGet);

    AstEventManager.addTypeHierarchyListener(listener);
    AstEventManager.addTypeHierarchyListener(listener);
    AstEventManager.fireTypeHierarchyListeners();

    // CopyOnWriteArrayList allows duplicates
    assertEquals("Duplicate listener should be called twice", 2, callCount.get());

    // Remove once — should still be registered once
    AstEventManager.removeTypeHierarchyListener(listener);
    callCount.set(0);
    AstEventManager.fireTypeHierarchyListeners();
    assertEquals("After one remove, duplicate should still fire once", 1, callCount.get());

    AstEventManager.removeTypeHierarchyListener(listener);
  }

  // ── Null safety ────────────────────────────────────────────────

  @Test
  public void removeNonRegisteredListener_doesNotThrow() {
    AstEventManager.TypeHierarchyListener listener = () -> {};
    // Removing a listener that was never added should not throw
    AstEventManager.removeTypeHierarchyListener(listener);
  }

  // ── Listener exception isolation ───────────────────────────────

  @Test
  public void listenerThrowingException_propagatesToCaller() {
    AstEventManager.TypeHierarchyListener throwingListener = trackListener(() -> {
      throw new RuntimeException("test explosion");
    });
    AstEventManager.addTypeHierarchyListener(throwingListener);

    try {
      AstEventManager.fireTypeHierarchyListeners();
      fail("Expected RuntimeException to propagate from listener");
    } catch (RuntimeException e) {
      assertEquals("test explosion", e.getMessage());
    }
  }

  // ── All static methods check ───────────────────────────────────

  @Test
  public void hasExactlyFourPublicMethods() {
    long publicMethodCount = java.util.Arrays.stream(AstEventManager.class.getDeclaredMethods())
        .filter(m -> Modifier.isPublic(m.getModifiers()))
        .count();
    assertEquals("AstEventManager should have 4 public methods: add, addAndInvoke, remove, fire",
        4, publicMethodCount);
  }
}
