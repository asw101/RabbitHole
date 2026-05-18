package org.alice.ide.ast;

import org.junit.After;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class AstEventManagerTest {

  @After
  public void tearDown() {
    // Clean up any listeners we added
  }

  @Test
  public void addAndRemoveTypeHierarchyListener() {
    AtomicInteger callCount = new AtomicInteger(0);
    AstEventManager.TypeHierarchyListener listener = callCount::incrementAndGet;
    AstEventManager.addTypeHierarchyListener(listener);
    AstEventManager.fireTypeHierarchyListeners();
    assertEquals(1, callCount.get());
    AstEventManager.removeTypeHierarchyListener(listener);
    AstEventManager.fireTypeHierarchyListeners();
    assertEquals(1, callCount.get());
  }

  @Test
  public void addAndInvokeTypeHierarchyListenerCallsImmediately() {
    AtomicInteger callCount = new AtomicInteger(0);
    AstEventManager.TypeHierarchyListener listener = callCount::incrementAndGet;
    AstEventManager.addAndInvokeTypeHierarchyListener(listener);
    assertEquals(1, callCount.get());
    AstEventManager.removeTypeHierarchyListener(listener);
  }

  @Test
  public void fireWithNoListenersDoesNotThrow() {
    AstEventManager.fireTypeHierarchyListeners();
  }

  @Test
  public void multipleListenersAllFired() {
    AtomicInteger count1 = new AtomicInteger(0);
    AtomicInteger count2 = new AtomicInteger(0);
    AstEventManager.TypeHierarchyListener l1 = count1::incrementAndGet;
    AstEventManager.TypeHierarchyListener l2 = count2::incrementAndGet;
    AstEventManager.addTypeHierarchyListener(l1);
    AstEventManager.addTypeHierarchyListener(l2);
    AstEventManager.fireTypeHierarchyListeners();
    assertEquals(1, count1.get());
    assertEquals(1, count2.get());
    AstEventManager.removeTypeHierarchyListener(l1);
    AstEventManager.removeTypeHierarchyListener(l2);
  }
}
