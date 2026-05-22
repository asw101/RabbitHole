package org.alice.ide.coverage;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MiniIdeSingletonSweep27Test {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  @Test
  public void loadsAndExercisesAssignedSingletonDependentClasses() {
    ClassLoadingSweepSupport.SweepResult result = ClassLoadingSweepSupport.sweepNamedClasses(
        "org.alice.stageide.sceneeditor.StorytellingSceneEditor",
        "org.alice.stageide.sceneeditor.draganddrop.SceneDropSite",
        "org.alice.stageide.sceneeditor.interact.manipulators.CopyObjectDragManipulator",
        "org.alice.stageide.sceneeditor.interact.manipulators.GetAGoodLookAtManipulator",
        "org.alice.stageide.sceneeditor.side.AddCameraMarkerFieldComposite",
        "org.alice.stageide.sceneeditor.side.AddMarkerFieldComposite",
        "org.alice.stageide.sceneeditor.side.AddObjectMarkerFieldComposite"
    );

    assertEquals(result.summary(), 7, result.discovered);
    assertEquals(result.summary(), result.discovered, result.loaded);
    assertTrue(result.summary(), result.classLoadFailures.isEmpty());
  }
}
