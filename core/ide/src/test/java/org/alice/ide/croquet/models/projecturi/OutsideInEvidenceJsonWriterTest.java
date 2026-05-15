package org.alice.ide.croquet.models.projecturi;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.Assert.*;

/**
 * Outside-in characterization tests that verify saveProofJson() produces
 * structurally valid JSON end-to-end through the refactored delegate.
 * These tests call the top-level public API, not individual delegate methods.
 */
public class OutsideInEvidenceJsonWriterTest {

  @Rule
  public TemporaryFolder tempDir = new TemporaryFolder();

  // ── Scenario 1: Proven save path (happy path) ─────────────────────

  @Test
  public void saveProofJson_provenPath_producesValidJsonWithAllSections() throws Exception {
    // Arrange: create a real .a3p file (zip with marker entry) in temp dir
    File proofRoot = tempDir.newFolder("proof-root");
    File a3pFile = new File(proofRoot, "classroom.a3p");
    createMinimalA3pFile(a3pFile);

    EvidenceJsonWriter.SaveProofSnapshot snap = new EvidenceJsonWriter.SaveProofSnapshot(
        true, true, true, true, true, false, true, true, true,
        "javax.swing.JFileChooser",
        a3pFile.getCanonicalPath(),
        3,
        true, true,
        null, null, null,
        a3pFile.getCanonicalPath(),
        a3pFile.toPath(),
        "classroom.a3p",
        proofRoot.toPath(),
        "alice-desktop-save-menu-dialog-write-proof",
        "outside-in-run-001");

    // Act: call the top-level method that delegates to SaveProofJsonDelegate
    String json = EvidenceJsonWriter.saveProofJson(snap);

    // Assert: all sections present with correct structure
    assertNotNull("JSON must not be null", json);
    assertTrue("Must start with {", json.startsWith("{"));
    assertTrue("Must end with }", json.trim().endsWith("}"));

    // Verify status=proven
    assertTrue("Must contain status:proven", json.contains("\"status\": \"proven\""));
    assertTrue("Must contain blocker:null", json.contains("\"blocker\": null"));

    // Verify all 8 JSON sections from delegate are present
    assertTrue("Must have schemaVersion", json.contains("\"schemaVersion\""));
    assertTrue("Must have scenario", json.contains("\"scenario\""));
    assertTrue("Must have menu section", json.contains("\"menu\""));
    assertTrue("Must have dialog section", json.contains("\"dialog\""));
    assertTrue("Must have control section", json.contains("\"control\""));
    assertTrue("Must have write section", json.contains("\"write\""));
    assertTrue("Must have readback section", json.contains("\"readback\""));
    assertTrue("Must have baselinePreserved", json.contains("\"baselinePreserved\""));
    assertTrue("Must have requiresNextEvidence", json.contains("\"requiresNextEvidence\""));
    assertTrue("Must have doesNotClaim", json.contains("\"doesNotClaim\""));

    // Verify proven claim text
    assertTrue("Must contain claim (proven path)",
        json.contains("\"claim\":"));
    assertFalse("Must NOT contain reportingSummary (proven path)",
        json.contains("\"reportingSummary\""));

    // Verify menu values
    assertTrue("fileMenuOpened=true", json.contains("\"fileMenuOpened\": true"));
    assertTrue("saveMenuItemInvoked=true", json.contains("\"saveMenuItemInvoked\": true"));

    // Verify write values
    assertTrue("fileWritten=true", json.contains("\"fileWritten\": true"));
    assertTrue("fileNonempty=true", json.contains("\"fileNonempty\": true"));

    // Verify readback values
    assertTrue("projectReadable=true", json.contains("\"projectReadable\": true"));
    assertTrue("markerPresent=true", json.contains("\"markerPresent\": true"));

    System.out.println("SCENARIO 1 PASS: Proven save path produces valid JSON with all sections");
  }

  // ── Scenario 2: Blocked path with auto-inferred blocker ───────────

