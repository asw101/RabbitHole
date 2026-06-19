package org.alice.tools;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class EatmeObjectTransformCommandTest {
  @Test
  public void wrapperFollowsEatmeLauncherContract() throws Exception {
    Path wrapper = repositoryRoot().resolve("tools/eatme-object-transform");

    assertTrue("tools/eatme-object-transform must exist", Files.isRegularFile(wrapper));
    assertTrue("tools/eatme-object-transform must be executable", Files.isExecutable(wrapper));

    String script = Files.readString(wrapper, StandardCharsets.UTF_8);
    assertTrue(script, script.startsWith("#!/usr/bin/env bash\n"));
    assertTrue(script, script.contains("set -euo pipefail"));
    assertTrue(script, script.contains("alice-ide/target/lib"));
    assertTrue(script, script.contains("run the Alice package step before tools/eatme-object-transform"));
    assertTrue(script, script.contains("-Dorg.alice.ide.rootDirectory=./core/resources/target/distribution"));
    assertTrue(script, script.contains("-Dedu.cmu.cs.dennisc.java.util.logging.Logger.Level=WARNING"));
    assertTrue(script, script.contains("-cp \"alice-ide/target/*:alice-ide/target/lib/*\""));
    assertTrue(script, script.contains("org.alice.tools.EatmeObjectTransformWorkflow"));
    assertTrue(script, script.contains("\"$@\""));
  }

  @Test
  public void wrapperFailsFastWhenPackagedLibDirectoryIsMissing() throws Exception {
    Path wrapper = repositoryRoot().resolve("tools/eatme-object-transform");
    assertTrue("tools/eatme-object-transform must exist", Files.isRegularFile(wrapper));

    Path emptyWorkingDirectory = Files.createTempDirectory("eatme-object-transform-wrapper-");
    try {
      Process process = new ProcessBuilder(
          "bash",
          wrapper.toAbsolutePath().toString(),
          "--source-project", "source.a3p",
          "--out-dir", "evidence",
          "--timeout-seconds", "300",
          "--json")
          .directory(emptyWorkingDirectory.toFile())
          .redirectOutput(ProcessBuilder.Redirect.PIPE)
          .redirectError(ProcessBuilder.Redirect.PIPE)
          .start();

      if (!process.waitFor(10, TimeUnit.SECONDS)) {
        process.destroyForcibly();
        fail("wrapper package preflight must not hang when alice-ide/target/lib is missing");
      }

      String stdout = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
      String stderr = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
      assertEquals(stdout, "", stdout);
      assertEquals(stderr, 2, process.exitValue());
      assertTrue(stderr, stderr.contains("alice-ide/target/lib is missing"));
      assertTrue(stderr, stderr.contains("tools/eatme-object-transform"));
    } finally {
      deleteRecursively(emptyWorkingDirectory);
    }
  }

  private static Path repositoryRoot() {
    Path current = Path.of(System.getProperty("user.dir")).toAbsolutePath();
    while (current != null) {
      if (Files.exists(current.resolve(".git")) && Files.isRegularFile(current.resolve("pom.xml"))) {
        return current;
      }
      current = current.getParent();
    }
    throw new AssertionError("Could not find repository root from " + System.getProperty("user.dir"));
  }

  private static void deleteRecursively(Path root) throws IOException {
    if (Files.notExists(root)) {
      return;
    }
    try (var paths = Files.walk(root)) {
      IOException[] failure = new IOException[1];
      paths
          .sorted((left, right) -> right.getNameCount() - left.getNameCount())
          .forEach(path -> {
            try {
              Files.deleteIfExists(path);
            } catch (IOException ex) {
              failure[0] = ex;
            }
          });
      if (failure[0] != null) {
        throw failure[0];
      }
    }
  }
}
