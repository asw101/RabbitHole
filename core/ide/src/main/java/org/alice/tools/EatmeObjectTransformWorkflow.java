package org.alice.tools;

import org.lgna.project.Project;
import org.lgna.project.VersionNotSupportedException;
import org.lgna.project.ast.AbstractType;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.Comment;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserParameter;
import org.lgna.project.io.IoUtilities;
import org.lgna.story.SScene;

import java.awt.GraphicsEnvironment;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class EatmeObjectTransformWorkflow {
  private static final String RESULT_SCHEMA = "eatme.object-transform-workflow-result/v1";
  private static final String FAILURE_SCHEMA = "eatme.object-transform-workflow-failure/v1";
  private static final String TRANSFORM_SCHEMA = "eatme.object-transform/v1";
  private static final String WORKFLOW_ARTIFACT = "object-transform-workflow.json";
  private static final String FAILURE_ARTIFACT = "object-transform-workflow-failure.json";
  private static final String STATUS_ARTIFACT = "status.txt";
  private static final String STEP_FAILURE_ARTIFACT = "step-failure.json";
  private static final String OBJECT_IDENTIFIER = "alice-gallery://animals/bunny";
  private static final String SELECTOR = "scene.eatmeObjectTransformStep";
  private static final String METHOD_NAME = "eatmeObjectTransformStep";
  private static final String EDIT_SPEC = "append-comment:eatme object transform proof";
  private static final int DEFAULT_TIMEOUT_SECONDS = 300;

  private static final List<String> TRANSFORM_ARTIFACTS = List.of(
      "transform/object-transform.json",
      "transform/transformed-project.a3p");
  private static final List<String> PLACEMENT_ARTIFACTS = List.of(
      "placement/placement.json",
      "placement/scene.diff.json",
      "placement/placed-project.a3p");
  private static final List<String> EDIT_ARTIFACTS = List.of(
      "edit/first-lesson-code-editor-action-proof.json",
      "edit/edited-project.a3p");
  private static final List<String> RUN_ARTIFACTS = List.of(
      "run/world-run.json",
      "run/runtime.log");
  private static final List<String> SAVE_ARTIFACTS = List.of(
      "save/project-save.json",
      "save/saved-project.a3p");
  private static final List<String> REOPEN_ARTIFACTS = List.of(
      "reopen/reopen-evidence.json",
      "reopen/reopened-state.json",
      "reopen/reopened.a3p");

  private EatmeObjectTransformWorkflow() {
  }

  public static void main(String[] args) {
    int status = run(args, System.out, System.err);
    if (status != 0) {
      System.exit(status);
    }
  }

  static int run(String[] args, PrintStream out, PrintStream err) {
    Path requestedOutDir = outDirFromArgs(args);
    PrintStream originalSystemOut = System.out;
    PrintStream silentSystemOut = new PrintStream(new ByteArrayOutputStream());
    System.setOut(silentSystemOut);
    try {
      Config config = Config.parse(args);
      preflight(config);
      List<StepResult> steps = runSteps(config);
      String resultJson = resultJson(config, steps);
      writeArtifact(config.outDir().resolve(WORKFLOW_ARTIFACT), resultJson);
      verifyRequiredArtifacts(config.outDir(), "workflow", requiredWorkflowArtifacts());
      writeArtifact(config.outDir().resolve(STATUS_ARTIFACT), successStatus(config, steps));
      System.setOut(originalSystemOut);
      out.println(resultJson);
      return 0;
    } catch (WorkflowFailure failure) {
      System.setOut(originalSystemOut);
      String evidenceFailure = writeFailureEvidence(requestedOutDir, failure);
      err.println(failure.getMessage());
      if (evidenceFailure != null) {
        err.println(evidenceFailure);
      }
      return failure.exitCode();
    } catch (IOException ex) {
      System.setOut(originalSystemOut);
      WorkflowFailure failure = new WorkflowFailure(
          "artifact-write-failure",
          "workflow",
          ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage(),
          3,
          ex);
      String evidenceFailure = writeFailureEvidence(requestedOutDir, failure);
      err.println(failure.getMessage());
      if (evidenceFailure != null) {
        err.println(evidenceFailure);
      }
      return failure.exitCode();
    } catch (RuntimeException ex) {
      System.setOut(originalSystemOut);
      WorkflowFailure failure = new WorkflowFailure(
          "workflow-error",
          "workflow",
          ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage(),
          3,
          ex);
      String evidenceFailure = writeFailureEvidence(requestedOutDir, failure);
      err.println(failure.getMessage());
      if (evidenceFailure != null) {
        err.println(evidenceFailure);
      }
      return failure.exitCode();
    } finally {
      System.setOut(originalSystemOut);
      silentSystemOut.close();
    }
  }

  private static List<StepResult> runSteps(Config config) throws WorkflowFailure {
    List<StepResult> steps = new ArrayList<>();
    steps.add(runStep(config, "transform", TRANSFORM_ARTIFACTS,
        () -> transformProject(config.sourceProject(), config.outDir().resolve("transform"))));
    steps.add(runToolStep(config, "placement", PLACEMENT_ARTIFACTS, () -> EatmePlaceObject.run(
        new String[] {
            "--project", config.outDir().resolve("transform/transformed-project.a3p").toString(),
            "--object", OBJECT_IDENTIFIER,
            "--evidence-dir", config.outDir().resolve("placement").toString(),
            "--json"
        },
        new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8),
        new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8))));
    steps.add(runToolStep(config, "edit", EDIT_ARTIFACTS, () -> EatmeEditProcedure.run(
        new String[] {
            "--project", config.outDir().resolve("placement/placed-project.a3p").toString(),
            "--procedure-selector", SELECTOR,
            "--edit-spec", EDIT_SPEC,
            "--evidence-dir", config.outDir().resolve("edit").toString(),
            "--json"
        },
        new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8),
        new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8))));
    steps.add(runToolStep(config, "run", RUN_ARTIFACTS, () -> EatmeRunWorld.run(
        new String[] {
            "--project", config.outDir().resolve("edit/edited-project.a3p").toString(),
            "--run-selector", SELECTOR,
            "--evidence-dir", config.outDir().resolve("run").toString(),
            "--json"
        },
        new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8),
        new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8))));
    steps.add(runToolStep(config, "save", SAVE_ARTIFACTS, () -> EatmeSaveProject.run(
        new String[] {
            "--project", config.outDir().resolve("edit/edited-project.a3p").toString(),
            "--save-selector", SELECTOR,
            "--evidence-dir", config.outDir().resolve("save").toString(),
            "--json"
        },
        new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8),
        new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8))));
    steps.add(runToolStep(config, "reopen", REOPEN_ARTIFACTS, () -> EatmeReopenProject.run(
        new String[] {
            "--saved-project", config.outDir().resolve("save/saved-project.a3p").toString(),
            "--reopen-selector", SELECTOR,
            "--evidence-dir", config.outDir().resolve("reopen").toString(),
            "--json"
        },
        new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8),
        new PrintStream(new ByteArrayOutputStream(), true, StandardCharsets.UTF_8))));
    return steps;
  }

  private static StepResult runToolStep(Config config, String name, List<String> artifacts, Callable<Integer> callable)
      throws WorkflowFailure {
    return runStep(config, name, artifacts, () -> {
      int status = callable.call();
      if (status != 0) {
        throw new WorkflowFailure("step-failed", name, name + " exited with status " + status, 3);
      }
    });
  }

  private static StepResult runStep(Config config, String name, List<String> artifacts, ThrowingRunnable runnable)
      throws WorkflowFailure {
    Instant started = Instant.now();
    ExecutorService executor = Executors.newSingleThreadExecutor(new DaemonThreadFactory("eatme-object-transform-" + name));
    Future<Void> future = executor.submit(() -> {
      runnable.run();
      return null;
    });
    try {
      future.get(config.timeoutSeconds(), TimeUnit.SECONDS);
      long durationMillis = Duration.between(started, Instant.now()).toMillis();
      verifyRequiredArtifacts(config.outDir(), name, artifacts);
      return new StepResult(name, "passed", durationMillis, artifacts);
    } catch (IOException ex) {
      throw new WorkflowFailure("missing-artifact", name, ex.getMessage(), 3, ex);
    } catch (TimeoutException ex) {
      future.cancel(true);
      throw new WorkflowFailure("timeout", name,
          name + " timed out after " + config.timeoutSeconds() + " seconds", 3, ex);
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
      throw new WorkflowFailure("interrupted", name, name + " was interrupted", 3, ex);
    } catch (ExecutionException ex) {
      Throwable cause = ex.getCause();
      if (cause instanceof WorkflowFailure failure) {
        throw failure;
      }
      throw new WorkflowFailure("step-failed", name, messageFor(cause), 3, cause);
    } finally {
      executor.shutdownNow();
    }
  }

  private static void transformProject(Path sourceProject, Path evidenceDir)
      throws IOException, VersionNotSupportedException {
    Files.createDirectories(evidenceDir);
    Project project = IoUtilities.readProject(sourceProject.toFile());
    NamedUserType sceneType = findSceneType(project);
    UserMethod method = findMethod(sceneType, METHOD_NAME);
    boolean methodCreated = false;
    if (method == null) {
      method = new UserMethod(
          METHOD_NAME,
          JavaType.VOID_TYPE,
          new UserParameter[0],
          new BlockStatement(new Comment("eatme object transform step")));
      sceneType.methods.add(method);
      methodCreated = true;
    } else if (method.body.getValue() == null || method.body.getValue().statements.isEmpty()) {
      method.body.setValue(new BlockStatement(new Comment("eatme object transform step")));
    }

    Path transformedProject = evidenceDir.resolve("transformed-project.a3p");
    IoUtilities.writeProject(transformedProject.toFile(), project);
    EatmeEvidenceWriter.requireNonEmptyArtifact(transformedProject, "transformed project artifact");

    Path transformArtifact = evidenceDir.resolve("object-transform.json");
    writeArtifact(transformArtifact, transformArtifactJson(sourceProject, sceneType.getName(), methodCreated));
    EatmeEvidenceWriter.requireNonEmptyArtifact(transformArtifact, "object transform artifact");
  }

  private static void preflight(Config config) throws WorkflowFailure {
    try {
      Files.createDirectories(config.outDir());
    } catch (IOException ex) {
      throw new WorkflowFailure("output-directory-validation", "preflight",
          "could not create output directory: " + config.outDir(), 2, ex);
    }
    if (!Files.isDirectory(config.outDir()) || !Files.isWritable(config.outDir())) {
      throw new WorkflowFailure("output-directory-validation", "preflight",
          "output directory is not writable: " + config.outDir(), 2);
    }
    if (!Files.isRegularFile(config.sourceProject())) {
      throw new WorkflowFailure("source-project-validation", "preflight",
          "source project does not exist: " + config.sourceProject(), 2);
    }
    if (!config.sourceProject().getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".a3p")) {
      throw new WorkflowFailure("source-project-validation", "preflight",
          "source project must be a .a3p file: " + config.sourceProject(), 2);
    }
    if (!Files.isReadable(config.sourceProject())) {
      throw new WorkflowFailure("source-project-validation", "preflight",
          "source project is not readable: " + config.sourceProject(), 2);
    }
    validateReadableProject(config.sourceProject());
    if (GraphicsEnvironment.isHeadless()) {
      throw new WorkflowFailure("display-validation", "preflight",
          "GUI-backed workflow steps require java.awt.headless=false", 2);
    }
    String display = System.getenv("DISPLAY");
    if (display == null || display.isBlank()) {
      throw new WorkflowFailure("display-validation", "preflight",
          "GUI-backed workflow steps require DISPLAY", 2);
    }
    for (String artifact : requiredWorkflowArtifacts()) {
      artifactPath(config.outDir(), artifact);
    }
  }

  private static void validateReadableProject(Path sourceProject) throws WorkflowFailure {
    try {
      IoUtilities.readProject(sourceProject.toFile());
    } catch (IOException | VersionNotSupportedException | RuntimeException ex) {
      throw new WorkflowFailure("invalid-project", "preflight",
          "source project could not be read as an Alice project: " + sourceProject, 2, ex);
    }
  }

  static void verifyRequiredArtifacts(Path outDir, String stepName, List<String> artifacts) throws IOException {
    for (String artifact : artifacts) {
      Path path = artifactPath(outDir, artifact);
      if (!Files.isRegularFile(path) || Files.size(path) == 0) {
        throw new IOException("missing-artifact: " + stepName + " did not write " + artifact);
      }
    }
  }

  private static Path artifactPath(Path outDir, String relativePath) {
    Path path = Path.of(relativePath);
    Path normalized = path.normalize();
    if (path.isAbsolute()
        || normalized.toString().isEmpty()
        || normalized.startsWith("..")) {
      throw new IllegalArgumentException("artifact path escapes output directory: " + relativePath);
    }
    Path resolved = outDir.resolve(normalized).normalize();
    if (!resolved.startsWith(outDir)) {
      throw new IllegalArgumentException("artifact path escapes output directory: " + relativePath);
    }
    return resolved;
  }

  private static List<String> requiredWorkflowArtifacts() {
    List<String> artifacts = new ArrayList<>();
    artifacts.add(WORKFLOW_ARTIFACT);
    artifacts.addAll(TRANSFORM_ARTIFACTS);
    artifacts.addAll(PLACEMENT_ARTIFACTS);
    artifacts.addAll(EDIT_ARTIFACTS);
    artifacts.addAll(RUN_ARTIFACTS);
    artifacts.addAll(SAVE_ARTIFACTS);
    artifacts.addAll(REOPEN_ARTIFACTS);
    return artifacts;
  }

  private static NamedUserType findSceneType(Project project) {
    for (UserField field : project.getProgramType().getDeclaredFields()) {
      AbstractType<?, ?, ?> valueType = field.getValueType();
      if (valueType instanceof NamedUserType namedUserType && valueType.isAssignableTo(SScene.class)) {
        return namedUserType;
      }
    }
    throw new IllegalArgumentException("project does not contain a program field typed by an SScene subtype");
  }

  private static UserMethod findMethod(NamedUserType sceneType, String methodName) {
    for (UserMethod method : sceneType.getDeclaredMethods()) {
      if (methodName.equals(method.getName())) {
        return method;
      }
    }
    return null;
  }

  private static String resultJson(Config config, List<StepResult> steps) {
    return "{"
        + "\"schema_version\":\"" + RESULT_SCHEMA + "\","
        + "\"status\":\"passed\","
        + "\"source_project\":\"" + escapeJson(config.sourceProject().toString()) + "\","
        + "\"object\":\"" + OBJECT_IDENTIFIER + "\","
        + "\"selector\":\"" + SELECTOR + "\","
        + "\"timeout_seconds\":" + config.timeoutSeconds() + ","
        + "\"workflow_artifact\":\"" + WORKFLOW_ARTIFACT + "\","
        + "\"status_artifact\":\"" + STATUS_ARTIFACT + "\","
        + "\"steps\":" + stepsJson(steps)
        + "}";
  }

  private static String stepsJson(List<StepResult> steps) {
    StringBuilder builder = new StringBuilder("[");
    for (int i = 0; i < steps.size(); i++) {
      if (i > 0) {
        builder.append(',');
      }
      StepResult step = steps.get(i);
      builder.append('{')
          .append("\"name\":\"").append(escapeJson(step.name())).append("\",")
          .append("\"status\":\"").append(step.status()).append("\",")
          .append("\"duration_millis\":").append(step.durationMillis()).append(',')
          .append("\"artifacts\":").append(jsonArray(step.artifacts()))
          .append('}');
    }
    return builder.append(']').toString();
  }

  private static String transformArtifactJson(Path sourceProject, String sceneType, boolean methodCreated) {
    return "{\n"
        + "  \"schema_version\": \"" + TRANSFORM_SCHEMA + "\",\n"
        + "  \"status\": \"transformed\",\n"
        + "  \"transform_mode\": \"objects_first_default_pose_v1\",\n"
        + "  \"source_project\": \"" + escapeJson(sourceProject.toString()) + "\",\n"
        + "  \"transformed_project\": \"transformed-project.a3p\",\n"
        + "  \"object\": \"" + OBJECT_IDENTIFIER + "\",\n"
        + "  \"selector\": \"" + SELECTOR + "\",\n"
        + "  \"scene_type\": \"" + escapeJson(sceneType) + "\",\n"
        + "  \"method_created\": " + methodCreated + "\n"
        + "}\n";
  }

  private static String successStatus(Config config, List<StepResult> steps) {
    return "outcome=passed\n"
        + "executionStatus=executed\n"
        + "executionClaim=eatme-object-transform-workflow-executed\n"
        + "workflowArtifact=" + WORKFLOW_ARTIFACT + "\n"
        + "selector=" + SELECTOR + "\n"
        + "object=" + OBJECT_IDENTIFIER + "\n"
        + "timeoutSeconds=" + config.timeoutSeconds() + "\n"
        + "stepCount=" + steps.size() + "\n";
  }

  private static String writeFailureEvidence(Path requestedOutDir, WorkflowFailure failure) {
    if (requestedOutDir == null) {
      return "failure evidence was not written because --out-dir was not provided";
    }
    try {
      Files.createDirectories(requestedOutDir);
      if (!"preflight".equals(failure.failedStep())) {
        Path stepDir = requestedOutDir.resolve(failure.failedStep());
        Files.createDirectories(stepDir);
        writeArtifact(stepDir.resolve(STEP_FAILURE_ARTIFACT), stepFailureJson(failure));
      }
      writeArtifact(requestedOutDir.resolve(FAILURE_ARTIFACT), failureJson(failure));
      writeArtifact(requestedOutDir.resolve(STATUS_ARTIFACT), failureStatus(failure));
      return null;
    } catch (IOException | RuntimeException ignored) {
      return "failure evidence was not written because the evidence directory is unusable: " + ignored.getMessage();
    }
  }

  private static String failureJson(WorkflowFailure failure) {
    return "{"
        + "\"schema_version\":\"" + FAILURE_SCHEMA + "\","
        + "\"status\":\"failed\","
        + "\"failure_kind\":\"" + escapeJson(failure.failureKind()) + "\","
        + "\"failed_step\":\"" + escapeJson(failure.failedStep()) + "\","
        + "\"message\":\"" + escapeJson(failure.getMessage()) + "\""
        + "}\n";
  }

  private static String stepFailureJson(WorkflowFailure failure) {
    return "{"
        + "\"schema_version\":\"eatme.object-transform-workflow-step-failure/v1\","
        + "\"status\":\"failed\","
        + "\"failure_kind\":\"" + escapeJson(failure.failureKind()) + "\","
        + "\"step\":\"" + escapeJson(failure.failedStep()) + "\","
        + "\"message\":\"" + escapeJson(failure.getMessage()) + "\""
        + "}\n";
  }

  private static String failureStatus(WorkflowFailure failure) {
    return "outcome=failed\n"
        + "executionStatus=failed\n"
        + "executionClaim=eatme-object-transform-workflow-failed\n"
        + "failureKind=" + failure.failureKind() + "\n"
        + "failedStep=" + failure.failedStep() + "\n"
        + "message=" + failure.getMessage() + "\n";
  }

  private static void writeArtifact(Path target, String content) throws IOException {
    Files.createDirectories(target.getParent());
    EatmeEvidenceWriter.writeStringAtomically(target, content);
    EatmeEvidenceWriter.requireNonEmptyArtifact(target, target.getFileName().toString());
  }

  private static Path outDirFromArgs(String[] args) {
    for (int i = 0; i < args.length; i++) {
      if ("--out-dir".equals(args[i]) && i + 1 < args.length && !args[i + 1].startsWith("--")) {
        return Path.of(args[i + 1]).toAbsolutePath().normalize();
      }
    }
    return null;
  }

  private static String messageFor(Throwable throwable) {
    if (throwable == null) {
      return "step failed";
    }
    String message = throwable.getMessage();
    return message == null || message.isBlank() ? throwable.getClass().getSimpleName() : message;
  }

  private static String jsonArray(List<String> values) {
    StringBuilder builder = new StringBuilder("[");
    for (int i = 0; i < values.size(); i++) {
      if (i > 0) {
        builder.append(',');
      }
      builder.append('"').append(escapeJson(values.get(i))).append('"');
    }
    return builder.append(']').toString();
  }

  private static String escapeJson(String value) {
    StringBuilder escaped = new StringBuilder(value.length());
    for (int i = 0; i < value.length(); i++) {
      char ch = value.charAt(i);
      switch (ch) {
        case '\\' -> escaped.append("\\\\");
        case '"' -> escaped.append("\\\"");
        case '\b' -> escaped.append("\\b");
        case '\f' -> escaped.append("\\f");
        case '\n' -> escaped.append("\\n");
        case '\r' -> escaped.append("\\r");
        case '\t' -> escaped.append("\\t");
        default -> {
          if (ch < 0x20) {
            escaped.append(String.format("\\u%04x", (int) ch));
          } else {
            escaped.append(ch);
          }
        }
      }
    }
    return escaped.toString();
  }

  private interface ThrowingRunnable {
    void run() throws Exception;
  }

  private record Config(Path sourceProject, Path outDir, int timeoutSeconds) {
    static Config parse(String[] args) throws WorkflowFailure {
      Path sourceProject = null;
      Path outDir = null;
      int timeoutSeconds = DEFAULT_TIMEOUT_SECONDS;
      boolean json = false;
      for (int i = 0; i < args.length; i++) {
        switch (args[i]) {
          case "--source-project" -> sourceProject = Path.of(nextValue(args, ++i, "--source-project"))
              .toAbsolutePath().normalize();
          case "--out-dir" -> outDir = Path.of(nextValue(args, ++i, "--out-dir")).toAbsolutePath().normalize();
          case "--timeout-seconds" -> timeoutSeconds = parseTimeout(nextValue(args, ++i, "--timeout-seconds"));
          case "--json" -> json = true;
          default -> throw new WorkflowFailure("argument-error", "preflight", "unexpected argument: " + args[i], 2);
        }
      }
      if (sourceProject == null) {
        throw new WorkflowFailure("argument-error", "preflight", "--source-project is required", 2);
      }
      if (outDir == null) {
        throw new WorkflowFailure("argument-error", "preflight", "--out-dir is required", 2);
      }
      if (!json) {
        throw new WorkflowFailure("argument-error", "preflight", "--json is required", 2);
      }
      return new Config(sourceProject, outDir, timeoutSeconds);
    }

    private static String nextValue(String[] args, int index, String option) throws WorkflowFailure {
      if (index >= args.length || args[index].startsWith("--")) {
        throw new WorkflowFailure("argument-error", "preflight", option + " requires a value", 2);
      }
      return args[index];
    }

    private static int parseTimeout(String value) throws WorkflowFailure {
      try {
        int parsed = Integer.parseInt(value);
        if (parsed <= 0) {
          throw new WorkflowFailure("argument-error", "preflight",
              "--timeout-seconds must be a positive integer: " + value, 2);
        }
        return parsed;
      } catch (NumberFormatException ex) {
        throw new WorkflowFailure("argument-error", "preflight",
            "--timeout-seconds must be a positive integer: " + value, 2, ex);
      }
    }
  }

  private record StepResult(String name, String status, long durationMillis, List<String> artifacts) {
  }

  private static final class WorkflowFailure extends Exception {
    private final String failureKind;
    private final String failedStep;
    private final int exitCode;

    WorkflowFailure(String failureKind, String failedStep, String message, int exitCode) {
      super(message);
      this.failureKind = failureKind;
      this.failedStep = failedStep;
      this.exitCode = exitCode;
    }

    WorkflowFailure(String failureKind, String failedStep, String message, int exitCode, Throwable cause) {
      super(message, cause);
      this.failureKind = failureKind;
      this.failedStep = failedStep;
      this.exitCode = exitCode;
    }

    String failureKind() {
      return failureKind;
    }

    String failedStep() {
      return failedStep;
    }

    int exitCode() {
      return exitCode;
    }
  }

  private static final class DaemonThreadFactory implements ThreadFactory {
    private final String name;

    DaemonThreadFactory(String name) {
      this.name = name;
    }

    @Override
    public Thread newThread(Runnable runnable) {
      Thread thread = new Thread(runnable, name);
      thread.setDaemon(true);
      return thread;
    }
  }
}
