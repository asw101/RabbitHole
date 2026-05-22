package edu.cmu.cs.dennisc.render.gl.structural;

import org.junit.Test;

public class GlrenderStructuralSweep12Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    edu.cmu.cs.dennisc.render.gl.ReflectionCoverageSupport.inspectClasses(
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrFog",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrGeometry",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrGhost",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrGraphic"
    );
  }
}
