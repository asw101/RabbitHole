package org.alice.stageide.sceneeditor;

import org.junit.Test;
import org.lgna.project.ast.AstMethodLookupHelpers;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.UserField;
import org.lgna.story.MutableRider;
import org.lgna.story.SThing;

import static org.junit.Assert.*;

public class SceneEditorLifecycleManagerLogicTest {
  @Test
  public void useSceneAsVehicleRewritesNullVehicleArgument() {
    UserField rider = new UserField("rider", Object.class);
    MethodInvocation invocation = AstUtilities.createMethodInvocation(new FieldAccess(rider), AstMethodLookupHelpers.lookupMethod(MutableRider.class, "setVehicle", (Class<?>) SThing.class), new org.lgna.project.ast.NullLiteral());

    assertTrue(SceneEditorLifecycleManagerLogic.usesNullVehicleArgument(invocation));
    SceneEditorLifecycleManagerLogic.useSceneAsVehicle(invocation);
    assertTrue(invocation.requiredArguments.get(0).expression.getValue() instanceof ThisExpression);
  }
}
