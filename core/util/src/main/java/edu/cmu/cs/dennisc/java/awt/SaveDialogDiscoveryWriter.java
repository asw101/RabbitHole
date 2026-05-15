/*******************************************************************************
 * Copyright (c) 2006, 2015, Carnegie Mellon University. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Products derived from the software may not be called "Alice", nor may
 *    "Alice" appear in their name, without prior written permission of
 *    Carnegie Mellon University.
 *
 * 4. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgement: "This product includes software
 *    developed by Carnegie Mellon University"
 *
 * 5. The gallery of art assets and animations provided with this software is
 *    contributed by Electronic Arts Inc. and may be used for personal,
 *    non-commercial, and academic use only. Redistributions of any program
 *    source code that utilizes The Sims 2 Assets must also retain the copyright
 *    notice, list of conditions and the disclaimer contained in
 *    The Alice 3.0 Art Gallery License.
 *
 * DISCLAIMER:
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.
 * ANY AND ALL EXPRESS, STATUTORY OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,  FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, AND NON-INFRINGEMENT ARE DISCLAIMED. IN NO EVENT
 * SHALL THE AUTHORS, COPYRIGHT OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, PUNITIVE OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING FROM OR OTHERWISE RELATING TO
 * THE USE OF OR OTHER DEALINGS WITH THE SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
package edu.cmu.cs.dennisc.java.awt;

import edu.cmu.cs.dennisc.java.lang.SystemUtilities;
import edu.cmu.cs.dennisc.java.util.logging.Logger;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Handles writing save-dialog discovery-target evidence JSON.
 * Package-private delegate extracted from {@link FileDialogUtilities}.
 */
final class SaveDialogDiscoveryWriter {

  private SaveDialogDiscoveryWriter() {
  }

  static void record(
      Component component, File directory, String filename, String extension,
      Component root, SelectedPathAutomation automation) {
    String evidenceDir = System.getProperty(FileDialogUtilities.SAVE_DIALOG_DISCOVERY_EVIDENCE_DIR_PROPERTY);
    if (evidenceDir == null || evidenceDir.isBlank()) {
      return;
    }
    try {
      write(Path.of(evidenceDir), component, directory, filename, extension, root, automation);
    } catch (IOException | RuntimeException ex) {
      Logger.throwable(ex, "Save dialog discovery target evidence write failed: " + evidenceDir);
    }
  }

  static Path write(
      Path evidenceDir, Component component, File directory,
      String filename, String extension, Component root) throws IOException {
    return write(evidenceDir, component, directory, filename, extension, root,
        SelectedPathAutomation.resolve(directory, extension));
  }

  static Path write(
      Path evidenceDir, Component component, File directory,
      String filename, String extension, Component root,
      SelectedPathAutomation automation) throws IOException {
    Files.createDirectories(evidenceDir);
    Path artifact = artifactPath(evidenceDir, FileDialogUtilities.SAVE_DIALOG_DISCOVERY_TARGET_ARTIFACT);
    Files.writeString(artifact, toJson(component, directory, filename, extension, root, automation),
        StandardCharsets.UTF_8);
    if (!Files.isRegularFile(artifact) || Files.size(artifact) == 0) {
      throw new IOException("Save dialog discovery target artifact was not written: " + artifact);
    }
    return artifact;
  }

  // --- JSON generation ---

