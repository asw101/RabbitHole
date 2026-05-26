package org.alice.stageide;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class RemainingStageIdePackageContractTest {
  private static final Set<String> CONTRACT_COVERED_PACKAGES = Set.of();

  private static final Path MAIN_ROOT = resolveDirectory(
      "core/ide/src/main/java/org/alice/stageide",
      "src/main/java/org/alice/stageide");
  private static final Path TEST_ROOT = resolveDirectory(
      "core/ide/src/test/java/org/alice/stageide",
      "src/test/java/org/alice/stageide");

  private static Path resolveDirectory(String repoRelativePath, String moduleRelativePath) {
    for (String candidate : new String[]{repoRelativePath, moduleRelativePath}) {
      Path path = Paths.get(candidate);
      if (Files.isDirectory(path)) {
        return path;
      }
    }
    throw new IllegalStateException("Could not resolve stageide source directory");
  }

  private static Set<String> collectPackages(Path root) throws IOException {
    Set<String> packages = new TreeSet<>();
    try (var stream = Files.walk(root)) {
      stream
          .filter(path -> path.toString().endsWith(".java"))
          .forEach(path -> packages.add(packageNameFor(root, path)));
    }
    return packages;
  }

  private static Map<String, Set<String>> collectTopLevelTypes(Path root) throws IOException {
    Map<String, Set<String>> typesByPackage = new LinkedHashMap<>();
    try (var stream = Files.walk(root)) {
      stream
          .filter(path -> path.toString().endsWith(".java"))
          .forEach(path -> {
            String packageName = packageNameFor(root, path);
            String className = path.getFileName().toString().replaceFirst("\\.java$", "");
            typesByPackage.computeIfAbsent(packageName, ignored -> new LinkedHashSet<>())
                .add(packageName + "." + className);
          });
    }
    return typesByPackage;
  }

  private static String packageNameFor(Path root, Path javaFile) {
    Path relative = root.relativize(javaFile);
    int nameCount = relative.getNameCount();
    if (nameCount == 1) {
      return "org.alice.stageide";
    }
    StringBuilder sb = new StringBuilder("org.alice.stageide");
    for (int i = 0; i < (nameCount - 1); i++) {
      sb.append('.').append(relative.getName(i).toString());
    }
    return sb.toString();
  }

  @Test
  public void remainingPackagesWithoutDedicatedTests_areExplicitlyTracked() throws IOException {
    Set<String> missingPackages = new TreeSet<>(collectPackages(MAIN_ROOT));
    missingPackages.removeAll(collectPackages(TEST_ROOT));

    assertEquals(new TreeSet<>(CONTRACT_COVERED_PACKAGES), missingPackages);
  }

  @Test
  public void trackedRemainingPackages_haveLoadableTopLevelTypes() throws Exception {
    Map<String, Set<String>> typesByPackage = collectTopLevelTypes(MAIN_ROOT);
    ClassLoader classLoader = getClass().getClassLoader();

    for (String packageName : CONTRACT_COVERED_PACKAGES) {
      Set<String> typeNames = typesByPackage.get(packageName);
      if ((typeNames == null) || typeNames.isEmpty()) {
        fail("Expected loadable types for package: " + packageName);
      }
      for (String typeName : typeNames) {
        Class<?> type = Class.forName(typeName, false, classLoader);
        assertNotNull(type);
      }
    }
  }

  @Test
  public void trackedRemainingPackages_areRootedInRealSourceDirectories() {
    assertTrue(CONTRACT_COVERED_PACKAGES.isEmpty());
    assertNotNull(MAIN_ROOT);
    assertNotNull(TEST_ROOT);
  }
}
