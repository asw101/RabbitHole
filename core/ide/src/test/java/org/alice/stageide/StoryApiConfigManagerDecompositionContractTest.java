package org.alice.stageide;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Integration contract tests for the StoryApiConfigurationManager decomposition (issue #661).
 *
 * This test file verifies cross-cutting concerns after extracting
 * StoryTypeComparator and JointMethodAugmentor:
 *   1. Line count target (under 500 lines)
 *   2. All three classes compile and load
 *   3. API preservation on StoryApiConfigurationManager
 *   4. Delegation wiring
 *   5. No orphaned imports
 *   6. BIPED_RESOURCE_TYPE stays in the manager (subclass dependency)
 *   7. No new public API surface
 *
 * TDD: these tests FAIL until the extraction is implemented.
 */
public class StoryApiConfigManagerDecompositionContractTest {

  private static final String MANAGER_FQCN = "org.alice.stageide.StoryApiConfigurationManager";
  private static final String COMPARATOR_FQCN = "org.alice.stageide.StoryTypeComparator";
  private static final String AUGMENTOR_FQCN = "org.alice.stageide.JointMethodAugmentor";

  private static final String MANAGER_SRC =
      "core/ide/src/main/java/org/alice/stageide/StoryApiConfigurationManager.java";
  private static final String COMPARATOR_SRC =
      "core/ide/src/main/java/org/alice/stageide/StoryTypeComparator.java";
  private static final String AUGMENTOR_SRC =
      "core/ide/src/main/java/org/alice/stageide/JointMethodAugmentor.java";

  private static Class<?> managerClazz;

  @BeforeClass
  public static void loadClasses() {
    try {
      managerClazz = Class.forName(MANAGER_FQCN);
    } catch (ClassNotFoundException e) {
      fail("StoryApiConfigurationManager not found: " + e.getMessage());
    }
  }

  // ── 1. Line count target ─────────────────────────────────────────

  @Test
  public void managerLineCount_under500() throws IOException {
    Path src = resolveSourceFile(MANAGER_SRC);
    if (!Files.exists(src)) {
      fail("Manager source not found at: " + MANAGER_SRC);
    }
    long lineCount = Files.lines(src).count();
    assertTrue("StoryApiConfigurationManager must be under 500 lines after extraction, found " + lineCount,
        lineCount < 500);
  }

  // ── 2. All three classes compile and load ─────────────────────────

  @Test
  public void allExtractedClasses_loadSuccessfully() {
    String[] fqcns = { MANAGER_FQCN, COMPARATOR_FQCN, AUGMENTOR_FQCN };
    for (String fqcn : fqcns) {
      try {
        Class<?> c = Class.forName(fqcn);
        assertNotNull(fqcn + " must load", c);
      } catch (ClassNotFoundException e) {
        fail(fqcn + " must compile and load: " + e.getMessage());
      }
    }
  }

  // ── 3. All new source files exist ────────────────────────────────

  @Test
  public void storyTypeComparatorFile_exists() {
    Path src = resolveSourceFile(COMPARATOR_SRC);
    assertTrue("StoryTypeComparator.java must exist", Files.exists(src));
  }

  @Test
  public void jointMethodAugmentorFile_exists() {
    Path src = resolveSourceFile(AUGMENTOR_SRC);
    assertTrue("JointMethodAugmentor.java must exist", Files.exists(src));
  }

  // ── 4. API preservation ──────────────────────────────────────────

  @Test
  public void manager_stillDeclares_getTypeComparator() {
    assertManagerDeclares("getTypeComparator");
  }

  @Test
  public void manager_stillDeclares_augmentTypeIfNecessary() {
    assertManagerDeclares("augmentTypeIfNecessary");
  }

  @Test
  public void manager_stillDeclares_getExpressionCreator() {
    assertManagerDeclares("getExpressionCreator");
  }

  @Test
  public void manager_stillDeclares_isExportTypeDesiredFor() {
    assertManagerDeclares("isExportTypeDesiredFor");
  }

  @Test
  public void manager_stillDeclares_isDeclaringTypeForManagedFields() {
    assertManagerDeclares("isDeclaringTypeForManagedFields");
  }

  @Test
  public void manager_stillDeclares_getBuildMethodPoseBuilderType() {
    assertManagerDeclares("getBuildMethodPoseBuilderType");
  }

  @Test
  public void manager_stillDeclares_isBuildMethod() {
    assertManagerDeclares("isBuildMethod");
  }

  @Test
  public void manager_stillDeclares_getInstance() {
    assertManagerDeclares("getInstance");
  }

  // ── 5. BIPED_RESOURCE_TYPE stays in manager ──────────────────────

  @Test
  public void bipedResourceType_stillInManager() throws IOException {
    Path src = resolveSourceFile(MANAGER_SRC);
    if (!Files.exists(src)) {
      fail("Manager source not found");
    }
    boolean found = Files.lines(src)
        .anyMatch(line -> line.contains("BIPED_RESOURCE_TYPE"));
    assertTrue("BIPED_RESOURCE_TYPE must remain in StoryApiConfigurationManager (used by subclass)",
        found);
  }

  @Test
  public void bipedResourceType_isProtectedStatic() {
    try {
      java.lang.reflect.Field field = managerClazz.getDeclaredField("BIPED_RESOURCE_TYPE");
      int mods = field.getModifiers();
      assertTrue("BIPED_RESOURCE_TYPE must be static", Modifier.isStatic(mods));
      assertTrue("BIPED_RESOURCE_TYPE must be protected", Modifier.isProtected(mods));
    } catch (NoSuchFieldException e) {
      fail("BIPED_RESOURCE_TYPE field must exist on StoryApiConfigurationManager");
    }
  }

  // ── 6. New classes are NOT public ────────────────────────────────

  @Test
  public void storyTypeComparator_notPublic() {
    try {
      Class<?> c = Class.forName(COMPARATOR_FQCN);
      assertFalse("StoryTypeComparator must not be public", Modifier.isPublic(c.getModifiers()));
    } catch (ClassNotFoundException e) {
      fail("StoryTypeComparator not found");
    }
  }

  @Test
  public void jointMethodAugmentor_notPublic() {
    try {
      Class<?> c = Class.forName(AUGMENTOR_FQCN);
      assertFalse("JointMethodAugmentor must not be public", Modifier.isPublic(c.getModifiers()));
    } catch (ClassNotFoundException e) {
      fail("JointMethodAugmentor not found");
    }
  }

  // ── 7. Delegation in source ──────────────────────────────────────

  @Test
  public void managerSource_references_StoryTypeComparator() throws IOException {
    Path src = resolveSourceFile(MANAGER_SRC);
    if (!Files.exists(src)) {
      fail("Manager source not found");
    }
    String content = Files.readString(src);
    assertTrue("Manager must reference StoryTypeComparator",
        content.contains("StoryTypeComparator"));
  }

  @Test
  public void managerSource_references_JointMethodAugmentor() throws IOException {
    Path src = resolveSourceFile(MANAGER_SRC);
    if (!Files.exists(src)) {
      fail("Manager source not found");
    }
    String content = Files.readString(src);
    assertTrue("Manager must reference JointMethodAugmentor",
        content.contains("JointMethodAugmentor"));
  }

  // ── 8. No orphaned imports ───────────────────────────────────────

  @Test
  public void managerSource_noOrphanedVisibilityImport() throws IOException {
    Path src = resolveSourceFile(MANAGER_SRC);
    if (!Files.exists(src)) {
      fail("Manager source not found");
    }
    Set<String> imports = readImports(src);
    boolean found = imports.stream().anyMatch(line ->
        line.contains(".Visibility;"));
    assertFalse("Visibility import must be removed from manager (only used in augmentor)", found);
  }

  @Test
  public void managerSource_noOrphanedReflectFieldImport() throws IOException {
    Path src = resolveSourceFile(MANAGER_SRC);
    if (!Files.exists(src)) {
      fail("Manager source not found");
    }
    Set<String> imports = readImports(src);
    boolean found = imports.stream().anyMatch(line ->
        line.contains("java.lang.reflect.Field"));
    assertFalse("java.lang.reflect.Field import must be removed from manager (only used in augmentor)", found);
  }

  @Test
  public void managerSource_noOrphanedFieldTemplateImport() throws IOException {
    Path src = resolveSourceFile(MANAGER_SRC);
    if (!Files.exists(src)) {
      fail("Manager source not found");
    }
    Set<String> imports = readImports(src);
    boolean found = imports.stream().anyMatch(line ->
        line.contains(".FieldTemplate;"));
    assertFalse("FieldTemplate import must be removed from manager (only used in augmentor)", found);
  }

  @Test
  public void managerSource_noOrphanedJointIdImport() throws IOException {
    Path src = resolveSourceFile(MANAGER_SRC);
    if (!Files.exists(src)) {
      fail("Manager source not found");
    }
    Set<String> imports = readImports(src);
    boolean found = imports.stream().anyMatch(line ->
        line.contains("resources.JointId;"));
    assertFalse("JointId import must be removed from manager (only used in augmentor)", found);
  }

  @Test
  public void managerSource_noOrphanedJointArrayIdImport() throws IOException {
    Path src = resolveSourceFile(MANAGER_SRC);
    if (!Files.exists(src)) {
      fail("Manager source not found");
    }
    Set<String> imports = readImports(src);
    boolean found = imports.stream().anyMatch(line ->
        line.contains("resources.JointArrayId;"));
    assertFalse("JointArrayId import must be removed from manager (only used in augmentor)", found);
  }

  @Test
  public void managerSource_noOrphanedDynamicResourceImport() throws IOException {
    Path src = resolveSourceFile(MANAGER_SRC);
    if (!Files.exists(src)) {
      fail("Manager source not found");
    }
    Set<String> imports = readImports(src);
    boolean found = imports.stream().anyMatch(line ->
        line.contains("resources.DynamicResource;"));
    assertFalse("DynamicResource import must be removed from manager (only used in augmentor)", found);
  }

  // ── 9. Manager retains imports it still uses ─────────────────────

  @Test
  public void managerSource_retainsListsImport() throws IOException {
    Path src = resolveSourceFile(MANAGER_SRC);
    if (!Files.exists(src)) {
      fail("Manager source not found");
    }
    Set<String> imports = readImports(src);
    assertImportPresent(imports, "Lists",
        "Manager still uses Lists.newLinkedList in createUnmodifiableSubCompositeList");
  }

  @Test
  public void managerSource_retainsBipedResourceImport() throws IOException {
    Path src = resolveSourceFile(MANAGER_SRC);
    if (!Files.exists(src)) {
      fail("Manager source not found");
    }
    Set<String> imports = readImports(src);
    // BipedResource is used for BIPED_RESOURCE_TYPE
    assertImportPresent(imports, "BipedResource",
        "Manager still uses BipedResource for BIPED_RESOURCE_TYPE");
  }

  // ── 10. Extracted code is ~140 lines in augmentor ────────────────

  @Test
  public void augmentorSource_sizeSanityCheck() throws IOException {
    Path src = resolveSourceFile(AUGMENTOR_SRC);
    if (!Files.exists(src)) {
      fail("Augmentor source not found");
    }
    long lineCount = Files.lines(src).count();
    // Copyright header is 42 lines, extracted code is ~140 lines, package+imports ~15
    // Total should be roughly 180-220 lines
    assertTrue("JointMethodAugmentor should be over 100 lines (contains ~140 lines of logic), found " + lineCount,
        lineCount > 100);
    assertTrue("JointMethodAugmentor should be under 250 lines, found " + lineCount,
        lineCount < 250);
  }

  @Test
  public void comparatorSource_sizeSanityCheck() throws IOException {
    Path src = resolveSourceFile(COMPARATOR_SRC);
    if (!Files.exists(src)) {
      fail("Comparator source not found");
    }
    long lineCount = Files.lines(src).count();
    // Copyright header is 42 lines, extracted code is ~38 lines, package+imports ~10
    // Total should be roughly 80-110 lines
    assertTrue("StoryTypeComparator should be over 60 lines, found " + lineCount,
        lineCount > 60);
    assertTrue("StoryTypeComparator should be under 130 lines, found " + lineCount,
        lineCount < 130);
  }

  // ── Helpers ──────────────────────────────────────────────────────

  private static void assertManagerDeclares(String methodName) {
    boolean found = Arrays.stream(managerClazz.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals(methodName));
    assertTrue("StoryApiConfigurationManager must still declare: " + methodName, found);
  }

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
