package org.alice.ide.ast.declaration;

import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.NullLiteral;

final class DeclarationLikeSubstanceCompositeHelper {
  private DeclarationLikeSubstanceCompositeHelper() {
    throw new AssertionError();
  }

  static AbstractType<?, ?, ?> resolveValueType(AbstractType<?, ?, ?> componentType, boolean isArrayType) {
    if (componentType == null) {
      return null;
    }
    return isArrayType ? componentType.getArrayType() : componentType;
  }

  static Expression normalizeInitializer(Expression initializer) {
    return initializer != null ? initializer : new NullLiteral();
  }

  static boolean isDisplayed(DeclarationLikeSubstanceComposite.ApplicabilityStatus status) {
    return status != null && status.isDisplayed();
  }
}
