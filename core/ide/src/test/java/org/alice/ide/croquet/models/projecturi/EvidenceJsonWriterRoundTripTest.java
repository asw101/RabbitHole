package org.alice.ide.croquet.models.projecturi;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Round-trip and branch-coverage tests for {@link EvidenceJsonWriter}.
 *
 * <p>Covers saveProofJson structure, dialogControlTargetJson branches,
 * saveActionInvocationProofJson all four reason branches, escapeJson
 * round-trip embedding, resultClaimOrSummaryJson, and nonEmptyProjectFileWrite
 * edge cases not covered by {@link EvidenceJsonWriterTest}.
 *
 * <p>JUnit 4, headless, no JSON parsing library — string assertions only.
 */
public class EvidenceJsonWriterRoundTripTest {

  // ── escapeJson round-trip embedding ──────────────────────────────

  @Test
  public void escapeJsonRoundTrip_embeddedInQuotesProducesValidJsonString() {
    String input = "C:\\Users\\alice\\Desktop\\project \"final\".a3p";
    String escaped = EvidenceJsonWriter.escapeJson(input);
    String jsonString = "\"" + escaped + "\"";
    assertTrue("Opening quote", jsonString.startsWith("\""));
    assertTrue("Closing quote", jsonString.endsWith("\""));
    assertFalse("No unescaped backslash",
        escaped.matches(".*(?<!\\\\)\\\\(?![\\\\\"bfnrtu]).*"));
  }

  @Test
  public void escapeJsonRoundTrip_nullByteEscapedAsUnicode() {
    String input = "before\u0000after";
    String escaped = EvidenceJsonWriter.escapeJson(input);
    assertTrue("Null byte escaped", escaped.contains("\\u0000"));
    assertFalse("Raw null byte removed",
        escaped.contains("\u0000"));
  }

  @Test
  public void escapeJsonRoundTrip_allControlCharsUnder0x20() {
    for (char ch = 0; ch < 0x20; ch++) {
      String input = "x" + ch + "y";
      String escaped = EvidenceJsonWriter.escapeJson(input);
      assertFalse("Control char " + (int) ch + " must not appear raw",
          escaped.contains(String.valueOf(ch)));
    }
  }

  @Test
  public void escapeJsonRoundTrip_mixedSpecialChars() {
    String input = "tab\there\nnewline\\backslash\"quote";
    String escaped = EvidenceJsonWriter.escapeJson(input);
    assertTrue(escaped.contains("\\t"));
    assertTrue(escaped.contains("\\n"));
    assertTrue(escaped.contains("\\\\"));
    assertTrue(escaped.contains("\\\""));
  }

  @Test
  public void escapeJsonRoundTrip_unicodeBeyond0x20Unchanged() {
    String input = "日本語 café résumé";
    String escaped = EvidenceJsonWriter.escapeJson(input);
    assertEquals(input, escaped);
  }

