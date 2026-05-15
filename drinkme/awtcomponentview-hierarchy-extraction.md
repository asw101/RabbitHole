# AwtComponentView hierarchy-lifecycle extraction into AwtHierarchyHandler

The `org.lgna.croquet.views.AwtComponentView` class has been reduced from 575 lines to approximately 483 lines by extracting all AWT hierarchy-lifecycle management into a new package-private class `AwtHierarchyHandler`. The twelve deprecated listener-forwarding methods have also been compacted to single-line bodies.

After extraction, `AwtComponentView` retains its role as the abstract base for all croquet view wrappers — component creation, layout, painting, geometry, and the four protected lifecycle hooks that 50+ subclasses override. The hierarchy-event plumbing that drives those hooks now lives entirely in `AwtHierarchyHandler`.

## Finished behavior

### AwtHierarchyHandler

`AwtHierarchyHandler` is a package-private (no `public` modifier) final class in `org.lgna.croquet.views`. It owns the internal state and dispatch logic previously embedded in `AwtComponentView`:

1. **Displayability tracking** — compares `isDisplayableState` against actual displayability and calls the owner's `handleDisplayable()` / `handleUndisplayable()` hooks on transitions.
2. **Parent-change detection** — tracks `awtParent` and calls the owner's `handleAddedTo()` / `handleRemovedFrom()` hooks on parent transitions.
3. **Hierarchy event dispatch** — the guts of `handleHierarchyChanged` (flag checking, displayability dispatch, parent-change dispatch, `isWarningAlreadyPrinted` guard) move here as `processHierarchyEvent()`.

**Note:** The `HierarchyListener` field and the protected `handleHierarchyChanged()` method remain in `AwtComponentView` because `ReturnToSceneTypeButton` (package-private class in `NonSceneTypeView.java`) overrides `handleHierarchyChanged`. The method body shrinks to a one-line delegation: `this.hierarchyHandler.processHierarchyEvent(e);`

#### Fields

| Field | Type | Semantics |
| --- | --- | --- |
| `owner` | `AwtComponentView<?>` | Back-reference to the view that delegates hierarchy tracking. Set at construction, never null. |
| `isDisplayableState` | `boolean` | Tracks last-known displayability to emit transitions, not duplicates. |
| `awtParent` | `Container` | Tracks last-known parent to detect add/remove transitions. |
| `isWarningAlreadyPrinted` | `static boolean` | JVM-global flag suppressing repeated "parent not actually changing" diagnostic. Remains static to preserve existing semantics. |

#### Public API

```java
final class AwtHierarchyHandler {
    AwtHierarchyHandler(AwtComponentView<?> owner);
    void processHierarchyEvent(HierarchyEvent e);
}
```

- **`AwtHierarchyHandler(AwtComponentView<?> owner)`** — Stores the back-reference. No side effects.

- **`processHierarchyEvent(HierarchyEvent e)`** — The internal dispatch logic extracted from `handleHierarchyChanged`. Checks event flags and delegates to `trackDisplayability()` / `handleParentChange()`. Contains the `isWarningAlreadyPrinted` guard for spurious parent-change events. Called from `AwtComponentView.handleHierarchyChanged()`.

#### Internal methods (package-private)

| Method | Purpose |
| --- | --- |
| `trackDisplayability(Component)` | Compares `isDisplayableState` against `awtComponent.isDisplayable()` and calls the appropriate owner hook on transitions. Takes the AWT component as a parameter (no stored reference). |
| `handleParentChange(Container)` | Emits `handleRemovedFrom` for the old parent (if any) and `handleAddedTo` for the new parent (if any), updating `awtParent`. |

#### Threading

All methods are called on the AWT Event Dispatch Thread. No new threads or synchronization are introduced. The handler inherits the single-thread EDT access pattern from `AwtComponentView`.

### AwtComponentView (reduced)

`AwtComponentView` retains:

