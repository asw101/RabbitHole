package org.lgna.croquet.views;

import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TDD structural contract tests for the FolderTabbedPane inner-class extraction.
 *
 * <p>These tests verify the extraction acceptance criteria:
 * <ul>
 *   <li>FolderTabbedPane.java is reduced to under 500 lines</li>
 *   <li>New files FolderTabRenderer.java and FolderTitlesPanel.java exist</li>
 *   <li>Constants TRAILING_TAB_PAD and OUTLINE_THICKNESS are package-private and shared</li>
 *   <li>createTitlesPanel() factory returns FolderTitlesPanel</li>
 *   <li>titlesPanel field type is FolderTitlesPanel (not the old TitlesPanel inner class)</li>
 *   <li>No TitlesPanel inner class remains in FolderTabbedPane</li>
 *   <li>No FolderTabTitleUI/JFolderTabTitle/FolderTabTitle inner classes remain</li>
 * </ul>
 *
 * <p>These tests will FAIL until the extraction is implemented.</p>
 */
public class FolderTabbedPaneExtractionTest {

  private static final String VIEWS_PACKAGE = "org.lgna.croquet.views";
  private static final String SOURCE_DIR = "core/croquet/src/main/java/org/lgna/croquet/views";

  // ── Line count acceptance criterion ──────────────────────────────────

  @Test
  public void folderTabbedPane_underFiveHundredLines() throws IOException {
    Path source = findSourceFile("FolderTabbedPane.java");
    long lineCount = Files.lines(source).count();
    assertTrue("FolderTabbedPane.java must be under 500 lines. Actual: " + lineCount,
        lineCount < 500);
  }

  // ── New files exist ──────────────────────────────────────────────────

  @Test
  public void folderTabRenderer_fileExists() {
    Path source = findSourceFile("FolderTabRenderer.java");
    assertTrue("FolderTabRenderer.java must exist at " + source, Files.exists(source));
  }

  @Test
  public void folderTitlesPanel_fileExists() {
    Path source = findSourceFile("FolderTitlesPanel.java");
    assertTrue("FolderTitlesPanel.java must exist at " + source, Files.exists(source));
  }

  // ── Extracted classes are loadable ───────────────────────────────────

  @Test
  public void folderTabTitleUI_classLoads() throws ClassNotFoundException {
    Class.forName(VIEWS_PACKAGE + ".FolderTabTitleUI");
  }

  @Test
  public void jFolderTabTitle_classLoads() throws ClassNotFoundException {
    Class.forName(VIEWS_PACKAGE + ".JFolderTabTitle");
  }

  @Test
  public void folderTabTitle_classLoads() throws ClassNotFoundException {
    Class.forName(VIEWS_PACKAGE + ".FolderTabTitle");
  }

  @Test
  public void folderTitlesPanel_classLoads() throws ClassNotFoundException {
    Class.forName(VIEWS_PACKAGE + ".FolderTitlesPanel");
  }

  // ── Inner classes removed from FolderTabbedPane ──────────────────────

  @Test
  public void folderTabbedPane_noFolderTabTitleUIInnerClass() {
    assertInnerClassAbsent(FolderTabbedPane.class, "FolderTabTitleUI",
        "FolderTabTitleUI must be extracted, not remain as inner class");
  }

  @Test
  public void folderTabbedPane_noJFolderTabTitleInnerClass() {
    assertInnerClassAbsent(FolderTabbedPane.class, "JFolderTabTitle",
        "JFolderTabTitle must be extracted, not remain as inner class");
  }

  @Test
  public void folderTabbedPane_noFolderTabTitleInnerClass() {
    assertInnerClassAbsent(FolderTabbedPane.class, "FolderTabTitle",
        "FolderTabTitle must be extracted, not remain as inner class");
  }

  @Test
  public void folderTabbedPane_noTitlesPanelInnerClass() {
    assertInnerClassAbsent(FolderTabbedPane.class, "TitlesPanel",
        "TitlesPanel must be promoted to FolderTitlesPanel, not remain as inner class");
  }

  // ── Constants are package-private (not private) ──────────────────────

  @Test
  public void trailingTabPad_isPackagePrivate() throws Exception {
    Field field = FolderTabbedPane.class.getDeclaredField("TRAILING_TAB_PAD");
    int modifiers = field.getModifiers();
    assertTrue("TRAILING_TAB_PAD must be static", Modifier.isStatic(modifiers));
    assertTrue("TRAILING_TAB_PAD must be final", Modifier.isFinal(modifiers));
    assertFalse("TRAILING_TAB_PAD must not be private (needs cross-file access)",
        Modifier.isPrivate(modifiers));
    assertFalse("TRAILING_TAB_PAD must not be public", Modifier.isPublic(modifiers));
    assertFalse("TRAILING_TAB_PAD must not be protected", Modifier.isProtected(modifiers));
  }

