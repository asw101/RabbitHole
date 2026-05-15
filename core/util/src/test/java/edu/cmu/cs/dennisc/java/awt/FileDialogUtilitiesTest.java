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

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

/**
 * Characterization tests for the public API of {@link FileDialogUtilities}
 * and its package-private delegates after extraction.
 */
public class FileDialogUtilitiesTest {

  // --- escapeJson preserves original behavior ---

  @Test
  public void escapeJson_plainText_unchanged() {
    assertEquals("hello", FileDialogUtilities.escapeJson("hello"));
  }

  @Test
  public void escapeJson_backslash_escaped() {
    assertEquals("a\\\\b", FileDialogUtilities.escapeJson("a\\b"));
  }

  @Test
  public void escapeJson_quotes_escaped() {
    assertEquals("a\\\"b", FileDialogUtilities.escapeJson("a\"b"));
  }

  @Test
  public void escapeJson_controlChars_escaped() {
    assertEquals("a\\nb\\tc", FileDialogUtilities.escapeJson("a\nb\tc"));
  }

  @Test
  public void escapeJson_lowControl_unicodeEscaped() {
    // \u0001 should become \\u0001
    assertEquals("\\u0001", FileDialogUtilities.escapeJson("\u0001"));
  }

  // --- SelectedPathAutomation record factory methods ---

  @Test
  public void selectedPathAutomation_inactive_notConfigured() {
    SelectedPathAutomation spa = SelectedPathAutomation.inactive();
    assertFalse(spa.isConfigured());
    assertEquals("inactive", spa.status());
    assertEquals("inactive", spa.reason());
    assertNull(spa.selectedFile());
  }

  @Test
  public void selectedPathAutomation_accepted_isConfigured() {
    File file = new File("/some/path.a3p");
    SelectedPathAutomation spa = SelectedPathAutomation.accepted("/some/path.a3p", file);
    assertTrue(spa.isConfigured());
    assertEquals("selected_path_injected", spa.status());
    assertEquals("selected_path_property_accepted", spa.reason());
    assertEquals(file, spa.selectedFile());
    assertTrue(spa.safeUnderRequestedDirectory());
  }

  @Test
  public void selectedPathAutomation_unsupported_isConfigured() {
    SelectedPathAutomation spa = SelectedPathAutomation.unsupported("test_reason", "/cfg", null, false);
    assertTrue(spa.isConfigured());
    assertEquals("unsupported", spa.status());
    assertEquals("test_reason", spa.reason());
    assertFalse(spa.safeUnderRequestedDirectory());
  }

  // --- addExtensionIfMissing ---

  @Test
  public void addExtension_nullExtension_noChange() {
    Path p = Path.of("/dir/file");
    assertEquals(p, SelectedPathAutomation.addExtensionIfMissing(p, null));
  }

  @Test
  public void addExtension_alreadyHasExtension_noChange() {
    Path p = Path.of("/dir/file.a3p");
    assertEquals(p, SelectedPathAutomation.addExtensionIfMissing(p, "a3p"));
  }

  @Test
  public void addExtension_missingExtension_appended() {
    Path p = Path.of("/dir/file");
    assertEquals(Path.of("/dir/file.a3p"), SelectedPathAutomation.addExtensionIfMissing(p, "a3p"));
  }

  // --- SelectedPathAutomation.resolve ---

  @Test
  public void resolve_noSystemProperty_inactive() {
    String prev = System.getProperty(FileDialogUtilities.SAVE_DIALOG_SELECTED_PATH_PROPERTY);
    try {
      System.clearProperty(FileDialogUtilities.SAVE_DIALOG_SELECTED_PATH_PROPERTY);
      SelectedPathAutomation result = SelectedPathAutomation.resolve(new File("."), "a3p");
      assertFalse(result.isConfigured());
    } finally {
      restoreProperty(FileDialogUtilities.SAVE_DIALOG_SELECTED_PATH_PROPERTY, prev);
    }
  }

