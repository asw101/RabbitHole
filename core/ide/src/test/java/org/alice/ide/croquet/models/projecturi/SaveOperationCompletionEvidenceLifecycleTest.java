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
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * Lifecycle tests for {@link SaveOperationCompletionEvidence} inner classes
 * and configured-proof helpers.
 *
 * <p>Covers {@link SaveOperationCompletionEvidence.InvocationTrigger},
 * {@link SaveOperationCompletionEvidence.SaveProofEvidence} lifecycle
 * (block, recordReadback, recordSelectedFile, proofContainsPath, write),
 * and the configuredSaveProof* methods.
 */
public class SaveOperationCompletionEvidenceLifecycleTest {

  @Rule
  public final TemporaryFolder tmp = new TemporaryFolder();

  // ── InvocationTrigger ─────────────────────────────────────────────

  @Test
  public void invocationTriggerNoneHasNullFields() {
    SaveOperationCompletionEvidence.InvocationTrigger trigger =
        SaveOperationCompletionEvidence.InvocationTrigger.none();
    assertEquals(null, trigger.triggerClass());
    assertEquals(null, trigger.viewControllerClass());
    assertEquals(null, trigger.awtSourceClass());
  }

  @Test
  public void invocationTriggerNoneIsNotMenuItemDispatch() {
    SaveOperationCompletionEvidence.InvocationTrigger trigger =
        SaveOperationCompletionEvidence.InvocationTrigger.none();
    assertFalse(trigger.isMenuItemDispatch());
  }

  @Test
  public void invocationTriggerWithCorrectClassesIsMenuItemDispatch() {
    SaveOperationCompletionEvidence.InvocationTrigger trigger =
        createTrigger(
            "org.lgna.croquet.triggers.ActionEventTrigger",
            "org.lgna.croquet.views.MenuItem",
            "javax.swing.JMenuItem");
    assertTrue(trigger.isMenuItemDispatch());
  }

  @Test
  public void invocationTriggerWithWrongTriggerClassIsNotMenuItemDispatch() {
    SaveOperationCompletionEvidence.InvocationTrigger trigger =
        createTrigger(
            "org.lgna.croquet.triggers.OtherTrigger",
            "org.lgna.croquet.views.MenuItem",
            "javax.swing.JMenuItem");
    assertFalse(trigger.isMenuItemDispatch());
  }

  @Test
  public void invocationTriggerWithWrongViewControllerIsNotMenuItemDispatch() {
    SaveOperationCompletionEvidence.InvocationTrigger trigger =
        createTrigger(
            "org.lgna.croquet.triggers.ActionEventTrigger",
            "org.lgna.croquet.views.Button",
            "javax.swing.JMenuItem");
    assertFalse(trigger.isMenuItemDispatch());
  }

  @Test
  public void invocationTriggerWithWrongAwtSourceIsNotMenuItemDispatch() {
    SaveOperationCompletionEvidence.InvocationTrigger trigger =
        createTrigger(
            "org.lgna.croquet.triggers.ActionEventTrigger",
            "org.lgna.croquet.views.MenuItem",
            "javax.swing.JButton");
    assertFalse(trigger.isMenuItemDispatch());
  }

  @Test
  public void invocationTriggerWithNullFieldsIsNotMenuItemDispatch() {
    SaveOperationCompletionEvidence.InvocationTrigger trigger =
        createTrigger(null, null, null);
    assertFalse(trigger.isMenuItemDispatch());
  }

  @Test
  public void invocationTriggerAccessorsReturnConstructorValues() {
    SaveOperationCompletionEvidence.InvocationTrigger trigger =
        createTrigger(
            "triggerClass", "viewControllerClass", "awtSourceClass");
    assertEquals("triggerClass", trigger.triggerClass());
    assertEquals("viewControllerClass", trigger.viewControllerClass());
    assertEquals("awtSourceClass", trigger.awtSourceClass());
  }

