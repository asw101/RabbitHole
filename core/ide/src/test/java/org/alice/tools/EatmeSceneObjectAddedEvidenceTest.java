package org.alice.tools;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

public class EatmeSceneObjectAddedEvidenceTest {
  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  // --- Happy path: writes correct JSON artifact ---

  @Test
  public void writesSceneObjectAddedArtifactWithExpectedSchema() throws Exception {
    Path evidenceDir = temporaryFolder.newFolder("evidence").toPath();

    Path artifact = EatmeSceneObjectAddedEvidence.writeObjectAdded(evidenceDir, "SBiped", 5);

    assertEquals(evidenceDir.resolve("scene-object-added.json"), artifact);
    assertTrue(Files.size(artifact) > 0);
    String json = Files.readString(artifact);
    assertTrue(json, json.contains("\"schema_version\": \"eatme.alice-scene-object-added/v1\""));
    assertTrue(json, json.contains("\"object_class_name\": \"SBiped\""));
    assertTrue(json, json.contains("\"scene_field_count_after\": 5"));
    assertTrue(json, json.contains("\"timestamp\":"));
  }

  @Test
  public void artifactContainsAllRequiredFields() throws Exception {
    Path evidenceDir = temporaryFolder.newFolder("all-fields-evidence").toPath();

    Path artifact = EatmeSceneObjectAddedEvidence.writeObjectAdded(evidenceDir, "SFlyer", 3);

    String json = Files.readString(artifact);
    assertTrue(json, json.contains("\"schema_version\":"));
    assertTrue(json, json.contains("\"timestamp\":"));
    assertTrue(json, json.contains("\"object_class_name\":"));
    assertTrue(json, json.contains("\"scene_field_count_after\":"));
  }

  @Test
  public void timestampIsRecentEpochMillis() throws Exception {
    Path evidenceDir = temporaryFolder.newFolder("timestamp-evidence").toPath();
    long before = System.currentTimeMillis();

    EatmeSceneObjectAddedEvidence.writeObjectAdded(evidenceDir, "SBiped", 1);

    long after = System.currentTimeMillis();
    String json = Files.readString(evidenceDir.resolve("scene-object-added.json"));
    // Extract timestamp value — find "timestamp": <number>
    int idx = json.indexOf("\"timestamp\":");
    assertTrue("timestamp field must be present", idx >= 0);
    String rest = json.substring(idx + "\"timestamp\":".length()).trim();
    // Parse until non-digit
    StringBuilder digits = new StringBuilder();
    for (char c : rest.toCharArray()) {
      if (Character.isDigit(c)) {
        digits.append(c);
      } else {
        break;
      }
    }
    long timestamp = Long.parseLong(digits.toString());
    assertTrue("timestamp should be >= test start", timestamp >= before);
    assertTrue("timestamp should be <= test end", timestamp <= after);
  }

  // --- JSON escaping of untrusted object class names ---

  @Test
  public void escapesUntrustedObjectClassNameInJson() throws Exception {
    Path evidenceDir = temporaryFolder.newFolder("escaping-evidence").toPath();

    Path artifact = EatmeSceneObjectAddedEvidence.writeObjectAdded(
        evidenceDir, "SBiped\"With\\Special\tChars\u0001", 2);

    String json = Files.readString(artifact);
    assertTrue(json, json.contains("\"object_class_name\": \"SBiped\\\"With\\\\Special\\tChars\\u0001\""));
  }

  // --- Null/empty handling ---

  @Test
  public void writesEmptyStringForNullObjectClassName() throws Exception {
    Path evidenceDir = temporaryFolder.newFolder("null-class-evidence").toPath();

    Path artifact = EatmeSceneObjectAddedEvidence.writeObjectAdded(evidenceDir, null, 1);

    String json = Files.readString(artifact);
    assertTrue(json, json.contains("\"object_class_name\": \"\""));
  }

  @Test
  public void writesZeroFieldCountWhenProvided() throws Exception {
    Path evidenceDir = temporaryFolder.newFolder("zero-count-evidence").toPath();

    Path artifact = EatmeSceneObjectAddedEvidence.writeObjectAdded(evidenceDir, "SBiped", 0);

    String json = Files.readString(artifact);
    assertTrue(json, json.contains("\"scene_field_count_after\": 0"));
  }

