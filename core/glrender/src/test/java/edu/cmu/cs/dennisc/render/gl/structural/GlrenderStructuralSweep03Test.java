package edu.cmu.cs.dennisc.render.gl.structural;

import org.junit.Test;

public class GlrenderStructuralSweep03Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    edu.cmu.cs.dennisc.render.gl.ReflectionCoverageSupport.inspectClasses(
        "edu.cmu.cs.dennisc.render.gl.imp.CurveRenderer",
        "edu.cmu.cs.dennisc.render.gl.imp.GetUtilities",
        "edu.cmu.cs.dennisc.render.gl.imp.GlrRenderContext"
    );
  }
}
