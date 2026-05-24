package org.alice.stageide.sceneeditor;

import org.alice.ide.ReasonToDisableSomeAmountOfRendering;
import org.alice.math.immutable.AffineMatrix4x4;
import org.lgna.project.ast.ArrayAccess;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.ThisExpression;

final class SceneEditorFieldManagerLogic {
  enum SelectionRequest {
    FIELD,
    EXPRESSION,
    ACTIVE_SCENE,
    NONE
  }

  private SceneEditorFieldManagerLogic() {
    throw new AssertionError();
  }

  static SelectionRequest getSelectionRequest(Expression expression, boolean hasActiveSceneField) {
    if (expression instanceof FieldAccess) {
      return SelectionRequest.FIELD;
    }
    if ((expression instanceof MethodInvocation) || (expression instanceof ArrayAccess)) {
      return SelectionRequest.EXPRESSION;
    }
    if ((expression instanceof ThisExpression) && hasActiveSceneField) {
      return SelectionRequest.ACTIVE_SCENE;
    }
    return SelectionRequest.NONE;
  }

  static boolean shouldToggleRendering(ReasonToDisableSomeAmountOfRendering reason) {
    return (reason == ReasonToDisableSomeAmountOfRendering.MODAL_DIALOG_WITH_RENDER_WINDOW_OF_ITS_OWN)
        || (reason == ReasonToDisableSomeAmountOfRendering.CLICK_AND_CLACK);
  }

  static AffineMatrix4x4 getTransformForNewObjectMarker(AffineMatrix4x4 selectedTransform) {
    return selectedTransform != null ? selectedTransform : AffineMatrix4x4.IDENTITY;
  }
}
