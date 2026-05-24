package org.alice.stageide.sceneeditor;

import org.lgna.project.ast.AbstractField;
import org.lgna.project.ast.DoubleLiteral;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.ExpressionStatement;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.JavaKeyedArgument;
import org.lgna.project.ast.JavaMethod;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.ThisExpression;
import org.lgna.story.DurationAnimationStyleArgumentFactory;
import org.lgna.story.Scale;

final class SetUpMethodGeneratorLogic {
  private SetUpMethodGeneratorLogic() {
    throw new AssertionError();
  }

  static Expression createInstanceExpression(boolean isThis, AbstractField field) {
    return isThis ? new ThisExpression() : new FieldAccess(field);
  }

  static void addDurationIfRequested(ExpressionStatement statement, double duration) {
    if (duration != -1) {
      MethodInvocation methodInvocation = (MethodInvocation) statement.expression.getValue();
      JavaMethod durationKeyMethod = JavaMethod.getInstance(DurationAnimationStyleArgumentFactory.class, "duration", Number.class);
      methodInvocation.keyedArguments.add(new JavaKeyedArgument(methodInvocation.method.getValue().getKeyedParameter(), durationKeyMethod, new DoubleLiteral(duration)));
    }
  }

  static boolean shouldCreateSizeStatement(boolean isBox, Scale scale) {
    return isBox || !Scale.IDENTITY.equals(scale);
  }
}
