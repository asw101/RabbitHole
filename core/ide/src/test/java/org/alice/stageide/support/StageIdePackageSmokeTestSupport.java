package org.alice.stageide.support;

import org.junit.Assert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Set;

public final class StageIdePackageSmokeTestSupport {
  private static final Path MAIN_ROOT = resolveDirectory(
      "core/ide/src/main/java",
      "src/main/java");

  private StageIdePackageSmokeTestSupport() {
  }

  public static void assertPackageTreeLoads(String packageName) throws Exception {
    Path packageRoot = MAIN_ROOT.resolve(packageName.replace('.', '/'));
    Assert.assertTrue("Expected package directory for " + packageName, Files.isDirectory(packageRoot));

    Set<String> typeNames = new LinkedHashSet<>();
    try (var stream = Files.walk(packageRoot)) {
      stream
          .filter(path -> path.toString().endsWith(".java"))
          .filter(path -> !path.getFileName().toString().equals("package-info.java"))
          .forEach(path -> typeNames.add(typeNameFor(path)));
    }

    Assert.assertFalse("Expected at least one Java type under " + packageName, typeNames.isEmpty());
    ClassLoader classLoader = StageIdePackageSmokeTestSupport.class.getClassLoader();
    for (String typeName : typeNames) {
      Class<?> type = Class.forName(typeName, false, classLoader);
      Assert.assertNotNull(type);
      Assert.assertTrue(typeName.startsWith(packageName + ".") || typeName.equals(packageName));
    }
  }

  private static String typeNameFor(Path javaFile) {
    Path relative = MAIN_ROOT.relativize(javaFile);
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < (relative.getNameCount() - 1); i++) {
      if (i > 0) {
        sb.append('.');
      }
      sb.append(relative.getName(i).toString());
    }
    if (sb.length() > 0) {
      sb.append('.');
    }
    sb.append(javaFile.getFileName().toString().replaceFirst("\\.java$", ""));
    return sb.toString();
  }

  private static Path resolveDirectory(String repoRelativePath, String moduleRelativePath) {
    for (String candidate : new String[]{repoRelativePath, moduleRelativePath}) {
      Path path = Paths.get(candidate);
      if (Files.isDirectory(path)) {
        return path;
      }
    }
    throw new IllegalStateException("Could not resolve Java source directory");
  }
}