- **Static lookup infrastructure** — `map`, `lookup()`, `InternalAwtContainerAdapter`, `InternalAwtComponentAdapter`.
- **Component creation template** — `createAwtComponent()` abstract method, `getAwtComponent()` lazy initializer (now delegates `trackDisplayability()` to `AwtHierarchyHandler`).
- **Protected lifecycle hooks** — `handleDisplayable()`, `handleUndisplayable()`, `handleAddedTo(AwtComponentView<?>)`, `handleRemovedFrom(AwtComponentView<?>)`. These remain in `AwtComponentView` because 50+ subclasses across `core/croquet` and `core/ide` override them.
- **Component property accessors** — font, color, visibility, location, bounds, size, orientation, opacity.
- **Preferred-size constraint system** — `minimumPreferredWidth/Height`, `maximumPreferredWidth/Height`, `constrainPreferredSizeIfNecessary()`, `isMaximumSizeClampedToPreferredSize`.
- **Coordinate conversion** — `convertRectangle()`, `convertMouseEvent()`, shape methods.
- **Ancestor traversal** — `getParent()`, `getRoot()`, `getFirstAncestorAssignableTo()`, `getScrollPaneAncestor()`.
- **Deprecated listener wrappers** — 12 methods (5 add/remove pairs for `HierarchyListener`, `KeyListener`, `MouseListener`, `MouseMotionListener`, `MouseWheelListener`, plus `setPreferredSize` and `makeStandOut`) compacted to single-line bodies.
- **Utility** — `getTreeLock()`, `checkEventDispatchThread()`, `checkTreeLock()`, `repaint()`, `requestFocus()`, `requestFocusLater()`, `appendRepr()`, `toString()`.

#### Removed from AwtComponentView

| Item | Disposition |
| --- | --- |
| `isDisplayableState` field (L128) | Moved to `AwtHierarchyHandler`. |
| `trackDisplayability()` method (L130–138) | Moved to `AwtHierarchyHandler.trackDisplayability()`. |
| `awtParent` field (L147) | Moved to `AwtHierarchyHandler`. |
| `handleParentChange()` method (L149–157) | Moved to `AwtHierarchyHandler.handleParentChange()`. |
| `isWarningAlreadyPrinted` static field (L159) | Moved to `AwtHierarchyHandler` (remains static). |
| `handleHierarchyChanged()` body (L161–187) | Dispatch logic moved to `AwtHierarchyHandler.processHierarchyEvent()`. Method signature stays in AwtComponentView (overridden by `ReturnToSceneTypeButton`) with a one-line delegation body. |

#### Retained in AwtComponentView (not moved)

| Item | Reason |
| --- | --- |
| `hierarchyListener` field (L116) | References `this::handleHierarchyChanged` — must stay so subclass overrides dispatch correctly. |
| `handleHierarchyChanged(HierarchyEvent)` (L161 signature) | Protected method overridden by `ReturnToSceneTypeButton` in `NonSceneTypeView.java`. Body shrinks from 27 lines to 1 line. |

#### New field in AwtComponentView

```java
private final AwtHierarchyHandler hierarchyHandler = new AwtHierarchyHandler(this);
```

Replaces the five removed declarations (3 fields + 2 methods) with a single delegation point. The `hierarchyListener` field and `handleHierarchyChanged` method remain but the latter shrinks to a one-line body.

#### Changed methods in AwtComponentView

**`getAwtComponent()`** — After `createAwtComponent()`, calls `hierarchyHandler.trackDisplayability(this.awtComponent)` instead of inline `this.trackDisplayability()`. The `addHierarchyListener` call is unchanged (listener field stays in AwtComponentView).

**`release()`** — Calls `hierarchyHandler.trackDisplayability(this.awtComponent)` instead of inline `this.trackDisplayability()`. The `removeHierarchyListener` call is unchanged.

**`handleHierarchyChanged(HierarchyEvent)`** — Body reduced from 27 lines to 1 line: `this.hierarchyHandler.processHierarchyEvent(e);`. Method remains protected so `ReturnToSceneTypeButton` (in `NonSceneTypeView.java`) can continue to override it.

### Deprecated listener wrappers (compacted)

The twelve deprecated methods at the end of `AwtComponentView` have been reformatted from multi-line bodies to single-line delegations:

