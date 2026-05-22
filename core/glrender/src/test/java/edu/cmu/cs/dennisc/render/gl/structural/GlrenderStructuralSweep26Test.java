package edu.cmu.cs.dennisc.render.gl.structural;

import org.junit.Test;

public class GlrenderStructuralSweep26Test {
  @Test
  public void loadsAndInspectsAssignedClasses() throws Exception {
    edu.cmu.cs.dennisc.render.gl.ReflectionCoverageSupport.inspectClasses(
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrWeightedMesh",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.WeightedMeshControl",
        "edu.cmu.cs.dennisc.render.gl.imp.adapters.adorn.GlrAdornment"
    );
  }
}
