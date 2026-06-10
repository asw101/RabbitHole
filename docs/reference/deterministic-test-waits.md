# Deterministic test wait reference

> **Status:** Implemented
> **Last reviewed:** 2026-06-10
> **Applies to:** deterministic-wait helpers for Java tests under `**/src/test/**`

This reference describes the deterministic-wait behavior used by RabbitHole
tests. Tests use observable synchronization instead of fixed sleeps. The test
wait helpers live under module-local `src/test/java` trees and do not add
runtime dependencies or production behavior.

## Contents

- [Scope](#scope)
- [Configuration](#configuration)
- [Helper modules](#helper-modules)
- [Common API contract](#common-api-contract)
- [Story event helper](#story-event-helper)
- [Core util helper](#core-util-helper)
- [GL render helper](#gl-render-helper)
- [IDE helper](#ide-helper)
- [NetBeans project helper](#netbeans-project-helper)
- [Retained semantic-time waits](#retained-semantic-time-waits)
- [Sleep inventory and disposition](#sleep-inventory-and-disposition)

## Scope

The rule applies to Java tests under `**/src/test/**`:

- no blind `Thread.sleep(...)` for async completion
- no `TimeUnit.*.sleep(...)` as test synchronization
- no open-coded polling loops that sleep between attempts
- no replacement with another unobservable delay such as `Robot.delay(...)`
  when waiting for UI state, file state, process state, or async completion

The rule does not change production code. Test-only helpers provide
bounded waits, event drains, futures, and completion signals.

## Configuration

No production configuration is required.

For local validation that invokes Node tooling, use the saved memory setting:

```bash
export NODE_OPTIONS=--max-old-space-size=32768
```

Before broad Maven validation, initialize the Tweedle grammar submodule:

```bash
git submodule update --init tweedle-lang
```

## Helper modules

| Helper | Location | Purpose |
| --- | --- | --- |
| `EventTestSupport` | `core/story-api/src/test/java/org/lgna/story/implementation/eventhandling/EventTestSupport.java` | Event callback waits, dispatcher idle waits, negative async assertions. |
| `TestWait` | `core/util/src/test/java/edu/cmu/cs/dennisc/TestWait.java` | Generic bounded condition and future waits for utility tests. |
| `GlRenderTestWait` | `core/glrender/src/test/java/edu/cmu/cs/dennisc/render/gl/GlRenderTestWait.java` | GL-render test condition waits without depending on another module's test classes. |
| `IdeTestWait` | `core/ide/src/test/java/org/alice/ide/IdeTestWait.java` | Swing event-dispatch-thread drains and bounded UI condition waits. |
| `ProjectTestWait` | `netbeans/src/test/java/org/alice/netbeans/project/ProjectTestWait.java` | Generated launcher fields, output markers, process exit, stream drains, and futures. |

Helpers are package-local unless tests outside the package need them. They
are small test bricks: each helper owns only one module's waiting vocabulary.

## Common API contract

Each helper follows the same contract:

- wait methods are bounded and never wait indefinitely
- failure messages name the condition that did not complete
- `InterruptedException` restores the interrupted status before failing
- condition suppliers are re-read after the wait before assertions use them
- helpers do not swallow exceptions from condition suppliers, futures, or event
  callbacks
- helpers do not change production classes, clocks, launchers, or runtime
  configuration
- semantic-time sleeps are named, bounded, and restore the interrupted status
  before failing

## Story event helper

`EventTestSupport` is used by tests in
`org.lgna.story.implementation.eventhandling`.

### `await`

```java
static void await(CountDownLatch latch, String description)
```

Waits for an expected callback count. Use it when a listener completion is the
observable signal.

### `until`

```java
static void until(BooleanSupplier condition, String description)
```

Waits for async event-handler state, such as an `isFiringMap` flag, to reach the
asserted value.

### `waitForEventDispatchIdle`

```java
static void waitForEventDispatchIdle(AbstractEventHandler<?, ?> handler)
```

Drains event-handler work that was triggered by the test. Use this before
asserting that a removed or silenced listener did not fire.

## Core util helper

`TestWait` is used by utility, animation, clock-adjacent, and filesystem
test code when a real semantic-time wait is not required.

### `until`

```java
static void until(BooleanSupplier condition, String description)
```

Waits for observable state such as a worker thread entering `WAITING` or a file
write becoming visible.

### `future`

```java
static <T> T future(Future<T> future, String description)
```

Returns a completed future value or fails with the helper timeout and
description.

### `sleepForSemanticTime`

```java
static void sleepForSemanticTime(long duration, TimeUnit unit, String description)
```

Sleeps only when elapsed wall-clock time or filesystem timestamp ordering is the
behavior under test. If interrupted, restores the interrupted status before
failing the test with the supplied description.

## GL render helper

`GlRenderTestWait` is used by GL-render tests. It is module-local because
`core/glrender` tests cannot depend on test classes from `core/util`.

### `until`

```java
static void until(BooleanSupplier condition, String description)
```

Waits for GL test state to satisfy the asserted condition without open-coded
sleeping between polls.

## IDE helper

`IdeTestWait` is used by Swing, Robot, project backup, and
property-adapter tests.

### `until`

```java
static void until(BooleanSupplier condition, String description)
```

Waits for non-Swing observable state such as file creation or backup selection
state.

### `untilOnEdt`

```java
static void untilOnEdt(BooleanSupplier condition, String description)
```

Evaluates the condition on the Swing event dispatch thread by using
`SwingUtilities.invokeAndWait`.

### `drainEdt`

```java
static void drainEdt()
```

Runs a no-op on the event dispatch thread so queued Swing work completes before
the next assertion.

### `sleepForSemanticTime`

```java
static void sleepForSemanticTime(long duration, TimeUnit unit, String description)
```

Sleeps only when filesystem timestamp ordering is the behavior under test. If
interrupted, restores the interrupted status before failing the test with the
supplied description.

## NetBeans project helper

`ProjectTestWait` is used by generated project and process/stream-drain
tests.

### `until`

```java
static void until(CheckedBooleanSupplier condition, String description)
```

Waits for generated-project fields, launcher markers, process state, or output
state.

### `await`

```java
static void await(CountDownLatch latch, String description)
```

Waits for a generated-project, launcher, or stream-drain completion signal.

### `untilNotNull`

```java
static <T> T untilNotNull(CheckedSupplier<T> supplier, String description)
```

Polls reflected generated-project state until it becomes non-null, then returns
the value.

### `get`

```java
static <T> T get(Future<T> future, String description)
```

Waits for stream-drain and process futures with a bounded timeout.

### `captureSystemOutUntil`

```java
static String captureSystemOutUntil(ThrowingRunnable action,
    CheckedBooleanSupplier completion, String description)
```

Captures `System.out` while running `action`, then waits for the completion
signal that proves async launcher work has finished.

The helper must restore the original `System.out` in a `finally` block. Tests
that use it must not run concurrently with other tests that capture or assert
global output.

## Retained semantic-time waits

Remaining semantic-time waits are allowed only when elapsed time or timestamp
ordering is the behavior being tested. Each retained call has an inline comment
explaining the semantic reason and uses the module helper's
`sleepForSemanticTime(...)` method so interruption is handled consistently.

Allowed categories:

| Category | Example | Rationale |
| --- | --- | --- |
| Clock elapsed time | `Clock.getCurrentTime_inSeconds` | The production clock is wall-clock based; a fake clock would change the production contract. |
| Animator elapsed time | `ClockBasedAnimatorDeepTest` speed-factor tests | The animator derives simulation time from real elapsed clock time. |
| Filesystem timestamp resolution | file modified time and backup creation ordering tests | The test verifies timestamp ordering on the active filesystem. |

The retained semantic-time waits are not synchronization shortcuts. They are
part of the asserted real-time or timestamp behavior.

## Sleep inventory and disposition

| Test source | Original sleep or delay use | Disposition |
| --- | --- | --- |
| `core/story-api/src/test/java/org/lgna/story/implementation/eventhandling/EventHandlerBehaviorTest.java` | Wait after removing a scene activation listener before asserting no callback. | Replaced with `EventTestSupport.waitForEventDispatchIdle(...)`, then assert absence. |
| `core/story-api/src/test/java/org/lgna/story/implementation/eventhandling/ExtendedEventManagerTest.java` | Wait after removed scene activation listener. | Replaced with event-dispatch idle wait. |
| `core/story-api/src/test/java/org/lgna/story/implementation/eventhandling/ExtendedEventManagerTest.java` | Wait after silencing scene activation listeners. | Replaced with event-dispatch idle wait before asserting no callback. |
| `core/story-api/src/test/java/org/lgna/story/implementation/eventhandling/ExtendedEventManagerTest.java` | Wait for `SceneActivationHandler` to fire at least once. | Replaced with listener-owned `CountDownLatch`. |
| `core/story-api/src/test/java/org/lgna/story/implementation/eventhandling/EventManagerDispatchTest.java` | Wait for duplicate listener dispatch to settle. | Replaced with callback latch plus dispatch-idle wait. |
| `core/story-api/src/test/java/org/lgna/story/implementation/eventhandling/EventManagerDispatchTest.java` | Wait for second dispatch after one duplicate registration is removed. | Replaced with bounded callback-count condition and dispatch-idle wait. |
| `core/story-api/src/test/java/org/lgna/story/implementation/eventhandling/AbstractEventHandlerAsyncTest.java` | Poll `isFiringMap` cleanup after queued event delivery. | Replaced with `EventTestSupport.until(...)`. |
| `core/story-api/src/test/java/org/lgna/story/implementation/eventhandling/AbstractEventHandlerAsyncTest.java` | Poll `isFiringMap` cleanup after listener exception. | Replaced with `EventTestSupport.until(...)`. |
| `netbeans/src/test/java/org/alice/netbeans/project/ProjectCodeGeneratorTest.java` | Poll reflected generated `String[]` field. | Replaced with `ProjectTestWait.untilNotNull(...)` for positive delegation cases; negative cases rely on synchronous launcher return. |
| `netbeans/src/test/java/org/alice/netbeans/project/ProjectCodeGeneratorStandaloneProjectTest.java` | Sleep after invoking generated JavaFX launcher. | Replaced with `ProjectTestWait.captureSystemOutUntil(...)` waiting on stubbed stage completion. |
| `netbeans/src/test/java/org/alice/netbeans/project/RunCommandStreamDrainTest.java` | Sleep after writing partial pipe output before closing stream. | Replaced with stream-drain latch confirming bytes were copied. |
| `core/glrender/src/test/java/edu/cmu/cs/dennisc/render/gl/CoverageBoostBehaviorTest.java` | Local `waitUntil` helper slept between polls. | Replaced with module-local `GlRenderTestWait.until(...)`. |
| `core/ide/src/test/java/org/alice/ide/integration/AliceIdeIntegrationTest.java` | `Robot.delay(...)` after clicking the frame before opening File menu. | Replaced with `robot.waitForIdle()`, EDT drain, and condition waits. |
| `core/ide/src/test/java/org/alice/ide/integration/AliceIdeIntegrationTest.java` | `Robot.delay(...)` loop waiting for scene editor perspective setup. | Replaced with bounded condition helper and preserved assumption skip behavior. |
| `core/ide/src/test/java/org/alice/ide/integration/AliceIdeIntegrationTest.java` | Three `Robot.delay(...)` calls used to pace drag movement. | Replaced with Robot idle/EDT drains between drag phases. |
| `core/ide/src/test/java/org/alice/ide/integration/AliceIdeIntegrationTest.java` | `waitForIdle()` added a fixed `Robot.delay(...)` cushion. | Replaced with `robot.waitForIdle()` plus `IdeTestWait.drainEdt()`. |
| `core/ide/src/test/java/org/alice/ide/integration/AliceIdeIntegrationTest.java` | `awaitCondition(...)` slept between condition checks. | Replaced with `IdeTestWait.until(...)`. |
| `core/ide/src/test/java/org/alice/ide/integration/AliceIdeIntegrationTest.java` | `awaitWindow(...)` slept while polling dialogs. | Replaced with `IdeTestWait.untilOnEdt(...)` returning the matching dialog. |
| `core/ide/src/test/java/org/alice/ide/croquet/models/projecturi/JMenuBarRobotClickSaveProofTest.java` | `Robot.delay(...)` after Swing paint/idle before reading menu bounds. | Replaced with `robot.waitForIdle()`, EDT drain, and `IdeTestWait.untilOnEdt(...)` for menu visibility. |
| `core/ide/src/test/java/org/alice/ide/croquet/models/projecturi/JMenuBarRobotClickSaveProofTest.java` | `Robot.delay(...)` loop waiting for File popup visibility. | Replaced with `IdeTestWait.untilOnEdt(...)`. |
| `core/ide/src/test/java/org/alice/ide/croquet/models/projecturi/JMenuBarRobotClickSaveProofTest.java` | `Robot.delay(...)` after clicking Save before reading evidence. | Replaced with bounded wait for the evidence artifact. |
| `core/ide/src/test/java/org/alice/ide/croquet/models/projecturi/RobotSaveMenuDialogWriteReadbackProofTest.java` | `Robot.delay(...)` during Robot setup after idle. | Replaced with `robot.waitForIdle()` and `IdeTestWait.drainEdt()`. |
| `core/ide/src/test/java/org/alice/ide/croquet/models/projecturi/RobotSaveMenuDialogWriteReadbackProofTest.java` | `Robot.delay(...)` loop waiting for File popup visibility. | Replaced with `IdeTestWait.untilOnEdt(...)`, preserving blocked-path return on timeout. |
| `core/ide/src/test/java/org/alice/tools/RunWindowDetectionProofTest.java` | Poll run-button visibility with sleep. | Replaced with `IdeTestWait.untilOnEdt(...)`. |
| `core/ide/src/test/java/org/alice/tools/RunWindowDetectionProofTest.java` | Poll windows with `Thread.sleep(intervalMs)`. | Replaced with bounded window condition wait. |
| `core/ide/src/test/java/org/alice/ide/properties/adapter/PropertyAdapterTestHelper.java` | Poll property value with sleep. | Replaced with `IdeTestWait.until(...)`. |
| `core/ide/src/test/java/org/alice/ide/croquet/models/projecturi/RobotSaveMenuDialogWriteReadbackProofTest.java` | Poll saved file existence with sleep. | Replaced with `IdeTestWait.until(...)` on `Files.isRegularFile(...)` and non-zero size. |
| `core/util/src/test/java/edu/cmu/cs/dennisc/animation/WaitingAnimationTest.java` | Poll worker thread until it enters `WAITING`. | Replaced with `TestWait.until(...)` on thread state after the readiness latch. |
| `core/util/src/test/java/edu/cmu/cs/dennisc/animation/AbstractAnimatorTest.java` | Poll animation worker until it enters `WAITING`. | Replaced with `TestWait.until(...)` on thread state. |
| `core/util/src/test/java/edu/cmu/cs/dennisc/java/io/FileUtilitiesTest.java` | Delay between file writes before checking modified time. | Kept as semantic filesystem timestamp wait through `TestWait.sleepForSemanticTime(...)` with inline rationale. |
| `core/ide/src/test/java/org/alice/ide/ProjectBackupManagerBehaviorTest.java` | Delay between backup file creation times. | Kept as semantic filesystem timestamp wait through `IdeTestWait.sleepForSemanticTime(...)` with inline rationale. |
| `core/util/src/test/java/edu/cmu/cs/dennisc/clock/ClockTest.java` | Delay before monotonic clock assertion. | Kept as semantic elapsed-time wait through `TestWait.sleepForSemanticTime(...)` with inline rationale. |
| `core/util/src/test/java/edu/cmu/cs/dennisc/clock/ClockTest.java` | Delay before asserting seconds-scale elapsed time. | Kept as semantic elapsed-time wait through `TestWait.sleepForSemanticTime(...)` with inline rationale. |
| `core/util/src/test/java/edu/cmu/cs/dennisc/animation/ClockBasedAnimatorDeepTest.java` | Delay before asserting time advances. | Kept as semantic animator elapsed-time wait through `TestWait.sleepForSemanticTime(...)` with inline rationale. |
| `core/util/src/test/java/edu/cmu/cs/dennisc/animation/ClockBasedAnimatorDeepTest.java` | Delay before asserting zero speed freezes simulation time. | Kept as semantic animator elapsed-time wait through `TestWait.sleepForSemanticTime(...)` with inline rationale. |
| `core/util/src/test/java/edu/cmu/cs/dennisc/animation/ClockBasedAnimatorDeepTest.java` | Delay before asserting negative speed moves backward. | Kept as semantic animator elapsed-time wait through `TestWait.sleepForSemanticTime(...)` with inline rationale. |
| `core/util/src/test/java/edu/cmu/cs/dennisc/animation/ClockBasedAnimatorDeepTest.java` | Delay before comparing half-speed and full-speed animators. | Kept as semantic animator elapsed-time wait through `TestWait.sleepForSemanticTime(...)` with inline rationale. |

The inventory covers all direct `Thread.sleep(...)`, `TimeUnit.*.sleep(...)`,
sleep-style polling sites, and `Robot.delay(...)` wait equivalents found under
`**/src/test/**` during the 2026-06-10 documentation review.
