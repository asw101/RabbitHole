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
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * TDD contract tests for JointMethodAugmentor extraction (issue #661).
 *
 * JointMethodAugmentor is a package-private final class extracted from
 * StoryApiConfigurationManager containing ~140 lines of joint method
 * generation logic (lines 406-544 in the original).
 *
 * Extracted members:
 *   - JOINTED_MODEL_TYPE constant
 *   - getFieldMethodNameHint(AbstractField)
 *   - addMethodsToType(UserType, DynamicResource)
 *   - addMethodsToType(UserType, AbstractType)
 *   - augmentTypeIfNecessary(UserType) → renamed to augment(UserType)
 *   - getArgumentField(AbstractConstructor)
 *
 * These tests verify:
 *   1. The class exists, is final, and is package-private
 *   2. It has the expected static entry point augment(UserType)
 *   3. Source file exists with CMU copyright header
 *   4. The extracted methods are removed from StoryApiConfigurationManager
 *   5. StoryApiConfigurationManager delegates augmentTypeIfNecessary to this class
 *   6. Required imports are present in the new file
 *
 * TDD: these tests FAIL until the extraction is implemented.
 */
public class JointMethodAugmentorContractTest {

  private static final String AUGMENTOR_FQCN = "org.alice.stageide.JointMethodAugmentor";
  private static final String MANAGER_FQCN = "org.alice.stageide.StoryApiConfigurationManager";

  private static final String AUGMENTOR_SRC =
      "core/ide/src/main/java/org/alice/stageide/JointMethodAugmentor.java";
  private static final String MANAGER_SRC =
      "core/ide/src/main/java/org/alice/stageide/StoryApiConfigurationManager.java";

  private static Class<?> augmentorClazz;
  private static Class<?> managerClazz;

  @BeforeClass
  public static void loadClasses() {
    try {
      augmentorClazz = Class.forName(AUGMENTOR_FQCN);
    } catch (ClassNotFoundException e) {
      fail("JointMethodAugmentor not found — extraction not yet implemented: " + e.getMessage());
    }
    try {
      managerClazz = Class.forName(MANAGER_FQCN);
    } catch (ClassNotFoundException e) {
      fail("StoryApiConfigurationManager not found: " + e.getMessage());
    }
  }

  // ── 1. Class structure ───────────────────────────────────────────

  @Test
  public void isFinalClass() {
    assertTrue("JointMethodAugmentor must be final",
        Modifier.isFinal(augmentorClazz.getModifiers()));
  }

  @Test
  public void isPackagePrivate() {
    int mods = augmentorClazz.getModifiers();
    assertFalse("JointMethodAugmentor must not be public", Modifier.isPublic(mods));
    assertFalse("JointMethodAugmentor must not be private", Modifier.isPrivate(mods));
    assertFalse("JointMethodAugmentor must not be protected", Modifier.isProtected(mods));
  }

  @Test
  public void isNotAnEnum() {
    assertFalse("JointMethodAugmentor must be a class, not an enum", augmentorClazz.isEnum());
  }

  @Test
  public void isNotAnInterface() {
    assertFalse("JointMethodAugmentor must be a class, not an interface", augmentorClazz.isInterface());
  }

  // ── 2. Entry point method ────────────────────────────────────────

  @Test
  public void hasAugmentMethod() {
    boolean found = Arrays.stream(augmentorClazz.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("augment"));
    assertTrue("JointMethodAugmentor must declare an 'augment' method", found);
  }

  @Test
  public void augmentMethod_isStatic() {
    Method augment = findMethod("augment");
    assertNotNull("augment method must exist", augment);
    assertTrue("augment must be static (package-private static entry point)",
        Modifier.isStatic(augment.getModifiers()));
  }

  @Test
  public void augmentMethod_isPackagePrivateOrPublic() {
    Method augment = findMethod("augment");
    assertNotNull("augment method must exist", augment);
    // At minimum not private — accessible from same package
    assertFalse("augment must not be private",
        Modifier.isPrivate(augment.getModifiers()));
  }

  @Test
  public void augmentMethod_takesOneParameter() {
    Method augment = findMethod("augment");
    assertNotNull("augment method must exist", augment);
    assertEquals("augment must take exactly 1 parameter (UserType<?>)",
        1, augment.getParameterCount());
  }

  @Test
  public void augmentMethod_returnsUserType() {
    Method augment = findMethod("augment");
    assertNotNull("augment method must exist", augment);
    String returnTypeName = augment.getReturnType().getSimpleName();
    assertEquals("augment must return UserType", "UserType", returnTypeName);
  }

  // ── 3. Private helper methods are NOT public ─────────────────────

  @Test
  public void addMethodsToType_notPublic() {
    for (Method m : augmentorClazz.getDeclaredMethods()) {
      if ("addMethodsToType".equals(m.getName())) {
        assertFalse("addMethodsToType must not be public",
            Modifier.isPublic(m.getModifiers()));
      }
    }
  }

  @Test
  public void getFieldMethodNameHint_notPublic() {
    for (Method m : augmentorClazz.getDeclaredMethods()) {
      if ("getFieldMethodNameHint".equals(m.getName())) {
        assertFalse("getFieldMethodNameHint must not be public",
            Modifier.isPublic(m.getModifiers()));
      }
    }
  }

  @Test
  public void getArgumentField_notPublic() {
    for (Method m : augmentorClazz.getDeclaredMethods()) {
      if ("getArgumentField".equals(m.getName())) {
        assertFalse("getArgumentField must not be public",
            Modifier.isPublic(m.getModifiers()));
      }
    }
  }

  // ── 4. Source file existence and structure ────────────────────────

  @Test
  public void sourceFileExists() {
    Path src = resolveSourceFile(AUGMENTOR_SRC);
    assertTrue("JointMethodAugmentor.java must exist at " + AUGMENTOR_SRC, Files.exists(src));
  }

  @Test
  public void sourceHasCopyrightHeader() throws IOException {
    Path src = resolveSourceFile(AUGMENTOR_SRC);
    if (!Files.exists(src)) {
      fail("Source file not found");
    }
    String content = Files.readString(src);
    assertTrue("Must contain CMU copyright header",
        content.contains("Copyright (c)") && content.contains("Carnegie Mellon University"));
  }

  @Test
  public void sourceHasCorrectPackage() throws IOException {
    Path src = resolveSourceFile(AUGMENTOR_SRC);
    if (!Files.exists(src)) {
      fail("Source file not found");
    }
    boolean hasPackage = Files.lines(src)
        .anyMatch(line -> line.trim().equals("package org.alice.stageide;"));
    assertTrue("Must declare package org.alice.stageide", hasPackage);
  }

  @Test
  public void sourceNotDeclaredPublic() throws IOException {
    Path src = resolveSourceFile(AUGMENTOR_SRC);
    if (!Files.exists(src)) {
      fail("Source file not found");
    }
    boolean hasPublicClass = Files.lines(src)
        .anyMatch(line -> line.trim().startsWith("public final class JointMethodAugmentor")
            || line.trim().startsWith("public class JointMethodAugmentor"));
    assertFalse("JointMethodAugmentor must NOT be declared public", hasPublicClass);
  }

  // ── 5. Extracted methods removed from StoryApiConfigurationManager ──

  @Test
  public void managerSource_noJointedModelTypeConstant() throws IOException {
    Path src = resolveSourceFile(MANAGER_SRC);
    if (!Files.exists(src)) {
      fail("Manager source not found");
    }
    boolean found = Files.lines(src)
        .anyMatch(line -> line.contains("JOINTED_MODEL_TYPE") && line.contains("JavaType"));
    assertFalse("JOINTED_MODEL_TYPE constant must be moved out of StoryApiConfigurationManager", found);
  }

  @Test
  public void managerSource_noGetFieldMethodNameHint() throws IOException {
    Path src = resolveSourceFile(MANAGER_SRC);
    if (!Files.exists(src)) {
      fail("Manager source not found");
    }
    boolean found = Files.lines(src)
        .anyMatch(line -> line.contains("getFieldMethodNameHint("));
    assertFalse("getFieldMethodNameHint must be moved out of StoryApiConfigurationManager", found);
  }

  @Test
  public void managerSource_noAddMethodsToType() throws IOException {
    Path src = resolveSourceFile(MANAGER_SRC);
    if (!Files.exists(src)) {
      fail("Manager source not found");
    }
    boolean found = Files.lines(src)
        .anyMatch(line -> line.contains("private void addMethodsToType(")
            || line.contains("private static void addMethodsToType("));
    assertFalse("addMethodsToType methods must be moved out of StoryApiConfigurationManager", found);
  }

  @Test
  public void managerSource_noGetArgumentField() throws IOException {
    Path src = resolveSourceFile(MANAGER_SRC);
    if (!Files.exists(src)) {
      fail("Manager source not found");
    }
    boolean found = Files.lines(src)
        .anyMatch(line -> line.contains("getArgumentField("));
    assertFalse("getArgumentField must be moved out of StoryApiConfigurationManager", found);
  }

  // ── 6. Manager delegation ────────────────────────────────────────

  @Test
  public void managerSource_augmentTypeIfNecessary_delegatesToJointMethodAugmentor() throws IOException {
    Path src = resolveSourceFile(MANAGER_SRC);
    if (!Files.exists(src)) {
      fail("Manager source not found");
    }
    String content = Files.readString(src);
    assertTrue("augmentTypeIfNecessary must reference JointMethodAugmentor",
        content.contains("JointMethodAugmentor"));
  }

  @Test
  public void manager_stillDeclaresAugmentTypeIfNecessary() {
    boolean found = Arrays.stream(managerClazz.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("augmentTypeIfNecessary"));
    assertTrue("StoryApiConfigurationManager must still declare augmentTypeIfNecessary (as delegation stub)",
        found);
  }

  // ── 7. Required imports in new file ──────────────────────────────

  @Test
  public void augmentorSource_importsVisibility() throws IOException {
    Path src = resolveSourceFile(AUGMENTOR_SRC);
    if (!Files.exists(src)) {
      fail("Source file not found");
    }
    Set<String> imports = readImports(src);
    assertImportPresent(imports, "Visibility",
        "JointMethodAugmentor uses Visibility.COMPLETELY_HIDDEN");
  }

  @Test
  public void augmentorSource_importsReflectField() throws IOException {
    Path src = resolveSourceFile(AUGMENTOR_SRC);
    if (!Files.exists(src)) {
      fail("Source file not found");
    }
    Set<String> imports = readImports(src);
    assertImportPresent(imports, "Field",
        "JointMethodAugmentor uses java.lang.reflect.Field in getFieldMethodNameHint");
  }

  @Test
  public void augmentorSource_importsFieldTemplate() throws IOException {
    Path src = resolveSourceFile(AUGMENTOR_SRC);
    if (!Files.exists(src)) {
      fail("Source file not found");
    }
    Set<String> imports = readImports(src);
    assertImportPresent(imports, "FieldTemplate",
        "JointMethodAugmentor uses FieldTemplate annotation");
  }

  @Test
  public void augmentorSource_importsSJointedModel() throws IOException {
    Path src = resolveSourceFile(AUGMENTOR_SRC);
    if (!Files.exists(src)) {
      fail("Source file not found");
    }
    Set<String> imports = readImports(src);
    // May be explicit or wildcard (org.lgna.story.*)
    boolean found = imports.stream().anyMatch(line ->
        line.contains("SJointedModel") || line.contains("org.lgna.story.*"));
    assertTrue("JointMethodAugmentor must import SJointedModel (explicitly or via wildcard)", found);
  }

  @Test
  public void augmentorSource_importsLogger() throws IOException {
    Path src = resolveSourceFile(AUGMENTOR_SRC);
    if (!Files.exists(src)) {
      fail("Source file not found");
    }
    Set<String> imports = readImports(src);
    assertImportPresent(imports, "Logger",
        "JointMethodAugmentor uses Logger.severe for the error fallback path");
  }

  // ── 8. Manager import cleanup ────────────────────────────────────

  @Test
  public void managerSource_noImportVisibility() throws IOException {
    Path src = resolveSourceFile(MANAGER_SRC);
    if (!Files.exists(src)) {
      fail("Manager source not found");
    }
    Set<String> imports = readImports(src);
    boolean found = imports.stream().anyMatch(line ->
        line.contains(".Visibility;"));
    assertFalse("StoryApiConfigurationManager must no longer import Visibility (moved to augmentor)",
        found);
  }

  @Test
  public void managerSource_noImportReflectField() throws IOException {
    Path src = resolveSourceFile(MANAGER_SRC);
    if (!Files.exists(src)) {
      fail("Manager source not found");
    }
    Set<String> imports = readImports(src);
    boolean found = imports.stream().anyMatch(line ->
        line.contains("java.lang.reflect.Field"));
    assertFalse("StoryApiConfigurationManager must no longer import java.lang.reflect.Field (moved to augmentor)",
        found);
  }

  @Test
  public void managerSource_noImportFieldTemplate() throws IOException {
    Path src = resolveSourceFile(MANAGER_SRC);
    if (!Files.exists(src)) {
      fail("Manager source not found");
    }
    Set<String> imports = readImports(src);
    boolean found = imports.stream().anyMatch(line ->
        line.contains(".FieldTemplate;"));
    assertFalse("StoryApiConfigurationManager must no longer import FieldTemplate (moved to augmentor)",
        found);
  }

  // ── Helpers ──────────────────────────────────────────────────────

  private Method findMethod(String name) {
    return Arrays.stream(augmentorClazz.getDeclaredMethods())
        .filter(m -> m.getName().equals(name))
        .findFirst()
        .orElse(null);
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
