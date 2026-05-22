package edu.cmu.cs.dennisc.render.gl.structural;

import org.junit.Test;

public class GlrenderStructuralSweep14Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    edu.cmu.cs.dennisc.render.gl.ReflectionCoverageSupport.inspectClasses(
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrIndexedTriangleArray",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrJoint",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrLayer",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrLeaf"
    );
  }
}
