package org.alice.ide.croquet.models.projecturi;

import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link EvidenceFileOperations} methods not covered by
 * {@link EvidenceFileOperationsTest}: {@code proofRelativePath},
 * deeper {@code redactedSavedPath} edge cases, path traversal security,
 * and symlink rejection scenarios.
 *
 * <p>JUnit 4, headless, uses real temp directories. A shared parent
 * directory avoids redundant mkdirs system calls; per-test isolation
 * uses an atomic counter rather than UUID.
 */
public class EvidenceFileOperationsProofPathTest {

  private static Path sharedParent;
  private static final AtomicInteger SEQ = new AtomicInteger();

  @BeforeClass
  public static void createSharedParent() throws Exception {
    sharedParent = Files.createDirectories(
        Path.of("target", "evidence-file-operations-proof-path-test",
            UUID.randomUUID().toString()));
  }

  // ── proofRelativePath ────────────────────────────────────────────

  @Test
  public void proofRelativePath_nullPath_returnsNull() throws Exception {
    Path proofRoot = newTestDir().toRealPath();
    assertNull(EvidenceFileOperations.proofRelativePath(null, proofRoot));
  }

  @Test
  public void proofRelativePath_pathInsideProofRoot_returnsRelative() throws Exception {
    Path proofRoot = newTestDir().toRealPath();
    Path nested = proofRoot.resolve("sub").resolve("file.a3p");
    String result = EvidenceFileOperations.proofRelativePath(nested, proofRoot);
    assertEquals("sub/file.a3p", result);
  }

  @Test
  public void proofRelativePath_pathIsProofRoot_returnsEmpty() throws Exception {
    Path proofRoot = newTestDir().toRealPath();
    String result = EvidenceFileOperations.proofRelativePath(proofRoot, proofRoot);
    assertEquals("", result);
  }

  @Test
  public void proofRelativePath_pathOutsideProofRoot_returnsOutsideMarker() throws Exception {
    Path proofRoot = newTestDir().toRealPath();
    Path outside = newTestDir().toRealPath();
    String result = EvidenceFileOperations.proofRelativePath(outside, proofRoot);
    assertEquals("[outside-proof-root]", result);
  }

  @Test
  public void proofRelativePath_pathWithDotDot_normalizedBeforeComparison() throws Exception {
    Path proofRoot = newTestDir().toRealPath();
    Path dotDot = proofRoot.resolve("a").resolve("b").resolve("..").resolve("c").resolve("file.a3p");
    String result = EvidenceFileOperations.proofRelativePath(dotDot, proofRoot);
    assertEquals("a/c/file.a3p", result);
  }

  @Test
  public void proofRelativePath_deeplyNestedPath_usesForwardSlashes() throws Exception {
    Path proofRoot = newTestDir().toRealPath();
    Path deep = proofRoot.resolve("a").resolve("b").resolve("c").resolve("d.json");
    String result = EvidenceFileOperations.proofRelativePath(deep, proofRoot);
    assertEquals("a/b/c/d.json", result);
    assertFalse("No backslashes", result.contains("\\"));
  }

  @Test
  public void proofRelativePath_traversalAttempt_returnsOutside() throws Exception {
    Path proofRoot = newTestDir().toRealPath();
    Path traversal = proofRoot.resolve("../../etc/passwd");
    String result = EvidenceFileOperations.proofRelativePath(traversal, proofRoot);
    assertEquals("[outside-proof-root]", result);
  }

  // ── redactedSavedPath deeper edge cases ──────────────────────────

  @Test
  public void redactedSavedPath_relativePath_withDotDot_normalized() {
    String result = EvidenceFileOperations.redactedSavedPath(
        Path.of("some/../other/file.a3p"));
    assertEquals("other/file.a3p", result);
  }

  @Test
  public void redactedSavedPath_absolutePathUnderCwd_relativized() {
    Path cwd = Path.of("").toAbsolutePath().normalize();
    Path underCwd = cwd.resolve("target").resolve("nested").resolve("output.a3p");
    String result = EvidenceFileOperations.redactedSavedPath(underCwd);
    assertEquals("target/nested/output.a3p", result);
  }

