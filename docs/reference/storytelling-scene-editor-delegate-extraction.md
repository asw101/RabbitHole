# StorytellingSceneEditor Delegate Extraction

This reference describes the extraction of field-management and camera/marker
helper methods from `StorytellingSceneEditor` into two package-private delegate
classes: `SceneEditorFieldManager` and `SceneEditorCameraHelper`. This is
step 2 of RabbitHole issue #528, reducing `StorytellingSceneEditor` from
1259 lines to ~1072 lines.

The extraction is a pure internal refactor. The public API surface —
`StorytellingSceneEditor` with its 37 public methods — is unchanged. All
existing field-management behavior, camera marker transforms, and scene editor
functionality are preserved identically. The `AbstractSceneEditor` override
methods remain on `StorytellingSceneEditor` as thin forwarding stubs.

## Contents

- [Motivation](#motivation)
- [Architecture](#architecture)
- [Class responsibilities](#class-responsibilities)
  - [SceneEditorFieldManager](#sceneeditorfieldmanager)
  - [SceneEditorCameraHelper](#sceneeditorcamerahelper)
  - [StorytellingSceneEditor changes](#storytellingsceneeditor-changes)
- [Public API](#public-api)
- [Package-private collaboration](#package-private-collaboration)
- [Delegate patterns](#delegate-patterns)
- [Visibility changes](#visibility-changes)
- [Security boundary](#security-boundary)
- [Error handling contract](#error-handling-contract)
- [Configuration](#configuration)
- [Validation](#validation)
- [Acceptance criteria](#acceptance-criteria)
- [Claim boundaries](#claim-boundaries)

## Motivation

After the [StorytellingSceneEditor Characterization](./storytelling-scene-editor-characterization.md)
(step 1) established a reflection-based structural contract for the class,
`StorytellingSceneEditor` remained at 1259 lines mixing six concerns:
scene lifecycle, rendering callbacks, camera management, drag-and-drop, field
management (add/copy/remove/riders), and camera/marker helpers. The field
management overrides alone account for ~170 lines, and the camera/marker
helpers add ~60 lines of stateless logic.

This extraction follows the delegate pattern established by
[NonCachingTextRenderer Inner Class Extraction](./noncaching-text-renderer-inner-class-extraction.md),
[Decoder Delegate Decomposition](./decoder-delegate-decomposition.md), and
[Encoder Delegate Decomposition](./encoder-delegate-decomposition.md).

## Architecture

```text
StorytellingSceneEditor (~1072 lines, singleton)
  extends AbstractSceneEditor
  implements RenderTargetListener
  ├── SceneEditorFieldManager (package-private, ~257 lines)
  │   └── Field add/copy/remove statement generation, rider detection,
  │       vehicle call construction, reference replacement
  └── SceneEditorCameraHelper (package-private final, ~91 lines, all static)
      └── Camera/object marker transforms, marker colors,
          point-of-view calculation, marker field lookup
```

Both delegate classes live in `org.alice.stageide.sceneeditor`, the same
package as `StorytellingSceneEditor`. `SceneEditorFieldManager` is
package-private with a back-reference to `AbstractSceneEditor`.
`SceneEditorCameraHelper` is a stateless static utility with a private
constructor.

## Class responsibilities

### SceneEditorFieldManager

| Responsibility | Method | Origin |
| --- | --- | --- |
| Copy field statements | `getDoStatementsForCopyField(UserField, UserField, AffineMatrix4x4, Statement)` | Extracted from `StorytellingSceneEditor.getDoStatementsForCopyField` |
| Add field statements | `getDoStatementsForAddField(UserField, AffineMatrix4x4)` | Extracted from `StorytellingSceneEditor.getDoStatementsForAddField` |
| Undo add field | `getUndoStatementsForAddField(UserField)` | Extracted from `StorytellingSceneEditor.getUndoStatementsForAddField` |
| Rider detection | `getRiders(UserField)` | Extracted from `StorytellingSceneEditor.getRiders` |
| Remove field statements | `getDoStatementsForRemoveField(UserField, Map)` | Extracted from `StorytellingSceneEditor.getDoStatementsForRemoveField` |
| Undo remove field | `getUndoStatementsForRemoveField(UserField, Map)` | Extracted from `StorytellingSceneEditor.getUndoStatementsForRemoveField` |
| Vehicle call detection | `asSetVehicleCall(Statement)` (static) | Extracted from private method on SSE |
| Reference replacement | `replaceReferencesInExpression(UserField, UserField, Statement)` (private) | Extracted from private method on SSE |
| Vehicle field matching | `doesSetVehicleImplyVehicle(MethodInvocation, UserField)` (private) | Extracted from inline logic in `getRiders` |
| Direct rider check | `isDirectRider(UserField, Expression)` (private) | New private helper, was inline |
| Joint rider check | `isJointRider(UserField, Expression)` (private) | New private helper, was inline |

`SceneEditorFieldManager` stores an `AbstractSceneEditor` reference passed at
construction and uses it for field instance lookups and active scene access.
The constructor signature:

```java
SceneEditorFieldManager(AbstractSceneEditor editor) {
  this.editor = editor;
}
```

**Key design decision:** `getDoStatementsForCopyField` receives the
`stateCodeStatement` (the output of `getCurrentStateCodeForField`) as a
parameter rather than calling back to `StorytellingSceneEditor`. This keeps
the delegate independent of the singleton and avoids a circular reference.

**`asSetVehicleCall` is static and package-private.** It is shared between
`SceneEditorFieldManager.getRiders()` and
`StorytellingSceneEditor.useSceneAsVehicleForDisconnectedModels()`. Making it
static avoids requiring a `SceneEditorFieldManager` instance for the unrelated
caller.

### SceneEditorCameraHelper

| Responsibility | Method | Origin |
| --- | --- | --- |
| Camera marker transform | `getTransformForNewCameraMarker(TransformableImp)` | Extracted from SSE |
| Object marker transform | `getTransformForNewObjectMarker(EntityImp)` | Extracted from SSE |
| Object marker color | `getColorForNewObjectMarker()` | Extracted from SSE |
| Camera marker color | `getColorForNewCameraMarker()` | Extracted from SSE |
| Object point of view | `getGoodPointOfViewInSceneForObject(AxisAlignedBox)` | Extracted from SSE |
| Marker for field | `getMarkerForField(Object)` | Extracted from SSE |

`SceneEditorCameraHelper` is a stateless utility class. All methods are static.
The class is `final` with a `private` constructor, preventing instantiation.
No back-reference to `StorytellingSceneEditor` is needed because each method
receives its required data as parameters.

```java
final class SceneEditorCameraHelper {
  private SceneEditorCameraHelper() {}
  // all static methods
}
```

### StorytellingSceneEditor changes

| Change | Detail |
| --- | --- |
| New field | `private final SceneEditorFieldManager fieldManager = new SceneEditorFieldManager(this)` (inline initialization; the SSE constructor is empty) |
| 5 override stubs + 1 forwarding stub | `getDoStatementsForCopyField`, `getDoStatementsForAddField`, `getUndoStatementsForAddField`, `getDoStatementsForRemoveField`, `getUndoStatementsForRemoveField` (`@Override` from `AbstractSceneEditor`); `getRiders` (no `@Override`, not declared on parent) — all delegate to `fieldManager` |
| 6 public forwarding stubs | `getTransformForNewCameraMarker`, `getTransformForNewObjectMarker`, `getColorForNewObjectMarker`, `getColorForNewCameraMarker`, `getGoodPointOfViewInSceneForObject`, `getMarkerForField` — bodies delegate to `SceneEditorCameraHelper` static methods |
| `asSetVehicleCall` removed | Private method removed; replaced by `SceneEditorFieldManager.asSetVehicleCall` (static, package-private) |
| `isSetVehicleInvocation` removed | Private method removed; callers use `SceneEditorFieldManager.asSetVehicleCall(s) != null` |
| `replaceReferencesInExpression` removed | Private method moved entirely to `SceneEditorFieldManager` |
| Net reduction | 1259 → 1072 lines (−187 lines) |

## Public API

The public API is exclusively `StorytellingSceneEditor`. No API changes are
made by this extraction. All 37 public methods remain, with identical
signatures and behavior.

```java
// Field management overrides — forwarded to SceneEditorFieldManager
@Override public Statement[] getDoStatementsForCopyField(UserField, UserField, AffineMatrix4x4);
@Override public Statement[] getDoStatementsForAddField(UserField, AffineMatrix4x4);
@Override public Statement[] getUndoStatementsForAddField(UserField);
public Map<AbstractField, Statement> getRiders(UserField);
@Override public Statement[] getDoStatementsForRemoveField(UserField, Map);
@Override public Statement[] getUndoStatementsForRemoveField(UserField, Map);

// Camera/marker helpers — forwarded to SceneEditorCameraHelper
public AffineMatrix4x4 getTransformForNewCameraMarker();
public AffineMatrix4x4 getTransformForNewObjectMarker();
public Color getColorForNewObjectMarker();
public Color getColorForNewCameraMarker();
public AffineMatrix4x4 getGoodPointOfViewInSceneForObject(AxisAlignedBox);
public MarkerImp getMarkerForField(UserField);
```

Callers never see the delegate classes.

## Package-private collaboration

`SceneEditorFieldManager` accesses `AbstractSceneEditor` methods via the
stored editor reference:

| Method | Purpose |
| --- | --- |
| `editor.getInstanceInJavaVMForField(field)` | Retrieve the Java VM object for a scene field |
| `editor.getFieldForInstanceInJavaVM(instance)` | Reverse-lookup: find the field for a VM object |
| `editor.getActiveSceneInstance()` | Get the current scene's `UserInstance` for setup generation |

`SceneEditorCameraHelper` has no collaboration dependencies. All data is
passed as method parameters.

Two methods on `StorytellingSceneEditor` call
`SceneEditorFieldManager.asSetVehicleCall(statement)` as a static
package-private utility:

- `useSceneAsVehicleForDisconnectedModels()` — iterates setup statements to
  re-parent disconnected models onto the scene.
- `getCurrentStateCodeForField()` — strips `setVehicle` calls from the
  generated state-code block before wrapping it in a `DoTogether`.

## Delegate patterns

### Instance delegate (SceneEditorFieldManager)

The field manager follows the same instance-delegate pattern as
[StatementEncoder](./statement-encoder-extraction.md) and the Phase 2
[Manager extraction](./noncaching-text-renderer-inner-class-extraction.md):

1. Package-private class in the same package
2. `final` reference to the parent, set at construction
3. No interface or inheritance — direct method calls
4. Parent's `@Override` methods become thin forwarding stubs

### Static utility (SceneEditorCameraHelper)

The camera helper follows the static utility pattern:

1. `final` class, `private` constructor
2. All methods `static` and package-private
3. No state, no back-reference
4. Each method receives all required data as parameters

This is appropriate because every camera/marker method is a pure function of
its inputs — no instance state of `StorytellingSceneEditor` is needed beyond
the values passed as arguments.

## Visibility changes

No visibility changes were required. The delegate classes are package-private,
and all methods they call on `AbstractSceneEditor` are already `public` or
`protected` (accessible from the same package).

| Member | Original visibility | New visibility | Reason |
| --- | --- | --- | --- |
| `asSetVehicleCall` | `private` on SSE | `static` package-private on `SceneEditorFieldManager` | Shared between `getRiders` and `useSceneAsVehicleForDisconnectedModels` |
| `isSetVehicleInvocation` | `private` on SSE | Removed | Replaced by `asSetVehicleCall(s) != null` at call sites |
| `replaceReferencesInExpression` | `private` on SSE | `private` on `SceneEditorFieldManager` | Sole caller moved to delegate |

## Security boundary

Both delegates are package-private — no new public API surface is exposed.

- `SceneEditorCameraHelper` is stateless (all static, private constructor) —
  no mutable state risk.
- `SceneEditorFieldManager` holds a `final` editor reference — no lifecycle
  concerns beyond the parent singleton.
- No new I/O, deserialization, network, or string-based code generation paths
  are introduced.
- No `setAccessible` or reflection is used in production code.

## Error handling contract

All error handling behavior is preserved identically from the original code:

- `SceneEditorFieldManager.getDoStatementsForAddField` returns `null`
  `initialTransform` to `SetUpMethodGenerator` when the model resource class
  has no bounding box — this matches the original behavior.
- `SceneEditorCameraHelper.getGoodPointOfViewInSceneForObject` throws
  `RuntimeException("todo")` — this is the pre-existing stub behavior,
  preserved as-is.
- `SceneEditorCameraHelper.getMarkerForField` returns `null` when the field
  instance is not an `SMarker` — original null-return behavior preserved.

## Configuration

No configuration changes. The delegates are internal implementation details
with no properties, flags, or external configuration.

## Validation

### Run the full characterization suite

```bash
git submodule update --init tweedle-lang
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/ide -am \
  -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Dcheckstyle.skip \
  -Dtest=StorytellingSceneEditorCharacterizationTest \
  test
```

Expected outcome: 80 test methods. All pass. The characterization suite
verifies the public API surface, inner class count, field declarations, and
aggregate stability guardrails. The forwarding stubs are transparent to the
reflection-based tests.

### Run the full core/ide test suite

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -DincludeSims=false -Dinstall4j.skip \
  -pl core/ide -am \
  -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  -Dcheckstyle.skip \
  test
```

Expected outcome: 414+ tests across the `core/ide` reactor. 0 failures,
0 errors. BUILD SUCCESS.

### Verify line counts

```bash
wc -l core/ide/src/main/java/org/alice/stageide/sceneeditor/StorytellingSceneEditor.java
wc -l core/ide/src/main/java/org/alice/stageide/sceneeditor/SceneEditorFieldManager.java
wc -l core/ide/src/main/java/org/alice/stageide/sceneeditor/SceneEditorCameraHelper.java
```

Expected:
- `StorytellingSceneEditor.java`: ~1072 lines (down from 1259)
- `SceneEditorFieldManager.java`: ~257 lines
- `SceneEditorCameraHelper.java`: ~91 lines

## Acceptance criteria

1. All 80 characterization tests pass without modification.
2. All 414+ `core/ide` tests pass.
3. `StorytellingSceneEditor` is under 1100 lines.
4. Both delegate classes are package-private (not public).
5. No new public methods or fields are added to `StorytellingSceneEditor`.
6. The `@Override` annotations remain on `StorytellingSceneEditor` for all
   `AbstractSceneEditor` overrides.
7. `asSetVehicleCall` is accessible to both `getRiders` (in delegate) and
   `useSceneAsVehicleForDisconnectedModels` (in SSE).

## Claim boundaries

This extraction does **not** claim:

- Correctness of field add/copy/remove behavior (only structural preservation)
- Correctness of camera/marker transform calculations
- 3D scene rendering or camera view switching behavior
- Drag-and-drop interaction
- VR pipeline integration
- Object manipulation handle visibility
- Runtime animation playback
- Desktop UI layout or project save/load
- Coverage of `getGoodPointOfViewInSceneForObject` (pre-existing `todo` stub)

The characterization tests verify structural identity (method names, parameter
types, modifiers) via reflection. Behavioral correctness is asserted only by
the pre-existing `core/ide` test suite.
