# Review Standalone Project Test Reliability

Use this guide to verify the `runCommand()` process stream drain and the macOS
xvfb-run skip guard in `ProjectCodeGeneratorStandaloneProjectTest`.

## Contents

- [Prerequisites](#prerequisites)
- [Run the validation](#run-the-validation)
- [Review the runCommand drain thread](#review-the-runcommand-drain-thread)
- [Review the macOS xvfb-run guard](#review-the-macos-xvfb-run-guard)
- [Add a new process-forking test helper](#add-a-new-process-forking-test-helper)
- [Add a new platform skip guard](#add-a-new-platform-skip-guard)
- [Common mistakes](#common-mistakes)

## Prerequisites

Run commands from the repository root. Initialize the grammar submodule:

```bash
git submodule update --init tweedle-lang
test -d tweedle-lang/Grammar
```

If a surrounding Node-based orchestrator is running:

```bash
export NODE_OPTIONS=--max-old-space-size=32768
```

## Run the validation

Run the affected test class:

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl netbeans -am \
  -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Dtest=ProjectCodeGeneratorStandaloneProjectTest \
  test -q
```

On macOS, expect the Xvfb display test to skip (macOS guard). On Linux without
Xvfb, expect the Xvfb display test to skip (xvfb-run not on PATH). On Linux
with Xvfb, expect all tests to pass. On all platforms, `runCommand()` tests
should never throw `IOException: Stream closed`.

## Review the runCommand drain thread

Open `ProjectCodeGeneratorStandaloneProjectTest.java` and find the
`runCommand` method.

Confirm the method follows this structure:

1. **`import java.io.IOException`** is present in the file's import block
   (required for the catch clause in the drain thread).

2. **Process starts** with `redirectErrorStream(true)`.

3. **Drain thread starts immediately** after process creation, before
   `waitFor()`:

   ```java
   ByteArrayOutputStream drainBuffer = new ByteArrayOutputStream();
   Thread drainer = new Thread(() -> {
     try {
       process.getInputStream().transferTo(drainBuffer);
     } catch (IOException ignored) {
     }
   });
   drainer.setDaemon(true);
   drainer.start();
   ```

4. **`waitFor(10, TimeUnit.SECONDS)`** runs while the drain thread captures
   output concurrently.

5. **If timeout**: `destroyForcibly()` kills the process, then
   `waitFor(5, TimeUnit.SECONDS)` confirms termination.

6. **`drainer.join(5000)`** waits for the drain thread to finish reading.

7. **Output read from buffer**: `drainBuffer.toString(StandardCharsets.UTF_8)`
   — never from `process.getInputStream()`.

Check that:

- The drain thread is a **daemon** thread (`setDaemon(true)`).
- The `IOException` in the drain thread is caught and **ignored** (not
  rethrown). The partial buffer content is preserved.
- The output `String` is constructed from the `ByteArrayOutputStream`, not
  from the process input stream.
- `process.getInputStream().readAllBytes()` does **not** appear anywhere in
  the method.

## Review the macOS xvfb-run guard

Open `ProjectCodeGeneratorStandaloneProjectTest.java` and find the
`templatePackagedLauncherWithRealJavaFxModulesRunsOnXvfbDisplay` method.

Confirm the first lines are:

```java
Path xvfbRun = findExecutableOnPath("xvfb-run");
org.junit.Assume.assumeTrue(
    "xvfb-run is required to prove the real JavaFX display launch path",
    xvfbRun != null);
org.junit.Assume.assumeFalse(
    "xvfb-run is unreliable on macOS — skipping virtual display test",
    System.getProperty("os.name").toLowerCase().contains("mac"));
```

Check that:

- The `assumeFalse` guard is **after** the `assumeTrue` guard (xvfb-run
  existence is checked first).
- The macOS detection uses `System.getProperty("os.name").toLowerCase().contains("mac")`.
- Both guards are **before** any test setup (temporary folder creation, file
  extraction, compilation, etc.).

## Add a new process-forking test helper

When writing a new test helper that forks a process and needs to capture output:

1. Create a `ByteArrayOutputStream` for the drain buffer.
2. Start a daemon thread that calls `process.getInputStream().transferTo(buffer)`.
3. Catch `IOException` in the drain thread — do not rethrow.
4. Call `process.waitFor(timeout, unit)` after starting the drain thread.
5. If the process times out, call `destroyForcibly()` then `waitFor()` to
   confirm death.
6. Call `drainer.join(timeout)` to flush the buffer.
7. Read output from `buffer.toString(charset)`, never from the process stream.

Example:

```java
private static String captureOutput(Process process, long timeoutSeconds) throws Exception {
  ByteArrayOutputStream buffer = new ByteArrayOutputStream();
  Thread drainer = new Thread(() -> {
    try {
      process.getInputStream().transferTo(buffer);
    } catch (IOException ignored) {
    }
  });
  drainer.setDaemon(true);
  drainer.start();
  boolean exited = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
  if (!exited) {
    process.destroyForcibly();
    process.waitFor(5, TimeUnit.SECONDS);
  }
  drainer.join(5000);
  return buffer.toString(StandardCharsets.UTF_8);
}
```

## Add a new platform skip guard

When writing a test that depends on a platform-specific external tool:

1. First check that the tool exists on PATH with `assumeTrue`.
2. Then add `assumeFalse` guards for platforms where the tool is unreliable.
3. Place all `assume` guards at the top of the test method, before any setup.
4. Use descriptive messages that explain why the test skips.

Example for a test that requires `xdg-open` (Linux-only):

```java
@Test
public void myLinuxOnlyTest() throws Exception {
  Path tool = findExecutableOnPath("xdg-open");
  assumeTrue("xdg-open required", tool != null);
  assumeFalse("xdg-open is Linux-only",
      System.getProperty("os.name").toLowerCase().contains("mac"));
  assumeFalse("xdg-open is Linux-only",
      System.getProperty("os.name").toLowerCase().contains("win"));
  // ... test body ...
}
```

## Common mistakes

| Mistake | Why it's wrong | Fix |
| --- | --- | --- |
| `process.getInputStream().readAllBytes()` after `destroyForcibly()` | The OS may close the pipe, causing `IOException: Stream closed`. | Drain via a thread into `ByteArrayOutputStream` before `waitFor()`. |
| Non-daemon drain thread | Can block JVM shutdown if `join()` times out. | Call `drainer.setDaemon(true)`. |
| Rethrowing `IOException` from drain thread | Propagates an expected exception when `destroyForcibly()` closes the pipe. | Catch and ignore — partial output is in the buffer. |
| Reading output from `process.getInputStream()` after `join()` | The stream may be closed even after the drain thread finishes. | Read from `drainBuffer.toString()`. |
| Relying on `xvfb-run` existing on PATH as proof it works | macOS may have `xvfb-run` installed but it does not provide a usable virtual display. | Add `assumeFalse` for macOS after the `assumeTrue` PATH check. |
| `assumeTrue(!isMac())` instead of `assumeFalse(isMac())` | Double negation is confusing and error-prone. | Use `assumeFalse` with a positive platform check. |
| Platform guard after test setup | Wastes time creating files/folders before discovering the test should skip. | Put all `assume` guards at the top of the method. |
| Missing `join()` on drain thread | Output buffer may be incomplete when read. | Call `drainer.join(timeout)` after the process exits. |
| Missing `import java.io.IOException` | Catch clause in drain thread won't compile without the import. | Add `import java.io.IOException;` to the imports block. |
