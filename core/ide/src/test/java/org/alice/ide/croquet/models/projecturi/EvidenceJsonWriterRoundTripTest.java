package org.alice.ide.croquet.models.projecturi;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Round-trip and structural tests for EvidenceJsonWriter.
 *
 * <p>Covers {@code escapeJson} round-trips with embedded special characters,
 * {@code saveProofJson} structure, {@code resultClaimOrSummaryJson} edge cases,
 * {@code nonEmptyProjectFileWrite} variants, and
 * {@code saveActionDoesNotClaimJson}/{@code saveActionRequiresNextEvidenceJson}
 * internal helpers verified through the public saveActionInvocationProofJson output.
 *
 * <p>No JSON parsing library is available — all validation is via string assertions.
 */
public class EvidenceJsonWriterRoundTripTest {

  @Rule
  public final TemporaryFolder tmp = new TemporaryFolder();

  // ── escapeJson round-trip: escaped string embedded in JSON stays valid ──

  @Test
  public void escapeJsonRoundTripBackslashPath() {
    String raw = "C:\\Users\\alice\\Documents\\classroom.a3p";
    String escaped = EvidenceJsonWriter.escapeJson(raw);
    String json = "{\"path\": \"" + escaped + "\"}";
    assertTrue(json, json.contains("C:\\\\Users\\\\alice\\\\Documents\\\\classroom.a3p"));
    assertBalancedBraces(json);
    assertNoUnescapedQuotesInValue(escaped);
  }

  @Test
  public void escapeJsonRoundTripNewlineInDescription() {
    String raw = "line one\nline two\nline three";
    String escaped = EvidenceJsonWriter.escapeJson(raw);
    String json = "{\"description\": \"" + escaped + "\"}";
    assertFalse("Raw newline should not appear", json.contains("\n\""));
    assertTrue(json, json.contains("\\n"));
    assertBalancedBraces(json);
  }

  @Test
  public void escapeJsonRoundTripTabCharacter() {
    String raw = "field1\tfield2";
    String escaped = EvidenceJsonWriter.escapeJson(raw);
    assertEquals("field1\\tfield2", escaped);
    String json = "{\"data\": \"" + escaped + "\"}";
    assertBalancedBraces(json);
  }

  @Test
  public void escapeJsonRoundTripQuoteInPath() {
    String raw = "/home/alice/\"my project\"/file.a3p";
    String escaped = EvidenceJsonWriter.escapeJson(raw);
    assertTrue(escaped, escaped.contains("\\\"my project\\\""));
    String json = "{\"path\": \"" + escaped + "\"}";
    assertBalancedBraces(json);
  }

  @Test
  public void escapeJsonRoundTripMixedSpecialCharacters() {
    String raw = "back\\slash\nnew\"quote\ttab\b\f";
    String escaped = EvidenceJsonWriter.escapeJson(raw);
    String json = "{\"v\": \"" + escaped + "\"}";
    assertNoUnescapedQuotesInValue(escaped);
    assertBalancedBraces(json);
    assertTrue(escaped, escaped.contains("\\\\"));
    assertTrue(escaped, escaped.contains("\\n"));
    assertTrue(escaped, escaped.contains("\\\""));
    assertTrue(escaped, escaped.contains("\\t"));
    assertTrue(escaped, escaped.contains("\\b"));
    assertTrue(escaped, escaped.contains("\\f"));
  }

  @Test
  public void escapeJsonRoundTripAllLowControlChars() {
    for (int i = 0; i < 0x20; i++) {
      String raw = "a" + (char) i + "b";
      String escaped = EvidenceJsonWriter.escapeJson(raw);
      assertFalse("Control char U+" + Integer.toHexString(i) + " must be escaped",
          escaped.contains(String.valueOf((char) i)));
    }
  }

  @Test
  public void escapeJsonRoundTripEmptyInputProducesEmptyOutput() {
    assertEquals("", EvidenceJsonWriter.escapeJson(""));
  }

  @Test
  public void escapeJsonRoundTripUnicodePassesThrough() {
    String raw = "日本語テスト — emoji: 🎭";
    String escaped = EvidenceJsonWriter.escapeJson(raw);
    assertEquals(raw, escaped);
  }

  @Test
  public void escapeJsonRoundTripConsecutiveBackslashes() {
    String raw = "\\\\\\";
    String escaped = EvidenceJsonWriter.escapeJson(raw);
    assertEquals("\\\\\\\\\\\\", escaped);
  }

