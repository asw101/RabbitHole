package edu.cmu.cs.dennisc.render.gl.structural;

import org.junit.Test;
import org.junit.Assume;
import java.awt.GraphicsEnvironment;

public class GlrenderStructuralSweep17Test {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    edu.cmu.cs.dennisc.render.gl.ReflectionCoverageSupport.inspectClasses(
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrObject",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrOldMesh",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrOrthographicCamera",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrPlanarReflector"
    );
  }
}
