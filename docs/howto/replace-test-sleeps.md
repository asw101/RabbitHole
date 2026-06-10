# Replace test sleeps with deterministic waits

> **Status:** Implemented
> **Last reviewed:** 2026-06-10
> **Applies to:** deterministic-wait helpers for Java tests under `**/src/test/**`

Use deterministic waits when a RabbitHole test observes async work, UI state,
generated launcher state, process output, or a background thread.

Direct `Thread.sleep(...)`, `TimeUnit.*.sleep(...)`, `Robot.delay(...)`, and
open-coded polling delays are not used for synchronization outside the
module-local wait helpers. Tests wait for the condition that proves the
behavior, then fail with a bounded timeout if that condition never arrives. The
[sleep inventory](../reference/deterministic-test-waits.md#sleep-inventory-and-disposition)
records the migration disposition and retained semantic-time waits.

## Contents

- [Before you start](#before-you-start)
- [Choose the synchronization signal](#choose-the-synchronization-signal)
- [Wait for event callbacks](#wait-for-event-callbacks)
- [Wait for async state cleanup](#wait-for-async-state-cleanup)
- [Wait for Swing and Robot UI state](#wait-for-swing-and-robot-ui-state)
- [Wait for generated launcher output](#wait-for-generated-launcher-output)
- [Wait for process stream drains](#wait-for-process-stream-drains)
- [Keep semantic-time waits only with rationale](#keep-semantic-time-waits-only-with-rationale)
- [Validate changed tests](#validate-changed-tests)

## Before you start

Read the helper contracts in
[Deterministic test wait reference](../reference/deterministic-test-waits.md).

For broad Maven validation, initialize the Tweedle grammar submodule first:

```bash
git submodule update --init tweedle-lang
```

Use the saved Node memory setting for validation lanes that invoke Node tooling:

```bash
export NODE_OPTIONS=--max-old-space-size=32768
```

## Choose the synchronization signal

Replace a sleep with the smallest observable signal that proves the behavior:

| Test behavior | Wait strategy |
| --- | --- |
| Listener callback must run | `CountDownLatch` or `CompletableFuture` completed by the listener |
| Async state must become idle | bounded condition wait against the state being asserted |
| Swing component must be visible or updated | `SwingUtilities.invokeAndWait` plus `IdeTestWait.until(...)` |
| Robot action must settle | `Robot.waitForIdle()` plus a bounded condition wait |
| Generated launcher must publish evidence | wait for the output marker or test stub field |
| Process output must be drained | future/latch completed by the drain thread, EOF, or process exit |
| Clock or filesystem timestamp semantics are under test | use the module helper's semantic-time sleep with an inline rationale |

Do not wait for "long enough." Wait for the state that matters.

## Wait for event callbacks

Use a latch or future owned by the callback. The helper makes the test fail only
when the callback does not run, not when CI happens to be slower than a fixed
delay.

```java
@Test
public void sceneActivationListenersFireThroughEventManager() throws Exception {
  CountDownLatch delivered = new CountDownLatch(3);
  AtomicInteger fired = new AtomicInteger();

  eventManager.addSceneActivationListener(event -> {
    fired.incrementAndGet();
    delivered.countDown();
  });
  eventManager.addSceneActivationListener(event -> {
    fired.incrementAndGet();
    delivered.countDown();
  });
  eventManager.addSceneActivationListener(event -> {
    fired.incrementAndGet();
    delivered.countDown();
  });

  eventManager.sceneActivated();

  EventTestSupport.await(delivered, "scene activation listeners");
  assertEquals(3, fired.get());
}
```

For removed or silenced listeners, drain the event dispatcher before asserting
absence:

```java
@Test
public void removedListenerDoesNotFire() throws Exception {
  AtomicBoolean called = new AtomicBoolean(false);
  SceneActivationListener listener = event -> called.set(true);

  activationHandler.addListener(listener);
  activationHandler.removeListener(listener);
  activationHandler.handleEventFire(new SceneActivationEvent());

  EventTestSupport.waitForEventDispatchIdle(activationHandler);
  assertFalse("removed listener should not fire", called.get());
}
```

The absence assertion is valid because the test first waits for the dispatcher
to reach an observable idle state.

## Wait for async state cleanup

Use a bounded condition wait for internal state that changes after a worker
thread completes.

```java
handler.dispatch(listener, new TestEvent("boom"));

EventTestSupport.await(fireStarted, "listener fire start");
EventTestSupport.until(
    () -> !activeThings.get(listener),
    "isFiringMap flag to clear after listener failure");

assertFalse("isFiringMap flag stuck true", activeThings.get(listener));
```

The helper restores interruption before failing if the test thread is
interrupted.

## Wait for Swing and Robot UI state

Use the event dispatch thread for Swing reads. Use `Robot.waitForIdle()` for
queued native input, then poll the Swing condition through `invokeAndWait`.

```java
JButton runButton = new JButton("Run");
toolbarFrame.getContentPane().add(runButton);
toolbarFrame.setVisible(true);

IdeTestWait.untilOnEdt(runButton::isShowing, "run button to become visible");

Robot robot = new Robot();
robot.setAutoWaitForIdle(true);
robot.mouseMove(clickX, clickY);
robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
robot.waitForIdle();

IdeTestWait.untilOnEdt(
    () -> runWindowRef.get() != null && runWindowRef.get().isShowing(),
    "run window to appear");
```

Do not read Swing state from the test thread unless the state is explicitly
thread-safe.

## Wait for generated launcher output

Generated launcher tests wait for launcher evidence instead of sleeping after
`main(...)` returns. The `ProjectTestWait.captureSystemOutUntil(...)`
helper captures global `System.out` only for the action scope, restores the
original stream in `finally`, and must not be used concurrently with another
test that captures or asserts global output.

```java
String output = ProjectTestWait.captureSystemOutUntil(
    () -> launcherClass.getMethod("main", String[].class).invoke(null, (Object) args),
    () -> (Boolean) stageClass.getField("showInvoked").get(null),
    "generated JavaFX launcher to show the stage");

assertOutputContainsInOrder(
    output,
    "ALICE_LAUNCHER_EVIDENCE main-entered",
    "ALICE_LAUNCHER_EVIDENCE javafx-launch-attempted",
    "ALICE_LAUNCHER_EVIDENCE javafx-application-started",
    "ALICE_LAUNCHER_EVIDENCE stage-received",
    "ALICE_LAUNCHER_EVIDENCE scene-configured observation-marker");
```

For reflected generated fields, wait for the field to become non-null:

```java
String[] classpath =
    ProjectTestWait.untilNotNull(() -> (String[]) field.get(null), "classpath array");
```

## Wait for process stream drains

Process tests wait for EOF, process exit, or a drain future. A test that writes
to a pipe signals when the drain thread has copied the expected bytes.

```java
CountDownLatch partialCopied = new CountDownLatch(1);

CompletableFuture<String> drained = CompletableFuture.supplyAsync(() -> {
  ByteArrayOutputStream buffer = new ByteArrayOutputStream();
  try {
    int next;
    while ((next = reader.read()) != -1) {
      buffer.write(next);
      if (buffer.toString(StandardCharsets.UTF_8).contains("PARTIAL_BEFORE_KILL")) {
        partialCopied.countDown();
      }
    }
    return buffer.toString(StandardCharsets.UTF_8);
  } catch (IOException ioe) {
    throw new UncheckedIOException(ioe);
  }
});

writer.write("PARTIAL_BEFORE_KILL\n".getBytes(StandardCharsets.UTF_8));
writer.flush();
ProjectTestWait.await(partialCopied, "drain thread to capture partial output");

writer.close();
String captured = ProjectTestWait.get(drained, "stream drain completion");
assertTrue(captured.contains("PARTIAL_BEFORE_KILL"));
```

Do not wait indefinitely for a process or stream. Every process wait has a
timeout and includes captured output in the failure message when that output is
needed to diagnose the failure.

## Keep semantic-time waits only with rationale

Some tests exercise real time. These waits remain because removing them would
change the behavior under test, but they use the module helper so interruption
is restored consistently:

```java
double first = Clock.getCurrentTime();

// Intentional real-time wait: Clock.getCurrentTime() is wall-clock based, and
// this test verifies elapsed time without introducing a fake production clock.
TestWait.sleepForSemanticTime(100, TimeUnit.MILLISECONDS,
    "Clock.getCurrentTime elapsed-time assertion");

double delta = Clock.getCurrentTime() - first;
assertTrue(delta >= 0.05 && delta < 1.0);
```

Keep a semantic-time wait only when all of these are true:

1. The test is asserting clock, timestamp, or fixture-delay semantics.
2. Replacing the sleep would require a production seam or change production
   behavior.
3. The semantic-time wait has an inline rationale.
4. The wait is bounded by a test helper that restores interruption before
   failing.

## Validate changed tests

Run focused modules first:

```bash
mvn -pl core/story-api -Dtest='*Event*Test' test
mvn -pl core/util -Dtest='*Animation*Test,ClockTest,FileUtilitiesTest' test
mvn -pl core/ide -Dtest='*RunWindow*Test,*ProjectBackup*Test,*RobotSave*Test' test
mvn -pl netbeans -Dtest='ProjectCodeGenerator*Test,RunCommandStreamDrainTest' test
```

Then run the relevant broader Maven lane after the Tweedle submodule is
initialized. See [Testing](../testing.md) for the maintained command list.
