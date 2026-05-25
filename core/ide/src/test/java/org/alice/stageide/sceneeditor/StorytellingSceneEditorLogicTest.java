package org.alice.stageide.sceneeditor;

import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.story.SModel;
import org.lgna.story.SThingMarker;

import static org.junit.Assert.*;

public class StorytellingSceneEditorLogicTest {
  @Test
  public void isSelectableTypeRejectsMarkers() {
    assertFalse(StorytellingSceneEditorLogic.isSelectableType(JavaType.getInstance(SThingMarker.class)));
    assertTrue(StorytellingSceneEditorLogic.isSelectableType(JavaType.getInstance(SModel.class)));
  }

  @Test
  public void createExpandContractPlanUsesStartingCameraWhenCollapsed() {
    StorytellingSceneEditorLogic.ExpandContractPlan collapsed = StorytellingSceneEditorLogic.createExpandContractPlan(false);
    StorytellingSceneEditorLogic.ExpandContractPlan expanded = StorytellingSceneEditorLogic.createExpandContractPlan(true);

    assertEquals(CameraOption.STARTING_CAMERA_VIEW, collapsed.forcedSelection);
    assertFalse(collapsed.showSelectionPanel);
    assertTrue(expanded.showSelectionPanel);
    assertTrue(expanded.restoreSavedSelection);
  }

  @Test
  public void determineInstanceFactorySelectionUsesSceneForActiveSceneField() {
    assertEquals(StorytellingSceneEditorLogic.InstanceFactorySelection.SCENE,
        StorytellingSceneEditorLogic.determineInstanceFactorySelection(true));
    assertEquals(StorytellingSceneEditorLogic.InstanceFactorySelection.FIELD,
        StorytellingSceneEditorLogic.determineInstanceFactorySelection(false));
  }

  @Test
  public void shouldRestoreSceneCameraTransformOnlyForNonStartingViews() {
    assertFalse(StorytellingSceneEditorLogic.shouldRestoreSceneCameraTransform(false, CameraOption.TOP));
    assertFalse(StorytellingSceneEditorLogic.shouldRestoreSceneCameraTransform(true, CameraOption.STARTING_CAMERA_VIEW));
    assertTrue(StorytellingSceneEditorLogic.shouldRestoreSceneCameraTransform(true, CameraOption.TOP));
  }
}
