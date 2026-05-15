package test.ik;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * TDD contract tests for IkProgram dead-code removal and main() extraction.
 *
 * Issue #704: Reduce IkProgram.java from 527 lines to under 500 by:
 *   1. Removing 8 commented-out dead code blocks (~88 lines)
 *   2. Removing unused createDragProp() method (24 lines, zero callers)
 *   3. Extracting main() into new IkProgramLauncher class
 *   4. Changing initializeTest() from private to package-private
 *   5. Removing 6 orphaned imports (Color, SCone, SModel, MoveDirection, Turn, TurnDirection)
 *
 * These tests are written BEFORE the refactoring (TDD red phase).
 * They define the structural contract and will fail until implementation is complete.
 */
public class IkProgramExtractionContractTest {

  private static final String SRC_ROOT =
      "core/ide/src/main/java/test/ik/";

  private static Set<String> ikProgramImports;
  private static long ikProgramLineCount;

  @BeforeClass
  public static void loadSourceMetadata() throws IOException {
    Path ikProgramPath = resolveSourceFile(SRC_ROOT + "IkProgram.java");
    List<String> lines = Files.readAllLines(ikProgramPath);
    ikProgramLineCount = lines.size();
    ikProgramImports = lines.stream()
        .map(String::trim)
        .filter(line -> line.startsWith("import "))
        .collect(Collectors.toSet());
  }

  // ── 1. IkProgramLauncher exists with correct structure ────────────

  @Test
  public void ikProgramLauncher_classExists() {
    try {
      Class<?> c = Class.forName("test.ik.IkProgramLauncher");
      assertNotNull("IkProgramLauncher must be loadable", c);
    } catch (ClassNotFoundException e) {
      fail("IkProgramLauncher must exist in test.ik package: " + e.getMessage());
    }
  }

  @Test
  public void ikProgramLauncher_hasPublicStaticMainMethod() throws Exception {
    Class<?> c = Class.forName("test.ik.IkProgramLauncher");
    Method main = c.getDeclaredMethod("main", String[].class);
    assertTrue("main() must be public",
        Modifier.isPublic(main.getModifiers()));
    assertTrue("main() must be static",
        Modifier.isStatic(main.getModifiers()));
    assertEquals("main() must return void",
        void.class, main.getReturnType());
  }

  @Test
  public void ikProgramLauncher_isFinalClass() throws Exception {
    Class<?> c = Class.forName("test.ik.IkProgramLauncher");
    assertTrue("IkProgramLauncher must be final",
        Modifier.isFinal(c.getModifiers()));
  }

  @Test
  public void ikProgramLauncher_isPackagePrivate() throws Exception {
    Class<?> c = Class.forName("test.ik.IkProgramLauncher");
    int mods = c.getModifiers();
    assertFalse("IkProgramLauncher must not be public",
        Modifier.isPublic(mods));
    assertFalse("IkProgramLauncher must not be protected",
        Modifier.isProtected(mods));
    assertFalse("IkProgramLauncher must not be private",
        Modifier.isPrivate(mods));
  }

  @Test
  public void ikProgramLauncher_sourceFileExists() {
    Path launcherPath = resolveSourceFile(SRC_ROOT + "IkProgramLauncher.java");
    assertTrue("IkProgramLauncher.java source file must exist",
        Files.exists(launcherPath));
  }

  // ── 2. IkProgram no longer has extracted/deleted methods ──────────

  @Test
  public void ikProgram_noMainMethod() {
    try {
      Method main = IkProgram.class.getDeclaredMethod("main", String[].class);
      fail("IkProgram must not have a main() method — it was extracted to IkProgramLauncher");
    } catch (NoSuchMethodException expected) {
      // pass — main() has been extracted
    }
  }

  @Test
  public void ikProgram_noCreateDragPropMethod() {
    for (Method m : IkProgram.class.getDeclaredMethods()) {
      assertFalse(
          "IkProgram must not have createDragProp() — it was deleted as unused",
          "createDragProp".equals(m.getName()));
    }
  }

  // ── 3. initializeTest() visibility changed to package-private ────

  @Test
  public void ikProgram_initializeTestIsPackagePrivate() throws Exception {
    Method m = IkProgram.class.getDeclaredMethod("initializeTest");
    int mods = m.getModifiers();
    assertFalse("initializeTest() must not be private (needs launcher access)",
        Modifier.isPrivate(mods));
    assertFalse("initializeTest() must not be public (package-private is sufficient)",
        Modifier.isPublic(mods));
    assertFalse("initializeTest() must not be protected",
        Modifier.isProtected(mods));
  }

  // ── 4. Line count target ─────────────────────────────────────────

  @Test
  public void ikProgram_underFiveHundredLines() {
    assertTrue(
        "IkProgram.java must be under 500 lines (currently " + ikProgramLineCount + ")",
        ikProgramLineCount < 500);
  }

