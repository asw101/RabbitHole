package edu.cmu.cs.dennisc.render.gl.structural;

import org.junit.Test;

public class GlrenderStructuralSweep13Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    edu.cmu.cs.dennisc.render.gl.ReflectionCoverageSupport.inspectClasses(
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrHorizontalSurface",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrIndexedPolygonArray",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrIndexedQuadrilateralArray"
    );
  }
}
