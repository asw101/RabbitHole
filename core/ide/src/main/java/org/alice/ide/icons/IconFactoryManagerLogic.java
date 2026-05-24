package org.alice.ide.icons;

import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.Expression;
import org.lgna.project.ast.InstanceCreation;
import org.lgna.story.Visual;
import org.lgna.story.resources.JointedModelResource;
import org.lgna.story.resources.ModelResource;

import java.util.Set;

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

  static boolean shouldUseBundledSvgIcon(Class<? extends ModelResource> resourceClass, String modelResourceName, Set<Class<? extends JointedModelResource>> classesWithIcons) {
    return (resourceClass != null) && (modelResourceName == null) && classesWithIcons.contains(resourceClass);
  }

  static String getBundledSvgPath(Class<? extends ModelResource> resourceClass) {
    return "images/resources/" + resourceClass.getSimpleName() + ".svg";
  }
}
