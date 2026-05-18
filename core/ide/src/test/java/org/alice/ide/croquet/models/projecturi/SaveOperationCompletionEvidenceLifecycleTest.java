package org.alice.ide.croquet.models.projecturi;

import org.junit.Assume;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * Lifecycle tests for {@link SaveOperationCompletionEvidence} inner classes:
 * {@link SaveOperationCompletionEvidence.InvocationTrigger} and
 * {@link SaveOperationCompletionEvidence.SaveProofEvidence}.
 *
 * <p>Also covers the static configuration methods:
 * {@code configuredSaveProofScenario()}, {@code configuredSaveProofRunId()},
 * {@code configuredSaveProofArtifact(Path)}, {@code isSaveActionInvocationProofOnly()},
 * and the complete write() lifecycle with atomic file creation.
 *
 * <p>JUnit 4, headless. Uses real temporary directories for filesystem tests.
 * System properties are saved/restored in try/finally to prevent leaks.
 */
public class SaveOperationCompletionEvidenceLifecycleTest {

  // ════════════════════════════════════════════════════════════════
  // InvocationTrigger tests
  // ════════════════════════════════════════════════════════════════

  @Test
  public void invocationTrigger_none_allFieldsNull() {
    SaveOperationCompletionEvidence.InvocationTrigger trigger =
        SaveOperationCompletionEvidence.InvocationTrigger.none();
    assertNull(trigger.triggerClass());
    assertNull(trigger.viewControllerClass());
    assertNull(trigger.awtSourceClass());
  }

  @Test
  public void invocationTrigger_none_isNotMenuItemDispatch() {
    SaveOperationCompletionEvidence.InvocationTrigger trigger =
        SaveOperationCompletionEvidence.InvocationTrigger.none();
    assertFalse(trigger.isMenuItemDispatch());
  }

  @Test
  public void invocationTrigger_menuItemDispatch_requiresAllThreeFields() {
    SaveOperationCompletionEvidence.InvocationTrigger trigger =
        createTrigger(
            "org.lgna.croquet.triggers.ActionEventTrigger",
            "org.lgna.croquet.views.MenuItem",
            "javax.swing.JMenuItem");
    assertTrue(trigger.isMenuItemDispatch());
  }

  @Test
  public void invocationTrigger_wrongTriggerClass_notMenuItemDispatch() {
    SaveOperationCompletionEvidence.InvocationTrigger trigger =
        createTrigger(
            "org.lgna.croquet.triggers.SomethingElse",
            "org.lgna.croquet.views.MenuItem",
            "javax.swing.JMenuItem");
    assertFalse(trigger.isMenuItemDispatch());
  }

  @Test
  public void invocationTrigger_wrongViewControllerClass_notMenuItemDispatch() {
    SaveOperationCompletionEvidence.InvocationTrigger trigger =
        createTrigger(
            "org.lgna.croquet.triggers.ActionEventTrigger",
            "org.lgna.croquet.views.Button",
            "javax.swing.JMenuItem");
    assertFalse(trigger.isMenuItemDispatch());
  }

  @Test
  public void invocationTrigger_wrongAwtSourceClass_notMenuItemDispatch() {
    SaveOperationCompletionEvidence.InvocationTrigger trigger =
        createTrigger(
            "org.lgna.croquet.triggers.ActionEventTrigger",
            "org.lgna.croquet.views.MenuItem",
            "javax.swing.JButton");
    assertFalse(trigger.isMenuItemDispatch());
  }

  @Test
  public void invocationTrigger_nullTriggerClass_notMenuItemDispatch() {
    SaveOperationCompletionEvidence.InvocationTrigger trigger =
        createTrigger(
            null,
            "org.lgna.croquet.views.MenuItem",
            "javax.swing.JMenuItem");
    assertFalse(trigger.isMenuItemDispatch());
  }

  @Test
  public void invocationTrigger_nullViewControllerClass_notMenuItemDispatch() {
    SaveOperationCompletionEvidence.InvocationTrigger trigger =
        createTrigger(
            "org.lgna.croquet.triggers.ActionEventTrigger",
            null,
            "javax.swing.JMenuItem");
    assertFalse(trigger.isMenuItemDispatch());
  }

  @Test
  public void invocationTrigger_nullAwtSourceClass_notMenuItemDispatch() {
    SaveOperationCompletionEvidence.InvocationTrigger trigger =
        createTrigger(
            "org.lgna.croquet.triggers.ActionEventTrigger",
            "org.lgna.croquet.views.MenuItem",
            null);
    assertFalse(trigger.isMenuItemDispatch());
  }

  @Test
  public void invocationTrigger_accessors_returnConstructorValues() {
    SaveOperationCompletionEvidence.InvocationTrigger trigger =
        createTrigger(
            "trigger.Class", "view.Class", "awt.Class");
    assertEquals("trigger.Class", trigger.triggerClass());
    assertEquals("view.Class", trigger.viewControllerClass());
    assertEquals("awt.Class", trigger.awtSourceClass());
  }

