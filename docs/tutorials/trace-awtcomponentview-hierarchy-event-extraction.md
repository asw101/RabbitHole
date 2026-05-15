# Trace: AwtComponentView Hierarchy Event Extraction

Step-by-step walkthrough of the `AwtComponentView` extraction (issue #686).
Use this to understand the reasoning behind each change.

## Starting point

`AwtComponentView.java` at 575 lines, containing:

1. **Core view abstraction** — `getAwtComponent()`, `release()`, layout
   preferences, color/font setters, coordinate conversion (~481 lines)
2. **Hierarchy lifecycle tracking** — `isDisplayableState`, `awtParent`,
   `isWarningAlreadyPrinted`, `trackDisplayability()`,
   `handleParentChange()`, `handleHierarchyChanged()` (~45 lines)
3. **Deprecated listener forwarding** — 10 `@Deprecated` methods that
   simply delegate `addXxxListener`/`removeXxxListener` to
   `getAwtComponent()` (~49 lines)

Groups 2 and 3 are independent extraction targets.

## Step 1: Characterization test

Before any refactoring, write `AwtHierarchyEventHandlerTest` to capture
the existing hierarchy lifecycle behavior:

- Displayability transitions fire `handleDisplayable()` /
  `handleUndisplayable()` exactly once per transition
- Parent changes fire `handleAddedTo()` / `handleRemovedFrom()`
- Duplicate parent-change events are suppressed (and print a one-time
  warning)

This test runs against the *extracted* handler, so it is written to match
the post-extraction API. Before the handler class exists, the test does
not compile — that is expected.

## Step 2: Create AwtHierarchyEventHandler

Extract these items from `AwtComponentView` into a new package-private
class:

| From AwtComponentView | To AwtHierarchyEventHandler |
| --- | --- |
| `boolean isDisplayableState` (L128) | Instance field |
| `Container awtParent` (L147) | Instance field |
| `static boolean isWarningAlreadyPrinted` (L159) | Static field |
| `trackDisplayability()` (L130-138) | Package-private method |
| `handleParentChange(Container)` (L149-157) | Private method |
| Body of `handleHierarchyChanged(HierarchyEvent)` (L161-187) | `processHierarchyChanged()` + `hierarchyChanged()` |

The handler takes an `AwtComponentView<?>` reference via its constructor.
It calls back into the owner's protected hooks:

```
handler.trackDisplayability()
  → owner.handleDisplayable()   // or handleUndisplayable()

handler.handleParentChange(newParent)
  → owner.handleRemovedFrom(…)  // then handleAddedTo(…)
```

## Step 3: Rewire AwtComponentView

Replace the old field + method-reference pattern:

```java
// BEFORE
private final HierarchyListener hierarchyListener =
    AwtComponentView.this::handleHierarchyChanged;
private boolean isDisplayableState = false;
private Container awtParent;
// ... trackDisplayability(), handleParentChange() methods
```

With handler delegation:

```java
// AFTER
private AwtHierarchyEventHandler hierarchyHandler;
```

In `getAwtComponent()`:

```java
this.hierarchyHandler = new AwtHierarchyEventHandler(this);
this.hierarchyHandler.trackDisplayability();
this.awtComponent.addHierarchyListener(this.hierarchyHandler);
```

In `release()`:

```java
this.awtComponent.removeHierarchyListener(this.hierarchyHandler);
this.hierarchyHandler.trackDisplayability();
```

`handleHierarchyChanged(HierarchyEvent)` stays as a protected method but
now delegates:

```java
protected void handleHierarchyChanged(HierarchyEvent e) {
  this.hierarchyHandler.processHierarchyChanged(e);
}
```

## Step 4: Remove deprecated listener forwarding

Delete these 10 methods (lines 502-550):

- `addHierarchyListener` / `removeHierarchyListener`
- `addKeyListener` / `removeKeyListener`
- `addMouseListener` / `removeMouseListener`
- `addMouseMotionListener` / `removeMouseMotionListener`
- `addMouseWheelListener` / `removeMouseWheelListener`

## Step 5: Update callers

Each caller replaces `this.addXxxListener(…)` with
`this.getAwtComponent().addXxxListener(…)`:

**core/croquet callers:**

| File | Changes |
| --- | --- |
| `List.java` | 4 calls (mouse + motion listener add/remove) |
| `DragComponent.java` | 4 calls (mouse + motion listener add/remove) |
| `FolderTabbedPane.java` | 2 calls (`titlesScrollPane` mouse + motion listener) |
| `ButtonWithRightClickCascade.java` | 4 calls (mouse + motion listener add/remove) |
| `HoverPopupView.java` | 2 calls (mouse listener add/remove) |

**core/ide callers:**

| File | Changes |
| --- | --- |
| `ImageCaptureRectangleStencilView.java` | 4 calls (mouse + motion listener add/remove) |
| `ArrayCustomExpressionCreatorView.java` | 2 calls (mouse listener add/remove) |
| `JavaCodeView.java` | 4 calls (key + mouse wheel listener add/remove) |
| `KeyViewController.java` | 2 calls (key listener add/remove) |
| `InstanceFactorySelectionPanel.java` | 2 calls (mouse listener add/remove) |
| `MarkersView.java` | 2 calls (mouse listener add/remove) |

**Note:** The design spec listed ShowAllSystemPropertiesView, ShowImageView,
and EulaView, but grep confirms they do not call any of the deprecated
listener forwarding methods — no changes needed in those files.

## Step 6: Validate

```bash
mvn -pl core/croquet compile -q    # handler + AwtComponentView
mvn -pl core/ide compile -q        # downstream callers
wc -l core/croquet/src/main/java/org/lgna/croquet/views/AwtComponentView.java
mvn -pl core/croquet test -Dtest=AwtHierarchyEventHandlerTest -q
```

## Decision log

| Decision | Rationale |
| --- | --- |
| Handler gets `AwtComponentView<?>` reference | Needed to call back into protected hooks and access `getAwtComponent()` |
| `handleHierarchyChanged` stays protected | No current overrides, but removing it would break API contract for any external subclass |
| `isWarningAlreadyPrinted` stays `static` | Cross-instance suppression is the original design; changing to instance would alter behavior |
| `setPreferredSize`/`makeStandOut` not removed | They are `@Deprecated` but not listener forwarding — out of scope |
| Handler implements `HierarchyListener` directly | Cleaner than method reference; handler is the listener object registered on the AWT component |

## Line count analysis

| Category | Lines |
| --- | --- |
| Starting total | 575 |
| Removed: deprecated forwarding methods | −49 |
| Removed: hierarchy fields + methods | −45 |
| Added: handler field + delegation stubs | +2 |
| **Final total** | **~483** |