  @Test
  public void saveProofJson_blockedPath_infersBlockerAndProducesAllSections() throws Exception {
    // Arrange: file menu not opened — first gate in blocker chain
    File proofRoot = tempDir.newFolder("proof-root-blocked");
    File a3pFile = new File(proofRoot, "blocked.a3p");
    // Don't create the file — simulates write failure

    EvidenceJsonWriter.SaveProofSnapshot snap = new EvidenceJsonWriter.SaveProofSnapshot(
        false,  // robotFileMenuOpened=false — triggers blocker inference
        false, false, false, false, false, false, false,
        false, null, null, 0, false, false,
        null, null, null,  // blockerKind/Observed/Required all null — force inference
        a3pFile.getCanonicalPath(),
        a3pFile.toPath(),
        "blocked.a3p",
        proofRoot.toPath(),
        "blocked-scenario",
        "outside-in-run-002");

    // Act
    String json = EvidenceJsonWriter.saveProofJson(snap);

    // Assert: blocked status with inferred blocker
    assertNotNull("JSON must not be null", json);
    assertTrue("Must start with {", json.startsWith("{"));
    assertTrue("Must end with }", json.trim().endsWith("}"));

    // Status must be blocked
    assertTrue("Must contain status:blocked", json.contains("\"status\": \"blocked\""));

    // Blocker must be auto-inferred as file_menu_not_showing
    assertTrue("Blocker kind must be inferred",
        json.contains("\"kind\": \"file_menu_not_showing\""));
    assertTrue("Blocker observed must be inferred",
        json.contains("\"observed\": \"The rendered File menu was not opened by Robot\""));
    assertTrue("Blocker required must be set",
        json.contains("\"required\":"));

    // Must have reportingSummary (blocked path), not claim
    assertTrue("Must have reportingSummary (blocked path)",
        json.contains("\"reportingSummary\""));
    assertFalse("Must NOT have claim (blocked path)",
        json.contains("\"claim\":"));

    // All sections must still be present
    assertTrue("Must have menu section", json.contains("\"menu\""));
    assertTrue("Must have dialog section", json.contains("\"dialog\""));
    assertTrue("Must have control section", json.contains("\"control\""));
    assertTrue("Must have write section", json.contains("\"write\""));
    assertTrue("Must have readback section", json.contains("\"readback\""));

    // Verify blocked values flow through
    assertTrue("fileMenuOpened=false", json.contains("\"fileMenuOpened\": false"));
    assertTrue("fileWritten=false", json.contains("\"fileWritten\": false"));

    System.out.println("SCENARIO 2 PASS: Blocked path with auto-inferred blocker produces valid JSON");
  }

  // ── Scenario 3: Edge case — special characters in JSON escaping ───

  @Test
  public void saveProofJson_specialCharsInScenario_areEscaped() throws Exception {
    File proofRoot = tempDir.newFolder("proof-root-escape");
    File a3pFile = new File(proofRoot, "test.a3p");
    createMinimalA3pFile(a3pFile);

    String scenarioWithSpecialChars = "test \"scenario\" with\nnewline and\\backslash";
    EvidenceJsonWriter.SaveProofSnapshot snap = new EvidenceJsonWriter.SaveProofSnapshot(
        true, true, true, true, true, false, true, true, true,
        "javax.swing.JFileChooser",
        a3pFile.getCanonicalPath(),
        1,
        true, true,
        null, null, null,
        a3pFile.getCanonicalPath(),
        a3pFile.toPath(),
        "test.a3p",
        proofRoot.toPath(),
        scenarioWithSpecialChars,
        "run-escape-001");

    String json = EvidenceJsonWriter.saveProofJson(snap);

    // Verify escaping worked — raw special chars must not appear
    assertFalse("Raw newline must not appear in JSON",
        json.contains("with\nnewline"));
    assertTrue("Escaped newline must appear",
        json.contains("with\\nnewline"));
    assertTrue("Escaped backslash must appear",
        json.contains("and\\\\backslash"));
    assertTrue("Escaped quote must appear",
        json.contains("\\\"scenario\\\""));

    System.out.println("SCENARIO 3 PASS: Special characters properly escaped in JSON output");
  }

  // ── Helper ─────────────────────────────────────────────────────────

  private void createMinimalA3pFile(File file) throws Exception {
    try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file))) {
      ZipEntry entry = new ZipEntry("manifest.mf");
      zos.putNextEntry(entry);
      zos.write("Manifest-Version: 1.0\n".getBytes());
      zos.closeEntry();
    }
  }
}
