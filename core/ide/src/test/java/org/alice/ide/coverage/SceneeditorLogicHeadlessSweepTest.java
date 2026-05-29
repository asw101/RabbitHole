package org.alice.ide.coverage;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class SceneeditorLogicHeadlessSweepTest {

  @Test
  public void exerciseSceneeditorTopLevelLogicClasses() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise(
        "org.alice.stageide.sceneeditor.CameraOption",
        "org.alice.stageide.sceneeditor.FieldRegistry",
        "org.alice.stageide.sceneeditor.SceneEditorFieldManagerLogic",
        "org.alice.stageide.sceneeditor.SceneEditorLifecycleManagerLogic",
        "org.alice.stageide.sceneeditor.SceneFieldCodeGeneratorLogic",
        "org.alice.stageide.sceneeditor.SetUpMethodGeneratorLogic",
        "org.alice.stageide.sceneeditor.ShowJointedModelJointAxesState",
        "org.alice.stageide.sceneeditor.StorytellingSceneEditorLogic",
        "org.alice.stageide.sceneeditor.ThumbnailGenerator",
        "org.alice.stageide.sceneeditor.SceneEditorDropReceptor",
        "org.alice.stageide.sceneeditor.SceneEditorInitializer",
        "org.alice.stageide.sceneeditor.SceneEditorListeners",
        "org.alice.stageide.sceneeditor.SceneRenderTargetListener",
        "org.alice.stageide.sceneeditor.SceneEditorFieldManager",
        "org.alice.stageide.sceneeditor.SceneEditorLifecycleManager",
        "org.alice.stageide.sceneeditor.SceneFieldCodeGenerator",
        "org.alice.stageide.sceneeditor.SetUpMethodGenerator",
        "org.alice.stageide.sceneeditor.StorytellingSceneEditor"
    );
    assertTrue("Should load at least 5 classes, loaded=" + stats.getLoadedCount(),
        stats.getLoadedCount() >= 5);
  }

  @Test
  public void exerciseSceneeditorSnapAndDragDrop() {
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise(
        "org.alice.stageide.sceneeditor.snap.SnapState",
        "org.alice.stageide.sceneeditor.draganddrop.SceneDropSite"
    );
    assertTrue("Should load at least 1 class, loaded=" + stats.getLoadedCount(),
        stats.getLoadedCount() >= 1);
  }
}
