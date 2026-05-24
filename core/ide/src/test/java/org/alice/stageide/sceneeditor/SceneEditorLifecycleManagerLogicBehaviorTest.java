package org.alice.stageide.sceneeditor;

import org.junit.Test;
import org.lgna.project.ast.AstMethodLookupHelpers;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.UserField;
import org.lgna.story.MutableRider;
import org.lgna.story.SThing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class SceneEditorLifecycleManagerLogicBehaviorTest {
  private static MethodInvocation setVehicleCall(Expression vehicleExpression) {
    UserField rider = new UserField("rider", Object.class);
    return AstUtilities.createMethodInvocation(
        new FieldAccess(rider),
        AstMethodLookupHelpers.lookupMethod(MutableRider.class, "setVehicle", (Class<?>) SThing.class),
        vehicleExpression);
  }

  @Test
  public void usesNullVehicleArgumentRecognizesSingleNullArgumentOnly() {
    assertTrue(SceneEditorLifecycleManagerLogic.usesNullVehicleArgument(setVehicleCall(new NullLiteral())));
    assertFalse(SceneEditorLifecycleManagerLogic.usesNullVehicleArgument(setVehicleCall(new ThisExpression())));
  }

  @Test
  public void useSceneAsVehicleRewritesNullArgumentsAndLeavesExistingTargetsAlone() {
    MethodInvocation nullCall = setVehicleCall(new NullLiteral());
    MethodInvocation existingCall = setVehicleCall(new FieldAccess(new UserField("vehicle", Object.class)));
    Expression originalExpression = existingCall.requiredArguments.get(0).expression.getValue();

    SceneEditorLifecycleManagerLogic.useSceneAsVehicle(nullCall);
    SceneEditorLifecycleManagerLogic.useSceneAsVehicle(existingCall);

    assertTrue(nullCall.requiredArguments.get(0).expression.getValue() instanceof ThisExpression);
    assertSame(originalExpression, existingCall.requiredArguments.get(0).expression.getValue());
  }
}
