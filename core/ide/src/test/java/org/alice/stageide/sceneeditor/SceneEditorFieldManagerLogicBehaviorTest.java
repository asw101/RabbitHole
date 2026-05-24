package org.alice.stageide.sceneeditor;

import org.alice.ide.ReasonToDisableSomeAmountOfRendering;
import org.alice.math.immutable.AffineMatrix4x4;
import org.junit.Test;
import org.lgna.project.ast.ArrayAccess;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.StringLiteral;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.UserField;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class SceneEditorFieldManagerLogicBehaviorTest {
  @Test
  public void getSelectionRequestDistinguishesFieldExpressionSceneAndFallbackCases() {
    UserField field = new UserField("ship", Object.class);
    MethodInvocation invocation = new MethodInvocation(new FieldAccess(field), JavaMethod.getInstance(Object.class, "toString"));
    ArrayAccess arrayAccess = new ArrayAccess(JavaType.getInstance(Object[].class), new FieldAccess(field), new IntegerLiteral(0));

    assertEquals(SceneEditorFieldManagerLogic.SelectionRequest.FIELD,
        SceneEditorFieldManagerLogic.getSelectionRequest(new FieldAccess(field), true));
    assertEquals(SceneEditorFieldManagerLogic.SelectionRequest.EXPRESSION,
        SceneEditorFieldManagerLogic.getSelectionRequest(invocation, true));
    assertEquals(SceneEditorFieldManagerLogic.SelectionRequest.EXPRESSION,
        SceneEditorFieldManagerLogic.getSelectionRequest(arrayAccess, true));
    assertEquals(SceneEditorFieldManagerLogic.SelectionRequest.ACTIVE_SCENE,
        SceneEditorFieldManagerLogic.getSelectionRequest(new ThisExpression(), true));
    assertEquals(SceneEditorFieldManagerLogic.SelectionRequest.NONE,
        SceneEditorFieldManagerLogic.getSelectionRequest(new ThisExpression(), false));
    assertEquals(SceneEditorFieldManagerLogic.SelectionRequest.NONE,
        SceneEditorFieldManagerLogic.getSelectionRequest(new StringLiteral("name"), true));
  }

  @Test
  public void shouldToggleRenderingOnlyForSceneEditorSpecificReasons() {
    assertTrue(SceneEditorFieldManagerLogic.shouldToggleRendering(
        ReasonToDisableSomeAmountOfRendering.MODAL_DIALOG_WITH_RENDER_WINDOW_OF_ITS_OWN));
    assertTrue(SceneEditorFieldManagerLogic.shouldToggleRendering(
        ReasonToDisableSomeAmountOfRendering.CLICK_AND_CLACK));
    assertFalse(SceneEditorFieldManagerLogic.shouldToggleRendering(
        ReasonToDisableSomeAmountOfRendering.DRAG_AND_DROP));
  }

  @Test
  public void getTransformForNewObjectMarkerUsesIdentityFallback() {
    AffineMatrix4x4 transform = AffineMatrix4x4.createTranslation(1.0, 2.0, 3.0);

    assertSame(transform, SceneEditorFieldManagerLogic.getTransformForNewObjectMarker(transform));
    assertSame(AffineMatrix4x4.IDENTITY, SceneEditorFieldManagerLogic.getTransformForNewObjectMarker(null));
  }
}
