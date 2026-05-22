package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MiniIdeSingletonSweep26Test {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Test
  public void loadsAndExercisesAssignedSingletonDependentClasses() {
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepNamedClasses(
        "org.alice.stageide.properties.MutableRiderVehicleAdapter",
        "org.alice.stageide.raytrace.ExportToPovRayOperation",
        "org.alice.stageide.run.RunComposite",
        "org.alice.stageide.sceneeditor.SceneEditorFieldManager",
        "org.alice.stageide.sceneeditor.SceneEditorInitializer",
        "org.alice.stageide.sceneeditor.SceneFieldCodeGenerator",
        "org.alice.stageide.sceneeditor.SetUpMethodGenerator"
    );

    assertEquals(result.summary(), 7, result.discovered);
    assertEquals(result.summary(), result.discovered, result.loaded);
    assertTrue(result.summary(), result.classLoadFailures.isEmpty());
  }
}
