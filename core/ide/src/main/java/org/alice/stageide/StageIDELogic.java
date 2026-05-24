package org.alice.stageide;

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
}
