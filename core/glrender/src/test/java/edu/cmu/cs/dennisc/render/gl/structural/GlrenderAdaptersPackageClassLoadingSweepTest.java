package edu.cmu.cs.dennisc.render.gl.structural;

import edu.cmu.cs.dennisc.render.gl.ClassLoadingSweepSupport;
import org.junit.Assume;
import java.awt.GraphicsEnvironment;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class GlrenderAdaptersPackageClassLoadingSweepTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Test
  public void loadsGlrenderAdapterPackages() throws Exception {
    ClassLoadingSweepSupport.SweepStats stats = ClassLoadingSweepSupport.sweepModuleSourceClassesWithPrefix("edu.cmu.cs.dennisc.render.gl.imp.adapters");
    String summary = "attempted=" + stats.getAttemptedClassCount()
        + ", loaded=" + stats.getLoadedClassCount()
        + ", instantiated=" + stats.getInstantiatedClassCount()
        + ", failures=" + stats.getFailures();

    assertTrue(summary, stats.getAttemptedClassCount() >= 70);
    assertTrue(summary, stats.getLoadedClassCount() >= 55);
    assertTrue(summary, stats.getInstantiatedClassCount() >= 35);
  }
}
