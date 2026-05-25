package org.alice.ide.croquet.models.html;

import org.lgna.project.ast.Expression;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.Lambda;
import org.lgna.project.ast.LambdaExpression;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.SimpleArgument;
import org.lgna.project.ast.Statement;
import org.lgna.project.ast.UserLambda;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.code.ProcessableNode;

import java.util.List;
import java.util.Map;
import java.util.Set;

final class HtmlEncoderLogic {
  private HtmlEncoderLogic() {
    throw new AssertionError();
  }

  static boolean isSectionToInclude(String key, Set<String> sectionsToSkip) {
    return !sectionsToSkip.contains(key);
  }

  static boolean isClassEmpty(Map<String, List<ProcessableNode>> sections, Set<String> sectionsToSkip) {
    for (Map.Entry<String, List<ProcessableNode>> entry : sections.entrySet()) {
      if (isSectionToInclude(entry.getKey(), sectionsToSkip) && !entry.getValue().isEmpty()) {
        for (ProcessableNode item : entry.getValue()) {
          if (!(item instanceof UserMethod) || !((UserMethod) item).getManagementLevel().isGenerated()) {
            return false;
          }
        }
      }
    }
    return true;
  }

  static boolean shouldSkipMethod(UserMethod method) {
    return method.getManagementLevel().isGenerated() || (method.isStatic() && "main".equals(method.getName()));
  }

  static boolean isListenerArgument(SimpleArgument argument) {
    return argument != null
        && argument.parameter.getValue() != null
        && "listener".equals(argument.parameter.getValue().getName());
  }

  static SimpleArgument getRequiredListenerArgument(MethodInvocation invocation) {
    if (invocation == null) {
      return null;
    }
    List<SimpleArgument> arguments = invocation.requiredArguments.getValue();
    if (arguments.isEmpty()) {
      return null;
    }
    SimpleArgument argument = arguments.getFirst();
    return isListenerArgument(argument) ? argument : null;
  }

  static UserLambda getUserLambda(SimpleArgument listenerArgument) {
    if (listenerArgument == null) {
      return null;
    }
    Expression expression = listenerArgument.expression.getValue();
    if (!(expression instanceof LambdaExpression lambdaExpression)) {
      return null;
    }
    Lambda lambda = lambdaExpression.value.getValue();
    return lambda instanceof UserLambda userLambda ? userLambda : null;
  }

  static MethodInvocation getListenerInvocation(Statement listener) {
    if (listener instanceof ExpressionStatement statement) {
      Expression exp = statement.expression.getValue();
      if (exp instanceof MethodInvocation invocation) {
        return invocation;
      }
    }
    return null;
  }
}
