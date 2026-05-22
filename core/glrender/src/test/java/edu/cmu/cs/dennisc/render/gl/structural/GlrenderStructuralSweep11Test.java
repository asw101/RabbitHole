package edu.cmu.cs.dennisc.render.gl.structural;

import org.junit.Test;

public class GlrenderStructuralSweep11Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    edu.cmu.cs.dennisc.render.gl.ReflectionCoverageSupport.inspectClasses(
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrElement",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrExponentialFog",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrExponentialSquaredFog"
    );
  }
}
