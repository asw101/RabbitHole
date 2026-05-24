package org.alice.stageide.custom;

import org.lgna.project.ast.AbstractField;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.TypeExpression;

final class KeyCustomExpressionCreatorCompositeLogic {
  private KeyCustomExpressionCreatorCompositeLogic() {
    throw new AssertionError();
  }

  static Expression createValue(org.lgna.story.Key key) {
    if (key == null) {
      return null;
    }
    AbstractType<?, ?, ?> type = JavaType.getInstance(org.lgna.story.Key.class);
    AbstractField field = type.getDeclaredField(type, key.name());
    assert field.isPublicAccess() && field.isStatic() && field.isFinal();
    return new FieldAccess(new TypeExpression(type), field);
  }

  static boolean hasSelectedKey(org.lgna.story.Key key) {
    return key != null;
  }

  static org.lgna.story.Key decodeSelectedKey(Expression expression) {
    if (expression instanceof FieldAccess fieldAccess) {
      AbstractType<?, ?, ?> type = fieldAccess.getType();
      if (type == JavaType.getInstance(org.lgna.story.Key.class)) {
        AbstractField field = fieldAccess.field.getValue();
        if (field != null) {
          return Enum.valueOf(org.lgna.story.Key.class, field.getName());
        }
      }
    }
    return null;
  }
}
