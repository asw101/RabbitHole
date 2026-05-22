package edu.cmu.cs.dennisc.render.gl.structural;

import org.junit.Test;

public class GlrenderStructuralSweep01Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    edu.cmu.cs.dennisc.render.gl.ReflectionCoverageSupport.inspectClasses(
        "edu.cmu.cs.dennisc.render.gl.TextureBinding$Data",
        "edu.cmu.cs.dennisc.render.gl.imp.SynchronousPicker$ActualPicker",
        "edu.cmu.cs.dennisc.render.gl.ForgettableBinding"
    );
  }
}