  @Test
  public void invocationTriggerIsMenuItemDispatchRequiresAllThreeFields() {
    // Missing any one field should return false
    assertFalse(createTrigger(
        "org.lgna.croquet.triggers.ActionEventTrigger",
        "org.lgna.croquet.views.MenuItem",
        null).isMenuItemDispatch());

    assertFalse(createTrigger(
        "org.lgna.croquet.triggers.ActionEventTrigger",
        null,
        "javax.swing.JMenuItem").isMenuItemDispatch());

    assertFalse(createTrigger(
        null,
        "org.lgna.croquet.views.MenuItem",
        "javax.swing.JMenuItem").isMenuItemDispatch());
  }

  // ── SaveProofEvidence lifecycle ────────────────────────────────────

  @Test
  public void saveProofEvidenceConstructionSetsBasicFields() throws IOException {
    File proofRoot = tmp.newFolder("proof-root");
    File target = new File(proofRoot, "test.a3p");
    Files.writeString(target.toPath(), "content");

    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(target, proofRoot.toPath());
    assertNotNull(evidence);
    assertTrue("Target is inside proof root", evidence.targetInsideProofRoot);
  }

  @Test
  public void saveProofEvidenceConstructionRejectsNullTargetFile() throws IOException {
    File proofRoot = tmp.newFolder("proof-root");
    assertThrows(NullPointerException.class,
        () -> SaveOperationCompletionEvidence.saveProofEvidence(null, proofRoot.toPath()));
  }

  @Test
  public void saveProofEvidenceConstructionRejectsNullProofRoot() throws IOException {
    File target = tmp.newFile("test.a3p");
    assertThrows(NullPointerException.class,
        () -> SaveOperationCompletionEvidence.saveProofEvidence(target, null));
  }

  @Test
  public void saveProofEvidenceBlockRecordsFirstBlocker() throws IOException {
    File proofRoot = tmp.newFolder("proof-root");
    File target = new File(proofRoot, "test.a3p");
    Files.writeString(target.toPath(), "content");
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(target, proofRoot.toPath());

    evidence.block("test_kind", "test_observed", "test_required");
    assertEquals("test_kind", evidence.blockerKind);
    assertEquals("test_observed", evidence.blockerObserved);
    assertEquals("test_required", evidence.blockerRequired);
  }

  @Test
  public void saveProofEvidenceBlockIgnoresSubsequentBlockers() throws IOException {
    File proofRoot = tmp.newFolder("proof-root");
    File target = new File(proofRoot, "test.a3p");
    Files.writeString(target.toPath(), "content");
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(target, proofRoot.toPath());

    evidence.block("first_kind", "first_observed", "first_required");
    evidence.block("second_kind", "second_observed", "second_required");

    assertEquals("first_kind", evidence.blockerKind);
    assertEquals("first_observed", evidence.blockerObserved);
    assertEquals("first_required", evidence.blockerRequired);
  }

  @Test
  public void saveProofEvidenceRecordReadbackSetsFields() throws IOException {
    File proofRoot = tmp.newFolder("proof-root");
    File target = new File(proofRoot, "test.a3p");
    Files.writeString(target.toPath(), "content");
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(target, proofRoot.toPath());

    evidence.recordReadback(true, true);
    assertTrue(evidence.projectReadable);
    assertTrue(evidence.markerPresent);
  }

  @Test
  public void saveProofEvidenceRecordReadbackFalse() throws IOException {
    File proofRoot = tmp.newFolder("proof-root");
    File target = new File(proofRoot, "test.a3p");
    Files.writeString(target.toPath(), "content");
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(target, proofRoot.toPath());

    evidence.recordReadback(false, false);
    assertFalse(evidence.projectReadable);
    assertFalse(evidence.markerPresent);
  }

  @Test
  public void saveProofEvidenceRecordReadbackCanUpdate() throws IOException {
    File proofRoot = tmp.newFolder("proof-root");
    File target = new File(proofRoot, "test.a3p");
    Files.writeString(target.toPath(), "content");
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(target, proofRoot.toPath());

    evidence.recordReadback(false, false);
    assertFalse(evidence.projectReadable);
    evidence.recordReadback(true, true);
    assertTrue(evidence.projectReadable);
    assertTrue(evidence.markerPresent);
  }

  // ── proofContainsPath ─────────────────────────────────────────────

