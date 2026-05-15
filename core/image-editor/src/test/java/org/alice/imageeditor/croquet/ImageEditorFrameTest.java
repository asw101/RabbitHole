package org.alice.imageeditor.croquet;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Characterization tests for ImageEditorFrame.
 *
 * These tests capture the existing API surface and structural properties
 * so that dead-code removal (or any future refactoring) can be verified
 * as behavior-preserving.
 */
public class ImageEditorFrameTest {

  private static final String SOURCE_RESOURCE =
      "/org/alice/imageeditor/croquet/ImageEditorFrame.java";

  // --- Static constant characterization ---

  @Test
  public void invalidPathNotADirectoryConstantHasExpectedValue() {
    assertEquals("INVALID_PATH_NOT_A_DIRECTORY", ImageEditorFrame.INVALID_PATH_NOT_A_DIRECTORY);
  }

  @Test
  public void invalidPathEmptySubPathConstantHasExpectedValue() {
    assertEquals("INVALID_PATH_EMPTY_SUB_PATH", ImageEditorFrame.INVALID_PATH_EMPTY_SUB_PATH);
  }

  // --- Public API surface characterization (via reflection) ---

  @Test
  public void publicMethodSetMatchesBaseline() {
    Set<String> expected = new TreeSet<>(Arrays.asList(
        "getImageHolder",
        "getPathHolder",
        "getCropSelectHolder",
        "getCropCommitHolder",
        "getToolState",
        "getCropOperation",
        "getUncropOperation",
        "getClearOperation",
        "getCopyOperation",
        "getShowDashedBorderState",
        "getShowInScreenResolutionState",
        "getDropShadowState",
        "getRootDirectoryState",
        "getJComboBox",
        "getBrowseOperation",
        "getSaveOperation",
        "addShape",
        "removeShape",
        "clearShapes",
        "getShapes",
        "setImageClearShapesAndShowFrame",
        "getFile",
        "handlePreActivation",
        "handlePostDeactivation"
    ));

    Set<String> actual = new TreeSet<>();
    for (Method m : ImageEditorFrame.class.getDeclaredMethods()) {
      if (Modifier.isPublic(m.getModifiers())) {
        actual.add(m.getName());
      }
    }

    assertEquals("Public API surface must not change", expected, actual);
  }

  @Test
  public void getImageHolderReturnsCorrectType() throws NoSuchMethodException {
    Method m = ImageEditorFrame.class.getMethod("getImageHolder");
    assertEquals("org.lgna.croquet.ValueHolder", m.getReturnType().getName());
  }

  @Test
  public void getShapesReturnsListType() throws NoSuchMethodException {
    Method m = ImageEditorFrame.class.getMethod("getShapes");
    assertEquals(java.util.List.class, m.getReturnType());
  }

  @Test
  public void getFileReturnsFileType() throws NoSuchMethodException {
    Method m = ImageEditorFrame.class.getMethod("getFile");
    assertEquals(java.io.File.class, m.getReturnType());
  }

  // --- Structural verification ---

  @Test
  public void sourceFileHasFewerThan500Lines() throws IOException {
    int lineCount = countSourceLines();
    assertTrue(
        "ImageEditorFrame.java should be under 500 lines, but has " + lineCount,
        lineCount < 500);
  }

  @Test
  public void sourceFileContainsNoCommentedOutMainMethod() throws IOException {
    List<String> lines = readSourceLines();
    boolean hasCommentedMain = lines.stream()
        .anyMatch(line -> line.contains("public static void main") && line.trim().startsWith("//"));
    assertFalse(
        "ImageEditorFrame.java should not contain a commented-out main() method",
        hasCommentedMain);
  }

  @Test
  public void classExtendsFrameCompositeWithInternalIsShowingState() {
    Class<?> superclass = ImageEditorFrame.class.getSuperclass();
    assertNotNull(superclass);
    assertEquals(
        "org.lgna.croquet.FrameCompositeWithInternalIsShowingState",
        superclass.getName());
  }

  // --- Helpers ---

  private int countSourceLines() throws IOException {
    return readSourceLines().size();
  }

  private List<String> readSourceLines() throws IOException {
    // Read the source file bundled as a test resource
    InputStream is = getClass().getResourceAsStream(SOURCE_RESOURCE);
    if (is == null) {
      // Fall back to reading from the known file-system path relative to module root
      java.io.File sourceFile = findSourceFile();
      is = new java.io.FileInputStream(sourceFile);
    }
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
      return reader.lines().collect(Collectors.toList());
    }
  }

  private java.io.File findSourceFile() {
    // Walk up from the test class output dir to find the module root
    String userDir = System.getProperty("user.dir");
    java.io.File candidate = new java.io.File(userDir,
        "core/image-editor/src/main/java/org/alice/imageeditor/croquet/ImageEditorFrame.java");
    if (!candidate.exists()) {
      // Try from the module root directly
      candidate = new java.io.File(
          "src/main/java/org/alice/imageeditor/croquet/ImageEditorFrame.java");
    }
    assertTrue("Could not find ImageEditorFrame.java source at " + candidate.getAbsolutePath(),
        candidate.exists());
    return candidate;
  }
}
