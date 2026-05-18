package org.alice.ide.croquet.models.projecturi;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link EvidenceFileOperations#proofRelativePath} and
 * deeper coverage of {@code redactedSavedPath}, path traversal guards,
 * and edge-case file-system operations.
 */
public class EvidenceFileOperationsProofPathTest {

  @Rule
  public final TemporaryFolder tmp = new TemporaryFolder();

  // ── proofRelativePath ─────────────────────────────────────────────

  @Test
  public void proofRelativePathReturnsNullForNullPath() throws IOException {
    Path proofRoot = tmp.newFolder("proof-root").toPath().toRealPath();
    assertNull(EvidenceFileOperations.proofRelativePath(null, proofRoot));
  }

  @Test
  public void proofRelativePathReturnsRelativeForChildPath() throws IOException {
    Path proofRoot = tmp.newFolder("proof-root").toPath().toRealPath();
    Path child = proofRoot.resolve("sub").resolve("artifact.json");
    String result = EvidenceFileOperations.proofRelativePath(child, proofRoot);
    assertEquals("sub/artifact.json", result);
  }

  @Test
  public void proofRelativePathReturnsOutsideMarkerForEscapedPath() throws IOException {
    Path proofRoot = tmp.newFolder("proof-root").toPath().toRealPath();
    Path outside = Path.of("/tmp/outside/artifact.json");
    String result = EvidenceFileOperations.proofRelativePath(outside, proofRoot);
    assertEquals("[outside-proof-root]", result);
  }

  @Test
  public void proofRelativePathReturnsEmptyForProofRootItself() throws IOException {
    Path proofRoot = tmp.newFolder("proof-root").toPath().toRealPath();
    String result = EvidenceFileOperations.proofRelativePath(proofRoot, proofRoot);
    assertEquals("", result);
  }

  @Test
  public void proofRelativePathHandlesDeepNesting() throws IOException {
    Path proofRoot = tmp.newFolder("proof-root").toPath().toRealPath();
    Path deep = proofRoot.resolve("a").resolve("b").resolve("c").resolve("file.json");
    String result = EvidenceFileOperations.proofRelativePath(deep, proofRoot);
    assertEquals("a/b/c/file.json", result);
  }

  @Test
  public void proofRelativePathNormalizesTraversalBeforeComparison() throws IOException {
    Path proofRoot = tmp.newFolder("proof-root").toPath().toRealPath();
    Path traversal = proofRoot.resolve("sub").resolve("..").resolve("other").resolve("file.json");
    String result = EvidenceFileOperations.proofRelativePath(traversal, proofRoot);
    assertEquals("other/file.json", result);
  }

  @Test
  public void proofRelativePathRejectsTraversalBeyondRoot() throws IOException {
    Path proofRoot = tmp.newFolder("proof-root").toPath().toRealPath();
    Path traversal = proofRoot.resolve("..").resolve("escape").resolve("file.json");
    String result = EvidenceFileOperations.proofRelativePath(traversal, proofRoot);
    assertEquals("[outside-proof-root]", result);
  }

  @Test
  public void proofRelativePathUsesForwardSlashSeparator() throws IOException {
    Path proofRoot = tmp.newFolder("proof-root").toPath().toRealPath();
    Path child = proofRoot.resolve("sub-dir").resolve("nested-dir").resolve("file.json");
    String result = EvidenceFileOperations.proofRelativePath(child, proofRoot);
    assertFalse("Should use forward slashes", result.contains("\\"));
    assertEquals("sub-dir/nested-dir/file.json", result);
  }

  // ── redactedSavedPath deep edge cases ─────────────────────────────

  @Test
  public void redactedSavedPathHandlesRootOnlyPath() {
    Path root = Path.of("/");
    String result = EvidenceFileOperations.redactedSavedPath(root);
    assertNotNull(result);
  }

  @Test
  public void redactedSavedPathHandlesRelativePathWithDots() {
    String result = EvidenceFileOperations.redactedSavedPath(
        Path.of("../relative/../path.a3p"));
    assertNotNull(result);
    // Relative path: normalize may produce ../path.a3p
    assertTrue(result, result.contains("path.a3p"));
  }

  @Test
  public void redactedSavedPathHandlesSingleFileName() {
    String result = EvidenceFileOperations.redactedSavedPath(Path.of("file.a3p"));
    assertEquals("file.a3p", result);
  }

  @Test
  public void redactedSavedFilePathHandlesRelativeFile() {
    String result = EvidenceFileOperations.redactedSavedFilePath(
        new File("relative/path/file.a3p"));
    assertNotNull(result);
    assertTrue(result, result.contains("file.a3p"));
  }

  // ── artifactPath edge cases ───────────────────────────────────────

  @Test
  public void artifactPathAcceptsSimpleFilename() throws IOException {
    Path evidenceDir = tmp.newFolder("evidence").toPath();
    Path artifact = EvidenceFileOperations.artifactPath(evidenceDir, "result.json");
    assertEquals(evidenceDir.resolve("result.json").normalize(), artifact);
  }