  @Test
  public void proofContainsPathReturnsTrueForChildPath() throws IOException {
    File proofRoot = tmp.newFolder("proof-root");
    File target = new File(proofRoot, "test.a3p");
    Files.writeString(target.toPath(), "content");
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(target, proofRoot.toPath());

    assertTrue(evidence.proofContainsPath(proofRoot.toPath().toRealPath().resolve("sub/file.json")));
  }

  @Test
  public void proofContainsPathReturnsFalseForOutsidePath() throws IOException {
    File proofRoot = tmp.newFolder("proof-root");
    File target = new File(proofRoot, "test.a3p");
    Files.writeString(target.toPath(), "content");
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(target, proofRoot.toPath());

    assertFalse(evidence.proofContainsPath(Path.of("/tmp/outside/file.json")));
  }

  @Test
  public void proofContainsPathReturnsFalseForNull() throws IOException {
    File proofRoot = tmp.newFolder("proof-root");
    File target = new File(proofRoot, "test.a3p");
    Files.writeString(target.toPath(), "content");
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(target, proofRoot.toPath());

    assertFalse(evidence.proofContainsPath(null));
  }

  @Test
  public void proofContainsPathReturnsTrueForRootItself() throws IOException {
    File proofRoot = tmp.newFolder("proof-root");
    File target = new File(proofRoot, "test.a3p");
    Files.writeString(target.toPath(), "content");
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(target, proofRoot.toPath());

    assertTrue(evidence.proofContainsPath(proofRoot.toPath().toRealPath()));
  }

  // ── recordSelectedFile ────────────────────────────────────────────

  @Test
  public void recordSelectedFileReturnsTrueForMatchingA3pInProofRoot() throws IOException {
    File proofRoot = tmp.newFolder("proof-root");
    File target = new File(proofRoot, "test.a3p");
    Files.writeString(target.toPath(), "content");
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(target, proofRoot.toPath());

    boolean result = evidence.recordSelectedFile(target);
    assertTrue(result);
    assertTrue(evidence.selectedFileVerified);
    assertNotNull(evidence.normalizedSelectedFile);
  }

  @Test
  public void recordSelectedFileReturnsFalseForDifferentFile() throws IOException {
    File proofRoot = tmp.newFolder("proof-root");
    File target = new File(proofRoot, "test.a3p");
    Files.writeString(target.toPath(), "content");
    File differentFile = new File(proofRoot, "other.a3p");
    Files.writeString(differentFile.toPath(), "other content");
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(target, proofRoot.toPath());

    boolean result = evidence.recordSelectedFile(differentFile);
    assertFalse(result);
    assertFalse(evidence.selectedFileVerified);
  }

  @Test
  public void recordSelectedFileReturnsFalseForNullFile() throws IOException {
    File proofRoot = tmp.newFolder("proof-root");
    File target = new File(proofRoot, "test.a3p");
    Files.writeString(target.toPath(), "content");
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(target, proofRoot.toPath());

    boolean result = evidence.recordSelectedFile(null);
    assertFalse(result);
    assertFalse(evidence.selectedFileVerified);
  }

  @Test
  public void recordSelectedFileReturnsFalseForNonA3pFile() throws IOException {
    File proofRoot = tmp.newFolder("proof-root");
    File target = new File(proofRoot, "test.txt");
    Files.writeString(target.toPath(), "content");
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(target, proofRoot.toPath());

    boolean result = evidence.recordSelectedFile(target);
    // selectedFileVerified should be true (canonical paths match)
    assertTrue(evidence.selectedFileVerified);
    // But overall result false because .txt not .a3p
    assertFalse(result);
  }

  @Test
  public void recordSelectedFileReturnsFalseForFileOutsideProofRoot() throws IOException {
    File proofRoot = tmp.newFolder("proof-root");
    File outsideDir = tmp.newFolder("outside");
    File target = new File(outsideDir, "test.a3p");
    Files.writeString(target.toPath(), "content");
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(target, proofRoot.toPath());

    boolean result = evidence.recordSelectedFile(target);
    // selectedFileVerified may be true (paths match) but targetInsideProofRoot is false
    assertFalse(result);
  }

  // ── SaveProofEvidence.write ───────────────────────────────────────

