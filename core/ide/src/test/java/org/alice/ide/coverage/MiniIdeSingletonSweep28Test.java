package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MiniIdeSingletonSweep28Test {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Test
  public void loadsAndExercisesAssignedSingletonDependentClasses() {
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepNamedClasses(
        "org.alice.stageide.sceneeditor.side.ObjectPropertiesToolPalette",
        "org.alice.stageide.sceneeditor.side.edits.MarkerColorIdEdit",
        "org.alice.stageide.sceneeditor.side.views.SideView",
        "org.alice.stageide.sceneeditor.viewmanager.CameraMarkerTracker",
        "org.alice.stageide.sceneeditor.viewmanager.MarkerFieldTile",
        "org.alice.stageide.sceneeditor.viewmanager.MarkerUtilities",
        "org.alice.stageide.sceneeditor.viewmanager.MoveActiveCameraToMarkerActionOperation"
    );

    assertEquals(result.summary(), 7, result.discovered);
    assertEquals(result.summary(), result.discovered, result.loaded);
    assertTrue(result.summary(), result.classLoadFailures.isEmpty());
  }
}
