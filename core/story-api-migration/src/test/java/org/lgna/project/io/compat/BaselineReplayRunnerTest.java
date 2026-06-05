package org.lgna.project.io.compat;

import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.lgna.project.Project;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.story.SProgram;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

public class BaselineReplayRunnerTest {
  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void runsBaselineSummaryScriptWithCaseSchemaAndCheckoutWorkingDirectory() throws Exception {
    assumePosixExecutableScripts();
    Path checkoutRoot = temporaryFolder.newFolder("baseline").toPath();
    installBaselineScript(checkoutRoot, """
        #!/usr/bin/env sh
        printf '%s\\n' "$(pwd)" > invoked.cwd
        if [ "$1" != "--case" ] || [ "$2" != "empty-project" ]; then
          echo "bad case arguments" >&2
          exit 2
        fi
        if [ "$3" != "--schema" ] || [ "$4" != "rabbithole.dual-baseline-summary/v1" ]; then
          echo "bad schema arguments" >&2
          exit 3
        fi
        printf 'case: empty-project\\n'
        printf 'summary-schema: rabbithole.dual-baseline-summary/v1\\n\\n'
        printf '[source-tree]\\n'
        """);
    ReplayCase replayCase = new ReplayCase("empty-project", project("EmptyProject"), List.of());

    ReplaySummary summary = new BaselineReplayRunner(
        new BaselineCheckout(checkoutRoot),
        Duration.ofSeconds(5)).summarize(replayCase);

    assertEquals("empty-project", summary.caseId());
    assertEquals("Baseline", summary.providerName());
    assertEquals(checkoutRoot.toRealPath().toString(),
        Files.readString(checkoutRoot.resolve("invoked.cwd"), StandardCharsets.UTF_8).trim());
  }

  @Test
  public void rejectsUnregisteredCaseIdBeforeLaunchingBaselineProcess() throws Exception {
    assumePosixExecutableScripts();
    Path checkoutRoot = temporaryFolder.newFolder("baseline-unregistered").toPath();
    installBaselineScript(checkoutRoot, """
        #!/usr/bin/env sh
        touch should-not-run
        exit 0
        """);
    ReplayCase replayCase = new ReplayCase("unregistered-case", project("UnregisteredCase"), List.of());

    IOException thrown = assertThrows(
        IOException.class,
        () -> new BaselineReplayRunner(new BaselineCheckout(checkoutRoot), Duration.ofSeconds(5)).summarize(replayCase));

    assertTrue(thrown.getMessage().contains("unregistered-case"));
    assertFalse(Files.exists(checkoutRoot.resolve("should-not-run")));
  }

  @Test
  public void nonZeroExitIncludesCaseIdExitCodeAndStderr() throws Exception {
    assumePosixExecutableScripts();
    Path checkoutRoot = temporaryFolder.newFolder("baseline-fails").toPath();
    installBaselineScript(checkoutRoot, """
        #!/usr/bin/env sh
        echo "baseline exploded" >&2
        exit 7
        """);
    ReplayCase replayCase = new ReplayCase("empty-project", project("EmptyProject"), List.of());

    IOException thrown = assertThrows(
        IOException.class,
        () -> new BaselineReplayRunner(new BaselineCheckout(checkoutRoot), Duration.ofSeconds(5)).summarize(replayCase));

    assertTrue(thrown.getMessage().contains("empty-project"));
    assertTrue(thrown.getMessage().contains("7"));
    assertTrue(thrown.getMessage().contains("baseline exploded"));
  }

  @Test
  public void malformedStdoutFailsBeforeComparison() throws Exception {
    assumePosixExecutableScripts();
    Path checkoutRoot = temporaryFolder.newFolder("baseline-malformed").toPath();
    installBaselineScript(checkoutRoot, """
        #!/usr/bin/env sh
        printf 'this is not a replay summary\\n'
        """);
    ReplayCase replayCase = new ReplayCase("empty-project", project("EmptyProject"), List.of());

    IOException thrown = assertThrows(
        IOException.class,
        () -> new BaselineReplayRunner(new BaselineCheckout(checkoutRoot), Duration.ofSeconds(5)).summarize(replayCase));

    assertTrue(thrown.getMessage().contains("empty-project"));
    assertTrue(thrown.getMessage().contains("malformed"));
  }

  @Test
  public void timeoutFailsStrictModeAndDoesNotComparePartialStdout() throws Exception {
    assumePosixExecutableScripts();
    Path checkoutRoot = temporaryFolder.newFolder("baseline-timeout").toPath();
    installBaselineScript(checkoutRoot, """
        #!/usr/bin/env sh
        printf 'case: empty-project\\n'
        sleep 5
        printf 'summary-schema: rabbithole.dual-baseline-summary/v1\\n'
        """);
    ReplayCase replayCase = new ReplayCase("empty-project", project("EmptyProject"), List.of());

    IOException thrown = assertThrows(
        IOException.class,
        () -> new BaselineReplayRunner(new BaselineCheckout(checkoutRoot), Duration.ofSeconds(1)).summarize(replayCase));

    assertTrue(thrown.getMessage().contains("empty-project"));
    assertTrue(thrown.getMessage().contains("timed out"));
    assertTrue(thrown.getMessage().contains("1"));
  }

  @Test
  public void missingBaselineScriptIsHardFailure() throws Exception {
    Path checkoutRoot = temporaryFolder.newFolder("baseline-without-script").toPath();
    ReplayCase replayCase = new ReplayCase("empty-project", project("EmptyProject"), List.of());

    IOException thrown = assertThrows(
        IOException.class,
        () -> new BaselineReplayRunner(new BaselineCheckout(checkoutRoot), Duration.ofSeconds(5)).summarize(replayCase));

    assertTrue(thrown.getMessage().contains("scripts/rabbithole-replay-summary"));
  }

  private static void assumePosixExecutableScripts() {
    Assume.assumeFalse(System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win"));
  }

  private static void installBaselineScript(Path checkoutRoot, String scriptText) throws IOException {
    Path script = checkoutRoot.resolve("scripts").resolve("rabbithole-replay-summary");
    Files.createDirectories(script.getParent());
    Files.writeString(script, scriptText, StandardCharsets.UTF_8);
    assertTrue("Unable to mark fake baseline script executable.", script.toFile().setExecutable(true));
  }

  private static Project project(String name) {
    NamedUserType programType = new NamedUserType();
    programType.name.setValue(name);
    programType.superType.setValue(JavaType.getInstance(SProgram.class));
    return new Project(programType, Project.SceneCameraType.WindowCamera);
  }
}
