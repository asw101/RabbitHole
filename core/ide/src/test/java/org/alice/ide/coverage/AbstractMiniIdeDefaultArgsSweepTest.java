package org.alice.ide.coverage;

import org.alice.ide.testing.TestIdeBootstrap;
import org.junit.Rule;
import org.junit.rules.Timeout;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.rules.Timeout;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.Timeout;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;

import static org.junit.Assert.assertEquals;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.rules.Timeout;

public abstract class AbstractMiniIdeDefaultArgsSweepTest {

  @Rule
  public Timeout globalTimeout = Timeout.seconds(60);
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