  @Test
  public void outlineThickness_isPackagePrivate() throws Exception {
    Field field = FolderTabbedPane.class.getDeclaredField("OUTLINE_THICKNESS");
    int modifiers = field.getModifiers();
    assertTrue("OUTLINE_THICKNESS must be static", Modifier.isStatic(modifiers));
    assertTrue("OUTLINE_THICKNESS must be final", Modifier.isFinal(modifiers));
    assertFalse("OUTLINE_THICKNESS must not be private (needs cross-file access)",
        Modifier.isPrivate(modifiers));
  }

  @Test
  public void trailingTabPad_valueIs32() throws Exception {
    Field field = FolderTabbedPane.class.getDeclaredField("TRAILING_TAB_PAD");
    field.setAccessible(true);
    assertEquals("TRAILING_TAB_PAD must equal 32", 32, field.getInt(null));
  }

  @Test
  public void outlineThickness_valueIs1() throws Exception {
    Field field = FolderTabbedPane.class.getDeclaredField("OUTLINE_THICKNESS");
    field.setAccessible(true);
    assertEquals("OUTLINE_THICKNESS must equal 1", 1, field.getInt(null));
  }

  // ── titlesPanel field type is FolderTitlesPanel ──────────────────────

  @Test
  public void titlesPanelField_isFolderTitlesPanel() throws Exception {
    Class<?> folderTitlesPanel = Class.forName(VIEWS_PACKAGE + ".FolderTitlesPanel");
    Field field = FolderTabbedPane.class.getDeclaredField("titlesPanel");
    assertEquals("titlesPanel field type must be FolderTitlesPanel (not TitlesPanel)",
        folderTitlesPanel, field.getType());
  }

  // ── createTitlesPanel factory return type ────────────────────────────

  @Test
  public void createTitlesPanel_returnTypeisFolderTitlesPanel() throws Exception {
    Class<?> folderTitlesPanel = Class.forName(VIEWS_PACKAGE + ".FolderTitlesPanel");
    java.lang.reflect.Method method = FolderTabbedPane.class.getDeclaredMethod("createTitlesPanel");
    assertEquals("createTitlesPanel() return type must be FolderTitlesPanel",
        folderTitlesPanel, method.getReturnType());
  }

  // ── PopupOperation/PopupButton/ScrollListener still in FolderTabbedPane ──

  @Test
  public void folderTabbedPane_retainsPopupOperationInnerClass() {
    assertInnerClassPresent(FolderTabbedPane.class, "PopupOperation",
        "PopupOperation must remain in FolderTabbedPane (not extracted)");
  }

  @Test
  public void folderTabbedPane_retainsPopupButtonInnerClass() {
    assertInnerClassPresent(FolderTabbedPane.class, "PopupButton",
        "PopupButton must remain in FolderTabbedPane (not extracted)");
  }

  @Test
  public void folderTabbedPane_retainsScrollListenerInnerClass() {
    assertInnerClassPresent(FolderTabbedPane.class, "ScrollListener",
        "ScrollListener must remain in FolderTabbedPane (not extracted)");
  }

  // ── Helper methods ──────────────────────────────────────────────────

  private static void assertInnerClassAbsent(Class<?> outer, String innerName, String message) {
    for (Class<?> inner : outer.getDeclaredClasses()) {
      if (inner.getSimpleName().equals(innerName)) {
        fail(message + " (found " + inner.getName() + ")");
      }
    }
  }

  private static void assertInnerClassPresent(Class<?> outer, String innerName, String message) {
    for (Class<?> inner : outer.getDeclaredClasses()) {
      if (inner.getSimpleName().equals(innerName)) {
        return;
      }
    }
    fail(message + " (not found in " + outer.getName() + ")");
  }

  /**
   * Finds a source file relative to the repository root. Walks up from CWD
   * to locate the git root, then resolves the file path.
   */
  private static Path findSourceFile(String filename) {
    // Try standard relative path from repo root
    Path candidate = Paths.get(SOURCE_DIR, filename);
    if (Files.exists(candidate)) {
      return candidate;
    }
    // Try from CWD (may be a worktree)
    Path cwd = Paths.get(System.getProperty("user.dir"));
    candidate = cwd.resolve(SOURCE_DIR).resolve(filename);
    if (Files.exists(candidate)) {
      return candidate;
    }
    // Walk up to find repo root (look for .git)
    Path dir = cwd;
    while (dir != null) {
      if (Files.exists(dir.resolve(".git"))) {
        candidate = dir.resolve(SOURCE_DIR).resolve(filename);
        if (Files.exists(candidate)) {
          return candidate;
        }
        break;
      }
      dir = dir.getParent();
    }
    // Return the relative path even if it doesn't exist (test will fail with clear message)
    return Paths.get(SOURCE_DIR, filename);
  }
}
