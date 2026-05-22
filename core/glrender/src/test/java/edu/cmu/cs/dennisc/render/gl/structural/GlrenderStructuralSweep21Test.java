package edu.cmu.cs.dennisc.render.gl.structural;

import org.junit.Test;

public class GlrenderStructuralSweep21Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    edu.cmu.cs.dennisc.render.gl.ReflectionCoverageSupport.inspectClasses(
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrSkeletonVisual",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrSphere",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrSpotLight",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrSprite"
    );
  }
}
