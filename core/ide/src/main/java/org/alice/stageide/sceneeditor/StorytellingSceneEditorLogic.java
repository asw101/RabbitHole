package org.alice.stageide.sceneeditor;

import org.lgna.project.ast.AbstractType;
import org.lgna.story.SCameraMarker;
import org.lgna.story.SThingMarker;
import org.lgna.story.SVRHand;
import org.lgna.story.SVRHeadset;

final class StorytellingSceneEditorLogic {
  enum InstanceFactorySelection {
    SCENE,
    FIELD
  }

  static final class ExpandContractPlan {
    final boolean showSelectionPanel;
    final boolean showContractButton;
    final boolean showNavigator;
    final boolean restoreSavedSelection;
    final CameraOption forcedSelection;

    private ExpandContractPlan(boolean showSelectionPanel, boolean showContractButton, boolean showNavigator,
                               boolean restoreSavedSelection, CameraOption forcedSelection) {
      this.showSelectionPanel = showSelectionPanel;
      this.showContractButton = showContractButton;
      this.showNavigator = showNavigator;
      this.restoreSavedSelection = restoreSavedSelection;
      this.forcedSelection = forcedSelection;
    }
  }

  private StorytellingSceneEditorLogic() {
    throw new AssertionError();
  }

  static boolean isSelectableType(AbstractType<?, ?, ?> valueType) {
    return !valueType.isAssignableFrom(SThingMarker.class)
        && !valueType.isAssignableFrom(SCameraMarker.class)
        && !valueType.isAssignableFrom(SVRHand.class)
        && !valueType.isAssignableFrom(SVRHeadset.class);
  }

  static ExpandContractPlan createExpandContractPlan(boolean isExpanded) {
    return isExpanded
        ? new ExpandContractPlan(true, true, true, true, null)
        : new ExpandContractPlan(false, false, false, false, CameraOption.STARTING_CAMERA_VIEW);
  }

  static InstanceFactorySelection determineInstanceFactorySelection(boolean selectedFieldIsActiveScene) {
    return selectedFieldIsActiveScene ? InstanceFactorySelection.SCENE : InstanceFactorySelection.FIELD;
  }

  static boolean shouldRestoreSceneCameraTransform(boolean isMovableSceneCamera, CameraOption selectedView) {
    return isMovableSceneCamera && (selectedView != CameraOption.STARTING_CAMERA_VIEW);
  }
}
