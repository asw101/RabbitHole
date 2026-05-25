package org.alice.stageide;

import org.lgna.project.ast.UserField;
import org.lgna.project.ast.ManagementLevel;

import java.util.function.Predicate;

final class StageIDELogic {
  private StageIDELogic() {
    throw new AssertionError();
  }

  static boolean isDropDownDesired(boolean superDesired, boolean lambdaExpression, boolean sceneTurnableField, boolean colorConstructor, boolean personResourceConstructor, boolean buildMethod) {
    if (!superDesired) {
      return false;
    }
    return !(lambdaExpression || sceneTurnableField || colorConstructor || personResourceConstructor || buildMethod);
  }

  static UserField findManagedSceneField(Iterable<UserField> fields, Predicate<UserField> isInstanceFactoryDesired) {
    UserField selected = null;
    for (UserField field : fields) {
      if ((field.managementLevel.getValue() == ManagementLevel.MANAGED) && isInstanceFactoryDesired.test(field)) {
        selected = field;
      }
    }
    return selected;
  }
}