  // --- Path traversal rejection (reuses artifactPath from EatmeRunWindowEvidence) ---

  @Test(expected = IllegalArgumentException.class)
  public void rejectsParentTraversalInArtifactPath() {
    EatmeRunWindowEvidence.artifactPath(temporaryFolder.getRoot().toPath(), "../scene-object-added.json");
  }

  @Test(expected = IllegalArgumentException.class)
  public void rejectsNestedArtifactPath() {
    EatmeRunWindowEvidence.artifactPath(temporaryFolder.getRoot().toPath(), "nested/scene-object-added.json");
  }

  @Test(expected = IllegalArgumentException.class)
  public void rejectsAbsoluteArtifactPath() {
    EatmeRunWindowEvidence.artifactPath(temporaryFolder.getRoot().toPath(), "/tmp/scene-object-added.json");
  }

  // --- Missing evidence directory ---

  @Test
  public void rejectsMissingEvidenceDirectoryWithoutCreatingIt() throws Exception {
    Path missingDir = temporaryFolder.getRoot().toPath().resolve("missing-evidence");

    try {
      EatmeSceneObjectAddedEvidence.writeObjectAdded(missingDir, "SBiped", 1);
      fail("missing evidence directory should be rejected");
    } catch (IOException expected) {
      assertTrue(Files.notExists(missingDir));
    }
  }

  // --- Symlink rejection ---

  @Test
  public void refusesToFollowArtifactSymlinkOutsideEvidenceDirectory() throws Exception {
    Path evidenceDir = temporaryFolder.newFolder("symlink-evidence").toPath();
    Path outsideArtifact = temporaryFolder.newFile("outside-scene-object-added.json").toPath();
    Files.writeString(outsideArtifact, "outside");
    Files.createSymbolicLink(
        evidenceDir.resolve(EatmeSceneObjectAddedEvidence.SCENE_OBJECT_ADDED_ARTIFACT),
        outsideArtifact);

    try {
      EatmeSceneObjectAddedEvidence.writeObjectAdded(evidenceDir, "SBiped", 1);
      fail("artifact symlink should be rejected");
    } catch (IOException expected) {
      assertEquals("outside", Files.readString(outsideArtifact));
    }
  }

  @Test
  public void rejectsSymlinkEvidenceDirectoryWithoutWritingOutsideScratchRoot() throws Exception {
    Path scratchRoot = temporaryFolder.newFolder("scratch-root").toPath();
    Path outsideTarget = temporaryFolder.newFolder("outside-evidence-target").toPath();
    Path symlinkDir = scratchRoot.resolve("linked-evidence");
    Files.createSymbolicLink(symlinkDir, outsideTarget);

    try {
      EatmeSceneObjectAddedEvidence.writeObjectAdded(symlinkDir, "SBiped", 1);
      fail("symlink evidence directory should be rejected");
    } catch (IOException expected) {
      assertTrue(Files.notExists(outsideTarget.resolve(EatmeSceneObjectAddedEvidence.SCENE_OBJECT_ADDED_ARTIFACT)));
    }
  }

  // --- Hard-link replacement ---

  @Test
  public void replacesHardLinkedArtifactWithoutMutatingLinkedTarget() throws Exception {
    Path evidenceDir = temporaryFolder.newFolder("hardlink-evidence").toPath();
    Path outsideArtifact = temporaryFolder.newFile("outside-hardlinked-scene-object-added.json").toPath();
    Files.writeString(outsideArtifact, "outside");
    Path hardLinkedArtifact = evidenceDir.resolve(EatmeSceneObjectAddedEvidence.SCENE_OBJECT_ADDED_ARTIFACT);
    try {
      Files.createLink(hardLinkedArtifact, outsideArtifact);
    } catch (IOException | SecurityException | UnsupportedOperationException ex) {
      assumeTrue("hard links are unavailable in this test environment", false);
    }

    EatmeSceneObjectAddedEvidence.writeObjectAdded(evidenceDir, "SBiped", 1);

    assertEquals("outside", Files.readString(outsideArtifact));
    String json = Files.readString(hardLinkedArtifact);
    assertTrue(json, json.contains("\"schema_version\": \"eatme.alice-scene-object-added/v1\""));
  }

  // --- System-property-gated public API ---

