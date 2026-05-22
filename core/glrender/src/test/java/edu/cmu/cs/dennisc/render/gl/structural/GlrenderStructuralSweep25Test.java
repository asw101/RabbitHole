package edu.cmu.cs.dennisc.render.gl.structural;

import org.junit.Test;

public class GlrenderStructuralSweep25Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    edu.cmu.cs.dennisc.render.gl.ReflectionCoverageSupport.inspectClasses(
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrTriangleFan",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrTriangleStrip",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrVertexGeometry",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrVisual"
    );
  }
}
