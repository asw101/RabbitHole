package org.alice.tools;

import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.awt.GraphicsEnvironment;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EatmeObjectTransformWorkflowXvfbTest {
  private static final String WORKFLOW_CLASS_NAME = "org.alice.tools.EatmeObjectTransformWorkflow";
  private static final List<String> REQUIRED_ARTIFACTS = List.of(
      "object-transform-workflow.json",
      "status.txt",
      "transform/object-transform.json",
      "transform/transformed-project.a3p",
      "placement/placement.json",
      "placement/scene.diff.json",
      "placement/placed-project.a3p",
      "edit/first-lesson-code-editor-action-proof.json",
      "edit/edited-project.a3p",
      "run/world-run.json",
      "run/runtime.log",
      "save/project-save.json",
      "save/saved-project.a3p",
      "reopen/reopen-evidence.json",
      "reopen/reopened-state.json",
      "reopen/reopened.a3p");

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void fullObjectTransformPathWritesDurableEvidenceUnderXvfb() throws Exception {
    assumeDisplayBackedRun();
    Path sourceProject = repositoryRoot().resolve(
        "core/resources/src/application/resources/starter-projects/magicMinimum.a3p");
    Assume.assumeTrue("starter project fixture is not available in this checkout",
        Files.isRegularFile(sourceProject));

    String sourceHashBefore = sha256(sourceProject);
    Path outDir = temporaryFolder.newFolder("object-transform-evidence").toPath();
    RunResult result = runWorkflow(
        "--source-project", sourceProject.toString(),
        "--out-dir", outDir.toString(),
        "--timeout-seconds", "300",
        "--json");

    assertEquals(result.stderr, 0, result.status);
    assertEquals("source project must remain read-only", sourceHashBefore, sha256(sourceProject));
    assertTrue(result.stdout, result.stdout.contains(
        "\"schema_version\":\"eatme.object-transform-workflow-result/v1\""));
    assertTrue(result.stdout, result.stdout.contains("\"status\":\"passed\""));
    assertTrue(result.stdout, result.stdout.contains("\"object\":\"alice-gallery://animals/bunny\""));
    assertTrue(result.stdout, result.stdout.contains("\"selector\":\"scene.eatmeObjectTransformStep\""));
    assertTrue(result.stdout, result.stdout.contains("\"workflow_artifact\":\"object-transform-workflow.json\""));
    assertStepOrder(result.stdout);

    for (String artifact : REQUIRED_ARTIFACTS) {
      Path path = outDir.resolve(artifact);
      assertTrue("required artifact must exist: " + artifact, Files.isRegularFile(path));
      assertTrue("required artifact must be non-empty: " + artifact, Files.size(path) > 0);
    }

    String status = Files.readString(outDir.resolve("status.txt"), StandardCharsets.UTF_8);
    assertTrue(status, status.contains("outcome=passed"));
    assertTrue(status, status.contains("executionStatus=executed"));
    assertTrue(status, status.contains("executionClaim=eatme-object-transform-workflow-executed"));

    String workflowJson = Files.readString(outDir.resolve("object-transform-workflow.json"), StandardCharsets.UTF_8);
    assertEquals("stdout JSON and workflow artifact JSON should match", result.stdout.trim(), workflowJson.trim());
    assertTrue(workflowJson, workflowJson.contains("\"transform/object-transform.json\""));
    assertTrue(workflowJson, workflowJson.contains("\"placement/placed-project.a3p\""));
    assertTrue(workflowJson, workflowJson.contains("\"edit/edited-project.a3p\""));
    assertTrue(workflowJson, workflowJson.contains("\"run/world-run.json\""));
    assertTrue(workflowJson, workflowJson.contains("\"save/saved-project.a3p\""));
    assertTrue(workflowJson, workflowJson.contains("\"reopen/reopened.a3p\""));
  }

  private static void assumeDisplayBackedRun() {
    Assume.assumeFalse("Xvfb test requires java.awt.headless=false", GraphicsEnvironment.isHeadless());
    String display = System.getenv("DISPLAY");
    Assume.assumeTrue("Xvfb test requires DISPLAY", display != null && !display.isBlank());
  }

  private static RunResult runWorkflow(String... args) throws Exception {
    ByteArrayOutputStream stdout = new ByteArrayOutputStream();
    ByteArrayOutputStream stderr = new ByteArrayOutputStream();
    Method runMethod = Class.forName(WORKFLOW_CLASS_NAME).getDeclaredMethod(
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

  private static String sha256(Path path) throws Exception {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    return HexFormat.of().formatHex(digest.digest(Files.readAllBytes(path)));
  }

  private static void assertStepOrder(String json) {
    assertInOrder(json, "\"name\":\"transform\"", "\"name\":\"placement\"");
    assertInOrder(json, "\"name\":\"placement\"", "\"name\":\"edit\"");
    assertInOrder(json, "\"name\":\"edit\"", "\"name\":\"run\"");
    assertInOrder(json, "\"name\":\"run\"", "\"name\":\"save\"");
    assertInOrder(json, "\"name\":\"save\"", "\"name\":\"reopen\"");
  }

  private static void assertInOrder(String text, String before, String after) {
    int beforeIndex = text.indexOf(before);
    int afterIndex = text.indexOf(after);
    assertTrue(text, beforeIndex >= 0);
    assertTrue(text, afterIndex > beforeIndex);
  }

  private record RunResult(int status, String stdout, String stderr) {
  }
}
