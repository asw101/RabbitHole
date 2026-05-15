package org.alice.ide.croquet.models.projecturi;

import java.nio.file.Path;
import java.time.Instant;

/**
 * Package-private delegate for save-proof JSON generation.
 *
 * <p>Extracted from {@link EvidenceJsonWriter} to reduce its line count.
 * Every method is a package-private or private static that produces a JSON
 * fragment. No file I/O is performed here.
 *
 * <p>Package-private, final, utility class — not part of the public API.
 */
final class SaveProofJsonDelegate {
  private SaveProofJsonDelegate() {
  }

  // ── Snapshot for SaveProofEvidence JSON generation ─────────────────

  record SaveProofSnapshot(
      boolean robotFileMenuOpened,
      boolean robotSaveItemClicked,
      boolean saveActionIdentityMatched,
      boolean chooserObserved,
      boolean approvedSelection,
      boolean ambiguousChooserDiscovery,
      boolean selectedFileVerified,
      boolean targetInsideProofRoot,
      boolean dialogShowing,
      String dialogClass,
      String normalizedSelectedFile,
      int pollCount,
      boolean projectReadable,
      boolean markerPresent,
      String blockerKind,
      String blockerObserved,
      String blockerRequired,
      String targetCanonicalPath,
      Path targetPath,
      String targetFileName,
      Path proofRoot,
      String scenario,
      String runId) {
  }

  // ── Entry point ──────────────────────────────────────────────────

  static String saveProofJson(SaveProofSnapshot snap) {
    Path selectedPath = snap.normalizedSelectedFile() == null
        ? null
        : Path.of(snap.normalizedSelectedFile()).normalize();
    EvidenceFileOperations.RegularFileState targetFileState =
        EvidenceFileOperations.regularFileState(snap.targetPath());
    boolean fileExists = targetFileState.exists();
    long fileSizeBytes = targetFileState.sizeBytes();
    boolean fileNonempty = targetFileState.nonEmpty();
    boolean fileHasExpectedExtension = snap.targetFileName().endsWith(".a3p");
    boolean selectedFileMatchesExpected =
        snap.normalizedSelectedFile() != null
            && snap.targetCanonicalPath().equals(snap.normalizedSelectedFile());
    boolean observedWrite =
        fileExists && fileNonempty && fileHasExpectedExtension && snap.targetInsideProofRoot();
    boolean proven = snap.robotFileMenuOpened()
        && snap.robotSaveItemClicked()
        && snap.saveActionIdentityMatched()
        && snap.chooserObserved()
        && snap.dialogShowing()
        && snap.approvedSelection()
        && !snap.ambiguousChooserDiscovery()
        && snap.selectedFileVerified()
        && selectedFileMatchesExpected
        && observedWrite
        && snap.projectReadable()
        && snap.markerPresent()
        && snap.blockerKind() == null;
    String blockerKind = snap.blockerKind();
    String blockerObserved = snap.blockerObserved();
    String blockerRequired = snap.blockerRequired();
    if (!proven && blockerKind == null) {
      blockerKind = inferBlockerKind(snap, observedWrite);
      blockerObserved = inferBlockerObserved(snap, observedWrite);
      blockerRequired =
          "A complete Robot File menu Save activation, rendered dialog approval, write, readback, and marker path";
    }
    String status = proven ? "proven" : "blocked";
    return "{\n"
        + headerJson(status, proven, snap)
        + blockerJson(proven, blockerKind, blockerObserved, blockerRequired)
        + menuJson(snap)
        + dialogJson(snap)
        + controlJson(snap, selectedPath, selectedFileMatchesExpected)
        + writeJson(fileExists, fileNonempty, fileHasExpectedExtension, fileSizeBytes, snap)
        + readbackJson(snap)
        + baselinePreservedJson()
        + requiresNextEvidenceJson()
        + doesNotClaimJson()
        + "}\n";
  }

  // ── Section builders ───────────────────────────────────────────────