  @Test
  public void artifactPathRejectsDoubleTraversal() throws IOException {
    Path evidenceDir = tmp.newFolder("evidence").toPath();
    assertThrows(IllegalArgumentException.class,
        () -> EvidenceFileOperations.artifactPath(evidenceDir, "../../etc/shadow"));
  }

  @Test
  public void artifactPathRejectsSingleTraversal() throws IOException {
    Path evidenceDir = tmp.newFolder("evidence").toPath();
    assertThrows(IllegalArgumentException.class,
        () -> EvidenceFileOperations.artifactPath(evidenceDir, "../outside.json"));
  }

  @Test
  public void artifactPathAcceptsSubDirectory() throws IOException {
    Path evidenceDir = tmp.newFolder("evidence").toPath();
    Path artifact = EvidenceFileOperations.artifactPath(evidenceDir, "sub/result.json");
    assertTrue(artifact.startsWith(evidenceDir.normalize()));
  }

  // ── regularFileState edge cases ───────────────────────────────────

  @Test
  public void regularFileStateForEmptyTempFile() throws IOException {
    File empty = tmp.newFile("empty.json");
    EvidenceFileOperations.RegularFileState state =
        EvidenceFileOperations.regularFileState(empty.toPath());
    assertTrue(state.exists());
    assertEquals(0, state.sizeBytes());
    assertFalse(state.nonEmpty());
  }

  @Test
  public void regularFileStateForNonEmptyFile() throws IOException {
    File f = tmp.newFile("data.json");
    Files.writeString(f.toPath(), "{\"key\":\"value\"}");
    EvidenceFileOperations.RegularFileState state =
        EvidenceFileOperations.regularFileState(f.toPath());
    assertTrue(state.exists());
    assertTrue(state.sizeBytes() > 0);
    assertTrue(state.nonEmpty());
  }

  @Test
  public void regularFileStateForSymlinkReturnsExistsIfTarget() throws IOException {
    File target = tmp.newFile("target.json");
    Files.writeString(target.toPath(), "content");
    Path symlink = tmp.getRoot().toPath().resolve("link.json");
    try {
      Files.createSymbolicLink(symlink, target.toPath());
    } catch (IOException | UnsupportedOperationException e) {
      return;  // Symlinks not supported
    }
    EvidenceFileOperations.RegularFileState state =
        EvidenceFileOperations.regularFileState(symlink);
    assertTrue(state.exists());
    assertTrue(state.nonEmpty());
  }

  // ── requireNonEmptyRegularFile edge cases ─────────────────────────

  @Test
  public void requireNonEmptyRegularFileThrowsForDirectory() throws IOException {
    File dir = tmp.newFolder("not-a-file");
    IOException thrown = assertThrows(IOException.class,
        () -> EvidenceFileOperations.requireNonEmptyRegularFile(
            dir.toPath(), "Expected file not dir"));
    assertTrue(thrown.getMessage(), thrown.getMessage().contains("Expected file not dir"));
  }

