package org.alice.ide.icons;

import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.InstanceCreation;
import org.lgna.story.Visual;

final class IconFactoryManagerLogic {
  private IconFactoryManagerLogic() {
    throw new AssertionError();
  }

  static int getRequiredArgumentsInInitializer(Expression initializer) {
    if (initializer instanceof InstanceCreation instanceCreation) {
      return instanceCreation.requiredArguments.size();
    }
    return -1;
  }

  static boolean shouldUseDynamicFieldIcon(AbstractType<?, ?, ?> type) {
    return type.isAssignableTo(Visual.class);
  }
}
