package edu.cmu.cs.dennisc.render.gl.structural;

import org.junit.Test;

public class GlrenderStructuralSweep09Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    edu.cmu.cs.dennisc.render.gl.ReflectionCoverageSupport.inspectClasses(
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrComponent",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrComponentArray",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrComposite"
    );
  }
}
