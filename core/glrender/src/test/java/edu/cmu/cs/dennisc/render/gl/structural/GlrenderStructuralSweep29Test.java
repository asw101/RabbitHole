package edu.cmu.cs.dennisc.render.gl.structural;

import org.junit.Test;

public class GlrenderStructuralSweep29Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    edu.cmu.cs.dennisc.render.gl.ReflectionCoverageSupport.inspectClasses(
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.graphics.GlrSubtitle",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.graphics.GlrText",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.graphics.GlrThoughtBubble",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.graphics.GlrTitle"
    );
  }
}
