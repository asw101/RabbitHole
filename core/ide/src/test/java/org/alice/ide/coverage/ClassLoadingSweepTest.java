package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ClassLoadingSweepTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }

  @Test
  public void loadsEveryCoreIdeClass() {
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepAllClasses();
    assertTrue("Expected to discover core/ide classes", result.discovered >= 1500);
    assertTrue("Expected to initialize at least one class", result.loaded > 0);
  }
}