```java
@Deprecated public void addHierarchyListener(HierarchyListener l) { this.getAwtComponent().addHierarchyListener(l); }
@Deprecated public void removeHierarchyListener(HierarchyListener l) { this.getAwtComponent().removeHierarchyListener(l); }
@Deprecated public void addKeyListener(KeyListener l) { this.getAwtComponent().addKeyListener(l); }
@Deprecated public void removeKeyListener(KeyListener l) { this.getAwtComponent().removeKeyListener(l); }
@Deprecated public void addMouseListener(MouseListener l) { this.getAwtComponent().addMouseListener(l); }
@Deprecated public void removeMouseListener(MouseListener l) { this.getAwtComponent().removeMouseListener(l); }
@Deprecated public void addMouseMotionListener(MouseMotionListener l) { this.getAwtComponent().addMouseMotionListener(l); }
@Deprecated public void removeMouseMotionListener(MouseMotionListener l) { this.getAwtComponent().removeMouseMotionListener(l); }
@Deprecated public void addMouseWheelListener(MouseWheelListener l) { this.getAwtComponent().addMouseWheelListener(l); }
@Deprecated public void removeMouseWheelListener(MouseWheelListener l) { this.getAwtComponent().removeMouseWheelListener(l); }
@Deprecated public void setPreferredSize(Dimension d) { this.getAwtComponent().setPreferredSize(d); }
@Deprecated public void makeStandOut() { ComponentUtilities.makeStandOut(this.getAwtComponent()); }
```

These wrappers are retained — not removed — because external callers may depend on them. They are already marked `@Deprecated` to signal that callers should use `getAwtComponent()` directly.

## Subclass override matrix

The four protected hooks remain in `AwtComponentView` and are invoked by the handler via same-package access. Key overriders (non-exhaustive):

| Hook | Overriding classes |
| --- | --- |
| `handleDisplayable()` | `Panel`, `Menu`, `TabbedPane`, `LayerStencil`, `ToolPaletteView`, `CodeEditor`, `AbstractSceneEditor`, `MarkersView`, and ~20 more |
| `handleUndisplayable()` | `Panel`, `Menu`, `TabbedPane`, `LayerStencil`, `ToolPaletteView`, `CodeEditor`, `AbstractSceneEditor`, `MarkersView`, and ~20 more |
| `handleAddedTo(AwtComponentView<?>)` | `DragComponent`, `ItemDropDown`, `FormPanel`, `InstanceFactorySelectionPanel`, `AddParameterView`, `ItemSelectablePanel`, `SelectedTypeView` |
| `handleRemovedFrom(AwtComponentView<?>)` | `DragComponent`, `ItemDropDown`, `FormPanel`, `InstanceFactorySelectionPanel`, `SelectedTypeView` |

None of these subclasses are modified by this extraction. They continue to override the hooks in `AwtComponentView` as before.

## Test coverage

`AwtHierarchyHandlerTest` (in `core/croquet/src/test/java/org/lgna/croquet/views/`) verifies:

1. **Displayability transitions** — calling `trackDisplayability()` with a displayable component fires `handleDisplayable()` on the owner; calling it when undisplayable fires `handleUndisplayable()`.
2. **No duplicate transitions** — repeated calls with the same displayability state do not re-trigger hooks.
3. **Parent-change callbacks** — `handleParentChange()` triggers `handleRemovedFrom()` for the old parent and `handleAddedTo()` for the new parent.
4. **No-op parent events** — `processHierarchyEvent()` where the parent hasn't actually changed does not trigger callbacks (exercises the `isWarningAlreadyPrinted` guard).
5. **Subclass override preserved** — `handleHierarchyChanged` remains overridable; a synthetic subclass test verifies the super-call chain works through the handler delegation.

## Line-count accounting

| Source | Lines removed | Lines added | Net |
| --- | --- | --- | --- |
| Moved fields & methods → `AwtHierarchyHandler` | −22 (3 fields, 2 methods) | +1 (`hierarchyHandler` field) | −21 |
| `handleHierarchyChanged` body → delegation | −24 (27-line body → 3-line stub) | 0 | −24 |
| `getAwtComponent()` inline → delegation | −1 | +1 | 0 |
| `release()` inline → delegation | −1 | +1 | 0 |
| Deprecated wrappers compacted (12 methods, L502–560) | −47 (59 lines → 12 one-liners) | 0 | −47 |
| **Total** | | | **~−92** |

Final `AwtComponentView.java`: ~483 lines (target: under 500 ✓).

`AwtHierarchyHandler.java`: ~55 lines (new file, single responsibility).

## Module and build impact

Both files reside in `org.lgna.croquet.views`. No changes to `pom.xml`, `module-info.java`, or any other module descriptor. The handler is package-private, so no new public API surface is exposed outside the package.

Build verification: `mvn test -pl core/croquet -am` must pass with zero new failures.
