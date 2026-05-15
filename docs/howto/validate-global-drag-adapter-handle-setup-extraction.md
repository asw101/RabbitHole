# Validate GlobalDragAdapter Handle Setup Extraction

Use this guide to verify the extraction of `HandleSetupDelegate`
from `GlobalDragAdapter` (issue #685).

For the full contract, see the [GlobalDragAdapter Handle Setup
Extraction reference](../reference/global-drag-adapter-handle-setup-extraction.md).

For the design walkthrough, see the
[Tutorial: Trace the GlobalDragAdapter Handle Setup Extraction](../tutorials/trace-global-drag-adapter-handle-setup-extraction.md).

## When to use this guide

Use this guide when:

- Reviewing changes that extract handle setup from `GlobalDragAdapter`
- Modifying `HandleSetupDelegate` (adding, removing, or changing handles)
- Changing handle registration methods on `DragAdapter`
- Adding new handle types or handle sets to the scene editor
- Verifying that `GlobalDragAdapter` meets the 500-line target

Do not use this guide for manipulator condition set changes,
interaction group configuration, snap state logic, or undo/redo
manipulation. Those responsibilities remain on `GlobalDragAdapter`
and are not affected by this extraction.

## Before you start

Run commands from the repository root:

```bash
git submodule update --init tweedle-lang
test -d tweedle-lang/Grammar
export NODE_OPTIONS=--max-old-space-size=32768
```

## Step 1: Verify compilation

```bash
mvn -pl core/ide -am compile
```

Both files (`GlobalDragAdapter.java` and `HandleSetupDelegate.java`)
must compile without errors.

## Step 2: Verify GlobalDragAdapter is under 500 lines

```bash
wc -l core/ide/src/main/java/org/alice/stageide/sceneeditor/interact/GlobalDragAdapter.java
```

Expected: ~339 lines. Must be under 500.

## Step 3: Verify HandleSetupDelegate exists and is package-private

```bash
head -50 core/ide/src/main/java/org/alice/stageide/sceneeditor/interact/HandleSetupDelegate.java
```

Check:
- Package declaration is `org.alice.stageide.sceneeditor.interact`
- Class declaration is `class HandleSetupDelegate` (no `public` modifier)
- Contains `static void setupHandles(DragAdapter adapter)`
- Has a `private HandleSetupDelegate()` constructor

## Step 4: Verify unused imports removed from GlobalDragAdapter

```bash
grep -n "Color4f\|Resizer" \
  core/ide/src/main/java/org/alice/stageide/sceneeditor/interact/GlobalDragAdapter.java
```

Expected: no output. Both `Color4f` and `Resizer` imports should be
removed since they are only used in the extracted `setupHandles()` body.

## Step 5: Verify delegation call site

```bash
grep -n "HandleSetupDelegate\|setupHandles" \
  core/ide/src/main/java/org/alice/stageide/sceneeditor/interact/GlobalDragAdapter.java
```

Expected output should show:
- One import for `HandleSetupDelegate` (or none if same package — no
  import needed)
- One call: `HandleSetupDelegate.setupHandles(this)`
- No `private void setupHandles()` method declaration

## Step 6: Verify no public API changes

Check that `GlobalDragAdapter` still exposes all original public and
protected methods:

```bash
grep -n "public\|protected" \
  core/ide/src/main/java/org/alice/stageide/sceneeditor/interact/GlobalDragAdapter.java
```

These methods must still exist with unchanged signatures:
- `public GlobalDragAdapter(...)` (constructor)
- `public void addClickAdapter(...)`
- `protected ImmutableDataSingleSelectListState<HandleStyle> getHandleStyleState()`
- `public AffineMatrix4x4 getDropTargetTransformation()`
- `public boolean shouldSnapToRotation()`
- `public boolean shouldSnapToGround()`
- `public boolean shouldSnapToGrid()`
- `public double getGridSpacing()`
- `public Angle getRotationSnapAngle()`
- `public void undoRedoEndManipulation(...)`

## Step 7: Verify all handle types preserved

```bash
grep -c "setDragAdapterAndAddHandle" \
  core/ide/src/main/java/org/alice/stageide/sceneeditor/interact/HandleSetupDelegate.java
```

Expected: 24 calls (one per handle object). Each handle must call
`handle.setDragAdapterAndAddHandle(adapter)`. Additionally, 8 of the
24 handles also call `adapter.addManipulationListener()`.

## Step 8: Run full module test suite

```bash
mvn -pl core/ide -am -DfailIfNoTests=false -Dcheckstyle.skip test
```

No regressions should appear. This confirms the handle setup still
wires correctly at runtime.

## Step 9: Verify DragAdapter parameter type

```bash
grep "setupHandles" \
  core/ide/src/main/java/org/alice/stageide/sceneeditor/interact/HandleSetupDelegate.java
```

The parameter type must be `DragAdapter`, not `GlobalDragAdapter`.
This is correct because `addManipulationListener()` is defined on
`DragAdapter` and `setDragAdapterAndAddHandle()` (on handle classes)
accepts a `DragAdapter` parameter.

## Troubleshooting

### "cannot find symbol" for HandleSetupDelegate

The file must be in
`core/ide/src/main/java/org/alice/stageide/sceneeditor/interact/`.
Verify the package declaration matches
`org.alice.stageide.sceneeditor.interact`. Since it is in the same
package as `GlobalDragAdapter`, no import is needed.

### Handle not appearing in scene editor

If a handle type stops appearing when selecting objects, verify that
`HandleSetupDelegate.setupHandles()` creates the handle, adds it to
the correct `HandleSet`, and calls both
`adapter.addManipulationListener(handle)` and
`handle.setDragAdapterAndAddHandle(adapter)`.

### Anonymous inner class compilation error

The `rotateAboutYAxisStoodUp` handle uses an anonymous
`ObjectRotateDragManipulator` subclass. This class captures no outer
state — it only overrides `getHandleSetToEnable()` with a constant.
If compilation fails, verify the anonymous class does not reference
`this` (the enclosing class instance).

### Color4f or Resizer "unused import" warnings

These imports should be removed from `GlobalDragAdapter.java` since
they are only used in the extracted handle setup code. They must be
present in `HandleSetupDelegate.java` instead.