  // ════════════════════════════════════════════════════════════════
  // SaveProofEvidence lifecycle tests
  // ════════════════════════════════════════════════════════════════

  @Test
  public void saveProofEvidence_constructorRequiresNonNullTargetFile() throws Exception {
    Path proofRoot = newTestDir();
    assertThrows(NullPointerException.class,
        () -> SaveOperationCompletionEvidence.saveProofEvidence(null, proofRoot));
  }

  @Test
  public void saveProofEvidence_constructorRequiresNonNullProofRoot() throws Exception {
    File targetFile = Files.writeString(newTestDir().resolve("test.a3p"), "content").toFile();
    assertThrows(NullPointerException.class,
        () -> SaveOperationCompletionEvidence.saveProofEvidence(targetFile, null));
  }

  @Test
  public void saveProofEvidence_initialState_allFlagsDefaultFalse() throws Exception {
    Path testDir = newTestDir();
    File targetFile = Files.writeString(testDir.resolve("test.a3p"), "content").toFile();
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(targetFile, testDir);
    assertFalse(evidence.robotFileMenuOpened);
    assertFalse(evidence.robotSaveItemClicked);
    assertFalse(evidence.saveActionIdentityMatched);
    assertFalse(evidence.chooserObserved);
    assertFalse(evidence.approvedSelection);
    assertFalse(evidence.ambiguousChooserDiscovery);
    assertFalse(evidence.selectedFileVerified);
    assertFalse(evidence.dialogShowing);
    assertFalse(evidence.projectReadable);
    assertFalse(evidence.markerPresent);
    assertNull(evidence.dialogClass);
    assertNull(evidence.normalizedSelectedFile);
    assertEquals(0, evidence.pollCount);
    assertNull(evidence.blockerKind);
    assertNull(evidence.blockerObserved);
    assertNull(evidence.blockerRequired);
  }

  @Test
  public void saveProofEvidence_targetInsideProofRoot_setOnConstruction() throws Exception {
    Path testDir = newTestDir();
    File targetFile = Files.writeString(testDir.resolve("test.a3p"), "content").toFile();
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(targetFile, testDir);
    assertTrue("Target inside proof root", evidence.targetInsideProofRoot);
  }

  @Test
  public void saveProofEvidence_targetOutsideProofRoot_targetInsideFalse() throws Exception {
    Path proofRoot = newTestDir();
    Path externalDir = newTestDir();
    File targetFile = Files.writeString(externalDir.resolve("external.a3p"), "content").toFile();
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(targetFile, proofRoot);
    assertFalse("Target outside proof root", evidence.targetInsideProofRoot);
  }

  // ── block() ──────────────────────────────────────────────────────

  @Test
  public void block_recordsFirstBlockerOnly() throws Exception {
    Path testDir = newTestDir();
    File targetFile = Files.writeString(testDir.resolve("block.a3p"), "content").toFile();
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(targetFile, testDir);

    evidence.block("first_kind", "first_observed", "first_required");
    evidence.block("second_kind", "second_observed", "second_required");

    assertEquals("first_kind", evidence.blockerKind);
    assertEquals("first_observed", evidence.blockerObserved);
    assertEquals("first_required", evidence.blockerRequired);
  }

  @Test
  public void block_withNullKind_stillRecords() throws Exception {
    Path testDir = newTestDir();
    File targetFile = Files.writeString(testDir.resolve("null-block.a3p"), "content").toFile();
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(targetFile, testDir);

    // blockerKind starts null, so block() won't overwrite — null == null is true for ==
    // Actually: if (this.blockerKind == null) { this.blockerKind = kind; }
    // So null kind will set it to null again, which means second call will also match
    evidence.block(null, "obs1", "req1");
    // blockerKind is now null (set to null), so the null check passes again
    evidence.block("actual_kind", "obs2", "req2");

    // Since null was set first, the second block should still overwrite because
    // the check is blockerKind == null
    assertEquals("actual_kind", evidence.blockerKind);
  }

  // ── recordReadback() ─────────────────────────────────────────────

  @Test
  public void recordReadback_setsFields() throws Exception {
    Path testDir = newTestDir();
    File targetFile = Files.writeString(testDir.resolve("rb.a3p"), "content").toFile();
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(targetFile, testDir);

    evidence.recordReadback(true, true);

    assertTrue(evidence.projectReadable);
    assertTrue(evidence.markerPresent);
  }

  @Test
  public void recordReadback_falseValues() throws Exception {
    Path testDir = newTestDir();
    File targetFile = Files.writeString(testDir.resolve("rb2.a3p"), "content").toFile();
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(targetFile, testDir);

    evidence.recordReadback(false, false);

    assertFalse(evidence.projectReadable);
    assertFalse(evidence.markerPresent);
  }

  // ── recordSelectedFile() ─────────────────────────────────────────

