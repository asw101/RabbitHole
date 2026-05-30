package org.alice.ide.coverage;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import static org.junit.Assert.assertTrue;

public class SceneeditorLogicHeadlessSweepTest {

  @Rule
  public Timeout globalTimeout = Timeout.seconds(120);

  @Test
  public void exerciseSceneeditorTopLevelLogicClasses() {
    // ThumbnailGenerator excluded: triggers OpenGL native library init (blocks)
    // Heavy IDE singletons excluded: require full IDE initialization
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise(
        "org.alice.stageide.sceneeditor.CameraOption",
        "org.alice.stageide.sceneeditor.FieldRegistry",
        "org.alice.stageide.sceneeditor.SceneEditorFieldManagerLogic",
        "org.alice.stageide.sceneeditor.SceneEditorLifecycleManagerLogic",
        "org.alice.stageide.sceneeditor.SceneFieldCodeGeneratorLogic",
        "org.alice.stageide.sceneeditor.SetUpMethodGeneratorLogic",
        "org.alice.stageide.sceneeditor.ShowJointedModelJointAxesState",
        "org.alice.stageide.sceneeditor.StorytellingSceneEditorLogic"
    );
    assertTrue("Should load at least 3 classes, loaded=" + stats.getLoadedCount(),
        stats.getLoadedCount() >= 3);
  }

  @Test
  public void exerciseSceneeditorSnapAndDragDrop() {
    // SnapState excluded: getSideComposite() triggers TreeUtilities.<clinit> → modal dialog
    HeadlessClassExerciseSupport.SmokeStats stats = HeadlessClassExerciseSupport.exercise(
        "org.alice.stageide.sceneeditor.draganddrop.SceneDropSite"
    );
    assertTrue("Should load at least 1 class, loaded=" + stats.getLoadedCount(),
        stats.getLoadedCount() >= 1);
  }
}
