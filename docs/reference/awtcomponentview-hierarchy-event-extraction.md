# AwtComponentView Hierarchy Event Extraction

This reference documents the extraction of hierarchy lifecycle internals from
`AwtComponentView` (issue #686) into a new package-private
`AwtHierarchyEventHandler` class, and the removal of 10 deprecated
AWT listener forwarding methods. The extraction reduces
`AwtComponentView.java` from 575 lines to ~483 lines (under the 500-line
target).

## Contents

- [Motivation](#motivation)
- [Extracted class](#extracted-class)
- [Removed deprecated methods](#removed-deprecated-methods)
- [File inventory](#file-inventory)
- [Caller migration pattern](#caller-migration-pattern)
- [Protected hooks retained](#protected-hooks-retained)
- [Validation commands](#validation-commands)
- [Compatibility rules](#compatibility-rules)
- [Examples](#examples)

## Motivation

`AwtComponentView` is the root view class in the Croquet UI toolkit,
extended by over 100 subclasses across `core/croquet` and `core/ide`. At
575 lines it contained two distinct responsibilities mixed into the main
class body:

| Responsibility | Lines | Issue |
| --- | --- | --- |
| Hierarchy lifecycle tracking | ~45 lines (4 fields + 2 private methods + protected method body) | Internal implementation detail leaking into the public class surface |
| Deprecated AWT listener forwarding | ~49 lines (10 `@Deprecated` methods) | Wrapper methods that simply delegate to `getAwtComponent()` — callers should use the AWT component directly |

Extracting the hierarchy lifecycle into `AwtHierarchyEventHandler` improves
cohesion: the handler owns its own state (`isDisplayableState`, `awtParent`,
`isWarningAlreadyPrinted`) and implements `HierarchyListener` directly.

Removing the deprecated forwarding methods eliminates a misleading
abstraction layer — callers now explicitly reach through to the underlying
AWT component, making the delegation visible at the call site.

## Extracted class

### AwtHierarchyEventHandler.java

**Package:** `org.lgna.croquet.views`
**Visibility:** Package-private (no `public` modifier on the class)

A self-contained `HierarchyListener` implementation that tracks component
displayability transitions and parent changes on behalf of an
`AwtComponentView` instance.

#### Fields

| Field | Type | Modifier | Purpose |
| --- | --- | --- | --- |
| `isDisplayableState` | `boolean` | instance | Tracks whether the component was last seen as displayable |
| `awtParent` | `Container` | instance | Caches the most recent AWT parent to detect parent changes |
| `isWarningAlreadyPrinted` | `boolean` | `static` | Suppresses repeated "parent not actually changing" warnings across all instances |

#### Constructor

```java
AwtHierarchyEventHandler(AwtComponentView<?> owner)
```

The handler holds a reference to its owning `AwtComponentView` to call
back into the protected lifecycle hooks (`handleDisplayable`,
`handleUndisplayable`, `handleAddedTo`, `handleRemovedFrom`) and to
access the underlying AWT component via `owner.getAwtComponent()`.

#### Methods

| Method | Visibility | Purpose |
| --- | --- | --- |
| `trackDisplayability()` | package-private | Compares `isDisplayableState` against the live `isDisplayable()` value; fires `handleDisplayable()` or `handleUndisplayable()` on transition |
| `handleParentChange(Container)` | private | Fires `handleRemovedFrom` / `handleAddedTo` when the AWT parent changes |
| `processHierarchyChanged(HierarchyEvent)` | package-private | Dispatches `DISPLAYABILITY_CHANGED` and `PARENT_CHANGED` flags; called from `AwtComponentView.handleHierarchyChanged` |
| `hierarchyChanged(HierarchyEvent)` | public (from `HierarchyListener`) | Entry point registered on the AWT component |

#### Static warning suppression

`isWarningAlreadyPrinted` remains `static` so that the "investigate:
hierarchyChanged seems to not be actually changing the parent" message
prints at most once across all `AwtComponentView` instances in the JVM,
matching the original behavior. The benign race on this boolean is
preserved as-is (worst case: one extra print).

## Removed deprecated methods

The following 10 `@Deprecated` methods were deleted from
`AwtComponentView.java`. Each was a one-line delegation to
`this.getAwtComponent().<method>(…)`:

| Method | Listener type |
| --- | --- |
| `addHierarchyListener(HierarchyListener)` | `HierarchyListener` |
| `removeHierarchyListener(HierarchyListener)` | `HierarchyListener` |
| `addKeyListener(KeyListener)` | `KeyListener` |
| `removeKeyListener(KeyListener)` | `KeyListener` |
| `addMouseListener(MouseListener)` | `MouseListener` |
| `removeMouseListener(MouseListener)` | `MouseListener` |
| `addMouseMotionListener(MouseMotionListener)` | `MouseMotionListener` |
| `removeMouseMotionListener(MouseMotionListener)` | `MouseMotionListener` |
| `addMouseWheelListener(MouseWheelListener)` | `MouseWheelListener` |
| `removeMouseWheelListener(MouseWheelListener)` | `MouseWheelListener` |

**Not removed:** `setPreferredSize(Dimension)` and `makeStandOut()` are
also `@Deprecated` but are not listener forwarding methods — they remain
in `AwtComponentView` to limit scope.

## File inventory

| File | Status | Module |
| --- | --- | --- |
| `core/croquet/…/views/AwtHierarchyEventHandler.java` | **New** | `core/croquet` |
| `core/croquet/…/views/AwtComponentView.java` | Modified (575 → ~483 lines) | `core/croquet` |
| `core/croquet/…/views/List.java` | Modified — 4 listener calls | `core/croquet` |
| `core/croquet/…/views/FolderTabbedPane.java` | Modified — 2 listener calls | `core/croquet` |
| `core/croquet/…/views/DragComponent.java` | Modified — 4 listener calls | `core/croquet` |
| `core/croquet/…/views/ButtonWithRightClickCascade.java` | Modified — 4 listener calls | `core/croquet` |
| `core/croquet/…/views/HoverPopupView.java` | Modified — 2 listener calls | `core/croquet` |
| `core/ide/…/capture/views/ImageCaptureRectangleStencilView.java` | Modified — 4 listener calls | `core/ide` |
| `core/ide/…/custom/components/ArrayCustomExpressionCreatorView.java` | Modified — 2 listener calls | `core/ide` |
| `core/ide/…/javacode/croquet/views/JavaCodeView.java` | Modified — 4 listener calls | `core/ide` |
| `core/ide/…/custom/components/KeyViewController.java` | Modified — 2 listener calls | `core/ide` |
| `core/ide/…/sceneeditor/views/InstanceFactorySelectionPanel.java` | Modified — 2 listener calls | `core/ide` |
| `core/ide/…/sceneeditor/side/views/MarkersView.java` | Modified — 2 listener calls | `core/ide` |
| `core/croquet/…/views/AwtHierarchyEventHandlerTest.java` | **New** — characterization test | `core/croquet` |

### Files NOT modified

| File | Reason |
| --- | --- |
| `ViewController.java` | Already uses `getAwtComponent()` directly |
| `JDragProxy.java` | Extends `JPanel`, not `AwtComponentView` — `addKeyListener` is a native Java call |
| `JTimeLineView.java` | Extends `JPanel`, not `AwtComponentView` |
| `TimeLinePoseMarker.java` | Extends `JToggleButton`, not `AwtComponentView` |
| `MemoryView.java` | Extends `JComponent`, not `AwtComponentView` |
| `CommentPane.java` (inner `CommentLine`) | Extends `JSuggestiveTextArea`, not `AwtComponentView` |
| `DropDownButtonUI.java` | Operates on raw `AbstractButton` |
| `ScrollingPopupMenuUtilities.java` | Operates on `JPopupMenu` |
| `ShowAllSystemPropertiesView.java` | No deprecated listener calls (in design spec but not actually affected) |
| `ShowImageView.java` | No deprecated listener calls (in design spec but not actually affected) |
| `EulaView.java` | No deprecated listener calls (in design spec but not actually affected) |

## Caller migration pattern

All callers follow the same mechanical transformation:

```java
// BEFORE (deprecated — removed)
this.addMouseListener(myListener);
this.removeMouseListener(myListener);

// AFTER (direct AWT access)
this.getAwtComponent().addMouseListener(myListener);
this.getAwtComponent().removeMouseListener(myListener);
```

For non-`this` targets, the pattern is identical:

```java
// BEFORE
this.titlesScrollPane.addMouseListener(myListener);

// AFTER
this.titlesScrollPane.getAwtComponent().addMouseListener(myListener);
```

This makes the AWT delegation explicit at the call site. The underlying
behavior is unchanged — the deprecated methods were already doing exactly
this delegation internally.

## Protected hooks retained

These methods remain in `AwtComponentView` as empty stubs for subclass
override. They are **not** moved to the handler:

| Method | Signature | Overriding subclasses (non-exhaustive) |
| --- | --- | --- |
| `handleDisplayable()` | `protected void handleDisplayable()` | `SwingComponentView`, `AbstractWindow`, several IDE views |
| `handleUndisplayable()` | `protected void handleUndisplayable()` | `SwingComponentView`, `AbstractWindow` |
| `handleAddedTo(parent)` | `protected void handleAddedTo(AwtComponentView<?> parent)` | `DragComponent`, `FolderTabbedPane`, IDE views |
| `handleRemovedFrom(parent)` | `protected void handleRemovedFrom(AwtComponentView<?> parent)` | `DragComponent`, `ScrollPane` |
| `handleHierarchyChanged(e)` | `protected void handleHierarchyChanged(HierarchyEvent e)` | No current overrides, but preserved for API stability |

The handler calls back into these hooks on the owning `AwtComponentView`
instance, preserving the subclass override contract.

## Validation commands

```bash
# Compile the affected modules
mvn -pl core/croquet compile -q

# Compile downstream modules to verify caller updates
mvn -pl core/ide compile -q

# Full build (optional — slower)
mvn compile -q

# Line count check
wc -l core/croquet/src/main/java/org/lgna/croquet/views/AwtComponentView.java
# Expected: < 500

# Run characterization tests
mvn -pl core/croquet test -Dtest=AwtHierarchyEventHandlerTest -q
```

## Compatibility rules

1. **No behavioral change.** The hierarchy lifecycle fires the same
   callbacks in the same order. `isWarningAlreadyPrinted` remains static.

2. **Package-private handler.** `AwtHierarchyEventHandler` must not be
   `public`. It is an internal implementation detail of `AwtComponentView`.
   No code outside `org.lgna.croquet.views` should reference it.

3. **No new public setters on handler fields.** `isDisplayableState` and
   `awtParent` are managed internally — exposing setters would break the
   lifecycle invariant.

4. **Protected hooks stay in `AwtComponentView`.** Moving
   `handleDisplayable()` et al. to the handler would break 20+ subclass
   overrides. The handler calls back into the owner — never the reverse.

5. **`checkEventDispatchThread()` guard preserved.** The EDT assertion in
   `getAwtComponent()` is unchanged. The handler does not bypass it.

6. **Thread safety is unchanged.** The static `isWarningAlreadyPrinted`
   field has a benign data race (boolean, monotonic false→true). This is
   the existing behavior and is deliberately preserved.

## Examples

### Registering a mouse listener on a Croquet view

```java
public class MyView extends SwingComponentView<JPanel> {
  private final MouseListener clickHandler = new MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e) {
      // handle click
    }
  };

  @Override
  protected void handleDisplayable() {
    super.handleDisplayable();
    // Register directly on the AWT component
    this.getAwtComponent().addMouseListener(this.clickHandler);
  }

  @Override
  protected void handleUndisplayable() {
    this.getAwtComponent().removeMouseListener(this.clickHandler);
    super.handleUndisplayable();
  }
}
```

### Hierarchy lifecycle hooks (unchanged)

Subclasses continue to override the protected hooks as before. The
extraction is invisible to subclass authors:

```java
public class MyPanel extends AwtContainerView<JPanel> {
  @Override
  protected void handleDisplayable() {
    // Called when the component becomes displayable
    initializeResources();
  }

  @Override
  protected void handleUndisplayable() {
    // Called when the component is no longer displayable
    releaseResources();
  }

  @Override
  protected void handleAddedTo(AwtComponentView<?> parent) {
    // Called when this view is added to a parent
    registerWithParent(parent);
  }

  @Override
  protected void handleRemovedFrom(AwtComponentView<?> parent) {
    // Called when this view is removed from a parent
    unregisterFromParent(parent);
  }
}
```

### Internal wiring (for maintainers)

The handler is created and registered inside `getAwtComponent()`:

```java
// In AwtComponentView.getAwtComponent()
if (this.awtComponent == null) {
  this.checkEventDispatchThread();
  this.awtComponent = this.createAwtComponent();
  this.hierarchyHandler = new AwtHierarchyEventHandler(this);
  this.hierarchyHandler.trackDisplayability();
  this.awtComponent.addHierarchyListener(this.hierarchyHandler);
  // ... name, orientation, map registration
}
```

And torn down in `release()`:

```java
// In AwtComponentView.release()
if (this.awtComponent != null) {
  this.awtComponent.removeHierarchyListener(this.hierarchyHandler);
  this.hierarchyHandler.trackDisplayability();
  // ... map cleanup
}
```
