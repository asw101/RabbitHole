package org.alice.stageide.sceneeditor;

import org.lgna.project.ast.AbstractField;
import org.lgna.project.ast.ArrayAccess;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.FieldAccess;
import org.lgna.project.ast.MethodInvocation;
import org.lgna.project.ast.ThisExpression;
import org.lgna.project.ast.UserField;
import org.lgna.story.SCameraMarker;
import org.lgna.story.SThingMarker;

final class FieldRegistry {
  SelectionPlan createSelectionPlan(Expression expression, UserField activeSceneField) {
    if (expression instanceof FieldAccess fieldAccess) {
      AbstractField field = fieldAccess.field.getValue();
      if (field instanceof UserField userField) {
        return SelectionPlan.forField(userField);
      }
    } else if ((expression instanceof MethodInvocation) || (expression instanceof ArrayAccess)) {
      return SelectionPlan.forExpression(expression);
    } else if (expression instanceof ThisExpression) {
      if (activeSceneField != null) {
        return SelectionPlan.forField(activeSceneField);
      }
    }
    return SelectionPlan.none();
  }

  MarkerKind classifyField(UserField field) {
    if (field == null) {
      return MarkerKind.NONE;
    }
    if (field.getValueType().isAssignableFrom(SCameraMarker.class)) {
      return MarkerKind.CAMERA;
    }
    if (field.getValueType().isAssignableFrom(SThingMarker.class)) {
      return MarkerKind.OBJECT;
    }
    return MarkerKind.REGULAR;
  }

  enum MarkerKind {
    NONE,
    CAMERA,
    OBJECT,
    REGULAR
  }

  static final class SelectionPlan {
    private final UserField field;
    private final Expression expression;

    private SelectionPlan(UserField field, Expression expression) {
      this.field = field;
      this.expression = expression;
    }

    static SelectionPlan forField(UserField field) {
      return new SelectionPlan(field, null);
    }

    static SelectionPlan forExpression(Expression expression) {
      return new SelectionPlan(null, expression);
    }

    static SelectionPlan none() {
      return new SelectionPlan(null, null);
    }

    boolean isFieldSelection() {
      return this.field != null;
    }

    boolean isExpressionSelection() {
      return this.expression != null;
    }

    UserField getField() {
      return this.field;
    }

    Expression getExpression() {
      return this.expression;
    }
  }
}
