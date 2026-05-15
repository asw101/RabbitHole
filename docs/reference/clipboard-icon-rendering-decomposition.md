# Clipboard Icon Rendering Decomposition

> **Module:** `core/ide`
> **Package:** `org.alice.ide.clipboard.icons`
> **Issue:** [#662](https://github.com/rysweet/alice3-modernization/issues/662) — Reduce ClipboardIcon.java from 619 to under 500 lines

## Overview

`ClipboardIcon` is an SVG-transcoded Swing `Icon` that renders the Alice IDE clipboard.
It was a single 619-line file containing all shape definitions, gradient factories,
alpha composite orchestration, and public API in one class.

The refactoring extracts three rendering delegates and one gradient utility class,
leaving `ClipboardIcon` as a lightweight orchestrator (~260 lines) that owns state
and delegates paint calls. Rendering output is pixel-identical to the original.

## Architecture

```
┌─────────────────────────────────────────────────────────┐
│                     ClipboardIcon                       │
│  implements javax.swing.Icon                            │
│                                                         │
│  Fields:                                                │
│    - origAlpha: float                                   │
│    - isFull: boolean                                    │
│    - width, height: int                                 │
│    - dragReceptorState: DragReceptorState                │
│    - boardRenderer: ClipboardBoardRenderer               │
│    - paperRenderer: ClipboardPaperRenderer               │
│    - clipRenderer:  ClipboardClipRenderer                │
│                                                         │
│  Public API (unchanged):                                │
│    paint(Graphics2D)                                    │
│    paintIcon(Component, Graphics, int, int)             │
│    getIconWidth/Height()                                │
│    setDimension(Dimension)                              │
│    getDragReceptorState/setDragReceptorState(...)       │
│    isFull/setFull(boolean)                              │
│    getOrigX/Y/Width/Height()                            │
│                                                         │
│  Orchestration (private):                               │
│    paintRootGraphicsNode_0(g)                           │
│    paintCanvasGraphicsNode_0_0(g)                       │
│    paintCompositeGraphicsNode_0_0_2(g)                  │
│    paintCompositeGraphicsNode_0_0_2_0(g)                │
│      ├── boardRenderer.paintComposite(g, origAlpha, st) │
│      ├── if(isFull||ENTERED) paperRenderer.paintAll(...) │
│      └── clipRenderer.paintComposite(g, origAlpha)      │
└─────────────────────────────────────────────────────────┘
         │               │               │
         ▼               ▼               ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│ Clipboard    │ │ Clipboard    │ │ Clipboard    │
│ Board        │ │ Paper        │ │ Clip         │
│ Renderer     │ │ Renderer     │ │ Renderer     │
│ (62 lines)   │ │ (155 lines)  │ │ (121 lines)  │
└──────┬───────┘ └──────┬───────┘ └──────┬───────┘
       │                │                │
       ▼                ▼                ▼
      ┌──────────────────────────────────┐
      │       GradientPaintFactory       │
      │  static new_LinearGradientPaint  │
      │  static new_RadialGradientPaint  │
      │  (31 lines of method body)       │
      └──────────────────────────────────┘
```

## New Classes

### `GradientPaintFactory`

**Visibility:** Package-private (no `public` modifier)
**Purpose:** Reflective gradient paint construction, extracted from ClipboardIcon L63–93.

The original `ClipboardIcon` used reflection to construct `LinearGradientPaint` and
`RadialGradientPaint` instances (a pattern from the svg2java transcoder that predates
direct API usage). These two static factory methods are shared by all three renderers.

```java
// Package-private — used only within org.alice.ide.clipboard.icons
class GradientPaintFactory {

    static Paint new_LinearGradientPaint(
        Point2D start, Point2D end,
        float[] fractions, Color[] colors,
        AffineTransform gradientTransform);

    static Paint new_RadialGradientPaint(
        Point2D center, float radius, Point2D focus,
        float[] fractions, Color[] colors,
        AffineTransform gradientTransform);
}
```

Both methods use `ReflectionUtilities.getConstructor()` to look up the JDK gradient
classes reflectively, falling back to `colors[0]` (a solid color) on failure. This
preserves the original svg2java-generated error handling.

### `ClipboardBoardRenderer`

**Visibility:** Package-private
**Purpose:** Renders the clipboard board (brown rectangle with shadow), extracted from
ClipboardIcon L119–180.

**Entry point:**
```java
class ClipboardBoardRenderer {
    void paintComposite(Graphics2D g, float origAlpha, DragReceptorState state);
}
```

**What it renders:**
| Shape Method | Composite | Description |
|---|---|---|
| `paintShapeNode_0_0_2_0_0_0` | `0.62650603f * origAlpha` | Board shadow (black rounded rect) |
| `paintShapeNode_0_0_2_0_0_1` | `0.1927711f * origAlpha` | Board shadow inner (same shape, no `setPaint` — inherits black from `_0`) |
| `paintShapeNode_0_0_2_0_0_2` | `1.0f * origAlpha` | Board body (linear gradient fill, uses `state.getBoardColor()`) |
| `paintShapeNode_0_0_2_0_0_3` | `0.3f * origAlpha` | Board highlight (white→transparent gradient stroke) |

The `paintComposite` method replicates the exact composite/transform/paint sequence
from the original `paintCompositeGraphicsNode_0_0_2_0_0`. Each shape is wrapped in its
`AlphaComposite.getInstance(3, factor * origAlpha)` and `AffineTransform` pair.

### `ClipboardPaperRenderer`

**Visibility:** Package-private
**Purpose:** Renders the paper sheet (white/green rectangle with fold corner), extracted
from ClipboardIcon L182–300 plus orchestration from L431–466.

**Entry point:**
```java
class ClipboardPaperRenderer {
    void paintAll(Graphics2D g, float origAlpha, DragReceptorState state);
}
```

**What it renders (in order):**
| Shape Method | Composite | Transform | Description |
|---|---|---|---|
| `paintShapeNode_0_0_2_0_1` | `0.2f * origAlpha` (explicit) | identity | Paper shadow (black fill + stroke) |
| *(composite-only `_0_0_2_0_2`)* | `1.0f * origAlpha` (explicit) | — | No shape. Sets composite for subsequent fills. |
| `paintShapeNode_0_0_2_0_4_0` | inherited `1.0f` | scale `(0.6232f, 0.6771f)` + translate `(164.31f, 56.77f)` | Paper body fill (radial gradient, uses `state.getPaperColor()`) |
| `paintShapeNode_0_0_2_0_5_0` | inherited `1.0f` | scale `(0.6232f, 0.6771f)` + translate `(164.31f, 56.77f)` | Paper border stroke (linear gradient) |
| `paintShapeNode_0_0_2_0_6` | `0.2f * origAlpha` (explicit) | scale `(1.0f, 0.903f)` + translate `(-167.0f, 5.12f)` | Fold corner shadow (black triangle) |
| `paintShapeNode_0_0_2_0_7` | `1.0f * origAlpha` (explicit) | identity | Fold corner body (radial gradient fill + stroke) |
| `paintShapeNode_0_0_2_0_8` | inherited `1.0f` from `_7` | identity | Paper inner highlight (white→transparent stroke) |

> **Note:** The SVG transcoder left empty nodes at positions `_0_0_2_0_2` and `_0_0_2_0_3`.
> Position `_2` only sets a composite (no shape); position `_3` is absent entirely.
> The paper renderer must reproduce the `_2` composite set to keep Graphics2D state identical.

The `paintAll` method encapsulates the full paper rendering sequence including the
per-shape alpha composite settings (`0.2`, `1.0`, etc.) and affine transforms that were
previously interleaved in `paintCompositeGraphicsNode_0_0_2_0` (L431–466). This is the
most complex renderer because the original orchestrator spread paper rendering across
individual method calls with interleaved composite state changes.

**Caller contract:** `ClipboardIcon.paintCompositeGraphicsNode_0_0_2_0` calls
`paperRenderer.paintAll(g, origAlpha, dragReceptorState)` inside the existing
`if (this.isFull || (this.dragReceptorState == DragReceptorState.ENTERED))` guard.
The visibility check stays in `ClipboardIcon`.

### `ClipboardClipRenderer`

**Visibility:** Package-private
**Purpose:** Renders the metal binder clip at the top of the clipboard, extracted from
ClipboardIcon L302–422.

**Entry point:**
```java
class ClipboardClipRenderer {
    void paintComposite(Graphics2D g, float origAlpha);
}
```

Note: No `DragReceptorState` parameter — the clip appearance does not change with state.

**What it renders:**
| Shape Method | Composite | Description |
|---|---|---|
| `paintShapeNode_0_0_2_0_9_0` | `0.44117647f * origAlpha` | Clip shadow (black, scaled `1.0502f`) |
| `paintShapeNode_0_0_2_0_9_1` | `1.0f * origAlpha` | Clip body (5-stop linear gradient fill + stroke) |
| `paintShapeNode_0_0_2_0_9_2` | `0.5f * origAlpha` | Clip ridge highlight (white fill) |
| `paintShapeNode_0_0_2_0_9_3` | `1.0f * origAlpha` | Clip inner highlight (white→transparent gradient) |
| `paintShapeNode_0_0_2_0_9_4` | `0.2f * origAlpha` | Clip outline (white stroke) |

## Modified Class

### `ClipboardIcon` (reduced from 619 → ~260 lines)

**Public API:** Completely unchanged. `ClipboardDragComponent` (the sole consumer)
requires zero changes.

**What was removed:**
- `new_LinearGradientPaint` / `new_RadialGradientPaint` static methods (→ `GradientPaintFactory`)
- `paintShapeNode_0_0_2_0_0_*` and `paintCompositeGraphicsNode_0_0_2_0_0` (→ `ClipboardBoardRenderer`)
- `paintShapeNode_0_0_2_0_1` through `paintShapeNode_0_0_2_0_8`, `paintCompositeGraphicsNode_0_0_2_0_4`, `paintCompositeGraphicsNode_0_0_2_0_5`, and inline orchestration from L431–465 (→ `ClipboardPaperRenderer`)
- `paintShapeNode_0_0_2_0_9_*` and `paintCompositeGraphicsNode_0_0_2_0_9` (→ `ClipboardClipRenderer`)

**What was added:**
```java
private final ClipboardBoardRenderer boardRenderer = new ClipboardBoardRenderer();
private final ClipboardPaperRenderer paperRenderer = new ClipboardPaperRenderer();
private final ClipboardClipRenderer clipRenderer = new ClipboardClipRenderer();
```

**Orchestration method changes in `paintCompositeGraphicsNode_0_0_2_0`:**

Before (L424–471, inlined calls):
```java
private void paintCompositeGraphicsNode_0_0_2_0(Graphics2D g) {
    // board (always painted)
    AffineTransform trans_0_0_2_0_0 = g.getTransform();
    g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, -167.00001525878906f, -3.0f));
    paintCompositeGraphicsNode_0_0_2_0_0(g);
    g.setTransform(trans_0_0_2_0_0);

    if (this.isFull || ...) {
        // paper — 36 lines of composite/transform/paint calls
        ...
    }

    // clip (always painted)
    AffineTransform trans_0_0_2_0_9 = g.getTransform();
    g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, -167.00001525878906f, -3.0f));
    paintCompositeGraphicsNode_0_0_2_0_9(g);
    g.setTransform(trans_0_0_2_0_9);
}
```

After (delegate calls):
```java
private void paintCompositeGraphicsNode_0_0_2_0(Graphics2D g) {
    // board (always painted)
    AffineTransform trans_0_0_2_0_0 = g.getTransform();
    g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, -167.00001525878906f, -3.0f));
    boardRenderer.paintComposite(g, origAlpha, dragReceptorState);
    g.setTransform(trans_0_0_2_0_0);

    if (this.isFull || (this.dragReceptorState == DragReceptorState.ENTERED)) {
        paperRenderer.paintAll(g, origAlpha, dragReceptorState);
    }

    // clip (always painted)
    AffineTransform trans_0_0_2_0_9 = g.getTransform();
    g.transform(new AffineTransform(1.0f, 0.0f, 0.0f, 1.0f, -167.00001525878906f, -3.0f));
    clipRenderer.paintComposite(g, origAlpha);
    g.setTransform(trans_0_0_2_0_9);
}
```

## File Inventory

| File | Lines | Visibility | New? |
|------|-------|------------|------|
| `ClipboardIcon.java` | ~260 | `public` | Modified |
| `GradientPaintFactory.java` | ~75 | package-private | New |
| `ClipboardBoardRenderer.java` | ~105 | package-private | New |
| `ClipboardPaperRenderer.java` | ~200 | package-private | New |
| `ClipboardClipRenderer.java` | ~165 | package-private | New |

All new files include the CMU copyright header (lines 1–42 from the original).

## Rendering Pipeline

The paint call chain, showing where delegates are invoked:

```
ClipboardIcon.paintIcon(Component, Graphics, int, int)
  └── ClipboardIcon.paint(Graphics2D)
        └── paintRootGraphicsNode_0(g)
              └── paintCanvasGraphicsNode_0_0(g)
                    └── paintCompositeGraphicsNode_0_0_2(g)
                          └── paintCompositeGraphicsNode_0_0_2_0(g)
                                ├── boardRenderer.paintComposite(g, origAlpha, state)
                                │     ├── paintShapeNode_0_0_2_0_0_0  (shadow outer)
                                │     ├── paintShapeNode_0_0_2_0_0_1  (shadow inner)
                                │     ├── paintShapeNode_0_0_2_0_0_2  (board body)
                                │     └── paintShapeNode_0_0_2_0_0_3  (board highlight)
                                │
                                ├── [if isFull or ENTERED]
                                │   paperRenderer.paintAll(g, origAlpha, state)
                                │     ├── paintShapeNode_0_0_2_0_1    (paper shadow, 0.2α)
                                │     ├── [composite-only _0_0_2_0_2] (set 1.0α, no shape)
                                │     ├── paintShapeNode_0_0_2_0_4_0  (paper fill, scaled)
                                │     ├── paintShapeNode_0_0_2_0_5_0  (paper stroke, scaled)
                                │     ├── paintShapeNode_0_0_2_0_6    (fold shadow, 0.2α)
                                │     ├── paintShapeNode_0_0_2_0_7    (fold body, 1.0α)
                                │     └── paintShapeNode_0_0_2_0_8    (inner highlight, inherited)
                                │
                                └── clipRenderer.paintComposite(g, origAlpha)
                                      ├── paintShapeNode_0_0_2_0_9_0  (clip shadow)
                                      ├── paintShapeNode_0_0_2_0_9_1  (clip body)
                                      ├── paintShapeNode_0_0_2_0_9_2  (clip ridge)
                                      ├── paintShapeNode_0_0_2_0_9_3  (clip highlight)
                                      └── paintShapeNode_0_0_2_0_9_4  (clip outline)
```

## Usage

No consumer changes required. The sole consumer is:

```java
// ClipboardDragComponent.java — unchanged
private static final ClipboardIcon ICON = new ClipboardIcon();
```

`ClipboardIcon` instantiates its three delegate renderers in field initializers.
There are no new constructor parameters, no new public methods, and no changes to
the `Icon` interface contract.

## Configuration

None. All classes are stateless renderers (no configuration, no properties files,
no environment variables). Color state flows through `DragReceptorState` which is
passed as a parameter from `ClipboardIcon` to board/paper renderers at paint time.

## Verification

```bash
# 1. Line count check
wc -l core/ide/src/main/java/org/alice/ide/clipboard/icons/ClipboardIcon.java
# Expected: < 500 lines (target ~260)

# 2. Compilation
mvn compile -pl core/ide -am -q
# Expected: BUILD SUCCESS

# 3. New file existence
ls core/ide/src/main/java/org/alice/ide/clipboard/icons/
# Expected: ClipboardIcon.java, GradientPaintFactory.java,
#           ClipboardBoardRenderer.java, ClipboardPaperRenderer.java,
#           ClipboardClipRenderer.java

# 4. No public modifier on new classes (package-private)
grep -c "^public class" core/ide/src/main/java/org/alice/ide/clipboard/icons/Gradient*.java
grep -c "^public class" core/ide/src/main/java/org/alice/ide/clipboard/icons/Clipboard*Renderer.java
# Expected: 0 for each

# 5. ClipboardDragComponent unchanged
git diff core/ide/src/main/java/org/alice/ide/clipboard/components/ClipboardDragComponent.java
# Expected: no output (no changes)
```

## Risks and Mitigations

| Risk | Likelihood | Mitigation |
|------|-----------|------------|
| Rendering regression | Low | Exact method body transplant, zero logic changes. `origAlpha` passed explicitly. |
| Broken alpha composites | Low | Same `AlphaComposite.getInstance(3, factor * origAlpha)` pattern, same call sites. |
| Missing gradient calls | Low | Simple text replacement `new_LinearGradientPaint` → `GradientPaintFactory.new_LinearGradientPaint`. |
| Compile failure from missed imports | Low | Each renderer imports only what its shapes need. Verified with `mvn compile`. |

## Design Decisions

1. **Renderers are stateless.** They receive all needed state (`origAlpha`, `DragReceptorState`)
   as method parameters. This avoids synchronization concerns and keeps the delegates
   simple value-like helpers.

1b. **Graphics2D state leaks between shape methods — both paint and composite.**
   This is intentional in the svg2java-generated code. Two forms:
   - **Paint leaks:** `paintShapeNode_0_0_2_0_0_1` (board shadow inner) calls
     `g.fill()` without `g.setPaint()` — inherits black from `_0_0_2_0_0_0`.
   - **Composite leaks:** `paintShapeNode_0_0_2_0_8` (paper inner highlight) has no
     `setComposite()` call — inherits `1.0f * origAlpha` from `_0_0_2_0_7`.
     Similarly, `_0_0_2_0_4` and `_0_0_2_0_5` inherit composite from the
     composite-only `_0_0_2_0_2` node.
   Renderers must preserve exact method call order and must NOT insert
   spurious `setComposite()`/`setPaint()` calls that would break this chain.

2. **Paper renderer owns its orchestration.** Unlike board and clip (which have a single
   `paintCompositeGraphicsNode` entry point), the paper shapes were orchestrated inline
   in `ClipboardIcon.paintCompositeGraphicsNode_0_0_2_0` with interleaved composite
   settings. The `paintAll` method absorbs this orchestration to keep `ClipboardIcon` clean.

3. **Clip renderer has no state parameter.** The clip's appearance is constant regardless
   of `DragReceptorState`, so its signature is `paintComposite(g, origAlpha)` — simpler
   than the other two.

4. **Gradient factory is separate from renderers.** All three renderers call both gradient
   methods. A shared utility avoids triple duplication.

5. **No `public` classes added.** All new classes are package-private. The clipboard icon
   package is an internal implementation detail with a single public entry point
   (`ClipboardIcon`).

6. **`DragReceptorState` is an enum with three values.** `IDLE` (brown board, white paper),
   `STARTED` (yellow/yellow), `ENTERED` (green/green). Board renderer uses
   `state.getBoardColor()`, paper renderer uses `state.getPaperColor()`. The clip is
   state-independent. Only `ENTERED` triggers paper rendering (alongside `isFull`).
