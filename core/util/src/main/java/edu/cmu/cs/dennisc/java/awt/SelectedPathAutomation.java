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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Encapsulates the selected-path automation logic for save dialogs.
 * Package-private delegate extracted from {@link FileDialogUtilities}.
 */
record SelectedPathAutomation(
    String status,
    String reason,
    String configuredPath,
    File selectedFile,
    Boolean safeUnderRequestedDirectory) {

  static SelectedPathAutomation inactive() {
    return new SelectedPathAutomation("inactive", "inactive", null, null, null);
  }

  static SelectedPathAutomation accepted(String configuredPath, File selectedFile) {
    return new SelectedPathAutomation(
        "selected_path_injected",
        "selected_path_property_accepted",
        configuredPath,
        selectedFile,
        true);
  }

  static SelectedPathAutomation unsupported(
      String reason,
      String configuredPath,
      File selectedFile,
      boolean safeUnderRequestedDirectory) {
    return new SelectedPathAutomation(
        "unsupported",
        reason,
        configuredPath,
        selectedFile,
        safeUnderRequestedDirectory);
  }

  boolean isConfigured() {
    return !"inactive".equals(this.status);
  }

  static SelectedPathAutomation resolve(File directory, String extension) {
    String configuredPath = System.getProperty(FileDialogUtilities.SAVE_DIALOG_SELECTED_PATH_PROPERTY);
    if (configuredPath == null || configuredPath.isBlank()) {
      return inactive();
    }
    if (directory == null) {
      return unsupported("missing_requested_directory", configuredPath, null, false);
    }
    if (!directory.isDirectory()) {
      return unsupported("requested_directory_not_available", configuredPath, null, false);
    }
    Path requestedDirectory;
    try {
      requestedDirectory = directory.toPath().toRealPath();
    } catch (IOException ioe) {
      return unsupported("requested_directory_not_available", configuredPath, null, false);
    }
    Path configured;
    try {
      configured = Path.of(configuredPath);
    } catch (RuntimeException ex) {
      return unsupported("selected_path_invalid", configuredPath, null, false);
    }
    if (!configured.isAbsolute()) {
      return unsupported("selected_path_not_absolute", configuredPath, null, false);
    }
    Path selectedPath = addExtensionIfMissing(configured.normalize(), extension);
    Path selectedParent = selectedPath.getParent();
    if (selectedParent == null || !Files.isDirectory(selectedParent)) {
      return unsupported("selected_parent_directory_not_available", configuredPath, null, false);
    }
    if (Files.isSymbolicLink(selectedPath)) {
      return unsupported("selected_path_is_symbolic_link", configuredPath, null, false);
    }
    Path selectedParentReal;
    try {
      selectedParentReal = selectedParent.toRealPath();
    } catch (IOException ioe) {
      return unsupported("selected_parent_directory_not_available", configuredPath, null, false);
    }
    boolean safeUnder = selectedParentReal.startsWith(requestedDirectory);
    if (!safeUnder) {
      return unsupported("selected_path_outside_requested_directory", configuredPath, null, false);
    }
    return accepted(configuredPath, selectedPath.toFile());
  }

  static Path addExtensionIfMissing(Path path, String extension) {
    if (extension == null || extension.isBlank()) {
      return path;
    }
    Path fileName = path.getFileName();
    if (fileName == null || fileName.toString().endsWith("." + extension)) {
      return path;
    }
    Path parent = path.getParent();
    Path fileNameWithExtension = Path.of(fileName + "." + extension);
    return parent == null ? fileNameWithExtension : parent.resolve(fileNameWithExtension);
  }
}