  @Test
  public void recordSelectedFile_matchingFile_returnsTrue() throws Exception {
    Path testDir = newTestDir();
    File targetFile = Files.writeString(testDir.resolve("match.a3p"), "content").toFile();
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(targetFile, testDir);

    boolean result = evidence.recordSelectedFile(targetFile);

    assertTrue("Selected file matches target", result);
    assertTrue(evidence.selectedFileVerified);
    assertNotNull(evidence.normalizedSelectedFile);
  }

  @Test
  public void recordSelectedFile_differentFile_returnsFalse() throws Exception {
    Path testDir = newTestDir();
    File targetFile = Files.writeString(testDir.resolve("target.a3p"), "content").toFile();
    File otherFile = Files.writeString(testDir.resolve("other.a3p"), "content").toFile();
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(targetFile, testDir);

    boolean result = evidence.recordSelectedFile(otherFile);

    assertFalse("Different file does not match", result);
    assertFalse(evidence.selectedFileVerified);
  }

  @Test
  public void recordSelectedFile_nullFile_returnsFalse() throws Exception {
    Path testDir = newTestDir();
    File targetFile = Files.writeString(testDir.resolve("null-sel.a3p"), "content").toFile();
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(targetFile, testDir);

    boolean result = evidence.recordSelectedFile(null);

    assertFalse("Null selected file returns false", result);
    assertNull(evidence.normalizedSelectedFile);
  }

  @Test
  public void recordSelectedFile_nonA3pExtension_returnsFalse() throws Exception {
    Path testDir = newTestDir();
    File targetFile = Files.writeString(testDir.resolve("target.txt"), "content").toFile();
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(targetFile, testDir);

    boolean result = evidence.recordSelectedFile(targetFile);

    assertFalse("Non-.a3p extension returns false", result);
  }

  @Test
  public void recordSelectedFile_outsideProofRoot_returnsFalse() throws Exception {
    Path proofRoot = newTestDir();
    Path externalDir = newTestDir();
    File targetFile = Files.writeString(externalDir.resolve("external.a3p"), "content").toFile();
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(targetFile, proofRoot);

    boolean result = evidence.recordSelectedFile(targetFile);

    assertFalse("File outside proof root returns false", result);
  }

  // ── proofContainsPath() ──────────────────────────────────────────

  @Test
  public void proofContainsPath_pathInsideRoot_true() throws Exception {
    Path testDir = newTestDir();
    File targetFile = Files.writeString(testDir.resolve("inside.a3p"), "content").toFile();
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(targetFile, testDir);

    // Use a path that already exists (the testDir itself) so toRealPath() works,
    // or use a path without calling toRealPath
    Path subDir = Files.createDirectories(testDir.resolve("sub"));
    Path fileInSub = Files.writeString(subDir.resolve("file.txt"), "data");
    assertTrue(evidence.proofContainsPath(fileInSub.toRealPath()));
  }

  @Test
  public void proofContainsPath_pathOutsideRoot_false() throws Exception {
    Path testDir = newTestDir();
    Path otherDir = newTestDir();
    File targetFile = Files.writeString(testDir.resolve("inside.a3p"), "content").toFile();
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(targetFile, testDir);

    assertFalse(evidence.proofContainsPath(otherDir.resolve("file.txt")));
  }

  @Test
  public void proofContainsPath_nullPath_false() throws Exception {
    Path testDir = newTestDir();
    File targetFile = Files.writeString(testDir.resolve("inside.a3p"), "content").toFile();
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(targetFile, testDir);

    assertFalse(evidence.proofContainsPath(null));
  }

  // ── write() lifecycle ────────────────────────────────────────────

  @Test
  public void write_producesNonEmptyFile() throws Exception {
    Path testDir = newTestDir();
    Path evidenceDir = Files.createDirectories(testDir.resolve("evidence"));
    File targetFile = Files.writeString(testDir.resolve("write-test.a3p"), "content").toFile();
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(targetFile, testDir);

    Path artifact = evidence.write(
        evidenceDir.resolve(SaveOperationCompletionEvidence.SAVE_PROOF_ARTIFACT));

    assertTrue(Files.exists(artifact));
    assertTrue(Files.size(artifact) > 0);
    assertEquals(SaveOperationCompletionEvidence.SAVE_PROOF_ARTIFACT,
        artifact.getFileName().toString());
  }

  @Test
  public void write_producesValidJsonStructure() throws Exception {
    Path testDir = newTestDir();
    Path evidenceDir = Files.createDirectories(testDir.resolve("evidence"));
    File targetFile = Files.writeString(testDir.resolve("json-test.a3p"), "content").toFile();
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(targetFile, testDir);

    Path artifact = evidence.write(
        evidenceDir.resolve(SaveOperationCompletionEvidence.SAVE_PROOF_ARTIFACT));

    String json = Files.readString(artifact);
    assertTrue("Starts with {", json.trim().startsWith("{"));
    assertTrue("Ends with }", json.trim().endsWith("}"));
    assertTrue("Contains schemaVersion",
        json.contains("\"schemaVersion\": \"" +
            SaveOperationCompletionEvidence.SAVE_PROOF_SCHEMA_VERSION + "\""));
  }

