package org.alice.tools;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for {@link EatmeEvidenceWriter} — atomic I/O, JSON formatting,
 * and artifact validation utilities.
 */
public class EatmeEvidenceWriterContractTest {

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  // ---- writeStringAtomically ----

  @Test
  public void writeStringAtomically_createsFile() throws IOException {
    Path target = tempFolder.getRoot().toPath().resolve("test.json");
    EatmeEvidenceWriter.writeStringAtomically(target, "{\"hello\": \"world\"}");
    assertTrue(Files.exists(target));
    assertEquals("{\"hello\": \"world\"}", Files.readString(target, StandardCharsets.UTF_8));
  }

  @Test
  public void writeStringAtomically_overwritesExisting() throws IOException {
    Path target = tempFolder.getRoot().toPath().resolve("test.json");
    Files.writeString(target, "old content", StandardCharsets.UTF_8);
    EatmeEvidenceWriter.writeStringAtomically(target, "new content");
    assertEquals("new content", Files.readString(target, StandardCharsets.UTF_8));
  }

  @Test
  public void writeStringAtomically_tempFileCleanedUp() throws IOException {
    Path target = tempFolder.getRoot().toPath().resolve("test.json");
    EatmeEvidenceWriter.writeStringAtomically(target, "content");
    Path tmp = target.resolveSibling("test.json.tmp");
    assertFalse("Temp file should be cleaned up", Files.exists(tmp));
  }

  // ---- requireNonEmptyArtifact ----

  @Test
  public void requireNonEmptyArtifact_existsAndNonEmpty_passes() throws IOException {
    Path file = tempFolder.newFile("artifact.json").toPath();
    Files.writeString(file, "data", StandardCharsets.UTF_8);
    EatmeEvidenceWriter.requireNonEmptyArtifact(file, "test artifact");
    // no exception = pass
  }

  @Test(expected = IOException.class)
  public void requireNonEmptyArtifact_emptyFile_throws() throws IOException {
    Path file = tempFolder.newFile("empty.json").toPath();
    // file exists but is empty
    EatmeEvidenceWriter.requireNonEmptyArtifact(file, "empty artifact");
  }

  @Test(expected = IOException.class)
  public void requireNonEmptyArtifact_missingFile_throws() throws IOException {
    Path file = tempFolder.getRoot().toPath().resolve("nonexistent.json");
    EatmeEvidenceWriter.requireNonEmptyArtifact(file, "missing artifact");
  }

  // ---- runtimeLog ----

  @Test
  public void runtimeLog_containsSchemaVersion() {
    String log = EatmeEvidenceWriter.runtimeLog("MyProgram", Collections.emptyList());
    assertTrue(log.contains("schema_version=eatme.alice-desktop-run-execution-log/v1"));
  }

  @Test
  public void runtimeLog_containsProgramType() {
    String log = EatmeEvidenceWriter.runtimeLog("MyProgram", Collections.emptyList());
    assertTrue(log.contains("program_type=MyProgram"));
  }

  @Test
  public void runtimeLog_includesEvents() {
    List<String> events = Arrays.asList("executing:ExpressionStatement", "executed:ExpressionStatement");
    String log = EatmeEvidenceWriter.runtimeLog("Prog", events);
    assertTrue(log.contains("executing:ExpressionStatement"));
    assertTrue(log.contains("executed:ExpressionStatement"));
  }

  @Test
  public void runtimeLog_containsRecordedAt() {
    String log = EatmeEvidenceWriter.runtimeLog("Prog", Collections.emptyList());
    assertTrue(log.contains("recorded_at="));
  }

  // ---- jsonArray ----

  @Test
  public void jsonArray_emptyList() {
    String json = EatmeEvidenceWriter.jsonArray(Collections.emptyList());
    assertEquals("[]", json);
  }

  @Test
  public void jsonArray_singleElement() {
    String json = EatmeEvidenceWriter.jsonArray(List.of("hello"));
    assertEquals("[\"hello\"]", json);
  }

  @Test
  public void jsonArray_multipleElements() {
    String json = EatmeEvidenceWriter.jsonArray(List.of("a", "b", "c"));
    assertEquals("[\"a\", \"b\", \"c\"]", json);
  }

  // ---- pixelObservationSummary / pixelObservationReportingNote ----
  // These require a PixelObservation instance; test with contract expectations

  // ---- REQUIRED_EXECUTION_GAP_EVIDENCE_ARTIFACTS ----

  @Test
  public void requiredArtifacts_isNonEmpty() {
    assertFalse(EatmeEvidenceWriter.REQUIRED_EXECUTION_GAP_EVIDENCE_ARTIFACTS.isEmpty());
  }

  @Test
  public void requiredArtifacts_containsExpectedCount() {
    assertEquals(6, EatmeEvidenceWriter.REQUIRED_EXECUTION_GAP_EVIDENCE_ARTIFACTS.size());
  }
}