  // ── 5. Orphaned imports removed from IkProgram ───────────────────

  @Test
  public void ikProgram_noImport_Color() {
    assertImportAbsent(ikProgramImports, "Color",
        "only used by deleted createDragProp()");
  }

  @Test
  public void ikProgram_noImport_SCone() {
    assertImportAbsent(ikProgramImports, "SCone",
        "only used by deleted createDragProp()");
  }

  @Test
  public void ikProgram_noImport_SModel() {
    assertImportAbsent(ikProgramImports, "SModel",
        "only used as return type of deleted createDragProp()");
  }

  @Test
  public void ikProgram_noImport_MoveDirection() {
    assertImportAbsent(ikProgramImports, "MoveDirection",
        "only used by deleted createDragProp()");
  }

  @Test
  public void ikProgram_noImport_Turn() {
    assertImportAbsent(ikProgramImports, "Turn",
        "only used by deleted createDragProp()");
  }

  @Test
  public void ikProgram_noImport_TurnDirection() {
    assertImportAbsent(ikProgramImports, "TurnDirection",
        "only used by deleted createDragProp()");
  }

  // ── 6. IkProgram retains imports it still needs ──────────────────

  @Test
  public void ikProgram_retains_SBiped() {
    assertImportPresent(ikProgramImports, "SBiped",
        "IkProgram scene uses SBiped");
  }

  @Test
  public void ikProgram_retains_BipedResource() {
    assertImportPresent(ikProgramImports, "BipedResource",
        "used by initializeOldIkEnforcer() joint chain setup");
  }

  @Test
  public void ikProgram_retains_SSphere() {
    assertImportPresent(ikProgramImports, "SSphere",
        "used for target sphere in scene");
  }

  @Test
  public void ikProgram_retains_JointedModelIkEnforcer() {
    assertImportPresent(ikProgramImports, "JointedModelIkEnforcer",
        "core IK enforcer used by active code");
  }

  @Test
  public void ikProgram_retains_SProgram() {
    assertImportPresent(ikProgramImports, "SProgram",
        "IkProgram extends SProgram");
  }

  // ── 7. No residual dead code patterns ────────────────────────────

  @Test
  public void ikProgram_noCommentedOutConstraintsClass() throws IOException {
    Path path = resolveSourceFile(SRC_ROOT + "IkProgram.java");
    List<String> lines = Files.readAllLines(path);
    boolean hasCommentedClass = false;
    for (String line : lines) {
      String trimmed = line.trim();
      if (trimmed.contains("//  private class Constraints")
          || trimmed.contains("//  class Constraints")) {
        hasCommentedClass = true;
        break;
      }
    }
    assertFalse(
        "Commented-out Constraints inner class must be removed",
        hasCommentedClass);
  }

  @Test
  public void ikProgram_noCommentedOutCreateChainMethod() throws IOException {
    Path path = resolveSourceFile(SRC_ROOT + "IkProgram.java");
    List<String> lines = Files.readAllLines(path);
    boolean hasCommentedMethod = false;
    for (String line : lines) {
      String trimmed = line.trim();
      if (trimmed.startsWith("//") && trimmed.contains("createChain()")) {
        hasCommentedMethod = true;
        break;
      }
    }
    assertFalse(
        "Commented-out createChain() method must be removed",
        hasCommentedMethod);
  }

  @Test
  public void ikProgram_noCommentedOutHandleChainChangedOld() throws IOException {
    Path path = resolveSourceFile(SRC_ROOT + "IkProgram.java");
    List<String> lines = Files.readAllLines(path);
    boolean hasCommentedMethod = false;
    for (String line : lines) {
      String trimmed = line.trim();
      if (trimmed.contains("handleChainChanged_old")
          || trimmed.contains("handleChainChanged_Old")) {
        hasCommentedMethod = true;
        break;
      }
    }
    assertFalse(
        "Commented-out handleChainChanged_old() method must be removed",
        hasCommentedMethod);
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
    fail("Cannot find source file: " + relativePath + " from " + cwd);
    return null; // unreachable
  }

  private static void assertImportAbsent(Set<String> imports, String simpleTypeName, String reason) {
    boolean found = imports.stream().anyMatch(line ->
        line.contains("." + simpleTypeName + ";"));
    assertFalse("Import of " + simpleTypeName + " must be absent (" + reason + ")",
        found);
  }

  private static void assertImportPresent(Set<String> imports, String simpleTypeName, String reason) {
    boolean found = imports.stream().anyMatch(line ->
        line.contains("." + simpleTypeName + ";")
            || (line.endsWith(".*;") && line.contains("test.ik")));
    assertTrue("Import of " + simpleTypeName + " must be present (" + reason + ")",
        found);
  }
}