  @Test
  public void writeProducesNonEmptyArtifact() throws IOException {
    File proofRoot = tmp.newFolder("proof-root");
    File target = new File(proofRoot, "test.a3p");
    Files.writeString(target.toPath(), "content");
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(target, proofRoot.toPath());

    Path evidenceDir = Files.createDirectories(proofRoot.toPath().resolve("evidence"));
    Path artifact = evidence.write(
        evidenceDir.resolve(SaveOperationCompletionEvidence.SAVE_PROOF_ARTIFACT));

    assertTrue(Files.exists(artifact));
    assertTrue(Files.size(artifact) > 0);
  }

  @Test
  public void writeProducesValidJsonStructure() throws IOException {
    File proofRoot = tmp.newFolder("proof-root");
    File target = new File(proofRoot, "test.a3p");
    Files.writeString(target.toPath(), "content");
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(target, proofRoot.toPath());
    evidence.robotFileMenuOpened = true;
    evidence.robotSaveItemClicked = true;

    Path evidenceDir = Files.createDirectories(proofRoot.toPath().resolve("evidence"));
    Path artifact = evidence.write(
        evidenceDir.resolve(SaveOperationCompletionEvidence.SAVE_PROOF_ARTIFACT));

    String json = Files.readString(artifact);
    assertTrue(json, json.trim().startsWith("{"));
    assertTrue(json, json.trim().endsWith("}"));
    assertTrue(json, json.contains("\"schemaVersion\":"));
    assertTrue(json, json.contains("\"fileMenuOpened\": true"));
    assertTrue(json, json.contains("\"saveMenuItemInvoked\": true"));
  }

  @Test
  public void writeRejectsWrongArtifactFilename() throws IOException {
    File proofRoot = tmp.newFolder("proof-root");
    File target = new File(proofRoot, "test.a3p");
    Files.writeString(target.toPath(), "content");
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(target, proofRoot.toPath());

    Path wrongName = proofRoot.toPath().resolve("wrong-name.json");
    assertThrows(IllegalArgumentException.class, () -> evidence.write(wrongName));
  }

  @Test
  public void writeRejectsArtifactOutsideProofRoot() throws IOException {
    File proofRoot = tmp.newFolder("proof-root");
    File outsideDir = tmp.newFolder("outside");
    File target = new File(proofRoot, "test.a3p");
    Files.writeString(target.toPath(), "content");
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(target, proofRoot.toPath());

    Path outsideArtifact = outsideDir.toPath().resolve(
        SaveOperationCompletionEvidence.SAVE_PROOF_ARTIFACT);
    assertThrows(IllegalArgumentException.class, () -> evidence.write(outsideArtifact));
  }

  @Test
  public void writeRefusesSymlinkArtifact() throws IOException {
    File proofRoot = tmp.newFolder("proof-root");
    File outsideDir = tmp.newFolder("outside");
    File target = new File(proofRoot, "test.a3p");
    Files.writeString(target.toPath(), "content");
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(target, proofRoot.toPath());

    Path outsideFile = Files.writeString(
        outsideDir.toPath().resolve(SaveOperationCompletionEvidence.SAVE_PROOF_ARTIFACT), "{}");
    Path symlinkArtifact = proofRoot.toPath().resolve(
        SaveOperationCompletionEvidence.SAVE_PROOF_ARTIFACT);
    try {
      Files.createSymbolicLink(symlinkArtifact, outsideFile);
    } catch (IOException | UnsupportedOperationException e) {
      return;
    }

    assertThrows(IOException.class, () -> evidence.write(symlinkArtifact));
  }

  @Test
  public void writeWithAllFieldsSetProducesBlockedEvidence() throws IOException {
    File proofRoot = tmp.newFolder("proof-root");
    File target = new File(proofRoot, "test.a3p");
    Files.writeString(target.toPath(), "content");
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(target, proofRoot.toPath());

    // Set most fields but not readback
    evidence.robotFileMenuOpened = true;
    evidence.robotSaveItemClicked = true;
    evidence.saveActionIdentityMatched = true;
    evidence.chooserObserved = true;
    evidence.dialogShowing = true;
    evidence.approvedSelection = true;
    evidence.selectedFileVerified = true;
    evidence.normalizedSelectedFile = target.getCanonicalPath();
    // No readback, so still blocked
    evidence.recordReadback(false, false);

    Path evidenceDir = Files.createDirectories(proofRoot.toPath().resolve("evidence"));
    Path artifact = evidence.write(
        evidenceDir.resolve(SaveOperationCompletionEvidence.SAVE_PROOF_ARTIFACT));

    String json = Files.readString(artifact);
    assertTrue(json, json.contains("\"status\": \"blocked\""));
    assertTrue(json, json.contains("\"kind\": \"readback_failed\""));
  }