  @Test
  public void write_rejectsWrongArtifactName() throws Exception {
    Path testDir = newTestDir();
    Path evidenceDir = Files.createDirectories(testDir.resolve("evidence"));
    File targetFile = Files.writeString(testDir.resolve("name-test.a3p"), "content").toFile();
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(targetFile, testDir);

    assertThrows(IllegalArgumentException.class,
        () -> evidence.write(evidenceDir.resolve("wrong-name.json")));
  }

  @Test
  public void write_rejectsArtifactOutsideProofRoot() throws Exception {
    Path proofRoot = Files.createDirectories(newTestDir().resolve("proof")).toRealPath();
    File targetFile = Files.writeString(proofRoot.resolve("test.a3p"), "content").toFile();
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(targetFile, proofRoot);
    Path outsideDir = newTestDir();
    Path outsideArtifact = outsideDir.resolve(SaveOperationCompletionEvidence.SAVE_PROOF_ARTIFACT);

    assertThrows(IllegalArgumentException.class,
        () -> evidence.write(outsideArtifact));
  }

  @Test
  public void write_refusesSymlinkOverwrite() throws Exception {
    Path proofRoot = Files.createDirectories(newTestDir().resolve("proof")).toRealPath();
    File targetFile = Files.writeString(proofRoot.resolve("sym-test.a3p"), "content").toFile();
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(targetFile, proofRoot);

    Path outsideDir = Files.createDirectories(newTestDir().resolve("outside")).toRealPath();
    Path outsideTarget = Files.writeString(
        outsideDir.resolve(SaveOperationCompletionEvidence.SAVE_PROOF_ARTIFACT), "{}");
    Path symlink = proofRoot.resolve(SaveOperationCompletionEvidence.SAVE_PROOF_ARTIFACT);
    try {
      Files.createSymbolicLink(symlink, outsideTarget);
    } catch (IOException | SecurityException | UnsupportedOperationException e) {
      Assume.assumeTrue("Symlinks not supported", false);
      return;
    }

    assertThrows(IOException.class, () -> evidence.write(symlink));
  }

  @Test
  public void write_overwritesExistingRegularFile() throws Exception {
    Path testDir = newTestDir();
    Path evidenceDir = Files.createDirectories(testDir.resolve("evidence"));
    File targetFile = Files.writeString(testDir.resolve("overwrite.a3p"), "content").toFile();
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(targetFile, testDir);

    Path artifactPath = evidenceDir.resolve(SaveOperationCompletionEvidence.SAVE_PROOF_ARTIFACT);
    Files.writeString(artifactPath, "old content");

    Path artifact = evidence.write(artifactPath);
    String json = Files.readString(artifact);
    assertFalse("Old content replaced", json.contains("old content"));
    assertTrue("New content present", json.contains("schemaVersion"));
  }

  // ── write() with state mutations ─────────────────────────────────

  @Test
  public void write_reflectsRecordReadback() throws Exception {
    Path testDir = newTestDir();
    Path evidenceDir = Files.createDirectories(testDir.resolve("evidence"));
    File targetFile = Files.writeString(testDir.resolve("readback-test.a3p"), "content").toFile();
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(targetFile, testDir);

    evidence.recordReadback(true, false);
    Path artifact = evidence.write(
        evidenceDir.resolve(SaveOperationCompletionEvidence.SAVE_PROOF_ARTIFACT));

    String json = Files.readString(artifact);
    assertTrue(json, json.contains("\"projectReadable\": true"));
    assertTrue(json, json.contains("\"markerPresent\": false"));
  }

  @Test
  public void write_reflectsBlockState() throws Exception {
    Path testDir = newTestDir();
    Path evidenceDir = Files.createDirectories(testDir.resolve("evidence"));
    File targetFile = Files.writeString(testDir.resolve("block-test.a3p"), "content").toFile();
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(targetFile, testDir);

    evidence.block("custom_block", "custom observed", "custom required");
    Path artifact = evidence.write(
        evidenceDir.resolve(SaveOperationCompletionEvidence.SAVE_PROOF_ARTIFACT));

    String json = Files.readString(artifact);
    assertTrue(json, json.contains("\"kind\": \"custom_block\""));
    assertTrue(json, json.contains("\"observed\": \"custom observed\""));
    assertTrue(json, json.contains("\"required\": \"custom required\""));
  }

  // ════════════════════════════════════════════════════════════════
  // configuredSaveProofScenario tests
  // ════════════════════════════════════════════════════════════════