  @Test
  public void redactedSavedPath_absolutePathOutsideCwd_redacted() throws Exception {
    Path external = Files.createTempFile("alice-redact-proof-", ".a3p");
    try {
      String result = EvidenceFileOperations.redactedSavedPath(external);
      assertTrue("Starts with [redacted]/", result.startsWith("[redacted]/"));
      assertTrue("Contains filename", result.contains(external.getFileName().toString()));
    } finally {
      Files.deleteIfExists(external);
    }
  }

  @Test
  public void redactedSavedPath_rootPath_redactedWithEmptyFilename() {
    Path root = Path.of("/").toAbsolutePath().normalize();
    String result = EvidenceFileOperations.redactedSavedPath(root);
    assertTrue("Contains [redacted]", result.contains("[redacted]/"));
  }

  @Test
  public void redactedSavedFilePath_delegatesToRedactedSavedPath() throws Exception {
    Path external = Files.createTempFile("alice-delegate-", ".a3p");
    try {
      String fileResult = EvidenceFileOperations.redactedSavedFilePath(external.toFile());
      String pathResult = EvidenceFileOperations.redactedSavedPath(external);
      assertEquals(pathResult, fileResult);
    } finally {
      Files.deleteIfExists(external);
    }
  }

  // ── artifactPath security ────────────────────────────────────────

  @Test
  public void artifactPath_simpleFileName_resolvesCorrectly() throws Exception {
    Path evidenceDir = newTestDir();
    Path artifact = EvidenceFileOperations.artifactPath(evidenceDir, "result.json");
    assertEquals(evidenceDir.resolve("result.json").normalize(), artifact);
  }

  @Test
  public void artifactPath_rejectsDoubleTraversal() throws Exception {
    Path evidenceDir = newTestDir();
    assertThrows(IllegalArgumentException.class,
        () -> EvidenceFileOperations.artifactPath(evidenceDir, "../../etc/passwd"));
  }

  @Test
  public void artifactPath_rejectsSingleTraversal() throws Exception {
    Path evidenceDir = newTestDir();
    assertThrows(IllegalArgumentException.class,
        () -> EvidenceFileOperations.artifactPath(evidenceDir, "../escape.json"));
  }

  @Test
  public void artifactPath_withSubdirectory_resolvesNormally() throws Exception {
    Path evidenceDir = newTestDir();
    Path artifact = EvidenceFileOperations.artifactPath(evidenceDir, "sub/artifact.json");
    assertTrue("Stays under evidence dir",
        artifact.startsWith(evidenceDir.normalize()));
  }

  // ── regularFileState security/edge cases ─────────────────────────

  @Test
  public void regularFileState_forSymlink_treatedAsRegularFile() throws Exception {
    Path testDir = newTestDir().toAbsolutePath();
    Path realFile = Files.writeString(testDir.resolve("real.txt"), "data");
    Path symlink = testDir.resolve("link.txt");
    try {
      Files.createSymbolicLink(symlink, realFile.toAbsolutePath());
    } catch (IOException | SecurityException | UnsupportedOperationException e) {
      Assume.assumeTrue("Symlinks not supported", false);
      return;
    }
    Assume.assumeTrue("Symlink exists", Files.exists(symlink));
    EvidenceFileOperations.RegularFileState state =
        EvidenceFileOperations.regularFileState(symlink);
    assertTrue("Symlink to regular file reports exists", state.exists());
    assertTrue("Symlink to regular file reports size", state.sizeBytes() > 0);
  }

  @Test
  public void regularFileState_forBrokenSymlink_returnsMissing() throws Exception {
    Path testDir = newTestDir().toAbsolutePath();
    Path symlink = testDir.resolve("broken-link.txt");
    try {
      Files.createSymbolicLink(symlink, testDir.resolve("nonexistent.txt").toAbsolutePath());
    } catch (IOException | SecurityException | UnsupportedOperationException e) {
      Assume.assumeTrue("Symlinks not supported", false);
      return;
    }
    EvidenceFileOperations.RegularFileState state =
        EvidenceFileOperations.regularFileState(symlink);
    assertFalse("Broken symlink returns not-exists", state.exists());
  }

  // ── requirePathUnderProofRoot edge cases ─────────────────────────

