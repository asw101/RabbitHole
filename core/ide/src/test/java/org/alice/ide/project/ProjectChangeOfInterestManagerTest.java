package org.alice.ide.project;

import org.alice.ide.project.events.ProjectChangeOfInterestListener;
import org.alice.ide.testing.TestIdeBootstrap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class ProjectChangeOfInterestManagerTest {
  @Before
  public void setUpIdeContext() {
    TestIdeBootstrap.ensureInstalled();
  }

  @After
  public void tearDownIdeContext() {
    TestIdeBootstrap.reset();
  }

  @Test
  public void singletonEnumConstantExists() {
    assertSame(ProjectChangeOfInterestManager.SINGLETON, ProjectChangeOfInterestManager.valueOf("SINGLETON"));
  }

  @Test
  public void fireProjectChangeListenersInvokesRegisteredListenerAndStopsAfterRemoval() {
    AtomicInteger callCount = new AtomicInteger();
    ProjectChangeOfInterestListener listener = callCount::incrementAndGet;

    ProjectChangeOfInterestManager.SINGLETON.addProjectChangeOfInterestListener(listener);
    try {
      ProjectChangeOfInterestManager.SINGLETON.fireProjectChangeOfInterestListeners();
      assertEquals(1, callCount.get());
    } finally {
      ProjectChangeOfInterestManager.SINGLETON.removeProjectChangeOfInterestListener(listener);
    }

    ProjectChangeOfInterestManager.SINGLETON.fireProjectChangeOfInterestListeners();
    assertEquals(1, callCount.get());
  }

  @Test
  public void duplicateListenerRegistrationRequiresMatchingNumberOfRemovals() {
    AtomicInteger callCount = new AtomicInteger();
    ProjectChangeOfInterestListener listener = callCount::incrementAndGet;

    ProjectChangeOfInterestManager.SINGLETON.addProjectChangeOfInterestListener(listener);
    ProjectChangeOfInterestManager.SINGLETON.addProjectChangeOfInterestListener(listener);
    try {
      ProjectChangeOfInterestManager.SINGLETON.fireProjectChangeOfInterestListeners();
      assertEquals(2, callCount.get());

      ProjectChangeOfInterestManager.SINGLETON.removeProjectChangeOfInterestListener(listener);
      ProjectChangeOfInterestManager.SINGLETON.fireProjectChangeOfInterestListeners();
      assertEquals(3, callCount.get());
    } finally {
      ProjectChangeOfInterestManager.SINGLETON.removeProjectChangeOfInterestListener(listener);
    }
  }

  @Test
  public void copyOnWriteIterationStillNotifiesSnapshotWhenListenerRemovesAnotherListener() {
    StringBuilder order = new StringBuilder();
    ProjectChangeOfInterestListener[] secondHolder = new ProjectChangeOfInterestListener[1];
    ProjectChangeOfInterestListener first = () -> {
      order.append('A');
      ProjectChangeOfInterestManager.SINGLETON.removeProjectChangeOfInterestListener(secondHolder[0]);
    };
    ProjectChangeOfInterestListener second = () -> order.append('B');
    secondHolder[0] = second;

    ProjectChangeOfInterestManager.SINGLETON.addProjectChangeOfInterestListener(first);
    ProjectChangeOfInterestManager.SINGLETON.addProjectChangeOfInterestListener(second);
    try {
      ProjectChangeOfInterestManager.SINGLETON.fireProjectChangeOfInterestListeners();
      assertEquals("AB", order.toString());

      ProjectChangeOfInterestManager.SINGLETON.fireProjectChangeOfInterestListeners();
      assertEquals("ABA", order.toString());
    } finally {
      ProjectChangeOfInterestManager.SINGLETON.removeProjectChangeOfInterestListener(first);
      ProjectChangeOfInterestManager.SINGLETON.removeProjectChangeOfInterestListener(second);
    }
  }
}
