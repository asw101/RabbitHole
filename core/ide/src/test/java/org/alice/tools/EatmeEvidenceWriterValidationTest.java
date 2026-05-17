package org.alice.tools;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for {@link EatmeEvidenceWriter#validateDesktopRunExecutionGapReport} —
 * validation logic for execution gap report evidence.
 */
public class EatmeEvidenceWriterValidationTest {

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  private static List<String> validArtifactList() {
    return new ArrayList<>(EatmeEvidenceWriter.REQUIRED_EXECUTION_GAP_EVIDENCE_ARTIFACTS);
  }

  private static List<String> validDoesNotClaim() {
    return Arrays.asList(
        "full world execution",
        "visible rendering correctness",
        "grading",
        "Save completion",
        "full UI automation");
  }

  // ---- validateDesktopRunExecutionGapReport: happy path ----

  @Test
  public void validate_happyPath_noException() {
    EatmeEvidenceWriter.validateDesktopRunExecutionGapReport(
        validArtifactList(),
        "Missing deterministic proof",
        validDoesNotClaim());
  }

  // ---- null/empty evidence artifacts ----

  @Test(expected = IllegalArgumentException.class)
  public void validate_nullArtifacts_throws() {
    EatmeEvidenceWriter.validateDesktopRunExecutionGapReport(
        null, "reason", validDoesNotClaim());
  }

  @Test(expected = IllegalArgumentException.class)
  public void validate_emptyArtifacts_throws() {
    EatmeEvidenceWriter.validateDesktopRunExecutionGapReport(
        Collections.emptyList(), "reason", validDoesNotClaim());
  }

  // ---- missing required artifact ----

  @Test(expected = IllegalArgumentException.class)
  public void validate_missingRequiredArtifact_throws() {
    List<String> artifacts = validArtifactList();
    artifacts.remove(0); // remove first required artifact
    EatmeEvidenceWriter.validateDesktopRunExecutionGapReport(
        artifacts, "reason", validDoesNotClaim());
  }

  // ---- null/empty blocker reason ----

  @Test(expected = IllegalArgumentException.class)
  public void validate_nullBlockerReason_throws() {
    EatmeEvidenceWriter.validateDesktopRunExecutionGapReport(
        validArtifactList(), null, validDoesNotClaim());
  }

  @Test(expected = IllegalArgumentException.class)
  public void validate_emptyBlockerReason_throws() {
    EatmeEvidenceWriter.validateDesktopRunExecutionGapReport(
        validArtifactList(), "", validDoesNotClaim());
  }

  @Test(expected = IllegalArgumentException.class)
  public void validate_blankBlockerReason_throws() {
    EatmeEvidenceWriter.validateDesktopRunExecutionGapReport(
        validArtifactList(), "   ", validDoesNotClaim());
  }

  // ---- null doesNotClaim ----

  @Test(expected = IllegalArgumentException.class)
  public void validate_nullDoesNotClaim_throws() {
    EatmeEvidenceWriter.validateDesktopRunExecutionGapReport(
        validArtifactList(), "reason", null);
  }

  // ---- missing doesNotClaim category ----

  @Test(expected = IllegalArgumentException.class)
  public void validate_missingDoesNotClaimCategory_throws() {
    List<String> dnc = new ArrayList<>(validDoesNotClaim());
    dnc.remove(0);
    EatmeEvidenceWriter.validateDesktopRunExecutionGapReport(
        validArtifactList(), "reason", dnc);
  }

  // ---- extra VM-listener artifact ----

  @Test(expected = IllegalArgumentException.class)
  public void validate_vmListenerArtifact_throws() {
    List<String> artifacts = validArtifactList();
    artifacts.add(EatmeDesktopRunExecutionEvidence.DESKTOP_RUN_EXECUTION_ARTIFACT);
    EatmeEvidenceWriter.validateDesktopRunExecutionGapReport(
        artifacts, "reason", validDoesNotClaim());
  }

  // ---- writeDesktopRunExecution ----

  @Test
  public void writeDesktopRunExecution_createsArtifacts() throws IOException {
    Path evidenceDir = tempFolder.getRoot().toPath();
    List<String> events = Arrays.asList("executing:ExpressionStatement", "executed:ExpressionStatement");
    Path artifact = EatmeEvidenceWriter.writeDesktopRunExecution(
        evidenceDir, "MyProgram", true, true, 5, 5, "executed:ExpressionStatement", events);
    assertTrue(Files.exists(artifact));
    String content = Files.readString(artifact, StandardCharsets.UTF_8);
    assertTrue(content.contains("statement_execution_observed"));
    assertTrue(content.contains("MyProgram"));
    assertTrue(content.contains("\"executing_statement_count\": 5"));
  }

  @Test
  public void writeDesktopRunExecution_preparingStatus() throws IOException {
    Path evidenceDir = tempFolder.getRoot().toPath();
    Path artifact = EatmeEvidenceWriter.writeDesktopRunExecution(
        evidenceDir, "MyProgram", false, false, 0, 0, "none", Collections.emptyList());
    String content = Files.readString(artifact, StandardCharsets.UTF_8);
    assertTrue(content.contains("\"status\": \"preparing\""));
  }

  @Test
  public void writeDesktopRunExecution_createsRuntimeLog() throws IOException {
    Path evidenceDir = tempFolder.getRoot().toPath();
    EatmeEvidenceWriter.writeDesktopRunExecution(
        evidenceDir, "Prog", true, true, 1, 1, "event", List.of("e1"));
    Path logPath = evidenceDir.resolve("desktop-run-runtime.log");
    assertTrue(Files.exists(logPath));
    String logContent = Files.readString(logPath, StandardCharsets.UTF_8);
    assertTrue(logContent.contains("program_type=Prog"));
    assertTrue(logContent.contains("e1"));
  }

  // ---- writeFirstLessonNextActionContract ----

  @Test
  public void writeFirstLessonNextActionContract_createsFile() throws IOException {
    Path artifact = tempFolder.getRoot().toPath().resolve("test-next-action.json");
    EatmeEvidenceWriter.writeFirstLessonNextActionContract(artifact);
    assertTrue(Files.exists(artifact));
    String content = Files.readString(artifact, StandardCharsets.UTF_8);
    assertTrue(content.contains("\"status\": \"blocked\""));
    assertTrue(content.contains("desktop-first-lesson-next-action"));
  }

  // ---- writeSaveMenuActionTargetNoGo ----

  @Test
  public void writeSaveMenuActionTargetNoGo_createsFile() throws IOException {
    Path artifact = tempFolder.getRoot().toPath().resolve("test-save-menu.json");
    EatmeEvidenceWriter.writeSaveMenuActionTargetNoGo(artifact);
    assertTrue(Files.exists(artifact));
    String content = Files.readString(artifact, StandardCharsets.UTF_8);
    assertTrue(content.contains("\"status\": \"blocked\""));
    assertTrue(content.contains("save-menu-action-target"));
  }

  // ---- constants ----

  @Test
  public void requiredArtifacts_count() {
    assertEquals(6, EatmeEvidenceWriter.REQUIRED_EXECUTION_GAP_EVIDENCE_ARTIFACTS.size());
  }

  @Test
  public void requiredArtifacts_containsRenderAffordance() {
    assertTrue(EatmeEvidenceWriter.REQUIRED_EXECUTION_GAP_EVIDENCE_ARTIFACTS
        .contains(EatmeDesktopRunExecutionEvidence.DESKTOP_RUN_RENDER_AFFORDANCE_ARTIFACT));
  }

  @Test
  public void requiredArtifacts_containsPixelBoundary() {
    assertTrue(EatmeEvidenceWriter.REQUIRED_EXECUTION_GAP_EVIDENCE_ARTIFACTS
        .contains(EatmeDesktopRunExecutionEvidence.DESKTOP_RUN_PIXEL_BOUNDARY_ARTIFACT));
  }

  @Test
  public void requiredArtifacts_containsStatusSummary() {
    assertTrue(EatmeEvidenceWriter.REQUIRED_EXECUTION_GAP_EVIDENCE_ARTIFACTS
        .contains(EatmeDesktopRunExecutionEvidence.DESKTOP_RUN_STATUS_SUMMARY_ARTIFACT));
  }
}
