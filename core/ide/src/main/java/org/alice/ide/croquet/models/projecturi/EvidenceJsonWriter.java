package org.alice.ide.croquet.models.projecturi;

import java.io.File;
import java.nio.file.Path;

/**
 * Pure JSON string builders extracted from {@link SaveOperationCompletionEvidence}.
 *
 * <p>Every method is a package-private static that produces a JSON fragment.
 * No file I/O is performed here — callers are responsible for writing the
 * returned strings to disk.
 */
final class EvidenceJsonWriter {
  private EvidenceJsonWriter() {
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

  // ── Core utilities ────────────────────────────────────────────────

  private static final char[] HEX = "0123456789abcdef".toCharArray();

  static String escapeJson(String value) {
    StringBuilder escaped = new StringBuilder(value.length() + 16);
    for (int i = 0; i < value.length(); i++) {
      char ch = value.charAt(i);
      switch (ch) {
        case '\\' -> escaped.append("\\\\");
        case '"' -> escaped.append("\\\"");
        case '\b' -> escaped.append("\\b");
        case '\f' -> escaped.append("\\f");
        case '\n' -> escaped.append("\\n");
        case '\r' -> escaped.append("\\r");
        case '\t' -> escaped.append("\\t");
        default -> {
          if (ch < 0x20) {
            // Manual hex: ch is 0x00–0x1f so top two digits are always 00
            escaped.append("\\u00");
            escaped.append(HEX[(ch >> 4) & 0xf]);
            escaped.append(HEX[ch & 0xf]);
          } else {
            escaped.append(ch);
          }
        }
      }
    }
    return escaped.toString();
  }

  static String stringJson(String value) {
    return value == null ? "null" : "\"" + escapeJson(value) + "\"";
  }

  static String nullToBlank(String value) {
    return value == null ? "" : value;
  }

  static String className(Object value) {
    return value == null ? null : value.getClass().getName();
  }

  static String operationSimpleName(String operationClass) {
    String value = nullToBlank(operationClass);
    int lastDot = value.lastIndexOf('.');
    return lastDot >= 0 ? value.substring(lastDot + 1) : value;
  }

  static String status(SaveOperationFlow.Result result) {
    if (result.finished()) {
      return "finished";
    }
    if (result.canceled()) {
      return "canceled";
    }
    return "incomplete";
  }

  // ── Saved-file JSON fragments ─────────────────────────────────────

  static String savedFileJson(File savedFile) {
    return savedFile == null
        ? "null"
        : "\"" + escapeJson(EvidenceFileOperations.redactedSavedFilePath(savedFile)) + "\"";
  }

  static String savedFileExistsJson(File savedFile, boolean savedFileExists) {
    return savedFile == null
        ? "null"
        : Boolean.toString(savedFileExists);
  }

  static String savedFileSizeJson(Long savedFileSizeBytes) {
    return savedFileSizeBytes == null ? "null" : savedFileSizeBytes.toString();
  }

  static boolean wroteFile(Path savedPath, EvidenceFileOperations.RegularFileState savedFileState, String extension) {
    if (!hasExtension(savedPath, extension)) {
      return false;
    }
    return savedFileState.exists() && savedFileState.nonEmpty();
  }

  static boolean hasExtension(Path savedPath, String extension) {
    if (savedPath == null) {
      return false;
    }
    if (extension == null || extension.isBlank()) {
      return true;
    }
    Path fileName = savedPath.getFileName();
    return fileName != null && fileName.toString().endsWith("." + extension);
  }

  // ── Result artifact JSON ──────────────────────────────────────────

  static String resultJson(
      String operationClass,
      String extension,
      SaveOperationFlow.Result result,
      EvidenceFileOperations.RegularFileState savedFileState) {
    File savedFile = result.savedFile();
    Path savedPath = savedFile == null ? null : savedFile.toPath();
    Long fileSizeBytes = savedFileState.exists() ? savedFileState.sizeBytes() : null;
    boolean wroteFile = wroteFile(savedPath, savedFileState, extension);
    String resultStatus = status(result);
    return "{\n"
        + "  \"schema_version\": \"eatme.alice-desktop-save-operation-result/v1\",\n"
        + "  \"status\": \"" + resultStatus + "\",\n"
        + "  \"source\": \"AbstractSaveOperation.perform\",\n"
        + "  \"operation\": \"" + escapeJson(nullToBlank(operationClass)) + "\",\n"
        + "  \"extension\": \"" + escapeJson(nullToBlank(extension)) + "\",\n"
        + "  \"finished\": " + result.finished() + ",\n"
        + "  \"canceled\": " + result.canceled() + ",\n"
        + "  \"prompt_count\": " + result.promptCount() + ",\n"
        + "  \"save_attempts\": " + result.saveAttempts() + ",\n"
        + "  \"saved_file\": " + savedFileJson(savedFile) + ",\n"
        + "  \"saved_file_exists\": " + savedFileExistsJson(savedFile, savedFileState.exists()) + ",\n"
        + "  \"saved_file_size_bytes\": " + savedFileSizeJson(fileSizeBytes) + ",\n"
        + "  \"dialogType\": \"Swing JFileChooser\",\n"
        + "  \"evidencePath\": \"Save dialog control/write path\",\n"
        + "  \"wroteFile\": " + wroteFile + ",\n"
        + "  \"fileExtension\": \"" + escapeJson(nullToBlank(extension)) + "\",\n"
        + resultClaimOrSummaryJson(wroteFile, resultStatus, extension)
        + "  \"doesNotClaim\": [\n"
        + "    \"desktop Save menu item was clicked\",\n"
        + "    \"full lesson completion\",\n"
        + "    \"full Alice UI automation\",\n"
        + "    \"first-lesson completion\",\n"
        + "    \"visible rendering correctness\",\n"
        + "    \"grading correctness\",\n"
        + "    \"broad UI automation coverage\",\n"
        + "    \"native dialog coverage\"\n"
        + "  ]\n"
        + "}\n";
  }

  static String resultClaimOrSummaryJson(boolean wroteFile, String resultStatus, String extension) {
    String fileWrite = nonEmptyProjectFileWrite(extension);
    if (wroteFile) {
      return "  \"claim\": \"" + escapeJson("Save control/dialog approval reached " + fileWrite) + "\",\n";
    }
    return "  \"reporting_summary\": \""
        + escapeJson("Save operation evidence recorded status " + resultStatus + " without proving " + fileWrite)
        + "\",\n";
  }

  static String nonEmptyProjectFileWrite(String extension) {
    if (extension == null || extension.isBlank()) {
      return "a non-empty project file write";
    }
    return "a non-empty ." + extension + " project file write";
  }

  // ── Dialog control target JSON ────────────────────────────────────

  static String dialogControlTargetJson(String operationClass, String extension, SaveOperationFlow.Result result) {
    boolean dialogWasRequested = result.promptCount() > 0;
    String status = dialogWasRequested ? "blocked" : "unsupported";
    String reason = dialogWasRequested
        ? "desktop_save_dialog_control_not_available"
        : "save_operation_did_not_request_dialog";
    String summary = dialogWasRequested
        ? "SaveOperationFlow requested the production Save dialog seam, but no desktop dialog discovery/control evidence exists yet."
        : "No Save dialog was requested, so this artifact cannot prove dialog discovery or control.";
    return "{\n"
        + "  \"schema_version\": \"eatme.alice-desktop-save-dialog-control-target/v1\",\n"
        + "  \"status\": \"" + status + "\",\n"
        + "  \"reason\": \"" + reason + "\",\n"
        + "  \"source\": \"AbstractSaveOperation.perform\",\n"
        + "  \"operation\": \"" + escapeJson(nullToBlank(operationClass)) + "\",\n"
        + "  \"extension\": \"" + escapeJson(nullToBlank(extension)) + "\",\n"
        + "  \"result_status\": \"" + status(result) + "\",\n"
        + "  \"prompt_count\": " + result.promptCount() + ",\n"
        + "  \"save_attempts\": " + result.saveAttempts() + ",\n"
        + "  \"saved_file\": " + savedFileJson(result.savedFile()) + ",\n"
        + "  \"dialog_targets\": {\n"
        + "    \"desktop_frame\": \"org.lgna.croquet.DocumentFrame#showSaveFileDialog(File,String,String)\",\n"
        + "    \"swing_file_chooser\": \"edu.cmu.cs.dennisc.java.awt.FileDialogUtilities#showSaveFileDialog(Component,File,String,String)\"\n"
        + "  },\n"
        + "  \"reporting_summary\": \"" + escapeJson(summary) + "\",\n"
        + "  \"missing_evidence\": [\n"
        + "    \"desktop Save dialog discovery\",\n"
        + "    \"desktop Save dialog control\",\n"
        + "    \"selected Save path supplied by UI automation\"\n"
        + "  ],\n"
        + "  \"requiresNextEvidence\": [\n"
        + "    \"desktop Save dialog owner/component artifact\",\n"
        + "    \"desktop Save dialog control result artifact\"\n"
        + "  ],\n"
        + "  \"doesNotClaim\": [\n"
        + "    \"desktop Save menu item was clicked\",\n"
        + "    \"desktop Save dialog control\",\n"
        + "    \"full Alice UI automation\",\n"
        + "    \"first-lesson completion\",\n"
        + "    \"visible rendering correctness\",\n"
        + "    \"grading\"\n"
        + "  ]\n"
        + "}\n";
  }

  // ── Save action invocation proof JSON ─────────────────────────────

  static String saveActionInvocationProofJson(
      String operationClass,
      String extension,
      boolean activeStageIdeAvailable,
      boolean projectDocumentFrameAvailable,
      SaveOperationCompletionEvidence.InvocationTrigger invocationTrigger) {
    SaveOperationCompletionEvidence.InvocationTrigger trigger =
        invocationTrigger == null ? SaveOperationCompletionEvidence.InvocationTrigger.none() : invocationTrigger;
    String reason = saveActionInvocationReason(activeStageIdeAvailable, projectDocumentFrameAvailable, trigger);
    String status = switch (reason) {
      case "save_action_invoked" -> "action_invoked";
      case "save_menu_item_dispatched" -> "menu_item_dispatched";
      case "missing_active_stage_ide" -> "unsupported";
      default -> "blocked";
    };
    String operationSimpleName = operationSimpleName(operationClass);
    return "{\n"
        + "  \"schema_version\": \"eatme.alice-desktop-save-action-invocation-proof/v1\",\n"
        + "  \"status\": \"" + status + "\",\n"
        + "  \"reason\": \"" + reason + "\",\n"
        + "  \"source\": \"AbstractSaveOperation.perform\",\n"
        + "  \"operation\": \"" + escapeJson(nullToBlank(operationClass)) + "\",\n"
        + "  \"extension\": \"" + escapeJson(nullToBlank(extension)) + "\",\n"
        + "  \"target\": {\n"
        + "    \"action\": \"" + escapeJson(operationSimpleName) + ".getInstance().fire(UserActivity)\",\n"
        + "    \"menu_item\": \"" + escapeJson(operationSimpleName) + ".getInstance().getMenuItemPrepModel()\",\n"
        + "    \"dialog_path\": \"application.getDocumentFrame().showSaveFileDialog(directory, filename, extension)\",\n"
        + "    \"required_active_application\": \"org.alice.stageide.StageIDE.getActiveInstance()\"\n"
        + "  },\n"
        + "  \"observed\": {\n"
        + "    \"active_stage_ide_available\": " + activeStageIdeAvailable + ",\n"
        + "    \"project_document_frame_available\": " + projectDocumentFrameAvailable + ",\n"
        + "    \"menu_item_dispatch\": " + trigger.isMenuItemDispatch() + ",\n"
        + "    \"trigger_class\": " + stringJson(trigger.triggerClass()) + ",\n"
        + "    \"view_controller_class\": " + stringJson(trigger.viewControllerClass()) + ",\n"
        + "    \"awt_source_class\": " + stringJson(trigger.awtSourceClass()) + "\n"
        + "  },\n"
        + "  \"blocker\": {\n"
        + "    \"observed\": \"" + escapeJson(saveActionObserved(reason)) + "\",\n"
        + "    \"required\": \"active StageIDE with ProjectDocumentFrame before AbstractSaveOperation can request the production Save dialog\"\n"
        + "  },\n"
        + "  \"reporting_summary\": \"" + escapeJson(saveActionReportingSummary(reason)) + "\",\n"
        + saveActionRequiresNextEvidenceJson(reason)
        + saveActionDoesNotClaimJson(trigger)
        + "}\n";
  }

  static String saveActionInvocationReason(
      boolean activeStageIdeAvailable,
      boolean projectDocumentFrameAvailable,
      SaveOperationCompletionEvidence.InvocationTrigger invocationTrigger) {
    if (!activeStageIdeAvailable) {
      return "missing_active_stage_ide";
    }
    if (!projectDocumentFrameAvailable) {
      return "missing_project_document_frame";
    }
    if (invocationTrigger.isMenuItemDispatch()) {
      return "save_menu_item_dispatched";
    }
    return "save_action_invoked";
  }

  // ── Save proof JSON ───────────────────────────────────────────────

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
      blockerKind = SaveProofJsonDelegate.inferBlockerKind(snap, observedWrite);
      blockerObserved = SaveProofJsonDelegate.inferBlockerObserved(snap, observedWrite);
      blockerRequired =
          "A complete Robot File menu Save activation, rendered dialog approval, write, readback, and marker path";
    }
    String status = proven ? "proven" : "blocked";
    return "{\n"
        + SaveProofJsonDelegate.headerJson(status, proven, snap)
        + SaveProofJsonDelegate.blockerJson(proven, blockerKind, blockerObserved, blockerRequired)
        + SaveProofJsonDelegate.menuJson(snap)
        + SaveProofJsonDelegate.dialogJson(snap)
        + SaveProofJsonDelegate.controlJson(snap, selectedPath, selectedFileMatchesExpected)
        + SaveProofJsonDelegate.writeJson(fileExists, fileNonempty, fileHasExpectedExtension, fileSizeBytes, snap)
        + SaveProofJsonDelegate.readbackJson(snap)
        + SaveProofJsonDelegate.baselinePreservedJson()
        + SaveProofJsonDelegate.requiresNextEvidenceJson()
        + SaveProofJsonDelegate.doesNotClaimJson()
        + "}\n";
  }

  // ── Private helpers ───────────────────────────────────────────────

  private static String saveActionObserved(String reason) {
    return switch (reason) {
      case "missing_active_stage_ide" -> "SaveProjectOperation.fire(UserActivity) reached AbstractSaveOperation.perform, but StageIDE.getActiveInstance() returned null.";
      case "missing_project_document_frame" -> "StageIDE.getActiveInstance() resolved, but application.getDocumentFrame() returned null.";
      case "save_menu_item_dispatched" -> "SaveProjectOperation Swing menu item doClick dispatched through OperationSwingModel and reached AbstractSaveOperation.perform with an active StageIDE and ProjectDocumentFrame.";
      default -> "SaveProjectOperation.fire(UserActivity) reached AbstractSaveOperation.perform with an active StageIDE and ProjectDocumentFrame.";
    };
  }

  private static String saveActionReportingSummary(String reason) {
    return switch (reason) {
      case "missing_active_stage_ide" -> "The Save action invocation path is executable, but this JVM has no active StageIDE, so the production Save dialog path cannot resolve application.getDocumentFrame().showSaveFileDialog.";
      case "missing_project_document_frame" -> "The Save action invocation path found an active StageIDE, but no ProjectDocumentFrame was available to own the Save dialog.";
      case "save_menu_item_dispatched" -> "The desktop Save menu item dispatch path reached the production Save operation owner; dialog display/control still require FileDialogUtilities evidence.";
      default -> "The Save action invocation reached the production Save operation owner; dialog display/control still require FileDialogUtilities evidence.";
    };
  }

  private static String saveActionRequiresNextEvidenceJson(String reason) {
    if ("save_action_invoked".equals(reason) || "save_menu_item_dispatched".equals(reason)) {
      return "  \"requiresNextEvidence\": [\n"
          + "    \"desktop Save dialog discovery artifact with target_resolved\",\n"
          + "    \"desktop Save dialog control result artifact\",\n"
          + "    \"selected Save path supplied by UI automation\"\n"
          + "  ],\n";
    }
    if ("missing_project_document_frame".equals(reason)) {
      return "  \"requiresNextEvidence\": [\n"
          + "    \"invoke SaveProjectOperation from an initialized Alice desktop with a ProjectDocumentFrame\",\n"
          + "    \"desktop Save dialog discovery artifact with target_resolved\",\n"
          + "    \"desktop Save dialog control result artifact\",\n"
          + "    \"selected Save path supplied by UI automation\"\n"
          + "  ],\n";
    }
    return "  \"requiresNextEvidence\": [\n"
        + "    \"invoke SaveProjectOperation from a running Alice desktop with StageIDE.getActiveInstance() resolved\",\n"
        + "    \"desktop Save dialog discovery artifact with target_resolved\",\n"
        + "    \"desktop Save dialog control result artifact\",\n"
        + "    \"selected Save path supplied by UI automation\"\n"
        + "  ],\n";
  }

  private static String saveActionDoesNotClaimJson(SaveOperationCompletionEvidence.InvocationTrigger invocationTrigger) {
    String menuItemClaim = invocationTrigger.isMenuItemDispatch()
        ? ""
        : "    \"desktop Save menu item was clicked\",\n";
    return "  \"doesNotClaim\": [\n"
        + menuItemClaim
        + "    \"Save dialog displayed\",\n"
        + "    \"desktop Save dialog control\",\n"
        + "    \"selected Save path supplied by UI automation\",\n"
        + "    \"saved file completed\",\n"
        + "    \"first-lesson completion\",\n"
        + "    \"visible rendering correctness\",\n"
        + "    \"grading\"\n"
        + "  ]\n";
  }

}