  private static String toJson(
      Component component, File directory, String filename, String extension,
      Component root, SelectedPathAutomation automation) {
    String reason = discoverReason(component, root);
    String status = switch (reason) {
      case "target_resolved" -> "target_resolved";
      case "missing_owner_component", "headless_graphics_environment" -> "unsupported";
      default -> "blocked";
    };
    return "{\n"
        + "  \"schema_version\": \"eatme.alice-desktop-save-dialog-discovery-target/v1\",\n"
        + "  \"status\": \"" + status + "\",\n"
        + "  \"reason\": \"" + reason + "\",\n"
        + "  \"source\": \"edu.cmu.cs.dennisc.java.awt.FileDialogUtilities#showSaveFileDialog(Component,File,String,String)\",\n"
        + "  \"dialog_targets\": {\n"
        + "    \"desktop_frame\": \"org.lgna.croquet.DocumentFrame#showSaveFileDialog(File,String,String)\",\n"
        + "    \"native_chooser\": \"edu.cmu.cs.dennisc.java.awt.FileDialogUtilities#showSaveFileDialog(Component,File,String,String)\",\n"
        + "    \"dialog_implementation\": \"" + escapeJson(dialogImplementation()) + "\"\n"
        + "  },\n"
        + "  \"request\": {\n"
        + "    \"title\": \"Save...\",\n"
        + "    \"mode\": \"SAVE\",\n"
        + "    \"directory\": " + fileJson(directory) + ",\n"
        + "    \"filename\": " + stringJson(filename) + ",\n"
        + "    \"extension\": " + stringJson(extension) + "\n"
        + "  },\n"
        + "  \"owner_window\": {\n"
        + "    \"headless\": " + GraphicsEnvironment.isHeadless() + ",\n"
        + "    \"owner_component_class\": " + componentJson(component) + ",\n"
        + "    \"owner_displayable\": " + booleanJson(component == null ? null : component.isDisplayable()) + ",\n"
        + "    \"owner_showing\": " + booleanJson(component == null ? null : component.isShowing()) + ",\n"
        + "    \"root_component_class\": " + componentJson(root) + ",\n"
        + "    \"root_displayable\": " + booleanJson(root == null ? null : root.isDisplayable()) + ",\n"
        + "    \"root_showing\": " + booleanJson(root == null ? null : root.isShowing()) + "\n"
        + "  },\n"
        + "  \"selected_path_automation\": " + automationJson(automation) + ",\n"
        + "  \"reporting_summary\": \"" + escapeJson(reportingSummary(reason)) + "\",\n"
        + "  \"blocker\": {\n"
        + "    \"observed\": \"" + escapeJson(observed(reason)) + "\",\n"
        + "    \"required\": \"" + escapeJson(blockerRequired(reason)) + "\"\n"
        + "  },\n"
        + requiresNextEvidenceJson(reason)
        + "  \"doesNotClaim\": [\n"
        + "    \"desktop Save menu item was clicked\",\n"
        + "    \"Save dialog displayed\",\n"
        + "    \"Save dialog controlled\",\n"
        + "    \"selected Save path supplied by UI automation\",\n"
        + "    \"saved file completed\",\n"
        + "    \"first-lesson completion\",\n"
        + "    \"visible rendering correctness\",\n"
        + "    \"grading\"\n"
        + "  ]\n"
        + "}\n";
  }

  static String discoverReason(Component component, Component root) {
    if (component == null) {
      return "missing_owner_component";
    }
    if (root == null) {
      return "missing_dialog_root_window";
    }
    if (GraphicsEnvironment.isHeadless()) {
      return "headless_graphics_environment";
    }
    if (!root.isDisplayable()) {
      return "dialog_root_not_displayable";
    }
    return "target_resolved";
  }

  private static String dialogImplementation() {
    return SystemUtilities.isLinux()
        ? "edu.cmu.cs.dennisc.java.awt.FileDialogUtilities.SwingFileDialog"
        : "edu.cmu.cs.dennisc.java.awt.FileDialogUtilities.AwtFileDialog";
  }

  private static String reportingSummary(String reason) {
    return switch (reason) {
      case "missing_owner_component" -> "No owner Component was supplied, so Alice cannot discover or control a Save dialog from this seam.";
      case "missing_dialog_root_window" -> "A Save dialog owner Component was supplied, but SwingUtilities.getRoot(component) did not find a desktop window.";
      case "headless_graphics_environment" -> "The JVM is headless, so Alice cannot display or control a desktop Save dialog here.";
      case "dialog_root_not_displayable" -> "The Save dialog root window exists but is not displayable yet.";
      default -> "The Save dialog owner and root target were resolved before FileDialog.show(); dialog display and control still require separate evidence.";
    };
  }

  private static String observed(String reason) {
    return switch (reason) {
      case "missing_owner_component" -> "owner Component is null";
      case "missing_dialog_root_window" -> "SwingUtilities.getRoot(component) is null";
      case "headless_graphics_environment" -> "GraphicsEnvironment.isHeadless() is true";
      case "dialog_root_not_displayable" -> "root Component exists but root.isDisplayable() is false";
      default -> "owner Component and displayable root Component resolved";
    };
  }

