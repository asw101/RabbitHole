package org.alice.stageide;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * TDD contract tests for StoryTypeComparator extraction (issue #661).
 *
 * StoryTypeComparator is the top-level package-private enum extracted from
 * StoryApiConfigurationManager's private inner TypeComparator enum.
 *
 * These tests verify:
 *   1. The enum class exists and loads
 *   2. It has the SINGLETON constant
 *   3. It implements Comparator (the raw type check)
 *   4. Package-private visibility (not public)
 *   5. Source file exists with the CMU copyright header
 *   6. The original inner enum is removed from StoryApiConfigurationManager
 *   7. StoryApiConfigurationManager.getTypeComparator() still works
 *
 * TDD: these tests FAIL until the extraction is implemented.
 */
public class StoryTypeComparatorContractTest {

  private static final String COMPARATOR_FQCN = "org.alice.stageide.StoryTypeComparator";
  private static final String MANAGER_FQCN = "org.alice.stageide.StoryApiConfigurationManager";

  private static final String COMPARATOR_SRC =
      "core/ide/src/main/java/org/alice/stageide/StoryTypeComparator.java";
  private static final String MANAGER_SRC =
      "core/ide/src/main/java/org/alice/stageide/StoryApiConfigurationManager.java";

  private static Class<?> comparatorClazz;
  private static Class<?> managerClazz;

  @BeforeClass
  public static void loadClasses() {
    try {
      comparatorClazz = Class.forName(COMPARATOR_FQCN);
    } catch (ClassNotFoundException e) {
      fail("StoryTypeComparator not found — extraction not yet implemented: " + e.getMessage());
    }
    try {
      managerClazz = Class.forName(MANAGER_FQCN);
    } catch (ClassNotFoundException e) {
      fail("StoryApiConfigurationManager not found: " + e.getMessage());
    }
  }

  // ── 1. Class structure ───────────────────────────────────────────

  @Test
  public void isEnum() {
    assertTrue("StoryTypeComparator must be an enum", comparatorClazz.isEnum());
  }

  @Test
  public void implementsComparator() {
    assertTrue("StoryTypeComparator must implement Comparator",
        Comparator.class.isAssignableFrom(comparatorClazz));
  }

  @Test
  public void isPackagePrivate() {
    int mods = comparatorClazz.getModifiers();
    assertFalse("StoryTypeComparator must not be public", Modifier.isPublic(mods));
    assertFalse("StoryTypeComparator must not be private", Modifier.isPrivate(mods));
    assertFalse("StoryTypeComparator must not be protected", Modifier.isProtected(mods));
  }

  @Test
  public void hasSingletonConstant() {
    Object[] constants = comparatorClazz.getEnumConstants();
    assertNotNull("Enum must have constants", constants);
    assertEquals("StoryTypeComparator must have exactly 1 constant (SINGLETON)", 1, constants.length);
    assertEquals("The constant must be named SINGLETON", "SINGLETON", ((Enum<?>) constants[0]).name());
  }

  // ── 2. Source file existence and structure ────────────────────────

  @Test
  public void sourceFileExists() {
    Path src = resolveSourceFile(COMPARATOR_SRC);
    assertTrue("StoryTypeComparator.java must exist at " + COMPARATOR_SRC, Files.exists(src));
  }

  @Test
  public void sourceHasCopyrightHeader() throws IOException {
    Path src = resolveSourceFile(COMPARATOR_SRC);
    if (!Files.exists(src)) {
      fail("Source file not found");
    }
    String content = Files.readString(src);
    assertTrue("Must contain CMU copyright header",
        content.contains("Copyright (c)") && content.contains("Carnegie Mellon University"));
  }

  @Test
  public void sourceHasCorrectPackage() throws IOException {
    Path src = resolveSourceFile(COMPARATOR_SRC);
    if (!Files.exists(src)) {
      fail("Source file not found");
    }
    boolean hasPackage = Files.lines(src)
        .anyMatch(line -> line.trim().equals("package org.alice.stageide;"));
    assertTrue("Must declare package org.alice.stageide", hasPackage);
  }

  @Test
  public void sourceNotDeclaredPublic() throws IOException {
    Path src = resolveSourceFile(COMPARATOR_SRC);
    if (!Files.exists(src)) {
      fail("Source file not found");
    }
    boolean hasPublicEnum = Files.lines(src)
        .anyMatch(line -> line.trim().startsWith("public enum StoryTypeComparator"));
    assertFalse("StoryTypeComparator must NOT be declared public", hasPublicEnum);
  }

  // ── 3. Inner enum removed from StoryApiConfigurationManager ──────

  @Test
  public void managerHasNoInnerTypeComparator() {
    Class<?>[] innerClasses = managerClazz.getDeclaredClasses();
    for (Class<?> inner : innerClasses) {
      assertNotEquals("TypeComparator must no longer be an inner class of StoryApiConfigurationManager",
          "TypeComparator", inner.getSimpleName());
    }
  }

  @Test
  public void managerSource_noPrivateEnumTypeComparator() throws IOException {
    Path src = resolveSourceFile(MANAGER_SRC);
    if (!Files.exists(src)) {
      fail("Manager source not found");
    }
    boolean hasInnerEnum = Files.lines(src)
        .anyMatch(line -> line.contains("private static enum TypeComparator")
            || line.contains("private enum TypeComparator"));
    assertFalse("StoryApiConfigurationManager must no longer contain 'private static enum TypeComparator'",
        hasInnerEnum);
  }

  // ── 4. Imports in extracted file ─────────────────────────────────

  @Test
  public void comparatorSource_importsMaps() throws IOException {
    Path src = resolveSourceFile(COMPARATOR_SRC);
    if (!Files.exists(src)) {
      fail("Source file not found");
    }
    Set<String> imports = readImports(src);
    assertImportPresent(imports, "Maps",
        "StoryTypeComparator uses Maps.newHashMap()");
  }

  @Test
  public void comparatorSource_importsJavaType() throws IOException {
    Path src = resolveSourceFile(COMPARATOR_SRC);
    if (!Files.exists(src)) {
      fail("Source file not found");
    }
    Set<String> imports = readImports(src);
    // May be a wildcard import or explicit
    boolean found = imports.stream().anyMatch(line ->
        line.contains("JavaType") || line.contains("org.lgna.project.ast.*"));
    assertTrue("StoryTypeComparator must import JavaType (explicitly or via wildcard)", found);
  }

  // ── 5. Manager delegation ────────────────────────────────────────

  @Test
  public void managerSource_getTypeComparator_delegatesToStoryTypeComparator() throws IOException {
    Path src = resolveSourceFile(MANAGER_SRC);
    if (!Files.exists(src)) {
      fail("Manager source not found");
    }
    String content = Files.readString(src);
    assertTrue("getTypeComparator() must reference StoryTypeComparator.SINGLETON",
        content.contains("StoryTypeComparator.SINGLETON"));
  }

  // ── Helpers ──────────────────────────────────────────────────────

  private static Path resolveSourceFile(String relativePath) {
    Path cwd = Paths.get(System.getProperty("user.dir"));
    Path candidate = cwd.resolve(relativePath);
    if (Files.exists(candidate)) {
      return candidate;
    }
    Path dir = cwd;
    while (dir != null) {
      candidate = dir.resolve(relativePath);
      if (Files.exists(candidate)) {
        return candidate;
      }
      dir = dir.getParent();
    }
    return Paths.get(relativePath);
  }

  private static Set<String> readImports(Path path) throws IOException {
    return Files.lines(path)
        .map(String::trim)
        .filter(line -> line.startsWith("import "))
        .collect(Collectors.toSet());
  }

  private static void assertImportPresent(Set<String> imports, String simpleTypeName, String reason) {
    boolean found = imports.stream().anyMatch(line ->
        line.contains("." + simpleTypeName + ";")
            || line.endsWith(".*;"));
    assertTrue("Import of " + simpleTypeName + " must be present (" + reason + ")", found);
  }
}
