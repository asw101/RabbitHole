package edu.cmu.cs.dennisc.render.gl.structural;

import org.junit.Test;

public class GlrenderStructuralSweep15Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    edu.cmu.cs.dennisc.render.gl.ReflectionCoverageSupport.inspectClasses(
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrLight",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrLineArray",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrLineLoop"
    );
  }
}