  @Test
  public void recordSceneObjectAddedIsNoopWhenEvidenceDirectoryIsUnconfigured() {
    String previous = System.getProperty(EatmeSceneObjectAddedEvidence.EVIDENCE_DIR_PROPERTY);
    try {
      System.clearProperty(EatmeSceneObjectAddedEvidence.EVIDENCE_DIR_PROPERTY);

      // Should complete without throwing — no evidence dir means silent no-op
      EatmeSceneObjectAddedEvidence.recordSceneObjectAdded(null, 0);
    } finally {
      restoreProperty(EatmeSceneObjectAddedEvidence.EVIDENCE_DIR_PROPERTY, previous);
    }
  }

  @Test
  public void recordSceneObjectAddedSurfacesInvalidConfiguredPath() {
    String previous = System.getProperty(EatmeSceneObjectAddedEvidence.EVIDENCE_DIR_PROPERTY);
    try {
      System.setProperty(EatmeSceneObjectAddedEvidence.EVIDENCE_DIR_PROPERTY, "bad\0path");

      try {
        EatmeSceneObjectAddedEvidence.recordSceneObjectAdded("SBiped", 1);
        fail("invalid configured evidence path should be surfaced");
      } catch (IllegalStateException expected) {
        assertTrue(expected.getMessage().contains("Scene-object-added evidence write failed"));
      }
    } finally {
      restoreProperty(EatmeSceneObjectAddedEvidence.EVIDENCE_DIR_PROPERTY, previous);
    }
  }

  @Test
  public void recordSceneObjectAddedWritesConfiguredEvidenceArtifact() throws Exception {
    Path evidenceDir = temporaryFolder.newFolder("configured-evidence").toPath();
    String previous = System.getProperty(EatmeSceneObjectAddedEvidence.EVIDENCE_DIR_PROPERTY);
    try {
      System.setProperty(EatmeSceneObjectAddedEvidence.EVIDENCE_DIR_PROPERTY, evidenceDir.toString());

      EatmeSceneObjectAddedEvidence.recordSceneObjectAdded("SBiped", 3);

      Path artifact = evidenceDir.resolve(EatmeSceneObjectAddedEvidence.SCENE_OBJECT_ADDED_ARTIFACT);
      assertTrue(Files.isRegularFile(artifact));
      String json = Files.readString(artifact);
      assertTrue(json, json.contains("\"schema_version\": \"eatme.alice-scene-object-added/v1\""));
      assertTrue(json, json.contains("\"object_class_name\": \"SBiped\""));
      assertTrue(json, json.contains("\"scene_field_count_after\": 3"));
    } finally {
      restoreProperty(EatmeSceneObjectAddedEvidence.EVIDENCE_DIR_PROPERTY, previous);
    }
  }

  // --- Post-write verification (artifact exists and non-empty) ---

  @Test
  public void postWriteVerificationEnsuresArtifactIsNonEmpty() throws Exception {
    Path evidenceDir = temporaryFolder.newFolder("verify-evidence").toPath();

    Path artifact = EatmeSceneObjectAddedEvidence.writeObjectAdded(evidenceDir, "SBiped", 1);

    assertTrue("artifact must exist after write", Files.isRegularFile(artifact));
    assertTrue("artifact must be non-empty", Files.size(artifact) > 0);
  }

  // --- Overwrite semantics: latest write wins ---

  @Test
  public void overwritesPreviousArtifactWithLatestObjectAdded() throws Exception {
    Path evidenceDir = temporaryFolder.newFolder("overwrite-evidence").toPath();

    EatmeSceneObjectAddedEvidence.writeObjectAdded(evidenceDir, "SBiped", 2);
    Path artifact = EatmeSceneObjectAddedEvidence.writeObjectAdded(evidenceDir, "SFlyer", 3);

    String json = Files.readString(artifact);
    assertTrue(json, json.contains("\"object_class_name\": \"SFlyer\""));
    assertTrue(json, json.contains("\"scene_field_count_after\": 3"));
    assertTrue("should not contain old class", !json.contains("\"object_class_name\": \"SBiped\""));
  }

  private static void restoreProperty(String key, String previousValue) {
    if (previousValue == null) {
      System.clearProperty(key);
    } else {
      System.setProperty(key, previousValue);
    }
  }
}