  @Test
  public void requirePathUnderProofRoot_exactRoot_doesNotThrow() throws Exception {
    Path proofRoot = newTestDir();
    EvidenceFileOperations.requirePathUnderProofRoot(
        proofRoot, proofRoot, "exact root must pass");
  }

  @Test
  public void requirePathUnderProofRoot_dotDotEscape_throws() throws Exception {
    Path proofRoot = newTestDir();
    Path escaped = proofRoot.resolve("sub/../../outside");
    assertThrows(IllegalArgumentException.class,
        () -> EvidenceFileOperations.requirePathUnderProofRoot(
            escaped, proofRoot, "must stay under root"));
  }

  @Test
  public void requirePathUnderProofRoot_siblingDir_throws() throws Exception {
    Path parent = newTestDir();
    Path proofRoot = Files.createDirectory(parent.resolve("root"));
    Path sibling = Files.createDirectory(parent.resolve("sibling"));
    assertThrows(IllegalArgumentException.class,
        () -> EvidenceFileOperations.requirePathUnderProofRoot(
            sibling, proofRoot, "sibling not under root"));
  }

  // ── canonicalDirectory edge cases ────────────────────────────────

  @Test
  public void canonicalDirectory_createsNestedStructure() throws Exception {
    Path testDir = newTestDir();
    Path deep = testDir.resolve("a").resolve("b").resolve("c");
    Path canonical = EvidenceFileOperations.canonicalDirectory(deep, "test");
    assertTrue(Files.isDirectory(canonical));
    assertEquals(canonical, canonical.toRealPath());
  }

  @Test
  public void canonicalDirectory_existingDir_returnsCanonical() throws Exception {
    Path testDir = newTestDir();
    Path canonical = EvidenceFileOperations.canonicalDirectory(testDir, "test");
    assertTrue(Files.isDirectory(canonical));
    assertEquals(canonical, canonical.toRealPath());
  }

  // ── canonicalDirectoryUnderProofRoot edge cases ──────────────────

  @Test
  public void canonicalDirectoryUnderProofRoot_directChild_succeeds() throws Exception {
    Path proofRoot = Files.createDirectories(newTestDir().resolve("proof")).toRealPath();
    Path child = proofRoot.resolve("child");
    Path result = EvidenceFileOperations.canonicalDirectoryUnderProofRoot(
        child, proofRoot, "test");
    assertTrue(Files.isDirectory(result));
    assertTrue(result.startsWith(proofRoot));
  }

  @Test
  public void canonicalDirectoryUnderProofRoot_deepNested_succeeds() throws Exception {
    Path proofRoot = Files.createDirectories(newTestDir().resolve("proof")).toRealPath();
    Path deep = proofRoot.resolve("a").resolve("b").resolve("c");
    Path result = EvidenceFileOperations.canonicalDirectoryUnderProofRoot(
        deep, proofRoot, "test");
    assertTrue(Files.isDirectory(result));
    assertTrue(result.startsWith(proofRoot));
  }

  @Test
  public void canonicalDirectoryUnderProofRoot_proofRootItself_returnsRoot() throws Exception {
    Path proofRoot = Files.createDirectories(newTestDir().resolve("proof")).toRealPath();
    Path result = EvidenceFileOperations.canonicalDirectoryUnderProofRoot(
        proofRoot, proofRoot, "test");
    assertEquals(proofRoot, result);
  }

  // ── ensureDirectoryWithoutFollowingSymlink edge cases ────────────

  @Test
  public void ensureDirectoryWithoutFollowingSymlink_createsNewDir() throws Exception {
    Path testDir = newTestDir();
    Path target = testDir.resolve("brand-new");
    EvidenceFileOperations.ensureDirectoryWithoutFollowingSymlink(target, "test");
    assertTrue(Files.isDirectory(target));
    assertFalse(Files.isSymbolicLink(target));
  }

  @Test
  public void ensureDirectoryWithoutFollowingSymlink_existingDir_passes() throws Exception {
    Path testDir = newTestDir();
    Path existing = Files.createDirectory(testDir.resolve("existing"));
    EvidenceFileOperations.ensureDirectoryWithoutFollowingSymlink(existing, "test");
    assertTrue(Files.isDirectory(existing));
  }

