package edu.cmu.cs.dennisc.render.gl.structural;

import org.junit.Test;

public class GlrenderStructuralSweep30Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    edu.cmu.cs.dennisc.render.gl.ReflectionCoverageSupport.inspectClasses(
        "edu.cmu.cs.dennisc.render.joglrenderer.DefaultRenderDelegate",
        "edu.cmu.cs.dennisc.render.joglrenderer.NonCachingTextRenderer",
        "edu.cmu.cs.dennisc.system.graphics.ConformanceTestResults"
    );
  }
}
