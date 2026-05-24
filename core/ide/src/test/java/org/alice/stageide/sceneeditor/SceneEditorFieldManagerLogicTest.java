package org.alice.stageide.sceneeditor;

import org.alice.ide.ReasonToDisableSomeAmountOfRendering;
import org.alice.math.immutable.AffineMatrix4x4;
import org.junit.Test;
import org.lgna.project.ast.ArrayAccess;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.UserField;

import static org.junit.Assert.*;

public class SceneEditorFieldManagerLogicTest {
  @Test
  public void getSelectionRequestDistinguishesFieldExpressionAndScene() {
    UserField field = new UserField("ship", Object.class);

    assertEquals(SceneEditorFieldManagerLogic.SelectionRequest.FIELD,
        SceneEditorFieldManagerLogic.getSelectionRequest(new FieldAccess(field), true));
    assertEquals(SceneEditorFieldManagerLogic.SelectionRequest.EXPRESSION,
        SceneEditorFieldManagerLogic.getSelectionRequest(new ArrayAccess(JavaType.getInstance(Object[].class), new FieldAccess(field), new org.lgna.project.ast.IntegerLiteral(0)), true));
    assertEquals(SceneEditorFieldManagerLogic.SelectionRequest.ACTIVE_SCENE,
        SceneEditorFieldManagerLogic.getSelectionRequest(new ThisExpression(), true));
    assertEquals(SceneEditorFieldManagerLogic.SelectionRequest.NONE,
        SceneEditorFieldManagerLogic.getSelectionRequest(new ThisExpression(), false));
  }

  @Test
  public void shouldToggleRenderingOnlyForSceneEditorSpecificReasons() {
    assertTrue(SceneEditorFieldManagerLogic.shouldToggleRendering(ReasonToDisableSomeAmountOfRendering.MODAL_DIALOG_WITH_RENDER_WINDOW_OF_ITS_OWN));
    assertTrue(SceneEditorFieldManagerLogic.shouldToggleRendering(ReasonToDisableSomeAmountOfRendering.CLICK_AND_CLACK));
    assertFalse(SceneEditorFieldManagerLogic.shouldToggleRendering(ReasonToDisableSomeAmountOfRendering.DRAG_AND_DROP));
  }

  @Test
  public void getTransformForNewObjectMarkerFallsBackToIdentity() {
    assertSame(AffineMatrix4x4.IDENTITY, SceneEditorFieldManagerLogic.getTransformForNewObjectMarker(null));
  }
}