  static String headerJson(String status, boolean proven, SaveProofSnapshot snap) {
    String claimOrSummary = proven
        ? "  \"claim\": \"AWT Robot opened File, clicked the production Save menu item, controlled the rendered Swing Save chooser, wrote a non-empty .a3p file, read it back, and verified " + SaveOperationCompletionEvidence.SAVE_PROOF_MARKER + "\",\n"
        : "  \"reportingSummary\": \"Robot File menu Save dialog/write/readback path was not proven; blocker.kind identifies the first missing or unsafe step.\",\n";
    return "  \"schemaVersion\": \"" + SaveOperationCompletionEvidence.SAVE_PROOF_SCHEMA_VERSION + "\",\n"
        + "  \"scenario\": \"" + EvidenceJsonWriter.escapeJson(snap.scenario()) + "\",\n"
        + "  \"workflow\": \"" + SaveOperationCompletionEvidence.SAVE_PROOF_WORKFLOW + "\",\n"
        + "  \"runId\": \"" + EvidenceJsonWriter.escapeJson(snap.runId()) + "\",\n"
        + "  \"generatedAtUtc\": \"" + Instant.now() + "\",\n"
        + "  \"status\": \"" + status + "\",\n"
        + "  \"proofTarget\": \"single rendered desktop Save path: menu, dialog, control, write, readback\",\n"
        + claimOrSummary;
  }

  static String blockerJson(
      boolean proven, String blockerKind, String blockerObserved, String blockerRequired) {
    if (proven) {
      return "  \"blocker\": null,\n";
    }
    return "  \"blocker\": {\n"
        + "    \"kind\": \"" + EvidenceJsonWriter.escapeJson(blockerKind) + "\",\n"
        + "    \"observed\": \"" + EvidenceJsonWriter.escapeJson(EvidenceJsonWriter.nullToBlank(blockerObserved)) + "\",\n"
        + "    \"required\": \"" + EvidenceJsonWriter.escapeJson(EvidenceJsonWriter.nullToBlank(blockerRequired)) + "\"\n"
        + "  },\n";
  }

  static String menuJson(SaveProofSnapshot snap) {
    return "  \"menu\": {\n"
        + "    \"fileMenuOpened\": " + snap.robotFileMenuOpened() + ",\n"
        + "    \"saveMenuItemInvoked\": " + snap.robotSaveItemClicked() + ",\n"
        + "    \"saveActionIdentityMatched\": " + snap.saveActionIdentityMatched() + "\n"
        + "  },\n";
  }

  static String dialogJson(SaveProofSnapshot snap) {
    return "  \"dialog\": {\n"
        + "    \"saveDialogObserved\": " + snap.chooserObserved() + ",\n"
        + "    \"dialogType\": \"Swing JFileChooser\",\n"
        + "    \"dialogClass\": " + EvidenceJsonWriter.stringJson(snap.dialogClass()) + ",\n"
        + "    \"dialogShowing\": " + snap.dialogShowing() + ",\n"
        + "    \"ambiguousChooserDiscovery\": " + snap.ambiguousChooserDiscovery() + ",\n"
        + "    \"pollCount\": " + snap.pollCount() + "\n"
        + "  },\n";
  }

  static String controlJson(
      SaveProofSnapshot snap, Path selectedPath, boolean selectedFileMatchesExpected) {
    return "  \"control\": {\n"
        + "    \"selectedPathSet\": " + snap.selectedFileVerified() + ",\n"
        + "    \"approvedSelection\": " + snap.approvedSelection() + ",\n"
        + "    \"selectedPathMatchesExpected\": " + selectedFileMatchesExpected + ",\n"
        + "    \"targetInsideProofRoot\": " + snap.targetInsideProofRoot() + ",\n"
        + "    \"normalizedSelectedPath\": " + EvidenceJsonWriter.stringJson(EvidenceFileOperations.proofRelativePath(selectedPath, snap.proofRoot())) + ",\n"
        + "    \"expectedPath\": " + EvidenceJsonWriter.stringJson(EvidenceFileOperations.proofRelativePath(snap.targetPath(), snap.proofRoot())) + "\n"
        + "  },\n";
  }

  static String writeJson(
      boolean fileExists,
      boolean fileNonempty,
      boolean fileHasExpectedExtension,
      long fileSizeBytes,
      SaveProofSnapshot snap) {
    return "  \"write\": {\n"
        + "    \"fileWritten\": " + fileExists + ",\n"
        + "    \"fileNonempty\": " + fileNonempty + ",\n"
        + "    \"fileHasExpectedExtension\": " + fileHasExpectedExtension + ",\n"
        + "    \"outputPath\": " + EvidenceJsonWriter.stringJson(EvidenceFileOperations.proofRelativePath(snap.targetPath(), snap.proofRoot())) + ",\n"
        + "    \"outputSizeBytes\": " + fileSizeBytes + "\n"
        + "  },\n";
  }