  @Test
  public void resolve_nullDirectory_unsupported() {
    String prev = System.getProperty(FileDialogUtilities.SAVE_DIALOG_SELECTED_PATH_PROPERTY);
    try {
      System.setProperty(FileDialogUtilities.SAVE_DIALOG_SELECTED_PATH_PROPERTY, "/some/path.a3p");
      SelectedPathAutomation result = SelectedPathAutomation.resolve(null, "a3p");
      assertTrue(result.isConfigured());
      assertEquals("missing_requested_directory", result.reason());
    } finally {
      restoreProperty(FileDialogUtilities.SAVE_DIALOG_SELECTED_PATH_PROPERTY, prev);
    }
  }

  @Test
  public void resolve_relativePath_unsupported() {
    String prev = System.getProperty(FileDialogUtilities.SAVE_DIALOG_SELECTED_PATH_PROPERTY);
    try {
      System.setProperty(FileDialogUtilities.SAVE_DIALOG_SELECTED_PATH_PROPERTY, "relative/path.a3p");
      SelectedPathAutomation result = SelectedPathAutomation.resolve(new File("."), "a3p");
      assertTrue(result.isConfigured());
      assertEquals("selected_path_not_absolute", result.reason());
    } finally {
      restoreProperty(FileDialogUtilities.SAVE_DIALOG_SELECTED_PATH_PROPERTY, prev);
    }
  }

  // --- SaveDialogDiscoveryWriter ---

  @Test
  public void discoveryWriter_escapeJson_matchesPublicApi() {
    String input = "path\\with\"special\tchars";
    assertEquals(FileDialogUtilities.escapeJson(input), SaveDialogDiscoveryWriter.escapeJson(input));
  }

  @Test
  public void discoveryWriter_artifactPath_withinDir() {
    Path dir = Path.of("/evidence");
    Path result = SaveDialogDiscoveryWriter.artifactPath(dir, "test.json");
    assertTrue(result.startsWith(dir));
  }

  @Test(expected = IllegalArgumentException.class)
  public void discoveryWriter_artifactPath_rejectsTraversal() {
    SaveDialogDiscoveryWriter.artifactPath(Path.of("/evidence"), "../escape.json");
  }

  @Test
  public void discoveryWriter_discoverReason_nullComponent() {
    assertEquals("missing_owner_component", SaveDialogDiscoveryWriter.discoverReason(null, null));
  }

  // --- writeSaveDialogDiscoveryTarget produces valid JSON ---

  @Test
  public void writeSaveDialogDiscoveryTarget_writesJson() throws IOException {
    Path evidenceDir = Files.createTempDirectory("fdu-test-");
    try {
      Path artifact = FileDialogUtilities.writeSaveDialogDiscoveryTarget(
          evidenceDir, null, new File(evidenceDir.toString()), "test", "a3p");
      assertTrue(Files.exists(artifact));
      String json = Files.readString(artifact);
      assertTrue(json.contains("\"schema_version\""));
      assertTrue(json.contains("\"status\""));
      assertTrue(json.contains("\"reason\""));
      assertTrue(json.contains("\"selected_path_automation\""));
    } finally {
      deleteRecursive(evidenceDir.toFile());
    }
  }

  // --- Public constants preserved ---

  @Test
  public void constants_preserved() {
    assertEquals("org.alice.eatme.saveDialogDiscoveryEvidenceDir",
        FileDialogUtilities.SAVE_DIALOG_DISCOVERY_EVIDENCE_DIR_PROPERTY);
    assertEquals("desktop-save-dialog-discovery-target.json",
        FileDialogUtilities.SAVE_DIALOG_DISCOVERY_TARGET_ARTIFACT);
    assertEquals("org.alice.eatme.saveDialogSelectedPath",
        FileDialogUtilities.SAVE_DIALOG_SELECTED_PATH_PROPERTY);
  }

  // --- Helpers ---

  private static void restoreProperty(String key, String previous) {
    if (previous == null) {
      System.clearProperty(key);
    } else {
      System.setProperty(key, previous);
    }
  }

  private static void deleteRecursive(File file) {
    if (file.isDirectory()) {
      File[] children = file.listFiles();
      if (children != null) {
        for (File child : children) {
          deleteRecursive(child);
        }
      }
    }
    file.delete();
  }
}
