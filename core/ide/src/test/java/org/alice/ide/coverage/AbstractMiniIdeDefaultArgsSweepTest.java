package org.alice.ide.coverage;

import org.alice.ide.testing.TestIdeBootstrap;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class AbstractMiniIdeDefaultArgsSweepTest {
  @BeforeClass
  public static void boot() {
    TestIdeBootstrap.boot();
  }

  @AfterClass
  public static void shutdown() {
    TestIdeBootstrap.shutdown();
  }

  @Test
  public void loadsAndExercisesAssignedSingletonDependentClasses() {
    String[] classNames = getClassNames();
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepNamedClassesWithDefaultArgs(classNames);

    assertEquals(result.summary(), classNames.length, result.discovered);
    assertEquals(result.summary(), result.discovered, result.loaded);
    assertTrue(result.summary(), result.classLoadFailures.isEmpty());
  }

  protected abstract String[] getClassNames();
}
