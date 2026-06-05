package org.lgna.project.io.compat;

import org.junit.Test;
import org.lgna.project.Project;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.story.SProgram;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ReplaySummaryWriterTest {
  @Test
  public void writesNormalizedSortedUtf8SummaryWithHashesOnly() throws Exception {
    ReplayCase replayCase = new ReplayCase(
        "writer-case",
        project("WriterCase"),
        List.of(new ReplaySourceInput("src/WriterCase.twe", "class WriterCase {\r\n}\r\n")));
    ReplaySummaryWriter.Input input = ReplaySummaryWriter.Input.builder("RabbitHole")
        .addArchiveEntries("a3p", List.of("version.txt", "resources/z.txt", "manifest.json"))
        .addArchiveEntries("a3w", List.of("src/WriterCase.twe", "version.txt", "manifest.json"))
        .addManifestFields("a3p", Map.of("projectName", "WriterCase", "archiveType", "a3p"))
        .addManifestFields("a3w", Map.of("projectName", "WriterCase", "archiveType", "a3w"))
        .addResource(new ReplaySummaryWriter.ResourceIdentity(
            "z.txt",
            "text/plain",
            "resources/z.txt",
            "resource\r\n".getBytes(StandardCharsets.UTF_8)))
        .build();

    ReplaySummary summary = new ReplaySummaryWriter().write(replayCase, input);
    String text = summary.text();

    assertEquals("rabbithole.dual-baseline-summary/v1", ReplaySummaryWriter.SCHEMA);
    assertEquals("writer-case", summary.caseId());
    assertEquals("RabbitHole", summary.providerName());
    assertTrue(text.startsWith("""
        case: writer-case
        summary-schema: rabbithole.dual-baseline-summary/v1

        """));
    assertFalse(text.contains("\r"));
    assertContainsLine(text, "[source-tree]");
    assertContainsLine(text, "src/WriterCase.twe");
    assertContainsLine(text, "src/WriterCase.twe sha256:" + sha256Text("class WriterCase {\n}\n"));
    assertContainsLine(text, "z.txt type=text/plain path=resources/z.txt bytes=10 sha256:" + sha256Bytes("resource\r\n".getBytes(StandardCharsets.UTF_8)));
    assertLineOrder(text, "[archives:a3p]", "manifest.json", "resources/z.txt", "version.txt");
    assertLineOrder(text, "[manifest:a3p]", "archiveType=a3p", "projectName=WriterCase");
    assertNoMachineLocalLeakage(text);
  }

  @Test
  public void rejectsUnsafeArchiveAndResourcePathsBeforeWritingSummary() {
    ReplayCase replayCase = new ReplayCase("unsafe-writer-case", project("UnsafeWriterCase"), List.of());
    ReplaySummaryWriter writer = new ReplaySummaryWriter();

    ReplaySummaryWriter.Input archiveInput = ReplaySummaryWriter.Input.builder("RabbitHole")
        .addArchiveEntries("a3p", List.of("manifest.json", "../evil.txt"))
        .build();
    IOException archiveThrown = assertThrows(IOException.class, () -> writer.write(replayCase, archiveInput));
    assertTrue(archiveThrown.getMessage().contains("../evil.txt"));

    ReplaySummaryWriter.Input resourceInput = ReplaySummaryWriter.Input.builder("RabbitHole")
        .addResource(new ReplaySummaryWriter.ResourceIdentity(
            "evil.txt",
            "text/plain",
            "/tmp/evil.txt",
            new byte[] {1, 2, 3}))
        .build();
    IOException resourceThrown = assertThrows(IOException.class, () -> writer.write(replayCase, resourceInput));
    assertTrue(resourceThrown.getMessage().contains("/tmp/evil.txt"));
  }

  @Test
  public void rejectsControlBytesAndEnvironmentDumpText() {
    ReplayCase replayCase = new ReplayCase("hygiene-case", project("HygieneCase"), List.of());
    ReplaySummaryWriter writer = new ReplaySummaryWriter();

    ReplaySummaryWriter.Input binaryResourceInput = ReplaySummaryWriter.Input.builder("RabbitHole")
        .addResource(new ReplaySummaryWriter.ResourceIdentity(
            "binary.bin",
            "application/octet-stream",
            "resources/binary.bin",
            new byte[] {0, 1, 2, 3}))
        .build();
    IOException binaryThrown = assertThrows(IOException.class, () -> writer.write(replayCase, binaryResourceInput));
    assertTrue(binaryThrown.getMessage().contains("binary.bin"));

    ReplaySummaryWriter.Input environmentDumpInput = ReplaySummaryWriter.Input.builder("RabbitHole")
        .addManifestFields("a3p", Map.of("environment", "PATH=/usr/bin"))
        .build();
    IOException envThrown = assertThrows(IOException.class, () -> writer.write(replayCase, environmentDumpInput));
    assertTrue(envThrown.getMessage().contains("environment"));
  }

  private static Project project(String name) {
    NamedUserType programType = new NamedUserType();
    programType.name.setValue(name);
    programType.superType.setValue(JavaType.getInstance(SProgram.class));
    return new Project(programType, Project.SceneCameraType.WindowCamera);
  }

  private static String sha256Text(String text) {
    return sha256Bytes(text.getBytes(StandardCharsets.UTF_8));
  }

  private static String sha256Bytes(byte[] bytes) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(bytes);
      StringBuilder builder = new StringBuilder(hash.length * 2);
      for (byte value : hash) {
        builder.append(String.format("%02x", value));
      }
      return builder.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new AssertionError(e);
    }
  }

  private static void assertContainsLine(String text, String line) {
    assertTrue("Missing line:\n" + line + "\n\nSummary:\n" + text,
        List.of(text.split("\n", -1)).contains(line));
  }

  private static void assertLineOrder(String text, String... orderedLines) {
    int previousIndex = -1;
    for (String line : orderedLines) {
      int currentIndex = text.indexOf(line, previousIndex + 1);
      assertTrue("Missing ordered line '" + line + "' in summary:\n" + text, currentIndex > previousIndex);
      previousIndex = currentIndex;
    }
  }

  private static void assertNoMachineLocalLeakage(String text) {
    assertFalse(text.contains(System.getProperty("user.home")));
    assertFalse(text.contains(System.getProperty("java.io.tmpdir")));
    assertFalse(text.matches("(?s).*\\b[0-9]{4}-[0-9]{2}-[0-9]{2}\\b.*"));
    assertFalse(text.matches("(?s).*@[0-9a-fA-F]{6,}\\b.*"));
  }
}
