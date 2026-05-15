# Tutorial: Trace the GlobalDragAdapter Handle Setup Extraction

This tutorial walks through the extraction of `HandleSetupDelegate`
from `GlobalDragAdapter` (issue #685). You will trace each design
decision — why a static method was chosen over a composition object,
why the parameter type is `DragAdapter` instead of
`GlobalDragAdapter`, and how the anonymous inner class moves safely.

For the full contract, see the [GlobalDragAdapter Handle Setup
Extraction reference](../reference/global-drag-adapter-handle-setup-extraction.md).

For validation steps, see the [Validation
how-to](../howto/validate-global-drag-adapter-handle-setup-extraction.md).

## Contents

- [Goal](#goal)
- [1. Understand the pre-extraction structure](#1-understand-the-pre-extraction-structure)
- [2. Identify the extraction boundary](#2-identify-the-extraction-boundary)
- [3. Trace the this-reference audit](#3-trace-the-this-reference-audit)
- [4. Understand the static method choice](#4-understand-the-static-method-choice)
- [5. Trace the parameter type decision](#5-trace-the-parameter-type-decision)
- [6. Trace the anonymous inner class safety](#6-trace-the-anonymous-inner-class-safety)
- [7. Trace the import cleanup](#7-trace-the-import-cleanup)
- [8. Verify the result](#8-verify-the-result)

## Goal

After this tutorial you will be able to explain:

- Why `setupHandles()` is a clean extraction boundary (no shared
  mutable state with the rest of the constructor)
- Why a static utility method is the right pattern (not a composition
  object or inner class)
- Why `DragAdapter` is the parameter type (not `GlobalDragAdapter`)
- Why the anonymous `ObjectRotateDragManipulator` subclass moves
  safely without capturing outer state
- Which imports become unused after extraction and why

Open these source files alongside this guide:

```text
core/ide/src/main/java/org/alice/stageide/sceneeditor/interact/GlobalDragAdapter.java
core/ide/src/main/java/org/alice/stageide/sceneeditor/interact/HandleSetupDelegate.java
```

## 1. Understand the pre-extraction structure

Before extraction, `GlobalDragAdapter.java` is 534 lines. The
constructor (starting at approximately line 71) does three things in
sequence:

1. **Lines ~71–215:** Creates `ManipulatorConditionSet` instances for
   various mouse interactions (left-click translate, ctrl-click
   rotate, alt-click resize, etc.) and registers them with
   `addManipulatorConditionSet()`.

2. **Line 219:** Calls `setupHandles()` — a private method that
   creates all visual handles.

3. **Lines ~221–254:** Configures `InteractionGroup` instances mapping
   `HandleStyle` enum values to manipulator condition sets, registers
   the handle style listener, and optionally creates a silhouette.

The `setupHandles()` method at lines 257–451 is a single, cohesive
block that creates 24 handle objects — rotation rings (3 standard +
3 joint + 1 stood-up), translation arrows (3 joint + 6 absolute in
paired directions), scale handles (7 including uniform), and a
visualization axis — and registers each with the drag adapter.

## 2. Identify the extraction boundary

`setupHandles()` is an ideal extraction target because:

1. **It is private.** No subclass overrides it or calls it. Removing
   it cannot break any external contract.

2. **It is called exactly once** — from the constructor at line 219.

3. **It has no return value.** It only produces side effects
   (registering handles with the adapter).

4. **It reads no fields of GlobalDragAdapter.** Every `this` reference
   in the method body calls either `addManipulationListener()` or
   `setDragAdapterAndAddHandle()` — both inherited from `DragAdapter`.

These properties mean the method body can be moved to a separate
class with zero coupling back to `GlobalDragAdapter`.

## 3. Trace the this-reference audit

Search for all `this` references in the original `setupHandles()`
method (lines 257–451):

```bash
grep -n "this\." core/ide/src/main/java/org/alice/stageide/sceneeditor/interact/GlobalDragAdapter.java \
  | awk -F: '$2 >= 257 && $2 <= 451'
```

You will find approximately 32 occurrences. Each one falls into
exactly two categories:

| Pattern | Count | Method called |
|---------|-------|---------------|
| `this.addManipulationListener(handle)` | 8 | Defined on `DragAdapter` |
| `handle.setDragAdapterAndAddHandle(this)` | 24 | Passes `this` as `DragAdapter` |

Not all handles call `addManipulationListener()` — only 8 do (e.g.,
`handleAxis`, `rotateAboutYAxisStoodUp`, the 6 absolute translation
handles). All 24 handles call `setDragAdapterAndAddHandle(this)`.

No `this.someField` reads or writes exist. No calls to methods
unique to `GlobalDragAdapter` exist. This confirms the parameter
type can be `DragAdapter`.

## 4. Understand the static method choice

Three extraction patterns were considered:

| Pattern | Pros | Cons | Chosen? |
|---------|------|------|---------|
| **Static utility method** | Simplest; no state; no lifecycle | Cannot be overridden | ✓ |
| **Composition object** | Could hold state; testable | No state to hold; adds unnecessary object | ✗ |
| **Inner class** | Stays visually near `GlobalDragAdapter` | Doesn't reduce line count | ✗ |

The static method wins because:
- There is no state to hold (all handles are local variables)
- There is no behavior to override (method is private)
- It maximally reduces `GlobalDragAdapter`'s line count
- It produces the simplest possible delegate

## 5. Trace the parameter type decision

The delegate method signature is:

```java
static void setupHandles(DragAdapter adapter)
```

Not `GlobalDragAdapter adapter`. This is correct because:

1. `addManipulationListener()` is defined on `DragAdapter` (public).
2. `setDragAdapterAndAddHandle()` accepts a `DragAdapter` parameter.
3. No method or field specific to `GlobalDragAdapter` is accessed.

Using the supertype keeps the delegate loosely coupled. If another
`DragAdapter` subclass needed the same handles, it could reuse this
delegate without modification.

## 6. Trace the anonymous inner class safety

At approximately line 267, the original code creates:

```java
rotateAboutYAxisStoodUp.setManipulation(new ObjectRotateDragManipulator() {
    @Override
    protected HandleSet getHandleSetToEnable() {
        return new HandleSet(
            HandleSet.HandleGroup.Y_AXIS,
            HandleSet.HandleGroup.VISUALIZATION,
            HandleSet.HandleGroup.STOOD_UP_ROTATION);
    }
});
```

This anonymous class is safe to move because:

1. **No outer-class field access.** The override returns a constant
   `HandleSet` constructed from enum values.
2. **No outer-class method calls.** The `getHandleSetToEnable()`
   body does not call `this` on the enclosing class.
3. **No captured local variables.** No variables from the enclosing
   scope are referenced.

In the delegate, this anonymous class compiles identically because
it has no implicit reference to any enclosing instance — it only
references static enum constants.

## 7. Trace the import cleanup

After moving `setupHandles()` out of `GlobalDragAdapter`, three
imports become unused and are removed:

| Import | Status | Used only in setupHandles() for |
|--------|--------|---------------------------------|
| `edu.cmu.cs.dennisc.color.Color4f` | Removed | `RotationRingHandle` (`RED`, `BLUE`, `WHITE`), `JointRotationRingHandle` (`WHITE`, `RED`, `BLUE`), `LinearTranslateHandle` (`GREEN`, `RED`, `WHITE`, `YELLOW`) |
| `org.alice.interact.event.ManipulationEvent` | Removed | `ManipulationEventCriteria` constructors (`EventType.Rotate`, `EventType.Translate`, `EventType.Scale`) |
| `org.alice.interact.event.ManipulationEventCriteria` | Removed | Handle condition registration via `addCondition()` |
| `edu.cmu.cs.dennisc.scenegraph.scale.Resizer` | Retained | Still used in `setUpControls()` for `ResizeDragManipulator(Resizer.UNIFORM, ...)` |

Verify that `Color4f` is no longer used (only a commented-out
reference remains) and that `Resizer` is still actively used:

```bash
grep -n "Color4f\|Resizer" \
  core/ide/src/main/java/org/alice/stageide/sceneeditor/interact/GlobalDragAdapter.java
```

After extraction, `Color4f` appears only in a comment. `Resizer`
appears in the import and at line 170 (`ResizeDragManipulator`
constructor). The wildcard imports (`org.alice.interact.handle.*`,
etc.) remain because they supply `HandleSet`, `HandleStyle`, and
other types still used in the constructor and other methods.

## 8. Verify the result

After extraction:

```bash
wc -l core/ide/src/main/java/org/alice/stageide/sceneeditor/interact/GlobalDragAdapter.java
# Expected: ~339 lines

wc -l core/ide/src/main/java/org/alice/stageide/sceneeditor/interact/HandleSetupDelegate.java
# Expected: ~260 lines

mvn -pl core/ide -am compile
# Expected: BUILD SUCCESS
```

The total line count across both files (~594) is slightly more than
the original 534 due to the new file's copyright header, package
declaration, and class/method structure. The goal is not fewer total
lines — it is that no single file exceeds 500 lines and each file
has a single, clear responsibility.
