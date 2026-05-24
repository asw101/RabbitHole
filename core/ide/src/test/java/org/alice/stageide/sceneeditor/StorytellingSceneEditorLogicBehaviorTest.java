package org.alice.stageide.sceneeditor;

import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.story.SCameraMarker;
import org.lgna.story.SModel;
import org.lgna.story.SThingMarker;
import org.lgna.story.SVRHand;
import org.lgna.story.SVRHeadset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class StorytellingSceneEditorLogicBehaviorTest {
  @Test
  public void isSelectableTypeRejectsMarkerAndVrTypes() {
    assertFalse(StorytellingSceneEditorLogic.isSelectableType(JavaType.getInstance(SThingMarker.class)));
    assertFalse(StorytellingSceneEditorLogic.isSelectableType(JavaType.getInstance(SCameraMarker.class)));
    assertFalse(StorytellingSceneEditorLogic.isSelectableType(JavaType.getInstance(SVRHand.class)));
    assertFalse(StorytellingSceneEditorLogic.isSelectableType(JavaType.getInstance(SVRHeadset.class)));
    assertTrue(StorytellingSceneEditorLogic.isSelectableType(JavaType.getInstance(SModel.class)));
  }

  @Test
  public void createExpandContractPlanReflectsExpandedState() {
    StorytellingSceneEditorLogic.ExpandContractPlan collapsed = StorytellingSceneEditorLogic.createExpandContractPlan(false);
    StorytellingSceneEditorLogic.ExpandContractPlan expanded = StorytellingSceneEditorLogic.createExpandContractPlan(true);

    assertFalse(collapsed.showSelectionPanel);
    assertFalse(collapsed.showContractButton);
    assertFalse(collapsed.showNavigator);
    assertFalse(collapsed.restoreSavedSelection);
    assertEquals(CameraOption.STARTING_CAMERA_VIEW, collapsed.forcedSelection);

    assertTrue(expanded.showSelectionPanel);
    assertTrue(expanded.showContractButton);
    assertTrue(expanded.showNavigator);
    assertTrue(expanded.restoreSavedSelection);
    assertNull(expanded.forcedSelection);
  }

  @Test
  public void shouldRestoreSceneCameraTransformRequiresMovableNonStartingView() {
    assertFalse(StorytellingSceneEditorLogic.shouldRestoreSceneCameraTransform(false, CameraOption.TOP));
    assertFalse(StorytellingSceneEditorLogic.shouldRestoreSceneCameraTransform(true, CameraOption.STARTING_CAMERA_VIEW));
    assertTrue(StorytellingSceneEditorLogic.shouldRestoreSceneCameraTransform(true, CameraOption.TOP));
  }
}
