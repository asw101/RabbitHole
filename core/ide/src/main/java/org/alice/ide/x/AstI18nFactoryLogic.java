package org.alice.ide.x;

import edu.cmu.cs.dennisc.property.InstancePropertyOwner;
import org.lgna.project.ast.AbstractConstructor;
import org.lgna.project.ast.AbstractDeclaration;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.SimpleArgument;
import org.lgna.project.ast.UserCode;
import org.lgna.project.ast.UserMethod;

final class AstI18nFactoryLogic {
  enum ComponentKind {
    DECLARATION_NAME,
    PARAMETER_NAME,
    TYPE,
    PARAMETERS,
    LABEL
  }

  private AstI18nFactoryLogic() {
    throw new AssertionError();
  }

  static ComponentKind getComponentKind(InstancePropertyOwner owner, String methodName) {
    if ((owner instanceof AbstractDeclaration) && methodName.equals("getName")) {
      return ComponentKind.DECLARATION_NAME;
    } else if ((owner instanceof SimpleArgument) && methodName.equals("getParameterNameText")) {
      return ComponentKind.PARAMETER_NAME;
    } else if ((owner instanceof AbstractConstructor) && methodName.equals("getDeclaringType")) {
      return ComponentKind.TYPE;
    } else if ((owner instanceof UserMethod) && methodName.equals("getReturnType")) {
      return ComponentKind.TYPE;
    } else if ((owner instanceof UserCode) && methodName.equals("getParameters")) {
      return ComponentKind.PARAMETERS;
    } else {
      return ComponentKind.LABEL;
    }
  }

  static String getLabelText(Object value) {
    if (value == null) {
      return null;
    }
    if (value instanceof AbstractType<?, ?, ?> type) {
      return type.getName();
    }
    return value.toString();
  }
}
