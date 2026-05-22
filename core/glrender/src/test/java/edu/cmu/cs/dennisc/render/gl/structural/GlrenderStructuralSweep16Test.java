package edu.cmu.cs.dennisc.render.gl.structural;

import org.junit.Test;

public class GlrenderStructuralSweep16Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    edu.cmu.cs.dennisc.render.gl.ReflectionCoverageSupport.inspectClasses(
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrLineStrip",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrLinearFog",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrMesh"
    );
  }
}
