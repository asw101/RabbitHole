# Tutorial: Trace the FolderTabbedPane Inner Class Extraction

This tutorial walks through the extraction of inner classes from
`FolderTabbedPane` (issue #643). You will trace each design decision —
why the tab rendering classes share a file, why the titles panel was
renamed, and how the non-static inner class's enclosing reference was
eliminated.

For the full contract, see the [FolderTabbedPane Inner Class Extraction
reference](../reference/folder-tabbed-pane-inner-class-extraction.md).

For validation steps, see the [Validation how-to](../howto/validate-folder-tabbed-pane-inner-class-extraction.md).

## Contents

- [Goal](#goal)
- [1. Understand the pre-extraction structure](#1-understand-the-pre-extraction-structure)
- [2. Trace the FolderTabRenderer extraction](#2-trace-the-foldertabrenderer-extraction)
- [3. Trace the FolderTitlesPanel extraction](#3-trace-the-foldertitlespanel-extraction)
- [4. Trace the enclosing instance elimination](#4-trace-the-enclosing-instance-elimination)
- [5. Trace the constant visibility widening](#5-trace-the-constant-visibility-widening)
- [6. Understand why PopupOperation, PopupButton, and ScrollListener stay](#6-understand-why-popupoperation-popupbutton-and-scrolllistener-stay)
- [7. Run the validation](#7-run-the-validation)

## Goal

After this tutorial you will be able to explain:

- Why `FolderTabTitleUI`, `JFolderTabTitle`, and `FolderTabTitle` live
  in one file instead of three
- Why `TitlesPanel` was renamed to `FolderTitlesPanel`
- How the non-static `FolderTabTitle` inner class's implicit outer
  reference was replaced with explicit dependency injection
- Why `TRAILING_TAB_PAD` and `OUTLINE_THICKNESS` were widened from
  `private` to package-private
- Why `PopupOperation`, `PopupButton`, and `ScrollListener` were not
  extracted

## 1. Understand the pre-extraction structure

Open `FolderTabbedPane.java` (643 lines) and identify the six inner
classes:

```
Lines 72–132:   FolderTabTitleUI (private static)
                 → Custom BasicToggleButtonUI for tab size/paint

Lines 134–177:  JFolderTabTitle (private static)
                 → JToggleButton that installs FolderTabTitleUI

Lines 179–219:  FolderTabTitle (private, non-static)
                 → BooleanStateButton wrapping JFolderTabTitle

Lines 221–339:  TitlesPanel (protected static)
                 ├── JTitlesPanel (protected static nested)
                 │    → Bézier tab painting, anti-aliased, z-ordered
                 └── TitlesPanel itself → LineAxisPanel factory

Lines 358–387:  PopupOperation (private, non-static)
Lines 389–440:  PopupButton (private, non-static)
Lines 442–488:  ScrollListener (private, non-static)
```

The first group (lines 72–219) is the tab rendering stack. The second
group (lines 221–339) is the tab layout panel. The last group
(lines 358–488) is the pane's interaction layer.

**Key insight:** The first two groups have no dependency on the enclosing
pane's instance beyond one call site (`FolderTabbedPane.this.getModel()`
in `FolderTabTitle`). The last three classes are deeply coupled to the
pane's fields.

## 2. Trace the FolderTabRenderer extraction

The three tab-rendering classes form a vertical stack:

```
FolderTabTitleUI (Swing UI delegate)
       ↑ installed by
JFolderTabTitle (JToggleButton subclass)
       ↑ returned by createAwtComponent()
FolderTabTitle (BooleanStateButton wrapper)
```

`FolderTabTitleUI` computes preferred size (handling icon, text, and
embedded close button) and paints tab text with three color states:
selected, rollover, and inactive. `JFolderTabTitle` installs this UI
delegate via `updateUI()`, suppresses opacity, and extends the repaint
region by `TRAILING_TAB_PAD` pixels to cover the curved tab tail.
`FolderTabTitle` is the croquet-layer wrapper that manages the close
button and `updateFor()` lifecycle.

**Why co-located?** These classes are tightly coupled through the
rendering contract:

1. `JFolderTabTitle.updateUI()` directly references `FolderTabTitleUI`
2. `FolderTabTitle.createAwtComponent()` returns `new JFolderTabTitle()`
3. `FolderTabTitleUI.paint()` and `getPreferredSize()` depend on the
   component structure that `JFolderTabTitle` sets up

Splitting them would scatter a single rendering concept across three
files with no readability benefit.

## 3. Trace the FolderTitlesPanel extraction

`TitlesPanel` was a `protected static` inner class — a common pattern
for customizable factories. The nested `JTitlesPanel` contains 100+
lines of custom painting: Bézier curves for tab shapes, anti-aliased
rendering, and z-order management to paint the selected tab on top.

**Why rename to `FolderTitlesPanel`?** The name `TitlesPanel` is too
generic for a top-level class in `org.lgna.croquet.views`. Prefixing
with `Folder` matches the naming convention of the other extracted
classes (`FolderTabRenderer`, `FolderTabTitle`) and makes the class's
origin clear when seen in import statements.

**Why one file?** `JTitlesPanel` is the Swing implementation of
`FolderTitlesPanel`. It remains as a `protected static` nested class —
the same pattern used throughout the croquet views package. Promoting it
to a separate top-level class would break the factory pattern established
by `LineAxisPanel.createJPanel()`.

The `createTitlesPanel()` factory method in `FolderTabbedPane` changes
its return type from `TitlesPanel` to `FolderTitlesPanel`. The
`titlesPanel` field declaration also changes type:

```java
// BEFORE:
private final TitlesPanel titlesPanel = this.createTitlesPanel();
// AFTER:
private final FolderTitlesPanel titlesPanel = this.createTitlesPanel();
```

This is safe because no subclass of `FolderTabbedPane` in the codebase overrides this
method.

## 4. Trace the enclosing instance elimination

`FolderTabTitle` was the only non-static inner class that referenced the
enclosing `FolderTabbedPane`. The reference appeared at exactly one call
site — the close button's action listener:

```java
// BEFORE: implicit outer reference via FolderTabbedPane.this
private class FolderTabTitle extends BooleanStateButton<AbstractButton> {
  public FolderTabTitle(final E item, BooleanState booleanState) {
    super(booleanState);
    if (item.isPotentiallyCloseable()) {
      ActionListener closeButtonActionListener = e ->
          FolderTabbedPane.this.getModel()
              .removeItemAndSelectAppropriateReplacement(item);
      // ...
    }
  }
}
```

The implicit outer reference creates a hidden coupling: every
`FolderTabTitle` holds a reference to the enclosing `FolderTabbedPane`,
even though it only needs the `SingleSelectListState` model.

After extraction, the dependency is made explicit:

```java
// AFTER: explicit dependency injection
class FolderTabTitle extends BooleanStateButton<AbstractButton> {
  private final SingleSelectListState<?, ?> listState;

  FolderTabTitle(TabComposite<?> item, BooleanState booleanState,
                 SingleSelectListState<?, ?> listState) {
    super(booleanState);
    this.listState = listState;
    if (item.isPotentiallyCloseable()) {
      ActionListener closeButtonActionListener = e ->
          this.listState.removeItemAndSelectAppropriateReplacement(item);
      // ...
    }
  }
}
```

The constructor parameter changes from `E item` to `TabComposite<?> item`
because `E` was the enclosing class's type parameter and is unavailable
after extraction. `TabComposite<?>` is the effective upper bound.

The call site in `FolderTabbedPane.createTitleButton()` passes
`this.getModel()`:

```java
@Override
protected BooleanStateButton<? extends javax.swing.AbstractButton> createTitleButton(E item, BooleanState itemSelectedState) {
  return new FolderTabTitle(item, itemSelectedState, this.getModel());
}
```

**Why not pass the whole pane?** Passing `FolderTabbedPane` itself would
recreate the coupling. The minimum dependency is `SingleSelectListState`,
which provides the single method needed:
`removeItemAndSelectAppropriateReplacement()`.

## 5. Trace the constant visibility widening

Two constants were widened from `private` to package-private:

| Constant | Value | Used by |
| --- | --- | --- |
| `TRAILING_TAB_PAD` | 32 | `JFolderTabTitle.repaint()`, `JTitlesPanel.getPreferredSize()`, `JTitlesPanel.addToPath()`, `PopupButton.isNecessary()`, border painting in constructor |
| `OUTLINE_THICKNESS` | 1 | Border painting in `FolderTabbedPane` constructor |

Before extraction, all references were within the same file, so
`private` visibility worked. After extraction, `TRAILING_TAB_PAD` is
referenced from three files (`FolderTabbedPane.java`,
`FolderTabRenderer.java`, `FolderTitlesPanel.java`).

**Why not move constants to a shared interface?** Both constants are
implementation details of the folder tab rendering. Creating a shared
constants interface would over-engineer a simple visibility change.
Package-private visibility is the minimum escalation needed.

`OUTLINE_THICKNESS` is technically only used within `FolderTabbedPane`
itself, but it was widened alongside `TRAILING_TAB_PAD` for consistency
and because future extractions may need it.

## 6. Understand why PopupOperation, PopupButton, and ScrollListener stay

These three inner classes were not extracted because they are deeply
coupled to the enclosing `FolderTabbedPane` instance:

### PopupOperation

Iterates `FolderTabbedPane.this.getModel()` to build a popup menu of
all tab items, and calls the private `getActionFor(item)` method:

```java
for (E item : FolderTabbedPane.this.getModel()) {
  JCheckBoxMenuItem checkBox = new JCheckBoxMenuItem(getActionFor(item));
  checkBox.setSelected(FolderTabbedPane.this.getModel().getValue() == item);
}
```

Extracting this would require passing both the model and the
`getActionFor` method reference — adding complexity for ~30 lines.

### PopupButton

Uses `FolderTabbedPane.this.getBackgroundColor()` in its paint method
and `TRAILING_TAB_PAD` for overflow detection. The custom `JButton`
anonymous class captures the pane's background for non-necessary state
painting.

### ScrollListener

Directly accesses the `titlesScrollPane` field for viewport
manipulation during drag-to-scroll:

```java
titlesScrollPane.getAwtComponent().getViewport().setViewPosition(...)
```

Extracting this would require passing the scroll pane as a constructor
parameter — adding a parameter for a private field that only one class
uses.

**Total retained lines:** ~130. The cost of extraction (3 constructor
parameters, 3 stored fields, breaking locality) exceeds the benefit.

## 7. Run the validation

Follow the [Validation how-to](../howto/validate-folder-tabbed-pane-inner-class-extraction.md)
to verify compilation, line count, and test passage.

The key checks:

```bash
# Compile
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/croquet -am -DfailIfNoTests=false -Dcheckstyle.skip compile

# Line count (must be under 500)
wc -l core/croquet/src/main/java/org/lgna/croquet/views/FolderTabbedPane.java

# No enclosing reference remains in extracted files
grep -n 'FolderTabbedPane\.this' \
  core/croquet/src/main/java/org/lgna/croquet/views/FolderTabRenderer.java
# Expected: 0 matches

# Constants are package-private
grep -n 'private.*TRAILING_TAB_PAD\|private.*OUTLINE_THICKNESS' \
  core/croquet/src/main/java/org/lgna/croquet/views/FolderTabbedPane.java
# Expected: 0 matches

# Tests pass
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/croquet -am -DfailIfNoTests=false test
```
