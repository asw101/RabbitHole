package org.alice.stageide.sceneeditor;

import org.junit.Test;
import org.lgna.project.ast.AstMethodLookupHelpers;
import org.lgna.project.ast.AstUtilities;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.UserField;
import org.lgna.story.MutableRider;
import org.lgna.story.SThing;

import static org.junit.Assert.*;

public class SceneEditorLifecycleManagerDeepBehaviorTest {
  @Test
  public void useSceneAsVehicleLeavesExplicitVehicleUntouched() {
    UserField rider = new UserField("rider", Object.class);
    Expression explicitVehicle = new ThisExpression();
    MethodInvocation invocation = AstUtilities.createMethodInvocation(
        new FieldAccess(rider),
        AstMethodLookupHelpers.lookupMethod(MutableRider.class, "setVehicle", (Class<?>) SThing.class),
        explicitVehicle);

    assertFalse(SceneEditorLifecycleManagerLogic.usesNullVehicleArgument(invocation));
    SceneEditorLifecycleManagerLogic.useSceneAsVehicle(invocation);
    assertSame(explicitVehicle, invocation.requiredArguments.get(0).expression.getValue());
  }
}
