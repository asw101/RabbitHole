# FolderTabbedPane Inner Class Extraction

This reference documents the extraction of inner classes from
`FolderTabbedPane` (issue #643) into two new top-level package-private
files in `org.lgna.croquet.views`. The extraction reduces
`FolderTabbedPane.java` from 643 lines to ~371 lines (well under the
500-line target).

## Contents

- [Motivation](#motivation)
- [Extracted classes](#extracted-classes)
- [File inventory](#file-inventory)
- [Enclosing instance pattern](#enclosing-instance-pattern)
- [Visibility changes](#visibility-changes)
- [Classes that stay inline](#classes-that-stay-inline)
- [Validation commands](#validation-commands)
- [Compatibility rules](#compatibility-rules)
- [Examples](#examples)

## Motivation

`FolderTabbedPane.java` contained 643 lines with 6 inner classes packed
into a single file. The file's original Javadoc acknowledged the problem:
"there are so many classes in just this one file!" The inner classes
divide into two cohesive groups:

| Group | Classes | Purpose |
| --- | --- | --- |
| Tab rendering stack | `FolderTabTitleUI`, `JFolderTabTitle`, `FolderTabTitle` | Custom Swing UI delegate, JToggleButton subclass, and BooleanStateButton wrapper for rendering folder-style tabs |
| Tab layout panel | `TitlesPanel`, `JTitlesPanel` | Horizontal tab strip with custom painting (Bézier curves, anti-aliased tab outlines, selected-tab-on-top z-order) |

Three inner classes remain in `FolderTabbedPane`: `PopupOperation`,
`PopupButton`, and `ScrollListener`. These directly reference the
enclosing pane's scroll viewport and model iterator — extracting them
would add constructor parameters for minimal line savings.

## Extracted classes

### FolderTabRenderer.java

Contains the three tab-rendering classes as a co-located group:

| Original inner class | Static? | Back-ref? | Purpose |
| --- | --- | --- | --- |
| `FolderTabTitleUI` | static | No | Custom `BasicToggleButtonUI` subclass. Computes preferred size for icon+text+close-button tabs. Paints tab text with rollover/selected color states. |
| `JFolderTabTitle` | static | No | Custom `JToggleButton`. Installs `FolderTabTitleUI`, suppresses opacity, extends repaint region by `TRAILING_TAB_PAD` pixels, and manages `ItemListener` lifecycle. |
| `FolderTabTitle` | non-static | Yes — resolved via constructor injection (see [Enclosing instance pattern](#enclosing-instance-pattern)) | `BooleanStateButton<AbstractButton>` wrapper. Manages the close button (`JCloseButton`) with Spring layout. Calls `updateFor()` to toggle closeable state per tab. |

**Why co-located?** These three classes form a tight rendering stack:
`FolderTabTitleUI` is the Swing UI delegate for `JFolderTabTitle`, which
is the AWT component returned by `FolderTabTitle.createAwtComponent()`.
Splitting them across files would scatter a single rendering concern.

### FolderTitlesPanel.java

Contains the tab strip layout panel and its custom `JPanel`:

| Original inner class | Static? | Back-ref? | Purpose |
| --- | --- | --- | --- |
| `TitlesPanel` | static (`protected`) | No | `LineAxisPanel` subclass. Factory for `JTitlesPanel`. |
| `JTitlesPanel` | static (inside `TitlesPanel`) | No | Custom `JPanel` with Bézier curve tab painting, anti-aliased rendering, and selected-tab-on-top z-order. Adds `TRAILING_TAB_PAD` to preferred width. |

**Why one file?** `JTitlesPanel` is a nested static class of
`TitlesPanel`. They form a single layout concept: the tab strip and its
Swing implementation.

## File inventory

After extraction, these files exist in
`core/croquet/src/main/java/org/lgna/croquet/views/`:

| File | Lines | Description |
| --- | --- | --- |
| `FolderTabbedPane.java` | ~371 | Constructor, popup operation, popup button, scroll listener, header layout, factory methods |
| `FolderTabRenderer.java` | ~212 | `FolderTabTitleUI` + `JFolderTabTitle` + `FolderTabTitle` (tab rendering stack) |
| `FolderTitlesPanel.java` | ~176 | `FolderTitlesPanel` (renamed from `TitlesPanel`) + `JTitlesPanel` (tab strip painting) |

All new files carry the CMU BSD copyright header matching the original.

## Enclosing instance pattern

`FolderTabTitle` was the only non-static inner class. It accessed the
enclosing `FolderTabbedPane` instance at one call site:

```java
// BEFORE (inner class enclosing access):
ActionListener closeButtonActionListener = e ->
    FolderTabbedPane.this.getModel().removeItemAndSelectAppropriateReplacement(item);
```

After extraction, the model reference is passed as a constructor
parameter:

```java
// AFTER (explicit dependency injection):
class FolderTabTitle extends BooleanStateButton<AbstractButton> {
  private final SingleSelectListState<?, ?> listState;

  FolderTabTitle(TabComposite<?> item, BooleanState booleanState, SingleSelectListState<?, ?> listState) {
    super(booleanState);
    this.listState = listState;
    // ...
    ActionListener closeButtonActionListener = e ->
        this.listState.removeItemAndSelectAppropriateReplacement(item);
  }
}
```

**Note on generic parameter `E`:** The original inner class used `E`
from the enclosing `FolderTabbedPane<E extends TabComposite<?>>`. After
extraction, `E` is unavailable. The constructor accepts `TabComposite<?>`
directly — this is the effective upper bound of `E` and sufficient for
the two methods called on it: `isPotentiallyCloseable()` and as the
argument to `removeItemAndSelectAppropriateReplacement()`.

The call site in `FolderTabbedPane.createTitleButton()` passes
`this.getModel()` as the third argument.

## Visibility changes

| Element | Before | After | Reason |
| --- | --- | --- | --- |
| `FolderTabTitleUI` (class) | `private static` inner | package-private top-level | Used by `JFolderTabTitle` in same file |
| `JFolderTabTitle` (class) | `private static` inner | package-private top-level | Used by `FolderTabTitle.createAwtComponent()` |
| `FolderTabTitle` (class) | `private` non-static inner | package-private top-level | Instantiated by `FolderTabbedPane.createTitleButton()` |
| `TitlesPanel` → `FolderTitlesPanel` (class) | `protected static` inner | package-private top-level (renamed) | Instantiated by `FolderTabbedPane.createTitlesPanel()` |
| `JTitlesPanel` (class) | `protected static` inner of `TitlesPanel` | `protected static` inner of `FolderTitlesPanel` | Kept as nested class of its parent |
| `TRAILING_TAB_PAD` (constant) | `private static final` | package-private `static final` | Shared across `FolderTabbedPane`, `JFolderTabTitle`, and `JTitlesPanel` |
| `OUTLINE_THICKNESS` (constant) | `private static final` | package-private `static final` | Used by `FolderTabbedPane` constructor border painting |
| `titlesPanel` (field) | `private final TitlesPanel` | `private final FolderTitlesPanel` | Type follows the `TitlesPanel` → `FolderTitlesPanel` rename |

**Constants** `TRAILING_TAB_PAD` and `OUTLINE_THICKNESS` remain on
`FolderTabbedPane` but are widened from `private` to package-private so
the extracted classes in the same package can reference them.

## Classes that stay inline

Three inner classes remain in `FolderTabbedPane` by design:

| Class | Static? | Reason |
| --- | --- | --- |
| `PopupOperation` | non-static | Iterates `FolderTabbedPane.this.getModel()` and calls `getActionFor(item)` — capturing the enclosing pane's model and action resolution |
| `PopupButton` | non-static | Accesses `FolderTabbedPane.this.getBackgroundColor()` for paint and `TRAILING_TAB_PAD` for overflow detection |
| `ScrollListener` | non-static | Accesses `titlesScrollPane` field for drag-to-scroll viewport manipulation |

Extracting these would require passing 2–3 constructor parameters each
for negligible line savings (~130 lines total). The enclosing pane
references are intrinsic to their function.

## Validation commands

All commands assume the repository root as working directory and the
`tweedle-lang` submodule initialized.

### Compilation

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/croquet -am -DfailIfNoTests=false -Dcheckstyle.skip compile
```

### Line count verification

```bash
wc -l core/croquet/src/main/java/org/lgna/croquet/views/FolderTabbedPane.java
# Target: under 500 lines (~371 expected)
```

### Verify new files exist

```bash
ls -la core/croquet/src/main/java/org/lgna/croquet/views/{FolderTabRenderer,FolderTitlesPanel}.java
```

### Test execution

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/croquet -am \
  -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  test
```

## Compatibility rules

1. **No public API change.** `FolderTabbedPane` is public but its inner
   classes were all `private` or `protected`. No downstream code
   references the inner classes by name. The `createTitlesPanel()`
   return type changes from `TitlesPanel` to `FolderTitlesPanel` — no
   existing subclasses override this method.

2. **Same package.** All extracted files remain in
   `org.lgna.croquet.views`. Package-private access is preserved.

3. **No reflection dependencies.** No Alice 3 code uses reflection to
   access these inner classes by name.

4. **No serialization impact.** None of the extracted classes implement
   `Serializable`.

5. **createTitlesPanel() factory.** The return type changes from the
   inner `TitlesPanel` to top-level `FolderTitlesPanel`. The
   `titlesPanel` field declaration also changes type accordingly. This
   is the factory method that subclasses may override to customize the
   tab strip. The rename is safe because no subclass of
   `FolderTabbedPane` overrides this method in the codebase.

6. **Co-location rules.** `FolderTabTitleUI`, `JFolderTabTitle`, and
   `FolderTabTitle` must stay in `FolderTabRenderer.java` — the UI
   delegate is installed by `JFolderTabTitle.updateUI()` and
   `JFolderTabTitle` is the AWT component returned by
   `FolderTabTitle.createAwtComponent()`.

## Examples

### Creating a tab button (from FolderTabbedPane)

```java
// FolderTabbedPane delegates tab button creation to FolderTabRenderer:
@Override
protected BooleanStateButton<? extends javax.swing.AbstractButton> createTitleButton(E item, BooleanState itemSelectedState) {
  return new FolderTabTitle(item, itemSelectedState, this.getModel());
}
```

### Creating a titles panel (factory method)

```java
// Default factory — subclasses can override to customize tab strip layout:
protected FolderTitlesPanel createTitlesPanel() {
  return new FolderTitlesPanel();
}
```

### Tab close button wiring (in FolderTabTitle)

```java
// Close button action removes the tab's item from the model:
ActionListener closeButtonActionListener = e ->
    this.listState.removeItemAndSelectAppropriateReplacement(item);
this.closeButton = new JCloseButton();
this.closeButton.addActionListener(closeButtonActionListener);
```
