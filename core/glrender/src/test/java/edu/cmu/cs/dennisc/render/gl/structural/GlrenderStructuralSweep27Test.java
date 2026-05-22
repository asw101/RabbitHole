package edu.cmu.cs.dennisc.render.gl.structural;

import org.junit.Test;

public class GlrenderStructuralSweep27Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    edu.cmu.cs.dennisc.render.gl.ReflectionCoverageSupport.inspectClasses(
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.adorn.GlrPivotFigure",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.adorn.GlrStickFigure",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.graphics.GlrBubble",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.graphics.GlrMainTitle"
    );
  }
}
