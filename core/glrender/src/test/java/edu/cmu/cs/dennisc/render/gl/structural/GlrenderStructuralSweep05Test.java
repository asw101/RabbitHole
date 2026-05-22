package edu.cmu.cs.dennisc.render.gl.structural;

import org.junit.Test;

public class GlrenderStructuralSweep05Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    edu.cmu.cs.dennisc.render.gl.ReflectionCoverageSupport.inspectClasses(
        "edu.cmu.cs.dennisc.render.gl.imp.SynchronousImageCapturer",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.AdapterFactory",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.ChangeHandler"
    );
  }
}