  static String readbackJson(SaveProofSnapshot snap) {
    return "  \"readback\": {\n"
        + "    \"projectReadable\": " + snap.projectReadable() + ",\n"
        + "    \"marker\": \"" + SaveOperationCompletionEvidence.SAVE_PROOF_MARKER + "\",\n"
        + "    \"markerPresent\": " + snap.markerPresent() + "\n"
        + "  },\n";
  }

  static String baselinePreservedJson() {
    return "  \"baselinePreserved\": [\n"
        + "    \"StageIdeSaveMenuDoClickToWriteProofTest\",\n"
        + "    \"ProjectApplicationSaveProjectToTest\",\n"
        + "    \"JMenuBarRobotClickSaveProofTest\"\n"
        + "  ],\n";
  }

  static String requiresNextEvidenceJson() {
    return "  \"requiresNextEvidence\": [\n"
        + "    \"Run under xvfb-run -a or an equivalent desktop session when blocker.kind is environment-related\",\n"
        + "    \"Use status proven only when Robot menu activation, dialog control, write, readback, and marker verification all succeed in one rendered path\"\n"
        + "  ],\n";
  }

  static String doesNotClaimJson() {
    return "  \"doesNotClaim\": [\n"
        + "    \"Save As coverage\",\n"
        + "    \"all Save variants\",\n"
        + "    \"full lesson completion\",\n"
        + "    \"visible rendering correctness\",\n"
        + "    \"grading correctness\",\n"
        + "    \"physical user click\",\n"
        + "    \"broad UI automation coverage\",\n"
        + "    \"native dialog coverage\"\n"
        + "  ]\n";
  }

  // ── Blocker inference helpers ─────────────────────────────────────

  static String inferBlockerKind(SaveProofSnapshot snap, boolean observedWrite) {
    if (!snap.robotFileMenuOpened()) {
      return "file_menu_not_showing";
    }
    if (!snap.robotSaveItemClicked() || !snap.saveActionIdentityMatched()) {
      return "save_item_not_attributed";
    }
    if (!snap.chooserObserved()) {
      return "dialog_not_observed";
    }
    if (snap.ambiguousChooserDiscovery()) {
      return "ambiguous_chooser_discovery";
    }
    if (!snap.dialogShowing() || !snap.selectedFileVerified() || !snap.approvedSelection()) {
      return "chooser_control_failed";
    }
    if (!snap.targetInsideProofRoot() || !snap.targetFileName().endsWith(".a3p")) {
      return "target_path_rejected";
    }
    if (!observedWrite) {
      return "write_not_observed";
    }
    if (!snap.projectReadable()) {
      return "readback_failed";
    }
    return "marker_missing";
  }

  static String inferBlockerObserved(SaveProofSnapshot snap, boolean observedWrite) {
    if (!snap.robotFileMenuOpened()) {
      return "The rendered File menu was not opened by Robot";
    }
    if (!snap.robotSaveItemClicked() || !snap.saveActionIdentityMatched()) {
      return "The production Save item click was not attributed to Robot";
    }
    if (!snap.chooserObserved()) {
      return "No live Swing JFileChooser was observed";
    }
    if (snap.ambiguousChooserDiscovery()) {
      return "Multiple live Swing JFileChoosers were observed";
    }
    if (!snap.dialogShowing() || !snap.selectedFileVerified() || !snap.approvedSelection()) {
      return "The live Save chooser could not be safely controlled";
    }
    if (!snap.targetInsideProofRoot() || !snap.targetFileName().endsWith(".a3p")) {
      return "The selected Save target was outside the proof root or not an .a3p file";
    }
    if (!observedWrite) {
      return "No non-empty .a3p write was observed at the controlled target";
    }
    if (!snap.projectReadable()) {
      return "The written .a3p file could not be read back as an Alice project";
    }
    return "The readback project did not contain " + SaveOperationCompletionEvidence.SAVE_PROOF_MARKER;
  }
}
