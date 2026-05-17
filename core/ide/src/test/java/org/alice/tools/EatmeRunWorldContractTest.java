package org.alice.tools;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import static org.junit.Assert.*;

/**
 * Tests for {@link EatmeRunWorld} — argument parsing, JSON escaping,
 * artifact path validation, and CLI error handling.
 */
public class EatmeRunWorldContractTest {

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  // ---- escapeJson ----

  @Test
  public void escapeJson_plainString_unchanged() {
    assertEquals("hello world", EatmeRunWorld.escapeJson("hello world"));
  }

  @Test
  public void escapeJson_backslash() {
    assertEquals("a\\\\b", EatmeRunWorld.escapeJson("a\\b"));
  }

  @Test
  public void escapeJson_doubleQuote() {
    assertEquals("a\\\"b", EatmeRunWorld.escapeJson("a\"b"));
  }

  @Test
  public void escapeJson_newline() {
    assertEquals("a\\nb", EatmeRunWorld.escapeJson("a\nb"));
  }

  @Test
  public void escapeJson_tab() {
    assertEquals("a\\tb", EatmeRunWorld.escapeJson("a\tb"));
  }

  @Test
  public void escapeJson_carriageReturn() {
    assertEquals("a\\rb", EatmeRunWorld.escapeJson("a\rb"));
  }

  @Test
  public void escapeJson_backspace() {
    assertEquals("a\\bb", EatmeRunWorld.escapeJson("a\bb"));
  }

  @Test
  public void escapeJson_formFeed() {
    assertEquals("a\\fb", EatmeRunWorld.escapeJson("a\fb"));
  }

  @Test
  public void escapeJson_controlChar() {
    String result = EatmeRunWorld.escapeJson("a\u0001b");
    assertEquals("a\\u0001b", result);
  }

  @Test
  public void escapeJson_emptyString() {
    assertEquals("", EatmeRunWorld.escapeJson(""));
  }

  @Test
  public void escapeJson_xssVector() {
    String input = "<script>alert(\"xss\")</script>";
    String escaped = EatmeRunWorld.escapeJson(input);
    assertTrue(escaped.contains("\\\"xss\\\""));
    assertFalse(escaped.contains("\"xss\""));
  }

  // ---- artifactPath ----

  @Test
  public void artifactPath_simpleFilename() {
    Path evidenceDir = tempFolder.getRoot().toPath();
    Path result = EatmeRunWorld.artifactPath(evidenceDir, "world-run.json");
    assertEquals(evidenceDir.resolve("world-run.json"), result);
  }

  @Test(expected = IllegalArgumentException.class)
  public void artifactPath_rejectsAbsolutePath() {
    Path evidenceDir = tempFolder.getRoot().toPath();
    EatmeRunWorld.artifactPath(evidenceDir, "/etc/passwd");
  }

  @Test(expected = IllegalArgumentException.class)
  public void artifactPath_rejectsDirectoryTraversal() {
    Path evidenceDir = tempFolder.getRoot().toPath();
    EatmeRunWorld.artifactPath(evidenceDir, "../escape.json");
  }

  @Test(expected = IllegalArgumentException.class)
  public void artifactPath_rejectsNestedPath() {
    Path evidenceDir = tempFolder.getRoot().toPath();
    EatmeRunWorld.artifactPath(evidenceDir, "sub/file.json");
  }

  // ---- run (CLI integration) ----

  @Test
  public void run_noArgs_returnsErrorCode() {
    PrintStream out = new PrintStream(new ByteArrayOutputStream());
    PrintStream err = new PrintStream(new ByteArrayOutputStream());
    int status = EatmeRunWorld.run(new String[0], out, err);
    assertEquals(2, status);
  }

  @Test
  public void run_missingProject_returnsErrorCode() {
    PrintStream out = new PrintStream(new ByteArrayOutputStream());
    PrintStream err = new PrintStream(new ByteArrayOutputStream());
    int status = EatmeRunWorld.run(new String[]{
        "--run-selector", "scene.test",
        "--evidence-dir", tempFolder.getRoot().toString(),
        "--json"
    }, out, err);
    assertEquals(2, status);
  }

  @Test
  public void run_missingRunSelector_returnsErrorCode() {
    PrintStream out = new PrintStream(new ByteArrayOutputStream());
    PrintStream err = new PrintStream(new ByteArrayOutputStream());
    int status = EatmeRunWorld.run(new String[]{
        "--project", "/nonexistent/project.a3p",
        "--evidence-dir", tempFolder.getRoot().toString(),
        "--json"
    }, out, err);
    assertEquals(2, status);
  }

  @Test
  public void run_missingEvidenceDir_returnsErrorCode() {
    PrintStream out = new PrintStream(new ByteArrayOutputStream());
    PrintStream err = new PrintStream(new ByteArrayOutputStream());
    int status = EatmeRunWorld.run(new String[]{
        "--project", "/nonexistent/project.a3p",
        "--run-selector", "scene.test",
        "--json"
    }, out, err);
    assertEquals(2, status);
  }

  @Test
  public void run_missingJsonFlag_returnsErrorCode() {
    PrintStream out = new PrintStream(new ByteArrayOutputStream());
    PrintStream err = new PrintStream(new ByteArrayOutputStream());
    int status = EatmeRunWorld.run(new String[]{
        "--project", "/nonexistent/project.a3p",
        "--run-selector", "scene.test",
        "--evidence-dir", tempFolder.getRoot().toString()
    }, out, err);
    assertEquals(2, status);
  }

  @Test
  public void run_unsupportedArg_returnsErrorCode() {
    PrintStream out = new PrintStream(new ByteArrayOutputStream());
    PrintStream err = new PrintStream(new ByteArrayOutputStream());
    int status = EatmeRunWorld.run(new String[]{
        "--unknown-flag"
    }, out, err);
    assertEquals(2, status);
  }

  @Test
  public void run_nonExistentProjectFile_returnsErrorCode() {
    PrintStream out = new PrintStream(new ByteArrayOutputStream());
    PrintStream err = new PrintStream(new ByteArrayOutputStream());
    int status = EatmeRunWorld.run(new String[]{
        "--project", "/nonexistent/path/project.a3p",
        "--run-selector", "scene.eatmeFirstLessonStep",
        "--evidence-dir", tempFolder.getRoot().toString(),
        "--json"
    }, out, err);
    assertEquals(2, status);
  }
}
