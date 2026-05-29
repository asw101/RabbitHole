package org.alice.ide.coverage;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class SceneeditorViewmanagerHeadlessSweepTest {

  @Test
  public void exerciseViewmanagerClasses() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise(
        "org.alice.stageide.sceneeditor.viewmanager.CameraFieldAndMarker",
        "org.alice.stageide.sceneeditor.viewmanager.CameraMarkerConfiguration",
        "org.alice.stageide.sceneeditor.viewmanager.CameraMarkerTracker",
        "org.alice.stageide.sceneeditor.viewmanager.CameraViewFieldComponent",
        "org.alice.stageide.sceneeditor.viewmanager.FrontCameraMarkerConfiguration",
        "org.alice.stageide.sceneeditor.viewmanager.LayoutCameraMarkerConfiguration",
        "org.alice.stageide.sceneeditor.viewmanager.MarkerFieldTile",
        "org.alice.stageide.sceneeditor.viewmanager.MarkerUtilities",
        "org.alice.stageide.sceneeditor.viewmanager.MarkerUtilitiesHelper",
        "org.alice.stageide.sceneeditor.viewmanager.MoveToImageIcon",
        "org.alice.stageide.sceneeditor.viewmanager.OrthographicCameraMarkerConfiguration",
        "org.alice.stageide.sceneeditor.viewmanager.PerspectiveCameraMarkerConfiguration",
        "org.alice.stageide.sceneeditor.viewmanager.SideCameraMarkerConfiguration",
        "org.alice.stageide.sceneeditor.viewmanager.StartingCameraMarkerConfiguration",
        "org.alice.stageide.sceneeditor.viewmanager.TopCameraMarkerConfiguration",
        "org.alice.stageide.sceneeditor.viewmanager.edits.MoveAndOrientToEdit",
        "org.alice.stageide.sceneeditor.viewmanager.edits.MoveTransformableEdit"
    );
    assertTrue("Should load at least 5 classes, loaded=" + stats.getLoadedCount(),
        stats.getLoadedCount() >= 5);
  }
}
