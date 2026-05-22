package edu.cmu.cs.dennisc.render.gl.structural;

import org.junit.Test;

public class GlrenderStructuralSweep19Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    edu.cmu.cs.dennisc.render.gl.ReflectionCoverageSupport.inspectClasses(
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrQuadStrip",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrRenderContributor",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrScalable",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrScene"
    );
  }
}
