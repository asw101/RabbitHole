# How to Validate the AwtComponentView Hierarchy Event Extraction

Validates that the extraction of hierarchy lifecycle internals from
`AwtComponentView` into `AwtHierarchyEventHandler` (issue #686) preserves
all behavior and that the deprecated listener forwarding methods are
properly removed.

## Prerequisites

- Java 17+ and Maven on `PATH`
- Tweedle grammar submodule initialized:
  `git submodule update --init tweedle-lang`

## Steps

### 1. Verify line count

```bash
wc -l core/croquet/src/main/java/org/lgna/croquet/views/AwtComponentView.java
```

Expected: under 500 lines.

### 2. Verify the new handler exists and is package-private

```bash
head -5 core/croquet/src/main/java/org/lgna/croquet/views/AwtHierarchyEventHandler.java
```

The class declaration must NOT include `public`:

```
class AwtHierarchyEventHandler implements HierarchyListener {
```

### 3. Verify deprecated forwarding methods are removed

```bash
grep -c '@Deprecated' \
  core/croquet/src/main/java/org/lgna/croquet/views/AwtComponentView.java
```

Expected: 4 remaining (`getParent`, `getRoot`, `setPreferredSize`,
`makeStandOut`). The 10 listener forwarding methods are gone.

### 4. Verify callers are updated

```bash
# Should find zero hits — no caller should use the removed methods via AwtComponentView
grep -rn 'this\.addMouseListener\|this\.removeMouseListener\|this\.addKeyListener\|this\.removeKeyListener\|this\.addMouseMotionListener\|this\.removeMouseMotionListener\|this\.addMouseWheelListener\|this\.removeMouseWheelListener\|this\.addHierarchyListener\|this\.removeHierarchyListener' \
  core/croquet/src/main/java/org/lgna/croquet/views/List.java \
  core/croquet/src/main/java/org/lgna/croquet/views/FolderTabbedPane.java \
  core/croquet/src/main/java/org/lgna/croquet/views/DragComponent.java \
  core/croquet/src/main/java/org/lgna/croquet/views/ButtonWithRightClickCascade.java \
  core/croquet/src/main/java/org/lgna/croquet/views/HoverPopupView.java \
  core/ide/src/main/java/org/alice/ide/capture/views/ImageCaptureRectangleStencilView.java \
  core/ide/src/main/java/org/alice/ide/custom/components/ArrayCustomExpressionCreatorView.java \
  core/ide/src/main/java/org/alice/ide/javacode/croquet/views/JavaCodeView.java \
  core/ide/src/main/java/org/alice/stageide/custom/components/KeyViewController.java \
  core/ide/src/main/java/org/alice/stageide/sceneeditor/views/InstanceFactorySelectionPanel.java \
  core/ide/src/main/java/org/alice/stageide/sceneeditor/side/views/MarkersView.java
```

All matching lines should use `this.getAwtComponent().addXxxListener(…)` instead.

### 5. Compile the affected modules

```bash
mvn -pl core/croquet compile -q
mvn -pl core/ide compile -q
```

Both must succeed with zero errors.

### 6. Run the characterization test

```bash
mvn -pl core/croquet test -Dtest=AwtHierarchyEventHandlerTest -q
```

### 7. Verify protected hooks are still in AwtComponentView

```bash
grep -n 'protected void handle' \
  core/croquet/src/main/java/org/lgna/croquet/views/AwtComponentView.java
```

Expected to find all five:
- `handleDisplayable()`
- `handleUndisplayable()`
- `handleAddedTo(AwtComponentView<?>)`
- `handleRemovedFrom(AwtComponentView<?>)`
- `handleHierarchyChanged(HierarchyEvent)`

## Success criteria

| Check | Expected |
| --- | --- |
| `AwtComponentView.java` line count | < 500 |
| `AwtHierarchyEventHandler.java` exists | Yes, package-private |
| Deprecated listener methods removed | 10 methods gone |
| `mvn -pl core/croquet compile -q` | Exit 0 |
| `mvn -pl core/ide compile -q` | Exit 0 |
| `AwtHierarchyEventHandlerTest` | All pass |
| Protected hooks in `AwtComponentView` | 5 methods present |
