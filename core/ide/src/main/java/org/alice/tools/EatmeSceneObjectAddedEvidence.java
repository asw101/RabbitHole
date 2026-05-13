package org.alice.tools;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

public final class EatmeSceneObjectAddedEvidence {
  public static final String EVIDENCE_DIR_PROPERTY = "org.alice.eatme.evidenceDir";
  public static final String SCENE_OBJECT_ADDED_ARTIFACT = "scene-object-added.json";
  private static final String SCHEMA_VERSION = "eatme.alice-scene-object-added/v1";

  private EatmeSceneObjectAddedEvidence() {
  }

  public static void recordSceneObjectAdded(String objectClassName, int sceneFieldCountAfter) {
    String evidenceDir = System.getProperty(EVIDENCE_DIR_PROPERTY);
    if (evidenceDir == null || evidenceDir.isBlank()) {
      return;
    }
    try {
      writeObjectAdded(Path.of(evidenceDir), objectClassName, sceneFieldCountAfter);
    } catch (IOException | SecurityException | IllegalArgumentException ex) {
      throw new IllegalStateException("Scene-object-added evidence write failed: " + evidenceDir, ex);
    }
  }

  static Path writeObjectAdded(Path evidenceDir, String objectClassName, int sceneFieldCountAfter) throws IOException {
    Path evidenceRoot = validateEvidenceDir(evidenceDir);
    Path artifact = EatmeRunWindowEvidence.artifactPath(evidenceRoot, SCENE_OBJECT_ADDED_ARTIFACT);
    String escapedClassName = EatmeRunWindowEvidence.escapeJson(objectClassName);
    long timestamp = System.currentTimeMillis();
    String content = "{\n"
        + "  \"schema_version\": \"" + SCHEMA_VERSION + "\",\n"
        + "  \"timestamp\": " + timestamp + ",\n"
        + "  \"object_class_name\": \"" + escapedClassName + "\",\n"
        + "  \"scene_field_count_after\": " + sceneFieldCountAfter + "\n"
        + "}\n";
    writeArtifactAtomically(evidenceRoot, artifact, content);
    BasicFileAttributes postAttrs = Files.readAttributes(artifact, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
    if (!postAttrs.isRegularFile() || postAttrs.size() == 0) {
      throw new IOException("Scene-object-added evidence artifact was not written: " + artifact);
    }
    return artifact;
  }

  private static void writeArtifactAtomically(Path evidenceRoot, Path artifact, String content) throws IOException {
    if (Files.isSymbolicLink(artifact)) {
      throw new IOException("Scene-object-added evidence artifact refuses to overwrite symlink: " + artifact);
    }
    Path tempArtifact = Files.createTempFile(evidenceRoot, SCENE_OBJECT_ADDED_ARTIFACT, ".tmp");
    try {
      Files.writeString(tempArtifact, content, StandardCharsets.UTF_8);
      Files.move(
          tempArtifact,
          artifact,
          StandardCopyOption.ATOMIC_MOVE,
          StandardCopyOption.REPLACE_EXISTING);
    } finally {
      Files.deleteIfExists(tempArtifact);
    }
  }

  private static Path validateEvidenceDir(Path evidenceDir) throws IOException {
    Path evidencePath = evidenceDir.toAbsolutePath().normalize();
    // Single lstat: checks both symlink status and file type in one syscall
    BasicFileAttributes dirAttrs = Files.readAttributes(evidencePath, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
    if (dirAttrs.isSymbolicLink()) {
      throw new IOException("Scene-object-added evidence path must not be a symbolic link: " + evidenceDir);
    }
    if (!dirAttrs.isDirectory()) {
      throw new IOException("Scene-object-added evidence path is not a directory: " + evidenceDir);
    }
    return evidencePath.toRealPath();
  }
}
