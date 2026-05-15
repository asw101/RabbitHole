# Validate FolderTabbedPane Inner Class Extraction

Use this guide to verify the extraction of inner classes from
`FolderTabbedPane` into two new top-level files (issue #643).

For the full contract, see the [FolderTabbedPane Inner Class Extraction
reference](../reference/folder-tabbed-pane-inner-class-extraction.md).

## When to use this guide

Use this guide when:

- Reviewing changes that extract inner classes from `FolderTabbedPane`
- Modifying `FolderTabRenderer.java` or `FolderTitlesPanel.java`
- Changing visibility of fields or methods in `FolderTabbedPane` that
  the extracted classes depend on (especially `TRAILING_TAB_PAD` or
  `OUTLINE_THICKNESS`)
- Changing the `createTitlesPanel()` factory method
- Modifying tab close-button behavior or the `removeItemAndSelectAppropriateReplacement` call

Do not use this guide for popup menu behavior, scroll-drag behavior, or
header component layout changes. Those remain in `FolderTabbedPane`
itself.

## Before you start

Run commands from the repository root:

```bash
git submodule update --init tweedle-lang
test -d tweedle-lang/Grammar
export NODE_OPTIONS=--max-old-space-size=32768
```

## Step 1: Verify new files exist

```bash
ls -la core/croquet/src/main/java/org/lgna/croquet/views/{FolderTabRenderer,FolderTitlesPanel}.java
```

Both files must exist. Each must have:

- The CMU BSD copyright header
- `package org.lgna.croquet.views;`
- Package-private class declarations (no `public` modifier on the
  primary class)

## Step 2: Verify compilation

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/croquet -am -DfailIfNoTests=false -Dcheckstyle.skip compile
```

Compilation must succeed with zero errors. Key things the compiler verifies:

- `FolderTabTitle` can access `TRAILING_TAB_PAD` from `FolderTabbedPane`
  via package-private visibility
- `FolderTabTitle` constructor accepts `TabComposite<?>` (not `E`) since
  the enclosing class's type parameter is unavailable after extraction
- `FolderTabTitle` constructor receives `SingleSelectListState` and
  calls `removeItemAndSelectAppropriateReplacement()` on it
- `FolderTitlesPanel` (renamed from `TitlesPanel`) is accessible from
  `FolderTabbedPane.createTitlesPanel()`
- `JTitlesPanel` can still access `TRAILING_TAB_PAD` as a package-private
  constant

## Step 3: Verify line count

```bash
wc -l core/croquet/src/main/java/org/lgna/croquet/views/FolderTabbedPane.java
```

Target: under 500 lines. Expected: ~371 lines (643 − 272 extracted lines).

## Step 4: Verify the enclosing instance replacement

```bash
grep -n 'FolderTabbedPane\.this' \
  core/croquet/src/main/java/org/lgna/croquet/views/FolderTabRenderer.java
```

This must return zero matches. The old `FolderTabbedPane.this.getModel()`
enclosing reference in `FolderTabTitle` has been replaced with an
explicit `SingleSelectListState` constructor parameter:

```bash
grep -n 'listState\.removeItemAndSelectAppropriateReplacement' \
  core/croquet/src/main/java/org/lgna/croquet/views/FolderTabRenderer.java
```

This must return one match inside the `FolderTabTitle` constructor.

## Step 5: Verify class visibility

```bash
grep -c 'public class' \
  core/croquet/src/main/java/org/lgna/croquet/views/FolderTabRenderer.java \
  core/croquet/src/main/java/org/lgna/croquet/views/FolderTitlesPanel.java
```

Both files must show `0` — no `public class` declarations. All extracted
top-level classes are package-private.

## Step 6: Verify constants are package-private

```bash
grep -n 'TRAILING_TAB_PAD\|OUTLINE_THICKNESS' \
  core/croquet/src/main/java/org/lgna/croquet/views/FolderTabbedPane.java
```

Must show declarations without the `private` modifier:

```java
static final int TRAILING_TAB_PAD = 32;
static final int OUTLINE_THICKNESS = 1;
```

These constants are used by `JFolderTabTitle.repaint()` (in
`FolderTabRenderer.java`), `JTitlesPanel` (in `FolderTitlesPanel.java`),
and the border painting anonymous class in `FolderTabbedPane`'s
constructor.

## Step 7: Verify createTitlesPanel() return type and field declaration

```bash
grep -n 'createTitlesPanel\|TitlesPanel titlesPanel\|FolderTitlesPanel titlesPanel' \
  core/croquet/src/main/java/org/lgna/croquet/views/FolderTabbedPane.java
```

Must show the factory method returning `FolderTitlesPanel` (not the old
inner `TitlesPanel`) and the field declared as `FolderTitlesPanel`:

```java
private final FolderTitlesPanel titlesPanel = this.createTitlesPanel();
// ...
protected FolderTitlesPanel createTitlesPanel() {
```

## Step 8: Verify no inner class remnants

```bash
grep -n 'class FolderTabTitleUI\|class JFolderTabTitle\|class FolderTabTitle\|class TitlesPanel\|class JTitlesPanel' \
  core/croquet/src/main/java/org/lgna/croquet/views/FolderTabbedPane.java
```

Must return zero matches. All 5 extracted class declarations have been
moved to their own files.

The remaining inner classes should be:

```bash
grep -n 'class PopupOperation\|class PopupButton\|class ScrollListener' \
  core/croquet/src/main/java/org/lgna/croquet/views/FolderTabbedPane.java
```

Must return exactly 3 matches.

## Step 9: Run tests

```bash
NODE_OPTIONS=--max-old-space-size=32768 \
mvn -pl core/croquet -am \
  -DfailIfNoTests=false \
  -Dsurefire.failIfNoSpecifiedTests=false \
  test
```

All existing tests must pass. No new tests are required for this pure
structural extraction — the extracted code has identical semantics.

## Summary checklist

| Check | Pass criteria |
| --- | --- |
| Two new files exist | `FolderTabRenderer.java`, `FolderTitlesPanel.java` |
| Compilation succeeds | `mvn compile` zero errors |
| Line count under 500 | `wc -l FolderTabbedPane.java` < 500 |
| No `FolderTabbedPane.this` in extracted files | grep confirms enclosing ref removed |
| `listState.removeItemAndSelectAppropriateReplacement` | grep confirms 1 match in `FolderTabRenderer.java` |
| Package-private visibility | No `public class` in extracted files |
| Constants not private | `TRAILING_TAB_PAD` / `OUTLINE_THICKNESS` are package-private |
| Factory return type updated | `createTitlesPanel()` returns `FolderTitlesPanel` |
| Field type updated | `titlesPanel` declared as `FolderTitlesPanel` |
| No inner class remnants | grep confirms 5 classes removed, 3 remain |
| Tests pass | `mvn test` zero failures |
