package org.alice.stageide.sceneeditor;

import org.alice.math.immutable.AffineMatrix4x4;
import org.junit.Test;
import org.lgna.project.ast.MethodInvocation;

import static org.junit.Assert.*;

public class SceneEditorFieldManagerDeepBehaviorTest {
  @Test
  public void getSelectionRequestTreatsMethodInvocationAsExpression() {
    assertEquals(SceneEditorFieldManagerLogic.SelectionRequest.EXPRESSION,
        SceneEditorFieldManagerLogic.getSelectionRequest(new MethodInvocation(), true));
  }

  @Test
  public void getTransformForNewObjectMarkerReturnsProvidedTransform() {
    AffineMatrix4x4 transform = AffineMatrix4x4.createTranslation(1.0, 2.0, 3.0);

    assertSame(transform, SceneEditorFieldManagerLogic.getTransformForNewObjectMarker(transform));
  }
}
