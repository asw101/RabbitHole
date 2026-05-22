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
}
