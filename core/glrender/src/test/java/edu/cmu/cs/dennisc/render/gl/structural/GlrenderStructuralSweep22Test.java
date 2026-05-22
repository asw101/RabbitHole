package edu.cmu.cs.dennisc.render.gl.structural;

import org.junit.Test;

public class GlrenderStructuralSweep22Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    edu.cmu.cs.dennisc.render.gl.ReflectionCoverageSupport.inspectClasses(
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrStandIn",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrSymmetricPerspectiveCamera",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrText"
    );
  }
}
