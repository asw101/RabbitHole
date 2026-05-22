package edu.cmu.cs.dennisc.render.gl.structural;

import org.junit.Test;

public class GlrenderStructuralSweep18Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    edu.cmu.cs.dennisc.render.gl.ReflectionCoverageSupport.inspectClasses(
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrPointArray",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrPointLight",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrQuadArray"
    );
  }
}
