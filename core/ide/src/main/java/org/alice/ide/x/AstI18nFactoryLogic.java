package org.alice.ide.x;

import edu.cmu.cs.dennisc.property.InstancePropertyOwner;
import org.lgna.project.ast.AbstractConstructor;
import org.lgna.project.ast.AbstractDeclaration;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.SimpleArgument;
import org.lgna.project.ast.UserCode;
import org.lgna.project.ast.UserMethod;

import java.util.Set;

final class AstI18nFactoryLogic {
  enum ComponentKind {
    DECLARATION_NAME,
    PARAMETER_NAME,
    TYPE,
    PARAMETERS,
    LABEL
  }

  enum LocalPropertyKind {
    NONE,
    LOCAL,
    LOCAL_DECLARATION
  }

  private static final Set<String> LOCAL_PROPERTY_NAMES = Set.of("local", "item", "variable", "constant");

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

  static LocalPropertyKind getLocalPropertyKind(String propertyName, int underscoreCount) {
    if (!LOCAL_PROPERTY_NAMES.contains(propertyName)) {
      return LocalPropertyKind.NONE;
    }
    return switch (underscoreCount) {
    case 1 -> LocalPropertyKind.LOCAL;
    case 2 -> LocalPropertyKind.LOCAL_DECLARATION;
    default -> LocalPropertyKind.NONE;
    };
  }

  static String createPoseBuilderPrefixText(AbstractType<?, ?, ?> type) {
    return "new " + type.getName() + "(...).";
  }
}
