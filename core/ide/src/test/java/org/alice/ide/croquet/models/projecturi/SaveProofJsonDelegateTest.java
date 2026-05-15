package org.alice.ide.croquet.models.projecturi;

import org.junit.Test;

import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * TDD tests for SaveProofJsonDelegate — the extracted save-proof JSON
 * section builders from EvidenceJsonWriter (issue #670).
 *
 * These tests define the contract that SaveProofJsonDelegate must satisfy.
 * They will FAIL until the extraction is implemented.
 */
public class SaveProofJsonDelegateTest {

  private static final Path PROOF_ROOT = Path.of("/tmp/alice-proof-root");
  private static final Path TARGET_PATH = Path.of("/tmp/alice-proof-root/classroom.a3p");
  private static final String TARGET_CANONICAL = "/tmp/alice-proof-root/classroom.a3p";
  private static final String TARGET_FILE_NAME = "classroom.a3p";
  private static final String SCENARIO = "test-scenario";
  private static final String RUN_ID = "run-001";

  // ── headerJson ────────────────────────────────────────────────────

  @Test
  public void headerJsonContainsSchemaVersion() {
    EvidenceJsonWriter.SaveProofSnapshot snap = provenSnapshot();
    String json = SaveProofJsonDelegate.headerJson("proven", true, snap);
    assertTrue(json, json.contains("\"schemaVersion\": \""
        + SaveOperationCompletionEvidence.SAVE_PROOF_SCHEMA_VERSION + "\""));
  }

  @Test
  public void headerJsonContainsScenarioAndRunId() {
    EvidenceJsonWriter.SaveProofSnapshot snap = provenSnapshot();
    String json = SaveProofJsonDelegate.headerJson("proven", true, snap);
    assertTrue(json, json.contains("\"scenario\": \"" + SCENARIO + "\""));
    assertTrue(json, json.contains("\"runId\": \"" + RUN_ID + "\""));
  }

  @Test
  public void headerJsonContainsWorkflow() {
    EvidenceJsonWriter.SaveProofSnapshot snap = provenSnapshot();
    String json = SaveProofJsonDelegate.headerJson("proven", true, snap);
    assertTrue(json, json.contains("\"workflow\": \""
        + SaveOperationCompletionEvidence.SAVE_PROOF_WORKFLOW + "\""));
  }

  @Test
  public void headerJsonContainsGeneratedAtUtc() {
    EvidenceJsonWriter.SaveProofSnapshot snap = provenSnapshot();
    String json = SaveProofJsonDelegate.headerJson("proven", true, snap);
    assertTrue(json, json.contains("\"generatedAtUtc\": \""));
  }

  @Test
  public void headerJsonContainsStatusField() {
    EvidenceJsonWriter.SaveProofSnapshot snap = provenSnapshot();
    String json = SaveProofJsonDelegate.headerJson("proven", true, snap);
    assertTrue(json, json.contains("\"status\": \"proven\""));
  }

  @Test
  public void headerJsonContainsClaimWhenProven() {
    EvidenceJsonWriter.SaveProofSnapshot snap = provenSnapshot();
    String json = SaveProofJsonDelegate.headerJson("proven", true, snap);
    assertTrue(json, json.contains("\"claim\":"));
    assertFalse(json, json.contains("\"reportingSummary\":"));
  }

  @Test
  public void headerJsonContainsReportingSummaryWhenBlocked() {
    EvidenceJsonWriter.SaveProofSnapshot snap = blockedSnapshot();
    String json = SaveProofJsonDelegate.headerJson("blocked", false, snap);
    assertTrue(json, json.contains("\"reportingSummary\":"));
    assertFalse(json, json.contains("\"claim\":"));
  }

  @Test
  public void headerJsonContainsProofTarget() {
    EvidenceJsonWriter.SaveProofSnapshot snap = provenSnapshot();
    String json = SaveProofJsonDelegate.headerJson("proven", true, snap);
    assertTrue(json, json.contains("\"proofTarget\":"));
  }

  @Test
  public void headerJsonEscapesScenarioSpecialChars() {
    EvidenceJsonWriter.SaveProofSnapshot snap = snapshotBuilder()
        .scenario("test\"scenario")
        .runId(RUN_ID)
        .build();
    String json = SaveProofJsonDelegate.headerJson("proven", true, snap);
    assertTrue(json, json.contains("\"scenario\": \"test\\\"scenario\""));
  }

  // ── blockerJson ───────────────────────────────────────────────────

  @Test
  public void blockerJsonReturnsNullWhenProven() {
    String json = SaveProofJsonDelegate.blockerJson(true, null, null, null);
    assertTrue(json, json.contains("\"blocker\": null"));
  }

  @Test
  public void blockerJsonReturnsObjectWhenBlocked() {
    String json = SaveProofJsonDelegate.blockerJson(
        false, "file_menu_not_showing",
        "The rendered File menu was not opened by Robot",
        "A complete Robot File menu Save activation");
    assertTrue(json, json.contains("\"blocker\": {"));
    assertTrue(json, json.contains("\"kind\": \"file_menu_not_showing\""));
    assertTrue(json, json.contains("\"observed\":"));
    assertTrue(json, json.contains("\"required\":"));
  }

  @Test
  public void blockerJsonEscapesStringValues() {
    String json = SaveProofJsonDelegate.blockerJson(
        false, "test\"kind", "obs\"erved", "req\"uired");
    assertTrue(json, json.contains("\"kind\": \"test\\\"kind\""));
    assertTrue(json, json.contains("\"observed\": \"obs\\\"erved\""));
    assertTrue(json, json.contains("\"required\": \"req\\\"uired\""));
  }

  @Test
  public void blockerJsonHandlesNullObservedAndRequired() {
    String json = SaveProofJsonDelegate.blockerJson(
        false, "some_kind", null, null);
    assertTrue(json, json.contains("\"observed\": \"\""));
    assertTrue(json, json.contains("\"required\": \"\""));
  }

  // ── menuJson ──────────────────────────────────────────────────────

  @Test
  public void menuJsonContainsFileMenuOpened() {
    EvidenceJsonWriter.SaveProofSnapshot snap = provenSnapshot();
    String json = SaveProofJsonDelegate.menuJson(snap);
    assertTrue(json, json.contains("\"menu\": {"));
    assertTrue(json, json.contains("\"fileMenuOpened\": true"));
  }

  @Test
  public void menuJsonContainsSaveMenuItemInvoked() {
    EvidenceJsonWriter.SaveProofSnapshot snap = provenSnapshot();
    String json = SaveProofJsonDelegate.menuJson(snap);
    assertTrue(json, json.contains("\"saveMenuItemInvoked\": true"));
  }

  @Test
  public void menuJsonContainsSaveActionIdentityMatched() {
    EvidenceJsonWriter.SaveProofSnapshot snap = provenSnapshot();
    String json = SaveProofJsonDelegate.menuJson(snap);
    assertTrue(json, json.contains("\"saveActionIdentityMatched\": true"));
  }

  @Test
  public void menuJsonShowsFalseWhenMenuNotOpened() {
    EvidenceJsonWriter.SaveProofSnapshot snap = snapshotBuilder()
        .robotFileMenuOpened(false)
        .build();
    String json = SaveProofJsonDelegate.menuJson(snap);
    assertTrue(json, json.contains("\"fileMenuOpened\": false"));
  }

  // ── dialogJson ────────────────────────────────────────────────────

  @Test
  public void dialogJsonContainsSaveDialogObserved() {
    EvidenceJsonWriter.SaveProofSnapshot snap = provenSnapshot();
    String json = SaveProofJsonDelegate.dialogJson(snap);
    assertTrue(json, json.contains("\"dialog\": {"));
    assertTrue(json, json.contains("\"saveDialogObserved\": true"));
  }

  @Test
  public void dialogJsonContainsDialogType() {
    EvidenceJsonWriter.SaveProofSnapshot snap = provenSnapshot();
    String json = SaveProofJsonDelegate.dialogJson(snap);
    assertTrue(json, json.contains("\"dialogType\": \"Swing JFileChooser\""));
  }

  @Test
  public void dialogJsonContainsDialogClassAsStringJson() {
    EvidenceJsonWriter.SaveProofSnapshot snap = snapshotBuilder()
        .dialogClass("javax.swing.JFileChooser")
        .build();
    String json = SaveProofJsonDelegate.dialogJson(snap);
    assertTrue(json, json.contains("\"dialogClass\": \"javax.swing.JFileChooser\""));
  }

  @Test
  public void dialogJsonReportsNullDialogClass() {
    EvidenceJsonWriter.SaveProofSnapshot snap = snapshotBuilder()
        .dialogClass(null)
        .build();
    String json = SaveProofJsonDelegate.dialogJson(snap);
    assertTrue(json, json.contains("\"dialogClass\": null"));
  }

  @Test
  public void dialogJsonContainsDialogShowing() {
    EvidenceJsonWriter.SaveProofSnapshot snap = provenSnapshot();
    String json = SaveProofJsonDelegate.dialogJson(snap);
    assertTrue(json, json.contains("\"dialogShowing\": true"));
  }

  @Test
  public void dialogJsonContainsAmbiguousChooserDiscovery() {
    EvidenceJsonWriter.SaveProofSnapshot snap = provenSnapshot();
    String json = SaveProofJsonDelegate.dialogJson(snap);
    assertTrue(json, json.contains("\"ambiguousChooserDiscovery\": false"));
  }

  @Test
  public void dialogJsonContainsPollCount() {
    EvidenceJsonWriter.SaveProofSnapshot snap = snapshotBuilder()
        .pollCount(5)
        .build();
    String json = SaveProofJsonDelegate.dialogJson(snap);
    assertTrue(json, json.contains("\"pollCount\": 5"));
  }

  // ── controlJson ───────────────────────────────────────────────────

  @Test
  public void controlJsonContainsSelectedPathSet() {
    EvidenceJsonWriter.SaveProofSnapshot snap = provenSnapshot();
    Path selectedPath = TARGET_PATH;
    String json = SaveProofJsonDelegate.controlJson(snap, selectedPath, true);
    assertTrue(json, json.contains("\"control\": {"));
    assertTrue(json, json.contains("\"selectedPathSet\": true"));
  }

  @Test
  public void controlJsonContainsApprovedSelection() {
    EvidenceJsonWriter.SaveProofSnapshot snap = provenSnapshot();
    String json = SaveProofJsonDelegate.controlJson(snap, TARGET_PATH, true);
    assertTrue(json, json.contains("\"approvedSelection\": true"));
  }

  @Test
  public void controlJsonContainsSelectedPathMatchesExpected() {
    EvidenceJsonWriter.SaveProofSnapshot snap = provenSnapshot();
    String json = SaveProofJsonDelegate.controlJson(snap, TARGET_PATH, true);
    assertTrue(json, json.contains("\"selectedPathMatchesExpected\": true"));
  }

  @Test
  public void controlJsonReportsFalseWhenPathDoesNotMatch() {
    EvidenceJsonWriter.SaveProofSnapshot snap = provenSnapshot();
    String json = SaveProofJsonDelegate.controlJson(snap, TARGET_PATH, false);
    assertTrue(json, json.contains("\"selectedPathMatchesExpected\": false"));
  }

  @Test
  public void controlJsonContainsTargetInsideProofRoot() {
    EvidenceJsonWriter.SaveProofSnapshot snap = provenSnapshot();
    String json = SaveProofJsonDelegate.controlJson(snap, TARGET_PATH, true);
    assertTrue(json, json.contains("\"targetInsideProofRoot\": true"));
  }

  @Test
  public void controlJsonContainsNormalizedAndExpectedPaths() {
    EvidenceJsonWriter.SaveProofSnapshot snap = provenSnapshot();
    String json = SaveProofJsonDelegate.controlJson(snap, TARGET_PATH, true);
    assertTrue(json, json.contains("\"normalizedSelectedPath\":"));
    assertTrue(json, json.contains("\"expectedPath\":"));
  }

  @Test
  public void controlJsonHandlesNullSelectedPath() {
    EvidenceJsonWriter.SaveProofSnapshot snap = provenSnapshot();
    String json = SaveProofJsonDelegate.controlJson(snap, null, false);
    assertTrue(json, json.contains("\"normalizedSelectedPath\": null"));
  }

  // ── writeJson ─────────────────────────────────────────────────────

  @Test
  public void writeJsonContainsFileWritten() {
    EvidenceJsonWriter.SaveProofSnapshot snap = provenSnapshot();
    String json = SaveProofJsonDelegate.writeJson(true, true, true, 1024, snap);
    assertTrue(json, json.contains("\"write\": {"));
    assertTrue(json, json.contains("\"fileWritten\": true"));
  }

  @Test
  public void writeJsonContainsFileNonempty() {
    EvidenceJsonWriter.SaveProofSnapshot snap = provenSnapshot();
    String json = SaveProofJsonDelegate.writeJson(true, true, true, 1024, snap);
    assertTrue(json, json.contains("\"fileNonempty\": true"));
  }

  @Test
  public void writeJsonContainsFileHasExpectedExtension() {
    EvidenceJsonWriter.SaveProofSnapshot snap = provenSnapshot();
    String json = SaveProofJsonDelegate.writeJson(true, true, true, 1024, snap);
    assertTrue(json, json.contains("\"fileHasExpectedExtension\": true"));
  }

  @Test
  public void writeJsonContainsOutputPath() {
    EvidenceJsonWriter.SaveProofSnapshot snap = provenSnapshot();
    String json = SaveProofJsonDelegate.writeJson(true, true, true, 1024, snap);
    assertTrue(json, json.contains("\"outputPath\":"));
  }

  @Test
  public void writeJsonContainsOutputSizeBytes() {
    EvidenceJsonWriter.SaveProofSnapshot snap = provenSnapshot();
    String json = SaveProofJsonDelegate.writeJson(true, true, true, 2048, snap);
    assertTrue(json, json.contains("\"outputSizeBytes\": 2048"));
  }

  @Test
  public void writeJsonReportsFalseWhenFileNotWritten() {
    EvidenceJsonWriter.SaveProofSnapshot snap = provenSnapshot();
    String json = SaveProofJsonDelegate.writeJson(false, false, true, 0, snap);
    assertTrue(json, json.contains("\"fileWritten\": false"));
    assertTrue(json, json.contains("\"fileNonempty\": false"));
  }

  // ── readbackJson ──────────────────────────────────────────────────

  @Test
  public void readbackJsonContainsProjectReadable() {
    EvidenceJsonWriter.SaveProofSnapshot snap = provenSnapshot();
    String json = SaveProofJsonDelegate.readbackJson(snap);
    assertTrue(json, json.contains("\"readback\": {"));
    assertTrue(json, json.contains("\"projectReadable\": true"));
  }

  @Test
  public void readbackJsonContainsMarkerConstant() {
    EvidenceJsonWriter.SaveProofSnapshot snap = provenSnapshot();
    String json = SaveProofJsonDelegate.readbackJson(snap);
    assertTrue(json, json.contains("\"marker\": \""
        + SaveOperationCompletionEvidence.SAVE_PROOF_MARKER + "\""));
  }

  @Test
  public void readbackJsonContainsMarkerPresent() {
    EvidenceJsonWriter.SaveProofSnapshot snap = provenSnapshot();
    String json = SaveProofJsonDelegate.readbackJson(snap);
    assertTrue(json, json.contains("\"markerPresent\": true"));
  }

  @Test
  public void readbackJsonReportsFalseWhenNotReadable() {
    EvidenceJsonWriter.SaveProofSnapshot snap = snapshotBuilder()
        .projectReadable(false)
        .markerPresent(false)
        .build();
    String json = SaveProofJsonDelegate.readbackJson(snap);
    assertTrue(json, json.contains("\"projectReadable\": false"));
    assertTrue(json, json.contains("\"markerPresent\": false"));
  }

  // ── baselinePreservedJson ─────────────────────────────────────────

  @Test
  public void baselinePreservedJsonContainsArray() {
    String json = SaveProofJsonDelegate.baselinePreservedJson();
    assertTrue(json, json.contains("\"baselinePreserved\": ["));
  }

  @Test
  public void baselinePreservedJsonListsThreeTestClasses() {
    String json = SaveProofJsonDelegate.baselinePreservedJson();
    assertTrue(json, json.contains("StageIdeSaveMenuDoClickToWriteProofTest"));
    assertTrue(json, json.contains("ProjectApplicationSaveProjectToTest"));
    assertTrue(json, json.contains("JMenuBarRobotClickSaveProofTest"));
  }

  // ── requiresNextEvidenceJson ──────────────────────────────────────

  @Test
  public void requiresNextEvidenceJsonContainsArray() {
    String json = SaveProofJsonDelegate.requiresNextEvidenceJson();
    assertTrue(json, json.contains("\"requiresNextEvidence\": ["));
  }

  @Test
  public void requiresNextEvidenceJsonMentionsXvfb() {
    String json = SaveProofJsonDelegate.requiresNextEvidenceJson();
    assertTrue(json, json.contains("xvfb-run"));
  }

  // ── doesNotClaimJson ──────────────────────────────────────────────

  @Test
  public void doesNotClaimJsonContainsArray() {
    String json = SaveProofJsonDelegate.doesNotClaimJson();
    assertTrue(json, json.contains("\"doesNotClaim\": ["));
  }

  @Test
  public void doesNotClaimJsonListsExcludedScopes() {
    String json = SaveProofJsonDelegate.doesNotClaimJson();
    assertTrue(json, json.contains("Save As coverage"));
    assertTrue(json, json.contains("all Save variants"));
    assertTrue(json, json.contains("full lesson completion"));
    assertTrue(json, json.contains("visible rendering correctness"));
    assertTrue(json, json.contains("grading correctness"));
    assertTrue(json, json.contains("native dialog coverage"));
  }

  // ── inferBlockerKind ──────────────────────────────────────────────

  @Test
  public void inferBlockerKindReturnsFileMenuNotShowing() {
    EvidenceJsonWriter.SaveProofSnapshot snap = snapshotBuilder()
        .robotFileMenuOpened(false)
        .build();
    assertEquals("file_menu_not_showing",
        SaveProofJsonDelegate.inferBlockerKind(snap, false));
  }

  @Test
  public void inferBlockerKindReturnsSaveItemNotAttributedWhenNotClicked() {
    EvidenceJsonWriter.SaveProofSnapshot snap = snapshotBuilder()
        .robotFileMenuOpened(true)
        .robotSaveItemClicked(false)
        .build();
    assertEquals("save_item_not_attributed",
        SaveProofJsonDelegate.inferBlockerKind(snap, false));
  }

  @Test
  public void inferBlockerKindReturnsSaveItemNotAttributedWhenIdentityNotMatched() {
    EvidenceJsonWriter.SaveProofSnapshot snap = snapshotBuilder()
        .robotFileMenuOpened(true)
        .robotSaveItemClicked(true)
        .saveActionIdentityMatched(false)
        .build();
    assertEquals("save_item_not_attributed",
        SaveProofJsonDelegate.inferBlockerKind(snap, false));
  }

  @Test
  public void inferBlockerKindReturnsDialogNotObserved() {
    EvidenceJsonWriter.SaveProofSnapshot snap = snapshotBuilder()
        .robotFileMenuOpened(true)
        .robotSaveItemClicked(true)
        .saveActionIdentityMatched(true)
        .chooserObserved(false)
        .build();
    assertEquals("dialog_not_observed",
        SaveProofJsonDelegate.inferBlockerKind(snap, false));
  }

  @Test
  public void inferBlockerKindReturnsAmbiguousChooserDiscovery() {
    EvidenceJsonWriter.SaveProofSnapshot snap = snapshotBuilder()
        .robotFileMenuOpened(true)
        .robotSaveItemClicked(true)
        .saveActionIdentityMatched(true)
        .chooserObserved(true)
        .ambiguousChooserDiscovery(true)
        .build();
    assertEquals("ambiguous_chooser_discovery",
        SaveProofJsonDelegate.inferBlockerKind(snap, false));
  }

  @Test
  public void inferBlockerKindReturnsChooserControlFailedWhenDialogNotShowing() {
    EvidenceJsonWriter.SaveProofSnapshot snap = snapshotBuilder()
        .robotFileMenuOpened(true)
        .robotSaveItemClicked(true)
        .saveActionIdentityMatched(true)
        .chooserObserved(true)
        .ambiguousChooserDiscovery(false)
        .dialogShowing(false)
        .build();
    assertEquals("chooser_control_failed",
        SaveProofJsonDelegate.inferBlockerKind(snap, false));
  }

  @Test
  public void inferBlockerKindReturnsChooserControlFailedWhenNotVerified() {
    EvidenceJsonWriter.SaveProofSnapshot snap = snapshotBuilder()
        .robotFileMenuOpened(true)
        .robotSaveItemClicked(true)
        .saveActionIdentityMatched(true)
        .chooserObserved(true)
        .ambiguousChooserDiscovery(false)
        .dialogShowing(true)
        .selectedFileVerified(false)
        .build();
    assertEquals("chooser_control_failed",
        SaveProofJsonDelegate.inferBlockerKind(snap, false));
  }

  @Test
  public void inferBlockerKindReturnsChooserControlFailedWhenNotApproved() {
    EvidenceJsonWriter.SaveProofSnapshot snap = snapshotBuilder()
        .robotFileMenuOpened(true)
        .robotSaveItemClicked(true)
        .saveActionIdentityMatched(true)
        .chooserObserved(true)
        .ambiguousChooserDiscovery(false)
        .dialogShowing(true)
        .selectedFileVerified(true)
        .approvedSelection(false)
        .build();
    assertEquals("chooser_control_failed",
        SaveProofJsonDelegate.inferBlockerKind(snap, false));
  }

  @Test
  public void inferBlockerKindReturnsTargetPathRejectedWhenOutsideRoot() {
    EvidenceJsonWriter.SaveProofSnapshot snap = snapshotBuilder()
        .robotFileMenuOpened(true)
        .robotSaveItemClicked(true)
        .saveActionIdentityMatched(true)
        .chooserObserved(true)
        .ambiguousChooserDiscovery(false)
        .dialogShowing(true)
        .selectedFileVerified(true)
        .approvedSelection(true)
        .targetInsideProofRoot(false)
        .targetFileName(TARGET_FILE_NAME)
        .build();
    assertEquals("target_path_rejected",
        SaveProofJsonDelegate.inferBlockerKind(snap, false));
  }

  @Test
  public void inferBlockerKindReturnsTargetPathRejectedWhenNotA3p() {
    EvidenceJsonWriter.SaveProofSnapshot snap = snapshotBuilder()
        .robotFileMenuOpened(true)
        .robotSaveItemClicked(true)
        .saveActionIdentityMatched(true)
        .chooserObserved(true)
        .ambiguousChooserDiscovery(false)
        .dialogShowing(true)
        .selectedFileVerified(true)
        .approvedSelection(true)
        .targetInsideProofRoot(true)
        .targetFileName("classroom.txt")
        .build();
    assertEquals("target_path_rejected",
        SaveProofJsonDelegate.inferBlockerKind(snap, false));
  }

  @Test
  public void inferBlockerKindReturnsWriteNotObserved() {
    EvidenceJsonWriter.SaveProofSnapshot snap = snapshotBuilder()
        .robotFileMenuOpened(true)
        .robotSaveItemClicked(true)
        .saveActionIdentityMatched(true)
        .chooserObserved(true)
        .ambiguousChooserDiscovery(false)
        .dialogShowing(true)
        .selectedFileVerified(true)
        .approvedSelection(true)
        .targetInsideProofRoot(true)
        .targetFileName(TARGET_FILE_NAME)
        .build();
    assertEquals("write_not_observed",
        SaveProofJsonDelegate.inferBlockerKind(snap, false));
  }

  @Test
  public void inferBlockerKindReturnsReadbackFailed() {
    EvidenceJsonWriter.SaveProofSnapshot snap = snapshotBuilder()
        .robotFileMenuOpened(true)
        .robotSaveItemClicked(true)
        .saveActionIdentityMatched(true)
        .chooserObserved(true)
        .ambiguousChooserDiscovery(false)
        .dialogShowing(true)
        .selectedFileVerified(true)
        .approvedSelection(true)
        .targetInsideProofRoot(true)
        .targetFileName(TARGET_FILE_NAME)
        .projectReadable(false)
        .build();
    assertEquals("readback_failed",
        SaveProofJsonDelegate.inferBlockerKind(snap, true));
  }

  @Test
  public void inferBlockerKindReturnsMarkerMissingAsFallback() {
    EvidenceJsonWriter.SaveProofSnapshot snap = snapshotBuilder()
        .robotFileMenuOpened(true)
        .robotSaveItemClicked(true)
        .saveActionIdentityMatched(true)
        .chooserObserved(true)
        .ambiguousChooserDiscovery(false)
        .dialogShowing(true)
        .selectedFileVerified(true)
        .approvedSelection(true)
        .targetInsideProofRoot(true)
        .targetFileName(TARGET_FILE_NAME)
        .projectReadable(true)
        .build();
    assertEquals("marker_missing",
        SaveProofJsonDelegate.inferBlockerKind(snap, true));
  }

  // ── inferBlockerObserved ──────────────────────────────────────────

  @Test
  public void inferBlockerObservedFileMenuNotOpened() {
    EvidenceJsonWriter.SaveProofSnapshot snap = snapshotBuilder()
        .robotFileMenuOpened(false)
        .build();
    String observed = SaveProofJsonDelegate.inferBlockerObserved(snap, false);
    assertTrue(observed, observed.contains("File menu"));
    assertTrue(observed, observed.contains("not opened"));
  }

  @Test
  public void inferBlockerObservedSaveItemNotAttributed() {
    EvidenceJsonWriter.SaveProofSnapshot snap = snapshotBuilder()
        .robotFileMenuOpened(true)
        .robotSaveItemClicked(false)
        .build();
    String observed = SaveProofJsonDelegate.inferBlockerObserved(snap, false);
    assertTrue(observed, observed.contains("Save item"));
    assertTrue(observed, observed.contains("not attributed"));
  }

  @Test
  public void inferBlockerObservedDialogNotObserved() {
    EvidenceJsonWriter.SaveProofSnapshot snap = snapshotBuilder()
        .robotFileMenuOpened(true)
        .robotSaveItemClicked(true)
        .saveActionIdentityMatched(true)
        .chooserObserved(false)
        .build();
    String observed = SaveProofJsonDelegate.inferBlockerObserved(snap, false);
    assertTrue(observed, observed.contains("JFileChooser"));
    assertTrue(observed, observed.contains("not observed") || observed.contains("No live"));
  }

  @Test
  public void inferBlockerObservedAmbiguousChooser() {
    EvidenceJsonWriter.SaveProofSnapshot snap = snapshotBuilder()
        .robotFileMenuOpened(true)
        .robotSaveItemClicked(true)
        .saveActionIdentityMatched(true)
        .chooserObserved(true)
        .ambiguousChooserDiscovery(true)
        .build();
    String observed = SaveProofJsonDelegate.inferBlockerObserved(snap, false);
    assertTrue(observed, observed.contains("Multiple"));
  }

  @Test
  public void inferBlockerObservedChooserControlFailed() {
    EvidenceJsonWriter.SaveProofSnapshot snap = snapshotBuilder()
        .robotFileMenuOpened(true)
        .robotSaveItemClicked(true)
        .saveActionIdentityMatched(true)
        .chooserObserved(true)
        .ambiguousChooserDiscovery(false)
        .dialogShowing(false)
        .build();
    String observed = SaveProofJsonDelegate.inferBlockerObserved(snap, false);
    assertTrue(observed, observed.contains("chooser") || observed.contains("controlled"));
  }

  @Test
  public void inferBlockerObservedTargetPathRejected() {
    EvidenceJsonWriter.SaveProofSnapshot snap = snapshotBuilder()
        .robotFileMenuOpened(true)
        .robotSaveItemClicked(true)
        .saveActionIdentityMatched(true)
        .chooserObserved(true)
        .ambiguousChooserDiscovery(false)
        .dialogShowing(true)
        .selectedFileVerified(true)
        .approvedSelection(true)
        .targetInsideProofRoot(false)
        .targetFileName(TARGET_FILE_NAME)
        .build();
    String observed = SaveProofJsonDelegate.inferBlockerObserved(snap, false);
    assertTrue(observed, observed.contains("outside") || observed.contains("proof root"));
  }

  @Test
  public void inferBlockerObservedWriteNotObserved() {
    EvidenceJsonWriter.SaveProofSnapshot snap = snapshotBuilder()
        .robotFileMenuOpened(true)
        .robotSaveItemClicked(true)
        .saveActionIdentityMatched(true)
        .chooserObserved(true)
        .ambiguousChooserDiscovery(false)
        .dialogShowing(true)
        .selectedFileVerified(true)
        .approvedSelection(true)
        .targetInsideProofRoot(true)
        .targetFileName(TARGET_FILE_NAME)
        .build();
    String observed = SaveProofJsonDelegate.inferBlockerObserved(snap, false);
    assertTrue(observed, observed.contains("write") || observed.contains(".a3p"));
  }

  @Test
  public void inferBlockerObservedReadbackFailed() {
    EvidenceJsonWriter.SaveProofSnapshot snap = snapshotBuilder()
        .robotFileMenuOpened(true)
        .robotSaveItemClicked(true)
        .saveActionIdentityMatched(true)
        .chooserObserved(true)
        .ambiguousChooserDiscovery(false)
        .dialogShowing(true)
        .selectedFileVerified(true)
        .approvedSelection(true)
        .targetInsideProofRoot(true)
        .targetFileName(TARGET_FILE_NAME)
        .projectReadable(false)
        .build();
    String observed = SaveProofJsonDelegate.inferBlockerObserved(snap, true);
    assertTrue(observed, observed.contains("read back"));
  }

  @Test
  public void inferBlockerObservedMarkerMissing() {
    EvidenceJsonWriter.SaveProofSnapshot snap = snapshotBuilder()
        .robotFileMenuOpened(true)
        .robotSaveItemClicked(true)
        .saveActionIdentityMatched(true)
        .chooserObserved(true)
        .ambiguousChooserDiscovery(false)
        .dialogShowing(true)
        .selectedFileVerified(true)
        .approvedSelection(true)
        .targetInsideProofRoot(true)
        .targetFileName(TARGET_FILE_NAME)
        .projectReadable(true)
        .build();
    String observed = SaveProofJsonDelegate.inferBlockerObserved(snap, true);
    assertTrue(observed, observed.contains(SaveOperationCompletionEvidence.SAVE_PROOF_MARKER));
  }

  // ── inferBlockerKind / inferBlockerObserved stay in sync ──────────

  @Test
  public void inferBlockerKindAndObservedAgreeOnFirstBlocker() {
    // When multiple conditions fail, both methods should agree
    // on which blocker is first (file menu)
    EvidenceJsonWriter.SaveProofSnapshot snap = snapshotBuilder()
        .robotFileMenuOpened(false)
        .robotSaveItemClicked(false)
        .chooserObserved(false)
        .build();
    assertEquals("file_menu_not_showing",
        SaveProofJsonDelegate.inferBlockerKind(snap, false));
    String observed = SaveProofJsonDelegate.inferBlockerObserved(snap, false);
    assertTrue(observed, observed.contains("File menu"));
  }

  // ── Integration: saveProofJson still works after extraction ───────

  @Test
  public void saveProofJsonFacadeProducesValidJsonForProvenSnapshot() {
    // This test verifies the EvidenceJsonWriter.saveProofJson() facade
    // still produces correct output after delegating to SaveProofJsonDelegate.
    // It will pass both before and after the extraction.
    EvidenceJsonWriter.SaveProofSnapshot snap = provenSnapshot();
    String json = EvidenceJsonWriter.saveProofJson(snap);
    assertNotNull(json);
    assertTrue(json, json.trim().startsWith("{"));
    assertTrue(json, json.trim().endsWith("}"));
    assertTrue(json, json.contains("\"schemaVersion\":"));
    assertTrue(json, json.contains("\"menu\":"));
    assertTrue(json, json.contains("\"dialog\":"));
    assertTrue(json, json.contains("\"control\":"));
    assertTrue(json, json.contains("\"write\":"));
    assertTrue(json, json.contains("\"readback\":"));
    assertTrue(json, json.contains("\"baselinePreserved\":"));
    assertTrue(json, json.contains("\"requiresNextEvidence\":"));
    assertTrue(json, json.contains("\"doesNotClaim\":"));
  }

  @Test
  public void saveProofJsonFacadeReportsBlockedWithInferredBlocker() {
    EvidenceJsonWriter.SaveProofSnapshot snap = blockedSnapshot();
    String json = EvidenceJsonWriter.saveProofJson(snap);
    assertTrue(json, json.contains("\"status\": \"blocked\""));
    assertTrue(json, json.contains("\"blocker\": {"));
    assertTrue(json, json.contains("\"kind\": \"file_menu_not_showing\""));
  }

  @Test
  public void saveProofJsonFacadeReportsBlockedWithExplicitBlocker() {
    EvidenceJsonWriter.SaveProofSnapshot snap = snapshotBuilder()
        .blockerKind("custom_blocker")
        .blockerObserved("Custom observed")
        .blockerRequired("Custom required")
        .build();
    String json = EvidenceJsonWriter.saveProofJson(snap);
    assertTrue(json, json.contains("\"status\": \"blocked\""));
    assertTrue(json, json.contains("\"kind\": \"custom_blocker\""));
    assertTrue(json, json.contains("\"observed\": \"Custom observed\""));
  }

  // ── helpers ───────────────────────────────────────────────────────

  private static EvidenceJsonWriter.SaveProofSnapshot provenSnapshot() {
    return new EvidenceJsonWriter.SaveProofSnapshot(
        true,   // robotFileMenuOpened
        true,   // robotSaveItemClicked
        true,   // saveActionIdentityMatched
        true,   // chooserObserved
        true,   // approvedSelection
        false,  // ambiguousChooserDiscovery
        true,   // selectedFileVerified
        true,   // targetInsideProofRoot
        true,   // dialogShowing
        "javax.swing.JFileChooser",  // dialogClass
        TARGET_CANONICAL,            // normalizedSelectedFile
        3,      // pollCount
        true,   // projectReadable
        true,   // markerPresent
        null,   // blockerKind
        null,   // blockerObserved
        null,   // blockerRequired
        TARGET_CANONICAL,  // targetCanonicalPath
        TARGET_PATH,       // targetPath
        TARGET_FILE_NAME,  // targetFileName
        PROOF_ROOT,        // proofRoot
        SCENARIO,          // scenario
        RUN_ID             // runId
    );
  }

  private static EvidenceJsonWriter.SaveProofSnapshot blockedSnapshot() {
    return snapshotBuilder()
        .robotFileMenuOpened(false)
        .build();
  }

  /**
   * Mutable builder for SaveProofSnapshot to reduce test boilerplate.
   * Defaults to a "mostly blocked" snapshot with safe defaults.
   */
  private static SnapshotBuilder snapshotBuilder() {
    return new SnapshotBuilder();
  }

  private static class SnapshotBuilder {
    boolean robotFileMenuOpened = false;
    boolean robotSaveItemClicked = false;
    boolean saveActionIdentityMatched = false;
    boolean chooserObserved = false;
    boolean approvedSelection = false;
    boolean ambiguousChooserDiscovery = false;
    boolean selectedFileVerified = false;
    boolean targetInsideProofRoot = false;
    boolean dialogShowing = false;
    String dialogClass = null;
    String normalizedSelectedFile = TARGET_CANONICAL;
    int pollCount = 1;
    boolean projectReadable = false;
    boolean markerPresent = false;
    String blockerKind = null;
    String blockerObserved = null;
    String blockerRequired = null;
    String targetCanonicalPath = TARGET_CANONICAL;
    Path targetPath = TARGET_PATH;
    String targetFileName = TARGET_FILE_NAME;
    Path proofRoot = PROOF_ROOT;
    String scenario = SCENARIO;
    String runId = RUN_ID;

    SnapshotBuilder robotFileMenuOpened(boolean v) { this.robotFileMenuOpened = v; return this; }
    SnapshotBuilder robotSaveItemClicked(boolean v) { this.robotSaveItemClicked = v; return this; }
    SnapshotBuilder saveActionIdentityMatched(boolean v) { this.saveActionIdentityMatched = v; return this; }
    SnapshotBuilder chooserObserved(boolean v) { this.chooserObserved = v; return this; }
    SnapshotBuilder approvedSelection(boolean v) { this.approvedSelection = v; return this; }
    SnapshotBuilder ambiguousChooserDiscovery(boolean v) { this.ambiguousChooserDiscovery = v; return this; }
    SnapshotBuilder selectedFileVerified(boolean v) { this.selectedFileVerified = v; return this; }
    SnapshotBuilder targetInsideProofRoot(boolean v) { this.targetInsideProofRoot = v; return this; }
    SnapshotBuilder dialogShowing(boolean v) { this.dialogShowing = v; return this; }
    SnapshotBuilder dialogClass(String v) { this.dialogClass = v; return this; }
    SnapshotBuilder normalizedSelectedFile(String v) { this.normalizedSelectedFile = v; return this; }
    SnapshotBuilder pollCount(int v) { this.pollCount = v; return this; }
    SnapshotBuilder projectReadable(boolean v) { this.projectReadable = v; return this; }
    SnapshotBuilder markerPresent(boolean v) { this.markerPresent = v; return this; }
    SnapshotBuilder blockerKind(String v) { this.blockerKind = v; return this; }
    SnapshotBuilder blockerObserved(String v) { this.blockerObserved = v; return this; }
    SnapshotBuilder blockerRequired(String v) { this.blockerRequired = v; return this; }
    SnapshotBuilder targetCanonicalPath(String v) { this.targetCanonicalPath = v; return this; }
    SnapshotBuilder targetPath(Path v) { this.targetPath = v; return this; }
    SnapshotBuilder targetFileName(String v) { this.targetFileName = v; return this; }
    SnapshotBuilder proofRoot(Path v) { this.proofRoot = v; return this; }
    SnapshotBuilder scenario(String v) { this.scenario = v; return this; }
    SnapshotBuilder runId(String v) { this.runId = v; return this; }

    EvidenceJsonWriter.SaveProofSnapshot build() {
      return new EvidenceJsonWriter.SaveProofSnapshot(
          robotFileMenuOpened, robotSaveItemClicked, saveActionIdentityMatched,
          chooserObserved, approvedSelection, ambiguousChooserDiscovery,
          selectedFileVerified, targetInsideProofRoot, dialogShowing,
          dialogClass, normalizedSelectedFile, pollCount,
          projectReadable, markerPresent,
          blockerKind, blockerObserved, blockerRequired,
          targetCanonicalPath, targetPath, targetFileName,
          proofRoot, scenario, runId);
    }
  }
}
