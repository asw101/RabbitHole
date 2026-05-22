package edu.cmu.cs.dennisc.render.gl.structural;

import org.junit.Test;

public class GlrenderStructuralSweep20Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    edu.cmu.cs.dennisc.render.gl.ReflectionCoverageSupport.inspectClasses(
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrShape",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrSilhouette",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrSimpleAppearance"
    );
  }
}