  @Test
  public void requireNonEmptyRegularFilePassesForLargeFile() throws IOException {
    File f = tmp.newFile("large.json");
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 1000; i++) {
      sb.append("{\"index\":").append(i).append("}\n");
    }
    Files.writeString(f.toPath(), sb.toString());
    // Should not throw
    EvidenceFileOperations.requireNonEmptyRegularFile(f.toPath(), "Must exist");
  }

  // ── canonicalDirectory edge cases ─────────────────────────────────

  @Test
  public void canonicalDirectoryCreatesDeepNesting() throws IOException {
    Path deep = tmp.getRoot().toPath().resolve("a").resolve("b").resolve("c").resolve("d");
    Path result = EvidenceFileOperations.canonicalDirectory(deep, "deep dir");
    assertTrue(Files.isDirectory(result));
    assertEquals(result, result.toRealPath());
  }

  @Test
  public void canonicalDirectoryIdempotent() throws IOException {
    Path dir = tmp.newFolder("existing-dir").toPath();
    Path result1 = EvidenceFileOperations.canonicalDirectory(dir, "first");
    Path result2 = EvidenceFileOperations.canonicalDirectory(dir, "second");
    assertEquals(result1, result2);
  }

  // ── requirePathUnderProofRoot edge cases ──────────────────────────

  @Test
  public void requirePathUnderProofRootSucceedsForExactRoot() throws IOException {
    Path proofRoot = tmp.newFolder("proof-root").toPath();
    // Exact root path should succeed
    EvidenceFileOperations.requirePathUnderProofRoot(proofRoot, proofRoot, "test");
  }

  @Test
  public void requirePathUnderProofRootNormalizesBeforeCheck() throws IOException {
    Path proofRoot = tmp.newFolder("proof-root").toPath();
    Path withDots = proofRoot.resolve("sub").resolve("..").resolve("sub").resolve("file.json");
    // Should not throw — normalized path is still under root
    EvidenceFileOperations.requirePathUnderProofRoot(withDots, proofRoot, "test");
  }

  @Test
  public void requirePathUnderProofRootRejectsParentTraversal() throws IOException {
    Path proofRoot = tmp.newFolder("proof-root").toPath();
    Path escaped = proofRoot.resolve("..").resolve("outside");
    assertThrows(IllegalArgumentException.class,
        () -> EvidenceFileOperations.requirePathUnderProofRoot(escaped, proofRoot, "test"));
  }

  // ── canonicalDirectoryUnderProofRoot edge cases ───────────────────

  @Test
  public void canonicalDirectoryUnderProofRootCreatesMultipleLevels() throws IOException {
    Path proofRoot = tmp.newFolder("proof-root").toPath().toRealPath();
    Path nested = proofRoot.resolve("level1").resolve("level2").resolve("level3");
    Path result = EvidenceFileOperations.canonicalDirectoryUnderProofRoot(
        nested, proofRoot, "test");
    assertTrue(Files.isDirectory(result));
    assertTrue(result.startsWith(proofRoot));
  }

  @Test
  public void canonicalDirectoryUnderProofRootIdempotentForExistingDir() throws IOException {
    Path proofRoot = tmp.newFolder("proof-root").toPath().toRealPath();
    Path sub = Files.createDirectories(proofRoot.resolve("existing-sub"));
    Path result = EvidenceFileOperations.canonicalDirectoryUnderProofRoot(
        sub, proofRoot, "test");
    assertTrue(Files.isDirectory(result));
  }

  // ── ensureDirectoryWithoutFollowingSymlink edge cases ──────────────

  @Test
  public void ensureDirectoryWithoutFollowingSymlinkCreatesNewDir() throws IOException {
    Path newDir = tmp.getRoot().toPath().resolve("brand-new-dir");
    EvidenceFileOperations.ensureDirectoryWithoutFollowingSymlink(newDir, "test");
    assertTrue(Files.isDirectory(newDir));
    assertFalse(Files.isSymbolicLink(newDir));
  }

  @Test
  public void ensureDirectoryWithoutFollowingSymlinkAcceptsExistingDir() throws IOException {
    Path dir = tmp.newFolder("existing").toPath();
    // Should not throw
    EvidenceFileOperations.ensureDirectoryWithoutFollowingSymlink(dir, "existing");
  }

  // ── RegularFileState record behavior ──────────────────────────────

  @Test
  public void regularFileStateMissingIsConsistent() {
    EvidenceFileOperations.RegularFileState missing = EvidenceFileOperations.RegularFileState.MISSING;
    assertFalse(missing.exists());
    assertEquals(0, missing.sizeBytes());
    assertFalse(missing.nonEmpty());
  }

  @Test
  public void regularFileStateNegativeSizeIsNotNonEmpty() {
    EvidenceFileOperations.RegularFileState state =
        new EvidenceFileOperations.RegularFileState(true, -1);
    assertTrue(state.exists());
    assertFalse("Negative size should not be considered nonEmpty", state.nonEmpty());
  }

  @Test
  public void regularFileStateExistsWithOneByteIsNonEmpty() {
    EvidenceFileOperations.RegularFileState state =
        new EvidenceFileOperations.RegularFileState(true, 1);
    assertTrue(state.exists());
    assertTrue(state.nonEmpty());
  }

  @Test
  public void regularFileStateLargeSizeIsNonEmpty() {
    EvidenceFileOperations.RegularFileState state =
        new EvidenceFileOperations.RegularFileState(true, 10_000_000L);
    assertTrue(state.nonEmpty());
  }

  @Test
  public void regularFileStateNotExistsWithSizeIsNotNonEmpty() {
    EvidenceFileOperations.RegularFileState state =
        new EvidenceFileOperations.RegularFileState(false, 500);
    assertFalse(state.exists());
    assertFalse(state.nonEmpty());
  }

  // ── Integration: proofRelativePath with canonicalDirectory ────────

  @Test
  public void proofRelativePathAfterCanonicalDirectoryCreation() throws IOException {
    Path proofRoot = tmp.newFolder("proof-root").toPath().toRealPath();
    Path subDir = proofRoot.resolve("evidence").resolve("sub");
    Path canonical = EvidenceFileOperations.canonicalDirectory(subDir, "test");
    Path artifact = canonical.resolve("result.json");
    String relative = EvidenceFileOperations.proofRelativePath(artifact, proofRoot);
    assertEquals("evidence/sub/result.json", relative);
  }

  // ── Integration: regularFileState lifecycle ────────────────────────

  @Test
  public void regularFileStateTracksFileCreationAndDeletion() throws IOException {
    Path file = tmp.getRoot().toPath().resolve("lifecycle.json");
    assertFalse(EvidenceFileOperations.regularFileState(file).exists());

    Files.writeString(file, "content");
    EvidenceFileOperations.RegularFileState afterWrite =
        EvidenceFileOperations.regularFileState(file);
    assertTrue(afterWrite.exists());
    assertTrue(afterWrite.nonEmpty());

    Files.delete(file);
    assertFalse(EvidenceFileOperations.regularFileState(file).exists());
  }
}
