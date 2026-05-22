package edu.cmu.cs.dennisc.render.gl.structural;

import org.junit.Test;

public class GlrenderStructuralSweep08Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    edu.cmu.cs.dennisc.render.gl.ReflectionCoverageSupport.inspectClasses(
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrBackground",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrBox",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrBufferedImageTexture",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrClippingPlane"
    );
  }
}
