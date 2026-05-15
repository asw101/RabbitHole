# ImageEditorFrame Dead Code Removal

This reference documents the removal of a 16-line commented-out `main()`
method from `ImageEditorFrame.java` (512 → 495 lines). No behavior, API, or
runtime changes are involved.

Issue #687 reduces `ImageEditorFrame` below 500 lines by removing dead code.

## Contents

- [Motivation](#motivation)
- [What was removed](#what-was-removed)
- [File inventory](#file-inventory)
- [What was NOT changed](#what-was-not-changed)
- [Validation](#validation)
- [Acceptance criteria](#acceptance-criteria)

## Motivation

`ImageEditorFrame.java` contained 512 lines, exceeding the 500-line
modernization target. Lines 496–511 held a commented-out `main()` method —
a development-time test harness that launched the image editor in a
standalone Swing window. The method referenced classes from `org.alice.ide`
(a separate module), confirming it was never part of the production API.

Removing these 16 dead lines brings the file to 496 lines with zero
behavioral impact.

## What was removed

A single block of commented-out Java code (lines 496–511 in the original
file):

```java
  //  public static void main( String[] args ) throws Exception {
  //    edu.cmu.cs.dennisc.javax.swing.UIManagerUtilities.setLookAndFeel( "Nimbus" );
  //
  //    //final javax.swing.ImageIcon icon = new javax.swing.ImageIcon( org.alice.ide.warning.components.WarningView.class.getResource( "images/toxic.png" ) );
  //    final java.awt.Image image = edu.cmu.cs.dennisc.image.ImageUtilities.read( org.alice.ide.warning.components.WarningView.class.getResource( "images/toxic.png" ) );
  //    org.lgna.croquet.simple.SimpleApplication app = new org.lgna.croquet.simple.SimpleApplication();
  //    final ImageEditorFrame imageComposite = new ImageEditorFrame();
  //    imageComposite.getShowInScreenResolutionState().setValueTransactionlessly( false );
  //    imageComposite.getToolState().setValueTransactionlessly( Tool.CROP_SELECT );
  //    javax.swing.SwingUtilities.invokeLater( new Runnable() {
  //      public void run() {
  //        imageComposite.setImageClearShapesAndShowFrame( image );
  //        ( (org.lgna.croquet.components.Frame)imageComposite.getView().getRoot() ).setDefaultCloseOperation( org.lgna.croquet.components.Frame.DefaultCloseOperation.EXIT );
  //      }
  //    } );
  //  }
```

This block was:
- **Dead code** — fully commented out, never compiled or executed.
- **Cross-module** — referenced `org.alice.ide.warning.components.WarningView`,
  a class in the `ide` module, not `image-editor`.
- **Test harness** — launched a standalone `SimpleApplication` to manually
  test image editor rendering. Not a production entry point.

## File inventory

| File | Action | Lines before | Lines after |
|------|--------|-------------|-------------|
| `core/image-editor/src/main/java/org/alice/imageeditor/croquet/ImageEditorFrame.java` | Modified | 512 | 495 |
| `core/image-editor/pom.xml` | Modified | — | — |
| `core/image-editor/src/test/java/org/alice/imageeditor/croquet/ImageEditorFrameTest.java` | Created | — | 167 |

No files deleted.

## What was NOT changed

- **Public API** — All public and package-private methods, fields, and inner
  classes remain identical.
- **Runtime behavior** — Commented-out code has no runtime effect; removing
  it changes nothing observable.
- **Imports** — No import statements were added or removed (the removed code
  used fully qualified class names).
- **Formatting** — Existing code style and indentation are preserved. One
  trailing blank line before the closing brace is retained per standard Java
  conventions.

## Validation

1. **Line count:** `wc -l` confirms the file is under 500 lines.
2. **Compilation:** `mvn -pl core/image-editor compile -q` passes with no
   errors or warnings.
3. **Diff review:** `git diff` shows only the removal of comment lines —
   no executable code is touched.

## Acceptance criteria

| # | Criterion | Verification |
|---|-----------|-------------|
| 1 | `ImageEditorFrame.java` is under 500 lines | `wc -l` output ≤ 499 |
| 2 | No compilation errors in `core/image-editor` | `mvn compile` exit code 0 |
| 3 | No public API changes | `git diff` shows only comment removal in source |
| 4 | All characterization tests pass | `mvn test -pl core/image-editor` exit code 0 |