  @Test
  public void configuredSaveProofScenario_defaultReturnsExpectedScenario() {
    String previousScenario = System.getProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO_PROPERTY);
    System.clearProperty(SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO_PROPERTY);
    try {
      String scenario = SaveOperationCompletionEvidence.configuredSaveProofScenario();
      assertEquals(SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO, scenario);
    } finally {
      restoreProperty(
          SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO_PROPERTY, previousScenario);
    }
  }

  @Test
  public void configuredSaveProofScenario_correctValueReturnsValue() {
    String previousScenario = System.getProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO_PROPERTY);
    System.setProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO_PROPERTY,
        SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO);
    try {
      String scenario = SaveOperationCompletionEvidence.configuredSaveProofScenario();
      assertEquals(SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO, scenario);
    } finally {
      restoreProperty(
          SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO_PROPERTY, previousScenario);
    }
  }

  @Test
  public void configuredSaveProofScenario_wrongValueThrows() {
    String previousScenario = System.getProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO_PROPERTY);
    System.setProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO_PROPERTY,
        "alice-desktop-wrong-scenario");
    try {
      assertThrows(IllegalArgumentException.class,
          SaveOperationCompletionEvidence::configuredSaveProofScenario);
    } finally {
      restoreProperty(
          SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO_PROPERTY, previousScenario);
    }
  }

  @Test
  public void configuredSaveProofScenario_blankValueReturnsDefault() {
    String previousScenario = System.getProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO_PROPERTY);
    System.setProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO_PROPERTY, "   ");
    try {
      String scenario = SaveOperationCompletionEvidence.configuredSaveProofScenario();
      assertEquals(SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO, scenario);
    } finally {
      restoreProperty(
          SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO_PROPERTY, previousScenario);
    }
  }

  // ════════════════════════════════════════════════════════════════
  // configuredSaveProofRunId tests
  // ════════════════════════════════════════════════════════════════

  @Test
  public void configuredSaveProofRunId_defaultStartsWithStandalone() {
    String previousRunId = System.getProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_RUN_ID_PROPERTY);
    System.clearProperty(SaveOperationCompletionEvidence.SAVE_PROOF_RUN_ID_PROPERTY);
    try {
      String runId = SaveOperationCompletionEvidence.configuredSaveProofRunId();
      assertTrue("Default starts with standalone-",
          runId.startsWith("standalone-"));
      assertTrue("Contains UUID", runId.length() > "standalone-".length());
    } finally {
      restoreProperty(
          SaveOperationCompletionEvidence.SAVE_PROOF_RUN_ID_PROPERTY, previousRunId);
    }
  }

  @Test
  public void configuredSaveProofRunId_validTokenAccepted() {
    String previousRunId = System.getProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_RUN_ID_PROPERTY);
    System.setProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_RUN_ID_PROPERTY,
        "ci-run-123.45_test");
    try {
      String runId = SaveOperationCompletionEvidence.configuredSaveProofRunId();
      assertEquals("ci-run-123.45_test", runId);
    } finally {
      restoreProperty(
          SaveOperationCompletionEvidence.SAVE_PROOF_RUN_ID_PROPERTY, previousRunId);
    }
  }

  @Test
  public void configuredSaveProofRunId_rejectsSpaces() {
    String previousRunId = System.getProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_RUN_ID_PROPERTY);
    System.setProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_RUN_ID_PROPERTY,
        "invalid run id");
    try {
      assertThrows(IllegalArgumentException.class,
          SaveOperationCompletionEvidence::configuredSaveProofRunId);
    } finally {
      restoreProperty(
          SaveOperationCompletionEvidence.SAVE_PROOF_RUN_ID_PROPERTY, previousRunId);
    }
  }

  @Test
  public void configuredSaveProofRunId_rejectsSemicolon() {
    String previousRunId = System.getProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_RUN_ID_PROPERTY);
    System.setProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_RUN_ID_PROPERTY,
        "run;id");
    try {
      assertThrows(IllegalArgumentException.class,
          SaveOperationCompletionEvidence::configuredSaveProofRunId);
    } finally {
      restoreProperty(
          SaveOperationCompletionEvidence.SAVE_PROOF_RUN_ID_PROPERTY, previousRunId);
    }
  }

  @Test
  public void configuredSaveProofRunId_rejectsPipe() {
    String previousRunId = System.getProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_RUN_ID_PROPERTY);
    System.setProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_RUN_ID_PROPERTY,
        "run|id");
    try {
      assertThrows(IllegalArgumentException.class,
          SaveOperationCompletionEvidence::configuredSaveProofRunId);
    } finally {
      restoreProperty(
          SaveOperationCompletionEvidence.SAVE_PROOF_RUN_ID_PROPERTY, previousRunId);
    }
  }

  @Test
  public void configuredSaveProofRunId_rejectsSlash() {
    String previousRunId = System.getProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_RUN_ID_PROPERTY);
    System.setProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_RUN_ID_PROPERTY,
        "../unsafe");
    try {
      assertThrows(IllegalArgumentException.class,
          SaveOperationCompletionEvidence::configuredSaveProofRunId);
    } finally {
      restoreProperty(
          SaveOperationCompletionEvidence.SAVE_PROOF_RUN_ID_PROPERTY, previousRunId);
    }
  }

  @Test
  public void configuredSaveProofRunId_blankFallsBackToStandalone() {
    String previousRunId = System.getProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_RUN_ID_PROPERTY);
    System.setProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_RUN_ID_PROPERTY, "   ");
    try {
      String runId = SaveOperationCompletionEvidence.configuredSaveProofRunId();
      assertTrue("Blank falls back to standalone-",
          runId.startsWith("standalone-"));
    } finally {
      restoreProperty(
          SaveOperationCompletionEvidence.SAVE_PROOF_RUN_ID_PROPERTY, previousRunId);
    }
  }

  // ════════════════════════════════════════════════════════════════
  // configuredSaveProofArtifact tests
  // ════════════════════════════════════════════════════════════════

  @Test
  public void configuredSaveProofArtifact_defaultResolvesUnderProofRoot() throws Exception {
    Path proofRoot = Files.createDirectories(newTestDir().resolve("proof")).toRealPath();
    String previousPath = System.getProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_EVIDENCE_PATH_PROPERTY);
    System.clearProperty(SaveOperationCompletionEvidence.SAVE_PROOF_EVIDENCE_PATH_PROPERTY);
    try {
      Path artifact = SaveOperationCompletionEvidence.configuredSaveProofArtifact(proofRoot);
      assertEquals(SaveOperationCompletionEvidence.SAVE_PROOF_ARTIFACT,
          artifact.getFileName().toString());
      assertTrue("Under proof root", artifact.startsWith(proofRoot));
    } finally {
      restoreProperty(
          SaveOperationCompletionEvidence.SAVE_PROOF_EVIDENCE_PATH_PROPERTY, previousPath);
    }
  }

  @Test
  public void configuredSaveProofArtifact_customPathUnderRoot() throws Exception {
    Path proofRoot = Files.createDirectories(newTestDir().resolve("proof")).toRealPath();
    Path customPath = proofRoot.resolve("sub")
        .resolve(SaveOperationCompletionEvidence.SAVE_PROOF_ARTIFACT);
    String previousPath = System.getProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_EVIDENCE_PATH_PROPERTY);
    System.setProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_EVIDENCE_PATH_PROPERTY,
        customPath.toString());
    try {
      Path artifact = SaveOperationCompletionEvidence.configuredSaveProofArtifact(proofRoot);
      assertEquals(SaveOperationCompletionEvidence.SAVE_PROOF_ARTIFACT,
          artifact.getFileName().toString());
    } finally {
      restoreProperty(
          SaveOperationCompletionEvidence.SAVE_PROOF_EVIDENCE_PATH_PROPERTY, previousPath);
    }
  }

  @Test
  public void configuredSaveProofArtifact_wrongFileName_throws() throws Exception {
    Path proofRoot = Files.createDirectories(newTestDir().resolve("proof")).toRealPath();
    String previousPath = System.getProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_EVIDENCE_PATH_PROPERTY);
    System.setProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_EVIDENCE_PATH_PROPERTY,
        proofRoot.resolve("wrong-name.json").toString());
    try {
      assertThrows(IllegalArgumentException.class,
          () -> SaveOperationCompletionEvidence.configuredSaveProofArtifact(proofRoot));
    } finally {
      restoreProperty(
          SaveOperationCompletionEvidence.SAVE_PROOF_EVIDENCE_PATH_PROPERTY, previousPath);
    }
  }

  @Test
  public void configuredSaveProofArtifact_outsideRoot_throws() throws Exception {
    Path proofRoot = Files.createDirectories(newTestDir().resolve("proof")).toRealPath();
    Path outsidePath = newTestDir().resolve(SaveOperationCompletionEvidence.SAVE_PROOF_ARTIFACT);
    String previousPath = System.getProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_EVIDENCE_PATH_PROPERTY);
    System.setProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_EVIDENCE_PATH_PROPERTY,
        outsidePath.toString());
    try {
      assertThrows(IllegalArgumentException.class,
          () -> SaveOperationCompletionEvidence.configuredSaveProofArtifact(proofRoot));
    } finally {
      restoreProperty(
          SaveOperationCompletionEvidence.SAVE_PROOF_EVIDENCE_PATH_PROPERTY, previousPath);
    }
  }

  @Test
  public void configuredSaveProofArtifact_nullProofRoot_throwsNPE() {
    assertThrows(NullPointerException.class,
        () -> SaveOperationCompletionEvidence.configuredSaveProofArtifact(null));
  }

  // ════════════════════════════════════════════════════════════════
  // isSaveActionInvocationProofOnly tests
  // ════════════════════════════════════════════════════════════════

  @Test
  public void isSaveActionInvocationProofOnly_defaultFalse() {
    String previous = System.getProperty(
        SaveOperationCompletionEvidence.PROOF_ONLY_PROPERTY);
    System.clearProperty(SaveOperationCompletionEvidence.PROOF_ONLY_PROPERTY);
    try {
      assertFalse(SaveOperationCompletionEvidence.isSaveActionInvocationProofOnly());
    } finally {
      restoreProperty(SaveOperationCompletionEvidence.PROOF_ONLY_PROPERTY, previous);
    }
  }

  @Test
  public void isSaveActionInvocationProofOnly_trueWhenPropertySet() {
    String previous = System.getProperty(
        SaveOperationCompletionEvidence.PROOF_ONLY_PROPERTY);
    System.setProperty(SaveOperationCompletionEvidence.PROOF_ONLY_PROPERTY, "true");
    try {
      assertTrue(SaveOperationCompletionEvidence.isSaveActionInvocationProofOnly());
    } finally {
      restoreProperty(SaveOperationCompletionEvidence.PROOF_ONLY_PROPERTY, previous);
    }
  }

  @Test
  public void isSaveActionInvocationProofOnly_falseForNonTrueValue() {
    String previous = System.getProperty(
        SaveOperationCompletionEvidence.PROOF_ONLY_PROPERTY);
    System.setProperty(SaveOperationCompletionEvidence.PROOF_ONLY_PROPERTY, "yes");
    try {
      assertFalse(SaveOperationCompletionEvidence.isSaveActionInvocationProofOnly());
    } finally {
      restoreProperty(SaveOperationCompletionEvidence.PROOF_ONLY_PROPERTY, previous);
    }
  }

  // ════════════════════════════════════════════════════════════════
  // record / recordSaveActionInvocation — opt-in via property tests
  // ════════════════════════════════════════════════════════════════

  @Test
  public void record_nullResultDoesNotThrow() {
    String previous = System.getProperty(
        SaveOperationCompletionEvidence.EVIDENCE_DIR_PROPERTY);
    System.setProperty(
        SaveOperationCompletionEvidence.EVIDENCE_DIR_PROPERTY, "/tmp/test-evidence");
    try {
      // Should not throw (swallows exception)
      SaveOperationCompletionEvidence.record("Op", "a3p", null);
    } finally {
      restoreProperty(SaveOperationCompletionEvidence.EVIDENCE_DIR_PROPERTY, previous);
    }
  }

  @Test
  public void record_blankPropertyDoesNotWriteArtifact() throws Exception {
    Path evidenceDir = newTestDir();
    String previous = System.getProperty(
        SaveOperationCompletionEvidence.EVIDENCE_DIR_PROPERTY);
    System.setProperty(
        SaveOperationCompletionEvidence.EVIDENCE_DIR_PROPERTY, "   ");
    try {
      SaveOperationCompletionEvidence.record("Op", "a3p",
          new SaveOperationFlow.Result(true, false, 1, 1, null));
      assertFalse(Files.exists(
          evidenceDir.resolve(SaveOperationCompletionEvidence.ARTIFACT)));
    } finally {
      restoreProperty(SaveOperationCompletionEvidence.EVIDENCE_DIR_PROPERTY, previous);
    }
  }

  @Test
  public void recordSaveActionInvocation_blankPropertyDoesNotWriteArtifact() throws Exception {
    Path evidenceDir = newTestDir();
    String previous = System.getProperty(
        SaveOperationCompletionEvidence.EVIDENCE_DIR_PROPERTY);
    System.setProperty(
        SaveOperationCompletionEvidence.EVIDENCE_DIR_PROPERTY, "");
    try {
      SaveOperationCompletionEvidence.recordSaveActionInvocation(
          "Op", "a3p", true, true);
      assertFalse(Files.exists(
          evidenceDir.resolve(SaveOperationCompletionEvidence.SAVE_ACTION_INVOCATION_PROOF_ARTIFACT)));
    } finally {
      restoreProperty(SaveOperationCompletionEvidence.EVIDENCE_DIR_PROPERTY, previous);
    }
  }

  @Test
  public void recordSaveActionInvocation_writesArtifact_whenPropertySet() throws Exception {
    Path evidenceDir = Files.createDirectories(newTestDir().resolve("invocation"));
    String previous = System.getProperty(
        SaveOperationCompletionEvidence.EVIDENCE_DIR_PROPERTY);
    System.setProperty(
        SaveOperationCompletionEvidence.EVIDENCE_DIR_PROPERTY,
        evidenceDir.toString());
    try {
      SaveOperationCompletionEvidence.recordSaveActionInvocation(
          "org.test.Op", "a3p", false, false);
      Path artifact = evidenceDir.resolve(
          SaveOperationCompletionEvidence.SAVE_ACTION_INVOCATION_PROOF_ARTIFACT);
      assertTrue(Files.exists(artifact));
      assertTrue(Files.size(artifact) > 0);
      String json = Files.readString(artifact);
      assertTrue(json, json.contains("\"reason\": \"missing_active_stage_ide\""));
    } finally {
      restoreProperty(SaveOperationCompletionEvidence.EVIDENCE_DIR_PROPERTY, previous);
    }
  }

  @Test
  public void recordSaveActionInvocation_withTrigger_writesArtifact() throws Exception {
    Path evidenceDir = Files.createDirectories(newTestDir().resolve("trigger-invocation"));
    String previous = System.getProperty(
        SaveOperationCompletionEvidence.EVIDENCE_DIR_PROPERTY);
    System.setProperty(
        SaveOperationCompletionEvidence.EVIDENCE_DIR_PROPERTY,
        evidenceDir.toString());
    try {
      SaveOperationCompletionEvidence.InvocationTrigger trigger =
          createTrigger(
              "org.lgna.croquet.triggers.ActionEventTrigger",
              "org.lgna.croquet.views.MenuItem",
              "javax.swing.JMenuItem");
      SaveOperationCompletionEvidence.recordSaveActionInvocation(
          "org.test.Op", "a3p", true, true, trigger);
      Path artifact = evidenceDir.resolve(
          SaveOperationCompletionEvidence.SAVE_ACTION_INVOCATION_PROOF_ARTIFACT);
      assertTrue(Files.exists(artifact));
      String json = Files.readString(artifact);
      assertTrue(json, json.contains("\"reason\": \"save_menu_item_dispatched\""));
      assertTrue(json, json.contains("\"menu_item_dispatch\": true"));
    } finally {
      restoreProperty(SaveOperationCompletionEvidence.EVIDENCE_DIR_PROPERTY, previous);
    }
  }

  // ════════════════════════════════════════════════════════════════
  // Constants verification
  // ════════════════════════════════════════════════════════════════

  @Test
  public void constants_artifactNames() {
    assertEquals("desktop-save-operation-result.json",
        SaveOperationCompletionEvidence.ARTIFACT);
    assertEquals("desktop-save-dialog-control-target.json",
        SaveOperationCompletionEvidence.DIALOG_CONTROL_ARTIFACT);
    assertEquals("desktop-save-action-invocation-proof.json",
        SaveOperationCompletionEvidence.SAVE_ACTION_INVOCATION_PROOF_ARTIFACT);
    assertEquals("robot-save-menu-dialog-write-readback-proof.json",
        SaveOperationCompletionEvidence.SAVE_PROOF_ARTIFACT);
  }

  @Test
  public void constants_schemaVersion() {
    assertEquals("eatme.alice-desktop-save-menu-dialog-write-readback-proof/v1",
        SaveOperationCompletionEvidence.SAVE_PROOF_SCHEMA_VERSION);
  }

  @Test
  public void constants_scenario() {
    assertEquals("alice-desktop-save-menu-dialog-write-proof",
        SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO);
  }

  @Test
  public void constants_workflow() {
    assertEquals("save-menu-dialog-write-proof",
        SaveOperationCompletionEvidence.SAVE_PROOF_WORKFLOW);
  }

  @Test
  public void constants_marker() {
    assertEquals("robotSaveMenuRoundTripMarker",
        SaveOperationCompletionEvidence.SAVE_PROOF_MARKER);
  }

  @Test
  public void constants_propertyNames() {
    assertEquals("org.alice.eatme.saveOperationEvidenceDir",
        SaveOperationCompletionEvidence.EVIDENCE_DIR_PROPERTY);
    assertEquals("org.alice.eatme.saveActionInvocationProofOnly",
        SaveOperationCompletionEvidence.PROOF_ONLY_PROPERTY);
    assertEquals("org.alice.eatme.saveProof.scenario",
        SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO_PROPERTY);
    assertEquals("org.alice.eatme.saveProof.runId",
        SaveOperationCompletionEvidence.SAVE_PROOF_RUN_ID_PROPERTY);
    assertEquals("org.alice.eatme.saveProof.evidencePath",
        SaveOperationCompletionEvidence.SAVE_PROOF_EVIDENCE_PATH_PROPERTY);
  }

  @Test
  public void constants_envNames() {
    assertEquals("ALICE_SAVE_PROOF_SCENARIO",
        SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO_ENV);
    assertEquals("ALICE_SAVE_PROOF_RUN_ID",
        SaveOperationCompletionEvidence.SAVE_PROOF_RUN_ID_ENV);
    assertEquals("ALICE_SAVE_PROOF_EVIDENCE_PATH",
        SaveOperationCompletionEvidence.SAVE_PROOF_EVIDENCE_PATH_ENV);
  }

  // ── escapeJson delegation ────────────────────────────────────────

  @Test
  public void escapeJson_delegatesToEvidenceJsonWriter() {
    String result = SaveOperationCompletionEvidence.escapeJson("a\"b");
    assertEquals("a\\\"b", result);
  }

  // ── helpers ──────────────────────────────────────────────────────

  private static Path newTestDir() throws Exception {
    return Files.createDirectories(Path.of(
        "target",
        "save-operation-lifecycle-test",
        UUID.randomUUID().toString()));
  }

  private static void restoreProperty(String name, String value) {
    if (value == null) {
      System.clearProperty(name);
    } else {
      System.setProperty(name, value);
    }
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