  private static String blockerRequired(String reason) {
    if ("target_resolved".equals(reason)) {
      return "Save dialog display/control result after FileDialog.show(), or completed saved project file evidence through the Save flow";
    }
    return "displayable Alice ProjectDocumentFrame root window before FileDialogUtilities.showSaveFileDialog displays the Save dialog";
  }

  private static String requiresNextEvidenceJson(String reason) {
    if ("target_resolved".equals(reason)) {
      return "  \"requiresNextEvidence\": [\n"
          + "    \"Save dialog displayed result from FileDialogUtilities after FileDialog.show() returns\",\n"
          + "    \"Save dialog control result artifact\",\n"
          + "    \"completed saved project file evidence through the Save flow\"\n"
          + "  ],\n";
    }
    return "  \"requiresNextEvidence\": [\n"
        + "    \"displayable Alice ProjectDocumentFrame root window at FileDialogUtilities.showSaveFileDialog\",\n"
        + "    \"Save dialog displayed result from FileDialogUtilities after FileDialog.show() returns\",\n"
        + "    \"selected Save path supplied by UI automation\"\n"
        + "  ],\n";
  }

  private static String automationJson(SelectedPathAutomation automation) {
    SelectedPathAutomation value = automation == null ? SelectedPathAutomation.inactive() : automation;
    return "{\n"
        + "    \"property\": \"" + FileDialogUtilities.SAVE_DIALOG_SELECTED_PATH_PROPERTY + "\",\n"
        + "    \"status\": \"" + value.status() + "\",\n"
        + "    \"reason\": \"" + value.reason() + "\",\n"
        + "    \"configured_path\": " + stringJson(value.configuredPath()) + ",\n"
        + "    \"selected_file\": " + fileJson(value.selectedFile()) + ",\n"
        + "    \"safe_under_requested_directory\": " + booleanJson(value.safeUnderRequestedDirectory()) + ",\n"
        + "    \"reporting_summary\": \"" + escapeJson(automationSummary(value.reason())) + "\"\n"
        + "  }";
  }

  private static String automationSummary(String reason) {
    return switch (reason) {
      case "inactive" -> "No selected Save path automation property was configured.";
      case "selected_path_property_accepted" -> "FileDialogUtilities.showSaveFileDialog returned the opt-in selected Save path without opening a desktop dialog.";
      case "missing_requested_directory" -> "Selected Save path automation requires the Save dialog request to include a directory.";
      case "requested_directory_not_available" -> "Selected Save path automation requires the requested Save directory to exist.";
      case "selected_path_invalid" -> "Configured selected Save path is not a valid local path.";
      case "selected_path_not_absolute" -> "Configured selected Save path must be absolute.";
      case "selected_parent_directory_not_available" -> "Configured selected Save path must have an existing parent directory.";
      case "selected_path_is_symbolic_link" -> "Configured selected Save path must not be a symbolic link.";
      case "selected_path_outside_requested_directory" -> "Configured selected Save path must stay under the requested directory.";
      default -> "Selected Save path automation was not accepted.";
    };
  }

  // --- Primitive JSON helpers ---

  static String escapeJson(String value) {
    StringBuilder escaped = new StringBuilder(value.length());
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
            escaped.append(String.format("\\u%04x", (int) ch));
          } else {
            escaped.append(ch);
          }
        }
      }
    }
    return escaped.toString();
  }

  static Path artifactPath(Path evidenceDir, String artifactName) {
    Path artifact = evidenceDir.resolve(artifactName).normalize();
    if (!artifact.startsWith(evidenceDir.normalize())) {
      throw new IllegalArgumentException("Save dialog discovery artifact escapes evidence dir");
    }
    return artifact;
  }

  private static String fileJson(File file) {
    return file == null ? "null" : stringJson(file.getPath());
  }

  private static String stringJson(String value) {
    return value == null ? "null" : "\"" + escapeJson(value) + "\"";
  }

  private static String componentJson(Component component) {
    return component == null ? "null" : stringJson(component.getClass().getName());
  }

  private static String booleanJson(Boolean value) {
    return value == null ? "null" : value.toString();
  }
}