  @Test
  public void writeWithAllFieldsAndReadbackStillBlockedWithoutMarker() throws IOException {
    File proofRoot = tmp.newFolder("proof-root");
    File target = new File(proofRoot, "test.a3p");
    Files.writeString(target.toPath(), "content");
    SaveOperationCompletionEvidence.SaveProofEvidence evidence =
        SaveOperationCompletionEvidence.saveProofEvidence(target, proofRoot.toPath());

    evidence.robotFileMenuOpened = true;
    evidence.robotSaveItemClicked = true;
    evidence.saveActionIdentityMatched = true;
    evidence.chooserObserved = true;
    evidence.dialogShowing = true;
    evidence.approvedSelection = true;
    evidence.selectedFileVerified = true;
    evidence.normalizedSelectedFile = target.getCanonicalPath();
    evidence.recordReadback(true, false);

    Path evidenceDir = Files.createDirectories(proofRoot.toPath().resolve("evidence"));
    Path artifact = evidence.write(
        evidenceDir.resolve(SaveOperationCompletionEvidence.SAVE_PROOF_ARTIFACT));

    String json = Files.readString(artifact);
    assertTrue(json, json.contains("\"status\": \"blocked\""));
    assertTrue(json, json.contains("\"kind\": \"marker_missing\""));
  }

  // ── configuredSaveProofScenario ───────────────────────────────────

