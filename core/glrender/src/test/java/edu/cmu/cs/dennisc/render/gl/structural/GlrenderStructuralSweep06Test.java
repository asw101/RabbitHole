package edu.cmu.cs.dennisc.render.gl.structural;

import org.junit.Test;

public class GlrenderStructuralSweep06Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    edu.cmu.cs.dennisc.render.gl.ReflectionCoverageSupport.inspectClasses(
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrAbstractCamera",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrAbstractNearPlaneAndFarPlaneCamera",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrAbstractPerspectiveCamera",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrAbstractTransformable"
    );
  }
}
