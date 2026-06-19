package org.alice.tools;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class EatmeObjectTransformWorkflowTest {
  private static final String WORKFLOW_CLASS_NAME = "org.alice.tools.EatmeObjectTransformWorkflow";
  private static final String FAILURE_SCHEMA = "eatme.object-transform-workflow-failure/v1";

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void exposesTestableNonInteractiveRunEntryPoint() throws Exception {
    Method runMethod = workflowClass().getDeclaredMethod(
        "run", String[].class, PrintStream.class, PrintStream.class);

    assertEquals("run should return a process exit code", int.class, runMethod.getReturnType());
  }

  @Test
  public void rejectsMissingSourceProjectWithFailureEvidence() throws Exception {
    Path outDir = temporaryFolder.newFolder("evidence").toPath();
    Path missingProject = temporaryFolder.getRoot().toPath().resolve("missing.a3p");

    RunResult result = runWorkflow(
        "--source-project", missingProject.toString(),
        "--out-dir", outDir.toString(),
        "--timeout-seconds", "300",
        "--json");

    assertEquals(result.stderr, 2, result.status);
    assertFailureEvidence(
        outDir,
        "source-project-validation",
        "preflight",
        "executionClaim=eatme-object-transform-workflow-failed");
    assertFalse("success artifact must not be written for failed workflow",
        Files.exists(outDir.resolve("object-transform-workflow.json")));
  }

  @Test
  public void rejectsMissingJsonFlagAsArgumentError() throws Exception {
    Path outDir = temporaryFolder.newFolder("evidence").toPath();
    Path sourceProject = temporaryFolder.newFile("source.a3p").toPath();

    RunResult result = runWorkflow(
        "--source-project", sourceProject.toString(),
        "--out-dir", outDir.toString(),
        "--timeout-seconds", "300");

    assertEquals(result.stderr, 2, result.status);
    assertFailureEvidence(outDir, "argument-error", "preflight", "failureKind=argument-error");
  }

  @Test
  public void rejectsInvalidTimeoutAsArgumentError() throws Exception {
    Path outDir = temporaryFolder.newFolder("evidence").toPath();
    Path sourceProject = temporaryFolder.newFile("source.a3p").toPath();

    RunResult result = runWorkflow(
        "--source-project", sourceProject.toString(),
        "--out-dir", outDir.toString(),
        "--timeout-seconds", "0",
        "--json");

    assertEquals(result.stderr, 2, result.status);
    assertFailureEvidence(outDir, "argument-error", "preflight", "timeout");
  }

  @Test
  public void rejectsUnexpectedPositionalArgumentsAsArgumentError() throws Exception {
    Path outDir = temporaryFolder.newFolder("evidence").toPath();
    Path sourceProject = temporaryFolder.newFile("source.a3p").toPath();

    RunResult result = runWorkflow(
        "--source-project", sourceProject.toString(),
        "--out-dir", outDir.toString(),
        "--timeout-seconds", "300",
        "--json",
        "unexpected-position");

    assertEquals(result.stderr, 2, result.status);
    assertFailureEvidence(outDir, "argument-error", "preflight", "unexpected-position");
  }

  @Test
  public void rejectsUnreadableA3pContentWithFailureEvidence() throws Exception {
    Path outDir = temporaryFolder.newFolder("evidence").toPath();
    Path sourceProject = temporaryFolder.newFile("source.a3p").toPath();
    Files.writeString(sourceProject, "not an Alice project", StandardCharsets.UTF_8);

    RunResult result = runWorkflow(
        "--source-project", sourceProject.toString(),
        "--out-dir", outDir.toString(),
        "--timeout-seconds", "300",
        "--json");

    assertEquals(result.stderr, 2, result.status);
    assertFailureEvidence(outDir, "invalid-project", "preflight", "failureKind=invalid-project");
    assertFalse("success artifact must not be written for failed workflow",
        Files.exists(outDir.resolve("object-transform-workflow.json")));
  }

  @Test
  public void requiredArtifactVerifierFailsClosedOnMissingEvidence() throws Exception {
    Path outDir = temporaryFolder.newFolder("evidence").toPath();
    Files.createDirectories(outDir.resolve("placement"));
    Files.writeString(outDir.resolve("placement/placement.json"), "{}", StandardCharsets.UTF_8);

    Method verifier = workflowClass().getDeclaredMethod(
        "verifyRequiredArtifacts", Path.class, String.class, List.class);
    verifier.setAccessible(true);

    try {
      verifier.invoke(null, outDir, "placement", List.of(
          "placement/placement.json",
          "placement/scene.diff.json",
          "placement/placed-project.a3p"));
      fail("missing workflow artifacts must fail closed");
    } catch (InvocationTargetException ex) {
      Throwable cause = ex.getCause();
      assertTrue(String.valueOf(cause.getMessage()),
          String.valueOf(cause.getMessage()).contains("missing-artifact"));
      assertTrue(String.valueOf(cause.getMessage()),
          String.valueOf(cause.getMessage()).contains("placement/scene.diff.json"));
    }
  }

  private static Class<?> workflowClass() throws ClassNotFoundException {
    return Class.forName(WORKFLOW_CLASS_NAME);
  }

  private static RunResult runWorkflow(String... args) throws Exception {
    ByteArrayOutputStream stdout = new ByteArrayOutputStream();
    ByteArrayOutputStream stderr = new ByteArrayOutputStream();
    Method runMethod = workflowClass().getDeclaredMethod(
        "run", String[].class, PrintStream.class, PrintStream.class);
    runMethod.setAccessible(true);
    int status = (Integer) runMethod.invoke(
        null,
        (Object) args,
        new PrintStream(stdout, true, StandardCharsets.UTF_8),
        new PrintStream(stderr, true, StandardCharsets.UTF_8));
    return new RunResult(
        status,
        stdout.toString(StandardCharsets.UTF_8),
        stderr.toString(StandardCharsets.UTF_8));
  }

  private static void assertFailureEvidence(
      Path outDir,
      String failureKind,
      String failedStep,
      String statusFragment) throws Exception {
    Path failureArtifact = outDir.resolve("object-transform-workflow-failure.json");
    Path statusArtifact = outDir.resolve("status.txt");
    assertTrue("failure artifact must exist", Files.isRegularFile(failureArtifact));
    assertTrue("failure artifact must be non-empty", Files.size(failureArtifact) > 0);
    assertTrue("status artifact must exist", Files.isRegularFile(statusArtifact));

    String failureJson = Files.readString(failureArtifact, StandardCharsets.UTF_8);
    assertTrue(failureJson, failureJson.contains("\"schema_version\":\"" + FAILURE_SCHEMA + "\""));
    assertTrue(failureJson, failureJson.contains("\"status\":\"failed\""));
    assertTrue(failureJson, failureJson.contains("\"failure_kind\":\"" + failureKind + "\""));
    assertTrue(failureJson, failureJson.contains("\"failed_step\":\"" + failedStep + "\""));

    String status = Files.readString(statusArtifact, StandardCharsets.UTF_8);
    assertTrue(status, status.contains("outcome=failed"));
    assertTrue(status, status.contains(statusFragment));
  }

  private record RunResult(int status, String stdout, String stderr) {
  }
}
