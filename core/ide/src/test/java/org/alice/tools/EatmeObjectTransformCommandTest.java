package org.alice.tools;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class EatmeObjectTransformCommandTest {
  private static final WrapperContract[] WRAPPER_CONTRACTS = {
      new WrapperContract("tools/eatme-place-object", "org.alice.tools.EatmePlaceObject"),
      new WrapperContract("tools/eatme-transform-object", "org.alice.tools.EatmeTransformObject"),
      new WrapperContract("tools/eatme-edit-procedure", "org.alice.tools.EatmeEditProcedure"),
      new WrapperContract("tools/eatme-run-world", "org.alice.tools.EatmeRunWorld"),
      new WrapperContract("tools/eatme-save-project", "org.alice.tools.EatmeSaveProject"),
      new WrapperContract("tools/eatme-reopen-project", "org.alice.tools.EatmeReopenProject"),
      new WrapperContract("tools/eatme-object-transform", "org.alice.tools.EatmeObjectTransformWorkflow")
  };

  private static final HookContract[] HOOK_CONTRACTS = {
      new HookContract(
          "place-object",
          EatmePlaceObject::run,
          new String[] {
              "--project", "/missing/project.a3p",
              "--object", "alice-gallery://animals/bunny",
              "--evidence-dir", "{evidence}"
          }),
      new HookContract(
          "transform-object",
          EatmeTransformObject::run,
          new String[] {
              "--project", "/missing/project.a3p",
              "--object-identifier", "alice-gallery://animals/bunny",
              "--evidence-dir", "{evidence}"
          }),
      new HookContract(
          "edit-procedure",
          EatmeEditProcedure::run,
          new String[] {
              "--project", "/missing/project.a3p",
              "--procedure-selector", "scene.eatmeFirstLesson",
              "--edit-spec", "append-comment:wave4-code-editor-action-proof",
              "--evidence-dir", "{evidence}"
          }),
      new HookContract(
          "run-world",
          EatmeRunWorld::run,
          new String[] {
              "--project", "/missing/project.a3p",
              "--run-selector", "scene.eatmeFirstLessonStep",
              "--evidence-dir", "{evidence}"
          }),
      new HookContract(
          "save-project",
          EatmeSaveProject::run,
          new String[] {
              "--project", "/missing/project.a3p",
              "--save-selector", "scene.eatmeFirstLessonStep",
              "--evidence-dir", "{evidence}"
          }),
      new HookContract(
          "reopen-project",
          EatmeReopenProject::run,
          new String[] {
              "--saved-project", "/missing/project.a3p",
              "--reopen-selector", "scene.eatmeFirstLessonStep",
              "--evidence-dir", "{evidence}"
          }),
      new HookContract(
          "object-transform-workflow",
          EatmeObjectTransformWorkflow::run,
          new String[] {
              "--source-project", "/missing/project.a3p",
              "--out-dir", "{evidence}"
          })
  };

  @Test
  public void wrappersFollowEatmeLauncherContracts() throws Exception {
    for (WrapperContract contract : WRAPPER_CONTRACTS) {
      Path wrapper = repositoryRoot().resolve(contract.path());

      assertTrue(contract.path() + " must exist", Files.isRegularFile(wrapper));
      assertTrue(contract.path() + " must be executable", Files.isExecutable(wrapper));

      String script = Files.readString(wrapper, StandardCharsets.UTF_8);
      assertTrue(script, script.startsWith("#!/usr/bin/env bash\n"));
      assertTrue(script, script.contains("set -euo pipefail"));
      assertTrue(script, script.contains("alice-ide/target/lib"));
      assertTrue(script, script.contains("run the Alice package step before " + contract.path()));
      assertTrue(script, script.contains("-Dorg.alice.ide.rootDirectory=./core/resources/target/distribution"));
      assertTrue(script, script.contains("-Dedu.cmu.cs.dennisc.java.util.logging.Logger.Level=WARNING"));
      assertTrue(script, script.contains("-cp \"alice-ide/target/*:alice-ide/target/lib/*\""));
      assertTrue(script, script.contains(contract.mainClass()));
      assertTrue(script, script.contains("\"$@\""));
    }
  }

  @Test
  public void wrappersFailFastWhenPackagedLibDirectoryIsMissing() throws Exception {
    for (WrapperContract contract : WRAPPER_CONTRACTS) {
      Path wrapper = repositoryRoot().resolve(contract.path());
      assertTrue(contract.path() + " must exist", Files.isRegularFile(wrapper));

      Path emptyWorkingDirectory = Files.createTempDirectory(contract.path().replace('/', '-') + "-wrapper-");
      try {
        Process process = new ProcessBuilder(
            "bash",
            wrapper.toAbsolutePath().toString(),
            "--json")
            .directory(emptyWorkingDirectory.toFile())
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start();

        if (!process.waitFor(10, TimeUnit.SECONDS)) {
          process.destroyForcibly();
          fail(contract.path() + " package preflight must not hang when alice-ide/target/lib is missing");
        }

        String stdout = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        String stderr = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
        assertEquals(stdout, "", stdout);
        assertEquals(stderr, 2, process.exitValue());
        assertTrue(stderr, stderr.contains("alice-ide/target/lib is missing"));
        assertTrue(stderr, stderr.contains(contract.path()));
      } finally {
        deleteRecursively(emptyWorkingDirectory);
      }
    }
  }

  @Test
  public void hookEntryPointsRequireJsonOutputContract() throws Exception {
    Path tempDirectory = Files.createTempDirectory("eatme-hook-json-contract-");
    try {
      for (HookContract contract : HOOK_CONTRACTS) {
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();
        int status = contract.runner().run(
            contract.argsWithoutJson(tempDirectory.resolve(contract.name())),
            new PrintStream(stdout),
            new PrintStream(stderr));

        assertEquals(contract.name(), 2, status);
        assertEquals(contract.name(), "", stdout.toString(StandardCharsets.UTF_8));
        assertTrue(
            contract.name() + " should require JSON output",
            stderr.toString(StandardCharsets.UTF_8).contains("--json is required"));
      }
    } finally {
      deleteRecursively(tempDirectory);
    }
  }

  @Test
  public void hookEntryPointsRejectUnknownArguments() {
    for (HookContract contract : HOOK_CONTRACTS) {
      ByteArrayOutputStream stdout = new ByteArrayOutputStream();
      ByteArrayOutputStream stderr = new ByteArrayOutputStream();
      int status = contract.runner().run(
          new String[] {"--unknown"},
          new PrintStream(stdout),
          new PrintStream(stderr));

      assertEquals(contract.name(), 2, status);
      assertEquals(contract.name(), "", stdout.toString(StandardCharsets.UTF_8));
      assertTrue(
          contract.name() + " should reject unknown arguments",
          stderr.toString(StandardCharsets.UTF_8).contains("unknown")
              || stderr.toString(StandardCharsets.UTF_8).contains("unexpected"));
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

  private record WrapperContract(String path, String mainClass) {
  }

  private record HookContract(String name, HookRunner runner, String[] argsWithoutJsonTemplate) {
    String[] argsWithoutJson(Path evidenceDir) {
      String[] resolved = new String[argsWithoutJsonTemplate.length];
      for (int i = 0; i < argsWithoutJsonTemplate.length; i++) {
        resolved[i] = "{evidence}".equals(argsWithoutJsonTemplate[i])
            ? evidenceDir.toString()
            : argsWithoutJsonTemplate[i];
      }
      return resolved;
    }
  }

  private interface HookRunner {
    int run(String[] args, PrintStream out, PrintStream err);
  }
}
