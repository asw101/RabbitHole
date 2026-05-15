package org.alice.ide;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * TDD contract tests for the SceneSetupManager extraction from IDE.java (issue #706).
 *
 * These tests define the structural contract that the extraction must satisfy:
 * <ol>
 *   <li>SceneSetupManager exists as a package-private, final class</li>
 *   <li>It has the correct constructor and method signatures</li>
 *   <li>IDE delegates to it via a private final field</li>
 *   <li>IDE.java is reduced below 500 lines</li>
 *   <li>Imports that belonged only to extracted code are removed from IDE</li>
 * </ol>
 *
 * Pure reflection — no GUI, no singleton instantiation.
 */
public class SceneSetupManagerExtractionContractTest {

  private static final String SSM_FQCN = "org.alice.ide.SceneSetupManager";
  private static final String IDE_FQCN = "org.alice.ide.IDE";
  private static Class<?> ssmClazz;
  private static Class<?> ideClazz;

  @BeforeClass
  public static void loadClasses() {
    try {
      ssmClazz = Class.forName(SSM_FQCN);
    } catch (ClassNotFoundException e) {
      fail("SceneSetupManager class not found — extraction not yet implemented: " + e.getMessage());
    }
    try {
      ideClazz = Class.forName(IDE_FQCN);
    } catch (ClassNotFoundException e) {
      fail("IDE class not found: " + e.getMessage());
    }
  }

  // ── SceneSetupManager structural contract ────────────────────────

  @Test
  public void sceneSetupManager_isTopLevelClass() {
    assertNull("SceneSetupManager must be a top-level class (no enclosing class)",
        ssmClazz.getEnclosingClass());
  }

  @Test
  public void sceneSetupManager_isPackagePrivate() {
    int mods = ssmClazz.getModifiers();
    assertFalse("SceneSetupManager must not be public", Modifier.isPublic(mods));
    assertFalse("SceneSetupManager must not be private", Modifier.isPrivate(mods));
    assertFalse("SceneSetupManager must not be protected", Modifier.isProtected(mods));
  }

  @Test
  public void sceneSetupManager_isFinal() {
    assertTrue("SceneSetupManager must be final",
        Modifier.isFinal(ssmClazz.getModifiers()));
  }

  // ── Constructor contract ─────────────────────────────────────────

  @Test
  public void sceneSetupManager_hasConstructorTakingIDE() {
    try {
      Constructor<?> ctor = ssmClazz.getDeclaredConstructor(ideClazz);
      assertFalse("Constructor must be package-private (not public)",
          Modifier.isPublic(ctor.getModifiers()));
      assertFalse("Constructor must be package-private (not private)",
          Modifier.isPrivate(ctor.getModifiers()));
      assertFalse("Constructor must be package-private (not protected)",
          Modifier.isProtected(ctor.getModifiers()));
    } catch (NoSuchMethodException e) {
      fail("SceneSetupManager must have a constructor taking IDE: " + e.getMessage());
    }
  }

  // ── Method contracts ─────────────────────────────────────────────

  @Test
  public void sceneSetupManager_hasGenerateCodeForSceneSetUp() {
    assertSsmHasMethod("generateCodeForSceneSetUp");
  }

  @Test
  public void sceneSetupManager_generateCodeForSceneSetUp_returnsVoid() {
    Method m = findSsmMethod("generateCodeForSceneSetUp");
    assertNotNull("generateCodeForSceneSetUp must exist", m);
    assertEquals("generateCodeForSceneSetUp must return void",
        void.class, m.getReturnType());
  }

  @Test
  public void sceneSetupManager_generateCodeForSceneSetUp_isPackagePrivate() {
    Method m = findSsmMethod("generateCodeForSceneSetUp");
    assertNotNull(m);
    assertPackagePrivate("generateCodeForSceneSetUp", m.getModifiers());
  }

  @Test
  public void sceneSetupManager_hasReorganizeFieldsIfNecessary() {
    assertSsmHasMethod("reorganizeFieldsIfNecessary");
  }

  @Test
  public void sceneSetupManager_reorganizeFieldsIfNecessary_returnsVoid() {
    Method m = findSsmMethod("reorganizeFieldsIfNecessary");
    assertNotNull("reorganizeFieldsIfNecessary must exist", m);
    assertEquals("reorganizeFieldsIfNecessary must return void",
        void.class, m.getReturnType());
  }

  @Test
  public void sceneSetupManager_reorganizeFieldsIfNecessary_isPackagePrivate() {
    Method m = findSsmMethod("reorganizeFieldsIfNecessary");
    assertNotNull(m);
    assertPackagePrivate("reorganizeFieldsIfNecessary", m.getModifiers());
  }

  @Test
  public void sceneSetupManager_hasReorganizeTypeFieldsIfNecessary() {
    assertSsmHasMethod("reorganizeTypeFieldsIfNecessary");
  }

  @Test
  public void sceneSetupManager_reorganizeTypeFieldsIfNecessary_returnsString() {
    Method m = findSsmMethod("reorganizeTypeFieldsIfNecessary");
    assertNotNull("reorganizeTypeFieldsIfNecessary must exist", m);
    assertEquals("reorganizeTypeFieldsIfNecessary must return String",
        String.class, m.getReturnType());
  }

  @Test
  public void sceneSetupManager_reorganizeTypeFieldsIfNecessary_takesThreeParams() {
    Method m = findSsmMethod("reorganizeTypeFieldsIfNecessary");
    assertNotNull(m);
    Class<?>[] params = m.getParameterTypes();
    assertEquals("reorganizeTypeFieldsIfNecessary must take 3 parameters", 3, params.length);
    assertEquals("First param must be NamedUserType",
        "org.lgna.project.ast.NamedUserType", params[0].getName());
    assertEquals("Second param must be int", int.class, params[1]);
    assertEquals("Third param must be Set",
        java.util.Set.class, params[2]);
  }

  // ── UnacceptableFieldAccessCrawler nested class ──────────────────

  @Test
  public void sceneSetupManager_containsUnacceptableFieldAccessCrawler() {
    Class<?>[] innerClasses = ssmClazz.getDeclaredClasses();
    boolean found = Arrays.stream(innerClasses)
        .anyMatch(c -> c.getSimpleName().equals("UnacceptableFieldAccessCrawler"));
    assertTrue("SceneSetupManager must contain UnacceptableFieldAccessCrawler as a nested class",
        found);
  }

  @Test
  public void unacceptableFieldAccessCrawler_isPrivateStatic() {
    Class<?> inner = Arrays.stream(ssmClazz.getDeclaredClasses())
        .filter(c -> c.getSimpleName().equals("UnacceptableFieldAccessCrawler"))
        .findFirst()
        .orElse(null);
    assertNotNull("UnacceptableFieldAccessCrawler must exist", inner);
    int mods = inner.getModifiers();
    assertTrue("UnacceptableFieldAccessCrawler must be private",
        Modifier.isPrivate(mods));
    assertTrue("UnacceptableFieldAccessCrawler must be static",
        Modifier.isStatic(mods));
  }

  // ── IDE delegation field ─────────────────────────────────────────

  @Test
  public void ide_hasSceneSetupManagerField() {
    try {
      ideClazz.getDeclaredField("sceneSetupManager");
    } catch (NoSuchFieldException e) {
      fail("IDE must have a 'sceneSetupManager' field");
    }
  }

  @Test
  public void ide_sceneSetupManagerField_isCorrectType() {
    try {
      Field f = ideClazz.getDeclaredField("sceneSetupManager");
      assertEquals("sceneSetupManager field must be of type SceneSetupManager",
          SSM_FQCN, f.getType().getName());
    } catch (NoSuchFieldException e) {
      fail("IDE must have a 'sceneSetupManager' field");
    }
  }

  @Test
  public void ide_sceneSetupManagerField_isPrivateFinal() {
    try {
      Field f = ideClazz.getDeclaredField("sceneSetupManager");
      int mods = f.getModifiers();
      assertTrue("sceneSetupManager must be private", Modifier.isPrivate(mods));
      assertTrue("sceneSetupManager must be final", Modifier.isFinal(mods));
    } catch (NoSuchFieldException e) {
      fail("IDE must have a 'sceneSetupManager' field");
    }
  }

  // ── IDE no longer contains extracted methods ─────────────────────

  @Test
  public void ide_doesNotDeclareReorganizeTypeFieldsIfNecessary() {
    Method m = findMethodOnClass(ideClazz, "reorganizeTypeFieldsIfNecessary");
    assertNull(
        "reorganizeTypeFieldsIfNecessary must be removed from IDE (moved to SceneSetupManager)",
        m);
  }

  @Test
  public void ide_doesNotDeclareReorganizeFieldsIfNecessary() {
    Method m = findMethodOnClass(ideClazz, "reorganizeFieldsIfNecessary");
    assertNull(
        "reorganizeFieldsIfNecessary must be removed from IDE (moved to SceneSetupManager)",
        m);
  }

  @Test
  public void ide_doesNotDeclareGenerateCodeForSceneSetUp() {
    Method m = findMethodOnClass(ideClazz, "generateCodeForSceneSetUp");
    assertNull(
        "generateCodeForSceneSetUp must be removed from IDE (moved to SceneSetupManager)",
        m);
  }

  @Test
  public void ide_doesNotContainUnacceptableFieldAccessCrawler() {
    boolean found = Arrays.stream(ideClazz.getDeclaredClasses())
        .anyMatch(c -> c.getSimpleName().equals("UnacceptableFieldAccessCrawler"));
    assertFalse(
        "UnacceptableFieldAccessCrawler must be removed from IDE (moved to SceneSetupManager)",
        found);
  }

  // ── IDE retains its public/protected API ─────────────────────────

  @Test
  public void ide_retainsUpdateProjectMethod() {
    Method m = findMethodOnClass(ideClazz, "updateProject");
    assertNotNull("IDE must still declare updateProject", m);
  }

  @Test
  public void ide_retainsEnsureProjectCodeUpToDate() {
    Method m = findMethodOnClass(ideClazz, "ensureProjectCodeUpToDate");
    assertNotNull("IDE must still declare ensureProjectCodeUpToDate", m);
  }

  @Test
  public void ide_retainsForceProjectCodeUpToDate() {
    Method m = findMethodOnClass(ideClazz, "forceProjectCodeUpToDate");
    assertNotNull("IDE must still declare forceProjectCodeUpToDate", m);
  }

  @Test
  public void ide_retainsCrawlFilteredProgramType() {
    Method m = findMethodOnClass(ideClazz, "crawlFilteredProgramType");
    assertNotNull("IDE must still declare crawlFilteredProgramType", m);
  }

  @Test
  public void ide_retainsGetProgramType() {
    Method m = findMethodOnClass(ideClazz, "getProgramType");
    assertNotNull("IDE must still declare getProgramType", m);
  }

  @Test
  public void ide_retainsGetPerformEditorGeneratedSetUpMethod() {
    Method m = findMethodOnClass(ideClazz, "getPerformEditorGeneratedSetUpMethod");
    assertNotNull("IDE must still declare getPerformEditorGeneratedSetUpMethod", m);
  }

  @Test
  public void ide_retainsGetSceneEditor() {
    Method m = findMethodOnClass(ideClazz, "getSceneEditor");
    assertNotNull("IDE must still declare getSceneEditor", m);
  }

  // ── IDE line count target ────────────────────────────────────────

  @Test
  public void ide_isUnder500Lines() throws URISyntaxException, IOException {
    URL url = ideClazz.getResource("IDE.java");
    if (url == null) {
      // Fall back: locate from source tree relative to class file
      URL classUrl = ideClazz.getResource(ideClazz.getSimpleName() + ".class");
      assertNotNull("Could not locate IDE.class to find source", classUrl);

      // Walk up from target/classes to src/main/java
      Path classPath = Paths.get(classUrl.toURI());
      Path projectRoot = classPath;
      while (projectRoot != null && !projectRoot.endsWith("core")) {
        projectRoot = projectRoot.getParent();
      }
      if (projectRoot == null) {
        // Can't reliably locate source — skip this test
        return;
      }
      Path sourceFile = projectRoot.resolve("ide/src/main/java/org/alice/ide/IDE.java");
      if (!Files.exists(sourceFile)) {
        // Source not co-located with build output; skip gracefully
        return;
      }
      long lineCount = Files.lines(sourceFile).count();
      assertTrue("IDE.java must be under 500 lines, but has " + lineCount + " lines",
          lineCount < 500);
      return;
    }
    long lineCount = Files.lines(Paths.get(url.toURI())).count();
    assertTrue("IDE.java must be under 500 lines, but has " + lineCount + " lines",
        lineCount < 500);
  }

  // ── Import cleanup verification ──────────────────────────────────

  @Test
  public void ide_doesNotImportSets() throws Exception {
    assertIdeSourceDoesNotContain("import edu.cmu.cs.dennisc.java.util.Sets;",
        "Sets import should be removed from IDE (used only by extracted code)");
  }

  @Test
  public void ide_doesNotImportDialogs() throws Exception {
    assertIdeSourceDoesNotContain("import edu.cmu.cs.dennisc.javax.swing.option.Dialogs;",
        "Dialogs import should be removed from IDE (used only by extracted code)");
  }

  @Test
  public void ide_doesNotImportIsInstanceCrawler() throws Exception {
    assertIdeSourceDoesNotContain("import edu.cmu.cs.dennisc.pattern.IsInstanceCrawler;",
        "IsInstanceCrawler import should be removed from IDE (used only by extracted code)");
  }

  @Test
  public void ide_doesNotImportStatementListProperty() throws Exception {
    assertIdeSourceDoesNotContain("import org.lgna.project.ast.StatementListProperty;",
        "StatementListProperty import should be removed from IDE (used only by extracted code)");
  }

  @Test
  public void ide_doesNotImportUserField() throws Exception {
    assertIdeSourceDoesNotContain("import org.lgna.project.ast.UserField;",
        "UserField import should be removed from IDE (used only by extracted code)");
  }

  @Test
  public void ide_doesNotImportJavaUtilSet() throws Exception {
    assertIdeSourceDoesNotContain("import java.util.Set;",
        "java.util.Set import should be removed from IDE (used only by extracted code)");
  }

  // ── IDE retains required imports ─────────────────────────────────

  @Test
  public void ide_retainsCommentImport() throws Exception {
    assertIdeSourceContains("import org.lgna.project.ast.Comment;",
        "Comment import must remain — used by commentThatWantsFocus");
  }

  @Test
  public void ide_retainsFieldAccessImport() throws Exception {
    assertIdeSourceContains("import org.lgna.project.ast.FieldAccess;",
        "FieldAccess import must remain — used by getPrefixPaneForFieldAccessIfAppropriate");
  }

  @Test
  public void ide_retainsCrawlerImport() throws Exception {
    assertIdeSourceContains("import edu.cmu.cs.dennisc.pattern.Crawler;",
        "Crawler import must remain — used by crawlFilteredProgramType");
  }

  @Test
  public void ide_retainsCrawlPolicyImport() throws Exception {
    assertIdeSourceContains("import org.lgna.project.ast.CrawlPolicy;",
        "CrawlPolicy import must remain — used by crawlFilteredProgramType");
  }

  @Test
  public void ide_retainsUserMethodImport() throws Exception {
    assertIdeSourceContains("import org.lgna.project.ast.UserMethod;",
        "UserMethod import must remain — used by abstract getPerformEditorGeneratedSetUpMethod");
  }

  // ── Helpers ──────────────────────────────────────────────────────

  private void assertSsmHasMethod(String methodName) {
    Method m = findSsmMethod(methodName);
    assertNotNull("SceneSetupManager must declare method: " + methodName, m);
  }

  private Method findSsmMethod(String name) {
    return findMethodOnClass(ssmClazz, name);
  }

  private static Method findMethodOnClass(Class<?> clazz, String name) {
    return Arrays.stream(clazz.getDeclaredMethods())
        .filter(m -> m.getName().equals(name))
        .findFirst()
        .orElse(null);
  }

  private static void assertPackagePrivate(String label, int modifiers) {
    assertFalse(label + " must not be public", Modifier.isPublic(modifiers));
    assertFalse(label + " must not be private", Modifier.isPrivate(modifiers));
    assertFalse(label + " must not be protected", Modifier.isProtected(modifiers));
  }

  private static Path findIdeSourceFile() {
    // Locate IDE.java source via the class file location and walk to source tree
    URL classUrl = IDE.class.getResource(IDE.class.getSimpleName() + ".class");
    if (classUrl == null) {
      return null;
    }
    try {
      Path classPath = Paths.get(classUrl.toURI());
      Path current = classPath;
      while (current != null && !current.getFileName().toString().equals("core")) {
        current = current.getParent();
      }
      if (current == null) {
        return null;
      }
      Path source = current.resolve("ide/src/main/java/org/alice/ide/IDE.java");
      return Files.exists(source) ? source : null;
    } catch (URISyntaxException e) {
      return null;
    }
  }

  private void assertIdeSourceDoesNotContain(String text, String message) throws Exception {
    Path source = findIdeSourceFile();
    if (source == null) {
      // Source not co-located; skip gracefully
      return;
    }
    String content = Files.readString(source);
    assertFalse(message, content.contains(text));
  }

  private void assertIdeSourceContains(String text, String message) throws Exception {
    Path source = findIdeSourceFile();
    if (source == null) {
      // Source not co-located; skip gracefully
      return;
    }
    String content = Files.readString(source);
    assertTrue(message, content.contains(text));
  }
}
