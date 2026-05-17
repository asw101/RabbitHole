# Standalone Project Test: Process Stream Drain and macOS Guards

This reference documents three reliability fixes in
`ProjectCodeGeneratorStandaloneProjectTest`: the `runCommand()` process stream
drain that prevents `IOException: Stream closed` on force-killed processes, the
macOS `assumeFalse` guard on the Xvfb display test, and the macOS `assumeFalse`
guard on the headless display boundary test. All are test-only changes. No
production code is modified.

## Contents

- [Scope](#scope)
- [Artifact inventory](#artifact-inventory)
- [Bug 1: runCommand() race condition](#bug-1-runcommand-race-condition)
- [Bug 2: macOS xvfb-run guard](#bug-2-macos-xvfb-run-guard)
- [Bug 3: macOS headless display boundary guard](#bug-3-macos-headless-display-boundary-guard)
- [API reference](#api-reference)
- [Configuration](#configuration)
- [Validation commands](#validation-commands)
- [Examples](#examples)
- [Compatibility rules](#compatibility-rules)
- [Non-claims](#non-claims)

## Scope

| Issue | Method | Problem |
| --- | --- | --- |
| #726 (bug 1) | `runCommand()` | After `destroyForcibly()`, `process.getInputStream().readAllBytes()` throws `IOException: Stream closed` because the OS reclaims the pipe before the read. The failure is timing-dependent and surfaces primarily on macOS CI. |
| #726 (bug 2) | `templatePackagedLauncherWithRealJavaFxModulesRunsOnXvfbDisplay()` | `xvfb-run` behaves differently on macOS even when present on PATH. The test passes the `assumeTrue(xvfbRun != null)` guard but then fails because the macOS `xvfb-run` wrapper does not provide a usable virtual display. |
| #731 | `templatePackagedLauncherWithRealJavaFxModulesStopsAtDisplayBoundaryWhenHeadless()` | On macOS CI runners, the display server is available even in headless mode. JavaFX starts successfully instead of failing with `Unable to open DISPLAY`, so the test's assertion that the process fails at the display boundary is never satisfied. |

All changes are in a single test file. No production code is modified.

## Artifact inventory

| File | Purpose |
| --- | --- |
| `netbeans/src/test/java/org/alice/netbeans/project/ProjectCodeGeneratorStandaloneProjectTest.java` | Test class for exported standalone project code generation. Contains `runCommand()` helper, `templatePackagedLauncherWithRealJavaFxModulesRunsOnXvfbDisplay()` test method with macOS xvfb-run guard, and `templatePackagedLauncherWithRealJavaFxModulesStopsAtDisplayBoundaryWhenHeadless()` test method with macOS display-available guard. Requires `import java.io.IOException` for the drain thread catch clause. |

## Bug 1: runCommand() race condition

### Before (flaky)

```java
private static ProcessResult runCommand(Path workingDirectory, List<String> command) throws Exception {
  Process process = new ProcessBuilder(command)
      .directory(workingDirectory.toFile())
      .redirectErrorStream(true)
      .start();
  boolean exited = process.waitFor(10, TimeUnit.SECONDS);
  if (!exited) {
    process.destroyForcibly();
    assertTrue("Timed out waiting for forked java to terminate", process.waitFor(5, TimeUnit.SECONDS));
  }
  String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
  return new ProcessResult(exited ? process.exitValue() : -1, output, !exited);
}
```

The `readAllBytes()` call occurs after `destroyForcibly()`. On macOS (and
occasionally on Linux), the OS closes the process pipe when the process is
force-killed. `readAllBytes()` then throws `IOException: Stream closed`. The
failure is timing-dependent: it occurs when the OS reclaims the pipe file
descriptor before the JVM's `readAllBytes()` opens or completes its read.

### After (fixed)

**Required import** (not present in the original file):

```java
import java.io.IOException;
```

```java
private static ProcessResult runCommand(Path workingDirectory, List<String> command) throws Exception {
  Process process = new ProcessBuilder(command)
      .directory(workingDirectory.toFile())
      .redirectErrorStream(true)
      .start();
  ByteArrayOutputStream drainBuffer = new ByteArrayOutputStream(4096);
  Thread drainThread = new Thread(() -> {
    byte[] buf = new byte[8192];
    try {
      int n;
      while ((n = process.getInputStream().read(buf)) != -1) {
        drainBuffer.write(buf, 0, n);
      }
    } catch (IOException ignored) {
      // Stream closed by destroyForcibly — partial output preserved in drainBuffer
    }
  }, "process-stdout-drain");
  drainThread.setDaemon(true);
  drainThread.start();
  boolean exited = process.waitFor(10, TimeUnit.SECONDS);
  if (!exited) {
    process.destroyForcibly();
    assertTrue("Timed out waiting for forked java to terminate", process.waitFor(5, TimeUnit.SECONDS));
  }
  drainThread.join(5000);
  String output = drainBuffer.toString(StandardCharsets.UTF_8);
  return new ProcessResult(exited ? process.exitValue() : -1, output, !exited);
}
```

Key changes:

1. **Drain thread starts before `waitFor()`**: A daemon thread reads from the
   process input stream into a `ByteArrayOutputStream` as soon as the process
   starts. This captures output continuously rather than attempting a single
   bulk read after the process exits.

2. **`IOException` caught in drain thread**: When `destroyForcibly()` closes the
   process pipe, the drain thread's `read()` may throw `IOException`. The
   catch block swallows it because partial output is already preserved in the
   buffer.

3. **`drainThread.join(5000)` after process exit**: After the process exits (or
   is force-killed and confirmed terminated), the main thread joins the drain
   thread with a 5-second timeout. This ensures the buffer is flushed before
   reading.

4. **Output read from buffer, not process stream**: `drainBuffer.toString()`
   replaces `process.getInputStream().readAllBytes()`. The buffer is always
   readable regardless of process stream state.

5. **Daemon flag**: The drain thread is marked as a daemon so it cannot block
   JVM shutdown if `join()` times out.

### Why the original code failed

The Java `Process.getInputStream()` returns a `ProcessPipeInputStream` backed
by a file descriptor. When `destroyForcibly()` kills the process, the OS may
close the pipe's file descriptor. The subsequent `readAllBytes()` call then
fails because the underlying file descriptor is no longer valid. This is a
documented JDK behavior: there is no guarantee that the process stream remains
readable after `destroyForcibly()`.

The fix follows the JDK-recommended pattern: drain the stream concurrently
with the process execution, so the buffer captures whatever output was produced
before the process was killed.

## Bug 2: macOS xvfb-run guard

### Before (flaky on macOS)

```java
@Test
public void templatePackagedLauncherWithRealJavaFxModulesRunsOnXvfbDisplay() throws Exception {
  Path xvfbRun = findExecutableOnPath("xvfb-run");
  org.junit.Assume.assumeTrue(
      "xvfb-run is required to prove the real JavaFX display launch path",
      xvfbRun != null);

  Path projectDirectory = temporaryFolder.newFolder("template-real-javafx-xvfb-runtime").toPath();
  // ...
}
```

On macOS, `xvfb-run` may exist on PATH (e.g., installed via Homebrew or Nix)
but does not provide a usable Xvfb virtual display. The macOS implementation
of `xvfb-run` differs from the Linux version: it may fail to create a virtual
framebuffer, produce X11 errors, or exit with a non-zero status before the
Java process starts. The existing `assumeTrue(xvfbRun != null)` guard passes
because the executable is found, but the test then fails when the forked Java
process cannot connect to the display.

### After (fixed)

```java
@Test
public void templatePackagedLauncherWithRealJavaFxModulesRunsOnXvfbDisplay() throws Exception {
  Path xvfbRun = findExecutableOnPath("xvfb-run");
  org.junit.Assume.assumeTrue(
      "xvfb-run is required to prove the real JavaFX display launch path",
      xvfbRun != null);
  org.junit.Assume.assumeFalse(
      "xvfb-run behaves differently on macOS even if found on PATH",
      System.getProperty("os.name").toLowerCase().contains("mac"));

  Path projectDirectory = temporaryFolder.newFolder("template-real-javafx-xvfb-runtime").toPath();
  // ...
}
```

The `assumeFalse` guard skips the test on macOS (any variant: macOS, Mac OS X).
JUnit reports the test as **skipped**, not failed or passed. The guard is
placed immediately after the existing `assumeTrue` so both preconditions are
checked before any test work begins.

### Why `assumeFalse` instead of fixing xvfb-run

The `xvfb-run` tool is a Linux-specific wrapper around `Xvfb`. On macOS, even
when installed, it cannot reliably provide a virtual X11 display because macOS
does not ship Xvfb or the X11 server infrastructure that `xvfb-run` depends
on. XQuartz provides partial X11 support, but its behavior differs from Linux
Xvfb in ways that make the test unreliable. Skipping is the correct behavior
because the test is proving a Linux CI virtual display path, not macOS display
compatibility.

## Bug 3: macOS headless display boundary guard

### Before (flaky on macOS CI)

```java
@Test
public void templatePackagedLauncherWithRealJavaFxModulesStopsAtDisplayBoundaryWhenHeadless() throws Exception {
  Path projectDirectory = temporaryFolder.newFolder("template-real-javafx-runtime").toPath();
  extractProjectTemplate(projectDirectory);
  // ... builds and launches JavaFX app expecting "Unable to open DISPLAY" failure ...
}
```

The test verifies that a packaged launcher with real JavaFX modules fails at
the display boundary when no display server is available. On Linux headless CI,
this works: the forked Java process attempts to initialize JavaFX, fails with
`Unable to open DISPLAY`, and exits with a non-zero exit code. The test
asserts this failure.

On macOS CI runners, however, the macOS display server (WindowServer) is
available even in nominally headless environments. JavaFX initializes
successfully and the process does not fail at the display boundary. The test's
assertion — that the process exits with a non-zero code containing
display-related error output — is never satisfied, causing a spurious test
failure.

### After (fixed)

```java
@Test
public void templatePackagedLauncherWithRealJavaFxModulesStopsAtDisplayBoundaryWhenHeadless() throws Exception {
  org.junit.Assume.assumeFalse(
      "macOS CI display is available even headless; JavaFX starts instead of failing",
      System.getProperty("os.name").toLowerCase().contains("mac"));
  Path projectDirectory = temporaryFolder.newFolder("template-real-javafx-runtime").toPath();
  extractProjectTemplate(projectDirectory);
  // ... rest of test unchanged ...
}
```

The `assumeFalse` guard skips the test on macOS. JUnit reports the test as
**skipped**, not failed or passed. The guard is placed immediately after the
method signature, before any setup or project extraction, following the same
pattern as the sibling Xvfb display test guard at line 424–426.

### Pattern details

| Aspect | Value |
| --- | --- |
| Guard type | `org.junit.Assume.assumeFalse` (fully-qualified, no import needed) |
| Condition | `System.getProperty("os.name").toLowerCase().contains("mac")` |
| Message | `"macOS CI display is available even headless; JavaFX starts instead of failing"` |
| Placement | First statement after method signature, before any setup |
| Sibling guard | `templatePackagedLauncherWithRealJavaFxModulesRunsOnXvfbDisplay()` at line 424–426 uses identical `os.name` detection pattern |

### Why `assumeFalse` instead of fixing the test assertion

The test's purpose is to prove that a headless Linux CI environment without a
display server causes JavaFX to fail at the display initialization boundary.
This is a Linux-specific CI reliability contract. On macOS, the display server
being available is not a bug — it is correct platform behavior. The test cannot
meaningfully assert "no display available" on a platform where a display is
always available. Skipping is the correct behavior because the test is proving
a Linux headless CI contract, not macOS display absence.

### Relationship to sibling guard

The `templatePackagedLauncherWithRealJavaFxModulesRunsOnXvfbDisplay()` test
(documented in [Bug 2](#bug-2-macos-xvfb-run-guard) above) also skips on macOS
using the same `assumeFalse` pattern. Both guards share:

- Identical `os.name` detection: `System.getProperty("os.name").toLowerCase().contains("mac")`
- Fully-qualified `org.junit.Assume.assumeFalse` (no import statement needed)
- Descriptive message explaining the macOS-specific reason for skipping
- Placement before any test work begins

The two tests verify complementary aspects of JavaFX display behavior:

| Test | What it proves | Why it skips on macOS |
| --- | --- | --- |
| `...StopsAtDisplayBoundaryWhenHeadless` | JavaFX fails without a display server | macOS always has a display server |
| `...RunsOnXvfbDisplay` | JavaFX succeeds with Xvfb virtual display | `xvfb-run` is unreliable on macOS |

## API reference

### `runCommand(Path workingDirectory, List<String> command) → ProcessResult`

| Parameter | Type | Description |
| --- | --- | --- |
| `workingDirectory` | `Path` | Working directory for the forked process. |
| `command` | `List<String>` | Command and arguments to execute. |
| **Returns** | `ProcessResult` | Contains exit code, captured output, and timeout flag. |
| **Throws** | `Exception` | If the process cannot be started or the drain thread is interrupted. |

The method starts a daemon drain thread on the process stdout+stderr stream
(merged via `redirectErrorStream(true)`). The process is given 10 seconds to
complete. If it does not exit within 10 seconds, it is force-killed and the
method waits up to 5 additional seconds for termination. Output is always read
from the drain buffer, never from the process stream directly.

### `ProcessResult`

| Field | Type | Description |
| --- | --- | --- |
| `exitCode` | `int` | Process exit code, or `-1` if the process timed out. |
| `output` | `String` | Captured stdout+stderr content (may be partial on timeout). |
| `timedOut` | `boolean` | `true` if the process did not exit within the 10-second window. |

### `findExecutableOnPath(String executableName) → Path`

Searches each entry in the `PATH` environment variable for an executable file
matching the given name. Returns the first match, or `null` if not found.

## Configuration

No new configuration is required. The test uses the same Maven profiles and
JVM properties as before:

```bash
git submodule update --init tweedle-lang
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl netbeans -am \
  -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Dtest=ProjectCodeGeneratorStandaloneProjectTest \
  test
```

### Platform behavior matrix

| Environment | `runCommand()` | Headless display boundary test | Xvfb display test |
| --- | --- | --- | --- |
| Linux headless CI (no Xvfb) | **Pass** (drain thread captures output) | **Pass** (JavaFX fails without display) | **Skip** (xvfb-run not on PATH) |
| Linux CI with Xvfb | **Pass** | **Pass** (display available → takes success path) | **Pass** |
| macOS CI (any) | **Pass** (drain thread prevents `IOException`) | **Skip** (macOS guard) | **Skip** (macOS guard) |
| macOS local with Xvfb on PATH | **Pass** | **Skip** (macOS guard) | **Skip** (macOS guard) |
| Windows CI | **Pass** | **Pass** (no display server in headless) | **Skip** (xvfb-run not on PATH) |

## Validation commands

Run the affected test class:

```bash
git submodule update --init tweedle-lang
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl netbeans -am \
  -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Dtest=ProjectCodeGeneratorStandaloneProjectTest \
  test -q
```

To run only the Xvfb display test (to verify the skip behavior on macOS):

```bash
mvn -pl netbeans -am \
  -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Dtest=ProjectCodeGeneratorStandaloneProjectTest#templatePackagedLauncherWithRealJavaFxModulesRunsOnXvfbDisplay \
  test -q
```

To run only the headless display boundary test (to verify the skip behavior on macOS):

```bash
mvn -pl netbeans -am \
  -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Dtest=ProjectCodeGeneratorStandaloneProjectTest#templatePackagedLauncherWithRealJavaFxModulesStopsAtDisplayBoundaryWhenHeadless \
  test -q
```

## Examples

### Linux CI without Xvfb

```text
Tests run: N, Failures: 0, Errors: 0, Skipped: 1
```

The Xvfb display test skips because `xvfb-run` is not on PATH. The headless
display boundary test passes (JavaFX correctly fails without a display server).
All other tests pass. The `runCommand()` drain thread captures process output
without race conditions.

### macOS CI

```text
Tests run: N, Failures: 0, Errors: 0, Skipped: 2
```

Two tests skip on macOS: the Xvfb display test (macOS `xvfb-run` guard) and
the headless display boundary test (macOS display-available guard). Both report
as **skipped** via JUnit `Assume`. All other tests pass. `runCommand()` no
longer throws `IOException: Stream closed` after `destroyForcibly()`.

### Linux CI with Xvfb

```text
Tests run: N, Failures: 0, Errors: 0, Skipped: 0
```

Both display tests pass. The Xvfb display test passes (virtual display
available via `xvfb-run`). The headless display boundary test also passes —
when `DISPLAY` is set by the CI environment, JavaFX starts successfully and
the test takes the display-available success path (verifying the full rendering
pipeline). The `runCommand()` drain thread operates normally — on a clean exit,
the read loop completes without `IOException` and the buffer contains full
output.

## Compatibility rules

1. **Always drain process output before `destroyForcibly()`**. Never call
   `readAllBytes()` or `read()` on a process stream after force-killing the
   process. Start a drain thread before `waitFor()` and read from the buffer
   after the thread joins.

2. **Use daemon threads for stream draining**. Mark the drain thread as a
   daemon so it cannot block JVM shutdown if `join()` times out or the process
   hangs.

3. **Catch `IOException` in drain threads**. When `destroyForcibly()` closes
   the pipe, the drain thread's read may throw. The catch block preserves
   partial output in the buffer.

4. **Skip platform-incompatible external tool tests with `assumeFalse`**. When
   an external tool (like `xvfb-run`) is unreliable on a platform, or when
   platform behavior invalidates a test's premise (like macOS providing a
   display server in headless environments), add an `assumeFalse` guard for
   that platform. JUnit reports the test as skipped, not passed or failed.

5. **Place `assumeFalse` guards immediately after `assumeTrue` guards**. Keep
   all precondition checks together at the top of the test method before any
   test work begins.

6. **Use `System.getProperty("os.name").toLowerCase().contains("mac")` for
   macOS detection**. This covers both `"Mac OS X"` and `"macOS"` values that
   different JDK versions report.

## Non-claims

These fixes do not prove:

- That `xvfb-run` works correctly on macOS with XQuartz.
- That macOS CI runners are truly headless (macOS WindowServer availability is
  a platform characteristic, not a CI misconfiguration).
- That the exported standalone project renders correctly on any display.
- That `runCommand()` captures all possible process output on all platforms.
  (Output produced between the last `read()` and the pipe close by
  `destroyForcibly()` may be lost. This is acceptable because the test already
  marks `timedOut=true`.)
- Full desktop automation, installer validation, Sims integration, or
  first-lesson completion.
- That the `ProcessResult.output` field is complete when `timedOut` is `true`.
- That the headless display boundary test exercises any meaningful assertion on
  macOS. The test's premise (no display server available) is structurally false
  on macOS.
