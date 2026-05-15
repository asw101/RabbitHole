# GlobalDragAdapter Handle Setup Extraction

This reference documents the extraction of the `setupHandles()` method
from `GlobalDragAdapter` (534 lines) into a new delegate class
`HandleSetupDelegate`. After extraction, `GlobalDragAdapter.java` is
reduced to ~339 lines (well under the 500-line target).

Issue #685 decomposes GlobalDragAdapter without changing observable
behavior. All handle creation, manipulator wiring, and drag adapter
registration are preserved identically.

## Contents

- [Motivation](#motivation)
- [Architecture](#architecture)
- [Extracted class](#extracted-class)
- [File inventory](#file-inventory)
- [Delegation pattern](#delegation-pattern)
- [Visibility rules](#visibility-rules)
- [Delegation method on GlobalDragAdapter](#delegation-method-on-globaldragadapter)
- [Import cleanup](#import-cleanup)
- [Configuration](#configuration)
- [Validation](#validation)
- [Acceptance criteria](#acceptance-criteria)
- [Claim boundaries](#claim-boundaries)

## Motivation

`GlobalDragAdapter.java` contained 534 lines mixing two concerns:

1. **Handle setup** — Creation and configuration of all visual handles
   (rotation rings, translation arrows, scale handles, manipulation
   axes) and their association with handle sets, interaction groups,
   and drag manipulators (~195 lines, `setupHandles()` method at
   lines 257–451).
2. **Drag adapter orchestration** — Manipulator condition sets, mouse
   click routing, interaction group configuration, snap state
   delegation, undo/redo end-manipulation wiring, and the handle style
   listener (~339 lines).

The handle setup concern is entirely self-contained: it creates handle
objects, configures their properties, and registers them with the drag
adapter by calling `addManipulationListener()` (defined on
`DragAdapter`) and `setDragAdapterAndAddHandle()` (defined on each
handle class, accepting a `DragAdapter` parameter). No handle setup
code reads or writes any `GlobalDragAdapter` instance field.

## Architecture

```text
GlobalDragAdapter (~339 lines)
├── Constructor: sets up manipulator condition sets,
│   calls HandleSetupDelegate.setupHandles(this),
│   configures interaction groups and silhouette
├── addClickAdapter()
├── getHandleStyleState()
├── getDropTargetTransformation()
├── handleStyleListener
├── Snap state overrides (5 methods)
└── undoRedoEndManipulation()

HandleSetupDelegate (package-private, ~260 lines)
└── static setupHandles(DragAdapter adapter)
    ├── ManipulationAxes (visualization axis)
    ├── StoodUpRotationRingHandle (default Y-axis rotation)
    ├── RotationRingHandle × 3 (Y, X, Z axes)
    ├── JointRotationRingHandle × 3 (Z, Y, X axes)
    ├── LinearTranslateHandle × 3 (joint Y, X, Z — local movement)
    ├── LinearTranslateHandle × 6 (absolute: up/down, left/right, forward/backward)
    ├── LinearScaleHandle × 7 (uniform, X, Y, Z, XY, XZ, YZ)
    └── All 24 handles registered via handle.setDragAdapterAndAddHandle(adapter)
        (8 also via adapter.addManipulationListener())
```

## Extracted class

### HandleSetupDelegate

| Property      | Value                                                    |
|---------------|----------------------------------------------------------|
| Package       | `org.alice.stageide.sceneeditor.interact`                |
| Visibility    | Package-private (`class`, not `public class`)            |
| Type          | Utility class with single static method                  |
| Constructor   | Private (prevents instantiation)                         |
| State         | None — entirely stateless                                |

**Static method:**

```java
static void setupHandles(DragAdapter adapter)
```

Creates and registers all visual manipulation handles with the
provided `DragAdapter` instance. The parameter type is `DragAdapter`
(not `GlobalDragAdapter`) because `addManipulationListener()` is
defined on `DragAdapter` and `setDragAdapterAndAddHandle()` accepts
a `DragAdapter` parameter — no `GlobalDragAdapter`-specific API is
needed.

**Handle inventory created by `setupHandles`:**

| Handle variable             | Type                        | Handle set / groups          |
|-----------------------------|-----------------------------|------------------------------|
| `handleAxis`                | `ManipulationAxes`          | VISUALIZATION group          |
| `rotateAboutYAxisStoodUp`   | `StoodUpRotationRingHandle` | DEFAULT_INTERACTION          |
| `rotateAboutYAxis`          | `RotationRingHandle`        | ROTATION_INTERACTION         |
| `rotateAboutXAxis`          | `RotationRingHandle`        | ROTATION_INTERACTION         |
| `rotateAboutZAxis`          | `RotationRingHandle`        | ROTATION_INTERACTION         |
| `rotateJointAboutZAxis`     | `JointRotationRingHandle`   | JOINT_ROTATION_INTERACTION   |
| `rotateJointAboutYAxis`     | `JointRotationRingHandle`   | JOINT_ROTATION_INTERACTION   |
| `rotateJointAboutXAxis`     | `JointRotationRingHandle`   | JOINT_ROTATION_INTERACTION   |
| `translateJointYAxis`       | `LinearTranslateHandle`     | JOINT_TRANSLATION_INTERACTION|
| `translateJointXAxis`       | `LinearTranslateHandle`     | JOINT_TRANSLATION_INTERACTION|
| `translateJointZAxis`       | `LinearTranslateHandle`     | JOINT_TRANSLATION_INTERACTION|
| `translateUp`               | `LinearTranslateHandle`     | ABSOLUTE_TRANSLATION group   |
| `translateDown`             | `LinearTranslateHandle`     | ABSOLUTE_TRANSLATION group   |
| `translateXAxisRight`       | `LinearTranslateHandle`     | ABSOLUTE_TRANSLATION group   |
| `translateXAxisLeft`        | `LinearTranslateHandle`     | ABSOLUTE_TRANSLATION group   |
| `translateForward`          | `LinearTranslateHandle`     | ABSOLUTE_TRANSLATION group   |
| `translateBackward`         | `LinearTranslateHandle`     | ABSOLUTE_TRANSLATION group   |
| `scaleAxisUniform`          | `LinearScaleHandle`         | RESIZE_INTERACTION           |
| `scaleAxisX`                | `LinearScaleHandle`         | RESIZE_INTERACTION           |
| `scaleAxisY`                | `LinearScaleHandle`         | RESIZE_INTERACTION           |
| `scaleAxisZ`                | `LinearScaleHandle`         | RESIZE_INTERACTION           |
| `scaleAxisXY`               | `LinearScaleHandle`         | RESIZE_INTERACTION           |
| `scaleAxisXZ`               | `LinearScaleHandle`         | RESIZE_INTERACTION           |
| `scaleAxisYZ`               | `LinearScaleHandle`         | RESIZE_INTERACTION           |

Total: 24 handle objects.

Not all handles follow the same registration pattern. The common steps
are:

1. Create handle instance
2. Set manipulation (drag manipulator)
3. Add to handle set and/or groups
4. Add manipulation event criteria conditions (some handles)
5. Call `adapter.addManipulationListener(handle)` (8 of 24 handles)
6. Call `handle.setDragAdapterAndAddHandle(adapter)` (all 24 handles)
7. Set debug name

**Anonymous inner class:** The `rotateAboutYAxisStoodUp` handle
contains an anonymous `ObjectRotateDragManipulator` subclass that
overrides `getHandleSetToEnable()` with a constant `HandleSet`. This
anonymous class captures no outer-class state and moves safely into
the delegate.

## File inventory

| File | Action | Lines after |
|------|--------|-------------|
| `core/ide/src/main/java/org/alice/stageide/sceneeditor/interact/GlobalDragAdapter.java` | Modify | ~339 |
| `core/ide/src/main/java/org/alice/stageide/sceneeditor/interact/HandleSetupDelegate.java` | Create | ~260 |

## Delegation pattern

The delegation uses a static method call — the simplest possible
pattern for stateless extraction.

**Before:**

```java
// In GlobalDragAdapter constructor
setupHandles();
```

**After:**

```java
// In GlobalDragAdapter constructor
HandleSetupDelegate.setupHandles(this);
```

The private `setupHandles()` method is deleted entirely. No
forwarding stub is needed because `setupHandles` was private and
called only from the constructor.

## Visibility rules

| Element | Visibility | Rationale |
|---------|------------|-----------|
| `HandleSetupDelegate` class | Package-private | Same package as `GlobalDragAdapter`; no external callers |
| `HandleSetupDelegate()` constructor | Private | Utility class; prevent instantiation |
| `setupHandles(DragAdapter)` | Package-private (default) | Only called from `GlobalDragAdapter` in same package |

No methods on `DragAdapter` or `GlobalDragAdapter` change visibility.
The `addManipulationListener` method used by the delegate is already
`public` on `DragAdapter`. The `setDragAdapterAndAddHandle` method is
defined on handle classes and already accepts `DragAdapter` as its
parameter type.

## Delegation method on GlobalDragAdapter

There is no delegation method. The call site in the constructor
changes from `setupHandles()` to `HandleSetupDelegate.setupHandles(this)`.
The private method is removed. No forwarding stub remains.

## Import cleanup

After extraction, three imports in `GlobalDragAdapter.java` become
unused and are removed:

| Import | Status | Reason |
|--------|--------|--------|
| `edu.cmu.cs.dennisc.color.Color4f` | Removed | Only used in `setupHandles()` for handle colors |
| `org.alice.interact.event.ManipulationEvent` | Removed | Only used in `setupHandles()` for `ManipulationEventCriteria` constructors |
| `org.alice.interact.event.ManipulationEventCriteria` | Removed | Only used in `setupHandles()` for handle condition registration |
| `edu.cmu.cs.dennisc.scenegraph.scale.Resizer` | Retained | Still used in `setUpControls()` for `ResizeDragManipulator(Resizer.UNIFORM, ...)` |

All wildcard imports (`org.alice.interact.handle.*`,
`org.alice.interact.manipulator.*`, etc.) remain because they supply
types still used in the remaining code (`HandleSet`, `HandleStyle`,
`ClickAdapterManipulator`, condition classes).

## Configuration

No configuration changes. The extraction is purely structural. No
new properties, flags, or environment variables are introduced.

## Validation

See [Validate GlobalDragAdapter Handle Setup
Extraction](../howto/validate-global-drag-adapter-handle-setup-extraction.md)
for step-by-step verification commands.

## Acceptance criteria

1. `GlobalDragAdapter.java` is under 500 lines (target: ~339).
2. `HandleSetupDelegate.java` exists in the same package, is
   package-private, and contains a single static `setupHandles` method.
3. `mvn -pl core/ide -am compile` succeeds with no errors.
4. `mvn -pl core/ide -am -DfailIfNoTests=false -Dcheckstyle.skip test`
   succeeds with no regressions.
5. No public API on `GlobalDragAdapter` changes (no new public methods,
   no removed public methods, no signature changes).
6. The `Color4f` and `Resizer` imports are removed from
   `GlobalDragAdapter.java`.
7. All 24 handle objects created by `setupHandles` are unchanged (same
   handle types, same handle sets/groups, same conditions, same names).

## Claim boundaries

This extraction covers **only** the `setupHandles()` method body
(lines 257–451 of the original 534-line file).

**In scope:**
- Handle creation and configuration
- Handle-to-adapter registration
- Import cleanup for `Color4f` and `Resizer`

**Out of scope:**
- Manipulator condition set setup (constructor lines before
  `setupHandles()` call)
- Interaction group configuration (constructor lines after
  `setupHandles()` call)
- `addClickAdapter`, snap state overrides, `undoRedoEndManipulation`
- Any changes to `DragAdapter` or other superclasses
- Any changes to other subclasses of `DragAdapter`
