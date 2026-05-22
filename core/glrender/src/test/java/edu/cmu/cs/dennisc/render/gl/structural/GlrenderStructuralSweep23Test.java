package edu.cmu.cs.dennisc.render.gl.structural;

import org.junit.Test;

public class GlrenderStructuralSweep23Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    edu.cmu.cs.dennisc.render.gl.ReflectionCoverageSupport.inspectClasses(
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrTexture",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrTexturedAppearance",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrTexturedVisual",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrTorus"
    );
  }
}