  @Test
  public void ensureDirectoryWithoutFollowingSymlink_symlinkToDir_throws() throws Exception {
    Path testDir = newTestDir();
    Path real = Files.createDirectory(testDir.resolve("real"));
    Path symlink = testDir.resolve("sym");
    try {
      Files.createSymbolicLink(symlink, real);
    } catch (IOException | SecurityException | UnsupportedOperationException e) {
      Assume.assumeTrue("Symlinks not supported", false);
      return;
    }
    assertThrows(IOException.class,
        () -> EvidenceFileOperations.ensureDirectoryWithoutFollowingSymlink(
            symlink, "no symlinks"));
  }

  @Test
  public void ensureDirectoryWithoutFollowingSymlink_regularFile_throws() throws Exception {
    Path testDir = newTestDir();
    Path file = Files.writeString(testDir.resolve("afile.txt"), "data");
    assertThrows(IOException.class,
        () -> EvidenceFileOperations.ensureDirectoryWithoutFollowingSymlink(
            file, "not a directory"));
  }

  // ── requireNonEmptyRegularFile edge cases ────────────────────────

  @Test
  public void requireNonEmptyRegularFile_directoryShouldThrow() throws Exception {
    Path testDir = newTestDir();
    assertThrows(IOException.class,
        () -> EvidenceFileOperations.requireNonEmptyRegularFile(
            testDir, "Must be regular file, not dir"));
  }

  @Test
  public void requireNonEmptyRegularFile_nonEmptyFile_passes() throws Exception {
    Path testDir = newTestDir();
    Path file = Files.writeString(testDir.resolve("non-empty.json"), "{\"ok\":true}");
    EvidenceFileOperations.requireNonEmptyRegularFile(file, "should pass");
  }

  // ── RegularFileState edge cases ──────────────────────────────────

  @Test
  public void regularFileState_negativeSize_nonEmptyIfExists() {
    EvidenceFileOperations.RegularFileState state =
        new EvidenceFileOperations.RegularFileState(true, -1);
    assertTrue(state.exists());
    assertFalse(state.nonEmpty());
  }

  @Test
  public void regularFileState_missing_sentinel_values() {
    EvidenceFileOperations.RegularFileState missing =
        EvidenceFileOperations.RegularFileState.MISSING;
    assertFalse(missing.exists());
    assertEquals(0, missing.sizeBytes());
    assertFalse(missing.nonEmpty());
  }

  @Test
  public void regularFileState_largeFile_nonEmpty() {
    EvidenceFileOperations.RegularFileState state =
        new EvidenceFileOperations.RegularFileState(true, 10_000_000L);
    assertTrue(state.exists());
    assertTrue(state.nonEmpty());
    assertEquals(10_000_000L, state.sizeBytes());
  }

  // ── Integration: proofRelativePath with canonicalDirectoryUnderProofRoot ──

  @Test
  public void proofRelativePath_afterCanonicalDirectoryCreation() throws Exception {
    Path proofRoot = Files.createDirectories(newTestDir().resolve("proof")).toRealPath();
    Path nested = proofRoot.resolve("sub").resolve("deep");
    Path canonical = EvidenceFileOperations.canonicalDirectoryUnderProofRoot(
        nested, proofRoot, "test");
    Path artifactPath = canonical.resolve("result.json");
    String relative = EvidenceFileOperations.proofRelativePath(artifactPath, proofRoot);
    assertEquals("sub/deep/result.json", relative);
  }

  @Test
  public void proofRelativePath_outsideRootAfterCanonical() throws Exception {
    Path proofRoot = Files.createDirectories(newTestDir().resolve("proof")).toRealPath();
    Path outsidePath = newTestDir().resolve("outside").resolve("result.json");
    String relative = EvidenceFileOperations.proofRelativePath(outsidePath, proofRoot);
    assertEquals("[outside-proof-root]", relative);
  }

  // ── helper ───────────────────────────────────────────────────────

  private static Path newTestDir() throws Exception {
    return Files.createDirectories(
        sharedParent.resolve("t" + SEQ.incrementAndGet()));
  }
}
