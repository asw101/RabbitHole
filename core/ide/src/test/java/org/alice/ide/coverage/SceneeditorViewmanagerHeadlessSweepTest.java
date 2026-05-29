package org.alice.ide.coverage;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import static org.junit.Assert.assertTrue;

public class SceneeditorViewmanagerHeadlessSweepTest {

  @Rule
  public Timeout globalTimeout = Timeout.seconds(120);

  @Test
  public void exerciseViewmanagerClasses() {
    // CameraMarkerConfiguration subclasses and CameraMarkerTracker excluded:
    // trigger OpenGL/StorytellingSceneEditor initialization which hangs
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise(
        "org.alice.stageide.sceneeditor.viewmanager.CameraFieldAndMarker",
        "org.alice.stageide.sceneeditor.viewmanager.MarkerUtilities",
        "org.alice.stageide.sceneeditor.viewmanager.MarkerUtilitiesHelper",
        "org.alice.stageide.sceneeditor.viewmanager.edits.MoveAndOrientToEdit",
        "org.alice.stageide.sceneeditor.viewmanager.edits.MoveTransformableEdit"
    );
    assertTrue("Should load at least 2 classes, loaded=" + stats.getLoadedCount(),
        stats.getLoadedCount() >= 2);
  }
}