  @Test
  public void escapeJsonRoundTripLongStringPerformance() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 1000; i++) {
      sb.append("segment_").append(i).append("_");
    }
    String raw = sb.toString();
    String escaped = EvidenceJsonWriter.escapeJson(raw);
    assertEquals("Plain text should pass through unchanged", raw, escaped);
  }

  // ── stringJson round-trip ───────────────────────────────────────────

  @Test
  public void stringJsonRoundTripSpecialChars() {
    String value = "path\\with\"quotes\nand\tnewlines";
    String json = EvidenceJsonWriter.stringJson(value);
    assertTrue(json, json.startsWith("\""));
    assertTrue(json, json.endsWith("\""));
    assertNoUnescapedQuotesInValue(json.substring(1, json.length() - 1));
  }

  @Test
  public void stringJsonNullProducesLiteralNull() {
    assertEquals("null", EvidenceJsonWriter.stringJson(null));
  }

  @Test
  public void stringJsonEmptyStringProducesEmptyQuoted() {
    assertEquals("\"\"", EvidenceJsonWriter.stringJson(""));
  }

  // ── saveProofJson structure ─────────────────────────────────────────

  @Test
  public void saveProofJsonContainsSchemaVersion() throws IOException {
    String json = buildDefaultSaveProofJson();
    assertTrue(json, json.contains("\"schemaVersion\": \"" +
        SaveOperationCompletionEvidence.SAVE_PROOF_SCHEMA_VERSION + "\""));
  }

  @Test
  public void saveProofJsonContainsScenarioAndWorkflow() throws IOException {
    String json = buildDefaultSaveProofJson();
    assertTrue(json, json.contains("\"scenario\": \"" +
        SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO + "\""));
    assertTrue(json, json.contains("\"workflow\": \"" +
        SaveOperationCompletionEvidence.SAVE_PROOF_WORKFLOW + "\""));
  }

  @Test
  public void saveProofJsonContainsRunId() throws IOException {
    String json = buildDefaultSaveProofJson();
    assertTrue(json, json.contains("\"runId\": \""));
  }

  @Test
  public void saveProofJsonContainsGeneratedAtUtc() throws IOException {
    String json = buildDefaultSaveProofJson();
    assertTrue(json, json.contains("\"generatedAtUtc\": \""));
  }

  @Test
  public void saveProofJsonContainsMenuSection() throws IOException {
    String json = buildDefaultSaveProofJson();
    assertTrue(json, json.contains("\"menu\": {"));
    assertTrue(json, json.contains("\"fileMenuOpened\":"));
    assertTrue(json, json.contains("\"saveMenuItemInvoked\":"));
    assertTrue(json, json.contains("\"saveActionIdentityMatched\":"));
  }

  @Test
  public void saveProofJsonContainsDialogSection() throws IOException {
    String json = buildDefaultSaveProofJson();
    assertTrue(json, json.contains("\"dialog\": {"));
    assertTrue(json, json.contains("\"saveDialogObserved\":"));
    assertTrue(json, json.contains("\"dialogType\": \"Swing JFileChooser\""));
    assertTrue(json, json.contains("\"dialogShowing\":"));
  }

  @Test
  public void saveProofJsonContainsControlSection() throws IOException {
    String json = buildDefaultSaveProofJson();
    assertTrue(json, json.contains("\"control\": {"));
    assertTrue(json, json.contains("\"selectedPathSet\":"));
    assertTrue(json, json.contains("\"approvedSelection\":"));
  }

  @Test
  public void saveProofJsonContainsWriteSection() throws IOException {
    String json = buildDefaultSaveProofJson();
    assertTrue(json, json.contains("\"write\": {"));
    assertTrue(json, json.contains("\"fileWritten\":"));
    assertTrue(json, json.contains("\"fileNonempty\":"));
  }

  @Test
  public void saveProofJsonContainsReadbackSection() throws IOException {
    String json = buildDefaultSaveProofJson();
    assertTrue(json, json.contains("\"readback\": {"));
    assertTrue(json, json.contains("\"projectReadable\":"));
    assertTrue(json, json.contains("\"markerPresent\":"));
    assertTrue(json, json.contains("\"marker\": \"" +
        SaveOperationCompletionEvidence.SAVE_PROOF_MARKER + "\""));
  }

  @Test
  public void saveProofJsonContainsBaselinePreserved() throws IOException {
    String json = buildDefaultSaveProofJson();
    assertTrue(json, json.contains("\"baselinePreserved\": ["));
  }

  @Test
  public void saveProofJsonContainsRequiresNextEvidence() throws IOException {
    String json = buildDefaultSaveProofJson();
    assertTrue(json, json.contains("\"requiresNextEvidence\": ["));
  }

  @Test
  public void saveProofJsonContainsDoesNotClaim() throws IOException {
    String json = buildDefaultSaveProofJson();
    assertTrue(json, json.contains("\"doesNotClaim\": ["));
  }

  @Test
  public void saveProofJsonBlockedHasBlockerSection() throws IOException {
    String json = buildDefaultSaveProofJson();
    assertTrue(json, json.contains("\"status\": \"blocked\""));
    assertTrue(json, json.contains("\"blocker\": {"));
    assertTrue(json, json.contains("\"kind\":"));
    assertTrue(json, json.contains("\"observed\":"));
    assertTrue(json, json.contains("\"required\":"));
  }

  @Test
  public void saveProofJsonIsWellFormedBraces() throws IOException {
    String json = buildDefaultSaveProofJson();
    assertBalancedBraces(json);
    assertTrue(json, json.trim().startsWith("{"));
    assertTrue(json, json.trim().endsWith("}"));
  }

  @Test
  public void saveProofJsonWriteAndReadBackPreservesContent() throws IOException {
    String json = buildDefaultSaveProofJson();
    Path file = tmp.newFile("save-proof-roundtrip.json").toPath();
    Files.writeString(file, json, StandardCharsets.UTF_8);
    String readBack = Files.readString(file, StandardCharsets.UTF_8);
    assertEquals(json, readBack);
  }

  // ── saveProofJson proven vs blocked ────────────────────────────────

  @Test
  public void saveProofJsonProvenHasNullBlocker() throws IOException {
    File proofRoot = tmp.newFolder("proven-proof-root");
    File target = new File(proofRoot, "test.a3p");
    Files.writeString(target.toPath(), "alice project content");

    EvidenceJsonWriter.SaveProofSnapshot snap = new EvidenceJsonWriter.SaveProofSnapshot(
        true, true, true, true, true, false, true, true,
        true, "javax.swing.JFileChooser",
        target.getCanonicalPath(),
        5, true, true,
        null, null, null,
        target.getCanonicalPath(),
        target.toPath(), target.getName(),
        proofRoot.toPath().toRealPath(),
        SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO,
        "test-run-id-123");

    String json = EvidenceJsonWriter.saveProofJson(snap);
    assertTrue(json, json.contains("\"status\": \"proven\""));
    assertTrue(json, json.contains("\"blocker\": null"));
    assertTrue(json, json.contains("\"claim\":"));
  }

  @Test
  public void saveProofJsonBlockedInfersFileMenuNotShowingBlocker() throws IOException {
    File proofRoot = tmp.newFolder("blocker-file-menu");
    File target = new File(proofRoot, "test.a3p");

    EvidenceJsonWriter.SaveProofSnapshot snap = new EvidenceJsonWriter.SaveProofSnapshot(
        false, false, false, false, false, false, false, false,
        false, null, null,
        0, false, false,
        null, null, null,
        target.getAbsolutePath(),
        target.toPath(), target.getName(),
        proofRoot.toPath().toRealPath(),
        SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO,
        "test-run-id-123");

    String json = EvidenceJsonWriter.saveProofJson(snap);
    assertTrue(json, json.contains("\"status\": \"blocked\""));
    assertTrue(json, json.contains("\"kind\": \"file_menu_not_showing\""));
  }

  // ── resultClaimOrSummaryJson edge cases ───────────────────────────

  @Test
  public void resultClaimOrSummaryJsonClaimWithNullExtension() {
    String json = EvidenceJsonWriter.resultClaimOrSummaryJson(true, "finished", null);
    assertTrue(json, json.contains("\"claim\":"));
    assertTrue(json, json.contains("a non-empty project file write"));
    assertFalse(json, json.contains(".null"));
  }

  @Test
  public void resultClaimOrSummaryJsonSummaryWithIncompleteStatus() {
    String json = EvidenceJsonWriter.resultClaimOrSummaryJson(false, "incomplete", "a3p");
    assertTrue(json, json.contains("\"reporting_summary\":"));
    assertTrue(json, json.contains("incomplete"));
    assertTrue(json, json.contains("a non-empty .a3p project file write"));
  }

  @Test
  public void resultClaimOrSummaryJsonClaimIncludesExtensionDot() {
    String json = EvidenceJsonWriter.resultClaimOrSummaryJson(true, "finished", "a3p");
    assertTrue(json, json.contains(".a3p"));
  }

  @Test
  public void resultClaimOrSummaryJsonSummaryWithBlankExtension() {
    String json = EvidenceJsonWriter.resultClaimOrSummaryJson(false, "canceled", "");
    assertTrue(json, json.contains("\"reporting_summary\":"));
    assertTrue(json, json.contains("a non-empty project file write"));
  }

  // ── nonEmptyProjectFileWrite edge cases ───────────────────────────

  @Test
  public void nonEmptyProjectFileWriteWhitespaceExtension() {
    assertEquals("a non-empty project file write",
        EvidenceJsonWriter.nonEmptyProjectFileWrite("   "));
  }

  @Test
  public void nonEmptyProjectFileWriteTabExtension() {
    assertEquals("a non-empty project file write",
        EvidenceJsonWriter.nonEmptyProjectFileWrite("\t"));
  }

  @Test
  public void nonEmptyProjectFileWriteExoticExtension() {
    assertEquals("a non-empty .lgp project file write",
        EvidenceJsonWriter.nonEmptyProjectFileWrite("lgp"));
  }

  // ── resultJson round-trip through filesystem ──────────────────────

  @Test
  public void resultJsonFinishedWriteAndReadBack() throws IOException {
    File savedFile = tmp.newFile("classroom.a3p");
    Files.writeString(savedFile.toPath(), "alice project content");
    SaveOperationFlow.Result result =
        new SaveOperationFlow.Result(true, false, 1, 1, savedFile);
    EvidenceFileOperations.RegularFileState fileState =
        EvidenceFileOperations.regularFileState(savedFile.toPath());

    String json = EvidenceJsonWriter.resultJson("org.alice.test.Op", "a3p", result, fileState);

    Path artifact = tmp.newFile("result-roundtrip.json").toPath();
    Files.writeString(artifact, json, StandardCharsets.UTF_8);
    String readBack = Files.readString(artifact, StandardCharsets.UTF_8);
    assertEquals(json, readBack);
    assertBalancedBraces(readBack);
    assertTrue(readBack, readBack.contains("\"wroteFile\": true"));
  }

  @Test
  public void resultJsonCanceledWriteAndReadBack() throws IOException {
    SaveOperationFlow.Result result =
        new SaveOperationFlow.Result(false, true, 2, 0, null);
    EvidenceFileOperations.RegularFileState missing =
        EvidenceFileOperations.RegularFileState.MISSING;

    String json = EvidenceJsonWriter.resultJson("org.alice.test.Op", "a3p", result, missing);

    Path artifact = tmp.newFile("canceled-roundtrip.json").toPath();
    Files.writeString(artifact, json, StandardCharsets.UTF_8);
    String readBack = Files.readString(artifact, StandardCharsets.UTF_8);
    assertEquals(json, readBack);
    assertTrue(readBack, readBack.contains("\"status\": \"canceled\""));
    assertTrue(readBack, readBack.contains("\"wroteFile\": false"));
    assertTrue(readBack, readBack.contains("\"saved_file\": null"));
    assertTrue(readBack, readBack.contains("\"prompt_count\": 2"));
  }

  // ── dialogControlTargetJson round-trip ─────────────────────────────

  @Test
  public void dialogControlTargetJsonWriteAndReadBack() throws IOException {
    SaveOperationFlow.Result result =
        new SaveOperationFlow.Result(true, false, 1, 1, null);
    String json = EvidenceJsonWriter.dialogControlTargetJson("org.alice.test.Op", "a3p", result);

    Path artifact = tmp.newFile("dialog-control-roundtrip.json").toPath();
    Files.writeString(artifact, json, StandardCharsets.UTF_8);
    String readBack = Files.readString(artifact, StandardCharsets.UTF_8);
    assertEquals(json, readBack);
    assertBalancedBraces(readBack);
  }

  @Test
  public void dialogControlTargetJsonContainsDoesNotClaim() {
    SaveOperationFlow.Result result =
        new SaveOperationFlow.Result(true, false, 1, 1, null);
    String json = EvidenceJsonWriter.dialogControlTargetJson("org.alice.test.Op", "a3p", result);
    assertTrue(json, json.contains("\"doesNotClaim\": ["));
    assertTrue(json, json.contains("\"desktop Save menu item was clicked\""));
    assertTrue(json, json.contains("\"desktop Save dialog control\""));
  }

  @Test
  public void dialogControlTargetJsonContainsMissingEvidence() {
    SaveOperationFlow.Result result =
        new SaveOperationFlow.Result(true, false, 1, 1, null);
    String json = EvidenceJsonWriter.dialogControlTargetJson("org.alice.test.Op", "a3p", result);
    assertTrue(json, json.contains("\"missing_evidence\": ["));
    assertTrue(json, json.contains("\"desktop Save dialog discovery\""));
    assertTrue(json, json.contains("\"selected Save path supplied by UI automation\""));
  }

  @Test
  public void dialogControlTargetJsonContainsRequiresNextEvidence() {
    SaveOperationFlow.Result result =
        new SaveOperationFlow.Result(true, false, 1, 1, null);
    String json = EvidenceJsonWriter.dialogControlTargetJson("org.alice.test.Op", "a3p", result);
    assertTrue(json, json.contains("\"requiresNextEvidence\": ["));
    assertTrue(json, json.contains("desktop Save dialog owner/component artifact"));
  }

  // ── saveActionInvocationProofJson comprehensive ────────────────────

  @Test
  public void saveActionInvocationProofJsonMenuItemDispatch() {
    SaveOperationCompletionEvidence.InvocationTrigger menuTrigger = createTrigger(
        "org.lgna.croquet.triggers.ActionEventTrigger",
        "org.lgna.croquet.views.MenuItem",
        "javax.swing.JMenuItem");
    String json = EvidenceJsonWriter.saveActionInvocationProofJson(
        "org.alice.test.Op", "a3p",
        true, true, menuTrigger);
    assertTrue(json, json.contains("\"status\": \"menu_item_dispatched\""));
    assertTrue(json, json.contains("\"reason\": \"save_menu_item_dispatched\""));
    assertTrue(json, json.contains("\"menu_item_dispatch\": true"));
    // When menu item dispatched, "desktop Save menu item was clicked" is not in doesNotClaim
    assertFalse(json, json.contains("\"desktop Save menu item was clicked\""));
  }

  @Test
  public void saveActionInvocationProofJsonActionInvokedContainsMenuItem() {
    String json = EvidenceJsonWriter.saveActionInvocationProofJson(
        "org.alice.test.Op", "a3p",
        true, true,
        SaveOperationCompletionEvidence.InvocationTrigger.none());
    assertTrue(json, json.contains("\"status\": \"action_invoked\""));
    // When not menu dispatch, doesNotClaim includes menu item
    assertTrue(json, json.contains("\"desktop Save menu item was clicked\""));
  }

  @Test
  public void saveActionInvocationProofJsonWriteAndReadBack() throws IOException {
    String json = EvidenceJsonWriter.saveActionInvocationProofJson(
        "org.alice.test.Op", "a3p",
        true, true,
        SaveOperationCompletionEvidence.InvocationTrigger.none());

    Path artifact = tmp.newFile("action-proof-roundtrip.json").toPath();
    Files.writeString(artifact, json, StandardCharsets.UTF_8);
    String readBack = Files.readString(artifact, StandardCharsets.UTF_8);
    assertEquals(json, readBack);
    assertBalancedBraces(readBack);
  }

  @Test
  public void saveActionInvocationProofJsonMissingDocumentFrameRequiresProjectDocumentFrame() {
    String json = EvidenceJsonWriter.saveActionInvocationProofJson(
        "org.alice.test.Op", "a3p",
        true, false,
        SaveOperationCompletionEvidence.InvocationTrigger.none());
    assertTrue(json, json.contains("\"reason\": \"missing_project_document_frame\""));
    assertTrue(json, json.contains("invoke SaveProjectOperation from an initialized Alice desktop with a ProjectDocumentFrame"));
  }

  @Test
  public void saveActionInvocationProofJsonMissingStageIdeRequiresActiveInstance() {
    String json = EvidenceJsonWriter.saveActionInvocationProofJson(
        "org.alice.test.Op", "a3p",
        false, false,
        SaveOperationCompletionEvidence.InvocationTrigger.none());
    assertTrue(json, json.contains("\"reason\": \"missing_active_stage_ide\""));
    assertTrue(json, json.contains("invoke SaveProjectOperation from a running Alice desktop with StageIDE.getActiveInstance() resolved"));
  }

  // ── saveActionInvocationReason edge cases ─────────────────────────

  @Test
  public void saveActionInvocationReasonMenuItemDispatch() {
    SaveOperationCompletionEvidence.InvocationTrigger menuTrigger = createTrigger(
        "org.lgna.croquet.triggers.ActionEventTrigger",
        "org.lgna.croquet.views.MenuItem",
        "javax.swing.JMenuItem");
    assertEquals("save_menu_item_dispatched",
        EvidenceJsonWriter.saveActionInvocationReason(true, true, menuTrigger));
  }

  @Test
  public void saveActionInvocationReasonMissingStageIdeEvenWithMenuTrigger() {
    SaveOperationCompletionEvidence.InvocationTrigger menuTrigger = createTrigger(
        "org.lgna.croquet.triggers.ActionEventTrigger",
        "org.lgna.croquet.views.MenuItem",
        "javax.swing.JMenuItem");
    assertEquals("missing_active_stage_ide",
        EvidenceJsonWriter.saveActionInvocationReason(false, true, menuTrigger));
  }

  // ── operationSimpleName edge cases ────────────────────────────────

  @Test
  public void operationSimpleNameWithTrailingDot() {
    assertEquals("", EvidenceJsonWriter.operationSimpleName("org.alice."));
  }

  @Test
  public void operationSimpleNameWithMultipleDots() {
    assertEquals("Op", EvidenceJsonWriter.operationSimpleName("a.b.c.d.Op"));
  }

  @Test
  public void operationSimpleNameEmptyString() {
    assertEquals("", EvidenceJsonWriter.operationSimpleName(""));
  }

  // ── className edge cases ──────────────────────────────────────────

  @Test
  public void classNameReturnsIntegerClass() {
    assertEquals("java.lang.Integer", EvidenceJsonWriter.className(42));
  }

  @Test
  public void classNameReturnsBooleanClass() {
    assertEquals("java.lang.Boolean", EvidenceJsonWriter.className(true));
  }

  // ── Helpers ───────────────────────────────────────────────────────

  private String buildDefaultSaveProofJson() throws IOException {
    File proofRoot = tmp.newFolder("proof-root");
    File target = new File(proofRoot, "test.a3p");

    EvidenceJsonWriter.SaveProofSnapshot snap = new EvidenceJsonWriter.SaveProofSnapshot(
        false, false, false, false, false, false, false, false,
        false, null, null,
        0, false, false,
        null, null, null,
        target.getAbsolutePath(),
        target.toPath(), target.getName(),
        proofRoot.toPath().toRealPath(),
        SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO,
        "test-run-id-default");

    return EvidenceJsonWriter.saveProofJson(snap);
  }

  private static void assertBalancedBraces(String json) {
    int braces = 0;
    int brackets = 0;
    boolean inString = false;
    boolean escape = false;
    for (int i = 0; i < json.length(); i++) {
      char ch = json.charAt(i);
      if (escape) {
        escape = false;
        continue;
      }
      if (ch == '\\' && inString) {
        escape = true;
        continue;
      }
      if (ch == '"') {
        inString = !inString;
        continue;
      }
      if (!inString) {
        if (ch == '{') braces++;
        else if (ch == '}') braces--;
        else if (ch == '[') brackets++;
        else if (ch == ']') brackets--;
      }
    }
    assertEquals("Unbalanced braces in JSON", 0, braces);
    assertEquals("Unbalanced brackets in JSON", 0, brackets);
  }

  private static void assertNoUnescapedQuotesInValue(String escapedValue) {
    for (int i = 0; i < escapedValue.length(); i++) {
      if (escapedValue.charAt(i) == '"') {
        assertTrue("Unescaped quote at index " + i,
            i > 0 && escapedValue.charAt(i - 1) == '\\');
      }
    }
  }

  private static SaveOperationCompletionEvidence.InvocationTrigger createTrigger(
      String triggerClass, String viewControllerClass, String awtSourceClass) {
    try {
      Constructor<SaveOperationCompletionEvidence.InvocationTrigger> ctor =
          SaveOperationCompletionEvidence.InvocationTrigger.class.getDeclaredConstructor(
              String.class, String.class, String.class);
      ctor.setAccessible(true);
      return ctor.newInstance(triggerClass, viewControllerClass, awtSourceClass);
    } catch (Exception e) {
      throw new RuntimeException("Failed to create InvocationTrigger via reflection", e);
    }
  }
}
