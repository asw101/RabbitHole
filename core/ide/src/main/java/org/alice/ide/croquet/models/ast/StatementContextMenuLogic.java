package org.alice.ide.croquet.models.ast;

import org.lgna.project.ast.AbstractMethod;
import org.lgna.project.ast.AbstractStatementWithBody;
import org.lgna.project.ast.ArgumentOwner;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.Comment;
import org.lgna.project.ast.DoInOrder;
import org.lgna.project.ast.DoTogether;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.JavaKeyedArgument;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.Statement;
import org.lgna.project.ast.UserMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class StatementContextMenuLogic {
  enum ConversionAction {
    NONE,
    DO_IN_ORDER_TO_DO_TOGETHER,
    DO_TOGETHER_TO_DO_IN_ORDER
  }

  private StatementContextMenuLogic() {
    throw new AssertionError();
  }

  static boolean shouldAddExecutionControls(Statement statement) {
    return !(statement instanceof Comment);
  }

  static UserMethod getInvokedUserMethod(Statement statement) {
    if (statement instanceof ExpressionStatement expressionStatement) {
      Expression expression = expressionStatement.expression.getValue();
      if (expression instanceof MethodInvocation methodInvocation) {
        AbstractMethod method = methodInvocation.method.getValue();
        if (method instanceof UserMethod userMethod) {
          return userMethod;
        }
      }
    }
    return null;
  }

  static boolean shouldAddDelete(Statement statement) {
    return statement.getParent() instanceof BlockStatement;
  }

  static boolean shouldAddDissolve(Statement statement) {
    return statement instanceof AbstractStatementWithBody;
  }

  static ConversionAction getConversionAction(Statement statement) {
    if (statement instanceof DoInOrder) {
      return ConversionAction.DO_IN_ORDER_TO_DO_TOGETHER;
    }
    if (statement instanceof DoTogether) {
      return ConversionAction.DO_TOGETHER_TO_DO_IN_ORDER;
    }
    return ConversionAction.NONE;
  }

  static List<JavaKeyedArgument> getRemovableKeyedArguments(Statement statement) {
    if (statement instanceof ExpressionStatement expressionStatement) {
      Expression expression = expressionStatement.expression.getValue();
      if (expression instanceof ArgumentOwner argumentOwner) {
        List<JavaKeyedArgument> removable = new ArrayList<>();
        for (JavaKeyedArgument argument : argumentOwner.getKeyedArgumentsProperty()) {
          removable.add(argument);
        }
        return removable;
      }
    }
    return Collections.emptyList();
  }
}
