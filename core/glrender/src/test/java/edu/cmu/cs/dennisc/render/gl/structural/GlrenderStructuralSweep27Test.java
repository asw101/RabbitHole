package edu.cmu.cs.dennisc.render.gl.structural;

import org.junit.Test;
import org.junit.Assume;
import java.awt.GraphicsEnvironment;

public class GlrenderStructuralSweep27Test {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
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
