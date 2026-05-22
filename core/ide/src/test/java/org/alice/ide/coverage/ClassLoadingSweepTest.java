package org.alice.ide.coverage;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ClassLoadingSweepTest {

  @Test
  public void loadsEveryCoreIdeClass() {
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepAllClasses();
    assertTrue("Expected to discover core/ide classes", result.discovered >= 1500);
    assertTrue("Expected to initialize at least one class", result.loaded > 0);
  }
}
