package org.alice.stageide.oneshot;

import org.alice.stageide.ast.sort.OneShotSorter;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.ManagementLevel;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.Statement;
import org.lgna.project.ast.UserMethod;

final class MethodInvocationBlankLogic {
  enum FillInKind {
    STRAIGHTEN_OUT_JOINTS,
    SET_PAINT,
    ROOM,
    SET_OPACITY,
    JAVA_DEFINED_STRIKE_POSE,
    LOCAL_TRANSFORMATION
  }

  private MethodInvocationBlankLogic() {
    throw new AssertionError();
  }

  static MethodInvocation getPoseInvocation(UserMethod userMethod) {
    if ((userMethod.managementLevel.getValue() == ManagementLevel.GENERATED)
        && (userMethod.getReturnType() == JavaType.VOID_TYPE)
        && !userMethod.body.getValue().statements.isEmpty()) {
      Statement poseStatement = userMethod.body.getValue().statements.get(0);
      if (poseStatement instanceof ExpressionStatement expressionStatement) {
        Expression expression = expressionStatement.expression.getValue();
        if (expression instanceof MethodInvocation poseInvocation
            && "strikePose".equals(poseInvocation.method.getValue().getName())
            && (poseInvocation.method.getValue() instanceof JavaMethod)) {
          return poseInvocation;
        }
      }
    }
    return null;
  }

  static FillInKind resolveFillInKind(JavaMethod method, boolean hasRoomFillIn) {
    if (method == OneShotSorter.STRAIGHTEN_OUT_JOINTS_METHOD) {
      return FillInKind.STRAIGHTEN_OUT_JOINTS;
    }
    String methodName = method.getName();
    if ("setPaint".equals(methodName)) {
      return FillInKind.SET_PAINT;
    }
    if (hasRoomFillIn) {
      return FillInKind.ROOM;
    }
    if ("setOpacity".equals(methodName)) {
      return FillInKind.SET_OPACITY;
    }
    if ("foldWings".equals(methodName) || "spreadWings".equals(methodName)) {
      return FillInKind.JAVA_DEFINED_STRIKE_POSE;
    }
    return FillInKind.LOCAL_TRANSFORMATION;
  }
}
