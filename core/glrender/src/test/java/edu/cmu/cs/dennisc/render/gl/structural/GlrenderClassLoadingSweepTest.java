package edu.cmu.cs.dennisc.render.gl.structural;

import edu.cmu.cs.dennisc.render.gl.ClassLoadingSweepSupport;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class GlrenderClassLoadingSweepTest {

  @Test
  public void loadsEntireGlrenderSourceTreeAndExercisesClassLoadingPaths() throws Exception {
    ClassLoadingSweepSupport.SweepStats stats = ClassLoadingSweepSupport.sweepModuleSourceTree();
    String summary = "attempted=" + stats.getAttemptedClassCount()
        + ", loaded=" + stats.getLoadedClassCount()
        + ", instantiated=" + stats.getInstantiatedClassCount()
        + ", enums=" + stats.getEnumExerciseCount()
        + ", staticFields=" + stats.getStaticFieldAccessCount()
        + ", staticMethods=" + stats.getStaticMethodCallCount();

    assertTrue(summary, stats.getAttemptedClassCount() >= 130);
    assertTrue(summary, stats.getLoadedClassCount() >= 70);
    assertTrue(summary, stats.getInstantiatedClassCount() >= 20);
    assertTrue(summary, stats.getEnumExerciseCount() >= 1);
    assertTrue(summary, stats.getStaticFieldAccessCount() >= 1);
    assertTrue(summary, stats.getStaticMethodCallCount() >= 1);
  }
}
