package edu.cmu.cs.dennisc.render.gl.structural;

import org.junit.Test;
import org.junit.Assume;
import java.awt.GraphicsEnvironment;

public class GlrenderStructuralSweep10Test {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    edu.cmu.cs.dennisc.render.gl.ReflectionCoverageSupport.inspectClasses(
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrCustomTexture",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrCylinder",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrDirectionalLight",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrDisc"
    );
  }
}
