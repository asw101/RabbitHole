package org.alice.ide.cascade;

import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NullLiteral;

final class CascadeBuilder {
  TypeResolution getTypeResolution(AbstractType<?, ?, ?> requestedType, boolean isRoot, Expression previousExpression) {
    AbstractType<?, ?, ?> resolvedType = normalizeType(requestedType);
    boolean isOtherTypeMenuDesired = resolvedType == JavaType.OBJECT_TYPE;
    if (isOtherTypeMenuDesired && isRoot && (previousExpression != null)) {
      resolvedType = previousExpression.getType();
    }
    if (resolvedType == JavaType.OBJECT_TYPE) {
      resolvedType = JavaType.STRING_TYPE;
    }
    return new TypeResolution(resolvedType, isOtherTypeMenuDesired);
  }

  AbstractType<?, ?, ?> normalizeType(AbstractType<?, ?, ?> type) {
    if (type == JavaType.getInstance(Number.class)) {
      return JavaType.DOUBLE_OBJECT_TYPE;
    }
    return type;
  }

  boolean isPreviousExpressionApplicable(boolean isRoot, AbstractType<?, ?, ?> desiredType, Expression previousExpression) {
    return isRoot && (previousExpression != null) && desiredType.isAssignableFrom(previousExpression.getType());
  }

  boolean shouldCreateArrayLengthFillIns(boolean isTop, AbstractType<?, ?, ?> type, Expression previousExpression) {
    return isTop && ((type == JavaType.INTEGER_OBJECT_TYPE)
        || (type.isAssignableFrom(JavaType.INTEGER_OBJECT_TYPE) && (previousExpression != null)));
  }

  boolean shouldShowStringConcatenationOptions(boolean isTop, Expression previousExpression) {
    return isTop && (previousExpression != null);
  }

  boolean shouldShowStringConcatenationRightOperandOnly(boolean isTop, Expression previousExpression) {
    return shouldShowStringConcatenationOptions(isTop, previousExpression)
        && !(previousExpression instanceof NullLiteral)
        && previousExpression.getType().isAssignableTo(String.class);
  }

  static final class TypeResolution {
    private final AbstractType<?, ?, ?> resolvedType;
    private final boolean otherTypeMenuDesired;

    TypeResolution(AbstractType<?, ?, ?> resolvedType, boolean otherTypeMenuDesired) {
      this.resolvedType = resolvedType;
      this.otherTypeMenuDesired = otherTypeMenuDesired;
    }

    AbstractType<?, ?, ?> getResolvedType() {
      return this.resolvedType;
    }

    boolean isOtherTypeMenuDesired() {
      return this.otherTypeMenuDesired;
    }
  }
}
