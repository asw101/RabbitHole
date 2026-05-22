package edu.cmu.cs.dennisc.render.gl.structural;

import org.junit.Test;

public class GlrenderStructuralSweep17Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    edu.cmu.cs.dennisc.render.gl.ReflectionCoverageSupport.inspectClasses(
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrObject",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrOldMesh",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrOrthographicCamera",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrPlanarReflector"
    );
  }
}
