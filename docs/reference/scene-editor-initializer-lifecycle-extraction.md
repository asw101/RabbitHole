# SceneEditorInitializer and SceneEditorLifecycleManager Extraction

This reference documents the final extraction of initialization and lifecycle
methods from `StorytellingSceneEditor` into two new delegate classes
(issue #545). This is the third and final round of SSE decomposition,
following the [SceneEditorFieldManager extraction](./scene-editor-field-manager-extraction.md)
(issue #528) and the original inner-class extraction (PR #534).

The extraction is a pure internal refactor. The public API surface —
`StorytellingSceneEditor` — is unchanged. All existing scene-editor behavior,
field initialization, camera setup, and project lifecycle sequencing are
preserved identically.

## Contents

- [Motivation](#motivation)
- [Architecture](#architecture)
- [File inventory](#file-inventory)
- [Class responsibilities](#class-responsibilities)
  - [SceneEditorInitializer](#sceneeditorinitializer)
  - [SceneEditorLifecycleManager](#sceneeditorlifecyclemanager)
  - [StorytellingSceneEditor changes](#storytellingsceneeditor-changes)
- [Public API](#public-api)
- [Delegation pattern](#delegation-pattern)
- [Package-private collaboration](#package-private-collaboration)
- [Forwarding methods for protected parent access](#forwarding-methods-for-protected-parent-access)
- [Visibility changes](#visibility-changes)
- [Security boundary](#security-boundary)
- [Error handling contract](#error-handling-contract)
- [Configuration](#configuration)
- [Validation](#validation)
- [Acceptance criteria](#acceptance-criteria)
- [Claim boundaries](#claim-boundaries)

## Motivation

After the SceneEditorFieldManager and SceneRenderTargetListener extractions
(issue #528), `StorytellingSceneEditor.java` stood at 693 lines — still
above the 500-line target for the final round. Two cohesive method groups
remained as extraction candidates:

| Extraction | Lines removed | Purpose |
| --- | --- | --- |
| `SceneEditorInitializer` | ~65 | `initializeComponents()` body — snap grid setup, drag adapter wiring, camera navigator widget creation, expand/contract button creation, selection listeners, right-click adapter, camera view tracker |
| `SceneEditorLifecycleManager` | ~150 | `setActiveScene()` SSE-specific logic, `addField()` SSE-specific logic, `handleProjectOpened()` pre-super cleanup, and the private `useSceneAsVehicleForDisconnectedModels()` helper |

After extraction, `StorytellingSceneEditor.java` drops from 693 to
496 lines — under the 500-line target. The public API surface is unchanged:
all public and protected override methods remain on SSE as thin delegation
stubs or unchanged methods. No external caller changes.

## Architecture

```text
StorytellingSceneEditor (public facade — thin stubs for 4 override methods)
├── SceneEditorFieldManager (issue #528 — selection, cameras, markers, rendering)
├── SceneRenderTargetListener (issue #528 — render target callbacks)
├── SceneEditorListeners (PR #534 — listener consolidation)
├── SceneEditorDropReceptor (PR #534 — gallery drag-and-drop)
├── LookingGlassPanel (PR #534 — render target panel wrapper)
├── SceneEditorInitializer (issue #545 — ephemeral: initializeComponents body)
└── SceneEditorLifecycleManager (issue #545 — stored field: scene lifecycle)
```

`SceneEditorInitializer` is ephemeral — instantiated and discarded within
`initializeComponents()`. It receives a `StorytellingSceneEditor` reference,
executes the initialization body, and is immediately garbage-collected.

`SceneEditorLifecycleManager` is stored as a field on SSE — it is instantiated
once in the constructor or lazily, and called from `setActiveScene()`,
`addField()`, and `handleProjectOpened()`.

Both classes are package-private and live in
`org.alice.stageide.sceneeditor`.

## File inventory

| File | Role | Approx lines |
| --- | --- | --- |
| `StorytellingSceneEditor.java` | Parent class — thin delegation stubs for 4 override methods. Owns all delegate fields. | ~496 |
| `SceneEditorInitializer.java` | Ephemeral delegate for `initializeComponents()` body. Owns `EXPAND_ICON` and `CONTRACT_ICON` constants. | ~137 |
| `SceneEditorLifecycleManager.java` | Stored delegate for scene activation, field addition, and project opening lifecycle. | ~217 |
| `SceneEditorFieldManager.java` | Selection, camera switching, marker handling, lifecycle, rendering control, and code generation delegation (unchanged from issue #528). | ~210 |
| `SceneRenderTargetListener.java` | Render target callbacks and horizon line painting (unchanged from issue #528). | ~60 |
| `SceneEditorListeners.java` | Listener consolidation (unchanged from PR #534). | ~86 |
| `SceneEditorDropReceptor.java` | Gallery drag-and-drop (unchanged from PR #534). | ~139 |
| `LookingGlassPanel.java` | Render target panel wrapper (unchanged from PR #534). | ~76 |
| `SceneFieldCodeGenerator.java` | Field code generation (unchanged — owned by `SceneEditorFieldManager`). | ~305 |

All source files reside in:

```text
core/ide/src/main/java/org/alice/stageide/sceneeditor/
```

## Class responsibilities

### SceneEditorInitializer

| Property | Value |
| --- | --- |
| New file | `SceneEditorInitializer.java` |
| Visibility | Package-private (no access modifier) |
| Constructor | `SceneEditorInitializer(StorytellingSceneEditor editor)` |
| Back-reference field | `editor` (`StorytellingSceneEditor`) |
| Lifecycle | Ephemeral — created and discarded within `initializeComponents()` |
| Approximate lines | ~137 |

This class owns the initialization body that was previously the
`initializeComponents()` method on SSE. It also owns the `EXPAND_ICON` and
`CONTRACT_ICON` static constants that were previously on SSE.

| Responsibility | Method/Field |
| --- | --- |
| Full initialization sequence | `initialize()` — snap grid setup, drag adapter wiring, camera navigator creation, button creation, selection listener registration, right-click adapter, camera view tracker setup, marker list wiring |
| Expand icon constant | `static final Icon EXPAND_ICON` — 24×24 SVG icon for expand button |
| Contract icon constant | `static final Icon CONTRACT_ICON` — 24×24 SVG icon for contract button |

The constructor signature:

```java
SceneEditorInitializer(StorytellingSceneEditor editor) {
  this.editor = editor;
}
```

The `initialize()` method performs the following operations in order:

1. Create `SnapGrid` and wire snap state listeners
2. Register instance factory selection listener from `IDE.getDocumentFrame()`
3. Create and configure `GlobalDragAdapter`
4. Register the `SceneRenderTargetListener` on the render target
5. Set the animator on the drag adapter
6. Restore manipulator selection if a field is already selected
7. Create `CameraNavigatorWidget`
8. Create expand/contract buttons from document frame operations
9. Create `InstanceFactorySelectionPanel`
10. Create `OrthographicCameraImp` with near clipping plane
11. Register selection listener (delegates to `fieldManager.handleManipulatorSelection`)
12. Register right-click adapter (delegates to `fieldManager.showRightClickMenuForModel`)
13. Create `CameraMarkerTracker` and wire camera view combo box
14. Wire camera and object marker list listeners
15. Set `isInitialized = true`

**Why ephemeral:**
The initializer is needed exactly once. Storing it as a field would waste
memory for the lifetime of the singleton `StorytellingSceneEditor`. The
ephemeral pattern — `new SceneEditorInitializer(this).initialize()` — keeps
the initialization logic organized without permanent overhead.

### SceneEditorLifecycleManager

| Property | Value |
| --- | --- |
| New file | `SceneEditorLifecycleManager.java` |
| Visibility | Package-private (no access modifier) |
| Constructor | `SceneEditorLifecycleManager(StorytellingSceneEditor editor)` |
| Back-reference field | `editor` (`StorytellingSceneEditor`) |
| Lifecycle | Stored as `lifecycleManager` field on SSE |
| Approximate lines | ~217 |

This class consolidates scene lifecycle concerns that were previously on SSE:
activating a new scene, handling field additions, and preparing for project
opens.

| Responsibility | Method |
| --- | --- |
| Scene activation | `activateScene(UserField sceneField)` — SSE-specific logic after `super.setActiveScene()`: camera reset, program speed, scene initialization, setup method invocation, camera detection (perspective vs VR), drag adapter camera setup, snap grid attachment, camera marker tracking, marker visibility, field code state initialization |
| Field addition | `handleAddField(UserType<?> declaringType, UserField field, int index, Statement... statements)` — SSE-specific logic after `super.addField()`: marker display setup, camera/object marker selection, initial code state, jointed model visualization |
| Project opening preparation | `prepareForProject()` — pre-super cleanup: render target cache clearing, nebulous model/person unloading, drag adapter clearing |
| Disconnected model vehicle fix | `useSceneAsVehicleForDisconnectedModels(UserMethod)` — private helper: replaces `null` vehicle arguments with `ThisExpression` in generated setup methods |

The constructor signature:

```java
SceneEditorLifecycleManager(StorytellingSceneEditor editor) {
  this.editor = editor;
}
```

**Method call ordering in SSE stubs:**

The `super` call ordering is critical and preserved in the SSE stubs:

```java
// setActiveScene: super first, then delegate
@Override
protected void setActiveScene(UserField sceneField) {
  super.setActiveScene(sceneField);
  lifecycleManager.activateScene(sceneField);
}

// addField: super first, then delegate
@Override
public void addField(UserType<?> declaringType, UserField field,
    int index, Statement... statements) {
  super.addField(declaringType, field, index, statements);
  lifecycleManager.handleAddField(declaringType, field, index, statements);
}

// handleProjectOpened: delegate first, then super
@Override
protected void handleProjectOpened(Project nextProject) {
  lifecycleManager.prepareForProject();
  super.handleProjectOpened(nextProject);
}
```

The `handleProjectOpened` ordering is reversed — the pre-super cleanup
(cache clearing, model unloading, drag adapter clearing) must happen
**before** the parent class sets up the new project. This matches the
original method body where the cleanup preceded `super.handleProjectOpened()`.

### StorytellingSceneEditor changes

| Change | Detail |
| --- | --- |
| New field | `final SceneEditorLifecycleManager lifecycleManager = new SceneEditorLifecycleManager(this)` |
| `EXPAND_ICON` removed | Constant moved to `SceneEditorInitializer.EXPAND_ICON` |
| `CONTRACT_ICON` removed | Constant moved to `SceneEditorInitializer.CONTRACT_ICON` |
| `initializeComponents()` | Body replaced with guard + `new SceneEditorInitializer(this).initialize()` |
| `setActiveScene(UserField)` | Body replaced with `super.setActiveScene(sceneField)` + `lifecycleManager.activateScene(sceneField)` |
| `addField(...)` | Body replaced with `super.addField(...)` + `lifecycleManager.handleAddField(...)` |
| `handleProjectOpened(Project)` | Body replaced with `lifecycleManager.prepareForProject()` + `super.handleProjectOpened(nextProject)` |
| `useSceneAsVehicleForDisconnectedModels` | Removed from SSE (now private on `SceneEditorLifecycleManager`) |
| 11 fields widened | See [Visibility changes](#visibility-changes) |
| 2 methods widened | See [Visibility changes](#visibility-changes) |
| 2 forwarding methods added | See [Forwarding methods](#forwarding-methods-for-protected-parent-access) |

## Public API

The public API is exclusively on `StorytellingSceneEditor`. No API changes
are made by this extraction. All 37+ public methods remain on SSE with
identical signatures. Neither `SceneEditorInitializer` nor
`SceneEditorLifecycleManager` exposes any public API.

All external callers continue to call `StorytellingSceneEditor.getInstance()`
and use its public methods. They are unaware of the delegate classes.

## Delegation pattern

`StorytellingSceneEditor` retains all override method signatures. Each
extracted method becomes a thin delegation stub:

```java
// Before (105-line body on SSE):
@Override
protected void setActiveScene(UserField sceneField) {
  super.setActiveScene(sceneField);
  if (movableSceneCameraImp != null) {
    movableSceneCameraImp.setLocalTransformation(AffineMatrix4x4.IDENTITY);
  }
  if (sceneField != null) {
    // ... 95 more lines of scene activation logic
  }
}

// After (thin delegation stub on SSE):
@Override
protected void setActiveScene(UserField sceneField) {
  super.setActiveScene(sceneField);
  lifecycleManager.activateScene(sceneField);
}
```

The `initializeComponents()` method uses the ephemeral pattern:

```java
// Before (65-line body on SSE):
@Override
protected void initializeComponents() {
  if (this.isInitialized) { return; }
  this.snapGrid = new SnapGrid();
  // ... 60 more lines of initialization
  this.isInitialized = true;
}

// After (ephemeral delegate on SSE):
@Override
protected void initializeComponents() {
  if (this.isInitialized) { return; }
  new SceneEditorInitializer(this).initialize();
}
```

The `handleProjectOpened()` method reverses the call order:

```java
// Before (10-line body on SSE):
@Override
protected void handleProjectOpened(Project nextProject) {
  if (this.onscreenRenderTarget != null) {
    this.onscreenRenderTarget.forgetAllCachedItems();
    NebulousIde.nonfree.unloadNebulousModelData();
  }
  NebulousIde.nonfree.unloadPerson();
  if (this.globalDragAdapter != null) {
    this.globalDragAdapter.clear();
  }
  super.handleProjectOpened(nextProject);
}

// After (delegate-first, then super):
@Override
protected void handleProjectOpened(Project nextProject) {
  lifecycleManager.prepareForProject();
  super.handleProjectOpened(nextProject);
}
```

## Package-private collaboration

Both delegates access `StorytellingSceneEditor` fields and methods via
the `editor` back-reference. The following SSE members are accessed by
the delegates:

### SceneEditorInitializer accesses

| Member | Type | Purpose |
| --- | --- | --- |
| `snapGrid` | field | Created during initialization |
| `listeners` | field | Snap grid, selection, and marker listeners |
| `globalDragAdapter` | field | Created and configured during initialization |
| `onscreenRenderTarget` | field | Render target registration |
| `renderTargetListener` | field | Registered on render target |
| `animator` | field | Set on drag adapter |
| `mainCameraNavigatorWidget` | field | Created during initialization |
| `expandButton` | field | Created from document frame operation |
| `contractButton` | field | Created from document frame operation |
| `instanceFactorySelectionPanel` | field | Created during initialization |
| `orthographicCameraImp` | field | Created during initialization |
| `fieldManager` | field | Selection and right-click handling |
| `mainCameraViewTracker` | field | Created during initialization |
| `mainCameraViewSelector` | field | Created from camera marker list |
| `mainCameraMarkerList` | field | Camera view state |
| `lookingGlassPanel` | field | Camera view combo box placement |
| `isInitialized` | field | Set to `true` at end of initialization |
| `getSelectedField()` | method (inherited) | Check for pre-existing selection |

### SceneEditorLifecycleManager accesses

| Member | Type | Purpose |
| --- | --- | --- |
| `movableSceneCameraImp` | field | Camera reset, camera marker operations |
| `sceneCameraImp` | field | Camera detection and assignment |
| `globalDragAdapter` | field | Camera view management |
| `orthographicCameraImp` | field | Camera setup |
| `snapGrid` | field | Scene attachment and visibility |
| `mainCameraViewTracker` | field | Marker tracking and camera setup |
| `savedSceneEditorViewSelection` | field | Reset on new scene |
| `mainCameraViewSelector` | field | Model refresh |
| `instanceFactorySelectionPanel` | field | Type assignment |
| `fieldManager` | field | Marker selection |
| `layoutCameraImp` | field | Camera setup |
| `onscreenRenderTarget` | field | Cache clearing |
| `isInitialized` | field | Guard check |
| `getPropertyPanel()` | method | Scene instance assignment |
| `setCameras()` | method | Camera initialization |
| `setIsVrActive(boolean)` | method | VR mode detection |
| `getProgramInstanceInJavaForDelegate()` | forwarding method | Program access |
| `setInitialCodeStateForFieldForDelegate(...)` | forwarding method | Field code state |
| `getActiveSceneInstance()` | method (inherited, public) | Scene instance access |
| `getInstanceInJavaVMForField(...)` | method (inherited, public) | Field instance lookup |
| `getActiveSceneImplementation()` | method (inherited, public) | Scene implementation |
| `getVirtualMachine()` | method (inherited, public) | VM access |
| `getImplementation(AbstractField)` | method (inherited, public) | Entity implementation lookup |
| `isVrActive()` | method (public) | VR state check |

No interfaces or inheritance are introduced. All collaboration uses direct
method calls within the same package, matching the
[SceneEditorFieldManager pattern](./scene-editor-field-manager-extraction.md).

## Forwarding methods for protected parent access

Because `SceneEditorLifecycleManager` is a separate class in the same package
but does **not** extend `AbstractSceneEditor`, it cannot call `protected`
methods inherited from `AbstractSceneEditor` (which lives in
`org.alice.ide.sceneeditor`). Java accessibility rules require forwarding
methods on the subclass.

Only **two** protected methods on `AbstractSceneEditor` are called by the
extracted lifecycle logic: `getProgramInstanceInJava()` and
`setInitialCodeStateForField()`. All other methods used by the delegates —
`getActiveSceneInstance()`, `getVirtualMachine()`,
`getInstanceInJavaVMForField()`, `getActiveSceneImplementation()`, and
`getImplementation()` — are `public` on `AbstractSceneEditor` and accessible
directly via the `editor` back-reference without forwarding.

```java
// StorytellingSceneEditor.java — forwarding methods for delegate access

SProgram getProgramInstanceInJavaForDelegate() {
  return getProgramInstanceInJava();
}

void setInitialCodeStateForFieldForDelegate(UserField field, Statement statement) {
  setInitialCodeStateForField(field, statement);
}
```

These forwarding methods are package-private. They exist solely to bridge the
Java visibility gap between `protected` inherited methods and the
package-private delegate class. This follows the same bridge pattern used by
`SceneEditorFieldManager` for its access to protected parent methods, and by
the [StatementEncoder](./statement-encoder-extraction.md) and
[ExpressionEncoder](./expression-encoder-extraction.md) extractions for
`TweedleEncoder` super-call bridges.

## Visibility changes

Eleven fields on `StorytellingSceneEditor` are widened from `private` to
package-private to allow cross-file access by the two new delegates.
These are in addition to the fields widened in issue #528 and PR #534.

### Fields (11)

| Field | Type | Before | After | Accessed by |
| --- | --- | --- | --- | --- |
| `isInitialized` | `boolean` | `private` | package-private | `SceneEditorInitializer` (guard + set) |
| `renderTargetListener` | `SceneRenderTargetListener` | `private final` | `final` (package-private) | `SceneEditorInitializer` (render target registration) |
| `animator` | `ClockBasedAnimator` | `private` | package-private | `SceneEditorInitializer` (drag adapter setup) |
| `expandButton` | `Button` | `private` | package-private | `SceneEditorInitializer` (creation), SSE (`handleExpandContractChange`) |
| `contractButton` | `Button` | `private` | package-private | `SceneEditorInitializer` (creation), SSE (`handleExpandContractChange`) |
| `instanceFactorySelectionPanel` | `InstanceFactorySelectionPanel` | `private` | package-private | `SceneEditorInitializer` (creation), `SceneEditorLifecycleManager` (type assignment), SSE (`handleExpandContractChange`) |
| `layoutCameraImp` | `SymmetricPerspectiveCameraImp` | `private final` | `final` (package-private) | `SceneEditorLifecycleManager` (camera setup) |
| `mainCameraViewSelector` | `ComboBox<CameraOption>` | `private` | package-private | `SceneEditorInitializer` (creation), `SceneEditorLifecycleManager` (model refresh), SSE (`handleExpandContractChange`) |
| `mainCameraViewTracker` | `CameraMarkerTracker` | `private` | package-private | `SceneEditorInitializer` (creation), `SceneEditorLifecycleManager` (scene tracking) |
| `savedSceneEditorViewSelection` | `CameraOption` | `private` | package-private | `SceneEditorLifecycleManager` (reset on new scene), SSE (`handleExpandContractChange`) |
| `mainCameraMarkerList` | `ImmutableDataSingleSelectListState<CameraOption>` | `private` | package-private | `SceneEditorInitializer` (listener wiring), SSE (`handleExpandContractChange`) |

### Methods (2)

| Method | Signature | Before | After | Accessed by |
| --- | --- | --- | --- | --- |
| `setIsVrActive` | `void setIsVrActive(boolean)` | `private` | package-private | `SceneEditorLifecycleManager` (VR detection during scene activation) |
| `setCameras` | `void setCameras()` | `private` | package-private | `SceneEditorLifecycleManager` (camera initialization during scene activation) |

### Constants removed from SSE

| Constant | Before (on SSE) | After (on SceneEditorInitializer) |
| --- | --- | --- |
| `EXPAND_ICON` | `private static Icon` | `static final Icon` (package-private) on `SceneEditorInitializer` |
| `CONTRACT_ICON` | `private static Icon` | `static final Icon` (package-private) on `SceneEditorInitializer` |

No member is widened beyond package-private. All extracted classes are in
the same package (`org.alice.stageide.sceneeditor`).

## Security boundary

No new I/O, network, reflection, or thread operations are introduced.
Both delegate classes only orchestrate existing operations through the
`StorytellingSceneEditor` back-reference. The `synchronized(getTreeLock())`
block in `handleExpandContractChange` stays on SSE — thread safety is not
diluted across class boundaries.

The `useSceneAsVehicleForDisconnectedModels` method modifies AST nodes
(replacing `NullLiteral` with `ThisExpression`), but this is unchanged
behavior — the method body is identical, only its location changes.

Package-private visibility is more restrictive than the original `private` +
inner-class access pattern. No new public API surface is introduced.

## Error handling contract

No error handling changes. The extracted methods preserve existing
exception behavior:

- `setActiveScene` contains a `try/finally` block around
  `program.setActiveScene(scene)` to ensure
  `ACCEPTABLE_HACK_FOR_SCENE_EDITOR_popPerformMinimalInitialization()`
  is always called. This block moves to `SceneEditorLifecycleManager.activateScene()`
  with identical semantics.
- `initializeComponents` has no try/catch — it either completes or
  propagates exceptions. The ephemeral delegate preserves this.
- The `assert` statement in `setActiveScene` (verifying non-null
  `globalDragAdapter`, `sceneCameraImp`, `orthographicCameraImp`) moves
  to `activateScene()` with identical behavior.

## Configuration

No runtime configuration changes. The extraction uses the existing Maven
reactor and JUnit configuration.

From a fresh checkout or worktree, initialize the Tweedle grammar submodule
before Maven validation:

```bash
git submodule update --init tweedle-lang
test -d tweedle-lang/Grammar
```

Set the Node memory preference when running Maven:

```bash
export NODE_OPTIONS=--max-old-space-size=32768
```

## Validation

### Run the full core/ide test suite

```bash
git submodule update --init tweedle-lang
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -DincludeSims=false -Dinstall4j.skip \
  -pl core/ide -am -DfailIfNoTests=false -Dcheckstyle.skip test
```

This is the primary validation command. All existing tests must pass.

### Run the characterization and extraction suites only

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -DincludeSims=false -Dinstall4j.skip \
  -pl core/ide -am \
  -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Dcheckstyle.skip \
  -Dtest=StorytellingSceneEditorCharacterizationTest,StorytellingSceneEditorExtractionTest \
  test
```

### Verify new file existence

```bash
ls core/ide/src/main/java/org/alice/stageide/sceneeditor/{SceneEditorInitializer,SceneEditorLifecycleManager}.java
```

Both files must exist.

### Verify line count target

```bash
wc -l core/ide/src/main/java/org/alice/stageide/sceneeditor/StorytellingSceneEditor.java
```

Expected: under 500 lines.

### Verify EXPAND_ICON/CONTRACT_ICON moved

```bash
grep -n 'EXPAND_ICON\|CONTRACT_ICON' \
  core/ide/src/main/java/org/alice/stageide/sceneeditor/StorytellingSceneEditor.java
```

Expected: no matches — both icon constants and their usages move entirely
to `SceneEditorInitializer`. The `handleExpandContractChange` method on SSE
references `expandButton` and `contractButton` (the Button objects), not the
icon constants.

```bash
grep -n 'EXPAND_ICON\|CONTRACT_ICON' \
  core/ide/src/main/java/org/alice/stageide/sceneeditor/SceneEditorInitializer.java
```

Expected: static final declarations and usage in `initialize()`.

### Verify delegation pattern

```bash
grep 'lifecycleManager\.' \
  core/ide/src/main/java/org/alice/stageide/sceneeditor/StorytellingSceneEditor.java | wc -l
```

Expected: at least 3 delegation calls (`activateScene`, `handleAddField`,
`prepareForProject`).

```bash
grep 'SceneEditorInitializer' \
  core/ide/src/main/java/org/alice/stageide/sceneeditor/StorytellingSceneEditor.java
```

Expected: one instantiation in `initializeComponents()`.

## Acceptance criteria

| Criterion | Verification |
| --- | --- |
| `SceneEditorInitializer.java` exists | File present in `core/ide/src/main/java/org/alice/stageide/sceneeditor/` |
| `SceneEditorInitializer` is package-private | No `public` keyword on class declaration |
| Constructor takes `StorytellingSceneEditor` | `SceneEditorInitializer(StorytellingSceneEditor editor)` |
| `initialize()` method present | Contains full `initializeComponents` body |
| `EXPAND_ICON` on initializer | `static final Icon EXPAND_ICON` declared on `SceneEditorInitializer` |
| `CONTRACT_ICON` on initializer | `static final Icon CONTRACT_ICON` declared on `SceneEditorInitializer` |
| `SceneEditorLifecycleManager.java` exists | File present in `core/ide/src/main/java/org/alice/stageide/sceneeditor/` |
| `SceneEditorLifecycleManager` is package-private | No `public` keyword on class declaration |
| Constructor takes `StorytellingSceneEditor` | `SceneEditorLifecycleManager(StorytellingSceneEditor editor)` |
| `activateScene(UserField)` method present | Contains `setActiveScene` SSE-specific logic |
| `handleAddField(...)` method present | Contains `addField` SSE-specific logic |
| `prepareForProject()` method present | Contains `handleProjectOpened` pre-super cleanup |
| `useSceneAsVehicleForDisconnectedModels` on lifecycle manager | Private method, no longer on SSE |
| SSE line count under 500 | `wc -l` shows ≤ 499 lines |
| SSE `initializeComponents` delegates | Body is guard + `new SceneEditorInitializer(this).initialize()` |
| SSE `setActiveScene` delegates | Body is `super` + `lifecycleManager.activateScene(sceneField)` |
| SSE `addField` delegates | Body is `super` + `lifecycleManager.handleAddField(...)` |
| SSE `handleProjectOpened` delegates | Body is `lifecycleManager.prepareForProject()` + `super` |
| SSE `EXPAND_ICON` removed | No static icon field declaration on SSE |
| SSE `CONTRACT_ICON` removed | No static icon field declaration on SSE |
| SSE has zero icon constant references | `handleExpandContractChange` uses buttons, not icons; grep finds no matches |
| Forwarding methods present | `getProgramInstanceInJavaForDelegate()`, `setInitialCodeStateForFieldForDelegate()` |
| 11 fields widened to package-private | `isInitialized`, `renderTargetListener`, `animator`, `expandButton`, `contractButton`, `instanceFactorySelectionPanel`, `layoutCameraImp`, `mainCameraViewSelector`, `mainCameraViewTracker`, `savedSceneEditorViewSelection`, `mainCameraMarkerList` |
| 2 methods widened to package-private | `setIsVrActive`, `setCameras` |
| `StorytellingSceneEditorCharacterizationTest` passes | All structural + API tests — zero failures |
| `StorytellingSceneEditorExtractionTest` passes | All extraction contract tests — zero failures |
| Full core/ide test suite passes | `mvn -pl core/ide -am test` — zero failures |

## Claim boundaries

This extraction proves:

- The `initializeComponents()` body can be extracted to an ephemeral
  delegate without changing observable behavior. The guard check
  (`if (isInitialized) return`) stays on SSE; the body delegates.
- The `setActiveScene()` SSE-specific logic (95+ lines) can be extracted
  to a stored delegate with `super` called first in the SSE stub.
- The `addField()` SSE-specific logic (29 lines) can be extracted
  to the same lifecycle delegate with `super` called first.
- The `handleProjectOpened()` pre-super cleanup (9 lines) can be
  extracted with the delegate called **before** `super`.
- The `useSceneAsVehicleForDisconnectedModels()` private helper moves
  cleanly to the lifecycle manager (only caller was `setActiveScene`
  / `activateScene`).
- The `try/finally` block in `setActiveScene` is preserved correctly
  through the delegate indirection.
- Protected parent method access is bridged correctly through
  package-private forwarding methods, following the established pattern.
- All existing characterization, extraction, and behavioral tests pass.

This extraction does **not** prove:

| Non-claim | Reason |
| --- | --- |
| Further SSE decomposition needed | At 496 lines, SSE is under the 500-line target. The remaining methods (`setSelectedField`, `handleExpandContractChange`, `setFieldToState`) are tightly coupled to SSE's state and `super` calls. |
| New initialization capabilities | No new widgets, listeners, or setup steps are added. |
| Performance improvement | Extraction is structural, not algorithmic. The ephemeral `SceneEditorInitializer` allocation is negligible — one object per application lifetime. |
| Thread safety improvement | `handleExpandContractChange` retains its `synchronized(getTreeLock())` block on SSE. No new concurrency patterns are introduced. |
| Public API expansion | No new public methods or classes are introduced. |
| Test coverage expansion | Existing characterization and extraction tests verify structural properties. No new behavioral tests are added by this extraction. |

Adjacent claims are owned by their own documents:

| Claim | Document |
| --- | --- |
| SceneEditorFieldManager and SceneRenderTargetListener extraction | [SceneEditorFieldManager Extraction](./scene-editor-field-manager-extraction.md) |
| Inner class extraction (PR #534) | Previous PR documentation |
| Modernization scorecard | [Modernization Scorecard](./modernization-scorecard.md) |
