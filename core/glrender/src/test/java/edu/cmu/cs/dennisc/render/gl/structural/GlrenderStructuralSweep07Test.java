package edu.cmu.cs.dennisc.render.gl.structural;

import org.junit.Test;

public class GlrenderStructuralSweep07Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    edu.cmu.cs.dennisc.render.gl.ReflectionCoverageSupport.inspectClasses(
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrAffector",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrAmbientLight",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrAppearance"
    );
  }
}
