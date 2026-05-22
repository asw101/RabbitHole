package edu.cmu.cs.dennisc.render.gl.structural;

import org.junit.Test;

public class GlrenderStructuralSweep10Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    edu.cmu.cs.dennisc.render.gl.ReflectionCoverageSupport.inspectClasses(
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrCustomTexture",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrCylinder",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrDirectionalLight",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrDisc"
    );
  }
}
