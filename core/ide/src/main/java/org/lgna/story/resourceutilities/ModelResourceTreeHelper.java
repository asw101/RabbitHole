package org.lgna.story.resourceutilities;

final class ModelResourceTreeHelper {
  private ModelResourceTreeHelper() {
    throw new AssertionError();
  }

  static String createAlicePackageName(String resourcePackage, String rootPackage, String defaultPackage) {
    int rootIndex = resourcePackage.indexOf(rootPackage);
    if (rootIndex != -1) {
      resourcePackage = resourcePackage.substring(rootIndex + rootPackage.length());
      if (resourcePackage.startsWith(".")) {
        resourcePackage = resourcePackage.substring(1);
      }
    }
    return defaultPackage + resourcePackage;
  }

  @SuppressWarnings("unchecked")
  static <T> Class<? extends T> findFirstMatchingInterface(Class<?>[] interfaces, Class<T> requiredType) {
    for (Class<?> intrfc : interfaces) {
      if (requiredType.isAssignableFrom(intrfc)) {
        return (Class<? extends T>) intrfc;
      }
    }
    return null;
  }

  static String getDynamicResourceClassName(Class<?> resourceClass) {
    return "org.lgna.story.resources.Dynamic" + resourceClass.getSimpleName();
  }
}
