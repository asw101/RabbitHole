package org.alice.stageide.sceneeditor;

import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.NullLiteral;
import org.lgna.project.ast.SimpleArgument;
import org.lgna.project.ast.ThisExpression;

import java.util.ArrayList;

final class SceneEditorLifecycleManagerLogic {
  private SceneEditorLifecycleManagerLogic() {
    throw new AssertionError();
  }

  static boolean usesNullVehicleArgument(MethodInvocation setVehicleCall) {
    ArrayList<SimpleArgument> args = setVehicleCall.requiredArguments.getValue();
    return (args.size() == 1) && (args.get(0).expression.getValue() instanceof NullLiteral);
  }

  static void useSceneAsVehicle(MethodInvocation setVehicleCall) {
    if (usesNullVehicleArgument(setVehicleCall)) {
      setVehicleCall.requiredArguments.get(0).expression.setValue(new ThisExpression());
    }
  }
}