  @Test
  public void escapeJsonRoundTrip_longStringWithLeadingPlainText() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 1000; i++) {
      sb.append('a');
    }
    sb.append('\n');
    String escaped = EvidenceJsonWriter.escapeJson(sb.toString());
    assertTrue("Escaping happened at position 1000",
        escaped.endsWith("\\n"));
    assertEquals(1000 + 2, escaped.length()); // 1000 'a' + "\\n"
  }

  @Test
  public void escapeJsonRoundTrip_emptyStringNoChange() {
    assertEquals("", EvidenceJsonWriter.escapeJson(""));
  }

  @Test
  public void escapeJson_backslashAtStartOfString() {
    assertEquals("\\\\start", EvidenceJsonWriter.escapeJson("\\start"));
  }

  @Test
  public void escapeJson_quoteAtEndOfString() {
    assertEquals("end\\\"", EvidenceJsonWriter.escapeJson("end\""));
  }

  @Test
  public void escapeJson_onlySpecialChars() {
    assertEquals("\\\"\\\\\\n\\r\\t",
        EvidenceJsonWriter.escapeJson("\"\\\n\r\t"));
  }

  // ── stringJson edge cases ────────────────────────────────────────

  @Test
  public void stringJson_emptyStringWrappedInQuotes() {
    assertEquals("\"\"", EvidenceJsonWriter.stringJson(""));
  }

  @Test
  public void stringJson_specialCharsEscapedInsideQuotes() {
    String result = EvidenceJsonWriter.stringJson("line1\nline2");
    assertEquals("\"line1\\nline2\"", result);
  }

  // ── saveProofJson structure ──────────────────────────────────────

  @Test
  public void saveProofJson_containsSchemaVersion() {
    String json = saveProofJsonForDefaults();
    assertTrue(json, json.contains(
        "\"schemaVersion\": \"" + SaveOperationCompletionEvidence.SAVE_PROOF_SCHEMA_VERSION + "\""));
  }

  @Test
  public void saveProofJson_containsScenario() {
    String json = saveProofJsonForDefaults();
    assertTrue(json, json.contains(
        "\"scenario\": \"" + SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO + "\""));
  }

  @Test
  public void saveProofJson_containsWorkflow() {
    String json = saveProofJsonForDefaults();
    assertTrue(json, json.contains(
        "\"workflow\": \"" + SaveOperationCompletionEvidence.SAVE_PROOF_WORKFLOW + "\""));
  }

  @Test
  public void saveProofJson_containsRunId() {
    String json = saveProofJsonForDefaults();
    assertTrue(json, json.contains("\"runId\": \""));
  }

  @Test
  public void saveProofJson_containsGeneratedAtUtc() {
    String json = saveProofJsonForDefaults();
    assertTrue(json, json.contains("\"generatedAtUtc\": \""));
  }

  @Test
  public void saveProofJson_blockedStatus_whenNotProven() {
    String json = saveProofJsonForDefaults();
    assertTrue(json, json.contains("\"status\": \"blocked\""));
    assertFalse(json, json.contains("\"status\": \"proven\""));
  }

  @Test
  public void saveProofJson_containsBlockerSection_whenBlocked() {
    String json = saveProofJsonForDefaults();
    assertTrue(json, json.contains("\"blocker\": {"));
    assertTrue(json, json.contains("\"kind\": \""));
  }

  @Test
  public void saveProofJson_containsMenuSection() {
    String json = saveProofJsonForDefaults();
    assertTrue(json, json.contains("\"menu\": {"));
    assertTrue(json, json.contains("\"fileMenuOpened\": false"));
    assertTrue(json, json.contains("\"saveMenuItemInvoked\": false"));
  }

  @Test
  public void saveProofJson_containsDialogSection() {
    String json = saveProofJsonForDefaults();
    assertTrue(json, json.contains("\"dialog\": {"));
    assertTrue(json, json.contains("\"saveDialogObserved\": false"));
    assertTrue(json, json.contains("\"dialogType\": \"Swing JFileChooser\""));
  }

  @Test
  public void saveProofJson_containsControlSection() {
    String json = saveProofJsonForDefaults();
    assertTrue(json, json.contains("\"control\": {"));
    assertTrue(json, json.contains("\"selectedPathSet\": false"));
    assertTrue(json, json.contains("\"approvedSelection\": false"));
  }

  @Test
  public void saveProofJson_containsWriteSection() {
    String json = saveProofJsonForDefaults();
    assertTrue(json, json.contains("\"write\": {"));
    assertTrue(json, json.contains("\"fileWritten\":"));
    assertTrue(json, json.contains("\"fileNonempty\":"));
  }

  @Test
  public void saveProofJson_containsReadbackSection() {
    String json = saveProofJsonForDefaults();
    assertTrue(json, json.contains("\"readback\": {"));
    assertTrue(json, json.contains("\"projectReadable\": false"));
    assertTrue(json, json.contains("\"markerPresent\": false"));
    assertTrue(json, json.contains(
        "\"marker\": \"" + SaveOperationCompletionEvidence.SAVE_PROOF_MARKER + "\""));
  }

  @Test
  public void saveProofJson_containsBaselinePreserved() {
    String json = saveProofJsonForDefaults();
    assertTrue(json, json.contains("\"baselinePreserved\": ["));
  }

  @Test
  public void saveProofJson_containsRequiresNextEvidence() {
    String json = saveProofJsonForDefaults();
    assertTrue(json, json.contains("\"requiresNextEvidence\": ["));
  }

  @Test
  public void saveProofJson_containsDoesNotClaim() {
    String json = saveProofJsonForDefaults();
    assertTrue(json, json.contains("\"doesNotClaim\": ["));
  }

  @Test
  public void saveProofJson_startsAndEndsWithBraces() {
    String json = saveProofJsonForDefaults();
    assertTrue(json, json.trim().startsWith("{"));
    assertTrue(json, json.trim().endsWith("}"));
  }

  @Test
  public void saveProofJson_noReportingSummary_whenBlocked() {
    String json = saveProofJsonForDefaults();
    assertTrue(json, json.contains("\"reportingSummary\":"));
    assertFalse(json, json.contains("\"claim\": \"AWT Robot opened File"));
  }

  @Test
  public void saveProofJson_provenStatus_whenAllFieldsTrue() throws Exception {
    Path testDir = newTestDir();
    Path targetFile = Files.writeString(testDir.resolve("proven.a3p"), "project content");
    EvidenceJsonWriter.SaveProofSnapshot snap = new EvidenceJsonWriter.SaveProofSnapshot(
        true,  // robotFileMenuOpened
        true,  // robotSaveItemClicked
        true,  // saveActionIdentityMatched
        true,  // chooserObserved
        true,  // approvedSelection
        false, // ambiguousChooserDiscovery
        true,  // selectedFileVerified
        true,  // targetInsideProofRoot
        true,  // dialogShowing
        "javax.swing.JFileChooser", // dialogClass
        targetFile.toRealPath().toString(), // normalizedSelectedFile
        5,     // pollCount
        true,  // projectReadable
        true,  // markerPresent
        null,  // blockerKind
        null,  // blockerObserved
        null,  // blockerRequired
        targetFile.toRealPath().toString(), // targetCanonicalPath
        targetFile.toRealPath(),            // targetPath
        "proven.a3p",                       // targetFileName
        testDir.toRealPath(),               // proofRoot
        SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO,
        "test-run-1234");
    String json = EvidenceJsonWriter.saveProofJson(snap);
    assertTrue(json, json.contains("\"status\": \"proven\""));
    assertTrue(json, json.contains("\"blocker\": null"));
    assertTrue(json, json.contains("\"claim\": \"AWT Robot opened File"));
  }

  @Test
  public void saveProofJson_explicitBlockerPreserved() throws Exception {
    Path testDir = newTestDir();
    Path targetFile = Files.writeString(testDir.resolve("blocked.a3p"), "content");
    EvidenceJsonWriter.SaveProofSnapshot snap = new EvidenceJsonWriter.SaveProofSnapshot(
        false, false, false, false, false, false, false, true, false,
        null, null, 0, false, false,
        "custom_blocker", "custom observed", "custom required",
        targetFile.toRealPath().toString(), targetFile.toRealPath(),
        "blocked.a3p", testDir.toRealPath(),
        SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO,
        "test-run-5678");
    String json = EvidenceJsonWriter.saveProofJson(snap);
    assertTrue(json, json.contains("\"kind\": \"custom_blocker\""));
    assertTrue(json, json.contains("\"observed\": \"custom observed\""));
    assertTrue(json, json.contains("\"required\": \"custom required\""));
  }

  @Test
  public void saveProofJson_inferredBlocker_fileMenuNotShowing() throws Exception {
    Path testDir = newTestDir();
    Path targetFile = Files.writeString(testDir.resolve("infer.a3p"), "content");
    EvidenceJsonWriter.SaveProofSnapshot snap = new EvidenceJsonWriter.SaveProofSnapshot(
        false, false, false, false, false, false, false, true, false,
        null, null, 0, false, false,
        null, null, null,
        targetFile.toRealPath().toString(), targetFile.toRealPath(),
        "infer.a3p", testDir.toRealPath(),
        SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO,
        "test-infer-1");
    String json = EvidenceJsonWriter.saveProofJson(snap);
    assertTrue(json, json.contains("\"kind\": \"file_menu_not_showing\""));
  }

  @Test
  public void saveProofJson_inferredBlocker_dialogNotObserved() throws Exception {
    Path testDir = newTestDir();
    Path targetFile = Files.writeString(testDir.resolve("infer-dialog.a3p"), "content");
    EvidenceJsonWriter.SaveProofSnapshot snap = new EvidenceJsonWriter.SaveProofSnapshot(
        true, true, true, false, false, false, false, true, false,
        null, null, 0, false, false,
        null, null, null,
        targetFile.toRealPath().toString(), targetFile.toRealPath(),
        "infer-dialog.a3p", testDir.toRealPath(),
        SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO,
        "test-infer-2");
    String json = EvidenceJsonWriter.saveProofJson(snap);
    assertTrue(json, json.contains("\"kind\": \"dialog_not_observed\""));
  }

  @Test
  public void saveProofJson_inferredBlocker_saveItemNotAttributed() throws Exception {
    Path testDir = newTestDir();
    Path targetFile = Files.writeString(testDir.resolve("attr.a3p"), "content");
    EvidenceJsonWriter.SaveProofSnapshot snap = new EvidenceJsonWriter.SaveProofSnapshot(
        true, false, false, false, false, false, false, true, false,
        null, null, 0, false, false,
        null, null, null,
        targetFile.toRealPath().toString(), targetFile.toRealPath(),
        "attr.a3p", testDir.toRealPath(),
        SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO,
        "test-attr");
    String json = EvidenceJsonWriter.saveProofJson(snap);
    assertTrue(json, json.contains("\"kind\": \"save_item_not_attributed\""));
  }

  @Test
  public void saveProofJson_inferredBlocker_ambiguousChooserDiscovery() throws Exception {
    Path testDir = newTestDir();
    Path targetFile = Files.writeString(testDir.resolve("ambig.a3p"), "content");
    EvidenceJsonWriter.SaveProofSnapshot snap = new EvidenceJsonWriter.SaveProofSnapshot(
        true, true, true, true, false, true, false, true, false,
        null, null, 0, false, false,
        null, null, null,
        targetFile.toRealPath().toString(), targetFile.toRealPath(),
        "ambig.a3p", testDir.toRealPath(),
        SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO,
        "test-ambig");
    String json = EvidenceJsonWriter.saveProofJson(snap);
    assertTrue(json, json.contains("\"kind\": \"ambiguous_chooser_discovery\""));
  }

  @Test
  public void saveProofJson_inferredBlocker_chooserControlFailed() throws Exception {
    Path testDir = newTestDir();
    Path targetFile = Files.writeString(testDir.resolve("ctrl.a3p"), "content");
    EvidenceJsonWriter.SaveProofSnapshot snap = new EvidenceJsonWriter.SaveProofSnapshot(
        true, true, true, true, false, false, false, true, false,
        null, null, 0, false, false,
        null, null, null,
        targetFile.toRealPath().toString(), targetFile.toRealPath(),
        "ctrl.a3p", testDir.toRealPath(),
        SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO,
        "test-ctrl");
    String json = EvidenceJsonWriter.saveProofJson(snap);
    assertTrue(json, json.contains("\"kind\": \"chooser_control_failed\""));
  }

  @Test
  public void saveProofJson_inferredBlocker_targetPathRejected() throws Exception {
    Path testDir = newTestDir();
    Path targetFile = Files.writeString(testDir.resolve("reject.txt"), "content");
    EvidenceJsonWriter.SaveProofSnapshot snap = new EvidenceJsonWriter.SaveProofSnapshot(
        true, true, true, true, true, false, true, true, true,
        "javax.swing.JFileChooser",
        targetFile.toRealPath().toString(),
        3, false, false,
        null, null, null,
        targetFile.toRealPath().toString(), targetFile.toRealPath(),
        "reject.txt", testDir.toRealPath(),
        SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO,
        "test-reject");
    String json = EvidenceJsonWriter.saveProofJson(snap);
    assertTrue(json, json.contains("\"kind\": \"target_path_rejected\""));
  }

  @Test
  public void saveProofJson_inferredBlocker_readbackFailed() throws Exception {
    Path testDir = newTestDir();
    Path targetFile = Files.writeString(testDir.resolve("readback.a3p"), "content");
    EvidenceJsonWriter.SaveProofSnapshot snap = new EvidenceJsonWriter.SaveProofSnapshot(
        true, true, true, true, true, false, true, true, true,
        "javax.swing.JFileChooser",
        targetFile.toRealPath().toString(),
        3, false, false,
        null, null, null,
        targetFile.toRealPath().toString(), targetFile.toRealPath(),
        "readback.a3p", testDir.toRealPath(),
        SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO,
        "test-readback");
    String json = EvidenceJsonWriter.saveProofJson(snap);
    assertTrue(json, json.contains("\"kind\": \"readback_failed\""));
  }

  @Test
  public void saveProofJson_inferredBlocker_markerMissing() throws Exception {
    Path testDir = newTestDir();
    Path targetFile = Files.writeString(testDir.resolve("marker.a3p"), "content");
    EvidenceJsonWriter.SaveProofSnapshot snap = new EvidenceJsonWriter.SaveProofSnapshot(
        true, true, true, true, true, false, true, true, true,
        "javax.swing.JFileChooser",
        targetFile.toRealPath().toString(),
        3, true, false,
        null, null, null,
        targetFile.toRealPath().toString(), targetFile.toRealPath(),
        "marker.a3p", testDir.toRealPath(),
        SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO,
        "test-marker");
    String json = EvidenceJsonWriter.saveProofJson(snap);
    assertTrue(json, json.contains("\"kind\": \"marker_missing\""));
  }

  // ── saveActionInvocationProofJson — all 4 reason branches ────────

  @Test
  public void saveActionInvocationProofJson_missingStageIde_unsupported() {
    String json = EvidenceJsonWriter.saveActionInvocationProofJson(
        "org.alice.test.Op", "a3p", false, false,
        SaveOperationCompletionEvidence.InvocationTrigger.none());
    assertTrue(json, json.contains("\"status\": \"unsupported\""));
    assertTrue(json, json.contains("\"reason\": \"missing_active_stage_ide\""));
    assertTrue(json, json.contains(
        "invoke SaveProjectOperation from a running Alice desktop with StageIDE.getActiveInstance() resolved"));
  }

  @Test
  public void saveActionInvocationProofJson_missingDocFrame_blocked() {
    String json = EvidenceJsonWriter.saveActionInvocationProofJson(
        "org.alice.test.Op", "a3p", true, false,
        SaveOperationCompletionEvidence.InvocationTrigger.none());
    assertTrue(json, json.contains("\"status\": \"blocked\""));
    assertTrue(json, json.contains("\"reason\": \"missing_project_document_frame\""));
    assertTrue(json, json.contains(
        "invoke SaveProjectOperation from an initialized Alice desktop with a ProjectDocumentFrame"));
  }

  @Test
  public void saveActionInvocationProofJson_actionInvoked() {
    String json = EvidenceJsonWriter.saveActionInvocationProofJson(
        "org.alice.test.Op", "a3p", true, true,
        SaveOperationCompletionEvidence.InvocationTrigger.none());
    assertTrue(json, json.contains("\"status\": \"action_invoked\""));
    assertTrue(json, json.contains("\"reason\": \"save_action_invoked\""));
    assertTrue(json, json.contains("\"menu_item_dispatch\": false"));
    assertTrue(json, json.contains(
        "\"desktop Save menu item was clicked\""));
  }

  @Test
  public void saveActionInvocationProofJson_menuItemDispatched() {
    SaveOperationCompletionEvidence.InvocationTrigger menuTrigger =
        createTrigger(
            "org.lgna.croquet.triggers.ActionEventTrigger",
            "org.lgna.croquet.views.MenuItem",
            "javax.swing.JMenuItem");
    String json = EvidenceJsonWriter.saveActionInvocationProofJson(
        "org.alice.test.Op", "a3p", true, true, menuTrigger);
    assertTrue(json, json.contains("\"status\": \"menu_item_dispatched\""));
    assertTrue(json, json.contains("\"reason\": \"save_menu_item_dispatched\""));
    assertTrue(json, json.contains("\"menu_item_dispatch\": true"));
    assertFalse("Menu item claim is NOT in doesNotClaim",
        json.contains("\"desktop Save menu item was clicked\",\n"));
  }

  // ── saveActionDoesNotClaimJson branches ───────────────────────────

  @Test
  public void saveActionDoesNotClaimJson_nonMenuDispatch_includesMenuClaim() {
    String json = EvidenceJsonWriter.saveActionInvocationProofJson(
        "org.alice.test.Op", "a3p", true, true,
        SaveOperationCompletionEvidence.InvocationTrigger.none());
    assertTrue(json, json.contains("\"desktop Save menu item was clicked\""));
  }

  @Test
  public void saveActionDoesNotClaimJson_menuDispatch_excludesMenuClaim() {
    SaveOperationCompletionEvidence.InvocationTrigger menuTrigger =
        createTrigger(
            "org.lgna.croquet.triggers.ActionEventTrigger",
            "org.lgna.croquet.views.MenuItem",
            "javax.swing.JMenuItem");
    String json = EvidenceJsonWriter.saveActionInvocationProofJson(
        "org.alice.test.Op", "a3p", true, true, menuTrigger);
    // When menu dispatched, "desktop Save menu item was clicked" is omitted
    String doesNotClaimSection = json.substring(json.indexOf("\"doesNotClaim\""));
    assertFalse("Menu item claim excluded from doesNotClaim for menu dispatch",
        doesNotClaimSection.contains("desktop Save menu item was clicked"));
  }

  // ── saveActionRequiresNextEvidenceJson branches ──────────────────

  @Test
  public void saveActionRequiresNextEvidence_actionInvoked_hasDialogDiscovery() {
    String json = EvidenceJsonWriter.saveActionInvocationProofJson(
        "org.alice.test.Op", "a3p", true, true,
        SaveOperationCompletionEvidence.InvocationTrigger.none());
    assertTrue(json, json.contains(
        "desktop Save dialog discovery artifact with target_resolved"));
  }

  @Test
  public void saveActionRequiresNextEvidence_missingDocFrame_hasProjectDocFrame() {
    String json = EvidenceJsonWriter.saveActionInvocationProofJson(
        "org.alice.test.Op", "a3p", true, false,
        SaveOperationCompletionEvidence.InvocationTrigger.none());
    assertTrue(json, json.contains(
        "invoke SaveProjectOperation from an initialized Alice desktop with a ProjectDocumentFrame"));
  }

  @Test
  public void saveActionRequiresNextEvidence_missingStageIde_hasStageIdeResolved() {
    String json = EvidenceJsonWriter.saveActionInvocationProofJson(
        "org.alice.test.Op", "a3p", false, false,
        SaveOperationCompletionEvidence.InvocationTrigger.none());
    assertTrue(json, json.contains(
        "invoke SaveProjectOperation from a running Alice desktop with StageIDE.getActiveInstance() resolved"));
  }

  // ── resultClaimOrSummaryJson edge cases ──────────────────────────

  @Test
  public void resultClaimOrSummaryJson_wroteTrue_nullExtension() {
    String json = EvidenceJsonWriter.resultClaimOrSummaryJson(true, "finished", null);
    assertTrue(json, json.contains("a non-empty project file write"));
    assertFalse(json, json.contains(".null"));
  }

  @Test
  public void resultClaimOrSummaryJson_wroteFalse_blankExtension() {
    String json = EvidenceJsonWriter.resultClaimOrSummaryJson(false, "incomplete", "");
    assertTrue(json, json.contains("reporting_summary"));
    assertTrue(json, json.contains("a non-empty project file write"));
  }

  @Test
  public void resultClaimOrSummaryJson_wroteFalse_canceledStatus() {
    String json = EvidenceJsonWriter.resultClaimOrSummaryJson(false, "canceled", "a3p");
    assertTrue(json, json.contains("status canceled"));
  }

  // ── nonEmptyProjectFileWrite edge cases ──────────────────────────

  @Test
  public void nonEmptyProjectFileWrite_withWhitespaceOnlyExtension() {
    assertEquals("a non-empty project file write",
        EvidenceJsonWriter.nonEmptyProjectFileWrite("   "));
  }

  @Test
  public void nonEmptyProjectFileWrite_withTabExtension() {
    assertEquals("a non-empty project file write",
        EvidenceJsonWriter.nonEmptyProjectFileWrite("\t"));
  }

  // ── dialogControlTargetJson edge cases ───────────────────────────

  @Test
  public void dialogControlTargetJson_nullOperationClass() {
    SaveOperationFlow.Result result =
        new SaveOperationFlow.Result(true, false, 1, 1, null);
    String json = EvidenceJsonWriter.dialogControlTargetJson(null, "a3p", result);
    assertTrue(json, json.contains("\"operation\": \"\""));
  }

  @Test
  public void dialogControlTargetJson_nullExtension() {
    SaveOperationFlow.Result result =
        new SaveOperationFlow.Result(true, false, 1, 1, null);
    String json = EvidenceJsonWriter.dialogControlTargetJson("Op", null, result);
    assertTrue(json, json.contains("\"extension\": \"\""));
  }

  @Test
  public void dialogControlTargetJson_blockedReportingSummary() {
    SaveOperationFlow.Result result =
        new SaveOperationFlow.Result(true, false, 1, 1, null);
    String json = EvidenceJsonWriter.dialogControlTargetJson("Op", "a3p", result);
    assertTrue(json, json.contains(
        "SaveOperationFlow requested the production Save dialog seam"));
  }

  @Test
  public void dialogControlTargetJson_unsupportedReportingSummary() {
    SaveOperationFlow.Result result =
        new SaveOperationFlow.Result(true, false, 0, 1, null);
    String json = EvidenceJsonWriter.dialogControlTargetJson("Op", "a3p", result);
    assertTrue(json, json.contains(
        "No Save dialog was requested"));
  }

  @Test
  public void dialogControlTargetJson_containsMissingEvidence() {
    SaveOperationFlow.Result result =
        new SaveOperationFlow.Result(true, false, 1, 1, null);
    String json = EvidenceJsonWriter.dialogControlTargetJson("Op", "a3p", result);
    assertTrue(json, json.contains("\"missing_evidence\": ["));
    assertTrue(json, json.contains("desktop Save dialog discovery"));
  }

  @Test
  public void dialogControlTargetJson_containsRequiresNextEvidence() {
    SaveOperationFlow.Result result =
        new SaveOperationFlow.Result(true, false, 1, 1, null);
    String json = EvidenceJsonWriter.dialogControlTargetJson("Op", "a3p", result);
    assertTrue(json, json.contains("\"requiresNextEvidence\": ["));
    assertTrue(json, json.contains("desktop Save dialog owner/component artifact"));
  }

  // ── resultJson edge cases ────────────────────────────────────────

  @Test
  public void resultJson_nullOperationAndNullExtension() {
    SaveOperationFlow.Result result =
        new SaveOperationFlow.Result(false, false, 0, 0, null);
    String json = EvidenceJsonWriter.resultJson(
        null, null, result, EvidenceFileOperations.RegularFileState.MISSING);
    assertTrue(json, json.contains("\"operation\": \"\""));
    assertTrue(json, json.contains("\"extension\": \"\""));
    assertTrue(json, json.contains("\"status\": \"incomplete\""));
  }

  @Test
  public void resultJson_specialCharsInOperationClassEscaped() {
    SaveOperationFlow.Result result =
        new SaveOperationFlow.Result(true, false, 1, 1, null);
    String json = EvidenceJsonWriter.resultJson(
        "org.test.\"Op\"", "a3p", result,
        EvidenceFileOperations.RegularFileState.MISSING);
    assertTrue(json, json.contains("\\\"Op\\\""));
  }

  @Test
  public void resultJson_wrongExtensionMeansWroteFileFalse() {
    SaveOperationFlow.Result result =
        new SaveOperationFlow.Result(true, false, 1, 1,
            new File("target/test-save/classroom.txt"));
    EvidenceFileOperations.RegularFileState state =
        new EvidenceFileOperations.RegularFileState(true, 1024);
    String json = EvidenceJsonWriter.resultJson(
        "org.alice.test.Op", "a3p", result, state);
    assertTrue(json, json.contains("\"wroteFile\": false"));
  }

  // ── operationSimpleName edge cases ───────────────────────────────

  @Test
  public void operationSimpleName_emptyString() {
    assertEquals("", EvidenceJsonWriter.operationSimpleName(""));
  }

  @Test
  public void operationSimpleName_singleDotPrefix() {
    assertEquals("Op", EvidenceJsonWriter.operationSimpleName(".Op"));
  }

  @Test
  public void operationSimpleName_trailingDot() {
    assertEquals("", EvidenceJsonWriter.operationSimpleName("org.alice."));
  }

  // ── className edge cases ─────────────────────────────────────────

  @Test
  public void className_forPrimitive() {
    assertEquals("java.lang.Integer", EvidenceJsonWriter.className(42));
  }

  @Test
  public void className_forArray() {
    assertEquals("[Ljava.lang.String;", EvidenceJsonWriter.className(new String[]{}));
  }

  // ── status edge cases ────────────────────────────────────────────

  @Test
  public void status_finishedTakesPrecedenceOverCanceled() {
    SaveOperationFlow.Result result =
        new SaveOperationFlow.Result(true, true, 1, 1, null);
    assertEquals("finished", EvidenceJsonWriter.status(result));
  }

  // ── helper ───────────────────────────────────────────────────────

  private String saveProofJsonForDefaults() {
    Path testDir;
    try {
      testDir = newTestDir();
      Files.writeString(testDir.resolve("default.a3p"), "content");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    try {
      EvidenceJsonWriter.SaveProofSnapshot snap = new EvidenceJsonWriter.SaveProofSnapshot(
          false, false, false, false, false, false, false, true, false,
          null, null, 0, false, false,
          null, null, null,
          testDir.resolve("default.a3p").toRealPath().toString(),
          testDir.resolve("default.a3p").toRealPath(),
          "default.a3p",
          testDir.toRealPath(),
          SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO,
          "round-trip-test-" + UUID.randomUUID());
      return EvidenceJsonWriter.saveProofJson(snap);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static Path newTestDir() throws Exception {
    return Files.createDirectories(Path.of(
        "target",
        "evidence-json-writer-round-trip-test",
        UUID.randomUUID().toString()));
  }

  private static SaveOperationCompletionEvidence.InvocationTrigger createTrigger(
      String triggerClass, String viewControllerClass, String awtSourceClass) {
    try {
      Constructor<SaveOperationCompletionEvidence.InvocationTrigger> ctor =
          SaveOperationCompletionEvidence.InvocationTrigger.class
              .getDeclaredConstructor(String.class, String.class, String.class);
      ctor.setAccessible(true);
      return ctor.newInstance(triggerClass, viewControllerClass, awtSourceClass);
    } catch (Exception e) {
      throw new RuntimeException("Failed to create InvocationTrigger via reflection", e);
    }
  }
}
