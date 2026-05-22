package edu.cmu.cs.dennisc.render.gl.structural;

import org.junit.Test;

public class GlrenderStructuralSweep24Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    edu.cmu.cs.dennisc.render.gl.ReflectionCoverageSupport.inspectClasses(
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrTransformable",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrTransformableVisual",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrTriangleArray"
    );
  }
}
