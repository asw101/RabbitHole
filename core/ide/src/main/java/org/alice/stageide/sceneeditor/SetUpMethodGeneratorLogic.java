package org.alice.stageide.sceneeditor;

import org.lgna.project.ast.AbstractField;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.ThisExpression;
import org.lgna.story.Scale;

final class SetUpMethodGeneratorLogic {
  private SetUpMethodGeneratorLogic() {
    throw new AssertionError();
  }

  static Expression createInstanceExpression(boolean isThis, AbstractField field) {
    return isThis ? new ThisExpression() : new FieldAccess(field);
  }

  static boolean shouldCreateSizeStatement(boolean isBox, Scale scale) {
    return isBox || !Scale.IDENTITY.equals(scale);
  }
}