  @Test
  public void configuredSaveProofScenarioReturnsDefaultWhenNotConfigured() {
    String previousScenario = System.getProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO_PROPERTY);
    System.clearProperty(SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO_PROPERTY);
    try {
      String scenario = SaveOperationCompletionEvidence.configuredSaveProofScenario();
      assertEquals(SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO, scenario);
    } finally {
      restoreProperty(SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO_PROPERTY, previousScenario);
    }
  }

  @Test
  public void configuredSaveProofScenarioAcceptsCorrectScenario() {
    String previousScenario = System.getProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO_PROPERTY);
    System.setProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO_PROPERTY,
        SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO);
    try {
      String scenario = SaveOperationCompletionEvidence.configuredSaveProofScenario();
      assertEquals(SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO, scenario);
    } finally {
      restoreProperty(SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO_PROPERTY, previousScenario);
    }
  }

  @Test
  public void configuredSaveProofScenarioRejectsWrongScenario() {
    String previousScenario = System.getProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO_PROPERTY);
    System.setProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO_PROPERTY,
        "wrong-scenario");
    try {
      assertThrows(IllegalArgumentException.class,
          SaveOperationCompletionEvidence::configuredSaveProofScenario);
    } finally {
      restoreProperty(SaveOperationCompletionEvidence.SAVE_PROOF_SCENARIO_PROPERTY, previousScenario);
    }
  }

  // ── configuredSaveProofRunId ──────────────────────────────────────

  @Test
  public void configuredSaveProofRunIdGeneratesStandaloneWhenNotConfigured() {
    String previousRunId = System.getProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_RUN_ID_PROPERTY);
    System.clearProperty(SaveOperationCompletionEvidence.SAVE_PROOF_RUN_ID_PROPERTY);
    try {
      String runId = SaveOperationCompletionEvidence.configuredSaveProofRunId();
      assertTrue(runId, runId.startsWith("standalone-"));
      assertTrue(runId, runId.matches("[A-Za-z0-9._-]+"));
    } finally {
      restoreProperty(SaveOperationCompletionEvidence.SAVE_PROOF_RUN_ID_PROPERTY, previousRunId);
    }
  }

  @Test
  public void configuredSaveProofRunIdAcceptsSafeToken() {
    String previousRunId = System.getProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_RUN_ID_PROPERTY);
    System.setProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_RUN_ID_PROPERTY,
        "my-test-run.123_v2");
    try {
      String runId = SaveOperationCompletionEvidence.configuredSaveProofRunId();
      assertEquals("my-test-run.123_v2", runId);
    } finally {
      restoreProperty(SaveOperationCompletionEvidence.SAVE_PROOF_RUN_ID_PROPERTY, previousRunId);
    }
  }

  @Test
  public void configuredSaveProofRunIdRejectsSpaces() {
    String previousRunId = System.getProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_RUN_ID_PROPERTY);
    System.setProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_RUN_ID_PROPERTY,
        "unsafe run id");
    try {
      assertThrows(IllegalArgumentException.class,
          SaveOperationCompletionEvidence::configuredSaveProofRunId);
    } finally {
      restoreProperty(SaveOperationCompletionEvidence.SAVE_PROOF_RUN_ID_PROPERTY, previousRunId);
    }
  }

  @Test
  public void configuredSaveProofRunIdRejectsSlash() {
    String previousRunId = System.getProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_RUN_ID_PROPERTY);
    System.setProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_RUN_ID_PROPERTY,
        "path/traversal");
    try {
      assertThrows(IllegalArgumentException.class,
          SaveOperationCompletionEvidence::configuredSaveProofRunId);
    } finally {
      restoreProperty(SaveOperationCompletionEvidence.SAVE_PROOF_RUN_ID_PROPERTY, previousRunId);
    }
  }

  // ── configuredSaveProofArtifact ───────────────────────────────────

  @Test
  public void configuredSaveProofArtifactReturnsDefaultUnderProofRoot() throws IOException {
    String previousEvidencePath = System.getProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_EVIDENCE_PATH_PROPERTY);
    System.clearProperty(SaveOperationCompletionEvidence.SAVE_PROOF_EVIDENCE_PATH_PROPERTY);
    try {
      Path proofRoot = tmp.newFolder("proof-root").toPath().toRealPath();
      Path artifact = SaveOperationCompletionEvidence.configuredSaveProofArtifact(proofRoot);
      assertEquals(SaveOperationCompletionEvidence.SAVE_PROOF_ARTIFACT,
          artifact.getFileName().toString());
      assertTrue(artifact.startsWith(proofRoot));
    } finally {
      restoreProperty(SaveOperationCompletionEvidence.SAVE_PROOF_EVIDENCE_PATH_PROPERTY,
          previousEvidencePath);
    }
  }

  @Test
  public void configuredSaveProofArtifactAcceptsValidPathUnderRoot() throws IOException {
    Path proofRoot = tmp.newFolder("proof-root").toPath().toRealPath();
    Path subDir = Files.createDirectories(proofRoot.resolve("sub"));
    Path validArtifact = subDir.resolve(SaveOperationCompletionEvidence.SAVE_PROOF_ARTIFACT);
    String previousEvidencePath = System.getProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_EVIDENCE_PATH_PROPERTY);
    System.setProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_EVIDENCE_PATH_PROPERTY,
        validArtifact.toString());
    try {
      Path artifact = SaveOperationCompletionEvidence.configuredSaveProofArtifact(proofRoot);
      assertEquals(SaveOperationCompletionEvidence.SAVE_PROOF_ARTIFACT,
          artifact.getFileName().toString());
    } finally {
      restoreProperty(SaveOperationCompletionEvidence.SAVE_PROOF_EVIDENCE_PATH_PROPERTY,
          previousEvidencePath);
    }
  }

  @Test
  public void configuredSaveProofArtifactRejectsWrongFilename() throws IOException {
    Path proofRoot = tmp.newFolder("proof-root").toPath().toRealPath();
    Path wrongName = proofRoot.resolve("wrong-artifact-name.json");
    String previousEvidencePath = System.getProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_EVIDENCE_PATH_PROPERTY);
    System.setProperty(
        SaveOperationCompletionEvidence.SAVE_PROOF_EVIDENCE_PATH_PROPERTY,
        wrongName.toString());
    try {
      assertThrows(IllegalArgumentException.class,
          () -> SaveOperationCompletionEvidence.configuredSaveProofArtifact(proofRoot));
    } finally {
      restoreProperty(SaveOperationCompletionEvidence.SAVE_PROOF_EVIDENCE_PATH_PROPERTY,
          previousEvidencePath);
    }
  }

  @Test
  public void configuredSaveProofArtifactRejectsNull() {
    assertThrows(NullPointerException.class,
        () -> SaveOperationCompletionEvidence.configuredSaveProofArtifact(null));
  }

  // ── isSaveActionInvocationProofOnly ───────────────────────────────

  @Test
  public void isSaveActionInvocationProofOnlyDefaultIsFalse() {
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
  public void isSaveActionInvocationProofOnlyTrueWhenSet() {
    String previous = System.getProperty(
        SaveOperationCompletionEvidence.PROOF_ONLY_PROPERTY);
    System.setProperty(SaveOperationCompletionEvidence.PROOF_ONLY_PROPERTY, "true");
    try {
      assertTrue(SaveOperationCompletionEvidence.isSaveActionInvocationProofOnly());
    } finally {
      restoreProperty(SaveOperationCompletionEvidence.PROOF_ONLY_PROPERTY, previous);
    }
  }

  // ── escapeJson delegation ─────────────────────────────────────────

  @Test
  public void escapeJsonDelegatesToEvidenceJsonWriter() {
    String input = "back\\slash\nnew\"quote";
    assertEquals(
        EvidenceJsonWriter.escapeJson(input),
        SaveOperationCompletionEvidence.escapeJson(input));
  }

  // ── Constants are consistent ──────────────────────────────────────

  @Test
  public void saveProofArtifactConstantMatchesExpectedFilename() {
    assertEquals("robot-save-menu-dialog-write-readback-proof.json",
        SaveOperationCompletionEvidence.SAVE_PROOF_ARTIFACT);
  }

  @Test
  public void saveProofSchemaVersionStartsWithEatme() {
    assertTrue(SaveOperationCompletionEvidence.SAVE_PROOF_SCHEMA_VERSION
        .startsWith("eatme."));
  }

  @Test
  public void evidenceDirPropertyHasEatmePrefix() {
    assertTrue(SaveOperationCompletionEvidence.EVIDENCE_DIR_PROPERTY
        .startsWith("org.alice.eatme."));
  }

  @Test
  public void saveProofMarkerIsNonEmpty() {
    assertNotNull(SaveOperationCompletionEvidence.SAVE_PROOF_MARKER);
    assertFalse(SaveOperationCompletionEvidence.SAVE_PROOF_MARKER.isEmpty());
  }

  // ── writeSaveActionInvocationProof ────────────────────────────────

  @Test
  public void writeSaveActionInvocationProofCreatesArtifact() throws IOException {
    Path evidenceDir = tmp.newFolder("evidence").toPath();
    Path artifact = SaveOperationCompletionEvidence.writeSaveActionInvocationProof(
        evidenceDir, "org.alice.test.Op", "a3p",
        true, true);
    assertTrue(Files.exists(artifact));
    assertTrue(Files.size(artifact) > 0);
    String json = Files.readString(artifact);
    assertTrue(json, json.contains("\"schema_version\":"));
    assertTrue(json, json.contains("\"status\": \"action_invoked\""));
  }

  @Test
  public void writeSaveActionInvocationProofWithTriggerCreatesArtifact() throws IOException {
    Path evidenceDir = tmp.newFolder("evidence").toPath();
    SaveOperationCompletionEvidence.InvocationTrigger trigger =
        createTrigger(
            "org.lgna.croquet.triggers.ActionEventTrigger",
            "org.lgna.croquet.views.MenuItem",
            "javax.swing.JMenuItem");
    Path artifact = SaveOperationCompletionEvidence.writeSaveActionInvocationProof(
        evidenceDir, "org.alice.test.Op", "a3p",
        true, true, trigger);
    assertTrue(Files.exists(artifact));
    String json = Files.readString(artifact);
    assertTrue(json, json.contains("\"status\": \"menu_item_dispatched\""));
    assertTrue(json, json.contains("\"menu_item_dispatch\": true"));
  }

  @Test
  public void writeSaveActionInvocationProofRejectsNullEvidenceDir() {
    assertThrows(NullPointerException.class,
        () -> SaveOperationCompletionEvidence.writeSaveActionInvocationProof(
            null, "org.alice.test.Op", "a3p", true, true));
  }

  // ── write and writeDialogControlTarget ────────────────────────────

  @Test
  public void writeCreatesMainAndDialogArtifacts() throws IOException {
    Path testDir = tmp.newFolder("write-test").toPath();
    Path evidenceDir = Files.createDirectories(testDir.resolve("evidence"));
    File savedFile = Files.writeString(testDir.resolve("test.a3p"), "project").toFile();
    SaveOperationFlow.Result result =
        new SaveOperationFlow.Result(true, false, 1, 1, savedFile);

    Path artifact = SaveOperationCompletionEvidence.write(
        evidenceDir, "org.alice.test.Op", "a3p", result);

    assertTrue(Files.exists(artifact));
    assertTrue(Files.exists(evidenceDir.resolve(
        SaveOperationCompletionEvidence.DIALOG_CONTROL_ARTIFACT)));
  }

  @Test
  public void writeDialogControlTargetCreatesArtifact() throws IOException {
    Path evidenceDir = tmp.newFolder("dialog-evidence").toPath();
    SaveOperationFlow.Result result =
        new SaveOperationFlow.Result(true, false, 1, 1, null);

    Path artifact = SaveOperationCompletionEvidence.writeDialogControlTarget(
        evidenceDir, "org.alice.test.Op", "a3p", result);

    assertTrue(Files.exists(artifact));
    String json = Files.readString(artifact);
    assertTrue(json, json.contains("\"schema_version\": \"eatme.alice-desktop-save-dialog-control-target/v1\""));
  }

  @Test
  public void writeRejectsNullResult() throws IOException {
    Path evidenceDir = tmp.newFolder("null-result").toPath();
    assertThrows(NullPointerException.class,
        () -> SaveOperationCompletionEvidence.write(
            evidenceDir, "org.alice.test.Op", "a3p", null));
  }

  @Test
  public void writeRejectsNullEvidenceDir() {
    SaveOperationFlow.Result result =
        new SaveOperationFlow.Result(false, true, 0, 0, null);
    assertThrows(NullPointerException.class,
        () -> SaveOperationCompletionEvidence.write(
            null, "org.alice.test.Op", "a3p", result));
  }

  // ── record opt-in behavior ────────────────────────────────────────

  @Test
  public void recordIgnoresNullResult() {
    String previousEvidenceDir = System.getProperty(
        SaveOperationCompletionEvidence.EVIDENCE_DIR_PROPERTY);
    System.setProperty(SaveOperationCompletionEvidence.EVIDENCE_DIR_PROPERTY, "/tmp/test");
    try {
      // Should not throw
      SaveOperationCompletionEvidence.record("org.test.Op", "a3p", null);
    } finally {
      restoreProperty(SaveOperationCompletionEvidence.EVIDENCE_DIR_PROPERTY, previousEvidenceDir);
    }
  }

  @Test
  public void recordIgnoresBlankEvidenceDir() {
    String previousEvidenceDir = System.getProperty(
        SaveOperationCompletionEvidence.EVIDENCE_DIR_PROPERTY);
    System.setProperty(SaveOperationCompletionEvidence.EVIDENCE_DIR_PROPERTY, "  ");
    try {
      SaveOperationCompletionEvidence.record("org.test.Op", "a3p",
          new SaveOperationFlow.Result(false, true, 0, 0, null));
    } finally {
      restoreProperty(SaveOperationCompletionEvidence.EVIDENCE_DIR_PROPERTY, previousEvidenceDir);
    }
  }

  // ── Helpers ───────────────────────────────────────────────────────

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
          SaveOperationCompletionEvidence.InvocationTrigger.class.getDeclaredConstructor(
              String.class, String.class, String.class);
      ctor.setAccessible(true);
      return ctor.newInstance(triggerClass, viewControllerClass, awtSourceClass);
    } catch (Exception e) {
      throw new RuntimeException("Failed to create InvocationTrigger via reflection", e);
    }
  }
}
