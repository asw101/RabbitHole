package edu.cmu.cs.dennisc.render.gl.structural;

import edu.cmu.cs.dennisc.render.gl.ClassLoadingSweepSupport;
import org.junit.Assume;
import java.awt.GraphicsEnvironment;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class GlrenderImpPackageClassLoadingSweepTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Test
  public void loadsGlrenderImpPackage() throws Exception {
    ClassLoadingSweepSupport.SweepStats stats = ClassLoadingSweepSupport.sweepModuleSourceClassesWithPrefix("edu.cmu.cs.dennisc.render.gl.imp.");
    String summary = "attempted=" + stats.getAttemptedClassCount()
        + ", loaded=" + stats.getLoadedClassCount()
        + ", instantiated=" + stats.getInstantiatedClassCount()
        + ", staticFields=" + stats.getStaticFieldAccessCount()
        + ", failures=" + stats.getFailures();

    assertTrue(summary, stats.getAttemptedClassCount() >= 25);
    assertTrue(summary, stats.getLoadedClassCount() >= 18);
    assertTrue(summary, stats.getInstantiatedClassCount() >= 10);
  }
}
